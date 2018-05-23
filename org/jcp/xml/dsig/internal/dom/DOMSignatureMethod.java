package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAParams;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.jcp.xml.dsig.internal.SignerOutputStream;
import org.w3c.dom.Element;
import sun.security.util.KeyUtil;

public abstract class DOMSignatureMethod
  extends AbstractDOMSignatureMethod
{
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  private SignatureMethodParameterSpec params;
  private Signature signature;
  static final String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
  static final String RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
  static final String RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
  static final String ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
  static final String ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
  static final String ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
  static final String ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
  static final String DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
  
  DOMSignatureMethod(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if ((paramAlgorithmParameterSpec != null) && (!(paramAlgorithmParameterSpec instanceof SignatureMethodParameterSpec))) {
      throw new InvalidAlgorithmParameterException("params must be of type SignatureMethodParameterSpec");
    }
    checkParams((SignatureMethodParameterSpec)paramAlgorithmParameterSpec);
    params = ((SignatureMethodParameterSpec)paramAlgorithmParameterSpec);
  }
  
  DOMSignatureMethod(Element paramElement)
    throws MarshalException
  {
    Element localElement = DOMUtils.getFirstChildElement(paramElement);
    if (localElement != null) {
      params = unmarshalParams(localElement);
    }
    try
    {
      checkParams(params);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new MarshalException(localInvalidAlgorithmParameterException);
    }
  }
  
  static SignatureMethod unmarshal(Element paramElement)
    throws MarshalException
  {
    String str = DOMUtils.getAttributeValue(paramElement, "Algorithm");
    if (str.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1")) {
      return new SHA1withRSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")) {
      return new SHA256withRSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384")) {
      return new SHA384withRSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512")) {
      return new SHA512withRSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
      return new SHA1withDSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2009/xmldsig11#dsa-sha256")) {
      return new SHA256withDSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1")) {
      return new SHA1withECDSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256")) {
      return new SHA256withECDSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384")) {
      return new SHA384withECDSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512")) {
      return new SHA512withECDSA(paramElement);
    }
    if (str.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1")) {
      return new DOMHMACSignatureMethod.SHA1(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256")) {
      return new DOMHMACSignatureMethod.SHA256(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384")) {
      return new DOMHMACSignatureMethod.SHA384(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512")) {
      return new DOMHMACSignatureMethod.SHA512(paramElement);
    }
    throw new MarshalException("unsupported SignatureMethod algorithm: " + str);
  }
  
  public final AlgorithmParameterSpec getParameterSpec()
  {
    return params;
  }
  
  boolean verify(Key paramKey, SignedInfo paramSignedInfo, byte[] paramArrayOfByte, XMLValidateContext paramXMLValidateContext)
    throws InvalidKeyException, SignatureException, XMLSignatureException
  {
    if ((paramKey == null) || (paramSignedInfo == null) || (paramArrayOfByte == null)) {
      throw new NullPointerException();
    }
    if (!(paramKey instanceof PublicKey)) {
      throw new InvalidKeyException("key must be PublicKey");
    }
    checkKeySize(paramXMLValidateContext, paramKey);
    if (signature == null) {
      try
      {
        Provider localProvider = (Provider)paramXMLValidateContext.getProperty("org.jcp.xml.dsig.internal.dom.SignatureProvider");
        signature = (localProvider == null ? Signature.getInstance(getJCAAlgorithm()) : Signature.getInstance(getJCAAlgorithm(), localProvider));
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new XMLSignatureException(localNoSuchAlgorithmException);
      }
    }
    signature.initVerify((PublicKey)paramKey);
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Signature provider:" + signature.getProvider());
      log.log(Level.FINE, "verifying with key: " + paramKey);
    }
    ((DOMSignedInfo)paramSignedInfo).canonicalize(paramXMLValidateContext, new SignerOutputStream(signature));
    try
    {
      AbstractDOMSignatureMethod.Type localType = getAlgorithmType();
      if (localType == AbstractDOMSignatureMethod.Type.DSA)
      {
        int i = ((DSAKey)paramKey).getParams().getQ().bitLength();
        return signature.verify(JavaUtils.convertDsaXMLDSIGtoASN1(paramArrayOfByte, i / 8));
      }
      if (localType == AbstractDOMSignatureMethod.Type.ECDSA) {
        return signature.verify(SignatureECDSA.convertXMLDSIGtoASN1(paramArrayOfByte));
      }
      return signature.verify(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new XMLSignatureException(localIOException);
    }
  }
  
  private static void checkKeySize(XMLCryptoContext paramXMLCryptoContext, Key paramKey)
    throws XMLSignatureException
  {
    if (Utils.secureValidation(paramXMLCryptoContext))
    {
      int i = KeyUtil.getKeySize(paramKey);
      if (i == -1)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Size for " + paramKey.getAlgorithm() + " key cannot be determined");
        }
        return;
      }
      if (Policy.restrictKey(paramKey.getAlgorithm(), i)) {
        throw new XMLSignatureException(paramKey.getAlgorithm() + " keys less than " + Policy.minKeySize(paramKey.getAlgorithm()) + " bits are forbidden when secure validation is enabled");
      }
    }
  }
  
  byte[] sign(Key paramKey, SignedInfo paramSignedInfo, XMLSignContext paramXMLSignContext)
    throws InvalidKeyException, XMLSignatureException
  {
    if ((paramKey == null) || (paramSignedInfo == null)) {
      throw new NullPointerException();
    }
    if (!(paramKey instanceof PrivateKey)) {
      throw new InvalidKeyException("key must be PrivateKey");
    }
    checkKeySize(paramXMLSignContext, paramKey);
    if (signature == null) {
      try
      {
        Provider localProvider = (Provider)paramXMLSignContext.getProperty("org.jcp.xml.dsig.internal.dom.SignatureProvider");
        signature = (localProvider == null ? Signature.getInstance(getJCAAlgorithm()) : Signature.getInstance(getJCAAlgorithm(), localProvider));
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new XMLSignatureException(localNoSuchAlgorithmException);
      }
    }
    signature.initSign((PrivateKey)paramKey);
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Signature provider:" + signature.getProvider());
      log.log(Level.FINE, "Signing with key: " + paramKey);
    }
    ((DOMSignedInfo)paramSignedInfo).canonicalize(paramXMLSignContext, new SignerOutputStream(signature));
    try
    {
      AbstractDOMSignatureMethod.Type localType = getAlgorithmType();
      if (localType == AbstractDOMSignatureMethod.Type.DSA)
      {
        int i = ((DSAKey)paramKey).getParams().getQ().bitLength();
        return JavaUtils.convertDsaASN1toXMLDSIG(signature.sign(), i / 8);
      }
      if (localType == AbstractDOMSignatureMethod.Type.ECDSA) {
        return SignatureECDSA.convertASN1toXMLDSIG(signature.sign());
      }
      return signature.sign();
    }
    catch (SignatureException localSignatureException)
    {
      throw new XMLSignatureException(localSignatureException);
    }
    catch (IOException localIOException)
    {
      throw new XMLSignatureException(localIOException);
    }
  }
  
  static final class SHA1withDSA
    extends DOMSignatureMethod
  {
    SHA1withDSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA1withDSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA1withDSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.DSA;
    }
  }
  
  static final class SHA1withECDSA
    extends DOMSignatureMethod
  {
    SHA1withECDSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA1withECDSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA1withECDSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.ECDSA;
    }
  }
  
  static final class SHA1withRSA
    extends DOMSignatureMethod
  {
    SHA1withRSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA1withRSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA1withRSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.RSA;
    }
  }
  
  static final class SHA256withDSA
    extends DOMSignatureMethod
  {
    SHA256withDSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA256withDSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2009/xmldsig11#dsa-sha256";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA256withDSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.DSA;
    }
  }
  
  static final class SHA256withECDSA
    extends DOMSignatureMethod
  {
    SHA256withECDSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA256withECDSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA256withECDSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.ECDSA;
    }
  }
  
  static final class SHA256withRSA
    extends DOMSignatureMethod
  {
    SHA256withRSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA256withRSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA256withRSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.RSA;
    }
  }
  
  static final class SHA384withECDSA
    extends DOMSignatureMethod
  {
    SHA384withECDSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA384withECDSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA384withECDSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.ECDSA;
    }
  }
  
  static final class SHA384withRSA
    extends DOMSignatureMethod
  {
    SHA384withRSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA384withRSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA384withRSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.RSA;
    }
  }
  
  static final class SHA512withECDSA
    extends DOMSignatureMethod
  {
    SHA512withECDSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA512withECDSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA512withECDSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.ECDSA;
    }
  }
  
  static final class SHA512withRSA
    extends DOMSignatureMethod
  {
    SHA512withRSA(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA512withRSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    }
    
    String getJCAAlgorithm()
    {
      return "SHA512withRSA";
    }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType()
    {
      return AbstractDOMSignatureMethod.Type.RSA;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMSignatureMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */