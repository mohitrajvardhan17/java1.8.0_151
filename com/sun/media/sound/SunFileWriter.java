package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.AudioFileWriter;

abstract class SunFileWriter
  extends AudioFileWriter
{
  protected static final int bufferSize = 16384;
  protected static final int bisBufferSize = 4096;
  final AudioFileFormat.Type[] types;
  
  SunFileWriter(AudioFileFormat.Type[] paramArrayOfType)
  {
    types = paramArrayOfType;
  }
  
  public final AudioFileFormat.Type[] getAudioFileTypes()
  {
    AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[types.length];
    System.arraycopy(types, 0, arrayOfType, 0, types.length);
    return arrayOfType;
  }
  
  public abstract AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream);
  
  public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
    throws IOException;
  
  public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
    throws IOException;
  
  final int rllong(DataInputStream paramDataInputStream)
    throws IOException
  {
    int n = 0;
    n = paramDataInputStream.readInt();
    int i = (n & 0xFF) << 24;
    int j = (n & 0xFF00) << 8;
    int k = (n & 0xFF0000) >> 8;
    int m = (n & 0xFF000000) >>> 24;
    n = i | j | k | m;
    return n;
  }
  
  final int big2little(int paramInt)
  {
    int i = (paramInt & 0xFF) << 24;
    int j = (paramInt & 0xFF00) << 8;
    int k = (paramInt & 0xFF0000) >> 8;
    int m = (paramInt & 0xFF000000) >>> 24;
    paramInt = i | j | k | m;
    return paramInt;
  }
  
  final short rlshort(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = 0;
    i = paramDataInputStream.readShort();
    int j = (short)((i & 0xFF) << 8);
    int k = (short)((i & 0xFF00) >>> 8);
    i = (short)(j | k);
    return i;
  }
  
  final short big2littleShort(short paramShort)
  {
    int i = (short)((paramShort & 0xFF) << 8);
    int j = (short)((paramShort & 0xFF00) >>> 8);
    paramShort = (short)(i | j);
    return paramShort;
  }
  
  final class NoCloseInputStream
    extends InputStream
  {
    private final InputStream in;
    
    NoCloseInputStream(InputStream paramInputStream)
    {
      in = paramInputStream;
    }
    
    public int read()
      throws IOException
    {
      return in.read();
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return in.read(paramArrayOfByte);
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      return in.read(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      return in.skip(paramLong);
    }
    
    public int available()
      throws IOException
    {
      return in.available();
    }
    
    public void close()
      throws IOException
    {}
    
    public void mark(int paramInt)
    {
      in.mark(paramInt);
    }
    
    public void reset()
      throws IOException
    {
      in.reset();
    }
    
    public boolean markSupported()
    {
      return in.markSupported();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SunFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */