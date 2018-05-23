package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.spi.MidiDeviceProvider;

public final class RealTimeSequencerProvider
  extends MidiDeviceProvider
{
  public RealTimeSequencerProvider() {}
  
  public MidiDevice.Info[] getDeviceInfo()
  {
    MidiDevice.Info[] arrayOfInfo = { RealTimeSequencer.info };
    return arrayOfInfo;
  }
  
  public MidiDevice getDevice(MidiDevice.Info paramInfo)
  {
    if ((paramInfo != null) && (!paramInfo.equals(RealTimeSequencer.info))) {
      return null;
    }
    try
    {
      return new RealTimeSequencer();
    }
    catch (MidiUnavailableException localMidiUnavailableException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\RealTimeSequencerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */