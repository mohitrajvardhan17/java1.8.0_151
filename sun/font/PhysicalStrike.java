package sun.font;

import java.awt.geom.Point2D.Float;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PhysicalStrike
  extends FontStrike
{
  static final long INTMASK = 4294967295L;
  static boolean longAddresses;
  private PhysicalFont physicalFont;
  protected CharToGlyphMapper mapper;
  protected long pScalerContext;
  protected long[] longGlyphImages;
  protected int[] intGlyphImages;
  ConcurrentHashMap<Integer, Point2D.Float> glyphPointMapCache;
  protected boolean getImageWithAdvance;
  protected static final int complexTX = 124;
  
  PhysicalStrike(PhysicalFont paramPhysicalFont, FontStrikeDesc paramFontStrikeDesc)
  {
    physicalFont = paramPhysicalFont;
    desc = paramFontStrikeDesc;
  }
  
  protected PhysicalStrike() {}
  
  public int getNumGlyphs()
  {
    return physicalFont.getNumGlyphs();
  }
  
  StrikeMetrics getFontMetrics()
  {
    if (strikeMetrics == null) {
      strikeMetrics = physicalFont.getFontMetrics(pScalerContext);
    }
    return strikeMetrics;
  }
  
  float getCodePointAdvance(int paramInt)
  {
    return getGlyphAdvance(physicalFont.getMapper().charToGlyph(paramInt));
  }
  
  Point2D.Float getCharMetrics(char paramChar)
  {
    return getGlyphMetrics(physicalFont.getMapper().charToGlyph(paramChar));
  }
  
  int getSlot0GlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt)
  {
    return 0;
  }
  
  Point2D.Float getGlyphPoint(int paramInt1, int paramInt2)
  {
    Point2D.Float localFloat = null;
    Integer localInteger = Integer.valueOf(paramInt1 << 16 | paramInt2);
    if (glyphPointMapCache == null) {
      synchronized (this)
      {
        if (glyphPointMapCache == null) {
          glyphPointMapCache = new ConcurrentHashMap();
        }
      }
    } else {
      localFloat = (Point2D.Float)glyphPointMapCache.get(localInteger);
    }
    if (localFloat == null)
    {
      localFloat = physicalFont.getGlyphPoint(pScalerContext, paramInt1, paramInt2);
      adjustPoint(localFloat);
      glyphPointMapCache.put(localInteger, localFloat);
    }
    return localFloat;
  }
  
  protected void adjustPoint(Point2D.Float paramFloat) {}
  
  static
  {
    switch (StrikeCache.nativeAddressSize)
    {
    case 8: 
      longAddresses = true;
      break;
    case 4: 
      longAddresses = false;
      break;
    default: 
      throw new RuntimeException("Unexpected address size");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\PhysicalStrike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */