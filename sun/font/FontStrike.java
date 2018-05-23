package sun.font;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D.Float;

public abstract class FontStrike
{
  protected FontStrikeDisposer disposer;
  protected FontStrikeDesc desc;
  protected StrikeMetrics strikeMetrics;
  protected boolean algoStyle = false;
  protected float boldness = 1.0F;
  protected float italic = 0.0F;
  
  public FontStrike() {}
  
  public abstract int getNumGlyphs();
  
  abstract StrikeMetrics getFontMetrics();
  
  abstract void getGlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt);
  
  abstract long getGlyphImagePtr(int paramInt);
  
  abstract void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle);
  
  abstract Point2D.Float getGlyphMetrics(int paramInt);
  
  abstract Point2D.Float getCharMetrics(char paramChar);
  
  abstract float getGlyphAdvance(int paramInt);
  
  abstract float getCodePointAdvance(int paramInt);
  
  abstract Rectangle2D.Float getGlyphOutlineBounds(int paramInt);
  
  abstract GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2);
  
  abstract GeneralPath getGlyphVectorOutline(int[] paramArrayOfInt, float paramFloat1, float paramFloat2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontStrike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */