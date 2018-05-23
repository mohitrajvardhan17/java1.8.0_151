package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.io.IOException;
import java.math.BigInteger;
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
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAParams;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignatureDSA
  extends SignatureAlgorithmSpi
{
  private static Logger log = Logger.getLogger(SignatureDSA.class.getName());
  private Signature signatureAlgorithm = null;
  private int size;
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
  }
  
  public SignatureDSA()
    throws XMLSignatureException
  {
    String str1 = JCEMapper.translateURItoJCEID(engineGetURI());
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Created SignatureDSA using " + str1);
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
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Called DSA.verify() on " + Base64.encode(paramArrayOfByte));
      }
      byte[] arrayOfByte = JavaUtils.convertDsaXMLDSIGtoASN1(paramArrayOfByte, size / 8);
      return signatureAlgorithm.verify(arrayOfByte);
    }
    catch (SignatureException localSignatureException)
    {
      throw new XMLSignatureException("empty", localSignatureException);
    }
    catch (IOException localIOException)
    {
      throw new XMLSignatureException("empty", localIOException);
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
    size = ((DSAKey)paramKey).getParams().getQ().bitLength();
  }
  
  protected byte[] engineSign()
    throws XMLSignatureException
  {
    try
    {
      byte[] arrayOfByte = signatureAlgorithm.sign();
      return JavaUtils.convertDsaASN1toXMLDSIG(arrayOfByte, size / 8);
    }
    catch (IOException localIOException)
    {
      throw new XMLSignatureException("empty", localIOException);
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
    size = ((DSAKey)paramKey).getParams().getQ().bitLength();
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
    size = ((DSAKey)paramKey).getParams().getQ().bitLength();
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
    throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnDSA");
  }
  
  public static class SHA256
    extends SignatureDSA
  {
    public SHA256()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2009/xmldsig11#dsa-sha256";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\implementations\SignatureDSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */