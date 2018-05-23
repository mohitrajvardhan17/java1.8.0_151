package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class DrawGlyphListAA
  extends GraphicsPrimitive
{
  public static final String methodSignature = "DrawGlyphListAA(...)".toString();
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawGlyphListAA locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return (DrawGlyphListAA)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected DrawGlyphListAA(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public DrawGlyphListAA(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void DrawGlyphListAA(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public GraphicsPrimitive traceWrap()
  {
    return new TraceDrawGlyphListAA(this);
  }
  
  static
  {
    GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphListAA(null, null, null));
  }
  
  public static class General
    extends DrawGlyphListAA
  {
    MaskFill maskop;
    
    public General(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
    {
      super(paramCompositeType, paramSurfaceType2);
      maskop = MaskFill.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    }
    
    public void DrawGlyphListAA(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList)
    {
      paramGlyphList.getBounds();
      int i = paramGlyphList.getNumGlyphs();
      Region localRegion = paramSunGraphics2D.getCompClip();
      int j = localRegion.getLoX();
      int k = localRegion.getLoY();
      int m = localRegion.getHiX();
      int n = localRegion.getHiY();
      for (int i1 = 0; i1 < i; i1++)
      {
        paramGlyphList.setGlyphIndex(i1);
        int[] arrayOfInt = paramGlyphList.getMetrics();
        int i2 = arrayOfInt[0];
        int i3 = arrayOfInt[1];
        int i4 = arrayOfInt[2];
        int i5 = i2 + i4;
        int i6 = i3 + arrayOfInt[3];
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
  
  private static class TraceDrawGlyphListAA
    extends DrawGlyphListAA
  {
    DrawGlyphListAA target;
    
    public TraceDrawGlyphListAA(DrawGlyphListAA paramDrawGlyphListAA)
    {
      super(paramDrawGlyphListAA.getCompositeType(), paramDrawGlyphListAA.getDestType());
      target = paramDrawGlyphListAA;
    }
    
    public GraphicsPrimitive traceWrap()
    {
      return this;
    }
    
    public void DrawGlyphListAA(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList)
    {
      tracePrimitive(target);
      target.DrawGlyphListAA(paramSunGraphics2D, paramSurfaceData, paramGlyphList);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\DrawGlyphListAA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */