package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.jcp.xml.dsig.internal.MacOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMHMACSignatureMethod
  extends AbstractDOMSignatureMethod
{
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  static final String HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
  static final String HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
  static final String HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
  private Mac hmac;
  private int outputLength;
  private boolean outputLengthSet;
  private SignatureMethodParameterSpec params;
  
  DOMHMACSignatureMethod(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    checkParams((SignatureMethodParameterSpec)paramAlgorithmParameterSpec);
    params = ((SignatureMethodParameterSpec)paramAlgorithmParameterSpec);
  }
  
  DOMHMACSignatureMethod(Element paramElement)
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
  
  void checkParams(SignatureMethodParameterSpec paramSignatureMethodParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramSignatureMethodParameterSpec != null)
    {
      if (!(paramSignatureMethodParameterSpec instanceof HMACParameterSpec)) {
        throw new InvalidAlgorithmParameterException("params must be of type HMACParameterSpec");
      }
      outputLength = ((HMACParameterSpec)paramSignatureMethodParameterSpec).getOutputLength();
      outputLengthSet = true;
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Setting outputLength from HMACParameterSpec to: " + outputLength);
      }
    }
  }
  
  public final AlgorithmParameterSpec getParameterSpec()
  {
    return params;
  }
  
  SignatureMethodParameterSpec unmarshalParams(Element paramElement)
    throws MarshalException
  {
    outputLength = Integer.valueOf(paramElement.getFirstChild().getNodeValue()).intValue();
    outputLengthSet = true;
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "unmarshalled outputLength: " + outputLength);
    }
    return new HMACParameterSpec(outputLength);
  }
  
  void marshalParams(Element paramElement, String paramString)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramElement);
    Element localElement = DOMUtils.createElement(localDocument, "HMACOutputLength", "http://www.w3.org/2000/09/xmldsig#", paramString);
    localElement.appendChild(localDocument.createTextNode(String.valueOf(outputLength)));
    paramElement.appendChild(localElement);
  }
  
  boolean verify(Key paramKey, SignedInfo paramSignedInfo, byte[] paramArrayOfByte, XMLValidateContext paramXMLValidateContext)
    throws InvalidKeyException, SignatureException, XMLSignatureException
  {
    if ((paramKey == null) || (paramSignedInfo == null) || (paramArrayOfByte == null)) {
      throw new NullPointerException();
    }
    if (!(paramKey instanceof SecretKey)) {
      throw new InvalidKeyException("key must be SecretKey");
    }
    if (hmac == null) {
      try
      {
        hmac = Mac.getInstance(getJCAAlgorithm());
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new XMLSignatureException(localNoSuchAlgorithmException);
      }
    }
    if ((outputLengthSet) && (outputLength < getDigestLength())) {
      throw new XMLSignatureException("HMACOutputLength must not be less than " + getDigestLength());
    }
    hmac.init((SecretKey)paramKey);
    ((DOMSignedInfo)paramSignedInfo).canonicalize(paramXMLValidateContext, new MacOutputStream(hmac));
    byte[] arrayOfByte = hmac.doFinal();
    return MessageDigest.isEqual(paramArrayOfByte, arrayOfByte);
  }
  
  byte[] sign(Key paramKey, SignedInfo paramSignedInfo, XMLSignContext paramXMLSignContext)
    throws InvalidKeyException, XMLSignatureException
  {
    if ((paramKey == null) || (paramSignedInfo == null)) {
      throw new NullPointerException();
    }
    if (!(paramKey instanceof SecretKey)) {
      throw new InvalidKeyException("key must be SecretKey");
    }
    if (hmac == null) {
      try
      {
        hmac = Mac.getInstance(getJCAAlgorithm());
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new XMLSignatureException(localNoSuchAlgorithmException);
      }
    }
    if ((outputLengthSet) && (outputLength < getDigestLength())) {
      throw new XMLSignatureException("HMACOutputLength must not be less than " + getDigestLength());
    }
    hmac.init((SecretKey)paramKey);
    ((DOMSignedInfo)paramSignedInfo).canonicalize(paramXMLSignContext, new MacOutputStream(hmac));
    return hmac.doFinal();
  }
  
  boolean paramsEqual(AlgorithmParameterSpec paramAlgorithmParameterSpec)
  {
    if (getParameterSpec() == paramAlgorithmParameterSpec) {
      return true;
    }
    if (!(paramAlgorithmParameterSpec instanceof HMACParameterSpec)) {
      return false;
    }
    HMACParameterSpec localHMACParameterSpec = (HMACParameterSpec)paramAlgorithmParameterSpec;
    return outputLength == localHMACParameterSpec.getOutputLength();
  }
  
  AbstractDOMSignatureMethod.Type getAlgorithmType()
  {
    return AbstractDOMSignatureMethod.Type.HMAC;
  }
  
  abstract int getDigestLength();
  
  static final class SHA1
    extends DOMHMACSignatureMethod
  {
    SHA1(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA1(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    }
    
    String getJCAAlgorithm()
    {
      return "HmacSHA1";
    }
    
    int getDigestLength()
    {
      return 160;
    }
  }
  
  static final class SHA256
    extends DOMHMACSignatureMethod
  {
    SHA256(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA256(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    }
    
    String getJCAAlgorithm()
    {
      return "HmacSHA256";
    }
    
    int getDigestLength()
    {
      return 256;
    }
  }
  
  static final class SHA384
    extends DOMHMACSignatureMethod
  {
    SHA384(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA384(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    }
    
    String getJCAAlgorithm()
    {
      return "HmacSHA384";
    }
    
    int getDigestLength()
    {
      return 384;
    }
  }
  
  static final class SHA512
    extends DOMHMACSignatureMethod
  {
    SHA512(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA512(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    }
    
    String getJCAAlgorithm()
    {
      return "HmacSHA512";
    }
    
    int getDigestLength()
    {
      return 512;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMHMACSignatureMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */