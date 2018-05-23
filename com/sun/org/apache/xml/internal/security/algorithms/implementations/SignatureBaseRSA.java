package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SignatureBaseRSA
  extends SignatureAlgorithmSpi
{
  private static Logger log = Logger.getLogger(SignatureBaseRSA.class.getName());
  private Signature signatureAlgorithm = null;
  
  public abstract String engineGetURI();
  
  public SignatureBaseRSA()
    throws XMLSignatureException
  {
    String str1 = JCEMapper.translateURItoJCEID(engineGetURI());
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Created SignatureRSA using " + str1);
    }
    String str2 = JCEMapper.getProviderId();
    try
    {
      if (str2 == null) {
        signatureAlgorithm = Signature.getInstance(str1);
      } else {
        signatureAlgorithm = Signature.getInstance(str1, str2);
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      arrayOfObject = new Object[] { str1, localNoSuchAlgorithmException.getLocalizedMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject);
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      Object[] arrayOfObject = { str1, localNoSuchProviderException.getLocalizedMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject);
    }
  }
  
  protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws XMLSignatureException
  {
    try
    {
      signatureAlgorithm.setParameter(paramAlgorithmParameterSpec);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new XMLSignatureException("empty", localInvalidAlgorithmParameterException);
    }
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte)
    throws XMLSignatureException
  {
    try
    {
      return signatureAlgorithm.verify(paramArrayOfByte);
    }
    catch (SignatureException localSignatureException)
    {
      throw new XMLSignatureException("empty", localSignatureException);
    }
  }
  
  protected void engineInitVerify(Key paramKey)
    throws XMLSignatureException
  {
    Object localObject;
    if (!(paramKey instanceof PublicKey))
    {
      String str = paramKey.getClass().getName();
      localObject = PublicKey.class.getName();
      Object[] arrayOfObject = { str, localObject };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    }
    try
    {
      signatureAlgorithm.initVerify((PublicKey)paramKey);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      localObject = signatureAlgorithm;
      try
      {
        signatureAlgorithm = Signature.getInstance(signatureAlgorithm.getAlgorithm());
      }
      catch (Exception localException)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Exception when reinstantiating Signature:" + localException);
        }
        signatureAlgorithm = ((Signature)localObject);
      }
      throw new XMLSignatureException("empty", localInvalidKeyException);
    }
  }
  
  protected byte[] engineSign()
    throws XMLSignatureException
  {
    try
    {
      return signatureAlgorithm.sign();
    }
    catch (SignatureException localSignatureException)
    {
      throw new XMLSignatureException("empty", localSignatureException);
    }
  }
  
  protected void engineInitSign(Key paramKey, SecureRandom paramSecureRandom)
    throws XMLSignatureException
  {
    if (!(paramKey instanceof PrivateKey))
    {
      String str1 = paramKey.getClass().getName();
      String str2 = PrivateKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    }
    try
    {
      signatureAlgorithm.initSign((PrivateKey)paramKey, paramSecureRandom);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new XMLSignatureException("empty", localInvalidKeyException);
    }
  }
  
  protected void engineInitSign(Key paramKey)
    throws XMLSignatureException
  {
    if (!(paramKey instanceof PrivateKey))
    {
      String str1 = paramKey.getClass().getName();
      String str2 = PrivateKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    }
    try
    {
      signatureAlgorithm.initSign((PrivateKey)paramKey);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new XMLSignatureException("empty", localInvalidKeyException);
    }
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte)
    throws XMLSignatureException
  {
    try
    {
      signatureAlgorithm.update(paramArrayOfByte);
    }
    catch (SignatureException localSignatureException)
    {
      throw new XMLSignatureException("empty", localSignatureException);
    }
  }
  
  protected void engineUpdate(byte paramByte)
    throws XMLSignatureException
  {
    try
    {
      signatureAlgorithm.update(paramByte);
    }
    catch (SignatureException localSignatureException)
    {
      throw new XMLSignatureException("empty", localSignatureException);
    }
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws XMLSignatureException
  {
    try
    {
      signatureAlgorithm.update(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (SignatureException localSignatureException)
    {
      throw new XMLSignatureException("empty", localSignatureException);
    }
  }
  
  protected String engineGetJCEAlgorithmString()
  {
    return signatureAlgorithm.getAlgorithm();
  }
  
  protected String engineGetJCEProviderName()
  {
    return signatureAlgorithm.getProvider().getName();
  }
  
  protected void engineSetHMACOutputLength(int paramInt)
    throws XMLSignatureException
  {
    throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
  }
  
  protected void engineInitSign(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws XMLSignatureException
  {
    throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnRSA");
  }
  
  public static class SignatureRSAMD5
    extends SignatureBaseRSA
  {
    public SignatureRSAMD5()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
    }
  }
  
  public static class SignatureRSARIPEMD160
    extends SignatureBaseRSA
  {
    public SignatureRSARIPEMD160()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
    }
  }
  
  public static class SignatureRSASHA1
    extends SignatureBaseRSA
  {
    public SignatureRSASHA1()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    }
  }
  
  public static class SignatureRSASHA256
    extends SignatureBaseRSA
  {
    public SignatureRSASHA256()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    }
  }
  
  public static class SignatureRSASHA384
    extends SignatureBaseRSA
  {
    public SignatureRSASHA384()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    }
  }
  
  public static class SignatureRSASHA512
    extends SignatureBaseRSA
  {
    public SignatureRSASHA512()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\implementations\SignatureBaseRSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */