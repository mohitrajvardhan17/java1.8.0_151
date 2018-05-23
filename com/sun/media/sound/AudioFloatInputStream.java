package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class AudioFloatInputStream
{
  public AudioFloatInputStream() {}
  
  public static AudioFloatInputStream getInputStream(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(paramURL));
  }
  
  public static AudioFloatInputStream getInputStream(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(paramFile));
  }
  
  public static AudioFloatInputStream getInputStream(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(paramInputStream));
  }
  
  public static AudioFloatInputStream getInputStream(AudioInputStream paramAudioInputStream)
  {
    return new DirectAudioFloatInputStream(paramAudioInputStream);
  }
  
  public static AudioFloatInputStream getInputStream(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    AudioFloatConverter localAudioFloatConverter = AudioFloatConverter.getConverter(paramAudioFormat);
    if (localAudioFloatConverter != null) {
      return new BytaArrayAudioFloatInputStream(localAudioFloatConverter, paramArrayOfByte, paramInt1, paramInt2);
    }
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2);
    long l = paramAudioFormat.getFrameSize() == -1 ? -1L : paramInt2 / paramAudioFormat.getFrameSize();
    AudioInputStream localAudioInputStream = new AudioInputStream(localByteArrayInputStream, paramAudioFormat, l);
    return getInputStream(localAudioInputStream);
  }
  
  public abstract AudioFormat getFormat();
  
  public abstract long getFrameLength();
  
  public abstract int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
    throws IOException;
  
  public final int read(float[] paramArrayOfFloat)
    throws IOException
  {
    return read(paramArrayOfFloat, 0, paramArrayOfFloat.length);
  }
  
  public final float read()
    throws IOException
  {
    float[] arrayOfFloat = new float[1];
    int i = read(arrayOfFloat, 0, 1);
    if ((i == -1) || (i == 0)) {
      return 0.0F;
    }
    return arrayOfFloat[0];
  }
  
  public abstract long skip(long paramLong)
    throws IOException;
  
  public abstract int available()
    throws IOException;
  
  public abstract void close()
    throws IOException;
  
  public abstract void mark(int paramInt);
  
  public abstract boolean markSupported();
  
  public abstract void reset()
    throws IOException;
  
  private static class BytaArrayAudioFloatInputStream
    extends AudioFloatInputStream
  {
    private int pos = 0;
    private int markpos = 0;
    private final AudioFloatConverter converter;
    private final AudioFormat format;
    private final byte[] buffer;
    private final int buffer_offset;
    private final int buffer_len;
    private final int framesize_pc;
    
    BytaArrayAudioFloatInputStream(AudioFloatConverter paramAudioFloatConverter, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      converter = paramAudioFloatConverter;
      format = paramAudioFloatConverter.getFormat();
      buffer = paramArrayOfByte;
      buffer_offset = paramInt1;
      framesize_pc = (format.getFrameSize() / format.getChannels());
      buffer_len = (paramInt2 / framesize_pc);
    }
    
    public AudioFormat getFormat()
    {
      return format;
    }
    
    public long getFrameLength()
    {
      return buffer_len;
    }
    
    public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      if (paramArrayOfFloat == null) {
        throw new NullPointerException();
      }
      if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfFloat.length - paramInt1)) {
        throw new IndexOutOfBoundsException();
      }
      if (pos >= buffer_len) {
        return -1;
      }
      if (paramInt2 == 0) {
        return 0;
      }
      if (pos + paramInt2 > buffer_len) {
        paramInt2 = buffer_len - pos;
      }
      converter.toFloatArray(buffer, buffer_offset + pos * framesize_pc, paramArrayOfFloat, paramInt1, paramInt2);
      pos += paramInt2;
      return paramInt2;
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (pos >= buffer_len) {
        return -1L;
      }
      if (paramLong <= 0L) {
        return 0L;
      }
      if (pos + paramLong > buffer_len) {
        paramLong = buffer_len - pos;
      }
      pos = ((int)(pos + paramLong));
      return paramLong;
    }
    
    public int available()
      throws IOException
    {
      return buffer_len - pos;
    }
    
    public void close()
      throws IOException
    {}
    
    public void mark(int paramInt)
    {
      markpos = pos;
    }
    
    public boolean markSupported()
    {
      return true;
    }
    
    public void reset()
      throws IOException
    {
      pos = markpos;
    }
  }
  
  private static class DirectAudioFloatInputStream
    extends AudioFloatInputStream
  {
    private final AudioInputStream stream;
    private AudioFloatConverter converter;
    private final int framesize_pc;
    private byte[] buffer;
    
    DirectAudioFloatInputStream(AudioInputStream paramAudioInputStream)
    {
      converter = AudioFloatConverter.getConverter(paramAudioInputStream.getFormat());
      if (converter == null)
      {
        AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
        AudioFormat[] arrayOfAudioFormat = AudioSystem.getTargetFormats(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat1);
        AudioFormat localAudioFormat2;
        if (arrayOfAudioFormat.length != 0)
        {
          localAudioFormat2 = arrayOfAudioFormat[0];
        }
        else
        {
          float f1 = localAudioFormat1.getSampleRate();
          int i = localAudioFormat1.getSampleSizeInBits();
          int j = localAudioFormat1.getFrameSize();
          float f2 = localAudioFormat1.getFrameRate();
          i = 16;
          j = localAudioFormat1.getChannels() * (i / 8);
          f2 = f1;
          localAudioFormat2 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, f1, i, localAudioFormat1.getChannels(), j, f2, false);
        }
        paramAudioInputStream = AudioSystem.getAudioInputStream(localAudioFormat2, paramAudioInputStream);
        converter = AudioFloatConverter.getConverter(paramAudioInputStream.getFormat());
      }
      framesize_pc = (paramAudioInputStream.getFormat().getFrameSize() / paramAudioInputStream.getFormat().getChannels());
      stream = paramAudioInputStream;
    }
    
    public AudioFormat getFormat()
    {
      return stream.getFormat();
    }
    
    public long getFrameLength()
    {
      return stream.getFrameLength();
    }
    
    public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt2 * framesize_pc;
      if ((buffer == null) || (buffer.length < i)) {
        buffer = new byte[i];
      }
      int j = stream.read(buffer, 0, i);
      if (j == -1) {
        return -1;
      }
      converter.toFloatArray(buffer, paramArrayOfFloat, paramInt1, j / framesize_pc);
      return j / framesize_pc;
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      long l1 = paramLong * framesize_pc;
      long l2 = stream.skip(l1);
      if (l2 == -1L) {
        return -1L;
      }
      return l2 / framesize_pc;
    }
    
    public int available()
      throws IOException
    {
      return stream.available() / framesize_pc;
    }
    
    public void close()
      throws IOException
    {
      stream.close();
    }
    
    public void mark(int paramInt)
    {
      stream.mark(paramInt * framesize_pc);
    }
    
    public boolean markSupported()
    {
      return stream.markSupported();
    }
    
    public void reset()
      throws IOException
    {
      stream.reset();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AudioFloatInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */