package com.sun.media.sound;

public final class ModelStandardTransform
  implements ModelTransform
{
  public static final boolean DIRECTION_MIN2MAX = false;
  public static final boolean DIRECTION_MAX2MIN = true;
  public static final boolean POLARITY_UNIPOLAR = false;
  public static final boolean POLARITY_BIPOLAR = true;
  public static final int TRANSFORM_LINEAR = 0;
  public static final int TRANSFORM_CONCAVE = 1;
  public static final int TRANSFORM_CONVEX = 2;
  public static final int TRANSFORM_SWITCH = 3;
  public static final int TRANSFORM_ABSOLUTE = 4;
  private boolean direction = false;
  private boolean polarity = false;
  private int transform = 0;
  
  public ModelStandardTransform() {}
  
  public ModelStandardTransform(boolean paramBoolean)
  {
    direction = paramBoolean;
  }
  
  public ModelStandardTransform(boolean paramBoolean1, boolean paramBoolean2)
  {
    direction = paramBoolean1;
    polarity = paramBoolean2;
  }
  
  public ModelStandardTransform(boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    direction = paramBoolean1;
    polarity = paramBoolean2;
    transform = paramInt;
  }
  
  public double transform(double paramDouble)
  {
    if (direction == true) {
      paramDouble = 1.0D - paramDouble;
    }
    if (polarity == true) {
      paramDouble = paramDouble * 2.0D - 1.0D;
    }
    double d1;
    double d2;
    switch (transform)
    {
    case 1: 
      d1 = Math.signum(paramDouble);
      d2 = Math.abs(paramDouble);
      d2 = -(0.4166666666666667D / Math.log(10.0D)) * Math.log(1.0D - d2);
      if (d2 < 0.0D) {
        d2 = 0.0D;
      } else if (d2 > 1.0D) {
        d2 = 1.0D;
      }
      return d1 * d2;
    case 2: 
      d1 = Math.signum(paramDouble);
      d2 = Math.abs(paramDouble);
      d2 = 1.0D + 0.4166666666666667D / Math.log(10.0D) * Math.log(d2);
      if (d2 < 0.0D) {
        d2 = 0.0D;
      } else if (d2 > 1.0D) {
        d2 = 1.0D;
      }
      return d1 * d2;
    case 3: 
      if (polarity == true) {
        return paramDouble > 0.0D ? 1.0D : -1.0D;
      }
      return paramDouble > 0.5D ? 1.0D : 0.0D;
    case 4: 
      return Math.abs(paramDouble);
    }
    return paramDouble;
  }
  
  public boolean getDirection()
  {
    return direction;
  }
  
  public void setDirection(boolean paramBoolean)
  {
    direction = paramBoolean;
  }
  
  public boolean getPolarity()
  {
    return polarity;
  }
  
  public void setPolarity(boolean paramBoolean)
  {
    polarity = paramBoolean;
  }
  
  public int getTransform()
  {
    return transform;
  }
  
  public void setTransform(int paramInt)
  {
    transform = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelStandardTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */