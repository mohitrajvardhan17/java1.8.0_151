package sun.java2d.loops;

import java.awt.geom.Path2D.Float;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class SetDrawPathANY
  extends DrawPath
{
  SetDrawPathANY()
  {
    super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
  }
  
  public void DrawPath(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, Path2D.Float paramFloat)
  {
    PixelWriter localPixelWriter = GeneralRenderer.createSolidPixelWriter(paramSunGraphics2D, paramSurfaceData);
    ProcessPath.drawPath(new PixelWriterDrawHandler(paramSurfaceData, localPixelWriter, paramSunGraphics2D.getCompClip(), strokeHint), paramFloat, paramInt1, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\SetDrawPathANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */