package sun.java2d.loops;

import java.awt.geom.Path2D.Float;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class FillPath
  extends GraphicsPrimitive
{
  public static final String methodSignature = "FillPath(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  
  public static FillPath locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (FillPath)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected FillPath(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public FillPath(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void FillPath(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, Path2D.Float paramFloat);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    throw new InternalError("FillPath not implemented for " + paramSurfaceType1 + " with " + paramCompositeType);
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceFillPath(this);
  }
  
  private static class TraceFillPath
    extends FillPath
  {
    FillPath target;
    
    public TraceFillPath(FillPath paramFillPath)
    {
      super(paramFillPath.getCompositeType(), paramFillPath.getDestType());
      target = paramFillPath;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void FillPath(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, Path2D.Float paramFloat)
    {
      tracePrimitive(target);
      target.FillPath(paramSunGraphics2D, paramSurfaceData, paramInt1, paramInt2, paramFloat);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\FillPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */