package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.spi.MixerProvider;

public final class DirectAudioDeviceProvider
  extends MixerProvider
{
  private static DirectAudioDeviceInfo[] infos;
  private static DirectAudioDevice[] devices;
  
  public DirectAudioDeviceProvider()
  {
    synchronized (DirectAudioDeviceProvider.class)
    {
      if (Platform.isDirectAudioEnabled())
      {
        init();
      }
      else
      {
        infos = new DirectAudioDeviceInfo[0];
        devices = new DirectAudioDevice[0];
      }
    }
  }
  
  private static void init()
  {
    int i = nGetNumDevices();
    if ((infos == null) || (infos.length != i))
    {
      infos = new DirectAudioDeviceInfo[i];
      devices = new DirectAudioDevice[i];
      for (int j = 0; j < infos.length; j++) {
        infos[j] = nNewDirectAudioDeviceInfo(j);
      }
    }
  }
  
  public Mixer.Info[] getMixerInfo()
  {
    synchronized (DirectAudioDeviceProvider.class)
    {
      Mixer.Info[] arrayOfInfo = new Mixer.Info[infos.length];
      System.arraycopy(infos, 0, arrayOfInfo, 0, infos.length);
      return arrayOfInfo;
    }
  }
  
  public Mixer getMixer(Mixer.Info paramInfo)
  {
    synchronized (DirectAudioDeviceProvider.class)
    {
      if (paramInfo == null) {
        for (i = 0; i < infos.length; i++)
        {
          Mixer localMixer = getDevice(infos[i]);
          if (localMixer.getSourceLineInfo().length > 0) {
            return localMixer;
          }
        }
      }
      for (int i = 0; i < infos.length; i++) {
        if (infos[i].equals(paramInfo)) {
          return getDevice(infos[i]);
        }
      }
    }
    throw new IllegalArgumentException("Mixer " + paramInfo.toString() + " not supported by this provider.");
  }
  
  private static Mixer getDevice(DirectAudioDeviceInfo paramDirectAudioDeviceInfo)
  {
    int i = paramDirectAudioDeviceInfo.getIndex();
    if (devices[i] == null) {
      devices[i] = new DirectAudioDevice(paramDirectAudioDeviceInfo);
    }
    return devices[i];
  }
  
  private static native int nGetNumDevices();
  
  private static native DirectAudioDeviceInfo nNewDirectAudioDeviceInfo(int paramInt);
  
  static {}
  
  static final class DirectAudioDeviceInfo
    extends Mixer.Info
  {
    private final int index;
    private final int maxSimulLines;
    private final int deviceID;
    
    private DirectAudioDeviceInfo(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, String paramString4)
    {
      super(paramString2, "Direct Audio Device: " + paramString3, paramString4);
      index = paramInt1;
      maxSimulLines = paramInt3;
      deviceID = paramInt2;
    }
    
    int getIndex()
    {
      return index;
    }
    
    int getMaxSimulLines()
    {
      return maxSimulLines;
    }
    
    int getDeviceID()
    {
      return deviceID;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DirectAudioDeviceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */