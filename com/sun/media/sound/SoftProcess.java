package com.sun.media.sound;

public abstract interface SoftProcess
  extends SoftControl
{
  public abstract void init(SoftSynthesizer paramSoftSynthesizer);
  
  public abstract double[] get(int paramInt, String paramString);
  
  public abstract void processControlLogic();
  
  public abstract void reset();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */