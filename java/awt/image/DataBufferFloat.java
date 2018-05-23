package java.awt.image;

import sun.java2d.StateTrackable.State;
import sun.java2d.StateTrackableDelegate;

public final class DataBufferFloat
  extends DataBuffer
{
  float[][] bankdata;
  float[] data;
  
  public DataBufferFloat(int paramInt)
  {
    super(StateTrackable.State.STABLE, 4, paramInt);
    data = new float[paramInt];
    bankdata = new float[1][];
    bankdata[0] = data;
  }
  
  public DataBufferFloat(int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.STABLE, 4, paramInt1, paramInt2);
    bankdata = new float[paramInt2][];
    for (int i = 0; i < paramInt2; i++) {
      bankdata[i] = new float[paramInt1];
    }
    data = bankdata[0];
  }
  
  public DataBufferFloat(float[] paramArrayOfFloat, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 4, paramInt);
    data = paramArrayOfFloat;
    bankdata = new float[1][];
    bankdata[0] = data;
  }
  
  public DataBufferFloat(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.UNTRACKABLE, 4, paramInt1, 1, paramInt2);
    data = paramArrayOfFloat;
    bankdata = new float[1][];
    bankdata[0] = data;
  }
  
  public DataBufferFloat(float[][] paramArrayOfFloat, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 4, paramInt, paramArrayOfFloat.length);
    bankdata = ((float[][])paramArrayOfFloat.clone());
    data = bankdata[0];
  }
  
  public DataBufferFloat(float[][] paramArrayOfFloat, int paramInt, int[] paramArrayOfInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 4, paramInt, paramArrayOfFloat.length, paramArrayOfInt);
    bankdata = ((float[][])paramArrayOfFloat.clone());
    data = bankdata[0];
  }
  
  public float[] getData()
  {
    theTrackable.setUntrackable();
    return data;
  }
  
  public float[] getData(int paramInt)
  {
    theTrackable.setUntrackable();
    return bankdata[paramInt];
  }
  
  public float[][] getBankData()
  {
    theTrackable.setUntrackable();
    return (float[][])bankdata.clone();
  }
  
  public int getElem(int paramInt)
  {
    return (int)data[(paramInt + offset)];
  }
  
  public int getElem(int paramInt1, int paramInt2)
  {
    return (int)bankdata[paramInt1][(paramInt2 + offsets[paramInt1])];
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
  
  public float getElemFloat(int paramInt)
  {
    return data[(paramInt + offset)];
  }
  
  public float getElemFloat(int paramInt1, int paramInt2)
  {
    return bankdata[paramInt1][(paramInt2 + offsets[paramInt1])];
  }
  
  public void setElemFloat(int paramInt, float paramFloat)
  {
    data[(paramInt + offset)] = paramFloat;
    theTrackable.markDirty();
  }
  
  public void setElemFloat(int paramInt1, int paramInt2, float paramFloat)
  {
    bankdata[paramInt1][(paramInt2 + offsets[paramInt1])] = paramFloat;
    theTrackable.markDirty();
  }
  
  public double getElemDouble(int paramInt)
  {
    return data[(paramInt + offset)];
  }
  
  public double getElemDouble(int paramInt1, int paramInt2)
  {
    return bankdata[paramInt1][(paramInt2 + offsets[paramInt1])];
  }
  
  public void setElemDouble(int paramInt, double paramDouble)
  {
    data[(paramInt + offset)] = ((float)paramDouble);
    theTrackable.markDirty();
  }
  
  public void setElemDouble(int paramInt1, int paramInt2, double paramDouble)
  {
    bankdata[paramInt1][(paramInt2 + offsets[paramInt1])] = ((float)paramDouble);
    theTrackable.markDirty();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\DataBufferFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */