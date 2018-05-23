package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class Toolkit
{
  private Toolkit() {}
  
  static void getUnsigned8(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
    {
      int tmp11_10 = i;
      paramArrayOfByte[tmp11_10] = ((byte)(paramArrayOfByte[tmp11_10] + 128));
    }
  }
  
  static void getByteSwapped(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (int j = paramInt1; j < paramInt1 + paramInt2; j += 2)
    {
      int i = paramArrayOfByte[j];
      paramArrayOfByte[j] = paramArrayOfByte[(j + 1)];
      paramArrayOfByte[(j + 1)] = i;
    }
  }
  
  static float linearToDB(float paramFloat)
  {
    float f = (float)(Math.log(paramFloat == 0.0D ? 1.0E-4D : paramFloat) / Math.log(10.0D) * 20.0D);
    return f;
  }
  
  static float dBToLinear(float paramFloat)
  {
    float f = (float)Math.pow(10.0D, paramFloat / 20.0D);
    return f;
  }
  
  static long align(long paramLong, int paramInt)
  {
    if (paramInt <= 1) {
      return paramLong;
    }
    return paramLong - paramLong % paramInt;
  }
  
  static int align(int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 1) {
      return paramInt1;
    }
    return paramInt1 - paramInt1 % paramInt2;
  }
  
  static long millis2bytes(AudioFormat paramAudioFormat, long paramLong)
  {
    long l = ((float)paramLong * paramAudioFormat.getFrameRate() / 1000.0F * paramAudioFormat.getFrameSize());
    return align(l, paramAudioFormat.getFrameSize());
  }
  
  static long bytes2millis(AudioFormat paramAudioFormat, long paramLong)
  {
    return ((float)paramLong / paramAudioFormat.getFrameRate() * 1000.0F / paramAudioFormat.getFrameSize());
  }
  
  static long micros2bytes(AudioFormat paramAudioFormat, long paramLong)
  {
    long l = ((float)paramLong * paramAudioFormat.getFrameRate() / 1000000.0F * paramAudioFormat.getFrameSize());
    return align(l, paramAudioFormat.getFrameSize());
  }
  
  static long bytes2micros(AudioFormat paramAudioFormat, long paramLong)
  {
    return ((float)paramLong / paramAudioFormat.getFrameRate() * 1000000.0F / paramAudioFormat.getFrameSize());
  }
  
  static long micros2frames(AudioFormat paramAudioFormat, long paramLong)
  {
    return ((float)paramLong * paramAudioFormat.getFrameRate() / 1000000.0F);
  }
  
  static long frames2micros(AudioFormat paramAudioFormat, long paramLong)
  {
    return (paramLong / paramAudioFormat.getFrameRate() * 1000000.0D);
  }
  
  static void isFullySpecifiedAudioFormat(AudioFormat paramAudioFormat)
  {
    if ((!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ULAW)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW))) {
      return;
    }
    if (paramAudioFormat.getFrameRate() <= 0.0F) {
      throw new IllegalArgumentException("invalid frame rate: " + (paramAudioFormat.getFrameRate() == -1.0F ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getFrameRate())));
    }
    if (paramAudioFormat.getSampleRate() <= 0.0F) {
      throw new IllegalArgumentException("invalid sample rate: " + (paramAudioFormat.getSampleRate() == -1.0F ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getSampleRate())));
    }
    if (paramAudioFormat.getSampleSizeInBits() <= 0) {
      throw new IllegalArgumentException("invalid sample size in bits: " + (paramAudioFormat.getSampleSizeInBits() == -1 ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getSampleSizeInBits())));
    }
    if (paramAudioFormat.getFrameSize() <= 0) {
      throw new IllegalArgumentException("invalid frame size: " + (paramAudioFormat.getFrameSize() == -1 ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getFrameSize())));
    }
    if (paramAudioFormat.getChannels() <= 0) {
      throw new IllegalArgumentException("invalid number of channels: " + (paramAudioFormat.getChannels() == -1 ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getChannels())));
    }
  }
  
  static boolean isFullySpecifiedPCMFormat(AudioFormat paramAudioFormat)
  {
    if ((!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))) {
      return false;
    }
    return (paramAudioFormat.getFrameRate() > 0.0F) && (paramAudioFormat.getSampleRate() > 0.0F) && (paramAudioFormat.getSampleSizeInBits() > 0) && (paramAudioFormat.getFrameSize() > 0) && (paramAudioFormat.getChannels() > 0);
  }
  
  public static AudioInputStream getPCMConvertedAudioInputStream(AudioInputStream paramAudioInputStream)
  {
    AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
    if ((!localAudioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) && (!localAudioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))) {
      try
      {
        AudioFormat localAudioFormat2 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat1.getSampleRate(), 16, localAudioFormat1.getChannels(), localAudioFormat1.getChannels() * 2, localAudioFormat1.getSampleRate(), Platform.isBigEndian());
        paramAudioInputStream = AudioSystem.getAudioInputStream(localAudioFormat2, paramAudioInputStream);
      }
      catch (Exception localException)
      {
        paramAudioInputStream = null;
      }
    }
    return paramAudioInputStream;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\Toolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */