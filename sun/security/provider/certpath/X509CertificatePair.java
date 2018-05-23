package sun.security.provider.certpath;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import javax.security.auth.x500.X500Principal;
import sun.security.provider.X509Factory;
import sun.security.util.Cache;
import sun.security.util.Cache.EqualByteArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.X509CertImpl;

public class X509CertificatePair
{
  private static final byte TAG_FORWARD = 0;
  private static final byte TAG_REVERSE = 1;
  private X509Certificate forward;
  private X509Certificate reverse;
  private byte[] encoded;
  private static final Cache<Object, X509CertificatePair> cache = Cache.newSoftMemoryCache(750);
  
  public X509CertificatePair() {}
  
  public X509CertificatePair(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2)
    throws CertificateException
  {
    if ((paramX509Certificate1 == null) && (paramX509Certificate2 == null)) {
      throw new CertificateException("at least one of certificate pair must be non-null");
    }
    forward = paramX509Certificate1;
    reverse = paramX509Certificate2;
    checkPair();
  }
  
  private X509CertificatePair(byte[] paramArrayOfByte)
    throws CertificateException
  {
    try
    {
      parse(new DerValue(paramArrayOfByte));
      encoded = paramArrayOfByte;
    }
    catch (IOException localIOException)
    {
      throw new CertificateException(localIOException.toString());
    }
    checkPair();
  }
  
  public static synchronized void clearCache()
  {
    cache.clear();
  }
  
  public static synchronized X509CertificatePair generateCertificatePair(byte[] paramArrayOfByte)
    throws CertificateException
  {
    Cache.EqualByteArray localEqualByteArray = new Cache.EqualByteArray(paramArrayOfByte);
    X509CertificatePair localX509CertificatePair = (X509CertificatePair)cache.get(localEqualByteArray);
    if (localX509CertificatePair != null) {
      return localX509CertificatePair;
    }
    localX509CertificatePair = new X509CertificatePair(paramArrayOfByte);
    localEqualByteArray = new Cache.EqualByteArray(encoded);
    cache.put(localEqualByteArray, localX509CertificatePair);
    return localX509CertificatePair;
  }
  
  public void setForward(X509Certificate paramX509Certificate)
    throws CertificateException
  {
    checkPair();
    forward = paramX509Certificate;
  }
  
  public void setReverse(X509Certificate paramX509Certificate)
    throws CertificateException
  {
    checkPair();
    reverse = paramX509Certificate;
  }
  
  public X509Certificate getForward()
  {
    return forward;
  }
  
  public X509Certificate getReverse()
  {
    return reverse;
  }
  
  public byte[] getEncoded()
    throws CertificateEncodingException
  {
    try
    {
      if (encoded == null)
      {
        DerOutputStream localDerOutputStream = new DerOutputStream();
        emit(localDerOutputStream);
        encoded = localDerOutputStream.toByteArray();
      }
    }
    catch (IOException localIOException)
    {
      throw new CertificateEncodingException(localIOException.toString());
    }
    return encoded;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("X.509 Certificate Pair: [\n");
    if (forward != null) {
      localStringBuilder.append("  Forward: ").append(forward).append("\n");
    }
    if (reverse != null) {
      localStringBuilder.append("  Reverse: ").append(reverse).append("\n");
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  private void parse(DerValue paramDerValue)
    throws IOException, CertificateException
  {
    if (tag != 48) {
      throw new IOException("Sequence tag missing for X509CertificatePair");
    }
    while ((data != null) && (data.available() != 0))
    {
      DerValue localDerValue = data.getDerValue();
      int i = (short)(byte)(tag & 0x1F);
      switch (i)
      {
      case 0: 
        if ((localDerValue.isContextSpecific()) && (localDerValue.isConstructed()))
        {
          if (forward != null) {
            throw new IOException("Duplicate forward certificate in X509CertificatePair");
          }
          localDerValue = data.getDerValue();
          forward = X509Factory.intern(new X509CertImpl(localDerValue.toByteArray()));
        }
        break;
      case 1: 
        if ((localDerValue.isContextSpecific()) && (localDerValue.isConstructed()))
        {
          if (reverse != null) {
            throw new IOException("Duplicate reverse certificate in X509CertificatePair");
          }
          localDerValue = data.getDerValue();
          reverse = X509Factory.intern(new X509CertImpl(localDerValue.toByteArray()));
        }
        break;
      default: 
        throw new IOException("Invalid encoding of X509CertificatePair");
      }
    }
    if ((forward == null) && (reverse == null)) {
      throw new CertificateException("at least one of certificate pair must be non-null");
    }
  }
  
  private void emit(DerOutputStream paramDerOutputStream)
    throws IOException, CertificateEncodingException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2;
    if (forward != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putDerValue(new DerValue(forward.getEncoded()));
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    }
    if (reverse != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putDerValue(new DerValue(reverse.getEncoded()));
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    }
    paramDerOutputStream.write((byte)48, localDerOutputStream1);
  }
  
  private void checkPair()
    throws CertificateException
  {
    if ((forward == null) || (reverse == null)) {
      return;
    }
    X500Principal localX500Principal1 = forward.getSubjectX500Principal();
    X500Principal localX500Principal2 = forward.getIssuerX500Principal();
    X500Principal localX500Principal3 = reverse.getSubjectX500Principal();
    X500Principal localX500Principal4 = reverse.getIssuerX500Principal();
    if ((!localX500Principal2.equals(localX500Principal3)) || (!localX500Principal4.equals(localX500Principal1))) {
      throw new CertificateException("subject and issuer names in forward and reverse certificates do not match");
    }
    try
    {
      PublicKey localPublicKey = reverse.getPublicKey();
      if ((!(localPublicKey instanceof DSAPublicKey)) || (((DSAPublicKey)localPublicKey).getParams() != null)) {
        forward.verify(localPublicKey);
      }
      localPublicKey = forward.getPublicKey();
      if ((!(localPublicKey instanceof DSAPublicKey)) || (((DSAPublicKey)localPublicKey).getParams() != null)) {
        reverse.verify(localPublicKey);
      }
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      throw new CertificateException("invalid signature: " + localGeneralSecurityException.getMessage());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\X509CertificatePair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */