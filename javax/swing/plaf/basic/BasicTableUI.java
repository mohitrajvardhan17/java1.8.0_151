package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellEditor;
import javax.swing.CellRendererPane;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTable.DropLocation;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.plaf.TableUI;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTableUI
  extends TableUI
{
  private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("Table.baselineComponent");
  protected JTable table;
  protected CellRendererPane rendererPane;
  protected KeyListener keyListener;
  protected FocusListener focusListener;
  protected MouseInputListener mouseInputListener;
  private Handler handler;
  private boolean isFileList = false;
  private static final TransferHandler defaultTransferHandler = new TableTransferHandler();
  
  public BasicTableUI() {}
  
  private boolean pointOutsidePrefSize(int paramInt1, int paramInt2, Point paramPoint)
  {
    if (!isFileList) {
      return false;
    }
    return SwingUtilities2.pointOutsidePrefSize(table, paramInt1, paramInt2, paramPoint);
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected KeyListener createKeyListener()
  {
    return null;
  }
  
  protected FocusListener createFocusListener()
  {
    return getHandler();
  }
  
  protected MouseInputListener createMouseInputListener()
  {
    return getHandler();
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicTableUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    table = ((JTable)paramJComponent);
    rendererPane = new CellRendererPane();
    table.add(rendererPane);
    installDefaults();
    installDefaults2();
    installListeners();
    installKeyboardActions();
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installColorsAndFont(table, "Table.background", "Table.foreground", "Table.font");
    LookAndFeel.installProperty(table, "opaque", Boolean.TRUE);
    Color localColor1 = table.getSelectionBackground();
    if ((localColor1 == null) || ((localColor1 instanceof UIResource)))
    {
      localColor1 = UIManager.getColor("Table.selectionBackground");
      table.setSelectionBackground(localColor1 != null ? localColor1 : UIManager.getColor("textHighlight"));
    }
    Color localColor2 = table.getSelectionForeground();
    if ((localColor2 == null) || ((localColor2 instanceof UIResource)))
    {
      localColor2 = UIManager.getColor("Table.selectionForeground");
      table.setSelectionForeground(localColor2 != null ? localColor2 : UIManager.getColor("textHighlightText"));
    }
    Color localColor3 = table.getGridColor();
    if ((localColor3 == null) || ((localColor3 instanceof UIResource)))
    {
      localColor3 = UIManager.getColor("Table.gridColor");
      table.setGridColor(localColor3 != null ? localColor3 : Color.GRAY);
    }
    Container localContainer = SwingUtilities.getUnwrappedParent(table);
    if (localContainer != null)
    {
      localContainer = localContainer.getParent();
      if ((localContainer != null) && ((localContainer instanceof JScrollPane))) {
        LookAndFeel.installBorder((JScrollPane)localContainer, "Table.scrollPaneBorder");
      }
    }
    isFileList = Boolean.TRUE.equals(table.getClientProperty("Table.isFileList"));
  }
  
  private void installDefaults2()
  {
    TransferHandler localTransferHandler = table.getTransferHandler();
    if ((localTransferHandler == null) || ((localTransferHandler instanceof UIResource)))
    {
      table.setTransferHandler(defaultTransferHandler);
      if ((table.getDropTarget() instanceof UIResource)) {
        table.setDropTarget(null);
      }
    }
  }
  
  protected void installListeners()
  {
    focusListener = createFocusListener();
    keyListener = createKeyListener();
    mouseInputListener = createMouseInputListener();
    table.addFocusListener(focusListener);
    table.addKeyListener(keyListener);
    table.addMouseListener(mouseInputListener);
    table.addMouseMotionListener(mouseInputListener);
    table.addPropertyChangeListener(getHandler());
    if (isFileList) {
      table.getSelectionModel().addListSelectionListener(getHandler());
    }
  }
  
  protected void installKeyboardActions()
  {
    LazyActionMap.installLazyActionMap(table, BasicTableUI.class, "Table.actionMap");
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(table, 1, localInputMap);
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1)
    {
      InputMap localInputMap1 = (InputMap)DefaultLookup.get(table, this, "Table.ancestorInputMap");
      InputMap localInputMap2;
      if ((table.getComponentOrientation().isLeftToRight()) || ((localInputMap2 = (InputMap)DefaultLookup.get(table, this, "Table.ancestorInputMap.RightToLeft")) == null)) {
        return localInputMap1;
      }
      localInputMap2.setParent(localInputMap1);
      return localInputMap2;
    }
    return null;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("selectNextColumn", 1, 0, false, false));
    paramLazyActionMap.put(new Actions("selectNextColumnChangeLead", 1, 0, false, false));
    paramLazyActionMap.put(new Actions("selectPreviousColumn", -1, 0, false, false));
    paramLazyActionMap.put(new Actions("selectPreviousColumnChangeLead", -1, 0, false, false));
    paramLazyActionMap.put(new Actions("selectNextRow", 0, 1, false, false));
    paramLazyActionMap.put(new Actions("selectNextRowChangeLead", 0, 1, false, false));
    paramLazyActionMap.put(new Actions("selectPreviousRow", 0, -1, false, false));
    paramLazyActionMap.put(new Actions("selectPreviousRowChangeLead", 0, -1, false, false));
    paramLazyActionMap.put(new Actions("selectNextColumnExtendSelection", 1, 0, true, false));
    paramLazyActionMap.put(new Actions("selectPreviousColumnExtendSelection", -1, 0, true, false));
    paramLazyActionMap.put(new Actions("selectNextRowExtendSelection", 0, 1, true, false));
    paramLazyActionMap.put(new Actions("selectPreviousRowExtendSelection", 0, -1, true, false));
    paramLazyActionMap.put(new Actions("scrollUpChangeSelection", false, false, true, false));
    paramLazyActionMap.put(new Actions("scrollDownChangeSelection", false, true, true, false));
    paramLazyActionMap.put(new Actions("selectFirstColumn", false, false, false, true));
    paramLazyActionMap.put(new Actions("selectLastColumn", false, true, false, true));
    paramLazyActionMap.put(new Actions("scrollUpExtendSelection", true, false, true, false));
    paramLazyActionMap.put(new Actions("scrollDownExtendSelection", true, true, true, false));
    paramLazyActionMap.put(new Actions("selectFirstColumnExtendSelection", true, false, false, true));
    paramLazyActionMap.put(new Actions("selectLastColumnExtendSelection", true, true, false, true));
    paramLazyActionMap.put(new Actions("selectFirstRow", false, false, true, true));
    paramLazyActionMap.put(new Actions("selectLastRow", false, true, true, true));
    paramLazyActionMap.put(new Actions("selectFirstRowExtendSelection", true, false, true, true));
    paramLazyActionMap.put(new Actions("selectLastRowExtendSelection", true, true, true, true));
    paramLazyActionMap.put(new Actions("selectNextColumnCell", 1, 0, false, true));
    paramLazyActionMap.put(new Actions("selectPreviousColumnCell", -1, 0, false, true));
    paramLazyActionMap.put(new Actions("selectNextRowCell", 0, 1, false, true));
    paramLazyActionMap.put(new Actions("selectPreviousRowCell", 0, -1, false, true));
    paramLazyActionMap.put(new Actions("selectAll"));
    paramLazyActionMap.put(new Actions("clearSelection"));
    paramLazyActionMap.put(new Actions("cancel"));
    paramLazyActionMap.put(new Actions("startEditing"));
    paramLazyActionMap.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
    paramLazyActionMap.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
    paramLazyActionMap.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
    paramLazyActionMap.put(new Actions("scrollLeftChangeSelection", false, false, false, false));
    paramLazyActionMap.put(new Actions("scrollRightChangeSelection", false, true, false, false));
    paramLazyActionMap.put(new Actions("scrollLeftExtendSelection", true, false, false, false));
    paramLazyActionMap.put(new Actions("scrollRightExtendSelection", true, true, false, false));
    paramLazyActionMap.put(new Actions("addToSelection"));
    paramLazyActionMap.put(new Actions("toggleAndAnchor"));
    paramLazyActionMap.put(new Actions("extendTo"));
    paramLazyActionMap.put(new Actions("moveSelectionTo"));
    paramLazyActionMap.put(new Actions("focusHeader"));
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    table.remove(rendererPane);
    rendererPane = null;
    table = null;
  }
  
  protected void uninstallDefaults()
  {
    if ((table.getTransferHandler() instanceof UIResource)) {
      table.setTransferHandler(null);
    }
  }
  
  protected void uninstallListeners()
  {
    table.removeFocusListener(focusListener);
    table.removeKeyListener(keyListener);
    table.removeMouseListener(mouseInputListener);
    table.removeMouseMotionListener(mouseInputListener);
    table.removePropertyChangeListener(getHandler());
    if (isFileList) {
      table.getSelectionModel().removeListSelectionListener(getHandler());
    }
    focusListener = null;
    keyListener = null;
    mouseInputListener = null;
    handler = null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIInputMap(table, 1, null);
    SwingUtilities.replaceUIActionMap(table, null);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    Component localComponent = (Component)localUIDefaults.get(BASELINE_COMPONENT_KEY);
    if (localComponent == null)
    {
      DefaultTableCellRenderer localDefaultTableCellRenderer = new DefaultTableCellRenderer();
      localComponent = localDefaultTableCellRenderer.getTableCellRendererComponent(table, "a", false, false, -1, -1);
      localUIDefaults.put(BASELINE_COMPONENT_KEY, localComponent);
    }
    localComponent.setFont(table.getFont());
    int i = table.getRowMargin();
    return localComponent.getBaseline(Integer.MAX_VALUE, table.getRowHeight() - i) + i / 2;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  private Dimension createTableSize(long paramLong)
  {
    int i = 0;
    int j = table.getRowCount();
    if ((j > 0) && (table.getColumnCount() > 0))
    {
      Rectangle localRectangle = table.getCellRect(j - 1, 0, true);
      i = y + height;
    }
    long l = Math.abs(paramLong);
    if (l > 2147483647L) {
      l = 2147483647L;
    }
    return new Dimension((int)l, i);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    long l = 0L;
    Enumeration localEnumeration = table.getColumnModel().getColumns();
    while (localEnumeration.hasMoreElements())
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      l += localTableColumn.getMinWidth();
    }
    return createTableSize(l);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    long l = 0L;
    Enumeration localEnumeration = table.getColumnModel().getColumns();
    while (localEnumeration.hasMoreElements())
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      l += localTableColumn.getPreferredWidth();
    }
    return createTableSize(l);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    long l = 0L;
    Enumeration localEnumeration = table.getColumnModel().getColumns();
    while (localEnumeration.hasMoreElements())
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      l += localTableColumn.getMaxWidth();
    }
    return createTableSize(l);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    Rectangle localRectangle1 = paramGraphics.getClipBounds();
    Rectangle localRectangle2 = table.getBounds();
    x = (y = 0);
    if ((table.getRowCount() <= 0) || (table.getColumnCount() <= 0) || (!localRectangle2.intersects(localRectangle1)))
    {
      paintDropLines(paramGraphics);
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
    paintGrid(paramGraphics, i, j, k, m);
    paintCells(paramGraphics, i, j, k, m);
    paintDropLines(paramGraphics);
  }
  
  private void paintDropLines(Graphics paramGraphics)
  {
    JTable.DropLocation localDropLocation = table.getDropLocation();
    if (localDropLocation == null) {
      return;
    }
    Color localColor1 = UIManager.getColor("Table.dropLineColor");
    Color localColor2 = UIManager.getColor("Table.dropLineShortColor");
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
  
  private void paintGrid(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGraphics.setColor(table.getGridColor());
    Rectangle localRectangle1 = table.getCellRect(paramInt1, paramInt3, true);
    Rectangle localRectangle2 = table.getCellRect(paramInt2, paramInt4, true);
    Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
    int j;
    int k;
    if (table.getShowHorizontalLines())
    {
      int i = x + width;
      j = y;
      for (k = paramInt1; k <= paramInt2; k++)
      {
        j += table.getRowHeight(k);
        paramGraphics.drawLine(x, j - 1, i - 1, j - 1);
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
          paramGraphics.drawLine(k - 1, 0, k - 1, j - 1);
        }
      }
      else
      {
        k = x;
        for (m = paramInt4; m >= paramInt3; m--)
        {
          n = localTableColumnModel.getColumn(m).getWidth();
          k += n;
          paramGraphics.drawLine(k - 1, 0, k - 1, j - 1);
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
  
  private void paintCells(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
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
            paintCell(paramGraphics, localRectangle, k, m);
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
          paintCell(paramGraphics, localRectangle, k, paramInt3);
        }
        for (m = paramInt3 + 1; m <= paramInt4; m++)
        {
          localTableColumn2 = localTableColumnModel.getColumn(m);
          j = localTableColumn2.getWidth();
          width = (j - i);
          x -= j;
          if (localTableColumn2 != localTableColumn1) {
            paintCell(paramGraphics, localRectangle, k, m);
          }
        }
      }
    }
    if (localTableColumn1 != null) {
      paintDraggedArea(paramGraphics, paramInt1, paramInt2, localTableColumn1, localJTableHeader.getDraggedDistance());
    }
    rendererPane.removeAll();
  }
  
  private void paintDraggedArea(Graphics paramGraphics, int paramInt1, int paramInt2, TableColumn paramTableColumn, int paramInt3)
  {
    int i = viewIndexForColumn(paramTableColumn);
    Rectangle localRectangle1 = table.getCellRect(paramInt1, i, true);
    Rectangle localRectangle2 = table.getCellRect(paramInt2, i, true);
    Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
    paramGraphics.setColor(table.getParent().getBackground());
    paramGraphics.fillRect(x, y, width, height);
    x += paramInt3;
    paramGraphics.setColor(table.getBackground());
    paramGraphics.fillRect(x, y, width, height);
    int n;
    if (table.getShowVerticalLines())
    {
      paramGraphics.setColor(table.getGridColor());
      j = x;
      int k = y;
      int m = j + width - 1;
      n = k + height - 1;
      paramGraphics.drawLine(j - 1, k, j - 1, n);
      paramGraphics.drawLine(m, k, m, n);
    }
    for (int j = paramInt1; j <= paramInt2; j++)
    {
      Rectangle localRectangle4 = table.getCellRect(j, i, false);
      x += paramInt3;
      paintCell(paramGraphics, localRectangle4, j, i);
      if (table.getShowHorizontalLines())
      {
        paramGraphics.setColor(table.getGridColor());
        Rectangle localRectangle5 = table.getCellRect(j, i, true);
        x += paramInt3;
        n = x;
        int i1 = y;
        int i2 = n + width - 1;
        int i3 = i1 + height - 1;
        paramGraphics.drawLine(n, i3, i2, i3);
      }
    }
  }
  
  private void paintCell(Graphics paramGraphics, Rectangle paramRectangle, int paramInt1, int paramInt2)
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
      rendererPane.paintComponent(paramGraphics, localComponent, table, x, y, width, height, true);
    }
  }
  
  private static int getAdjustedLead(JTable paramJTable, boolean paramBoolean, ListSelectionModel paramListSelectionModel)
  {
    int i = paramListSelectionModel.getLeadSelectionIndex();
    int j = paramBoolean ? paramJTable.getRowCount() : paramJTable.getColumnCount();
    return i < j ? i : -1;
  }
  
  private static int getAdjustedLead(JTable paramJTable, boolean paramBoolean)
  {
    return paramBoolean ? getAdjustedLead(paramJTable, paramBoolean, paramJTable.getSelectionModel()) : getAdjustedLead(paramJTable, paramBoolean, paramJTable.getColumnModel().getSelectionModel());
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String CANCEL_EDITING = "cancel";
    private static final String SELECT_ALL = "selectAll";
    private static final String CLEAR_SELECTION = "clearSelection";
    private static final String START_EDITING = "startEditing";
    private static final String NEXT_ROW = "selectNextRow";
    private static final String NEXT_ROW_CELL = "selectNextRowCell";
    private static final String NEXT_ROW_EXTEND_SELECTION = "selectNextRowExtendSelection";
    private static final String NEXT_ROW_CHANGE_LEAD = "selectNextRowChangeLead";
    private static final String PREVIOUS_ROW = "selectPreviousRow";
    private static final String PREVIOUS_ROW_CELL = "selectPreviousRowCell";
    private static final String PREVIOUS_ROW_EXTEND_SELECTION = "selectPreviousRowExtendSelection";
    private static final String PREVIOUS_ROW_CHANGE_LEAD = "selectPreviousRowChangeLead";
    private static final String NEXT_COLUMN = "selectNextColumn";
    private static final String NEXT_COLUMN_CELL = "selectNextColumnCell";
    private static final String NEXT_COLUMN_EXTEND_SELECTION = "selectNextColumnExtendSelection";
    private static final String NEXT_COLUMN_CHANGE_LEAD = "selectNextColumnChangeLead";
    private static final String PREVIOUS_COLUMN = "selectPreviousColumn";
    private static final String PREVIOUS_COLUMN_CELL = "selectPreviousColumnCell";
    private static final String PREVIOUS_COLUMN_EXTEND_SELECTION = "selectPreviousColumnExtendSelection";
    private static final String PREVIOUS_COLUMN_CHANGE_LEAD = "selectPreviousColumnChangeLead";
    private static final String SCROLL_LEFT_CHANGE_SELECTION = "scrollLeftChangeSelection";
    private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
    private static final String SCROLL_RIGHT_CHANGE_SELECTION = "scrollRightChangeSelection";
    private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";
    private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
    private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
    private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
    private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";
    private static final String FIRST_COLUMN = "selectFirstColumn";
    private static final String FIRST_COLUMN_EXTEND_SELECTION = "selectFirstColumnExtendSelection";
    private static final String LAST_COLUMN = "selectLastColumn";
    private static final String LAST_COLUMN_EXTEND_SELECTION = "selectLastColumnExtendSelection";
    private static final String FIRST_ROW = "selectFirstRow";
    private static final String FIRST_ROW_EXTEND_SELECTION = "selectFirstRowExtendSelection";
    private static final String LAST_ROW = "selectLastRow";
    private static final String LAST_ROW_EXTEND_SELECTION = "selectLastRowExtendSelection";
    private static final String ADD_TO_SELECTION = "addToSelection";
    private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
    private static final String EXTEND_TO = "extendTo";
    private static final String MOVE_SELECTION_TO = "moveSelectionTo";
    private static final String FOCUS_HEADER = "focusHeader";
    protected int dx;
    protected int dy;
    protected boolean extend;
    protected boolean inSelection;
    protected boolean forwards;
    protected boolean vertically;
    protected boolean toLimit;
    protected int leadRow;
    protected int leadColumn;
    
    Actions(String paramString)
    {
      super();
    }
    
    Actions(String paramString, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    {
      super();
      if (paramBoolean2)
      {
        inSelection = true;
        paramInt1 = sign(paramInt1);
        paramInt2 = sign(paramInt2);
        assert (((paramInt1 == 0) || (paramInt2 == 0)) && ((paramInt1 != 0) || (paramInt2 != 0)));
      }
      dx = paramInt1;
      dy = paramInt2;
      extend = paramBoolean1;
    }
    
    Actions(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    {
      this(paramString, 0, 0, paramBoolean1, false);
      forwards = paramBoolean2;
      vertically = paramBoolean3;
      toLimit = paramBoolean4;
    }
    
    private static int clipToRange(int paramInt1, int paramInt2, int paramInt3)
    {
      return Math.min(Math.max(paramInt1, paramInt2), paramInt3 - 1);
    }
    
    private void moveWithinTableRange(JTable paramJTable, int paramInt1, int paramInt2)
    {
      leadRow = clipToRange(leadRow + paramInt2, 0, paramJTable.getRowCount());
      leadColumn = clipToRange(leadColumn + paramInt1, 0, paramJTable.getColumnCount());
    }
    
    private static int sign(int paramInt)
    {
      return paramInt == 0 ? 0 : paramInt < 0 ? -1 : 1;
    }
    
    private boolean moveWithinSelectedRange(JTable paramJTable, int paramInt1, int paramInt2, ListSelectionModel paramListSelectionModel1, ListSelectionModel paramListSelectionModel2)
    {
      boolean bool1 = paramJTable.getRowSelectionAllowed();
      boolean bool2 = paramJTable.getColumnSelectionAllowed();
      int i;
      int j;
      int k;
      int m;
      int n;
      if ((bool1) && (bool2))
      {
        i = paramJTable.getSelectedRowCount() * paramJTable.getSelectedColumnCount();
        j = paramListSelectionModel2.getMinSelectionIndex();
        k = paramListSelectionModel2.getMaxSelectionIndex();
        m = paramListSelectionModel1.getMinSelectionIndex();
        n = paramListSelectionModel1.getMaxSelectionIndex();
      }
      else if (bool1)
      {
        i = paramJTable.getSelectedRowCount();
        j = 0;
        k = paramJTable.getColumnCount() - 1;
        m = paramListSelectionModel1.getMinSelectionIndex();
        n = paramListSelectionModel1.getMaxSelectionIndex();
      }
      else if (bool2)
      {
        i = paramJTable.getSelectedColumnCount();
        j = paramListSelectionModel2.getMinSelectionIndex();
        k = paramListSelectionModel2.getMaxSelectionIndex();
        m = 0;
        n = paramJTable.getRowCount() - 1;
      }
      else
      {
        i = 0;
        j = k = m = n = 0;
      }
      boolean bool3;
      if ((i == 0) || ((i == 1) && (paramJTable.isCellSelected(leadRow, leadColumn))))
      {
        bool3 = false;
        k = paramJTable.getColumnCount() - 1;
        n = paramJTable.getRowCount() - 1;
        j = Math.min(0, k);
        m = Math.min(0, n);
      }
      else
      {
        bool3 = true;
      }
      if ((paramInt2 == 1) && (leadColumn == -1))
      {
        leadColumn = j;
        leadRow = -1;
      }
      else if ((paramInt1 == 1) && (leadRow == -1))
      {
        leadRow = m;
        leadColumn = -1;
      }
      else if ((paramInt2 == -1) && (leadColumn == -1))
      {
        leadColumn = k;
        leadRow = (n + 1);
      }
      else if ((paramInt1 == -1) && (leadRow == -1))
      {
        leadRow = n;
        leadColumn = (k + 1);
      }
      leadRow = Math.min(Math.max(leadRow, m - 1), n + 1);
      leadColumn = Math.min(Math.max(leadColumn, j - 1), k + 1);
      do
      {
        calcNextPos(paramInt1, j, k, paramInt2, m, n);
      } while ((bool3) && (!paramJTable.isCellSelected(leadRow, leadColumn)));
      return bool3;
    }
    
    private void calcNextPos(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      if (paramInt1 != 0)
      {
        leadColumn += paramInt1;
        if (leadColumn > paramInt3)
        {
          leadColumn = paramInt2;
          leadRow += 1;
          if (leadRow > paramInt6) {
            leadRow = paramInt5;
          }
        }
        else if (leadColumn < paramInt2)
        {
          leadColumn = paramInt3;
          leadRow -= 1;
          if (leadRow < paramInt5) {
            leadRow = paramInt6;
          }
        }
      }
      else
      {
        leadRow += paramInt4;
        if (leadRow > paramInt6)
        {
          leadRow = paramInt5;
          leadColumn += 1;
          if (leadColumn > paramInt3) {
            leadColumn = paramInt2;
          }
        }
        else if (leadRow < paramInt5)
        {
          leadRow = paramInt6;
          leadColumn -= 1;
          if (leadColumn < paramInt2) {
            leadColumn = paramInt3;
          }
        }
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      String str = getName();
      JTable localJTable = (JTable)paramActionEvent.getSource();
      ListSelectionModel localListSelectionModel1 = localJTable.getSelectionModel();
      leadRow = BasicTableUI.getAdjustedLead(localJTable, true, localListSelectionModel1);
      ListSelectionModel localListSelectionModel2 = localJTable.getColumnModel().getSelectionModel();
      leadColumn = BasicTableUI.getAdjustedLead(localJTable, false, localListSelectionModel2);
      Rectangle localRectangle;
      if ((str == "scrollLeftChangeSelection") || (str == "scrollLeftExtendSelection") || (str == "scrollRightChangeSelection") || (str == "scrollRightExtendSelection") || (str == "scrollUpChangeSelection") || (str == "scrollUpExtendSelection") || (str == "scrollDownChangeSelection") || (str == "scrollDownExtendSelection") || (str == "selectFirstColumn") || (str == "selectFirstColumnExtendSelection") || (str == "selectFirstRow") || (str == "selectFirstRowExtendSelection") || (str == "selectLastColumn") || (str == "selectLastColumnExtendSelection") || (str == "selectLastRow") || (str == "selectLastRowExtendSelection")) {
        if (toLimit)
        {
          int i;
          if (vertically)
          {
            i = localJTable.getRowCount();
            dx = 0;
            dy = (forwards ? i : -i);
          }
          else
          {
            i = localJTable.getColumnCount();
            dx = (forwards ? i : -i);
            dy = 0;
          }
        }
        else
        {
          if (!(SwingUtilities.getUnwrappedParent(localJTable).getParent() instanceof JScrollPane)) {
            return;
          }
          Dimension localDimension = localJTable.getParent().getSize();
          int n;
          if (vertically)
          {
            localRectangle = localJTable.getCellRect(leadRow, 0, true);
            if (forwards) {
              y += Math.max(height, height);
            } else {
              y -= height;
            }
            dx = 0;
            n = localJTable.rowAtPoint(localRectangle.getLocation());
            if ((n == -1) && (forwards)) {
              n = localJTable.getRowCount();
            }
            dy = (n - leadRow);
          }
          else
          {
            localRectangle = localJTable.getCellRect(0, leadColumn, true);
            if (forwards) {
              x += Math.max(width, width);
            } else {
              x -= width;
            }
            n = localJTable.columnAtPoint(localRectangle.getLocation());
            if (n == -1)
            {
              boolean bool = localJTable.getComponentOrientation().isLeftToRight();
              n = bool ? 0 : forwards ? 0 : bool ? localJTable.getColumnCount() : localJTable.getColumnCount();
            }
            dx = (n - leadColumn);
            dy = 0;
          }
        }
      }
      if ((str == "selectNextRow") || (str == "selectNextRowCell") || (str == "selectNextRowExtendSelection") || (str == "selectNextRowChangeLead") || (str == "selectNextColumn") || (str == "selectNextColumnCell") || (str == "selectNextColumnExtendSelection") || (str == "selectNextColumnChangeLead") || (str == "selectPreviousRow") || (str == "selectPreviousRowCell") || (str == "selectPreviousRowExtendSelection") || (str == "selectPreviousRowChangeLead") || (str == "selectPreviousColumn") || (str == "selectPreviousColumnCell") || (str == "selectPreviousColumnExtendSelection") || (str == "selectPreviousColumnChangeLead") || (str == "scrollLeftChangeSelection") || (str == "scrollLeftExtendSelection") || (str == "scrollRightChangeSelection") || (str == "scrollRightExtendSelection") || (str == "scrollUpChangeSelection") || (str == "scrollUpExtendSelection") || (str == "scrollDownChangeSelection") || (str == "scrollDownExtendSelection") || (str == "selectFirstColumn") || (str == "selectFirstColumnExtendSelection") || (str == "selectFirstRow") || (str == "selectFirstRowExtendSelection") || (str == "selectLastColumn") || (str == "selectLastColumnExtendSelection") || (str == "selectLastRow") || (str == "selectLastRowExtendSelection"))
      {
        if ((localJTable.isEditing()) && (!localJTable.getCellEditor().stopCellEditing())) {
          return;
        }
        int j = 0;
        if ((str == "selectNextRowChangeLead") || (str == "selectPreviousRowChangeLead")) {
          j = localListSelectionModel1.getSelectionMode() == 2 ? 1 : 0;
        } else if ((str == "selectNextColumnChangeLead") || (str == "selectPreviousColumnChangeLead")) {
          j = localListSelectionModel2.getSelectionMode() == 2 ? 1 : 0;
        }
        if (j != 0)
        {
          moveWithinTableRange(localJTable, dx, dy);
          if (dy != 0)
          {
            ((DefaultListSelectionModel)localListSelectionModel1).moveLeadSelectionIndex(leadRow);
            if ((BasicTableUI.getAdjustedLead(localJTable, false, localListSelectionModel2) == -1) && (localJTable.getColumnCount() > 0)) {
              ((DefaultListSelectionModel)localListSelectionModel2).moveLeadSelectionIndex(0);
            }
          }
          else
          {
            ((DefaultListSelectionModel)localListSelectionModel2).moveLeadSelectionIndex(leadColumn);
            if ((BasicTableUI.getAdjustedLead(localJTable, true, localListSelectionModel1) == -1) && (localJTable.getRowCount() > 0)) {
              ((DefaultListSelectionModel)localListSelectionModel1).moveLeadSelectionIndex(0);
            }
          }
          localRectangle = localJTable.getCellRect(leadRow, leadColumn, false);
          if (localRectangle != null) {
            localJTable.scrollRectToVisible(localRectangle);
          }
        }
        else if (!inSelection)
        {
          moveWithinTableRange(localJTable, dx, dy);
          localJTable.changeSelection(leadRow, leadColumn, false, extend);
        }
        else
        {
          if ((localJTable.getRowCount() <= 0) || (localJTable.getColumnCount() <= 0)) {
            return;
          }
          if (moveWithinSelectedRange(localJTable, dx, dy, localListSelectionModel1, localListSelectionModel2))
          {
            if (localListSelectionModel1.isSelectedIndex(leadRow)) {
              localListSelectionModel1.addSelectionInterval(leadRow, leadRow);
            } else {
              localListSelectionModel1.removeSelectionInterval(leadRow, leadRow);
            }
            if (localListSelectionModel2.isSelectedIndex(leadColumn)) {
              localListSelectionModel2.addSelectionInterval(leadColumn, leadColumn);
            } else {
              localListSelectionModel2.removeSelectionInterval(leadColumn, leadColumn);
            }
            localRectangle = localJTable.getCellRect(leadRow, leadColumn, false);
            if (localRectangle != null) {
              localJTable.scrollRectToVisible(localRectangle);
            }
          }
          else
          {
            localJTable.changeSelection(leadRow, leadColumn, false, false);
          }
        }
      }
      else if (str == "cancel")
      {
        localJTable.removeEditor();
      }
      else if (str == "selectAll")
      {
        localJTable.selectAll();
      }
      else if (str == "clearSelection")
      {
        localJTable.clearSelection();
      }
      else if (str == "startEditing")
      {
        if (!localJTable.hasFocus())
        {
          localObject = localJTable.getCellEditor();
          if ((localObject != null) && (!((CellEditor)localObject).stopCellEditing())) {
            return;
          }
          localJTable.requestFocus();
          return;
        }
        localJTable.editCellAt(leadRow, leadColumn, paramActionEvent);
        Object localObject = localJTable.getEditorComponent();
        if (localObject != null) {
          ((Component)localObject).requestFocus();
        }
      }
      else
      {
        int m;
        if (str == "addToSelection")
        {
          if (!localJTable.isCellSelected(leadRow, leadColumn))
          {
            int k = localListSelectionModel1.getAnchorSelectionIndex();
            m = localListSelectionModel2.getAnchorSelectionIndex();
            localListSelectionModel1.setValueIsAdjusting(true);
            localListSelectionModel2.setValueIsAdjusting(true);
            localJTable.changeSelection(leadRow, leadColumn, true, false);
            localListSelectionModel1.setAnchorSelectionIndex(k);
            localListSelectionModel2.setAnchorSelectionIndex(m);
            localListSelectionModel1.setValueIsAdjusting(false);
            localListSelectionModel2.setValueIsAdjusting(false);
          }
        }
        else if (str == "toggleAndAnchor")
        {
          localJTable.changeSelection(leadRow, leadColumn, true, false);
        }
        else if (str == "extendTo")
        {
          localJTable.changeSelection(leadRow, leadColumn, false, true);
        }
        else if (str == "moveSelectionTo")
        {
          localJTable.changeSelection(leadRow, leadColumn, false, false);
        }
        else if (str == "focusHeader")
        {
          JTableHeader localJTableHeader = localJTable.getTableHeader();
          if (localJTableHeader != null)
          {
            m = localJTable.getSelectedColumn();
            if (m >= 0)
            {
              TableHeaderUI localTableHeaderUI = localJTableHeader.getUI();
              if ((localTableHeaderUI instanceof BasicTableHeaderUI)) {
                ((BasicTableHeaderUI)localTableHeaderUI).selectColumn(m);
              }
            }
            localJTableHeader.requestFocusInWindow();
          }
        }
      }
    }
    
    public boolean isEnabled(Object paramObject)
    {
      String str = getName();
      if (((paramObject instanceof JTable)) && (Boolean.TRUE.equals(((JTable)paramObject).getClientProperty("Table.isFileList"))) && ((str == "selectNextColumn") || (str == "selectNextColumnCell") || (str == "selectNextColumnExtendSelection") || (str == "selectNextColumnChangeLead") || (str == "selectPreviousColumn") || (str == "selectPreviousColumnCell") || (str == "selectPreviousColumnExtendSelection") || (str == "selectPreviousColumnChangeLead") || (str == "scrollLeftChangeSelection") || (str == "scrollLeftExtendSelection") || (str == "scrollRightChangeSelection") || (str == "scrollRightExtendSelection") || (str == "selectFirstColumn") || (str == "selectFirstColumnExtendSelection") || (str == "selectLastColumn") || (str == "selectLastColumnExtendSelection") || (str == "selectNextRowCell") || (str == "selectPreviousRowCell"))) {
        return false;
      }
      if ((str == "cancel") && ((paramObject instanceof JTable))) {
        return ((JTable)paramObject).isEditing();
      }
      if ((str == "selectNextRowChangeLead") || (str == "selectPreviousRowChangeLead")) {
        return (paramObject != null) && ((((JTable)paramObject).getSelectionModel() instanceof DefaultListSelectionModel));
      }
      if ((str == "selectNextColumnChangeLead") || (str == "selectPreviousColumnChangeLead")) {
        return (paramObject != null) && ((((JTable)paramObject).getColumnModel().getSelectionModel() instanceof DefaultListSelectionModel));
      }
      JTable localJTable;
      if ((str == "addToSelection") && ((paramObject instanceof JTable)))
      {
        localJTable = (JTable)paramObject;
        int i = BasicTableUI.getAdjustedLead(localJTable, true);
        int j = BasicTableUI.getAdjustedLead(localJTable, false);
        return (!localJTable.isEditing()) && (!localJTable.isCellSelected(i, j));
      }
      if ((str == "focusHeader") && ((paramObject instanceof JTable)))
      {
        localJTable = (JTable)paramObject;
        return localJTable.getTableHeader() != null;
      }
      return true;
    }
  }
  
  public class FocusHandler
    implements FocusListener
  {
    public FocusHandler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicTableUI.this.getHandler().focusGained(paramFocusEvent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicTableUI.this.getHandler().focusLost(paramFocusEvent);
    }
  }
  
  private class Handler
    implements FocusListener, MouseInputListener, PropertyChangeListener, ListSelectionListener, ActionListener, DragRecognitionSupport.BeforeDrag
  {
    private Component dispatchComponent;
    private int pressedRow;
    private int pressedCol;
    private MouseEvent pressedEvent;
    private boolean dragPressDidSelection;
    private boolean dragStarted;
    private boolean shouldStartTimer;
    private boolean outsidePrefSize;
    private Timer timer = null;
    
    private Handler() {}
    
    private void repaintLeadCell()
    {
      int i = BasicTableUI.getAdjustedLead(table, true);
      int j = BasicTableUI.getAdjustedLead(table, false);
      if ((i < 0) || (j < 0)) {
        return;
      }
      Rectangle localRectangle = table.getCellRect(i, j, false);
      table.repaint(localRectangle);
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      repaintLeadCell();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      repaintLeadCell();
    }
    
    public void keyPressed(KeyEvent paramKeyEvent) {}
    
    public void keyReleased(KeyEvent paramKeyEvent) {}
    
    public void keyTyped(KeyEvent paramKeyEvent)
    {
      KeyStroke localKeyStroke = KeyStroke.getKeyStroke(paramKeyEvent.getKeyChar(), paramKeyEvent.getModifiers());
      InputMap localInputMap = table.getInputMap(0);
      if ((localInputMap != null) && (localInputMap.get(localKeyStroke) != null)) {
        return;
      }
      localInputMap = table.getInputMap(1);
      if ((localInputMap != null) && (localInputMap.get(localKeyStroke) != null)) {
        return;
      }
      localKeyStroke = KeyStroke.getKeyStrokeForEvent(paramKeyEvent);
      if (paramKeyEvent.getKeyChar() == '\r') {
        return;
      }
      int i = BasicTableUI.getAdjustedLead(table, true);
      int j = BasicTableUI.getAdjustedLead(table, false);
      if ((i != -1) && (j != -1) && (!table.isEditing()) && (!table.editCellAt(i, j))) {
        return;
      }
      Component localComponent = table.getEditorComponent();
      if ((table.isEditing()) && (localComponent != null) && ((localComponent instanceof JComponent)))
      {
        JComponent localJComponent = (JComponent)localComponent;
        localInputMap = localJComponent.getInputMap(0);
        Object localObject = localInputMap != null ? localInputMap.get(localKeyStroke) : null;
        if (localObject == null)
        {
          localInputMap = localJComponent.getInputMap(1);
          localObject = localInputMap != null ? localInputMap.get(localKeyStroke) : null;
        }
        if (localObject != null)
        {
          ActionMap localActionMap = localJComponent.getActionMap();
          Action localAction = localActionMap != null ? localActionMap.get(localObject) : null;
          if ((localAction != null) && (SwingUtilities.notifyAction(localAction, localKeyStroke, paramKeyEvent, localJComponent, paramKeyEvent.getModifiers()))) {
            paramKeyEvent.consume();
          }
        }
      }
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    private void setDispatchComponent(MouseEvent paramMouseEvent)
    {
      Component localComponent = table.getEditorComponent();
      Point localPoint1 = paramMouseEvent.getPoint();
      Point localPoint2 = SwingUtilities.convertPoint(table, localPoint1, localComponent);
      dispatchComponent = SwingUtilities.getDeepestComponentAt(localComponent, x, y);
      SwingUtilities2.setSkipClickCount(dispatchComponent, paramMouseEvent.getClickCount() - 1);
    }
    
    private boolean repostEvent(MouseEvent paramMouseEvent)
    {
      if ((dispatchComponent == null) || (!table.isEditing())) {
        return false;
      }
      MouseEvent localMouseEvent = SwingUtilities.convertMouseEvent(table, paramMouseEvent, dispatchComponent);
      dispatchComponent.dispatchEvent(localMouseEvent);
      return true;
    }
    
    private void setValueIsAdjusting(boolean paramBoolean)
    {
      table.getSelectionModel().setValueIsAdjusting(paramBoolean);
      table.getColumnModel().getSelectionModel().setValueIsAdjusting(paramBoolean);
    }
    
    private boolean canStartDrag()
    {
      if ((pressedRow == -1) || (pressedCol == -1)) {
        return false;
      }
      if (isFileList) {
        return !outsidePrefSize;
      }
      if ((table.getSelectionModel().getSelectionMode() == 0) && (table.getColumnModel().getSelectionModel().getSelectionMode() == 0)) {
        return true;
      }
      return table.isCellSelected(pressedRow, pressedCol);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (SwingUtilities2.shouldIgnore(paramMouseEvent, table)) {
        return;
      }
      if ((table.isEditing()) && (!table.getCellEditor().stopCellEditing()))
      {
        localObject = table.getEditorComponent();
        if ((localObject != null) && (!((Component)localObject).hasFocus())) {
          SwingUtilities2.compositeRequestFocus((Component)localObject);
        }
        return;
      }
      Object localObject = paramMouseEvent.getPoint();
      pressedRow = table.rowAtPoint((Point)localObject);
      pressedCol = table.columnAtPoint((Point)localObject);
      outsidePrefSize = BasicTableUI.this.pointOutsidePrefSize(pressedRow, pressedCol, (Point)localObject);
      if (isFileList) {
        shouldStartTimer = ((table.isCellSelected(pressedRow, pressedCol)) && (!paramMouseEvent.isShiftDown()) && (!BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent)) && (!outsidePrefSize));
      }
      if (table.getDragEnabled())
      {
        mousePressedDND(paramMouseEvent);
      }
      else
      {
        SwingUtilities2.adjustFocus(table);
        if (!isFileList) {
          setValueIsAdjusting(true);
        }
        adjustSelection(paramMouseEvent);
      }
    }
    
    private void mousePressedDND(MouseEvent paramMouseEvent)
    {
      pressedEvent = paramMouseEvent;
      int i = 1;
      dragStarted = false;
      if ((canStartDrag()) && (DragRecognitionSupport.mousePressed(paramMouseEvent)))
      {
        dragPressDidSelection = false;
        if ((BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent)) && (isFileList)) {
          return;
        }
        if ((!paramMouseEvent.isShiftDown()) && (table.isCellSelected(pressedRow, pressedCol)))
        {
          table.getSelectionModel().addSelectionInterval(pressedRow, pressedRow);
          table.getColumnModel().getSelectionModel().addSelectionInterval(pressedCol, pressedCol);
          return;
        }
        dragPressDidSelection = true;
        i = 0;
      }
      else if (!isFileList)
      {
        setValueIsAdjusting(true);
      }
      if (i != 0) {
        SwingUtilities2.adjustFocus(table);
      }
      adjustSelection(paramMouseEvent);
    }
    
    private void adjustSelection(MouseEvent paramMouseEvent)
    {
      if (outsidePrefSize)
      {
        if ((paramMouseEvent.getID() == 501) && ((!paramMouseEvent.isShiftDown()) || (table.getSelectionModel().getSelectionMode() == 0)))
        {
          table.clearSelection();
          TableCellEditor localTableCellEditor1 = table.getCellEditor();
          if (localTableCellEditor1 != null) {
            localTableCellEditor1.stopCellEditing();
          }
        }
        return;
      }
      if ((pressedCol == -1) || (pressedRow == -1)) {
        return;
      }
      boolean bool = table.getDragEnabled();
      if ((!bool) && (!isFileList) && (table.editCellAt(pressedRow, pressedCol, paramMouseEvent)))
      {
        setDispatchComponent(paramMouseEvent);
        repostEvent(paramMouseEvent);
      }
      TableCellEditor localTableCellEditor2 = table.getCellEditor();
      if ((bool) || (localTableCellEditor2 == null) || (localTableCellEditor2.shouldSelectCell(paramMouseEvent))) {
        table.changeSelection(pressedRow, pressedCol, BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent), paramMouseEvent.isShiftDown());
      }
    }
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      if (timer != null)
      {
        timer.stop();
        timer = null;
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      table.editCellAt(pressedRow, pressedCol, null);
      Component localComponent = table.getEditorComponent();
      if ((localComponent != null) && (!localComponent.hasFocus())) {
        SwingUtilities2.compositeRequestFocus(localComponent);
      }
    }
    
    private void maybeStartTimer()
    {
      if (!shouldStartTimer) {
        return;
      }
      if (timer == null)
      {
        timer = new Timer(1200, this);
        timer.setRepeats(false);
      }
      timer.start();
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (SwingUtilities2.shouldIgnore(paramMouseEvent, table)) {
        return;
      }
      if (table.getDragEnabled()) {
        mouseReleasedDND(paramMouseEvent);
      } else if (isFileList) {
        maybeStartTimer();
      }
      pressedEvent = null;
      repostEvent(paramMouseEvent);
      dispatchComponent = null;
      setValueIsAdjusting(false);
    }
    
    private void mouseReleasedDND(MouseEvent paramMouseEvent)
    {
      MouseEvent localMouseEvent = DragRecognitionSupport.mouseReleased(paramMouseEvent);
      if (localMouseEvent != null)
      {
        SwingUtilities2.adjustFocus(table);
        if (!dragPressDidSelection) {
          adjustSelection(localMouseEvent);
        }
      }
      if (!dragStarted)
      {
        if (isFileList)
        {
          maybeStartTimer();
          return;
        }
        Point localPoint = paramMouseEvent.getPoint();
        if ((pressedEvent != null) && (table.rowAtPoint(localPoint) == pressedRow) && (table.columnAtPoint(localPoint) == pressedCol) && (table.editCellAt(pressedRow, pressedCol, pressedEvent)))
        {
          setDispatchComponent(pressedEvent);
          repostEvent(pressedEvent);
          TableCellEditor localTableCellEditor = table.getCellEditor();
          if (localTableCellEditor != null) {
            localTableCellEditor.shouldSelectCell(pressedEvent);
          }
        }
      }
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void dragStarting(MouseEvent paramMouseEvent)
    {
      dragStarted = true;
      if ((BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent)) && (isFileList))
      {
        table.getSelectionModel().addSelectionInterval(pressedRow, pressedRow);
        table.getColumnModel().getSelectionModel().addSelectionInterval(pressedCol, pressedCol);
      }
      pressedEvent = null;
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (SwingUtilities2.shouldIgnore(paramMouseEvent, table)) {
        return;
      }
      if ((table.getDragEnabled()) && ((DragRecognitionSupport.mouseDragged(paramMouseEvent, this)) || (dragStarted))) {
        return;
      }
      repostEvent(paramMouseEvent);
      if ((isFileList) || (table.isEditing())) {
        return;
      }
      Point localPoint = paramMouseEvent.getPoint();
      int i = table.rowAtPoint(localPoint);
      int j = table.columnAtPoint(localPoint);
      if ((j == -1) || (i == -1)) {
        return;
      }
      table.changeSelection(i, j, BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent), true);
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject;
      if ("componentOrientation" == str)
      {
        localObject = getInputMap(1);
        SwingUtilities.replaceUIInputMap(table, 1, (InputMap)localObject);
        JTableHeader localJTableHeader = table.getTableHeader();
        if (localJTableHeader != null) {
          localJTableHeader.setComponentOrientation((ComponentOrientation)paramPropertyChangeEvent.getNewValue());
        }
      }
      else if ("dropLocation" == str)
      {
        localObject = (JTable.DropLocation)paramPropertyChangeEvent.getOldValue();
        repaintDropLocation((JTable.DropLocation)localObject);
        repaintDropLocation(table.getDropLocation());
      }
      else if ("Table.isFileList" == str)
      {
        isFileList = Boolean.TRUE.equals(table.getClientProperty("Table.isFileList"));
        table.revalidate();
        table.repaint();
        if (isFileList)
        {
          table.getSelectionModel().addListSelectionListener(BasicTableUI.this.getHandler());
        }
        else
        {
          table.getSelectionModel().removeListSelectionListener(BasicTableUI.this.getHandler());
          timer = null;
        }
      }
      else if (("selectionModel" == str) && (isFileList))
      {
        localObject = (ListSelectionModel)paramPropertyChangeEvent.getOldValue();
        ((ListSelectionModel)localObject).removeListSelectionListener(BasicTableUI.this.getHandler());
        table.getSelectionModel().addListSelectionListener(BasicTableUI.this.getHandler());
      }
    }
    
    private void repaintDropLocation(JTable.DropLocation paramDropLocation)
    {
      if (paramDropLocation == null) {
        return;
      }
      Rectangle localRectangle;
      if ((!paramDropLocation.isInsertRow()) && (!paramDropLocation.isInsertColumn()))
      {
        localRectangle = table.getCellRect(paramDropLocation.getRow(), paramDropLocation.getColumn(), false);
        if (localRectangle != null) {
          table.repaint(localRectangle);
        }
        return;
      }
      if (paramDropLocation.isInsertRow())
      {
        localRectangle = BasicTableUI.this.extendRect(BasicTableUI.access$500(BasicTableUI.this, paramDropLocation), true);
        if (localRectangle != null) {
          table.repaint(localRectangle);
        }
      }
      if (paramDropLocation.isInsertColumn())
      {
        localRectangle = BasicTableUI.this.extendRect(BasicTableUI.access$700(BasicTableUI.this, paramDropLocation), false);
        if (localRectangle != null) {
          table.repaint(localRectangle);
        }
      }
    }
  }
  
  public class KeyHandler
    implements KeyListener
  {
    public KeyHandler() {}
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      BasicTableUI.this.getHandler().keyPressed(paramKeyEvent);
    }
    
    public void keyReleased(KeyEvent paramKeyEvent)
    {
      BasicTableUI.this.getHandler().keyReleased(paramKeyEvent);
    }
    
    public void keyTyped(KeyEvent paramKeyEvent)
    {
      BasicTableUI.this.getHandler().keyTyped(paramKeyEvent);
    }
  }
  
  public class MouseInputHandler
    implements MouseInputListener
  {
    public MouseInputHandler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      BasicTableUI.this.getHandler().mouseClicked(paramMouseEvent);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      BasicTableUI.this.getHandler().mousePressed(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      BasicTableUI.this.getHandler().mouseReleased(paramMouseEvent);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      BasicTableUI.this.getHandler().mouseEntered(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      BasicTableUI.this.getHandler().mouseExited(paramMouseEvent);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      BasicTableUI.this.getHandler().mouseMoved(paramMouseEvent);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      BasicTableUI.this.getHandler().mouseDragged(paramMouseEvent);
    }
  }
  
  static class TableTransferHandler
    extends TransferHandler
    implements UIResource
  {
    TableTransferHandler() {}
    
    protected Transferable createTransferable(JComponent paramJComponent)
    {
      if ((paramJComponent instanceof JTable))
      {
        JTable localJTable = (JTable)paramJComponent;
        if ((!localJTable.getRowSelectionAllowed()) && (!localJTable.getColumnSelectionAllowed())) {
          return null;
        }
        int i;
        int[] arrayOfInt1;
        int j;
        if (!localJTable.getRowSelectionAllowed())
        {
          i = localJTable.getRowCount();
          arrayOfInt1 = new int[i];
          for (j = 0; j < i; j++) {
            arrayOfInt1[j] = j;
          }
        }
        else
        {
          arrayOfInt1 = localJTable.getSelectedRows();
        }
        int[] arrayOfInt2;
        if (!localJTable.getColumnSelectionAllowed())
        {
          i = localJTable.getColumnCount();
          arrayOfInt2 = new int[i];
          for (j = 0; j < i; j++) {
            arrayOfInt2[j] = j;
          }
        }
        else
        {
          arrayOfInt2 = localJTable.getSelectedColumns();
        }
        if ((arrayOfInt1 == null) || (arrayOfInt2 == null) || (arrayOfInt1.length == 0) || (arrayOfInt2.length == 0)) {
          return null;
        }
        StringBuffer localStringBuffer1 = new StringBuffer();
        StringBuffer localStringBuffer2 = new StringBuffer();
        localStringBuffer2.append("<html>\n<body>\n<table>\n");
        for (int k = 0; k < arrayOfInt1.length; k++)
        {
          localStringBuffer2.append("<tr>\n");
          for (int m = 0; m < arrayOfInt2.length; m++)
          {
            Object localObject = localJTable.getValueAt(arrayOfInt1[k], arrayOfInt2[m]);
            String str = localObject == null ? "" : localObject.toString();
            localStringBuffer1.append(str + "\t");
            localStringBuffer2.append("  <td>" + str + "</td>\n");
          }
          localStringBuffer1.deleteCharAt(localStringBuffer1.length() - 1).append("\n");
          localStringBuffer2.append("</tr>\n");
        }
        localStringBuffer1.deleteCharAt(localStringBuffer1.length() - 1);
        localStringBuffer2.append("</table>\n</body>\n</html>");
        return new BasicTransferable(localStringBuffer1.toString(), localStringBuffer2.toString());
      }
      return null;
    }
    
    public int getSourceActions(JComponent paramJComponent)
    {
      return 1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicTableUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */