package com.sun.media.sound;

import java.io.InputStream;
import java.util.Arrays;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class DLSSample
  extends SoundbankResource
{
  byte[] guid = null;
  DLSInfo info = new DLSInfo();
  DLSSampleOptions sampleoptions;
  ModelByteBuffer data;
  AudioFormat format;
  
  public DLSSample(Soundbank paramSoundbank)
  {
    super(paramSoundbank, null, AudioInputStream.class);
  }
  
  public DLSSample()
  {
    super(null, null, AudioInputStream.class);
  }
  
  public DLSInfo getInfo()
  {
    return info;
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
  
  public AudioFormat getFormat()
  {
    return format;
  }
  
  public void setFormat(AudioFormat paramAudioFormat)
  {
    format = paramAudioFormat;
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
  
  public String getName()
  {
    return info.name;
  }
  
  public void setName(String paramString)
  {
    info.name = paramString;
  }
  
  public DLSSampleOptions getSampleoptions()
  {
    return sampleoptions;
  }
  
  public void setSampleoptions(DLSSampleOptions paramDLSSampleOptions)
  {
    sampleoptions = paramDLSSampleOptions;
  }
  
  public String toString()
  {
    return "Sample: " + info.name;
  }
  
  public byte[] getGuid()
  {
    return guid == null ? null : Arrays.copyOf(guid, guid.length);
  }
  
  public void setGuid(byte[] paramArrayOfByte)
  {
    guid = (paramArrayOfByte == null ? null : Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DLSSample.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */