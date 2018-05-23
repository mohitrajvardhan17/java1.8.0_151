package sun.java2d.d3d;

import java.awt.Color;
import sun.java2d.InvalidPipeException;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;

public class D3DSurfaceDataProxy
  extends SurfaceDataProxy
{
  D3DGraphicsConfig d3dgc;
  int transparency;
  
  public static SurfaceDataProxy createProxy(SurfaceData paramSurfaceData, D3DGraphicsConfig paramD3DGraphicsConfig)
  {
    if ((paramSurfaceData instanceof D3DSurfaceData)) {
      return UNCACHED;
    }
    return new D3DSurfaceDataProxy(paramD3DGraphicsConfig, paramSurfaceData.getTransparency());
  }
  
  public D3DSurfaceDataProxy(D3DGraphicsConfig paramD3DGraphicsConfig, int paramInt)
  {
    d3dgc = paramD3DGraphicsConfig;
    transparency = paramInt;
    activateDisplayListener();
  }
  
  public SurfaceData validateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2)
  {
    if ((paramSurfaceData2 == null) || (paramSurfaceData2.isSurfaceLost())) {
      try
      {
        paramSurfaceData2 = d3dgc.createManagedSurface(paramInt1, paramInt2, transparency);
      }
      catch (InvalidPipeException localInvalidPipeException)
      {
        d3dgc.getD3DDevice();
        if (!D3DGraphicsDevice.isD3DAvailable())
        {
          invalidate();
          flush();
          return null;
        }
      }
    }
    return paramSurfaceData2;
  }
  
  public boolean isSupportedOperation(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor)
  {
    return (paramColor == null) || (transparency == 1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DSurfaceDataProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */