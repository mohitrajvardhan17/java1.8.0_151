package sun.security.provider.certpath;

import java.math.BigInteger;
import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CRLException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;
import sun.security.util.AnchorCertificates;
import sun.security.util.ConstraintsParameters;
import sun.security.util.Debug;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.util.KeyUtil;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

public final class AlgorithmChecker
  extends PKIXCertPathChecker
{
  private static final Debug debug = Debug.getInstance("certpath");
  private final AlgorithmConstraints constraints;
  private final PublicKey trustedPubKey;
  private final Date pkixdate;
  private PublicKey prevPubKey;
  private final Timestamp jarTimestamp;
  private final String variant;
  private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
  private static final Set<CryptoPrimitive> KU_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE, CryptoPrimitive.KEY_ENCAPSULATION, CryptoPrimitive.PUBLIC_KEY_ENCRYPTION, CryptoPrimitive.KEY_AGREEMENT));
  private static final DisabledAlgorithmConstraints certPathDefaultConstraints = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
  private static final boolean publicCALimits = certPathDefaultConstraints.checkProperty("jdkCA");
  private boolean trustedMatch = false;
  
  public AlgorithmChecker(TrustAnchor paramTrustAnchor, String paramString)
  {
    this(paramTrustAnchor, certPathDefaultConstraints, null, null, paramString);
  }
  
  public AlgorithmChecker(AlgorithmConstraints paramAlgorithmConstraints, Timestamp paramTimestamp, String paramString)
  {
    this(null, paramAlgorithmConstraints, null, paramTimestamp, paramString);
  }
  
  public AlgorithmChecker(TrustAnchor paramTrustAnchor, AlgorithmConstraints paramAlgorithmConstraints, Date paramDate, Timestamp paramTimestamp, String paramString)
  {
    if (paramTrustAnchor != null)
    {
      if (paramTrustAnchor.getTrustedCert() != null)
      {
        trustedPubKey = paramTrustAnchor.getTrustedCert().getPublicKey();
        trustedMatch = checkFingerprint(paramTrustAnchor.getTrustedCert());
        if ((trustedMatch) && (debug != null)) {
          debug.println("trustedMatch = true");
        }
      }
      else
      {
        trustedPubKey = paramTrustAnchor.getCAPublicKey();
      }
    }
    else
    {
      trustedPubKey = null;
      if (debug != null) {
        debug.println("TrustAnchor is null, trustedMatch is false.");
      }
    }
    prevPubKey = trustedPubKey;
    constraints = (paramAlgorithmConstraints == null ? certPathDefaultConstraints : paramAlgorithmConstraints);
    pkixdate = (paramTimestamp != null ? paramTimestamp.getTimestamp() : paramDate);
    jarTimestamp = paramTimestamp;
    variant = (paramString == null ? "generic" : paramString);
  }
  
  public AlgorithmChecker(TrustAnchor paramTrustAnchor, Date paramDate, String paramString)
  {
    this(paramTrustAnchor, certPathDefaultConstraints, paramDate, null, paramString);
  }
  
  private static boolean checkFingerprint(X509Certificate paramX509Certificate)
  {
    if (!publicCALimits) {
      return false;
    }
    if (debug != null) {
      debug.println("AlgorithmChecker.contains: " + paramX509Certificate.getSigAlgName());
    }
    return AnchorCertificates.contains(paramX509Certificate);
  }
  
  public void init(boolean paramBoolean)
    throws CertPathValidatorException
  {
    if (!paramBoolean)
    {
      if (trustedPubKey != null) {
        prevPubKey = trustedPubKey;
      } else {
        prevPubKey = null;
      }
    }
    else {
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
    if ((!(paramCertificate instanceof X509Certificate)) || (constraints == null)) {
      return;
    }
    boolean[] arrayOfBoolean = ((X509Certificate)paramCertificate).getKeyUsage();
    if ((arrayOfBoolean != null) && (arrayOfBoolean.length < 9)) {
      throw new CertPathValidatorException("incorrect KeyUsage extension", null, null, -1, PKIXReason.INVALID_KEY_USAGE);
    }
    AlgorithmId localAlgorithmId;
    try
    {
      X509CertImpl localX509CertImpl = X509CertImpl.toImpl((X509Certificate)paramCertificate);
      localAlgorithmId = (AlgorithmId)localX509CertImpl.get("x509.algorithm");
    }
    catch (CertificateException localCertificateException)
    {
      throw new CertPathValidatorException(localCertificateException);
    }
    AlgorithmParameters localAlgorithmParameters = localAlgorithmId.getParameters();
    PublicKey localPublicKey = paramCertificate.getPublicKey();
    String str = ((X509Certificate)paramCertificate).getSigAlgName();
    if (!constraints.permits(SIGNATURE_PRIMITIVE_SET, str, localAlgorithmParameters)) {
      throw new CertPathValidatorException("Algorithm constraints check failed on signature algorithm: " + str, null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
    }
    Object localObject = KU_PRIMITIVE_SET;
    if (arrayOfBoolean != null)
    {
      localObject = EnumSet.noneOf(CryptoPrimitive.class);
      if ((arrayOfBoolean[0] != 0) || (arrayOfBoolean[1] != 0) || (arrayOfBoolean[5] != 0) || (arrayOfBoolean[6] != 0)) {
        ((Set)localObject).add(CryptoPrimitive.SIGNATURE);
      }
      if (arrayOfBoolean[2] != 0) {
        ((Set)localObject).add(CryptoPrimitive.KEY_ENCAPSULATION);
      }
      if (arrayOfBoolean[3] != 0) {
        ((Set)localObject).add(CryptoPrimitive.PUBLIC_KEY_ENCRYPTION);
      }
      if (arrayOfBoolean[4] != 0) {
        ((Set)localObject).add(CryptoPrimitive.KEY_AGREEMENT);
      }
      if (((Set)localObject).isEmpty()) {
        throw new CertPathValidatorException("incorrect KeyUsage extension bits", null, null, -1, PKIXReason.INVALID_KEY_USAGE);
      }
    }
    ConstraintsParameters localConstraintsParameters = new ConstraintsParameters((X509Certificate)paramCertificate, trustedMatch, pkixdate, jarTimestamp, variant);
    if ((constraints instanceof DisabledAlgorithmConstraints))
    {
      ((DisabledAlgorithmConstraints)constraints).permits(str, localConstraintsParameters);
    }
    else
    {
      certPathDefaultConstraints.permits(str, localConstraintsParameters);
      if (!constraints.permits((Set)localObject, localPublicKey)) {
        throw new CertPathValidatorException("Algorithm constraints check failed on key " + localPublicKey.getAlgorithm() + " with size of " + KeyUtil.getKeySize(localPublicKey) + "bits", null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
      }
    }
    if (prevPubKey == null)
    {
      prevPubKey = localPublicKey;
      return;
    }
    if (!constraints.permits(SIGNATURE_PRIMITIVE_SET, str, prevPubKey, localAlgorithmParameters)) {
      throw new CertPathValidatorException("Algorithm constraints check failed on signature algorithm: " + str, null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
    }
    if (PKIX.isDSAPublicKeyWithoutParams(localPublicKey))
    {
      if (!(prevPubKey instanceof DSAPublicKey)) {
        throw new CertPathValidatorException("Input key is not of a appropriate type for inheriting parameters");
      }
      DSAParams localDSAParams = ((DSAPublicKey)prevPubKey).getParams();
      if (localDSAParams == null) {
        throw new CertPathValidatorException("Key parameters missing from public key.");
      }
      try
      {
        BigInteger localBigInteger = ((DSAPublicKey)localPublicKey).getY();
        KeyFactory localKeyFactory = KeyFactory.getInstance("DSA");
        DSAPublicKeySpec localDSAPublicKeySpec = new DSAPublicKeySpec(localBigInteger, localDSAParams.getP(), localDSAParams.getQ(), localDSAParams.getG());
        localPublicKey = localKeyFactory.generatePublic(localDSAPublicKeySpec);
      }
      catch (GeneralSecurityException localGeneralSecurityException)
      {
        throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + localGeneralSecurityException.getMessage(), localGeneralSecurityException);
      }
    }
    prevPubKey = localPublicKey;
  }
  
  void trySetTrustAnchor(TrustAnchor paramTrustAnchor)
  {
    if (prevPubKey == null)
    {
      if (paramTrustAnchor == null) {
        throw new IllegalArgumentException("The trust anchor cannot be null");
      }
      if (paramTrustAnchor.getTrustedCert() != null)
      {
        prevPubKey = paramTrustAnchor.getTrustedCert().getPublicKey();
        trustedMatch = checkFingerprint(paramTrustAnchor.getTrustedCert());
        if ((trustedMatch) && (debug != null)) {
          debug.println("trustedMatch = true");
        }
      }
      else
      {
        prevPubKey = paramTrustAnchor.getCAPublicKey();
      }
    }
  }
  
  static void check(PublicKey paramPublicKey, X509CRL paramX509CRL, String paramString)
    throws CertPathValidatorException
  {
    X509CRLImpl localX509CRLImpl = null;
    try
    {
      localX509CRLImpl = X509CRLImpl.toImpl(paramX509CRL);
    }
    catch (CRLException localCRLException)
    {
      throw new CertPathValidatorException(localCRLException);
    }
    AlgorithmId localAlgorithmId = localX509CRLImpl.getSigAlgId();
    check(paramPublicKey, localAlgorithmId, paramString);
  }
  
  static void check(PublicKey paramPublicKey, AlgorithmId paramAlgorithmId, String paramString)
    throws CertPathValidatorException
  {
    String str = paramAlgorithmId.getName();
    AlgorithmParameters localAlgorithmParameters = paramAlgorithmId.getParameters();
    certPathDefaultConstraints.permits(new ConstraintsParameters(str, localAlgorithmParameters, paramPublicKey, paramString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\AlgorithmChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */