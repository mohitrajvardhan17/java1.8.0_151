package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public final class MidiDeviceTransmitterEnvelope
  implements MidiDeviceTransmitter
{
  private final MidiDevice device;
  private final Transmitter transmitter;
  
  public MidiDeviceTransmitterEnvelope(MidiDevice paramMidiDevice, Transmitter paramTransmitter)
  {
    if ((paramMidiDevice == null) || (paramTransmitter == null)) {
      throw new NullPointerException();
    }
    device = paramMidiDevice;
    transmitter = paramTransmitter;
  }
  
  public void setReceiver(Receiver paramReceiver)
  {
    transmitter.setReceiver(paramReceiver);
  }
  
  public Receiver getReceiver()
  {
    return transmitter.getReceiver();
  }
  
  public void close()
  {
    transmitter.close();
  }
  
  public MidiDevice getMidiDevice()
  {
    return device;
  }
  
  public Transmitter getTransmitter()
  {
    return transmitter;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\MidiDeviceTransmitterEnvelope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */