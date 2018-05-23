package sun.security.tools.keytool;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import sun.security.x509.X509Key;

public final class CertAndKeyGen
{
  private SecureRandom prng;
  private String sigAlg;
  private KeyPairGenerator keyGen;
  private PublicKey publicKey;
  private PrivateKey privateKey;
  
  public CertAndKeyGen(String paramString1, String paramString2)
    throws NoSuchAlgorithmException
  {
    keyGen = KeyPairGenerator.getInstance(paramString1);
    sigAlg = paramString2;
  }
  
  public CertAndKeyGen(String paramString1, String paramString2, String paramString3)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    if (paramString3 == null) {
      keyGen = KeyPairGenerator.getInstance(paramString1);
    } else {
      try
      {
        keyGen = KeyPairGenerator.getInstance(paramString1, paramString3);
      }
      catch (Exception localException)
      {
        keyGen = KeyPairGenerator.getInstance(paramString1);
      }
    }
    sigAlg = paramString2;
  }
  
  public void setRandom(SecureRandom paramSecureRandom)
  {
    prng = paramSecureRandom;
  }
  
  public void generate(int paramInt)
    throws InvalidKeyException
  {
    KeyPair localKeyPair;
    try
    {
      if (prng == null) {
        prng = new SecureRandom();
      }
      keyGen.initialize(paramInt, prng);
      localKeyPair = keyGen.generateKeyPair();
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentException(localException.getMessage());
    }
    publicKey = localKeyPair.getPublic();
    privateKey = localKeyPair.getPrivate();
    if (!"X.509".equalsIgnoreCase(publicKey.getFormat())) {
      throw new IllegalArgumentException("publicKey's is not X.509, but " + publicKey.getFormat());
    }
  }
  
  public X509Key getPublicKey()
  {
    if (!(publicKey instanceof X509Key)) {
      return null;
    }
    return (X509Key)publicKey;
  }
  
  public PublicKey getPublicKeyAnyway()
  {
    return publicKey;
  }
  
  public PrivateKey getPrivateKey()
  {
    return privateKey;
  }
  
  public X509Certificate getSelfCertificate(X500Name paramX500Name, Date paramDate, long paramLong)
    throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException
  {
    return getSelfCertificate(paramX500Name, paramDate, paramLong, null);
  }
  
  public X509Certificate getSelfCertificate(X500Name paramX500Name, Date paramDate, long paramLong, CertificateExtensions paramCertificateExtensions)
    throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException
  {
    try
    {
      Date localDate = new Date();
      localDate.setTime(paramDate.getTime() + paramLong * 1000L);
      CertificateValidity localCertificateValidity = new CertificateValidity(paramDate, localDate);
      X509CertInfo localX509CertInfo = new X509CertInfo();
      localX509CertInfo.set("version", new CertificateVersion(2));
      localX509CertInfo.set("serialNumber", new CertificateSerialNumber(new Random().nextInt() & 0x7FFFFFFF));
      AlgorithmId localAlgorithmId = AlgorithmId.get(sigAlg);
      localX509CertInfo.set("algorithmID", new CertificateAlgorithmId(localAlgorithmId));
      localX509CertInfo.set("subject", paramX500Name);
      localX509CertInfo.set("key", new CertificateX509Key(publicKey));
      localX509CertInfo.set("validity", localCertificateValidity);
      localX509CertInfo.set("issuer", paramX500Name);
      if (paramCertificateExtensions != null) {
        localX509CertInfo.set("extensions", paramCertificateExtensions);
      }
      X509CertImpl localX509CertImpl = new X509CertImpl(localX509CertInfo);
      localX509CertImpl.sign(privateKey, sigAlg);
      return localX509CertImpl;
    }
    catch (IOException localIOException)
    {
      throw new CertificateEncodingException("getSelfCert: " + localIOException.getMessage());
    }
  }
  
  public X509Certificate getSelfCertificate(X500Name paramX500Name, long paramLong)
    throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException
  {
    return getSelfCertificate(paramX500Name, new Date(), paramLong);
  }
  
  public PKCS10 getCertRequest(X500Name paramX500Name)
    throws InvalidKeyException, SignatureException
  {
    PKCS10 localPKCS10 = new PKCS10(publicKey);
    try
    {
      Signature localSignature = Signature.getInstance(sigAlg);
      localSignature.initSign(privateKey);
      localPKCS10.encodeAndSign(paramX500Name, localSignature);
    }
    catch (CertificateException localCertificateException)
    {
      throw new SignatureException(sigAlg + " CertificateException");
    }
    catch (IOException localIOException)
    {
      throw new SignatureException(sigAlg + " IOException");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new SignatureException(sigAlg + " unavailable?");
    }
    return localPKCS10;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\keytool\CertAndKeyGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */