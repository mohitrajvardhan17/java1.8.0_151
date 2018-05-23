package sun.java2d;

import java.awt.GraphicsConfiguration;
import sun.awt.image.BufImgVolatileSurfaceManager;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.d3d.D3DGraphicsConfig;
import sun.java2d.d3d.D3DVolatileSurfaceManager;
import sun.java2d.opengl.WGLGraphicsConfig;
import sun.java2d.opengl.WGLVolatileSurfaceManager;

public class WindowsSurfaceManagerFactory
  extends SurfaceManagerFactory
{
  public WindowsSurfaceManagerFactory() {}
  
  public VolatileSurfaceManager createVolatileManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
  {
    GraphicsConfiguration localGraphicsConfiguration = paramSunVolatileImage.getGraphicsConfig();
    if ((localGraphicsConfiguration instanceof D3DGraphicsConfig)) {
      return new D3DVolatileSurfaceManager(paramSunVolatileImage, paramObject);
    }
    if ((localGraphicsConfiguration instanceof WGLGraphicsConfig)) {
      return new WGLVolatileSurfaceManager(paramSunVolatileImage, paramObject);
    }
    return new BufImgVolatileSurfaceManager(paramSunVolatileImage, paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\WindowsSurfaceManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */