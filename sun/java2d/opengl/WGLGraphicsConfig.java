package sun.java2d.opengl;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ImageCapabilities;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.VolatileImage;
import sun.awt.Win32GraphicsConfig;
import sun.awt.Win32GraphicsDevice;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.awt.windows.WComponentPeer;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.SunGraphics2D;
import sun.java2d.Surface;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.AccelDeviceEventListener;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.hw.AccelTypedVolatileImage;
import sun.java2d.pipe.hw.ContextCapabilities;
import sun.java2d.windows.GDIWindowSurfaceData;

public class WGLGraphicsConfig
  extends Win32GraphicsConfig
  implements OGLGraphicsConfig
{
  protected static boolean wglAvailable = initWGL();
  private static ImageCapabilities imageCaps = new WGLImageCaps(null);
  private BufferCapabilities bufferCaps;
  private long pConfigInfo;
  private ContextCapabilities oglCaps;
  private OGLContext context;
  private Object disposerReferent = new Object();
  
  public static native int getDefaultPixFmt(int paramInt);
  
  private static native boolean initWGL();
  
  private static native long getWGLConfigInfo(int paramInt1, int paramInt2);
  
  private static native int getOGLCapabilities(long paramLong);
  
  protected WGLGraphicsConfig(Win32GraphicsDevice paramWin32GraphicsDevice, int paramInt, long paramLong, ContextCapabilities paramContextCapabilities)
  {
    super(paramWin32GraphicsDevice, paramInt);
    pConfigInfo = paramLong;
    oglCaps = paramContextCapabilities;
    context = new OGLContext(OGLRenderQueue.getInstance(), this);
    Disposer.addRecord(disposerReferent, new WGLGCDisposerRecord(pConfigInfo, paramWin32GraphicsDevice.getScreen()));
  }
  
  public Object getProxyKey()
  {
    return this;
  }
  
  public SurfaceData createManagedSurface(int paramInt1, int paramInt2, int paramInt3)
  {
    return WGLSurfaceData.createData(this, paramInt1, paramInt2, getColorModel(paramInt3), null, 3);
  }
  
  public static WGLGraphicsConfig getConfig(Win32GraphicsDevice paramWin32GraphicsDevice, int paramInt)
  {
    if (!wglAvailable) {
      return null;
    }
    long l = 0L;
    String[] arrayOfString = new String[1];
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.lock();
    try
    {
      OGLContext.invalidateCurrentContext();
      WGLGetConfigInfo localWGLGetConfigInfo = new WGLGetConfigInfo(paramWin32GraphicsDevice.getScreen(), paramInt, null);
      localOGLRenderQueue.flushAndInvokeNow(localWGLGetConfigInfo);
      l = localWGLGetConfigInfo.getConfigInfo();
      if (l != 0L)
      {
        OGLContext.setScratchSurface(l);
        localOGLRenderQueue.flushAndInvokeNow(new Runnable()
        {
          public void run()
          {
            val$ids[0] = OGLContext.getOGLIdString();
          }
        });
      }
    }
    finally
    {
      localOGLRenderQueue.unlock();
    }
    if (l == 0L) {
      return null;
    }
    int i = getOGLCapabilities(l);
    OGLContext.OGLContextCaps localOGLContextCaps = new OGLContext.OGLContextCaps(i, arrayOfString[0]);
    return new WGLGraphicsConfig(paramWin32GraphicsDevice, paramInt, l, localOGLContextCaps);
  }
  
  public static boolean isWGLAvailable()
  {
    return wglAvailable;
  }
  
  public final boolean isCapPresent(int paramInt)
  {
    return (oglCaps.getCaps() & paramInt) != 0;
  }
  
  public final long getNativeConfigInfo()
  {
    return pConfigInfo;
  }
  
  public final OGLContext getContext()
  {
    return context;
  }
  
  /* Error */
  public synchronized void displayChanged()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 387	sun/awt/Win32GraphicsConfig:displayChanged	()V
    //   4: invokestatic 407	sun/java2d/opengl/OGLRenderQueue:getInstance	()Lsun/java2d/opengl/OGLRenderQueue;
    //   7: astore_1
    //   8: aload_1
    //   9: invokevirtual 404	sun/java2d/opengl/OGLRenderQueue:lock	()V
    //   12: invokestatic 400	sun/java2d/opengl/OGLContext:invalidateCurrentContext	()V
    //   15: aload_1
    //   16: invokevirtual 405	sun/java2d/opengl/OGLRenderQueue:unlock	()V
    //   19: goto +10 -> 29
    //   22: astore_2
    //   23: aload_1
    //   24: invokevirtual 405	sun/java2d/opengl/OGLRenderQueue:unlock	()V
    //   27: aload_2
    //   28: athrow
    //   29: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	WGLGraphicsConfig
    //   7	17	1	localOGLRenderQueue	OGLRenderQueue
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
    return "WGLGraphicsConfig[dev=" + screen + ",pixfmt=" + visual + "]";
  }
  
  public SurfaceData createSurfaceData(WComponentPeer paramWComponentPeer, int paramInt)
  {
    Object localObject = WGLSurfaceData.createData(paramWComponentPeer);
    if (localObject == null) {
      localObject = GDIWindowSurfaceData.createData(paramWComponentPeer);
    }
    return (SurfaceData)localObject;
  }
  
  public void assertOperationSupported(Component paramComponent, int paramInt, BufferCapabilities paramBufferCapabilities)
    throws AWTException
  {
    if (paramInt > 2) {
      throw new AWTException("Only double or single buffering is supported");
    }
    BufferCapabilities localBufferCapabilities = getBufferCapabilities();
    if (!localBufferCapabilities.isPageFlipping()) {
      throw new AWTException("Page flipping is not supported");
    }
    if (paramBufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.PRIOR) {
      throw new AWTException("FlipContents.PRIOR is not supported");
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
    Object localObject1;
    if (paramFlipContents == BufferCapabilities.FlipContents.COPIED)
    {
      localObject1 = SurfaceManager.getManager(paramVolatileImage);
      SurfaceData localSurfaceData1 = ((SurfaceManager)localObject1).getPrimarySurfaceData();
      Object localObject2;
      if ((localSurfaceData1 instanceof WGLSurfaceData.WGLVSyncOffScreenSurfaceData))
      {
        localObject2 = (WGLSurfaceData.WGLVSyncOffScreenSurfaceData)localSurfaceData1;
        SurfaceData localSurfaceData2 = ((WGLSurfaceData.WGLVSyncOffScreenSurfaceData)localObject2).getFlipSurface();
        SunGraphics2D localSunGraphics2D = new SunGraphics2D(localSurfaceData2, Color.black, Color.white, null);
        try
        {
          localSunGraphics2D.drawImage(paramVolatileImage, 0, 0, null);
        }
        finally
        {
          localSunGraphics2D.dispose();
        }
      }
      else
      {
        localObject2 = paramWComponentPeer.getGraphics();
        try
        {
          ((Graphics)localObject2).drawImage(paramVolatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt3, paramInt4, null);
        }
        finally
        {
          ((Graphics)localObject2).dispose();
        }
        return;
      }
    }
    else if (paramFlipContents == BufferCapabilities.FlipContents.PRIOR)
    {
      return;
    }
    OGLSurfaceData.swapBuffers(paramWComponentPeer.getData());
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
    if (bufferCaps == null)
    {
      boolean bool = isCapPresent(65536);
      bufferCaps = new WGLBufferCaps(bool);
    }
    return bufferCaps;
  }
  
  public ImageCapabilities getImageCapabilities()
  {
    return imageCaps;
  }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt4 == 4) || (paramInt4 == 1) || (paramInt4 == 0) || (paramInt3 == 2)) {
      return null;
    }
    if (paramInt4 == 5)
    {
      if (!isCapPresent(12)) {
        return null;
      }
    }
    else if (paramInt4 == 2)
    {
      int i = paramInt3 == 1 ? 1 : 0;
      if ((i == 0) && (!isCapPresent(2))) {
        return null;
      }
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
    return oglCaps;
  }
  
  public void addDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener)
  {
    AccelDeviceEventNotifier.addListener(paramAccelDeviceEventListener, screen.getScreen());
  }
  
  public void removeDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener)
  {
    AccelDeviceEventNotifier.removeListener(paramAccelDeviceEventListener);
  }
  
  private static class WGLBufferCaps
    extends BufferCapabilities
  {
    public WGLBufferCaps(boolean paramBoolean)
    {
      super(WGLGraphicsConfig.imageCaps, paramBoolean ? BufferCapabilities.FlipContents.UNDEFINED : null);
    }
  }
  
  private static class WGLGCDisposerRecord
    implements DisposerRecord
  {
    private long pCfgInfo;
    private int screen;
    
    public WGLGCDisposerRecord(long paramLong, int paramInt)
    {
      pCfgInfo = paramLong;
    }
    
    /* Error */
    public void dispose()
    {
      // Byte code:
      //   0: invokestatic 53	sun/java2d/opengl/OGLRenderQueue:getInstance	()Lsun/java2d/opengl/OGLRenderQueue;
      //   3: astore_1
      //   4: aload_1
      //   5: invokevirtual 49	sun/java2d/opengl/OGLRenderQueue:lock	()V
      //   8: aload_1
      //   9: new 32	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord$1
      //   12: dup
      //   13: aload_0
      //   14: invokespecial 54	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord$1:<init>	(Lsun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord;)V
      //   17: invokevirtual 52	sun/java2d/opengl/OGLRenderQueue:flushAndInvokeNow	(Ljava/lang/Runnable;)V
      //   20: aload_1
      //   21: invokevirtual 50	sun/java2d/opengl/OGLRenderQueue:unlock	()V
      //   24: goto +10 -> 34
      //   27: astore_2
      //   28: aload_1
      //   29: invokevirtual 50	sun/java2d/opengl/OGLRenderQueue:unlock	()V
      //   32: aload_2
      //   33: athrow
      //   34: aload_0
      //   35: getfield 47	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord:pCfgInfo	J
      //   38: lconst_0
      //   39: lcmp
      //   40: ifeq +15 -> 55
      //   43: aload_0
      //   44: getfield 47	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord:pCfgInfo	J
      //   47: invokestatic 51	sun/java2d/opengl/OGLRenderQueue:disposeGraphicsConfig	(J)V
      //   50: aload_0
      //   51: lconst_0
      //   52: putfield 47	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord:pCfgInfo	J
      //   55: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	56	0	this	WGLGCDisposerRecord
      //   3	26	1	localOGLRenderQueue	OGLRenderQueue
      //   27	6	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   8	20	27	finally
    }
  }
  
  private static class WGLGetConfigInfo
    implements Runnable
  {
    private int screen;
    private int pixfmt;
    private long cfginfo;
    
    private WGLGetConfigInfo(int paramInt1, int paramInt2)
    {
      screen = paramInt1;
      pixfmt = paramInt2;
    }
    
    public void run()
    {
      cfginfo = WGLGraphicsConfig.getWGLConfigInfo(screen, pixfmt);
    }
    
    public long getConfigInfo()
    {
      return cfginfo;
    }
  }
  
  private static class WGLImageCaps
    extends ImageCapabilities
  {
    private WGLImageCaps()
    {
      super();
    }
    
    public boolean isTrueVolatile()
    {
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\WGLGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */