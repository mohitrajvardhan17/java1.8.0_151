package com.sun.media.sound;

public abstract interface ModelWavetable
  extends ModelOscillator
{
  public static final int LOOP_TYPE_OFF = 0;
  public static final int LOOP_TYPE_FORWARD = 1;
  public static final int LOOP_TYPE_RELEASE = 2;
  public static final int LOOP_TYPE_PINGPONG = 4;
  public static final int LOOP_TYPE_REVERSE = 8;
  
  public abstract AudioFloatInputStream openStream();
  
  public abstract float getLoopLength();
  
  public abstract float getLoopStart();
  
  public abstract int getLoopType();
  
  public abstract float getPitchcorrection();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelWavetable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */