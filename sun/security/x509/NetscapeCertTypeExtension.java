package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NetscapeCertTypeExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.NetscapeCertType";
  public static final String NAME = "NetscapeCertType";
  public static final String SSL_CLIENT = "ssl_client";
  public static final String SSL_SERVER = "ssl_server";
  public static final String S_MIME = "s_mime";
  public static final String OBJECT_SIGNING = "object_signing";
  public static final String SSL_CA = "ssl_ca";
  public static final String S_MIME_CA = "s_mime_ca";
  public static final String OBJECT_SIGNING_CA = "object_signing_ca";
  private static final int[] CertType_data = { 2, 16, 840, 1, 113730, 1, 1 };
  public static ObjectIdentifier NetscapeCertType_Id;
  private boolean[] bitString;
  private static MapEntry[] mMapData;
  private static final Vector<String> mAttributeNames;
  
  private static int getPosition(String paramString)
    throws IOException
  {
    for (int i = 0; i < mMapData.length; i++) {
      if (paramString.equalsIgnoreCase(mMapDatamName)) {
        return mMapDatamPosition;
      }
    }
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:NetscapeCertType.");
  }
  
  private void encodeThis()
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putTruncatedUnalignedBitString(new BitArray(bitString));
    extensionValue = localDerOutputStream.toByteArray();
  }
  
  private boolean isSet(int paramInt)
  {
    return (paramInt < bitString.length) && (bitString[paramInt] != 0);
  }
  
  private void set(int paramInt, boolean paramBoolean)
  {
    if (paramInt >= bitString.length)
    {
      boolean[] arrayOfBoolean = new boolean[paramInt + 1];
      System.arraycopy(bitString, 0, arrayOfBoolean, 0, bitString.length);
      bitString = arrayOfBoolean;
    }
    bitString[paramInt] = paramBoolean;
  }
  
  public NetscapeCertTypeExtension(byte[] paramArrayOfByte)
    throws IOException
  {
    bitString = new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte).toBooleanArray();
    extensionId = NetscapeCertType_Id;
    critical = true;
    encodeThis();
  }
  
  public NetscapeCertTypeExtension(boolean[] paramArrayOfBoolean)
    throws IOException
  {
    bitString = paramArrayOfBoolean;
    extensionId = NetscapeCertType_Id;
    critical = true;
    encodeThis();
  }
  
  public NetscapeCertTypeExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = NetscapeCertType_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue = new DerValue(extensionValue);
    bitString = localDerValue.getUnalignedBitString().toBooleanArray();
  }
  
  public NetscapeCertTypeExtension()
  {
    extensionId = NetscapeCertType_Id;
    critical = true;
    bitString = new boolean[0];
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof Boolean)) {
      throw new IOException("Attribute must be of type Boolean.");
    }
    boolean bool = ((Boolean)paramObject).booleanValue();
    set(getPosition(paramString), bool);
    encodeThis();
  }
  
  public Boolean get(String paramString)
    throws IOException
  {
    return Boolean.valueOf(isSet(getPosition(paramString)));
  }
  
  public void delete(String paramString)
    throws IOException
  {
    set(getPosition(paramString), false);
    encodeThis();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString());
    localStringBuilder.append("NetscapeCertType [\n");
    if (isSet(0)) {
      localStringBuilder.append("   SSL client\n");
    }
    if (isSet(1)) {
      localStringBuilder.append("   SSL server\n");
    }
    if (isSet(2)) {
      localStringBuilder.append("   S/MIME\n");
    }
    if (isSet(3)) {
      localStringBuilder.append("   Object Signing\n");
    }
    if (isSet(5)) {
      localStringBuilder.append("   SSL CA\n");
    }
    if (isSet(6)) {
      localStringBuilder.append("   S/MIME CA\n");
    }
    if (isSet(7)) {
      localStringBuilder.append("   Object Signing CA");
    }
    localStringBuilder.append("]\n");
    return localStringBuilder.toString();
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = NetscapeCertType_Id;
      critical = true;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements()
  {
    return mAttributeNames.elements();
  }
  
  public String getName()
  {
    return "NetscapeCertType";
  }
  
  public boolean[] getKeyUsageMappedBits()
  {
    KeyUsageExtension localKeyUsageExtension = new KeyUsageExtension();
    Boolean localBoolean = Boolean.TRUE;
    try
    {
      if ((isSet(getPosition("ssl_client"))) || (isSet(getPosition("s_mime"))) || (isSet(getPosition("object_signing")))) {
        localKeyUsageExtension.set("digital_signature", localBoolean);
      }
      if (isSet(getPosition("ssl_server"))) {
        localKeyUsageExtension.set("key_encipherment", localBoolean);
      }
      if ((isSet(getPosition("ssl_ca"))) || (isSet(getPosition("s_mime_ca"))) || (isSet(getPosition("object_signing_ca")))) {
        localKeyUsageExtension.set("key_certsign", localBoolean);
      }
    }
    catch (IOException localIOException) {}
    return localKeyUsageExtension.getBits();
  }
  
  static
  {
    try
    {
      NetscapeCertType_Id = new ObjectIdentifier(CertType_data);
    }
    catch (IOException localIOException) {}
    mMapData = new MapEntry[] { new MapEntry("ssl_client", 0), new MapEntry("ssl_server", 1), new MapEntry("s_mime", 2), new MapEntry("object_signing", 3), new MapEntry("ssl_ca", 5), new MapEntry("s_mime_ca", 6), new MapEntry("object_signing_ca", 7) };
    mAttributeNames = new Vector();
    for (MapEntry localMapEntry : mMapData) {
      mAttributeNames.add(mName);
    }
  }
  
  private static class MapEntry
  {
    String mName;
    int mPosition;
    
    MapEntry(String paramString, int paramInt)
    {
      mName = paramString;
      mPosition = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\NetscapeCertTypeExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */