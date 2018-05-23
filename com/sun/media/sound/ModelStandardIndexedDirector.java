package com.sun.media.sound;

import java.util.Arrays;

public final class ModelStandardIndexedDirector
  implements ModelDirector
{
  private final ModelPerformer[] performers;
  private final ModelDirectedPlayer player;
  private boolean noteOnUsed = false;
  private boolean noteOffUsed = false;
  private byte[][] trantables;
  private int[] counters;
  private int[][] mat;
  
  public ModelStandardIndexedDirector(ModelPerformer[] paramArrayOfModelPerformer, ModelDirectedPlayer paramModelDirectedPlayer)
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
    buildindex();
  }
  
  private int[] lookupIndex(int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= 0) && (paramInt1 < 128) && (paramInt2 >= 0) && (paramInt2 < 128))
    {
      int i = trantables[0][paramInt1];
      int j = trantables[1][paramInt2];
      if ((i != -1) && (j != -1)) {
        return mat[(i + j * counters[0])];
      }
    }
    return null;
  }
  
  private int restrict(int paramInt)
  {
    if (paramInt < 0) {
      return 0;
    }
    if (paramInt > 127) {
      return 127;
    }
    return paramInt;
  }
  
  private void buildindex()
  {
    trantables = new byte[2][''];
    counters = new int[trantables.length];
    int n;
    int i1;
    int i2;
    int i3;
    for (ModelPerformer localModelPerformer : performers)
    {
      n = localModelPerformer.getKeyFrom();
      i1 = localModelPerformer.getKeyTo();
      i2 = localModelPerformer.getVelFrom();
      i3 = localModelPerformer.getVelTo();
      if ((n <= i1) && (i2 <= i3))
      {
        n = restrict(n);
        i1 = restrict(i1);
        i2 = restrict(i2);
        i3 = restrict(i3);
        trantables[0][n] = 1;
        trantables[0][(i1 + 1)] = 1;
        trantables[1][i2] = 1;
        trantables[1][(i3 + 1)] = 1;
      }
    }
    Object localObject1;
    int m;
    for (int i = 0; i < trantables.length; i++)
    {
      localObject1 = trantables[i];
      ??? = localObject1.length;
      for (m = ??? - 1; m >= 0; m--)
      {
        if (localObject1[m] == 1)
        {
          localObject1[m] = -1;
          break;
        }
        localObject1[m] = -1;
      }
      m = -1;
      for (n = 0; n < ???; n++)
      {
        if (localObject1[n] != 0)
        {
          m++;
          if (localObject1[n] == -1) {
            break;
          }
        }
        localObject1[n] = ((byte)m);
      }
      counters[i] = m;
    }
    mat = new int[counters[0] * counters[1]][];
    i = 0;
    for (Object localObject2 : performers)
    {
      i1 = ((ModelPerformer)localObject2).getKeyFrom();
      i2 = ((ModelPerformer)localObject2).getKeyTo();
      i3 = ((ModelPerformer)localObject2).getVelFrom();
      int i4 = ((ModelPerformer)localObject2).getVelTo();
      if ((i1 <= i2) && (i3 <= i4))
      {
        i1 = restrict(i1);
        i2 = restrict(i2);
        i3 = restrict(i3);
        i4 = restrict(i4);
        int i5 = trantables[0][i1];
        int i6 = trantables[0][(i2 + 1)];
        int i7 = trantables[1][i3];
        int i8 = trantables[1][(i4 + 1)];
        if (i6 == -1) {
          i6 = counters[0];
        }
        if (i8 == -1) {
          i8 = counters[1];
        }
        for (int i9 = i7; i9 < i8; i9++)
        {
          int i10 = i5 + i9 * counters[0];
          for (int i11 = i5; i11 < i6; i11++)
          {
            int[] arrayOfInt1 = mat[i10];
            if (arrayOfInt1 == null)
            {
              mat[i10] = { i };
            }
            else
            {
              int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
              arrayOfInt2[(arrayOfInt2.length - 1)] = i;
              for (int i12 = 0; i12 < arrayOfInt1.length; i12++) {
                arrayOfInt2[i12] = arrayOfInt1[i12];
              }
              mat[i10] = arrayOfInt2;
            }
            i10++;
          }
        }
        i++;
      }
    }
  }
  
  public void close() {}
  
  public void noteOff(int paramInt1, int paramInt2)
  {
    if (!noteOffUsed) {
      return;
    }
    int[] arrayOfInt1 = lookupIndex(paramInt1, paramInt2);
    if (arrayOfInt1 == null) {
      return;
    }
    for (int k : arrayOfInt1)
    {
      ModelPerformer localModelPerformer = performers[k];
      if (localModelPerformer.isReleaseTriggered()) {
        player.play(k, null);
      }
    }
  }
  
  public void noteOn(int paramInt1, int paramInt2)
  {
    if (!noteOnUsed) {
      return;
    }
    int[] arrayOfInt1 = lookupIndex(paramInt1, paramInt2);
    if (arrayOfInt1 == null) {
      return;
    }
    for (int k : arrayOfInt1)
    {
      ModelPerformer localModelPerformer = performers[k];
      if (!localModelPerformer.isReleaseTriggered()) {
        player.play(k, null);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelStandardIndexedDirector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */