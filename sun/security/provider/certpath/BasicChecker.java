package sun.security.provider.certpath;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.X500Name;

class BasicChecker
  extends PKIXCertPathChecker
{
  private static final Debug debug = Debug.getInstance("certpath");
  private final PublicKey trustedPubKey;
  private final X500Principal caName;
  private final Date date;
  private final String sigProvider;
  private final boolean sigOnly;
  private X500Principal prevSubject;
  private PublicKey prevPubKey;
  
  BasicChecker(TrustAnchor paramTrustAnchor, Date paramDate, String paramString, boolean paramBoolean)
  {
    if (paramTrustAnchor.getTrustedCert() != null)
    {
      trustedPubKey = paramTrustAnchor.getTrustedCert().getPublicKey();
      caName = paramTrustAnchor.getTrustedCert().getSubjectX500Principal();
    }
    else
    {
      trustedPubKey = paramTrustAnchor.getCAPublicKey();
      caName = paramTrustAnchor.getCA();
    }
    date = paramDate;
    sigProvider = paramString;
    sigOnly = paramBoolean;
    prevPubKey = trustedPubKey;
  }
  
  public void init(boolean paramBoolean)
    throws CertPathValidatorException
  {
    if (!paramBoolean)
    {
      prevPubKey = trustedPubKey;
      if (PKIX.isDSAPublicKeyWithoutParams(prevPubKey)) {
        throw new CertPathValidatorException("Key parameters missing");
      }
      prevSubject = caName;
    }
    else
    {
      throw new CertPathValidatorException("forward checking not supported");
    }
  }
  
  public boolean isForwardCheckingSupported()
  {
    return false;
  }
  
  public Set<String> getSupportedExtensions()
  {
    return null;
  }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection)
    throws CertPathValidatorException
  {
    X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
    if (!sigOnly)
    {
      verifyValidity(localX509Certificate);
      verifyNameChaining(localX509Certificate);
    }
    verifySignature(localX509Certificate);
    updateState(localX509Certificate);
  }
  
  private void verifySignature(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    String str = "signature";
    if (debug != null) {
      debug.println("---checking " + str + "...");
    }
    try
    {
      paramX509Certificate.verify(prevPubKey, sigProvider);
    }
    catch (SignatureException localSignatureException)
    {
      throw new CertPathValidatorException(str + " check failed", localSignatureException, null, -1, CertPathValidatorException.BasicReason.INVALID_SIGNATURE);
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      throw new CertPathValidatorException(str + " check failed", localGeneralSecurityException);
    }
    if (debug != null) {
      debug.println(str + " verified.");
    }
  }
  
  private void verifyValidity(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    String str = "validity";
    if (debug != null) {
      debug.println("---checking " + str + ":" + date.toString() + "...");
    }
    try
    {
      paramX509Certificate.checkValidity(date);
    }
    catch (CertificateExpiredException localCertificateExpiredException)
    {
      throw new CertPathValidatorException(str + " check failed", localCertificateExpiredException, null, -1, CertPathValidatorException.BasicReason.EXPIRED);
    }
    catch (CertificateNotYetValidException localCertificateNotYetValidException)
    {
      throw new CertPathValidatorException(str + " check failed", localCertificateNotYetValidException, null, -1, CertPathValidatorException.BasicReason.NOT_YET_VALID);
    }
    if (debug != null) {
      debug.println(str + " verified.");
    }
  }
  
  private void verifyNameChaining(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    if (prevSubject != null)
    {
      String str = "subject/issuer name chaining";
      if (debug != null) {
        debug.println("---checking " + str + "...");
      }
      X500Principal localX500Principal = paramX509Certificate.getIssuerX500Principal();
      if (X500Name.asX500Name(localX500Principal).isEmpty()) {
        throw new CertPathValidatorException(str + " check failed: empty/null issuer DN in certificate is invalid", null, null, -1, PKIXReason.NAME_CHAINING);
      }
      if (!localX500Principal.equals(prevSubject)) {
        throw new CertPathValidatorException(str + " check failed", null, null, -1, PKIXReason.NAME_CHAINING);
      }
      if (debug != null) {
        debug.println(str + " verified.");
      }
    }
  }
  
  private void updateState(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    PublicKey localPublicKey = paramX509Certificate.getPublicKey();
    if (debug != null) {
      debug.println("BasicChecker.updateState issuer: " + paramX509Certificate.getIssuerX500Principal().toString() + "; subject: " + paramX509Certificate.getSubjectX500Principal() + "; serial#: " + paramX509Certificate.getSerialNumber().toString());
    }
    if (PKIX.isDSAPublicKeyWithoutParams(localPublicKey))
    {
      localPublicKey = makeInheritedParamsKey(localPublicKey, prevPubKey);
      if (debug != null) {
        debug.println("BasicChecker.updateState Made key with inherited params");
      }
    }
    prevPubKey = localPublicKey;
    prevSubject = paramX509Certificate.getSubjectX500Principal();
  }
  
  static PublicKey makeInheritedParamsKey(PublicKey paramPublicKey1, PublicKey paramPublicKey2)
    throws CertPathValidatorException
  {
    if ((!(paramPublicKey1 instanceof DSAPublicKey)) || (!(paramPublicKey2 instanceof DSAPublicKey))) {
      throw new CertPathValidatorException("Input key is not appropriate type for inheriting parameters");
    }
    DSAParams localDSAParams = ((DSAPublicKey)paramPublicKey2).getParams();
    if (localDSAParams == null) {
      throw new CertPathValidatorException("Key parameters missing");
    }
    try
    {
      BigInteger localBigInteger = ((DSAPublicKey)paramPublicKey1).getY();
      KeyFactory localKeyFactory = KeyFactory.getInstance("DSA");
      DSAPublicKeySpec localDSAPublicKeySpec = new DSAPublicKeySpec(localBigInteger, localDSAParams.getP(), localDSAParams.getQ(), localDSAParams.getG());
      return localKeyFactory.generatePublic(localDSAPublicKeySpec);
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + localGeneralSecurityException.getMessage(), localGeneralSecurityException);
    }
  }
  
  PublicKey getPublicKey()
  {
    return prevPubKey;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\BasicChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */