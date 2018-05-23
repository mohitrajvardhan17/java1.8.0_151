package sun.java2d.loops;

import java.awt.geom.Path2D.Float;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorFillPathANY
  extends FillPath
{
  XorFillPathANY()
  {
    super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
  }
  
  public void FillPath(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, Path2D.Float paramFloat)
  {
    PixelWriter localPixelWriter = GeneralRenderer.createXorPixelWriter(paramSunGraphics2D, paramSurfaceData);
    ProcessPath.fillPath(new PixelWriterDrawHandler(paramSurfaceData, localPixelWriter, paramSunGraphics2D.getCompClip(), strokeHint), paramFloat, paramInt1, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\XorFillPathANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */