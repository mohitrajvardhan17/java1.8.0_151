package com.sun.media.sound;

import javax.sound.sampled.Clip;

abstract interface AutoClosingClip
  extends Clip
{
  public abstract boolean isAutoClosing();
  
  public abstract void setAutoClosing(boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AutoClosingClip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */