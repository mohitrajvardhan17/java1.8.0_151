package javax.imageio.stream;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Stack;
import javax.imageio.IIOException;

public abstract class ImageInputStreamImpl
  implements ImageInputStream
{
  private Stack markByteStack = new Stack();
  private Stack markBitStack = new Stack();
  private boolean isClosed = false;
  private static final int BYTE_BUF_LENGTH = 8192;
  byte[] byteBuf = new byte[' '];
  protected ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
  protected long streamPos;
  protected int bitOffset;
  protected long flushedPos = 0L;
  
  public ImageInputStreamImpl() {}
  
  protected final void checkClosed()
    throws IOException
  {
    if (isClosed) {
      throw new IOException("closed");
    }
  }
  
  public void setByteOrder(ByteOrder paramByteOrder)
  {
    byteOrder = paramByteOrder;
  }
  
  public ByteOrder getByteOrder()
  {
    return byteOrder;
  }
  
  public abstract int read()
    throws IOException;
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public void readBytes(IIOByteBuffer paramIIOByteBuffer, int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw new IndexOutOfBoundsException("len < 0!");
    }
    if (paramIIOByteBuffer == null) {
      throw new NullPointerException("buf == null!");
    }
    byte[] arrayOfByte = new byte[paramInt];
    paramInt = read(arrayOfByte, 0, paramInt);
    paramIIOByteBuffer.setData(arrayOfByte);
    paramIIOByteBuffer.setOffset(0);
    paramIIOByteBuffer.setLength(paramInt);
  }
  
  public boolean readBoolean()
    throws IOException
  {
    int i = read();
    if (i < 0) {
      throw new EOFException();
    }
    return i != 0;
  }
  
  public byte readByte()
    throws IOException
  {
    int i = read();
    if (i < 0) {
      throw new EOFException();
    }
    return (byte)i;
  }
  
  public int readUnsignedByte()
    throws IOException
  {
    int i = read();
    if (i < 0) {
      throw new EOFException();
    }
    return i;
  }
  
  public short readShort()
    throws IOException
  {
    if (read(byteBuf, 0, 2) != 2) {
      throw new EOFException();
    }
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      return (short)((byteBuf[0] & 0xFF) << 8 | (byteBuf[1] & 0xFF) << 0);
    }
    return (short)((byteBuf[1] & 0xFF) << 8 | (byteBuf[0] & 0xFF) << 0);
  }
  
  public int readUnsignedShort()
    throws IOException
  {
    return readShort() & 0xFFFF;
  }
  
  public char readChar()
    throws IOException
  {
    return (char)readShort();
  }
  
  public int readInt()
    throws IOException
  {
    if (read(byteBuf, 0, 4) != 4) {
      throw new EOFException();
    }
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      return (byteBuf[0] & 0xFF) << 24 | (byteBuf[1] & 0xFF) << 16 | (byteBuf[2] & 0xFF) << 8 | (byteBuf[3] & 0xFF) << 0;
    }
    return (byteBuf[3] & 0xFF) << 24 | (byteBuf[2] & 0xFF) << 16 | (byteBuf[1] & 0xFF) << 8 | (byteBuf[0] & 0xFF) << 0;
  }
  
  public long readUnsignedInt()
    throws IOException
  {
    return readInt() & 0xFFFFFFFF;
  }
  
  public long readLong()
    throws IOException
  {
    int i = readInt();
    int j = readInt();
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      return (i << 32) + (j & 0xFFFFFFFF);
    }
    return (j << 32) + (i & 0xFFFFFFFF);
  }
  
  public float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(readInt());
  }
  
  public double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(readLong());
  }
  
  public String readLine()
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = -1;
    int j = 0;
    while (j == 0) {
      switch (i = read())
      {
      case -1: 
      case 10: 
        j = 1;
        break;
      case 13: 
        j = 1;
        long l = getStreamPosition();
        if (read() != 10) {
          seek(l);
        }
        break;
      default: 
        localStringBuffer.append((char)i);
      }
    }
    if ((i == -1) && (localStringBuffer.length() == 0)) {
      return null;
    }
    return localStringBuffer.toString();
  }
  
  public String readUTF()
    throws IOException
  {
    bitOffset = 0;
    ByteOrder localByteOrder = getByteOrder();
    setByteOrder(ByteOrder.BIG_ENDIAN);
    String str;
    try
    {
      str = DataInputStream.readUTF(this);
    }
    catch (IOException localIOException)
    {
      setByteOrder(localByteOrder);
      throw localIOException;
    }
    setByteOrder(localByteOrder);
    return str;
  }
  
  public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length!");
    }
    while (paramInt2 > 0)
    {
      int i = read(paramArrayOfByte, paramInt1, paramInt2);
      if (i == -1) {
        throw new EOFException();
      }
      paramInt1 += i;
      paramInt2 -= i;
    }
  }
  
  public void readFully(byte[] paramArrayOfByte)
    throws IOException
  {
    readFully(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void readFully(short[] paramArrayOfShort, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfShort.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!");
    }
    while (paramInt2 > 0)
    {
      int i = Math.min(paramInt2, byteBuf.length / 2);
      readFully(byteBuf, 0, i * 2);
      toShorts(byteBuf, paramArrayOfShort, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    }
  }
  
  public void readFully(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!");
    }
    while (paramInt2 > 0)
    {
      int i = Math.min(paramInt2, byteBuf.length / 2);
      readFully(byteBuf, 0, i * 2);
      toChars(byteBuf, paramArrayOfChar, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    }
  }
  
  public void readFully(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfInt.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!");
    }
    while (paramInt2 > 0)
    {
      int i = Math.min(paramInt2, byteBuf.length / 4);
      readFully(byteBuf, 0, i * 4);
      toInts(byteBuf, paramArrayOfInt, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    }
  }
  
  public void readFully(long[] paramArrayOfLong, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfLong.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!");
    }
    while (paramInt2 > 0)
    {
      int i = Math.min(paramInt2, byteBuf.length / 8);
      readFully(byteBuf, 0, i * 8);
      toLongs(byteBuf, paramArrayOfLong, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    }
  }
  
  public void readFully(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfFloat.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!");
    }
    while (paramInt2 > 0)
    {
      int i = Math.min(paramInt2, byteBuf.length / 4);
      readFully(byteBuf, 0, i * 4);
      toFloats(byteBuf, paramArrayOfFloat, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    }
  }
  
  public void readFully(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfDouble.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!");
    }
    while (paramInt2 > 0)
    {
      int i = Math.min(paramInt2, byteBuf.length / 8);
      readFully(byteBuf, 0, i * 8);
      toDoubles(byteBuf, paramArrayOfDouble, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    }
  }
  
  private void toShorts(byte[] paramArrayOfByte, short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j;
    int k;
    int m;
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[i];
        m = paramArrayOfByte[(i + 1)] & 0xFF;
        paramArrayOfShort[(paramInt1 + j)] = ((short)(k << 8 | m));
        i += 2;
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[(i + 1)];
        m = paramArrayOfByte[i] & 0xFF;
        paramArrayOfShort[(paramInt1 + j)] = ((short)(k << 8 | m));
        i += 2;
      }
    }
  }
  
  private void toChars(byte[] paramArrayOfByte, char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j;
    int k;
    int m;
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[i];
        m = paramArrayOfByte[(i + 1)] & 0xFF;
        paramArrayOfChar[(paramInt1 + j)] = ((char)(k << 8 | m));
        i += 2;
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[(i + 1)];
        m = paramArrayOfByte[i] & 0xFF;
        paramArrayOfChar[(paramInt1 + j)] = ((char)(k << 8 | m));
        i += 2;
      }
    }
  }
  
  private void toInts(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j;
    int k;
    int m;
    int n;
    int i1;
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[i];
        m = paramArrayOfByte[(i + 1)] & 0xFF;
        n = paramArrayOfByte[(i + 2)] & 0xFF;
        i1 = paramArrayOfByte[(i + 3)] & 0xFF;
        paramArrayOfInt[(paramInt1 + j)] = (k << 24 | m << 16 | n << 8 | i1);
        i += 4;
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[(i + 3)];
        m = paramArrayOfByte[(i + 2)] & 0xFF;
        n = paramArrayOfByte[(i + 1)] & 0xFF;
        i1 = paramArrayOfByte[i] & 0xFF;
        paramArrayOfInt[(paramInt1 + j)] = (k << 24 | m << 16 | n << 8 | i1);
        i += 4;
      }
    }
  }
  
  private void toLongs(byte[] paramArrayOfByte, long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    int i6;
    int i7;
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[i];
        m = paramArrayOfByte[(i + 1)] & 0xFF;
        n = paramArrayOfByte[(i + 2)] & 0xFF;
        i1 = paramArrayOfByte[(i + 3)] & 0xFF;
        i2 = paramArrayOfByte[(i + 4)];
        i3 = paramArrayOfByte[(i + 5)] & 0xFF;
        i4 = paramArrayOfByte[(i + 6)] & 0xFF;
        i5 = paramArrayOfByte[(i + 7)] & 0xFF;
        i6 = k << 24 | m << 16 | n << 8 | i1;
        i7 = i2 << 24 | i3 << 16 | i4 << 8 | i5;
        paramArrayOfLong[(paramInt1 + j)] = (i6 << 32 | i7 & 0xFFFFFFFF);
        i += 8;
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[(i + 7)];
        m = paramArrayOfByte[(i + 6)] & 0xFF;
        n = paramArrayOfByte[(i + 5)] & 0xFF;
        i1 = paramArrayOfByte[(i + 4)] & 0xFF;
        i2 = paramArrayOfByte[(i + 3)];
        i3 = paramArrayOfByte[(i + 2)] & 0xFF;
        i4 = paramArrayOfByte[(i + 1)] & 0xFF;
        i5 = paramArrayOfByte[i] & 0xFF;
        i6 = k << 24 | m << 16 | n << 8 | i1;
        i7 = i2 << 24 | i3 << 16 | i4 << 8 | i5;
        paramArrayOfLong[(paramInt1 + j)] = (i6 << 32 | i7 & 0xFFFFFFFF);
        i += 8;
      }
    }
  }
  
  private void toFloats(byte[] paramArrayOfByte, float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[i];
        m = paramArrayOfByte[(i + 1)] & 0xFF;
        n = paramArrayOfByte[(i + 2)] & 0xFF;
        i1 = paramArrayOfByte[(i + 3)] & 0xFF;
        i2 = k << 24 | m << 16 | n << 8 | i1;
        paramArrayOfFloat[(paramInt1 + j)] = Float.intBitsToFloat(i2);
        i += 4;
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[(i + 3)];
        m = paramArrayOfByte[(i + 2)] & 0xFF;
        n = paramArrayOfByte[(i + 1)] & 0xFF;
        i1 = paramArrayOfByte[(i + 0)] & 0xFF;
        i2 = k << 24 | m << 16 | n << 8 | i1;
        paramArrayOfFloat[(paramInt1 + j)] = Float.intBitsToFloat(i2);
        i += 4;
      }
    }
  }
  
  private void toDoubles(byte[] paramArrayOfByte, double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    int i6;
    int i7;
    long l;
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[i];
        m = paramArrayOfByte[(i + 1)] & 0xFF;
        n = paramArrayOfByte[(i + 2)] & 0xFF;
        i1 = paramArrayOfByte[(i + 3)] & 0xFF;
        i2 = paramArrayOfByte[(i + 4)];
        i3 = paramArrayOfByte[(i + 5)] & 0xFF;
        i4 = paramArrayOfByte[(i + 6)] & 0xFF;
        i5 = paramArrayOfByte[(i + 7)] & 0xFF;
        i6 = k << 24 | m << 16 | n << 8 | i1;
        i7 = i2 << 24 | i3 << 16 | i4 << 8 | i5;
        l = i6 << 32 | i7 & 0xFFFFFFFF;
        paramArrayOfDouble[(paramInt1 + j)] = Double.longBitsToDouble(l);
        i += 8;
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfByte[(i + 7)];
        m = paramArrayOfByte[(i + 6)] & 0xFF;
        n = paramArrayOfByte[(i + 5)] & 0xFF;
        i1 = paramArrayOfByte[(i + 4)] & 0xFF;
        i2 = paramArrayOfByte[(i + 3)];
        i3 = paramArrayOfByte[(i + 2)] & 0xFF;
        i4 = paramArrayOfByte[(i + 1)] & 0xFF;
        i5 = paramArrayOfByte[i] & 0xFF;
        i6 = k << 24 | m << 16 | n << 8 | i1;
        i7 = i2 << 24 | i3 << 16 | i4 << 8 | i5;
        l = i6 << 32 | i7 & 0xFFFFFFFF;
        paramArrayOfDouble[(paramInt1 + j)] = Double.longBitsToDouble(l);
        i += 8;
      }
    }
  }
  
  public long getStreamPosition()
    throws IOException
  {
    checkClosed();
    return streamPos;
  }
  
  public int getBitOffset()
    throws IOException
  {
    checkClosed();
    return bitOffset;
  }
  
  public void setBitOffset(int paramInt)
    throws IOException
  {
    checkClosed();
    if ((paramInt < 0) || (paramInt > 7)) {
      throw new IllegalArgumentException("bitOffset must be betwwen 0 and 7!");
    }
    bitOffset = paramInt;
  }
  
  public int readBit()
    throws IOException
  {
    checkClosed();
    int i = bitOffset + 1 & 0x7;
    int j = read();
    if (j == -1) {
      throw new EOFException();
    }
    if (i != 0)
    {
      seek(getStreamPosition() - 1L);
      j >>= 8 - i;
    }
    bitOffset = i;
    return j & 0x1;
  }
  
  public long readBits(int paramInt)
    throws IOException
  {
    checkClosed();
    if ((paramInt < 0) || (paramInt > 64)) {
      throw new IllegalArgumentException();
    }
    if (paramInt == 0) {
      return 0L;
    }
    int i = paramInt + bitOffset;
    int j = bitOffset + paramInt & 0x7;
    long l = 0L;
    while (i > 0)
    {
      int k = read();
      if (k == -1) {
        throw new EOFException();
      }
      l <<= 8;
      l |= k;
      i -= 8;
    }
    if (j != 0) {
      seek(getStreamPosition() - 1L);
    }
    bitOffset = j;
    l >>>= -i;
    l &= -1L >>> 64 - paramInt;
    return l;
  }
  
  public long length()
  {
    return -1L;
  }
  
  public int skipBytes(int paramInt)
    throws IOException
  {
    long l = getStreamPosition();
    seek(l + paramInt);
    return (int)(getStreamPosition() - l);
  }
  
  public long skipBytes(long paramLong)
    throws IOException
  {
    long l = getStreamPosition();
    seek(l + paramLong);
    return getStreamPosition() - l;
  }
  
  public void seek(long paramLong)
    throws IOException
  {
    checkClosed();
    if (paramLong < flushedPos) {
      throw new IndexOutOfBoundsException("pos < flushedPos!");
    }
    streamPos = paramLong;
    bitOffset = 0;
  }
  
  public void mark()
  {
    try
    {
      markByteStack.push(Long.valueOf(getStreamPosition()));
      markBitStack.push(Integer.valueOf(getBitOffset()));
    }
    catch (IOException localIOException) {}
  }
  
  public void reset()
    throws IOException
  {
    if (markByteStack.empty()) {
      return;
    }
    long l = ((Long)markByteStack.pop()).longValue();
    if (l < flushedPos) {
      throw new IIOException("Previous marked position has been discarded!");
    }
    seek(l);
    int i = ((Integer)markBitStack.pop()).intValue();
    setBitOffset(i);
  }
  
  public void flushBefore(long paramLong)
    throws IOException
  {
    checkClosed();
    if (paramLong < flushedPos) {
      throw new IndexOutOfBoundsException("pos < flushedPos!");
    }
    if (paramLong > getStreamPosition()) {
      throw new IndexOutOfBoundsException("pos > getStreamPosition()!");
    }
    flushedPos = paramLong;
  }
  
  public void flush()
    throws IOException
  {
    flushBefore(getStreamPosition());
  }
  
  public long getFlushedPosition()
  {
    return flushedPos;
  }
  
  public boolean isCached()
  {
    return false;
  }
  
  public boolean isCachedMemory()
  {
    return false;
  }
  
  public boolean isCachedFile()
  {
    return false;
  }
  
  public void close()
    throws IOException
  {
    checkClosed();
    isClosed = true;
  }
  
  protected void finalize()
    throws Throwable
  {
    if (!isClosed) {
      try
      {
        close();
      }
      catch (IOException localIOException) {}
    }
    super.finalize();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\stream\ImageInputStreamImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */