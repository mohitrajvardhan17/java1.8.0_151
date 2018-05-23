package sun.java2d.opengl;

import java.awt.BufferCapabilities.FlipContents;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.awt.windows.WComponentPeer;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities.VSyncType;

public class WGLVolatileSurfaceManager
  extends VolatileSurfaceManager
{
  private boolean accelerationEnabled;
  
  public WGLVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
  {
    super(paramSunVolatileImage, paramObject);
    int i = paramSunVolatileImage.getTransparency();
    WGLGraphicsConfig localWGLGraphicsConfig = (WGLGraphicsConfig)paramSunVolatileImage.getGraphicsConfig();
    accelerationEnabled = ((i == 1) || ((i == 3) && ((localWGLGraphicsConfig.isCapPresent(12)) || (localWGLGraphicsConfig.isCapPresent(2)))));
  }
  
  protected boolean isAccelerationEnabled()
  {
    return accelerationEnabled;
  }
  
  protected SurfaceData initAcceleratedSurface()
  {
    Component localComponent = vImg.getComponent();
    WComponentPeer localWComponentPeer = localComponent != null ? (WComponentPeer)localComponent.getPeer() : null;
    WGLSurfaceData.WGLOffScreenSurfaceData localWGLOffScreenSurfaceData;
    try
    {
      int i = 0;
      boolean bool = false;
      Object localObject1;
      Object localObject2;
      if ((context instanceof Boolean))
      {
        bool = ((Boolean)context).booleanValue();
        if (bool)
        {
          localObject1 = localWComponentPeer.getBackBufferCaps();
          if ((localObject1 instanceof ExtendedBufferCapabilities))
          {
            localObject2 = (ExtendedBufferCapabilities)localObject1;
            if ((((ExtendedBufferCapabilities)localObject2).getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON) && (((ExtendedBufferCapabilities)localObject2).getFlipContents() == BufferCapabilities.FlipContents.COPIED))
            {
              i = 1;
              bool = false;
            }
          }
        }
      }
      if (bool)
      {
        localWGLOffScreenSurfaceData = WGLSurfaceData.createData(localWComponentPeer, vImg, 4);
      }
      else
      {
        localObject1 = (WGLGraphicsConfig)vImg.getGraphicsConfig();
        localObject2 = ((WGLGraphicsConfig)localObject1).getColorModel(vImg.getTransparency());
        int j = vImg.getForcedAccelSurfaceType();
        if (j == 0) {
          j = ((WGLGraphicsConfig)localObject1).isCapPresent(12) ? 5 : 2;
        }
        if (i != 0) {
          localWGLOffScreenSurfaceData = WGLSurfaceData.createData(localWComponentPeer, vImg, j);
        } else {
          localWGLOffScreenSurfaceData = WGLSurfaceData.createData((WGLGraphicsConfig)localObject1, vImg.getWidth(), vImg.getHeight(), (ColorModel)localObject2, vImg, j);
        }
      }
    }
    catch (NullPointerException localNullPointerException)
    {
      localWGLOffScreenSurfaceData = null;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      localWGLOffScreenSurfaceData = null;
    }
    return localWGLOffScreenSurfaceData;
  }
  
  protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration)
  {
    return (paramGraphicsConfiguration == null) || (((paramGraphicsConfiguration instanceof WGLGraphicsConfig)) && (paramGraphicsConfiguration == vImg.getGraphicsConfig()));
  }
  
  public void initContents()
  {
    if (vImg.getForcedAccelSurfaceType() != 3) {
      super.initContents();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\WGLVolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */