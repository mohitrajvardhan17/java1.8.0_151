package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.CellRendererPane;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JList.DropLocation;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ListUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Position.Bias;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicListUI
  extends ListUI
{
  private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("List.baselineComponent");
  protected JList list = null;
  protected CellRendererPane rendererPane;
  protected FocusListener focusListener;
  protected MouseInputListener mouseInputListener;
  protected ListSelectionListener listSelectionListener;
  protected ListDataListener listDataListener;
  protected PropertyChangeListener propertyChangeListener;
  private Handler handler;
  protected int[] cellHeights = null;
  protected int cellHeight = -1;
  protected int cellWidth = -1;
  protected int updateLayoutStateNeeded = 1;
  private int listHeight;
  private int listWidth;
  private int layoutOrientation;
  private int columnCount;
  private int preferredHeight;
  private int rowsPerColumn;
  private long timeFactor = 1000L;
  private boolean isFileList = false;
  private boolean isLeftToRight = true;
  protected static final int modelChanged = 1;
  protected static final int selectionModelChanged = 2;
  protected static final int fontChanged = 4;
  protected static final int fixedCellWidthChanged = 8;
  protected static final int fixedCellHeightChanged = 16;
  protected static final int prototypeCellValueChanged = 32;
  protected static final int cellRendererChanged = 64;
  private static final int layoutOrientationChanged = 128;
  private static final int heightChanged = 256;
  private static final int widthChanged = 512;
  private static final int componentOrientationChanged = 1024;
  private static final int DROP_LINE_THICKNESS = 2;
  private static final int CHANGE_LEAD = 0;
  private static final int CHANGE_SELECTION = 1;
  private static final int EXTEND_SELECTION = 2;
  private static final TransferHandler defaultTransferHandler = new ListTransferHandler();
  
  public BasicListUI() {}
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("selectPreviousColumn"));
    paramLazyActionMap.put(new Actions("selectPreviousColumnExtendSelection"));
    paramLazyActionMap.put(new Actions("selectPreviousColumnChangeLead"));
    paramLazyActionMap.put(new Actions("selectNextColumn"));
    paramLazyActionMap.put(new Actions("selectNextColumnExtendSelection"));
    paramLazyActionMap.put(new Actions("selectNextColumnChangeLead"));
    paramLazyActionMap.put(new Actions("selectPreviousRow"));
    paramLazyActionMap.put(new Actions("selectPreviousRowExtendSelection"));
    paramLazyActionMap.put(new Actions("selectPreviousRowChangeLead"));
    paramLazyActionMap.put(new Actions("selectNextRow"));
    paramLazyActionMap.put(new Actions("selectNextRowExtendSelection"));
    paramLazyActionMap.put(new Actions("selectNextRowChangeLead"));
    paramLazyActionMap.put(new Actions("selectFirstRow"));
    paramLazyActionMap.put(new Actions("selectFirstRowExtendSelection"));
    paramLazyActionMap.put(new Actions("selectFirstRowChangeLead"));
    paramLazyActionMap.put(new Actions("selectLastRow"));
    paramLazyActionMap.put(new Actions("selectLastRowExtendSelection"));
    paramLazyActionMap.put(new Actions("selectLastRowChangeLead"));
    paramLazyActionMap.put(new Actions("scrollUp"));
    paramLazyActionMap.put(new Actions("scrollUpExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollUpChangeLead"));
    paramLazyActionMap.put(new Actions("scrollDown"));
    paramLazyActionMap.put(new Actions("scrollDownExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollDownChangeLead"));
    paramLazyActionMap.put(new Actions("selectAll"));
    paramLazyActionMap.put(new Actions("clearSelection"));
    paramLazyActionMap.put(new Actions("addToSelection"));
    paramLazyActionMap.put(new Actions("toggleAndAnchor"));
    paramLazyActionMap.put(new Actions("extendTo"));
    paramLazyActionMap.put(new Actions("moveSelectionTo"));
    paramLazyActionMap.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
    paramLazyActionMap.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
    paramLazyActionMap.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
  }
  
  protected void paintCell(Graphics paramGraphics, int paramInt1, Rectangle paramRectangle, ListCellRenderer paramListCellRenderer, ListModel paramListModel, ListSelectionModel paramListSelectionModel, int paramInt2)
  {
    Object localObject = paramListModel.getElementAt(paramInt1);
    boolean bool1 = (list.hasFocus()) && (paramInt1 == paramInt2);
    boolean bool2 = paramListSelectionModel.isSelectedIndex(paramInt1);
    Component localComponent = paramListCellRenderer.getListCellRendererComponent(list, localObject, paramInt1, bool2, bool1);
    int i = x;
    int j = y;
    int k = width;
    int m = height;
    if (isFileList)
    {
      int n = Math.min(k, getPreferredSizewidth + 4);
      if (!isLeftToRight) {
        i += k - n;
      }
      k = n;
    }
    rendererPane.paintComponent(paramGraphics, localComponent, list, i, j, k, m, true);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    Shape localShape = paramGraphics.getClip();
    paintImpl(paramGraphics, paramJComponent);
    paramGraphics.setClip(localShape);
    paintDropLine(paramGraphics);
  }
  
  private void paintImpl(Graphics paramGraphics, JComponent paramJComponent)
  {
    switch (layoutOrientation)
    {
    case 1: 
      if (list.getHeight() != listHeight)
      {
        updateLayoutStateNeeded |= 0x100;
        redrawList();
      }
      break;
    case 2: 
      if (list.getWidth() != listWidth)
      {
        updateLayoutStateNeeded |= 0x200;
        redrawList();
      }
      break;
    }
    maybeUpdateLayoutState();
    ListCellRenderer localListCellRenderer = list.getCellRenderer();
    ListModel localListModel = list.getModel();
    ListSelectionModel localListSelectionModel = list.getSelectionModel();
    int i;
    if ((localListCellRenderer == null) || ((i = localListModel.getSize()) == 0)) {
      return;
    }
    Rectangle localRectangle1 = paramGraphics.getClipBounds();
    int j;
    int k;
    if (paramJComponent.getComponentOrientation().isLeftToRight())
    {
      j = convertLocationToColumn(x, y);
      k = convertLocationToColumn(x + width, y);
    }
    else
    {
      j = convertLocationToColumn(x + width, y);
      k = convertLocationToColumn(x, y);
    }
    int m = y + height;
    int n = adjustIndex(list.getLeadSelectionIndex(), list);
    int i1 = layoutOrientation == 2 ? columnCount : 1;
    for (int i2 = j; i2 <= k; i2++)
    {
      int i3 = convertLocationToRowInColumn(y, i2);
      int i4 = getRowCount(i2);
      int i5 = getModelIndex(i2, i3);
      Rectangle localRectangle2 = getCellBounds(list, i5, i5);
      if (localRectangle2 == null) {
        return;
      }
      while ((i3 < i4) && (y < m) && (i5 < i))
      {
        height = getHeight(i2, i3);
        paramGraphics.setClip(x, y, width, height);
        paramGraphics.clipRect(x, y, width, height);
        paintCell(paramGraphics, i5, localRectangle2, localListCellRenderer, localListModel, localListSelectionModel, n);
        y += height;
        i5 += i1;
        i3++;
      }
    }
    rendererPane.removeAll();
  }
  
  private void paintDropLine(Graphics paramGraphics)
  {
    JList.DropLocation localDropLocation = list.getDropLocation();
    if ((localDropLocation == null) || (!localDropLocation.isInsert())) {
      return;
    }
    Color localColor = DefaultLookup.getColor(list, this, "List.dropLineColor", null);
    if (localColor != null)
    {
      paramGraphics.setColor(localColor);
      Rectangle localRectangle = getDropLineRect(localDropLocation);
      paramGraphics.fillRect(x, y, width, height);
    }
  }
  
  private Rectangle getDropLineRect(JList.DropLocation paramDropLocation)
  {
    int i = list.getModel().getSize();
    if (i == 0)
    {
      localObject = list.getInsets();
      if (layoutOrientation == 2)
      {
        if (isLeftToRight) {
          return new Rectangle(left, top, 2, 20);
        }
        return new Rectangle(list.getWidth() - 2 - right, top, 2, 20);
      }
      return new Rectangle(left, top, list.getWidth() - left - right, 2);
    }
    Object localObject = null;
    int j = paramDropLocation.getIndex();
    int k = 0;
    Rectangle localRectangle1;
    Rectangle localRectangle2;
    Point localPoint;
    if (layoutOrientation == 2)
    {
      if (j == i)
      {
        k = 1;
      }
      else if ((j != 0) && (convertModelToRow(j) != convertModelToRow(j - 1)))
      {
        localRectangle1 = getCellBounds(list, j - 1);
        localRectangle2 = getCellBounds(list, j);
        localPoint = paramDropLocation.getDropPoint();
        if (isLeftToRight) {
          k = Point2D.distance(x + width, y + (int)(height / 2.0D), x, y) < Point2D.distance(x, y + (int)(height / 2.0D), x, y) ? 1 : 0;
        } else {
          k = Point2D.distance(x, y + (int)(height / 2.0D), x, y) < Point2D.distance(x + width, y + (int)(height / 2.0D), x, y) ? 1 : 0;
        }
      }
      if (k != 0)
      {
        j--;
        localObject = getCellBounds(list, j);
        if (isLeftToRight) {
          x += width;
        } else {
          x -= 2;
        }
      }
      else
      {
        localObject = getCellBounds(list, j);
        if (!isLeftToRight) {
          x += width - 2;
        }
      }
      if (x >= list.getWidth()) {
        x = (list.getWidth() - 2);
      } else if (x < 0) {
        x = 0;
      }
      width = 2;
    }
    else if (layoutOrientation == 1)
    {
      if (j == i)
      {
        j--;
        localObject = getCellBounds(list, j);
        y += height;
      }
      else if ((j != 0) && (convertModelToColumn(j) != convertModelToColumn(j - 1)))
      {
        localRectangle1 = getCellBounds(list, j - 1);
        localRectangle2 = getCellBounds(list, j);
        localPoint = paramDropLocation.getDropPoint();
        if (Point2D.distance(x + (int)(width / 2.0D), y + height, x, y) < Point2D.distance(x + (int)(width / 2.0D), y, x, y))
        {
          j--;
          localObject = getCellBounds(list, j);
          y += height;
        }
        else
        {
          localObject = getCellBounds(list, j);
        }
      }
      else
      {
        localObject = getCellBounds(list, j);
      }
      if (y >= list.getHeight()) {
        y = (list.getHeight() - 2);
      }
      height = 2;
    }
    else
    {
      if (j == i)
      {
        j--;
        localObject = getCellBounds(list, j);
        y += height;
      }
      else
      {
        localObject = getCellBounds(list, j);
      }
      if (y >= list.getHeight()) {
        y = (list.getHeight() - 2);
      }
      height = 2;
    }
    return (Rectangle)localObject;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    int i = list.getFixedCellHeight();
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    Component localComponent = (Component)localUIDefaults.get(BASELINE_COMPONENT_KEY);
    if (localComponent == null)
    {
      Object localObject = (ListCellRenderer)UIManager.get("List.cellRenderer");
      if (localObject == null) {
        localObject = new DefaultListCellRenderer();
      }
      localComponent = ((ListCellRenderer)localObject).getListCellRendererComponent(list, "a", -1, false, false);
      localUIDefaults.put(BASELINE_COMPONENT_KEY, localComponent);
    }
    localComponent.setFont(list.getFont());
    if (i == -1) {
      i = getPreferredSizeheight;
    }
    return localComponent.getBaseline(Integer.MAX_VALUE, i) + list.getInsets().top;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    maybeUpdateLayoutState();
    int i = list.getModel().getSize() - 1;
    if (i < 0) {
      return new Dimension(0, 0);
    }
    Insets localInsets = list.getInsets();
    int j = cellWidth * columnCount + left + right;
    int k;
    if (layoutOrientation != 0)
    {
      k = preferredHeight;
    }
    else
    {
      Rectangle localRectangle = getCellBounds(list, i);
      if (localRectangle != null) {
        k = y + height + bottom;
      } else {
        k = 0;
      }
    }
    return new Dimension(j, k);
  }
  
  protected void selectPreviousIndex()
  {
    int i = list.getSelectedIndex();
    if (i > 0)
    {
      i--;
      list.setSelectedIndex(i);
      list.ensureIndexIsVisible(i);
    }
  }
  
  protected void selectNextIndex()
  {
    int i = list.getSelectedIndex();
    if (i + 1 < list.getModel().getSize())
    {
      i++;
      list.setSelectedIndex(i);
      list.ensureIndexIsVisible(i);
    }
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(list, 0, localInputMap);
    LazyActionMap.installLazyActionMap(list, BasicListUI.class, "List.actionMap");
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 0)
    {
      InputMap localInputMap1 = (InputMap)DefaultLookup.get(list, this, "List.focusInputMap");
      InputMap localInputMap2;
      if ((isLeftToRight) || ((localInputMap2 = (InputMap)DefaultLookup.get(list, this, "List.focusInputMap.RightToLeft")) == null)) {
        return localInputMap1;
      }
      localInputMap2.setParent(localInputMap1);
      return localInputMap2;
    }
    return null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIActionMap(list, null);
    SwingUtilities.replaceUIInputMap(list, 0, null);
  }
  
  protected void installListeners()
  {
    TransferHandler localTransferHandler = list.getTransferHandler();
    if ((localTransferHandler == null) || ((localTransferHandler instanceof UIResource)))
    {
      list.setTransferHandler(defaultTransferHandler);
      if ((list.getDropTarget() instanceof UIResource)) {
        list.setDropTarget(null);
      }
    }
    focusListener = createFocusListener();
    mouseInputListener = createMouseInputListener();
    propertyChangeListener = createPropertyChangeListener();
    listSelectionListener = createListSelectionListener();
    listDataListener = createListDataListener();
    list.addFocusListener(focusListener);
    list.addMouseListener(mouseInputListener);
    list.addMouseMotionListener(mouseInputListener);
    list.addPropertyChangeListener(propertyChangeListener);
    list.addKeyListener(getHandler());
    ListModel localListModel = list.getModel();
    if (localListModel != null) {
      localListModel.addListDataListener(listDataListener);
    }
    ListSelectionModel localListSelectionModel = list.getSelectionModel();
    if (localListSelectionModel != null) {
      localListSelectionModel.addListSelectionListener(listSelectionListener);
    }
  }
  
  protected void uninstallListeners()
  {
    list.removeFocusListener(focusListener);
    list.removeMouseListener(mouseInputListener);
    list.removeMouseMotionListener(mouseInputListener);
    list.removePropertyChangeListener(propertyChangeListener);
    list.removeKeyListener(getHandler());
    ListModel localListModel = list.getModel();
    if (localListModel != null) {
      localListModel.removeListDataListener(listDataListener);
    }
    ListSelectionModel localListSelectionModel = list.getSelectionModel();
    if (localListSelectionModel != null) {
      localListSelectionModel.removeListSelectionListener(listSelectionListener);
    }
    focusListener = null;
    mouseInputListener = null;
    listSelectionListener = null;
    listDataListener = null;
    propertyChangeListener = null;
    handler = null;
  }
  
  protected void installDefaults()
  {
    list.setLayout(null);
    LookAndFeel.installBorder(list, "List.border");
    LookAndFeel.installColorsAndFont(list, "List.background", "List.foreground", "List.font");
    LookAndFeel.installProperty(list, "opaque", Boolean.TRUE);
    if (list.getCellRenderer() == null) {
      list.setCellRenderer((ListCellRenderer)UIManager.get("List.cellRenderer"));
    }
    Color localColor1 = list.getSelectionBackground();
    if ((localColor1 == null) || ((localColor1 instanceof UIResource))) {
      list.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
    }
    Color localColor2 = list.getSelectionForeground();
    if ((localColor2 == null) || ((localColor2 instanceof UIResource))) {
      list.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
    }
    Long localLong = (Long)UIManager.get("List.timeFactor");
    timeFactor = (localLong != null ? localLong.longValue() : 1000L);
    updateIsFileList();
  }
  
  private void updateIsFileList()
  {
    boolean bool = Boolean.TRUE.equals(list.getClientProperty("List.isFileList"));
    if (bool != isFileList)
    {
      isFileList = bool;
      Font localFont1 = list.getFont();
      if ((localFont1 == null) || ((localFont1 instanceof UIResource)))
      {
        Font localFont2 = UIManager.getFont(bool ? "FileChooser.listFont" : "List.font");
        if ((localFont2 != null) && (localFont2 != localFont1)) {
          list.setFont(localFont2);
        }
      }
    }
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(list);
    if ((list.getFont() instanceof UIResource)) {
      list.setFont(null);
    }
    if ((list.getForeground() instanceof UIResource)) {
      list.setForeground(null);
    }
    if ((list.getBackground() instanceof UIResource)) {
      list.setBackground(null);
    }
    if ((list.getSelectionBackground() instanceof UIResource)) {
      list.setSelectionBackground(null);
    }
    if ((list.getSelectionForeground() instanceof UIResource)) {
      list.setSelectionForeground(null);
    }
    if ((list.getCellRenderer() instanceof UIResource)) {
      list.setCellRenderer(null);
    }
    if ((list.getTransferHandler() instanceof UIResource)) {
      list.setTransferHandler(null);
    }
  }
  
  public void installUI(JComponent paramJComponent)
  {
    list = ((JList)paramJComponent);
    layoutOrientation = list.getLayoutOrientation();
    rendererPane = new CellRendererPane();
    list.add(rendererPane);
    columnCount = 1;
    updateLayoutStateNeeded = 1;
    isLeftToRight = list.getComponentOrientation().isLeftToRight();
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallListeners();
    uninstallDefaults();
    uninstallKeyboardActions();
    cellWidth = (cellHeight = -1);
    cellHeights = null;
    listWidth = (listHeight = -1);
    list.remove(rendererPane);
    rendererPane = null;
    list = null;
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicListUI();
  }
  
  public int locationToIndex(JList paramJList, Point paramPoint)
  {
    maybeUpdateLayoutState();
    return convertLocationToModel(x, y);
  }
  
  public Point indexToLocation(JList paramJList, int paramInt)
  {
    maybeUpdateLayoutState();
    Rectangle localRectangle = getCellBounds(paramJList, paramInt, paramInt);
    if (localRectangle != null) {
      return new Point(x, y);
    }
    return null;
  }
  
  public Rectangle getCellBounds(JList paramJList, int paramInt1, int paramInt2)
  {
    maybeUpdateLayoutState();
    int i = Math.min(paramInt1, paramInt2);
    int j = Math.max(paramInt1, paramInt2);
    if (i >= paramJList.getModel().getSize()) {
      return null;
    }
    Rectangle localRectangle1 = getCellBounds(paramJList, i);
    if (localRectangle1 == null) {
      return null;
    }
    if (i == j) {
      return localRectangle1;
    }
    Rectangle localRectangle2 = getCellBounds(paramJList, j);
    if (localRectangle2 != null)
    {
      if (layoutOrientation == 2)
      {
        int k = convertModelToRow(i);
        int m = convertModelToRow(j);
        if (k != m)
        {
          x = 0;
          width = paramJList.getWidth();
        }
      }
      else if (x != x)
      {
        y = 0;
        height = paramJList.getHeight();
      }
      localRectangle1.add(localRectangle2);
    }
    return localRectangle1;
  }
  
  private Rectangle getCellBounds(JList paramJList, int paramInt)
  {
    maybeUpdateLayoutState();
    int i = convertModelToRow(paramInt);
    int j = convertModelToColumn(paramInt);
    if ((i == -1) || (j == -1)) {
      return null;
    }
    Insets localInsets = paramJList.getInsets();
    int m = cellWidth;
    int n = top;
    int k;
    int i1;
    switch (layoutOrientation)
    {
    case 1: 
    case 2: 
      if (isLeftToRight) {
        k = left + j * cellWidth;
      } else {
        k = paramJList.getWidth() - right - (j + 1) * cellWidth;
      }
      n += cellHeight * i;
      i1 = cellHeight;
      break;
    default: 
      k = left;
      if (cellHeights == null) {
        n += cellHeight * i;
      } else if (i >= cellHeights.length) {
        n = 0;
      } else {
        for (int i2 = 0; i2 < i; i2++) {
          n += cellHeights[i2];
        }
      }
      m = paramJList.getWidth() - (left + right);
      i1 = getRowHeight(paramInt);
    }
    return new Rectangle(k, n, m, i1);
  }
  
  protected int getRowHeight(int paramInt)
  {
    return getHeight(0, paramInt);
  }
  
  protected int convertYToRow(int paramInt)
  {
    return convertLocationToRow(0, paramInt, false);
  }
  
  protected int convertRowToY(int paramInt)
  {
    if ((paramInt >= getRowCount(0)) || (paramInt < 0)) {
      return -1;
    }
    Rectangle localRectangle = getCellBounds(list, paramInt, paramInt);
    return y;
  }
  
  private int getHeight(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > columnCount) || (paramInt2 < 0)) {
      return -1;
    }
    if (layoutOrientation != 0) {
      return cellHeight;
    }
    if (paramInt2 >= list.getModel().getSize()) {
      return -1;
    }
    return paramInt2 < cellHeights.length ? cellHeights[paramInt2] : cellHeights == null ? cellHeight : -1;
  }
  
  private int convertLocationToRow(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = list.getModel().getSize();
    if (i <= 0) {
      return -1;
    }
    Insets localInsets = list.getInsets();
    if (cellHeights == null)
    {
      j = cellHeight == 0 ? 0 : (paramInt2 - top) / cellHeight;
      if (paramBoolean) {
        if (j < 0) {
          j = 0;
        } else if (j >= i) {
          j = i - 1;
        }
      }
      return j;
    }
    if (i > cellHeights.length) {
      return -1;
    }
    int j = top;
    int k = 0;
    if ((paramBoolean) && (paramInt2 < j)) {
      return 0;
    }
    for (int m = 0; m < i; m++)
    {
      if ((paramInt2 >= j) && (paramInt2 < j + cellHeights[m])) {
        return k;
      }
      j += cellHeights[m];
      k++;
    }
    return m - 1;
  }
  
  private int convertLocationToRowInColumn(int paramInt1, int paramInt2)
  {
    int i = 0;
    if (layoutOrientation != 0) {
      if (isLeftToRight) {
        i = paramInt2 * cellWidth;
      } else {
        i = list.getWidth() - (paramInt2 + 1) * cellWidth - list.getInsets().right;
      }
    }
    return convertLocationToRow(i, paramInt1, true);
  }
  
  private int convertLocationToModel(int paramInt1, int paramInt2)
  {
    int i = convertLocationToRow(paramInt1, paramInt2, true);
    int j = convertLocationToColumn(paramInt1, paramInt2);
    if ((i >= 0) && (j >= 0)) {
      return getModelIndex(j, i);
    }
    return -1;
  }
  
  private int getRowCount(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= columnCount)) {
      return -1;
    }
    if ((layoutOrientation == 0) || ((paramInt == 0) && (columnCount == 1))) {
      return list.getModel().getSize();
    }
    if (paramInt >= columnCount) {
      return -1;
    }
    if (layoutOrientation == 1)
    {
      if (paramInt < columnCount - 1) {
        return rowsPerColumn;
      }
      return list.getModel().getSize() - (columnCount - 1) * rowsPerColumn;
    }
    int i = columnCount - (columnCount * rowsPerColumn - list.getModel().getSize());
    if (paramInt >= i) {
      return Math.max(0, rowsPerColumn - 1);
    }
    return rowsPerColumn;
  }
  
  private int getModelIndex(int paramInt1, int paramInt2)
  {
    switch (layoutOrientation)
    {
    case 1: 
      return Math.min(list.getModel().getSize() - 1, rowsPerColumn * paramInt1 + Math.min(paramInt2, rowsPerColumn - 1));
    case 2: 
      return Math.min(list.getModel().getSize() - 1, paramInt2 * columnCount + paramInt1);
    }
    return paramInt2;
  }
  
  private int convertLocationToColumn(int paramInt1, int paramInt2)
  {
    if (cellWidth > 0)
    {
      if (layoutOrientation == 0) {
        return 0;
      }
      Insets localInsets = list.getInsets();
      int i;
      if (isLeftToRight) {
        i = (paramInt1 - left) / cellWidth;
      } else {
        i = (list.getWidth() - paramInt1 - right - 1) / cellWidth;
      }
      if (i < 0) {
        return 0;
      }
      if (i >= columnCount) {
        return columnCount - 1;
      }
      return i;
    }
    return 0;
  }
  
  private int convertModelToRow(int paramInt)
  {
    int i = list.getModel().getSize();
    if ((paramInt < 0) || (paramInt >= i)) {
      return -1;
    }
    if ((layoutOrientation != 0) && (columnCount > 1) && (rowsPerColumn > 0))
    {
      if (layoutOrientation == 1) {
        return paramInt % rowsPerColumn;
      }
      return paramInt / columnCount;
    }
    return paramInt;
  }
  
  private int convertModelToColumn(int paramInt)
  {
    int i = list.getModel().getSize();
    if ((paramInt < 0) || (paramInt >= i)) {
      return -1;
    }
    if ((layoutOrientation != 0) && (rowsPerColumn > 0) && (columnCount > 1))
    {
      if (layoutOrientation == 1) {
        return paramInt / rowsPerColumn;
      }
      return paramInt % columnCount;
    }
    return 0;
  }
  
  protected void maybeUpdateLayoutState()
  {
    if (updateLayoutStateNeeded != 0)
    {
      updateLayoutState();
      updateLayoutStateNeeded = 0;
    }
  }
  
  protected void updateLayoutState()
  {
    int i = list.getFixedCellHeight();
    int j = list.getFixedCellWidth();
    cellWidth = (j != -1 ? j : -1);
    if (i != -1)
    {
      cellHeight = i;
      cellHeights = null;
    }
    else
    {
      cellHeight = -1;
      cellHeights = new int[list.getModel().getSize()];
    }
    if ((j == -1) || (i == -1))
    {
      ListModel localListModel = list.getModel();
      int k = localListModel.getSize();
      ListCellRenderer localListCellRenderer = list.getCellRenderer();
      int m;
      if (localListCellRenderer != null)
      {
        for (m = 0; m < k; m++)
        {
          Object localObject = localListModel.getElementAt(m);
          Component localComponent = localListCellRenderer.getListCellRendererComponent(list, localObject, m, false, false);
          rendererPane.add(localComponent);
          Dimension localDimension = localComponent.getPreferredSize();
          if (j == -1) {
            cellWidth = Math.max(width, cellWidth);
          }
          if (i == -1) {
            cellHeights[m] = height;
          }
        }
      }
      else
      {
        if (cellWidth == -1) {
          cellWidth = 0;
        }
        if (cellHeights == null) {
          cellHeights = new int[k];
        }
        for (m = 0; m < k; m++) {
          cellHeights[m] = 0;
        }
      }
    }
    columnCount = 1;
    if (layoutOrientation != 0) {
      updateHorizontalLayoutState(j, i);
    }
  }
  
  private void updateHorizontalLayoutState(int paramInt1, int paramInt2)
  {
    int i = list.getVisibleRowCount();
    int j = list.getModel().getSize();
    Insets localInsets = list.getInsets();
    listHeight = list.getHeight();
    listWidth = list.getWidth();
    if (j == 0)
    {
      rowsPerColumn = (columnCount = 0);
      preferredHeight = (top + bottom);
      return;
    }
    int k;
    if (paramInt2 != -1)
    {
      k = paramInt2;
    }
    else
    {
      int m = 0;
      if (cellHeights.length > 0)
      {
        m = cellHeights[(cellHeights.length - 1)];
        for (int n = cellHeights.length - 2; n >= 0; n--) {
          m = Math.max(m, cellHeights[n]);
        }
      }
      k = cellHeight = m;
      cellHeights = null;
    }
    rowsPerColumn = j;
    if (i > 0)
    {
      rowsPerColumn = i;
      columnCount = Math.max(1, j / rowsPerColumn);
      if ((j > 0) && (j > rowsPerColumn) && (j % rowsPerColumn != 0)) {
        columnCount += 1;
      }
      if (layoutOrientation == 2)
      {
        rowsPerColumn = (j / columnCount);
        if (j % columnCount > 0) {
          rowsPerColumn += 1;
        }
      }
    }
    else if ((layoutOrientation == 1) && (k != 0))
    {
      rowsPerColumn = Math.max(1, (listHeight - top - bottom) / k);
      columnCount = Math.max(1, j / rowsPerColumn);
      if ((j > 0) && (j > rowsPerColumn) && (j % rowsPerColumn != 0)) {
        columnCount += 1;
      }
    }
    else if ((layoutOrientation == 2) && (cellWidth > 0) && (listWidth > 0))
    {
      columnCount = Math.max(1, (listWidth - left - right) / cellWidth);
      rowsPerColumn = (j / columnCount);
      if (j % columnCount > 0) {
        rowsPerColumn += 1;
      }
    }
    preferredHeight = (rowsPerColumn * cellHeight + top + bottom);
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected MouseInputListener createMouseInputListener()
  {
    return getHandler();
  }
  
  protected FocusListener createFocusListener()
  {
    return getHandler();
  }
  
  protected ListSelectionListener createListSelectionListener()
  {
    return getHandler();
  }
  
  private void redrawList()
  {
    list.revalidate();
    list.repaint();
  }
  
  protected ListDataListener createListDataListener()
  {
    return getHandler();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  private static int adjustIndex(int paramInt, JList paramJList)
  {
    return paramInt < paramJList.getModel().getSize() ? paramInt : -1;
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String SELECT_PREVIOUS_COLUMN = "selectPreviousColumn";
    private static final String SELECT_PREVIOUS_COLUMN_EXTEND = "selectPreviousColumnExtendSelection";
    private static final String SELECT_PREVIOUS_COLUMN_CHANGE_LEAD = "selectPreviousColumnChangeLead";
    private static final String SELECT_NEXT_COLUMN = "selectNextColumn";
    private static final String SELECT_NEXT_COLUMN_EXTEND = "selectNextColumnExtendSelection";
    private static final String SELECT_NEXT_COLUMN_CHANGE_LEAD = "selectNextColumnChangeLead";
    private static final String SELECT_PREVIOUS_ROW = "selectPreviousRow";
    private static final String SELECT_PREVIOUS_ROW_EXTEND = "selectPreviousRowExtendSelection";
    private static final String SELECT_PREVIOUS_ROW_CHANGE_LEAD = "selectPreviousRowChangeLead";
    private static final String SELECT_NEXT_ROW = "selectNextRow";
    private static final String SELECT_NEXT_ROW_EXTEND = "selectNextRowExtendSelection";
    private static final String SELECT_NEXT_ROW_CHANGE_LEAD = "selectNextRowChangeLead";
    private static final String SELECT_FIRST_ROW = "selectFirstRow";
    private static final String SELECT_FIRST_ROW_EXTEND = "selectFirstRowExtendSelection";
    private static final String SELECT_FIRST_ROW_CHANGE_LEAD = "selectFirstRowChangeLead";
    private static final String SELECT_LAST_ROW = "selectLastRow";
    private static final String SELECT_LAST_ROW_EXTEND = "selectLastRowExtendSelection";
    private static final String SELECT_LAST_ROW_CHANGE_LEAD = "selectLastRowChangeLead";
    private static final String SCROLL_UP = "scrollUp";
    private static final String SCROLL_UP_EXTEND = "scrollUpExtendSelection";
    private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
    private static final String SCROLL_DOWN = "scrollDown";
    private static final String SCROLL_DOWN_EXTEND = "scrollDownExtendSelection";
    private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
    private static final String SELECT_ALL = "selectAll";
    private static final String CLEAR_SELECTION = "clearSelection";
    private static final String ADD_TO_SELECTION = "addToSelection";
    private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
    private static final String EXTEND_TO = "extendTo";
    private static final String MOVE_SELECTION_TO = "moveSelectionTo";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      String str = getName();
      JList localJList = (JList)paramActionEvent.getSource();
      BasicListUI localBasicListUI = (BasicListUI)BasicLookAndFeel.getUIOfType(localJList.getUI(), BasicListUI.class);
      if (str == "selectPreviousColumn")
      {
        changeSelection(localJList, 1, getNextColumnIndex(localJList, localBasicListUI, -1), -1);
      }
      else if (str == "selectPreviousColumnExtendSelection")
      {
        changeSelection(localJList, 2, getNextColumnIndex(localJList, localBasicListUI, -1), -1);
      }
      else if (str == "selectPreviousColumnChangeLead")
      {
        changeSelection(localJList, 0, getNextColumnIndex(localJList, localBasicListUI, -1), -1);
      }
      else if (str == "selectNextColumn")
      {
        changeSelection(localJList, 1, getNextColumnIndex(localJList, localBasicListUI, 1), 1);
      }
      else if (str == "selectNextColumnExtendSelection")
      {
        changeSelection(localJList, 2, getNextColumnIndex(localJList, localBasicListUI, 1), 1);
      }
      else if (str == "selectNextColumnChangeLead")
      {
        changeSelection(localJList, 0, getNextColumnIndex(localJList, localBasicListUI, 1), 1);
      }
      else if (str == "selectPreviousRow")
      {
        changeSelection(localJList, 1, getNextIndex(localJList, localBasicListUI, -1), -1);
      }
      else if (str == "selectPreviousRowExtendSelection")
      {
        changeSelection(localJList, 2, getNextIndex(localJList, localBasicListUI, -1), -1);
      }
      else if (str == "selectPreviousRowChangeLead")
      {
        changeSelection(localJList, 0, getNextIndex(localJList, localBasicListUI, -1), -1);
      }
      else if (str == "selectNextRow")
      {
        changeSelection(localJList, 1, getNextIndex(localJList, localBasicListUI, 1), 1);
      }
      else if (str == "selectNextRowExtendSelection")
      {
        changeSelection(localJList, 2, getNextIndex(localJList, localBasicListUI, 1), 1);
      }
      else if (str == "selectNextRowChangeLead")
      {
        changeSelection(localJList, 0, getNextIndex(localJList, localBasicListUI, 1), 1);
      }
      else if (str == "selectFirstRow")
      {
        changeSelection(localJList, 1, 0, -1);
      }
      else if (str == "selectFirstRowExtendSelection")
      {
        changeSelection(localJList, 2, 0, -1);
      }
      else if (str == "selectFirstRowChangeLead")
      {
        changeSelection(localJList, 0, 0, -1);
      }
      else if (str == "selectLastRow")
      {
        changeSelection(localJList, 1, localJList.getModel().getSize() - 1, 1);
      }
      else if (str == "selectLastRowExtendSelection")
      {
        changeSelection(localJList, 2, localJList.getModel().getSize() - 1, 1);
      }
      else if (str == "selectLastRowChangeLead")
      {
        changeSelection(localJList, 0, localJList.getModel().getSize() - 1, 1);
      }
      else if (str == "scrollUp")
      {
        changeSelection(localJList, 1, getNextPageIndex(localJList, -1), -1);
      }
      else if (str == "scrollUpExtendSelection")
      {
        changeSelection(localJList, 2, getNextPageIndex(localJList, -1), -1);
      }
      else if (str == "scrollUpChangeLead")
      {
        changeSelection(localJList, 0, getNextPageIndex(localJList, -1), -1);
      }
      else if (str == "scrollDown")
      {
        changeSelection(localJList, 1, getNextPageIndex(localJList, 1), 1);
      }
      else if (str == "scrollDownExtendSelection")
      {
        changeSelection(localJList, 2, getNextPageIndex(localJList, 1), 1);
      }
      else if (str == "scrollDownChangeLead")
      {
        changeSelection(localJList, 0, getNextPageIndex(localJList, 1), 1);
      }
      else if (str == "selectAll")
      {
        selectAll(localJList);
      }
      else if (str == "clearSelection")
      {
        clearSelection(localJList);
      }
      else
      {
        int i;
        if (str == "addToSelection")
        {
          i = BasicListUI.adjustIndex(localJList.getSelectionModel().getLeadSelectionIndex(), localJList);
          if (!localJList.isSelectedIndex(i))
          {
            int j = localJList.getSelectionModel().getAnchorSelectionIndex();
            localJList.setValueIsAdjusting(true);
            localJList.addSelectionInterval(i, i);
            localJList.getSelectionModel().setAnchorSelectionIndex(j);
            localJList.setValueIsAdjusting(false);
          }
        }
        else if (str == "toggleAndAnchor")
        {
          i = BasicListUI.adjustIndex(localJList.getSelectionModel().getLeadSelectionIndex(), localJList);
          if (localJList.isSelectedIndex(i)) {
            localJList.removeSelectionInterval(i, i);
          } else {
            localJList.addSelectionInterval(i, i);
          }
        }
        else if (str == "extendTo")
        {
          changeSelection(localJList, 2, BasicListUI.adjustIndex(localJList.getSelectionModel().getLeadSelectionIndex(), localJList), 0);
        }
        else if (str == "moveSelectionTo")
        {
          changeSelection(localJList, 1, BasicListUI.adjustIndex(localJList.getSelectionModel().getLeadSelectionIndex(), localJList), 0);
        }
      }
    }
    
    public boolean isEnabled(Object paramObject)
    {
      String str = getName();
      if ((str == "selectPreviousColumnChangeLead") || (str == "selectNextColumnChangeLead") || (str == "selectPreviousRowChangeLead") || (str == "selectNextRowChangeLead") || (str == "selectFirstRowChangeLead") || (str == "selectLastRowChangeLead") || (str == "scrollUpChangeLead") || (str == "scrollDownChangeLead")) {
        return (paramObject != null) && ((((JList)paramObject).getSelectionModel() instanceof DefaultListSelectionModel));
      }
      return true;
    }
    
    private void clearSelection(JList paramJList)
    {
      paramJList.clearSelection();
    }
    
    private void selectAll(JList paramJList)
    {
      int i = paramJList.getModel().getSize();
      if (i > 0)
      {
        ListSelectionModel localListSelectionModel = paramJList.getSelectionModel();
        int j = BasicListUI.adjustIndex(localListSelectionModel.getLeadSelectionIndex(), paramJList);
        int k;
        if (localListSelectionModel.getSelectionMode() == 0)
        {
          if (j == -1)
          {
            k = BasicListUI.adjustIndex(paramJList.getMinSelectionIndex(), paramJList);
            j = k == -1 ? 0 : k;
          }
          paramJList.setSelectionInterval(j, j);
          paramJList.ensureIndexIsVisible(j);
        }
        else
        {
          paramJList.setValueIsAdjusting(true);
          k = BasicListUI.adjustIndex(localListSelectionModel.getAnchorSelectionIndex(), paramJList);
          paramJList.setSelectionInterval(0, i - 1);
          SwingUtilities2.setLeadAnchorWithoutSelection(localListSelectionModel, k, j);
          paramJList.setValueIsAdjusting(false);
        }
      }
    }
    
    private int getNextPageIndex(JList paramJList, int paramInt)
    {
      if (paramJList.getModel().getSize() == 0) {
        return -1;
      }
      int i = -1;
      Rectangle localRectangle1 = paramJList.getVisibleRect();
      ListSelectionModel localListSelectionModel = paramJList.getSelectionModel();
      int j = BasicListUI.adjustIndex(localListSelectionModel.getLeadSelectionIndex(), paramJList);
      Rectangle localRectangle2 = j == -1 ? new Rectangle() : paramJList.getCellBounds(j, j);
      Point localPoint;
      Rectangle localRectangle3;
      if ((paramJList.getLayoutOrientation() == 1) && (paramJList.getVisibleRowCount() <= 0))
      {
        if (!paramJList.getComponentOrientation().isLeftToRight()) {
          paramInt = -paramInt;
        }
        if (paramInt < 0)
        {
          x = (x + width - width);
          localPoint = new Point(x - 1, y);
          i = paramJList.locationToIndex(localPoint);
          localRectangle3 = paramJList.getCellBounds(i, i);
          if (localRectangle1.intersects(localRectangle3))
          {
            x = (x - 1);
            i = paramJList.locationToIndex(localPoint);
            localRectangle3 = paramJList.getCellBounds(i, i);
          }
          if (y != y)
          {
            x = (x + width);
            i = paramJList.locationToIndex(localPoint);
          }
        }
        else
        {
          x = x;
          localPoint = new Point(x + width, y);
          i = paramJList.locationToIndex(localPoint);
          localRectangle3 = paramJList.getCellBounds(i, i);
          if (localRectangle1.intersects(localRectangle3))
          {
            x = (x + width);
            i = paramJList.locationToIndex(localPoint);
            localRectangle3 = paramJList.getCellBounds(i, i);
          }
          if (y != y)
          {
            x = (x - 1);
            i = paramJList.locationToIndex(localPoint);
          }
        }
      }
      else if (paramInt < 0)
      {
        localPoint = new Point(x, y);
        i = paramJList.locationToIndex(localPoint);
        if (j <= i)
        {
          y = (y + height - height);
          y = y;
          i = paramJList.locationToIndex(localPoint);
          localRectangle3 = paramJList.getCellBounds(i, i);
          if (y < y)
          {
            y = (y + height);
            i = paramJList.locationToIndex(localPoint);
            localRectangle3 = paramJList.getCellBounds(i, i);
          }
          if (y >= y)
          {
            y = (y - 1);
            i = paramJList.locationToIndex(localPoint);
          }
        }
      }
      else
      {
        localPoint = new Point(x, y + height - 1);
        i = paramJList.locationToIndex(localPoint);
        localRectangle3 = paramJList.getCellBounds(i, i);
        if (y + height > y + height)
        {
          y = (y - 1);
          i = paramJList.locationToIndex(localPoint);
          localRectangle3 = paramJList.getCellBounds(i, i);
          i = Math.max(i, j);
        }
        if (j >= i)
        {
          y = y;
          y = (y + height - 1);
          i = paramJList.locationToIndex(localPoint);
          localRectangle3 = paramJList.getCellBounds(i, i);
          if (y + height > y + height)
          {
            y = (y - 1);
            i = paramJList.locationToIndex(localPoint);
            localRectangle3 = paramJList.getCellBounds(i, i);
          }
          if (y <= y)
          {
            y = (y + height);
            i = paramJList.locationToIndex(localPoint);
          }
        }
      }
      return i;
    }
    
    private void changeSelection(JList paramJList, int paramInt1, int paramInt2, int paramInt3)
    {
      if ((paramInt2 >= 0) && (paramInt2 < paramJList.getModel().getSize()))
      {
        ListSelectionModel localListSelectionModel = paramJList.getSelectionModel();
        if ((paramInt1 == 0) && (paramJList.getSelectionMode() != 2)) {
          paramInt1 = 1;
        }
        adjustScrollPositionIfNecessary(paramJList, paramInt2, paramInt3);
        if (paramInt1 == 2)
        {
          int i = BasicListUI.adjustIndex(localListSelectionModel.getAnchorSelectionIndex(), paramJList);
          if (i == -1) {
            i = 0;
          }
          paramJList.setSelectionInterval(i, paramInt2);
        }
        else if (paramInt1 == 1)
        {
          paramJList.setSelectedIndex(paramInt2);
        }
        else
        {
          ((DefaultListSelectionModel)localListSelectionModel).moveLeadSelectionIndex(paramInt2);
        }
      }
    }
    
    private void adjustScrollPositionIfNecessary(JList paramJList, int paramInt1, int paramInt2)
    {
      if (paramInt2 == 0) {
        return;
      }
      Object localObject = paramJList.getCellBounds(paramInt1, paramInt1);
      Rectangle localRectangle1 = paramJList.getVisibleRect();
      if ((localObject != null) && (!localRectangle1.contains((Rectangle)localObject)))
      {
        int i;
        int j;
        Rectangle localRectangle2;
        if ((paramJList.getLayoutOrientation() == 1) && (paramJList.getVisibleRowCount() <= 0))
        {
          if (paramJList.getComponentOrientation().isLeftToRight())
          {
            if (paramInt2 > 0)
            {
              i = Math.max(0, x + width - width);
              j = paramJList.locationToIndex(new Point(i, y));
              localRectangle2 = paramJList.getCellBounds(j, j);
              if ((x < i) && (x < x))
              {
                x += width;
                j = paramJList.locationToIndex(localRectangle2.getLocation());
                localRectangle2 = paramJList.getCellBounds(j, j);
              }
              localObject = localRectangle2;
            }
            width = width;
          }
          else if (paramInt2 > 0)
          {
            i = x + width;
            j = paramJList.locationToIndex(new Point(i, y));
            localRectangle2 = paramJList.getCellBounds(j, j);
            if ((x + width > i) && (x > x)) {
              width = 0;
            }
            x = Math.max(0, x + width - width);
            width = width;
          }
          else
          {
            x += Math.max(0, width - width);
            width = Math.min(width, width);
          }
        }
        else if ((paramInt2 > 0) && ((y < y) || (y + height > y + height)))
        {
          i = Math.max(0, y + height - height);
          j = paramJList.locationToIndex(new Point(x, i));
          localRectangle2 = paramJList.getCellBounds(j, j);
          if ((y < i) && (y < y))
          {
            y += height;
            j = paramJList.locationToIndex(localRectangle2.getLocation());
            localRectangle2 = paramJList.getCellBounds(j, j);
          }
          localObject = localRectangle2;
          height = height;
        }
        else
        {
          height = Math.min(height, height);
        }
        paramJList.scrollRectToVisible((Rectangle)localObject);
      }
    }
    
    private int getNextColumnIndex(JList paramJList, BasicListUI paramBasicListUI, int paramInt)
    {
      if (paramJList.getLayoutOrientation() != 0)
      {
        int i = BasicListUI.adjustIndex(paramJList.getLeadSelectionIndex(), paramJList);
        int j = paramJList.getModel().getSize();
        if (i == -1) {
          return 0;
        }
        if (j == 1) {
          return 0;
        }
        if ((paramBasicListUI == null) || (columnCount <= 1)) {
          return -1;
        }
        int k = paramBasicListUI.convertModelToColumn(i);
        int m = paramBasicListUI.convertModelToRow(i);
        k += paramInt;
        if ((k >= columnCount) || (k < 0)) {
          return -1;
        }
        int n = paramBasicListUI.getRowCount(k);
        if (m >= n) {
          return -1;
        }
        return paramBasicListUI.getModelIndex(k, m);
      }
      return -1;
    }
    
    private int getNextIndex(JList paramJList, BasicListUI paramBasicListUI, int paramInt)
    {
      int i = BasicListUI.adjustIndex(paramJList.getLeadSelectionIndex(), paramJList);
      int j = paramJList.getModel().getSize();
      if (i == -1)
      {
        if (j > 0) {
          if (paramInt > 0) {
            i = 0;
          } else {
            i = j - 1;
          }
        }
      }
      else if (j == 1) {
        i = 0;
      } else if (paramJList.getLayoutOrientation() == 2)
      {
        if (paramBasicListUI != null) {
          i += columnCount * paramInt;
        }
      }
      else {
        i += paramInt;
      }
      return i;
    }
  }
  
  public class FocusHandler
    implements FocusListener
  {
    public FocusHandler() {}
    
    protected void repaintCellFocus()
    {
      BasicListUI.this.getHandler().repaintCellFocus();
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicListUI.this.getHandler().focusGained(paramFocusEvent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicListUI.this.getHandler().focusLost(paramFocusEvent);
    }
  }
  
  private class Handler
    implements FocusListener, KeyListener, ListDataListener, ListSelectionListener, MouseInputListener, PropertyChangeListener, DragRecognitionSupport.BeforeDrag
  {
    private String prefix = "";
    private String typedString = "";
    private long lastTime = 0L;
    private boolean dragPressDidSelection;
    
    private Handler() {}
    
    public void keyTyped(KeyEvent paramKeyEvent)
    {
      JList localJList = (JList)paramKeyEvent.getSource();
      ListModel localListModel = localJList.getModel();
      if ((localListModel.getSize() == 0) || (paramKeyEvent.isAltDown()) || (BasicGraphicsUtils.isMenuShortcutKeyDown(paramKeyEvent)) || (isNavigationKey(paramKeyEvent))) {
        return;
      }
      int i = 1;
      char c = paramKeyEvent.getKeyChar();
      long l = paramKeyEvent.getWhen();
      int j = BasicListUI.adjustIndex(localJList.getLeadSelectionIndex(), list);
      if (l - lastTime < timeFactor)
      {
        typedString += c;
        if ((prefix.length() == 1) && (c == prefix.charAt(0))) {
          j++;
        } else {
          prefix = typedString;
        }
      }
      else
      {
        j++;
        typedString = ("" + c);
        prefix = typedString;
      }
      lastTime = l;
      if ((j < 0) || (j >= localListModel.getSize()))
      {
        i = 0;
        j = 0;
      }
      int k = localJList.getNextMatch(prefix, j, Position.Bias.Forward);
      if (k >= 0)
      {
        localJList.setSelectedIndex(k);
        localJList.ensureIndexIsVisible(k);
      }
      else if (i != 0)
      {
        k = localJList.getNextMatch(prefix, 0, Position.Bias.Forward);
        if (k >= 0)
        {
          localJList.setSelectedIndex(k);
          localJList.ensureIndexIsVisible(k);
        }
      }
    }
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      if (isNavigationKey(paramKeyEvent))
      {
        prefix = "";
        typedString = "";
        lastTime = 0L;
      }
    }
    
    public void keyReleased(KeyEvent paramKeyEvent) {}
    
    private boolean isNavigationKey(KeyEvent paramKeyEvent)
    {
      InputMap localInputMap = list.getInputMap(1);
      KeyStroke localKeyStroke = KeyStroke.getKeyStrokeForEvent(paramKeyEvent);
      return (localInputMap != null) && (localInputMap.get(localKeyStroke) != null);
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject1;
      Object localObject2;
      if (str == "model")
      {
        localObject1 = (ListModel)paramPropertyChangeEvent.getOldValue();
        localObject2 = (ListModel)paramPropertyChangeEvent.getNewValue();
        if (localObject1 != null) {
          ((ListModel)localObject1).removeListDataListener(listDataListener);
        }
        if (localObject2 != null) {
          ((ListModel)localObject2).addListDataListener(listDataListener);
        }
        updateLayoutStateNeeded |= 0x1;
        BasicListUI.this.redrawList();
      }
      else if (str == "selectionModel")
      {
        localObject1 = (ListSelectionModel)paramPropertyChangeEvent.getOldValue();
        localObject2 = (ListSelectionModel)paramPropertyChangeEvent.getNewValue();
        if (localObject1 != null) {
          ((ListSelectionModel)localObject1).removeListSelectionListener(listSelectionListener);
        }
        if (localObject2 != null) {
          ((ListSelectionModel)localObject2).addListSelectionListener(listSelectionListener);
        }
        updateLayoutStateNeeded |= 0x1;
        BasicListUI.this.redrawList();
      }
      else if (str == "cellRenderer")
      {
        updateLayoutStateNeeded |= 0x40;
        BasicListUI.this.redrawList();
      }
      else if (str == "font")
      {
        updateLayoutStateNeeded |= 0x4;
        BasicListUI.this.redrawList();
      }
      else if (str == "prototypeCellValue")
      {
        updateLayoutStateNeeded |= 0x20;
        BasicListUI.this.redrawList();
      }
      else if (str == "fixedCellHeight")
      {
        updateLayoutStateNeeded |= 0x10;
        BasicListUI.this.redrawList();
      }
      else if (str == "fixedCellWidth")
      {
        updateLayoutStateNeeded |= 0x8;
        BasicListUI.this.redrawList();
      }
      else if (str == "selectionForeground")
      {
        list.repaint();
      }
      else if (str == "selectionBackground")
      {
        list.repaint();
      }
      else if ("layoutOrientation" == str)
      {
        updateLayoutStateNeeded |= 0x80;
        layoutOrientation = list.getLayoutOrientation();
        BasicListUI.this.redrawList();
      }
      else if ("visibleRowCount" == str)
      {
        if (layoutOrientation != 0)
        {
          updateLayoutStateNeeded |= 0x80;
          BasicListUI.this.redrawList();
        }
      }
      else if ("componentOrientation" == str)
      {
        isLeftToRight = list.getComponentOrientation().isLeftToRight();
        updateLayoutStateNeeded |= 0x400;
        BasicListUI.this.redrawList();
        localObject1 = getInputMap(0);
        SwingUtilities.replaceUIInputMap(list, 0, (InputMap)localObject1);
      }
      else if ("List.isFileList" == str)
      {
        BasicListUI.this.updateIsFileList();
        BasicListUI.this.redrawList();
      }
      else if ("dropLocation" == str)
      {
        localObject1 = (JList.DropLocation)paramPropertyChangeEvent.getOldValue();
        repaintDropLocation((JList.DropLocation)localObject1);
        repaintDropLocation(list.getDropLocation());
      }
    }
    
    private void repaintDropLocation(JList.DropLocation paramDropLocation)
    {
      if (paramDropLocation == null) {
        return;
      }
      Rectangle localRectangle;
      if (paramDropLocation.isInsert()) {
        localRectangle = BasicListUI.this.getDropLineRect(paramDropLocation);
      } else {
        localRectangle = BasicListUI.this.getCellBounds(list, paramDropLocation.getIndex());
      }
      if (localRectangle != null) {
        list.repaint(localRectangle);
      }
    }
    
    public void intervalAdded(ListDataEvent paramListDataEvent)
    {
      updateLayoutStateNeeded = 1;
      int i = Math.min(paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
      int j = Math.max(paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
      ListSelectionModel localListSelectionModel = list.getSelectionModel();
      if (localListSelectionModel != null) {
        localListSelectionModel.insertIndexInterval(i, j - i + 1, true);
      }
      BasicListUI.this.redrawList();
    }
    
    public void intervalRemoved(ListDataEvent paramListDataEvent)
    {
      updateLayoutStateNeeded = 1;
      ListSelectionModel localListSelectionModel = list.getSelectionModel();
      if (localListSelectionModel != null) {
        localListSelectionModel.removeIndexInterval(paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
      }
      BasicListUI.this.redrawList();
    }
    
    public void contentsChanged(ListDataEvent paramListDataEvent)
    {
      updateLayoutStateNeeded = 1;
      BasicListUI.this.redrawList();
    }
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      maybeUpdateLayoutState();
      int i = list.getModel().getSize();
      int j = Math.min(i - 1, Math.max(paramListSelectionEvent.getFirstIndex(), 0));
      int k = Math.min(i - 1, Math.max(paramListSelectionEvent.getLastIndex(), 0));
      Rectangle localRectangle = getCellBounds(list, j, k);
      if (localRectangle != null) {
        list.repaint(x, y, width, height);
      }
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (SwingUtilities2.shouldIgnore(paramMouseEvent, list)) {
        return;
      }
      boolean bool = list.getDragEnabled();
      int i = 1;
      if (bool)
      {
        int j = SwingUtilities2.loc2IndexFileList(list, paramMouseEvent.getPoint());
        if ((j != -1) && (DragRecognitionSupport.mousePressed(paramMouseEvent)))
        {
          dragPressDidSelection = false;
          if (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent)) {
            return;
          }
          if ((!paramMouseEvent.isShiftDown()) && (list.isSelectedIndex(j)))
          {
            list.addSelectionInterval(j, j);
            return;
          }
          i = 0;
          dragPressDidSelection = true;
        }
      }
      else
      {
        list.setValueIsAdjusting(true);
      }
      if (i != 0) {
        SwingUtilities2.adjustFocus(list);
      }
      adjustSelection(paramMouseEvent);
    }
    
    private void adjustSelection(MouseEvent paramMouseEvent)
    {
      int i = SwingUtilities2.loc2IndexFileList(list, paramMouseEvent.getPoint());
      if (i < 0)
      {
        if ((isFileList) && (paramMouseEvent.getID() == 501) && ((!paramMouseEvent.isShiftDown()) || (list.getSelectionMode() == 0))) {
          list.clearSelection();
        }
      }
      else
      {
        int j = BasicListUI.adjustIndex(list.getAnchorSelectionIndex(), list);
        boolean bool;
        if (j == -1)
        {
          j = 0;
          bool = false;
        }
        else
        {
          bool = list.isSelectedIndex(j);
        }
        if (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent))
        {
          if (paramMouseEvent.isShiftDown())
          {
            if (bool)
            {
              list.addSelectionInterval(j, i);
            }
            else
            {
              list.removeSelectionInterval(j, i);
              if (isFileList)
              {
                list.addSelectionInterval(i, i);
                list.getSelectionModel().setAnchorSelectionIndex(j);
              }
            }
          }
          else if (list.isSelectedIndex(i)) {
            list.removeSelectionInterval(i, i);
          } else {
            list.addSelectionInterval(i, i);
          }
        }
        else if (paramMouseEvent.isShiftDown()) {
          list.setSelectionInterval(j, i);
        } else {
          list.setSelectionInterval(i, i);
        }
      }
    }
    
    public void dragStarting(MouseEvent paramMouseEvent)
    {
      if (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent))
      {
        int i = SwingUtilities2.loc2IndexFileList(list, paramMouseEvent.getPoint());
        list.addSelectionInterval(i, i);
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (SwingUtilities2.shouldIgnore(paramMouseEvent, list)) {
        return;
      }
      if (list.getDragEnabled())
      {
        DragRecognitionSupport.mouseDragged(paramMouseEvent, this);
        return;
      }
      if ((paramMouseEvent.isShiftDown()) || (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent))) {
        return;
      }
      int i = locationToIndex(list, paramMouseEvent.getPoint());
      if (i != -1)
      {
        if (isFileList) {
          return;
        }
        Rectangle localRectangle = getCellBounds(list, i, i);
        if (localRectangle != null)
        {
          list.scrollRectToVisible(localRectangle);
          list.setSelectionInterval(i, i);
        }
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (SwingUtilities2.shouldIgnore(paramMouseEvent, list)) {
        return;
      }
      if (list.getDragEnabled())
      {
        MouseEvent localMouseEvent = DragRecognitionSupport.mouseReleased(paramMouseEvent);
        if (localMouseEvent != null)
        {
          SwingUtilities2.adjustFocus(list);
          if (!dragPressDidSelection) {
            adjustSelection(localMouseEvent);
          }
        }
      }
      else
      {
        list.setValueIsAdjusting(false);
      }
    }
    
    protected void repaintCellFocus()
    {
      int i = BasicListUI.adjustIndex(list.getLeadSelectionIndex(), list);
      if (i != -1)
      {
        Rectangle localRectangle = getCellBounds(list, i, i);
        if (localRectangle != null) {
          list.repaint(x, y, width, height);
        }
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      repaintCellFocus();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      repaintCellFocus();
    }
  }
  
  public class ListDataHandler
    implements ListDataListener
  {
    public ListDataHandler() {}
    
    public void intervalAdded(ListDataEvent paramListDataEvent)
    {
      BasicListUI.this.getHandler().intervalAdded(paramListDataEvent);
    }
    
    public void intervalRemoved(ListDataEvent paramListDataEvent)
    {
      BasicListUI.this.getHandler().intervalRemoved(paramListDataEvent);
    }
    
    public void contentsChanged(ListDataEvent paramListDataEvent)
    {
      BasicListUI.this.getHandler().contentsChanged(paramListDataEvent);
    }
  }
  
  public class ListSelectionHandler
    implements ListSelectionListener
  {
    public ListSelectionHandler() {}
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      BasicListUI.this.getHandler().valueChanged(paramListSelectionEvent);
    }
  }
  
  static class ListTransferHandler
    extends TransferHandler
    implements UIResource
  {
    ListTransferHandler() {}
    
    protected Transferable createTransferable(JComponent paramJComponent)
    {
      if ((paramJComponent instanceof JList))
      {
        JList localJList = (JList)paramJComponent;
        Object[] arrayOfObject = localJList.getSelectedValues();
        if ((arrayOfObject == null) || (arrayOfObject.length == 0)) {
          return null;
        }
        StringBuffer localStringBuffer1 = new StringBuffer();
        StringBuffer localStringBuffer2 = new StringBuffer();
        localStringBuffer2.append("<html>\n<body>\n<ul>\n");
        for (int i = 0; i < arrayOfObject.length; i++)
        {
          Object localObject = arrayOfObject[i];
          String str = localObject == null ? "" : localObject.toString();
          localStringBuffer1.append(str + "\n");
          localStringBuffer2.append("  <li>" + str + "\n");
        }
        localStringBuffer1.deleteCharAt(localStringBuffer1.length() - 1);
        localStringBuffer2.append("</ul>\n</body>\n</html>");
        return new BasicTransferable(localStringBuffer1.toString(), localStringBuffer2.toString());
      }
      return null;
    }
    
    public int getSourceActions(JComponent paramJComponent)
    {
      return 1;
    }
  }
  
  public class MouseInputHandler
    implements MouseInputListener
  {
    public MouseInputHandler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      BasicListUI.this.getHandler().mouseClicked(paramMouseEvent);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      BasicListUI.this.getHandler().mouseEntered(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      BasicListUI.this.getHandler().mouseExited(paramMouseEvent);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      BasicListUI.this.getHandler().mousePressed(paramMouseEvent);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      BasicListUI.this.getHandler().mouseDragged(paramMouseEvent);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      BasicListUI.this.getHandler().mouseMoved(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      BasicListUI.this.getHandler().mouseReleased(paramMouseEvent);
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicListUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicListUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */