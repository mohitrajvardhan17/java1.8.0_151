package sun.java2d.opengl;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import sun.awt.SunToolkit;
import sun.awt.windows.WComponentPeer;
import sun.java2d.SurfaceData;

public abstract class WGLSurfaceData
  extends OGLSurfaceData
{
  protected WComponentPeer peer;
  private WGLGraphicsConfig graphicsConfig;
  
  private native void initOps(long paramLong1, WComponentPeer paramWComponentPeer, long paramLong2);
  
  protected native boolean initPbuffer(long paramLong1, long paramLong2, boolean paramBoolean, int paramInt1, int paramInt2);
  
  protected WGLSurfaceData(WComponentPeer paramWComponentPeer, WGLGraphicsConfig paramWGLGraphicsConfig, ColorModel paramColorModel, int paramInt)
  {
    super(paramWGLGraphicsConfig, paramColorModel, paramInt);
    peer = paramWComponentPeer;
    graphicsConfig = paramWGLGraphicsConfig;
    long l1 = paramWGLGraphicsConfig.getNativeConfigInfo();
    long l2 = paramWComponentPeer != null ? paramWComponentPeer.getHWnd() : 0L;
    initOps(l1, paramWComponentPeer, l2);
  }
  
  public GraphicsConfiguration getDeviceConfiguration()
  {
    return graphicsConfig;
  }
  
  public static WGLWindowSurfaceData createData(WComponentPeer paramWComponentPeer)
  {
    if ((!paramWComponentPeer.isAccelCapable()) || (!SunToolkit.isContainingTopLevelOpaque((Component)paramWComponentPeer.getTarget()))) {
      return null;
    }
    WGLGraphicsConfig localWGLGraphicsConfig = getGC(paramWComponentPeer);
    return new WGLWindowSurfaceData(paramWComponentPeer, localWGLGraphicsConfig);
  }
  
  public static WGLOffScreenSurfaceData createData(WComponentPeer paramWComponentPeer, Image paramImage, int paramInt)
  {
    if ((!paramWComponentPeer.isAccelCapable()) || (!SunToolkit.isContainingTopLevelOpaque((Component)paramWComponentPeer.getTarget()))) {
      return null;
    }
    WGLGraphicsConfig localWGLGraphicsConfig = getGC(paramWComponentPeer);
    Rectangle localRectangle = paramWComponentPeer.getBounds();
    if (paramInt == 4) {
      return new WGLOffScreenSurfaceData(paramWComponentPeer, localWGLGraphicsConfig, width, height, paramImage, paramWComponentPeer.getColorModel(), paramInt);
    }
    return new WGLVSyncOffScreenSurfaceData(paramWComponentPeer, localWGLGraphicsConfig, width, height, paramImage, paramWComponentPeer.getColorModel(), paramInt);
  }
  
  public static WGLOffScreenSurfaceData createData(WGLGraphicsConfig paramWGLGraphicsConfig, int paramInt1, int paramInt2, ColorModel paramColorModel, Image paramImage, int paramInt3)
  {
    return new WGLOffScreenSurfaceData(null, paramWGLGraphicsConfig, paramInt1, paramInt2, paramImage, paramColorModel, paramInt3);
  }
  
  public static WGLGraphicsConfig getGC(WComponentPeer paramWComponentPeer)
  {
    if (paramWComponentPeer != null) {
      return (WGLGraphicsConfig)paramWComponentPeer.getGraphicsConfiguration();
    }
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice localGraphicsDevice = localGraphicsEnvironment.getDefaultScreenDevice();
    return (WGLGraphicsConfig)localGraphicsDevice.getDefaultConfiguration();
  }
  
  public static native boolean updateWindowAccelImpl(long paramLong, WComponentPeer paramWComponentPeer, int paramInt1, int paramInt2);
  
  public static class WGLOffScreenSurfaceData
    extends WGLSurfaceData
  {
    private Image offscreenImage;
    private int width;
    private int height;
    
    public WGLOffScreenSurfaceData(WComponentPeer paramWComponentPeer, WGLGraphicsConfig paramWGLGraphicsConfig, int paramInt1, int paramInt2, Image paramImage, ColorModel paramColorModel, int paramInt3)
    {
      super(paramWGLGraphicsConfig, paramColorModel, paramInt3);
      width = paramInt1;
      height = paramInt2;
      offscreenImage = paramImage;
      initSurface(paramInt1, paramInt2);
    }
    
    public SurfaceData getReplacement()
    {
      return restoreContents(offscreenImage);
    }
    
    public Rectangle getBounds()
    {
      if (type == 4)
      {
        Rectangle localRectangle = peer.getBounds();
        x = (y = 0);
        return localRectangle;
      }
      return new Rectangle(width, height);
    }
    
    public Object getDestination()
    {
      return offscreenImage;
    }
  }
  
  public static class WGLVSyncOffScreenSurfaceData
    extends WGLSurfaceData.WGLOffScreenSurfaceData
  {
    private WGLSurfaceData.WGLOffScreenSurfaceData flipSurface;
    
    public WGLVSyncOffScreenSurfaceData(WComponentPeer paramWComponentPeer, WGLGraphicsConfig paramWGLGraphicsConfig, int paramInt1, int paramInt2, Image paramImage, ColorModel paramColorModel, int paramInt3)
    {
      super(paramWGLGraphicsConfig, paramInt1, paramInt2, paramImage, paramColorModel, paramInt3);
      flipSurface = WGLSurfaceData.createData(paramWComponentPeer, paramImage, 4);
    }
    
    public SurfaceData getFlipSurface()
    {
      return flipSurface;
    }
    
    public void flush()
    {
      flipSurface.flush();
      super.flush();
    }
  }
  
  public static class WGLWindowSurfaceData
    extends WGLSurfaceData
  {
    public WGLWindowSurfaceData(WComponentPeer paramWComponentPeer, WGLGraphicsConfig paramWGLGraphicsConfig)
    {
      super(paramWGLGraphicsConfig, paramWComponentPeer.getColorModel(), 1);
    }
    
    public SurfaceData getReplacement()
    {
      return peer.getSurfaceData();
    }
    
    public Rectangle getBounds()
    {
      Rectangle localRectangle = peer.getBounds();
      x = (y = 0);
      return localRectangle;
    }
    
    public Object getDestination()
    {
      return peer.getTarget();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\WGLSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */