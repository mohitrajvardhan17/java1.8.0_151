package sun.java2d.d3d;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.ImageCapabilities;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.VolatileImage;
import sun.awt.Win32GraphicsConfig;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.awt.windows.WComponentPeer;
import sun.java2d.Surface;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.AccelDeviceEventListener;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.hw.AccelTypedVolatileImage;
import sun.java2d.pipe.hw.ContextCapabilities;

public class D3DGraphicsConfig
  extends Win32GraphicsConfig
  implements AccelGraphicsConfig
{
  private static ImageCapabilities imageCaps = new D3DImageCaps(null);
  private BufferCapabilities bufferCaps;
  private D3DGraphicsDevice device;
  
  protected D3DGraphicsConfig(D3DGraphicsDevice paramD3DGraphicsDevice)
  {
    super(paramD3DGraphicsDevice, 0);
    device = paramD3DGraphicsDevice;
  }
  
  public SurfaceData createManagedSurface(int paramInt1, int paramInt2, int paramInt3)
  {
    return D3DSurfaceData.createData(this, paramInt1, paramInt2, getColorModel(paramInt3), null, 3);
  }
  
  /* Error */
  public synchronized void displayChanged()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 270	sun/awt/Win32GraphicsConfig:displayChanged	()V
    //   4: invokestatic 291	sun/java2d/d3d/D3DRenderQueue:getInstance	()Lsun/java2d/d3d/D3DRenderQueue;
    //   7: astore_1
    //   8: aload_1
    //   9: invokevirtual 289	sun/java2d/d3d/D3DRenderQueue:lock	()V
    //   12: invokestatic 280	sun/java2d/d3d/D3DContext:invalidateCurrentContext	()V
    //   15: aload_1
    //   16: invokevirtual 290	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   19: goto +10 -> 29
    //   22: astore_2
    //   23: aload_1
    //   24: invokevirtual 290	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   27: aload_2
    //   28: athrow
    //   29: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	D3DGraphicsConfig
    //   7	17	1	localD3DRenderQueue	D3DRenderQueue
    //   22	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   12	15	22	finally
  }
  
  public ColorModel getColorModel(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return new DirectColorModel(24, 16711680, 65280, 255);
    case 2: 
      return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
    case 3: 
      ColorSpace localColorSpace = ColorSpace.getInstance(1000);
      return new DirectColorModel(localColorSpace, 32, 16711680, 65280, 255, -16777216, true, 3);
    }
    return null;
  }
  
  public String toString()
  {
    return "D3DGraphicsConfig[dev=" + screen + ",pixfmt=" + visual + "]";
  }
  
  public SurfaceData createSurfaceData(WComponentPeer paramWComponentPeer, int paramInt)
  {
    return super.createSurfaceData(paramWComponentPeer, paramInt);
  }
  
  public void assertOperationSupported(Component paramComponent, int paramInt, BufferCapabilities paramBufferCapabilities)
    throws AWTException
  {
    if ((paramInt < 2) || (paramInt > 4)) {
      throw new AWTException("Only 2-4 buffers supported");
    }
    if ((paramBufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.COPIED) && (paramInt != 2)) {
      throw new AWTException("FlipContents.COPIED is onlysupported for 2 buffers");
    }
  }
  
  public VolatileImage createBackBuffer(WComponentPeer paramWComponentPeer)
  {
    Component localComponent = (Component)paramWComponentPeer.getTarget();
    int i = Math.max(1, localComponent.getWidth());
    int j = Math.max(1, localComponent.getHeight());
    return new SunVolatileImage(localComponent, i, j, Boolean.TRUE);
  }
  
  public void flip(WComponentPeer paramWComponentPeer, Component paramComponent, VolatileImage paramVolatileImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents)
  {
    SurfaceManager localSurfaceManager = SurfaceManager.getManager(paramVolatileImage);
    SurfaceData localSurfaceData = localSurfaceManager.getPrimarySurfaceData();
    Object localObject1;
    if ((localSurfaceData instanceof D3DSurfaceData))
    {
      localObject1 = (D3DSurfaceData)localSurfaceData;
      D3DSurfaceData.swapBuffers((D3DSurfaceData)localObject1, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    else
    {
      localObject1 = paramWComponentPeer.getGraphics();
      try
      {
        ((Graphics)localObject1).drawImage(paramVolatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt3, paramInt4, null);
      }
      finally
      {
        ((Graphics)localObject1).dispose();
      }
    }
    if (paramFlipContents == BufferCapabilities.FlipContents.BACKGROUND)
    {
      localObject1 = paramVolatileImage.getGraphics();
      try
      {
        ((Graphics)localObject1).setColor(paramComponent.getBackground());
        ((Graphics)localObject1).fillRect(0, 0, paramVolatileImage.getWidth(), paramVolatileImage.getHeight());
      }
      finally
      {
        ((Graphics)localObject1).dispose();
      }
    }
  }
  
  public BufferCapabilities getBufferCapabilities()
  {
    if (bufferCaps == null) {
      bufferCaps = new D3DBufferCaps();
    }
    return bufferCaps;
  }
  
  public ImageCapabilities getImageCapabilities()
  {
    return imageCaps;
  }
  
  D3DGraphicsDevice getD3DDevice()
  {
    return device;
  }
  
  public D3DContext getContext()
  {
    return device.getContext();
  }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt4 == 4) || (paramInt4 == 1) || (paramInt4 == 0) || (paramInt3 == 2)) {
      return null;
    }
    int i = paramInt3 == 1 ? 1 : 0;
    if (paramInt4 == 5)
    {
      int j = i != 0 ? 8 : 4;
      if (!device.isCapPresent(j)) {
        return null;
      }
    }
    else if ((paramInt4 == 2) && (i == 0) && (!device.isCapPresent(2)))
    {
      return null;
    }
    AccelTypedVolatileImage localAccelTypedVolatileImage = new AccelTypedVolatileImage(this, paramInt1, paramInt2, paramInt3, paramInt4);
    Surface localSurface = localAccelTypedVolatileImage.getDestSurface();
    if ((!(localSurface instanceof AccelSurface)) || (((AccelSurface)localSurface).getType() != paramInt4))
    {
      localAccelTypedVolatileImage.flush();
      localAccelTypedVolatileImage = null;
    }
    return localAccelTypedVolatileImage;
  }
  
  public ContextCapabilities getContextCapabilities()
  {
    return device.getContextCapabilities();
  }
  
  public void addDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener)
  {
    AccelDeviceEventNotifier.addListener(paramAccelDeviceEventListener, device.getScreen());
  }
  
  public void removeDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener)
  {
    AccelDeviceEventNotifier.removeListener(paramAccelDeviceEventListener);
  }
  
  private static class D3DBufferCaps
    extends BufferCapabilities
  {
    public D3DBufferCaps()
    {
      super(D3DGraphicsConfig.imageCaps, BufferCapabilities.FlipContents.UNDEFINED);
    }
    
    public boolean isMultiBufferAvailable()
    {
      return true;
    }
  }
  
  private static class D3DImageCaps
    extends ImageCapabilities
  {
    private D3DImageCaps()
    {
      super();
    }
    
    public boolean isTrueVolatile()
    {
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */