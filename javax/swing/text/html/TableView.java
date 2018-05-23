package javax.swing.text.html;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

class TableView
  extends BoxView
  implements ViewFactory
{
  private AttributeSet attr;
  private StyleSheet.BoxPainter painter;
  private int cellSpacing;
  private int borderWidth;
  private int captionIndex = -1;
  private boolean relativeCells;
  private boolean multiRowCells;
  int[] columnSpans;
  int[] columnOffsets;
  SizeRequirements totalColumnRequirements = new SizeRequirements();
  SizeRequirements[] columnRequirements;
  RowIterator rowIterator = new RowIterator();
  ColumnIterator colIterator = new ColumnIterator();
  Vector<RowView> rows = new Vector();
  boolean skipComments = false;
  boolean gridValid = false;
  private static final BitSet EMPTY = new BitSet();
  
  public TableView(Element paramElement)
  {
    super(paramElement, 1);
  }
  
  protected RowView createTableRow(Element paramElement)
  {
    Object localObject = paramElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
    if (localObject == HTML.Tag.TR) {
      return new RowView(paramElement);
    }
    return null;
  }
  
  public int getColumnCount()
  {
    return columnSpans.length;
  }
  
  public int getColumnSpan(int paramInt)
  {
    if (paramInt < columnSpans.length) {
      return columnSpans[paramInt];
    }
    return 0;
  }
  
  public int getRowCount()
  {
    return rows.size();
  }
  
  public int getMultiRowSpan(int paramInt1, int paramInt2)
  {
    RowView localRowView1 = getRow(paramInt1);
    RowView localRowView2 = getRow(paramInt2);
    if ((localRowView1 != null) && (localRowView2 != null))
    {
      int i = viewIndex;
      int j = viewIndex;
      int k = getOffset(1, j) - getOffset(1, i) + getSpan(1, j);
      return k;
    }
    return 0;
  }
  
  public int getRowSpan(int paramInt)
  {
    RowView localRowView = getRow(paramInt);
    if (localRowView != null) {
      return getSpan(1, viewIndex);
    }
    return 0;
  }
  
  RowView getRow(int paramInt)
  {
    if (paramInt < rows.size()) {
      return (RowView)rows.elementAt(paramInt);
    }
    return null;
  }
  
  protected View getViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle)
  {
    int i = getViewCount();
    Rectangle localRectangle = new Rectangle();
    for (int j = 0; j < i; j++)
    {
      localRectangle.setBounds(paramRectangle);
      childAllocation(j, localRectangle);
      View localView = getView(j);
      if ((localView instanceof RowView))
      {
        localView = ((RowView)localView).findViewAtPoint(paramInt1, paramInt2, localRectangle);
        if (localView != null)
        {
          paramRectangle.setBounds(localRectangle);
          return localView;
        }
      }
    }
    return super.getViewAtPoint(paramInt1, paramInt2, paramRectangle);
  }
  
  protected int getColumnsOccupied(View paramView)
  {
    AttributeSet localAttributeSet = paramView.getElement().getAttributes();
    if (localAttributeSet.isDefined(HTML.Attribute.COLSPAN))
    {
      String str = (String)localAttributeSet.getAttribute(HTML.Attribute.COLSPAN);
      if (str != null) {
        try
        {
          return Integer.parseInt(str);
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
    }
    return 1;
  }
  
  protected int getRowsOccupied(View paramView)
  {
    AttributeSet localAttributeSet = paramView.getElement().getAttributes();
    if (localAttributeSet.isDefined(HTML.Attribute.ROWSPAN))
    {
      String str = (String)localAttributeSet.getAttribute(HTML.Attribute.ROWSPAN);
      if (str != null) {
        try
        {
          return Integer.parseInt(str);
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
    }
    return 1;
  }
  
  protected void invalidateGrid()
  {
    gridValid = false;
  }
  
  protected StyleSheet getStyleSheet()
  {
    HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
    return localHTMLDocument.getStyleSheet();
  }
  
  void updateInsets()
  {
    short s1 = (short)(int)painter.getInset(1, this);
    short s2 = (short)(int)painter.getInset(3, this);
    if (captionIndex != -1)
    {
      View localView = getView(captionIndex);
      short s3 = (short)(int)localView.getPreferredSpan(1);
      AttributeSet localAttributeSet = localView.getAttributes();
      Object localObject = localAttributeSet.getAttribute(CSS.Attribute.CAPTION_SIDE);
      if ((localObject != null) && (localObject.equals("bottom"))) {
        s2 = (short)(s2 + s3);
      } else {
        s1 = (short)(s1 + s3);
      }
    }
    setInsets(s1, (short)(int)painter.getInset(2, this), s2, (short)(int)painter.getInset(4, this));
  }
  
  protected void setPropertiesFromAttributes()
  {
    StyleSheet localStyleSheet = getStyleSheet();
    attr = localStyleSheet.getViewAttributes(this);
    painter = localStyleSheet.getBoxPainter(attr);
    if (attr != null)
    {
      setInsets((short)(int)painter.getInset(1, this), (short)(int)painter.getInset(2, this), (short)(int)painter.getInset(3, this), (short)(int)painter.getInset(4, this));
      CSS.LengthValue localLengthValue = (CSS.LengthValue)attr.getAttribute(CSS.Attribute.BORDER_SPACING);
      if (localLengthValue != null) {
        cellSpacing = ((int)localLengthValue.getValue());
      } else {
        cellSpacing = 2;
      }
      localLengthValue = (CSS.LengthValue)attr.getAttribute(CSS.Attribute.BORDER_TOP_WIDTH);
      if (localLengthValue != null) {
        borderWidth = ((int)localLengthValue.getValue());
      } else {
        borderWidth = 0;
      }
    }
  }
  
  void updateGrid()
  {
    if (!gridValid)
    {
      relativeCells = false;
      multiRowCells = false;
      captionIndex = -1;
      rows.removeAllElements();
      int i = getViewCount();
      Object localObject2;
      for (int j = 0; j < i; j++)
      {
        View localView1 = getView(j);
        Object localObject1;
        if ((localView1 instanceof RowView))
        {
          rows.addElement((RowView)localView1);
          localObject1 = (RowView)localView1;
          ((RowView)localObject1).clearFilledColumns();
          rowIndex = (rows.size() - 1);
          viewIndex = j;
        }
        else
        {
          localObject1 = localView1.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
          if ((localObject1 instanceof HTML.Tag))
          {
            localObject2 = (HTML.Tag)localObject1;
            if (localObject2 == HTML.Tag.CAPTION) {
              captionIndex = j;
            }
          }
        }
      }
      j = 0;
      int k = rows.size();
      for (int m = 0; m < k; m++)
      {
        localObject2 = getRow(m);
        int n = 0;
        int i1 = 0;
        while (i1 < ((RowView)localObject2).getViewCount())
        {
          View localView2 = ((RowView)localObject2).getView(i1);
          if (!relativeCells)
          {
            AttributeSet localAttributeSet = localView2.getAttributes();
            CSS.LengthValue localLengthValue = (CSS.LengthValue)localAttributeSet.getAttribute(CSS.Attribute.WIDTH);
            if ((localLengthValue != null) && (localLengthValue.isPercentage())) {
              relativeCells = true;
            }
          }
          while (((RowView)localObject2).isFilled(n)) {
            n++;
          }
          int i2 = getRowsOccupied(localView2);
          if (i2 > 1) {
            multiRowCells = true;
          }
          int i3 = getColumnsOccupied(localView2);
          if ((i3 > 1) || (i2 > 1))
          {
            int i4 = m + i2;
            int i5 = n + i3;
            for (int i6 = m; i6 < i4; i6++) {
              for (int i7 = n; i7 < i5; i7++) {
                if ((i6 != m) || (i7 != n)) {
                  addFill(i6, i7);
                }
              }
            }
            if (i3 > 1) {
              n += i3 - 1;
            }
          }
          i1++;
          n++;
        }
        j = Math.max(j, n);
      }
      columnSpans = new int[j];
      columnOffsets = new int[j];
      columnRequirements = new SizeRequirements[j];
      for (m = 0; m < j; m++)
      {
        columnRequirements[m] = new SizeRequirements();
        columnRequirements[m].maximum = Integer.MAX_VALUE;
      }
      gridValid = true;
    }
  }
  
  void addFill(int paramInt1, int paramInt2)
  {
    RowView localRowView = getRow(paramInt1);
    if (localRowView != null) {
      localRowView.fillColumn(paramInt2);
    }
  }
  
  protected void layoutColumns(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, SizeRequirements[] paramArrayOfSizeRequirements)
  {
    Arrays.fill(paramArrayOfInt1, 0);
    Arrays.fill(paramArrayOfInt2, 0);
    colIterator.setLayoutArrays(paramArrayOfInt1, paramArrayOfInt2, paramInt);
    CSS.calculateTiledLayout(colIterator, paramInt);
  }
  
  void calculateColumnRequirements(int paramInt)
  {
    for (Object localObject2 : columnRequirements)
    {
      minimum = 0;
      preferred = 0;
      maximum = Integer.MAX_VALUE;
    }
    ??? = getContainer();
    if (??? != null) {
      if ((??? instanceof JTextComponent)) {
        skipComments = (!((JTextComponent)???).isEditable());
      } else {
        skipComments = true;
      }
    }
    ??? = 0;
    ??? = getRowCount();
    RowView localRowView;
    int m;
    int n;
    int i1;
    View localView;
    int i2;
    for (int k = 0; k < ???; k++)
    {
      localRowView = getRow(k);
      m = 0;
      n = localRowView.getViewCount();
      for (i1 = 0; i1 < n; i1++)
      {
        localView = localRowView.getView(i1);
        if ((!skipComments) || ((localView instanceof CellView)))
        {
          while (localRowView.isFilled(m)) {
            m++;
          }
          i2 = getRowsOccupied(localView);
          int i3 = getColumnsOccupied(localView);
          if (i3 == 1)
          {
            checkSingleColumnCell(paramInt, m, localView);
          }
          else
          {
            ??? = 1;
            m += i3 - 1;
          }
          m++;
        }
      }
    }
    if (??? != 0) {
      for (k = 0; k < ???; k++)
      {
        localRowView = getRow(k);
        m = 0;
        n = localRowView.getViewCount();
        for (i1 = 0; i1 < n; i1++)
        {
          localView = localRowView.getView(i1);
          if ((!skipComments) || ((localView instanceof CellView)))
          {
            while (localRowView.isFilled(m)) {
              m++;
            }
            i2 = getColumnsOccupied(localView);
            if (i2 > 1)
            {
              checkMultiColumnCell(paramInt, m, i2, localView);
              m += i2 - 1;
            }
            m++;
          }
        }
      }
    }
  }
  
  void checkSingleColumnCell(int paramInt1, int paramInt2, View paramView)
  {
    SizeRequirements localSizeRequirements = columnRequirements[paramInt2];
    minimum = Math.max((int)paramView.getMinimumSpan(paramInt1), minimum);
    preferred = Math.max((int)paramView.getPreferredSpan(paramInt1), preferred);
  }
  
  void checkMultiColumnCell(int paramInt1, int paramInt2, int paramInt3, View paramView)
  {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    Object localObject1;
    for (int i = 0; i < paramInt3; i++)
    {
      localObject1 = columnRequirements[(paramInt2 + i)];
      l1 += minimum;
      l2 += preferred;
      l3 += maximum;
    }
    i = (int)paramView.getMinimumSpan(paramInt1);
    Object localObject2;
    if (i > l1)
    {
      localObject1 = new SizeRequirements[paramInt3];
      for (int k = 0; k < paramInt3; k++) {
        localObject1[k] = columnRequirements[(paramInt2 + k)];
      }
      localObject2 = new int[paramInt3];
      int[] arrayOfInt1 = new int[paramInt3];
      SizeRequirements.calculateTiledPositions(i, null, (SizeRequirements[])localObject1, arrayOfInt1, (int[])localObject2);
      for (int n = 0; n < paramInt3; n++)
      {
        Object localObject3 = localObject1[n];
        minimum = Math.max(localObject2[n], minimum);
        preferred = Math.max(minimum, preferred);
        maximum = Math.max(preferred, maximum);
      }
    }
    int j = (int)paramView.getPreferredSpan(paramInt1);
    if (j > l2)
    {
      localObject2 = new SizeRequirements[paramInt3];
      for (int m = 0; m < paramInt3; m++) {
        localObject2[m] = columnRequirements[(paramInt2 + m)];
      }
      int[] arrayOfInt2 = new int[paramInt3];
      int[] arrayOfInt3 = new int[paramInt3];
      SizeRequirements.calculateTiledPositions(j, null, (SizeRequirements[])localObject2, arrayOfInt3, arrayOfInt2);
      for (int i1 = 0; i1 < paramInt3; i1++)
      {
        Object localObject4 = localObject2[i1];
        preferred = Math.max(arrayOfInt2[i1], preferred);
        maximum = Math.max(preferred, maximum);
      }
    }
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    updateGrid();
    calculateColumnRequirements(paramInt);
    if (paramSizeRequirements == null) {
      paramSizeRequirements = new SizeRequirements();
    }
    long l1 = 0L;
    long l2 = 0L;
    int i = columnRequirements.length;
    for (int j = 0; j < i; j++)
    {
      localObject1 = columnRequirements[j];
      l1 += minimum;
      l2 += preferred;
    }
    j = (i + 1) * cellSpacing + 2 * borderWidth;
    l1 += j;
    l2 += j;
    minimum = ((int)l1);
    preferred = ((int)l2);
    maximum = ((int)l2);
    Object localObject1 = getAttributes();
    CSS.LengthValue localLengthValue = (CSS.LengthValue)((AttributeSet)localObject1).getAttribute(CSS.Attribute.WIDTH);
    if ((BlockView.spanSetFromAttributes(paramInt, paramSizeRequirements, localLengthValue, null)) && (minimum < (int)l1)) {
      maximum = (minimum = preferred = (int)l1);
    }
    totalColumnRequirements.minimum = minimum;
    totalColumnRequirements.preferred = preferred;
    totalColumnRequirements.maximum = maximum;
    Object localObject2 = ((AttributeSet)localObject1).getAttribute(CSS.Attribute.TEXT_ALIGN);
    if (localObject2 != null)
    {
      String str = localObject2.toString();
      if (str.equals("left")) {
        alignment = 0.0F;
      } else if (str.equals("center")) {
        alignment = 0.5F;
      } else if (str.equals("right")) {
        alignment = 1.0F;
      } else {
        alignment = 0.0F;
      }
    }
    else
    {
      alignment = 0.0F;
    }
    return paramSizeRequirements;
  }
  
  protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    updateInsets();
    rowIterator.updateAdjustments();
    paramSizeRequirements = CSS.calculateTiledRequirements(rowIterator, paramSizeRequirements);
    maximum = preferred;
    return paramSizeRequirements;
  }
  
  protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    updateGrid();
    int i = getRowCount();
    for (int j = 0; j < i; j++)
    {
      RowView localRowView = getRow(j);
      localRowView.layoutChanged(paramInt2);
    }
    layoutColumns(paramInt1, columnOffsets, columnSpans, columnRequirements);
    super.layoutMinorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    rowIterator.setLayoutArrays(paramArrayOfInt1, paramArrayOfInt2);
    CSS.calculateTiledLayout(rowIterator, paramInt1);
    if (captionIndex != -1)
    {
      View localView = getView(captionIndex);
      int i = (int)localView.getPreferredSpan(1);
      paramArrayOfInt2[captionIndex] = i;
      int j = (short)(int)painter.getInset(3, this);
      if (j != getBottomInset()) {
        paramArrayOfInt1[captionIndex] = (paramInt1 + j);
      } else {
        paramArrayOfInt1[captionIndex] = (-getTopInset());
      }
    }
  }
  
  protected View getViewAtPosition(int paramInt, Rectangle paramRectangle)
  {
    int i = getViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView2 = getView(j);
      int k = localView2.getStartOffset();
      int m = localView2.getEndOffset();
      if ((paramInt >= k) && (paramInt < m))
      {
        if (paramRectangle != null) {
          childAllocation(j, paramRectangle);
        }
        return localView2;
      }
    }
    if (paramInt == getEndOffset())
    {
      View localView1 = getView(i - 1);
      if (paramRectangle != null) {
        childAllocation(i - 1, paramRectangle);
      }
      return localView1;
    }
    return null;
  }
  
  public AttributeSet getAttributes()
  {
    if (attr == null)
    {
      StyleSheet localStyleSheet = getStyleSheet();
      attr = localStyleSheet.getViewAttributes(this);
    }
    return attr;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Rectangle localRectangle = paramShape.getBounds();
    setSize(width, height);
    if (captionIndex != -1)
    {
      i = (short)(int)painter.getInset(1, this);
      j = (short)(int)painter.getInset(3, this);
      if (i != getTopInset())
      {
        int k = getTopInset() - i;
        y += k;
        height -= k;
      }
      else
      {
        height -= getBottomInset() - j;
      }
    }
    painter.paint(paramGraphics, x, y, width, height, this);
    int i = getViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getView(j);
      localView.paint(paramGraphics, getChildAllocation(j, paramShape));
    }
  }
  
  public void setParent(View paramView)
  {
    super.setParent(paramView);
    if (paramView != null) {
      setPropertiesFromAttributes();
    }
  }
  
  public ViewFactory getViewFactory()
  {
    return this;
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.insertUpdate(paramDocumentEvent, paramShape, this);
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.removeUpdate(paramDocumentEvent, paramShape, this);
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.changedUpdate(paramDocumentEvent, paramShape, this);
  }
  
  protected void forwardUpdate(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.forwardUpdate(paramElementChange, paramDocumentEvent, paramShape, paramViewFactory);
    if (paramShape != null)
    {
      Container localContainer = getContainer();
      if (localContainer != null)
      {
        Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
        localContainer.repaint(x, y, width, height);
      }
    }
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView)
  {
    super.replace(paramInt1, paramInt2, paramArrayOfView);
    invalidateGrid();
  }
  
  public View create(Element paramElement)
  {
    Object localObject1 = paramElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
    if ((localObject1 instanceof HTML.Tag))
    {
      localObject2 = (HTML.Tag)localObject1;
      if (localObject2 == HTML.Tag.TR) {
        return createTableRow(paramElement);
      }
      if ((localObject2 == HTML.Tag.TD) || (localObject2 == HTML.Tag.TH)) {
        return new CellView(paramElement);
      }
      if (localObject2 == HTML.Tag.CAPTION) {
        return new ParagraphView(paramElement);
      }
    }
    Object localObject2 = getParent();
    if (localObject2 != null)
    {
      ViewFactory localViewFactory = ((View)localObject2).getViewFactory();
      if (localViewFactory != null) {
        return localViewFactory.create(paramElement);
      }
    }
    return null;
  }
  
  class CellView
    extends BlockView
  {
    public CellView(Element paramElement)
    {
      super(1);
    }
    
    protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      super.layoutMajorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
      int i = 0;
      int j = paramArrayOfInt2.length;
      for (int k = 0; k < j; k++) {
        i += paramArrayOfInt2[k];
      }
      k = 0;
      if (i < paramInt1)
      {
        String str = (String)getElement().getAttributes().getAttribute(HTML.Attribute.VALIGN);
        if (str == null)
        {
          AttributeSet localAttributeSet = getElement().getParentElement().getAttributes();
          str = (String)localAttributeSet.getAttribute(HTML.Attribute.VALIGN);
        }
        if ((str == null) || (str.equals("middle"))) {
          k = (paramInt1 - i) / 2;
        } else if (str.equals("bottom")) {
          k = paramInt1 - i;
        }
      }
      if (k != 0) {
        for (int m = 0; m < j; m++) {
          paramArrayOfInt1[m] += k;
        }
      }
    }
    
    protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
    {
      SizeRequirements localSizeRequirements = super.calculateMajorAxisRequirements(paramInt, paramSizeRequirements);
      maximum = Integer.MAX_VALUE;
      return localSizeRequirements;
    }
    
    protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
    {
      SizeRequirements localSizeRequirements = super.calculateMinorAxisRequirements(paramInt, paramSizeRequirements);
      int i = getViewCount();
      int j = 0;
      for (int k = 0; k < i; k++)
      {
        View localView = getView(k);
        j = Math.max((int)localView.getMinimumSpan(paramInt), j);
      }
      minimum = Math.min(minimum, j);
      return localSizeRequirements;
    }
  }
  
  class ColumnIterator
    implements CSS.LayoutIterator
  {
    private int col;
    private int[] percentages;
    private int[] adjustmentWeights;
    private int[] offsets;
    private int[] spans;
    
    ColumnIterator() {}
    
    void disablePercentages()
    {
      percentages = null;
    }
    
    private void updatePercentagesAndAdjustmentWeights(int paramInt)
    {
      adjustmentWeights = new int[columnRequirements.length];
      for (int i = 0; i < columnRequirements.length; i++) {
        adjustmentWeights[i] = 0;
      }
      if (relativeCells) {
        percentages = new int[columnRequirements.length];
      } else {
        percentages = null;
      }
      i = getRowCount();
      for (int j = 0; j < i; j++)
      {
        TableView.RowView localRowView = getRow(j);
        int k = 0;
        int m = localRowView.getViewCount();
        int n = 0;
        while (n < m)
        {
          View localView = localRowView.getView(n);
          while (localRowView.isFilled(k)) {
            k++;
          }
          int i1 = getRowsOccupied(localView);
          int i2 = getColumnsOccupied(localView);
          AttributeSet localAttributeSet = localView.getAttributes();
          CSS.LengthValue localLengthValue = (CSS.LengthValue)localAttributeSet.getAttribute(CSS.Attribute.WIDTH);
          if (localLengthValue != null)
          {
            int i3 = (int)(localLengthValue.getValue(paramInt) / i2 + 0.5F);
            for (int i4 = 0; i4 < i2; i4++) {
              if (localLengthValue.isPercentage())
              {
                percentages[(k + i4)] = Math.max(percentages[(k + i4)], i3);
                adjustmentWeights[(k + i4)] = Math.max(adjustmentWeights[(k + i4)], 2);
              }
              else
              {
                adjustmentWeights[(k + i4)] = Math.max(adjustmentWeights[(k + i4)], 1);
              }
            }
          }
          k += i2 - 1;
          n++;
          k++;
        }
      }
    }
    
    public void setLayoutArrays(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
    {
      offsets = paramArrayOfInt1;
      spans = paramArrayOfInt2;
      updatePercentagesAndAdjustmentWeights(paramInt);
    }
    
    public int getCount()
    {
      return columnRequirements.length;
    }
    
    public void setIndex(int paramInt)
    {
      col = paramInt;
    }
    
    public void setOffset(int paramInt)
    {
      offsets[col] = paramInt;
    }
    
    public int getOffset()
    {
      return offsets[col];
    }
    
    public void setSpan(int paramInt)
    {
      spans[col] = paramInt;
    }
    
    public int getSpan()
    {
      return spans[col];
    }
    
    public float getMinimumSpan(float paramFloat)
    {
      return columnRequirements[col].minimum;
    }
    
    public float getPreferredSpan(float paramFloat)
    {
      if ((percentages != null) && (percentages[col] != 0)) {
        return Math.max(percentages[col], columnRequirements[col].minimum);
      }
      return columnRequirements[col].preferred;
    }
    
    public float getMaximumSpan(float paramFloat)
    {
      return columnRequirements[col].maximum;
    }
    
    public float getBorderWidth()
    {
      return borderWidth;
    }
    
    public float getLeadingCollapseSpan()
    {
      return cellSpacing;
    }
    
    public float getTrailingCollapseSpan()
    {
      return cellSpacing;
    }
    
    public int getAdjustmentWeight()
    {
      return adjustmentWeights[col];
    }
  }
  
  class RowIterator
    implements CSS.LayoutIterator
  {
    private int row;
    private int[] adjustments;
    private int[] offsets;
    private int[] spans;
    
    RowIterator() {}
    
    void updateAdjustments()
    {
      int i = 1;
      if (multiRowCells)
      {
        int j = getRowCount();
        adjustments = new int[j];
        for (int k = 0; k < j; k++)
        {
          TableView.RowView localRowView = getRow(k);
          if (multiRowCells == true)
          {
            int m = localRowView.getViewCount();
            for (int n = 0; n < m; n++)
            {
              View localView = localRowView.getView(n);
              int i1 = getRowsOccupied(localView);
              if (i1 > 1)
              {
                int i2 = (int)localView.getPreferredSpan(i);
                adjustMultiRowSpan(i2, i1, k);
              }
            }
          }
        }
      }
      else
      {
        adjustments = null;
      }
    }
    
    void adjustMultiRowSpan(int paramInt1, int paramInt2, int paramInt3)
    {
      if (paramInt3 + paramInt2 > getCount())
      {
        paramInt2 = getCount() - paramInt3;
        if (paramInt2 < 1) {
          return;
        }
      }
      int i = 0;
      for (int j = 0; j < paramInt2; j++)
      {
        TableView.RowView localRowView1 = getRow(paramInt3 + j);
        i = (int)(i + localRowView1.getPreferredSpan(1));
      }
      if (paramInt1 > i)
      {
        j = paramInt1 - i;
        int k = j / paramInt2;
        int m = k + (j - k * paramInt2);
        TableView.RowView localRowView2 = getRow(paramInt3);
        adjustments[paramInt3] = Math.max(adjustments[paramInt3], m);
        for (int n = 1; n < paramInt2; n++) {
          adjustments[(paramInt3 + n)] = Math.max(adjustments[(paramInt3 + n)], k);
        }
      }
    }
    
    void setLayoutArrays(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      offsets = paramArrayOfInt1;
      spans = paramArrayOfInt2;
    }
    
    public void setOffset(int paramInt)
    {
      TableView.RowView localRowView = getRow(row);
      if (localRowView != null) {
        offsets[viewIndex] = paramInt;
      }
    }
    
    public int getOffset()
    {
      TableView.RowView localRowView = getRow(row);
      if (localRowView != null) {
        return offsets[viewIndex];
      }
      return 0;
    }
    
    public void setSpan(int paramInt)
    {
      TableView.RowView localRowView = getRow(row);
      if (localRowView != null) {
        spans[viewIndex] = paramInt;
      }
    }
    
    public int getSpan()
    {
      TableView.RowView localRowView = getRow(row);
      if (localRowView != null) {
        return spans[viewIndex];
      }
      return 0;
    }
    
    public int getCount()
    {
      return rows.size();
    }
    
    public void setIndex(int paramInt)
    {
      row = paramInt;
    }
    
    public float getMinimumSpan(float paramFloat)
    {
      return getPreferredSpan(paramFloat);
    }
    
    public float getPreferredSpan(float paramFloat)
    {
      TableView.RowView localRowView = getRow(row);
      if (localRowView != null)
      {
        int i = adjustments != null ? adjustments[row] : 0;
        return localRowView.getPreferredSpan(getAxis()) + i;
      }
      return 0.0F;
    }
    
    public float getMaximumSpan(float paramFloat)
    {
      return getPreferredSpan(paramFloat);
    }
    
    public float getBorderWidth()
    {
      return borderWidth;
    }
    
    public float getLeadingCollapseSpan()
    {
      return cellSpacing;
    }
    
    public float getTrailingCollapseSpan()
    {
      return cellSpacing;
    }
    
    public int getAdjustmentWeight()
    {
      return 0;
    }
  }
  
  public class RowView
    extends BoxView
  {
    private StyleSheet.BoxPainter painter;
    private AttributeSet attr;
    BitSet fillColumns = new BitSet();
    int rowIndex;
    int viewIndex;
    boolean multiRowCells;
    
    public RowView(Element paramElement)
    {
      super(0);
      setPropertiesFromAttributes();
    }
    
    void clearFilledColumns()
    {
      fillColumns.and(TableView.EMPTY);
    }
    
    void fillColumn(int paramInt)
    {
      fillColumns.set(paramInt);
    }
    
    boolean isFilled(int paramInt)
    {
      return fillColumns.get(paramInt);
    }
    
    int getColumnCount()
    {
      int i = 0;
      int j = fillColumns.size();
      for (int k = 0; k < j; k++) {
        if (fillColumns.get(k)) {
          i++;
        }
      }
      return getViewCount() + i;
    }
    
    public AttributeSet getAttributes()
    {
      return attr;
    }
    
    View findViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle)
    {
      int i = getViewCount();
      for (int j = 0; j < i; j++) {
        if (getChildAllocation(j, paramRectangle).contains(paramInt1, paramInt2))
        {
          childAllocation(j, paramRectangle);
          return getView(j);
        }
      }
      return null;
    }
    
    protected StyleSheet getStyleSheet()
    {
      HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
      return localHTMLDocument.getStyleSheet();
    }
    
    public void preferenceChanged(View paramView, boolean paramBoolean1, boolean paramBoolean2)
    {
      super.preferenceChanged(paramView, paramBoolean1, paramBoolean2);
      if ((multiRowCells) && (paramBoolean2)) {
        for (int i = rowIndex - 1; i >= 0; i--)
        {
          RowView localRowView = getRow(i);
          if (multiRowCells)
          {
            localRowView.preferenceChanged(null, false, true);
            break;
          }
        }
      }
    }
    
    protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
    {
      SizeRequirements localSizeRequirements = new SizeRequirements();
      minimum = totalColumnRequirements.minimum;
      maximum = totalColumnRequirements.maximum;
      preferred = totalColumnRequirements.preferred;
      alignment = 0.0F;
      return localSizeRequirements;
    }
    
    public float getMinimumSpan(int paramInt)
    {
      float f;
      if (paramInt == 0) {
        f = totalColumnRequirements.minimum + getLeftInset() + getRightInset();
      } else {
        f = super.getMinimumSpan(paramInt);
      }
      return f;
    }
    
    public float getMaximumSpan(int paramInt)
    {
      float f;
      if (paramInt == 0) {
        f = 2.14748365E9F;
      } else {
        f = super.getMaximumSpan(paramInt);
      }
      return f;
    }
    
    public float getPreferredSpan(int paramInt)
    {
      float f;
      if (paramInt == 0) {
        f = totalColumnRequirements.preferred + getLeftInset() + getRightInset();
      } else {
        f = super.getPreferredSpan(paramInt);
      }
      return f;
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
      int i = paramDocumentEvent.getOffset();
      if ((i <= getStartOffset()) && (i + paramDocumentEvent.getLength() >= getEndOffset())) {
        setPropertiesFromAttributes();
      }
    }
    
    public void paint(Graphics paramGraphics, Shape paramShape)
    {
      Rectangle localRectangle = (Rectangle)paramShape;
      painter.paint(paramGraphics, x, y, width, height, this);
      super.paint(paramGraphics, localRectangle);
    }
    
    public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView)
    {
      super.replace(paramInt1, paramInt2, paramArrayOfView);
      invalidateGrid();
    }
    
    protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
    {
      long l1 = 0L;
      long l2 = 0L;
      long l3 = 0L;
      multiRowCells = false;
      int i = getViewCount();
      for (int j = 0; j < i; j++)
      {
        View localView = getView(j);
        if (getRowsOccupied(localView) > 1)
        {
          multiRowCells = true;
          l3 = Math.max((int)localView.getMaximumSpan(paramInt), l3);
        }
        else
        {
          l1 = Math.max((int)localView.getMinimumSpan(paramInt), l1);
          l2 = Math.max((int)localView.getPreferredSpan(paramInt), l2);
          l3 = Math.max((int)localView.getMaximumSpan(paramInt), l3);
        }
      }
      if (paramSizeRequirements == null)
      {
        paramSizeRequirements = new SizeRequirements();
        alignment = 0.5F;
      }
      preferred = ((int)l2);
      minimum = ((int)l1);
      maximum = ((int)l3);
      return paramSizeRequirements;
    }
    
    protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      int i = 0;
      int j = getViewCount();
      for (int k = 0; k < j; k++)
      {
        View localView = getView(k);
        if ((!skipComments) || ((localView instanceof TableView.CellView)))
        {
          while (isFilled(i)) {
            i++;
          }
          int m = getColumnsOccupied(localView);
          paramArrayOfInt2[k] = columnSpans[i];
          paramArrayOfInt1[k] = columnOffsets[i];
          if (m > 1)
          {
            int n = columnSpans.length;
            for (int i1 = 1; i1 < m; i1++) {
              if (i + i1 < n)
              {
                paramArrayOfInt2[k] += columnSpans[(i + i1)];
                paramArrayOfInt2[k] += cellSpacing;
              }
            }
            i += m - 1;
          }
          i++;
        }
      }
    }
    
    protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      super.layoutMinorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
      int i = 0;
      int j = getViewCount();
      int k = 0;
      while (k < j)
      {
        View localView = getView(k);
        while (isFilled(i)) {
          i++;
        }
        int m = getColumnsOccupied(localView);
        int n = getRowsOccupied(localView);
        if (n > 1)
        {
          int i1 = rowIndex;
          int i2 = Math.min(rowIndex + n - 1, getRowCount() - 1);
          paramArrayOfInt2[k] = getMultiRowSpan(i1, i2);
        }
        if (m > 1) {
          i += m - 1;
        }
        k++;
        i++;
      }
    }
    
    public int getResizeWeight(int paramInt)
    {
      return 1;
    }
    
    protected View getViewAtPosition(int paramInt, Rectangle paramRectangle)
    {
      int i = getViewCount();
      for (int j = 0; j < i; j++)
      {
        View localView2 = getView(j);
        int k = localView2.getStartOffset();
        int m = localView2.getEndOffset();
        if ((paramInt >= k) && (paramInt < m))
        {
          if (paramRectangle != null) {
            childAllocation(j, paramRectangle);
          }
          return localView2;
        }
      }
      if (paramInt == getEndOffset())
      {
        View localView1 = getView(i - 1);
        if (paramRectangle != null) {
          childAllocation(i - 1, paramRectangle);
        }
        return localView1;
      }
      return null;
    }
    
    void setPropertiesFromAttributes()
    {
      StyleSheet localStyleSheet = getStyleSheet();
      attr = localStyleSheet.getViewAttributes(this);
      painter = localStyleSheet.getBoxPainter(attr);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\TableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */