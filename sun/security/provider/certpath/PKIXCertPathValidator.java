package sun.security.provider.certpath;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertPathChecker;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXReason;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.x509.X509CertImpl;

public final class PKIXCertPathValidator
  extends CertPathValidatorSpi
{
  private static final Debug debug = Debug.getInstance("certpath");
  
  public PKIXCertPathValidator() {}
  
  public CertPathChecker engineGetRevocationChecker()
  {
    return new RevocationChecker();
  }
  
  public CertPathValidatorResult engineValidate(CertPath paramCertPath, CertPathParameters paramCertPathParameters)
    throws CertPathValidatorException, InvalidAlgorithmParameterException
  {
    PKIX.ValidatorParams localValidatorParams = PKIX.checkParams(paramCertPath, paramCertPathParameters);
    return validate(localValidatorParams);
  }
  
  private static PKIXCertPathValidatorResult validate(PKIX.ValidatorParams paramValidatorParams)
    throws CertPathValidatorException
  {
    if (debug != null) {
      debug.println("PKIXCertPathValidator.engineValidate()...");
    }
    AdaptableX509CertSelector localAdaptableX509CertSelector = null;
    List localList = paramValidatorParams.certificates();
    if (!localList.isEmpty())
    {
      localAdaptableX509CertSelector = new AdaptableX509CertSelector();
      localObject = (X509Certificate)localList.get(0);
      localAdaptableX509CertSelector.setSubject(((X509Certificate)localObject).getIssuerX500Principal());
      try
      {
        X509CertImpl localX509CertImpl = X509CertImpl.toImpl((X509Certificate)localObject);
        localAdaptableX509CertSelector.setSkiAndSerialNumber(localX509CertImpl.getAuthorityKeyIdentifierExtension());
      }
      catch (CertificateException|IOException localCertificateException) {}
    }
    Object localObject = null;
    Iterator localIterator = paramValidatorParams.trustAnchors().iterator();
    while (localIterator.hasNext())
    {
      TrustAnchor localTrustAnchor = (TrustAnchor)localIterator.next();
      X509Certificate localX509Certificate = localTrustAnchor.getTrustedCert();
      if (localX509Certificate != null)
      {
        if ((localAdaptableX509CertSelector != null) && (!localAdaptableX509CertSelector.match(localX509Certificate)))
        {
          if (debug == null) {
            continue;
          }
          debug.println("NO - don't try this trustedCert");
          continue;
        }
        if (debug != null)
        {
          debug.println("YES - try this trustedCert");
          debug.println("anchor.getTrustedCert().getSubjectX500Principal() = " + localX509Certificate.getSubjectX500Principal());
        }
      }
      else if (debug != null)
      {
        debug.println("PKIXCertPathValidator.engineValidate(): anchor.getTrustedCert() == null");
      }
      try
      {
        return validate(localTrustAnchor, paramValidatorParams);
      }
      catch (CertPathValidatorException localCertPathValidatorException)
      {
        localObject = localCertPathValidatorException;
      }
    }
    if (localObject != null) {
      throw ((Throwable)localObject);
    }
    throw new CertPathValidatorException("Path does not chain with any of the trust anchors", null, null, -1, PKIXReason.NO_TRUST_ANCHOR);
  }
  
  private static PKIXCertPathValidatorResult validate(TrustAnchor paramTrustAnchor, PKIX.ValidatorParams paramValidatorParams)
    throws CertPathValidatorException
  {
    UntrustedChecker localUntrustedChecker = new UntrustedChecker();
    X509Certificate localX509Certificate = paramTrustAnchor.getTrustedCert();
    if (localX509Certificate != null) {
      localUntrustedChecker.check(localX509Certificate);
    }
    int i = paramValidatorParams.certificates().size();
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(localUntrustedChecker);
    localArrayList.add(new AlgorithmChecker(paramTrustAnchor, null, paramValidatorParams.date(), paramValidatorParams.timestamp(), paramValidatorParams.variant()));
    localArrayList.add(new KeyChecker(i, paramValidatorParams.targetCertConstraints()));
    localArrayList.add(new ConstraintsChecker(i));
    PolicyNodeImpl localPolicyNodeImpl = new PolicyNodeImpl(null, "2.5.29.32.0", null, false, Collections.singleton("2.5.29.32.0"), false);
    PolicyChecker localPolicyChecker = new PolicyChecker(paramValidatorParams.initialPolicies(), i, paramValidatorParams.explicitPolicyRequired(), paramValidatorParams.policyMappingInhibited(), paramValidatorParams.anyPolicyInhibited(), paramValidatorParams.policyQualifiersRejected(), localPolicyNodeImpl);
    localArrayList.add(localPolicyChecker);
    BasicChecker localBasicChecker = new BasicChecker(paramTrustAnchor, paramValidatorParams.timestamp() == null ? paramValidatorParams.date() : paramValidatorParams.timestamp().getTimestamp(), paramValidatorParams.sigProvider(), false);
    localArrayList.add(localBasicChecker);
    int j = 0;
    List localList = paramValidatorParams.certPathCheckers();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator.next();
      if ((localPKIXCertPathChecker instanceof PKIXRevocationChecker))
      {
        if (j != 0) {
          throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified");
        }
        j = 1;
        if ((localPKIXCertPathChecker instanceof RevocationChecker)) {
          ((RevocationChecker)localPKIXCertPathChecker).init(paramTrustAnchor, paramValidatorParams);
        }
      }
    }
    if ((paramValidatorParams.revocationEnabled()) && (j == 0)) {
      localArrayList.add(new RevocationChecker(paramTrustAnchor, paramValidatorParams));
    }
    localArrayList.addAll(localList);
    PKIXMasterCertPathValidator.validate(paramValidatorParams.certPath(), paramValidatorParams.certificates(), localArrayList);
    return new PKIXCertPathValidatorResult(paramTrustAnchor, localPolicyChecker.getPolicyTree(), localBasicChecker.getPublicKey());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\PKIXCertPathValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */