package sun.java2d.opengl;

import java.awt.Graphics;
import java.awt.Rectangle;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class OGLUtilities
{
  public static final int UNDEFINED = 0;
  public static final int WINDOW = 1;
  public static final int PBUFFER = 2;
  public static final int TEXTURE = 3;
  public static final int FLIP_BACKBUFFER = 4;
  public static final int FBOBJECT = 5;
  
  private OGLUtilities() {}
  
  public static boolean isQueueFlusherThread()
  {
    return OGLRenderQueue.isQueueFlusherThread();
  }
  
  public static boolean invokeWithOGLContextCurrent(Graphics paramGraphics, Runnable paramRunnable)
  {
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.lock();
    try
    {
      if (paramGraphics != null)
      {
        if (!(paramGraphics instanceof SunGraphics2D))
        {
          boolean bool1 = false;
          return bool1;
        }
        SurfaceData localSurfaceData = surfaceData;
        if (!(localSurfaceData instanceof OGLSurfaceData))
        {
          boolean bool2 = false;
          return bool2;
        }
        OGLContext.validateContext((OGLSurfaceData)localSurfaceData);
      }
      localOGLRenderQueue.flushAndInvokeNow(paramRunnable);
      OGLContext.invalidateCurrentContext();
    }
    finally
    {
      localOGLRenderQueue.unlock();
    }
    return true;
  }
  
  /* Error */
  public static boolean invokeWithOGLSharedContextCurrent(java.awt.GraphicsConfiguration paramGraphicsConfiguration, Runnable paramRunnable)
  {
    // Byte code:
    //   0: aload_0
    //   1: instanceof 71
    //   4: ifne +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: invokestatic 127	sun/java2d/opengl/OGLRenderQueue:getInstance	()Lsun/java2d/opengl/OGLRenderQueue;
    //   12: astore_2
    //   13: aload_2
    //   14: invokevirtual 123	sun/java2d/opengl/OGLRenderQueue:lock	()V
    //   17: aload_0
    //   18: checkcast 71	sun/java2d/opengl/OGLGraphicsConfig
    //   21: invokestatic 121	sun/java2d/opengl/OGLContext:setScratchSurface	(Lsun/java2d/opengl/OGLGraphicsConfig;)V
    //   24: aload_2
    //   25: aload_1
    //   26: invokevirtual 126	sun/java2d/opengl/OGLRenderQueue:flushAndInvokeNow	(Ljava/lang/Runnable;)V
    //   29: invokestatic 120	sun/java2d/opengl/OGLContext:invalidateCurrentContext	()V
    //   32: aload_2
    //   33: invokevirtual 124	sun/java2d/opengl/OGLRenderQueue:unlock	()V
    //   36: goto +10 -> 46
    //   39: astore_3
    //   40: aload_2
    //   41: invokevirtual 124	sun/java2d/opengl/OGLRenderQueue:unlock	()V
    //   44: aload_3
    //   45: athrow
    //   46: iconst_1
    //   47: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	paramGraphicsConfiguration	java.awt.GraphicsConfiguration
    //   0	48	1	paramRunnable	Runnable
    //   12	29	2	localOGLRenderQueue	OGLRenderQueue
    //   39	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   17	32	39	finally
  }
  
  public static Rectangle getOGLViewport(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return null;
    }
    SunGraphics2D localSunGraphics2D = (SunGraphics2D)paramGraphics;
    SurfaceData localSurfaceData = surfaceData;
    int i = transX;
    int j = transY;
    Rectangle localRectangle = localSurfaceData.getBounds();
    int k = i;
    int m = height - (j + paramInt2);
    return new Rectangle(k, m, paramInt1, paramInt2);
  }
  
  public static Rectangle getOGLScissorBox(Graphics paramGraphics)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return null;
    }
    SunGraphics2D localSunGraphics2D = (SunGraphics2D)paramGraphics;
    SurfaceData localSurfaceData = surfaceData;
    Region localRegion = localSunGraphics2D.getCompClip();
    if (!localRegion.isRectangular()) {
      return null;
    }
    int i = localRegion.getLoX();
    int j = localRegion.getLoY();
    int k = localRegion.getWidth();
    int m = localRegion.getHeight();
    Rectangle localRectangle = localSurfaceData.getBounds();
    int n = i;
    int i1 = height - (j + m);
    return new Rectangle(n, i1, k, m);
  }
  
  public static Object getOGLSurfaceIdentifier(Graphics paramGraphics)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return null;
    }
    return surfaceData;
  }
  
  public static int getOGLSurfaceType(Graphics paramGraphics)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return 0;
    }
    SurfaceData localSurfaceData = surfaceData;
    if (!(localSurfaceData instanceof OGLSurfaceData)) {
      return 0;
    }
    return ((OGLSurfaceData)localSurfaceData).getType();
  }
  
  public static int getOGLTextureType(Graphics paramGraphics)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return 0;
    }
    SurfaceData localSurfaceData = surfaceData;
    if (!(localSurfaceData instanceof OGLSurfaceData)) {
      return 0;
    }
    return ((OGLSurfaceData)localSurfaceData).getTextureTarget();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */