package javax.swing.text;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.BitSet;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.TextUI;

public class ParagraphView
  extends FlowView
  implements TabExpander
{
  private int justification;
  private float lineSpacing;
  protected int firstLineIndent = 0;
  private int tabBase;
  static Class i18nStrategy;
  static char[] tabChars = new char[1];
  static char[] tabDecimalChars;
  
  public ParagraphView(Element paramElement)
  {
    super(paramElement, 1);
    setPropertiesFromAttributes();
    Document localDocument = paramElement.getDocument();
    Object localObject1 = localDocument.getProperty("i18n");
    if ((localObject1 != null) && (localObject1.equals(Boolean.TRUE))) {
      try
      {
        if (i18nStrategy == null)
        {
          localObject2 = "javax.swing.text.TextLayoutStrategy";
          ClassLoader localClassLoader = getClass().getClassLoader();
          if (localClassLoader != null) {
            i18nStrategy = localClassLoader.loadClass((String)localObject2);
          } else {
            i18nStrategy = Class.forName((String)localObject2);
          }
        }
        Object localObject2 = i18nStrategy.newInstance();
        if ((localObject2 instanceof FlowView.FlowStrategy)) {
          strategy = ((FlowView.FlowStrategy)localObject2);
        }
      }
      catch (Throwable localThrowable)
      {
        throw new StateInvariantError("ParagraphView: Can't create i18n strategy: " + localThrowable.getMessage());
      }
    }
  }
  
  protected void setJustification(int paramInt)
  {
    justification = paramInt;
  }
  
  protected void setLineSpacing(float paramFloat)
  {
    lineSpacing = paramFloat;
  }
  
  protected void setFirstLineIndent(float paramFloat)
  {
    firstLineIndent = ((int)paramFloat);
  }
  
  protected void setPropertiesFromAttributes()
  {
    AttributeSet localAttributeSet = getAttributes();
    if (localAttributeSet != null)
    {
      setParagraphInsets(localAttributeSet);
      Integer localInteger = (Integer)localAttributeSet.getAttribute(StyleConstants.Alignment);
      int i;
      if (localInteger == null)
      {
        Document localDocument = getElement().getDocument();
        Object localObject = localDocument.getProperty(TextAttribute.RUN_DIRECTION);
        if ((localObject != null) && (localObject.equals(TextAttribute.RUN_DIRECTION_RTL))) {
          i = 2;
        } else {
          i = 0;
        }
      }
      else
      {
        i = localInteger.intValue();
      }
      setJustification(i);
      setLineSpacing(StyleConstants.getLineSpacing(localAttributeSet));
      setFirstLineIndent(StyleConstants.getFirstLineIndent(localAttributeSet));
    }
  }
  
  protected int getLayoutViewCount()
  {
    return layoutPool.getViewCount();
  }
  
  protected View getLayoutView(int paramInt)
  {
    return layoutPool.getView(paramInt);
  }
  
  protected int getNextNorthSouthVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    int i;
    if (paramInt1 == -1)
    {
      i = paramInt2 == 1 ? getViewCount() - 1 : 0;
    }
    else
    {
      if ((paramBias == Position.Bias.Backward) && (paramInt1 > 0)) {
        i = getViewIndexAtPosition(paramInt1 - 1);
      } else {
        i = getViewIndexAtPosition(paramInt1);
      }
      if (paramInt2 == 1)
      {
        if (i == 0) {
          return -1;
        }
        i--;
      }
      else
      {
        i++;
        if (i >= getViewCount()) {
          return -1;
        }
      }
    }
    JTextComponent localJTextComponent = (JTextComponent)getContainer();
    Caret localCaret = localJTextComponent.getCaret();
    Object localObject = localCaret != null ? localCaret.getMagicCaretPosition() : null;
    int j;
    if (localObject == null)
    {
      Rectangle localRectangle;
      try
      {
        localRectangle = localJTextComponent.getUI().modelToView(localJTextComponent, paramInt1, paramBias);
      }
      catch (BadLocationException localBadLocationException)
      {
        localRectangle = null;
      }
      if (localRectangle == null) {
        j = 0;
      } else {
        j = getBoundsx;
      }
    }
    else
    {
      j = x;
    }
    return getClosestPositionTo(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias, i, j);
  }
  
  protected int getClosestPositionTo(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias, int paramInt3, int paramInt4)
    throws BadLocationException
  {
    JTextComponent localJTextComponent = (JTextComponent)getContainer();
    Document localDocument = getDocument();
    View localView1 = getView(paramInt3);
    int i = -1;
    paramArrayOfBias[0] = Position.Bias.Forward;
    int j = 0;
    int k = localView1.getViewCount();
    while (j < k)
    {
      View localView2 = localView1.getView(j);
      int m = localView2.getStartOffset();
      boolean bool = AbstractDocument.isLeftToRight(localDocument, m, m + 1);
      if (bool)
      {
        i = m;
        int n = localView2.getEndOffset();
        while (i < n)
        {
          float f2 = modelToViewgetBoundsx;
          if (f2 >= paramInt4)
          {
            do
            {
              i++;
            } while ((i < n) && (modelToViewgetBoundsx == f2));
            i--;
            return i;
          }
          i++;
        }
        i--;
      }
      else
      {
        for (i = localView2.getEndOffset() - 1; i >= m; i--)
        {
          float f1 = modelToViewgetBoundsx;
          if (f1 >= paramInt4)
          {
            do
            {
              i--;
            } while ((i >= m) && (modelToViewgetBoundsx == f1));
            i++;
            return i;
          }
        }
        i++;
      }
      j++;
    }
    if (i == -1) {
      return getStartOffset();
    }
    return i;
  }
  
  protected boolean flipEastAndWestAtEnds(int paramInt, Position.Bias paramBias)
  {
    Document localDocument = getDocument();
    paramInt = getStartOffset();
    return !AbstractDocument.isLeftToRight(localDocument, paramInt, paramInt + 1);
  }
  
  public int getFlowSpan(int paramInt)
  {
    View localView = getView(paramInt);
    int i = 0;
    if ((localView instanceof Row))
    {
      Row localRow = (Row)localView;
      i = localRow.getLeftInset() + localRow.getRightInset();
    }
    return layoutSpan == Integer.MAX_VALUE ? layoutSpan : layoutSpan - i;
  }
  
  public int getFlowStart(int paramInt)
  {
    View localView = getView(paramInt);
    int i = 0;
    if ((localView instanceof Row))
    {
      Row localRow = (Row)localView;
      i = localRow.getLeftInset();
    }
    return tabBase + i;
  }
  
  protected View createRow()
  {
    return new Row(getElement());
  }
  
  public float nextTabStop(float paramFloat, int paramInt)
  {
    if (justification != 0) {
      return paramFloat + 10.0F;
    }
    paramFloat -= tabBase;
    TabSet localTabSet = getTabSet();
    if (localTabSet == null) {
      return tabBase + ((int)paramFloat / 72 + 1) * 72;
    }
    TabStop localTabStop = localTabSet.getTabAfter(paramFloat + 0.01F);
    if (localTabStop == null) {
      return tabBase + paramFloat + 5.0F;
    }
    int i = localTabStop.getAlignment();
    int j;
    switch (i)
    {
    case 0: 
    case 3: 
    default: 
      return tabBase + localTabStop.getPosition();
    case 5: 
      return tabBase + localTabStop.getPosition();
    case 1: 
    case 2: 
      j = findOffsetToCharactersInString(tabChars, paramInt + 1);
      break;
    case 4: 
      j = findOffsetToCharactersInString(tabDecimalChars, paramInt + 1);
    }
    if (j == -1) {
      j = getEndOffset();
    }
    float f = getPartialSize(paramInt + 1, j);
    switch (i)
    {
    case 1: 
    case 4: 
      return tabBase + Math.max(paramFloat, localTabStop.getPosition() - f);
    case 2: 
      return tabBase + Math.max(paramFloat, localTabStop.getPosition() - f / 2.0F);
    }
    return paramFloat;
  }
  
  protected TabSet getTabSet()
  {
    return StyleConstants.getTabSet(getElement().getAttributes());
  }
  
  protected float getPartialSize(int paramInt1, int paramInt2)
  {
    float f = 0.0F;
    int j = getViewCount();
    int i = getElement().getElementIndex(paramInt1);
    j = layoutPool.getViewCount();
    while ((paramInt1 < paramInt2) && (i < j))
    {
      View localView = layoutPool.getView(i++);
      int k = localView.getEndOffset();
      int m = Math.min(paramInt2, k);
      if ((localView instanceof TabableView)) {
        f += ((TabableView)localView).getPartialSpan(paramInt1, m);
      } else if ((paramInt1 == localView.getStartOffset()) && (m == localView.getEndOffset())) {
        f += localView.getPreferredSpan(0);
      } else {
        return 0.0F;
      }
      paramInt1 = k;
    }
    return f;
  }
  
  protected int findOffsetToCharactersInString(char[] paramArrayOfChar, int paramInt)
  {
    int i = paramArrayOfChar.length;
    int j = getEndOffset();
    Segment localSegment = new Segment();
    try
    {
      getDocument().getText(paramInt, j - paramInt, localSegment);
    }
    catch (BadLocationException localBadLocationException)
    {
      return -1;
    }
    int k = offset;
    int m = offset + count;
    while (k < m)
    {
      int n = array[k];
      for (int i1 = 0; i1 < i; i1++) {
        if (n == paramArrayOfChar[i1]) {
          return k - offset + paramInt;
        }
      }
      k++;
    }
    return -1;
  }
  
  protected float getTabBase()
  {
    return tabBase;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Rectangle localRectangle1 = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    tabBase = (x + getLeftInset());
    super.paint(paramGraphics, paramShape);
    if (firstLineIndent < 0)
    {
      Shape localShape = getChildAllocation(0, paramShape);
      if ((localShape != null) && (localShape.intersects(localRectangle1)))
      {
        int i = x + getLeftInset() + firstLineIndent;
        int j = y + getTopInset();
        Rectangle localRectangle2 = paramGraphics.getClipBounds();
        tempRect.x = (i + getOffset(0, 0));
        tempRect.y = (j + getOffset(1, 0));
        tempRect.width = (getSpan(0, 0) - firstLineIndent);
        tempRect.height = getSpan(1, 0);
        if (tempRect.intersects(localRectangle2))
        {
          tempRect.x -= firstLineIndent;
          paintChild(paramGraphics, tempRect, 0);
        }
      }
    }
  }
  
  public float getAlignment(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      float f = 0.5F;
      if (getViewCount() != 0)
      {
        int i = (int)getPreferredSpan(1);
        View localView = getView(0);
        int j = (int)localView.getPreferredSpan(1);
        f = i != 0 ? j / 2 / i : 0.0F;
      }
      return f;
    case 0: 
      return 0.5F;
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public View breakView(int paramInt, float paramFloat, Shape paramShape)
  {
    if (paramInt == 1)
    {
      if (paramShape != null)
      {
        Rectangle localRectangle = paramShape.getBounds();
        setSize(width, height);
      }
      return this;
    }
    return this;
  }
  
  public int getBreakWeight(int paramInt, float paramFloat)
  {
    if (paramInt == 1) {
      return 0;
    }
    return 0;
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    paramSizeRequirements = super.calculateMinorAxisRequirements(paramInt, paramSizeRequirements);
    float f1 = 0.0F;
    float f2 = 0.0F;
    int i = getLayoutViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getLayoutView(j);
      float f3 = localView.getMinimumSpan(paramInt);
      if (localView.getBreakWeight(paramInt, 0.0F, localView.getMaximumSpan(paramInt)) > 0)
      {
        int k = localView.getStartOffset();
        int m = localView.getEndOffset();
        float f4 = findEdgeSpan(localView, paramInt, k, k, m);
        float f5 = findEdgeSpan(localView, paramInt, m, k, m);
        f2 += f4;
        f1 = Math.max(f1, Math.max(f3, f2));
        f2 = f5;
      }
      else
      {
        f2 += f3;
        f1 = Math.max(f1, f2);
      }
    }
    minimum = Math.max(minimum, (int)f1);
    preferred = Math.max(minimum, preferred);
    maximum = Math.max(preferred, maximum);
    return paramSizeRequirements;
  }
  
  private float findEdgeSpan(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt4 - paramInt3;
    if (i <= 1) {
      return paramView.getMinimumSpan(paramInt1);
    }
    int j = paramInt3 + i / 2;
    int k = j > paramInt2 ? 1 : 0;
    View localView = k != 0 ? paramView.createFragment(paramInt2, j) : paramView.createFragment(j, paramInt2);
    int m = localView.getBreakWeight(paramInt1, 0.0F, localView.getMaximumSpan(paramInt1)) > 0 ? 1 : 0;
    if (m == k) {
      paramInt4 = j;
    } else {
      paramInt3 = j;
    }
    return findEdgeSpan(localView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    setPropertiesFromAttributes();
    layoutChanged(0);
    layoutChanged(1);
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  static
  {
    tabChars[0] = '\t';
    tabDecimalChars = new char[2];
    tabDecimalChars[0] = '\t';
    tabDecimalChars[1] = '.';
  }
  
  class Row
    extends BoxView
  {
    static final int SPACE_ADDON = 0;
    static final int SPACE_ADDON_LEFTOVER_END = 1;
    static final int START_JUSTIFIABLE = 2;
    static final int END_JUSTIFIABLE = 3;
    int[] justificationData = null;
    
    Row(Element paramElement)
    {
      super(0);
    }
    
    protected void loadChildren(ViewFactory paramViewFactory) {}
    
    public AttributeSet getAttributes()
    {
      View localView = getParent();
      return localView != null ? localView.getAttributes() : null;
    }
    
    public float getAlignment(int paramInt)
    {
      if (paramInt == 0) {
        switch (justification)
        {
        case 0: 
          return 0.0F;
        case 2: 
          return 1.0F;
        case 1: 
          return 0.5F;
        case 3: 
          float f = 0.5F;
          if (isJustifiableDocument()) {
            f = 0.0F;
          }
          return f;
        }
      }
      return super.getAlignment(paramInt);
    }
    
    public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
      throws BadLocationException
    {
      Rectangle localRectangle = paramShape.getBounds();
      View localView = getViewAtPosition(paramInt, localRectangle);
      if ((localView != null) && (!localView.getElement().isLeaf())) {
        return super.modelToView(paramInt, paramShape, paramBias);
      }
      localRectangle = paramShape.getBounds();
      int i = height;
      int j = y;
      Shape localShape = super.modelToView(paramInt, paramShape, paramBias);
      localRectangle = localShape.getBounds();
      height = i;
      y = j;
      return localRectangle;
    }
    
    public int getStartOffset()
    {
      int i = Integer.MAX_VALUE;
      int j = getViewCount();
      for (int k = 0; k < j; k++)
      {
        View localView = getView(k);
        i = Math.min(i, localView.getStartOffset());
      }
      return i;
    }
    
    public int getEndOffset()
    {
      int i = 0;
      int j = getViewCount();
      for (int k = 0; k < j; k++)
      {
        View localView = getView(k);
        i = Math.max(i, localView.getEndOffset());
      }
      return i;
    }
    
    protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      baselineLayout(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
    }
    
    protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
    {
      return baselineRequirements(paramInt, paramSizeRequirements);
    }
    
    private boolean isLastRow()
    {
      View localView;
      return ((localView = getParent()) == null) || (this == localView.getView(localView.getViewCount() - 1));
    }
    
    private boolean isBrokenRow()
    {
      boolean bool = false;
      int i = getViewCount();
      if (i > 0)
      {
        View localView = getView(i - 1);
        if (localView.getBreakWeight(0, 0.0F, 0.0F) >= 3000) {
          bool = true;
        }
      }
      return bool;
    }
    
    private boolean isJustifiableDocument()
    {
      return !Boolean.TRUE.equals(getDocument().getProperty("i18n"));
    }
    
    private boolean isJustifyEnabled()
    {
      boolean bool = justification == 3;
      bool = (bool) && (isJustifiableDocument());
      bool = (bool) && (!isLastRow());
      bool = (bool) && (!isBrokenRow());
      return bool;
    }
    
    protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
    {
      int[] arrayOfInt = justificationData;
      justificationData = null;
      SizeRequirements localSizeRequirements = super.calculateMajorAxisRequirements(paramInt, paramSizeRequirements);
      if (isJustifyEnabled()) {
        justificationData = arrayOfInt;
      }
      return localSizeRequirements;
    }
    
    protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      int[] arrayOfInt1 = justificationData;
      justificationData = null;
      super.layoutMajorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
      if (!isJustifyEnabled()) {
        return;
      }
      int i = 0;
      for (n : paramArrayOfInt2) {
        i += n;
      }
      if (i == paramInt1) {
        return;
      }
      int j = 0;
      ??? = -1;
      ??? = -1;
      int n = 0;
      int i1 = getStartOffset();
      int i2 = getEndOffset();
      int[] arrayOfInt3 = new int[i2 - i1];
      Arrays.fill(arrayOfInt3, 0);
      for (int i3 = getViewCount() - 1; i3 >= 0; i3--)
      {
        View localView = getView(i3);
        if ((localView instanceof GlyphView))
        {
          GlyphView.JustificationInfo localJustificationInfo = ((GlyphView)localView).getJustificationInfo(i1);
          i6 = localView.getStartOffset();
          i7 = i6 - i1;
          for (int i8 = 0; i8 < spaceMap.length(); i8++) {
            if (spaceMap.get(i8)) {
              arrayOfInt3[(i8 + i7)] = 1;
            }
          }
          if (??? > 0) {
            if (end >= 0) {
              j += trailingSpaces;
            } else {
              n += trailingSpaces;
            }
          }
          if (start >= 0)
          {
            ??? = start + i6;
            j += n;
          }
          if ((end >= 0) && (??? < 0)) {
            ??? = end + i6;
          }
          j += contentSpaces;
          n = leadingSpaces;
          if (hasTab) {
            break;
          }
        }
      }
      if (j <= 0) {
        return;
      }
      i3 = paramInt1 - i;
      int i4 = j > 0 ? i3 / j : 0;
      int i5 = -1;
      int i6 = ??? - i1;
      int i7 = i3 - i4 * j;
      while (i7 > 0)
      {
        i5 = i6;
        i7 -= arrayOfInt3[i6];
        i6++;
      }
      if ((i4 > 0) || (i5 >= 0))
      {
        justificationData = (arrayOfInt1 != null ? arrayOfInt1 : new int[4]);
        justificationData[0] = i4;
        justificationData[1] = i5;
        justificationData[2] = (??? - i1);
        justificationData[3] = (??? - i1);
        super.layoutMajorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
      }
    }
    
    public float getMaximumSpan(int paramInt)
    {
      float f;
      if ((0 == paramInt) && (isJustifyEnabled())) {
        f = Float.MAX_VALUE;
      } else {
        f = super.getMaximumSpan(paramInt);
      }
      return f;
    }
    
    protected int getViewIndexAtPosition(int paramInt)
    {
      if ((paramInt < getStartOffset()) || (paramInt >= getEndOffset())) {
        return -1;
      }
      for (int i = getViewCount() - 1; i >= 0; i--)
      {
        View localView = getView(i);
        if ((paramInt >= localView.getStartOffset()) && (paramInt < localView.getEndOffset())) {
          return i;
        }
      }
      return -1;
    }
    
    protected short getLeftInset()
    {
      int i = 0;
      View localView;
      if (((localView = getParent()) != null) && (this == localView.getView(0))) {
        i = firstLineIndent;
      }
      return (short)(super.getLeftInset() + i);
    }
    
    protected short getBottomInset()
    {
      return (short)(int)(super.getBottomInset() + (minorRequest != null ? minorRequest.preferred : 0) * lineSpacing);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\ParagraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */