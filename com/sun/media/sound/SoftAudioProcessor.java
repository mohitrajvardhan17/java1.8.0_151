package com.sun.media.sound;

public abstract interface SoftAudioProcessor
{
  public abstract void globalParameterControlChange(int[] paramArrayOfInt, long paramLong1, long paramLong2);
  
  public abstract void init(float paramFloat1, float paramFloat2);
  
  public abstract void setInput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer);
  
  public abstract void setOutput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer);
  
  public abstract void setMixMode(boolean paramBoolean);
  
  public abstract void processAudio();
  
  public abstract void processControlLogic();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftAudioProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */