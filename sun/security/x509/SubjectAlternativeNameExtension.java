package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class SubjectAlternativeNameExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.SubjectAlternativeName";
  public static final String NAME = "SubjectAlternativeName";
  public static final String SUBJECT_NAME = "subject_name";
  GeneralNames names = null;
  
  private void encodeThis()
    throws IOException
  {
    if ((names == null) || (names.isEmpty()))
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream = new DerOutputStream();
    names.encode(localDerOutputStream);
    extensionValue = localDerOutputStream.toByteArray();
  }
  
  public SubjectAlternativeNameExtension(GeneralNames paramGeneralNames)
    throws IOException
  {
    this(Boolean.FALSE, paramGeneralNames);
  }
  
  public SubjectAlternativeNameExtension(Boolean paramBoolean, GeneralNames paramGeneralNames)
    throws IOException
  {
    names = paramGeneralNames;
    extensionId = PKIXExtensions.SubjectAlternativeName_Id;
    critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public SubjectAlternativeNameExtension()
  {
    extensionId = PKIXExtensions.SubjectAlternativeName_Id;
    critical = false;
    names = new GeneralNames();
  }
  
  public SubjectAlternativeNameExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.SubjectAlternativeName_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue = new DerValue(extensionValue);
    if (data == null)
    {
      names = new GeneralNames();
      return;
    }
    names = new GeneralNames(localDerValue);
  }
  
  public String toString()
  {
    String str = super.toString() + "SubjectAlternativeName [\n";
    if (names == null)
    {
      str = str + "  null\n";
    }
    else
    {
      Iterator localIterator = names.names().iterator();
      while (localIterator.hasNext())
      {
        GeneralName localGeneralName = (GeneralName)localIterator.next();
        str = str + "  " + localGeneralName + "\n";
      }
    }
    str = str + "]\n";
    return str;
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.SubjectAlternativeName_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("subject_name"))
    {
      if (!(paramObject instanceof GeneralNames)) {
        throw new IOException("Attribute value should be of type GeneralNames.");
      }
      names = ((GeneralNames)paramObject);
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:SubjectAlternativeName.");
    }
    encodeThis();
  }
  
  public GeneralNames get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("subject_name")) {
      return names;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:SubjectAlternativeName.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("subject_name")) {
      names = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:SubjectAlternativeName.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("subject_name");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "SubjectAlternativeName";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\SubjectAlternativeNameExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */