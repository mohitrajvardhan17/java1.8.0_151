package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class Extension
  implements java.security.cert.Extension
{
  protected ObjectIdentifier extensionId = null;
  protected boolean critical = false;
  protected byte[] extensionValue = null;
  private static final int hashMagic = 31;
  
  public Extension() {}
  
  public Extension(DerValue paramDerValue)
    throws IOException
  {
    DerInputStream localDerInputStream = paramDerValue.toDerInputStream();
    extensionId = localDerInputStream.getOID();
    DerValue localDerValue = localDerInputStream.getDerValue();
    if (tag == 1)
    {
      critical = localDerValue.getBoolean();
      localDerValue = localDerInputStream.getDerValue();
      extensionValue = localDerValue.getOctetString();
    }
    else
    {
      critical = false;
      extensionValue = localDerValue.getOctetString();
    }
  }
  
  public Extension(ObjectIdentifier paramObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfByte)
    throws IOException
  {
    extensionId = paramObjectIdentifier;
    critical = paramBoolean;
    DerValue localDerValue = new DerValue(paramArrayOfByte);
    extensionValue = localDerValue.getOctetString();
  }
  
  public Extension(Extension paramExtension)
  {
    extensionId = extensionId;
    critical = critical;
    extensionValue = extensionValue;
  }
  
  public static Extension newExtension(ObjectIdentifier paramObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfByte)
    throws IOException
  {
    Extension localExtension = new Extension();
    extensionId = paramObjectIdentifier;
    critical = paramBoolean;
    extensionValue = paramArrayOfByte;
    return localExtension;
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    if (paramOutputStream == null) {
      throw new NullPointerException();
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream1.putOID(extensionId);
    if (critical) {
      localDerOutputStream1.putBoolean(critical);
    }
    localDerOutputStream1.putOctetString(extensionValue);
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    paramOutputStream.write(localDerOutputStream2.toByteArray());
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    if (extensionId == null) {
      throw new IOException("Null OID to encode for the extension!");
    }
    if (extensionValue == null) {
      throw new IOException("No value to encode for the extension!");
    }
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putOID(extensionId);
    if (critical) {
      localDerOutputStream.putBoolean(critical);
    }
    localDerOutputStream.putOctetString(extensionValue);
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
  
  public boolean isCritical()
  {
    return critical;
  }
  
  public ObjectIdentifier getExtensionId()
  {
    return extensionId;
  }
  
  public byte[] getValue()
  {
    return (byte[])extensionValue.clone();
  }
  
  public byte[] getExtensionValue()
  {
    return extensionValue;
  }
  
  public String getId()
  {
    return extensionId.toString();
  }
  
  public String toString()
  {
    String str = "ObjectId: " + extensionId.toString();
    if (critical) {
      str = str + " Criticality=true\n";
    } else {
      str = str + " Criticality=false\n";
    }
    return str;
  }
  
  public int hashCode()
  {
    int i = 0;
    if (extensionValue != null)
    {
      byte[] arrayOfByte = extensionValue;
      int j = arrayOfByte.length;
      while (j > 0) {
        i += j * arrayOfByte[(--j)];
      }
    }
    i = i * 31 + extensionId.hashCode();
    i = i * 31 + (critical ? 1231 : 1237);
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Extension)) {
      return false;
    }
    Extension localExtension = (Extension)paramObject;
    if (critical != critical) {
      return false;
    }
    if (!extensionId.equals(extensionId)) {
      return false;
    }
    return Arrays.equals(extensionValue, extensionValue);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\Extension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */