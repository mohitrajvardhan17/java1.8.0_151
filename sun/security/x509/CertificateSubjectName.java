package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.security.auth.x500.X500Principal;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateSubjectName
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.subject";
  public static final String NAME = "subject";
  public static final String DN_NAME = "dname";
  public static final String DN_PRINCIPAL = "x500principal";
  private X500Name dnName;
  private X500Principal dnPrincipal;
  
  public CertificateSubjectName(X500Name paramX500Name)
  {
    dnName = paramX500Name;
  }
  
  public CertificateSubjectName(DerInputStream paramDerInputStream)
    throws IOException
  {
    dnName = new X500Name(paramDerInputStream);
  }
  
  public CertificateSubjectName(InputStream paramInputStream)
    throws IOException
  {
    DerValue localDerValue = new DerValue(paramInputStream);
    dnName = new X500Name(localDerValue);
  }
  
  public String toString()
  {
    if (dnName == null) {
      return "";
    }
    return dnName.toString();
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    dnName.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof X500Name)) {
      throw new IOException("Attribute must be of type X500Name.");
    }
    if (paramString.equalsIgnoreCase("dname"))
    {
      dnName = ((X500Name)paramObject);
      dnPrincipal = null;
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSubjectName.");
    }
  }
  
  public Object get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("dname")) {
      return dnName;
    }
    if (paramString.equalsIgnoreCase("x500principal"))
    {
      if ((dnPrincipal == null) && (dnName != null)) {
        dnPrincipal = dnName.asX500Principal();
      }
      return dnPrincipal;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSubjectName.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("dname"))
    {
      dnName = null;
      dnPrincipal = null;
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSubjectName.");
    }
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("dname");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "subject";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificateSubjectName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */