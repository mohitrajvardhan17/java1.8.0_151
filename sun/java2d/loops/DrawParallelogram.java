package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawParallelogram
  extends GraphicsPrimitive
{
  public static final String methodSignature = "DrawParallelogram(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawParallelogram locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (DrawParallelogram)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected DrawParallelogram(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public DrawParallelogram(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void DrawParallelogram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    throw new InternalError("DrawParallelogram not implemented for " + paramSurfaceType1 + " with " + paramCompositeType);
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceDrawParallelogram(this);
  }
  
  private static class TraceDrawParallelogram
    extends DrawParallelogram
  {
    DrawParallelogram target;
    
    public TraceDrawParallelogram(DrawParallelogram paramDrawParallelogram)
    {
      super(paramDrawParallelogram.getCompositeType(), paramDrawParallelogram.getDestType());
      target = paramDrawParallelogram;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void DrawParallelogram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8)
    {
      tracePrimitive(target);
      target.DrawParallelogram(paramSunGraphics2D, paramSurfaceData, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\DrawParallelogram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */