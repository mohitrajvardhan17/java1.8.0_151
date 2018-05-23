package com.sun.media.sound;

public abstract interface ModelOscillator
{
  public abstract int getChannels();
  
  public abstract float getAttenuation();
  
  public abstract ModelOscillatorStream open(float paramFloat);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelOscillator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */