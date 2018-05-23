package sun.font;

import java.awt.font.GlyphVector;
import sun.java2d.loops.FontInfo;
import sun.misc.Unsafe;

public final class GlyphList
{
  private static final int MINGRAYLENGTH = 1024;
  private static final int MAXGRAYLENGTH = 8192;
  private static final int DEFAULT_LENGTH = 32;
  int glyphindex;
  int[] metrics;
  byte[] graybits;
  Object strikelist;
  int len = 0;
  int maxLen = 0;
  int maxPosLen = 0;
  int[] glyphData;
  char[] chData;
  long[] images;
  float[] positions;
  float x;
  float y;
  float gposx;
  float gposy;
  boolean usePositions;
  boolean lcdRGBOrder;
  boolean lcdSubPixPos;
  private static GlyphList reusableGL = new GlyphList();
  private static boolean inUse;
  
  void ensureCapacity(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    if ((usePositions) && (paramInt > maxPosLen))
    {
      positions = new float[paramInt * 2 + 2];
      maxPosLen = paramInt;
    }
    if ((maxLen == 0) || (paramInt > maxLen))
    {
      glyphData = new int[paramInt];
      chData = new char[paramInt];
      images = new long[paramInt];
      maxLen = paramInt;
    }
  }
  
  private GlyphList() {}
  
  public static GlyphList getInstance()
  {
    if (inUse) {
      return new GlyphList();
    }
    synchronized (GlyphList.class)
    {
      if (inUse) {
        return new GlyphList();
      }
      inUse = true;
      return reusableGL;
    }
  }
  
  public boolean setFromString(FontInfo paramFontInfo, String paramString, float paramFloat1, float paramFloat2)
  {
    x = paramFloat1;
    y = paramFloat2;
    strikelist = fontStrike;
    lcdRGBOrder = lcdRGBOrder;
    lcdSubPixPos = lcdSubPixPos;
    len = paramString.length();
    ensureCapacity(len);
    paramString.getChars(0, len, chData, 0);
    return mapChars(paramFontInfo, len);
  }
  
  public boolean setFromChars(FontInfo paramFontInfo, char[] paramArrayOfChar, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    x = paramFloat1;
    y = paramFloat2;
    strikelist = fontStrike;
    lcdRGBOrder = lcdRGBOrder;
    lcdSubPixPos = lcdSubPixPos;
    len = paramInt2;
    if (paramInt2 < 0) {
      len = 0;
    } else {
      len = paramInt2;
    }
    ensureCapacity(len);
    System.arraycopy(paramArrayOfChar, paramInt1, chData, 0, len);
    return mapChars(paramFontInfo, len);
  }
  
  private final boolean mapChars(FontInfo paramFontInfo, int paramInt)
  {
    if (font2D.getMapper().charsToGlyphsNS(paramInt, chData, glyphData)) {
      return false;
    }
    fontStrike.getGlyphImagePtrs(glyphData, images, paramInt);
    glyphindex = -1;
    return true;
  }
  
  public void setFromGlyphVector(FontInfo paramFontInfo, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
  {
    x = paramFloat1;
    y = paramFloat2;
    lcdRGBOrder = lcdRGBOrder;
    lcdSubPixPos = lcdSubPixPos;
    StandardGlyphVector localStandardGlyphVector = StandardGlyphVector.getStandardGV(paramGlyphVector, paramFontInfo);
    usePositions = localStandardGlyphVector.needsPositions(devTx);
    len = localStandardGlyphVector.getNumGlyphs();
    ensureCapacity(len);
    strikelist = localStandardGlyphVector.setupGlyphImages(images, usePositions ? positions : null, devTx);
    glyphindex = -1;
  }
  
  public int[] getBounds()
  {
    if (glyphindex >= 0) {
      throw new InternalError("calling getBounds after setGlyphIndex");
    }
    if (metrics == null) {
      metrics = new int[5];
    }
    gposx = (x + 0.5F);
    gposy = (y + 0.5F);
    fillBounds(metrics);
    return metrics;
  }
  
  public void setGlyphIndex(int paramInt)
  {
    glyphindex = paramInt;
    float f1 = StrikeCache.unsafe.getFloat(images[paramInt] + StrikeCache.topLeftXOffset);
    float f2 = StrikeCache.unsafe.getFloat(images[paramInt] + StrikeCache.topLeftYOffset);
    if (usePositions)
    {
      metrics[0] = ((int)Math.floor(positions[(paramInt << 1)] + gposx + f1));
      metrics[1] = ((int)Math.floor(positions[((paramInt << 1) + 1)] + gposy + f2));
    }
    else
    {
      metrics[0] = ((int)Math.floor(gposx + f1));
      metrics[1] = ((int)Math.floor(gposy + f2));
      gposx += StrikeCache.unsafe.getFloat(images[paramInt] + StrikeCache.xAdvanceOffset);
      gposy += StrikeCache.unsafe.getFloat(images[paramInt] + StrikeCache.yAdvanceOffset);
    }
    metrics[2] = StrikeCache.unsafe.getChar(images[paramInt] + StrikeCache.widthOffset);
    metrics[3] = StrikeCache.unsafe.getChar(images[paramInt] + StrikeCache.heightOffset);
    metrics[4] = StrikeCache.unsafe.getChar(images[paramInt] + StrikeCache.rowBytesOffset);
  }
  
  public int[] getMetrics()
  {
    return metrics;
  }
  
  public byte[] getGrayBits()
  {
    int i = metrics[4] * metrics[3];
    if (graybits == null) {
      graybits = new byte[Math.max(i, 1024)];
    } else if (i > graybits.length) {
      graybits = new byte[i];
    }
    long l = StrikeCache.unsafe.getAddress(images[glyphindex] + StrikeCache.pixelDataOffset);
    if (l == 0L) {
      return graybits;
    }
    for (int j = 0; j < i; j++) {
      graybits[j] = StrikeCache.unsafe.getByte(l + j);
    }
    return graybits;
  }
  
  public long[] getImages()
  {
    return images;
  }
  
  public boolean usePositions()
  {
    return usePositions;
  }
  
  public float[] getPositions()
  {
    return positions;
  }
  
  public float getX()
  {
    return x;
  }
  
  public float getY()
  {
    return y;
  }
  
  public Object getStrike()
  {
    return strikelist;
  }
  
  public boolean isSubPixPos()
  {
    return lcdSubPixPos;
  }
  
  public boolean isRGBOrder()
  {
    return lcdRGBOrder;
  }
  
  public void dispose()
  {
    if (this == reusableGL)
    {
      if ((graybits != null) && (graybits.length > 8192)) {
        graybits = null;
      }
      usePositions = false;
      strikelist = null;
      inUse = false;
    }
  }
  
  public int getNumGlyphs()
  {
    return len;
  }
  
  private void fillBounds(int[] paramArrayOfInt)
  {
    int i = StrikeCache.topLeftXOffset;
    int j = StrikeCache.topLeftYOffset;
    int k = StrikeCache.widthOffset;
    int m = StrikeCache.heightOffset;
    int n = StrikeCache.xAdvanceOffset;
    int i1 = StrikeCache.yAdvanceOffset;
    if (len == 0)
    {
      paramArrayOfInt[0] = (paramArrayOfInt[1] = paramArrayOfInt[2] = paramArrayOfInt[3] = 0);
      return;
    }
    float f2;
    float f1 = f2 = Float.POSITIVE_INFINITY;
    float f4;
    float f3 = f4 = Float.NEGATIVE_INFINITY;
    int i2 = 0;
    float f5 = x + 0.5F;
    float f6 = y + 0.5F;
    for (int i5 = 0; i5 < len; i5++)
    {
      float f7 = StrikeCache.unsafe.getFloat(images[i5] + i);
      float f8 = StrikeCache.unsafe.getFloat(images[i5] + j);
      int i3 = StrikeCache.unsafe.getChar(images[i5] + k);
      int i4 = StrikeCache.unsafe.getChar(images[i5] + m);
      float f9;
      float f10;
      if (usePositions)
      {
        f9 = positions[(i2++)] + f7 + f5;
        f10 = positions[(i2++)] + f8 + f6;
      }
      else
      {
        f9 = f5 + f7;
        f10 = f6 + f8;
        f5 += StrikeCache.unsafe.getFloat(images[i5] + n);
        f6 += StrikeCache.unsafe.getFloat(images[i5] + i1);
      }
      float f11 = f9 + i3;
      float f12 = f10 + i4;
      if (f1 > f9) {
        f1 = f9;
      }
      if (f2 > f10) {
        f2 = f10;
      }
      if (f3 < f11) {
        f3 = f11;
      }
      if (f4 < f12) {
        f4 = f12;
      }
    }
    paramArrayOfInt[0] = ((int)Math.floor(f1));
    paramArrayOfInt[1] = ((int)Math.floor(f2));
    paramArrayOfInt[2] = ((int)Math.floor(f3));
    paramArrayOfInt[3] = ((int)Math.floor(f4));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\GlyphList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */