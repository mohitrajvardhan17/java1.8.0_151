package java.awt.image;

import sun.java2d.StateTrackable.State;
import sun.java2d.StateTrackableDelegate;

public final class DataBufferShort
  extends DataBuffer
{
  short[] data;
  short[][] bankdata;
  
  public DataBufferShort(int paramInt)
  {
    super(StateTrackable.State.STABLE, 2, paramInt);
    data = new short[paramInt];
    bankdata = new short[1][];
    bankdata[0] = data;
  }
  
  public DataBufferShort(int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.STABLE, 2, paramInt1, paramInt2);
    bankdata = new short[paramInt2][];
    for (int i = 0; i < paramInt2; i++) {
      bankdata[i] = new short[paramInt1];
    }
    data = bankdata[0];
  }
  
  public DataBufferShort(short[] paramArrayOfShort, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 2, paramInt);
    data = paramArrayOfShort;
    bankdata = new short[1][];
    bankdata[0] = data;
  }
  
  public DataBufferShort(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.UNTRACKABLE, 2, paramInt1, 1, paramInt2);
    data = paramArrayOfShort;
    bankdata = new short[1][];
    bankdata[0] = data;
  }
  
  public DataBufferShort(short[][] paramArrayOfShort, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 2, paramInt, paramArrayOfShort.length);
    bankdata = ((short[][])paramArrayOfShort.clone());
    data = bankdata[0];
  }
  
  public DataBufferShort(short[][] paramArrayOfShort, int paramInt, int[] paramArrayOfInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 2, paramInt, paramArrayOfShort.length, paramArrayOfInt);
    bankdata = ((short[][])paramArrayOfShort.clone());
    data = bankdata[0];
  }
  
  public short[] getData()
  {
    theTrackable.setUntrackable();
    return data;
  }
  
  public short[] getData(int paramInt)
  {
    theTrackable.setUntrackable();
    return bankdata[paramInt];
  }
  
  public short[][] getBankData()
  {
    theTrackable.setUntrackable();
    return (short[][])bankdata.clone();
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
    data[(paramInt1 + offset)] = ((short)paramInt2);
    theTrackable.markDirty();
  }
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3)
  {
    bankdata[paramInt1][(paramInt2 + offsets[paramInt1])] = ((short)paramInt3);
    theTrackable.markDirty();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\DataBufferShort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */