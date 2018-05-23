package com.sun.media.sound;

import java.io.IOException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.VoiceStatus;

public abstract interface ModelOscillatorStream
{
  public abstract void setPitch(float paramFloat);
  
  public abstract void noteOn(MidiChannel paramMidiChannel, VoiceStatus paramVoiceStatus, int paramInt1, int paramInt2);
  
  public abstract void noteOff(int paramInt);
  
  public abstract int read(float[][] paramArrayOfFloat, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void close()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelOscillatorStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */