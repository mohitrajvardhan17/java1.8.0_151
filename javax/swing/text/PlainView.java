package javax.swing.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.event.DocumentEvent.EventType;

public class PlainView
  extends View
  implements TabExpander
{
  protected FontMetrics metrics;
  Element longLine;
  Font font;
  Segment lineBuffer;
  int tabSize;
  int tabBase;
  int sel0;
  int sel1;
  Color unselected;
  Color selected;
  int firstLineOffset;
  
  public PlainView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected int getTabSize()
  {
    Integer localInteger = (Integer)getDocument().getProperty("tabSize");
    int i = localInteger != null ? localInteger.intValue() : 8;
    return i;
  }
  
  protected void drawLine(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3)
  {
    Element localElement1 = getElement().getElement(paramInt1);
    try
    {
      if (localElement1.isLeaf())
      {
        drawElement(paramInt1, localElement1, paramGraphics, paramInt2, paramInt3);
      }
      else
      {
        int i = localElement1.getElementCount();
        for (int j = 0; j < i; j++)
        {
          Element localElement2 = localElement1.getElement(j);
          paramInt2 = drawElement(paramInt1, localElement2, paramGraphics, paramInt2, paramInt3);
        }
      }
    }
    catch (BadLocationException localBadLocationException)
    {
      throw new StateInvariantError("Can't render line: " + paramInt1);
    }
  }
  
  private int drawElement(int paramInt1, Element paramElement, Graphics paramGraphics, int paramInt2, int paramInt3)
    throws BadLocationException
  {
    int i = paramElement.getStartOffset();
    int j = paramElement.getEndOffset();
    j = Math.min(getDocument().getLength(), j);
    if (paramInt1 == 0) {
      paramInt2 += firstLineOffset;
    }
    AttributeSet localAttributeSet = paramElement.getAttributes();
    if (Utilities.isComposedTextAttributeDefined(localAttributeSet))
    {
      paramGraphics.setColor(unselected);
      paramInt2 = Utilities.drawComposedText(this, localAttributeSet, paramGraphics, paramInt2, paramInt3, i - paramElement.getStartOffset(), j - paramElement.getStartOffset());
    }
    else if ((sel0 == sel1) || (selected == unselected))
    {
      paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, i, j);
    }
    else if ((i >= sel0) && (i <= sel1) && (j >= sel0) && (j <= sel1))
    {
      paramInt2 = drawSelectedText(paramGraphics, paramInt2, paramInt3, i, j);
    }
    else if ((sel0 >= i) && (sel0 <= j))
    {
      if ((sel1 >= i) && (sel1 <= j))
      {
        paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, i, sel0);
        paramInt2 = drawSelectedText(paramGraphics, paramInt2, paramInt3, sel0, sel1);
        paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, sel1, j);
      }
      else
      {
        paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, i, sel0);
        paramInt2 = drawSelectedText(paramGraphics, paramInt2, paramInt3, sel0, j);
      }
    }
    else if ((sel1 >= i) && (sel1 <= j))
    {
      paramInt2 = drawSelectedText(paramGraphics, paramInt2, paramInt3, i, sel1);
      paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, sel1, j);
    }
    else
    {
      paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, i, j);
    }
    return paramInt2;
  }
  
  protected int drawUnselectedText(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws BadLocationException
  {
    paramGraphics.setColor(unselected);
    Document localDocument = getDocument();
    Segment localSegment = SegmentCache.getSharedSegment();
    localDocument.getText(paramInt3, paramInt4 - paramInt3, localSegment);
    int i = Utilities.drawTabbedText(this, localSegment, paramInt1, paramInt2, paramGraphics, this, paramInt3);
    SegmentCache.releaseSharedSegment(localSegment);
    return i;
  }
  
  protected int drawSelectedText(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws BadLocationException
  {
    paramGraphics.setColor(selected);
    Document localDocument = getDocument();
    Segment localSegment = SegmentCache.getSharedSegment();
    localDocument.getText(paramInt3, paramInt4 - paramInt3, localSegment);
    int i = Utilities.drawTabbedText(this, localSegment, paramInt1, paramInt2, paramGraphics, this, paramInt3);
    SegmentCache.releaseSharedSegment(localSegment);
    return i;
  }
  
  protected final Segment getLineBuffer()
  {
    if (lineBuffer == null) {
      lineBuffer = new Segment();
    }
    return lineBuffer;
  }
  
  protected void updateMetrics()
  {
    Container localContainer = getContainer();
    Font localFont = localContainer.getFont();
    if (font != localFont)
    {
      calculateLongestLine();
      tabSize = (getTabSize() * metrics.charWidth('m'));
    }
  }
  
  public float getPreferredSpan(int paramInt)
  {
    updateMetrics();
    switch (paramInt)
    {
    case 0: 
      return getLineWidth(longLine);
    case 1: 
      return getElement().getElementCount() * metrics.getHeight();
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Shape localShape = paramShape;
    paramShape = adjustPaintRegion(paramShape);
    Rectangle localRectangle1 = (Rectangle)paramShape;
    tabBase = x;
    JTextComponent localJTextComponent = (JTextComponent)getContainer();
    Highlighter localHighlighter = localJTextComponent.getHighlighter();
    paramGraphics.setFont(localJTextComponent.getFont());
    sel0 = localJTextComponent.getSelectionStart();
    sel1 = localJTextComponent.getSelectionEnd();
    unselected = (localJTextComponent.isEnabled() ? localJTextComponent.getForeground() : localJTextComponent.getDisabledTextColor());
    Caret localCaret = localJTextComponent.getCaret();
    selected = ((localCaret.isSelectionVisible()) && (localHighlighter != null) ? localJTextComponent.getSelectedTextColor() : unselected);
    updateMetrics();
    Rectangle localRectangle2 = paramGraphics.getClipBounds();
    int i = metrics.getHeight();
    int j = y + height - (y + height);
    int k = y - y;
    int m;
    int n;
    int i1;
    if (i > 0)
    {
      m = Math.max(0, j / i);
      n = Math.max(0, k / i);
      i1 = height / i;
      if (height % i != 0) {
        i1++;
      }
    }
    else
    {
      m = n = i1 = 0;
    }
    Rectangle localRectangle3 = lineToRect(paramShape, n);
    int i2 = y + metrics.getAscent();
    int i3 = x;
    Element localElement1 = getElement();
    int i4 = localElement1.getElementCount();
    int i5 = Math.min(i4, i1 - m);
    i4--;
    Object localObject = (localHighlighter instanceof LayeredHighlighter) ? (LayeredHighlighter)localHighlighter : null;
    for (int i6 = n; i6 < i5; i6++)
    {
      if (localObject != null)
      {
        Element localElement2 = localElement1.getElement(i6);
        if (i6 == i4) {
          ((LayeredHighlighter)localObject).paintLayeredHighlights(paramGraphics, localElement2.getStartOffset(), localElement2.getEndOffset(), localShape, localJTextComponent, this);
        } else {
          ((LayeredHighlighter)localObject).paintLayeredHighlights(paramGraphics, localElement2.getStartOffset(), localElement2.getEndOffset() - 1, localShape, localJTextComponent, this);
        }
      }
      drawLine(i6, paramGraphics, i3, i2);
      i2 += i;
      if (i6 == 0) {
        i3 -= firstLineOffset;
      }
    }
  }
  
  Shape adjustPaintRegion(Shape paramShape)
  {
    return paramShape;
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    Document localDocument = getDocument();
    Element localElement1 = getElement();
    int i = localElement1.getElementIndex(paramInt);
    if (i < 0) {
      return lineToRect(paramShape, 0);
    }
    Rectangle localRectangle = lineToRect(paramShape, i);
    tabBase = x;
    Element localElement2 = localElement1.getElement(i);
    int j = localElement2.getStartOffset();
    Segment localSegment = SegmentCache.getSharedSegment();
    localDocument.getText(j, paramInt - j, localSegment);
    int k = Utilities.getTabbedTextWidth(localSegment, metrics, tabBase, this, j);
    SegmentCache.releaseSharedSegment(localSegment);
    x += k;
    width = 1;
    height = metrics.getHeight();
    return localRectangle;
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    paramArrayOfBias[0] = Position.Bias.Forward;
    Rectangle localRectangle = paramShape.getBounds();
    Document localDocument = getDocument();
    int i = (int)paramFloat1;
    int j = (int)paramFloat2;
    if (j < y) {
      return getStartOffset();
    }
    if (j > y + height) {
      return getEndOffset() - 1;
    }
    Element localElement1 = localDocument.getDefaultRootElement();
    int k = metrics.getHeight();
    int m = k > 0 ? Math.abs((j - y) / k) : localElement1.getElementCount() - 1;
    if (m >= localElement1.getElementCount()) {
      return getEndOffset() - 1;
    }
    Element localElement2 = localElement1.getElement(m);
    int n = 0;
    if (m == 0)
    {
      x += firstLineOffset;
      width -= firstLineOffset;
    }
    if (i < x) {
      return localElement2.getStartOffset();
    }
    if (i > x + width) {
      return localElement2.getEndOffset() - 1;
    }
    try
    {
      int i1 = localElement2.getStartOffset();
      int i2 = localElement2.getEndOffset() - 1;
      Segment localSegment = SegmentCache.getSharedSegment();
      localDocument.getText(i1, i2 - i1, localSegment);
      tabBase = x;
      int i3 = i1 + Utilities.getTabbedTextOffset(localSegment, metrics, tabBase, i, this, i1);
      SegmentCache.releaseSharedSegment(localSegment);
      return i3;
    }
    catch (BadLocationException localBadLocationException) {}
    return -1;
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    updateDamage(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    updateDamage(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    updateDamage(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  public void setSize(float paramFloat1, float paramFloat2)
  {
    super.setSize(paramFloat1, paramFloat2);
    updateMetrics();
  }
  
  public float nextTabStop(float paramFloat, int paramInt)
  {
    if (tabSize == 0) {
      return paramFloat;
    }
    int i = ((int)paramFloat - tabBase) / tabSize;
    return tabBase + (i + 1) * tabSize;
  }
  
  protected void updateDamage(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    Container localContainer = getContainer();
    updateMetrics();
    Element localElement1 = getElement();
    DocumentEvent.ElementChange localElementChange = paramDocumentEvent.getChange(localElement1);
    Object localObject1 = localElementChange != null ? localElementChange.getChildrenAdded() : null;
    Object localObject2 = localElementChange != null ? localElementChange.getChildrenRemoved() : null;
    int j;
    int k;
    if (((localObject1 != null) && (localObject1.length > 0)) || ((localObject2 != null) && (localObject2.length > 0)))
    {
      int i;
      if (localObject1 != null)
      {
        i = getLineWidth(longLine);
        for (j = 0; j < localObject1.length; j++)
        {
          k = getLineWidth(localObject1[j]);
          if (k > i)
          {
            i = k;
            longLine = localObject1[j];
          }
        }
      }
      if (localObject2 != null) {
        for (i = 0; i < localObject2.length; i++) {
          if (localObject2[i] == longLine)
          {
            calculateLongestLine();
            break;
          }
        }
      }
      preferenceChanged(null, true, true);
      localContainer.repaint();
    }
    else
    {
      Element localElement2 = getElement();
      j = localElement2.getElementIndex(paramDocumentEvent.getOffset());
      damageLineRange(j, j, paramShape, localContainer);
      if (paramDocumentEvent.getType() == DocumentEvent.EventType.INSERT)
      {
        k = getLineWidth(longLine);
        Element localElement3 = localElement2.getElement(j);
        if (localElement3 == longLine)
        {
          preferenceChanged(null, true, false);
        }
        else if (getLineWidth(localElement3) > k)
        {
          longLine = localElement3;
          preferenceChanged(null, true, false);
        }
      }
      else if ((paramDocumentEvent.getType() == DocumentEvent.EventType.REMOVE) && (localElement2.getElement(j) == longLine))
      {
        calculateLongestLine();
        preferenceChanged(null, true, false);
      }
    }
  }
  
  protected void damageLineRange(int paramInt1, int paramInt2, Shape paramShape, Component paramComponent)
  {
    if (paramShape != null)
    {
      Rectangle localRectangle1 = lineToRect(paramShape, paramInt1);
      Rectangle localRectangle2 = lineToRect(paramShape, paramInt2);
      if ((localRectangle1 != null) && (localRectangle2 != null))
      {
        Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
        paramComponent.repaint(x, y, width, height);
      }
      else
      {
        paramComponent.repaint();
      }
    }
  }
  
  protected Rectangle lineToRect(Shape paramShape, int paramInt)
  {
    Rectangle localRectangle1 = null;
    updateMetrics();
    if (metrics != null)
    {
      Rectangle localRectangle2 = paramShape.getBounds();
      if (paramInt == 0)
      {
        x += firstLineOffset;
        width -= firstLineOffset;
      }
      localRectangle1 = new Rectangle(x, y + paramInt * metrics.getHeight(), width, metrics.getHeight());
    }
    return localRectangle1;
  }
  
  private void calculateLongestLine()
  {
    Container localContainer = getContainer();
    font = localContainer.getFont();
    metrics = localContainer.getFontMetrics(font);
    Document localDocument = getDocument();
    Element localElement1 = getElement();
    int i = localElement1.getElementCount();
    int j = -1;
    for (int k = 0; k < i; k++)
    {
      Element localElement2 = localElement1.getElement(k);
      int m = getLineWidth(localElement2);
      if (m > j)
      {
        j = m;
        longLine = localElement2;
      }
    }
  }
  
  private int getLineWidth(Element paramElement)
  {
    if (paramElement == null) {
      return 0;
    }
    int i = paramElement.getStartOffset();
    int j = paramElement.getEndOffset();
    Segment localSegment = SegmentCache.getSharedSegment();
    int k;
    try
    {
      paramElement.getDocument().getText(i, j - i, localSegment);
      k = Utilities.getTabbedTextWidth(localSegment, metrics, tabBase, this, i);
    }
    catch (BadLocationException localBadLocationException)
    {
      k = 0;
    }
    SegmentCache.releaseSharedSegment(localSegment);
    return k;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\PlainView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */