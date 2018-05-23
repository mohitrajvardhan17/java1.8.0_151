package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PKIXParameters
  implements CertPathParameters
{
  private Set<TrustAnchor> unmodTrustAnchors;
  private Date date;
  private List<PKIXCertPathChecker> certPathCheckers;
  private String sigProvider;
  private boolean revocationEnabled = true;
  private Set<String> unmodInitialPolicies;
  private boolean explicitPolicyRequired = false;
  private boolean policyMappingInhibited = false;
  private boolean anyPolicyInhibited = false;
  private boolean policyQualifiersRejected = true;
  private List<CertStore> certStores;
  private CertSelector certSelector;
  
  public PKIXParameters(Set<TrustAnchor> paramSet)
    throws InvalidAlgorithmParameterException
  {
    setTrustAnchors(paramSet);
    unmodInitialPolicies = Collections.emptySet();
    certPathCheckers = new ArrayList();
    certStores = new ArrayList();
  }
  
  public PKIXParameters(KeyStore paramKeyStore)
    throws KeyStoreException, InvalidAlgorithmParameterException
  {
    if (paramKeyStore == null) {
      throw new NullPointerException("the keystore parameter must be non-null");
    }
    HashSet localHashSet = new HashSet();
    Enumeration localEnumeration = paramKeyStore.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (paramKeyStore.isCertificateEntry(str))
      {
        Certificate localCertificate = paramKeyStore.getCertificate(str);
        if ((localCertificate instanceof X509Certificate)) {
          localHashSet.add(new TrustAnchor((X509Certificate)localCertificate, null));
        }
      }
    }
    setTrustAnchors(localHashSet);
    unmodInitialPolicies = Collections.emptySet();
    certPathCheckers = new ArrayList();
    certStores = new ArrayList();
  }
  
  public Set<TrustAnchor> getTrustAnchors()
  {
    return unmodTrustAnchors;
  }
  
  public void setTrustAnchors(Set<TrustAnchor> paramSet)
    throws InvalidAlgorithmParameterException
  {
    if (paramSet == null) {
      throw new NullPointerException("the trustAnchors parameters must be non-null");
    }
    if (paramSet.isEmpty()) {
      throw new InvalidAlgorithmParameterException("the trustAnchors parameter must be non-empty");
    }
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext()) {
      if (!(localIterator.next() instanceof TrustAnchor)) {
        throw new ClassCastException("all elements of set must be of type java.security.cert.TrustAnchor");
      }
    }
    unmodTrustAnchors = Collections.unmodifiableSet(new HashSet(paramSet));
  }
  
  public Set<String> getInitialPolicies()
  {
    return unmodInitialPolicies;
  }
  
  public void setInitialPolicies(Set<String> paramSet)
  {
    if (paramSet != null)
    {
      Iterator localIterator = paramSet.iterator();
      while (localIterator.hasNext()) {
        if (!(localIterator.next() instanceof String)) {
          throw new ClassCastException("all elements of set must be of type java.lang.String");
        }
      }
      unmodInitialPolicies = Collections.unmodifiableSet(new HashSet(paramSet));
    }
    else
    {
      unmodInitialPolicies = Collections.emptySet();
    }
  }
  
  public void setCertStores(List<CertStore> paramList)
  {
    if (paramList == null)
    {
      certStores = new ArrayList();
    }
    else
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext()) {
        if (!(localIterator.next() instanceof CertStore)) {
          throw new ClassCastException("all elements of list must be of type java.security.cert.CertStore");
        }
      }
      certStores = new ArrayList(paramList);
    }
  }
  
  public void addCertStore(CertStore paramCertStore)
  {
    if (paramCertStore != null) {
      certStores.add(paramCertStore);
    }
  }
  
  public List<CertStore> getCertStores()
  {
    return Collections.unmodifiableList(new ArrayList(certStores));
  }
  
  public void setRevocationEnabled(boolean paramBoolean)
  {
    revocationEnabled = paramBoolean;
  }
  
  public boolean isRevocationEnabled()
  {
    return revocationEnabled;
  }
  
  public void setExplicitPolicyRequired(boolean paramBoolean)
  {
    explicitPolicyRequired = paramBoolean;
  }
  
  public boolean isExplicitPolicyRequired()
  {
    return explicitPolicyRequired;
  }
  
  public void setPolicyMappingInhibited(boolean paramBoolean)
  {
    policyMappingInhibited = paramBoolean;
  }
  
  public boolean isPolicyMappingInhibited()
  {
    return policyMappingInhibited;
  }
  
  public void setAnyPolicyInhibited(boolean paramBoolean)
  {
    anyPolicyInhibited = paramBoolean;
  }
  
  public boolean isAnyPolicyInhibited()
  {
    return anyPolicyInhibited;
  }
  
  public void setPolicyQualifiersRejected(boolean paramBoolean)
  {
    policyQualifiersRejected = paramBoolean;
  }
  
  public boolean getPolicyQualifiersRejected()
  {
    return policyQualifiersRejected;
  }
  
  public Date getDate()
  {
    if (date == null) {
      return null;
    }
    return (Date)date.clone();
  }
  
  public void setDate(Date paramDate)
  {
    if (paramDate != null) {
      date = ((Date)paramDate.clone());
    } else {
      paramDate = null;
    }
  }
  
  public void setCertPathCheckers(List<PKIXCertPathChecker> paramList)
  {
    if (paramList != null)
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator.next();
        localArrayList.add((PKIXCertPathChecker)localPKIXCertPathChecker.clone());
      }
      certPathCheckers = localArrayList;
    }
    else
    {
      certPathCheckers = new ArrayList();
    }
  }
  
  public List<PKIXCertPathChecker> getCertPathCheckers()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = certPathCheckers.iterator();
    while (localIterator.hasNext())
    {
      PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator.next();
      localArrayList.add((PKIXCertPathChecker)localPKIXCertPathChecker.clone());
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public void addCertPathChecker(PKIXCertPathChecker paramPKIXCertPathChecker)
  {
    if (paramPKIXCertPathChecker != null) {
      certPathCheckers.add((PKIXCertPathChecker)paramPKIXCertPathChecker.clone());
    }
  }
  
  public String getSigProvider()
  {
    return sigProvider;
  }
  
  public void setSigProvider(String paramString)
  {
    sigProvider = paramString;
  }
  
  public CertSelector getTargetCertConstraints()
  {
    if (certSelector != null) {
      return (CertSelector)certSelector.clone();
    }
    return null;
  }
  
  public void setTargetCertConstraints(CertSelector paramCertSelector)
  {
    if (paramCertSelector != null) {
      certSelector = ((CertSelector)paramCertSelector.clone());
    } else {
      certSelector = null;
    }
  }
  
  public Object clone()
  {
    try
    {
      PKIXParameters localPKIXParameters = (PKIXParameters)super.clone();
      if (certStores != null) {
        certStores = new ArrayList(certStores);
      }
      if (certPathCheckers != null)
      {
        certPathCheckers = new ArrayList(certPathCheckers.size());
        Iterator localIterator = certPathCheckers.iterator();
        while (localIterator.hasNext())
        {
          PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator.next();
          certPathCheckers.add((PKIXCertPathChecker)localPKIXCertPathChecker.clone());
        }
      }
      return localPKIXParameters;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("[\n");
    if (unmodTrustAnchors != null) {
      localStringBuffer.append("  Trust Anchors: " + unmodTrustAnchors.toString() + "\n");
    }
    if (unmodInitialPolicies != null) {
      if (unmodInitialPolicies.isEmpty()) {
        localStringBuffer.append("  Initial Policy OIDs: any\n");
      } else {
        localStringBuffer.append("  Initial Policy OIDs: [" + unmodInitialPolicies.toString() + "]\n");
      }
    }
    localStringBuffer.append("  Validity Date: " + String.valueOf(date) + "\n");
    localStringBuffer.append("  Signature Provider: " + String.valueOf(sigProvider) + "\n");
    localStringBuffer.append("  Default Revocation Enabled: " + revocationEnabled + "\n");
    localStringBuffer.append("  Explicit Policy Required: " + explicitPolicyRequired + "\n");
    localStringBuffer.append("  Policy Mapping Inhibited: " + policyMappingInhibited + "\n");
    localStringBuffer.append("  Any Policy Inhibited: " + anyPolicyInhibited + "\n");
    localStringBuffer.append("  Policy Qualifiers Rejected: " + policyQualifiersRejected + "\n");
    localStringBuffer.append("  Target Cert Constraints: " + String.valueOf(certSelector) + "\n");
    if (certPathCheckers != null) {
      localStringBuffer.append("  Certification Path Checkers: [" + certPathCheckers.toString() + "]\n");
    }
    if (certStores != null) {
      localStringBuffer.append("  CertStores: [" + certStores.toString() + "]\n");
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\PKIXParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */