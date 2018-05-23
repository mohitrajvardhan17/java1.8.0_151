package java.awt.image;

public class Kernel
  implements Cloneable
{
  private int width;
  private int height;
  private int xOrigin;
  private int yOrigin;
  private float[] data;
  
  private static native void initIDs();
  
  public Kernel(int paramInt1, int paramInt2, float[] paramArrayOfFloat)
  {
    width = paramInt1;
    height = paramInt2;
    xOrigin = (paramInt1 - 1 >> 1);
    yOrigin = (paramInt2 - 1 >> 1);
    int i = paramInt1 * paramInt2;
    if (paramArrayOfFloat.length < i) {
      throw new IllegalArgumentException("Data array too small (is " + paramArrayOfFloat.length + " and should be " + i);
    }
    data = new float[i];
    System.arraycopy(paramArrayOfFloat, 0, data, 0, i);
  }
  
  public final int getXOrigin()
  {
    return xOrigin;
  }
  
  public final int getYOrigin()
  {
    return yOrigin;
  }
  
  public final int getWidth()
  {
    return width;
  }
  
  public final int getHeight()
  {
    return height;
  }
  
  public final float[] getKernelData(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      paramArrayOfFloat = new float[data.length];
    } else if (paramArrayOfFloat.length < data.length) {
      throw new IllegalArgumentException("Data array too small (should be " + data.length + " but is " + paramArrayOfFloat.length + " )");
    }
    System.arraycopy(data, 0, paramArrayOfFloat, 0, data.length);
    return paramArrayOfFloat;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  static
  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\Kernel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */