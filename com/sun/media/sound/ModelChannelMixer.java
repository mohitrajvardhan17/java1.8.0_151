package com.sun.media.sound;

import javax.sound.midi.MidiChannel;

public abstract interface ModelChannelMixer
  extends MidiChannel
{
  public abstract boolean process(float[][] paramArrayOfFloat, int paramInt1, int paramInt2);
  
  public abstract void stop();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelChannelMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */