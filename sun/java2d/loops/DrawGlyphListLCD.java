package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawGlyphListLCD
  extends GraphicsPrimitive
{
  public static final String methodSignature = "DrawGlyphListLCD(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawGlyphListLCD locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (DrawGlyphListLCD)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected DrawGlyphListLCD(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public DrawGlyphListLCD(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void DrawGlyphListLCD(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return null;
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceDrawGlyphListLCD(this);
  }
  
  static
  {
    GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphListLCD(null, null, null));
  }
  
  private static class TraceDrawGlyphListLCD
    extends DrawGlyphListLCD
  {
    DrawGlyphListLCD target;
    
    public TraceDrawGlyphListLCD(DrawGlyphListLCD paramDrawGlyphListLCD)
    {
      super(paramDrawGlyphListLCD.getCompositeType(), paramDrawGlyphListLCD.getDestType());
      target = paramDrawGlyphListLCD;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void DrawGlyphListLCD(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList)
    {
      tracePrimitive(target);
      target.DrawGlyphListLCD(paramSunGraphics2D, paramSurfaceData, paramGlyphList);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\DrawGlyphListLCD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */