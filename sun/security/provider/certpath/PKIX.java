package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;

class PKIX
{
  private static final Debug debug = Debug.getInstance("certpath");
  
  private PKIX() {}
  
  static boolean isDSAPublicKeyWithoutParams(PublicKey paramPublicKey)
  {
    return ((paramPublicKey instanceof DSAPublicKey)) && (((DSAPublicKey)paramPublicKey).getParams() == null);
  }
  
  static ValidatorParams checkParams(CertPath paramCertPath, CertPathParameters paramCertPathParameters)
    throws InvalidAlgorithmParameterException
  {
    if (!(paramCertPathParameters instanceof PKIXParameters)) {
      throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXParameters");
    }
    return new ValidatorParams(paramCertPath, (PKIXParameters)paramCertPathParameters);
  }
  
  static BuilderParams checkBuilderParams(CertPathParameters paramCertPathParameters)
    throws InvalidAlgorithmParameterException
  {
    if (!(paramCertPathParameters instanceof PKIXBuilderParameters)) {
      throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXBuilderParameters");
    }
    return new BuilderParams((PKIXBuilderParameters)paramCertPathParameters);
  }
  
  static class BuilderParams
    extends PKIX.ValidatorParams
  {
    private PKIXBuilderParameters params;
    private List<CertStore> stores;
    private X500Principal targetSubject;
    
    BuilderParams(PKIXBuilderParameters paramPKIXBuilderParameters)
      throws InvalidAlgorithmParameterException
    {
      super();
      checkParams(paramPKIXBuilderParameters);
    }
    
    private void checkParams(PKIXBuilderParameters paramPKIXBuilderParameters)
      throws InvalidAlgorithmParameterException
    {
      CertSelector localCertSelector = targetCertConstraints();
      if (!(localCertSelector instanceof X509CertSelector)) {
        throw new InvalidAlgorithmParameterException("the targetCertConstraints parameter must be an X509CertSelector");
      }
      params = paramPKIXBuilderParameters;
      targetSubject = getTargetSubject(certStores(), (X509CertSelector)targetCertConstraints());
    }
    
    List<CertStore> certStores()
    {
      if (stores == null)
      {
        stores = new ArrayList(params.getCertStores());
        Collections.sort(stores, new PKIX.CertStoreComparator(null));
      }
      return stores;
    }
    
    int maxPathLength()
    {
      return params.getMaxPathLength();
    }
    
    PKIXBuilderParameters params()
    {
      return params;
    }
    
    X500Principal targetSubject()
    {
      return targetSubject;
    }
    
    private static X500Principal getTargetSubject(List<CertStore> paramList, X509CertSelector paramX509CertSelector)
      throws InvalidAlgorithmParameterException
    {
      X500Principal localX500Principal = paramX509CertSelector.getSubject();
      if (localX500Principal != null) {
        return localX500Principal;
      }
      X509Certificate localX509Certificate1 = paramX509CertSelector.getCertificate();
      if (localX509Certificate1 != null) {
        localX500Principal = localX509Certificate1.getSubjectX500Principal();
      }
      if (localX500Principal != null) {
        return localX500Principal;
      }
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        CertStore localCertStore = (CertStore)localIterator.next();
        try
        {
          Collection localCollection = localCertStore.getCertificates(paramX509CertSelector);
          if (!localCollection.isEmpty())
          {
            X509Certificate localX509Certificate2 = (X509Certificate)localCollection.iterator().next();
            return localX509Certificate2.getSubjectX500Principal();
          }
        }
        catch (CertStoreException localCertStoreException)
        {
          if (PKIX.debug != null)
          {
            PKIX.debug.println("BuilderParams.getTargetSubjectDN: non-fatal exception retrieving certs: " + localCertStoreException);
            localCertStoreException.printStackTrace();
          }
        }
      }
      throw new InvalidAlgorithmParameterException("Could not determine unique target subject");
    }
  }
  
  private static class CertStoreComparator
    implements Comparator<CertStore>
  {
    private CertStoreComparator() {}
    
    public int compare(CertStore paramCertStore1, CertStore paramCertStore2)
    {
      if ((paramCertStore1.getType().equals("Collection")) || ((paramCertStore1.getCertStoreParameters() instanceof CollectionCertStoreParameters))) {
        return -1;
      }
      return 1;
    }
  }
  
  static class CertStoreTypeException
    extends CertStoreException
  {
    private static final long serialVersionUID = 7463352639238322556L;
    private final String type;
    
    CertStoreTypeException(String paramString, CertStoreException paramCertStoreException)
    {
      super(paramCertStoreException.getCause());
      type = paramString;
    }
    
    String getType()
    {
      return type;
    }
  }
  
  static class ValidatorParams
  {
    private final PKIXParameters params;
    private CertPath certPath;
    private List<PKIXCertPathChecker> checkers;
    private List<CertStore> stores;
    private boolean gotDate;
    private Date date;
    private Set<String> policies;
    private boolean gotConstraints;
    private CertSelector constraints;
    private Set<TrustAnchor> anchors;
    private List<X509Certificate> certs;
    private Timestamp timestamp;
    private String variant;
    
    ValidatorParams(CertPath paramCertPath, PKIXParameters paramPKIXParameters)
      throws InvalidAlgorithmParameterException
    {
      this(paramPKIXParameters);
      if ((!paramCertPath.getType().equals("X.509")) && (!paramCertPath.getType().equals("X509"))) {
        throw new InvalidAlgorithmParameterException("inappropriate CertPath type specified, must be X.509 or X509");
      }
      certPath = paramCertPath;
    }
    
    ValidatorParams(PKIXParameters paramPKIXParameters)
      throws InvalidAlgorithmParameterException
    {
      if ((paramPKIXParameters instanceof PKIXExtendedParameters))
      {
        timestamp = ((PKIXExtendedParameters)paramPKIXParameters).getTimestamp();
        variant = ((PKIXExtendedParameters)paramPKIXParameters).getVariant();
      }
      anchors = paramPKIXParameters.getTrustAnchors();
      Iterator localIterator = anchors.iterator();
      while (localIterator.hasNext())
      {
        TrustAnchor localTrustAnchor = (TrustAnchor)localIterator.next();
        if (localTrustAnchor.getNameConstraints() != null) {
          throw new InvalidAlgorithmParameterException("name constraints in trust anchor not supported");
        }
      }
      params = paramPKIXParameters;
    }
    
    CertPath certPath()
    {
      return certPath;
    }
    
    void setCertPath(CertPath paramCertPath)
    {
      certPath = paramCertPath;
    }
    
    List<X509Certificate> certificates()
    {
      if (certs == null) {
        if (certPath == null)
        {
          certs = Collections.emptyList();
        }
        else
        {
          ArrayList localArrayList = new ArrayList(certPath.getCertificates());
          Collections.reverse(localArrayList);
          certs = localArrayList;
        }
      }
      return certs;
    }
    
    List<PKIXCertPathChecker> certPathCheckers()
    {
      if (checkers == null) {
        checkers = params.getCertPathCheckers();
      }
      return checkers;
    }
    
    List<CertStore> certStores()
    {
      if (stores == null) {
        stores = params.getCertStores();
      }
      return stores;
    }
    
    Date date()
    {
      if (!gotDate)
      {
        date = params.getDate();
        if (date == null) {
          date = new Date();
        }
        gotDate = true;
      }
      return date;
    }
    
    Set<String> initialPolicies()
    {
      if (policies == null) {
        policies = params.getInitialPolicies();
      }
      return policies;
    }
    
    CertSelector targetCertConstraints()
    {
      if (!gotConstraints)
      {
        constraints = params.getTargetCertConstraints();
        gotConstraints = true;
      }
      return constraints;
    }
    
    Set<TrustAnchor> trustAnchors()
    {
      return anchors;
    }
    
    boolean revocationEnabled()
    {
      return params.isRevocationEnabled();
    }
    
    boolean policyMappingInhibited()
    {
      return params.isPolicyMappingInhibited();
    }
    
    boolean explicitPolicyRequired()
    {
      return params.isExplicitPolicyRequired();
    }
    
    boolean policyQualifiersRejected()
    {
      return params.getPolicyQualifiersRejected();
    }
    
    String sigProvider()
    {
      return params.getSigProvider();
    }
    
    boolean anyPolicyInhibited()
    {
      return params.isAnyPolicyInhibited();
    }
    
    PKIXParameters getPKIXParameters()
    {
      return params;
    }
    
    Timestamp timestamp()
    {
      return timestamp;
    }
    
    String variant()
    {
      return variant;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\PKIX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */