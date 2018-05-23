package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawLine
  extends GraphicsPrimitive
{
  public static final String methodSignature = "DrawLine(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawLine locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (DrawLine)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected DrawLine(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public DrawLine(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void DrawLine(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    throw new InternalError("DrawLine not implemented for " + paramSurfaceType1 + " with " + paramCompositeType);
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceDrawLine(this);
  }
  
  private static class TraceDrawLine
    extends DrawLine
  {
    DrawLine target;
    
    public TraceDrawLine(DrawLine paramDrawLine)
    {
      super(paramDrawLine.getCompositeType(), paramDrawLine.getDestType());
      target = paramDrawLine;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void DrawLine(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      tracePrimitive(target);
      target.DrawLine(paramSunGraphics2D, paramSurfaceData, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\DrawLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */