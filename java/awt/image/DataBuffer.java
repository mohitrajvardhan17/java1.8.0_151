package java.awt.image;

import sun.awt.image.SunWritableRaster;
import sun.awt.image.SunWritableRaster.DataStealer;
import sun.java2d.StateTrackable.State;
import sun.java2d.StateTrackableDelegate;

public abstract class DataBuffer
{
  public static final int TYPE_BYTE = 0;
  public static final int TYPE_USHORT = 1;
  public static final int TYPE_SHORT = 2;
  public static final int TYPE_INT = 3;
  public static final int TYPE_FLOAT = 4;
  public static final int TYPE_DOUBLE = 5;
  public static final int TYPE_UNDEFINED = 32;
  protected int dataType;
  protected int banks;
  protected int offset;
  protected int size;
  protected int[] offsets;
  StateTrackableDelegate theTrackable;
  private static final int[] dataTypeSize = { 8, 16, 16, 32, 32, 64 };
  
  public static int getDataTypeSize(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 5)) {
      throw new IllegalArgumentException("Unknown data type " + paramInt);
    }
    return dataTypeSize[paramInt];
  }
  
  protected DataBuffer(int paramInt1, int paramInt2)
  {
    this(StateTrackable.State.UNTRACKABLE, paramInt1, paramInt2);
  }
  
  DataBuffer(StateTrackable.State paramState, int paramInt1, int paramInt2)
  {
    theTrackable = StateTrackableDelegate.createInstance(paramState);
    dataType = paramInt1;
    banks = 1;
    size = paramInt2;
    offset = 0;
    offsets = new int[1];
  }
  
  protected DataBuffer(int paramInt1, int paramInt2, int paramInt3)
  {
    this(StateTrackable.State.UNTRACKABLE, paramInt1, paramInt2, paramInt3);
  }
  
  DataBuffer(StateTrackable.State paramState, int paramInt1, int paramInt2, int paramInt3)
  {
    theTrackable = StateTrackableDelegate.createInstance(paramState);
    dataType = paramInt1;
    banks = paramInt3;
    size = paramInt2;
    offset = 0;
    offsets = new int[banks];
  }
  
  protected DataBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(StateTrackable.State.UNTRACKABLE, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  DataBuffer(StateTrackable.State paramState, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    theTrackable = StateTrackableDelegate.createInstance(paramState);
    dataType = paramInt1;
    banks = paramInt3;
    size = paramInt2;
    offset = paramInt4;
    offsets = new int[paramInt3];
    for (int i = 0; i < paramInt3; i++) {
      offsets[i] = paramInt4;
    }
  }
  
  protected DataBuffer(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    this(StateTrackable.State.UNTRACKABLE, paramInt1, paramInt2, paramInt3, paramArrayOfInt);
  }
  
  DataBuffer(StateTrackable.State paramState, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    if (paramInt3 != paramArrayOfInt.length) {
      throw new ArrayIndexOutOfBoundsException("Number of banks does not match number of bank offsets");
    }
    theTrackable = StateTrackableDelegate.createInstance(paramState);
    dataType = paramInt1;
    banks = paramInt3;
    size = paramInt2;
    offset = paramArrayOfInt[0];
    offsets = ((int[])paramArrayOfInt.clone());
  }
  
  public int getDataType()
  {
    return dataType;
  }
  
  public int getSize()
  {
    return size;
  }
  
  public int getOffset()
  {
    return offset;
  }
  
  public int[] getOffsets()
  {
    return (int[])offsets.clone();
  }
  
  public int getNumBanks()
  {
    return banks;
  }
  
  public int getElem(int paramInt)
  {
    return getElem(0, paramInt);
  }
  
  public abstract int getElem(int paramInt1, int paramInt2);
  
  public void setElem(int paramInt1, int paramInt2)
  {
    setElem(0, paramInt1, paramInt2);
  }
  
  public abstract void setElem(int paramInt1, int paramInt2, int paramInt3);
  
  public float getElemFloat(int paramInt)
  {
    return getElem(paramInt);
  }
  
  public float getElemFloat(int paramInt1, int paramInt2)
  {
    return getElem(paramInt1, paramInt2);
  }
  
  public void setElemFloat(int paramInt, float paramFloat)
  {
    setElem(paramInt, (int)paramFloat);
  }
  
  public void setElemFloat(int paramInt1, int paramInt2, float paramFloat)
  {
    setElem(paramInt1, paramInt2, (int)paramFloat);
  }
  
  public double getElemDouble(int paramInt)
  {
    return getElem(paramInt);
  }
  
  public double getElemDouble(int paramInt1, int paramInt2)
  {
    return getElem(paramInt1, paramInt2);
  }
  
  public void setElemDouble(int paramInt, double paramDouble)
  {
    setElem(paramInt, (int)paramDouble);
  }
  
  public void setElemDouble(int paramInt1, int paramInt2, double paramDouble)
  {
    setElem(paramInt1, paramInt2, (int)paramDouble);
  }
  
  static int[] toIntArray(Object paramObject)
  {
    if ((paramObject instanceof int[])) {
      return (int[])paramObject;
    }
    if (paramObject == null) {
      return null;
    }
    Object localObject;
    int[] arrayOfInt;
    int i;
    if ((paramObject instanceof short[]))
    {
      localObject = (short[])paramObject;
      arrayOfInt = new int[localObject.length];
      for (i = 0; i < localObject.length; i++) {
        localObject[i] &= 0xFFFF;
      }
      return arrayOfInt;
    }
    if ((paramObject instanceof byte[]))
    {
      localObject = (byte[])paramObject;
      arrayOfInt = new int[localObject.length];
      for (i = 0; i < localObject.length; i++) {
        arrayOfInt[i] = (0xFF & localObject[i]);
      }
      return arrayOfInt;
    }
    return null;
  }
  
  static
  {
    SunWritableRaster.setDataStealer(new SunWritableRaster.DataStealer()
    {
      public byte[] getData(DataBufferByte paramAnonymousDataBufferByte, int paramAnonymousInt)
      {
        return bankdata[paramAnonymousInt];
      }
      
      public short[] getData(DataBufferUShort paramAnonymousDataBufferUShort, int paramAnonymousInt)
      {
        return bankdata[paramAnonymousInt];
      }
      
      public int[] getData(DataBufferInt paramAnonymousDataBufferInt, int paramAnonymousInt)
      {
        return bankdata[paramAnonymousInt];
      }
      
      public StateTrackableDelegate getTrackable(DataBuffer paramAnonymousDataBuffer)
      {
        return theTrackable;
      }
      
      public void setTrackable(DataBuffer paramAnonymousDataBuffer, StateTrackableDelegate paramAnonymousStateTrackableDelegate)
      {
        theTrackable = paramAnonymousStateTrackableDelegate;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\DataBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */