package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorDrawLineANY
  extends DrawLine
{
  XorDrawLineANY()
  {
    super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
  }
  
  public void DrawLine(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    PixelWriter localPixelWriter = GeneralRenderer.createXorPixelWriter(paramSunGraphics2D, paramSurfaceData);
    if (paramInt2 >= paramInt4) {
      GeneralRenderer.doDrawLine(paramSurfaceData, localPixelWriter, null, paramSunGraphics2D.getCompClip(), paramInt3, paramInt4, paramInt1, paramInt2);
    } else {
      GeneralRenderer.doDrawLine(paramSurfaceData, localPixelWriter, null, paramSunGraphics2D.getCompClip(), paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\XorDrawLineANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */