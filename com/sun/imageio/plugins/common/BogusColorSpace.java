package com.sun.imageio.plugins.common;

import java.awt.color.ColorSpace;

public class BogusColorSpace
  extends ColorSpace
{
  private static int getType(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("numComponents < 1!");
    }
    int i;
    switch (paramInt)
    {
    case 1: 
      i = 6;
      break;
    default: 
      i = paramInt + 10;
    }
    return i;
  }
  
  public BogusColorSpace(int paramInt)
  {
    super(getType(paramInt), paramInt);
  }
  
  public float[] toRGB(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length < getNumComponents()) {
      throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
    }
    float[] arrayOfFloat = new float[3];
    System.arraycopy(paramArrayOfFloat, 0, arrayOfFloat, 0, Math.min(3, getNumComponents()));
    return paramArrayOfFloat;
  }
  
  public float[] fromRGB(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length < 3) {
      throw new ArrayIndexOutOfBoundsException("rgbvalue.length < 3");
    }
    float[] arrayOfFloat = new float[getNumComponents()];
    System.arraycopy(paramArrayOfFloat, 0, arrayOfFloat, 0, Math.min(3, arrayOfFloat.length));
    return paramArrayOfFloat;
  }
  
  public float[] toCIEXYZ(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length < getNumComponents()) {
      throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
    }
    float[] arrayOfFloat = new float[3];
    System.arraycopy(paramArrayOfFloat, 0, arrayOfFloat, 0, Math.min(3, getNumComponents()));
    return paramArrayOfFloat;
  }
  
  public float[] fromCIEXYZ(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length < 3) {
      throw new ArrayIndexOutOfBoundsException("xyzvalue.length < 3");
    }
    float[] arrayOfFloat = new float[getNumComponents()];
    System.arraycopy(paramArrayOfFloat, 0, arrayOfFloat, 0, Math.min(3, arrayOfFloat.length));
    return paramArrayOfFloat;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\BogusColorSpace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */