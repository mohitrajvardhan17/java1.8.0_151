package sun.font;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import sun.misc.Unsafe;
import sun.util.logging.PlatformLogger;

public class FileFontStrike
  extends PhysicalStrike
{
  static final int INVISIBLE_GLYPHS = 65534;
  private FileFont fileFont;
  private static final int UNINITIALISED = 0;
  private static final int INTARRAY = 1;
  private static final int LONGARRAY = 2;
  private static final int SEGINTARRAY = 3;
  private static final int SEGLONGARRAY = 4;
  private volatile int glyphCacheFormat = 0;
  private static final int SEGSHIFT = 5;
  private static final int SEGSIZE = 32;
  private boolean segmentedCache;
  private int[][] segIntGlyphImages;
  private long[][] segLongGlyphImages;
  private float[] horizontalAdvances;
  private float[][] segHorizontalAdvances;
  ConcurrentHashMap<Integer, Rectangle2D.Float> boundsMap;
  SoftReference<ConcurrentHashMap<Integer, Point2D.Float>> glyphMetricsMapRef;
  AffineTransform invertDevTx;
  boolean useNatives;
  NativeStrike[] nativeStrikes;
  private int intPtSize;
  private static boolean isXPorLater = false;
  private WeakReference<ConcurrentHashMap<Integer, GeneralPath>> outlineMapRef;
  
  private static native boolean initNative();
  
  FileFontStrike(FileFont paramFileFont, FontStrikeDesc paramFontStrikeDesc)
  {
    super(paramFileFont, paramFontStrikeDesc);
    fileFont = paramFileFont;
    if (style != style)
    {
      if (((style & 0x2) == 2) && ((style & 0x2) == 0))
      {
        algoStyle = true;
        italic = 0.7F;
      }
      if (((style & 0x1) == 1) && ((style & 0x1) == 0))
      {
        algoStyle = true;
        boldness = 1.33F;
      }
    }
    double[] arrayOfDouble = new double[4];
    AffineTransform localAffineTransform = glyphTx;
    localAffineTransform.getMatrix(arrayOfDouble);
    if ((!devTx.isIdentity()) && (devTx.getType() != 1)) {
      try
      {
        invertDevTx = devTx.createInverse();
      }
      catch (NoninvertibleTransformException localNoninvertibleTransformException) {}
    }
    boolean bool = (aaHint != 1) && (familyName.startsWith("Amble"));
    if ((Double.isNaN(arrayOfDouble[0])) || (Double.isNaN(arrayOfDouble[1])) || (Double.isNaN(arrayOfDouble[2])) || (Double.isNaN(arrayOfDouble[3])) || (paramFileFont.getScaler() == null)) {
      pScalerContext = NullFontScaler.getNullScalerContext();
    } else {
      pScalerContext = paramFileFont.getScaler().createScalerContext(arrayOfDouble, aaHint, fmHint, boldness, italic, bool);
    }
    mapper = paramFileFont.getMapper();
    int i = mapper.getNumGlyphs();
    float f = (float)arrayOfDouble[3];
    int j = intPtSize = (int)f;
    int k = (localAffineTransform.getType() & 0x7C) == 0 ? 1 : 0;
    segmentedCache = ((i > 256) || ((i > 64) && ((k == 0) || (f != j) || (j < 6) || (j > 36))));
    if (pScalerContext == 0L)
    {
      disposer = new FontStrikeDisposer(paramFileFont, paramFontStrikeDesc);
      initGlyphCache();
      pScalerContext = NullFontScaler.getNullScalerContext();
      SunFontManager.getInstance().deRegisterBadFont(paramFileFont);
      return;
    }
    if ((FontUtilities.isWindows) && (isXPorLater) && (!FontUtilities.useT2K) && (!GraphicsEnvironment.isHeadless()) && (!useJavaRasterizer) && ((aaHint == 4) || (aaHint == 5)) && (arrayOfDouble[1] == 0.0D) && (arrayOfDouble[2] == 0.0D) && (arrayOfDouble[0] == arrayOfDouble[3]) && (arrayOfDouble[0] >= 3.0D) && (arrayOfDouble[0] <= 100.0D) && (!((TrueTypeFont)paramFileFont).useEmbeddedBitmapsForSize(intPtSize)))
    {
      useNatives = true;
    }
    else if ((paramFileFont.checkUseNatives()) && (aaHint == 0) && (!algoStyle) && (arrayOfDouble[1] == 0.0D) && (arrayOfDouble[2] == 0.0D) && (arrayOfDouble[0] >= 6.0D) && (arrayOfDouble[0] <= 36.0D) && (arrayOfDouble[0] == arrayOfDouble[3]))
    {
      useNatives = true;
      int m = nativeFonts.length;
      nativeStrikes = new NativeStrike[m];
      for (int n = 0; n < m; n++) {
        nativeStrikes[n] = new NativeStrike(nativeFonts[n], paramFontStrikeDesc, false);
      }
    }
    if ((FontUtilities.isLogging()) && (FontUtilities.isWindows)) {
      FontUtilities.getLogger().info("Strike for " + paramFileFont + " at size = " + intPtSize + " use natives = " + useNatives + " useJavaRasteriser = " + useJavaRasterizer + " AAHint = " + aaHint + " Has Embedded bitmaps = " + ((TrueTypeFont)paramFileFont).useEmbeddedBitmapsForSize(intPtSize));
    }
    disposer = new FontStrikeDisposer(paramFileFont, paramFontStrikeDesc, pScalerContext);
    double d = 48.0D;
    getImageWithAdvance = ((Math.abs(localAffineTransform.getScaleX()) <= d) && (Math.abs(localAffineTransform.getScaleY()) <= d) && (Math.abs(localAffineTransform.getShearX()) <= d) && (Math.abs(localAffineTransform.getShearY()) <= d));
    if (!getImageWithAdvance)
    {
      int i1;
      if (!segmentedCache)
      {
        horizontalAdvances = new float[i];
        for (i1 = 0; i1 < i; i1++) {
          horizontalAdvances[i1] = Float.MAX_VALUE;
        }
      }
      else
      {
        i1 = (i + 32 - 1) / 32;
        segHorizontalAdvances = new float[i1][];
      }
    }
  }
  
  public int getNumGlyphs()
  {
    return fileFont.getNumGlyphs();
  }
  
  long getGlyphImageFromNative(int paramInt)
  {
    if (FontUtilities.isWindows) {
      return getGlyphImageFromWindows(paramInt);
    }
    return getGlyphImageFromX11(paramInt);
  }
  
  private native long _getGlyphImageFromWindows(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  long getGlyphImageFromWindows(int paramInt)
  {
    String str = fileFont.getFamilyName(null);
    int i = desc.style & 0x1 | desc.style & 0x2 | fileFont.getStyle();
    int j = intPtSize;
    long l = _getGlyphImageFromWindows(str, i, j, paramInt, desc.fmHint == 2);
    if (l != 0L)
    {
      float f = getGlyphAdvance(paramInt, false);
      StrikeCache.unsafe.putFloat(l + StrikeCache.xAdvanceOffset, f);
      return l;
    }
    return fileFont.getGlyphImage(pScalerContext, paramInt);
  }
  
  long getGlyphImageFromX11(int paramInt)
  {
    char c = fileFont.glyphToCharMap[paramInt];
    for (int i = 0; i < nativeStrikes.length; i++)
    {
      CharToGlyphMapper localCharToGlyphMapper = fileFont.nativeFonts[i].getMapper();
      int j = localCharToGlyphMapper.charToGlyph(c) & 0xFFFF;
      if (j != localCharToGlyphMapper.getMissingGlyphCode())
      {
        long l = nativeStrikes[i].getGlyphImagePtrNoCache(j);
        if (l != 0L) {
          return l;
        }
      }
    }
    return fileFont.getGlyphImage(pScalerContext, paramInt);
  }
  
  long getGlyphImagePtr(int paramInt)
  {
    if (paramInt >= 65534) {
      return StrikeCache.invisibleGlyphPtr;
    }
    long l = 0L;
    if ((l = getCachedGlyphPtr(paramInt)) != 0L) {
      return l;
    }
    if (useNatives)
    {
      l = getGlyphImageFromNative(paramInt);
      if ((l == 0L) && (FontUtilities.isLogging())) {
        FontUtilities.getLogger().info("Strike for " + fileFont + " at size = " + intPtSize + " couldn't get native glyph for code = " + paramInt);
      }
    }
    if (l == 0L) {
      l = fileFont.getGlyphImage(pScalerContext, paramInt);
    }
    return setCachedGlyphPtr(paramInt, l);
  }
  
  void getGlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      int j = paramArrayOfInt[i];
      if (j >= 65534)
      {
        paramArrayOfLong[i] = StrikeCache.invisibleGlyphPtr;
      }
      else if ((paramArrayOfLong[i] = getCachedGlyphPtr(j)) == 0L)
      {
        long l = 0L;
        if (useNatives) {
          l = getGlyphImageFromNative(j);
        }
        if (l == 0L) {
          l = fileFont.getGlyphImage(pScalerContext, j);
        }
        paramArrayOfLong[i] = setCachedGlyphPtr(j, l);
      }
    }
  }
  
  int getSlot0GlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt)
  {
    int i = 0;
    for (int j = 0; j < paramInt; j++)
    {
      int k = paramArrayOfInt[j];
      if (k >>> 24 != 0) {
        return i;
      }
      i++;
      if (k >= 65534)
      {
        paramArrayOfLong[j] = StrikeCache.invisibleGlyphPtr;
      }
      else if ((paramArrayOfLong[j] = getCachedGlyphPtr(k)) == 0L)
      {
        long l = 0L;
        if (useNatives) {
          l = getGlyphImageFromNative(k);
        }
        if (l == 0L) {
          l = fileFont.getGlyphImage(pScalerContext, k);
        }
        paramArrayOfLong[j] = setCachedGlyphPtr(k, l);
      }
    }
    return i;
  }
  
  long getCachedGlyphPtr(int paramInt)
  {
    try
    {
      return getCachedGlyphPtrInternal(paramInt);
    }
    catch (Exception localException)
    {
      NullFontScaler localNullFontScaler = (NullFontScaler)FontScaler.getNullScaler();
      long l = NullFontScaler.getNullScalerContext();
      return localNullFontScaler.getGlyphImage(l, paramInt);
    }
  }
  
  private long getCachedGlyphPtrInternal(int paramInt)
  {
    int i;
    int j;
    switch (glyphCacheFormat)
    {
    case 1: 
      return intGlyphImages[paramInt] & 0xFFFFFFFF;
    case 3: 
      i = paramInt >> 5;
      if (segIntGlyphImages[i] != null)
      {
        j = paramInt % 32;
        return segIntGlyphImages[i][j] & 0xFFFFFFFF;
      }
      return 0L;
    case 2: 
      return longGlyphImages[paramInt];
    case 4: 
      i = paramInt >> 5;
      if (segLongGlyphImages[i] != null)
      {
        j = paramInt % 32;
        return segLongGlyphImages[i][j];
      }
      return 0L;
    }
    return 0L;
  }
  
  private synchronized long setCachedGlyphPtr(int paramInt, long paramLong)
  {
    try
    {
      return setCachedGlyphPtrInternal(paramInt, paramLong);
    }
    catch (Exception localException)
    {
      switch (glyphCacheFormat)
      {
      case 1: 
      case 3: 
        StrikeCache.freeIntPointer((int)paramLong);
        break;
      case 2: 
      case 4: 
        StrikeCache.freeLongPointer(paramLong);
      }
      NullFontScaler localNullFontScaler = (NullFontScaler)FontScaler.getNullScaler();
      long l = NullFontScaler.getNullScalerContext();
      return localNullFontScaler.getGlyphImage(l, paramInt);
    }
  }
  
  private long setCachedGlyphPtrInternal(int paramInt, long paramLong)
  {
    int i;
    int j;
    switch (glyphCacheFormat)
    {
    case 1: 
      if (intGlyphImages[paramInt] == 0)
      {
        intGlyphImages[paramInt] = ((int)paramLong);
        return paramLong;
      }
      StrikeCache.freeIntPointer((int)paramLong);
      return intGlyphImages[paramInt] & 0xFFFFFFFF;
    case 3: 
      i = paramInt >> 5;
      j = paramInt % 32;
      if (segIntGlyphImages[i] == null) {
        segIntGlyphImages[i] = new int[32];
      }
      if (segIntGlyphImages[i][j] == 0)
      {
        segIntGlyphImages[i][j] = ((int)paramLong);
        return paramLong;
      }
      StrikeCache.freeIntPointer((int)paramLong);
      return segIntGlyphImages[i][j] & 0xFFFFFFFF;
    case 2: 
      if (longGlyphImages[paramInt] == 0L)
      {
        longGlyphImages[paramInt] = paramLong;
        return paramLong;
      }
      StrikeCache.freeLongPointer(paramLong);
      return longGlyphImages[paramInt];
    case 4: 
      i = paramInt >> 5;
      j = paramInt % 32;
      if (segLongGlyphImages[i] == null) {
        segLongGlyphImages[i] = new long[32];
      }
      if (segLongGlyphImages[i][j] == 0L)
      {
        segLongGlyphImages[i][j] = paramLong;
        return paramLong;
      }
      StrikeCache.freeLongPointer(paramLong);
      return segLongGlyphImages[i][j];
    }
    initGlyphCache();
    return setCachedGlyphPtr(paramInt, paramLong);
  }
  
  private synchronized void initGlyphCache()
  {
    int i = mapper.getNumGlyphs();
    int j = 0;
    if (segmentedCache)
    {
      int k = (i + 32 - 1) / 32;
      if (longAddresses)
      {
        j = 4;
        segLongGlyphImages = new long[k][];
        disposer.segLongGlyphImages = segLongGlyphImages;
      }
      else
      {
        j = 3;
        segIntGlyphImages = new int[k][];
        disposer.segIntGlyphImages = segIntGlyphImages;
      }
    }
    else if (longAddresses)
    {
      j = 2;
      longGlyphImages = new long[i];
      disposer.longGlyphImages = longGlyphImages;
    }
    else
    {
      j = 1;
      intGlyphImages = new int[i];
      disposer.intGlyphImages = intGlyphImages;
    }
    glyphCacheFormat = j;
  }
  
  float getGlyphAdvance(int paramInt)
  {
    return getGlyphAdvance(paramInt, true);
  }
  
  private float getGlyphAdvance(int paramInt, boolean paramBoolean)
  {
    if (paramInt >= 65534) {
      return 0.0F;
    }
    float f;
    if (horizontalAdvances != null)
    {
      f = horizontalAdvances[paramInt];
      if (f != Float.MAX_VALUE)
      {
        if ((!paramBoolean) && (invertDevTx != null))
        {
          Point2D.Float localFloat1 = new Point2D.Float(f, 0.0F);
          desc.devTx.deltaTransform(localFloat1, localFloat1);
          return x;
        }
        return f;
      }
    }
    else if ((segmentedCache) && (segHorizontalAdvances != null))
    {
      int i = paramInt >> 5;
      float[] arrayOfFloat = segHorizontalAdvances[i];
      if (arrayOfFloat != null)
      {
        f = arrayOfFloat[(paramInt % 32)];
        if (f != Float.MAX_VALUE)
        {
          if ((!paramBoolean) && (invertDevTx != null))
          {
            Point2D.Float localFloat3 = new Point2D.Float(f, 0.0F);
            desc.devTx.deltaTransform(localFloat3, localFloat3);
            return x;
          }
          return f;
        }
      }
    }
    if ((!paramBoolean) && (invertDevTx != null))
    {
      Point2D.Float localFloat2 = new Point2D.Float();
      fileFont.getGlyphMetrics(pScalerContext, paramInt, localFloat2);
      return x;
    }
    if ((invertDevTx != null) || (!paramBoolean))
    {
      f = getGlyphMetricsx;
    }
    else
    {
      long l;
      if (getImageWithAdvance) {
        l = getGlyphImagePtr(paramInt);
      } else {
        l = getCachedGlyphPtr(paramInt);
      }
      if (l != 0L) {
        f = StrikeCache.unsafe.getFloat(l + StrikeCache.xAdvanceOffset);
      } else {
        f = fileFont.getGlyphAdvance(pScalerContext, paramInt);
      }
    }
    if (horizontalAdvances != null)
    {
      horizontalAdvances[paramInt] = f;
    }
    else if ((segmentedCache) && (segHorizontalAdvances != null))
    {
      int j = paramInt >> 5;
      int k = paramInt % 32;
      if (segHorizontalAdvances[j] == null)
      {
        segHorizontalAdvances[j] = new float[32];
        for (int m = 0; m < 32; m++) {
          segHorizontalAdvances[j][m] = Float.MAX_VALUE;
        }
      }
      segHorizontalAdvances[j][k] = f;
    }
    return f;
  }
  
  float getCodePointAdvance(int paramInt)
  {
    return getGlyphAdvance(mapper.charToGlyph(paramInt));
  }
  
  void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle)
  {
    long l = getGlyphImagePtr(paramInt);
    if (l == 0L)
    {
      x = ((int)Math.floor(x));
      y = ((int)Math.floor(y));
      width = (height = 0);
      return;
    }
    float f1 = StrikeCache.unsafe.getFloat(l + StrikeCache.topLeftXOffset);
    float f2 = StrikeCache.unsafe.getFloat(l + StrikeCache.topLeftYOffset);
    x = ((int)Math.floor(x + f1));
    y = ((int)Math.floor(y + f2));
    width = (StrikeCache.unsafe.getShort(l + StrikeCache.widthOffset) & 0xFFFF);
    height = (StrikeCache.unsafe.getShort(l + StrikeCache.heightOffset) & 0xFFFF);
    if (((desc.aaHint == 4) || (desc.aaHint == 5)) && (f1 <= -2.0F))
    {
      int i = getGlyphImageMinX(l, x);
      if (i > x)
      {
        x += 1;
        width -= 1;
      }
    }
  }
  
  private int getGlyphImageMinX(long paramLong, int paramInt)
  {
    int i = StrikeCache.unsafe.getChar(paramLong + StrikeCache.widthOffset);
    int j = StrikeCache.unsafe.getChar(paramLong + StrikeCache.heightOffset);
    int k = StrikeCache.unsafe.getChar(paramLong + StrikeCache.rowBytesOffset);
    if (k == i) {
      return paramInt;
    }
    long l = StrikeCache.unsafe.getAddress(paramLong + StrikeCache.pixelDataOffset);
    if (l == 0L) {
      return paramInt;
    }
    for (int m = 0; m < j; m++) {
      for (int n = 0; n < 3; n++) {
        if (StrikeCache.unsafe.getByte(l + m * k + n) != 0) {
          return paramInt;
        }
      }
    }
    return paramInt + 1;
  }
  
  StrikeMetrics getFontMetrics()
  {
    if (strikeMetrics == null)
    {
      strikeMetrics = fileFont.getFontMetrics(pScalerContext);
      if (invertDevTx != null) {
        strikeMetrics.convertToUserSpace(invertDevTx);
      }
    }
    return strikeMetrics;
  }
  
  Point2D.Float getGlyphMetrics(int paramInt)
  {
    return getGlyphMetrics(paramInt, true);
  }
  
  private Point2D.Float getGlyphMetrics(int paramInt, boolean paramBoolean)
  {
    Point2D.Float localFloat1 = new Point2D.Float();
    if (paramInt >= 65534) {
      return localFloat1;
    }
    long l;
    if ((getImageWithAdvance) && (paramBoolean)) {
      l = getGlyphImagePtr(paramInt);
    } else {
      l = getCachedGlyphPtr(paramInt);
    }
    if (l != 0L)
    {
      localFloat1 = new Point2D.Float();
      x = StrikeCache.unsafe.getFloat(l + StrikeCache.xAdvanceOffset);
      y = StrikeCache.unsafe.getFloat(l + StrikeCache.yAdvanceOffset);
      if (invertDevTx != null) {
        invertDevTx.deltaTransform(localFloat1, localFloat1);
      }
    }
    else
    {
      Integer localInteger = Integer.valueOf(paramInt);
      Point2D.Float localFloat2 = null;
      ConcurrentHashMap localConcurrentHashMap = null;
      if (glyphMetricsMapRef != null) {
        localConcurrentHashMap = (ConcurrentHashMap)glyphMetricsMapRef.get();
      }
      if (localConcurrentHashMap != null)
      {
        localFloat2 = (Point2D.Float)localConcurrentHashMap.get(localInteger);
        if (localFloat2 != null)
        {
          x = x;
          y = y;
          return localFloat1;
        }
      }
      if (localFloat2 == null)
      {
        fileFont.getGlyphMetrics(pScalerContext, paramInt, localFloat1);
        if (invertDevTx != null) {
          invertDevTx.deltaTransform(localFloat1, localFloat1);
        }
        localFloat2 = new Point2D.Float(x, y);
        if (localConcurrentHashMap == null)
        {
          localConcurrentHashMap = new ConcurrentHashMap();
          glyphMetricsMapRef = new SoftReference(localConcurrentHashMap);
        }
        localConcurrentHashMap.put(localInteger, localFloat2);
      }
    }
    return localFloat1;
  }
  
  Point2D.Float getCharMetrics(char paramChar)
  {
    return getGlyphMetrics(mapper.charToGlyph(paramChar));
  }
  
  Rectangle2D.Float getGlyphOutlineBounds(int paramInt)
  {
    if (boundsMap == null) {
      boundsMap = new ConcurrentHashMap();
    }
    Integer localInteger = Integer.valueOf(paramInt);
    Rectangle2D.Float localFloat = (Rectangle2D.Float)boundsMap.get(localInteger);
    if (localFloat == null)
    {
      localFloat = fileFont.getGlyphOutlineBounds(pScalerContext, paramInt);
      boundsMap.put(localInteger, localFloat);
    }
    return localFloat;
  }
  
  public Rectangle2D getOutlineBounds(int paramInt)
  {
    return fileFont.getGlyphOutlineBounds(pScalerContext, paramInt);
  }
  
  GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2)
  {
    GeneralPath localGeneralPath = null;
    ConcurrentHashMap localConcurrentHashMap = null;
    if (outlineMapRef != null)
    {
      localConcurrentHashMap = (ConcurrentHashMap)outlineMapRef.get();
      if (localConcurrentHashMap != null) {
        localGeneralPath = (GeneralPath)localConcurrentHashMap.get(Integer.valueOf(paramInt));
      }
    }
    if (localGeneralPath == null)
    {
      localGeneralPath = fileFont.getGlyphOutline(pScalerContext, paramInt, 0.0F, 0.0F);
      if (localConcurrentHashMap == null)
      {
        localConcurrentHashMap = new ConcurrentHashMap();
        outlineMapRef = new WeakReference(localConcurrentHashMap);
      }
      localConcurrentHashMap.put(Integer.valueOf(paramInt), localGeneralPath);
    }
    localGeneralPath = (GeneralPath)localGeneralPath.clone();
    if ((paramFloat1 != 0.0F) || (paramFloat2 != 0.0F)) {
      localGeneralPath.transform(AffineTransform.getTranslateInstance(paramFloat1, paramFloat2));
    }
    return localGeneralPath;
  }
  
  GeneralPath getGlyphVectorOutline(int[] paramArrayOfInt, float paramFloat1, float paramFloat2)
  {
    return fileFont.getGlyphVectorOutline(pScalerContext, paramArrayOfInt, paramArrayOfInt.length, paramFloat1, paramFloat2);
  }
  
  protected void adjustPoint(Point2D.Float paramFloat)
  {
    if (invertDevTx != null) {
      invertDevTx.deltaTransform(paramFloat, paramFloat);
    }
  }
  
  static
  {
    if ((FontUtilities.isWindows) && (!FontUtilities.useT2K) && (!GraphicsEnvironment.isHeadless())) {
      isXPorLater = initNative();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FileFontStrike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */