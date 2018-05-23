package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class DrawGlyphList
  extends GraphicsPrimitive
{
  public static final String methodSignature = "DrawGlyphList(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawGlyphList locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (DrawGlyphList)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected DrawGlyphList(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public DrawGlyphList(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void DrawGlyphList(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceDrawGlyphList(this);
  }
  
  static
  {
    GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphList(null, null, null));
  }
  
  private static class General
    extends DrawGlyphList
  {
    MaskFill maskop;
    
    public General(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
    {
      super(paramCompositeType, paramSurfaceType2);
      maskop = MaskFill.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    }
    
    public void DrawGlyphList(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList)
    {
      int[] arrayOfInt1 = paramGlyphList.getBounds();
      int i = paramGlyphList.getNumGlyphs();
      Region localRegion = paramSunGraphics2D.getCompClip();
      int j = localRegion.getLoX();
      int k = localRegion.getLoY();
      int m = localRegion.getHiX();
      int n = localRegion.getHiY();
      for (int i1 = 0; i1 < i; i1++)
      {
        paramGlyphList.setGlyphIndex(i1);
        int[] arrayOfInt2 = paramGlyphList.getMetrics();
        int i2 = arrayOfInt2[0];
        int i3 = arrayOfInt2[1];
        int i4 = arrayOfInt2[2];
        int i5 = i2 + i4;
        int i6 = i3 + arrayOfInt2[3];
        int i7 = 0;
        if (i2 < j)
        {
          i7 = j - i2;
          i2 = j;
        }
        if (i3 < k)
        {
          i7 += (k - i3) * i4;
          i3 = k;
        }
        if (i5 > m) {
          i5 = m;
        }
        if (i6 > n) {
          i6 = n;
        }
        if ((i5 > i2) && (i6 > i3))
        {
          byte[] arrayOfByte = paramGlyphList.getGrayBits();
          maskop.MaskFill(paramSunGraphics2D, paramSurfaceData, composite, i2, i3, i5 - i2, i6 - i3, arrayOfByte, i7, i4);
        }
      }
    }
  }
  
  private static class TraceDrawGlyphList
    extends DrawGlyphList
  {
    DrawGlyphList target;
    
    public TraceDrawGlyphList(DrawGlyphList paramDrawGlyphList)
    {
      super(paramDrawGlyphList.getCompositeType(), paramDrawGlyphList.getDestType());
      target = paramDrawGlyphList;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void DrawGlyphList(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList)
    {
      tracePrimitive(target);
      target.DrawGlyphList(paramSunGraphics2D, paramSurfaceData, paramGlyphList);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\DrawGlyphList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */