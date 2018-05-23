package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateX509Key
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.key";
  public static final String NAME = "key";
  public static final String KEY = "value";
  private PublicKey key;
  
  public CertificateX509Key(PublicKey paramPublicKey)
  {
    key = paramPublicKey;
  }
  
  public CertificateX509Key(DerInputStream paramDerInputStream)
    throws IOException
  {
    DerValue localDerValue = paramDerInputStream.getDerValue();
    key = X509Key.parse(localDerValue);
  }
  
  public CertificateX509Key(InputStream paramInputStream)
    throws IOException
  {
    DerValue localDerValue = new DerValue(paramInputStream);
    key = X509Key.parse(localDerValue);
  }
  
  public String toString()
  {
    if (key == null) {
      return "";
    }
    return key.toString();
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.write(key.getEncoded());
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("value")) {
      key = ((PublicKey)paramObject);
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateX509Key.");
    }
  }
  
  public PublicKey get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("value")) {
      return key;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet: CertificateX509Key.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("value")) {
      key = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateX509Key.");
    }
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("value");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "key";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificateX509Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */