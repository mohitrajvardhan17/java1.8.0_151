package sun.java2d.opengl;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

final class OGLAnyCompositeBlit
  extends Blit
{
  private WeakReference<SurfaceData> dstTmp;
  private WeakReference<SurfaceData> srcTmp;
  private final Blit convertsrc;
  private final Blit convertdst;
  private final Blit convertresult;
  
  OGLAnyCompositeBlit(SurfaceType paramSurfaceType, Blit paramBlit1, Blit paramBlit2, Blit paramBlit3)
  {
    super(paramSurfaceType, CompositeType.Any, OGLSurfaceData.OpenGLSurface);
    convertsrc = paramBlit1;
    convertdst = paramBlit2;
    convertresult = paramBlit3;
  }
  
  public synchronized void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if (convertsrc != null)
    {
      localSurfaceData1 = null;
      if (srcTmp != null) {
        localSurfaceData1 = (SurfaceData)srcTmp.get();
      }
      paramSurfaceData1 = convertFrom(convertsrc, paramSurfaceData1, paramInt1, paramInt2, paramInt5, paramInt6, localSurfaceData1, 3);
      if (paramSurfaceData1 != localSurfaceData1) {
        srcTmp = new WeakReference(paramSurfaceData1);
      }
    }
    SurfaceData localSurfaceData1 = null;
    if (dstTmp != null) {
      localSurfaceData1 = (SurfaceData)dstTmp.get();
    }
    SurfaceData localSurfaceData2 = convertFrom(convertdst, paramSurfaceData2, paramInt3, paramInt4, paramInt5, paramInt6, localSurfaceData1, 3);
    Region localRegion = paramRegion == null ? null : paramRegion.getTranslatedRegion(-paramInt3, -paramInt4);
    Blit localBlit = Blit.getFromCache(paramSurfaceData1.getSurfaceType(), CompositeType.Any, localSurfaceData2.getSurfaceType());
    localBlit.Blit(paramSurfaceData1, localSurfaceData2, paramComposite, localRegion, paramInt1, paramInt2, 0, 0, paramInt5, paramInt6);
    if (localSurfaceData2 != localSurfaceData1) {
      dstTmp = new WeakReference(localSurfaceData2);
    }
    convertresult.Blit(localSurfaceData2, paramSurfaceData2, AlphaComposite.Src, paramRegion, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLAnyCompositeBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */