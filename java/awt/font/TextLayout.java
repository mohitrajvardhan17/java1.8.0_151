package java.awt.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.Map;
import sun.font.AttributeValues;
import sun.font.CoreMetrics;
import sun.font.FontResolver;
import sun.font.GraphicComponent;
import sun.font.LayoutPathImpl;
import sun.text.CodePointIterator;

public final class TextLayout
  implements Cloneable
{
  private int characterCount;
  private boolean isVerticalLine = false;
  private byte baseline;
  private float[] baselineOffsets;
  private TextLine textLine;
  private TextLine.TextLineMetrics lineMetrics = null;
  private float visibleAdvance;
  private int hashCodeCache;
  private boolean cacheIsValid = false;
  private float justifyRatio;
  private static final float ALREADY_JUSTIFIED = -53.9F;
  private static float dx;
  private static float dy;
  private Rectangle2D naturalBounds = null;
  private Rectangle2D boundsRect = null;
  private boolean caretsInLigaturesAreAllowed = false;
  public static final CaretPolicy DEFAULT_CARET_POLICY = new CaretPolicy();
  
  public TextLayout(String paramString, Font paramFont, FontRenderContext paramFontRenderContext)
  {
    if (paramFont == null) {
      throw new IllegalArgumentException("Null font passed to TextLayout constructor.");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("Null string passed to TextLayout constructor.");
    }
    if (paramString.length() == 0) {
      throw new IllegalArgumentException("Zero length string passed to TextLayout constructor.");
    }
    Map localMap = null;
    if (paramFont.hasLayoutAttributes()) {
      localMap = paramFont.getAttributes();
    }
    char[] arrayOfChar = paramString.toCharArray();
    if (sameBaselineUpTo(paramFont, arrayOfChar, 0, arrayOfChar.length) == arrayOfChar.length)
    {
      fastInit(arrayOfChar, paramFont, localMap, paramFontRenderContext);
    }
    else
    {
      AttributedString localAttributedString = localMap == null ? new AttributedString(paramString) : new AttributedString(paramString, localMap);
      localAttributedString.addAttribute(TextAttribute.FONT, paramFont);
      standardInit(localAttributedString.getIterator(), arrayOfChar, paramFontRenderContext);
    }
  }
  
  public TextLayout(String paramString, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, FontRenderContext paramFontRenderContext)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Null string passed to TextLayout constructor.");
    }
    if (paramMap == null) {
      throw new IllegalArgumentException("Null map passed to TextLayout constructor.");
    }
    if (paramString.length() == 0) {
      throw new IllegalArgumentException("Zero length string passed to TextLayout constructor.");
    }
    char[] arrayOfChar = paramString.toCharArray();
    Font localFont = singleFont(arrayOfChar, 0, arrayOfChar.length, paramMap);
    if (localFont != null)
    {
      fastInit(arrayOfChar, localFont, paramMap, paramFontRenderContext);
    }
    else
    {
      AttributedString localAttributedString = new AttributedString(paramString, paramMap);
      standardInit(localAttributedString.getIterator(), arrayOfChar, paramFontRenderContext);
    }
  }
  
  private static Font singleFont(char[] paramArrayOfChar, int paramInt1, int paramInt2, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
  {
    if (paramMap.get(TextAttribute.CHAR_REPLACEMENT) != null) {
      return null;
    }
    Font localFont = null;
    try
    {
      localFont = (Font)paramMap.get(TextAttribute.FONT);
    }
    catch (ClassCastException localClassCastException) {}
    if (localFont == null) {
      if (paramMap.get(TextAttribute.FAMILY) != null)
      {
        localFont = Font.getFont(paramMap);
        if (localFont.canDisplayUpTo(paramArrayOfChar, paramInt1, paramInt2) != -1) {
          return null;
        }
      }
      else
      {
        FontResolver localFontResolver = FontResolver.getInstance();
        CodePointIterator localCodePointIterator = CodePointIterator.create(paramArrayOfChar, paramInt1, paramInt2);
        int i = localFontResolver.nextFontRunIndex(localCodePointIterator);
        if (localCodePointIterator.charIndex() == paramInt2) {
          localFont = localFontResolver.getFont(i, paramMap);
        }
      }
    }
    if (sameBaselineUpTo(localFont, paramArrayOfChar, paramInt1, paramInt2) != paramInt2) {
      return null;
    }
    return localFont;
  }
  
  public TextLayout(AttributedCharacterIterator paramAttributedCharacterIterator, FontRenderContext paramFontRenderContext)
  {
    if (paramAttributedCharacterIterator == null) {
      throw new IllegalArgumentException("Null iterator passed to TextLayout constructor.");
    }
    int i = paramAttributedCharacterIterator.getBeginIndex();
    int j = paramAttributedCharacterIterator.getEndIndex();
    if (i == j) {
      throw new IllegalArgumentException("Zero length iterator passed to TextLayout constructor.");
    }
    int k = j - i;
    paramAttributedCharacterIterator.first();
    char[] arrayOfChar = new char[k];
    int m = 0;
    for (int n = paramAttributedCharacterIterator.first(); n != 65535; n = paramAttributedCharacterIterator.next()) {
      arrayOfChar[(m++)] = n;
    }
    paramAttributedCharacterIterator.first();
    if (paramAttributedCharacterIterator.getRunLimit() == j)
    {
      Map localMap = paramAttributedCharacterIterator.getAttributes();
      Font localFont = singleFont(arrayOfChar, 0, k, localMap);
      if (localFont != null)
      {
        fastInit(arrayOfChar, localFont, localMap, paramFontRenderContext);
        return;
      }
    }
    standardInit(paramAttributedCharacterIterator, arrayOfChar, paramFontRenderContext);
  }
  
  TextLayout(TextLine paramTextLine, byte paramByte, float[] paramArrayOfFloat, float paramFloat)
  {
    characterCount = paramTextLine.characterCount();
    baseline = paramByte;
    baselineOffsets = paramArrayOfFloat;
    textLine = paramTextLine;
    justifyRatio = paramFloat;
  }
  
  private void paragraphInit(byte paramByte, CoreMetrics paramCoreMetrics, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, char[] paramArrayOfChar)
  {
    baseline = paramByte;
    baselineOffsets = TextLine.getNormalizedOffsets(baselineOffsets, baseline);
    justifyRatio = AttributeValues.getJustification(paramMap);
    NumericShaper localNumericShaper = AttributeValues.getNumericShaping(paramMap);
    if (localNumericShaper != null) {
      localNumericShaper.shape(paramArrayOfChar, 0, paramArrayOfChar.length);
    }
  }
  
  private void fastInit(char[] paramArrayOfChar, Font paramFont, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, FontRenderContext paramFontRenderContext)
  {
    isVerticalLine = false;
    LineMetrics localLineMetrics = paramFont.getLineMetrics(paramArrayOfChar, 0, paramArrayOfChar.length, paramFontRenderContext);
    CoreMetrics localCoreMetrics = CoreMetrics.get(localLineMetrics);
    byte b = (byte)baselineIndex;
    if (paramMap == null)
    {
      baseline = b;
      baselineOffsets = baselineOffsets;
      justifyRatio = 1.0F;
    }
    else
    {
      paragraphInit(b, localCoreMetrics, paramMap, paramArrayOfChar);
    }
    characterCount = paramArrayOfChar.length;
    textLine = TextLine.fastCreateTextLine(paramFontRenderContext, paramArrayOfChar, paramFont, localCoreMetrics, paramMap);
  }
  
  private void standardInit(AttributedCharacterIterator paramAttributedCharacterIterator, char[] paramArrayOfChar, FontRenderContext paramFontRenderContext)
  {
    characterCount = paramArrayOfChar.length;
    Map localMap = paramAttributedCharacterIterator.getAttributes();
    boolean bool = TextLine.advanceToFirstFont(paramAttributedCharacterIterator);
    Object localObject1;
    int i;
    Object localObject2;
    if (bool)
    {
      localObject1 = TextLine.getFontAtCurrentPos(paramAttributedCharacterIterator);
      i = paramAttributedCharacterIterator.getIndex() - paramAttributedCharacterIterator.getBeginIndex();
      localObject2 = ((Font)localObject1).getLineMetrics(paramArrayOfChar, i, i + 1, paramFontRenderContext);
      CoreMetrics localCoreMetrics = CoreMetrics.get((LineMetrics)localObject2);
      paragraphInit((byte)baselineIndex, localCoreMetrics, localMap, paramArrayOfChar);
    }
    else
    {
      localObject1 = (GraphicAttribute)localMap.get(TextAttribute.CHAR_REPLACEMENT);
      i = getBaselineFromGraphic((GraphicAttribute)localObject1);
      localObject2 = GraphicComponent.createCoreMetrics((GraphicAttribute)localObject1);
      paragraphInit(i, (CoreMetrics)localObject2, localMap, paramArrayOfChar);
    }
    textLine = TextLine.standardCreateTextLine(paramFontRenderContext, paramAttributedCharacterIterator, paramArrayOfChar, baselineOffsets);
  }
  
  private void ensureCache()
  {
    if (!cacheIsValid) {
      buildCache();
    }
  }
  
  private void buildCache()
  {
    lineMetrics = textLine.getMetrics();
    int i;
    int j;
    if (textLine.isDirectionLTR())
    {
      for (i = characterCount - 1; i != -1; i--)
      {
        j = textLine.visualToLogical(i);
        if (!textLine.isCharSpace(j)) {
          break;
        }
      }
      if (i == characterCount - 1)
      {
        visibleAdvance = lineMetrics.advance;
      }
      else if (i == -1)
      {
        visibleAdvance = 0.0F;
      }
      else
      {
        j = textLine.visualToLogical(i);
        visibleAdvance = (textLine.getCharLinePosition(j) + textLine.getCharAdvance(j));
      }
    }
    else
    {
      for (i = 0; i != characterCount; i++)
      {
        j = textLine.visualToLogical(i);
        if (!textLine.isCharSpace(j)) {
          break;
        }
      }
      if (i == characterCount)
      {
        visibleAdvance = 0.0F;
      }
      else if (i == 0)
      {
        visibleAdvance = lineMetrics.advance;
      }
      else
      {
        j = textLine.visualToLogical(i);
        float f = textLine.getCharLinePosition(j);
        visibleAdvance = (lineMetrics.advance - f);
      }
    }
    naturalBounds = null;
    boundsRect = null;
    hashCodeCache = 0;
    cacheIsValid = true;
  }
  
  private Rectangle2D getNaturalBounds()
  {
    ensureCache();
    if (naturalBounds == null) {
      naturalBounds = textLine.getItalicBounds();
    }
    return naturalBounds;
  }
  
  protected Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  private void checkTextHit(TextHitInfo paramTextHitInfo)
  {
    if (paramTextHitInfo == null) {
      throw new IllegalArgumentException("TextHitInfo is null.");
    }
    if ((paramTextHitInfo.getInsertionIndex() < 0) || (paramTextHitInfo.getInsertionIndex() > characterCount)) {
      throw new IllegalArgumentException("TextHitInfo is out of range");
    }
  }
  
  public TextLayout getJustifiedLayout(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      throw new IllegalArgumentException("justificationWidth <= 0 passed to TextLayout.getJustifiedLayout()");
    }
    if (justifyRatio == -53.9F) {
      throw new Error("Can't justify again.");
    }
    ensureCache();
    for (int i = characterCount; (i > 0) && (textLine.isCharWhitespace(i - 1)); i--) {}
    TextLine localTextLine = textLine.getJustifiedLine(paramFloat, justifyRatio, 0, i);
    if (localTextLine != null) {
      return new TextLayout(localTextLine, baseline, baselineOffsets, -53.9F);
    }
    return this;
  }
  
  protected void handleJustify(float paramFloat) {}
  
  public byte getBaseline()
  {
    return baseline;
  }
  
  public float[] getBaselineOffsets()
  {
    float[] arrayOfFloat = new float[baselineOffsets.length];
    System.arraycopy(baselineOffsets, 0, arrayOfFloat, 0, arrayOfFloat.length);
    return arrayOfFloat;
  }
  
  public float getAdvance()
  {
    ensureCache();
    return lineMetrics.advance;
  }
  
  public float getVisibleAdvance()
  {
    ensureCache();
    return visibleAdvance;
  }
  
  public float getAscent()
  {
    ensureCache();
    return lineMetrics.ascent;
  }
  
  public float getDescent()
  {
    ensureCache();
    return lineMetrics.descent;
  }
  
  public float getLeading()
  {
    ensureCache();
    return lineMetrics.leading;
  }
  
  public Rectangle2D getBounds()
  {
    ensureCache();
    if (boundsRect == null)
    {
      localObject = textLine.getVisualBounds();
      if ((dx != 0.0F) || (dy != 0.0F)) {
        ((Rectangle2D)localObject).setRect(((Rectangle2D)localObject).getX() - dx, ((Rectangle2D)localObject).getY() - dy, ((Rectangle2D)localObject).getWidth(), ((Rectangle2D)localObject).getHeight());
      }
      boundsRect = ((Rectangle2D)localObject);
    }
    Object localObject = new Rectangle2D.Float();
    ((Rectangle2D)localObject).setRect(boundsRect);
    return (Rectangle2D)localObject;
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2)
  {
    return textLine.getPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2);
  }
  
  public boolean isLeftToRight()
  {
    return textLine.isDirectionLTR();
  }
  
  public boolean isVertical()
  {
    return isVerticalLine;
  }
  
  public int getCharacterCount()
  {
    return characterCount;
  }
  
  private float[] getCaretInfo(int paramInt, Rectangle2D paramRectangle2D, float[] paramArrayOfFloat)
  {
    float f8;
    float f2;
    float f1;
    float f4;
    float f3;
    if ((paramInt == 0) || (paramInt == characterCount))
    {
      int j;
      float f5;
      if (paramInt == characterCount)
      {
        j = textLine.visualToLogical(characterCount - 1);
        f5 = textLine.getCharLinePosition(j) + textLine.getCharAdvance(j);
      }
      else
      {
        j = textLine.visualToLogical(paramInt);
        f5 = textLine.getCharLinePosition(j);
      }
      f8 = textLine.getCharAngle(j);
      float f9 = textLine.getCharShift(j);
      f5 += f8 * f9;
      f1 = f2 = f5 + f8 * textLine.getCharAscent(j);
      f3 = f4 = f5 - f8 * textLine.getCharDescent(j);
    }
    else
    {
      int i = textLine.visualToLogical(paramInt - 1);
      f7 = textLine.getCharAngle(i);
      f8 = textLine.getCharLinePosition(i) + textLine.getCharAdvance(i);
      if (f7 != 0.0F)
      {
        f8 += f7 * textLine.getCharShift(i);
        f1 = f8 + f7 * textLine.getCharAscent(i);
        f3 = f8 - f7 * textLine.getCharDescent(i);
      }
      else
      {
        f1 = f3 = f8;
      }
      i = textLine.visualToLogical(paramInt);
      f7 = textLine.getCharAngle(i);
      f8 = textLine.getCharLinePosition(i);
      if (f7 != 0.0F)
      {
        f8 += f7 * textLine.getCharShift(i);
        f2 = f8 + f7 * textLine.getCharAscent(i);
        f4 = f8 - f7 * textLine.getCharDescent(i);
      }
      else
      {
        f2 = f4 = f8;
      }
    }
    float f6 = (f1 + f2) / 2.0F;
    float f7 = (f3 + f4) / 2.0F;
    if (paramArrayOfFloat == null) {
      paramArrayOfFloat = new float[2];
    }
    if (isVerticalLine)
    {
      paramArrayOfFloat[1] = ((float)((f6 - f7) / paramRectangle2D.getWidth()));
      paramArrayOfFloat[0] = ((float)(f6 + paramArrayOfFloat[1] * paramRectangle2D.getX()));
    }
    else
    {
      paramArrayOfFloat[1] = ((float)((f6 - f7) / paramRectangle2D.getHeight()));
      paramArrayOfFloat[0] = ((float)(f7 + paramArrayOfFloat[1] * paramRectangle2D.getMaxY()));
    }
    return paramArrayOfFloat;
  }
  
  public float[] getCaretInfo(TextHitInfo paramTextHitInfo, Rectangle2D paramRectangle2D)
  {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    return getCaretInfoTestInternal(paramTextHitInfo, paramRectangle2D);
  }
  
  private float[] getCaretInfoTestInternal(TextHitInfo paramTextHitInfo, Rectangle2D paramRectangle2D)
  {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    float[] arrayOfFloat = new float[6];
    getCaretInfo(hitToCaret(paramTextHitInfo), paramRectangle2D, arrayOfFloat);
    int i = paramTextHitInfo.getCharIndex();
    boolean bool1 = paramTextHitInfo.isLeadingEdge();
    boolean bool2 = textLine.isDirectionLTR();
    int j = !isVertical() ? 1 : 0;
    Object localObject;
    double d1;
    double d5;
    double d3;
    double d4;
    double d6;
    if ((i == -1) || (i == characterCount))
    {
      localObject = textLine.getMetrics();
      int k = bool2 == (i == -1) ? 1 : 0;
      d1 = 0.0D;
      if (j != 0)
      {
        d3 = d5 = k != 0 ? 0.0D : advance;
        d4 = -ascent;
        d6 = descent;
      }
      else
      {
        d4 = d6 = k != 0 ? 0.0D : advance;
        d3 = descent;
        d5 = ascent;
      }
    }
    else
    {
      localObject = textLine.getCoreMetricsAt(i);
      d1 = italicAngle;
      double d2 = textLine.getCharLinePosition(i, bool1);
      if (baselineIndex < 0)
      {
        TextLine.TextLineMetrics localTextLineMetrics = textLine.getMetrics();
        if (j != 0)
        {
          d3 = d5 = d2;
          if (baselineIndex == -1)
          {
            d4 = -ascent;
            d6 = d4 + height;
          }
          else
          {
            d6 = descent;
            d4 = d6 - height;
          }
        }
        else
        {
          d4 = d6 = d2;
          d3 = descent;
          d5 = ascent;
        }
      }
      else
      {
        float f = baselineOffsets[baselineIndex];
        if (j != 0)
        {
          d2 += d1 * ssOffset;
          d3 = d2 + d1 * ascent;
          d5 = d2 - d1 * descent;
          d4 = f - ascent;
          d6 = f + descent;
        }
        else
        {
          d2 -= d1 * ssOffset;
          d4 = d2 + d1 * ascent;
          d6 = d2 - d1 * descent;
          d3 = f + ascent;
          d5 = f + descent;
        }
      }
    }
    arrayOfFloat[2] = ((float)d3);
    arrayOfFloat[3] = ((float)d4);
    arrayOfFloat[4] = ((float)d5);
    arrayOfFloat[5] = ((float)d6);
    return arrayOfFloat;
  }
  
  public float[] getCaretInfo(TextHitInfo paramTextHitInfo)
  {
    return getCaretInfo(paramTextHitInfo, getNaturalBounds());
  }
  
  private int hitToCaret(TextHitInfo paramTextHitInfo)
  {
    int i = paramTextHitInfo.getCharIndex();
    if (i < 0) {
      return textLine.isDirectionLTR() ? 0 : characterCount;
    }
    if (i >= characterCount) {
      return textLine.isDirectionLTR() ? characterCount : 0;
    }
    int j = textLine.logicalToVisual(i);
    if (paramTextHitInfo.isLeadingEdge() != textLine.isCharLTR(i)) {
      j++;
    }
    return j;
  }
  
  private TextHitInfo caretToHit(int paramInt)
  {
    if ((paramInt == 0) || (paramInt == characterCount))
    {
      if ((paramInt == characterCount) == textLine.isDirectionLTR()) {
        return TextHitInfo.leading(characterCount);
      }
      return TextHitInfo.trailing(-1);
    }
    int i = textLine.visualToLogical(paramInt);
    boolean bool = textLine.isCharLTR(i);
    return bool ? TextHitInfo.leading(i) : TextHitInfo.trailing(i);
  }
  
  private boolean caretIsValid(int paramInt)
  {
    if ((paramInt == characterCount) || (paramInt == 0)) {
      return true;
    }
    int i = textLine.visualToLogical(paramInt);
    if (!textLine.isCharLTR(i))
    {
      i = textLine.visualToLogical(paramInt - 1);
      if (textLine.isCharLTR(i)) {
        return true;
      }
    }
    return textLine.caretAtOffsetIsValid(i);
  }
  
  public TextHitInfo getNextRightHit(TextHitInfo paramTextHitInfo)
  {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    int i = hitToCaret(paramTextHitInfo);
    if (i == characterCount) {
      return null;
    }
    do
    {
      i++;
    } while (!caretIsValid(i));
    return caretToHit(i);
  }
  
  public TextHitInfo getNextRightHit(int paramInt, CaretPolicy paramCaretPolicy)
  {
    if ((paramInt < 0) || (paramInt > characterCount)) {
      throw new IllegalArgumentException("Offset out of bounds in TextLayout.getNextRightHit()");
    }
    if (paramCaretPolicy == null) {
      throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getNextRightHit()");
    }
    TextHitInfo localTextHitInfo1 = TextHitInfo.afterOffset(paramInt);
    TextHitInfo localTextHitInfo2 = localTextHitInfo1.getOtherHit();
    TextHitInfo localTextHitInfo3 = getNextRightHit(paramCaretPolicy.getStrongCaret(localTextHitInfo1, localTextHitInfo2, this));
    if (localTextHitInfo3 != null)
    {
      TextHitInfo localTextHitInfo4 = getVisualOtherHit(localTextHitInfo3);
      return paramCaretPolicy.getStrongCaret(localTextHitInfo4, localTextHitInfo3, this);
    }
    return null;
  }
  
  public TextHitInfo getNextRightHit(int paramInt)
  {
    return getNextRightHit(paramInt, DEFAULT_CARET_POLICY);
  }
  
  public TextHitInfo getNextLeftHit(TextHitInfo paramTextHitInfo)
  {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    int i = hitToCaret(paramTextHitInfo);
    if (i == 0) {
      return null;
    }
    do
    {
      i--;
    } while (!caretIsValid(i));
    return caretToHit(i);
  }
  
  public TextHitInfo getNextLeftHit(int paramInt, CaretPolicy paramCaretPolicy)
  {
    if (paramCaretPolicy == null) {
      throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getNextLeftHit()");
    }
    if ((paramInt < 0) || (paramInt > characterCount)) {
      throw new IllegalArgumentException("Offset out of bounds in TextLayout.getNextLeftHit()");
    }
    TextHitInfo localTextHitInfo1 = TextHitInfo.afterOffset(paramInt);
    TextHitInfo localTextHitInfo2 = localTextHitInfo1.getOtherHit();
    TextHitInfo localTextHitInfo3 = getNextLeftHit(paramCaretPolicy.getStrongCaret(localTextHitInfo1, localTextHitInfo2, this));
    if (localTextHitInfo3 != null)
    {
      TextHitInfo localTextHitInfo4 = getVisualOtherHit(localTextHitInfo3);
      return paramCaretPolicy.getStrongCaret(localTextHitInfo4, localTextHitInfo3, this);
    }
    return null;
  }
  
  public TextHitInfo getNextLeftHit(int paramInt)
  {
    return getNextLeftHit(paramInt, DEFAULT_CARET_POLICY);
  }
  
  public TextHitInfo getVisualOtherHit(TextHitInfo paramTextHitInfo)
  {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    int i = paramTextHitInfo.getCharIndex();
    int k;
    int j;
    boolean bool;
    if ((i == -1) || (i == characterCount))
    {
      if (textLine.isDirectionLTR() == (i == -1)) {
        k = 0;
      } else {
        k = characterCount - 1;
      }
      j = textLine.visualToLogical(k);
      if (textLine.isDirectionLTR() == (i == -1)) {
        bool = textLine.isCharLTR(j);
      } else {
        bool = !textLine.isCharLTR(j);
      }
    }
    else
    {
      k = textLine.logicalToVisual(i);
      int m;
      if (textLine.isCharLTR(i) == paramTextHitInfo.isLeadingEdge())
      {
        k--;
        m = 0;
      }
      else
      {
        k++;
        m = 1;
      }
      if ((k > -1) && (k < characterCount))
      {
        j = textLine.visualToLogical(k);
        bool = m == textLine.isCharLTR(j);
      }
      else
      {
        j = m == textLine.isDirectionLTR() ? characterCount : -1;
        bool = j == characterCount;
      }
    }
    return bool ? TextHitInfo.leading(j) : TextHitInfo.trailing(j);
  }
  
  private double[] getCaretPath(TextHitInfo paramTextHitInfo, Rectangle2D paramRectangle2D)
  {
    float[] arrayOfFloat = getCaretInfo(paramTextHitInfo, paramRectangle2D);
    return new double[] { arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5] };
  }
  
  private double[] getCaretPath(int paramInt, Rectangle2D paramRectangle2D, boolean paramBoolean)
  {
    float[] arrayOfFloat = getCaretInfo(paramInt, paramRectangle2D, null);
    double d1 = arrayOfFloat[0];
    double d2 = arrayOfFloat[1];
    double d7 = -3141.59D;
    double d8 = -2.7D;
    double d9 = paramRectangle2D.getX();
    double d10 = d9 + paramRectangle2D.getWidth();
    double d11 = paramRectangle2D.getY();
    double d12 = d11 + paramRectangle2D.getHeight();
    int i = 0;
    double d3;
    double d5;
    double d4;
    double d6;
    if (isVerticalLine)
    {
      if (d2 >= 0.0D)
      {
        d3 = d9;
        d5 = d10;
      }
      else
      {
        d5 = d9;
        d3 = d10;
      }
      d4 = d1 + d3 * d2;
      d6 = d1 + d5 * d2;
      if (paramBoolean) {
        if (d4 < d11)
        {
          if ((d2 <= 0.0D) || (d6 <= d11))
          {
            d4 = d6 = d11;
          }
          else
          {
            i = 1;
            d4 = d11;
            d8 = d11;
            d7 = d5 + (d11 - d6) / d2;
            if (d6 > d12) {
              d6 = d12;
            }
          }
        }
        else if (d6 > d12) {
          if ((d2 >= 0.0D) || (d4 >= d12))
          {
            d4 = d6 = d12;
          }
          else
          {
            i = 1;
            d6 = d12;
            d8 = d12;
            d7 = d3 + (d12 - d5) / d2;
          }
        }
      }
    }
    else
    {
      if (d2 >= 0.0D)
      {
        d4 = d12;
        d6 = d11;
      }
      else
      {
        d6 = d12;
        d4 = d11;
      }
      d3 = d1 - d4 * d2;
      d5 = d1 - d6 * d2;
      if (paramBoolean) {
        if (d3 < d9)
        {
          if ((d2 <= 0.0D) || (d5 <= d9))
          {
            d3 = d5 = d9;
          }
          else
          {
            i = 1;
            d3 = d9;
            d7 = d9;
            d8 = d6 - (d9 - d5) / d2;
            if (d5 > d10) {
              d5 = d10;
            }
          }
        }
        else if (d5 > d10) {
          if ((d2 >= 0.0D) || (d3 >= d10))
          {
            d3 = d5 = d10;
          }
          else
          {
            i = 1;
            d5 = d10;
            d7 = d10;
            d8 = d4 - (d10 - d3) / d2;
          }
        }
      }
    }
    return new double[] { d3, d4, d5, i != 0 ? new double[] { d3, d4, d7, d8, d5, d6 } : d6 };
  }
  
  private static GeneralPath pathToShape(double[] paramArrayOfDouble, boolean paramBoolean, LayoutPathImpl paramLayoutPathImpl)
  {
    GeneralPath localGeneralPath = new GeneralPath(0, paramArrayOfDouble.length);
    localGeneralPath.moveTo((float)paramArrayOfDouble[0], (float)paramArrayOfDouble[1]);
    for (int i = 2; i < paramArrayOfDouble.length; i += 2) {
      localGeneralPath.lineTo((float)paramArrayOfDouble[i], (float)paramArrayOfDouble[(i + 1)]);
    }
    if (paramBoolean) {
      localGeneralPath.closePath();
    }
    if (paramLayoutPathImpl != null) {
      localGeneralPath = (GeneralPath)paramLayoutPathImpl.mapShape(localGeneralPath);
    }
    return localGeneralPath;
  }
  
  public Shape getCaretShape(TextHitInfo paramTextHitInfo, Rectangle2D paramRectangle2D)
  {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    if (paramRectangle2D == null) {
      throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getCaret()");
    }
    return pathToShape(getCaretPath(paramTextHitInfo, paramRectangle2D), false, textLine.getLayoutPath());
  }
  
  public Shape getCaretShape(TextHitInfo paramTextHitInfo)
  {
    return getCaretShape(paramTextHitInfo, getNaturalBounds());
  }
  
  private final TextHitInfo getStrongHit(TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
  {
    int i = getCharacterLevel(paramTextHitInfo1.getCharIndex());
    int j = getCharacterLevel(paramTextHitInfo2.getCharIndex());
    if (i == j)
    {
      if ((paramTextHitInfo2.isLeadingEdge()) && (!paramTextHitInfo1.isLeadingEdge())) {
        return paramTextHitInfo2;
      }
      return paramTextHitInfo1;
    }
    return i < j ? paramTextHitInfo1 : paramTextHitInfo2;
  }
  
  public byte getCharacterLevel(int paramInt)
  {
    if ((paramInt < -1) || (paramInt > characterCount)) {
      throw new IllegalArgumentException("Index is out of range in getCharacterLevel.");
    }
    ensureCache();
    if ((paramInt == -1) || (paramInt == characterCount)) {
      return (byte)(textLine.isDirectionLTR() ? 0 : 1);
    }
    return textLine.getCharLevel(paramInt);
  }
  
  public Shape[] getCaretShapes(int paramInt, Rectangle2D paramRectangle2D, CaretPolicy paramCaretPolicy)
  {
    ensureCache();
    if ((paramInt < 0) || (paramInt > characterCount)) {
      throw new IllegalArgumentException("Offset out of bounds in TextLayout.getCaretShapes()");
    }
    if (paramRectangle2D == null) {
      throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getCaretShapes()");
    }
    if (paramCaretPolicy == null) {
      throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getCaretShapes()");
    }
    Shape[] arrayOfShape = new Shape[2];
    TextHitInfo localTextHitInfo1 = TextHitInfo.afterOffset(paramInt);
    int i = hitToCaret(localTextHitInfo1);
    LayoutPathImpl localLayoutPathImpl = textLine.getLayoutPath();
    GeneralPath localGeneralPath1 = pathToShape(getCaretPath(localTextHitInfo1, paramRectangle2D), false, localLayoutPathImpl);
    TextHitInfo localTextHitInfo2 = localTextHitInfo1.getOtherHit();
    int j = hitToCaret(localTextHitInfo2);
    if (i == j)
    {
      arrayOfShape[0] = localGeneralPath1;
    }
    else
    {
      GeneralPath localGeneralPath2 = pathToShape(getCaretPath(localTextHitInfo2, paramRectangle2D), false, localLayoutPathImpl);
      TextHitInfo localTextHitInfo3 = paramCaretPolicy.getStrongCaret(localTextHitInfo1, localTextHitInfo2, this);
      boolean bool = localTextHitInfo3.equals(localTextHitInfo1);
      if (bool)
      {
        arrayOfShape[0] = localGeneralPath1;
        arrayOfShape[1] = localGeneralPath2;
      }
      else
      {
        arrayOfShape[0] = localGeneralPath2;
        arrayOfShape[1] = localGeneralPath1;
      }
    }
    return arrayOfShape;
  }
  
  public Shape[] getCaretShapes(int paramInt, Rectangle2D paramRectangle2D)
  {
    return getCaretShapes(paramInt, paramRectangle2D, DEFAULT_CARET_POLICY);
  }
  
  public Shape[] getCaretShapes(int paramInt)
  {
    return getCaretShapes(paramInt, getNaturalBounds(), DEFAULT_CARET_POLICY);
  }
  
  private GeneralPath boundingShape(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    GeneralPath localGeneralPath = pathToShape(paramArrayOfDouble1, false, null);
    int i;
    if (isVerticalLine) {
      i = (paramArrayOfDouble1[1] > paramArrayOfDouble1[(paramArrayOfDouble1.length - 1)] ? 1 : 0) == (paramArrayOfDouble2[1] > paramArrayOfDouble2[(paramArrayOfDouble2.length - 1)] ? 1 : 0) ? 1 : 0;
    } else {
      i = (paramArrayOfDouble1[0] > paramArrayOfDouble1[(paramArrayOfDouble1.length - 2)] ? 1 : 0) == (paramArrayOfDouble2[0] > paramArrayOfDouble2[(paramArrayOfDouble2.length - 2)] ? 1 : 0) ? 1 : 0;
    }
    int j;
    int k;
    int m;
    if (i != 0)
    {
      j = paramArrayOfDouble2.length - 2;
      k = -2;
      m = -2;
    }
    else
    {
      j = 0;
      k = paramArrayOfDouble2.length;
      m = 2;
    }
    int n = j;
    while (n != k)
    {
      localGeneralPath.lineTo((float)paramArrayOfDouble2[n], (float)paramArrayOfDouble2[(n + 1)]);
      n += m;
    }
    localGeneralPath.closePath();
    return localGeneralPath;
  }
  
  private GeneralPath caretBoundingShape(int paramInt1, int paramInt2, Rectangle2D paramRectangle2D)
  {
    if (paramInt1 > paramInt2)
    {
      int i = paramInt1;
      paramInt1 = paramInt2;
      paramInt2 = i;
    }
    return boundingShape(getCaretPath(paramInt1, paramRectangle2D, true), getCaretPath(paramInt2, paramRectangle2D, true));
  }
  
  private GeneralPath leftShape(Rectangle2D paramRectangle2D)
  {
    double[] arrayOfDouble1;
    if (isVerticalLine) {
      arrayOfDouble1 = new double[] { paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getX() + paramRectangle2D.getWidth(), paramRectangle2D.getY() };
    } else {
      arrayOfDouble1 = new double[] { paramRectangle2D.getX(), paramRectangle2D.getY() + paramRectangle2D.getHeight(), paramRectangle2D.getX(), paramRectangle2D.getY() };
    }
    double[] arrayOfDouble2 = getCaretPath(0, paramRectangle2D, true);
    return boundingShape(arrayOfDouble1, arrayOfDouble2);
  }
  
  private GeneralPath rightShape(Rectangle2D paramRectangle2D)
  {
    double[] arrayOfDouble1;
    if (isVerticalLine) {
      arrayOfDouble1 = new double[] { paramRectangle2D.getX(), paramRectangle2D.getY() + paramRectangle2D.getHeight(), paramRectangle2D.getX() + paramRectangle2D.getWidth(), paramRectangle2D.getY() + paramRectangle2D.getHeight() };
    } else {
      arrayOfDouble1 = new double[] { paramRectangle2D.getX() + paramRectangle2D.getWidth(), paramRectangle2D.getY() + paramRectangle2D.getHeight(), paramRectangle2D.getX() + paramRectangle2D.getWidth(), paramRectangle2D.getY() };
    }
    double[] arrayOfDouble2 = getCaretPath(characterCount, paramRectangle2D, true);
    return boundingShape(arrayOfDouble2, arrayOfDouble1);
  }
  
  public int[] getLogicalRangesForVisualSelection(TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
  {
    ensureCache();
    checkTextHit(paramTextHitInfo1);
    checkTextHit(paramTextHitInfo2);
    boolean[] arrayOfBoolean = new boolean[characterCount];
    int i = hitToCaret(paramTextHitInfo1);
    int j = hitToCaret(paramTextHitInfo2);
    if (i > j)
    {
      k = i;
      i = j;
      j = k;
    }
    if (i < j) {
      for (k = i; k < j; k++) {
        arrayOfBoolean[textLine.visualToLogical(k)] = true;
      }
    }
    int k = 0;
    int m = 0;
    for (int n = 0; n < characterCount; n++) {
      if (arrayOfBoolean[n] != m)
      {
        m = m == 0 ? 1 : 0;
        if (m != 0) {
          k++;
        }
      }
    }
    int[] arrayOfInt = new int[k * 2];
    k = 0;
    m = 0;
    for (int i1 = 0; i1 < characterCount; i1++) {
      if (arrayOfBoolean[i1] != m)
      {
        arrayOfInt[(k++)] = i1;
        m = m == 0 ? 1 : 0;
      }
    }
    if (m != 0) {
      arrayOfInt[(k++)] = characterCount;
    }
    return arrayOfInt;
  }
  
  public Shape getVisualHighlightShape(TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2, Rectangle2D paramRectangle2D)
  {
    ensureCache();
    checkTextHit(paramTextHitInfo1);
    checkTextHit(paramTextHitInfo2);
    if (paramRectangle2D == null) {
      throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getVisualHighlightShape()");
    }
    GeneralPath localGeneralPath = new GeneralPath(0);
    int i = hitToCaret(paramTextHitInfo1);
    int j = hitToCaret(paramTextHitInfo2);
    localGeneralPath.append(caretBoundingShape(i, j, paramRectangle2D), false);
    if ((i == 0) || (j == 0))
    {
      localObject = leftShape(paramRectangle2D);
      if (!((GeneralPath)localObject).getBounds().isEmpty()) {
        localGeneralPath.append((Shape)localObject, false);
      }
    }
    if ((i == characterCount) || (j == characterCount))
    {
      localObject = rightShape(paramRectangle2D);
      if (!((GeneralPath)localObject).getBounds().isEmpty()) {
        localGeneralPath.append((Shape)localObject, false);
      }
    }
    Object localObject = textLine.getLayoutPath();
    if (localObject != null) {
      localGeneralPath = (GeneralPath)((LayoutPathImpl)localObject).mapShape(localGeneralPath);
    }
    return localGeneralPath;
  }
  
  public Shape getVisualHighlightShape(TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2)
  {
    return getVisualHighlightShape(paramTextHitInfo1, paramTextHitInfo2, getNaturalBounds());
  }
  
  public Shape getLogicalHighlightShape(int paramInt1, int paramInt2, Rectangle2D paramRectangle2D)
  {
    if (paramRectangle2D == null) {
      throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getLogicalHighlightShape()");
    }
    ensureCache();
    if (paramInt1 > paramInt2)
    {
      int i = paramInt1;
      paramInt1 = paramInt2;
      paramInt2 = i;
    }
    if ((paramInt1 < 0) || (paramInt2 > characterCount)) {
      throw new IllegalArgumentException("Range is invalid in TextLayout.getLogicalHighlightShape()");
    }
    GeneralPath localGeneralPath = new GeneralPath(0);
    Object localObject1 = new int[10];
    int j = 0;
    if (paramInt1 < paramInt2)
    {
      k = paramInt1;
      do
      {
        localObject1[(j++)] = hitToCaret(TextHitInfo.leading(k));
        boolean bool = textLine.isCharLTR(k);
        do
        {
          k++;
        } while ((k < paramInt2) && (textLine.isCharLTR(k) == bool));
        int m = k;
        localObject1[(j++)] = hitToCaret(TextHitInfo.trailing(m - 1));
        if (j == localObject1.length)
        {
          int[] arrayOfInt = new int[localObject1.length + 10];
          System.arraycopy(localObject1, 0, arrayOfInt, 0, j);
          localObject1 = arrayOfInt;
        }
      } while (k < paramInt2);
    }
    else
    {
      j = 2;
      localObject1[0] = (localObject1[1] = hitToCaret(TextHitInfo.leading(paramInt1)));
    }
    for (int k = 0; k < j; k += 2) {
      localGeneralPath.append(caretBoundingShape(localObject1[k], localObject1[(k + 1)], paramRectangle2D), false);
    }
    if (paramInt1 != paramInt2)
    {
      if (((textLine.isDirectionLTR()) && (paramInt1 == 0)) || ((!textLine.isDirectionLTR()) && (paramInt2 == characterCount)))
      {
        localObject2 = leftShape(paramRectangle2D);
        if (!((GeneralPath)localObject2).getBounds().isEmpty()) {
          localGeneralPath.append((Shape)localObject2, false);
        }
      }
      if (((textLine.isDirectionLTR()) && (paramInt2 == characterCount)) || ((!textLine.isDirectionLTR()) && (paramInt1 == 0)))
      {
        localObject2 = rightShape(paramRectangle2D);
        if (!((GeneralPath)localObject2).getBounds().isEmpty()) {
          localGeneralPath.append((Shape)localObject2, false);
        }
      }
    }
    Object localObject2 = textLine.getLayoutPath();
    if (localObject2 != null) {
      localGeneralPath = (GeneralPath)((LayoutPathImpl)localObject2).mapShape(localGeneralPath);
    }
    return localGeneralPath;
  }
  
  public Shape getLogicalHighlightShape(int paramInt1, int paramInt2)
  {
    return getLogicalHighlightShape(paramInt1, paramInt2, getNaturalBounds());
  }
  
  public Shape getBlackBoxBounds(int paramInt1, int paramInt2)
  {
    ensureCache();
    if (paramInt1 > paramInt2)
    {
      int i = paramInt1;
      paramInt1 = paramInt2;
      paramInt2 = i;
    }
    if ((paramInt1 < 0) || (paramInt2 > characterCount)) {
      throw new IllegalArgumentException("Invalid range passed to TextLayout.getBlackBoxBounds()");
    }
    GeneralPath localGeneralPath = new GeneralPath(1);
    if (paramInt1 < characterCount) {
      for (int j = paramInt1; j < paramInt2; j++)
      {
        Rectangle2D localRectangle2D = textLine.getCharBounds(j);
        if (!localRectangle2D.isEmpty()) {
          localGeneralPath.append(localRectangle2D, false);
        }
      }
    }
    if ((dx != 0.0F) || (dy != 0.0F))
    {
      localObject = AffineTransform.getTranslateInstance(dx, dy);
      localGeneralPath = (GeneralPath)((AffineTransform)localObject).createTransformedShape(localGeneralPath);
    }
    Object localObject = textLine.getLayoutPath();
    if (localObject != null) {
      localGeneralPath = (GeneralPath)((LayoutPathImpl)localObject).mapShape(localGeneralPath);
    }
    return localGeneralPath;
  }
  
  private float caretToPointDistance(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2)
  {
    float f1 = isVerticalLine ? paramFloat2 : paramFloat1;
    float f2 = isVerticalLine ? -paramFloat1 : paramFloat2;
    return f1 - paramArrayOfFloat[0] + f2 * paramArrayOfFloat[1];
  }
  
  public TextHitInfo hitTestChar(float paramFloat1, float paramFloat2, Rectangle2D paramRectangle2D)
  {
    LayoutPathImpl localLayoutPathImpl = textLine.getLayoutPath();
    boolean bool = false;
    if (localLayoutPathImpl != null)
    {
      Point2D.Float localFloat = new Point2D.Float(paramFloat1, paramFloat2);
      bool = localLayoutPathImpl.pointToPath(localFloat, localFloat);
      paramFloat1 = x;
      paramFloat2 = y;
    }
    if (isVertical())
    {
      if (paramFloat2 < paramRectangle2D.getMinY()) {
        return TextHitInfo.leading(0);
      }
      if (paramFloat2 >= paramRectangle2D.getMaxY()) {
        return TextHitInfo.trailing(characterCount - 1);
      }
    }
    else
    {
      if (paramFloat1 < paramRectangle2D.getMinX()) {
        return isLeftToRight() ? TextHitInfo.leading(0) : TextHitInfo.trailing(characterCount - 1);
      }
      if (paramFloat1 >= paramRectangle2D.getMaxX()) {
        return isLeftToRight() ? TextHitInfo.trailing(characterCount - 1) : TextHitInfo.leading(0);
      }
    }
    double d1 = Double.MAX_VALUE;
    int i = 0;
    int j = -1;
    Object localObject = null;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f4 = 0.0F;
    float f5 = 0.0F;
    float f6 = 0.0F;
    for (int k = 0; k < characterCount; k++) {
      if (textLine.caretAtOffsetIsValid(k))
      {
        if (j == -1) {
          j = k;
        }
        CoreMetrics localCoreMetrics = textLine.getCoreMetricsAt(k);
        if (localCoreMetrics != localObject)
        {
          localObject = localCoreMetrics;
          if (baselineIndex == -1) {
            f4 = -(textLine.getMetrics().ascent - ascent) + ssOffset;
          } else if (baselineIndex == -2) {
            f4 = textLine.getMetrics().descent - descent + ssOffset;
          } else {
            f4 = localCoreMetrics.effectiveBaselineOffset(baselineOffsets) + ssOffset;
          }
          f7 = (descent - ascent) / 2.0F - f4;
          f5 = f7 * italicAngle;
          f4 += f7;
          f6 = (f4 - paramFloat2) * (f4 - paramFloat2);
        }
        float f7 = textLine.getCharXPosition(k);
        float f8 = textLine.getCharAdvance(k);
        float f9 = f8 / 2.0F;
        f7 += f9 - f5;
        double d2 = Math.sqrt(4.0F * (f7 - paramFloat1) * (f7 - paramFloat1) + f6);
        if (d2 < d1)
        {
          d1 = d2;
          i = k;
          j = -1;
          f1 = f7;
          f2 = f4;
          f3 = italicAngle;
        }
      }
    }
    k = paramFloat1 < f1 - (paramFloat2 - f2) * f3 ? 1 : 0;
    int m = textLine.isCharLTR(i) == k ? 1 : 0;
    if (j == -1) {
      j = characterCount;
    }
    TextHitInfo localTextHitInfo = m != 0 ? TextHitInfo.leading(i) : TextHitInfo.trailing(j - 1);
    return localTextHitInfo;
  }
  
  public TextHitInfo hitTestChar(float paramFloat1, float paramFloat2)
  {
    return hitTestChar(paramFloat1, paramFloat2, getNaturalBounds());
  }
  
  public int hashCode()
  {
    if (hashCodeCache == 0)
    {
      ensureCache();
      hashCodeCache = textLine.hashCode();
    }
    return hashCodeCache;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof TextLayout)) && (equals((TextLayout)paramObject));
  }
  
  public boolean equals(TextLayout paramTextLayout)
  {
    if (paramTextLayout == null) {
      return false;
    }
    if (paramTextLayout == this) {
      return true;
    }
    ensureCache();
    return textLine.equals(textLine);
  }
  
  public String toString()
  {
    ensureCache();
    return textLine.toString();
  }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2)
  {
    if (paramGraphics2D == null) {
      throw new IllegalArgumentException("Null Graphics2D passed to TextLayout.draw()");
    }
    textLine.draw(paramGraphics2D, paramFloat1 - dx, paramFloat2 - dy);
  }
  
  TextLine getTextLineForTesting()
  {
    return textLine;
  }
  
  private static int sameBaselineUpTo(Font paramFont, char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return paramInt2;
  }
  
  static byte getBaselineFromGraphic(GraphicAttribute paramGraphicAttribute)
  {
    byte b = (byte)paramGraphicAttribute.getAlignment();
    if ((b == -2) || (b == -1)) {
      return 0;
    }
    return b;
  }
  
  public Shape getOutline(AffineTransform paramAffineTransform)
  {
    ensureCache();
    Shape localShape = textLine.getOutline(paramAffineTransform);
    LayoutPathImpl localLayoutPathImpl = textLine.getLayoutPath();
    if (localLayoutPathImpl != null) {
      localShape = localLayoutPathImpl.mapShape(localShape);
    }
    return localShape;
  }
  
  public LayoutPath getLayoutPath()
  {
    return textLine.getLayoutPath();
  }
  
  public void hitToPoint(TextHitInfo paramTextHitInfo, Point2D paramPoint2D)
  {
    if ((paramTextHitInfo == null) || (paramPoint2D == null)) {
      throw new NullPointerException((paramTextHitInfo == null ? "hit" : "point") + " can't be null");
    }
    ensureCache();
    checkTextHit(paramTextHitInfo);
    float f1 = 0.0F;
    float f2 = 0.0F;
    int i = paramTextHitInfo.getCharIndex();
    boolean bool1 = paramTextHitInfo.isLeadingEdge();
    boolean bool2;
    if ((i == -1) || (i == textLine.characterCount()))
    {
      bool2 = textLine.isDirectionLTR();
      f1 = bool2 == (i == -1) ? 0.0F : lineMetrics.advance;
    }
    else
    {
      bool2 = textLine.isCharLTR(i);
      f1 = textLine.getCharLinePosition(i, bool1);
      f2 = textLine.getCharYPosition(i);
    }
    paramPoint2D.setLocation(f1, f2);
    LayoutPathImpl localLayoutPathImpl = textLine.getLayoutPath();
    if (localLayoutPathImpl != null) {
      localLayoutPathImpl.pathToPoint(paramPoint2D, bool2 != bool1, paramPoint2D);
    }
  }
  
  public static class CaretPolicy
  {
    public CaretPolicy() {}
    
    public TextHitInfo getStrongCaret(TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2, TextLayout paramTextLayout)
    {
      return paramTextLayout.getStrongHit(paramTextHitInfo1, paramTextHitInfo2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\TextLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */