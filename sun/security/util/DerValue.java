package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import sun.misc.IOUtils;

public class DerValue
{
  public static final byte TAG_UNIVERSAL = 0;
  public static final byte TAG_APPLICATION = 64;
  public static final byte TAG_CONTEXT = -128;
  public static final byte TAG_PRIVATE = -64;
  public byte tag;
  protected DerInputBuffer buffer;
  public final DerInputStream data;
  private int length;
  public static final byte tag_Boolean = 1;
  public static final byte tag_Integer = 2;
  public static final byte tag_BitString = 3;
  public static final byte tag_OctetString = 4;
  public static final byte tag_Null = 5;
  public static final byte tag_ObjectId = 6;
  public static final byte tag_Enumerated = 10;
  public static final byte tag_UTF8String = 12;
  public static final byte tag_PrintableString = 19;
  public static final byte tag_T61String = 20;
  public static final byte tag_IA5String = 22;
  public static final byte tag_UtcTime = 23;
  public static final byte tag_GeneralizedTime = 24;
  public static final byte tag_GeneralString = 27;
  public static final byte tag_UniversalString = 28;
  public static final byte tag_BMPString = 30;
  public static final byte tag_Sequence = 48;
  public static final byte tag_SequenceOf = 48;
  public static final byte tag_Set = 49;
  public static final byte tag_SetOf = 49;
  
  public boolean isUniversal()
  {
    return (tag & 0xC0) == 0;
  }
  
  public boolean isApplication()
  {
    return (tag & 0xC0) == 64;
  }
  
  public boolean isContextSpecific()
  {
    return (tag & 0xC0) == 128;
  }
  
  public boolean isContextSpecific(byte paramByte)
  {
    if (!isContextSpecific()) {
      return false;
    }
    return (tag & 0x1F) == paramByte;
  }
  
  boolean isPrivate()
  {
    return (tag & 0xC0) == 192;
  }
  
  public boolean isConstructed()
  {
    return (tag & 0x20) == 32;
  }
  
  public boolean isConstructed(byte paramByte)
  {
    if (!isConstructed()) {
      return false;
    }
    return (tag & 0x1F) == paramByte;
  }
  
  public DerValue(String paramString)
    throws IOException
  {
    int i = 1;
    for (int j = 0; j < paramString.length(); j++) {
      if (!isPrintableStringChar(paramString.charAt(j)))
      {
        i = 0;
        break;
      }
    }
    data = init((byte)(i != 0 ? 19 : 12), paramString);
  }
  
  public DerValue(byte paramByte, String paramString)
    throws IOException
  {
    data = init(paramByte, paramString);
  }
  
  DerValue(byte paramByte, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    tag = paramByte;
    buffer = new DerInputBuffer((byte[])paramArrayOfByte.clone(), paramBoolean);
    length = paramArrayOfByte.length;
    data = new DerInputStream(buffer);
    data.mark(Integer.MAX_VALUE);
  }
  
  public DerValue(byte paramByte, byte[] paramArrayOfByte)
  {
    this(paramByte, paramArrayOfByte, true);
  }
  
  DerValue(DerInputBuffer paramDerInputBuffer)
    throws IOException
  {
    tag = ((byte)paramDerInputBuffer.read());
    int i = (byte)paramDerInputBuffer.read();
    length = DerInputStream.getLength(i, paramDerInputBuffer);
    if (length == -1)
    {
      DerInputBuffer localDerInputBuffer = paramDerInputBuffer.dup();
      int j = localDerInputBuffer.available();
      int k = 2;
      byte[] arrayOfByte = new byte[j + k];
      arrayOfByte[0] = tag;
      arrayOfByte[1] = i;
      DataInputStream localDataInputStream = new DataInputStream(localDerInputBuffer);
      localDataInputStream.readFully(arrayOfByte, k, j);
      localDataInputStream.close();
      DerIndefLenConverter localDerIndefLenConverter = new DerIndefLenConverter();
      localDerInputBuffer = new DerInputBuffer(localDerIndefLenConverter.convert(arrayOfByte), allowBER);
      if (tag != localDerInputBuffer.read()) {
        throw new IOException("Indefinite length encoding not supported");
      }
      length = DerInputStream.getLength(localDerInputBuffer);
      buffer = localDerInputBuffer.dup();
      buffer.truncate(length);
      data = new DerInputStream(buffer);
      paramDerInputBuffer.skip(length + k);
    }
    else
    {
      buffer = paramDerInputBuffer.dup();
      buffer.truncate(length);
      data = new DerInputStream(buffer);
      paramDerInputBuffer.skip(length);
    }
  }
  
  DerValue(byte[] paramArrayOfByte, boolean paramBoolean)
    throws IOException
  {
    data = init(true, new ByteArrayInputStream(paramArrayOfByte), paramBoolean);
  }
  
  public DerValue(byte[] paramArrayOfByte)
    throws IOException
  {
    this(paramArrayOfByte, true);
  }
  
  DerValue(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException
  {
    data = init(true, new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2), paramBoolean);
  }
  
  public DerValue(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this(paramArrayOfByte, paramInt1, paramInt2, true);
  }
  
  DerValue(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    data = init(false, paramInputStream, paramBoolean);
  }
  
  public DerValue(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, true);
  }
  
  private DerInputStream init(byte paramByte, String paramString)
    throws IOException
  {
    String str = null;
    tag = paramByte;
    switch (paramByte)
    {
    case 19: 
    case 22: 
    case 27: 
      str = "ASCII";
      break;
    case 20: 
      str = "ISO-8859-1";
      break;
    case 30: 
      str = "UnicodeBigUnmarked";
      break;
    case 12: 
      str = "UTF8";
      break;
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
    case 21: 
    case 23: 
    case 24: 
    case 25: 
    case 26: 
    case 28: 
    case 29: 
    default: 
      throw new IllegalArgumentException("Unsupported DER string type");
    }
    byte[] arrayOfByte = paramString.getBytes(str);
    length = arrayOfByte.length;
    buffer = new DerInputBuffer(arrayOfByte, true);
    DerInputStream localDerInputStream = new DerInputStream(buffer);
    localDerInputStream.mark(Integer.MAX_VALUE);
    return localDerInputStream;
  }
  
  private DerInputStream init(boolean paramBoolean1, InputStream paramInputStream, boolean paramBoolean2)
    throws IOException
  {
    tag = ((byte)paramInputStream.read());
    int i = (byte)paramInputStream.read();
    length = DerInputStream.getLength(i, paramInputStream);
    if (length == -1)
    {
      int j = paramInputStream.available();
      int k = 2;
      byte[] arrayOfByte2 = new byte[j + k];
      arrayOfByte2[0] = tag;
      arrayOfByte2[1] = i;
      DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
      localDataInputStream.readFully(arrayOfByte2, k, j);
      localDataInputStream.close();
      DerIndefLenConverter localDerIndefLenConverter = new DerIndefLenConverter();
      paramInputStream = new ByteArrayInputStream(localDerIndefLenConverter.convert(arrayOfByte2));
      if (tag != paramInputStream.read()) {
        throw new IOException("Indefinite length encoding not supported");
      }
      length = DerInputStream.getLength(paramInputStream);
    }
    if ((paramBoolean1) && (paramInputStream.available() != length)) {
      throw new IOException("extra data given to DerValue constructor");
    }
    byte[] arrayOfByte1 = IOUtils.readFully(paramInputStream, length, true);
    buffer = new DerInputBuffer(arrayOfByte1, paramBoolean2);
    return new DerInputStream(buffer);
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    paramDerOutputStream.write(tag);
    paramDerOutputStream.putLength(length);
    if (length > 0)
    {
      byte[] arrayOfByte = new byte[length];
      synchronized (data)
      {
        buffer.reset();
        if (buffer.read(arrayOfByte) != length) {
          throw new IOException("short DER value read (encode)");
        }
        paramDerOutputStream.write(arrayOfByte);
      }
    }
  }
  
  public final DerInputStream getData()
  {
    return data;
  }
  
  public final byte getTag()
  {
    return tag;
  }
  
  public boolean getBoolean()
    throws IOException
  {
    if (tag != 1) {
      throw new IOException("DerValue.getBoolean, not a BOOLEAN " + tag);
    }
    if (length != 1) {
      throw new IOException("DerValue.getBoolean, invalid length " + length);
    }
    return buffer.read() != 0;
  }
  
  public ObjectIdentifier getOID()
    throws IOException
  {
    if (tag != 6) {
      throw new IOException("DerValue.getOID, not an OID " + tag);
    }
    return new ObjectIdentifier(buffer);
  }
  
  private byte[] append(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte1 == null) {
      return paramArrayOfByte2;
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramArrayOfByte2.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, paramArrayOfByte1.length, paramArrayOfByte2.length);
    return arrayOfByte;
  }
  
  public byte[] getOctetString()
    throws IOException
  {
    if ((tag != 4) && (!isConstructed((byte)4))) {
      throw new IOException("DerValue.getOctetString, not an Octet String: " + tag);
    }
    byte[] arrayOfByte = new byte[length];
    if (length == 0) {
      return arrayOfByte;
    }
    if (buffer.read(arrayOfByte) != length) {
      throw new IOException("short read on DerValue buffer");
    }
    if (isConstructed())
    {
      DerInputStream localDerInputStream = new DerInputStream(arrayOfByte, 0, arrayOfByte.length, buffer.allowBER);
      for (arrayOfByte = null; localDerInputStream.available() != 0; arrayOfByte = append(arrayOfByte, localDerInputStream.getOctetString())) {}
    }
    return arrayOfByte;
  }
  
  public int getInteger()
    throws IOException
  {
    if (tag != 2) {
      throw new IOException("DerValue.getInteger, not an int " + tag);
    }
    return buffer.getInteger(data.available());
  }
  
  public BigInteger getBigInteger()
    throws IOException
  {
    if (tag != 2) {
      throw new IOException("DerValue.getBigInteger, not an int " + tag);
    }
    return buffer.getBigInteger(data.available(), false);
  }
  
  public BigInteger getPositiveBigInteger()
    throws IOException
  {
    if (tag != 2) {
      throw new IOException("DerValue.getBigInteger, not an int " + tag);
    }
    return buffer.getBigInteger(data.available(), true);
  }
  
  public int getEnumerated()
    throws IOException
  {
    if (tag != 10) {
      throw new IOException("DerValue.getEnumerated, incorrect tag: " + tag);
    }
    return buffer.getInteger(data.available());
  }
  
  public byte[] getBitString()
    throws IOException
  {
    if (tag != 3) {
      throw new IOException("DerValue.getBitString, not a bit string " + tag);
    }
    return buffer.getBitString();
  }
  
  public BitArray getUnalignedBitString()
    throws IOException
  {
    if (tag != 3) {
      throw new IOException("DerValue.getBitString, not a bit string " + tag);
    }
    return buffer.getUnalignedBitString();
  }
  
  public String getAsString()
    throws IOException
  {
    if (tag == 12) {
      return getUTF8String();
    }
    if (tag == 19) {
      return getPrintableString();
    }
    if (tag == 20) {
      return getT61String();
    }
    if (tag == 22) {
      return getIA5String();
    }
    if (tag == 30) {
      return getBMPString();
    }
    if (tag == 27) {
      return getGeneralString();
    }
    return null;
  }
  
  public byte[] getBitString(boolean paramBoolean)
    throws IOException
  {
    if ((!paramBoolean) && (tag != 3)) {
      throw new IOException("DerValue.getBitString, not a bit string " + tag);
    }
    return buffer.getBitString();
  }
  
  public BitArray getUnalignedBitString(boolean paramBoolean)
    throws IOException
  {
    if ((!paramBoolean) && (tag != 3)) {
      throw new IOException("DerValue.getBitString, not a bit string " + tag);
    }
    return buffer.getUnalignedBitString();
  }
  
  public byte[] getDataBytes()
    throws IOException
  {
    byte[] arrayOfByte = new byte[length];
    synchronized (data)
    {
      data.reset();
      data.getBytes(arrayOfByte);
    }
    return arrayOfByte;
  }
  
  public String getPrintableString()
    throws IOException
  {
    if (tag != 19) {
      throw new IOException("DerValue.getPrintableString, not a string " + tag);
    }
    return new String(getDataBytes(), "ASCII");
  }
  
  public String getT61String()
    throws IOException
  {
    if (tag != 20) {
      throw new IOException("DerValue.getT61String, not T61 " + tag);
    }
    return new String(getDataBytes(), "ISO-8859-1");
  }
  
  public String getIA5String()
    throws IOException
  {
    if (tag != 22) {
      throw new IOException("DerValue.getIA5String, not IA5 " + tag);
    }
    return new String(getDataBytes(), "ASCII");
  }
  
  public String getBMPString()
    throws IOException
  {
    if (tag != 30) {
      throw new IOException("DerValue.getBMPString, not BMP " + tag);
    }
    return new String(getDataBytes(), "UnicodeBigUnmarked");
  }
  
  public String getUTF8String()
    throws IOException
  {
    if (tag != 12) {
      throw new IOException("DerValue.getUTF8String, not UTF-8 " + tag);
    }
    return new String(getDataBytes(), "UTF8");
  }
  
  public String getGeneralString()
    throws IOException
  {
    if (tag != 27) {
      throw new IOException("DerValue.getGeneralString, not GeneralString " + tag);
    }
    return new String(getDataBytes(), "ASCII");
  }
  
  public Date getUTCTime()
    throws IOException
  {
    if (tag != 23) {
      throw new IOException("DerValue.getUTCTime, not a UtcTime: " + tag);
    }
    return buffer.getUTCTime(data.available());
  }
  
  public Date getGeneralizedTime()
    throws IOException
  {
    if (tag != 24) {
      throw new IOException("DerValue.getGeneralizedTime, not a GeneralizedTime: " + tag);
    }
    return buffer.getGeneralizedTime(data.available());
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DerValue)) {
      return equals((DerValue)paramObject);
    }
    return false;
  }
  
  public boolean equals(DerValue paramDerValue)
  {
    if (this == paramDerValue) {
      return true;
    }
    if (tag != tag) {
      return false;
    }
    if (data == data) {
      return true;
    }
    return System.identityHashCode(data) > System.identityHashCode(data) ? doEquals(this, paramDerValue) : doEquals(paramDerValue, this);
  }
  
  private static boolean doEquals(DerValue paramDerValue1, DerValue paramDerValue2)
  {
    synchronized (data)
    {
      synchronized (data)
      {
        data.reset();
        data.reset();
        return buffer.equals(buffer);
      }
    }
  }
  
  public String toString()
  {
    try
    {
      String str = getAsString();
      if (str != null) {
        return "\"" + str + "\"";
      }
      if (tag == 5) {
        return "[DerValue, null]";
      }
      if (tag == 6) {
        return "OID." + getOID();
      }
      return "[DerValue, tag = " + tag + ", length = " + length + "]";
    }
    catch (IOException localIOException)
    {
      throw new IllegalArgumentException("misformatted DER value");
    }
  }
  
  public byte[] toByteArray()
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    encode(localDerOutputStream);
    data.reset();
    return localDerOutputStream.toByteArray();
  }
  
  public DerInputStream toDerInputStream()
    throws IOException
  {
    if ((tag == 48) || (tag == 49)) {
      return new DerInputStream(buffer);
    }
    throw new IOException("toDerInputStream rejects tag type " + tag);
  }
  
  public int length()
  {
    return length;
  }
  
  public static boolean isPrintableStringChar(char paramChar)
  {
    if (((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z')) || ((paramChar >= '0') && (paramChar <= '9'))) {
      return true;
    }
    switch (paramChar)
    {
    case ' ': 
    case '\'': 
    case '(': 
    case ')': 
    case '+': 
    case ',': 
    case '-': 
    case '.': 
    case '/': 
    case ':': 
    case '=': 
    case '?': 
      return true;
    }
    return false;
  }
  
  public static byte createTag(byte paramByte1, boolean paramBoolean, byte paramByte2)
  {
    byte b = (byte)(paramByte1 | paramByte2);
    if (paramBoolean) {
      b = (byte)(b | 0x20);
    }
    return b;
  }
  
  public void resetTag(byte paramByte)
  {
    tag = paramByte;
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\DerValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */