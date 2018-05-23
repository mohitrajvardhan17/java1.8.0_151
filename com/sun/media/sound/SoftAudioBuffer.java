package com.sun.media.sound;

import java.util.Arrays;
import javax.sound.sampled.AudioFormat;

public final class SoftAudioBuffer
{
  private int size;
  private float[] buffer;
  private boolean empty = true;
  private AudioFormat format;
  private AudioFloatConverter converter;
  private byte[] converter_buffer;
  
  public SoftAudioBuffer(int paramInt, AudioFormat paramAudioFormat)
  {
    size = paramInt;
    format = paramAudioFormat;
    converter = AudioFloatConverter.getConverter(paramAudioFormat);
  }
  
  public void swap(SoftAudioBuffer paramSoftAudioBuffer)
  {
    int i = size;
    float[] arrayOfFloat = buffer;
    boolean bool = empty;
    AudioFormat localAudioFormat = format;
    AudioFloatConverter localAudioFloatConverter = converter;
    byte[] arrayOfByte = converter_buffer;
    size = size;
    buffer = buffer;
    empty = empty;
    format = format;
    converter = converter;
    converter_buffer = converter_buffer;
    size = i;
    buffer = arrayOfFloat;
    empty = bool;
    format = localAudioFormat;
    converter = localAudioFloatConverter;
    converter_buffer = arrayOfByte;
  }
  
  public AudioFormat getFormat()
  {
    return format;
  }
  
  public int getSize()
  {
    return size;
  }
  
  public void clear()
  {
    if (!empty)
    {
      Arrays.fill(buffer, 0.0F);
      empty = true;
    }
  }
  
  public boolean isSilent()
  {
    return empty;
  }
  
  public float[] array()
  {
    empty = false;
    if (buffer == null) {
      buffer = new float[size];
    }
    return buffer;
  }
  
  public void get(byte[] paramArrayOfByte, int paramInt)
  {
    int i = format.getFrameSize() / format.getChannels();
    int j = size * i;
    if ((converter_buffer == null) || (converter_buffer.length < j)) {
      converter_buffer = new byte[j];
    }
    if (format.getChannels() == 1)
    {
      converter.toByteArray(array(), size, paramArrayOfByte);
    }
    else
    {
      converter.toByteArray(array(), size, converter_buffer);
      if (paramInt >= format.getChannels()) {
        return;
      }
      int k = format.getChannels() * i;
      int m = i;
      for (int n = 0; n < i; n++)
      {
        int i1 = n;
        int i2 = paramInt * i + n;
        for (int i3 = 0; i3 < size; i3++)
        {
          paramArrayOfByte[i2] = converter_buffer[i1];
          i2 += k;
          i1 += m;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftAudioBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */