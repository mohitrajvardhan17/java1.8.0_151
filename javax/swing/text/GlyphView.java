package javax.swing.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.text.BreakIterator;
import java.util.BitSet;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import sun.swing.SwingUtilities2;

public class GlyphView
  extends View
  implements TabableView, Cloneable
{
  private byte[] selections = null;
  int offset = 0;
  int length = 0;
  boolean impliedCR;
  boolean skipWidth;
  TabExpander expander;
  private float minimumSpan = -1.0F;
  private int[] breakSpots = null;
  int x;
  GlyphPainter painter;
  static GlyphPainter defaultPainter;
  private JustificationInfo justificationInfo = null;
  
  public GlyphView(Element paramElement)
  {
    super(paramElement);
    Element localElement = paramElement.getParentElement();
    AttributeSet localAttributeSet = paramElement.getAttributes();
    impliedCR = ((localAttributeSet != null) && (localAttributeSet.getAttribute("CR") != null) && (localElement != null) && (localElement.getElementCount() > 1));
    skipWidth = paramElement.getName().equals("br");
  }
  
  protected final Object clone()
  {
    Object localObject;
    try
    {
      localObject = super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localObject = null;
    }
    return localObject;
  }
  
  public GlyphPainter getGlyphPainter()
  {
    return painter;
  }
  
  public void setGlyphPainter(GlyphPainter paramGlyphPainter)
  {
    painter = paramGlyphPainter;
  }
  
  public Segment getText(int paramInt1, int paramInt2)
  {
    Segment localSegment = SegmentCache.getSharedSegment();
    try
    {
      Document localDocument = getDocument();
      localDocument.getText(paramInt1, paramInt2 - paramInt1, localSegment);
    }
    catch (BadLocationException localBadLocationException)
    {
      throw new StateInvariantError("GlyphView: Stale view: " + localBadLocationException);
    }
    return localSegment;
  }
  
  public Color getBackground()
  {
    Document localDocument = getDocument();
    if ((localDocument instanceof StyledDocument))
    {
      AttributeSet localAttributeSet = getAttributes();
      if (localAttributeSet.isDefined(StyleConstants.Background)) {
        return ((StyledDocument)localDocument).getBackground(localAttributeSet);
      }
    }
    return null;
  }
  
  public Color getForeground()
  {
    Document localDocument = getDocument();
    if ((localDocument instanceof StyledDocument))
    {
      localObject = getAttributes();
      return ((StyledDocument)localDocument).getForeground((AttributeSet)localObject);
    }
    Object localObject = getContainer();
    if (localObject != null) {
      return ((Component)localObject).getForeground();
    }
    return null;
  }
  
  public Font getFont()
  {
    Document localDocument = getDocument();
    if ((localDocument instanceof StyledDocument))
    {
      localObject = getAttributes();
      return ((StyledDocument)localDocument).getFont((AttributeSet)localObject);
    }
    Object localObject = getContainer();
    if (localObject != null) {
      return ((Component)localObject).getFont();
    }
    return null;
  }
  
  public boolean isUnderline()
  {
    AttributeSet localAttributeSet = getAttributes();
    return StyleConstants.isUnderline(localAttributeSet);
  }
  
  public boolean isStrikeThrough()
  {
    AttributeSet localAttributeSet = getAttributes();
    return StyleConstants.isStrikeThrough(localAttributeSet);
  }
  
  public boolean isSubscript()
  {
    AttributeSet localAttributeSet = getAttributes();
    return StyleConstants.isSubscript(localAttributeSet);
  }
  
  public boolean isSuperscript()
  {
    AttributeSet localAttributeSet = getAttributes();
    return StyleConstants.isSuperscript(localAttributeSet);
  }
  
  public TabExpander getTabExpander()
  {
    return expander;
  }
  
  protected void checkPainter()
  {
    if (painter == null)
    {
      if (defaultPainter == null)
      {
        String str = "javax.swing.text.GlyphPainter1";
        try
        {
          ClassLoader localClassLoader = getClass().getClassLoader();
          Class localClass;
          if (localClassLoader != null) {
            localClass = localClassLoader.loadClass(str);
          } else {
            localClass = Class.forName(str);
          }
          Object localObject = localClass.newInstance();
          if ((localObject instanceof GlyphPainter)) {
            defaultPainter = (GlyphPainter)localObject;
          }
        }
        catch (Throwable localThrowable)
        {
          throw new StateInvariantError("GlyphView: Can't load glyph painter: " + str);
        }
      }
      setGlyphPainter(defaultPainter.getPainter(this, getStartOffset(), getEndOffset()));
    }
  }
  
  public float getTabbedSpan(float paramFloat, TabExpander paramTabExpander)
  {
    checkPainter();
    TabExpander localTabExpander = expander;
    expander = paramTabExpander;
    if (expander != localTabExpander) {
      preferenceChanged(null, true, false);
    }
    x = ((int)paramFloat);
    int i = getStartOffset();
    int j = getEndOffset();
    float f = painter.getSpan(this, i, j, expander, paramFloat);
    return f;
  }
  
  public float getPartialSpan(int paramInt1, int paramInt2)
  {
    checkPainter();
    float f = painter.getSpan(this, paramInt1, paramInt2, expander, x);
    return f;
  }
  
  public int getStartOffset()
  {
    Element localElement = getElement();
    return length > 0 ? localElement.getStartOffset() + offset : localElement.getStartOffset();
  }
  
  public int getEndOffset()
  {
    Element localElement = getElement();
    return length > 0 ? localElement.getStartOffset() + offset + length : localElement.getEndOffset();
  }
  
  private void initSelections(int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1 + 1;
    if ((selections == null) || (i > selections.length))
    {
      selections = new byte[i];
      return;
    }
    int j = 0;
    while (j < i) {
      selections[(j++)] = 0;
    }
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    checkPainter();
    int i = 0;
    Container localContainer = getContainer();
    int j = getStartOffset();
    int k = getEndOffset();
    Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    Color localColor1 = getBackground();
    Color localColor2 = getForeground();
    if ((localContainer != null) && (!localContainer.isEnabled())) {
      localColor2 = (localContainer instanceof JTextComponent) ? ((JTextComponent)localContainer).getDisabledTextColor() : UIManager.getColor("textInactiveText");
    }
    if (localColor1 != null)
    {
      paramGraphics.setColor(localColor1);
      paramGraphics.fillRect(x, y, width, height);
    }
    JTextComponent localJTextComponent;
    Object localObject;
    if ((localContainer instanceof JTextComponent))
    {
      localJTextComponent = (JTextComponent)localContainer;
      localObject = localJTextComponent.getHighlighter();
      if ((localObject instanceof LayeredHighlighter)) {
        ((LayeredHighlighter)localObject).paintLayeredHighlights(paramGraphics, j, k, paramShape, localJTextComponent, this);
      }
    }
    if (Utilities.isComposedTextElement(getElement()))
    {
      Utilities.paintComposedText(paramGraphics, paramShape.getBounds(), this);
      i = 1;
    }
    else if ((localContainer instanceof JTextComponent))
    {
      localJTextComponent = (JTextComponent)localContainer;
      localObject = localJTextComponent.getSelectedTextColor();
      if ((localJTextComponent.getHighlighter() != null) && (localObject != null) && (!((Color)localObject).equals(localColor2)))
      {
        Highlighter.Highlight[] arrayOfHighlight = localJTextComponent.getHighlighter().getHighlights();
        if (arrayOfHighlight.length != 0)
        {
          int m = 0;
          int n = 0;
          int i3;
          int i4;
          for (int i1 = 0; i1 < arrayOfHighlight.length; i1++)
          {
            Highlighter.Highlight localHighlight = arrayOfHighlight[i1];
            i3 = localHighlight.getStartOffset();
            i4 = localHighlight.getEndOffset();
            if ((i3 <= k) && (i4 >= j) && (SwingUtilities2.useSelectedTextColor(localHighlight, localJTextComponent)))
            {
              if ((i3 <= j) && (i4 >= k))
              {
                paintTextUsingColor(paramGraphics, paramShape, (Color)localObject, j, k);
                i = 1;
                break;
              }
              if (m == 0)
              {
                initSelections(j, k);
                m = 1;
              }
              i3 = Math.max(j, i3);
              i4 = Math.min(k, i4);
              paintTextUsingColor(paramGraphics, paramShape, (Color)localObject, i3, i4);
              int tmp426_425 = (i3 - j);
              byte[] tmp426_418 = selections;
              tmp426_418[tmp426_425] = ((byte)(tmp426_418[tmp426_425] + 1));
              int tmp441_440 = (i4 - j);
              byte[] tmp441_433 = selections;
              tmp441_433[tmp441_440] = ((byte)(tmp441_433[tmp441_440] - 1));
              n++;
            }
          }
          if ((i == 0) && (n > 0))
          {
            i1 = -1;
            int i2 = 0;
            i3 = k - j;
            while (i1++ < i3)
            {
              while ((i1 < i3) && (selections[i1] == 0)) {
                i1++;
              }
              if (i2 != i1) {
                paintTextUsingColor(paramGraphics, paramShape, localColor2, j + i2, j + i1);
              }
              i4 = 0;
              while ((i1 < i3) && (i4 += selections[i1] != 0)) {
                i1++;
              }
              i2 = i1;
            }
            i = 1;
          }
        }
      }
    }
    if (i == 0) {
      paintTextUsingColor(paramGraphics, paramShape, localColor2, j, k);
    }
  }
  
  final void paintTextUsingColor(Graphics paramGraphics, Shape paramShape, Color paramColor, int paramInt1, int paramInt2)
  {
    paramGraphics.setColor(paramColor);
    painter.paint(this, paramGraphics, paramShape, paramInt1, paramInt2);
    boolean bool1 = isUnderline();
    boolean bool2 = isStrikeThrough();
    if ((bool1) || (bool2))
    {
      Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      View localView = getParent();
      if ((localView != null) && (localView.getEndOffset() == paramInt2))
      {
        Segment localSegment = getText(paramInt1, paramInt2);
        while (Character.isWhitespace(localSegment.last()))
        {
          paramInt2--;
          count -= 1;
        }
        SegmentCache.releaseSharedSegment(localSegment);
      }
      int i = x;
      int j = getStartOffset();
      if (j != paramInt1) {
        i += (int)painter.getSpan(this, j, paramInt1, getTabExpander(), i);
      }
      int k = i + (int)painter.getSpan(this, paramInt1, paramInt2, getTabExpander(), i);
      int m = y + (int)(painter.getHeight(this) - painter.getDescent(this));
      int n;
      if (bool1)
      {
        n = m + 1;
        paramGraphics.drawLine(i, n, k, n);
      }
      if (bool2)
      {
        n = m - (int)(painter.getAscent(this) * 0.3F);
        paramGraphics.drawLine(i, n, k, n);
      }
    }
  }
  
  public float getMinimumSpan(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      if (minimumSpan < 0.0F)
      {
        minimumSpan = 0.0F;
        int i = getStartOffset();
        int k;
        for (int j = getEndOffset(); j > i; j = k - 1)
        {
          k = getBreakSpot(i, j);
          if (k == -1) {
            k = i;
          }
          minimumSpan = Math.max(minimumSpan, getPartialSpan(k, j));
        }
      }
      return minimumSpan;
    case 1: 
      return super.getMinimumSpan(paramInt);
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getPreferredSpan(int paramInt)
  {
    if (impliedCR) {
      return 0.0F;
    }
    checkPainter();
    int i = getStartOffset();
    int j = getEndOffset();
    switch (paramInt)
    {
    case 0: 
      if (skipWidth) {
        return 0.0F;
      }
      return painter.getSpan(this, i, j, expander, x);
    case 1: 
      float f = painter.getHeight(this);
      if (isSuperscript()) {
        f += f / 3.0F;
      }
      return f;
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getAlignment(int paramInt)
  {
    checkPainter();
    if (paramInt == 1)
    {
      boolean bool1 = isSuperscript();
      boolean bool2 = isSubscript();
      float f1 = painter.getHeight(this);
      float f2 = painter.getDescent(this);
      float f3 = painter.getAscent(this);
      float f4;
      if (bool1) {
        f4 = 1.0F;
      } else if (bool2) {
        f4 = f1 > 0.0F ? (f1 - (f2 + f3 / 2.0F)) / f1 : 0.0F;
      } else {
        f4 = f1 > 0.0F ? (f1 - f2) / f1 : 0.0F;
      }
      return f4;
    }
    return super.getAlignment(paramInt);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    checkPainter();
    return painter.modelToView(this, paramInt, paramBias, paramShape);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    checkPainter();
    return painter.viewToModel(this, paramFloat1, paramFloat2, paramShape, paramArrayOfBias);
  }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2)
  {
    if (paramInt == 0)
    {
      checkPainter();
      int i = getStartOffset();
      int j = painter.getBoundedPosition(this, i, paramFloat1, paramFloat2);
      return getBreakSpot(i, j) != -1 ? 2000 : j == i ? 0 : 1000;
    }
    return super.getBreakWeight(paramInt, paramFloat1, paramFloat2);
  }
  
  public View breakView(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    if (paramInt1 == 0)
    {
      checkPainter();
      int i = painter.getBoundedPosition(this, paramInt2, paramFloat1, paramFloat2);
      int j = getBreakSpot(paramInt2, i);
      if (j != -1) {
        i = j;
      }
      if ((paramInt2 == getStartOffset()) && (i == getEndOffset())) {
        return this;
      }
      GlyphView localGlyphView = (GlyphView)createFragment(paramInt2, i);
      x = ((int)paramFloat1);
      return localGlyphView;
    }
    return this;
  }
  
  private int getBreakSpot(int paramInt1, int paramInt2)
  {
    if (breakSpots == null)
    {
      i = getStartOffset();
      j = getEndOffset();
      int[] arrayOfInt = new int[j + 1 - i];
      int m = 0;
      Element localElement = getElement().getParentElement();
      int n = localElement == null ? i : localElement.getStartOffset();
      int i1 = localElement == null ? j : localElement.getEndOffset();
      Segment localSegment = getText(n, i1);
      localSegment.first();
      BreakIterator localBreakIterator = getBreaker();
      localBreakIterator.setText(localSegment);
      int i2 = j + (i1 > j ? 1 : 0);
      for (;;)
      {
        i2 = localBreakIterator.preceding(offset + (i2 - n)) + (n - offset);
        if (i2 <= i) {
          break;
        }
        arrayOfInt[(m++)] = i2;
      }
      SegmentCache.releaseSharedSegment(localSegment);
      breakSpots = new int[m];
      System.arraycopy(arrayOfInt, 0, breakSpots, 0, m);
    }
    int i = -1;
    for (int j = 0; j < breakSpots.length; j++)
    {
      int k = breakSpots[j];
      if (k <= paramInt2)
      {
        if (k <= paramInt1) {
          break;
        }
        i = k;
        break;
      }
    }
    return i;
  }
  
  private BreakIterator getBreaker()
  {
    Document localDocument = getDocument();
    if ((localDocument != null) && (Boolean.TRUE.equals(localDocument.getProperty(AbstractDocument.MultiByteProperty))))
    {
      Container localContainer = getContainer();
      Locale localLocale = localContainer == null ? Locale.getDefault() : localContainer.getLocale();
      return BreakIterator.getLineInstance(localLocale);
    }
    return new WhitespaceBasedBreakIterator();
  }
  
  public View createFragment(int paramInt1, int paramInt2)
  {
    checkPainter();
    Element localElement = getElement();
    GlyphView localGlyphView = (GlyphView)clone();
    offset = (paramInt1 - localElement.getStartOffset());
    length = (paramInt2 - paramInt1);
    painter = painter.getPainter(localGlyphView, paramInt1, paramInt2);
    justificationInfo = null;
    return localGlyphView;
  }
  
  public int getNextVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    if (paramInt1 < -1) {
      throw new BadLocationException("invalid position", paramInt1);
    }
    return painter.getNextVisualPositionFrom(this, paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    justificationInfo = null;
    breakSpots = null;
    minimumSpan = -1.0F;
    syncCR();
    preferenceChanged(null, true, false);
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    justificationInfo = null;
    breakSpots = null;
    minimumSpan = -1.0F;
    syncCR();
    preferenceChanged(null, true, false);
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    minimumSpan = -1.0F;
    syncCR();
    preferenceChanged(null, true, true);
  }
  
  private void syncCR()
  {
    if (impliedCR)
    {
      Element localElement = getElement().getParentElement();
      impliedCR = ((localElement != null) && (localElement.getElementCount() > 1));
    }
  }
  
  void updateAfterChange()
  {
    breakSpots = null;
  }
  
  JustificationInfo getJustificationInfo(int paramInt)
  {
    if (justificationInfo != null) {
      return justificationInfo;
    }
    int i = getStartOffset();
    int j = getEndOffset();
    Segment localSegment = getText(i, j);
    int k = offset;
    int m = offset + count - 1;
    int n = m + 1;
    int i1 = k - 1;
    int i2 = k - 1;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    boolean bool = false;
    BitSet localBitSet = new BitSet(j - i + 1);
    int i6 = m;
    int i7 = 0;
    while (i6 >= k)
    {
      if (' ' == array[i6])
      {
        localBitSet.set(i6 - k);
        if (i7 == 0)
        {
          i3++;
        }
        else if (i7 == 1)
        {
          i7 = 2;
          i5 = 1;
        }
        else if (i7 == 2)
        {
          i5++;
        }
      }
      else
      {
        if ('\t' == array[i6])
        {
          bool = true;
          break;
        }
        if (i7 == 0)
        {
          if (('\n' != array[i6]) && ('\r' != array[i6]))
          {
            i7 = 1;
            i1 = i6;
          }
        }
        else if ((i7 != 1) && (i7 == 2))
        {
          i4 += i5;
          i5 = 0;
        }
        n = i6;
      }
      i6--;
    }
    SegmentCache.releaseSharedSegment(localSegment);
    i6 = -1;
    if (n < m) {
      i6 = n - k;
    }
    i7 = -1;
    if (i1 > k) {
      i7 = i1 - k;
    }
    justificationInfo = new JustificationInfo(i6, i7, i5, i4, i3, bool, localBitSet);
    return justificationInfo;
  }
  
  public static abstract class GlyphPainter
  {
    public GlyphPainter() {}
    
    public abstract float getSpan(GlyphView paramGlyphView, int paramInt1, int paramInt2, TabExpander paramTabExpander, float paramFloat);
    
    public abstract float getHeight(GlyphView paramGlyphView);
    
    public abstract float getAscent(GlyphView paramGlyphView);
    
    public abstract float getDescent(GlyphView paramGlyphView);
    
    public abstract void paint(GlyphView paramGlyphView, Graphics paramGraphics, Shape paramShape, int paramInt1, int paramInt2);
    
    public abstract Shape modelToView(GlyphView paramGlyphView, int paramInt, Position.Bias paramBias, Shape paramShape)
      throws BadLocationException;
    
    public abstract int viewToModel(GlyphView paramGlyphView, float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias);
    
    public abstract int getBoundedPosition(GlyphView paramGlyphView, int paramInt, float paramFloat1, float paramFloat2);
    
    public GlyphPainter getPainter(GlyphView paramGlyphView, int paramInt1, int paramInt2)
    {
      return this;
    }
    
    public int getNextVisualPositionFrom(GlyphView paramGlyphView, int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
      throws BadLocationException
    {
      int i = paramGlyphView.getStartOffset();
      int j = paramGlyphView.getEndOffset();
      switch (paramInt2)
      {
      case 1: 
      case 5: 
        if (paramInt1 != -1) {
          return -1;
        }
        Container localContainer = paramGlyphView.getContainer();
        if ((localContainer instanceof JTextComponent))
        {
          Caret localCaret = ((JTextComponent)localContainer).getCaret();
          Object localObject = localCaret != null ? localCaret.getMagicCaretPosition() : null;
          if (localObject == null)
          {
            paramArrayOfBias[0] = Position.Bias.Forward;
            return i;
          }
          int k = paramGlyphView.viewToModel(x, 0.0F, paramShape, paramArrayOfBias);
          return k;
        }
        break;
      case 3: 
        if (i == paramGlyphView.getDocument().getLength())
        {
          if (paramInt1 == -1)
          {
            paramArrayOfBias[0] = Position.Bias.Forward;
            return i;
          }
          return -1;
        }
        if (paramInt1 == -1)
        {
          paramArrayOfBias[0] = Position.Bias.Forward;
          return i;
        }
        if (paramInt1 == j) {
          return -1;
        }
        paramInt1++;
        if (paramInt1 == j) {
          return -1;
        }
        paramArrayOfBias[0] = Position.Bias.Forward;
        return paramInt1;
      case 7: 
        if (i == paramGlyphView.getDocument().getLength())
        {
          if (paramInt1 == -1)
          {
            paramArrayOfBias[0] = Position.Bias.Forward;
            return i;
          }
          return -1;
        }
        if (paramInt1 == -1)
        {
          paramArrayOfBias[0] = Position.Bias.Forward;
          return j - 1;
        }
        if (paramInt1 == i) {
          return -1;
        }
        paramArrayOfBias[0] = Position.Bias.Forward;
        return paramInt1 - 1;
      case 2: 
      case 4: 
      case 6: 
      default: 
        throw new IllegalArgumentException("Bad direction: " + paramInt2);
      }
      return paramInt1;
    }
  }
  
  static class JustificationInfo
  {
    final int start;
    final int end;
    final int leadingSpaces;
    final int contentSpaces;
    final int trailingSpaces;
    final boolean hasTab;
    final BitSet spaceMap;
    
    JustificationInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, BitSet paramBitSet)
    {
      start = paramInt1;
      end = paramInt2;
      leadingSpaces = paramInt3;
      contentSpaces = paramInt4;
      trailingSpaces = paramInt5;
      hasTab = paramBoolean;
      spaceMap = paramBitSet;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\GlyphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */