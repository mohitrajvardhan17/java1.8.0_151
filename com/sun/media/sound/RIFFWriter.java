package com.sun.media.sound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public final class RIFFWriter
  extends OutputStream
{
  private int chunktype = 0;
  private RandomAccessWriter raf;
  private final long chunksizepointer;
  private final long startpointer;
  private RIFFWriter childchunk = null;
  private boolean open = true;
  private boolean writeoverride = false;
  
  public RIFFWriter(String paramString1, String paramString2)
    throws IOException
  {
    this(new RandomAccessFileWriter(paramString1), paramString2, 0);
  }
  
  public RIFFWriter(File paramFile, String paramString)
    throws IOException
  {
    this(new RandomAccessFileWriter(paramFile), paramString, 0);
  }
  
  public RIFFWriter(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    this(new RandomAccessByteWriter(paramOutputStream), paramString, 0);
  }
  
  private RIFFWriter(RandomAccessWriter paramRandomAccessWriter, String paramString, int paramInt)
    throws IOException
  {
    if ((paramInt == 0) && (paramRandomAccessWriter.length() != 0L)) {
      paramRandomAccessWriter.setLength(0L);
    }
    raf = paramRandomAccessWriter;
    if (paramRandomAccessWriter.getPointer() % 2L != 0L) {
      paramRandomAccessWriter.write(0);
    }
    if (paramInt == 0) {
      paramRandomAccessWriter.write("RIFF".getBytes("ascii"));
    } else if (paramInt == 1) {
      paramRandomAccessWriter.write("LIST".getBytes("ascii"));
    } else {
      paramRandomAccessWriter.write((paramString + "    ").substring(0, 4).getBytes("ascii"));
    }
    chunksizepointer = paramRandomAccessWriter.getPointer();
    chunktype = 2;
    writeUnsignedInt(0L);
    chunktype = paramInt;
    startpointer = paramRandomAccessWriter.getPointer();
    if (paramInt != 2) {
      paramRandomAccessWriter.write((paramString + "    ").substring(0, 4).getBytes("ascii"));
    }
  }
  
  public void seek(long paramLong)
    throws IOException
  {
    raf.seek(paramLong);
  }
  
  public long getFilePointer()
    throws IOException
  {
    return raf.getPointer();
  }
  
  public void setWriteOverride(boolean paramBoolean)
  {
    writeoverride = paramBoolean;
  }
  
  public boolean getWriteOverride()
  {
    return writeoverride;
  }
  
  public void close()
    throws IOException
  {
    if (!open) {
      return;
    }
    if (childchunk != null)
    {
      childchunk.close();
      childchunk = null;
    }
    int i = chunktype;
    long l = raf.getPointer();
    raf.seek(chunksizepointer);
    chunktype = 2;
    writeUnsignedInt(l - startpointer);
    if (i == 0) {
      raf.close();
    } else {
      raf.seek(l);
    }
    open = false;
    raf = null;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (!writeoverride)
    {
      if (chunktype != 2) {
        throw new IllegalArgumentException("Only chunks can write bytes!");
      }
      if (childchunk != null)
      {
        childchunk.close();
        childchunk = null;
      }
    }
    raf.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (!writeoverride)
    {
      if (chunktype != 2) {
        throw new IllegalArgumentException("Only chunks can write bytes!");
      }
      if (childchunk != null)
      {
        childchunk.close();
        childchunk = null;
      }
    }
    raf.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public RIFFWriter writeList(String paramString)
    throws IOException
  {
    if (chunktype == 2) {
      throw new IllegalArgumentException("Only LIST and RIFF can write lists!");
    }
    if (childchunk != null)
    {
      childchunk.close();
      childchunk = null;
    }
    childchunk = new RIFFWriter(raf, paramString, 1);
    return childchunk;
  }
  
  public RIFFWriter writeChunk(String paramString)
    throws IOException
  {
    if (chunktype == 2) {
      throw new IllegalArgumentException("Only LIST and RIFF can write chunks!");
    }
    if (childchunk != null)
    {
      childchunk.close();
      childchunk = null;
    }
    childchunk = new RIFFWriter(raf, paramString, 2);
    return childchunk;
  }
  
  public void writeString(String paramString)
    throws IOException
  {
    byte[] arrayOfByte = paramString.getBytes();
    write(arrayOfByte);
  }
  
  public void writeString(String paramString, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = paramString.getBytes();
    if (arrayOfByte.length > paramInt)
    {
      write(arrayOfByte, 0, paramInt);
    }
    else
    {
      write(arrayOfByte);
      for (int i = arrayOfByte.length; i < paramInt; i++) {
        write(0);
      }
    }
  }
  
  public void writeByte(int paramInt)
    throws IOException
  {
    write(paramInt);
  }
  
  public void writeShort(short paramShort)
    throws IOException
  {
    write(paramShort >>> 0 & 0xFF);
    write(paramShort >>> 8 & 0xFF);
  }
  
  public void writeInt(int paramInt)
    throws IOException
  {
    write(paramInt >>> 0 & 0xFF);
    write(paramInt >>> 8 & 0xFF);
    write(paramInt >>> 16 & 0xFF);
    write(paramInt >>> 24 & 0xFF);
  }
  
  public void writeLong(long paramLong)
    throws IOException
  {
    write((int)(paramLong >>> 0) & 0xFF);
    write((int)(paramLong >>> 8) & 0xFF);
    write((int)(paramLong >>> 16) & 0xFF);
    write((int)(paramLong >>> 24) & 0xFF);
    write((int)(paramLong >>> 32) & 0xFF);
    write((int)(paramLong >>> 40) & 0xFF);
    write((int)(paramLong >>> 48) & 0xFF);
    write((int)(paramLong >>> 56) & 0xFF);
  }
  
  public void writeUnsignedByte(int paramInt)
    throws IOException
  {
    writeByte((byte)paramInt);
  }
  
  public void writeUnsignedShort(int paramInt)
    throws IOException
  {
    writeShort((short)paramInt);
  }
  
  public void writeUnsignedInt(long paramLong)
    throws IOException
  {
    writeInt((int)paramLong);
  }
  
  private static class RandomAccessByteWriter
    implements RIFFWriter.RandomAccessWriter
  {
    byte[] buff = new byte[32];
    int length = 0;
    int pos = 0;
    byte[] s;
    final OutputStream stream;
    
    RandomAccessByteWriter(OutputStream paramOutputStream)
    {
      stream = paramOutputStream;
    }
    
    public void seek(long paramLong)
      throws IOException
    {
      pos = ((int)paramLong);
    }
    
    public long getPointer()
      throws IOException
    {
      return pos;
    }
    
    public void close()
      throws IOException
    {
      stream.write(buff, 0, length);
      stream.close();
    }
    
    public void write(int paramInt)
      throws IOException
    {
      if (s == null) {
        s = new byte[1];
      }
      s[0] = ((byte)paramInt);
      write(s, 0, 1);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = pos + paramInt2;
      if (i > length) {
        setLength(i);
      }
      int j = paramInt1 + paramInt2;
      for (int k = paramInt1; k < j; k++) {
        buff[(pos++)] = paramArrayOfByte[k];
      }
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      write(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public long length()
      throws IOException
    {
      return length;
    }
    
    public void setLength(long paramLong)
      throws IOException
    {
      length = ((int)paramLong);
      if (length > buff.length)
      {
        int i = Math.max(buff.length << 1, length);
        byte[] arrayOfByte = new byte[i];
        System.arraycopy(buff, 0, arrayOfByte, 0, buff.length);
        buff = arrayOfByte;
      }
    }
  }
  
  private static class RandomAccessFileWriter
    implements RIFFWriter.RandomAccessWriter
  {
    RandomAccessFile raf;
    
    RandomAccessFileWriter(File paramFile)
      throws FileNotFoundException
    {
      raf = new RandomAccessFile(paramFile, "rw");
    }
    
    RandomAccessFileWriter(String paramString)
      throws FileNotFoundException
    {
      raf = new RandomAccessFile(paramString, "rw");
    }
    
    public void seek(long paramLong)
      throws IOException
    {
      raf.seek(paramLong);
    }
    
    public long getPointer()
      throws IOException
    {
      return raf.getFilePointer();
    }
    
    public void close()
      throws IOException
    {
      raf.close();
    }
    
    public void write(int paramInt)
      throws IOException
    {
      raf.write(paramInt);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      raf.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      raf.write(paramArrayOfByte);
    }
    
    public long length()
      throws IOException
    {
      return raf.length();
    }
    
    public void setLength(long paramLong)
      throws IOException
    {
      raf.setLength(paramLong);
    }
  }
  
  private static abstract interface RandomAccessWriter
  {
    public abstract void seek(long paramLong)
      throws IOException;
    
    public abstract long getPointer()
      throws IOException;
    
    public abstract void close()
      throws IOException;
    
    public abstract void write(int paramInt)
      throws IOException;
    
    public abstract void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException;
    
    public abstract void write(byte[] paramArrayOfByte)
      throws IOException;
    
    public abstract long length()
      throws IOException;
    
    public abstract void setLength(long paramLong)
      throws IOException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\RIFFWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */