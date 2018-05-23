package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorDrawGlyphListAAANY
  extends DrawGlyphListAA
{
  XorDrawGlyphListAAANY()
  {
    super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
  }
  
  public void DrawGlyphListAA(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList)
  {
    PixelWriter localPixelWriter = GeneralRenderer.createXorPixelWriter(paramSunGraphics2D, paramSurfaceData);
    GeneralRenderer.doDrawGlyphList(paramSurfaceData, localPixelWriter, paramGlyphList, paramSunGraphics2D.getCompClip());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\XorDrawGlyphListAAANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */