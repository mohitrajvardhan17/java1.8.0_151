package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.DrawGlyphList;
import sun.java2d.loops.DrawGlyphListAA;
import sun.java2d.loops.DrawGlyphListLCD;
import sun.java2d.loops.RenderLoops;

public abstract class GlyphListLoopPipe
  extends GlyphListPipe
  implements LoopBasedPipe
{
  public GlyphListLoopPipe() {}
  
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList, int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      loops.drawGlyphListLoop.DrawGlyphList(paramSunGraphics2D, surfaceData, paramGlyphList);
      return;
    case 2: 
      loops.drawGlyphListAALoop.DrawGlyphListAA(paramSunGraphics2D, surfaceData, paramGlyphList);
      return;
    case 4: 
    case 6: 
      loops.drawGlyphListLCDLoop.DrawGlyphListLCD(paramSunGraphics2D, surfaceData, paramGlyphList);
      return;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\GlyphListLoopPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */