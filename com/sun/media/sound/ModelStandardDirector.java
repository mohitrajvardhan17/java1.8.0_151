package com.sun.media.sound;

import java.util.Arrays;

public final class ModelStandardDirector
  implements ModelDirector
{
  private final ModelPerformer[] performers;
  private final ModelDirectedPlayer player;
  private boolean noteOnUsed = false;
  private boolean noteOffUsed = false;
  
  public ModelStandardDirector(ModelPerformer[] paramArrayOfModelPerformer, ModelDirectedPlayer paramModelDirectedPlayer)
  {
    performers = ((ModelPerformer[])Arrays.copyOf(paramArrayOfModelPerformer, paramArrayOfModelPerformer.length));
    player = paramModelDirectedPlayer;
    for (ModelPerformer localModelPerformer : performers) {
      if (localModelPerformer.isReleaseTriggered()) {
        noteOffUsed = true;
      } else {
        noteOnUsed = true;
      }
    }
  }
  
  public void close() {}
  
  public void noteOff(int paramInt1, int paramInt2)
  {
    if (!noteOffUsed) {
      return;
    }
    for (int i = 0; i < performers.length; i++)
    {
      ModelPerformer localModelPerformer = performers[i];
      if ((localModelPerformer.getKeyFrom() <= paramInt1) && (localModelPerformer.getKeyTo() >= paramInt1) && (localModelPerformer.getVelFrom() <= paramInt2) && (localModelPerformer.getVelTo() >= paramInt2) && (localModelPerformer.isReleaseTriggered())) {
        player.play(i, null);
      }
    }
  }
  
  public void noteOn(int paramInt1, int paramInt2)
  {
    if (!noteOnUsed) {
      return;
    }
    for (int i = 0; i < performers.length; i++)
    {
      ModelPerformer localModelPerformer = performers[i];
      if ((localModelPerformer.getKeyFrom() <= paramInt1) && (localModelPerformer.getKeyTo() >= paramInt1) && (localModelPerformer.getVelFrom() <= paramInt2) && (localModelPerformer.getVelTo() >= paramInt2) && (!localModelPerformer.isReleaseTriggered())) {
        player.play(i, null);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelStandardDirector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */