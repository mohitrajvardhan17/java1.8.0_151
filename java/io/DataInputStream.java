package java.io;

public class DataInputStream
  extends FilterInputStream
  implements DataInput
{
  private byte[] bytearr = new byte[80];
  private char[] chararr = new char[80];
  private byte[] readBuffer = new byte[8];
  private char[] lineBuffer;
  
  public DataInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public final int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return in.read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return in.read(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public final void readFully(byte[] paramArrayOfByte)
    throws IOException
  {
    readFully(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public final void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 < 0) {
      throw new IndexOutOfBoundsException();
    }
    int i = 0;
    while (i < paramInt2)
    {
      int j = in.read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j < 0) {
        throw new EOFException();
      }
      i += j;
    }
  }
  
  public final int skipBytes(int paramInt)
    throws IOException
  {
    int i = 0;
    int j = 0;
    while ((i < paramInt) && ((j = (int)in.skip(paramInt - i)) > 0)) {
      i += j;
    }
    return i;
  }
  
  public final boolean readBoolean()
    throws IOException
  {
    int i = in.read();
    if (i < 0) {
      throw new EOFException();
    }
    return i != 0;
  }
  
  public final byte readByte()
    throws IOException
  {
    int i = in.read();
    if (i < 0) {
      throw new EOFException();
    }
    return (byte)i;
  }
  
  public final int readUnsignedByte()
    throws IOException
  {
    int i = in.read();
    if (i < 0) {
      throw new EOFException();
    }
    return i;
  }
  
  public final short readShort()
    throws IOException
  {
    int i = in.read();
    int j = in.read();
    if ((i | j) < 0) {
      throw new EOFException();
    }
    return (short)((i << 8) + (j << 0));
  }
  
  public final int readUnsignedShort()
    throws IOException
  {
    int i = in.read();
    int j = in.read();
    if ((i | j) < 0) {
      throw new EOFException();
    }
    return (i << 8) + (j << 0);
  }
  
  public final char readChar()
    throws IOException
  {
    int i = in.read();
    int j = in.read();
    if ((i | j) < 0) {
      throw new EOFException();
    }
    return (char)((i << 8) + (j << 0));
  }
  
  public final int readInt()
    throws IOException
  {
    int i = in.read();
    int j = in.read();
    int k = in.read();
    int m = in.read();
    if ((i | j | k | m) < 0) {
      throw new EOFException();
    }
    return (i << 24) + (j << 16) + (k << 8) + (m << 0);
  }
  
  public final long readLong()
    throws IOException
  {
    readFully(readBuffer, 0, 8);
    return (readBuffer[0] << 56) + ((readBuffer[1] & 0xFF) << 48) + ((readBuffer[2] & 0xFF) << 40) + ((readBuffer[3] & 0xFF) << 32) + ((readBuffer[4] & 0xFF) << 24) + ((readBuffer[5] & 0xFF) << 16) + ((readBuffer[6] & 0xFF) << 8) + ((readBuffer[7] & 0xFF) << 0);
  }
  
  public final float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(readInt());
  }
  
  public final double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(readLong());
  }
  
  @Deprecated
  public final String readLine()
    throws IOException
  {
    char[] arrayOfChar = lineBuffer;
    if (arrayOfChar == null) {
      arrayOfChar = lineBuffer = new char[''];
    }
    int i = arrayOfChar.length;
    int j = 0;
    int k;
    for (;;)
    {
      switch (k = in.read())
      {
      case -1: 
      case 10: 
        break;
      case 13: 
        int m = in.read();
        if ((m == 10) || (m == -1)) {
          break;
        }
        if (!(in instanceof PushbackInputStream)) {
          in = new PushbackInputStream(in);
        }
        ((PushbackInputStream)in).unread(m);
        break;
      default: 
        i--;
        if (i < 0)
        {
          arrayOfChar = new char[j + 128];
          i = arrayOfChar.length - j - 1;
          System.arraycopy(lineBuffer, 0, arrayOfChar, 0, j);
          lineBuffer = arrayOfChar;
        }
        arrayOfChar[(j++)] = ((char)k);
      }
    }
    if ((k == -1) && (j == 0)) {
      return null;
    }
    return String.copyValueOf(arrayOfChar, 0, j);
  }
  
  public final String readUTF()
    throws IOException
  {
    return readUTF(this);
  }
  
  public static final String readUTF(DataInput paramDataInput)
    throws IOException
  {
    int i = paramDataInput.readUnsignedShort();
    byte[] arrayOfByte = null;
    char[] arrayOfChar = null;
    if ((paramDataInput instanceof DataInputStream))
    {
      DataInputStream localDataInputStream = (DataInputStream)paramDataInput;
      if (bytearr.length < i)
      {
        bytearr = new byte[i * 2];
        chararr = new char[i * 2];
      }
      arrayOfChar = chararr;
      arrayOfByte = bytearr;
    }
    else
    {
      arrayOfByte = new byte[i];
      arrayOfChar = new char[i];
    }
    int n = 0;
    int i1 = 0;
    paramDataInput.readFully(arrayOfByte, 0, i);
    int j;
    while (n < i)
    {
      j = arrayOfByte[n] & 0xFF;
      if (j > 127) {
        break;
      }
      n++;
      arrayOfChar[(i1++)] = ((char)j);
    }
    while (n < i)
    {
      j = arrayOfByte[n] & 0xFF;
      int k;
      switch (j >> 4)
      {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
        n++;
        arrayOfChar[(i1++)] = ((char)j);
        break;
      case 12: 
      case 13: 
        n += 2;
        if (n > i) {
          throw new UTFDataFormatException("malformed input: partial character at end");
        }
        k = arrayOfByte[(n - 1)];
        if ((k & 0xC0) != 128) {
          throw new UTFDataFormatException("malformed input around byte " + n);
        }
        arrayOfChar[(i1++)] = ((char)((j & 0x1F) << 6 | k & 0x3F));
        break;
      case 14: 
        n += 3;
        if (n > i) {
          throw new UTFDataFormatException("malformed input: partial character at end");
        }
        k = arrayOfByte[(n - 2)];
        int m = arrayOfByte[(n - 1)];
        if (((k & 0xC0) != 128) || ((m & 0xC0) != 128)) {
          throw new UTFDataFormatException("malformed input around byte " + (n - 1));
        }
        arrayOfChar[(i1++)] = ((char)((j & 0xF) << 12 | (k & 0x3F) << 6 | (m & 0x3F) << 0));
        break;
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      default: 
        throw new UTFDataFormatException("malformed input around byte " + n);
      }
    }
    return new String(arrayOfChar, 0, i1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\DataInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */