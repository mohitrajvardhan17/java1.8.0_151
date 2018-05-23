package sun.java2d.loops;

import java.awt.Composite;
import java.awt.image.BufferedImage;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class MaskFill
  extends GraphicsPrimitive
{
  public static final String methodSignature = "MaskFill(...)".toString();
  public static final String fillPgramSignature = "FillAAPgram(...)".toString();
  public static final String drawPgramSignature = "DrawAAPgram(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  private static RenderCache fillcache = new RenderCache(10);
  
  public static MaskFill locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (MaskFill)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public static MaskFill locatePrim(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (MaskFill)GraphicsPrimitiveMgr.locatePrim(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public static MaskFill getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    Object localObject = fillcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (localObject != null) {
      return (MaskFill)localObject;
    }
    MaskFill localMaskFill = locatePrim(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (localMaskFill != null) {
      fillcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, localMaskFill);
    }
    return localMaskFill;
  }
  
  protected MaskFill(String paramString, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramString, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected MaskFill(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public MaskFill(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6);
  
  public native void FillAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public native void DrawAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8);
  
  public boolean canDoParallelograms()
  {
    return getNativePrim() != 0L;
  }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    if ((SurfaceType.OpaqueColor.equals(paramSurfaceType1)) || (SurfaceType.AnyColor.equals(paramSurfaceType1)))
    {
      if (CompositeType.Xor.equals(paramCompositeType)) {
        throw new InternalError("Cannot construct MaskFill for XOR mode");
      }
      return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    }
    throw new InternalError("MaskFill can only fill with colors");
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceMaskFill(this);
  }
  
  static
  {
    GraphicsPrimitiveMgr.registerGeneral(new MaskFill(null, null, null));
  }
  
  private static class General
    extends MaskFill
  {
    FillRect fillop;
    MaskBlit maskop;
    
    public General(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
    {
      super(paramCompositeType, paramSurfaceType2);
      fillop = FillRect.locate(paramSurfaceType1, CompositeType.SrcNoEa, SurfaceType.IntArgb);
      maskop = MaskBlit.locate(SurfaceType.IntArgb, paramCompositeType, paramSurfaceType2);
    }
    
    public void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
    {
      BufferedImage localBufferedImage = new BufferedImage(paramInt3, paramInt4, 2);
      SurfaceData localSurfaceData = BufImgSurfaceData.createData(localBufferedImage);
      Region localRegion = clipRegion;
      clipRegion = null;
      int i = pixel;
      pixel = localSurfaceData.pixelFor(paramSunGraphics2D.getColor());
      fillop.FillRect(paramSunGraphics2D, localSurfaceData, 0, 0, paramInt3, paramInt4);
      pixel = i;
      clipRegion = localRegion;
      maskop.MaskBlit(localSurfaceData, paramSurfaceData, paramComposite, null, 0, 0, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, paramInt5, paramInt6);
    }
  }
  
  private static class TraceMaskFill
    extends MaskFill
  {
    MaskFill target;
    MaskFill fillPgramTarget;
    MaskFill drawPgramTarget;
    
    public TraceMaskFill(MaskFill paramMaskFill)
    {
      super(paramMaskFill.getCompositeType(), paramMaskFill.getDestType());
      target = paramMaskFill;
      fillPgramTarget = new MaskFill(fillPgramSignature, paramMaskFill.getSourceType(), paramMaskFill.getCompositeType(), paramMaskFill.getDestType());
      drawPgramTarget = new MaskFill(drawPgramSignature, paramMaskFill.getSourceType(), paramMaskFill.getCompositeType(), paramMaskFill.getDestType());
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
    {
      tracePrimitive(target);
      target.MaskFill(paramSunGraphics2D, paramSurfaceData, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, paramInt5, paramInt6);
    }
    
    public void FillAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
    {
      tracePrimitive(fillPgramTarget);
      target.FillAAPgram(paramSunGraphics2D, paramSurfaceData, paramComposite, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6);
    }
    
    public void DrawAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8)
    {
      tracePrimitive(drawPgramTarget);
      target.DrawAAPgram(paramSunGraphics2D, paramSurfaceData, paramComposite, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8);
    }
    
    public boolean canDoParallelograms()
    {
      return target.canDoParallelograms();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\MaskFill.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */