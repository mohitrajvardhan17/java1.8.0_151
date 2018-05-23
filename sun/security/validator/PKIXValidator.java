package sun.security.validator;

import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.action.GetBooleanAction;
import sun.security.provider.certpath.AlgorithmChecker;
import sun.security.provider.certpath.PKIXExtendedParameters;

public final class PKIXValidator
  extends Validator
{
  private static final boolean checkTLSRevocation = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.net.ssl.checkRevocation"))).booleanValue();
  private static final boolean TRY_VALIDATOR = true;
  private final Set<X509Certificate> trustedCerts;
  private final PKIXBuilderParameters parameterTemplate;
  private int certPathLength = -1;
  private final Map<X500Principal, List<PublicKey>> trustedSubjects;
  private final CertificateFactory factory;
  private final boolean plugin;
  
  PKIXValidator(String paramString, Collection<X509Certificate> paramCollection)
  {
    super("PKIX", paramString);
    if ((paramCollection instanceof Set)) {
      trustedCerts = ((Set)paramCollection);
    } else {
      trustedCerts = new HashSet(paramCollection);
    }
    HashSet localHashSet = new HashSet();
    Iterator localIterator1 = paramCollection.iterator();
    X509Certificate localX509Certificate;
    while (localIterator1.hasNext())
    {
      localX509Certificate = (X509Certificate)localIterator1.next();
      localHashSet.add(new TrustAnchor(localX509Certificate, null));
    }
    try
    {
      parameterTemplate = new PKIXBuilderParameters(localHashSet, null);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new RuntimeException("Unexpected error: " + localInvalidAlgorithmParameterException.toString(), localInvalidAlgorithmParameterException);
    }
    setDefaultParameters(paramString);
    trustedSubjects = new HashMap();
    Iterator localIterator2 = paramCollection.iterator();
    while (localIterator2.hasNext())
    {
      localX509Certificate = (X509Certificate)localIterator2.next();
      X500Principal localX500Principal = localX509Certificate.getSubjectX500Principal();
      Object localObject;
      if (trustedSubjects.containsKey(localX500Principal))
      {
        localObject = (List)trustedSubjects.get(localX500Principal);
      }
      else
      {
        localObject = new ArrayList();
        trustedSubjects.put(localX500Principal, localObject);
      }
      ((List)localObject).add(localX509Certificate.getPublicKey());
    }
    try
    {
      factory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException localCertificateException)
    {
      throw new RuntimeException("Internal error", localCertificateException);
    }
    plugin = paramString.equals("plugin code signing");
  }
  
  PKIXValidator(String paramString, PKIXBuilderParameters paramPKIXBuilderParameters)
  {
    super("PKIX", paramString);
    trustedCerts = new HashSet();
    Iterator localIterator = paramPKIXBuilderParameters.getTrustAnchors().iterator();
    Object localObject1;
    Object localObject2;
    while (localIterator.hasNext())
    {
      localObject1 = (TrustAnchor)localIterator.next();
      localObject2 = ((TrustAnchor)localObject1).getTrustedCert();
      if (localObject2 != null) {
        trustedCerts.add(localObject2);
      }
    }
    parameterTemplate = paramPKIXBuilderParameters;
    trustedSubjects = new HashMap();
    localIterator = trustedCerts.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (X509Certificate)localIterator.next();
      localObject2 = ((X509Certificate)localObject1).getSubjectX500Principal();
      Object localObject3;
      if (trustedSubjects.containsKey(localObject2))
      {
        localObject3 = (List)trustedSubjects.get(localObject2);
      }
      else
      {
        localObject3 = new ArrayList();
        trustedSubjects.put(localObject2, localObject3);
      }
      ((List)localObject3).add(((X509Certificate)localObject1).getPublicKey());
    }
    try
    {
      factory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException localCertificateException)
    {
      throw new RuntimeException("Internal error", localCertificateException);
    }
    plugin = paramString.equals("plugin code signing");
  }
  
  public Collection<X509Certificate> getTrustedCertificates()
  {
    return trustedCerts;
  }
  
  public int getCertPathLength()
  {
    return certPathLength;
  }
  
  private void setDefaultParameters(String paramString)
  {
    if ((paramString == "tls server") || (paramString == "tls client")) {
      parameterTemplate.setRevocationEnabled(checkTLSRevocation);
    } else {
      parameterTemplate.setRevocationEnabled(false);
    }
  }
  
  public PKIXBuilderParameters getParameters()
  {
    return parameterTemplate;
  }
  
  X509Certificate[] engineValidate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, AlgorithmConstraints paramAlgorithmConstraints, Object paramObject)
    throws CertificateException
  {
    if ((paramArrayOfX509Certificate == null) || (paramArrayOfX509Certificate.length == 0)) {
      throw new CertificateException("null or zero-length certificate chain");
    }
    PKIXExtendedParameters localPKIXExtendedParameters = null;
    try
    {
      localPKIXExtendedParameters = new PKIXExtendedParameters((PKIXBuilderParameters)parameterTemplate.clone(), (paramObject instanceof Timestamp) ? (Timestamp)paramObject : null, variant);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException1) {}
    if (paramAlgorithmConstraints != null) {
      localPKIXExtendedParameters.addCertPathChecker(new AlgorithmChecker(paramAlgorithmConstraints, null, variant));
    }
    Object localObject1 = null;
    X509Certificate[] arrayOfX509Certificate;
    for (int i = 0; i < paramArrayOfX509Certificate.length; i++)
    {
      localObject2 = paramArrayOfX509Certificate[i];
      localX500Principal = ((X509Certificate)localObject2).getSubjectX500Principal();
      if ((i != 0) && (!localX500Principal.equals(localObject1))) {
        return doBuild(paramArrayOfX509Certificate, paramCollection, localPKIXExtendedParameters);
      }
      if ((trustedCerts.contains(localObject2)) || ((trustedSubjects.containsKey(localX500Principal)) && (((List)trustedSubjects.get(localX500Principal)).contains(((X509Certificate)localObject2).getPublicKey()))))
      {
        if (i == 0) {
          return new X509Certificate[] { paramArrayOfX509Certificate[0] };
        }
        arrayOfX509Certificate = new X509Certificate[i];
        System.arraycopy(paramArrayOfX509Certificate, 0, arrayOfX509Certificate, 0, i);
        return doValidate(arrayOfX509Certificate, localPKIXExtendedParameters);
      }
      localObject1 = ((X509Certificate)localObject2).getIssuerX500Principal();
    }
    X509Certificate localX509Certificate = paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)];
    Object localObject2 = localX509Certificate.getIssuerX500Principal();
    X500Principal localX500Principal = localX509Certificate.getSubjectX500Principal();
    if ((trustedSubjects.containsKey(localObject2)) && (isSignatureValid((List)trustedSubjects.get(localObject2), localX509Certificate))) {
      return doValidate(paramArrayOfX509Certificate, localPKIXExtendedParameters);
    }
    if (plugin)
    {
      if (paramArrayOfX509Certificate.length > 1)
      {
        arrayOfX509Certificate = new X509Certificate[paramArrayOfX509Certificate.length - 1];
        System.arraycopy(paramArrayOfX509Certificate, 0, arrayOfX509Certificate, 0, arrayOfX509Certificate.length);
        try
        {
          localPKIXExtendedParameters.setTrustAnchors(Collections.singleton(new TrustAnchor(paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)], null)));
        }
        catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException2)
        {
          throw new CertificateException(localInvalidAlgorithmParameterException2);
        }
        doValidate(arrayOfX509Certificate, localPKIXExtendedParameters);
      }
      throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
    }
    return doBuild(paramArrayOfX509Certificate, paramCollection, localPKIXExtendedParameters);
  }
  
  private boolean isSignatureValid(List<PublicKey> paramList, X509Certificate paramX509Certificate)
  {
    if (plugin)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        PublicKey localPublicKey = (PublicKey)localIterator.next();
        try
        {
          paramX509Certificate.verify(localPublicKey);
          return true;
        }
        catch (Exception localException) {}
      }
      return false;
    }
    return true;
  }
  
  private static X509Certificate[] toArray(CertPath paramCertPath, TrustAnchor paramTrustAnchor)
    throws CertificateException
  {
    List localList = paramCertPath.getCertificates();
    X509Certificate[] arrayOfX509Certificate = new X509Certificate[localList.size() + 1];
    localList.toArray(arrayOfX509Certificate);
    X509Certificate localX509Certificate = paramTrustAnchor.getTrustedCert();
    if (localX509Certificate == null) {
      throw new ValidatorException("TrustAnchor must be specified as certificate");
    }
    arrayOfX509Certificate[(arrayOfX509Certificate.length - 1)] = localX509Certificate;
    return arrayOfX509Certificate;
  }
  
  private void setDate(PKIXBuilderParameters paramPKIXBuilderParameters)
  {
    Date localDate = validationDate;
    if (localDate != null) {
      paramPKIXBuilderParameters.setDate(localDate);
    }
  }
  
  private X509Certificate[] doValidate(X509Certificate[] paramArrayOfX509Certificate, PKIXBuilderParameters paramPKIXBuilderParameters)
    throws CertificateException
  {
    try
    {
      setDate(paramPKIXBuilderParameters);
      CertPathValidator localCertPathValidator = CertPathValidator.getInstance("PKIX");
      CertPath localCertPath = factory.generateCertPath(Arrays.asList(paramArrayOfX509Certificate));
      certPathLength = paramArrayOfX509Certificate.length;
      PKIXCertPathValidatorResult localPKIXCertPathValidatorResult = (PKIXCertPathValidatorResult)localCertPathValidator.validate(localCertPath, paramPKIXBuilderParameters);
      return toArray(localCertPath, localPKIXCertPathValidatorResult.getTrustAnchor());
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      throw new ValidatorException("PKIX path validation failed: " + localGeneralSecurityException.toString(), localGeneralSecurityException);
    }
  }
  
  private X509Certificate[] doBuild(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, PKIXBuilderParameters paramPKIXBuilderParameters)
    throws CertificateException
  {
    try
    {
      setDate(paramPKIXBuilderParameters);
      X509CertSelector localX509CertSelector = new X509CertSelector();
      localX509CertSelector.setCertificate(paramArrayOfX509Certificate[0]);
      paramPKIXBuilderParameters.setTargetCertConstraints(localX509CertSelector);
      ArrayList localArrayList = new ArrayList();
      localArrayList.addAll(Arrays.asList(paramArrayOfX509Certificate));
      if (paramCollection != null) {
        localArrayList.addAll(paramCollection);
      }
      CertStore localCertStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(localArrayList));
      paramPKIXBuilderParameters.addCertStore(localCertStore);
      CertPathBuilder localCertPathBuilder = CertPathBuilder.getInstance("PKIX");
      PKIXCertPathBuilderResult localPKIXCertPathBuilderResult = (PKIXCertPathBuilderResult)localCertPathBuilder.build(paramPKIXBuilderParameters);
      return toArray(localPKIXCertPathBuilderResult.getCertPath(), localPKIXCertPathBuilderResult.getTrustAnchor());
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      throw new ValidatorException("PKIX path building failed: " + localGeneralSecurityException.toString(), localGeneralSecurityException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\validator\PKIXValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */