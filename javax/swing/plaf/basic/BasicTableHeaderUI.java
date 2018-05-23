package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.CellRendererPane;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTableHeaderUI
  extends TableHeaderUI
{
  private static Cursor resizeCursor = Cursor.getPredefinedCursor(11);
  protected JTableHeader header;
  protected CellRendererPane rendererPane;
  protected MouseInputListener mouseInputListener;
  private int rolloverColumn = -1;
  private int selectedColumnIndex = 0;
  private static FocusListener focusListener = new FocusListener()
  {
    public void focusGained(FocusEvent paramAnonymousFocusEvent)
    {
      repaintHeader(paramAnonymousFocusEvent.getSource());
    }
    
    public void focusLost(FocusEvent paramAnonymousFocusEvent)
    {
      repaintHeader(paramAnonymousFocusEvent.getSource());
    }
    
    private void repaintHeader(Object paramAnonymousObject)
    {
      if ((paramAnonymousObject instanceof JTableHeader))
      {
        JTableHeader localJTableHeader = (JTableHeader)paramAnonymousObject;
        BasicTableHeaderUI localBasicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(localJTableHeader.getUI(), BasicTableHeaderUI.class);
        if (localBasicTableHeaderUI == null) {
          return;
        }
        localJTableHeader.repaint(localJTableHeader.getHeaderRect(localBasicTableHeaderUI.getSelectedColumnIndex()));
      }
    }
  };
  
  public BasicTableHeaderUI() {}
  
  protected MouseInputListener createMouseInputListener()
  {
    return new MouseInputHandler();
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicTableHeaderUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    header = ((JTableHeader)paramJComponent);
    rendererPane = new CellRendererPane();
    header.add(rendererPane);
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installColorsAndFont(header, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
    LookAndFeel.installProperty(header, "opaque", Boolean.TRUE);
  }
  
  protected void installListeners()
  {
    mouseInputListener = createMouseInputListener();
    header.addMouseListener(mouseInputListener);
    header.addMouseMotionListener(mouseInputListener);
    header.addFocusListener(focusListener);
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = (InputMap)DefaultLookup.get(header, this, "TableHeader.ancestorInputMap");
    SwingUtilities.replaceUIInputMap(header, 1, localInputMap);
    LazyActionMap.installLazyActionMap(header, BasicTableHeaderUI.class, "TableHeader.actionMap");
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    header.remove(rendererPane);
    rendererPane = null;
    header = null;
  }
  
  protected void uninstallDefaults() {}
  
  protected void uninstallListeners()
  {
    header.removeMouseListener(mouseInputListener);
    header.removeMouseMotionListener(mouseInputListener);
    mouseInputListener = null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIInputMap(header, 0, null);
    SwingUtilities.replaceUIActionMap(header, null);
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("toggleSortOrder"));
    paramLazyActionMap.put(new Actions("selectColumnToLeft"));
    paramLazyActionMap.put(new Actions("selectColumnToRight"));
    paramLazyActionMap.put(new Actions("moveColumnLeft"));
    paramLazyActionMap.put(new Actions("moveColumnRight"));
    paramLazyActionMap.put(new Actions("resizeLeft"));
    paramLazyActionMap.put(new Actions("resizeRight"));
    paramLazyActionMap.put(new Actions("focusTable"));
  }
  
  protected int getRolloverColumn()
  {
    return rolloverColumn;
  }
  
  protected void rolloverColumnUpdated(int paramInt1, int paramInt2) {}
  
  private void updateRolloverColumn(MouseEvent paramMouseEvent)
  {
    if ((header.getDraggedColumn() == null) && (header.contains(paramMouseEvent.getPoint())))
    {
      int i = header.columnAtPoint(paramMouseEvent.getPoint());
      if (i != rolloverColumn)
      {
        int j = rolloverColumn;
        rolloverColumn = i;
        rolloverColumnUpdated(j, rolloverColumn);
      }
    }
  }
  
  private int selectNextColumn(boolean paramBoolean)
  {
    int i = getSelectedColumnIndex();
    if (i < header.getColumnModel().getColumnCount() - 1)
    {
      i++;
      if (paramBoolean) {
        selectColumn(i);
      }
    }
    return i;
  }
  
  private int selectPreviousColumn(boolean paramBoolean)
  {
    int i = getSelectedColumnIndex();
    if (i > 0)
    {
      i--;
      if (paramBoolean) {
        selectColumn(i);
      }
    }
    return i;
  }
  
  void selectColumn(int paramInt)
  {
    selectColumn(paramInt, true);
  }
  
  void selectColumn(int paramInt, boolean paramBoolean)
  {
    Rectangle localRectangle = header.getHeaderRect(selectedColumnIndex);
    header.repaint(localRectangle);
    selectedColumnIndex = paramInt;
    localRectangle = header.getHeaderRect(paramInt);
    header.repaint(localRectangle);
    if (paramBoolean) {
      scrollToColumn(paramInt);
    }
  }
  
  private void scrollToColumn(int paramInt)
  {
    Container localContainer;
    JTable localJTable;
    if ((header.getParent() == null) || ((localContainer = header.getParent().getParent()) == null) || (!(localContainer instanceof JScrollPane)) || ((localJTable = header.getTable()) == null)) {
      return;
    }
    Rectangle localRectangle1 = localJTable.getVisibleRect();
    Rectangle localRectangle2 = localJTable.getCellRect(0, paramInt, true);
    x = x;
    width = width;
    localJTable.scrollRectToVisible(localRectangle1);
  }
  
  private int getSelectedColumnIndex()
  {
    int i = header.getColumnModel().getColumnCount();
    if ((selectedColumnIndex >= i) && (i > 0)) {
      selectedColumnIndex = (i - 1);
    }
    return selectedColumnIndex;
  }
  
  private static boolean canResize(TableColumn paramTableColumn, JTableHeader paramJTableHeader)
  {
    return (paramTableColumn != null) && (paramJTableHeader.getResizingAllowed()) && (paramTableColumn.getResizable());
  }
  
  private int changeColumnWidth(TableColumn paramTableColumn, JTableHeader paramJTableHeader, int paramInt1, int paramInt2)
  {
    paramTableColumn.setWidth(paramInt2);
    Container localContainer;
    JTable localJTable;
    if ((paramJTableHeader.getParent() == null) || ((localContainer = paramJTableHeader.getParent().getParent()) == null) || (!(localContainer instanceof JScrollPane)) || ((localJTable = paramJTableHeader.getTable()) == null)) {
      return 0;
    }
    if ((!localContainer.getComponentOrientation().isLeftToRight()) && (!paramJTableHeader.getComponentOrientation().isLeftToRight()))
    {
      JViewport localJViewport = ((JScrollPane)localContainer).getViewport();
      int i = localJViewport.getWidth();
      int j = paramInt2 - paramInt1;
      int k = localJTable.getWidth() + j;
      Dimension localDimension = localJTable.getSize();
      width += j;
      localJTable.setSize(localDimension);
      if ((k >= i) && (localJTable.getAutoResizeMode() == 0))
      {
        Point localPoint = localJViewport.getViewPosition();
        x = Math.max(0, Math.min(k - i, x + j));
        localJViewport.setViewPosition(localPoint);
        return j;
      }
    }
    return 0;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    int i = -1;
    TableColumnModel localTableColumnModel = header.getColumnModel();
    for (int j = 0; j < localTableColumnModel.getColumnCount(); j++)
    {
      TableColumn localTableColumn = localTableColumnModel.getColumn(j);
      Component localComponent = getHeaderRenderer(j);
      Dimension localDimension = localComponent.getPreferredSize();
      int k = localComponent.getBaseline(width, paramInt2);
      if (k >= 0) {
        if (i == -1)
        {
          i = k;
        }
        else if (i != k)
        {
          i = -1;
          break;
        }
      }
    }
    return i;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (header.getColumnModel().getColumnCount() <= 0) {
      return;
    }
    boolean bool = header.getComponentOrientation().isLeftToRight();
    Rectangle localRectangle1 = paramGraphics.getClipBounds();
    Point localPoint1 = localRectangle1.getLocation();
    Point localPoint2 = new Point(x + width - 1, y);
    TableColumnModel localTableColumnModel = header.getColumnModel();
    int i = header.columnAtPoint(bool ? localPoint1 : localPoint2);
    int j = header.columnAtPoint(bool ? localPoint2 : localPoint1);
    if (i == -1) {
      i = 0;
    }
    if (j == -1) {
      j = localTableColumnModel.getColumnCount() - 1;
    }
    TableColumn localTableColumn1 = header.getDraggedColumn();
    Rectangle localRectangle2 = header.getHeaderRect(bool ? i : j);
    int m;
    TableColumn localTableColumn2;
    int k;
    if (bool) {
      for (m = i; m <= j; m++)
      {
        localTableColumn2 = localTableColumnModel.getColumn(m);
        k = localTableColumn2.getWidth();
        width = k;
        if (localTableColumn2 != localTableColumn1) {
          paintCell(paramGraphics, localRectangle2, m);
        }
        x += k;
      }
    } else {
      for (m = j; m >= i; m--)
      {
        localTableColumn2 = localTableColumnModel.getColumn(m);
        k = localTableColumn2.getWidth();
        width = k;
        if (localTableColumn2 != localTableColumn1) {
          paintCell(paramGraphics, localRectangle2, m);
        }
        x += k;
      }
    }
    if (localTableColumn1 != null)
    {
      m = viewIndexForColumn(localTableColumn1);
      Rectangle localRectangle3 = header.getHeaderRect(m);
      paramGraphics.setColor(header.getParent().getBackground());
      paramGraphics.fillRect(x, y, width, height);
      x += header.getDraggedDistance();
      paramGraphics.setColor(header.getBackground());
      paramGraphics.fillRect(x, y, width, height);
      paintCell(paramGraphics, localRectangle3, m);
    }
    rendererPane.removeAll();
  }
  
  private Component getHeaderRenderer(int paramInt)
  {
    TableColumn localTableColumn = header.getColumnModel().getColumn(paramInt);
    TableCellRenderer localTableCellRenderer = localTableColumn.getHeaderRenderer();
    if (localTableCellRenderer == null) {
      localTableCellRenderer = header.getDefaultRenderer();
    }
    boolean bool = (!header.isPaintingForPrint()) && (paramInt == getSelectedColumnIndex()) && (header.hasFocus());
    return localTableCellRenderer.getTableCellRendererComponent(header.getTable(), localTableColumn.getHeaderValue(), false, bool, -1, paramInt);
  }
  
  private void paintCell(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    Component localComponent = getHeaderRenderer(paramInt);
    rendererPane.paintComponent(paramGraphics, localComponent, header, x, y, width, height, true);
  }
  
  private int viewIndexForColumn(TableColumn paramTableColumn)
  {
    TableColumnModel localTableColumnModel = header.getColumnModel();
    for (int i = 0; i < localTableColumnModel.getColumnCount(); i++) {
      if (localTableColumnModel.getColumn(i) == paramTableColumn) {
        return i;
      }
    }
    return -1;
  }
  
  private int getHeaderHeight()
  {
    int i = 0;
    int j = 0;
    TableColumnModel localTableColumnModel = header.getColumnModel();
    for (int k = 0; k < localTableColumnModel.getColumnCount(); k++)
    {
      TableColumn localTableColumn = localTableColumnModel.getColumn(k);
      int m = localTableColumn.getHeaderRenderer() == null ? 1 : 0;
      if ((m == 0) || (j == 0))
      {
        Component localComponent = getHeaderRenderer(k);
        int n = getPreferredSizeheight;
        i = Math.max(i, n);
        if ((m != 0) && (n > 0))
        {
          Object localObject = localTableColumn.getHeaderValue();
          if (localObject != null)
          {
            localObject = localObject.toString();
            if ((localObject != null) && (!localObject.equals(""))) {
              j = 1;
            }
          }
        }
      }
    }
    return i;
  }
  
  private Dimension createHeaderSize(long paramLong)
  {
    if (paramLong > 2147483647L) {
      paramLong = 2147483647L;
    }
    return new Dimension((int)paramLong, getHeaderHeight());
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    long l = 0L;
    Enumeration localEnumeration = header.getColumnModel().getColumns();
    while (localEnumeration.hasMoreElements())
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      l += localTableColumn.getMinWidth();
    }
    return createHeaderSize(l);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    long l = 0L;
    Enumeration localEnumeration = header.getColumnModel().getColumns();
    while (localEnumeration.hasMoreElements())
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      l += localTableColumn.getPreferredWidth();
    }
    return createHeaderSize(l);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    long l = 0L;
    Enumeration localEnumeration = header.getColumnModel().getColumns();
    while (localEnumeration.hasMoreElements())
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      l += localTableColumn.getMaxWidth();
    }
    return createHeaderSize(l);
  }
  
  private static class Actions
    extends UIAction
  {
    public static final String TOGGLE_SORT_ORDER = "toggleSortOrder";
    public static final String SELECT_COLUMN_TO_LEFT = "selectColumnToLeft";
    public static final String SELECT_COLUMN_TO_RIGHT = "selectColumnToRight";
    public static final String MOVE_COLUMN_LEFT = "moveColumnLeft";
    public static final String MOVE_COLUMN_RIGHT = "moveColumnRight";
    public static final String RESIZE_LEFT = "resizeLeft";
    public static final String RESIZE_RIGHT = "resizeRight";
    public static final String FOCUS_TABLE = "focusTable";
    
    public Actions(String paramString)
    {
      super();
    }
    
    public boolean isEnabled(Object paramObject)
    {
      if ((paramObject instanceof JTableHeader))
      {
        JTableHeader localJTableHeader = (JTableHeader)paramObject;
        TableColumnModel localTableColumnModel = localJTableHeader.getColumnModel();
        if (localTableColumnModel.getColumnCount() <= 0) {
          return false;
        }
        String str = getName();
        BasicTableHeaderUI localBasicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(localJTableHeader.getUI(), BasicTableHeaderUI.class);
        if (localBasicTableHeaderUI != null)
        {
          if (str == "moveColumnLeft") {
            return (localJTableHeader.getReorderingAllowed()) && (maybeMoveColumn(true, localJTableHeader, localBasicTableHeaderUI, false));
          }
          if (str == "moveColumnRight") {
            return (localJTableHeader.getReorderingAllowed()) && (maybeMoveColumn(false, localJTableHeader, localBasicTableHeaderUI, false));
          }
          if ((str == "resizeLeft") || (str == "resizeRight")) {
            return BasicTableHeaderUI.canResize(localTableColumnModel.getColumn(BasicTableHeaderUI.access$000(localBasicTableHeaderUI)), localJTableHeader);
          }
          if (str == "focusTable") {
            return localJTableHeader.getTable() != null;
          }
        }
      }
      return true;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTableHeader localJTableHeader = (JTableHeader)paramActionEvent.getSource();
      BasicTableHeaderUI localBasicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(localJTableHeader.getUI(), BasicTableHeaderUI.class);
      if (localBasicTableHeaderUI == null) {
        return;
      }
      String str = getName();
      JTable localJTable;
      if ("toggleSortOrder" == str)
      {
        localJTable = localJTableHeader.getTable();
        RowSorter localRowSorter = localJTable == null ? null : localJTable.getRowSorter();
        if (localRowSorter != null)
        {
          int i = localBasicTableHeaderUI.getSelectedColumnIndex();
          i = localJTable.convertColumnIndexToModel(i);
          localRowSorter.toggleSortOrder(i);
        }
      }
      else if ("selectColumnToLeft" == str)
      {
        if (localJTableHeader.getComponentOrientation().isLeftToRight()) {
          localBasicTableHeaderUI.selectPreviousColumn(true);
        } else {
          localBasicTableHeaderUI.selectNextColumn(true);
        }
      }
      else if ("selectColumnToRight" == str)
      {
        if (localJTableHeader.getComponentOrientation().isLeftToRight()) {
          localBasicTableHeaderUI.selectNextColumn(true);
        } else {
          localBasicTableHeaderUI.selectPreviousColumn(true);
        }
      }
      else if ("moveColumnLeft" == str)
      {
        moveColumn(true, localJTableHeader, localBasicTableHeaderUI);
      }
      else if ("moveColumnRight" == str)
      {
        moveColumn(false, localJTableHeader, localBasicTableHeaderUI);
      }
      else if ("resizeLeft" == str)
      {
        resize(true, localJTableHeader, localBasicTableHeaderUI);
      }
      else if ("resizeRight" == str)
      {
        resize(false, localJTableHeader, localBasicTableHeaderUI);
      }
      else if ("focusTable" == str)
      {
        localJTable = localJTableHeader.getTable();
        if (localJTable != null) {
          localJTable.requestFocusInWindow();
        }
      }
    }
    
    private void moveColumn(boolean paramBoolean, JTableHeader paramJTableHeader, BasicTableHeaderUI paramBasicTableHeaderUI)
    {
      maybeMoveColumn(paramBoolean, paramJTableHeader, paramBasicTableHeaderUI, true);
    }
    
    private boolean maybeMoveColumn(boolean paramBoolean1, JTableHeader paramJTableHeader, BasicTableHeaderUI paramBasicTableHeaderUI, boolean paramBoolean2)
    {
      int i = paramBasicTableHeaderUI.getSelectedColumnIndex();
      int j;
      if (paramJTableHeader.getComponentOrientation().isLeftToRight()) {
        j = paramBoolean1 ? paramBasicTableHeaderUI.selectPreviousColumn(paramBoolean2) : paramBasicTableHeaderUI.selectNextColumn(paramBoolean2);
      } else {
        j = paramBoolean1 ? paramBasicTableHeaderUI.selectNextColumn(paramBoolean2) : paramBasicTableHeaderUI.selectPreviousColumn(paramBoolean2);
      }
      if (j != i) {
        if (paramBoolean2) {
          paramJTableHeader.getColumnModel().moveColumn(i, j);
        } else {
          return true;
        }
      }
      return false;
    }
    
    private void resize(boolean paramBoolean, JTableHeader paramJTableHeader, BasicTableHeaderUI paramBasicTableHeaderUI)
    {
      int i = paramBasicTableHeaderUI.getSelectedColumnIndex();
      TableColumn localTableColumn = paramJTableHeader.getColumnModel().getColumn(i);
      paramJTableHeader.setResizingColumn(localTableColumn);
      int j = localTableColumn.getWidth();
      int k = j;
      if (paramJTableHeader.getComponentOrientation().isLeftToRight()) {
        k += (paramBoolean ? -1 : 1);
      } else {
        k += (paramBoolean ? 1 : -1);
      }
      paramBasicTableHeaderUI.changeColumnWidth(localTableColumn, paramJTableHeader, j, k);
    }
  }
  
  public class MouseInputHandler
    implements MouseInputListener
  {
    private int mouseXOffset;
    private Cursor otherCursor = BasicTableHeaderUI.resizeCursor;
    
    public MouseInputHandler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      if (!header.isEnabled()) {
        return;
      }
      if ((paramMouseEvent.getClickCount() % 2 == 1) && (SwingUtilities.isLeftMouseButton(paramMouseEvent)))
      {
        JTable localJTable = header.getTable();
        RowSorter localRowSorter;
        if ((localJTable != null) && ((localRowSorter = localJTable.getRowSorter()) != null))
        {
          int i = header.columnAtPoint(paramMouseEvent.getPoint());
          if (i != -1)
          {
            i = localJTable.convertColumnIndexToModel(i);
            localRowSorter.toggleSortOrder(i);
          }
        }
      }
    }
    
    private TableColumn getResizingColumn(Point paramPoint)
    {
      return getResizingColumn(paramPoint, header.columnAtPoint(paramPoint));
    }
    
    private TableColumn getResizingColumn(Point paramPoint, int paramInt)
    {
      if (paramInt == -1) {
        return null;
      }
      Rectangle localRectangle = header.getHeaderRect(paramInt);
      localRectangle.grow(-3, 0);
      if (localRectangle.contains(paramPoint)) {
        return null;
      }
      int i = x + width / 2;
      int j;
      if (header.getComponentOrientation().isLeftToRight()) {
        j = x < i ? paramInt - 1 : paramInt;
      } else {
        j = x < i ? paramInt : paramInt - 1;
      }
      if (j == -1) {
        return null;
      }
      return header.getColumnModel().getColumn(j);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (!header.isEnabled()) {
        return;
      }
      header.setDraggedColumn(null);
      header.setResizingColumn(null);
      header.setDraggedDistance(0);
      Point localPoint = paramMouseEvent.getPoint();
      TableColumnModel localTableColumnModel = header.getColumnModel();
      int i = header.columnAtPoint(localPoint);
      if (i != -1)
      {
        TableColumn localTableColumn1 = getResizingColumn(localPoint, i);
        if (BasicTableHeaderUI.canResize(localTableColumn1, header))
        {
          header.setResizingColumn(localTableColumn1);
          if (header.getComponentOrientation().isLeftToRight()) {
            mouseXOffset = (x - localTableColumn1.getWidth());
          } else {
            mouseXOffset = (x + localTableColumn1.getWidth());
          }
        }
        else if (header.getReorderingAllowed())
        {
          TableColumn localTableColumn2 = localTableColumnModel.getColumn(i);
          header.setDraggedColumn(localTableColumn2);
          mouseXOffset = x;
        }
      }
      if (header.getReorderingAllowed())
      {
        int j = rolloverColumn;
        rolloverColumn = -1;
        rolloverColumnUpdated(j, rolloverColumn);
      }
    }
    
    private void swapCursor()
    {
      Cursor localCursor = header.getCursor();
      header.setCursor(otherCursor);
      otherCursor = localCursor;
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      if (!header.isEnabled()) {
        return;
      }
      if (BasicTableHeaderUI.canResize(getResizingColumn(paramMouseEvent.getPoint()), header) != (header.getCursor() == BasicTableHeaderUI.resizeCursor)) {
        swapCursor();
      }
      BasicTableHeaderUI.this.updateRolloverColumn(paramMouseEvent);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (!header.isEnabled()) {
        return;
      }
      int i = paramMouseEvent.getX();
      TableColumn localTableColumn1 = header.getResizingColumn();
      TableColumn localTableColumn2 = header.getDraggedColumn();
      boolean bool = header.getComponentOrientation().isLeftToRight();
      int k;
      if (localTableColumn1 != null)
      {
        int j = localTableColumn1.getWidth();
        if (bool) {
          k = i - mouseXOffset;
        } else {
          k = mouseXOffset - i;
        }
        mouseXOffset += BasicTableHeaderUI.this.changeColumnWidth(localTableColumn1, header, j, k);
      }
      else if (localTableColumn2 != null)
      {
        TableColumnModel localTableColumnModel = header.getColumnModel();
        k = i - mouseXOffset;
        int m = k < 0 ? -1 : 1;
        int n = BasicTableHeaderUI.this.viewIndexForColumn(localTableColumn2);
        int i1 = n + (bool ? m : -m);
        if ((0 <= i1) && (i1 < localTableColumnModel.getColumnCount()))
        {
          int i2 = localTableColumnModel.getColumn(i1).getWidth();
          if (Math.abs(k) > i2 / 2)
          {
            mouseXOffset += m * i2;
            header.setDraggedDistance(k - m * i2);
            int i3 = SwingUtilities2.convertColumnIndexToModel(header.getColumnModel(), BasicTableHeaderUI.this.getSelectedColumnIndex());
            localTableColumnModel.moveColumn(n, i1);
            selectColumn(SwingUtilities2.convertColumnIndexToView(header.getColumnModel(), i3), false);
            return;
          }
        }
        setDraggedDistance(k, n);
      }
      BasicTableHeaderUI.this.updateRolloverColumn(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (!header.isEnabled()) {
        return;
      }
      setDraggedDistance(0, BasicTableHeaderUI.this.viewIndexForColumn(header.getDraggedColumn()));
      header.setResizingColumn(null);
      header.setDraggedColumn(null);
      BasicTableHeaderUI.this.updateRolloverColumn(paramMouseEvent);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      if (!header.isEnabled()) {
        return;
      }
      BasicTableHeaderUI.this.updateRolloverColumn(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      if (!header.isEnabled()) {
        return;
      }
      int i = rolloverColumn;
      rolloverColumn = -1;
      rolloverColumnUpdated(i, rolloverColumn);
    }
    
    private void setDraggedDistance(int paramInt1, int paramInt2)
    {
      header.setDraggedDistance(paramInt1);
      if (paramInt2 != -1) {
        header.getColumnModel().moveColumn(paramInt2, paramInt2);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicTableHeaderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */