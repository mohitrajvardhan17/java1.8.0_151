package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D.Float;
import java.lang.ref.WeakReference;

class FreetypeFontScaler
  extends FontScaler
{
  private static final int TRUETYPE_FONT = 1;
  private static final int TYPE1_FONT = 2;
  
  private static native void initIDs(Class paramClass);
  
  private void invalidateScaler()
    throws FontScalerException
  {
    nativeScaler = 0L;
    font = null;
    throw new FontScalerException();
  }
  
  public FreetypeFontScaler(Font2D paramFont2D, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    int i = 1;
    if ((paramFont2D instanceof Type1Font)) {
      i = 2;
    }
    nativeScaler = initNativeScaler(paramFont2D, i, paramInt1, paramBoolean, paramInt2);
    font = new WeakReference(paramFont2D);
  }
  
  synchronized StrikeMetrics getFontMetrics(long paramLong)
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getFontMetricsNative((Font2D)font.get(), paramLong, nativeScaler);
    }
    return FontScaler.getNullScaler().getFontMetrics(0L);
  }
  
  synchronized float getGlyphAdvance(long paramLong, int paramInt)
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getGlyphAdvanceNative((Font2D)font.get(), paramLong, nativeScaler, paramInt);
    }
    return FontScaler.getNullScaler().getGlyphAdvance(0L, paramInt);
  }
  
  synchronized void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat)
    throws FontScalerException
  {
    if (nativeScaler != 0L)
    {
      getGlyphMetricsNative((Font2D)font.get(), paramLong, nativeScaler, paramInt, paramFloat);
      return;
    }
    FontScaler.getNullScaler().getGlyphMetrics(0L, paramInt, paramFloat);
  }
  
  synchronized long getGlyphImage(long paramLong, int paramInt)
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getGlyphImageNative((Font2D)font.get(), paramLong, nativeScaler, paramInt);
    }
    return FontScaler.getNullScaler().getGlyphImage(0L, paramInt);
  }
  
  synchronized Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt)
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getGlyphOutlineBoundsNative((Font2D)font.get(), paramLong, nativeScaler, paramInt);
    }
    return FontScaler.getNullScaler().getGlyphOutlineBounds(0L, paramInt);
  }
  
  synchronized GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2)
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getGlyphOutlineNative((Font2D)font.get(), paramLong, nativeScaler, paramInt, paramFloat1, paramFloat2);
    }
    return FontScaler.getNullScaler().getGlyphOutline(0L, paramInt, paramFloat1, paramFloat2);
  }
  
  synchronized GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2)
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getGlyphVectorOutlineNative((Font2D)font.get(), paramLong, nativeScaler, paramArrayOfInt, paramInt, paramFloat1, paramFloat2);
    }
    return FontScaler.getNullScaler().getGlyphVectorOutline(0L, paramArrayOfInt, paramInt, paramFloat1, paramFloat2);
  }
  
  synchronized long getLayoutTableCache()
    throws FontScalerException
  {
    return getLayoutTableCacheNative(nativeScaler);
  }
  
  public synchronized void dispose()
  {
    if (nativeScaler != 0L)
    {
      disposeNativeScaler((Font2D)font.get(), nativeScaler);
      nativeScaler = 0L;
    }
  }
  
  synchronized int getNumGlyphs()
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getNumGlyphsNative(nativeScaler);
    }
    return FontScaler.getNullScaler().getNumGlyphs();
  }
  
  synchronized int getMissingGlyphCode()
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getMissingGlyphCodeNative(nativeScaler);
    }
    return FontScaler.getNullScaler().getMissingGlyphCode();
  }
  
  synchronized int getGlyphCode(char paramChar)
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getGlyphCodeNative((Font2D)font.get(), nativeScaler, paramChar);
    }
    return FontScaler.getNullScaler().getGlyphCode(paramChar);
  }
  
  synchronized Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2)
    throws FontScalerException
  {
    if (nativeScaler != 0L) {
      return getGlyphPointNative((Font2D)font.get(), paramLong, nativeScaler, paramInt1, paramInt2);
    }
    return FontScaler.getNullScaler().getGlyphPoint(paramLong, paramInt1, paramInt2);
  }
  
  synchronized long getUnitsPerEm()
  {
    return getUnitsPerEMNative(nativeScaler);
  }
  
  long createScalerContext(double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if (nativeScaler != 0L) {
      return createScalerContextNative(nativeScaler, paramArrayOfDouble, paramInt1, paramInt2, paramFloat1, paramFloat2);
    }
    return NullFontScaler.getNullScalerContext();
  }
  
  private native long initNativeScaler(Font2D paramFont2D, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3);
  
  private native StrikeMetrics getFontMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2);
  
  private native float getGlyphAdvanceNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native void getGlyphMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, Point2D.Float paramFloat);
  
  private native long getGlyphImageNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native Rectangle2D.Float getGlyphOutlineBoundsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native GeneralPath getGlyphOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, float paramFloat1, float paramFloat2);
  
  private native GeneralPath getGlyphVectorOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2);
  
  native Point2D.Float getGlyphPointNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt1, int paramInt2);
  
  private native long getLayoutTableCacheNative(long paramLong);
  
  private native void disposeNativeScaler(Font2D paramFont2D, long paramLong);
  
  private native int getGlyphCodeNative(Font2D paramFont2D, long paramLong, char paramChar);
  
  private native int getNumGlyphsNative(long paramLong);
  
  private native int getMissingGlyphCodeNative(long paramLong);
  
  private native long getUnitsPerEMNative(long paramLong);
  
  native long createScalerContextNative(long paramLong, double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2);
  
  void invalidateScalerContext(long paramLong) {}
  
  static
  {
    FontManagerNativeLibrary.load();
    initIDs(FreetypeFontScaler.class);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FreetypeFontScaler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */