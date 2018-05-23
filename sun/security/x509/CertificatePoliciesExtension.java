package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificatePoliciesExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.CertificatePolicies";
  public static final String NAME = "CertificatePolicies";
  public static final String POLICIES = "policies";
  private List<PolicyInformation> certPolicies;
  
  private void encodeThis()
    throws IOException
  {
    if ((certPolicies == null) || (certPolicies.isEmpty()))
    {
      extensionValue = null;
    }
    else
    {
      DerOutputStream localDerOutputStream1 = new DerOutputStream();
      DerOutputStream localDerOutputStream2 = new DerOutputStream();
      Iterator localIterator = certPolicies.iterator();
      while (localIterator.hasNext())
      {
        PolicyInformation localPolicyInformation = (PolicyInformation)localIterator.next();
        localPolicyInformation.encode(localDerOutputStream2);
      }
      localDerOutputStream1.write((byte)48, localDerOutputStream2);
      extensionValue = localDerOutputStream1.toByteArray();
    }
  }
  
  public CertificatePoliciesExtension(List<PolicyInformation> paramList)
    throws IOException
  {
    this(Boolean.FALSE, paramList);
  }
  
  public CertificatePoliciesExtension(Boolean paramBoolean, List<PolicyInformation> paramList)
    throws IOException
  {
    certPolicies = paramList;
    extensionId = PKIXExtensions.CertificatePolicies_Id;
    critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public CertificatePoliciesExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.CertificatePolicies_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue1 = new DerValue(extensionValue);
    if (tag != 48) {
      throw new IOException("Invalid encoding for CertificatePoliciesExtension.");
    }
    certPolicies = new ArrayList();
    while (data.available() != 0)
    {
      DerValue localDerValue2 = data.getDerValue();
      PolicyInformation localPolicyInformation = new PolicyInformation(localDerValue2);
      certPolicies.add(localPolicyInformation);
    }
  }
  
  public String toString()
  {
    if (certPolicies == null) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(super.toString());
    localStringBuilder.append("CertificatePolicies [\n");
    Iterator localIterator = certPolicies.iterator();
    while (localIterator.hasNext())
    {
      PolicyInformation localPolicyInformation = (PolicyInformation)localIterator.next();
      localStringBuilder.append(localPolicyInformation.toString());
    }
    localStringBuilder.append("]\n");
    return localStringBuilder.toString();
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.CertificatePolicies_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("policies"))
    {
      if (!(paramObject instanceof List)) {
        throw new IOException("Attribute value should be of type List.");
      }
      certPolicies = ((List)paramObject);
    }
    else
    {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
    }
    encodeThis();
  }
  
  public List<PolicyInformation> get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("policies")) {
      return certPolicies;
    }
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("policies")) {
      certPolicies = null;
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("policies");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "CertificatePolicies";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificatePoliciesExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */