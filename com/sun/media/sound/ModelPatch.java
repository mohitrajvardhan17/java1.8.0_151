package com.sun.media.sound;

import javax.sound.midi.Patch;

public final class ModelPatch
  extends Patch
{
  private boolean percussion = false;
  
  public ModelPatch(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
  }
  
  public ModelPatch(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramInt1, paramInt2);
    percussion = paramBoolean;
  }
  
  public boolean isPercussion()
  {
    return percussion;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelPatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */