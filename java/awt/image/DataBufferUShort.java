package java.awt.image;

import sun.java2d.StateTrackable.State;
import sun.java2d.StateTrackableDelegate;

public final class DataBufferUShort
  extends DataBuffer
{
  short[] data;
  short[][] bankdata;
  
  public DataBufferUShort(int paramInt)
  {
    super(StateTrackable.State.STABLE, 1, paramInt);
    data = new short[paramInt];
    bankdata = new short[1][];
    bankdata[0] = data;
  }
  
  public DataBufferUShort(int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.STABLE, 1, paramInt1, paramInt2);
    bankdata = new short[paramInt2][];
    for (int i = 0; i < paramInt2; i++) {
      bankdata[i] = new short[paramInt1];
    }
    data = bankdata[0];
  }
  
  public DataBufferUShort(short[] paramArrayOfShort, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 1, paramInt);
    if (paramArrayOfShort == null) {
      throw new NullPointerException("dataArray is null");
    }
    data = paramArrayOfShort;
    bankdata = new short[1][];
    bankdata[0] = data;
  }
  
  public DataBufferUShort(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.UNTRACKABLE, 1, paramInt1, 1, paramInt2);
    if (paramArrayOfShort == null) {
      throw new NullPointerException("dataArray is null");
    }
    if (paramInt1 + paramInt2 > paramArrayOfShort.length) {
      throw new IllegalArgumentException("Length of dataArray is less  than size+offset.");
    }
    data = paramArrayOfShort;
    bankdata = new short[1][];
    bankdata[0] = data;
  }
  
  public DataBufferUShort(short[][] paramArrayOfShort, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 1, paramInt, paramArrayOfShort.length);
    if (paramArrayOfShort == null) {
      throw new NullPointerException("dataArray is null");
    }
    for (int i = 0; i < paramArrayOfShort.length; i++) {
      if (paramArrayOfShort[i] == null) {
        throw new NullPointerException("dataArray[" + i + "] is null");
      }
    }
    bankdata = ((short[][])paramArrayOfShort.clone());
    data = bankdata[0];
  }
  
  public DataBufferUShort(short[][] paramArrayOfShort, int paramInt, int[] paramArrayOfInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 1, paramInt, paramArrayOfShort.length, paramArrayOfInt);
    if (paramArrayOfShort == null) {
      throw new NullPointerException("dataArray is null");
    }
    for (int i = 0; i < paramArrayOfShort.length; i++)
    {
      if (paramArrayOfShort[i] == null) {
        throw new NullPointerException("dataArray[" + i + "] is null");
      }
      if (paramInt + paramArrayOfInt[i] > paramArrayOfShort[i].length) {
        throw new IllegalArgumentException("Length of dataArray[" + i + "] is less than size+offsets[" + i + "].");
      }
    }
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
    return data[(paramInt + offset)] & 0xFFFF;
  }
  
  public int getElem(int paramInt1, int paramInt2)
  {
    return bankdata[paramInt1][(paramInt2 + offsets[paramInt1])] & 0xFFFF;
  }
  
  public void setElem(int paramInt1, int paramInt2)
  {
    data[(paramInt1 + offset)] = ((short)(paramInt2 & 0xFFFF));
    theTrackable.markDirty();
  }
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3)
  {
    bankdata[paramInt1][(paramInt2 + offsets[paramInt1])] = ((short)(paramInt3 & 0xFFFF));
    theTrackable.markDirty();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\DataBufferUShort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */