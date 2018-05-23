package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateIssuerExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String NAME = "CertificateIssuer";
  public static final String ISSUER = "issuer";
  private GeneralNames names;
  
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
  
  public CertificateIssuerExtension(GeneralNames paramGeneralNames)
    throws IOException
  {
    extensionId = PKIXExtensions.CertificateIssuer_Id;
    critical = true;
    names = paramGeneralNames;
    encodeThis();
  }
  
  public CertificateIssuerExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.CertificateIssuer_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue = new DerValue(extensionValue);
    names = new GeneralNames(localDerValue);
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("issuer"))
    {
      if (!(paramObject instanceof GeneralNames)) {
        throw new IOException("Attribute value must be of type GeneralNames");
      }
      names = ((GeneralNames)paramObject);
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuer");
    }
    encodeThis();
  }
  
  public GeneralNames get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("issuer")) {
      return names;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuer");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("issuer")) {
      names = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuer");
    }
    encodeThis();
  }
  
  public String toString()
  {
    return super.toString() + "Certificate Issuer [\n" + String.valueOf(names) + "]\n";
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.CertificateIssuer_Id;
      critical = true;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("issuer");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "CertificateIssuer";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificateIssuerExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */