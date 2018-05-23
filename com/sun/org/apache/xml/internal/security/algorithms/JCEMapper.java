package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

public class JCEMapper
{
  private static Logger log = Logger.getLogger(JCEMapper.class.getName());
  private static Map<String, Algorithm> algorithmsMap = new ConcurrentHashMap();
  private static String providerName = null;
  
  public JCEMapper() {}
  
  public static void register(String paramString, Algorithm paramAlgorithm)
  {
    JavaUtils.checkRegisterPermission();
    algorithmsMap.put(paramString, paramAlgorithm);
  }
  
  public static void registerDefaultAlgorithms()
  {
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#md5", new Algorithm("", "MD5", "MessageDigest"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#ripemd160", new Algorithm("", "RIPEMD160", "MessageDigest"));
    algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#sha1", new Algorithm("", "SHA-1", "MessageDigest"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#sha256", new Algorithm("", "SHA-256", "MessageDigest"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#sha384", new Algorithm("", "SHA-384", "MessageDigest"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#sha512", new Algorithm("", "SHA-512", "MessageDigest"));
    algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", new Algorithm("", "SHA1withDSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2009/xmldsig11#dsa-sha256", new Algorithm("", "SHA256withDSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-md5", new Algorithm("", "MD5withRSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160", new Algorithm("", "RIPEMD160withRSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", new Algorithm("", "SHA1withRSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", new Algorithm("", "SHA256withRSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", new Algorithm("", "SHA384withRSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", new Algorithm("", "SHA512withRSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", new Algorithm("", "SHA1withECDSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", new Algorithm("", "SHA256withECDSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", new Algorithm("", "SHA384withECDSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", new Algorithm("", "SHA512withECDSA", "Signature"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-md5", new Algorithm("", "HmacMD5", "Mac"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160", new Algorithm("", "HMACRIPEMD160", "Mac"));
    algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", new Algorithm("", "HmacSHA1", "Mac"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", new Algorithm("", "HmacSHA256", "Mac"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", new Algorithm("", "HmacSHA384", "Mac"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", new Algorithm("", "HmacSHA512", "Mac"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", new Algorithm("DESede", "DESede/CBC/ISO10126Padding", "BlockEncryption", 192));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#aes128-cbc", new Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 128));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#aes192-cbc", new Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 192));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#aes256-cbc", new Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 256));
    algorithmsMap.put("http://www.w3.org/2009/xmlenc11#aes128-gcm", new Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 128));
    algorithmsMap.put("http://www.w3.org/2009/xmlenc11#aes192-gcm", new Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 192));
    algorithmsMap.put("http://www.w3.org/2009/xmlenc11#aes256-gcm", new Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 256));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#rsa-1_5", new Algorithm("RSA", "RSA/ECB/PKCS1Padding", "KeyTransport"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p", new Algorithm("RSA", "RSA/ECB/OAEPPadding", "KeyTransport"));
    algorithmsMap.put("http://www.w3.org/2009/xmlenc11#rsa-oaep", new Algorithm("RSA", "RSA/ECB/OAEPPadding", "KeyTransport"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#dh", new Algorithm("", "", "KeyAgreement"));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-tripledes", new Algorithm("DESede", "DESedeWrap", "SymmetricKeyWrap", 192));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-aes128", new Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 128));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-aes192", new Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 192));
    algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-aes256", new Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 256));
  }
  
  public static String translateURItoJCEID(String paramString)
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Request for URI " + paramString);
    }
    Algorithm localAlgorithm = (Algorithm)algorithmsMap.get(paramString);
    if (localAlgorithm != null) {
      return jceName;
    }
    return null;
  }
  
  public static String getAlgorithmClassFromURI(String paramString)
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Request for URI " + paramString);
    }
    Algorithm localAlgorithm = (Algorithm)algorithmsMap.get(paramString);
    if (localAlgorithm != null) {
      return algorithmClass;
    }
    return null;
  }
  
  public static int getKeyLengthFromURI(String paramString)
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Request for URI " + paramString);
    }
    Algorithm localAlgorithm = (Algorithm)algorithmsMap.get(paramString);
    if (localAlgorithm != null) {
      return keyLength;
    }
    return 0;
  }
  
  public static String getJCEKeyAlgorithmFromURI(String paramString)
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Request for URI " + paramString);
    }
    Algorithm localAlgorithm = (Algorithm)algorithmsMap.get(paramString);
    if (localAlgorithm != null) {
      return requiredKey;
    }
    return null;
  }
  
  public static String getProviderId()
  {
    return providerName;
  }
  
  public static void setProviderId(String paramString)
  {
    JavaUtils.checkRegisterPermission();
    providerName = paramString;
  }
  
  public static class Algorithm
  {
    final String requiredKey;
    final String jceName;
    final String algorithmClass;
    final int keyLength;
    
    public Algorithm(Element paramElement)
    {
      requiredKey = paramElement.getAttribute("RequiredKey");
      jceName = paramElement.getAttribute("JCEName");
      algorithmClass = paramElement.getAttribute("AlgorithmClass");
      if (paramElement.hasAttribute("KeyLength")) {
        keyLength = Integer.parseInt(paramElement.getAttribute("KeyLength"));
      } else {
        keyLength = 0;
      }
    }
    
    public Algorithm(String paramString1, String paramString2)
    {
      this(paramString1, paramString2, null, 0);
    }
    
    public Algorithm(String paramString1, String paramString2, String paramString3)
    {
      this(paramString1, paramString2, paramString3, 0);
    }
    
    public Algorithm(String paramString1, String paramString2, int paramInt)
    {
      this(paramString1, paramString2, null, paramInt);
    }
    
    public Algorithm(String paramString1, String paramString2, String paramString3, int paramInt)
    {
      requiredKey = paramString1;
      jceName = paramString2;
      algorithmClass = paramString3;
      keyLength = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\JCEMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */