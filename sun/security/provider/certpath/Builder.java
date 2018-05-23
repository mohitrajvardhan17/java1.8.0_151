package sun.security.provider.certpath;

import java.io.IOException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import sun.security.action.GetBooleanAction;
import sun.security.util.Debug;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.GeneralSubtree;
import sun.security.x509.GeneralSubtrees;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

public abstract class Builder
{
  private static final Debug debug = Debug.getInstance("certpath");
  private Set<String> matchingPolicies;
  final PKIX.BuilderParams buildParams;
  final X509CertSelector targetCertConstraints;
  static final boolean USE_AIA = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.security.enableAIAcaIssuers"))).booleanValue();
  
  Builder(PKIX.BuilderParams paramBuilderParams)
  {
    buildParams = paramBuilderParams;
    targetCertConstraints = ((X509CertSelector)paramBuilderParams.targetCertConstraints());
  }
  
  abstract Collection<X509Certificate> getMatchingCerts(State paramState, List<CertStore> paramList)
    throws CertStoreException, CertificateException, IOException;
  
  abstract void verifyCert(X509Certificate paramX509Certificate, State paramState, List<X509Certificate> paramList)
    throws GeneralSecurityException;
  
  abstract boolean isPathCompleted(X509Certificate paramX509Certificate);
  
  abstract void addCertToPath(X509Certificate paramX509Certificate, LinkedList<X509Certificate> paramLinkedList);
  
  abstract void removeFinalCertFromPath(LinkedList<X509Certificate> paramLinkedList);
  
  static int distance(GeneralNameInterface paramGeneralNameInterface1, GeneralNameInterface paramGeneralNameInterface2, int paramInt)
  {
    switch (paramGeneralNameInterface1.constrains(paramGeneralNameInterface2))
    {
    case -1: 
      if (debug != null) {
        debug.println("Builder.distance(): Names are different types");
      }
      return paramInt;
    case 3: 
      if (debug != null) {
        debug.println("Builder.distance(): Names are same type but in different subtrees");
      }
      return paramInt;
    case 0: 
      return 0;
    case 2: 
      break;
    case 1: 
      break;
    default: 
      return paramInt;
    }
    return paramGeneralNameInterface2.subtreeDepth() - paramGeneralNameInterface1.subtreeDepth();
  }
  
  static int hops(GeneralNameInterface paramGeneralNameInterface1, GeneralNameInterface paramGeneralNameInterface2, int paramInt)
  {
    int i = paramGeneralNameInterface1.constrains(paramGeneralNameInterface2);
    switch (i)
    {
    case -1: 
      if (debug != null) {
        debug.println("Builder.hops(): Names are different types");
      }
      return paramInt;
    case 3: 
      break;
    case 0: 
      return 0;
    case 2: 
      return paramGeneralNameInterface2.subtreeDepth() - paramGeneralNameInterface1.subtreeDepth();
    case 1: 
      return paramGeneralNameInterface2.subtreeDepth() - paramGeneralNameInterface1.subtreeDepth();
    default: 
      return paramInt;
    }
    if (paramGeneralNameInterface1.getType() != 4)
    {
      if (debug != null) {
        debug.println("Builder.hops(): hopDistance not implemented for this name type");
      }
      return paramInt;
    }
    X500Name localX500Name1 = (X500Name)paramGeneralNameInterface1;
    X500Name localX500Name2 = (X500Name)paramGeneralNameInterface2;
    X500Name localX500Name3 = localX500Name1.commonAncestor(localX500Name2);
    if (localX500Name3 == null)
    {
      if (debug != null) {
        debug.println("Builder.hops(): Names are in different namespaces");
      }
      return paramInt;
    }
    int j = localX500Name3.subtreeDepth();
    int k = localX500Name1.subtreeDepth();
    int m = localX500Name2.subtreeDepth();
    return k + m - 2 * j;
  }
  
  static int targetDistance(NameConstraintsExtension paramNameConstraintsExtension, X509Certificate paramX509Certificate, GeneralNameInterface paramGeneralNameInterface)
    throws IOException
  {
    if ((paramNameConstraintsExtension != null) && (!paramNameConstraintsExtension.verify(paramX509Certificate))) {
      throw new IOException("certificate does not satisfy existing name constraints");
    }
    X509CertImpl localX509CertImpl;
    try
    {
      localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    }
    catch (CertificateException localCertificateException)
    {
      throw new IOException("Invalid certificate", localCertificateException);
    }
    X500Name localX500Name = X500Name.asX500Name(localX509CertImpl.getSubjectX500Principal());
    if (localX500Name.equals(paramGeneralNameInterface)) {
      return 0;
    }
    SubjectAlternativeNameExtension localSubjectAlternativeNameExtension = localX509CertImpl.getSubjectAlternativeNameExtension();
    if (localSubjectAlternativeNameExtension != null)
    {
      localObject = localSubjectAlternativeNameExtension.get("subject_name");
      if (localObject != null)
      {
        int i = 0;
        int j = ((GeneralNames)localObject).size();
        while (i < j)
        {
          GeneralNameInterface localGeneralNameInterface1 = ((GeneralNames)localObject).get(i).getName();
          if (localGeneralNameInterface1.equals(paramGeneralNameInterface)) {
            return 0;
          }
          i++;
        }
      }
    }
    Object localObject = localX509CertImpl.getNameConstraintsExtension();
    if (localObject == null) {
      return -1;
    }
    if (paramNameConstraintsExtension != null) {
      paramNameConstraintsExtension.merge((NameConstraintsExtension)localObject);
    } else {
      paramNameConstraintsExtension = (NameConstraintsExtension)((NameConstraintsExtension)localObject).clone();
    }
    if (debug != null) {
      debug.println("Builder.targetDistance() merged constraints: " + String.valueOf(paramNameConstraintsExtension));
    }
    GeneralSubtrees localGeneralSubtrees1 = paramNameConstraintsExtension.get("permitted_subtrees");
    GeneralSubtrees localGeneralSubtrees2 = paramNameConstraintsExtension.get("excluded_subtrees");
    if (localGeneralSubtrees1 != null) {
      localGeneralSubtrees1.reduce(localGeneralSubtrees2);
    }
    if (debug != null) {
      debug.println("Builder.targetDistance() reduced constraints: " + localGeneralSubtrees1);
    }
    if (!paramNameConstraintsExtension.verify(paramGeneralNameInterface)) {
      throw new IOException("New certificate not allowed to sign certificate for target");
    }
    if (localGeneralSubtrees1 == null) {
      return -1;
    }
    int k = 0;
    int m = localGeneralSubtrees1.size();
    while (k < m)
    {
      GeneralNameInterface localGeneralNameInterface2 = localGeneralSubtrees1.get(k).getName().getName();
      int n = distance(localGeneralNameInterface2, paramGeneralNameInterface, -1);
      if (n >= 0) {
        return n + 1;
      }
      k++;
    }
    return -1;
  }
  
  Set<String> getMatchingPolicies()
  {
    if (matchingPolicies != null)
    {
      Set localSet = buildParams.initialPolicies();
      if ((!localSet.isEmpty()) && (!localSet.contains("2.5.29.32.0")) && (buildParams.policyMappingInhibited()))
      {
        matchingPolicies = new HashSet(localSet);
        matchingPolicies.add("2.5.29.32.0");
      }
      else
      {
        matchingPolicies = Collections.emptySet();
      }
    }
    return matchingPolicies;
  }
  
  boolean addMatchingCerts(X509CertSelector paramX509CertSelector, Collection<CertStore> paramCollection, Collection<X509Certificate> paramCollection1, boolean paramBoolean)
  {
    X509Certificate localX509Certificate = paramX509CertSelector.getCertificate();
    if (localX509Certificate != null)
    {
      if ((paramX509CertSelector.match(localX509Certificate)) && (!X509CertImpl.isSelfSigned(localX509Certificate, buildParams.sigProvider())))
      {
        if (debug != null) {
          debug.println("Builder.addMatchingCerts: adding target cert\n  SN: " + Debug.toHexString(localX509Certificate.getSerialNumber()) + "\n  Subject: " + localX509Certificate.getSubjectX500Principal() + "\n  Issuer: " + localX509Certificate.getIssuerX500Principal());
        }
        return paramCollection1.add(localX509Certificate);
      }
      return false;
    }
    boolean bool = false;
    Iterator localIterator1 = paramCollection.iterator();
    while (localIterator1.hasNext())
    {
      CertStore localCertStore = (CertStore)localIterator1.next();
      try
      {
        Collection localCollection = localCertStore.getCertificates(paramX509CertSelector);
        Iterator localIterator2 = localCollection.iterator();
        while (localIterator2.hasNext())
        {
          Certificate localCertificate = (Certificate)localIterator2.next();
          if ((!X509CertImpl.isSelfSigned((X509Certificate)localCertificate, buildParams.sigProvider())) && (paramCollection1.add((X509Certificate)localCertificate))) {
            bool = true;
          }
        }
        if ((!paramBoolean) && (bool)) {
          return true;
        }
      }
      catch (CertStoreException localCertStoreException)
      {
        if (debug != null)
        {
          debug.println("Builder.addMatchingCerts, non-fatal exception retrieving certs: " + localCertStoreException);
          localCertStoreException.printStackTrace();
        }
      }
    }
    return bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\Builder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */