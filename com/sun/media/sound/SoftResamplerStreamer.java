package com.sun.media.sound;

import java.io.IOException;

public abstract interface SoftResamplerStreamer
  extends ModelOscillatorStream
{
  public abstract void open(ModelWavetable paramModelWavetable, float paramFloat)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftResamplerStreamer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */