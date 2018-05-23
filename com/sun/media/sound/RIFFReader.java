package com.sun.media.sound;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class RIFFReader
  extends InputStream
{
  private final RIFFReader root;
  private long filepointer = 0L;
  private final String fourcc;
  private String riff_type = null;
  private long ckSize = 2147483647L;
  private InputStream stream;
  private long avail = 2147483647L;
  private RIFFReader lastiterator = null;
  
  public RIFFReader(InputStream paramInputStream)
    throws IOException
  {
    if ((paramInputStream instanceof RIFFReader)) {
      root = root;
    } else {
      root = this;
    }
    stream = paramInputStream;
    int i;
    for (;;)
    {
      i = read();
      if (i == -1)
      {
        fourcc = "";
        riff_type = null;
        avail = 0L;
        return;
      }
      if (i != 0) {
        break;
      }
    }
    byte[] arrayOfByte1 = new byte[4];
    arrayOfByte1[0] = ((byte)i);
    readFully(arrayOfByte1, 1, 3);
    fourcc = new String(arrayOfByte1, "ascii");
    ckSize = readUnsignedInt();
    avail = ckSize;
    if ((getFormat().equals("RIFF")) || (getFormat().equals("LIST")))
    {
      if (avail > 2147483647L) {
        throw new RIFFInvalidDataException("Chunk size too big");
      }
      byte[] arrayOfByte2 = new byte[4];
      readFully(arrayOfByte2);
      riff_type = new String(arrayOfByte2, "ascii");
    }
  }
  
  public long getFilePointer()
    throws IOException
  {
    return root.filepointer;
  }
  
  public boolean hasNextChunk()
    throws IOException
  {
    if (lastiterator != null) {
      lastiterator.finish();
    }
    return avail != 0L;
  }
  
  public RIFFReader nextChunk()
    throws IOException
  {
    if (lastiterator != null) {
      lastiterator.finish();
    }
    if (avail == 0L) {
      return null;
    }
    lastiterator = new RIFFReader(this);
    return lastiterator;
  }
  
  public String getFormat()
  {
    return fourcc;
  }
  
  public String getType()
  {
    return riff_type;
  }
  
  public long getSize()
  {
    return ckSize;
  }
  
  public int read()
    throws IOException
  {
    if (avail == 0L) {
      return -1;
    }
    int i = stream.read();
    if (i == -1)
    {
      avail = 0L;
      return -1;
    }
    avail -= 1L;
    filepointer += 1L;
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (avail == 0L) {
      return -1;
    }
    if (paramInt2 > avail)
    {
      i = stream.read(paramArrayOfByte, paramInt1, (int)avail);
      if (i != -1) {
        filepointer += i;
      }
      avail = 0L;
      return i;
    }
    int i = stream.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i == -1)
    {
      avail = 0L;
      return -1;
    }
    avail -= i;
    filepointer += i;
    return i;
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
    while (paramInt2 > 0)
    {
      int i = read(paramArrayOfByte, paramInt1, paramInt2);
      if (i < 0) {
        throw new EOFException();
      }
      if (i == 0) {
        Thread.yield();
      }
      paramInt1 += i;
      paramInt2 -= i;
    }
  }
  
  public final long skipBytes(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      return 0L;
    }
    long l2;
    for (long l1 = 0L; l1 != paramLong; l1 += l2)
    {
      l2 = skip(paramLong - l1);
      if (l2 < 0L) {
        break;
      }
      if (l2 == 0L) {
        Thread.yield();
      }
    }
    return l1;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (avail == 0L) {
      return -1L;
    }
    if (paramLong > avail)
    {
      l = stream.skip(avail);
      if (l != -1L) {
        filepointer += l;
      }
      avail = 0L;
      return l;
    }
    long l = stream.skip(paramLong);
    if (l == -1L)
    {
      avail = 0L;
      return -1L;
    }
    avail -= l;
    filepointer += l;
    return l;
  }
  
  public int available()
  {
    return (int)avail;
  }
  
  public void finish()
    throws IOException
  {
    if (avail != 0L) {
      skipBytes(avail);
    }
  }
  
  public String readString(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = new byte[paramInt];
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      throw new IOException("Length too big", localOutOfMemoryError);
    }
    readFully(arrayOfByte);
    for (int i = 0; i < arrayOfByte.length; i++) {
      if (arrayOfByte[i] == 0) {
        return new String(arrayOfByte, 0, i, "ascii");
      }
    }
    return new String(arrayOfByte, "ascii");
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
  
  public short readShort()
    throws IOException
  {
    int i = read();
    int j = read();
    if (i < 0) {
      throw new EOFException();
    }
    if (j < 0) {
      throw new EOFException();
    }
    return (short)(i | j << 8);
  }
  
  public int readInt()
    throws IOException
  {
    int i = read();
    int j = read();
    int k = read();
    int m = read();
    if (i < 0) {
      throw new EOFException();
    }
    if (j < 0) {
      throw new EOFException();
    }
    if (k < 0) {
      throw new EOFException();
    }
    if (m < 0) {
      throw new EOFException();
    }
    return i + (j << 8) | k << 16 | m << 24;
  }
  
  public long readLong()
    throws IOException
  {
    long l1 = read();
    long l2 = read();
    long l3 = read();
    long l4 = read();
    long l5 = read();
    long l6 = read();
    long l7 = read();
    long l8 = read();
    if (l1 < 0L) {
      throw new EOFException();
    }
    if (l2 < 0L) {
      throw new EOFException();
    }
    if (l3 < 0L) {
      throw new EOFException();
    }
    if (l4 < 0L) {
      throw new EOFException();
    }
    if (l5 < 0L) {
      throw new EOFException();
    }
    if (l6 < 0L) {
      throw new EOFException();
    }
    if (l7 < 0L) {
      throw new EOFException();
    }
    if (l8 < 0L) {
      throw new EOFException();
    }
    return l1 | l2 << 8 | l3 << 16 | l4 << 24 | l5 << 32 | l6 << 40 | l7 << 48 | l8 << 56;
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
  
  public int readUnsignedShort()
    throws IOException
  {
    int i = read();
    int j = read();
    if (i < 0) {
      throw new EOFException();
    }
    if (j < 0) {
      throw new EOFException();
    }
    return i | j << 8;
  }
  
  public long readUnsignedInt()
    throws IOException
  {
    long l1 = read();
    long l2 = read();
    long l3 = read();
    long l4 = read();
    if (l1 < 0L) {
      throw new EOFException();
    }
    if (l2 < 0L) {
      throw new EOFException();
    }
    if (l3 < 0L) {
      throw new EOFException();
    }
    if (l4 < 0L) {
      throw new EOFException();
    }
    return l1 + (l2 << 8) | l3 << 16 | l4 << 24;
  }
  
  public void close()
    throws IOException
  {
    finish();
    if (this == root) {
      stream.close();
    }
    stream = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\RIFFReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */