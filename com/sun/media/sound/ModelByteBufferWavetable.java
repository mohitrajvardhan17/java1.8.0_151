package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class ModelByteBufferWavetable
  implements ModelWavetable
{
  private float loopStart = -1.0F;
  private float loopLength = -1.0F;
  private final ModelByteBuffer buffer;
  private ModelByteBuffer buffer8 = null;
  private AudioFormat format = null;
  private float pitchcorrection = 0.0F;
  private float attenuation = 0.0F;
  private int loopType = 0;
  
  public ModelByteBufferWavetable(ModelByteBuffer paramModelByteBuffer)
  {
    buffer = paramModelByteBuffer;
  }
  
  public ModelByteBufferWavetable(ModelByteBuffer paramModelByteBuffer, float paramFloat)
  {
    buffer = paramModelByteBuffer;
    pitchcorrection = paramFloat;
  }
  
  public ModelByteBufferWavetable(ModelByteBuffer paramModelByteBuffer, AudioFormat paramAudioFormat)
  {
    format = paramAudioFormat;
    buffer = paramModelByteBuffer;
  }
  
  public ModelByteBufferWavetable(ModelByteBuffer paramModelByteBuffer, AudioFormat paramAudioFormat, float paramFloat)
  {
    format = paramAudioFormat;
    buffer = paramModelByteBuffer;
    pitchcorrection = paramFloat;
  }
  
  public void set8BitExtensionBuffer(ModelByteBuffer paramModelByteBuffer)
  {
    buffer8 = paramModelByteBuffer;
  }
  
  public ModelByteBuffer get8BitExtensionBuffer()
  {
    return buffer8;
  }
  
  public ModelByteBuffer getBuffer()
  {
    return buffer;
  }
  
  public AudioFormat getFormat()
  {
    if (format == null)
    {
      if (buffer == null) {
        return null;
      }
      InputStream localInputStream = buffer.getInputStream();
      AudioFormat localAudioFormat = null;
      try
      {
        localAudioFormat = AudioSystem.getAudioFileFormat(localInputStream).getFormat();
      }
      catch (Exception localException) {}
      try
      {
        localInputStream.close();
      }
      catch (IOException localIOException) {}
      return localAudioFormat;
    }
    return format;
  }
  
  public AudioFloatInputStream openStream()
  {
    if (buffer == null) {
      return null;
    }
    Object localObject1;
    Object localObject2;
    if (format == null)
    {
      localObject1 = buffer.getInputStream();
      localObject2 = null;
      try
      {
        localObject2 = AudioSystem.getAudioInputStream((InputStream)localObject1);
      }
      catch (Exception localException)
      {
        return null;
      }
      return AudioFloatInputStream.getInputStream((AudioInputStream)localObject2);
    }
    if (buffer.array() == null) {
      return AudioFloatInputStream.getInputStream(new AudioInputStream(buffer.getInputStream(), format, buffer.capacity() / format.getFrameSize()));
    }
    if ((buffer8 != null) && ((format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) || (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))))
    {
      localObject1 = new Buffer8PlusInputStream();
      localObject2 = new AudioFormat(format.getEncoding(), format.getSampleRate(), format.getSampleSizeInBits() + 8, format.getChannels(), format.getFrameSize() + 1 * format.getChannels(), format.getFrameRate(), format.isBigEndian());
      AudioInputStream localAudioInputStream = new AudioInputStream((InputStream)localObject1, (AudioFormat)localObject2, buffer.capacity() / format.getFrameSize());
      return AudioFloatInputStream.getInputStream(localAudioInputStream);
    }
    return AudioFloatInputStream.getInputStream(format, buffer.array(), (int)buffer.arrayOffset(), (int)buffer.capacity());
  }
  
  public int getChannels()
  {
    return getFormat().getChannels();
  }
  
  public ModelOscillatorStream open(float paramFloat)
  {
    return null;
  }
  
  public float getAttenuation()
  {
    return attenuation;
  }
  
  public void setAttenuation(float paramFloat)
  {
    attenuation = paramFloat;
  }
  
  public float getLoopLength()
  {
    return loopLength;
  }
  
  public void setLoopLength(float paramFloat)
  {
    loopLength = paramFloat;
  }
  
  public float getLoopStart()
  {
    return loopStart;
  }
  
  public void setLoopStart(float paramFloat)
  {
    loopStart = paramFloat;
  }
  
  public void setLoopType(int paramInt)
  {
    loopType = paramInt;
  }
  
  public int getLoopType()
  {
    return loopType;
  }
  
  public float getPitchcorrection()
  {
    return pitchcorrection;
  }
  
  public void setPitchcorrection(float paramFloat)
  {
    pitchcorrection = paramFloat;
  }
  
  private class Buffer8PlusInputStream
    extends InputStream
  {
    private final boolean bigendian = format.isBigEndian();
    private final int framesize_pc = format.getFrameSize() / format.getChannels();
    int pos = 0;
    int pos2 = 0;
    int markpos = 0;
    int markpos2 = 0;
    
    Buffer8PlusInputStream() {}
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = available();
      if (i <= 0) {
        return -1;
      }
      if (paramInt2 > i) {
        paramInt2 = i;
      }
      byte[] arrayOfByte1 = buffer.array();
      byte[] arrayOfByte2 = buffer8.array();
      pos = ((int)(pos + buffer.arrayOffset()));
      pos2 = ((int)(pos2 + buffer8.arrayOffset()));
      int j;
      if (bigendian)
      {
        j = 0;
        while (j < paramInt2)
        {
          System.arraycopy(arrayOfByte1, pos, paramArrayOfByte, j, framesize_pc);
          System.arraycopy(arrayOfByte2, pos2, paramArrayOfByte, j + framesize_pc, 1);
          pos += framesize_pc;
          pos2 += 1;
          j += framesize_pc + 1;
        }
      }
      else
      {
        j = 0;
        while (j < paramInt2)
        {
          System.arraycopy(arrayOfByte2, pos2, paramArrayOfByte, j, 1);
          System.arraycopy(arrayOfByte1, pos, paramArrayOfByte, j + 1, framesize_pc);
          pos += framesize_pc;
          pos2 += 1;
          j += framesize_pc + 1;
        }
      }
      pos = ((int)(pos - buffer.arrayOffset()));
      pos2 = ((int)(pos2 - buffer8.arrayOffset()));
      return paramInt2;
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      int i = available();
      if (i <= 0) {
        return -1L;
      }
      if (paramLong > i) {
        paramLong = i;
      }
      pos = ((int)(pos + paramLong / (framesize_pc + 1) * framesize_pc));
      pos2 = ((int)(pos2 + paramLong / (framesize_pc + 1)));
      return super.skip(paramLong);
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      int i = read(arrayOfByte, 0, 1);
      if (i == -1) {
        return -1;
      }
      return 0;
    }
    
    public boolean markSupported()
    {
      return true;
    }
    
    public int available()
      throws IOException
    {
      return (int)buffer.capacity() + (int)buffer8.capacity() - pos - pos2;
    }
    
    public synchronized void mark(int paramInt)
    {
      markpos = pos;
      markpos2 = pos2;
    }
    
    public synchronized void reset()
      throws IOException
    {
      pos = markpos;
      pos2 = markpos2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelByteBufferWavetable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */