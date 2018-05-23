package com.sun.media.sound;

import java.util.Arrays;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

public final class SoftProvider
  extends MidiDeviceProvider
{
  static final MidiDevice.Info softinfo = SoftSynthesizer.info;
  private static final MidiDevice.Info[] softinfos = { softinfo };
  
  public SoftProvider() {}
  
  public MidiDevice.Info[] getDeviceInfo()
  {
    return (MidiDevice.Info[])Arrays.copyOf(softinfos, softinfos.length);
  }
  
  public MidiDevice getDevice(MidiDevice.Info paramInfo)
  {
    if (paramInfo == softinfo) {
      return new SoftSynthesizer();
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */