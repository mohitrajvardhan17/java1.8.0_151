package com.sun.media.sound;

import java.io.InputStream;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class SF2Sample
  extends SoundbankResource
{
  String name = "";
  long startLoop = 0L;
  long endLoop = 0L;
  long sampleRate = 44100L;
  int originalPitch = 60;
  byte pitchCorrection = 0;
  int sampleLink = 0;
  int sampleType = 0;
  ModelByteBuffer data;
  ModelByteBuffer data24;
  
  public SF2Sample(Soundbank paramSoundbank)
  {
    super(paramSoundbank, null, AudioInputStream.class);
  }
  
  public SF2Sample()
  {
    super(null, null, AudioInputStream.class);
  }
  
  public Object getData()
  {
    AudioFormat localAudioFormat = getFormat();
    InputStream localInputStream = data.getInputStream();
    if (localInputStream == null) {
      return null;
    }
    return new AudioInputStream(localInputStream, localAudioFormat, data.capacity());
  }
  
  public ModelByteBuffer getDataBuffer()
  {
    return data;
  }
  
  public ModelByteBuffer getData24Buffer()
  {
    return data24;
  }
  
  public AudioFormat getFormat()
  {
    return new AudioFormat((float)sampleRate, 16, 1, true, false);
  }
  
  public void setData(ModelByteBuffer paramModelByteBuffer)
  {
    data = paramModelByteBuffer;
  }
  
  public void setData(byte[] paramArrayOfByte)
  {
    data = new ModelByteBuffer(paramArrayOfByte);
  }
  
  public void setData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    data = new ModelByteBuffer(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void setData24(ModelByteBuffer paramModelByteBuffer)
  {
    data24 = paramModelByteBuffer;
  }
  
  public void setData24(byte[] paramArrayOfByte)
  {
    data24 = new ModelByteBuffer(paramArrayOfByte);
  }
  
  public void setData24(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    data24 = new ModelByteBuffer(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public long getEndLoop()
  {
    return endLoop;
  }
  
  public void setEndLoop(long paramLong)
  {
    endLoop = paramLong;
  }
  
  public int getOriginalPitch()
  {
    return originalPitch;
  }
  
  public void setOriginalPitch(int paramInt)
  {
    originalPitch = paramInt;
  }
  
  public byte getPitchCorrection()
  {
    return pitchCorrection;
  }
  
  public void setPitchCorrection(byte paramByte)
  {
    pitchCorrection = paramByte;
  }
  
  public int getSampleLink()
  {
    return sampleLink;
  }
  
  public void setSampleLink(int paramInt)
  {
    sampleLink = paramInt;
  }
  
  public long getSampleRate()
  {
    return sampleRate;
  }
  
  public void setSampleRate(long paramLong)
  {
    sampleRate = paramLong;
  }
  
  public int getSampleType()
  {
    return sampleType;
  }
  
  public void setSampleType(int paramInt)
  {
    sampleType = paramInt;
  }
  
  public long getStartLoop()
  {
    return startLoop;
  }
  
  public void setStartLoop(long paramLong)
  {
    startLoop = paramLong;
  }
  
  public String toString()
  {
    return "Sample: " + name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SF2Sample.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */