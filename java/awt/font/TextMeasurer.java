package java.awt.font;

import java.awt.Font;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;
import java.text.Bidi;
import java.text.BreakIterator;
import java.util.Hashtable;
import java.util.Map;
import sun.font.AttributeValues;
import sun.font.BidiUtils;
import sun.font.TextLabelFactory;
import sun.font.TextLineComponent;

public final class TextMeasurer
  implements Cloneable
{
  private static float EST_LINES = 2.1F;
  private FontRenderContext fFrc;
  private int fStart;
  private char[] fChars;
  private Bidi fBidi;
  private byte[] fLevels;
  private TextLineComponent[] fComponents;
  private int fComponentStart;
  private int fComponentLimit;
  private boolean haveLayoutWindow;
  private BreakIterator fLineBreak = null;
  private CharArrayIterator charIter = null;
  int layoutCount = 0;
  int layoutCharCount = 0;
  private StyledParagraph fParagraph;
  private boolean fIsDirectionLTR;
  private byte fBaseline;
  private float[] fBaselineOffsets;
  private float fJustifyRatio = 1.0F;
  private int formattedChars = 0;
  private static boolean wantStats = false;
  private boolean collectStats = false;
  
  public TextMeasurer(AttributedCharacterIterator paramAttributedCharacterIterator, FontRenderContext paramFontRenderContext)
  {
    fFrc = paramFontRenderContext;
    initAll(paramAttributedCharacterIterator);
  }
  
  protected Object clone()
  {
    TextMeasurer localTextMeasurer;
    try
    {
      localTextMeasurer = (TextMeasurer)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new Error();
    }
    if (fComponents != null) {
      fComponents = ((TextLineComponent[])fComponents.clone());
    }
    return localTextMeasurer;
  }
  
  private void invalidateComponents()
  {
    fComponentStart = (fComponentLimit = fChars.length);
    fComponents = null;
    haveLayoutWindow = false;
  }
  
  private void initAll(AttributedCharacterIterator paramAttributedCharacterIterator)
  {
    fStart = paramAttributedCharacterIterator.getBeginIndex();
    fChars = new char[paramAttributedCharacterIterator.getEndIndex() - fStart];
    int i = 0;
    for (int j = paramAttributedCharacterIterator.first(); j != 65535; j = paramAttributedCharacterIterator.next()) {
      fChars[(i++)] = j;
    }
    paramAttributedCharacterIterator.first();
    fBidi = new Bidi(paramAttributedCharacterIterator);
    if (fBidi.isLeftToRight()) {
      fBidi = null;
    }
    paramAttributedCharacterIterator.first();
    Map localMap = paramAttributedCharacterIterator.getAttributes();
    NumericShaper localNumericShaper = AttributeValues.getNumericShaping(localMap);
    if (localNumericShaper != null) {
      localNumericShaper.shape(fChars, 0, fChars.length);
    }
    fParagraph = new StyledParagraph(paramAttributedCharacterIterator, fChars);
    fJustifyRatio = AttributeValues.getJustification(localMap);
    boolean bool = TextLine.advanceToFirstFont(paramAttributedCharacterIterator);
    Object localObject1;
    Object localObject2;
    if (bool)
    {
      localObject1 = TextLine.getFontAtCurrentPos(paramAttributedCharacterIterator);
      int k = paramAttributedCharacterIterator.getIndex() - paramAttributedCharacterIterator.getBeginIndex();
      localObject2 = ((Font)localObject1).getLineMetrics(fChars, k, k + 1, fFrc);
      fBaseline = ((byte)((LineMetrics)localObject2).getBaselineIndex());
      fBaselineOffsets = ((LineMetrics)localObject2).getBaselineOffsets();
    }
    else
    {
      localObject1 = (GraphicAttribute)localMap.get(TextAttribute.CHAR_REPLACEMENT);
      fBaseline = TextLayout.getBaselineFromGraphic((GraphicAttribute)localObject1);
      Hashtable localHashtable = new Hashtable(5, 0.9F);
      localObject2 = new Font(localHashtable);
      LineMetrics localLineMetrics = ((Font)localObject2).getLineMetrics(" ", 0, 1, fFrc);
      fBaselineOffsets = localLineMetrics.getBaselineOffsets();
    }
    fBaselineOffsets = TextLine.getNormalizedOffsets(fBaselineOffsets, fBaseline);
    invalidateComponents();
  }
  
  private void generateComponents(int paramInt1, int paramInt2)
  {
    if (collectStats) {
      formattedChars += paramInt2 - paramInt1;
    }
    int i = 0;
    TextLabelFactory localTextLabelFactory = new TextLabelFactory(fFrc, fChars, fBidi, i);
    int[] arrayOfInt1 = null;
    if (fBidi != null)
    {
      fLevels = BidiUtils.getLevels(fBidi);
      int[] arrayOfInt2 = BidiUtils.createVisualToLogicalMap(fLevels);
      arrayOfInt1 = BidiUtils.createInverseMap(arrayOfInt2);
      fIsDirectionLTR = fBidi.baseIsLeftToRight();
    }
    else
    {
      fLevels = null;
      fIsDirectionLTR = true;
    }
    try
    {
      fComponents = TextLine.getComponents(fParagraph, fChars, paramInt1, paramInt2, arrayOfInt1, fLevels, localTextLabelFactory);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      System.out.println("startingAt=" + paramInt1 + "; endingAt=" + paramInt2);
      System.out.println("fComponentLimit=" + fComponentLimit);
      throw localIllegalArgumentException;
    }
    fComponentStart = paramInt1;
    fComponentLimit = paramInt2;
  }
  
  private int calcLineBreak(int paramInt, float paramFloat)
  {
    int i = paramInt;
    float f = paramFloat;
    int k = fComponentStart;
    for (int j = 0; j < fComponents.length; j++)
    {
      int m = k + fComponents[j].getNumCharacters();
      if (m > i) {
        break;
      }
      k = m;
    }
    while (j < fComponents.length)
    {
      TextLineComponent localTextLineComponent = fComponents[j];
      int n = localTextLineComponent.getNumCharacters();
      int i1 = localTextLineComponent.getLineBreakIndex(i - k, f);
      if ((i1 == n) && (j < fComponents.length))
      {
        f -= localTextLineComponent.getAdvanceBetween(i - k, i1);
        k += n;
        i = k;
      }
      else
      {
        return k + i1;
      }
      j++;
    }
    if (fComponentLimit < fChars.length)
    {
      generateComponents(paramInt, fChars.length);
      return calcLineBreak(paramInt, paramFloat);
    }
    return fChars.length;
  }
  
  private int trailingCdWhitespaceStart(int paramInt1, int paramInt2)
  {
    if (fLevels != null)
    {
      int i = (byte)(fIsDirectionLTR ? 0 : 1);
      int j = paramInt2;
      do
      {
        j--;
        if (j < paramInt1) {
          break;
        }
      } while ((fLevels[j] % 2 != i) && (Character.getDirectionality(fChars[j]) == 12));
      j++;
      return j;
    }
    return paramInt1;
  }
  
  private TextLineComponent[] makeComponentsOnRange(int paramInt1, int paramInt2)
  {
    int i = trailingCdWhitespaceStart(paramInt1, paramInt2);
    int k = fComponentStart;
    for (int j = 0; j < fComponents.length; j++)
    {
      m = k + fComponents[j].getNumCharacters();
      if (m > paramInt1) {
        break;
      }
      k = m;
    }
    int n = 0;
    int i1 = k;
    int i2 = j;
    int i3 = 1;
    int i4;
    while (i3 != 0)
    {
      i4 = i1 + fComponents[i2].getNumCharacters();
      if ((i > Math.max(i1, paramInt1)) && (i < Math.min(i4, paramInt2))) {
        n = 1;
      }
      if (i4 >= paramInt2) {
        i3 = 0;
      } else {
        i1 = i4;
      }
      i2++;
    }
    int m = i2 - j;
    if (n != 0) {
      m++;
    }
    TextLineComponent[] arrayOfTextLineComponent = new TextLineComponent[m];
    i1 = 0;
    i2 = paramInt1;
    i3 = i;
    if (i3 == paramInt1)
    {
      i4 = fIsDirectionLTR ? 0 : 1;
      i3 = paramInt2;
    }
    else
    {
      i4 = 2;
    }
    while (i2 < paramInt2)
    {
      int i5 = fComponents[j].getNumCharacters();
      int i6 = k + i5;
      int i7 = Math.max(i2, k);
      int i8 = Math.min(i3, i6);
      arrayOfTextLineComponent[(i1++)] = fComponents[j].getSubset(i7 - k, i8 - k, i4);
      i2 += i8 - i7;
      if (i2 == i3)
      {
        i3 = paramInt2;
        i4 = fIsDirectionLTR ? 0 : 1;
      }
      if (i2 == i6)
      {
        j++;
        k = i6;
      }
    }
    return arrayOfTextLineComponent;
  }
  
  private TextLine makeTextLineOnRange(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt1 = null;
    byte[] arrayOfByte = null;
    if (fBidi != null)
    {
      localObject = fBidi.createLineBidi(paramInt1, paramInt2);
      arrayOfByte = BidiUtils.getLevels((Bidi)localObject);
      int[] arrayOfInt2 = BidiUtils.createVisualToLogicalMap(arrayOfByte);
      arrayOfInt1 = BidiUtils.createInverseMap(arrayOfInt2);
    }
    Object localObject = makeComponentsOnRange(paramInt1, paramInt2);
    return new TextLine(fFrc, (TextLineComponent[])localObject, fBaselineOffsets, fChars, paramInt1, paramInt2, arrayOfInt1, arrayOfByte, fIsDirectionLTR);
  }
  
  private void ensureComponents(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < fComponentStart) || (paramInt2 > fComponentLimit)) {
      generateComponents(paramInt1, paramInt2);
    }
  }
  
  private void makeLayoutWindow(int paramInt)
  {
    int i = paramInt;
    int j = fChars.length;
    if ((layoutCount > 0) && (!haveLayoutWindow))
    {
      float f = Math.max(layoutCharCount / layoutCount, 1);
      j = Math.min(paramInt + (int)(f * EST_LINES), fChars.length);
    }
    if ((paramInt > 0) || (j < fChars.length))
    {
      if (charIter == null) {
        charIter = new CharArrayIterator(fChars);
      } else {
        charIter.reset(fChars);
      }
      if (fLineBreak == null) {
        fLineBreak = BreakIterator.getLineInstance();
      }
      fLineBreak.setText(charIter);
      if ((paramInt > 0) && (!fLineBreak.isBoundary(paramInt))) {
        i = fLineBreak.preceding(paramInt);
      }
      if ((j < fChars.length) && (!fLineBreak.isBoundary(j))) {
        j = fLineBreak.following(j);
      }
    }
    ensureComponents(i, j);
    haveLayoutWindow = true;
  }
  
  public int getLineBreakIndex(int paramInt, float paramFloat)
  {
    int i = paramInt - fStart;
    if ((!haveLayoutWindow) || (i < fComponentStart) || (i >= fComponentLimit)) {
      makeLayoutWindow(i);
    }
    return calcLineBreak(i, paramFloat) + fStart;
  }
  
  public float getAdvanceBetween(int paramInt1, int paramInt2)
  {
    int i = paramInt1 - fStart;
    int j = paramInt2 - fStart;
    ensureComponents(i, j);
    TextLine localTextLine = makeTextLineOnRange(i, j);
    return getMetricsadvance;
  }
  
  public TextLayout getLayout(int paramInt1, int paramInt2)
  {
    int i = paramInt1 - fStart;
    int j = paramInt2 - fStart;
    ensureComponents(i, j);
    TextLine localTextLine = makeTextLineOnRange(i, j);
    if (j < fChars.length)
    {
      layoutCharCount += paramInt2 - paramInt1;
      layoutCount += 1;
    }
    return new TextLayout(localTextLine, fBaseline, fBaselineOffsets, fJustifyRatio);
  }
  
  private void printStats()
  {
    System.out.println("formattedChars: " + formattedChars);
    collectStats = false;
  }
  
  public void insertChar(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt)
  {
    if (collectStats) {
      printStats();
    }
    if (wantStats) {
      collectStats = true;
    }
    fStart = paramAttributedCharacterIterator.getBeginIndex();
    int i = paramAttributedCharacterIterator.getEndIndex();
    if (i - fStart != fChars.length + 1) {
      initAll(paramAttributedCharacterIterator);
    }
    char[] arrayOfChar = new char[i - fStart];
    int j = paramInt - fStart;
    System.arraycopy(fChars, 0, arrayOfChar, 0, j);
    int k = paramAttributedCharacterIterator.setIndex(paramInt);
    arrayOfChar[j] = k;
    System.arraycopy(fChars, j, arrayOfChar, j + 1, i - paramInt - 1);
    fChars = arrayOfChar;
    if ((fBidi != null) || (Bidi.requiresBidi(arrayOfChar, j, j + 1)) || (paramAttributedCharacterIterator.getAttribute(TextAttribute.BIDI_EMBEDDING) != null))
    {
      fBidi = new Bidi(paramAttributedCharacterIterator);
      if (fBidi.isLeftToRight()) {
        fBidi = null;
      }
    }
    fParagraph = StyledParagraph.insertChar(paramAttributedCharacterIterator, fChars, paramInt, fParagraph);
    invalidateComponents();
  }
  
  public void deleteChar(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt)
  {
    fStart = paramAttributedCharacterIterator.getBeginIndex();
    int i = paramAttributedCharacterIterator.getEndIndex();
    if (i - fStart != fChars.length - 1) {
      initAll(paramAttributedCharacterIterator);
    }
    char[] arrayOfChar = new char[i - fStart];
    int j = paramInt - fStart;
    System.arraycopy(fChars, 0, arrayOfChar, 0, paramInt - fStart);
    System.arraycopy(fChars, j + 1, arrayOfChar, j, i - paramInt);
    fChars = arrayOfChar;
    if (fBidi != null)
    {
      fBidi = new Bidi(paramAttributedCharacterIterator);
      if (fBidi.isLeftToRight()) {
        fBidi = null;
      }
    }
    fParagraph = StyledParagraph.deleteChar(paramAttributedCharacterIterator, fChars, paramInt, fParagraph);
    invalidateComponents();
  }
  
  char[] getChars()
  {
    return fChars;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\TextMeasurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */