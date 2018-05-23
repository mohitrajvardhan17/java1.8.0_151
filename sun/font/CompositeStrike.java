package sun.font;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D.Float;

public final class CompositeStrike
  extends FontStrike
{
  static final int SLOTMASK = 16777215;
  private CompositeFont compFont;
  private PhysicalStrike[] strikes;
  int numGlyphs = 0;
  
  CompositeStrike(CompositeFont paramCompositeFont, FontStrikeDesc paramFontStrikeDesc)
  {
    compFont = paramCompositeFont;
    desc = paramFontStrikeDesc;
    disposer = new FontStrikeDisposer(compFont, paramFontStrikeDesc);
    if (style != compFont.style)
    {
      algoStyle = true;
      if (((style & 0x1) == 1) && ((compFont.style & 0x1) == 0)) {
        boldness = 1.33F;
      }
      if (((style & 0x2) == 2) && ((compFont.style & 0x2) == 0)) {
        italic = 0.7F;
      }
    }
    strikes = new PhysicalStrike[compFont.numSlots];
  }
  
  PhysicalStrike getStrikeForGlyph(int paramInt)
  {
    return getStrikeForSlot(paramInt >>> 24);
  }
  
  PhysicalStrike getStrikeForSlot(int paramInt)
  {
    PhysicalStrike localPhysicalStrike = strikes[paramInt];
    if (localPhysicalStrike == null)
    {
      localPhysicalStrike = (PhysicalStrike)compFont.getSlotFont(paramInt).getStrike(desc);
      strikes[paramInt] = localPhysicalStrike;
    }
    return localPhysicalStrike;
  }
  
  public int getNumGlyphs()
  {
    return compFont.getNumGlyphs();
  }
  
  StrikeMetrics getFontMetrics()
  {
    if (strikeMetrics == null)
    {
      StrikeMetrics localStrikeMetrics = new StrikeMetrics();
      for (int i = 0; i < compFont.numMetricsSlots; i++) {
        localStrikeMetrics.merge(getStrikeForSlot(i).getFontMetrics());
      }
      strikeMetrics = localStrikeMetrics;
    }
    return strikeMetrics;
  }
  
  void getGlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt)
  {
    PhysicalStrike localPhysicalStrike = getStrikeForSlot(0);
    int i = localPhysicalStrike.getSlot0GlyphImagePtrs(paramArrayOfInt, paramArrayOfLong, paramInt);
    if (i == paramInt) {
      return;
    }
    for (int j = i; j < paramInt; j++)
    {
      localPhysicalStrike = getStrikeForGlyph(paramArrayOfInt[j]);
      paramArrayOfLong[j] = localPhysicalStrike.getGlyphImagePtr(paramArrayOfInt[j] & 0xFFFFFF);
    }
  }
  
  long getGlyphImagePtr(int paramInt)
  {
    PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
    return localPhysicalStrike.getGlyphImagePtr(paramInt & 0xFFFFFF);
  }
  
  void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle)
  {
    PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
    localPhysicalStrike.getGlyphImageBounds(paramInt & 0xFFFFFF, paramFloat, paramRectangle);
  }
  
  Point2D.Float getGlyphMetrics(int paramInt)
  {
    PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
    return localPhysicalStrike.getGlyphMetrics(paramInt & 0xFFFFFF);
  }
  
  Point2D.Float getCharMetrics(char paramChar)
  {
    return getGlyphMetrics(compFont.getMapper().charToGlyph(paramChar));
  }
  
  float getGlyphAdvance(int paramInt)
  {
    PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
    return localPhysicalStrike.getGlyphAdvance(paramInt & 0xFFFFFF);
  }
  
  float getCodePointAdvance(int paramInt)
  {
    return getGlyphAdvance(compFont.getMapper().charToGlyph(paramInt));
  }
  
  Rectangle2D.Float getGlyphOutlineBounds(int paramInt)
  {
    PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
    return localPhysicalStrike.getGlyphOutlineBounds(paramInt & 0xFFFFFF);
  }
  
  GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2)
  {
    PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
    GeneralPath localGeneralPath = localPhysicalStrike.getGlyphOutline(paramInt & 0xFFFFFF, paramFloat1, paramFloat2);
    if (localGeneralPath == null) {
      return new GeneralPath();
    }
    return localGeneralPath;
  }
  
  GeneralPath getGlyphVectorOutline(int[] paramArrayOfInt, float paramFloat1, float paramFloat2)
  {
    Object localObject = null;
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      int j = i;
      int k = paramArrayOfInt[i] >>> 24;
      while ((i < paramArrayOfInt.length) && (paramArrayOfInt[(i + 1)] >>> 24 == k)) {
        i++;
      }
      int m = i - j + 1;
      int[] arrayOfInt = new int[m];
      for (int n = 0; n < m; n++) {
        paramArrayOfInt[n] &= 0xFFFFFF;
      }
      GeneralPath localGeneralPath = getStrikeForSlot(k).getGlyphVectorOutline(arrayOfInt, paramFloat1, paramFloat2);
      if (localObject == null) {
        localObject = localGeneralPath;
      } else if (localGeneralPath != null) {
        ((GeneralPath)localObject).append(localGeneralPath, false);
      }
    }
    if (localObject == null) {
      return new GeneralPath();
    }
    return (GeneralPath)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\CompositeStrike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */