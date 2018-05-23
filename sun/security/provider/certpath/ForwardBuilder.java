package sun.security.provider.certpath;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

class ForwardBuilder
  extends Builder
{
  private static final Debug debug = Debug.getInstance("certpath");
  private final Set<X509Certificate> trustedCerts;
  private final Set<X500Principal> trustedSubjectDNs;
  private final Set<TrustAnchor> trustAnchors;
  private X509CertSelector eeSelector;
  private AdaptableX509CertSelector caSelector;
  private X509CertSelector caTargetSelector;
  TrustAnchor trustAnchor;
  private boolean searchAllCertStores = true;
  
  ForwardBuilder(PKIX.BuilderParams paramBuilderParams, boolean paramBoolean)
  {
    super(paramBuilderParams);
    trustAnchors = paramBuilderParams.trustAnchors();
    trustedCerts = new HashSet(trustAnchors.size());
    trustedSubjectDNs = new HashSet(trustAnchors.size());
    Iterator localIterator = trustAnchors.iterator();
    while (localIterator.hasNext())
    {
      TrustAnchor localTrustAnchor = (TrustAnchor)localIterator.next();
      X509Certificate localX509Certificate = localTrustAnchor.getTrustedCert();
      if (localX509Certificate != null)
      {
        trustedCerts.add(localX509Certificate);
        trustedSubjectDNs.add(localX509Certificate.getSubjectX500Principal());
      }
      else
      {
        trustedSubjectDNs.add(localTrustAnchor.getCA());
      }
    }
    searchAllCertStores = paramBoolean;
  }
  
  Collection<X509Certificate> getMatchingCerts(State paramState, List<CertStore> paramList)
    throws CertStoreException, CertificateException, IOException
  {
    if (debug != null) {
      debug.println("ForwardBuilder.getMatchingCerts()...");
    }
    ForwardState localForwardState = (ForwardState)paramState;
    PKIXCertComparator localPKIXCertComparator = new PKIXCertComparator(trustedSubjectDNs, cert);
    TreeSet localTreeSet = new TreeSet(localPKIXCertComparator);
    if (localForwardState.isInitial()) {
      getMatchingEECerts(localForwardState, paramList, localTreeSet);
    }
    getMatchingCACerts(localForwardState, paramList, localTreeSet);
    return localTreeSet;
  }
  
  private void getMatchingEECerts(ForwardState paramForwardState, List<CertStore> paramList, Collection<X509Certificate> paramCollection)
    throws IOException
  {
    if (debug != null) {
      debug.println("ForwardBuilder.getMatchingEECerts()...");
    }
    if (eeSelector == null)
    {
      eeSelector = ((X509CertSelector)targetCertConstraints.clone());
      eeSelector.setCertificateValid(buildParams.date());
      if (buildParams.explicitPolicyRequired()) {
        eeSelector.setPolicy(getMatchingPolicies());
      }
      eeSelector.setBasicConstraints(-2);
    }
    addMatchingCerts(eeSelector, paramList, paramCollection, searchAllCertStores);
  }
  
  private void getMatchingCACerts(ForwardState paramForwardState, List<CertStore> paramList, Collection<X509Certificate> paramCollection)
    throws IOException
  {
    if (debug != null) {
      debug.println("ForwardBuilder.getMatchingCACerts()...");
    }
    int i = paramCollection.size();
    Object localObject1 = null;
    if (paramForwardState.isInitial())
    {
      if (targetCertConstraints.getBasicConstraints() == -2) {
        return;
      }
      if (debug != null) {
        debug.println("ForwardBuilder.getMatchingCACerts(): the target is a CA");
      }
      if (caTargetSelector == null)
      {
        caTargetSelector = ((X509CertSelector)targetCertConstraints.clone());
        if (buildParams.explicitPolicyRequired()) {
          caTargetSelector.setPolicy(getMatchingPolicies());
        }
      }
      localObject1 = caTargetSelector;
    }
    else
    {
      if (caSelector == null)
      {
        caSelector = new AdaptableX509CertSelector();
        if (buildParams.explicitPolicyRequired()) {
          caSelector.setPolicy(getMatchingPolicies());
        }
      }
      caSelector.setSubject(issuerDN);
      CertPathHelper.setPathToNames(caSelector, subjectNamesTraversed);
      caSelector.setValidityPeriod(cert.getNotBefore(), cert.getNotAfter());
      localObject1 = caSelector;
    }
    ((X509CertSelector)localObject1).setBasicConstraints(-1);
    Object localObject2 = trustedCerts.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      X509Certificate localX509Certificate = (X509Certificate)((Iterator)localObject2).next();
      if (((X509CertSelector)localObject1).match(localX509Certificate))
      {
        if (debug != null) {
          debug.println("ForwardBuilder.getMatchingCACerts: found matching trust anchor.\n  SN: " + Debug.toHexString(localX509Certificate.getSerialNumber()) + "\n  Subject: " + localX509Certificate.getSubjectX500Principal() + "\n  Issuer: " + localX509Certificate.getIssuerX500Principal());
        }
        if ((paramCollection.add(localX509Certificate)) && (!searchAllCertStores)) {
          return;
        }
      }
    }
    ((X509CertSelector)localObject1).setCertificateValid(buildParams.date());
    ((X509CertSelector)localObject1).setBasicConstraints(traversedCACerts);
    if (((paramForwardState.isInitial()) || (buildParams.maxPathLength() == -1) || (buildParams.maxPathLength() > traversedCACerts)) && (addMatchingCerts((X509CertSelector)localObject1, paramList, paramCollection, searchAllCertStores)) && (!searchAllCertStores)) {
      return;
    }
    if ((!paramForwardState.isInitial()) && (Builder.USE_AIA))
    {
      localObject2 = cert.getAuthorityInfoAccessExtension();
      if (localObject2 != null) {
        getCerts((AuthorityInfoAccessExtension)localObject2, paramCollection);
      }
    }
    if (debug != null)
    {
      int j = paramCollection.size() - i;
      debug.println("ForwardBuilder.getMatchingCACerts: found " + j + " CA certs");
    }
  }
  
  private boolean getCerts(AuthorityInfoAccessExtension paramAuthorityInfoAccessExtension, Collection<X509Certificate> paramCollection)
  {
    if (!Builder.USE_AIA) {
      return false;
    }
    List localList = paramAuthorityInfoAccessExtension.getAccessDescriptions();
    if ((localList == null) || (localList.isEmpty())) {
      return false;
    }
    boolean bool = false;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      AccessDescription localAccessDescription = (AccessDescription)localIterator.next();
      CertStore localCertStore = URICertStore.getInstance(localAccessDescription);
      if (localCertStore != null) {
        try
        {
          if (paramCollection.addAll(localCertStore.getCertificates(caSelector)))
          {
            bool = true;
            if (!searchAllCertStores) {
              return true;
            }
          }
        }
        catch (CertStoreException localCertStoreException)
        {
          if (debug != null)
          {
            debug.println("exception getting certs from CertStore:");
            localCertStoreException.printStackTrace();
          }
        }
      }
    }
    return bool;
  }
  
  void verifyCert(X509Certificate paramX509Certificate, State paramState, List<X509Certificate> paramList)
    throws GeneralSecurityException
  {
    if (debug != null) {
      debug.println("ForwardBuilder.verifyCert(SN: " + Debug.toHexString(paramX509Certificate.getSerialNumber()) + "\n  Issuer: " + paramX509Certificate.getIssuerX500Principal() + ")\n  Subject: " + paramX509Certificate.getSubjectX500Principal() + ")");
    }
    ForwardState localForwardState = (ForwardState)paramState;
    untrustedChecker.check(paramX509Certificate, Collections.emptySet());
    Object localObject;
    if (paramList != null)
    {
      Iterator localIterator1 = paramList.iterator();
      while (localIterator1.hasNext())
      {
        localObject = (X509Certificate)localIterator1.next();
        if (paramX509Certificate.equals(localObject))
        {
          if (debug != null) {
            debug.println("loop detected!!");
          }
          throw new CertPathValidatorException("loop detected");
        }
      }
    }
    boolean bool = trustedCerts.contains(paramX509Certificate);
    if (!bool)
    {
      localObject = paramX509Certificate.getCriticalExtensionOIDs();
      if (localObject == null) {
        localObject = Collections.emptySet();
      }
      Iterator localIterator2 = forwardCheckers.iterator();
      PKIXCertPathChecker localPKIXCertPathChecker;
      while (localIterator2.hasNext())
      {
        localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator2.next();
        localPKIXCertPathChecker.check(paramX509Certificate, (Collection)localObject);
      }
      localIterator2 = buildParams.certPathCheckers().iterator();
      while (localIterator2.hasNext())
      {
        localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator2.next();
        if (!localPKIXCertPathChecker.isForwardCheckingSupported())
        {
          Set localSet = localPKIXCertPathChecker.getSupportedExtensions();
          if (localSet != null) {
            ((Set)localObject).removeAll(localSet);
          }
        }
      }
      if (!((Set)localObject).isEmpty())
      {
        ((Set)localObject).remove(PKIXExtensions.BasicConstraints_Id.toString());
        ((Set)localObject).remove(PKIXExtensions.NameConstraints_Id.toString());
        ((Set)localObject).remove(PKIXExtensions.CertificatePolicies_Id.toString());
        ((Set)localObject).remove(PKIXExtensions.PolicyMappings_Id.toString());
        ((Set)localObject).remove(PKIXExtensions.PolicyConstraints_Id.toString());
        ((Set)localObject).remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
        ((Set)localObject).remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
        ((Set)localObject).remove(PKIXExtensions.KeyUsage_Id.toString());
        ((Set)localObject).remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
        if (!((Set)localObject).isEmpty()) {
          throw new CertPathValidatorException("Unrecognized critical extension(s)", null, null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT);
        }
      }
    }
    if (localForwardState.isInitial()) {
      return;
    }
    if (!bool)
    {
      if (paramX509Certificate.getBasicConstraints() == -1) {
        throw new CertificateException("cert is NOT a CA cert");
      }
      KeyChecker.verifyCAKeyUsage(paramX509Certificate);
    }
    if (!localForwardState.keyParamsNeeded()) {
      cert.verify(paramX509Certificate.getPublicKey(), buildParams.sigProvider());
    }
  }
  
  boolean isPathCompleted(X509Certificate paramX509Certificate)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = trustAnchors.iterator();
    TrustAnchor localTrustAnchor;
    X500Principal localX500Principal;
    PublicKey localPublicKey;
    while (localIterator.hasNext())
    {
      localTrustAnchor = (TrustAnchor)localIterator.next();
      if (localTrustAnchor.getTrustedCert() != null)
      {
        if (paramX509Certificate.equals(localTrustAnchor.getTrustedCert()))
        {
          trustAnchor = localTrustAnchor;
          return true;
        }
      }
      else
      {
        localX500Principal = localTrustAnchor.getCA();
        localPublicKey = localTrustAnchor.getCAPublicKey();
        if ((localX500Principal != null) && (localPublicKey != null) && (localX500Principal.equals(paramX509Certificate.getSubjectX500Principal())) && (localPublicKey.equals(paramX509Certificate.getPublicKey())))
        {
          trustAnchor = localTrustAnchor;
          return true;
        }
        localArrayList.add(localTrustAnchor);
      }
    }
    localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      localTrustAnchor = (TrustAnchor)localIterator.next();
      localX500Principal = localTrustAnchor.getCA();
      localPublicKey = localTrustAnchor.getCAPublicKey();
      if ((localX500Principal != null) && (localX500Principal.equals(paramX509Certificate.getIssuerX500Principal())) && (!PKIX.isDSAPublicKeyWithoutParams(localPublicKey)))
      {
        try
        {
          paramX509Certificate.verify(localPublicKey, buildParams.sigProvider());
        }
        catch (InvalidKeyException localInvalidKeyException)
        {
          if (debug != null) {
            debug.println("ForwardBuilder.isPathCompleted() invalid DSA key found");
          }
          continue;
        }
        catch (GeneralSecurityException localGeneralSecurityException)
        {
          if (debug != null)
          {
            debug.println("ForwardBuilder.isPathCompleted() unexpected exception");
            localGeneralSecurityException.printStackTrace();
          }
        }
        continue;
        trustAnchor = localTrustAnchor;
        return true;
      }
    }
    return false;
  }
  
  void addCertToPath(X509Certificate paramX509Certificate, LinkedList<X509Certificate> paramLinkedList)
  {
    paramLinkedList.addFirst(paramX509Certificate);
  }
  
  void removeFinalCertFromPath(LinkedList<X509Certificate> paramLinkedList)
  {
    paramLinkedList.removeFirst();
  }
  
  static class PKIXCertComparator
    implements Comparator<X509Certificate>
  {
    static final String METHOD_NME = "PKIXCertComparator.compare()";
    private final Set<X500Principal> trustedSubjectDNs;
    private final X509CertSelector certSkidSelector;
    
    PKIXCertComparator(Set<X500Principal> paramSet, X509CertImpl paramX509CertImpl)
      throws IOException
    {
      trustedSubjectDNs = paramSet;
      certSkidSelector = getSelector(paramX509CertImpl);
    }
    
    private X509CertSelector getSelector(X509CertImpl paramX509CertImpl)
      throws IOException
    {
      if (paramX509CertImpl != null)
      {
        AuthorityKeyIdentifierExtension localAuthorityKeyIdentifierExtension = paramX509CertImpl.getAuthorityKeyIdentifierExtension();
        if (localAuthorityKeyIdentifierExtension != null)
        {
          byte[] arrayOfByte = localAuthorityKeyIdentifierExtension.getEncodedKeyIdentifier();
          if (arrayOfByte != null)
          {
            X509CertSelector localX509CertSelector = new X509CertSelector();
            localX509CertSelector.setSubjectKeyIdentifier(arrayOfByte);
            return localX509CertSelector;
          }
        }
      }
      return null;
    }
    
    public int compare(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2)
    {
      if (paramX509Certificate1.equals(paramX509Certificate2)) {
        return 0;
      }
      if (certSkidSelector != null)
      {
        if (certSkidSelector.match(paramX509Certificate1)) {
          return -1;
        }
        if (certSkidSelector.match(paramX509Certificate2)) {
          return 1;
        }
      }
      X500Principal localX500Principal1 = paramX509Certificate1.getIssuerX500Principal();
      X500Principal localX500Principal2 = paramX509Certificate2.getIssuerX500Principal();
      X500Name localX500Name1 = X500Name.asX500Name(localX500Principal1);
      X500Name localX500Name2 = X500Name.asX500Name(localX500Principal2);
      if (ForwardBuilder.debug != null)
      {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Issuer:  " + localX500Principal1);
        ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Issuer:  " + localX500Principal2);
      }
      if (ForwardBuilder.debug != null) {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() MATCH TRUSTED SUBJECT TEST...");
      }
      boolean bool1 = trustedSubjectDNs.contains(localX500Principal1);
      boolean bool2 = trustedSubjectDNs.contains(localX500Principal2);
      if (ForwardBuilder.debug != null)
      {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() m1: " + bool1);
        ForwardBuilder.debug.println("PKIXCertComparator.compare() m2: " + bool2);
      }
      if ((bool1) && (bool2)) {
        return -1;
      }
      if (bool1) {
        return -1;
      }
      if (bool2) {
        return 1;
      }
      if (ForwardBuilder.debug != null) {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING DESCENDANT TEST...");
      }
      Object localObject = trustedSubjectDNs.iterator();
      int i;
      int j;
      while (((Iterator)localObject).hasNext())
      {
        localX500Principal3 = (X500Principal)((Iterator)localObject).next();
        localX500Name3 = X500Name.asX500Name(localX500Principal3);
        i = Builder.distance(localX500Name3, localX500Name1, -1);
        j = Builder.distance(localX500Name3, localX500Name2, -1);
        if (ForwardBuilder.debug != null)
        {
          ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + i);
          ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + j);
        }
        if ((i > 0) || (j > 0))
        {
          if (i == j) {
            return -1;
          }
          if ((i > 0) && (j <= 0)) {
            return -1;
          }
          if ((i <= 0) && (j > 0)) {
            return 1;
          }
          if (i < j) {
            return -1;
          }
          return 1;
        }
      }
      if (ForwardBuilder.debug != null) {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING ANCESTOR TEST...");
      }
      localObject = trustedSubjectDNs.iterator();
      while (((Iterator)localObject).hasNext())
      {
        localX500Principal3 = (X500Principal)((Iterator)localObject).next();
        localX500Name3 = X500Name.asX500Name(localX500Principal3);
        i = Builder.distance(localX500Name3, localX500Name1, Integer.MAX_VALUE);
        j = Builder.distance(localX500Name3, localX500Name2, Integer.MAX_VALUE);
        if (ForwardBuilder.debug != null)
        {
          ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + i);
          ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + j);
        }
        if ((i < 0) || (j < 0))
        {
          if (i == j) {
            return -1;
          }
          if ((i < 0) && (j >= 0)) {
            return -1;
          }
          if ((i >= 0) && (j < 0)) {
            return 1;
          }
          if (i > j) {
            return -1;
          }
          return 1;
        }
      }
      if (ForwardBuilder.debug != null) {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() SAME NAMESPACE AS TRUSTED TEST...");
      }
      localObject = trustedSubjectDNs.iterator();
      while (((Iterator)localObject).hasNext())
      {
        localX500Principal3 = (X500Principal)((Iterator)localObject).next();
        localX500Name3 = X500Name.asX500Name(localX500Principal3);
        localX500Name4 = localX500Name3.commonAncestor(localX500Name1);
        X500Name localX500Name5 = localX500Name3.commonAncestor(localX500Name2);
        if (ForwardBuilder.debug != null)
        {
          ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo1: " + String.valueOf(localX500Name4));
          ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo2: " + String.valueOf(localX500Name5));
        }
        if ((localX500Name4 != null) || (localX500Name5 != null)) {
          if ((localX500Name4 != null) && (localX500Name5 != null))
          {
            m = Builder.hops(localX500Name3, localX500Name1, Integer.MAX_VALUE);
            int n = Builder.hops(localX500Name3, localX500Name2, Integer.MAX_VALUE);
            if (ForwardBuilder.debug != null)
            {
              ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto1: " + m);
              ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto2: " + n);
            }
            if (m != n)
            {
              if (m > n) {
                return 1;
              }
              return -1;
            }
          }
          else
          {
            if (localX500Name4 == null) {
              return 1;
            }
            return -1;
          }
        }
      }
      if (ForwardBuilder.debug != null) {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() CERT ISSUER/SUBJECT COMPARISON TEST...");
      }
      localObject = paramX509Certificate1.getSubjectX500Principal();
      X500Principal localX500Principal3 = paramX509Certificate2.getSubjectX500Principal();
      X500Name localX500Name3 = X500Name.asX500Name((X500Principal)localObject);
      X500Name localX500Name4 = X500Name.asX500Name(localX500Principal3);
      if (ForwardBuilder.debug != null)
      {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Subject: " + localObject);
        ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Subject: " + localX500Principal3);
      }
      int k = Builder.distance(localX500Name3, localX500Name1, Integer.MAX_VALUE);
      int m = Builder.distance(localX500Name4, localX500Name2, Integer.MAX_VALUE);
      if (ForwardBuilder.debug != null)
      {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI1: " + k);
        ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI2: " + m);
      }
      if (m > k) {
        return -1;
      }
      if (m < k) {
        return 1;
      }
      if (ForwardBuilder.debug != null) {
        ForwardBuilder.debug.println("PKIXCertComparator.compare() no tests matched; RETURN 0");
      }
      return -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\ForwardBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */