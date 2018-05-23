package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.Timestamp;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.TrustAnchor;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PKIXTimestampParameters
  extends PKIXBuilderParameters
{
  private final PKIXBuilderParameters p;
  private Timestamp jarTimestamp;
  
  public PKIXTimestampParameters(PKIXBuilderParameters paramPKIXBuilderParameters, Timestamp paramTimestamp)
    throws InvalidAlgorithmParameterException
  {
    super(paramPKIXBuilderParameters.getTrustAnchors(), null);
    p = paramPKIXBuilderParameters;
    jarTimestamp = paramTimestamp;
  }
  
  public Timestamp getTimestamp()
  {
    return jarTimestamp;
  }
  
  public void setTimestamp(Timestamp paramTimestamp)
  {
    jarTimestamp = paramTimestamp;
  }
  
  public void setDate(Date paramDate)
  {
    p.setDate(paramDate);
  }
  
  public void addCertPathChecker(PKIXCertPathChecker paramPKIXCertPathChecker)
  {
    p.addCertPathChecker(paramPKIXCertPathChecker);
  }
  
  public void setMaxPathLength(int paramInt)
  {
    p.setMaxPathLength(paramInt);
  }
  
  public int getMaxPathLength()
  {
    return p.getMaxPathLength();
  }
  
  public String toString()
  {
    return p.toString();
  }
  
  public Set<TrustAnchor> getTrustAnchors()
  {
    return p.getTrustAnchors();
  }
  
  public void setTrustAnchors(Set<TrustAnchor> paramSet)
    throws InvalidAlgorithmParameterException
  {
    if (p == null) {
      return;
    }
    p.setTrustAnchors(paramSet);
  }
  
  public Set<String> getInitialPolicies()
  {
    return p.getInitialPolicies();
  }
  
  public void setInitialPolicies(Set<String> paramSet)
  {
    p.setInitialPolicies(paramSet);
  }
  
  public void setCertStores(List<CertStore> paramList)
  {
    p.setCertStores(paramList);
  }
  
  public void addCertStore(CertStore paramCertStore)
  {
    p.addCertStore(paramCertStore);
  }
  
  public List<CertStore> getCertStores()
  {
    return p.getCertStores();
  }
  
  public void setRevocationEnabled(boolean paramBoolean)
  {
    p.setRevocationEnabled(paramBoolean);
  }
  
  public boolean isRevocationEnabled()
  {
    return p.isRevocationEnabled();
  }
  
  public void setExplicitPolicyRequired(boolean paramBoolean)
  {
    p.setExplicitPolicyRequired(paramBoolean);
  }
  
  public boolean isExplicitPolicyRequired()
  {
    return p.isExplicitPolicyRequired();
  }
  
  public void setPolicyMappingInhibited(boolean paramBoolean)
  {
    p.setPolicyMappingInhibited(paramBoolean);
  }
  
  public boolean isPolicyMappingInhibited()
  {
    return p.isPolicyMappingInhibited();
  }
  
  public void setAnyPolicyInhibited(boolean paramBoolean)
  {
    p.setAnyPolicyInhibited(paramBoolean);
  }
  
  public boolean isAnyPolicyInhibited()
  {
    return p.isAnyPolicyInhibited();
  }
  
  public void setPolicyQualifiersRejected(boolean paramBoolean)
  {
    p.setPolicyQualifiersRejected(paramBoolean);
  }
  
  public boolean getPolicyQualifiersRejected()
  {
    return p.getPolicyQualifiersRejected();
  }
  
  public Date getDate()
  {
    return p.getDate();
  }
  
  public void setCertPathCheckers(List<PKIXCertPathChecker> paramList)
  {
    p.setCertPathCheckers(paramList);
  }
  
  public List<PKIXCertPathChecker> getCertPathCheckers()
  {
    return p.getCertPathCheckers();
  }
  
  public String getSigProvider()
  {
    return p.getSigProvider();
  }
  
  public void setSigProvider(String paramString)
  {
    p.setSigProvider(paramString);
  }
  
  public CertSelector getTargetCertConstraints()
  {
    return p.getTargetCertConstraints();
  }
  
  public void setTargetCertConstraints(CertSelector paramCertSelector)
  {
    if (p == null) {
      return;
    }
    p.setTargetCertConstraints(paramCertSelector);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\PKIXTimestampParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */