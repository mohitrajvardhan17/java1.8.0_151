package sun.java2d.loops;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class ScaledBlit
  extends GraphicsPrimitive
{
  public static final String methodSignature = "ScaledBlit(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  private static RenderCache blitcache = new RenderCache(20);
  
  public static ScaledBlit locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (ScaledBlit)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public static ScaledBlit getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    Object localObject = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (localObject != null) {
      return (ScaledBlit)localObject;
    }
    ScaledBlit localScaledBlit = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (localScaledBlit != null) {
      blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, localScaledBlit);
    }
    return localScaledBlit;
  }
  
  protected ScaledBlit(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public ScaledBlit(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return null;
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceScaledBlit(this);
  }
  
  static
  {
    GraphicsPrimitiveMgr.registerGeneral(new ScaledBlit(null, null, null));
  }
  
  private static class TraceScaledBlit
    extends ScaledBlit
  {
    ScaledBlit target;
    
    public TraceScaledBlit(ScaledBlit paramScaledBlit)
    {
      super(paramScaledBlit.getCompositeType(), paramScaledBlit.getDestType());
      target = paramScaledBlit;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      tracePrimitive(target);
      target.Scale(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\ScaledBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */