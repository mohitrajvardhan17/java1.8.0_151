package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class BasicConstraintsExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.BasicConstraints";
  public static final String NAME = "BasicConstraints";
  public static final String IS_CA = "is_ca";
  public static final String PATH_LEN = "path_len";
  private boolean ca = false;
  private int pathLen = -1;
  
  private void encodeThis()
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    if (ca)
    {
      localDerOutputStream2.putBoolean(ca);
      if (pathLen >= 0) {
        localDerOutputStream2.putInteger(pathLen);
      }
    }
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    extensionValue = localDerOutputStream1.toByteArray();
  }
  
  public BasicConstraintsExtension(boolean paramBoolean, int paramInt)
    throws IOException
  {
    this(Boolean.valueOf(paramBoolean), paramBoolean, paramInt);
  }
  
  public BasicConstraintsExtension(Boolean paramBoolean, boolean paramBoolean1, int paramInt)
    throws IOException
  {
    ca = paramBoolean1;
    pathLen = paramInt;
    extensionId = PKIXExtensions.BasicConstraints_Id;
    critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public BasicConstraintsExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.BasicConstraints_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue1 = new DerValue(extensionValue);
    if (tag != 48) {
      throw new IOException("Invalid encoding of BasicConstraints");
    }
    if ((data == null) || (data.available() == 0)) {
      return;
    }
    DerValue localDerValue2 = data.getDerValue();
    if (tag != 1) {
      return;
    }
    ca = localDerValue2.getBoolean();
    if (data.available() == 0)
    {
      pathLen = Integer.MAX_VALUE;
      return;
    }
    localDerValue2 = data.getDerValue();
    if (tag != 2) {
      throw new IOException("Invalid encoding of BasicConstraints");
    }
    pathLen = localDerValue2.getInteger();
  }
  
  public String toString()
  {
    String str = super.toString() + "BasicConstraints:[\n";
    str = str + (ca ? "  CA:true" : "  CA:false") + "\n";
    if (pathLen >= 0) {
      str = str + "  PathLen:" + pathLen + "\n";
    } else {
      str = str + "  PathLen: undefined\n";
    }
    return str + "]\n";
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.BasicConstraints_Id;
      if (ca) {
        critical = true;
      } else {
        critical = false;
      }
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("is_ca"))
    {
      if (!(paramObject instanceof Boolean)) {
        throw new IOException("Attribute value should be of type Boolean.");
      }
      ca = ((Boolean)paramObject).booleanValue();
    }
    else if (paramString.equalsIgnoreCase("path_len"))
    {
      if (!(paramObject instanceof Integer)) {
        throw new IOException("Attribute value should be of type Integer.");
      }
      pathLen = ((Integer)paramObject).intValue();
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:BasicConstraints.");
    }
    encodeThis();
  }
  
  public Object get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("is_ca")) {
      return Boolean.valueOf(ca);
    }
    if (paramString.equalsIgnoreCase("path_len")) {
      return Integer.valueOf(pathLen);
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:BasicConstraints.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("is_ca")) {
      ca = false;
    } else if (paramString.equalsIgnoreCase("path_len")) {
      pathLen = -1;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:BasicConstraints.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("is_ca");
    localAttributeNameEnumeration.addElement("path_len");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "BasicConstraints";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\BasicConstraintsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */