package sun.font;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.lang.ref.SoftReference;
import java.text.CharacterIterator;
import sun.java2d.loops.FontInfo;

public class StandardGlyphVector
  extends GlyphVector
{
  private Font font;
  private FontRenderContext frc;
  private int[] glyphs;
  private int[] userGlyphs;
  private float[] positions;
  private int[] charIndices;
  private int flags;
  private static final int UNINITIALIZED_FLAGS = -1;
  private GlyphTransformInfo gti;
  private AffineTransform ftx;
  private AffineTransform dtx;
  private AffineTransform invdtx;
  private AffineTransform frctx;
  private Font2D font2D;
  private SoftReference fsref;
  private SoftReference lbcacheRef;
  private SoftReference vbcacheRef;
  public static final int FLAG_USES_VERTICAL_BASELINE = 128;
  public static final int FLAG_USES_VERTICAL_METRICS = 256;
  public static final int FLAG_USES_ALTERNATE_ORIENTATION = 512;
  
  public StandardGlyphVector(Font paramFont, String paramString, FontRenderContext paramFontRenderContext)
  {
    init(paramFont, paramString.toCharArray(), 0, paramString.length(), paramFontRenderContext, -1);
  }
  
  public StandardGlyphVector(Font paramFont, char[] paramArrayOfChar, FontRenderContext paramFontRenderContext)
  {
    init(paramFont, paramArrayOfChar, 0, paramArrayOfChar.length, paramFontRenderContext, -1);
  }
  
  public StandardGlyphVector(Font paramFont, char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
  {
    init(paramFont, paramArrayOfChar, paramInt1, paramInt2, paramFontRenderContext, -1);
  }
  
  private float getTracking(Font paramFont)
  {
    if (paramFont.hasLayoutAttributes())
    {
      AttributeValues localAttributeValues = ((AttributeMap)paramFont.getAttributes()).getValues();
      return localAttributeValues.getTracking();
    }
    return 0.0F;
  }
  
  public StandardGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, int[] paramArrayOfInt1, float[] paramArrayOfFloat, int[] paramArrayOfInt2, int paramInt)
  {
    initGlyphVector(paramFont, paramFontRenderContext, paramArrayOfInt1, paramArrayOfFloat, paramArrayOfInt2, paramInt);
    float f1 = getTracking(paramFont);
    if (f1 != 0.0F)
    {
      f1 *= paramFont.getSize2D();
      Point2D.Float localFloat = new Point2D.Float(f1, 0.0F);
      if (paramFont.isTransformed())
      {
        localObject = paramFont.getTransform();
        ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
      }
      Object localObject = FontUtilities.getFont2D(paramFont);
      FontStrike localFontStrike = ((Font2D)localObject).getStrike(paramFont, paramFontRenderContext);
      float[] arrayOfFloat = { x, y };
      for (int i = 0; i < arrayOfFloat.length; i++)
      {
        float f2 = arrayOfFloat[i];
        if (f2 != 0.0F)
        {
          float f3 = 0.0F;
          int j = i;
          int k = 0;
          while (k < paramArrayOfInt1.length)
          {
            if (localFontStrike.getGlyphAdvance(paramArrayOfInt1[(k++)]) != 0.0F)
            {
              paramArrayOfFloat[j] += f3;
              f3 += f2;
            }
            j += 2;
          }
          paramArrayOfFloat[(paramArrayOfFloat.length - 2 + i)] += f3;
        }
      }
    }
  }
  
  public void initGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, int[] paramArrayOfInt1, float[] paramArrayOfFloat, int[] paramArrayOfInt2, int paramInt)
  {
    font = paramFont;
    frc = paramFontRenderContext;
    glyphs = paramArrayOfInt1;
    userGlyphs = paramArrayOfInt1;
    positions = paramArrayOfFloat;
    charIndices = paramArrayOfInt2;
    flags = paramInt;
    initFontData();
  }
  
  public StandardGlyphVector(Font paramFont, CharacterIterator paramCharacterIterator, FontRenderContext paramFontRenderContext)
  {
    int i = paramCharacterIterator.getBeginIndex();
    char[] arrayOfChar = new char[paramCharacterIterator.getEndIndex() - i];
    for (int j = paramCharacterIterator.first(); j != 65535; j = paramCharacterIterator.next()) {
      arrayOfChar[(paramCharacterIterator.getIndex() - i)] = j;
    }
    init(paramFont, arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext, -1);
  }
  
  public StandardGlyphVector(Font paramFont, int[] paramArrayOfInt, FontRenderContext paramFontRenderContext)
  {
    font = paramFont;
    frc = paramFontRenderContext;
    flags = -1;
    initFontData();
    userGlyphs = paramArrayOfInt;
    glyphs = getValidatedGlyphs(userGlyphs);
  }
  
  public static StandardGlyphVector getStandardGV(GlyphVector paramGlyphVector, FontInfo paramFontInfo)
  {
    if (aaHint == 2)
    {
      Object localObject = paramGlyphVector.getFontRenderContext().getAntiAliasingHint();
      if ((localObject != RenderingHints.VALUE_TEXT_ANTIALIAS_ON) && (localObject != RenderingHints.VALUE_TEXT_ANTIALIAS_GASP))
      {
        FontRenderContext localFontRenderContext = paramGlyphVector.getFontRenderContext();
        localFontRenderContext = new FontRenderContext(localFontRenderContext.getTransform(), RenderingHints.VALUE_TEXT_ANTIALIAS_ON, localFontRenderContext.getFractionalMetricsHint());
        return new StandardGlyphVector(paramGlyphVector, localFontRenderContext);
      }
    }
    if ((paramGlyphVector instanceof StandardGlyphVector)) {
      return (StandardGlyphVector)paramGlyphVector;
    }
    return new StandardGlyphVector(paramGlyphVector, paramGlyphVector.getFontRenderContext());
  }
  
  public Font getFont()
  {
    return font;
  }
  
  public FontRenderContext getFontRenderContext()
  {
    return frc;
  }
  
  public void performDefaultLayout()
  {
    positions = null;
    if (getTracking(font) == 0.0F) {
      clearFlags(2);
    }
  }
  
  public int getNumGlyphs()
  {
    return glyphs.length;
  }
  
  public int getGlyphCode(int paramInt)
  {
    return userGlyphs[paramInt];
  }
  
  public int[] getGlyphCodes(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("count = " + paramInt2);
    }
    if (paramInt1 < 0) {
      throw new IndexOutOfBoundsException("start = " + paramInt1);
    }
    if (paramInt1 > glyphs.length - paramInt2) {
      throw new IndexOutOfBoundsException("start + count = " + (paramInt1 + paramInt2));
    }
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt2];
    }
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfInt[i] = userGlyphs[(i + paramInt1)];
    }
    return paramArrayOfInt;
  }
  
  public int getGlyphCharIndex(int paramInt)
  {
    if ((paramInt < 0) && (paramInt >= glyphs.length)) {
      throw new IndexOutOfBoundsException("" + paramInt);
    }
    if (charIndices == null)
    {
      if ((getLayoutFlags() & 0x4) != 0) {
        return glyphs.length - 1 - paramInt;
      }
      return paramInt;
    }
    return charIndices[paramInt];
  }
  
  public int[] getGlyphCharIndices(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > glyphs.length - paramInt1)) {
      throw new IndexOutOfBoundsException("" + paramInt1 + ", " + paramInt2);
    }
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt2];
    }
    int i;
    if (charIndices == null)
    {
      int j;
      if ((getLayoutFlags() & 0x4) != 0)
      {
        i = 0;
        for (j = glyphs.length - 1 - paramInt1; i < paramInt2; j--)
        {
          paramArrayOfInt[i] = j;
          i++;
        }
      }
      else
      {
        i = 0;
        for (j = paramInt1; i < paramInt2; j++)
        {
          paramArrayOfInt[i] = j;
          i++;
        }
      }
    }
    else
    {
      for (i = 0; i < paramInt2; i++) {
        paramArrayOfInt[i] = charIndices[(i + paramInt1)];
      }
    }
    return paramArrayOfInt;
  }
  
  public Rectangle2D getLogicalBounds()
  {
    setFRCTX();
    initPositions();
    LineMetrics localLineMetrics = font.getLineMetrics("", frc);
    float f1 = 0.0F;
    float f2 = -localLineMetrics.getAscent();
    float f3 = 0.0F;
    float f4 = localLineMetrics.getDescent() + localLineMetrics.getLeading();
    if (glyphs.length > 0) {
      f3 = positions[(positions.length - 2)];
    }
    return new Rectangle2D.Float(f1, f2, f3 - f1, f4 - f2);
  }
  
  public Rectangle2D getVisualBounds()
  {
    Object localObject = null;
    for (int i = 0; i < glyphs.length; i++)
    {
      Rectangle2D localRectangle2D = getGlyphVisualBounds(i).getBounds2D();
      if (!localRectangle2D.isEmpty()) {
        if (localObject == null) {
          localObject = localRectangle2D;
        } else {
          Rectangle2D.union((Rectangle2D)localObject, localRectangle2D, (Rectangle2D)localObject);
        }
      }
    }
    if (localObject == null) {
      localObject = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
    }
    return (Rectangle2D)localObject;
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2)
  {
    return getGlyphsPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2, 0, glyphs.length);
  }
  
  public Shape getOutline()
  {
    return getGlyphsOutline(0, glyphs.length, 0.0F, 0.0F);
  }
  
  public Shape getOutline(float paramFloat1, float paramFloat2)
  {
    return getGlyphsOutline(0, glyphs.length, paramFloat1, paramFloat2);
  }
  
  public Shape getGlyphOutline(int paramInt)
  {
    return getGlyphsOutline(paramInt, 1, 0.0F, 0.0F);
  }
  
  public Shape getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2)
  {
    return getGlyphsOutline(paramInt, 1, paramFloat1, paramFloat2);
  }
  
  public Point2D getGlyphPosition(int paramInt)
  {
    initPositions();
    paramInt *= 2;
    return new Point2D.Float(positions[paramInt], positions[(paramInt + 1)]);
  }
  
  public void setGlyphPosition(int paramInt, Point2D paramPoint2D)
  {
    initPositions();
    int i = paramInt << 1;
    positions[i] = ((float)paramPoint2D.getX());
    positions[(i + 1)] = ((float)paramPoint2D.getY());
    clearCaches(paramInt);
    addFlags(2);
  }
  
  public AffineTransform getGlyphTransform(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= glyphs.length)) {
      throw new IndexOutOfBoundsException("ix = " + paramInt);
    }
    if (gti != null) {
      return gti.getGlyphTransform(paramInt);
    }
    return null;
  }
  
  public void setGlyphTransform(int paramInt, AffineTransform paramAffineTransform)
  {
    if ((paramInt < 0) || (paramInt >= glyphs.length)) {
      throw new IndexOutOfBoundsException("ix = " + paramInt);
    }
    if (gti == null)
    {
      if ((paramAffineTransform == null) || (paramAffineTransform.isIdentity())) {
        return;
      }
      gti = new GlyphTransformInfo(this);
    }
    gti.setGlyphTransform(paramInt, paramAffineTransform);
    if (gti.transformCount() == 0) {
      gti = null;
    }
  }
  
  public int getLayoutFlags()
  {
    if (flags == -1)
    {
      flags = 0;
      if ((charIndices != null) && (glyphs.length > 1))
      {
        int i = 1;
        int j = 1;
        int k = charIndices.length;
        for (int m = 0; (m < charIndices.length) && ((i != 0) || (j != 0)); m++)
        {
          int n = charIndices[m];
          i = (i != 0) && (n == m) ? 1 : 0;
          j = (j != 0) && (n == --k) ? 1 : 0;
        }
        if (j != 0) {
          flags |= 0x4;
        }
        if ((j == 0) && (i == 0)) {
          flags |= 0x8;
        }
      }
    }
    return flags;
  }
  
  public float[] getGlyphPositions(int paramInt1, int paramInt2, float[] paramArrayOfFloat)
  {
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("count = " + paramInt2);
    }
    if (paramInt1 < 0) {
      throw new IndexOutOfBoundsException("start = " + paramInt1);
    }
    if (paramInt1 > glyphs.length + 1 - paramInt2) {
      throw new IndexOutOfBoundsException("start + count = " + (paramInt1 + paramInt2));
    }
    return internalGetGlyphPositions(paramInt1, paramInt2, 0, paramArrayOfFloat);
  }
  
  public Shape getGlyphLogicalBounds(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= glyphs.length)) {
      throw new IndexOutOfBoundsException("ix = " + paramInt);
    }
    Shape[] arrayOfShape;
    if ((lbcacheRef == null) || ((arrayOfShape = (Shape[])lbcacheRef.get()) == null))
    {
      arrayOfShape = new Shape[glyphs.length];
      lbcacheRef = new SoftReference(arrayOfShape);
    }
    Object localObject = arrayOfShape[paramInt];
    if (localObject == null)
    {
      setFRCTX();
      initPositions();
      ADL localADL = new ADL();
      GlyphStrike localGlyphStrike = getGlyphStrike(paramInt);
      localGlyphStrike.getADL(localADL);
      Point2D.Float localFloat = strike.getGlyphMetrics(glyphs[paramInt]);
      float f1 = x;
      float f2 = y;
      float f3 = descentX + leadingX + ascentX;
      float f4 = descentY + leadingY + ascentY;
      float f5 = positions[(paramInt * 2)] + dx - ascentX;
      float f6 = positions[(paramInt * 2 + 1)] + dy - ascentY;
      GeneralPath localGeneralPath = new GeneralPath();
      localGeneralPath.moveTo(f5, f6);
      localGeneralPath.lineTo(f5 + f1, f6 + f2);
      localGeneralPath.lineTo(f5 + f1 + f3, f6 + f2 + f4);
      localGeneralPath.lineTo(f5 + f3, f6 + f4);
      localGeneralPath.closePath();
      localObject = new DelegatingShape(localGeneralPath);
      arrayOfShape[paramInt] = localObject;
    }
    return (Shape)localObject;
  }
  
  public Shape getGlyphVisualBounds(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= glyphs.length)) {
      throw new IndexOutOfBoundsException("ix = " + paramInt);
    }
    Shape[] arrayOfShape;
    if ((vbcacheRef == null) || ((arrayOfShape = (Shape[])vbcacheRef.get()) == null))
    {
      arrayOfShape = new Shape[glyphs.length];
      vbcacheRef = new SoftReference(arrayOfShape);
    }
    Object localObject = arrayOfShape[paramInt];
    if (localObject == null)
    {
      localObject = new DelegatingShape(getGlyphOutlineBounds(paramInt));
      arrayOfShape[paramInt] = localObject;
    }
    return (Shape)localObject;
  }
  
  public Rectangle getGlyphPixelBounds(int paramInt, FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2)
  {
    return getGlyphsPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2, paramInt, 1);
  }
  
  public GlyphMetrics getGlyphMetrics(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= glyphs.length)) {
      throw new IndexOutOfBoundsException("ix = " + paramInt);
    }
    Rectangle2D localRectangle2D = getGlyphVisualBounds(paramInt).getBounds2D();
    Point2D localPoint2D = getGlyphPosition(paramInt);
    localRectangle2D.setRect(localRectangle2D.getMinX() - localPoint2D.getX(), localRectangle2D.getMinY() - localPoint2D.getY(), localRectangle2D.getWidth(), localRectangle2D.getHeight());
    Point2D.Float localFloat = getGlyphStrikestrike.getGlyphMetrics(glyphs[paramInt]);
    GlyphMetrics localGlyphMetrics = new GlyphMetrics(true, x, y, localRectangle2D, (byte)0);
    return localGlyphMetrics;
  }
  
  public GlyphJustificationInfo getGlyphJustificationInfo(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= glyphs.length)) {
      throw new IndexOutOfBoundsException("ix = " + paramInt);
    }
    return null;
  }
  
  public boolean equals(GlyphVector paramGlyphVector)
  {
    if (this == paramGlyphVector) {
      return true;
    }
    if (paramGlyphVector == null) {
      return false;
    }
    try
    {
      StandardGlyphVector localStandardGlyphVector = (StandardGlyphVector)paramGlyphVector;
      if (glyphs.length != glyphs.length) {
        return false;
      }
      for (int i = 0; i < glyphs.length; i++) {
        if (glyphs[i] != glyphs[i]) {
          return false;
        }
      }
      if (!font.equals(font)) {
        return false;
      }
      if (!frc.equals(frc)) {
        return false;
      }
      if ((positions == null ? 1 : 0) != (positions == null ? 1 : 0)) {
        if (positions == null) {
          initPositions();
        } else {
          localStandardGlyphVector.initPositions();
        }
      }
      if (positions != null) {
        for (i = 0; i < positions.length; i++) {
          if (positions[i] != positions[i]) {
            return false;
          }
        }
      }
      if (gti == null) {
        return gti == null;
      }
      return gti.equals(gti);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public int hashCode()
  {
    return font.hashCode() ^ glyphs.length;
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      return equals((GlyphVector)paramObject);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public StandardGlyphVector copy()
  {
    return (StandardGlyphVector)clone();
  }
  
  public Object clone()
  {
    try
    {
      StandardGlyphVector localStandardGlyphVector = (StandardGlyphVector)super.clone();
      localStandardGlyphVector.clearCaches();
      if (positions != null) {
        positions = ((float[])positions.clone());
      }
      if (gti != null) {
        gti = new GlyphTransformInfo(localStandardGlyphVector, gti);
      }
      return localStandardGlyphVector;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return this;
  }
  
  public void setGlyphPositions(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt3 < 0) {
      throw new IllegalArgumentException("count = " + paramInt3);
    }
    initPositions();
    int i = paramInt2 * 2;
    int j = i + paramInt3 * 2;
    for (int k = paramInt1; i < j; k++)
    {
      positions[i] = paramArrayOfFloat[k];
      i++;
    }
    clearCaches();
    addFlags(2);
  }
  
  public void setGlyphPositions(float[] paramArrayOfFloat)
  {
    int i = glyphs.length * 2 + 2;
    if (paramArrayOfFloat.length != i) {
      throw new IllegalArgumentException("srcPositions.length != " + i);
    }
    positions = ((float[])paramArrayOfFloat.clone());
    clearCaches();
    addFlags(2);
  }
  
  public float[] getGlyphPositions(float[] paramArrayOfFloat)
  {
    return internalGetGlyphPositions(0, glyphs.length + 1, 0, paramArrayOfFloat);
  }
  
  public AffineTransform[] getGlyphTransforms(int paramInt1, int paramInt2, AffineTransform[] paramArrayOfAffineTransform)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > glyphs.length)) {
      throw new IllegalArgumentException("start: " + paramInt1 + " count: " + paramInt2);
    }
    if (gti == null) {
      return null;
    }
    if (paramArrayOfAffineTransform == null) {
      paramArrayOfAffineTransform = new AffineTransform[paramInt2];
    }
    int i = 0;
    while (i < paramInt2)
    {
      paramArrayOfAffineTransform[i] = gti.getGlyphTransform(paramInt1);
      i++;
      paramInt1++;
    }
    return paramArrayOfAffineTransform;
  }
  
  public AffineTransform[] getGlyphTransforms()
  {
    return getGlyphTransforms(0, glyphs.length, null);
  }
  
  public void setGlyphTransforms(AffineTransform[] paramArrayOfAffineTransform, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt2;
    int j = paramInt2 + paramInt3;
    while (i < j)
    {
      setGlyphTransform(i, paramArrayOfAffineTransform[(paramInt1 + i)]);
      i++;
    }
  }
  
  public void setGlyphTransforms(AffineTransform[] paramArrayOfAffineTransform)
  {
    setGlyphTransforms(paramArrayOfAffineTransform, 0, 0, glyphs.length);
  }
  
  public float[] getGlyphInfo()
  {
    setFRCTX();
    initPositions();
    float[] arrayOfFloat = new float[glyphs.length * 8];
    int i = 0;
    for (int j = 0; i < glyphs.length; j += 8)
    {
      float f1 = positions[(i * 2)];
      float f2 = positions[(i * 2 + 1)];
      arrayOfFloat[j] = f1;
      arrayOfFloat[(j + 1)] = f2;
      int k = glyphs[i];
      GlyphStrike localGlyphStrike = getGlyphStrike(i);
      Point2D.Float localFloat = strike.getGlyphMetrics(k);
      arrayOfFloat[(j + 2)] = x;
      arrayOfFloat[(j + 3)] = y;
      Rectangle2D localRectangle2D = getGlyphVisualBounds(i).getBounds2D();
      arrayOfFloat[(j + 4)] = ((float)localRectangle2D.getMinX());
      arrayOfFloat[(j + 5)] = ((float)localRectangle2D.getMinY());
      arrayOfFloat[(j + 6)] = ((float)localRectangle2D.getWidth());
      arrayOfFloat[(j + 7)] = ((float)localRectangle2D.getHeight());
      i++;
    }
    return arrayOfFloat;
  }
  
  public void pixellate(FontRenderContext paramFontRenderContext, Point2D paramPoint2D, Point paramPoint)
  {
    if (paramFontRenderContext == null) {
      paramFontRenderContext = frc;
    }
    AffineTransform localAffineTransform = paramFontRenderContext.getTransform();
    localAffineTransform.transform(paramPoint2D, paramPoint2D);
    x = ((int)paramPoint2D.getX());
    y = ((int)paramPoint2D.getY());
    paramPoint2D.setLocation(x, y);
    try
    {
      localAffineTransform.inverseTransform(paramPoint2D, paramPoint2D);
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      throw new IllegalArgumentException("must be able to invert frc transform");
    }
  }
  
  boolean needsPositions(double[] paramArrayOfDouble)
  {
    return (gti != null) || ((getLayoutFlags() & 0x2) != 0) || (!matchTX(paramArrayOfDouble, frctx));
  }
  
  Object setupGlyphImages(long[] paramArrayOfLong, float[] paramArrayOfFloat, double[] paramArrayOfDouble)
  {
    initPositions();
    setRenderTransform(paramArrayOfDouble);
    if (gti != null) {
      return gti.setupGlyphImages(paramArrayOfLong, paramArrayOfFloat, dtx);
    }
    GlyphStrike localGlyphStrike = getDefaultStrike();
    strike.getGlyphImagePtrs(glyphs, paramArrayOfLong, glyphs.length);
    if (paramArrayOfFloat != null) {
      if (dtx.isIdentity()) {
        System.arraycopy(positions, 0, paramArrayOfFloat, 0, glyphs.length * 2);
      } else {
        dtx.transform(positions, 0, paramArrayOfFloat, 0, glyphs.length);
      }
    }
    return localGlyphStrike;
  }
  
  private static boolean matchTX(double[] paramArrayOfDouble, AffineTransform paramAffineTransform)
  {
    return (paramArrayOfDouble[0] == paramAffineTransform.getScaleX()) && (paramArrayOfDouble[1] == paramAffineTransform.getShearY()) && (paramArrayOfDouble[2] == paramAffineTransform.getShearX()) && (paramArrayOfDouble[3] == paramAffineTransform.getScaleY());
  }
  
  private static AffineTransform getNonTranslateTX(AffineTransform paramAffineTransform)
  {
    if ((paramAffineTransform.getTranslateX() != 0.0D) || (paramAffineTransform.getTranslateY() != 0.0D)) {
      paramAffineTransform = new AffineTransform(paramAffineTransform.getScaleX(), paramAffineTransform.getShearY(), paramAffineTransform.getShearX(), paramAffineTransform.getScaleY(), 0.0D, 0.0D);
    }
    return paramAffineTransform;
  }
  
  private static boolean equalNonTranslateTX(AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2)
  {
    return (paramAffineTransform1.getScaleX() == paramAffineTransform2.getScaleX()) && (paramAffineTransform1.getShearY() == paramAffineTransform2.getShearY()) && (paramAffineTransform1.getShearX() == paramAffineTransform2.getShearX()) && (paramAffineTransform1.getScaleY() == paramAffineTransform2.getScaleY());
  }
  
  private void setRenderTransform(double[] paramArrayOfDouble)
  {
    assert (paramArrayOfDouble.length == 4);
    if (!matchTX(paramArrayOfDouble, dtx)) {
      resetDTX(new AffineTransform(paramArrayOfDouble));
    }
  }
  
  private final void setDTX(AffineTransform paramAffineTransform)
  {
    if (!equalNonTranslateTX(dtx, paramAffineTransform)) {
      resetDTX(getNonTranslateTX(paramAffineTransform));
    }
  }
  
  private final void setFRCTX()
  {
    if (!equalNonTranslateTX(frctx, dtx)) {
      resetDTX(getNonTranslateTX(frctx));
    }
  }
  
  private final void resetDTX(AffineTransform paramAffineTransform)
  {
    fsref = null;
    dtx = paramAffineTransform;
    invdtx = null;
    if (!dtx.isIdentity()) {
      try
      {
        invdtx = dtx.createInverse();
      }
      catch (NoninvertibleTransformException localNoninvertibleTransformException) {}
    }
    if (gti != null) {
      gti.strikesRef = null;
    }
  }
  
  private StandardGlyphVector(GlyphVector paramGlyphVector, FontRenderContext paramFontRenderContext)
  {
    font = paramGlyphVector.getFont();
    frc = paramFontRenderContext;
    initFontData();
    int i = paramGlyphVector.getNumGlyphs();
    userGlyphs = paramGlyphVector.getGlyphCodes(0, i, null);
    if ((paramGlyphVector instanceof StandardGlyphVector)) {
      glyphs = userGlyphs;
    } else {
      glyphs = getValidatedGlyphs(userGlyphs);
    }
    flags = (paramGlyphVector.getLayoutFlags() & 0xF);
    if ((flags & 0x2) != 0) {
      positions = paramGlyphVector.getGlyphPositions(0, i + 1, null);
    }
    if ((flags & 0x8) != 0) {
      charIndices = paramGlyphVector.getGlyphCharIndices(0, i, null);
    }
    if ((flags & 0x1) != 0)
    {
      AffineTransform[] arrayOfAffineTransform = new AffineTransform[i];
      for (int j = 0; j < i; j++) {
        arrayOfAffineTransform[j] = paramGlyphVector.getGlyphTransform(j);
      }
      setGlyphTransforms(arrayOfAffineTransform);
    }
  }
  
  int[] getValidatedGlyphs(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; j++) {
      if ((paramArrayOfInt[j] == 65534) || (paramArrayOfInt[j] == 65535)) {
        arrayOfInt[j] = paramArrayOfInt[j];
      } else {
        arrayOfInt[j] = font2D.getValidatedGlyphCode(paramArrayOfInt[j]);
      }
    }
    return arrayOfInt;
  }
  
  private void init(Font paramFont, char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length)) {
      throw new ArrayIndexOutOfBoundsException("start or count out of bounds");
    }
    font = paramFont;
    frc = paramFontRenderContext;
    flags = paramInt3;
    if (getTracking(paramFont) != 0.0F) {
      addFlags(2);
    }
    if (paramInt1 != 0)
    {
      char[] arrayOfChar = new char[paramInt2];
      System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, paramInt2);
      paramArrayOfChar = arrayOfChar;
    }
    initFontData();
    glyphs = new int[paramInt2];
    userGlyphs = glyphs;
    font2D.getMapper().charsToGlyphs(paramInt2, paramArrayOfChar, glyphs);
  }
  
  private void initFontData()
  {
    font2D = FontUtilities.getFont2D(font);
    float f = font.getSize2D();
    if (font.isTransformed())
    {
      ftx = font.getTransform();
      if ((ftx.getTranslateX() != 0.0D) || (ftx.getTranslateY() != 0.0D)) {
        addFlags(2);
      }
      ftx.setTransform(ftx.getScaleX(), ftx.getShearY(), ftx.getShearX(), ftx.getScaleY(), 0.0D, 0.0D);
      ftx.scale(f, f);
    }
    else
    {
      ftx = AffineTransform.getScaleInstance(f, f);
    }
    frctx = frc.getTransform();
    resetDTX(getNonTranslateTX(frctx));
  }
  
  private float[] internalGetGlyphPositions(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      paramArrayOfFloat = new float[paramInt3 + paramInt2 * 2];
    }
    initPositions();
    int i = paramInt3;
    int j = paramInt3 + paramInt2 * 2;
    for (int k = paramInt1 * 2; i < j; k++)
    {
      paramArrayOfFloat[i] = positions[k];
      i++;
    }
    return paramArrayOfFloat;
  }
  
  private Rectangle2D getGlyphOutlineBounds(int paramInt)
  {
    setFRCTX();
    initPositions();
    return getGlyphStrike(paramInt).getGlyphOutlineBounds(glyphs[paramInt], positions[(paramInt * 2)], positions[(paramInt * 2 + 1)]);
  }
  
  private Shape getGlyphsOutline(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    setFRCTX();
    initPositions();
    GeneralPath localGeneralPath = new GeneralPath(1);
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1 * 2; i < j; k += 2)
    {
      float f1 = paramFloat1 + positions[k];
      float f2 = paramFloat2 + positions[(k + 1)];
      getGlyphStrike(i).appendGlyphOutline(glyphs[i], localGeneralPath, f1, f2);
      i++;
    }
    return localGeneralPath;
  }
  
  private Rectangle getGlyphsPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    initPositions();
    AffineTransform localAffineTransform = null;
    if ((paramFontRenderContext == null) || (paramFontRenderContext.equals(frc))) {
      localAffineTransform = frctx;
    } else {
      localAffineTransform = paramFontRenderContext.getTransform();
    }
    setDTX(localAffineTransform);
    if (gti != null) {
      return gti.getGlyphsPixelBounds(localAffineTransform, paramFloat1, paramFloat2, paramInt1, paramInt2);
    }
    FontStrike localFontStrike = getDefaultStrikestrike;
    Rectangle localRectangle1 = null;
    Rectangle localRectangle2 = new Rectangle();
    Point2D.Float localFloat = new Point2D.Float();
    int i = paramInt1 * 2;
    for (;;)
    {
      paramInt2--;
      if (paramInt2 < 0) {
        break;
      }
      x = (paramFloat1 + positions[(i++)]);
      y = (paramFloat2 + positions[(i++)]);
      localAffineTransform.transform(localFloat, localFloat);
      localFontStrike.getGlyphImageBounds(glyphs[(paramInt1++)], localFloat, localRectangle2);
      if (!localRectangle2.isEmpty()) {
        if (localRectangle1 == null) {
          localRectangle1 = new Rectangle(localRectangle2);
        } else {
          localRectangle1.add(localRectangle2);
        }
      }
    }
    return localRectangle1 != null ? localRectangle1 : localRectangle2;
  }
  
  private void clearCaches(int paramInt)
  {
    Shape[] arrayOfShape;
    if (lbcacheRef != null)
    {
      arrayOfShape = (Shape[])lbcacheRef.get();
      if (arrayOfShape != null) {
        arrayOfShape[paramInt] = null;
      }
    }
    if (vbcacheRef != null)
    {
      arrayOfShape = (Shape[])vbcacheRef.get();
      if (arrayOfShape != null) {
        arrayOfShape[paramInt] = null;
      }
    }
  }
  
  private void clearCaches()
  {
    lbcacheRef = null;
    vbcacheRef = null;
  }
  
  private void initPositions()
  {
    if (positions == null)
    {
      setFRCTX();
      positions = new float[glyphs.length * 2 + 2];
      Point2D.Float localFloat1 = null;
      float f = getTracking(font);
      if (f != 0.0F)
      {
        f *= font.getSize2D();
        localFloat1 = new Point2D.Float(f, 0.0F);
      }
      Point2D.Float localFloat2 = new Point2D.Float(0.0F, 0.0F);
      if (font.isTransformed())
      {
        AffineTransform localAffineTransform = font.getTransform();
        localAffineTransform.transform(localFloat2, localFloat2);
        positions[0] = x;
        positions[1] = y;
        if (localFloat1 != null) {
          localAffineTransform.deltaTransform(localFloat1, localFloat1);
        }
      }
      int i = 0;
      for (int j = 2; i < glyphs.length; j += 2)
      {
        getGlyphStrike(i).addDefaultGlyphAdvance(glyphs[i], localFloat2);
        if (localFloat1 != null)
        {
          x += x;
          y += y;
        }
        positions[j] = x;
        positions[(j + 1)] = y;
        i++;
      }
    }
  }
  
  private void addFlags(int paramInt)
  {
    flags = (getLayoutFlags() | paramInt);
  }
  
  private void clearFlags(int paramInt)
  {
    flags = (getLayoutFlags() & (paramInt ^ 0xFFFFFFFF));
  }
  
  private GlyphStrike getGlyphStrike(int paramInt)
  {
    if (gti == null) {
      return getDefaultStrike();
    }
    return gti.getStrike(paramInt);
  }
  
  private GlyphStrike getDefaultStrike()
  {
    GlyphStrike localGlyphStrike = null;
    if (fsref != null) {
      localGlyphStrike = (GlyphStrike)fsref.get();
    }
    if (localGlyphStrike == null)
    {
      localGlyphStrike = GlyphStrike.create(this, dtx, null);
      fsref = new SoftReference(localGlyphStrike);
    }
    return localGlyphStrike;
  }
  
  public String toString()
  {
    return appendString(null).toString();
  }
  
  StringBuffer appendString(StringBuffer paramStringBuffer)
  {
    if (paramStringBuffer == null) {
      paramStringBuffer = new StringBuffer();
    }
    try
    {
      paramStringBuffer.append("SGV{font: ");
      paramStringBuffer.append(font.toString());
      paramStringBuffer.append(", frc: ");
      paramStringBuffer.append(frc.toString());
      paramStringBuffer.append(", glyphs: (");
      paramStringBuffer.append(glyphs.length);
      paramStringBuffer.append(")[");
      for (int i = 0; i < glyphs.length; i++)
      {
        if (i > 0) {
          paramStringBuffer.append(", ");
        }
        paramStringBuffer.append(Integer.toHexString(glyphs[i]));
      }
      paramStringBuffer.append("]");
      if (positions != null)
      {
        paramStringBuffer.append(", positions: (");
        paramStringBuffer.append(positions.length);
        paramStringBuffer.append(")[");
        for (i = 0; i < positions.length; i += 2)
        {
          if (i > 0) {
            paramStringBuffer.append(", ");
          }
          paramStringBuffer.append(positions[i]);
          paramStringBuffer.append("@");
          paramStringBuffer.append(positions[(i + 1)]);
        }
        paramStringBuffer.append("]");
      }
      if (charIndices != null)
      {
        paramStringBuffer.append(", indices: (");
        paramStringBuffer.append(charIndices.length);
        paramStringBuffer.append(")[");
        for (i = 0; i < charIndices.length; i++)
        {
          if (i > 0) {
            paramStringBuffer.append(", ");
          }
          paramStringBuffer.append(charIndices[i]);
        }
        paramStringBuffer.append("]");
      }
      paramStringBuffer.append(", flags:");
      if (getLayoutFlags() == 0)
      {
        paramStringBuffer.append(" default");
      }
      else
      {
        if ((flags & 0x1) != 0) {
          paramStringBuffer.append(" tx");
        }
        if ((flags & 0x2) != 0) {
          paramStringBuffer.append(" pos");
        }
        if ((flags & 0x4) != 0) {
          paramStringBuffer.append(" rtl");
        }
        if ((flags & 0x8) != 0) {
          paramStringBuffer.append(" complex");
        }
      }
    }
    catch (Exception localException)
    {
      paramStringBuffer.append(" " + localException.getMessage());
    }
    paramStringBuffer.append("}");
    return paramStringBuffer;
  }
  
  static class ADL
  {
    public float ascentX;
    public float ascentY;
    public float descentX;
    public float descentY;
    public float leadingX;
    public float leadingY;
    
    ADL() {}
    
    public String toString()
    {
      return toStringBuffer(null).toString();
    }
    
    protected StringBuffer toStringBuffer(StringBuffer paramStringBuffer)
    {
      if (paramStringBuffer == null) {
        paramStringBuffer = new StringBuffer();
      }
      paramStringBuffer.append("ax: ");
      paramStringBuffer.append(ascentX);
      paramStringBuffer.append(" ay: ");
      paramStringBuffer.append(ascentY);
      paramStringBuffer.append(" dx: ");
      paramStringBuffer.append(descentX);
      paramStringBuffer.append(" dy: ");
      paramStringBuffer.append(descentY);
      paramStringBuffer.append(" lx: ");
      paramStringBuffer.append(leadingX);
      paramStringBuffer.append(" ly: ");
      paramStringBuffer.append(leadingY);
      return paramStringBuffer;
    }
  }
  
  public static final class GlyphStrike
  {
    StandardGlyphVector sgv;
    FontStrike strike;
    float dx;
    float dy;
    
    static GlyphStrike create(StandardGlyphVector paramStandardGlyphVector, AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2)
    {
      float f1 = 0.0F;
      float f2 = 0.0F;
      AffineTransform localAffineTransform = ftx;
      if ((!paramAffineTransform1.isIdentity()) || (paramAffineTransform2 != null))
      {
        localAffineTransform = new AffineTransform(ftx);
        if (paramAffineTransform2 != null)
        {
          localAffineTransform.preConcatenate(paramAffineTransform2);
          f1 = (float)localAffineTransform.getTranslateX();
          f2 = (float)localAffineTransform.getTranslateY();
        }
        if (!paramAffineTransform1.isIdentity()) {
          localAffineTransform.preConcatenate(paramAffineTransform1);
        }
      }
      int i = 1;
      Object localObject = frc.getAntiAliasingHint();
      if ((localObject == RenderingHints.VALUE_TEXT_ANTIALIAS_GASP) && (!localAffineTransform.isIdentity()) && ((localAffineTransform.getType() & 0xFFFFFFFE) != 0))
      {
        double d1 = localAffineTransform.getShearX();
        if (d1 != 0.0D)
        {
          double d2 = localAffineTransform.getScaleY();
          i = (int)Math.sqrt(d1 * d1 + d2 * d2);
        }
        else
        {
          i = (int)Math.abs(localAffineTransform.getScaleY());
        }
      }
      int j = FontStrikeDesc.getAAHintIntVal(localObject, font2D, i);
      int k = FontStrikeDesc.getFMHintIntVal(frc.getFractionalMetricsHint());
      FontStrikeDesc localFontStrikeDesc = new FontStrikeDesc(paramAffineTransform1, localAffineTransform, font.getStyle(), j, k);
      FontStrike localFontStrike = font2D.handle.font2D.getStrike(localFontStrikeDesc);
      return new GlyphStrike(paramStandardGlyphVector, localFontStrike, f1, f2);
    }
    
    private GlyphStrike(StandardGlyphVector paramStandardGlyphVector, FontStrike paramFontStrike, float paramFloat1, float paramFloat2)
    {
      sgv = paramStandardGlyphVector;
      strike = paramFontStrike;
      dx = paramFloat1;
      dy = paramFloat2;
    }
    
    void getADL(StandardGlyphVector.ADL paramADL)
    {
      StrikeMetrics localStrikeMetrics = strike.getFontMetrics();
      Point2D.Float localFloat = null;
      if (sgv.font.isTransformed())
      {
        localFloat = new Point2D.Float();
        x = ((float)sgv.font.getTransform().getTranslateX());
        y = ((float)sgv.font.getTransform().getTranslateY());
      }
      ascentX = (-ascentX);
      ascentY = (-ascentY);
      descentX = descentX;
      descentY = descentY;
      leadingX = leadingX;
      leadingY = leadingY;
    }
    
    void getGlyphPosition(int paramInt1, int paramInt2, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      paramArrayOfFloat1[paramInt2] += dx;
      paramInt2++;
      paramArrayOfFloat1[paramInt2] += dy;
    }
    
    void addDefaultGlyphAdvance(int paramInt, Point2D.Float paramFloat)
    {
      Point2D.Float localFloat = strike.getGlyphMetrics(paramInt);
      x += x + dx;
      y += y + dy;
    }
    
    Rectangle2D getGlyphOutlineBounds(int paramInt, float paramFloat1, float paramFloat2)
    {
      Object localObject = null;
      if (sgv.invdtx == null)
      {
        localObject = new Rectangle2D.Float();
        ((Rectangle2D)localObject).setRect(strike.getGlyphOutlineBounds(paramInt));
      }
      else
      {
        GeneralPath localGeneralPath = strike.getGlyphOutline(paramInt, 0.0F, 0.0F);
        localGeneralPath.transform(sgv.invdtx);
        localObject = localGeneralPath.getBounds2D();
      }
      if (!((Rectangle2D)localObject).isEmpty()) {
        ((Rectangle2D)localObject).setRect(((Rectangle2D)localObject).getMinX() + paramFloat1 + dx, ((Rectangle2D)localObject).getMinY() + paramFloat2 + dy, ((Rectangle2D)localObject).getWidth(), ((Rectangle2D)localObject).getHeight());
      }
      return (Rectangle2D)localObject;
    }
    
    void appendGlyphOutline(int paramInt, GeneralPath paramGeneralPath, float paramFloat1, float paramFloat2)
    {
      GeneralPath localGeneralPath = null;
      if (sgv.invdtx == null)
      {
        localGeneralPath = strike.getGlyphOutline(paramInt, paramFloat1 + dx, paramFloat2 + dy);
      }
      else
      {
        localGeneralPath = strike.getGlyphOutline(paramInt, 0.0F, 0.0F);
        localGeneralPath.transform(sgv.invdtx);
        localGeneralPath.transform(AffineTransform.getTranslateInstance(paramFloat1 + dx, paramFloat2 + dy));
      }
      PathIterator localPathIterator = localGeneralPath.getPathIterator(null);
      paramGeneralPath.append(localPathIterator, false);
    }
  }
  
  static final class GlyphTransformInfo
  {
    StandardGlyphVector sgv;
    int[] indices;
    double[] transforms;
    SoftReference strikesRef;
    boolean haveAllStrikes;
    
    GlyphTransformInfo(StandardGlyphVector paramStandardGlyphVector)
    {
      sgv = paramStandardGlyphVector;
    }
    
    GlyphTransformInfo(StandardGlyphVector paramStandardGlyphVector, GlyphTransformInfo paramGlyphTransformInfo)
    {
      sgv = paramStandardGlyphVector;
      indices = (indices == null ? null : (int[])indices.clone());
      transforms = (transforms == null ? null : (double[])transforms.clone());
      strikesRef = null;
    }
    
    public boolean equals(GlyphTransformInfo paramGlyphTransformInfo)
    {
      if (paramGlyphTransformInfo == null) {
        return false;
      }
      if (paramGlyphTransformInfo == this) {
        return true;
      }
      if (indices.length != indices.length) {
        return false;
      }
      if (transforms.length != transforms.length) {
        return false;
      }
      for (int i = 0; i < indices.length; i++)
      {
        int j = indices[i];
        int k = indices[i];
        if ((j == 0 ? 1 : 0) != (k == 0 ? 1 : 0)) {
          return false;
        }
        if (j != 0)
        {
          j *= 6;
          k *= 6;
          for (int m = 6; m > 0; m--) {
            if (indices[(--j)] != indices[(--k)]) {
              return false;
            }
          }
        }
      }
      return true;
    }
    
    void setGlyphTransform(int paramInt, AffineTransform paramAffineTransform)
    {
      double[] arrayOfDouble1 = new double[6];
      int i = 1;
      if ((paramAffineTransform == null) || (paramAffineTransform.isIdentity()))
      {
        arrayOfDouble1[0] = (arrayOfDouble1[3] = 1.0D);
      }
      else
      {
        i = 0;
        paramAffineTransform.getMatrix(arrayOfDouble1);
      }
      if (indices == null)
      {
        if (i != 0) {
          return;
        }
        indices = new int[sgv.glyphs.length];
        indices[paramInt] = 1;
        transforms = arrayOfDouble1;
      }
      else
      {
        int j = 0;
        int k = -1;
        int n;
        if (i != 0)
        {
          k = 0;
        }
        else
        {
          j = 1;
          label156:
          for (m = 0; m < transforms.length; m += 6)
          {
            for (n = 0; n < 6; n++) {
              if (transforms[(m + n)] != arrayOfDouble1[n]) {
                break label156;
              }
            }
            j = 0;
            break;
          }
          k = m / 6 + 1;
        }
        int m = indices[paramInt];
        if (k != m)
        {
          n = 0;
          if (m != 0)
          {
            n = 1;
            for (int i1 = 0; i1 < indices.length; i1++) {
              if ((indices[i1] == m) && (i1 != paramInt))
              {
                n = 0;
                break;
              }
            }
          }
          if ((n != 0) && (j != 0))
          {
            k = m;
            System.arraycopy(arrayOfDouble1, 0, transforms, (k - 1) * 6, 6);
          }
          else
          {
            double[] arrayOfDouble2;
            if (n != 0)
            {
              if (transforms.length == 6)
              {
                indices = null;
                transforms = null;
                sgv.clearCaches(paramInt);
                sgv.clearFlags(1);
                strikesRef = null;
                return;
              }
              arrayOfDouble2 = new double[transforms.length - 6];
              System.arraycopy(transforms, 0, arrayOfDouble2, 0, (m - 1) * 6);
              System.arraycopy(transforms, m * 6, arrayOfDouble2, (m - 1) * 6, transforms.length - m * 6);
              transforms = arrayOfDouble2;
              for (int i2 = 0; i2 < indices.length; i2++) {
                if (indices[i2] > m) {
                  indices[i2] -= 1;
                }
              }
              if (k > m) {
                k--;
              }
            }
            else if (j != 0)
            {
              arrayOfDouble2 = new double[transforms.length + 6];
              System.arraycopy(transforms, 0, arrayOfDouble2, 0, transforms.length);
              System.arraycopy(arrayOfDouble1, 0, arrayOfDouble2, transforms.length, 6);
              transforms = arrayOfDouble2;
            }
          }
          indices[paramInt] = k;
        }
      }
      sgv.clearCaches(paramInt);
      sgv.addFlags(1);
      strikesRef = null;
    }
    
    AffineTransform getGlyphTransform(int paramInt)
    {
      int i = indices[paramInt];
      if (i == 0) {
        return null;
      }
      int j = (i - 1) * 6;
      return new AffineTransform(transforms[(j + 0)], transforms[(j + 1)], transforms[(j + 2)], transforms[(j + 3)], transforms[(j + 4)], transforms[(j + 5)]);
    }
    
    int transformCount()
    {
      if (transforms == null) {
        return 0;
      }
      return transforms.length / 6;
    }
    
    Object setupGlyphImages(long[] paramArrayOfLong, float[] paramArrayOfFloat, AffineTransform paramAffineTransform)
    {
      int i = sgv.glyphs.length;
      StandardGlyphVector.GlyphStrike[] arrayOfGlyphStrike = getAllStrikes();
      for (int j = 0; j < i; j++)
      {
        StandardGlyphVector.GlyphStrike localGlyphStrike = arrayOfGlyphStrike[indices[j]];
        int k = sgv.glyphs[j];
        paramArrayOfLong[j] = strike.getGlyphImagePtr(k);
        localGlyphStrike.getGlyphPosition(k, j * 2, sgv.positions, paramArrayOfFloat);
      }
      paramAffineTransform.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, i);
      return arrayOfGlyphStrike;
    }
    
    Rectangle getGlyphsPixelBounds(AffineTransform paramAffineTransform, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
    {
      Rectangle localRectangle1 = null;
      Rectangle localRectangle2 = new Rectangle();
      Point2D.Float localFloat = new Point2D.Float();
      int i = paramInt1 * 2;
      for (;;)
      {
        paramInt2--;
        if (paramInt2 < 0) {
          break;
        }
        StandardGlyphVector.GlyphStrike localGlyphStrike = getStrike(paramInt1);
        x = (paramFloat1 + sgv.positions[(i++)] + dx);
        y = (paramFloat2 + sgv.positions[(i++)] + dy);
        paramAffineTransform.transform(localFloat, localFloat);
        strike.getGlyphImageBounds(sgv.glyphs[(paramInt1++)], localFloat, localRectangle2);
        if (!localRectangle2.isEmpty()) {
          if (localRectangle1 == null) {
            localRectangle1 = new Rectangle(localRectangle2);
          } else {
            localRectangle1.add(localRectangle2);
          }
        }
      }
      return localRectangle1 != null ? localRectangle1 : localRectangle2;
    }
    
    StandardGlyphVector.GlyphStrike getStrike(int paramInt)
    {
      if (indices != null)
      {
        StandardGlyphVector.GlyphStrike[] arrayOfGlyphStrike = getStrikeArray();
        return getStrikeAtIndex(arrayOfGlyphStrike, indices[paramInt]);
      }
      return sgv.getDefaultStrike();
    }
    
    private StandardGlyphVector.GlyphStrike[] getAllStrikes()
    {
      if (indices == null) {
        return null;
      }
      StandardGlyphVector.GlyphStrike[] arrayOfGlyphStrike = getStrikeArray();
      if (!haveAllStrikes)
      {
        for (int i = 0; i < arrayOfGlyphStrike.length; i++) {
          getStrikeAtIndex(arrayOfGlyphStrike, i);
        }
        haveAllStrikes = true;
      }
      return arrayOfGlyphStrike;
    }
    
    private StandardGlyphVector.GlyphStrike[] getStrikeArray()
    {
      StandardGlyphVector.GlyphStrike[] arrayOfGlyphStrike = null;
      if (strikesRef != null) {
        arrayOfGlyphStrike = (StandardGlyphVector.GlyphStrike[])strikesRef.get();
      }
      if (arrayOfGlyphStrike == null)
      {
        haveAllStrikes = false;
        arrayOfGlyphStrike = new StandardGlyphVector.GlyphStrike[transformCount() + 1];
        strikesRef = new SoftReference(arrayOfGlyphStrike);
      }
      return arrayOfGlyphStrike;
    }
    
    private StandardGlyphVector.GlyphStrike getStrikeAtIndex(StandardGlyphVector.GlyphStrike[] paramArrayOfGlyphStrike, int paramInt)
    {
      StandardGlyphVector.GlyphStrike localGlyphStrike = paramArrayOfGlyphStrike[paramInt];
      if (localGlyphStrike == null)
      {
        if (paramInt == 0)
        {
          localGlyphStrike = sgv.getDefaultStrike();
        }
        else
        {
          int i = (paramInt - 1) * 6;
          AffineTransform localAffineTransform = new AffineTransform(transforms[i], transforms[(i + 1)], transforms[(i + 2)], transforms[(i + 3)], transforms[(i + 4)], transforms[(i + 5)]);
          localGlyphStrike = StandardGlyphVector.GlyphStrike.create(sgv, sgv.dtx, localAffineTransform);
        }
        paramArrayOfGlyphStrike[paramInt] = localGlyphStrike;
      }
      return localGlyphStrike;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\StandardGlyphVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */