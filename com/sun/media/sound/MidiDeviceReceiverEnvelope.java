package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public final class MidiDeviceReceiverEnvelope
  implements MidiDeviceReceiver
{
  private final MidiDevice device;
  private final Receiver receiver;
  
  public MidiDeviceReceiverEnvelope(MidiDevice paramMidiDevice, Receiver paramReceiver)
  {
    if ((paramMidiDevice == null) || (paramReceiver == null)) {
      throw new NullPointerException();
    }
    device = paramMidiDevice;
    receiver = paramReceiver;
  }
  
  public void close()
  {
    receiver.close();
  }
  
  public void send(MidiMessage paramMidiMessage, long paramLong)
  {
    receiver.send(paramMidiMessage, paramLong);
  }
  
  public MidiDevice getMidiDevice()
  {
    return device;
  }
  
  public Receiver getReceiver()
  {
    return receiver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\MidiDeviceReceiverEnvelope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */