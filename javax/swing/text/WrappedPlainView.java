package javax.swing.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.lang.ref.SoftReference;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;

public class WrappedPlainView
  extends BoxView
  implements TabExpander
{
  FontMetrics metrics;
  Segment lineBuffer;
  boolean widthChanging;
  int tabBase;
  int tabSize;
  boolean wordWrap;
  int sel0;
  int sel1;
  Color unselected;
  Color selected;
  
  public WrappedPlainView(Element paramElement)
  {
    this(paramElement, false);
  }
  
  public WrappedPlainView(Element paramElement, boolean paramBoolean)
  {
    super(paramElement, 1);
    wordWrap = paramBoolean;
  }
  
  protected int getTabSize()
  {
    Integer localInteger = (Integer)getDocument().getProperty("tabSize");
    int i = localInteger != null ? localInteger.intValue() : 8;
    return i;
  }
  
  protected void drawLine(int paramInt1, int paramInt2, Graphics paramGraphics, int paramInt3, int paramInt4)
  {
    Element localElement1 = getElement();
    Element localElement2 = localElement1.getElement(localElement1.getElementIndex(paramInt1));
    try
    {
      if (localElement2.isLeaf())
      {
        drawText(localElement2, paramInt1, paramInt2, paramGraphics, paramInt3, paramInt4);
      }
      else
      {
        int i = localElement2.getElementIndex(paramInt1);
        int j = localElement2.getElementIndex(paramInt2);
        while (i <= j)
        {
          Element localElement3 = localElement2.getElement(i);
          int k = Math.max(localElement3.getStartOffset(), paramInt1);
          int m = Math.min(localElement3.getEndOffset(), paramInt2);
          paramInt3 = drawText(localElement3, k, m, paramGraphics, paramInt3, paramInt4);
          i++;
        }
      }
    }
    catch (BadLocationException localBadLocationException)
    {
      throw new StateInvariantError("Can't render: " + paramInt1 + "," + paramInt2);
    }
  }
  
  private int drawText(Element paramElement, int paramInt1, int paramInt2, Graphics paramGraphics, int paramInt3, int paramInt4)
    throws BadLocationException
  {
    paramInt2 = Math.min(getDocument().getLength(), paramInt2);
    AttributeSet localAttributeSet = paramElement.getAttributes();
    if (Utilities.isComposedTextAttributeDefined(localAttributeSet))
    {
      paramGraphics.setColor(unselected);
      paramInt3 = Utilities.drawComposedText(this, localAttributeSet, paramGraphics, paramInt3, paramInt4, paramInt1 - paramElement.getStartOffset(), paramInt2 - paramElement.getStartOffset());
    }
    else if ((sel0 == sel1) || (selected == unselected))
    {
      paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, paramInt1, paramInt2);
    }
    else if ((paramInt1 >= sel0) && (paramInt1 <= sel1) && (paramInt2 >= sel0) && (paramInt2 <= sel1))
    {
      paramInt3 = drawSelectedText(paramGraphics, paramInt3, paramInt4, paramInt1, paramInt2);
    }
    else if ((sel0 >= paramInt1) && (sel0 <= paramInt2))
    {
      if ((sel1 >= paramInt1) && (sel1 <= paramInt2))
      {
        paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, paramInt1, sel0);
        paramInt3 = drawSelectedText(paramGraphics, paramInt3, paramInt4, sel0, sel1);
        paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, sel1, paramInt2);
      }
      else
      {
        paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, paramInt1, sel0);
        paramInt3 = drawSelectedText(paramGraphics, paramInt3, paramInt4, sel0, paramInt2);
      }
    }
    else if ((sel1 >= paramInt1) && (sel1 <= paramInt2))
    {
      paramInt3 = drawSelectedText(paramGraphics, paramInt3, paramInt4, paramInt1, sel1);
      paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, sel1, paramInt2);
    }
    else
    {
      paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, paramInt1, paramInt2);
    }
    return paramInt3;
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
  
  protected int calculateBreakPosition(int paramInt1, int paramInt2)
  {
    Segment localSegment = SegmentCache.getSharedSegment();
    loadText(localSegment, paramInt1, paramInt2);
    int j = getWidth();
    int i;
    if (wordWrap) {
      i = paramInt1 + Utilities.getBreakLocation(localSegment, metrics, tabBase, tabBase + j, this, paramInt1);
    } else {
      i = paramInt1 + Utilities.getTabbedTextOffset(localSegment, metrics, tabBase, tabBase + j, this, paramInt1, false);
    }
    SegmentCache.releaseSharedSegment(localSegment);
    return i;
  }
  
  protected void loadChildren(ViewFactory paramViewFactory)
  {
    Element localElement = getElement();
    int i = localElement.getElementCount();
    if (i > 0)
    {
      View[] arrayOfView = new View[i];
      for (int j = 0; j < i; j++) {
        arrayOfView[j] = new WrappedLine(localElement.getElement(j));
      }
      replace(0, 0, arrayOfView);
    }
  }
  
  void updateChildren(DocumentEvent paramDocumentEvent, Shape paramShape)
  {
    Element localElement = getElement();
    DocumentEvent.ElementChange localElementChange = paramDocumentEvent.getChange(localElement);
    if (localElementChange != null)
    {
      Element[] arrayOfElement1 = localElementChange.getChildrenRemoved();
      Element[] arrayOfElement2 = localElementChange.getChildrenAdded();
      View[] arrayOfView = new View[arrayOfElement2.length];
      for (int i = 0; i < arrayOfElement2.length; i++) {
        arrayOfView[i] = new WrappedLine(arrayOfElement2[i]);
      }
      replace(localElementChange.getIndex(), arrayOfElement1.length, arrayOfView);
      if (paramShape != null)
      {
        preferenceChanged(null, true, true);
        getContainer().repaint();
      }
    }
    updateMetrics();
  }
  
  final void loadText(Segment paramSegment, int paramInt1, int paramInt2)
  {
    try
    {
      Document localDocument = getDocument();
      localDocument.getText(paramInt1, paramInt2 - paramInt1, paramSegment);
    }
    catch (BadLocationException localBadLocationException)
    {
      throw new StateInvariantError("Can't get line text");
    }
  }
  
  final void updateMetrics()
  {
    Container localContainer = getContainer();
    Font localFont = localContainer.getFont();
    metrics = localContainer.getFontMetrics(localFont);
    tabSize = (getTabSize() * metrics.charWidth('m'));
  }
  
  public float nextTabStop(float paramFloat, int paramInt)
  {
    if (tabSize == 0) {
      return paramFloat;
    }
    int i = ((int)paramFloat - tabBase) / tabSize;
    return tabBase + (i + 1) * tabSize;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Rectangle localRectangle = (Rectangle)paramShape;
    tabBase = x;
    JTextComponent localJTextComponent = (JTextComponent)getContainer();
    sel0 = localJTextComponent.getSelectionStart();
    sel1 = localJTextComponent.getSelectionEnd();
    unselected = (localJTextComponent.isEnabled() ? localJTextComponent.getForeground() : localJTextComponent.getDisabledTextColor());
    Caret localCaret = localJTextComponent.getCaret();
    selected = ((localCaret.isSelectionVisible()) && (localJTextComponent.getHighlighter() != null) ? localJTextComponent.getSelectedTextColor() : unselected);
    paramGraphics.setFont(localJTextComponent.getFont());
    super.paint(paramGraphics, paramShape);
  }
  
  public void setSize(float paramFloat1, float paramFloat2)
  {
    updateMetrics();
    if ((int)paramFloat1 != getWidth())
    {
      preferenceChanged(null, true, true);
      widthChanging = true;
    }
    super.setSize(paramFloat1, paramFloat2);
    widthChanging = false;
  }
  
  public float getPreferredSpan(int paramInt)
  {
    updateMetrics();
    return super.getPreferredSpan(paramInt);
  }
  
  public float getMinimumSpan(int paramInt)
  {
    updateMetrics();
    return super.getMinimumSpan(paramInt);
  }
  
  public float getMaximumSpan(int paramInt)
  {
    updateMetrics();
    return super.getMaximumSpan(paramInt);
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    updateChildren(paramDocumentEvent, paramShape);
    Rectangle localRectangle = (paramShape != null) && (isAllocationValid()) ? getInsideAllocation(paramShape) : null;
    int i = paramDocumentEvent.getOffset();
    View localView = getViewAtPosition(i, localRectangle);
    if (localView != null) {
      localView.insertUpdate(paramDocumentEvent, localRectangle, paramViewFactory);
    }
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    updateChildren(paramDocumentEvent, paramShape);
    Rectangle localRectangle = (paramShape != null) && (isAllocationValid()) ? getInsideAllocation(paramShape) : null;
    int i = paramDocumentEvent.getOffset();
    View localView = getViewAtPosition(i, localRectangle);
    if (localView != null) {
      localView.removeUpdate(paramDocumentEvent, localRectangle, paramViewFactory);
    }
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    updateChildren(paramDocumentEvent, paramShape);
  }
  
  class WrappedLine
    extends View
  {
    int lineCount = -1;
    SoftReference<int[]> lineCache = null;
    
    WrappedLine(Element paramElement)
    {
      super();
    }
    
    public float getPreferredSpan(int paramInt)
    {
      switch (paramInt)
      {
      case 0: 
        float f = getWidth();
        if (f == 2.14748365E9F) {
          return 100.0F;
        }
        return f;
      case 1: 
        if ((lineCount < 0) || (widthChanging)) {
          breakLines(getStartOffset());
        }
        return lineCount * metrics.getHeight();
      }
      throw new IllegalArgumentException("Invalid axis: " + paramInt);
    }
    
    public void paint(Graphics paramGraphics, Shape paramShape)
    {
      Rectangle localRectangle = (Rectangle)paramShape;
      int i = y + metrics.getAscent();
      int j = x;
      JTextComponent localJTextComponent = (JTextComponent)getContainer();
      Highlighter localHighlighter = localJTextComponent.getHighlighter();
      Object localObject = (localHighlighter instanceof LayeredHighlighter) ? (LayeredHighlighter)localHighlighter : null;
      int k = getStartOffset();
      int m = getEndOffset();
      int n = k;
      int[] arrayOfInt = getLineEnds();
      for (int i1 = 0; i1 < lineCount; i1++)
      {
        int i2 = arrayOfInt == null ? m : k + arrayOfInt[i1];
        if (localObject != null)
        {
          int i3 = i2 == m ? i2 - 1 : i2;
          ((LayeredHighlighter)localObject).paintLayeredHighlights(paramGraphics, n, i3, paramShape, localJTextComponent, this);
        }
        drawLine(n, i2, paramGraphics, j, i);
        n = i2;
        i += metrics.getHeight();
      }
    }
    
    public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
      throws BadLocationException
    {
      Rectangle localRectangle = paramShape.getBounds();
      height = metrics.getHeight();
      width = 1;
      int i = getStartOffset();
      if ((paramInt < i) || (paramInt > getEndOffset())) {
        throw new BadLocationException("Position out of range", paramInt);
      }
      int j = paramBias == Position.Bias.Forward ? paramInt : Math.max(i, paramInt - 1);
      int k = 0;
      int[] arrayOfInt = getLineEnds();
      if (arrayOfInt != null)
      {
        k = findLine(j - i);
        if (k > 0) {
          i += arrayOfInt[(k - 1)];
        }
        y += height * k;
      }
      if (paramInt > i)
      {
        Segment localSegment = SegmentCache.getSharedSegment();
        loadText(localSegment, i, paramInt);
        x += Utilities.getTabbedTextWidth(localSegment, metrics, x, WrappedPlainView.this, i);
        SegmentCache.releaseSharedSegment(localSegment);
      }
      return localRectangle;
    }
    
    public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
    {
      paramArrayOfBias[0] = Position.Bias.Forward;
      Rectangle localRectangle = (Rectangle)paramShape;
      int i = (int)paramFloat1;
      int j = (int)paramFloat2;
      if (j < y) {
        return getStartOffset();
      }
      if (j > y + height) {
        return getEndOffset() - 1;
      }
      height = metrics.getHeight();
      int k = height > 0 ? (j - y) / height : lineCount - 1;
      if (k >= lineCount) {
        return getEndOffset() - 1;
      }
      int m = getStartOffset();
      int n;
      if (lineCount == 1)
      {
        n = getEndOffset();
      }
      else
      {
        localObject = getLineEnds();
        n = m + localObject[k];
        if (k > 0) {
          m += localObject[(k - 1)];
        }
      }
      if (i < x) {
        return m;
      }
      if (i > x + width) {
        return n - 1;
      }
      Object localObject = SegmentCache.getSharedSegment();
      loadText((Segment)localObject, m, n);
      int i1 = Utilities.getTabbedTextOffset((Segment)localObject, metrics, x, i, WrappedPlainView.this, m);
      SegmentCache.releaseSharedSegment((Segment)localObject);
      return Math.min(m + i1, n - 1);
    }
    
    public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      update(paramDocumentEvent, paramShape);
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      update(paramDocumentEvent, paramShape);
    }
    
    private void update(DocumentEvent paramDocumentEvent, Shape paramShape)
    {
      int i = lineCount;
      breakLines(paramDocumentEvent.getOffset());
      if (i != lineCount)
      {
        preferenceChanged(this, false, true);
        getContainer().repaint();
      }
      else if (paramShape != null)
      {
        Container localContainer = getContainer();
        Rectangle localRectangle = (Rectangle)paramShape;
        localContainer.repaint(x, y, width, height);
      }
    }
    
    final int[] getLineEnds()
    {
      if (lineCache == null) {
        return null;
      }
      int[] arrayOfInt = (int[])lineCache.get();
      if (arrayOfInt == null) {
        return breakLines(getStartOffset());
      }
      return arrayOfInt;
    }
    
    final int[] breakLines(int paramInt)
    {
      Object localObject1 = lineCache == null ? null : (int[])lineCache.get();
      Object localObject2 = localObject1;
      int i = getStartOffset();
      int j = 0;
      if (localObject1 != null)
      {
        j = findLine(paramInt - i);
        if (j > 0) {
          j--;
        }
      }
      int k = j == 0 ? i : i + localObject1[(j - 1)];
      int m = getEndOffset();
      int n;
      while (k < m)
      {
        n = calculateBreakPosition(k, m);
        n++;
        k = n == k ? n : n;
        if ((j == 0) && (k >= m))
        {
          lineCache = null;
          localObject1 = null;
          j = 1;
          break;
        }
        if ((localObject1 == null) || (j >= localObject1.length))
        {
          double d = (m - i) / (k - i);
          int i1 = (int)Math.ceil((j + 1) * d);
          i1 = Math.max(i1, j + 2);
          int[] arrayOfInt2 = new int[i1];
          if (localObject1 != null) {
            System.arraycopy(localObject1, 0, arrayOfInt2, 0, j);
          }
          localObject1 = arrayOfInt2;
        }
        localObject1[(j++)] = (k - i);
      }
      lineCount = j;
      if (lineCount > 1)
      {
        n = lineCount + lineCount / 3;
        if (localObject1.length > n)
        {
          int[] arrayOfInt1 = new int[n];
          System.arraycopy(localObject1, 0, arrayOfInt1, 0, lineCount);
          localObject1 = arrayOfInt1;
        }
      }
      if ((localObject1 != null) && (localObject1 != localObject2)) {
        lineCache = new SoftReference(localObject1);
      }
      return (int[])localObject1;
    }
    
    private int findLine(int paramInt)
    {
      int[] arrayOfInt = (int[])lineCache.get();
      if (paramInt < arrayOfInt[0]) {
        return 0;
      }
      if (paramInt > arrayOfInt[(lineCount - 1)]) {
        return lineCount;
      }
      return findLine(arrayOfInt, paramInt, 0, lineCount - 1);
    }
    
    private int findLine(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
    {
      if (paramInt3 - paramInt2 <= 1) {
        return paramInt3;
      }
      int i = (paramInt3 + paramInt2) / 2;
      return paramInt1 < paramArrayOfInt[i] ? findLine(paramArrayOfInt, paramInt1, paramInt2, i) : findLine(paramArrayOfInt, paramInt1, i, paramInt3);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\WrappedPlainView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */