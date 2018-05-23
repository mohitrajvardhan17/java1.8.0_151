package sun.java2d;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;

public abstract class SurfaceManagerFactory
{
  private static SurfaceManagerFactory instance;
  
  public SurfaceManagerFactory() {}
  
  public static synchronized SurfaceManagerFactory getInstance()
  {
    if (instance == null) {
      throw new IllegalStateException("No SurfaceManagerFactory set.");
    }
    return instance;
  }
  
  public static synchronized void setInstance(SurfaceManagerFactory paramSurfaceManagerFactory)
  {
    if (paramSurfaceManagerFactory == null) {
      throw new IllegalArgumentException("factory must be non-null");
    }
    if (instance != null) {
      throw new IllegalStateException("The surface manager factory is already initialized");
    }
    instance = paramSurfaceManagerFactory;
  }
  
  public abstract VolatileSurfaceManager createVolatileManager(SunVolatileImage paramSunVolatileImage, Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\SurfaceManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */