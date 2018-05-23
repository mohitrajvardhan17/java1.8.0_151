package sun.java2d.d3d;

import java.awt.AlphaComposite;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import sun.awt.SunToolkit;
import sun.awt.image.DataBufferNative;
import sun.awt.image.PixelConverter.ArgbPre;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.awt.image.WritableRasterNative;
import sun.awt.windows.WComponentPeer;
import sun.java2d.InvalidPipeException;
import sun.java2d.ScreenUpdateManager;
import sun.java2d.StateTracker;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities.VSyncType;

public class D3DSurfaceData
  extends SurfaceData
  implements AccelSurface
{
  public static final int D3D_DEVICE_RESOURCE = 100;
  public static final int ST_INT_ARGB = 0;
  public static final int ST_INT_ARGB_PRE = 1;
  public static final int ST_INT_ARGB_BM = 2;
  public static final int ST_INT_RGB = 3;
  public static final int ST_INT_BGR = 4;
  public static final int ST_USHORT_565_RGB = 5;
  public static final int ST_USHORT_555_RGB = 6;
  public static final int ST_BYTE_INDEXED = 7;
  public static final int ST_BYTE_INDEXED_BM = 8;
  public static final int ST_3BYTE_BGR = 9;
  public static final int SWAP_DISCARD = 1;
  public static final int SWAP_FLIP = 2;
  public static final int SWAP_COPY = 3;
  private static final String DESC_D3D_SURFACE = "D3D Surface";
  private static final String DESC_D3D_SURFACE_RTT = "D3D Surface (render-to-texture)";
  private static final String DESC_D3D_TEXTURE = "D3D Texture";
  static final SurfaceType D3DSurface = SurfaceType.Any.deriveSubType("D3D Surface", PixelConverter.ArgbPre.instance);
  static final SurfaceType D3DSurfaceRTT = D3DSurface.deriveSubType("D3D Surface (render-to-texture)");
  static final SurfaceType D3DTexture = SurfaceType.Any.deriveSubType("D3D Texture");
  private int type;
  private int width;
  private int height;
  private int nativeWidth;
  private int nativeHeight;
  protected WComponentPeer peer;
  private Image offscreenImage;
  protected D3DGraphicsDevice graphicsDevice;
  private int swapEffect;
  private ExtendedBufferCapabilities.VSyncType syncType;
  private int backBuffersNum;
  private WritableRasterNative wrn;
  protected static D3DRenderer d3dRenderPipe;
  protected static PixelToParallelogramConverter d3dTxRenderPipe;
  protected static ParallelogramPipe d3dAAPgramPipe;
  protected static D3DTextRenderer d3dTextPipe;
  protected static D3DDrawImage d3dImagePipe;
  
  private native boolean initTexture(long paramLong, boolean paramBoolean1, boolean paramBoolean2);
  
  private native boolean initFlipBackbuffer(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3);
  
  private native boolean initRTSurface(long paramLong, boolean paramBoolean);
  
  private native void initOps(int paramInt1, int paramInt2, int paramInt3);
  
  protected D3DSurfaceData(WComponentPeer paramWComponentPeer, D3DGraphicsConfig paramD3DGraphicsConfig, int paramInt1, int paramInt2, Image paramImage, ColorModel paramColorModel, int paramInt3, int paramInt4, ExtendedBufferCapabilities.VSyncType paramVSyncType, int paramInt5)
  {
    super(getCustomSurfaceType(paramInt5), paramColorModel);
    graphicsDevice = paramD3DGraphicsConfig.getD3DDevice();
    peer = paramWComponentPeer;
    type = paramInt5;
    width = paramInt1;
    height = paramInt2;
    offscreenImage = paramImage;
    backBuffersNum = paramInt3;
    swapEffect = paramInt4;
    syncType = paramVSyncType;
    initOps(graphicsDevice.getScreen(), paramInt1, paramInt2);
    if (paramInt5 == 1) {
      setSurfaceLost(true);
    } else {
      initSurface();
    }
    setBlitProxyKey(paramD3DGraphicsConfig.getProxyKey());
  }
  
  public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData)
  {
    return D3DSurfaceDataProxy.createProxy(paramSurfaceData, (D3DGraphicsConfig)graphicsDevice.getDefaultConfiguration());
  }
  
  public static D3DSurfaceData createData(WComponentPeer paramWComponentPeer, Image paramImage)
  {
    D3DGraphicsConfig localD3DGraphicsConfig = getGC(paramWComponentPeer);
    if ((localD3DGraphicsConfig == null) || (!paramWComponentPeer.isAccelCapable())) {
      return null;
    }
    BufferCapabilities localBufferCapabilities = paramWComponentPeer.getBackBufferCaps();
    ExtendedBufferCapabilities.VSyncType localVSyncType = ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT;
    if ((localBufferCapabilities instanceof ExtendedBufferCapabilities)) {
      localVSyncType = ((ExtendedBufferCapabilities)localBufferCapabilities).getVSync();
    }
    Rectangle localRectangle = paramWComponentPeer.getBounds();
    BufferCapabilities.FlipContents localFlipContents = localBufferCapabilities.getFlipContents();
    int i;
    if (localFlipContents == BufferCapabilities.FlipContents.COPIED) {
      i = 3;
    } else if (localFlipContents == BufferCapabilities.FlipContents.PRIOR) {
      i = 2;
    } else {
      i = 1;
    }
    return new D3DSurfaceData(paramWComponentPeer, localD3DGraphicsConfig, width, height, paramImage, paramWComponentPeer.getColorModel(), paramWComponentPeer.getBackBuffersNum(), i, localVSyncType, 4);
  }
  
  public static D3DSurfaceData createData(WComponentPeer paramWComponentPeer)
  {
    D3DGraphicsConfig localD3DGraphicsConfig = getGC(paramWComponentPeer);
    if ((localD3DGraphicsConfig == null) || (!paramWComponentPeer.isAccelCapable())) {
      return null;
    }
    return new D3DWindowSurfaceData(paramWComponentPeer, localD3DGraphicsConfig);
  }
  
  public static D3DSurfaceData createData(D3DGraphicsConfig paramD3DGraphicsConfig, int paramInt1, int paramInt2, ColorModel paramColorModel, Image paramImage, int paramInt3)
  {
    if (paramInt3 == 5)
    {
      int i = paramColorModel.getTransparency() == 1 ? 1 : 0;
      int j = i != 0 ? 8 : 4;
      if (!paramD3DGraphicsConfig.getD3DDevice().isCapPresent(j)) {
        paramInt3 = 2;
      }
    }
    D3DSurfaceData localD3DSurfaceData = null;
    try
    {
      localD3DSurfaceData = new D3DSurfaceData(null, paramD3DGraphicsConfig, paramInt1, paramInt2, paramImage, paramColorModel, 0, 1, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, paramInt3);
    }
    catch (InvalidPipeException localInvalidPipeException)
    {
      if ((paramInt3 == 5) && (((SunVolatileImage)paramImage).getForcedAccelSurfaceType() != 5))
      {
        paramInt3 = 2;
        localD3DSurfaceData = new D3DSurfaceData(null, paramD3DGraphicsConfig, paramInt1, paramInt2, paramImage, paramColorModel, 0, 1, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, paramInt3);
      }
    }
    return localD3DSurfaceData;
  }
  
  private static SurfaceType getCustomSurfaceType(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
      return D3DTexture;
    case 5: 
      return D3DSurfaceRTT;
    }
    return D3DSurface;
  }
  
  private boolean initSurfaceNow()
  {
    boolean bool = getTransparency() == 1;
    switch (type)
    {
    case 2: 
      return initRTSurface(getNativeOps(), bool);
    case 3: 
      return initTexture(getNativeOps(), false, bool);
    case 5: 
      return initTexture(getNativeOps(), true, bool);
    case 1: 
    case 4: 
      return initFlipBackbuffer(getNativeOps(), peer.getData(), backBuffersNum, swapEffect, syncType.id());
    }
    return false;
  }
  
  /* Error */
  protected void initSurface()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: aconst_null
    //   6: putfield 542	sun/java2d/d3d/D3DSurfaceData:wrn	Lsun/awt/image/WritableRasterNative;
    //   9: aload_1
    //   10: monitorexit
    //   11: goto +8 -> 19
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    //   19: new 296	sun/java2d/d3d/D3DSurfaceData$1Status
    //   22: dup
    //   23: aload_0
    //   24: invokespecial 634	sun/java2d/d3d/D3DSurfaceData$1Status:<init>	(Lsun/java2d/d3d/D3DSurfaceData;)V
    //   27: astore_1
    //   28: invokestatic 606	sun/java2d/d3d/D3DRenderQueue:getInstance	()Lsun/java2d/d3d/D3DRenderQueue;
    //   31: astore_2
    //   32: aload_2
    //   33: invokevirtual 600	sun/java2d/d3d/D3DRenderQueue:lock	()V
    //   36: aload_2
    //   37: new 295	sun/java2d/d3d/D3DSurfaceData$1
    //   40: dup
    //   41: aload_0
    //   42: aload_1
    //   43: invokespecial 633	sun/java2d/d3d/D3DSurfaceData$1:<init>	(Lsun/java2d/d3d/D3DSurfaceData;Lsun/java2d/d3d/D3DSurfaceData$1Status;)V
    //   46: invokevirtual 605	sun/java2d/d3d/D3DRenderQueue:flushAndInvokeNow	(Ljava/lang/Runnable;)V
    //   49: aload_1
    //   50: getfield 554	sun/java2d/d3d/D3DSurfaceData$1Status:success	Z
    //   53: ifne +13 -> 66
    //   56: new 280	sun/java2d/InvalidPipeException
    //   59: dup
    //   60: ldc 6
    //   62: invokespecial 582	sun/java2d/InvalidPipeException:<init>	(Ljava/lang/String;)V
    //   65: athrow
    //   66: aload_2
    //   67: invokevirtual 601	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   70: goto +10 -> 80
    //   73: astore_3
    //   74: aload_2
    //   75: invokevirtual 601	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   78: aload_3
    //   79: athrow
    //   80: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	D3DSurfaceData
    //   2	48	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    //   31	44	2	localD3DRenderQueue	D3DRenderQueue
    //   73	6	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   4	11	14	finally
    //   14	17	14	finally
    //   36	66	73	finally
  }
  
  public final D3DContext getContext()
  {
    return graphicsDevice.getContext();
  }
  
  public final int getType()
  {
    return type;
  }
  
  private static native int dbGetPixelNative(long paramLong, int paramInt1, int paramInt2);
  
  private static native void dbSetPixelNative(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public synchronized Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (wrn == null)
    {
      DirectColorModel localDirectColorModel = (DirectColorModel)getColorModel();
      int i = 0;
      int j = width;
      if (localDirectColorModel.getPixelSize() > 16) {
        i = 3;
      } else {
        i = 1;
      }
      SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = new SinglePixelPackedSampleModel(i, width, height, j, localDirectColorModel.getMasks());
      D3DDataBufferNative localD3DDataBufferNative = new D3DDataBufferNative(this, i, width, height);
      wrn = WritableRasterNative.createNativeRaster(localSinglePixelPackedSampleModel, localD3DDataBufferNative);
    }
    return wrn;
  }
  
  public boolean canRenderLCDText(SunGraphics2D paramSunGraphics2D)
  {
    return (graphicsDevice.isCapPresent(65536)) && (compositeState <= 0) && (paintState <= 0) && (surfaceData.getTransparency() == 1);
  }
  
  void disableAccelerationForSurface()
  {
    if (offscreenImage != null)
    {
      SurfaceManager localSurfaceManager = SurfaceManager.getManager(offscreenImage);
      if ((localSurfaceManager instanceof D3DVolatileSurfaceManager))
      {
        setSurfaceLost(true);
        ((D3DVolatileSurfaceManager)localSurfaceManager).setAccelerationEnabled(false);
      }
    }
  }
  
  public void validatePipe(SunGraphics2D paramSunGraphics2D)
  {
    int i = 0;
    if (compositeState >= 2)
    {
      super.validatePipe(paramSunGraphics2D);
      imagepipe = d3dImagePipe;
      disableAccelerationForSurface();
      return;
    }
    Object localObject;
    if (((compositeState <= 0) && (paintState <= 1)) || ((compositeState == 1) && (paintState <= 1) && (((AlphaComposite)composite).getRule() == 3)) || ((compositeState == 2) && (paintState <= 1)))
    {
      localObject = d3dTextPipe;
    }
    else
    {
      super.validatePipe(paramSunGraphics2D);
      localObject = textpipe;
      i = 1;
    }
    PixelToParallelogramConverter localPixelToParallelogramConverter1 = null;
    D3DRenderer localD3DRenderer = null;
    if (antialiasHint != 2)
    {
      if (paintState <= 1)
      {
        if (compositeState <= 2)
        {
          localPixelToParallelogramConverter1 = d3dTxRenderPipe;
          localD3DRenderer = d3dRenderPipe;
        }
      }
      else if ((compositeState <= 1) && (D3DPaints.isValid(paramSunGraphics2D)))
      {
        localPixelToParallelogramConverter1 = d3dTxRenderPipe;
        localD3DRenderer = d3dRenderPipe;
      }
    }
    else if (paintState <= 1) {
      if ((graphicsDevice.isCapPresent(524288)) && ((imageComp == CompositeType.SrcOverNoEa) || (imageComp == CompositeType.SrcOver)))
      {
        if (i == 0)
        {
          super.validatePipe(paramSunGraphics2D);
          i = 1;
        }
        PixelToParallelogramConverter localPixelToParallelogramConverter2 = new PixelToParallelogramConverter(shapepipe, d3dAAPgramPipe, 0.125D, 0.499D, false);
        drawpipe = localPixelToParallelogramConverter2;
        fillpipe = localPixelToParallelogramConverter2;
        shapepipe = localPixelToParallelogramConverter2;
      }
      else if (compositeState == 2)
      {
        localPixelToParallelogramConverter1 = d3dTxRenderPipe;
        localD3DRenderer = d3dRenderPipe;
      }
    }
    if (localPixelToParallelogramConverter1 != null)
    {
      if (transformState >= 3)
      {
        drawpipe = localPixelToParallelogramConverter1;
        fillpipe = localPixelToParallelogramConverter1;
      }
      else if (strokeState != 0)
      {
        drawpipe = localPixelToParallelogramConverter1;
        fillpipe = localD3DRenderer;
      }
      else
      {
        drawpipe = localD3DRenderer;
        fillpipe = localD3DRenderer;
      }
      shapepipe = localPixelToParallelogramConverter1;
    }
    else if (i == 0)
    {
      super.validatePipe(paramSunGraphics2D);
    }
    textpipe = ((TextPipe)localObject);
    imagepipe = d3dImagePipe;
  }
  
  protected MaskFill getMaskFill(SunGraphics2D paramSunGraphics2D)
  {
    if ((paintState > 1) && ((!D3DPaints.isValid(paramSunGraphics2D)) || (!graphicsDevice.isCapPresent(16)))) {
      return null;
    }
    return super.getMaskFill(paramSunGraphics2D);
  }
  
  public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if ((transformState < 3) && (compositeState < 2))
    {
      paramInt1 += transX;
      paramInt2 += transY;
      d3dRenderPipe.copyArea(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      return true;
    }
    return false;
  }
  
  /* Error */
  public void flush()
  {
    // Byte code:
    //   0: invokestatic 606	sun/java2d/d3d/D3DRenderQueue:getInstance	()Lsun/java2d/d3d/D3DRenderQueue;
    //   3: astore_1
    //   4: aload_1
    //   5: invokevirtual 600	sun/java2d/d3d/D3DRenderQueue:lock	()V
    //   8: aload_1
    //   9: invokevirtual 607	sun/java2d/d3d/D3DRenderQueue:getBuffer	()Lsun/java2d/pipe/RenderBuffer;
    //   12: astore_2
    //   13: aload_1
    //   14: bipush 12
    //   16: iconst_4
    //   17: invokevirtual 604	sun/java2d/d3d/D3DRenderQueue:ensureCapacityAndAlignment	(II)V
    //   20: aload_2
    //   21: bipush 72
    //   23: invokevirtual 646	sun/java2d/pipe/RenderBuffer:putInt	(I)Lsun/java2d/pipe/RenderBuffer;
    //   26: pop
    //   27: aload_2
    //   28: aload_0
    //   29: invokevirtual 613	sun/java2d/d3d/D3DSurfaceData:getNativeOps	()J
    //   32: invokevirtual 647	sun/java2d/pipe/RenderBuffer:putLong	(J)Lsun/java2d/pipe/RenderBuffer;
    //   35: pop
    //   36: aload_1
    //   37: invokevirtual 599	sun/java2d/d3d/D3DRenderQueue:flushNow	()V
    //   40: aload_1
    //   41: invokevirtual 601	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   44: goto +10 -> 54
    //   47: astore_3
    //   48: aload_1
    //   49: invokevirtual 601	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   52: aload_3
    //   53: athrow
    //   54: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	D3DSurfaceData
    //   3	46	1	localD3DRenderQueue	D3DRenderQueue
    //   12	16	2	localRenderBuffer	RenderBuffer
    //   47	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	40	47	finally
  }
  
  static void dispose(long paramLong)
  {
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    localD3DRenderQueue.lock();
    try
    {
      RenderBuffer localRenderBuffer = localD3DRenderQueue.getBuffer();
      localD3DRenderQueue.ensureCapacityAndAlignment(12, 4);
      localRenderBuffer.putInt(73);
      localRenderBuffer.putLong(paramLong);
      localD3DRenderQueue.flushNow();
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
  }
  
  static void swapBuffers(D3DSurfaceData paramD3DSurfaceData, final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4)
  {
    long l = paramD3DSurfaceData.getNativeOps();
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    Object localObject1;
    if (D3DRenderQueue.isRenderQueueThread())
    {
      if (!localD3DRenderQueue.tryLock())
      {
        localObject1 = (Component)paramD3DSurfaceData.getPeer().getTarget();
        SunToolkit.executeOnEventHandlerThread(localObject1, new Runnable()
        {
          public void run()
          {
            val$target.repaint(paramInt1, paramInt2, paramInt3, paramInt4);
          }
        });
      }
    }
    else {
      localD3DRenderQueue.lock();
    }
    try
    {
      localObject1 = localD3DRenderQueue.getBuffer();
      localD3DRenderQueue.ensureCapacityAndAlignment(28, 4);
      ((RenderBuffer)localObject1).putInt(80);
      ((RenderBuffer)localObject1).putLong(l);
      ((RenderBuffer)localObject1).putInt(paramInt1);
      ((RenderBuffer)localObject1).putInt(paramInt2);
      ((RenderBuffer)localObject1).putInt(paramInt3);
      ((RenderBuffer)localObject1).putInt(paramInt4);
      localD3DRenderQueue.flushNow();
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
  }
  
  public Object getDestination()
  {
    return offscreenImage;
  }
  
  public Rectangle getBounds()
  {
    if ((type == 4) || (type == 1))
    {
      Rectangle localRectangle = peer.getBounds();
      x = (y = 0);
      return localRectangle;
    }
    return new Rectangle(width, height);
  }
  
  public Rectangle getNativeBounds()
  {
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    localD3DRenderQueue.lock();
    try
    {
      Rectangle localRectangle = new Rectangle(nativeWidth, nativeHeight);
      return localRectangle;
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
  }
  
  public GraphicsConfiguration getDeviceConfiguration()
  {
    return graphicsDevice.getDefaultConfiguration();
  }
  
  public SurfaceData getReplacement()
  {
    return restoreContents(offscreenImage);
  }
  
  private static D3DGraphicsConfig getGC(WComponentPeer paramWComponentPeer)
  {
    GraphicsConfiguration localGraphicsConfiguration;
    if (paramWComponentPeer != null)
    {
      localGraphicsConfiguration = paramWComponentPeer.getGraphicsConfiguration();
    }
    else
    {
      GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice localGraphicsDevice = localGraphicsEnvironment.getDefaultScreenDevice();
      localGraphicsConfiguration = localGraphicsDevice.getDefaultConfiguration();
    }
    return (localGraphicsConfiguration instanceof D3DGraphicsConfig) ? (D3DGraphicsConfig)localGraphicsConfiguration : null;
  }
  
  void restoreSurface()
  {
    initSurface();
  }
  
  WComponentPeer getPeer()
  {
    return peer;
  }
  
  public void setSurfaceLost(boolean paramBoolean)
  {
    super.setSurfaceLost(paramBoolean);
    if ((paramBoolean) && (offscreenImage != null))
    {
      SurfaceManager localSurfaceManager = SurfaceManager.getManager(offscreenImage);
      localSurfaceManager.acceleratedSurfaceLost();
    }
  }
  
  private static native long getNativeResourceNative(long paramLong, int paramInt);
  
  public long getNativeResource(int paramInt)
  {
    return getNativeResourceNative(getNativeOps(), paramInt);
  }
  
  public static native boolean updateWindowAccelImpl(long paramLong1, long paramLong2, int paramInt1, int paramInt2);
  
  static
  {
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    d3dImagePipe = new D3DDrawImage();
    d3dTextPipe = new D3DTextRenderer(localD3DRenderQueue);
    d3dRenderPipe = new D3DRenderer(localD3DRenderQueue);
    if (GraphicsPrimitive.tracingEnabled())
    {
      d3dTextPipe = d3dTextPipe.traceWrap();
      d3dRenderPipe = d3dRenderPipe.traceWrap();
    }
    d3dAAPgramPipe = d3dRenderPipe.getAAParallelogramPipe();
    d3dTxRenderPipe = new PixelToParallelogramConverter(d3dRenderPipe, d3dRenderPipe, 1.0D, 0.25D, true);
    D3DBlitLoops.register();
    D3DMaskFill.register();
    D3DMaskBlit.register();
  }
  
  static class D3DDataBufferNative
    extends DataBufferNative
  {
    int pixel;
    
    protected D3DDataBufferNative(SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt1, paramInt2, paramInt3);
    }
    
    protected int getElem(final int paramInt1, final int paramInt2, final SurfaceData paramSurfaceData)
    {
      if (paramSurfaceData.isSurfaceLost()) {
        return 0;
      }
      D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
      localD3DRenderQueue.lock();
      int i;
      try
      {
        localD3DRenderQueue.flushAndInvokeNow(new Runnable()
        {
          public void run()
          {
            pixel = D3DSurfaceData.dbGetPixelNative(paramSurfaceData.getNativeOps(), paramInt1, paramInt2);
          }
        });
      }
      finally
      {
        i = pixel;
        localD3DRenderQueue.unlock();
      }
      return i;
    }
    
    protected void setElem(final int paramInt1, final int paramInt2, final int paramInt3, final SurfaceData paramSurfaceData)
    {
      if (paramSurfaceData.isSurfaceLost()) {
        return;
      }
      D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
      localD3DRenderQueue.lock();
      try
      {
        localD3DRenderQueue.flushAndInvokeNow(new Runnable()
        {
          public void run()
          {
            D3DSurfaceData.dbSetPixelNative(paramSurfaceData.getNativeOps(), paramInt1, paramInt2, paramInt3);
          }
        });
        paramSurfaceData.markDirty();
      }
      finally
      {
        localD3DRenderQueue.unlock();
      }
    }
  }
  
  public static class D3DWindowSurfaceData
    extends D3DSurfaceData
  {
    StateTracker dirtyTracker = getStateTracker();
    
    public D3DWindowSurfaceData(WComponentPeer paramWComponentPeer, D3DGraphicsConfig paramD3DGraphicsConfig)
    {
      super(paramD3DGraphicsConfig, getBoundswidth, getBoundsheight, null, paramWComponentPeer.getColorModel(), 1, 3, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, 1);
    }
    
    public SurfaceData getReplacement()
    {
      ScreenUpdateManager localScreenUpdateManager = ScreenUpdateManager.getInstance();
      return localScreenUpdateManager.getReplacementScreenSurface(peer, this);
    }
    
    public Object getDestination()
    {
      return peer.getTarget();
    }
    
    void disableAccelerationForSurface()
    {
      setSurfaceLost(true);
      invalidate();
      flush();
      peer.disableAcceleration();
      ScreenUpdateManager.getInstance().dropScreenSurface(this);
    }
    
    /* Error */
    void restoreSurface()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 120	sun/java2d/d3d/D3DSurfaceData$D3DWindowSurfaceData:peer	Lsun/awt/windows/WComponentPeer;
      //   4: invokevirtual 125	sun/awt/windows/WComponentPeer:isAccelCapable	()Z
      //   7: ifne +13 -> 20
      //   10: new 63	sun/java2d/InvalidPipeException
      //   13: dup
      //   14: ldc 2
      //   16: invokespecial 129	sun/java2d/InvalidPipeException:<init>	(Ljava/lang/String;)V
      //   19: athrow
      //   20: aload_0
      //   21: getfield 122	sun/java2d/d3d/D3DSurfaceData$D3DWindowSurfaceData:graphicsDevice	Lsun/java2d/d3d/D3DGraphicsDevice;
      //   24: invokevirtual 134	sun/java2d/d3d/D3DGraphicsDevice:getFullScreenWindow	()Ljava/awt/Window;
      //   27: astore_1
      //   28: aload_1
      //   29: ifnull +24 -> 53
      //   32: aload_1
      //   33: aload_0
      //   34: getfield 120	sun/java2d/d3d/D3DSurfaceData$D3DWindowSurfaceData:peer	Lsun/awt/windows/WComponentPeer;
      //   37: invokevirtual 128	sun/awt/windows/WComponentPeer:getTarget	()Ljava/lang/Object;
      //   40: if_acmpeq +13 -> 53
      //   43: new 63	sun/java2d/InvalidPipeException
      //   46: dup
      //   47: ldc 1
      //   49: invokespecial 129	sun/java2d/InvalidPipeException:<init>	(Ljava/lang/String;)V
      //   52: athrow
      //   53: aload_0
      //   54: invokespecial 138	sun/java2d/d3d/D3DSurfaceData:restoreSurface	()V
      //   57: aload_0
      //   58: iconst_0
      //   59: invokevirtual 143	sun/java2d/d3d/D3DSurfaceData$D3DWindowSurfaceData:setSurfaceLost	(Z)V
      //   62: invokestatic 137	sun/java2d/d3d/D3DRenderQueue:getInstance	()Lsun/java2d/d3d/D3DRenderQueue;
      //   65: astore_2
      //   66: aload_2
      //   67: invokevirtual 135	sun/java2d/d3d/D3DRenderQueue:lock	()V
      //   70: aload_0
      //   71: invokevirtual 145	sun/java2d/d3d/D3DSurfaceData$D3DWindowSurfaceData:getContext	()Lsun/java2d/d3d/D3DContext;
      //   74: invokevirtual 133	sun/java2d/d3d/D3DContext:invalidateContext	()V
      //   77: aload_2
      //   78: invokevirtual 136	sun/java2d/d3d/D3DRenderQueue:unlock	()V
      //   81: goto +10 -> 91
      //   84: astore_3
      //   85: aload_2
      //   86: invokevirtual 136	sun/java2d/d3d/D3DRenderQueue:unlock	()V
      //   89: aload_3
      //   90: athrow
      //   91: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	92	0	this	D3DWindowSurfaceData
      //   27	6	1	localWindow	java.awt.Window
      //   65	21	2	localD3DRenderQueue	D3DRenderQueue
      //   84	6	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   70	77	84	finally
    }
    
    public boolean isDirty()
    {
      return !dirtyTracker.isCurrent();
    }
    
    public void markClean()
    {
      dirtyTracker = getStateTracker();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */