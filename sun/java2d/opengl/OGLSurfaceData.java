package sun.java2d.opengl;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.security.AccessController;
import sun.awt.image.PixelConverter.ArgbPre;
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
import sun.security.action.GetPropertyAction;

public abstract class OGLSurfaceData
  extends SurfaceData
  implements AccelSurface
{
  public static final int PBUFFER = 2;
  public static final int FBOBJECT = 5;
  public static final int PF_INT_ARGB = 0;
  public static final int PF_INT_ARGB_PRE = 1;
  public static final int PF_INT_RGB = 2;
  public static final int PF_INT_RGBX = 3;
  public static final int PF_INT_BGR = 4;
  public static final int PF_INT_BGRX = 5;
  public static final int PF_USHORT_565_RGB = 6;
  public static final int PF_USHORT_555_RGB = 7;
  public static final int PF_USHORT_555_RGBX = 8;
  public static final int PF_BYTE_GRAY = 9;
  public static final int PF_USHORT_GRAY = 10;
  public static final int PF_3BYTE_BGR = 11;
  private static final String DESC_OPENGL_SURFACE = "OpenGL Surface";
  private static final String DESC_OPENGL_SURFACE_RTT = "OpenGL Surface (render-to-texture)";
  private static final String DESC_OPENGL_TEXTURE = "OpenGL Texture";
  static final SurfaceType OpenGLSurface = SurfaceType.Any.deriveSubType("OpenGL Surface", PixelConverter.ArgbPre.instance);
  static final SurfaceType OpenGLSurfaceRTT = OpenGLSurface.deriveSubType("OpenGL Surface (render-to-texture)");
  static final SurfaceType OpenGLTexture = SurfaceType.Any.deriveSubType("OpenGL Texture");
  private static boolean isFBObjectEnabled;
  private static boolean isLCDShaderEnabled;
  private static boolean isBIOpShaderEnabled;
  private static boolean isGradShaderEnabled;
  private OGLGraphicsConfig graphicsConfig;
  protected int type;
  private int nativeWidth;
  private int nativeHeight;
  protected static OGLRenderer oglRenderPipe;
  protected static PixelToParallelogramConverter oglTxRenderPipe;
  protected static ParallelogramPipe oglAAPgramPipe;
  protected static OGLTextRenderer oglTextPipe;
  protected static OGLDrawImage oglImagePipe;
  
  protected native boolean initTexture(long paramLong, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, int paramInt2);
  
  protected native boolean initFBObject(long paramLong, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, int paramInt2);
  
  protected native boolean initFlipBackbuffer(long paramLong);
  
  protected abstract boolean initPbuffer(long paramLong1, long paramLong2, boolean paramBoolean, int paramInt1, int paramInt2);
  
  private native int getTextureTarget(long paramLong);
  
  private native int getTextureID(long paramLong);
  
  protected OGLSurfaceData(OGLGraphicsConfig paramOGLGraphicsConfig, ColorModel paramColorModel, int paramInt)
  {
    super(getCustomSurfaceType(paramInt), paramColorModel);
    graphicsConfig = paramOGLGraphicsConfig;
    type = paramInt;
    setBlitProxyKey(paramOGLGraphicsConfig.getProxyKey());
  }
  
  public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData)
  {
    return OGLSurfaceDataProxy.createProxy(paramSurfaceData, graphicsConfig);
  }
  
  private static SurfaceType getCustomSurfaceType(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
      return OpenGLTexture;
    case 5: 
      return OpenGLSurfaceRTT;
    }
    return OpenGLSurface;
  }
  
  private void initSurfaceNow(int paramInt1, int paramInt2)
  {
    boolean bool1 = getTransparency() == 1;
    boolean bool2 = false;
    switch (type)
    {
    case 2: 
      bool2 = initPbuffer(getNativeOps(), graphicsConfig.getNativeConfigInfo(), bool1, paramInt1, paramInt2);
      break;
    case 3: 
      bool2 = initTexture(getNativeOps(), bool1, isTexNonPow2Available(), isTexRectAvailable(), paramInt1, paramInt2);
      break;
    case 5: 
      bool2 = initFBObject(getNativeOps(), bool1, isTexNonPow2Available(), isTexRectAvailable(), paramInt1, paramInt2);
      break;
    case 4: 
      bool2 = initFlipBackbuffer(getNativeOps());
      break;
    }
    if (!bool2) {
      throw new OutOfMemoryError("can't create offscreen surface");
    }
  }
  
  protected void initSurface(final int paramInt1, final int paramInt2)
  {
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.lock();
    try
    {
      switch (type)
      {
      case 2: 
      case 3: 
      case 5: 
        OGLContext.setScratchSurface(graphicsConfig);
        break;
      }
      localOGLRenderQueue.flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          OGLSurfaceData.this.initSurfaceNow(paramInt1, paramInt2);
        }
      });
    }
    finally
    {
      localOGLRenderQueue.unlock();
    }
  }
  
  public final OGLContext getContext()
  {
    return graphicsConfig.getContext();
  }
  
  final OGLGraphicsConfig getOGLGraphicsConfig()
  {
    return graphicsConfig;
  }
  
  public final int getType()
  {
    return type;
  }
  
  public final int getTextureTarget()
  {
    return getTextureTarget(getNativeOps());
  }
  
  public final int getTextureID()
  {
    return getTextureID(getNativeOps());
  }
  
  public long getNativeResource(int paramInt)
  {
    if (paramInt == 3) {
      return getTextureID();
    }
    return 0L;
  }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    throw new InternalError("not implemented yet");
  }
  
  public boolean canRenderLCDText(SunGraphics2D paramSunGraphics2D)
  {
    return (graphicsConfig.isCapPresent(131072)) && (surfaceData.getTransparency() == 1) && (paintState <= 0) && ((compositeState <= 0) || ((compositeState <= 1) && (canHandleComposite(composite))));
  }
  
  private boolean canHandleComposite(Composite paramComposite)
  {
    if ((paramComposite instanceof AlphaComposite))
    {
      AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
      return (localAlphaComposite.getRule() == 3) && (localAlphaComposite.getAlpha() >= 1.0F);
    }
    return false;
  }
  
  public void validatePipe(SunGraphics2D paramSunGraphics2D)
  {
    int i = 0;
    Object localObject;
    if (((compositeState <= 0) && (paintState <= 1)) || ((compositeState == 1) && (paintState <= 1) && (((AlphaComposite)composite).getRule() == 3)) || ((compositeState == 2) && (paintState <= 1)))
    {
      localObject = oglTextPipe;
    }
    else
    {
      super.validatePipe(paramSunGraphics2D);
      localObject = textpipe;
      i = 1;
    }
    PixelToParallelogramConverter localPixelToParallelogramConverter1 = null;
    OGLRenderer localOGLRenderer = null;
    if (antialiasHint != 2)
    {
      if (paintState <= 1)
      {
        if (compositeState <= 2)
        {
          localPixelToParallelogramConverter1 = oglTxRenderPipe;
          localOGLRenderer = oglRenderPipe;
        }
      }
      else if ((compositeState <= 1) && (OGLPaints.isValid(paramSunGraphics2D)))
      {
        localPixelToParallelogramConverter1 = oglTxRenderPipe;
        localOGLRenderer = oglRenderPipe;
      }
    }
    else if (paintState <= 1) {
      if ((graphicsConfig.isCapPresent(256)) && ((imageComp == CompositeType.SrcOverNoEa) || (imageComp == CompositeType.SrcOver)))
      {
        if (i == 0)
        {
          super.validatePipe(paramSunGraphics2D);
          i = 1;
        }
        PixelToParallelogramConverter localPixelToParallelogramConverter2 = new PixelToParallelogramConverter(shapepipe, oglAAPgramPipe, 0.125D, 0.499D, false);
        drawpipe = localPixelToParallelogramConverter2;
        fillpipe = localPixelToParallelogramConverter2;
        shapepipe = localPixelToParallelogramConverter2;
      }
      else if (compositeState == 2)
      {
        localPixelToParallelogramConverter1 = oglTxRenderPipe;
        localOGLRenderer = oglRenderPipe;
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
        fillpipe = localOGLRenderer;
      }
      else
      {
        drawpipe = localOGLRenderer;
        fillpipe = localOGLRenderer;
      }
      shapepipe = localPixelToParallelogramConverter1;
    }
    else if (i == 0)
    {
      super.validatePipe(paramSunGraphics2D);
    }
    textpipe = ((TextPipe)localObject);
    imagepipe = oglImagePipe;
  }
  
  protected MaskFill getMaskFill(SunGraphics2D paramSunGraphics2D)
  {
    if ((paintState > 1) && ((!OGLPaints.isValid(paramSunGraphics2D)) || (!graphicsConfig.isCapPresent(16)))) {
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
      oglRenderPipe.copyArea(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      return true;
    }
    return false;
  }
  
  /* Error */
  public void flush()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 452	sun/java2d/opengl/OGLSurfaceData:invalidate	()V
    //   4: invokestatic 443	sun/java2d/opengl/OGLRenderQueue:getInstance	()Lsun/java2d/opengl/OGLRenderQueue;
    //   7: astore_1
    //   8: aload_1
    //   9: invokevirtual 439	sun/java2d/opengl/OGLRenderQueue:lock	()V
    //   12: aload_0
    //   13: getfield 411	sun/java2d/opengl/OGLSurfaceData:graphicsConfig	Lsun/java2d/opengl/OGLGraphicsConfig;
    //   16: invokestatic 433	sun/java2d/opengl/OGLContext:setScratchSurface	(Lsun/java2d/opengl/OGLGraphicsConfig;)V
    //   19: aload_1
    //   20: invokevirtual 444	sun/java2d/opengl/OGLRenderQueue:getBuffer	()Lsun/java2d/pipe/RenderBuffer;
    //   23: astore_2
    //   24: aload_1
    //   25: bipush 12
    //   27: iconst_4
    //   28: invokevirtual 441	sun/java2d/opengl/OGLRenderQueue:ensureCapacityAndAlignment	(II)V
    //   31: aload_2
    //   32: bipush 72
    //   34: invokevirtual 471	sun/java2d/pipe/RenderBuffer:putInt	(I)Lsun/java2d/pipe/RenderBuffer;
    //   37: pop
    //   38: aload_2
    //   39: aload_0
    //   40: invokevirtual 451	sun/java2d/opengl/OGLSurfaceData:getNativeOps	()J
    //   43: invokevirtual 472	sun/java2d/pipe/RenderBuffer:putLong	(J)Lsun/java2d/pipe/RenderBuffer;
    //   46: pop
    //   47: aload_1
    //   48: invokevirtual 438	sun/java2d/opengl/OGLRenderQueue:flushNow	()V
    //   51: aload_1
    //   52: invokevirtual 440	sun/java2d/opengl/OGLRenderQueue:unlock	()V
    //   55: goto +10 -> 65
    //   58: astore_3
    //   59: aload_1
    //   60: invokevirtual 440	sun/java2d/opengl/OGLRenderQueue:unlock	()V
    //   63: aload_3
    //   64: athrow
    //   65: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	OGLSurfaceData
    //   7	53	1	localOGLRenderQueue	OGLRenderQueue
    //   23	16	2	localRenderBuffer	RenderBuffer
    //   58	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   12	51	58	finally
  }
  
  static void dispose(long paramLong1, long paramLong2)
  {
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.lock();
    try
    {
      OGLContext.setScratchSurface(paramLong2);
      RenderBuffer localRenderBuffer = localOGLRenderQueue.getBuffer();
      localOGLRenderQueue.ensureCapacityAndAlignment(12, 4);
      localRenderBuffer.putInt(73);
      localRenderBuffer.putLong(paramLong1);
      localOGLRenderQueue.flushNow();
    }
    finally
    {
      localOGLRenderQueue.unlock();
    }
  }
  
  static void swapBuffers(long paramLong)
  {
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.lock();
    try
    {
      RenderBuffer localRenderBuffer = localOGLRenderQueue.getBuffer();
      localOGLRenderQueue.ensureCapacityAndAlignment(12, 4);
      localRenderBuffer.putInt(80);
      localRenderBuffer.putLong(paramLong);
      localOGLRenderQueue.flushNow();
    }
    finally
    {
      localOGLRenderQueue.unlock();
    }
  }
  
  boolean isTexNonPow2Available()
  {
    return graphicsConfig.isCapPresent(32);
  }
  
  boolean isTexRectAvailable()
  {
    return graphicsConfig.isCapPresent(1048576);
  }
  
  public Rectangle getNativeBounds()
  {
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.lock();
    try
    {
      Rectangle localRectangle = new Rectangle(nativeWidth, nativeHeight);
      return localRectangle;
    }
    finally
    {
      localOGLRenderQueue.unlock();
    }
  }
  
  boolean isOnScreen()
  {
    return getType() == 1;
  }
  
  static
  {
    if (!GraphicsEnvironment.isHeadless())
    {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.fbobject"));
      isFBObjectEnabled = !"false".equals(str1);
      String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.lcdshader"));
      isLCDShaderEnabled = !"false".equals(str2);
      String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.biopshader"));
      isBIOpShaderEnabled = !"false".equals(str3);
      String str4 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.gradshader"));
      isGradShaderEnabled = !"false".equals(str4);
      OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
      oglImagePipe = new OGLDrawImage();
      oglTextPipe = new OGLTextRenderer(localOGLRenderQueue);
      oglRenderPipe = new OGLRenderer(localOGLRenderQueue);
      if (GraphicsPrimitive.tracingEnabled()) {
        oglTextPipe = oglTextPipe.traceWrap();
      }
      oglAAPgramPipe = oglRenderPipe.getAAParallelogramPipe();
      oglTxRenderPipe = new PixelToParallelogramConverter(oglRenderPipe, oglRenderPipe, 1.0D, 0.25D, true);
      OGLBlitLoops.register();
      OGLMaskFill.register();
      OGLMaskBlit.register();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */