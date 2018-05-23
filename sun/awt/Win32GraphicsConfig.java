package sun.awt;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import sun.awt.image.OffScreenImage;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager.ProxiedGraphicsConfig;
import sun.awt.windows.WComponentPeer;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.windows.GDIWindowSurfaceData;

public class Win32GraphicsConfig
  extends GraphicsConfiguration
  implements DisplayChangedListener, SurfaceManager.ProxiedGraphicsConfig
{
  protected Win32GraphicsDevice screen;
  protected int visual;
  protected RenderLoops solidloops;
  private SurfaceType sTypeOrig = null;
  
  private static native void initIDs();
  
  public static Win32GraphicsConfig getConfig(Win32GraphicsDevice paramWin32GraphicsDevice, int paramInt)
  {
    return new Win32GraphicsConfig(paramWin32GraphicsDevice, paramInt);
  }
  
  @Deprecated
  public Win32GraphicsConfig(GraphicsDevice paramGraphicsDevice, int paramInt)
  {
    screen = ((Win32GraphicsDevice)paramGraphicsDevice);
    visual = paramInt;
    ((Win32GraphicsDevice)paramGraphicsDevice).addDisplayChangedListener(this);
  }
  
  public GraphicsDevice getDevice()
  {
    return screen;
  }
  
  public int getVisual()
  {
    return visual;
  }
  
  public Object getProxyKey()
  {
    return screen;
  }
  
  public synchronized RenderLoops getSolidLoops(SurfaceType paramSurfaceType)
  {
    if ((solidloops == null) || (sTypeOrig != paramSurfaceType))
    {
      solidloops = SurfaceData.makeRenderLoops(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, paramSurfaceType);
      sTypeOrig = paramSurfaceType;
    }
    return solidloops;
  }
  
  public synchronized ColorModel getColorModel()
  {
    return screen.getColorModel();
  }
  
  public ColorModel getDeviceColorModel()
  {
    return screen.getDynamicColorModel();
  }
  
  public ColorModel getColorModel(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return getColorModel();
    case 2: 
      return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
    case 3: 
      return ColorModel.getRGBdefault();
    }
    return null;
  }
  
  public AffineTransform getDefaultTransform()
  {
    return new AffineTransform();
  }
  
  public AffineTransform getNormalizingTransform()
  {
    Win32GraphicsEnvironment localWin32GraphicsEnvironment = (Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
    double d1 = localWin32GraphicsEnvironment.getXResolution() / 72.0D;
    double d2 = localWin32GraphicsEnvironment.getYResolution() / 72.0D;
    return new AffineTransform(d1, 0.0D, 0.0D, d2, 0.0D, 0.0D);
  }
  
  public String toString()
  {
    return super.toString() + "[dev=" + screen + ",pixfmt=" + visual + "]";
  }
  
  private native Rectangle getBounds(int paramInt);
  
  public Rectangle getBounds()
  {
    return getBounds(screen.getScreen());
  }
  
  public synchronized void displayChanged()
  {
    solidloops = null;
  }
  
  public void paletteChanged() {}
  
  public SurfaceData createSurfaceData(WComponentPeer paramWComponentPeer, int paramInt)
  {
    return GDIWindowSurfaceData.createData(paramWComponentPeer);
  }
  
  public Image createAcceleratedImage(Component paramComponent, int paramInt1, int paramInt2)
  {
    ColorModel localColorModel = getColorModel(1);
    WritableRaster localWritableRaster = localColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
    return new OffScreenImage(paramComponent, localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied());
  }
  
  public void assertOperationSupported(Component paramComponent, int paramInt, BufferCapabilities paramBufferCapabilities)
    throws AWTException
  {
    throw new AWTException("The operation requested is not supported");
  }
  
  public VolatileImage createBackBuffer(WComponentPeer paramWComponentPeer)
  {
    Component localComponent = (Component)paramWComponentPeer.getTarget();
    return new SunVolatileImage(localComponent, localComponent.getWidth(), localComponent.getHeight(), Boolean.TRUE);
  }
  
  public void flip(WComponentPeer paramWComponentPeer, Component paramComponent, VolatileImage paramVolatileImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents)
  {
    Graphics localGraphics;
    if ((paramFlipContents == BufferCapabilities.FlipContents.COPIED) || (paramFlipContents == BufferCapabilities.FlipContents.UNDEFINED))
    {
      localGraphics = paramWComponentPeer.getGraphics();
      try
      {
        localGraphics.drawImage(paramVolatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt3, paramInt4, null);
      }
      finally
      {
        localGraphics.dispose();
      }
    }
    else if (paramFlipContents == BufferCapabilities.FlipContents.BACKGROUND)
    {
      localGraphics = paramVolatileImage.getGraphics();
      try
      {
        localGraphics.setColor(paramComponent.getBackground());
        localGraphics.fillRect(0, 0, paramVolatileImage.getWidth(), paramVolatileImage.getHeight());
      }
      finally
      {
        localGraphics.dispose();
      }
    }
  }
  
  public boolean isTranslucencyCapable()
  {
    return true;
  }
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\Win32GraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */