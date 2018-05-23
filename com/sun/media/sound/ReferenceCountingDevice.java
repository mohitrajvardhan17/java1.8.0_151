package com.sun.media.sound;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public abstract interface ReferenceCountingDevice
{
  public abstract Receiver getReceiverReferenceCounting()
    throws MidiUnavailableException;
  
  public abstract Transmitter getTransmitterReferenceCounting()
    throws MidiUnavailableException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ReferenceCountingDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */