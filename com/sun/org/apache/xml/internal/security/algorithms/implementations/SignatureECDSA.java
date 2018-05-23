package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.IOException;
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

public abstract class SignatureECDSA
  extends SignatureAlgorithmSpi
{
  private static Logger log = Logger.getLogger(SignatureECDSA.class.getName());
  private Signature signatureAlgorithm = null;
  
  public abstract String engineGetURI();
  
  public static byte[] convertASN1toXMLDSIG(byte[] paramArrayOfByte)
    throws IOException
  {
    if ((paramArrayOfByte.length < 8) || (paramArrayOfByte[0] != 48)) {
      throw new IOException("Invalid ASN.1 format of ECDSA signature");
    }
    int i;
    if (paramArrayOfByte[1] > 0) {
      i = 2;
    } else if (paramArrayOfByte[1] == -127) {
      i = 3;
    } else {
      throw new IOException("Invalid ASN.1 format of ECDSA signature");
    }
    int j = paramArrayOfByte[(i + 1)];
    for (int k = j; (k > 0) && (paramArrayOfByte[(i + 2 + j - k)] == 0); k--) {}
    int m = paramArrayOfByte[(i + 2 + j + 1)];
    for (int n = m; (n > 0) && (paramArrayOfByte[(i + 2 + j + 2 + m - n)] == 0); n--) {}
    int i1 = Math.max(k, n);
    if (((paramArrayOfByte[(i - 1)] & 0xFF) != paramArrayOfByte.length - i) || ((paramArrayOfByte[(i - 1)] & 0xFF) != 2 + j + 2 + m) || (paramArrayOfByte[i] != 2) || (paramArrayOfByte[(i + 2 + j)] != 2)) {
      throw new IOException("Invalid ASN.1 format of ECDSA signature");
    }
    byte[] arrayOfByte = new byte[2 * i1];
    System.arraycopy(paramArrayOfByte, i + 2 + j - k, arrayOfByte, i1 - k, k);
    System.arraycopy(paramArrayOfByte, i + 2 + j + 2 + m - n, arrayOfByte, 2 * i1 - n, n);
    return arrayOfByte;
  }
  
  public static byte[] convertXMLDSIGtoASN1(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = paramArrayOfByte.length / 2;
    for (int j = i; (j > 0) && (paramArrayOfByte[(i - j)] == 0); j--) {}
    int k = j;
    if (paramArrayOfByte[(i - j)] < 0) {
      k++;
    }
    for (int m = i; (m > 0) && (paramArrayOfByte[(2 * i - m)] == 0); m--) {}
    int n = m;
    if (paramArrayOfByte[(2 * i - m)] < 0) {
      n++;
    }
    int i1 = 2 + k + 2 + n;
    if (i1 > 255) {
      throw new IOException("Invalid XMLDSIG format of ECDSA signature");
    }
    byte[] arrayOfByte;
    int i2;
    if (i1 < 128)
    {
      arrayOfByte = new byte[4 + k + 2 + n];
      i2 = 1;
    }
    else
    {
      arrayOfByte = new byte[5 + k + 2 + n];
      arrayOfByte[1] = -127;
      i2 = 2;
    }
    arrayOfByte[0] = 48;
    arrayOfByte[(i2++)] = ((byte)i1);
    arrayOfByte[(i2++)] = 2;
    arrayOfByte[(i2++)] = ((byte)k);
    System.arraycopy(paramArrayOfByte, i - j, arrayOfByte, i2 + k - j, j);
    i2 += k;
    arrayOfByte[(i2++)] = 2;
    arrayOfByte[(i2++)] = ((byte)n);
    System.arraycopy(paramArrayOfByte, 2 * i - m, arrayOfByte, i2 + n - m, m);
    return arrayOfByte;
  }
  
  public SignatureECDSA()
    throws XMLSignatureException
  {
    String str1 = JCEMapper.translateURItoJCEID(engineGetURI());
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Created SignatureECDSA using " + str1);
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
      byte[] arrayOfByte = convertXMLDSIGtoASN1(paramArrayOfByte);
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Called ECDSA.verify() on " + Base64.encode(paramArrayOfByte));
      }
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
  }
  
  protected byte[] engineSign()
    throws XMLSignatureException
  {
    try
    {
      byte[] arrayOfByte = signatureAlgorithm.sign();
      return convertASN1toXMLDSIG(arrayOfByte);
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
  
  public static class SignatureECDSASHA1
    extends SignatureECDSA
  {
    public SignatureECDSASHA1()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    }
  }
  
  public static class SignatureECDSASHA256
    extends SignatureECDSA
  {
    public SignatureECDSASHA256()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    }
  }
  
  public static class SignatureECDSASHA384
    extends SignatureECDSA
  {
    public SignatureECDSASHA384()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    }
  }
  
  public static class SignatureECDSASHA512
    extends SignatureECDSA
  {
    public SignatureECDSASHA512()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\implementations\SignatureECDSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */