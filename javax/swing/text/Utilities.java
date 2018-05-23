package javax.swing.text;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;

public class Utilities
{
  public Utilities() {}
  
  static JComponent getJComponent(View paramView)
  {
    if (paramView != null)
    {
      Container localContainer = paramView.getContainer();
      if ((localContainer instanceof JComponent)) {
        return (JComponent)localContainer;
      }
    }
    return null;
  }
  
  public static final int drawTabbedText(Segment paramSegment, int paramInt1, int paramInt2, Graphics paramGraphics, TabExpander paramTabExpander, int paramInt3)
  {
    return drawTabbedText(null, paramSegment, paramInt1, paramInt2, paramGraphics, paramTabExpander, paramInt3);
  }
  
  static final int drawTabbedText(View paramView, Segment paramSegment, int paramInt1, int paramInt2, Graphics paramGraphics, TabExpander paramTabExpander, int paramInt3)
  {
    return drawTabbedText(paramView, paramSegment, paramInt1, paramInt2, paramGraphics, paramTabExpander, paramInt3, null);
  }
  
  static final int drawTabbedText(View paramView, Segment paramSegment, int paramInt1, int paramInt2, Graphics paramGraphics, TabExpander paramTabExpander, int paramInt3, int[] paramArrayOfInt)
  {
    JComponent localJComponent = getJComponent(paramView);
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(localJComponent, paramGraphics);
    int i = paramInt1;
    char[] arrayOfChar = array;
    View localView1 = offset;
    int j = 0;
    int k = offset;
    int m = 0;
    View localView2 = -1;
    View localView3 = 0;
    View localView4 = 0;
    if (paramArrayOfInt != null)
    {
      localView5 = -paramInt3 + localView1;
      localView6 = null;
      if ((paramView != null) && ((localView6 = paramView.getParent()) != null)) {
        localView5 += localView6.getStartOffset();
      }
      m = paramArrayOfInt[0];
      localView2 = paramArrayOfInt[1] + localView5;
      localView3 = paramArrayOfInt[2] + localView5;
      localView4 = paramArrayOfInt[3] + localView5;
    }
    View localView5 = offset + count;
    for (View localView6 = localView1; localView6 < localView5; localView6++) {
      if ((arrayOfChar[localView6] == '\t') || (((m != 0) || (localView6 <= localView2)) && (arrayOfChar[localView6] == ' ') && (localView3 <= localView6) && (localView6 <= localView4)))
      {
        if (j > 0)
        {
          i = SwingUtilities2.drawChars(localJComponent, paramGraphics, arrayOfChar, k, j, paramInt1, paramInt2);
          j = 0;
        }
        k = localView6 + 1;
        if (arrayOfChar[localView6] == '\t')
        {
          if (paramTabExpander != null) {
            i = (int)paramTabExpander.nextTabStop(i, paramInt3 + localView6 - localView1);
          } else {
            i += localFontMetrics.charWidth(' ');
          }
        }
        else if (arrayOfChar[localView6] == ' ')
        {
          i += localFontMetrics.charWidth(' ') + m;
          if (localView6 <= localView2) {
            i++;
          }
        }
        paramInt1 = i;
      }
      else if ((arrayOfChar[localView6] == '\n') || (arrayOfChar[localView6] == '\r'))
      {
        if (j > 0)
        {
          i = SwingUtilities2.drawChars(localJComponent, paramGraphics, arrayOfChar, k, j, paramInt1, paramInt2);
          j = 0;
        }
        k = localView6 + 1;
        paramInt1 = i;
      }
      else
      {
        j++;
      }
    }
    if (j > 0) {
      i = SwingUtilities2.drawChars(localJComponent, paramGraphics, arrayOfChar, k, j, paramInt1, paramInt2);
    }
    return i;
  }
  
  public static final int getTabbedTextWidth(Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, TabExpander paramTabExpander, int paramInt2)
  {
    return getTabbedTextWidth(null, paramSegment, paramFontMetrics, paramInt1, paramTabExpander, paramInt2, null);
  }
  
  static final int getTabbedTextWidth(View paramView, Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, TabExpander paramTabExpander, int paramInt2, int[] paramArrayOfInt)
  {
    int i = paramInt1;
    char[] arrayOfChar = array;
    int j = offset;
    int k = offset + count;
    int m = 0;
    int n = 0;
    int i1 = -1;
    int i2 = 0;
    int i3 = 0;
    if (paramArrayOfInt != null)
    {
      i4 = -paramInt2 + j;
      View localView = null;
      if ((paramView != null) && ((localView = paramView.getParent()) != null)) {
        i4 += localView.getStartOffset();
      }
      n = paramArrayOfInt[0];
      i1 = paramArrayOfInt[1] + i4;
      i2 = paramArrayOfInt[2] + i4;
      i3 = paramArrayOfInt[3] + i4;
    }
    for (int i4 = j; i4 < k; i4++) {
      if ((arrayOfChar[i4] == '\t') || (((n != 0) || (i4 <= i1)) && (arrayOfChar[i4] == ' ') && (i2 <= i4) && (i4 <= i3)))
      {
        i += paramFontMetrics.charsWidth(arrayOfChar, i4 - m, m);
        m = 0;
        if (arrayOfChar[i4] == '\t')
        {
          if (paramTabExpander != null) {
            i = (int)paramTabExpander.nextTabStop(i, paramInt2 + i4 - j);
          } else {
            i += paramFontMetrics.charWidth(' ');
          }
        }
        else if (arrayOfChar[i4] == ' ')
        {
          i += paramFontMetrics.charWidth(' ') + n;
          if (i4 <= i1) {
            i++;
          }
        }
      }
      else if (arrayOfChar[i4] == '\n')
      {
        i += paramFontMetrics.charsWidth(arrayOfChar, i4 - m, m);
        m = 0;
      }
      else
      {
        m++;
      }
    }
    i += paramFontMetrics.charsWidth(arrayOfChar, k - m, m);
    return i - paramInt1;
  }
  
  public static final int getTabbedTextOffset(Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3)
  {
    return getTabbedTextOffset(paramSegment, paramFontMetrics, paramInt1, paramInt2, paramTabExpander, paramInt3, true);
  }
  
  static final int getTabbedTextOffset(View paramView, Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3, int[] paramArrayOfInt)
  {
    return getTabbedTextOffset(paramView, paramSegment, paramFontMetrics, paramInt1, paramInt2, paramTabExpander, paramInt3, true, paramArrayOfInt);
  }
  
  public static final int getTabbedTextOffset(Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3, boolean paramBoolean)
  {
    return getTabbedTextOffset(null, paramSegment, paramFontMetrics, paramInt1, paramInt2, paramTabExpander, paramInt3, paramBoolean, null);
  }
  
  static final int getTabbedTextOffset(View paramView, Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3, boolean paramBoolean, int[] paramArrayOfInt)
  {
    if (paramInt1 >= paramInt2) {
      return 0;
    }
    int i = paramInt1;
    char[] arrayOfChar = array;
    int j = offset;
    int k = count;
    int m = 0;
    int n = -1;
    int i1 = 0;
    int i2 = 0;
    if (paramArrayOfInt != null)
    {
      i3 = -paramInt3 + j;
      View localView = null;
      if ((paramView != null) && ((localView = paramView.getParent()) != null)) {
        i3 += localView.getStartOffset();
      }
      m = paramArrayOfInt[0];
      n = paramArrayOfInt[1] + i3;
      i1 = paramArrayOfInt[2] + i3;
      i2 = paramArrayOfInt[3] + i3;
    }
    int i3 = offset + count;
    for (int i4 = offset; i4 < i3; i4++)
    {
      if ((arrayOfChar[i4] == '\t') || (((m != 0) || (i4 <= n)) && (arrayOfChar[i4] == ' ') && (i1 <= i4) && (i4 <= i2)))
      {
        if (arrayOfChar[i4] == '\t')
        {
          if (paramTabExpander != null) {
            i = (int)paramTabExpander.nextTabStop(i, paramInt3 + i4 - j);
          } else {
            i += paramFontMetrics.charWidth(' ');
          }
        }
        else if (arrayOfChar[i4] == ' ')
        {
          i += paramFontMetrics.charWidth(' ') + m;
          if (i4 <= n) {
            i++;
          }
        }
      }
      else {
        i += paramFontMetrics.charWidth(arrayOfChar[i4]);
      }
      if (paramInt2 < i)
      {
        int i5;
        if (paramBoolean)
        {
          i5 = i4 + 1 - j;
          int i6 = paramFontMetrics.charsWidth(arrayOfChar, j, i5);
          int i7 = paramInt2 - paramInt1;
          if (i7 < i6) {
            while (i5 > 0)
            {
              int i8 = i5 > 1 ? paramFontMetrics.charsWidth(arrayOfChar, j, i5 - 1) : 0;
              if (i7 >= i8)
              {
                if (i7 - i8 >= i6 - i7) {
                  break;
                }
                i5--;
                break;
              }
              i6 = i8;
              i5--;
            }
          }
        }
        else
        {
          for (i5 = i4 - j; (i5 > 0) && (paramFontMetrics.charsWidth(arrayOfChar, j, i5) > paramInt2 - paramInt1); i5--) {}
        }
        return i5;
      }
    }
    return k;
  }
  
  public static final int getBreakLocation(Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3)
  {
    char[] arrayOfChar = array;
    int i = offset;
    int j = count;
    int k = getTabbedTextOffset(paramSegment, paramFontMetrics, paramInt1, paramInt2, paramTabExpander, paramInt3, false);
    if (k >= j - 1) {
      return j;
    }
    for (int m = i + k; m >= i; m--)
    {
      char c = arrayOfChar[m];
      if (c < 'Ā')
      {
        if (Character.isWhitespace(c))
        {
          k = m - i + 1;
          break;
        }
      }
      else
      {
        BreakIterator localBreakIterator = BreakIterator.getLineInstance();
        localBreakIterator.setText(paramSegment);
        int n = localBreakIterator.preceding(m + 1);
        if (n <= i) {
          break;
        }
        k = n - i;
        break;
      }
    }
    return k;
  }
  
  public static final int getRowStart(JTextComponent paramJTextComponent, int paramInt)
    throws BadLocationException
  {
    Rectangle localRectangle = paramJTextComponent.modelToView(paramInt);
    if (localRectangle == null) {
      return -1;
    }
    int i = paramInt;
    int j = y;
    while ((localRectangle != null) && (j == y))
    {
      if (height != 0) {
        paramInt = i;
      }
      i--;
      localRectangle = i >= 0 ? paramJTextComponent.modelToView(i) : null;
    }
    return paramInt;
  }
  
  public static final int getRowEnd(JTextComponent paramJTextComponent, int paramInt)
    throws BadLocationException
  {
    Rectangle localRectangle = paramJTextComponent.modelToView(paramInt);
    if (localRectangle == null) {
      return -1;
    }
    int i = paramJTextComponent.getDocument().getLength();
    int j = paramInt;
    int k = y;
    while ((localRectangle != null) && (k == y))
    {
      if (height != 0) {
        paramInt = j;
      }
      j++;
      localRectangle = j <= i ? paramJTextComponent.modelToView(j) : null;
    }
    return paramInt;
  }
  
  public static final int getPositionAbove(JTextComponent paramJTextComponent, int paramInt1, int paramInt2)
    throws BadLocationException
  {
    int i = getRowStart(paramJTextComponent, paramInt1) - 1;
    if (i < 0) {
      return -1;
    }
    int j = Integer.MAX_VALUE;
    int k = 0;
    Rectangle localRectangle = null;
    if (i >= 0)
    {
      localRectangle = paramJTextComponent.modelToView(i);
      k = y;
    }
    while ((localRectangle != null) && (k == y))
    {
      int m = Math.abs(x - paramInt2);
      if (m < j)
      {
        paramInt1 = i;
        j = m;
      }
      i--;
      localRectangle = i >= 0 ? paramJTextComponent.modelToView(i) : null;
    }
    return paramInt1;
  }
  
  public static final int getPositionBelow(JTextComponent paramJTextComponent, int paramInt1, int paramInt2)
    throws BadLocationException
  {
    int i = getRowEnd(paramJTextComponent, paramInt1) + 1;
    if (i <= 0) {
      return -1;
    }
    int j = Integer.MAX_VALUE;
    int k = paramJTextComponent.getDocument().getLength();
    int m = 0;
    Rectangle localRectangle = null;
    if (i <= k)
    {
      localRectangle = paramJTextComponent.modelToView(i);
      m = y;
    }
    while ((localRectangle != null) && (m == y))
    {
      int n = Math.abs(paramInt2 - x);
      if (n < j)
      {
        paramInt1 = i;
        j = n;
      }
      i++;
      localRectangle = i <= k ? paramJTextComponent.modelToView(i) : null;
    }
    return paramInt1;
  }
  
  public static final int getWordStart(JTextComponent paramJTextComponent, int paramInt)
    throws BadLocationException
  {
    Document localDocument = paramJTextComponent.getDocument();
    Element localElement = getParagraphElement(paramJTextComponent, paramInt);
    if (localElement == null) {
      throw new BadLocationException("No word at " + paramInt, paramInt);
    }
    int i = localElement.getStartOffset();
    int j = Math.min(localElement.getEndOffset(), localDocument.getLength());
    Segment localSegment = SegmentCache.getSharedSegment();
    localDocument.getText(i, j - i, localSegment);
    if (count > 0)
    {
      BreakIterator localBreakIterator = BreakIterator.getWordInstance(paramJTextComponent.getLocale());
      localBreakIterator.setText(localSegment);
      int k = offset + paramInt - i;
      if (k >= localBreakIterator.last()) {
        k = localBreakIterator.last() - 1;
      }
      localBreakIterator.following(k);
      paramInt = i + localBreakIterator.previous() - offset;
    }
    SegmentCache.releaseSharedSegment(localSegment);
    return paramInt;
  }
  
  public static final int getWordEnd(JTextComponent paramJTextComponent, int paramInt)
    throws BadLocationException
  {
    Document localDocument = paramJTextComponent.getDocument();
    Element localElement = getParagraphElement(paramJTextComponent, paramInt);
    if (localElement == null) {
      throw new BadLocationException("No word at " + paramInt, paramInt);
    }
    int i = localElement.getStartOffset();
    int j = Math.min(localElement.getEndOffset(), localDocument.getLength());
    Segment localSegment = SegmentCache.getSharedSegment();
    localDocument.getText(i, j - i, localSegment);
    if (count > 0)
    {
      BreakIterator localBreakIterator = BreakIterator.getWordInstance(paramJTextComponent.getLocale());
      localBreakIterator.setText(localSegment);
      int k = paramInt - i + offset;
      if (k >= localBreakIterator.last()) {
        k = localBreakIterator.last() - 1;
      }
      paramInt = i + localBreakIterator.following(k) - offset;
    }
    SegmentCache.releaseSharedSegment(localSegment);
    return paramInt;
  }
  
  public static final int getNextWord(JTextComponent paramJTextComponent, int paramInt)
    throws BadLocationException
  {
    Element localElement = getParagraphElement(paramJTextComponent, paramInt);
    for (int i = getNextWordInParagraph(paramJTextComponent, localElement, paramInt, false); i == -1; i = getNextWordInParagraph(paramJTextComponent, localElement, paramInt, true))
    {
      paramInt = localElement.getEndOffset();
      localElement = getParagraphElement(paramJTextComponent, paramInt);
    }
    return i;
  }
  
  static int getNextWordInParagraph(JTextComponent paramJTextComponent, Element paramElement, int paramInt, boolean paramBoolean)
    throws BadLocationException
  {
    if (paramElement == null) {
      throw new BadLocationException("No more words", paramInt);
    }
    Document localDocument = paramElement.getDocument();
    int i = paramElement.getStartOffset();
    int j = Math.min(paramElement.getEndOffset(), localDocument.getLength());
    if ((paramInt >= j) || (paramInt < i)) {
      throw new BadLocationException("No more words", paramInt);
    }
    Segment localSegment = SegmentCache.getSharedSegment();
    localDocument.getText(i, j - i, localSegment);
    BreakIterator localBreakIterator = BreakIterator.getWordInstance(paramJTextComponent.getLocale());
    localBreakIterator.setText(localSegment);
    if ((paramBoolean) && (localBreakIterator.first() == offset + paramInt - i) && (!Character.isWhitespace(array[localBreakIterator.first()]))) {
      return paramInt;
    }
    int k = localBreakIterator.following(offset + paramInt - i);
    if ((k == -1) || (k >= offset + count)) {
      return -1;
    }
    char c = array[k];
    if (!Character.isWhitespace(c)) {
      return i + k - offset;
    }
    k = localBreakIterator.next();
    if (k != -1)
    {
      paramInt = i + k - offset;
      if (paramInt != j) {
        return paramInt;
      }
    }
    SegmentCache.releaseSharedSegment(localSegment);
    return -1;
  }
  
  public static final int getPreviousWord(JTextComponent paramJTextComponent, int paramInt)
    throws BadLocationException
  {
    Element localElement = getParagraphElement(paramJTextComponent, paramInt);
    for (int i = getPrevWordInParagraph(paramJTextComponent, localElement, paramInt); i == -1; i = getPrevWordInParagraph(paramJTextComponent, localElement, paramInt))
    {
      paramInt = localElement.getStartOffset() - 1;
      localElement = getParagraphElement(paramJTextComponent, paramInt);
    }
    return i;
  }
  
  static int getPrevWordInParagraph(JTextComponent paramJTextComponent, Element paramElement, int paramInt)
    throws BadLocationException
  {
    if (paramElement == null) {
      throw new BadLocationException("No more words", paramInt);
    }
    Document localDocument = paramElement.getDocument();
    int i = paramElement.getStartOffset();
    int j = paramElement.getEndOffset();
    if ((paramInt > j) || (paramInt < i)) {
      throw new BadLocationException("No more words", paramInt);
    }
    Segment localSegment = SegmentCache.getSharedSegment();
    localDocument.getText(i, j - i, localSegment);
    BreakIterator localBreakIterator = BreakIterator.getWordInstance(paramJTextComponent.getLocale());
    localBreakIterator.setText(localSegment);
    if (localBreakIterator.following(offset + paramInt - i) == -1) {
      localBreakIterator.last();
    }
    int k = localBreakIterator.previous();
    if (k == offset + paramInt - i) {
      k = localBreakIterator.previous();
    }
    if (k == -1) {
      return -1;
    }
    char c = array[k];
    if (!Character.isWhitespace(c)) {
      return i + k - offset;
    }
    k = localBreakIterator.previous();
    if (k != -1) {
      return i + k - offset;
    }
    SegmentCache.releaseSharedSegment(localSegment);
    return -1;
  }
  
  public static final Element getParagraphElement(JTextComponent paramJTextComponent, int paramInt)
  {
    Document localDocument = paramJTextComponent.getDocument();
    if ((localDocument instanceof StyledDocument)) {
      return ((StyledDocument)localDocument).getParagraphElement(paramInt);
    }
    Element localElement1 = localDocument.getDefaultRootElement();
    int i = localElement1.getElementIndex(paramInt);
    Element localElement2 = localElement1.getElement(i);
    if ((paramInt >= localElement2.getStartOffset()) && (paramInt < localElement2.getEndOffset())) {
      return localElement2;
    }
    return null;
  }
  
  static boolean isComposedTextElement(Document paramDocument, int paramInt)
  {
    for (Element localElement = paramDocument.getDefaultRootElement(); !localElement.isLeaf(); localElement = localElement.getElement(localElement.getElementIndex(paramInt))) {}
    return isComposedTextElement(localElement);
  }
  
  static boolean isComposedTextElement(Element paramElement)
  {
    AttributeSet localAttributeSet = paramElement.getAttributes();
    return isComposedTextAttributeDefined(localAttributeSet);
  }
  
  static boolean isComposedTextAttributeDefined(AttributeSet paramAttributeSet)
  {
    return (paramAttributeSet != null) && (paramAttributeSet.isDefined(StyleConstants.ComposedTextAttribute));
  }
  
  static int drawComposedText(View paramView, AttributeSet paramAttributeSet, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws BadLocationException
  {
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    AttributedString localAttributedString = (AttributedString)paramAttributeSet.getAttribute(StyleConstants.ComposedTextAttribute);
    localAttributedString.addAttribute(TextAttribute.FONT, paramGraphics.getFont());
    if (paramInt3 >= paramInt4) {
      return paramInt1;
    }
    AttributedCharacterIterator localAttributedCharacterIterator = localAttributedString.getIterator(null, paramInt3, paramInt4);
    return paramInt1 + (int)SwingUtilities2.drawString(getJComponent(paramView), localGraphics2D, localAttributedCharacterIterator, paramInt1, paramInt2);
  }
  
  static void paintComposedText(Graphics paramGraphics, Rectangle paramRectangle, GlyphView paramGlyphView)
  {
    if ((paramGraphics instanceof Graphics2D))
    {
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      int i = paramGlyphView.getStartOffset();
      int j = paramGlyphView.getEndOffset();
      AttributeSet localAttributeSet = paramGlyphView.getElement().getAttributes();
      AttributedString localAttributedString = (AttributedString)localAttributeSet.getAttribute(StyleConstants.ComposedTextAttribute);
      int k = paramGlyphView.getElement().getStartOffset();
      int m = y + height - (int)paramGlyphView.getGlyphPainter().getDescent(paramGlyphView);
      int n = x;
      localAttributedString.addAttribute(TextAttribute.FONT, paramGlyphView.getFont());
      localAttributedString.addAttribute(TextAttribute.FOREGROUND, paramGlyphView.getForeground());
      if (StyleConstants.isBold(paramGlyphView.getAttributes())) {
        localAttributedString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
      }
      if (StyleConstants.isItalic(paramGlyphView.getAttributes())) {
        localAttributedString.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
      }
      if (paramGlyphView.isUnderline()) {
        localAttributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
      }
      if (paramGlyphView.isStrikeThrough()) {
        localAttributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
      }
      if (paramGlyphView.isSuperscript()) {
        localAttributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
      }
      if (paramGlyphView.isSubscript()) {
        localAttributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
      }
      AttributedCharacterIterator localAttributedCharacterIterator = localAttributedString.getIterator(null, i - k, j - k);
      SwingUtilities2.drawString(getJComponent(paramGlyphView), localGraphics2D, localAttributedCharacterIterator, n, m);
    }
  }
  
  static boolean isLeftToRight(Component paramComponent)
  {
    return paramComponent.getComponentOrientation().isLeftToRight();
  }
  
  static int getNextVisualPositionFrom(View paramView, int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    if (paramView.getViewCount() == 0) {
      return paramInt1;
    }
    int i = (paramInt2 == 1) || (paramInt2 == 7) ? 1 : 0;
    int k;
    Object localObject;
    int j;
    if (paramInt1 == -1)
    {
      k = i != 0 ? paramView.getViewCount() - 1 : 0;
      View localView = paramView.getView(k);
      localObject = paramView.getChildAllocation(k, paramShape);
      j = localView.getNextVisualPositionFrom(paramInt1, paramBias, (Shape)localObject, paramInt2, paramArrayOfBias);
      if ((j == -1) && (i == 0) && (paramView.getViewCount() > 1))
      {
        localView = paramView.getView(1);
        localObject = paramView.getChildAllocation(1, paramShape);
        j = localView.getNextVisualPositionFrom(-1, paramArrayOfBias[0], (Shape)localObject, paramInt2, paramArrayOfBias);
      }
    }
    else
    {
      k = i != 0 ? -1 : 1;
      int m;
      if ((paramBias == Position.Bias.Backward) && (paramInt1 > 0)) {
        m = paramView.getViewIndex(paramInt1 - 1, Position.Bias.Forward);
      } else {
        m = paramView.getViewIndex(paramInt1, Position.Bias.Forward);
      }
      localObject = paramView.getView(m);
      Shape localShape = paramView.getChildAllocation(m, paramShape);
      j = ((View)localObject).getNextVisualPositionFrom(paramInt1, paramBias, localShape, paramInt2, paramArrayOfBias);
      if (((paramInt2 == 3) || (paramInt2 == 7)) && ((paramView instanceof CompositeView)) && (((CompositeView)paramView).flipEastAndWestAtEnds(paramInt1, paramBias))) {
        k *= -1;
      }
      m += k;
      if ((j == -1) && (m >= 0) && (m < paramView.getViewCount()))
      {
        localObject = paramView.getView(m);
        localShape = paramView.getChildAllocation(m, paramShape);
        j = ((View)localObject).getNextVisualPositionFrom(-1, paramBias, localShape, paramInt2, paramArrayOfBias);
        if ((j == paramInt1) && (paramArrayOfBias[0] != paramBias)) {
          return getNextVisualPositionFrom(paramView, paramInt1, paramArrayOfBias[0], paramShape, paramInt2, paramArrayOfBias);
        }
      }
      else if ((j != -1) && (paramArrayOfBias[0] != paramBias) && (((k == 1) && (((View)localObject).getEndOffset() == j)) || ((k == -1) && (((View)localObject).getStartOffset() == j) && (m >= 0) && (m < paramView.getViewCount()))))
      {
        localObject = paramView.getView(m);
        localShape = paramView.getChildAllocation(m, paramShape);
        Position.Bias localBias = paramArrayOfBias[0];
        int n = ((View)localObject).getNextVisualPositionFrom(-1, paramBias, localShape, paramInt2, paramArrayOfBias);
        if (paramArrayOfBias[0] == paramBias) {
          j = n;
        } else {
          paramArrayOfBias[0] = localBias;
        }
      }
    }
    return j;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\Utilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */