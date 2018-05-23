package java.awt.image;

import sun.java2d.StateTrackable.State;
import sun.java2d.StateTrackableDelegate;

public final class DataBufferDouble
  extends DataBuffer
{
  double[][] bankdata;
  double[] data;
  
  public DataBufferDouble(int paramInt)
  {
    super(StateTrackable.State.STABLE, 5, paramInt);
    data = new double[paramInt];
    bankdata = new double[1][];
    bankdata[0] = data;
  }
  
  public DataBufferDouble(int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.STABLE, 5, paramInt1, paramInt2);
    bankdata = new double[paramInt2][];
    for (int i = 0; i < paramInt2; i++) {
      bankdata[i] = new double[paramInt1];
    }
    data = bankdata[0];
  }
  
  public DataBufferDouble(double[] paramArrayOfDouble, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 5, paramInt);
    data = paramArrayOfDouble;
    bankdata = new double[1][];
    bankdata[0] = data;
  }
  
  public DataBufferDouble(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    super(StateTrackable.State.UNTRACKABLE, 5, paramInt1, 1, paramInt2);
    data = paramArrayOfDouble;
    bankdata = new double[1][];
    bankdata[0] = data;
  }
  
  public DataBufferDouble(double[][] paramArrayOfDouble, int paramInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 5, paramInt, paramArrayOfDouble.length);
    bankdata = ((double[][])paramArrayOfDouble.clone());
    data = bankdata[0];
  }
  
  public DataBufferDouble(double[][] paramArrayOfDouble, int paramInt, int[] paramArrayOfInt)
  {
    super(StateTrackable.State.UNTRACKABLE, 5, paramInt, paramArrayOfDouble.length, paramArrayOfInt);
    bankdata = ((double[][])paramArrayOfDouble.clone());
    data = bankdata[0];
  }
  
  public double[] getData()
  {
    theTrackable.setUntrackable();
    return data;
  }
  
  public double[] getData(int paramInt)
  {
    theTrackable.setUntrackable();
    return bankdata[paramInt];
  }
  
  public double[][] getBankData()
  {
    theTrackable.setUntrackable();
    return (double[][])bankdata.clone();
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
    return (float)data[(paramInt + offset)];
  }
  
  public float getElemFloat(int paramInt1, int paramInt2)
  {
    return (float)bankdata[paramInt1][(paramInt2 + offsets[paramInt1])];
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
    data[(paramInt + offset)] = paramDouble;
    theTrackable.markDirty();
  }
  
  public void setElemDouble(int paramInt1, int paramInt2, double paramDouble)
  {
    bankdata[paramInt1][(paramInt2 + offsets[paramInt1])] = paramDouble;
    theTrackable.markDirty();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\DataBufferDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */