package sun.java2d.loops;

import java.awt.Composite;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class MaskBlit
  extends GraphicsPrimitive
{
  public static final String methodSignature = "MaskBlit(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  private static RenderCache blitcache = new RenderCache(20);
  
  public static MaskBlit locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (MaskBlit)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public static MaskBlit getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    Object localObject = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (localObject != null) {
      return (MaskBlit)localObject;
    }
    MaskBlit localMaskBlit = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (localMaskBlit == null)
    {
      System.out.println("mask blit loop not found for:");
      System.out.println("src:  " + paramSurfaceType1);
      System.out.println("comp: " + paramCompositeType);
      System.out.println("dst:  " + paramSurfaceType2);
    }
    else
    {
      blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, localMaskBlit);
    }
    return localMaskBlit;
  }
  
  protected MaskBlit(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public MaskBlit(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void MaskBlit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, byte[] paramArrayOfByte, int paramInt7, int paramInt8);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    if (CompositeType.Xor.equals(paramCompositeType)) {
      throw new InternalError("Cannot construct MaskBlit for XOR mode");
    }
    General localGeneral = new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    setupGeneralBinaryOp(localGeneral);
    return localGeneral;
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceMaskBlit(this);
  }
  
  static
  {
    GraphicsPrimitiveMgr.registerGeneral(new MaskBlit(null, null, null));
  }
  
  private static class General
    extends MaskBlit
    implements GraphicsPrimitive.GeneralBinaryOp
  {
    Blit convertsrc;
    Blit convertdst;
    MaskBlit performop;
    Blit convertresult;
    WeakReference srcTmp;
    WeakReference dstTmp;
    
    public General(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
    {
      super(paramCompositeType, paramSurfaceType2);
    }
    
    public void setPrimitives(Blit paramBlit1, Blit paramBlit2, GraphicsPrimitive paramGraphicsPrimitive, Blit paramBlit3)
    {
      convertsrc = paramBlit1;
      convertdst = paramBlit2;
      performop = ((MaskBlit)paramGraphicsPrimitive);
      convertresult = paramBlit3;
    }
    
    public synchronized void MaskBlit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, byte[] paramArrayOfByte, int paramInt7, int paramInt8)
    {
      SurfaceData localSurfaceData1;
      int i;
      int j;
      SurfaceData localSurfaceData3;
      if (convertsrc == null)
      {
        localSurfaceData1 = paramSurfaceData1;
        i = paramInt1;
        j = paramInt2;
      }
      else
      {
        localSurfaceData3 = null;
        if (srcTmp != null) {
          localSurfaceData3 = (SurfaceData)srcTmp.get();
        }
        localSurfaceData1 = convertFrom(convertsrc, paramSurfaceData1, paramInt1, paramInt2, paramInt5, paramInt6, localSurfaceData3);
        i = 0;
        j = 0;
        if (localSurfaceData1 != localSurfaceData3) {
          srcTmp = new WeakReference(localSurfaceData1);
        }
      }
      SurfaceData localSurfaceData2;
      int k;
      int m;
      Region localRegion;
      if (convertdst == null)
      {
        localSurfaceData2 = paramSurfaceData2;
        k = paramInt3;
        m = paramInt4;
        localRegion = paramRegion;
      }
      else
      {
        localSurfaceData3 = null;
        if (dstTmp != null) {
          localSurfaceData3 = (SurfaceData)dstTmp.get();
        }
        localSurfaceData2 = convertFrom(convertdst, paramSurfaceData2, paramInt3, paramInt4, paramInt5, paramInt6, localSurfaceData3);
        k = 0;
        m = 0;
        localRegion = null;
        if (localSurfaceData2 != localSurfaceData3) {
          dstTmp = new WeakReference(localSurfaceData2);
        }
      }
      performop.MaskBlit(localSurfaceData1, localSurfaceData2, paramComposite, localRegion, i, j, k, m, paramInt5, paramInt6, paramArrayOfByte, paramInt7, paramInt8);
      if (convertresult != null) {
        convertTo(convertresult, localSurfaceData2, paramSurfaceData2, paramRegion, paramInt3, paramInt4, paramInt5, paramInt6);
      }
    }
  }
  
  private static class TraceMaskBlit
    extends MaskBlit
  {
    MaskBlit target;
    
    public TraceMaskBlit(MaskBlit paramMaskBlit)
    {
      super(paramMaskBlit.getSourceType(), paramMaskBlit.getCompositeType(), paramMaskBlit.getDestType());
      target = paramMaskBlit;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void MaskBlit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, byte[] paramArrayOfByte, int paramInt7, int paramInt8)
    {
      tracePrimitive(target);
      target.MaskBlit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfByte, paramInt7, paramInt8);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\MaskBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */