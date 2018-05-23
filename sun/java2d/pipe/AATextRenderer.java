package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.DrawGlyphListAA;
import sun.java2d.loops.RenderLoops;

public class AATextRenderer
  extends GlyphListLoopPipe
  implements LoopBasedPipe
{
  public AATextRenderer() {}
  
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
  {
    loops.drawGlyphListAALoop.DrawGlyphListAA(paramSunGraphics2D, surfaceData, paramGlyphList);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\AATextRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */