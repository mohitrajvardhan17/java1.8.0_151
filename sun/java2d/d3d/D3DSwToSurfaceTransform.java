package sun.java2d.d3d;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.Region;

class D3DSwToSurfaceTransform
  extends TransformBlit
{
  private int typeval;
  
  D3DSwToSurfaceTransform(SurfaceType paramSurfaceType, int paramInt)
  {
    super(paramSurfaceType, CompositeType.AnyAlpha, D3DSurfaceData.D3DSurface);
    typeval = paramInt;
  }
  
  public void Transform(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    D3DBlitLoops.Blit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramAffineTransform, paramInt1, paramInt2, paramInt3, paramInt2 + paramInt6, paramInt3 + paramInt7, paramInt4, paramInt5, paramInt4 + paramInt6, paramInt5 + paramInt7, typeval, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DSwToSurfaceTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */