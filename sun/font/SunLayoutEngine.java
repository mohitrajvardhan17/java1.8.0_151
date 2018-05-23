package sun.font;

import java.awt.geom.Point2D.Float;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

public final class SunLayoutEngine
  implements GlyphLayout.LayoutEngine, GlyphLayout.LayoutEngineFactory
{
  private GlyphLayout.LayoutEngineKey key;
  private static GlyphLayout.LayoutEngineFactory instance;
  private SoftReference cacheref = new SoftReference(null);
  
  private static native void initGVIDs();
  
  public static GlyphLayout.LayoutEngineFactory instance()
  {
    if (instance == null) {
      instance = new SunLayoutEngine();
    }
    return instance;
  }
  
  private SunLayoutEngine() {}
  
  public GlyphLayout.LayoutEngine getEngine(Font2D paramFont2D, int paramInt1, int paramInt2)
  {
    return getEngine(new GlyphLayout.LayoutEngineKey(paramFont2D, paramInt1, paramInt2));
  }
  
  public GlyphLayout.LayoutEngine getEngine(GlyphLayout.LayoutEngineKey paramLayoutEngineKey)
  {
    ConcurrentHashMap localConcurrentHashMap = (ConcurrentHashMap)cacheref.get();
    if (localConcurrentHashMap == null)
    {
      localConcurrentHashMap = new ConcurrentHashMap();
      cacheref = new SoftReference(localConcurrentHashMap);
    }
    Object localObject = (GlyphLayout.LayoutEngine)localConcurrentHashMap.get(paramLayoutEngineKey);
    if (localObject == null)
    {
      GlyphLayout.LayoutEngineKey localLayoutEngineKey = paramLayoutEngineKey.copy();
      localObject = new SunLayoutEngine(localLayoutEngineKey);
      localConcurrentHashMap.put(localLayoutEngineKey, localObject);
    }
    return (GlyphLayout.LayoutEngine)localObject;
  }
  
  private SunLayoutEngine(GlyphLayout.LayoutEngineKey paramLayoutEngineKey)
  {
    key = paramLayoutEngineKey;
  }
  
  public void layout(FontStrikeDesc paramFontStrikeDesc, float[] paramArrayOfFloat, int paramInt1, int paramInt2, TextRecord paramTextRecord, int paramInt3, Point2D.Float paramFloat, GlyphLayout.GVData paramGVData)
  {
    Font2D localFont2D = key.font();
    FontStrike localFontStrike = localFont2D.getStrike(paramFontStrikeDesc);
    long l = 0L;
    if ((localFont2D instanceof TrueTypeFont)) {
      l = ((TrueTypeFont)localFont2D).getLayoutTableCache();
    }
    nativeLayout(localFont2D, localFontStrike, paramArrayOfFloat, paramInt1, paramInt2, text, start, limit, min, max, key.script(), key.lang(), paramInt3, paramFloat, paramGVData, localFont2D.getUnitsPerEm(), l);
  }
  
  private static native void nativeLayout(Font2D paramFont2D, FontStrike paramFontStrike, float[] paramArrayOfFloat, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, Point2D.Float paramFloat, GlyphLayout.GVData paramGVData, long paramLong1, long paramLong2);
  
  static
  {
    FontManagerNativeLibrary.load();
    initGVIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\SunLayoutEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */