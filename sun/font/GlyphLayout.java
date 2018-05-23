package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D.Float;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import sun.util.logging.PlatformLogger;

public final class GlyphLayout
{
  private GVData _gvdata = new GVData();
  private static volatile GlyphLayout cache;
  private LayoutEngineFactory _lef;
  private TextRecord _textRecord = new TextRecord();
  private ScriptRun _scriptRuns = new ScriptRun();
  private FontRunIterator _fontRuns = new FontRunIterator();
  private int _ercount;
  private ArrayList _erecords = new ArrayList(10);
  private Point2D.Float _pt = new Point2D.Float();
  private FontStrikeDesc _sd = new FontStrikeDesc();
  private float[] _mat = new float[4];
  private int _typo_flags;
  private int _offset;
  
  public static GlyphLayout get(LayoutEngineFactory paramLayoutEngineFactory)
  {
    if (paramLayoutEngineFactory == null) {
      paramLayoutEngineFactory = SunLayoutEngine.instance();
    }
    GlyphLayout localGlyphLayout = null;
    synchronized (GlyphLayout.class)
    {
      if (cache != null)
      {
        localGlyphLayout = cache;
        cache = null;
      }
    }
    if (localGlyphLayout == null) {
      localGlyphLayout = new GlyphLayout();
    }
    _lef = paramLayoutEngineFactory;
    return localGlyphLayout;
  }
  
  public static void done(GlyphLayout paramGlyphLayout)
  {
    _lef = null;
    cache = paramGlyphLayout;
  }
  
  public StandardGlyphVector layout(Font paramFont, FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, StandardGlyphVector paramStandardGlyphVector)
  {
    if ((paramArrayOfChar == null) || (paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfChar.length - paramInt1)) {
      throw new IllegalArgumentException();
    }
    init(paramInt2);
    if (paramFont.hasLayoutAttributes())
    {
      localObject1 = ((AttributeMap)paramFont.getAttributes()).getValues();
      if (((AttributeValues)localObject1).getKerning() != 0) {
        _typo_flags |= 0x1;
      }
      if (((AttributeValues)localObject1).getLigatures() != 0) {
        _typo_flags |= 0x2;
      }
    }
    _offset = paramInt1;
    Object localObject1 = SDCache.get(paramFont, paramFontRenderContext);
    _mat[0] = ((float)gtx.getScaleX());
    _mat[1] = ((float)gtx.getShearY());
    _mat[2] = ((float)gtx.getShearX());
    _mat[3] = ((float)gtx.getScaleY());
    _pt.setLocation(delta);
    int i = paramInt1 + paramInt2;
    int j = 0;
    int k = paramArrayOfChar.length;
    if (paramInt3 != 0)
    {
      if ((paramInt3 & 0x1) != 0) {
        _typo_flags |= 0x80000000;
      }
      if ((paramInt3 & 0x2) != 0) {
        j = paramInt1;
      }
      if ((paramInt3 & 0x4) != 0) {
        k = i;
      }
    }
    int m = -1;
    Font2D localFont2D = FontUtilities.getFont2D(paramFont);
    _textRecord.init(paramArrayOfChar, paramInt1, i, j, k);
    int n = paramInt1;
    if ((localFont2D instanceof CompositeFont))
    {
      _scriptRuns.init(paramArrayOfChar, paramInt1, paramInt2);
      _fontRuns.init((CompositeFont)localFont2D, paramArrayOfChar, paramInt1, i);
      while (_scriptRuns.next())
      {
        i1 = _scriptRuns.getScriptLimit();
        i2 = _scriptRuns.getScriptCode();
        while (_fontRuns.next(i2, i1))
        {
          PhysicalFont localPhysicalFont = _fontRuns.getFont();
          if ((localPhysicalFont instanceof NativeFont)) {
            localPhysicalFont = ((NativeFont)localPhysicalFont).getDelegateFont();
          }
          int i4 = _fontRuns.getGlyphMask();
          int i5 = _fontRuns.getPos();
          nextEngineRecord(n, i5, i2, m, localPhysicalFont, i4);
          n = i5;
        }
      }
    }
    _scriptRuns.init(paramArrayOfChar, paramInt1, paramInt2);
    while (_scriptRuns.next())
    {
      i1 = _scriptRuns.getScriptLimit();
      i2 = _scriptRuns.getScriptCode();
      nextEngineRecord(n, i1, i2, m, localFont2D, 0);
      n = i1;
    }
    int i1 = 0;
    int i2 = _ercount;
    int i3 = 1;
    if (_typo_flags < 0)
    {
      i1 = i2 - 1;
      i2 = -1;
      i3 = -1;
    }
    _sd = sd;
    Object localObject2;
    while (i1 != i2)
    {
      localObject2 = (EngineRecord)_erecords.get(i1);
      for (;;)
      {
        try
        {
          ((EngineRecord)localObject2).layout();
        }
        catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
        {
          if (_gvdata._count >= 0) {
            _gvdata.grow();
          }
        }
      }
      if (_gvdata._count < 0) {
        break;
      }
      i1 += i3;
    }
    if (_gvdata._count < 0)
    {
      localObject2 = new StandardGlyphVector(paramFont, paramArrayOfChar, paramInt1, paramInt2, paramFontRenderContext);
      if (FontUtilities.debugFonts()) {
        FontUtilities.getLogger().warning("OpenType layout failed on font: " + paramFont);
      }
    }
    else
    {
      localObject2 = _gvdata.createGlyphVector(paramFont, paramFontRenderContext, paramStandardGlyphVector);
    }
    return (StandardGlyphVector)localObject2;
  }
  
  private GlyphLayout() {}
  
  private void init(int paramInt)
  {
    _typo_flags = 0;
    _ercount = 0;
    _gvdata.init(paramInt);
  }
  
  private void nextEngineRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Font2D paramFont2D, int paramInt5)
  {
    EngineRecord localEngineRecord = null;
    if (_ercount == _erecords.size())
    {
      localEngineRecord = new EngineRecord();
      _erecords.add(localEngineRecord);
    }
    else
    {
      localEngineRecord = (EngineRecord)_erecords.get(_ercount);
    }
    localEngineRecord.init(paramInt1, paramInt2, paramFont2D, paramInt3, paramInt4, paramInt5);
    _ercount += 1;
  }
  
  private final class EngineRecord
  {
    private int start;
    private int limit;
    private int gmask;
    private int eflags;
    private GlyphLayout.LayoutEngineKey key = new GlyphLayout.LayoutEngineKey();
    private GlyphLayout.LayoutEngine engine;
    
    EngineRecord() {}
    
    void init(int paramInt1, int paramInt2, Font2D paramFont2D, int paramInt3, int paramInt4, int paramInt5)
    {
      start = paramInt1;
      limit = paramInt2;
      gmask = paramInt5;
      key.init(paramFont2D, paramInt3, paramInt4);
      eflags = 0;
      for (int i = paramInt1; i < paramInt2; i++)
      {
        int j = _textRecord.text[i];
        if ((Character.isHighSurrogate((char)j)) && (i < paramInt2 - 1) && (Character.isLowSurrogate(_textRecord.text[(i + 1)]))) {
          j = Character.toCodePoint((char)j, _textRecord.text[(++i)]);
        }
        int k = Character.getType(j);
        if ((k == 6) || (k == 7) || (k == 8))
        {
          eflags = 4;
          break;
        }
      }
      engine = _lef.getEngine(key);
    }
    
    void layout()
    {
      _textRecord.start = start;
      _textRecord.limit = limit;
      engine.layout(_sd, _mat, gmask, start - _offset, _textRecord, _typo_flags | eflags, _pt, _gvdata);
    }
  }
  
  public static final class GVData
  {
    public int _count;
    public int _flags;
    public int[] _glyphs;
    public float[] _positions;
    public int[] _indices;
    private static final int UNINITIALIZED_FLAGS = -1;
    
    public GVData() {}
    
    public void init(int paramInt)
    {
      _count = 0;
      _flags = -1;
      if ((_glyphs == null) || (_glyphs.length < paramInt))
      {
        if (paramInt < 20) {
          paramInt = 20;
        }
        _glyphs = new int[paramInt];
        _positions = new float[paramInt * 2 + 2];
        _indices = new int[paramInt];
      }
    }
    
    public void grow()
    {
      grow(_glyphs.length / 4);
    }
    
    public void grow(int paramInt)
    {
      int i = _glyphs.length + paramInt;
      int[] arrayOfInt1 = new int[i];
      System.arraycopy(_glyphs, 0, arrayOfInt1, 0, _count);
      _glyphs = arrayOfInt1;
      float[] arrayOfFloat = new float[i * 2 + 2];
      System.arraycopy(_positions, 0, arrayOfFloat, 0, _count * 2 + 2);
      _positions = arrayOfFloat;
      int[] arrayOfInt2 = new int[i];
      System.arraycopy(_indices, 0, arrayOfInt2, 0, _count);
      _indices = arrayOfInt2;
    }
    
    public void adjustPositions(AffineTransform paramAffineTransform)
    {
      paramAffineTransform.transform(_positions, 0, _positions, 0, _count);
    }
    
    public StandardGlyphVector createGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, StandardGlyphVector paramStandardGlyphVector)
    {
      if (_flags == -1)
      {
        _flags = 0;
        if (_count > 1)
        {
          int i = 1;
          int j = 1;
          int k = _count;
          for (int m = 0; (m < _count) && ((i != 0) || (j != 0)); m++)
          {
            int n = _indices[m];
            i = (i != 0) && (n == m) ? 1 : 0;
            j = (j != 0) && (n == --k) ? 1 : 0;
          }
          if (j != 0) {
            _flags |= 0x4;
          }
          if ((j == 0) && (i == 0)) {
            _flags |= 0x8;
          }
        }
        _flags |= 0x2;
      }
      int[] arrayOfInt1 = new int[_count];
      System.arraycopy(_glyphs, 0, arrayOfInt1, 0, _count);
      float[] arrayOfFloat = null;
      if ((_flags & 0x2) != 0)
      {
        arrayOfFloat = new float[_count * 2 + 2];
        System.arraycopy(_positions, 0, arrayOfFloat, 0, arrayOfFloat.length);
      }
      int[] arrayOfInt2 = null;
      if ((_flags & 0x8) != 0)
      {
        arrayOfInt2 = new int[_count];
        System.arraycopy(_indices, 0, arrayOfInt2, 0, _count);
      }
      if (paramStandardGlyphVector == null) {
        paramStandardGlyphVector = new StandardGlyphVector(paramFont, paramFontRenderContext, arrayOfInt1, arrayOfFloat, arrayOfInt2, _flags);
      } else {
        paramStandardGlyphVector.initGlyphVector(paramFont, paramFontRenderContext, arrayOfInt1, arrayOfFloat, arrayOfInt2, _flags);
      }
      return paramStandardGlyphVector;
    }
  }
  
  public static abstract interface LayoutEngine
  {
    public abstract void layout(FontStrikeDesc paramFontStrikeDesc, float[] paramArrayOfFloat, int paramInt1, int paramInt2, TextRecord paramTextRecord, int paramInt3, Point2D.Float paramFloat, GlyphLayout.GVData paramGVData);
  }
  
  public static abstract interface LayoutEngineFactory
  {
    public abstract GlyphLayout.LayoutEngine getEngine(Font2D paramFont2D, int paramInt1, int paramInt2);
    
    public abstract GlyphLayout.LayoutEngine getEngine(GlyphLayout.LayoutEngineKey paramLayoutEngineKey);
  }
  
  public static final class LayoutEngineKey
  {
    private Font2D font;
    private int script;
    private int lang;
    
    LayoutEngineKey() {}
    
    LayoutEngineKey(Font2D paramFont2D, int paramInt1, int paramInt2)
    {
      init(paramFont2D, paramInt1, paramInt2);
    }
    
    void init(Font2D paramFont2D, int paramInt1, int paramInt2)
    {
      font = paramFont2D;
      script = paramInt1;
      lang = paramInt2;
    }
    
    LayoutEngineKey copy()
    {
      return new LayoutEngineKey(font, script, lang);
    }
    
    Font2D font()
    {
      return font;
    }
    
    int script()
    {
      return script;
    }
    
    int lang()
    {
      return lang;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      try
      {
        LayoutEngineKey localLayoutEngineKey = (LayoutEngineKey)paramObject;
        return (script == script) && (lang == lang) && (font.equals(font));
      }
      catch (ClassCastException localClassCastException) {}
      return false;
    }
    
    public int hashCode()
    {
      return script ^ lang ^ font.hashCode();
    }
  }
  
  private static final class SDCache
  {
    public Font key_font;
    public FontRenderContext key_frc;
    public AffineTransform dtx;
    public AffineTransform invdtx;
    public AffineTransform gtx;
    public Point2D.Float delta;
    public FontStrikeDesc sd;
    private static final Point2D.Float ZERO_DELTA = new Point2D.Float();
    private static SoftReference<ConcurrentHashMap<SDKey, SDCache>> cacheRef;
    
    private SDCache(Font paramFont, FontRenderContext paramFontRenderContext)
    {
      key_font = paramFont;
      key_frc = paramFontRenderContext;
      dtx = paramFontRenderContext.getTransform();
      dtx.setTransform(dtx.getScaleX(), dtx.getShearY(), dtx.getShearX(), dtx.getScaleY(), 0.0D, 0.0D);
      if (!dtx.isIdentity()) {
        try
        {
          invdtx = dtx.createInverse();
        }
        catch (NoninvertibleTransformException localNoninvertibleTransformException)
        {
          throw new InternalError(localNoninvertibleTransformException);
        }
      }
      float f = paramFont.getSize2D();
      if (paramFont.isTransformed())
      {
        gtx = paramFont.getTransform();
        gtx.scale(f, f);
        delta = new Point2D.Float((float)gtx.getTranslateX(), (float)gtx.getTranslateY());
        gtx.setTransform(gtx.getScaleX(), gtx.getShearY(), gtx.getShearX(), gtx.getScaleY(), 0.0D, 0.0D);
        gtx.preConcatenate(dtx);
      }
      else
      {
        delta = ZERO_DELTA;
        gtx = new AffineTransform(dtx);
        gtx.scale(f, f);
      }
      int i = FontStrikeDesc.getAAHintIntVal(paramFontRenderContext.getAntiAliasingHint(), FontUtilities.getFont2D(paramFont), (int)Math.abs(f));
      int j = FontStrikeDesc.getFMHintIntVal(paramFontRenderContext.getFractionalMetricsHint());
      sd = new FontStrikeDesc(dtx, gtx, paramFont.getStyle(), i, j);
    }
    
    public static SDCache get(Font paramFont, FontRenderContext paramFontRenderContext)
    {
      if (paramFontRenderContext.isTransformed())
      {
        localObject = paramFontRenderContext.getTransform();
        if ((((AffineTransform)localObject).getTranslateX() != 0.0D) || (((AffineTransform)localObject).getTranslateY() != 0.0D))
        {
          localObject = new AffineTransform(((AffineTransform)localObject).getScaleX(), ((AffineTransform)localObject).getShearY(), ((AffineTransform)localObject).getShearX(), ((AffineTransform)localObject).getScaleY(), 0.0D, 0.0D);
          paramFontRenderContext = new FontRenderContext((AffineTransform)localObject, paramFontRenderContext.getAntiAliasingHint(), paramFontRenderContext.getFractionalMetricsHint());
        }
      }
      Object localObject = new SDKey(paramFont, paramFontRenderContext);
      ConcurrentHashMap localConcurrentHashMap = null;
      SDCache localSDCache = null;
      if (cacheRef != null)
      {
        localConcurrentHashMap = (ConcurrentHashMap)cacheRef.get();
        if (localConcurrentHashMap != null) {
          localSDCache = (SDCache)localConcurrentHashMap.get(localObject);
        }
      }
      if (localSDCache == null)
      {
        localSDCache = new SDCache(paramFont, paramFontRenderContext);
        if (localConcurrentHashMap == null)
        {
          localConcurrentHashMap = new ConcurrentHashMap(10);
          cacheRef = new SoftReference(localConcurrentHashMap);
        }
        else if (localConcurrentHashMap.size() >= 512)
        {
          localConcurrentHashMap.clear();
        }
        localConcurrentHashMap.put(localObject, localSDCache);
      }
      return localSDCache;
    }
    
    private static final class SDKey
    {
      private final Font font;
      private final FontRenderContext frc;
      private final int hash;
      
      SDKey(Font paramFont, FontRenderContext paramFontRenderContext)
      {
        font = paramFont;
        frc = paramFontRenderContext;
        hash = (paramFont.hashCode() ^ paramFontRenderContext.hashCode());
      }
      
      public int hashCode()
      {
        return hash;
      }
      
      public boolean equals(Object paramObject)
      {
        try
        {
          SDKey localSDKey = (SDKey)paramObject;
          return (hash == hash) && (font.equals(font)) && (frc.equals(frc));
        }
        catch (ClassCastException localClassCastException) {}
        return false;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\GlyphLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */