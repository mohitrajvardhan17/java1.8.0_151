package sun.font;

import sun.java2d.Disposer.PollDisposable;
import sun.java2d.DisposerRecord;

class FontStrikeDisposer
  implements DisposerRecord, Disposer.PollDisposable
{
  Font2D font2D;
  FontStrikeDesc desc;
  long[] longGlyphImages;
  int[] intGlyphImages;
  int[][] segIntGlyphImages;
  long[][] segLongGlyphImages;
  long pScalerContext = 0L;
  boolean disposed = false;
  boolean comp = false;
  
  public FontStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc, long paramLong, int[] paramArrayOfInt)
  {
    font2D = paramFont2D;
    desc = paramFontStrikeDesc;
    pScalerContext = paramLong;
    intGlyphImages = paramArrayOfInt;
  }
  
  public FontStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc, long paramLong, long[] paramArrayOfLong)
  {
    font2D = paramFont2D;
    desc = paramFontStrikeDesc;
    pScalerContext = paramLong;
    longGlyphImages = paramArrayOfLong;
  }
  
  public FontStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc, long paramLong)
  {
    font2D = paramFont2D;
    desc = paramFontStrikeDesc;
    pScalerContext = paramLong;
  }
  
  public FontStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc)
  {
    font2D = paramFont2D;
    desc = paramFontStrikeDesc;
    comp = true;
  }
  
  public synchronized void dispose()
  {
    if (!disposed)
    {
      font2D.removeFromCache(desc);
      StrikeCache.disposeStrike(this);
      disposed = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontStrikeDisposer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */