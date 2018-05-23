package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class AuthorityKeyIdentifierExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.AuthorityKeyIdentifier";
  public static final String NAME = "AuthorityKeyIdentifier";
  public static final String KEY_ID = "key_id";
  public static final String AUTH_NAME = "auth_name";
  public static final String SERIAL_NUMBER = "serial_number";
  private static final byte TAG_ID = 0;
  private static final byte TAG_NAMES = 1;
  private static final byte TAG_SERIAL_NUM = 2;
  private KeyIdentifier id = null;
  private GeneralNames names = null;
  private SerialNumber serialNum = null;
  
  private void encodeThis()
    throws IOException
  {
    if ((id == null) && (names == null) && (serialNum == null))
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    DerOutputStream localDerOutputStream3;
    if (id != null)
    {
      localDerOutputStream3 = new DerOutputStream();
      id.encode(localDerOutputStream3);
      localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)0), localDerOutputStream3);
    }
    try
    {
      if (names != null)
      {
        localDerOutputStream3 = new DerOutputStream();
        names.encode(localDerOutputStream3);
        localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream3);
      }
    }
    catch (Exception localException)
    {
      throw new IOException(localException.toString());
    }
    if (serialNum != null)
    {
      DerOutputStream localDerOutputStream4 = new DerOutputStream();
      serialNum.encode(localDerOutputStream4);
      localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)2), localDerOutputStream4);
    }
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    extensionValue = localDerOutputStream1.toByteArray();
  }
  
  public AuthorityKeyIdentifierExtension(KeyIdentifier paramKeyIdentifier, GeneralNames paramGeneralNames, SerialNumber paramSerialNumber)
    throws IOException
  {
    id = paramKeyIdentifier;
    names = paramGeneralNames;
    serialNum = paramSerialNumber;
    extensionId = PKIXExtensions.AuthorityKey_Id;
    critical = false;
    encodeThis();
  }
  
  public AuthorityKeyIdentifierExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.AuthorityKey_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue1 = new DerValue(extensionValue);
    if (tag != 48) {
      throw new IOException("Invalid encoding for AuthorityKeyIdentifierExtension.");
    }
    while ((data != null) && (data.available() != 0))
    {
      DerValue localDerValue2 = data.getDerValue();
      if ((localDerValue2.isContextSpecific((byte)0)) && (!localDerValue2.isConstructed()))
      {
        if (id != null) {
          throw new IOException("Duplicate KeyIdentifier in AuthorityKeyIdentifier.");
        }
        localDerValue2.resetTag((byte)4);
        id = new KeyIdentifier(localDerValue2);
      }
      else if ((localDerValue2.isContextSpecific((byte)1)) && (localDerValue2.isConstructed()))
      {
        if (names != null) {
          throw new IOException("Duplicate GeneralNames in AuthorityKeyIdentifier.");
        }
        localDerValue2.resetTag((byte)48);
        names = new GeneralNames(localDerValue2);
      }
      else if ((localDerValue2.isContextSpecific((byte)2)) && (!localDerValue2.isConstructed()))
      {
        if (serialNum != null) {
          throw new IOException("Duplicate SerialNumber in AuthorityKeyIdentifier.");
        }
        localDerValue2.resetTag((byte)2);
        serialNum = new SerialNumber(localDerValue2);
      }
      else
      {
        throw new IOException("Invalid encoding of AuthorityKeyIdentifierExtension.");
      }
    }
  }
  
  public String toString()
  {
    String str = super.toString() + "AuthorityKeyIdentifier [\n";
    if (id != null) {
      str = str + id.toString();
    }
    if (names != null) {
      str = str + names.toString() + "\n";
    }
    if (serialNum != null) {
      str = str + serialNum.toString() + "\n";
    }
    return str + "]\n";
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.AuthorityKey_Id;
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
    else if (paramString.equalsIgnoreCase("auth_name"))
    {
      if (!(paramObject instanceof GeneralNames)) {
        throw new IOException("Attribute value should be of type GeneralNames.");
      }
      names = ((GeneralNames)paramObject);
    }
    else if (paramString.equalsIgnoreCase("serial_number"))
    {
      if (!(paramObject instanceof SerialNumber)) {
        throw new IOException("Attribute value should be of type SerialNumber.");
      }
      serialNum = ((SerialNumber)paramObject);
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:AuthorityKeyIdentifier.");
    }
    encodeThis();
  }
  
  public Object get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("key_id")) {
      return id;
    }
    if (paramString.equalsIgnoreCase("auth_name")) {
      return names;
    }
    if (paramString.equalsIgnoreCase("serial_number")) {
      return serialNum;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:AuthorityKeyIdentifier.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("key_id")) {
      id = null;
    } else if (paramString.equalsIgnoreCase("auth_name")) {
      names = null;
    } else if (paramString.equalsIgnoreCase("serial_number")) {
      serialNum = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:AuthorityKeyIdentifier.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("key_id");
    localAttributeNameEnumeration.addElement("auth_name");
    localAttributeNameEnumeration.addElement("serial_number");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "AuthorityKeyIdentifier";
  }
  
  public byte[] getEncodedKeyIdentifier()
    throws IOException
  {
    if (id != null)
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      id.encode(localDerOutputStream);
      return localDerOutputStream.toByteArray();
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\AuthorityKeyIdentifierExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */