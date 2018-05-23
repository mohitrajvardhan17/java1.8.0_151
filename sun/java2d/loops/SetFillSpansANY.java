package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.SpanIterator;

class SetFillSpansANY
  extends FillSpans
{
  SetFillSpansANY()
  {
    super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
  }
  
  public void FillSpans(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, SpanIterator paramSpanIterator)
  {
    PixelWriter localPixelWriter = GeneralRenderer.createSolidPixelWriter(paramSunGraphics2D, paramSurfaceData);
    int[] arrayOfInt = new int[4];
    while (paramSpanIterator.nextSpan(arrayOfInt)) {
      GeneralRenderer.doSetRect(paramSurfaceData, localPixelWriter, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\SetFillSpansANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */