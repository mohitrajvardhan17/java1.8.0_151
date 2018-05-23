package com.sun.media.sound;

import java.util.TreeMap;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public final class SoftReceiver
  implements MidiDeviceReceiver
{
  boolean open = true;
  private final Object control_mutex;
  private final SoftSynthesizer synth;
  TreeMap<Long, Object> midimessages;
  SoftMainMixer mainmixer;
  
  public SoftReceiver(SoftSynthesizer paramSoftSynthesizer)
  {
    control_mutex = control_mutex;
    synth = paramSoftSynthesizer;
    mainmixer = paramSoftSynthesizer.getMainMixer();
    if (mainmixer != null) {
      midimessages = mainmixer.midimessages;
    }
  }
  
  public MidiDevice getMidiDevice()
  {
    return synth;
  }
  
  public void send(MidiMessage paramMidiMessage, long paramLong)
  {
    synchronized (control_mutex)
    {
      if (!open) {
        throw new IllegalStateException("Receiver is not open");
      }
    }
    if (paramLong != -1L) {
      synchronized (control_mutex)
      {
        mainmixer.activity();
        while (midimessages.get(Long.valueOf(paramLong)) != null) {
          paramLong += 1L;
        }
        if (((paramMidiMessage instanceof ShortMessage)) && (((ShortMessage)paramMidiMessage).getChannel() > 15)) {
          midimessages.put(Long.valueOf(paramLong), paramMidiMessage.clone());
        } else {
          midimessages.put(Long.valueOf(paramLong), paramMidiMessage.getMessage());
        }
      }
    } else {
      mainmixer.processMessage(paramMidiMessage);
    }
  }
  
  public void close()
  {
    synchronized (control_mutex)
    {
      open = false;
    }
    synth.removeReceiver(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftReceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */