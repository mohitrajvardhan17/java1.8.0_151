package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Date;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTable.DropLocation;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class SynthTableUI
  extends BasicTableUI
  implements SynthUI, PropertyChangeListener
{
  private SynthStyle style;
  private boolean useTableColors;
  private boolean useUIBorder;
  private Color alternateColor;
  private TableCellRenderer dateRenderer;
  private TableCellRenderer numberRenderer;
  private TableCellRenderer doubleRender;
  private TableCellRenderer floatRenderer;
  private TableCellRenderer iconRenderer;
  private TableCellRenderer imageIconRenderer;
  private TableCellRenderer booleanRenderer;
  private TableCellRenderer objectRenderer;
  
  public SynthTableUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthTableUI();
  }
  
  protected void installDefaults()
  {
    dateRenderer = installRendererIfPossible(Date.class, null);
    numberRenderer = installRendererIfPossible(Number.class, null);
    doubleRender = installRendererIfPossible(Double.class, null);
    floatRenderer = installRendererIfPossible(Float.class, null);
    iconRenderer = installRendererIfPossible(Icon.class, null);
    imageIconRenderer = installRendererIfPossible(ImageIcon.class, null);
    booleanRenderer = installRendererIfPossible(Boolean.class, new SynthBooleanTableCellRenderer());
    objectRenderer = installRendererIfPossible(Object.class, new SynthTableCellRenderer(null));
    updateStyle(table);
  }
  
  private TableCellRenderer installRendererIfPossible(Class paramClass, TableCellRenderer paramTableCellRenderer)
  {
    TableCellRenderer localTableCellRenderer = table.getDefaultRenderer(paramClass);
    if ((localTableCellRenderer instanceof UIResource)) {
      table.setDefaultRenderer(paramClass, paramTableCellRenderer);
    }
    return localTableCellRenderer;
  }
  
  private void updateStyle(JTable paramJTable)
  {
    SynthContext localSynthContext = getContext(paramJTable, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      localSynthContext.setComponentState(513);
      Color localColor1 = table.getSelectionBackground();
      if ((localColor1 == null) || ((localColor1 instanceof UIResource))) {
        table.setSelectionBackground(style.getColor(localSynthContext, ColorType.TEXT_BACKGROUND));
      }
      Color localColor2 = table.getSelectionForeground();
      if ((localColor2 == null) || ((localColor2 instanceof UIResource))) {
        table.setSelectionForeground(style.getColor(localSynthContext, ColorType.TEXT_FOREGROUND));
      }
      localSynthContext.setComponentState(1);
      Color localColor3 = table.getGridColor();
      if ((localColor3 == null) || ((localColor3 instanceof UIResource)))
      {
        localColor3 = (Color)style.get(localSynthContext, "Table.gridColor");
        if (localColor3 == null) {
          localColor3 = style.getColor(localSynthContext, ColorType.FOREGROUND);
        }
        table.setGridColor(localColor3 == null ? new ColorUIResource(Color.GRAY) : localColor3);
      }
      useTableColors = style.getBoolean(localSynthContext, "Table.rendererUseTableColors", true);
      useUIBorder = style.getBoolean(localSynthContext, "Table.rendererUseUIBorder", true);
      Object localObject = style.get(localSynthContext, "Table.rowHeight");
      if (localObject != null) {
        LookAndFeel.installProperty(table, "rowHeight", localObject);
      }
      boolean bool = style.getBoolean(localSynthContext, "Table.showGrid", true);
      if (!bool) {
        table.setShowGrid(false);
      }
      Dimension localDimension = table.getIntercellSpacing();
      if (localDimension != null) {
        localDimension = (Dimension)style.get(localSynthContext, "Table.intercellSpacing");
      }
      alternateColor = ((Color)style.get(localSynthContext, "Table.alternateRowColor"));
      if (localDimension != null) {
        table.setIntercellSpacing(localDimension);
      }
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    localSynthContext.dispose();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    table.addPropertyChangeListener(this);
  }
  
  protected void uninstallDefaults()
  {
    table.setDefaultRenderer(Date.class, dateRenderer);
    table.setDefaultRenderer(Number.class, numberRenderer);
    table.setDefaultRenderer(Double.class, doubleRender);
    table.setDefaultRenderer(Float.class, floatRenderer);
    table.setDefaultRenderer(Icon.class, iconRenderer);
    table.setDefaultRenderer(ImageIcon.class, imageIconRenderer);
    table.setDefaultRenderer(Boolean.class, booleanRenderer);
    table.setDefaultRenderer(Object.class, objectRenderer);
    if ((table.getTransferHandler() instanceof UIResource)) {
      table.setTransferHandler(null);
    }
    SynthContext localSynthContext = getContext(table, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  protected void uninstallListeners()
  {
    table.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintTableBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintTableBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    Rectangle localRectangle1 = paramGraphics.getClipBounds();
    Rectangle localRectangle2 = table.getBounds();
    x = (y = 0);
    if ((table.getRowCount() <= 0) || (table.getColumnCount() <= 0) || (!localRectangle2.intersects(localRectangle1)))
    {
      paintDropLines(paramSynthContext, paramGraphics);
      return;
    }
    boolean bool = table.getComponentOrientation().isLeftToRight();
    Point localPoint1 = localRectangle1.getLocation();
    Point localPoint2 = new Point(x + width - 1, y + height - 1);
    int i = table.rowAtPoint(localPoint1);
    int j = table.rowAtPoint(localPoint2);
    if (i == -1) {
      i = 0;
    }
    if (j == -1) {
      j = table.getRowCount() - 1;
    }
    int k = table.columnAtPoint(bool ? localPoint1 : localPoint2);
    int m = table.columnAtPoint(bool ? localPoint2 : localPoint1);
    if (k == -1) {
      k = 0;
    }
    if (m == -1) {
      m = table.getColumnCount() - 1;
    }
    paintCells(paramSynthContext, paramGraphics, i, j, k, m);
    paintGrid(paramSynthContext, paramGraphics, i, j, k, m);
    paintDropLines(paramSynthContext, paramGraphics);
  }
  
  private void paintDropLines(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    JTable.DropLocation localDropLocation = table.getDropLocation();
    if (localDropLocation == null) {
      return;
    }
    Color localColor1 = (Color)style.get(paramSynthContext, "Table.dropLineColor");
    Color localColor2 = (Color)style.get(paramSynthContext, "Table.dropLineShortColor");
    if ((localColor1 == null) && (localColor2 == null)) {
      return;
    }
    Rectangle localRectangle = getHDropLineRect(localDropLocation);
    int i;
    int j;
    if (localRectangle != null)
    {
      i = x;
      j = width;
      if (localColor1 != null)
      {
        extendRect(localRectangle, true);
        paramGraphics.setColor(localColor1);
        paramGraphics.fillRect(x, y, width, height);
      }
      if ((!localDropLocation.isInsertColumn()) && (localColor2 != null))
      {
        paramGraphics.setColor(localColor2);
        paramGraphics.fillRect(i, y, j, height);
      }
    }
    localRectangle = getVDropLineRect(localDropLocation);
    if (localRectangle != null)
    {
      i = y;
      j = height;
      if (localColor1 != null)
      {
        extendRect(localRectangle, false);
        paramGraphics.setColor(localColor1);
        paramGraphics.fillRect(x, y, width, height);
      }
      if ((!localDropLocation.isInsertRow()) && (localColor2 != null))
      {
        paramGraphics.setColor(localColor2);
        paramGraphics.fillRect(x, i, width, j);
      }
    }
  }
  
  private Rectangle getHDropLineRect(JTable.DropLocation paramDropLocation)
  {
    if (!paramDropLocation.isInsertRow()) {
      return null;
    }
    int i = paramDropLocation.getRow();
    int j = paramDropLocation.getColumn();
    if (j >= table.getColumnCount()) {
      j--;
    }
    Rectangle localRectangle1 = table.getCellRect(i, j, true);
    if (i >= table.getRowCount())
    {
      i--;
      Rectangle localRectangle2 = table.getCellRect(i, j, true);
      y += height;
    }
    if (y == 0) {
      y = -1;
    } else {
      y -= 2;
    }
    height = 3;
    return localRectangle1;
  }
  
  private Rectangle getVDropLineRect(JTable.DropLocation paramDropLocation)
  {
    if (!paramDropLocation.isInsertColumn()) {
      return null;
    }
    boolean bool = table.getComponentOrientation().isLeftToRight();
    int i = paramDropLocation.getColumn();
    Rectangle localRectangle = table.getCellRect(paramDropLocation.getRow(), i, true);
    if (i >= table.getColumnCount())
    {
      i--;
      localRectangle = table.getCellRect(paramDropLocation.getRow(), i, true);
      if (bool) {
        x += width;
      }
    }
    else if (!bool)
    {
      x += width;
    }
    if (x == 0) {
      x = -1;
    } else {
      x -= 2;
    }
    width = 3;
    return localRectangle;
  }
  
  private Rectangle extendRect(Rectangle paramRectangle, boolean paramBoolean)
  {
    if (paramRectangle == null) {
      return paramRectangle;
    }
    if (paramBoolean)
    {
      x = 0;
      width = table.getWidth();
    }
    else
    {
      y = 0;
      if (table.getRowCount() != 0)
      {
        Rectangle localRectangle = table.getCellRect(table.getRowCount() - 1, 0, true);
        height = (y + height);
      }
      else
      {
        height = table.getHeight();
      }
    }
    return paramRectangle;
  }
  
  private void paintGrid(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGraphics.setColor(table.getGridColor());
    Rectangle localRectangle1 = table.getCellRect(paramInt1, paramInt3, true);
    Rectangle localRectangle2 = table.getCellRect(paramInt2, paramInt4, true);
    Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
    SynthGraphicsUtils localSynthGraphicsUtils = paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext);
    int j;
    int k;
    if (table.getShowHorizontalLines())
    {
      int i = x + width;
      j = y;
      for (k = paramInt1; k <= paramInt2; k++)
      {
        j += table.getRowHeight(k);
        localSynthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, x, j - 1, i - 1, j - 1);
      }
    }
    if (table.getShowVerticalLines())
    {
      TableColumnModel localTableColumnModel = table.getColumnModel();
      j = y + height;
      int m;
      int n;
      if (table.getComponentOrientation().isLeftToRight())
      {
        k = x;
        for (m = paramInt3; m <= paramInt4; m++)
        {
          n = localTableColumnModel.getColumn(m).getWidth();
          k += n;
          localSynthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, k - 1, 0, k - 1, j - 1);
        }
      }
      else
      {
        k = x;
        for (m = paramInt4; m >= paramInt3; m--)
        {
          n = localTableColumnModel.getColumn(m).getWidth();
          k += n;
          localSynthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, k - 1, 0, k - 1, j - 1);
        }
      }
    }
  }
  
  private int viewIndexForColumn(TableColumn paramTableColumn)
  {
    TableColumnModel localTableColumnModel = table.getColumnModel();
    for (int i = 0; i < localTableColumnModel.getColumnCount(); i++) {
      if (localTableColumnModel.getColumn(i) == paramTableColumn) {
        return i;
      }
    }
    return -1;
  }
  
  private void paintCells(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    JTableHeader localJTableHeader = table.getTableHeader();
    TableColumn localTableColumn1 = localJTableHeader == null ? null : localJTableHeader.getDraggedColumn();
    TableColumnModel localTableColumnModel = table.getColumnModel();
    int i = localTableColumnModel.getColumnMargin();
    int k;
    Rectangle localRectangle;
    int m;
    TableColumn localTableColumn2;
    int j;
    if (table.getComponentOrientation().isLeftToRight()) {
      for (k = paramInt1; k <= paramInt2; k++)
      {
        localRectangle = table.getCellRect(k, paramInt3, false);
        for (m = paramInt3; m <= paramInt4; m++)
        {
          localTableColumn2 = localTableColumnModel.getColumn(m);
          j = localTableColumn2.getWidth();
          width = (j - i);
          if (localTableColumn2 != localTableColumn1) {
            paintCell(paramSynthContext, paramGraphics, localRectangle, k, m);
          }
          x += j;
        }
      }
    } else {
      for (k = paramInt1; k <= paramInt2; k++)
      {
        localRectangle = table.getCellRect(k, paramInt3, false);
        localTableColumn2 = localTableColumnModel.getColumn(paramInt3);
        if (localTableColumn2 != localTableColumn1)
        {
          j = localTableColumn2.getWidth();
          width = (j - i);
          paintCell(paramSynthContext, paramGraphics, localRectangle, k, paramInt3);
        }
        for (m = paramInt3 + 1; m <= paramInt4; m++)
        {
          localTableColumn2 = localTableColumnModel.getColumn(m);
          j = localTableColumn2.getWidth();
          width = (j - i);
          x -= j;
          if (localTableColumn2 != localTableColumn1) {
            paintCell(paramSynthContext, paramGraphics, localRectangle, k, m);
          }
        }
      }
    }
    if (localTableColumn1 != null) {
      paintDraggedArea(paramSynthContext, paramGraphics, paramInt1, paramInt2, localTableColumn1, localJTableHeader.getDraggedDistance());
    }
    rendererPane.removeAll();
  }
  
  private void paintDraggedArea(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, TableColumn paramTableColumn, int paramInt3)
  {
    int i = viewIndexForColumn(paramTableColumn);
    Rectangle localRectangle1 = table.getCellRect(paramInt1, i, true);
    Rectangle localRectangle2 = table.getCellRect(paramInt2, i, true);
    Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
    paramGraphics.setColor(table.getParent().getBackground());
    paramGraphics.fillRect(x, y, width, height);
    x += paramInt3;
    paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.BACKGROUND));
    paramGraphics.fillRect(x, y, width, height);
    SynthGraphicsUtils localSynthGraphicsUtils = paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext);
    int n;
    if (table.getShowVerticalLines())
    {
      paramGraphics.setColor(table.getGridColor());
      j = x;
      int k = y;
      int m = j + width - 1;
      n = k + height - 1;
      localSynthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, j - 1, k, j - 1, n);
      localSynthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, m, k, m, n);
    }
    for (int j = paramInt1; j <= paramInt2; j++)
    {
      Rectangle localRectangle4 = table.getCellRect(j, i, false);
      x += paramInt3;
      paintCell(paramSynthContext, paramGraphics, localRectangle4, j, i);
      if (table.getShowHorizontalLines())
      {
        paramGraphics.setColor(table.getGridColor());
        Rectangle localRectangle5 = table.getCellRect(j, i, true);
        x += paramInt3;
        n = x;
        int i1 = y;
        int i2 = n + width - 1;
        int i3 = i1 + height - 1;
        localSynthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, n, i3, i2, i3);
      }
    }
  }
  
  private void paintCell(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    Object localObject;
    if ((table.isEditing()) && (table.getEditingRow() == paramInt1) && (table.getEditingColumn() == paramInt2))
    {
      localObject = table.getEditorComponent();
      ((Component)localObject).setBounds(paramRectangle);
      ((Component)localObject).validate();
    }
    else
    {
      localObject = table.getCellRenderer(paramInt1, paramInt2);
      Component localComponent = table.prepareRenderer((TableCellRenderer)localObject, paramInt1, paramInt2);
      Color localColor = localComponent.getBackground();
      if (((localColor == null) || ((localColor instanceof UIResource)) || ((localComponent instanceof SynthBooleanTableCellRenderer))) && (!table.isCellSelected(paramInt1, paramInt2)) && (alternateColor != null) && (paramInt1 % 2 != 0)) {
        localComponent.setBackground(alternateColor);
      }
      rendererPane.paintComponent(paramGraphics, localComponent, table, x, y, width, height, true);
    }
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JTable)paramPropertyChangeEvent.getSource());
    }
  }
  
  private class SynthBooleanTableCellRenderer
    extends JCheckBox
    implements TableCellRenderer
  {
    private boolean isRowSelected;
    
    public SynthBooleanTableCellRenderer()
    {
      setHorizontalAlignment(0);
      setName("Table.cellRenderer");
    }
    
    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      isRowSelected = paramBoolean1;
      if (paramBoolean1)
      {
        setForeground(unwrap(paramJTable.getSelectionForeground()));
        setBackground(unwrap(paramJTable.getSelectionBackground()));
      }
      else
      {
        setForeground(unwrap(paramJTable.getForeground()));
        setBackground(unwrap(paramJTable.getBackground()));
      }
      setSelected((paramObject != null) && (((Boolean)paramObject).booleanValue()));
      return this;
    }
    
    private Color unwrap(Color paramColor)
    {
      if ((paramColor instanceof UIResource)) {
        return new Color(paramColor.getRGB());
      }
      return paramColor;
    }
    
    public boolean isOpaque()
    {
      return isRowSelected ? true : super.isOpaque();
    }
  }
  
  private class SynthTableCellRenderer
    extends DefaultTableCellRenderer
  {
    private Object numberFormat;
    private Object dateFormat;
    private boolean opaque;
    
    private SynthTableCellRenderer() {}
    
    public void setOpaque(boolean paramBoolean)
    {
      opaque = paramBoolean;
    }
    
    public boolean isOpaque()
    {
      return opaque;
    }
    
    public String getName()
    {
      String str = super.getName();
      if (str == null) {
        return "Table.cellRenderer";
      }
      return str;
    }
    
    public void setBorder(Border paramBorder)
    {
      if ((useUIBorder) || ((paramBorder instanceof SynthBorder))) {
        super.setBorder(paramBorder);
      }
    }
    
    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      if ((!useTableColors) && ((paramBoolean1) || (paramBoolean2))) {
        SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), paramBoolean1, paramBoolean2, paramJTable.isEnabled(), false);
      } else {
        SynthLookAndFeel.resetSelectedUI();
      }
      super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
      setIcon(null);
      if (paramJTable != null) {
        configureValue(paramObject, paramJTable.getColumnClass(paramInt2));
      }
      return this;
    }
    
    private void configureValue(Object paramObject, Class paramClass)
    {
      if ((paramClass == Object.class) || (paramClass == null))
      {
        setHorizontalAlignment(10);
      }
      else if ((paramClass == Float.class) || (paramClass == Double.class))
      {
        if (numberFormat == null) {
          numberFormat = NumberFormat.getInstance();
        }
        setHorizontalAlignment(11);
        setText(paramObject == null ? "" : ((NumberFormat)numberFormat).format(paramObject));
      }
      else if (paramClass == Number.class)
      {
        setHorizontalAlignment(11);
      }
      else if ((paramClass == Icon.class) || (paramClass == ImageIcon.class))
      {
        setHorizontalAlignment(0);
        setIcon((paramObject instanceof Icon) ? (Icon)paramObject : null);
        setText("");
      }
      else if (paramClass == Date.class)
      {
        if (dateFormat == null) {
          dateFormat = DateFormat.getDateInstance();
        }
        setHorizontalAlignment(10);
        setText(paramObject == null ? "" : ((Format)dateFormat).format(paramObject));
      }
      else
      {
        configureValue(paramObject, paramClass.getSuperclass());
      }
    }
    
    public void paint(Graphics paramGraphics)
    {
      super.paint(paramGraphics);
      SynthLookAndFeel.resetSelectedUI();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthTableUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */