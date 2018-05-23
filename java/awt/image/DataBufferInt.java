package java.awt.image;

import sun.java2d.StateTrackable.State;
import sun.java2d.StateTrackableDelegate;

public final class DataBufferInt
  extends DataBuffer
{
  int[] data;
  int[][] bankdata;
  
  public DataBufferInt(int paramInt)
  {
    super(StateTrackable.State.STABLE, 3, paramInt);
    data = new int[paramInt];
    bankdata = new int[1][];
    bankdata[0] = data;
  }
  
  public DataBufferInt(int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.STABLE, 3, paramInt1, paramInt2);
    bankdata = new int[paramInt2][];
    for (int i = 0; i < paramInt2; i++) {
      bankdata[i] = new int[paramInt1];
    }
    data = bankdata[0];
  }
  
  public DataBufferInt(int[] paramArrayOfInt, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 3, paramInt);
    data = paramArrayOfInt;
    bankdata = new int[1][];
    bankdata[0] = data;
  }
  
  public DataBufferInt(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.UNTRACKABLE, 3, paramInt1, 1, paramInt2);
    data = paramArrayOfInt;
    bankdata = new int[1][];
    bankdata[0] = data;
  }
  
  public DataBufferInt(int[][] paramArrayOfInt, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 3, paramInt, paramArrayOfInt.length);
    bankdata = ((int[][])paramArrayOfInt.clone());
    data = bankdata[0];
  }
  
  public DataBufferInt(int[][] paramArrayOfInt, int paramInt, int[] paramArrayOfInt1)
  {
    super(StateTrackable.State.UNTRACKABLE, 3, paramInt, paramArrayOfInt.length, paramArrayOfInt1);
    bankdata = ((int[][])paramArrayOfInt.clone());
    data = bankdata[0];
  }
  
  public int[] getData()
  {
    theTrackable.setUntrackable();
    return data;
  }
  
  public int[] getData(int paramInt)
  {
    theTrackable.setUntrackable();
    return bankdata[paramInt];
  }
  
  public int[][] getBankData()
  {
    theTrackable.setUntrackable();
    return (int[][])bankdata.clone();
  }
  
  public int getElem(int paramInt)
  {
    return data[(paramInt + offset)];
  }
  
  public int getElem(int paramInt1, int paramInt2)
  {
    return bankdata[paramInt1][(paramInt2 + offsets[paramInt1])];
  }
  
  public void setElem(int paramInt1, int paramInt2)
  {
    data[(paramInt1 + offset)] = paramInt2;
    theTrackable.markDirty();
  }
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3)
  {
    bankdata[paramInt1][(paramInt2 + offsets[paramInt1])] = paramInt3;
    theTrackable.markDirty();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\DataBufferInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */