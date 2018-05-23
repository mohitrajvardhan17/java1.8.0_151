package sun.java2d.opengl;

import java.awt.Composite;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderBuffer;

final class OGLSurfaceToSwBlit
  extends Blit
{
  private final int typeval;
  private WeakReference<SurfaceData> srcTmp;
  
  OGLSurfaceToSwBlit(SurfaceType paramSurfaceType, int paramInt)
  {
    super(OGLSurfaceData.OpenGLSurface, CompositeType.SrcNoEa, paramSurfaceType);
    typeval = paramInt;
  }
  
  private synchronized void complexClipBlit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    SurfaceData localSurfaceData = null;
    if (srcTmp != null) {
      localSurfaceData = (SurfaceData)srcTmp.get();
    }
    int i = typeval == 1 ? 3 : 2;
    paramSurfaceData1 = convertFrom(this, paramSurfaceData1, paramInt1, paramInt2, paramInt5, paramInt6, localSurfaceData, i);
    Blit localBlit = Blit.getFromCache(paramSurfaceData1.getSurfaceType(), CompositeType.SrcNoEa, paramSurfaceData2.getSurfaceType());
    localBlit.Blit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6);
    if (paramSurfaceData1 != localSurfaceData) {
      srcTmp = new WeakReference(paramSurfaceData1);
    }
  }
  
  public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if (paramRegion != null)
    {
      paramRegion = paramRegion.getIntersectionXYWH(paramInt3, paramInt4, paramInt5, paramInt6);
      if (paramRegion.isEmpty()) {
        return;
      }
      paramInt1 += paramRegion.getLoX() - paramInt3;
      paramInt2 += paramRegion.getLoY() - paramInt4;
      paramInt3 = paramRegion.getLoX();
      paramInt4 = paramRegion.getLoY();
      paramInt5 = paramRegion.getWidth();
      paramInt6 = paramRegion.getHeight();
      if (!paramRegion.isRectangular())
      {
        complexClipBlit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        return;
      }
    }
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.lock();
    try
    {
      localOGLRenderQueue.addReference(paramSurfaceData2);
      RenderBuffer localRenderBuffer = localOGLRenderQueue.getBuffer();
      OGLContext.validateContext((OGLSurfaceData)paramSurfaceData1);
      localOGLRenderQueue.ensureCapacityAndAlignment(48, 32);
      localRenderBuffer.putInt(34);
      localRenderBuffer.putInt(paramInt1).putInt(paramInt2);
      localRenderBuffer.putInt(paramInt3).putInt(paramInt4);
      localRenderBuffer.putInt(paramInt5).putInt(paramInt6);
      localRenderBuffer.putInt(typeval);
      localRenderBuffer.putLong(paramSurfaceData1.getNativeOps());
      localRenderBuffer.putLong(paramSurfaceData2.getNativeOps());
      localOGLRenderQueue.flushNow();
    }
    finally
    {
      localOGLRenderQueue.unlock();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLSurfaceToSwBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */