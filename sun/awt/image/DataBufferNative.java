package sun.awt.image;

import java.awt.image.DataBuffer;
import sun.java2d.SurfaceData;

public class DataBufferNative
  extends DataBuffer
{
  protected SurfaceData surfaceData;
  protected int width;
  
  public DataBufferNative(SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramInt1, paramInt2 * paramInt3);
    width = paramInt2;
    surfaceData = paramSurfaceData;
  }
  
  protected native int getElem(int paramInt1, int paramInt2, SurfaceData paramSurfaceData);
  
  public int getElem(int paramInt1, int paramInt2)
  {
    return getElem(paramInt2 % width, paramInt2 / width, surfaceData);
  }
  
  protected native void setElem(int paramInt1, int paramInt2, int paramInt3, SurfaceData paramSurfaceData);
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3)
  {
    setElem(paramInt2 % width, paramInt2 / width, paramInt3, surfaceData);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\DataBufferNative.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */