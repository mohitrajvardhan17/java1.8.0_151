package sun.awt.image;

import sun.java2d.SurfaceData;

public class BufImgVolatileSurfaceManager
  extends VolatileSurfaceManager
{
  public BufImgVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
  {
    super(paramSunVolatileImage, paramObject);
  }
  
  protected boolean isAccelerationEnabled()
  {
    return false;
  }
  
  protected SurfaceData initAcceleratedSurface()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\BufImgVolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */