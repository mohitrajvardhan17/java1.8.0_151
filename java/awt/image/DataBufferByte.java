package java.awt.image;

import sun.java2d.StateTrackable.State;
import sun.java2d.StateTrackableDelegate;

public final class DataBufferByte
  extends DataBuffer
{
  byte[] data;
  byte[][] bankdata;
  
  public DataBufferByte(int paramInt)
  {
    super(StateTrackable.State.STABLE, 0, paramInt);
    data = new byte[paramInt];
    bankdata = new byte[1][];
    bankdata[0] = data;
  }
  
  public DataBufferByte(int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.STABLE, 0, paramInt1, paramInt2);
    bankdata = new byte[paramInt2][];
    for (int i = 0; i < paramInt2; i++) {
      bankdata[i] = new byte[paramInt1];
    }
    data = bankdata[0];
  }
  
  public DataBufferByte(byte[] paramArrayOfByte, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 0, paramInt);
    data = paramArrayOfByte;
    bankdata = new byte[1][];
    bankdata[0] = data;
  }
  
  public DataBufferByte(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.UNTRACKABLE, 0, paramInt1, 1, paramInt2);
    data = paramArrayOfByte;
    bankdata = new byte[1][];
    bankdata[0] = data;
  }
  
  public DataBufferByte(byte[][] paramArrayOfByte, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 0, paramInt, paramArrayOfByte.length);
    bankdata = ((byte[][])paramArrayOfByte.clone());
    data = bankdata[0];
  }
  
  public DataBufferByte(byte[][] paramArrayOfByte, int paramInt, int[] paramArrayOfInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 0, paramInt, paramArrayOfByte.length, paramArrayOfInt);
    bankdata = ((byte[][])paramArrayOfByte.clone());
    data = bankdata[0];
  }
  
  public byte[] getData()
  {
    theTrackable.setUntrackable();
    return data;
  }
  
  public byte[] getData(int paramInt)
  {
    theTrackable.setUntrackable();
    return bankdata[paramInt];
  }
  
  public byte[][] getBankData()
  {
    theTrackable.setUntrackable();
    return (byte[][])bankdata.clone();
  }
  
  public int getElem(int paramInt)
  {
    return data[(paramInt + offset)] & 0xFF;
  }
  
  public int getElem(int paramInt1, int paramInt2)
  {
    return bankdata[paramInt1][(paramInt2 + offsets[paramInt1])] & 0xFF;
  }
  
  public void setElem(int paramInt1, int paramInt2)
  {
    data[(paramInt1 + offset)] = ((byte)paramInt2);
    theTrackable.markDirty();
  }
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3)
  {
    bankdata[paramInt1][(paramInt2 + offsets[paramInt1])] = ((byte)paramInt3);
    theTrackable.markDirty();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\DataBufferByte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */