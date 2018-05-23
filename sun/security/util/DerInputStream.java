package sun.security.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Vector;

public class DerInputStream
{
  DerInputBuffer buffer;
  public byte tag;
  
  public DerInputStream(byte[] paramArrayOfByte)
    throws IOException
  {
    init(paramArrayOfByte, 0, paramArrayOfByte.length, true);
  }
  
  public DerInputStream(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException
  {
    init(paramArrayOfByte, paramInt1, paramInt2, paramBoolean);
  }
  
  public DerInputStream(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    init(paramArrayOfByte, paramInt1, paramInt2, true);
  }
  
  private void init(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException
  {
    if ((paramInt1 + 2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 > paramArrayOfByte.length)) {
      throw new IOException("Encoding bytes too short");
    }
    if (DerIndefLenConverter.isIndefinite(paramArrayOfByte[(paramInt1 + 1)]))
    {
      if (!paramBoolean) {
        throw new IOException("Indefinite length BER encoding found");
      }
      byte[] arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
      DerIndefLenConverter localDerIndefLenConverter = new DerIndefLenConverter();
      buffer = new DerInputBuffer(localDerIndefLenConverter.convert(arrayOfByte), paramBoolean);
    }
    else
    {
      buffer = new DerInputBuffer(paramArrayOfByte, paramInt1, paramInt2, paramBoolean);
    }
    buffer.mark(Integer.MAX_VALUE);
  }
  
  DerInputStream(DerInputBuffer paramDerInputBuffer)
  {
    buffer = paramDerInputBuffer;
    buffer.mark(Integer.MAX_VALUE);
  }
  
  public DerInputStream subStream(int paramInt, boolean paramBoolean)
    throws IOException
  {
    DerInputBuffer localDerInputBuffer = buffer.dup();
    localDerInputBuffer.truncate(paramInt);
    if (paramBoolean) {
      buffer.skip(paramInt);
    }
    return new DerInputStream(localDerInputBuffer);
  }
  
  public byte[] toByteArray()
  {
    return buffer.toByteArray();
  }
  
  public int getInteger()
    throws IOException
  {
    if (buffer.read() != 2) {
      throw new IOException("DER input, Integer tag error");
    }
    return buffer.getInteger(getLength(buffer));
  }
  
  public BigInteger getBigInteger()
    throws IOException
  {
    if (buffer.read() != 2) {
      throw new IOException("DER input, Integer tag error");
    }
    return buffer.getBigInteger(getLength(buffer), false);
  }
  
  public BigInteger getPositiveBigInteger()
    throws IOException
  {
    if (buffer.read() != 2) {
      throw new IOException("DER input, Integer tag error");
    }
    return buffer.getBigInteger(getLength(buffer), true);
  }
  
  public int getEnumerated()
    throws IOException
  {
    if (buffer.read() != 10) {
      throw new IOException("DER input, Enumerated tag error");
    }
    return buffer.getInteger(getLength(buffer));
  }
  
  public byte[] getBitString()
    throws IOException
  {
    if (buffer.read() != 3) {
      throw new IOException("DER input not an bit string");
    }
    return buffer.getBitString(getLength(buffer));
  }
  
  public BitArray getUnalignedBitString()
    throws IOException
  {
    if (buffer.read() != 3) {
      throw new IOException("DER input not a bit string");
    }
    int i = getLength(buffer) - 1;
    int j = buffer.read();
    if (j < 0) {
      throw new IOException("Unused bits of bit string invalid");
    }
    int k = i * 8 - j;
    if (k < 0) {
      throw new IOException("Valid bits of bit string invalid");
    }
    byte[] arrayOfByte = new byte[i];
    if ((i != 0) && (buffer.read(arrayOfByte) != i)) {
      throw new IOException("Short read of DER bit string");
    }
    return new BitArray(k, arrayOfByte);
  }
  
  public byte[] getOctetString()
    throws IOException
  {
    if (buffer.read() != 4) {
      throw new IOException("DER input not an octet string");
    }
    int i = getLength(buffer);
    byte[] arrayOfByte = new byte[i];
    if ((i != 0) && (buffer.read(arrayOfByte) != i)) {
      throw new IOException("Short read of DER octet string");
    }
    return arrayOfByte;
  }
  
  public void getBytes(byte[] paramArrayOfByte)
    throws IOException
  {
    if ((paramArrayOfByte.length != 0) && (buffer.read(paramArrayOfByte) != paramArrayOfByte.length)) {
      throw new IOException("Short read of DER octet string");
    }
  }
  
  public void getNull()
    throws IOException
  {
    if ((buffer.read() != 5) || (buffer.read() != 0)) {
      throw new IOException("getNull, bad data");
    }
  }
  
  public ObjectIdentifier getOID()
    throws IOException
  {
    return new ObjectIdentifier(this);
  }
  
  public DerValue[] getSequence(int paramInt)
    throws IOException
  {
    tag = ((byte)buffer.read());
    if (tag != 48) {
      throw new IOException("Sequence tag error");
    }
    return readVector(paramInt);
  }
  
  public DerValue[] getSet(int paramInt)
    throws IOException
  {
    tag = ((byte)buffer.read());
    if (tag != 49) {
      throw new IOException("Set tag error");
    }
    return readVector(paramInt);
  }
  
  public DerValue[] getSet(int paramInt, boolean paramBoolean)
    throws IOException
  {
    tag = ((byte)buffer.read());
    if ((!paramBoolean) && (tag != 49)) {
      throw new IOException("Set tag error");
    }
    return readVector(paramInt);
  }
  
  protected DerValue[] readVector(int paramInt)
    throws IOException
  {
    int i = (byte)buffer.read();
    int j = getLength(i, buffer);
    if (j == -1)
    {
      int k = buffer.available();
      int m = 2;
      byte[] arrayOfByte = new byte[k + m];
      arrayOfByte[0] = tag;
      arrayOfByte[1] = i;
      DataInputStream localDataInputStream = new DataInputStream(buffer);
      localDataInputStream.readFully(arrayOfByte, m, k);
      localDataInputStream.close();
      localObject = new DerIndefLenConverter();
      buffer = new DerInputBuffer(((DerIndefLenConverter)localObject).convert(arrayOfByte), buffer.allowBER);
      if (tag != buffer.read()) {
        throw new IOException("Indefinite length encoding not supported");
      }
      j = getLength(buffer);
    }
    if (j == 0) {
      return new DerValue[0];
    }
    DerInputStream localDerInputStream;
    if (buffer.available() == j) {
      localDerInputStream = this;
    } else {
      localDerInputStream = subStream(j, true);
    }
    Vector localVector = new Vector(paramInt);
    do
    {
      DerValue localDerValue = new DerValue(buffer, buffer.allowBER);
      localVector.addElement(localDerValue);
    } while (localDerInputStream.available() > 0);
    if (localDerInputStream.available() != 0) {
      throw new IOException("Extra data at end of vector");
    }
    int i1 = localVector.size();
    Object localObject = new DerValue[i1];
    for (int n = 0; n < i1; n++) {
      localObject[n] = ((DerValue)localVector.elementAt(n));
    }
    return (DerValue[])localObject;
  }
  
  public DerValue getDerValue()
    throws IOException
  {
    return new DerValue(buffer);
  }
  
  public String getUTF8String()
    throws IOException
  {
    return readString((byte)12, "UTF-8", "UTF8");
  }
  
  public String getPrintableString()
    throws IOException
  {
    return readString((byte)19, "Printable", "ASCII");
  }
  
  public String getT61String()
    throws IOException
  {
    return readString((byte)20, "T61", "ISO-8859-1");
  }
  
  public String getIA5String()
    throws IOException
  {
    return readString((byte)22, "IA5", "ASCII");
  }
  
  public String getBMPString()
    throws IOException
  {
    return readString((byte)30, "BMP", "UnicodeBigUnmarked");
  }
  
  public String getGeneralString()
    throws IOException
  {
    return readString((byte)27, "General", "ASCII");
  }
  
  private String readString(byte paramByte, String paramString1, String paramString2)
    throws IOException
  {
    if (buffer.read() != paramByte) {
      throw new IOException("DER input not a " + paramString1 + " string");
    }
    int i = getLength(buffer);
    byte[] arrayOfByte = new byte[i];
    if ((i != 0) && (buffer.read(arrayOfByte) != i)) {
      throw new IOException("Short read of DER " + paramString1 + " string");
    }
    return new String(arrayOfByte, paramString2);
  }
  
  public Date getUTCTime()
    throws IOException
  {
    if (buffer.read() != 23) {
      throw new IOException("DER input, UTCtime tag invalid ");
    }
    return buffer.getUTCTime(getLength(buffer));
  }
  
  public Date getGeneralizedTime()
    throws IOException
  {
    if (buffer.read() != 24) {
      throw new IOException("DER input, GeneralizedTime tag invalid ");
    }
    return buffer.getGeneralizedTime(getLength(buffer));
  }
  
  int getByte()
    throws IOException
  {
    return 0xFF & buffer.read();
  }
  
  public int peekByte()
    throws IOException
  {
    return buffer.peek();
  }
  
  int getLength()
    throws IOException
  {
    return getLength(buffer);
  }
  
  static int getLength(InputStream paramInputStream)
    throws IOException
  {
    return getLength(paramInputStream.read(), paramInputStream);
  }
  
  static int getLength(int paramInt, InputStream paramInputStream)
    throws IOException
  {
    if (paramInt == -1) {
      throw new IOException("Short read of DER length");
    }
    String str = "DerInputStream.getLength(): ";
    int j = paramInt;
    int i;
    if ((j & 0x80) == 0)
    {
      i = j;
    }
    else
    {
      j &= 0x7F;
      if (j == 0) {
        return -1;
      }
      if ((j < 0) || (j > 4)) {
        throw new IOException(str + "lengthTag=" + j + ", " + (j < 0 ? "incorrect DER encoding." : "too big."));
      }
      i = 0xFF & paramInputStream.read();
      j--;
      if (i == 0) {
        throw new IOException(str + "Redundant length bytes found");
      }
      while (j-- > 0)
      {
        i <<= 8;
        i += (0xFF & paramInputStream.read());
      }
      if (i < 0) {
        throw new IOException(str + "Invalid length bytes");
      }
      if (i <= 127) {
        throw new IOException(str + "Should use short form for length");
      }
    }
    return i;
  }
  
  public void mark(int paramInt)
  {
    buffer.mark(paramInt);
  }
  
  public void reset()
  {
    buffer.reset();
  }
  
  public int available()
  {
    return buffer.available();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\DerInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */