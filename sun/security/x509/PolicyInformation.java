package sun.security.x509;

import java.io.IOException;
import java.security.cert.PolicyQualifierInfo;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PolicyInformation
{
  public static final String NAME = "PolicyInformation";
  public static final String ID = "id";
  public static final String QUALIFIERS = "qualifiers";
  private CertificatePolicyId policyIdentifier;
  private Set<PolicyQualifierInfo> policyQualifiers;
  
  public PolicyInformation(CertificatePolicyId paramCertificatePolicyId, Set<PolicyQualifierInfo> paramSet)
    throws IOException
  {
    if (paramSet == null) {
      throw new NullPointerException("policyQualifiers is null");
    }
    policyQualifiers = new LinkedHashSet(paramSet);
    policyIdentifier = paramCertificatePolicyId;
  }
  
  public PolicyInformation(DerValue paramDerValue)
    throws IOException
  {
    if (tag != 48) {
      throw new IOException("Invalid encoding of PolicyInformation");
    }
    policyIdentifier = new CertificatePolicyId(data.getDerValue());
    if (data.available() != 0)
    {
      policyQualifiers = new LinkedHashSet();
      DerValue localDerValue = data.getDerValue();
      if (tag != 48) {
        throw new IOException("Invalid encoding of PolicyInformation");
      }
      if (data.available() == 0) {
        throw new IOException("No data available in policyQualifiers");
      }
      while (data.available() != 0) {
        policyQualifiers.add(new PolicyQualifierInfo(data.getDerValue().toByteArray()));
      }
    }
    else
    {
      policyQualifiers = Collections.emptySet();
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof PolicyInformation)) {
      return false;
    }
    PolicyInformation localPolicyInformation = (PolicyInformation)paramObject;
    if (!policyIdentifier.equals(localPolicyInformation.getPolicyIdentifier())) {
      return false;
    }
    return policyQualifiers.equals(localPolicyInformation.getPolicyQualifiers());
  }
  
  public int hashCode()
  {
    int i = 37 + policyIdentifier.hashCode();
    i = 37 * i + policyQualifiers.hashCode();
    return i;
  }
  
  public CertificatePolicyId getPolicyIdentifier()
  {
    return policyIdentifier;
  }
  
  public Set<PolicyQualifierInfo> getPolicyQualifiers()
  {
    return policyQualifiers;
  }
  
  public Object get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("id")) {
      return policyIdentifier;
    }
    if (paramString.equalsIgnoreCase("qualifiers")) {
      return policyQualifiers;
    }
    throw new IOException("Attribute name [" + paramString + "] not recognized by PolicyInformation.");
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("id"))
    {
      if ((paramObject instanceof CertificatePolicyId)) {
        policyIdentifier = ((CertificatePolicyId)paramObject);
      } else {
        throw new IOException("Attribute value must be instance of CertificatePolicyId.");
      }
    }
    else if (paramString.equalsIgnoreCase("qualifiers"))
    {
      if (policyIdentifier == null) {
        throw new IOException("Attribute must have a CertificatePolicyIdentifier value before PolicyQualifierInfo can be set.");
      }
      if ((paramObject instanceof Set))
      {
        Iterator localIterator = ((Set)paramObject).iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          if (!(localObject instanceof PolicyQualifierInfo)) {
            throw new IOException("Attribute value must be aSet of PolicyQualifierInfo objects.");
          }
        }
        policyQualifiers = ((Set)paramObject);
      }
      else
      {
        throw new IOException("Attribute value must be of type Set.");
      }
    }
    else
    {
      throw new IOException("Attribute name [" + paramString + "] not recognized by PolicyInformation");
    }
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("qualifiers"))
    {
      policyQualifiers = Collections.emptySet();
    }
    else
    {
      if (paramString.equalsIgnoreCase("id")) {
        throw new IOException("Attribute ID may not be deleted from PolicyInformation.");
      }
      throw new IOException("Attribute name [" + paramString + "] not recognized by PolicyInformation.");
    }
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("id");
    localAttributeNameEnumeration.addElement("qualifiers");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "PolicyInformation";
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("  [" + policyIdentifier.toString());
    localStringBuilder.append(policyQualifiers + "  ]\n");
    return localStringBuilder.toString();
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    policyIdentifier.encode(localDerOutputStream1);
    if (!policyQualifiers.isEmpty())
    {
      DerOutputStream localDerOutputStream2 = new DerOutputStream();
      Iterator localIterator = policyQualifiers.iterator();
      while (localIterator.hasNext())
      {
        PolicyQualifierInfo localPolicyQualifierInfo = (PolicyQualifierInfo)localIterator.next();
        localDerOutputStream2.write(localPolicyQualifierInfo.getEncoded());
      }
      localDerOutputStream1.write((byte)48, localDerOutputStream2);
    }
    paramDerOutputStream.write((byte)48, localDerOutputStream1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\PolicyInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */