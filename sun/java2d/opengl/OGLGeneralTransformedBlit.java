package sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.Region;

final class OGLGeneralTransformedBlit
  extends TransformBlit
{
  private final TransformBlit performop;
  private WeakReference<SurfaceData> srcTmp;
  
  OGLGeneralTransformedBlit(TransformBlit paramTransformBlit)
  {
    super(SurfaceType.Any, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
    performop = paramTransformBlit;
  }
  
  public synchronized void Transform(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    Blit localBlit = Blit.getFromCache(paramSurfaceData1.getSurfaceType(), CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
    SurfaceData localSurfaceData = srcTmp != null ? (SurfaceData)srcTmp.get() : null;
    paramSurfaceData1 = convertFrom(localBlit, paramSurfaceData1, paramInt2, paramInt3, paramInt6, paramInt7, localSurfaceData, 3);
    performop.Transform(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramAffineTransform, paramInt1, 0, 0, paramInt4, paramInt5, paramInt6, paramInt7);
    if (paramSurfaceData1 != localSurfaceData) {
      srcTmp = new WeakReference(paramSurfaceData1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLGeneralTransformedBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */