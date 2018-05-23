package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac.IntegrityHmacMD5;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac.IntegrityHmacRIPEMD160;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac.IntegrityHmacSHA1;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac.IntegrityHmacSHA256;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac.IntegrityHmacSHA384;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac.IntegrityHmacSHA512;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSAMD5;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSARIPEMD160;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSASHA1;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSASHA256;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSASHA384;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSASHA512;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA.SHA256;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA.SignatureECDSASHA1;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA.SignatureECDSASHA256;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA.SignatureECDSASHA384;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA.SignatureECDSASHA512;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureAlgorithm
  extends Algorithm
{
  private static Logger log = Logger.getLogger(SignatureAlgorithm.class.getName());
  private static Map<String, Class<? extends SignatureAlgorithmSpi>> algorithmHash = new ConcurrentHashMap();
  private final SignatureAlgorithmSpi signatureAlgorithm;
  private final String algorithmURI;
  
  public SignatureAlgorithm(Document paramDocument, String paramString)
    throws XMLSecurityException
  {
    super(paramDocument, paramString);
    algorithmURI = paramString;
    signatureAlgorithm = getSignatureAlgorithmSpi(paramString);
    signatureAlgorithm.engineGetContextFromElement(constructionElement);
  }
  
  public SignatureAlgorithm(Document paramDocument, String paramString, int paramInt)
    throws XMLSecurityException
  {
    super(paramDocument, paramString);
    algorithmURI = paramString;
    signatureAlgorithm = getSignatureAlgorithmSpi(paramString);
    signatureAlgorithm.engineGetContextFromElement(constructionElement);
    signatureAlgorithm.engineSetHMACOutputLength(paramInt);
    ((IntegrityHmac)signatureAlgorithm).engineAddContextToElement(constructionElement);
  }
  
  public SignatureAlgorithm(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    this(paramElement, paramString, false);
  }
  
  public SignatureAlgorithm(Element paramElement, String paramString, boolean paramBoolean)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
    algorithmURI = getURI();
    Attr localAttr = paramElement.getAttributeNodeNS(null, "Id");
    if (localAttr != null) {
      paramElement.setIdAttributeNode(localAttr, true);
    }
    if ((paramBoolean) && (("http://www.w3.org/2001/04/xmldsig-more#hmac-md5".equals(algorithmURI)) || ("http://www.w3.org/2001/04/xmldsig-more#rsa-md5".equals(algorithmURI))))
    {
      Object[] arrayOfObject = { algorithmURI };
      throw new XMLSecurityException("signature.signatureAlgorithm", arrayOfObject);
    }
    signatureAlgorithm = getSignatureAlgorithmSpi(algorithmURI);
    signatureAlgorithm.engineGetContextFromElement(constructionElement);
  }
  
  private static SignatureAlgorithmSpi getSignatureAlgorithmSpi(String paramString)
    throws XMLSignatureException
  {
    try
    {
      Class localClass = (Class)algorithmHash.get(paramString);
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Create URI \"" + paramString + "\" class \"" + localClass + "\"");
      }
      return (SignatureAlgorithmSpi)localClass.newInstance();
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      arrayOfObject = new Object[] { paramString, localIllegalAccessException.getMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      arrayOfObject = new Object[] { paramString, localInstantiationException.getMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, localInstantiationException);
    }
    catch (NullPointerException localNullPointerException)
    {
      Object[] arrayOfObject = { paramString, localNullPointerException.getMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, localNullPointerException);
    }
  }
  
  public byte[] sign()
    throws XMLSignatureException
  {
    return signatureAlgorithm.engineSign();
  }
  
  public String getJCEAlgorithmString()
  {
    return signatureAlgorithm.engineGetJCEAlgorithmString();
  }
  
  public String getJCEProviderName()
  {
    return signatureAlgorithm.engineGetJCEProviderName();
  }
  
  public void update(byte[] paramArrayOfByte)
    throws XMLSignatureException
  {
    signatureAlgorithm.engineUpdate(paramArrayOfByte);
  }
  
  public void update(byte paramByte)
    throws XMLSignatureException
  {
    signatureAlgorithm.engineUpdate(paramByte);
  }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws XMLSignatureException
  {
    signatureAlgorithm.engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void initSign(Key paramKey)
    throws XMLSignatureException
  {
    signatureAlgorithm.engineInitSign(paramKey);
  }
  
  public void initSign(Key paramKey, SecureRandom paramSecureRandom)
    throws XMLSignatureException
  {
    signatureAlgorithm.engineInitSign(paramKey, paramSecureRandom);
  }
  
  public void initSign(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws XMLSignatureException
  {
    signatureAlgorithm.engineInitSign(paramKey, paramAlgorithmParameterSpec);
  }
  
  public void setParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws XMLSignatureException
  {
    signatureAlgorithm.engineSetParameter(paramAlgorithmParameterSpec);
  }
  
  public void initVerify(Key paramKey)
    throws XMLSignatureException
  {
    signatureAlgorithm.engineInitVerify(paramKey);
  }
  
  public boolean verify(byte[] paramArrayOfByte)
    throws XMLSignatureException
  {
    return signatureAlgorithm.engineVerify(paramArrayOfByte);
  }
  
  public final String getURI()
  {
    return constructionElement.getAttributeNS(null, "Algorithm");
  }
  
  public static void register(String paramString1, String paramString2)
    throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException
  {
    
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Try to register " + paramString1 + " " + paramString2);
    }
    Class localClass = (Class)algorithmHash.get(paramString1);
    Object localObject;
    if (localClass != null)
    {
      localObject = new Object[] { paramString1, localClass };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", (Object[])localObject);
    }
    try
    {
      localObject = ClassLoaderUtils.loadClass(paramString2, SignatureAlgorithm.class);
      algorithmHash.put(paramString1, localObject);
    }
    catch (NullPointerException localNullPointerException)
    {
      Object[] arrayOfObject = { paramString1, localNullPointerException.getMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, localNullPointerException);
    }
  }
  
  public static void register(String paramString, Class<? extends SignatureAlgorithmSpi> paramClass)
    throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException
  {
    
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Try to register " + paramString + " " + paramClass);
    }
    Class localClass = (Class)algorithmHash.get(paramString);
    if (localClass != null)
    {
      Object[] arrayOfObject = { paramString, localClass };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
    }
    algorithmHash.put(paramString, paramClass);
  }
  
  public static void registerDefaultAlgorithms()
  {
    algorithmHash.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", SignatureDSA.class);
    algorithmHash.put("http://www.w3.org/2009/xmldsig11#dsa-sha256", SignatureDSA.SHA256.class);
    algorithmHash.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", SignatureBaseRSA.SignatureRSASHA1.class);
    algorithmHash.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", IntegrityHmac.IntegrityHmacSHA1.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-md5", SignatureBaseRSA.SignatureRSAMD5.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160", SignatureBaseRSA.SignatureRSARIPEMD160.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", SignatureBaseRSA.SignatureRSASHA256.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", SignatureBaseRSA.SignatureRSASHA384.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", SignatureBaseRSA.SignatureRSASHA512.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", SignatureECDSA.SignatureECDSASHA1.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", SignatureECDSA.SignatureECDSASHA256.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", SignatureECDSA.SignatureECDSASHA384.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", SignatureECDSA.SignatureECDSASHA512.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-md5", IntegrityHmac.IntegrityHmacMD5.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160", IntegrityHmac.IntegrityHmacRIPEMD160.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", IntegrityHmac.IntegrityHmacSHA256.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", IntegrityHmac.IntegrityHmacSHA384.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", IntegrityHmac.IntegrityHmacSHA512.class);
  }
  
  public String getBaseNamespace()
  {
    return "http://www.w3.org/2000/09/xmldsig#";
  }
  
  public String getBaseLocalName()
  {
    return "SignatureMethod";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\SignatureAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */