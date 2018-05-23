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

public class SubjectInfoAccessExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.SubjectInfoAccess";
  public static final String NAME = "SubjectInfoAccess";
  public static final String DESCRIPTIONS = "descriptions";
  private List<AccessDescription> accessDescriptions;
  
  public SubjectInfoAccessExtension(List<AccessDescription> paramList)
    throws IOException
  {
    extensionId = PKIXExtensions.SubjectInfoAccess_Id;
    critical = false;
    accessDescriptions = paramList;
    encodeThis();
  }
  
  public SubjectInfoAccessExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.SubjectInfoAccess_Id;
    critical = paramBoolean.booleanValue();
    if (!(paramObject instanceof byte[])) {
      throw new IOException("Illegal argument type");
    }
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue1 = new DerValue(extensionValue);
    if (tag != 48) {
      throw new IOException("Invalid encoding for SubjectInfoAccessExtension.");
    }
    accessDescriptions = new ArrayList();
    while (data.available() != 0)
    {
      DerValue localDerValue2 = data.getDerValue();
      AccessDescription localAccessDescription = new AccessDescription(localDerValue2);
      accessDescriptions.add(localAccessDescription);
    }
  }
  
  public List<AccessDescription> getAccessDescriptions()
  {
    return accessDescriptions;
  }
  
  public String getName()
  {
    return "SubjectInfoAccess";
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.SubjectInfoAccess_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("descriptions"))
    {
      if (!(paramObject instanceof List)) {
        throw new IOException("Attribute value should be of type List.");
      }
      accessDescriptions = ((List)paramObject);
    }
    else
    {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
    }
    encodeThis();
  }
  
  public List<AccessDescription> get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("descriptions")) {
      return accessDescriptions;
    }
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("descriptions")) {
      accessDescriptions = Collections.emptyList();
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("descriptions");
    return localAttributeNameEnumeration.elements();
  }
  
  private void encodeThis()
    throws IOException
  {
    if (accessDescriptions.isEmpty())
    {
      extensionValue = null;
    }
    else
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      Object localObject = accessDescriptions.iterator();
      while (((Iterator)localObject).hasNext())
      {
        AccessDescription localAccessDescription = (AccessDescription)((Iterator)localObject).next();
        localAccessDescription.encode(localDerOutputStream);
      }
      localObject = new DerOutputStream();
      ((DerOutputStream)localObject).write((byte)48, localDerOutputStream);
      extensionValue = ((DerOutputStream)localObject).toByteArray();
    }
  }
  
  public String toString()
  {
    return super.toString() + "SubjectInfoAccess [\n  " + accessDescriptions + "\n]\n";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\SubjectInfoAccessExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */