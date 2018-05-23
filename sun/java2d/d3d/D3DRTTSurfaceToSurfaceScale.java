package sun.java2d.d3d;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.ScaledBlit;
import sun.java2d.pipe.Region;

class D3DRTTSurfaceToSurfaceScale
  extends ScaledBlit
{
  D3DRTTSurfaceToSurfaceScale()
  {
    super(D3DSurfaceData.D3DSurfaceRTT, CompositeType.AnyAlpha, D3DSurfaceData.D3DSurface);
  }
  
  public void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    D3DBlitLoops.IsoBlit(paramSurfaceData1, paramSurfaceData2, null, null, paramComposite, paramRegion, null, 1, paramInt1, paramInt2, paramInt3, paramInt4, paramDouble1, paramDouble2, paramDouble3, paramDouble4, true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DRTTSurfaceToSurfaceScale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */