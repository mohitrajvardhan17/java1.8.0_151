package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class SubjectKeyIdentifierExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.SubjectKeyIdentifier";
  public static final String NAME = "SubjectKeyIdentifier";
  public static final String KEY_ID = "key_id";
  private KeyIdentifier id = null;
  
  private void encodeThis()
    throws IOException
  {
    if (id == null)
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream = new DerOutputStream();
    id.encode(localDerOutputStream);
    extensionValue = localDerOutputStream.toByteArray();
  }
  
  public SubjectKeyIdentifierExtension(byte[] paramArrayOfByte)
    throws IOException
  {
    id = new KeyIdentifier(paramArrayOfByte);
    extensionId = PKIXExtensions.SubjectKey_Id;
    critical = false;
    encodeThis();
  }
  
  public SubjectKeyIdentifierExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.SubjectKey_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue = new DerValue(extensionValue);
    id = new KeyIdentifier(localDerValue);
  }
  
  public String toString()
  {
    return super.toString() + "SubjectKeyIdentifier [\n" + String.valueOf(id) + "]\n";
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.SubjectKey_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("key_id"))
    {
      if (!(paramObject instanceof KeyIdentifier)) {
        throw new IOException("Attribute value should be of type KeyIdentifier.");
      }
      id = ((KeyIdentifier)paramObject);
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:SubjectKeyIdentifierExtension.");
    }
    encodeThis();
  }
  
  public KeyIdentifier get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("key_id")) {
      return id;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:SubjectKeyIdentifierExtension.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("key_id")) {
      id = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:SubjectKeyIdentifierExtension.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("key_id");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "SubjectKeyIdentifier";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\SubjectKeyIdentifierExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */