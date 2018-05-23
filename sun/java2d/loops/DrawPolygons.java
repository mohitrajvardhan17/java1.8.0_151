package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawPolygons
  extends GraphicsPrimitive
{
  public static final String methodSignature = "DrawPolygons(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawPolygons locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (DrawPolygons)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected DrawPolygons(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public DrawPolygons(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void DrawPolygons(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    throw new InternalError("DrawPolygons not implemented for " + paramSurfaceType1 + " with " + paramCompositeType);
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceDrawPolygons(this);
  }
  
  private static class TraceDrawPolygons
    extends DrawPolygons
  {
    DrawPolygons target;
    
    public TraceDrawPolygons(DrawPolygons paramDrawPolygons)
    {
      super(paramDrawPolygons.getCompositeType(), paramDrawPolygons.getDestType());
      target = paramDrawPolygons;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void DrawPolygons(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      tracePrimitive(target);
      target.DrawPolygons(paramSunGraphics2D, paramSurfaceData, paramArrayOfInt1, paramArrayOfInt2, paramArrayOfInt3, paramInt1, paramInt2, paramInt3, paramBoolean);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\DrawPolygons.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */