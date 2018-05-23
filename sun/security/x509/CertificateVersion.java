package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateVersion
  implements CertAttrSet<String>
{
  public static final int V1 = 0;
  public static final int V2 = 1;
  public static final int V3 = 2;
  public static final String IDENT = "x509.info.version";
  public static final String NAME = "version";
  public static final String VERSION = "number";
  int version = 0;
  
  private int getVersion()
  {
    return version;
  }
  
  private void construct(DerValue paramDerValue)
    throws IOException
  {
    if ((paramDerValue.isConstructed()) && (paramDerValue.isContextSpecific()))
    {
      paramDerValue = data.getDerValue();
      version = paramDerValue.getInteger();
      if (data.available() != 0) {
        throw new IOException("X.509 version, bad format");
      }
    }
  }
  
  public CertificateVersion()
  {
    version = 0;
  }
  
  public CertificateVersion(int paramInt)
    throws IOException
  {
    if ((paramInt == 0) || (paramInt == 1) || (paramInt == 2)) {
      version = paramInt;
    } else {
      throw new IOException("X.509 Certificate version " + paramInt + " not supported.\n");
    }
  }
  
  public CertificateVersion(DerInputStream paramDerInputStream)
    throws IOException
  {
    version = 0;
    DerValue localDerValue = paramDerInputStream.getDerValue();
    construct(localDerValue);
  }
  
  public CertificateVersion(InputStream paramInputStream)
    throws IOException
  {
    version = 0;
    DerValue localDerValue = new DerValue(paramInputStream);
    construct(localDerValue);
  }
  
  public CertificateVersion(DerValue paramDerValue)
    throws IOException
  {
    version = 0;
    construct(paramDerValue);
  }
  
  public String toString()
  {
    return "Version: V" + (version + 1);
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    if (version == 0) {
      return;
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(version);
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream1);
    paramOutputStream.write(localDerOutputStream2.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof Integer)) {
      throw new IOException("Attribute must be of type Integer.");
    }
    if (paramString.equalsIgnoreCase("number")) {
      version = ((Integer)paramObject).intValue();
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateVersion.");
    }
  }
  
  public Integer get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("number")) {
      return new Integer(getVersion());
    }
    throw new IOException("Attribute name not recognized by CertAttrSet: CertificateVersion.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("number")) {
      version = 0;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateVersion.");
    }
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("number");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "version";
  }
  
  public int compare(int paramInt)
  {
    return version - paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificateVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */