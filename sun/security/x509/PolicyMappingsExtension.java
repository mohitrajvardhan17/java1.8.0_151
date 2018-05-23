package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PolicyMappingsExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.PolicyMappings";
  public static final String NAME = "PolicyMappings";
  public static final String MAP = "map";
  private List<CertificatePolicyMap> maps;
  
  private void encodeThis()
    throws IOException
  {
    if ((maps == null) || (maps.isEmpty()))
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    Iterator localIterator = maps.iterator();
    while (localIterator.hasNext())
    {
      CertificatePolicyMap localCertificatePolicyMap = (CertificatePolicyMap)localIterator.next();
      localCertificatePolicyMap.encode(localDerOutputStream2);
    }
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    extensionValue = localDerOutputStream1.toByteArray();
  }
  
  public PolicyMappingsExtension(List<CertificatePolicyMap> paramList)
    throws IOException
  {
    maps = paramList;
    extensionId = PKIXExtensions.PolicyMappings_Id;
    critical = false;
    encodeThis();
  }
  
  public PolicyMappingsExtension()
  {
    extensionId = PKIXExtensions.KeyUsage_Id;
    critical = false;
    maps = Collections.emptyList();
  }
  
  public PolicyMappingsExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.PolicyMappings_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue1 = new DerValue(extensionValue);
    if (tag != 48) {
      throw new IOException("Invalid encoding for PolicyMappingsExtension.");
    }
    maps = new ArrayList();
    while (data.available() != 0)
    {
      DerValue localDerValue2 = data.getDerValue();
      CertificatePolicyMap localCertificatePolicyMap = new CertificatePolicyMap(localDerValue2);
      maps.add(localCertificatePolicyMap);
    }
  }
  
  public String toString()
  {
    if (maps == null) {
      return "";
    }
    String str = super.toString() + "PolicyMappings [\n" + maps.toString() + "]\n";
    return str;
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.PolicyMappings_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("map"))
    {
      if (!(paramObject instanceof List)) {
        throw new IOException("Attribute value should be of type List.");
      }
      maps = ((List)paramObject);
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
    }
    encodeThis();
  }
  
  public List<CertificatePolicyMap> get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("map")) {
      return maps;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("map")) {
      maps = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("map");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "PolicyMappings";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\PolicyMappingsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */