package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawRect
  extends GraphicsPrimitive
{
  public static final String methodSignature = "DrawRect(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawRect locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (DrawRect)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected DrawRect(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public DrawRect(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void DrawRect(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    throw new InternalError("DrawRect not implemented for " + paramSurfaceType1 + " with " + paramCompositeType);
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceDrawRect(this);
  }
  
  private static class TraceDrawRect
    extends DrawRect
  {
    DrawRect target;
    
    public TraceDrawRect(DrawRect paramDrawRect)
    {
      super(paramDrawRect.getCompositeType(), paramDrawRect.getDestType());
      target = paramDrawRect;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void DrawRect(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      tracePrimitive(target);
      target.DrawRect(paramSunGraphics2D, paramSurfaceData, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\DrawRect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */