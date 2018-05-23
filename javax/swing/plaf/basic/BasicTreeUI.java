package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JTree.DropLocation;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Position.Bias;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.AbstractLayoutCache.NodeDimensions;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.FixedHeightLayoutCache;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.VariableHeightLayoutCache;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTreeUI
  extends TreeUI
{
  private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("Tree.baselineComponent");
  private static final Actions SHARED_ACTION = new Actions();
  protected transient Icon collapsedIcon;
  protected transient Icon expandedIcon;
  private Color hashColor;
  protected int leftChildIndent;
  protected int rightChildIndent;
  protected int totalChildIndent;
  protected Dimension preferredMinSize;
  protected int lastSelectedRow;
  protected JTree tree;
  protected transient TreeCellRenderer currentCellRenderer;
  protected boolean createdRenderer;
  protected transient TreeCellEditor cellEditor;
  protected boolean createdCellEditor;
  protected boolean stopEditingInCompleteEditing;
  protected CellRendererPane rendererPane;
  protected Dimension preferredSize;
  protected boolean validCachedPreferredSize;
  protected AbstractLayoutCache treeState;
  protected Hashtable<TreePath, Boolean> drawingCache;
  protected boolean largeModel;
  protected AbstractLayoutCache.NodeDimensions nodeDimensions;
  protected TreeModel treeModel;
  protected TreeSelectionModel treeSelectionModel;
  protected int depthOffset;
  protected Component editingComponent;
  protected TreePath editingPath;
  protected int editingRow;
  protected boolean editorHasDifferentSize;
  private int leadRow;
  private boolean ignoreLAChange;
  private boolean leftToRight;
  private PropertyChangeListener propertyChangeListener;
  private PropertyChangeListener selectionModelPropertyChangeListener;
  private MouseListener mouseListener;
  private FocusListener focusListener;
  private KeyListener keyListener;
  private ComponentListener componentListener;
  private CellEditorListener cellEditorListener;
  private TreeSelectionListener treeSelectionListener;
  private TreeModelListener treeModelListener;
  private TreeExpansionListener treeExpansionListener;
  private boolean paintLines = true;
  private boolean lineTypeDashed;
  private long timeFactor = 1000L;
  private Handler handler;
  private MouseEvent releaseEvent;
  private static final TransferHandler defaultTransferHandler = new TreeTransferHandler();
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicTreeUI();
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("selectPrevious"));
    paramLazyActionMap.put(new Actions("selectPreviousChangeLead"));
    paramLazyActionMap.put(new Actions("selectPreviousExtendSelection"));
    paramLazyActionMap.put(new Actions("selectNext"));
    paramLazyActionMap.put(new Actions("selectNextChangeLead"));
    paramLazyActionMap.put(new Actions("selectNextExtendSelection"));
    paramLazyActionMap.put(new Actions("selectChild"));
    paramLazyActionMap.put(new Actions("selectChildChangeLead"));
    paramLazyActionMap.put(new Actions("selectParent"));
    paramLazyActionMap.put(new Actions("selectParentChangeLead"));
    paramLazyActionMap.put(new Actions("scrollUpChangeSelection"));
    paramLazyActionMap.put(new Actions("scrollUpChangeLead"));
    paramLazyActionMap.put(new Actions("scrollUpExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollDownChangeSelection"));
    paramLazyActionMap.put(new Actions("scrollDownExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollDownChangeLead"));
    paramLazyActionMap.put(new Actions("selectFirst"));
    paramLazyActionMap.put(new Actions("selectFirstChangeLead"));
    paramLazyActionMap.put(new Actions("selectFirstExtendSelection"));
    paramLazyActionMap.put(new Actions("selectLast"));
    paramLazyActionMap.put(new Actions("selectLastChangeLead"));
    paramLazyActionMap.put(new Actions("selectLastExtendSelection"));
    paramLazyActionMap.put(new Actions("toggle"));
    paramLazyActionMap.put(new Actions("cancel"));
    paramLazyActionMap.put(new Actions("startEditing"));
    paramLazyActionMap.put(new Actions("selectAll"));
    paramLazyActionMap.put(new Actions("clearSelection"));
    paramLazyActionMap.put(new Actions("scrollLeft"));
    paramLazyActionMap.put(new Actions("scrollRight"));
    paramLazyActionMap.put(new Actions("scrollLeftExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollRightExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollRightChangeLead"));
    paramLazyActionMap.put(new Actions("scrollLeftChangeLead"));
    paramLazyActionMap.put(new Actions("expand"));
    paramLazyActionMap.put(new Actions("collapse"));
    paramLazyActionMap.put(new Actions("moveSelectionToParent"));
    paramLazyActionMap.put(new Actions("addToSelection"));
    paramLazyActionMap.put(new Actions("toggleAndAnchor"));
    paramLazyActionMap.put(new Actions("extendTo"));
    paramLazyActionMap.put(new Actions("moveSelectionTo"));
    paramLazyActionMap.put(TransferHandler.getCutAction());
    paramLazyActionMap.put(TransferHandler.getCopyAction());
    paramLazyActionMap.put(TransferHandler.getPasteAction());
  }
  
  public BasicTreeUI() {}
  
  protected Color getHashColor()
  {
    return hashColor;
  }
  
  protected void setHashColor(Color paramColor)
  {
    hashColor = paramColor;
  }
  
  public void setLeftChildIndent(int paramInt)
  {
    leftChildIndent = paramInt;
    totalChildIndent = (leftChildIndent + rightChildIndent);
    if (treeState != null) {
      treeState.invalidateSizes();
    }
    updateSize();
  }
  
  public int getLeftChildIndent()
  {
    return leftChildIndent;
  }
  
  public void setRightChildIndent(int paramInt)
  {
    rightChildIndent = paramInt;
    totalChildIndent = (leftChildIndent + rightChildIndent);
    if (treeState != null) {
      treeState.invalidateSizes();
    }
    updateSize();
  }
  
  public int getRightChildIndent()
  {
    return rightChildIndent;
  }
  
  public void setExpandedIcon(Icon paramIcon)
  {
    expandedIcon = paramIcon;
  }
  
  public Icon getExpandedIcon()
  {
    return expandedIcon;
  }
  
  public void setCollapsedIcon(Icon paramIcon)
  {
    collapsedIcon = paramIcon;
  }
  
  public Icon getCollapsedIcon()
  {
    return collapsedIcon;
  }
  
  protected void setLargeModel(boolean paramBoolean)
  {
    if (getRowHeight() < 1) {
      paramBoolean = false;
    }
    if (largeModel != paramBoolean)
    {
      completeEditing();
      largeModel = paramBoolean;
      treeState = createLayoutCache();
      configureLayoutCache();
      updateLayoutCacheExpandedNodesIfNecessary();
      updateSize();
    }
  }
  
  protected boolean isLargeModel()
  {
    return largeModel;
  }
  
  protected void setRowHeight(int paramInt)
  {
    completeEditing();
    if (treeState != null)
    {
      setLargeModel(tree.isLargeModel());
      treeState.setRowHeight(paramInt);
      updateSize();
    }
  }
  
  protected int getRowHeight()
  {
    return tree == null ? -1 : tree.getRowHeight();
  }
  
  protected void setCellRenderer(TreeCellRenderer paramTreeCellRenderer)
  {
    completeEditing();
    updateRenderer();
    if (treeState != null)
    {
      treeState.invalidateSizes();
      updateSize();
    }
  }
  
  protected TreeCellRenderer getCellRenderer()
  {
    return currentCellRenderer;
  }
  
  protected void setModel(TreeModel paramTreeModel)
  {
    completeEditing();
    if ((treeModel != null) && (treeModelListener != null)) {
      treeModel.removeTreeModelListener(treeModelListener);
    }
    treeModel = paramTreeModel;
    if ((treeModel != null) && (treeModelListener != null)) {
      treeModel.addTreeModelListener(treeModelListener);
    }
    if (treeState != null)
    {
      treeState.setModel(paramTreeModel);
      updateLayoutCacheExpandedNodesIfNecessary();
      updateSize();
    }
  }
  
  protected TreeModel getModel()
  {
    return treeModel;
  }
  
  protected void setRootVisible(boolean paramBoolean)
  {
    completeEditing();
    updateDepthOffset();
    if (treeState != null)
    {
      treeState.setRootVisible(paramBoolean);
      treeState.invalidateSizes();
      updateSize();
    }
  }
  
  protected boolean isRootVisible()
  {
    return tree != null ? tree.isRootVisible() : false;
  }
  
  protected void setShowsRootHandles(boolean paramBoolean)
  {
    completeEditing();
    updateDepthOffset();
    if (treeState != null)
    {
      treeState.invalidateSizes();
      updateSize();
    }
  }
  
  protected boolean getShowsRootHandles()
  {
    return tree != null ? tree.getShowsRootHandles() : false;
  }
  
  protected void setCellEditor(TreeCellEditor paramTreeCellEditor)
  {
    updateCellEditor();
  }
  
  protected TreeCellEditor getCellEditor()
  {
    return tree != null ? tree.getCellEditor() : null;
  }
  
  protected void setEditable(boolean paramBoolean)
  {
    updateCellEditor();
  }
  
  protected boolean isEditable()
  {
    return tree != null ? tree.isEditable() : false;
  }
  
  protected void setSelectionModel(TreeSelectionModel paramTreeSelectionModel)
  {
    completeEditing();
    if ((selectionModelPropertyChangeListener != null) && (treeSelectionModel != null)) {
      treeSelectionModel.removePropertyChangeListener(selectionModelPropertyChangeListener);
    }
    if ((treeSelectionListener != null) && (treeSelectionModel != null)) {
      treeSelectionModel.removeTreeSelectionListener(treeSelectionListener);
    }
    treeSelectionModel = paramTreeSelectionModel;
    if (treeSelectionModel != null)
    {
      if (selectionModelPropertyChangeListener != null) {
        treeSelectionModel.addPropertyChangeListener(selectionModelPropertyChangeListener);
      }
      if (treeSelectionListener != null) {
        treeSelectionModel.addTreeSelectionListener(treeSelectionListener);
      }
      if (treeState != null) {
        treeState.setSelectionModel(treeSelectionModel);
      }
    }
    else if (treeState != null)
    {
      treeState.setSelectionModel(null);
    }
    if (tree != null) {
      tree.repaint();
    }
  }
  
  protected TreeSelectionModel getSelectionModel()
  {
    return treeSelectionModel;
  }
  
  public Rectangle getPathBounds(JTree paramJTree, TreePath paramTreePath)
  {
    if ((paramJTree != null) && (treeState != null)) {
      return getPathBounds(paramTreePath, paramJTree.getInsets(), new Rectangle());
    }
    return null;
  }
  
  private Rectangle getPathBounds(TreePath paramTreePath, Insets paramInsets, Rectangle paramRectangle)
  {
    paramRectangle = treeState.getBounds(paramTreePath, paramRectangle);
    if (paramRectangle != null)
    {
      if (leftToRight) {
        x += left;
      } else {
        x = (tree.getWidth() - (x + width) - right);
      }
      y += top;
    }
    return paramRectangle;
  }
  
  public TreePath getPathForRow(JTree paramJTree, int paramInt)
  {
    return treeState != null ? treeState.getPathForRow(paramInt) : null;
  }
  
  public int getRowForPath(JTree paramJTree, TreePath paramTreePath)
  {
    return treeState != null ? treeState.getRowForPath(paramTreePath) : -1;
  }
  
  public int getRowCount(JTree paramJTree)
  {
    return treeState != null ? treeState.getRowCount() : 0;
  }
  
  public TreePath getClosestPathForLocation(JTree paramJTree, int paramInt1, int paramInt2)
  {
    if ((paramJTree != null) && (treeState != null))
    {
      paramInt2 -= getInsetstop;
      return treeState.getPathClosestTo(paramInt1, paramInt2);
    }
    return null;
  }
  
  public boolean isEditing(JTree paramJTree)
  {
    return editingComponent != null;
  }
  
  public boolean stopEditing(JTree paramJTree)
  {
    if ((editingComponent != null) && (cellEditor.stopCellEditing()))
    {
      completeEditing(false, false, true);
      return true;
    }
    return false;
  }
  
  public void cancelEditing(JTree paramJTree)
  {
    if (editingComponent != null) {
      completeEditing(false, true, false);
    }
  }
  
  public void startEditingAtPath(JTree paramJTree, TreePath paramTreePath)
  {
    paramJTree.scrollPathToVisible(paramTreePath);
    if ((paramTreePath != null) && (paramJTree.isVisible(paramTreePath))) {
      startEditing(paramTreePath, null);
    }
  }
  
  public TreePath getEditingPath(JTree paramJTree)
  {
    return editingPath;
  }
  
  public void installUI(JComponent paramJComponent)
  {
    if (paramJComponent == null) {
      throw new NullPointerException("null component passed to BasicTreeUI.installUI()");
    }
    tree = ((JTree)paramJComponent);
    prepareForUIInstall();
    installDefaults();
    installKeyboardActions();
    installComponents();
    installListeners();
    completeUIInstall();
  }
  
  protected void prepareForUIInstall()
  {
    drawingCache = new Hashtable(7);
    leftToRight = BasicGraphicsUtils.isLeftToRight(tree);
    stopEditingInCompleteEditing = true;
    lastSelectedRow = -1;
    leadRow = -1;
    preferredSize = new Dimension();
    largeModel = tree.isLargeModel();
    if (getRowHeight() <= 0) {
      largeModel = false;
    }
    setModel(tree.getModel());
  }
  
  protected void completeUIInstall()
  {
    setShowsRootHandles(tree.getShowsRootHandles());
    updateRenderer();
    updateDepthOffset();
    setSelectionModel(tree.getSelectionModel());
    treeState = createLayoutCache();
    configureLayoutCache();
    updateSize();
  }
  
  protected void installDefaults()
  {
    if ((tree.getBackground() == null) || ((tree.getBackground() instanceof UIResource))) {
      tree.setBackground(UIManager.getColor("Tree.background"));
    }
    if ((getHashColor() == null) || ((getHashColor() instanceof UIResource))) {
      setHashColor(UIManager.getColor("Tree.hash"));
    }
    if ((tree.getFont() == null) || ((tree.getFont() instanceof UIResource))) {
      tree.setFont(UIManager.getFont("Tree.font"));
    }
    setExpandedIcon((Icon)UIManager.get("Tree.expandedIcon"));
    setCollapsedIcon((Icon)UIManager.get("Tree.collapsedIcon"));
    setLeftChildIndent(((Integer)UIManager.get("Tree.leftChildIndent")).intValue());
    setRightChildIndent(((Integer)UIManager.get("Tree.rightChildIndent")).intValue());
    LookAndFeel.installProperty(tree, "rowHeight", UIManager.get("Tree.rowHeight"));
    largeModel = ((tree.isLargeModel()) && (tree.getRowHeight() > 0));
    Object localObject1 = UIManager.get("Tree.scrollsOnExpand");
    if (localObject1 != null) {
      LookAndFeel.installProperty(tree, "scrollsOnExpand", localObject1);
    }
    paintLines = UIManager.getBoolean("Tree.paintLines");
    lineTypeDashed = UIManager.getBoolean("Tree.lineTypeDashed");
    Long localLong = (Long)UIManager.get("Tree.timeFactor");
    timeFactor = (localLong != null ? localLong.longValue() : 1000L);
    Object localObject2 = UIManager.get("Tree.showsRootHandles");
    if (localObject2 != null) {
      LookAndFeel.installProperty(tree, "showsRootHandles", localObject2);
    }
  }
  
  protected void installListeners()
  {
    if ((propertyChangeListener = createPropertyChangeListener()) != null) {
      tree.addPropertyChangeListener(propertyChangeListener);
    }
    if ((mouseListener = createMouseListener()) != null)
    {
      tree.addMouseListener(mouseListener);
      if ((mouseListener instanceof MouseMotionListener)) {
        tree.addMouseMotionListener((MouseMotionListener)mouseListener);
      }
    }
    if ((focusListener = createFocusListener()) != null) {
      tree.addFocusListener(focusListener);
    }
    if ((keyListener = createKeyListener()) != null) {
      tree.addKeyListener(keyListener);
    }
    if ((treeExpansionListener = createTreeExpansionListener()) != null) {
      tree.addTreeExpansionListener(treeExpansionListener);
    }
    if (((treeModelListener = createTreeModelListener()) != null) && (treeModel != null)) {
      treeModel.addTreeModelListener(treeModelListener);
    }
    if (((selectionModelPropertyChangeListener = createSelectionModelPropertyChangeListener()) != null) && (treeSelectionModel != null)) {
      treeSelectionModel.addPropertyChangeListener(selectionModelPropertyChangeListener);
    }
    if (((treeSelectionListener = createTreeSelectionListener()) != null) && (treeSelectionModel != null)) {
      treeSelectionModel.addTreeSelectionListener(treeSelectionListener);
    }
    TransferHandler localTransferHandler = tree.getTransferHandler();
    if ((localTransferHandler == null) || ((localTransferHandler instanceof UIResource)))
    {
      tree.setTransferHandler(defaultTransferHandler);
      if ((tree.getDropTarget() instanceof UIResource)) {
        tree.setDropTarget(null);
      }
    }
    LookAndFeel.installProperty(tree, "opaque", Boolean.TRUE);
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(tree, 1, localInputMap);
    localInputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(tree, 0, localInputMap);
    LazyActionMap.installLazyActionMap(tree, BasicTreeUI.class, "Tree.actionMap");
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(tree, this, "Tree.ancestorInputMap");
    }
    if (paramInt == 0)
    {
      InputMap localInputMap1 = (InputMap)DefaultLookup.get(tree, this, "Tree.focusInputMap");
      InputMap localInputMap2;
      if ((tree.getComponentOrientation().isLeftToRight()) || ((localInputMap2 = (InputMap)DefaultLookup.get(tree, this, "Tree.focusInputMap.RightToLeft")) == null)) {
        return localInputMap1;
      }
      localInputMap2.setParent(localInputMap1);
      return localInputMap2;
    }
    return null;
  }
  
  protected void installComponents()
  {
    if ((rendererPane = createCellRendererPane()) != null) {
      tree.add(rendererPane);
    }
  }
  
  protected AbstractLayoutCache.NodeDimensions createNodeDimensions()
  {
    return new NodeDimensionsHandler();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected MouseListener createMouseListener()
  {
    return getHandler();
  }
  
  protected FocusListener createFocusListener()
  {
    return getHandler();
  }
  
  protected KeyListener createKeyListener()
  {
    return getHandler();
  }
  
  protected PropertyChangeListener createSelectionModelPropertyChangeListener()
  {
    return getHandler();
  }
  
  protected TreeSelectionListener createTreeSelectionListener()
  {
    return getHandler();
  }
  
  protected CellEditorListener createCellEditorListener()
  {
    return getHandler();
  }
  
  protected ComponentListener createComponentListener()
  {
    return new ComponentHandler();
  }
  
  protected TreeExpansionListener createTreeExpansionListener()
  {
    return getHandler();
  }
  
  protected AbstractLayoutCache createLayoutCache()
  {
    if ((isLargeModel()) && (getRowHeight() > 0)) {
      return new FixedHeightLayoutCache();
    }
    return new VariableHeightLayoutCache();
  }
  
  protected CellRendererPane createCellRendererPane()
  {
    return new CellRendererPane();
  }
  
  protected TreeCellEditor createDefaultCellEditor()
  {
    if ((currentCellRenderer != null) && ((currentCellRenderer instanceof DefaultTreeCellRenderer)))
    {
      DefaultTreeCellEditor localDefaultTreeCellEditor = new DefaultTreeCellEditor(tree, (DefaultTreeCellRenderer)currentCellRenderer);
      return localDefaultTreeCellEditor;
    }
    return new DefaultTreeCellEditor(tree, null);
  }
  
  protected TreeCellRenderer createDefaultCellRenderer()
  {
    return new DefaultTreeCellRenderer();
  }
  
  protected TreeModelListener createTreeModelListener()
  {
    return getHandler();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    completeEditing();
    prepareForUIUninstall();
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    uninstallComponents();
    completeUIUninstall();
  }
  
  protected void prepareForUIUninstall() {}
  
  protected void completeUIUninstall()
  {
    if (createdRenderer) {
      tree.setCellRenderer(null);
    }
    if (createdCellEditor) {
      tree.setCellEditor(null);
    }
    cellEditor = null;
    currentCellRenderer = null;
    rendererPane = null;
    componentListener = null;
    propertyChangeListener = null;
    mouseListener = null;
    focusListener = null;
    keyListener = null;
    setSelectionModel(null);
    treeState = null;
    drawingCache = null;
    selectionModelPropertyChangeListener = null;
    tree = null;
    treeModel = null;
    treeSelectionModel = null;
    treeSelectionListener = null;
    treeExpansionListener = null;
  }
  
  protected void uninstallDefaults()
  {
    if ((tree.getTransferHandler() instanceof UIResource)) {
      tree.setTransferHandler(null);
    }
  }
  
  protected void uninstallListeners()
  {
    if (componentListener != null) {
      tree.removeComponentListener(componentListener);
    }
    if (propertyChangeListener != null) {
      tree.removePropertyChangeListener(propertyChangeListener);
    }
    if (mouseListener != null)
    {
      tree.removeMouseListener(mouseListener);
      if ((mouseListener instanceof MouseMotionListener)) {
        tree.removeMouseMotionListener((MouseMotionListener)mouseListener);
      }
    }
    if (focusListener != null) {
      tree.removeFocusListener(focusListener);
    }
    if (keyListener != null) {
      tree.removeKeyListener(keyListener);
    }
    if (treeExpansionListener != null) {
      tree.removeTreeExpansionListener(treeExpansionListener);
    }
    if ((treeModel != null) && (treeModelListener != null)) {
      treeModel.removeTreeModelListener(treeModelListener);
    }
    if ((selectionModelPropertyChangeListener != null) && (treeSelectionModel != null)) {
      treeSelectionModel.removePropertyChangeListener(selectionModelPropertyChangeListener);
    }
    if ((treeSelectionListener != null) && (treeSelectionModel != null)) {
      treeSelectionModel.removeTreeSelectionListener(treeSelectionListener);
    }
    handler = null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIActionMap(tree, null);
    SwingUtilities.replaceUIInputMap(tree, 1, null);
    SwingUtilities.replaceUIInputMap(tree, 0, null);
  }
  
  protected void uninstallComponents()
  {
    if (rendererPane != null) {
      tree.remove(rendererPane);
    }
  }
  
  private void redoTheLayout()
  {
    if (treeState != null) {
      treeState.invalidateSizes();
    }
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    Component localComponent = (Component)localUIDefaults.get(BASELINE_COMPONENT_KEY);
    if (localComponent == null)
    {
      TreeCellRenderer localTreeCellRenderer = createDefaultCellRenderer();
      localComponent = localTreeCellRenderer.getTreeCellRendererComponent(tree, "a", false, false, false, -1, false);
      localUIDefaults.put(BASELINE_COMPONENT_KEY, localComponent);
    }
    int i = tree.getRowHeight();
    int j;
    if (i > 0)
    {
      j = localComponent.getBaseline(Integer.MAX_VALUE, i);
    }
    else
    {
      Dimension localDimension = localComponent.getPreferredSize();
      j = localComponent.getBaseline(width, height);
    }
    return j + tree.getInsets().top;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (tree != paramJComponent) {
      throw new InternalError("incorrect component");
    }
    if (treeState == null) {
      return;
    }
    Rectangle localRectangle1 = paramGraphics.getClipBounds();
    Insets localInsets = tree.getInsets();
    TreePath localTreePath1 = getClosestPathForLocation(tree, 0, y);
    Enumeration localEnumeration = treeState.getVisiblePathsFrom(localTreePath1);
    int i = treeState.getRowForPath(localTreePath1);
    int j = y + height;
    drawingCache.clear();
    if ((localTreePath1 != null) && (localEnumeration != null))
    {
      TreePath localTreePath2 = localTreePath1;
      for (localTreePath2 = localTreePath2.getParentPath(); localTreePath2 != null; localTreePath2 = localTreePath2.getParentPath())
      {
        paintVerticalPartOfLeg(paramGraphics, localRectangle1, localInsets, localTreePath2);
        drawingCache.put(localTreePath2, Boolean.TRUE);
      }
      int k = 0;
      Rectangle localRectangle2 = new Rectangle();
      boolean bool4 = isRootVisible();
      while ((k == 0) && (localEnumeration.hasMoreElements()))
      {
        TreePath localTreePath3 = (TreePath)localEnumeration.nextElement();
        if (localTreePath3 != null)
        {
          boolean bool3 = treeModel.isLeaf(localTreePath3.getLastPathComponent());
          boolean bool2;
          boolean bool1;
          if (bool3)
          {
            bool1 = bool2 = 0;
          }
          else
          {
            bool1 = treeState.getExpandedState(localTreePath3);
            bool2 = tree.hasBeenExpanded(localTreePath3);
          }
          Rectangle localRectangle3 = getPathBounds(localTreePath3, localInsets, localRectangle2);
          if (localRectangle3 == null) {
            return;
          }
          localTreePath2 = localTreePath3.getParentPath();
          if (localTreePath2 != null)
          {
            if (drawingCache.get(localTreePath2) == null)
            {
              paintVerticalPartOfLeg(paramGraphics, localRectangle1, localInsets, localTreePath2);
              drawingCache.put(localTreePath2, Boolean.TRUE);
            }
            paintHorizontalPartOfLeg(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath3, i, bool1, bool2, bool3);
          }
          else if ((bool4) && (i == 0))
          {
            paintHorizontalPartOfLeg(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath3, i, bool1, bool2, bool3);
          }
          if (shouldPaintExpandControl(localTreePath3, i, bool1, bool2, bool3)) {
            paintExpandControl(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath3, i, bool1, bool2, bool3);
          }
          paintRow(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath3, i, bool1, bool2, bool3);
          if (y + height >= j) {
            k = 1;
          }
        }
        else
        {
          k = 1;
        }
        i++;
      }
    }
    paintDropLine(paramGraphics);
    rendererPane.removeAll();
    drawingCache.clear();
  }
  
  protected boolean isDropLine(JTree.DropLocation paramDropLocation)
  {
    return (paramDropLocation != null) && (paramDropLocation.getPath() != null) && (paramDropLocation.getChildIndex() != -1);
  }
  
  protected void paintDropLine(Graphics paramGraphics)
  {
    JTree.DropLocation localDropLocation = tree.getDropLocation();
    if (!isDropLine(localDropLocation)) {
      return;
    }
    Color localColor = UIManager.getColor("Tree.dropLineColor");
    if (localColor != null)
    {
      paramGraphics.setColor(localColor);
      Rectangle localRectangle = getDropLineRect(localDropLocation);
      paramGraphics.fillRect(x, y, width, height);
    }
  }
  
  protected Rectangle getDropLineRect(JTree.DropLocation paramDropLocation)
  {
    TreePath localTreePath1 = paramDropLocation.getPath();
    int i = paramDropLocation.getChildIndex();
    boolean bool = leftToRight;
    Insets localInsets = tree.getInsets();
    Rectangle localRectangle1;
    if (tree.getRowCount() == 0)
    {
      localRectangle1 = new Rectangle(left, top, tree.getWidth() - left - right, 0);
    }
    else
    {
      TreeModel localTreeModel = getModel();
      Object localObject = localTreeModel.getRoot();
      if ((localTreePath1.getLastPathComponent() == localObject) && (i >= localTreeModel.getChildCount(localObject)))
      {
        localRectangle1 = tree.getRowBounds(tree.getRowCount() - 1);
        y += height;
        Rectangle localRectangle2;
        if (!tree.isRootVisible())
        {
          localRectangle2 = tree.getRowBounds(0);
        }
        else if (localTreeModel.getChildCount(localObject) == 0)
        {
          localRectangle2 = tree.getRowBounds(0);
          x += totalChildIndent;
          width -= totalChildIndent + totalChildIndent;
        }
        else
        {
          TreePath localTreePath2 = localTreePath1.pathByAddingChild(localTreeModel.getChild(localObject, localTreeModel.getChildCount(localObject) - 1));
          localRectangle2 = tree.getPathBounds(localTreePath2);
        }
        x = x;
        width = width;
      }
      else
      {
        localRectangle1 = tree.getPathBounds(localTreePath1.pathByAddingChild(localTreeModel.getChild(localTreePath1.getLastPathComponent(), i)));
      }
    }
    if (y != 0) {
      y -= 1;
    }
    if (!bool) {
      x = (x + width - 100);
    }
    width = 100;
    height = 2;
    return localRectangle1;
  }
  
  protected void paintHorizontalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (!paintLines) {
      return;
    }
    int i = paramTreePath.getPathCount() - 1;
    if (((i == 0) || ((i == 1) && (!isRootVisible()))) && (!getShowsRootHandles())) {
      return;
    }
    int j = x;
    int k = x + width;
    int m = y;
    int n = y + height;
    int i1 = y + height / 2;
    int i2;
    int i3;
    if (leftToRight)
    {
      i2 = x - getRightChildIndent();
      i3 = x - getHorizontalLegBuffer();
      if ((i1 >= m) && (i1 < n) && (i3 >= j) && (i2 < k) && (i2 < i3))
      {
        paramGraphics.setColor(getHashColor());
        paintHorizontalLine(paramGraphics, tree, i1, i2, i3 - 1);
      }
    }
    else
    {
      i2 = x + width + getHorizontalLegBuffer();
      i3 = x + width + getRightChildIndent();
      if ((i1 >= m) && (i1 < n) && (i3 >= j) && (i2 < k) && (i2 < i3))
      {
        paramGraphics.setColor(getHashColor());
        paintHorizontalLine(paramGraphics, tree, i1, i2, i3 - 1);
      }
    }
  }
  
  protected void paintVerticalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle, Insets paramInsets, TreePath paramTreePath)
  {
    if (!paintLines) {
      return;
    }
    int i = paramTreePath.getPathCount() - 1;
    if ((i == 0) && (!getShowsRootHandles()) && (!isRootVisible())) {
      return;
    }
    int j = getRowX(-1, i + 1);
    if (leftToRight) {
      j = j - getRightChildIndent() + left;
    } else {
      j = tree.getWidth() - j - right + getRightChildIndent() - 1;
    }
    int k = x;
    int m = x + (width - 1);
    if ((j >= k) && (j <= m))
    {
      int n = y;
      int i1 = y + height;
      Rectangle localRectangle1 = getPathBounds(tree, paramTreePath);
      Rectangle localRectangle2 = getPathBounds(tree, getLastChildPath(paramTreePath));
      if (localRectangle2 == null) {
        return;
      }
      int i2;
      if (localRectangle1 == null) {
        i2 = Math.max(top + getVerticalLegBuffer(), n);
      } else {
        i2 = Math.max(y + height + getVerticalLegBuffer(), n);
      }
      if ((i == 0) && (!isRootVisible()))
      {
        TreeModel localTreeModel = getModel();
        if (localTreeModel != null)
        {
          Object localObject = localTreeModel.getRoot();
          if (localTreeModel.getChildCount(localObject) > 0)
          {
            localRectangle1 = getPathBounds(tree, paramTreePath.pathByAddingChild(localTreeModel.getChild(localObject, 0)));
            if (localRectangle1 != null) {
              i2 = Math.max(top + getVerticalLegBuffer(), y + height / 2);
            }
          }
        }
      }
      int i3 = Math.min(y + height / 2, i1);
      if (i2 <= i3)
      {
        paramGraphics.setColor(getHashColor());
        paintVerticalLine(paramGraphics, tree, j, i2, i3);
      }
    }
  }
  
  protected void paintExpandControl(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    Object localObject = paramTreePath.getLastPathComponent();
    if ((!paramBoolean3) && ((!paramBoolean2) || (treeModel.getChildCount(localObject) > 0)))
    {
      int i;
      if (leftToRight) {
        i = x - getRightChildIndent() + 1;
      } else {
        i = x + width + getRightChildIndent() - 1;
      }
      int j = y + height / 2;
      Icon localIcon;
      if (paramBoolean1)
      {
        localIcon = getExpandedIcon();
        if (localIcon != null) {
          drawCentered(tree, paramGraphics, localIcon, i, j);
        }
      }
      else
      {
        localIcon = getCollapsedIcon();
        if (localIcon != null) {
          drawCentered(tree, paramGraphics, localIcon, i, j);
        }
      }
    }
  }
  
  protected void paintRow(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if ((editingComponent != null) && (editingRow == paramInt)) {
      return;
    }
    int i;
    if (tree.hasFocus()) {
      i = getLeadSelectionRow();
    } else {
      i = -1;
    }
    Component localComponent = currentCellRenderer.getTreeCellRendererComponent(tree, paramTreePath.getLastPathComponent(), tree.isRowSelected(paramInt), paramBoolean1, paramBoolean3, paramInt, i == paramInt);
    rendererPane.paintComponent(paramGraphics, localComponent, tree, x, y, width, height, true);
  }
  
  protected boolean shouldPaintExpandControl(TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramBoolean3) {
      return false;
    }
    int i = paramTreePath.getPathCount() - 1;
    return ((i != 0) && ((i != 1) || (isRootVisible()))) || (getShowsRootHandles());
  }
  
  protected void paintVerticalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3)
  {
    if (lineTypeDashed) {
      drawDashedVerticalLine(paramGraphics, paramInt1, paramInt2, paramInt3);
    } else {
      paramGraphics.drawLine(paramInt1, paramInt2, paramInt1, paramInt3);
    }
  }
  
  protected void paintHorizontalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3)
  {
    if (lineTypeDashed) {
      drawDashedHorizontalLine(paramGraphics, paramInt1, paramInt2, paramInt3);
    } else {
      paramGraphics.drawLine(paramInt2, paramInt1, paramInt3, paramInt1);
    }
  }
  
  protected int getVerticalLegBuffer()
  {
    return 0;
  }
  
  protected int getHorizontalLegBuffer()
  {
    return 0;
  }
  
  private int findCenteredX(int paramInt1, int paramInt2)
  {
    return leftToRight ? paramInt1 - (int)Math.ceil(paramInt2 / 2.0D) : paramInt1 - (int)Math.floor(paramInt2 / 2.0D);
  }
  
  protected void drawCentered(Component paramComponent, Graphics paramGraphics, Icon paramIcon, int paramInt1, int paramInt2)
  {
    paramIcon.paintIcon(paramComponent, paramGraphics, findCenteredX(paramInt1, paramIcon.getIconWidth()), paramInt2 - paramIcon.getIconHeight() / 2);
  }
  
  protected void drawDashedHorizontalLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt2 += paramInt2 % 2;
    for (int i = paramInt2; i <= paramInt3; i += 2) {
      paramGraphics.drawLine(i, paramInt1, i, paramInt1);
    }
  }
  
  protected void drawDashedVerticalLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt2 += paramInt2 % 2;
    for (int i = paramInt2; i <= paramInt3; i += 2) {
      paramGraphics.drawLine(paramInt1, i, paramInt1, i);
    }
  }
  
  protected int getRowX(int paramInt1, int paramInt2)
  {
    return totalChildIndent * (paramInt2 + depthOffset);
  }
  
  protected void updateLayoutCacheExpandedNodes()
  {
    if ((treeModel != null) && (treeModel.getRoot() != null)) {
      updateExpandedDescendants(new TreePath(treeModel.getRoot()));
    }
  }
  
  private void updateLayoutCacheExpandedNodesIfNecessary()
  {
    if ((treeModel != null) && (treeModel.getRoot() != null))
    {
      TreePath localTreePath = new TreePath(treeModel.getRoot());
      if (tree.isExpanded(localTreePath)) {
        updateLayoutCacheExpandedNodes();
      } else {
        treeState.setExpandedState(localTreePath, false);
      }
    }
  }
  
  protected void updateExpandedDescendants(TreePath paramTreePath)
  {
    completeEditing();
    if (treeState != null)
    {
      treeState.setExpandedState(paramTreePath, true);
      Enumeration localEnumeration = tree.getExpandedDescendants(paramTreePath);
      if (localEnumeration != null) {
        while (localEnumeration.hasMoreElements())
        {
          paramTreePath = (TreePath)localEnumeration.nextElement();
          treeState.setExpandedState(paramTreePath, true);
        }
      }
      updateLeadSelectionRow();
      updateSize();
    }
  }
  
  protected TreePath getLastChildPath(TreePath paramTreePath)
  {
    if (treeModel != null)
    {
      int i = treeModel.getChildCount(paramTreePath.getLastPathComponent());
      if (i > 0) {
        return paramTreePath.pathByAddingChild(treeModel.getChild(paramTreePath.getLastPathComponent(), i - 1));
      }
    }
    return null;
  }
  
  protected void updateDepthOffset()
  {
    if (isRootVisible())
    {
      if (getShowsRootHandles()) {
        depthOffset = 1;
      } else {
        depthOffset = 0;
      }
    }
    else if (!getShowsRootHandles()) {
      depthOffset = -1;
    } else {
      depthOffset = 0;
    }
  }
  
  protected void updateCellEditor()
  {
    completeEditing();
    TreeCellEditor localTreeCellEditor;
    if (tree == null)
    {
      localTreeCellEditor = null;
    }
    else if (tree.isEditable())
    {
      localTreeCellEditor = tree.getCellEditor();
      if (localTreeCellEditor == null)
      {
        localTreeCellEditor = createDefaultCellEditor();
        if (localTreeCellEditor != null)
        {
          tree.setCellEditor(localTreeCellEditor);
          createdCellEditor = true;
        }
      }
    }
    else
    {
      localTreeCellEditor = null;
    }
    if (localTreeCellEditor != cellEditor)
    {
      if ((cellEditor != null) && (cellEditorListener != null)) {
        cellEditor.removeCellEditorListener(cellEditorListener);
      }
      cellEditor = localTreeCellEditor;
      if (cellEditorListener == null) {
        cellEditorListener = createCellEditorListener();
      }
      if ((localTreeCellEditor != null) && (cellEditorListener != null)) {
        localTreeCellEditor.addCellEditorListener(cellEditorListener);
      }
      createdCellEditor = false;
    }
  }
  
  protected void updateRenderer()
  {
    if (tree != null)
    {
      TreeCellRenderer localTreeCellRenderer = tree.getCellRenderer();
      if (localTreeCellRenderer == null)
      {
        tree.setCellRenderer(createDefaultCellRenderer());
        createdRenderer = true;
      }
      else
      {
        createdRenderer = false;
        currentCellRenderer = localTreeCellRenderer;
        if (createdCellEditor) {
          tree.setCellEditor(null);
        }
      }
    }
    else
    {
      createdRenderer = false;
      currentCellRenderer = null;
    }
    updateCellEditor();
  }
  
  protected void configureLayoutCache()
  {
    if ((treeState != null) && (tree != null))
    {
      if (nodeDimensions == null) {
        nodeDimensions = createNodeDimensions();
      }
      treeState.setNodeDimensions(nodeDimensions);
      treeState.setRootVisible(tree.isRootVisible());
      treeState.setRowHeight(tree.getRowHeight());
      treeState.setSelectionModel(getSelectionModel());
      if (treeState.getModel() != tree.getModel()) {
        treeState.setModel(tree.getModel());
      }
      updateLayoutCacheExpandedNodesIfNecessary();
      if (isLargeModel())
      {
        if (componentListener == null)
        {
          componentListener = createComponentListener();
          if (componentListener != null) {
            tree.addComponentListener(componentListener);
          }
        }
      }
      else if (componentListener != null)
      {
        tree.removeComponentListener(componentListener);
        componentListener = null;
      }
    }
    else if (componentListener != null)
    {
      tree.removeComponentListener(componentListener);
      componentListener = null;
    }
  }
  
  protected void updateSize()
  {
    validCachedPreferredSize = false;
    tree.treeDidChange();
  }
  
  private void updateSize0()
  {
    validCachedPreferredSize = false;
    tree.revalidate();
  }
  
  protected void updateCachedPreferredSize()
  {
    if (treeState != null)
    {
      Insets localInsets = tree.getInsets();
      if (isLargeModel())
      {
        Rectangle localRectangle = tree.getVisibleRect();
        if ((x == 0) && (y == 0) && (width == 0) && (height == 0) && (tree.getVisibleRowCount() > 0))
        {
          width = 1;
          height = (tree.getRowHeight() * tree.getVisibleRowCount());
        }
        else
        {
          x -= left;
          y -= top;
        }
        Container localContainer = SwingUtilities.getUnwrappedParent(tree);
        if ((localContainer instanceof JViewport))
        {
          localContainer = localContainer.getParent();
          if ((localContainer instanceof JScrollPane))
          {
            JScrollPane localJScrollPane = (JScrollPane)localContainer;
            JScrollBar localJScrollBar = localJScrollPane.getHorizontalScrollBar();
            if ((localJScrollBar != null) && (localJScrollBar.isVisible()))
            {
              int i = localJScrollBar.getHeight();
              y -= i;
              height += i;
            }
          }
        }
        preferredSize.width = treeState.getPreferredWidth(localRectangle);
      }
      else
      {
        preferredSize.width = treeState.getPreferredWidth(null);
      }
      preferredSize.height = treeState.getPreferredHeight();
      preferredSize.width += left + right;
      preferredSize.height += top + bottom;
    }
    validCachedPreferredSize = true;
  }
  
  protected void pathWasExpanded(TreePath paramTreePath)
  {
    if (tree != null) {
      tree.fireTreeExpanded(paramTreePath);
    }
  }
  
  protected void pathWasCollapsed(TreePath paramTreePath)
  {
    if (tree != null) {
      tree.fireTreeCollapsed(paramTreePath);
    }
  }
  
  protected void ensureRowsAreVisible(int paramInt1, int paramInt2)
  {
    if ((tree != null) && (paramInt1 >= 0) && (paramInt2 < getRowCount(tree)))
    {
      boolean bool = DefaultLookup.getBoolean(tree, this, "Tree.scrollsHorizontallyAndVertically", false);
      Rectangle localRectangle1;
      if (paramInt1 == paramInt2)
      {
        localRectangle1 = getPathBounds(tree, getPathForRow(tree, paramInt1));
        if (localRectangle1 != null)
        {
          if (!bool)
          {
            x = tree.getVisibleRect().x;
            width = 1;
          }
          tree.scrollRectToVisible(localRectangle1);
        }
      }
      else
      {
        localRectangle1 = getPathBounds(tree, getPathForRow(tree, paramInt1));
        if (localRectangle1 != null)
        {
          Rectangle localRectangle2 = tree.getVisibleRect();
          Rectangle localRectangle3 = localRectangle1;
          int i = y;
          int j = i + height;
          for (int k = paramInt1 + 1; k <= paramInt2; k++)
          {
            localRectangle3 = getPathBounds(tree, getPathForRow(tree, k));
            if (localRectangle3 == null) {
              return;
            }
            if (y + height > j) {
              k = paramInt2;
            }
          }
          tree.scrollRectToVisible(new Rectangle(x, i, 1, y + height - i));
        }
      }
    }
  }
  
  public void setPreferredMinSize(Dimension paramDimension)
  {
    preferredMinSize = paramDimension;
  }
  
  public Dimension getPreferredMinSize()
  {
    if (preferredMinSize == null) {
      return null;
    }
    return new Dimension(preferredMinSize);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return getPreferredSize(paramJComponent, true);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent, boolean paramBoolean)
  {
    Dimension localDimension = getPreferredMinSize();
    if (!validCachedPreferredSize) {
      updateCachedPreferredSize();
    }
    if (tree != null)
    {
      if (localDimension != null) {
        return new Dimension(Math.max(width, preferredSize.width), Math.max(height, preferredSize.height));
      }
      return new Dimension(preferredSize.width, preferredSize.height);
    }
    if (localDimension != null) {
      return localDimension;
    }
    return new Dimension(0, 0);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    if (getPreferredMinSize() != null) {
      return getPreferredMinSize();
    }
    return new Dimension(0, 0);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    if (tree != null) {
      return getPreferredSize(tree);
    }
    if (getPreferredMinSize() != null) {
      return getPreferredMinSize();
    }
    return new Dimension(0, 0);
  }
  
  protected void completeEditing()
  {
    if ((tree.getInvokesStopCellEditing()) && (stopEditingInCompleteEditing) && (editingComponent != null)) {
      cellEditor.stopCellEditing();
    }
    completeEditing(false, true, false);
  }
  
  protected void completeEditing(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if ((stopEditingInCompleteEditing) && (editingComponent != null))
    {
      Component localComponent = editingComponent;
      TreePath localTreePath = editingPath;
      TreeCellEditor localTreeCellEditor = cellEditor;
      Object localObject = localTreeCellEditor.getCellEditorValue();
      Rectangle localRectangle = getPathBounds(tree, editingPath);
      int i = (tree != null) && ((tree.hasFocus()) || (SwingUtilities.findFocusOwner(editingComponent) != null)) ? 1 : 0;
      editingComponent = null;
      editingPath = null;
      if (paramBoolean1) {
        localTreeCellEditor.stopCellEditing();
      } else if (paramBoolean2) {
        localTreeCellEditor.cancelCellEditing();
      }
      tree.remove(localComponent);
      if (editorHasDifferentSize)
      {
        treeState.invalidatePathBounds(localTreePath);
        updateSize();
      }
      else if (localRectangle != null)
      {
        x = 0;
        width = tree.getSize().width;
        tree.repaint(localRectangle);
      }
      if (i != 0) {
        tree.requestFocus();
      }
      if (paramBoolean3) {
        treeModel.valueForPathChanged(localTreePath, localObject);
      }
    }
  }
  
  private boolean startEditingOnRelease(TreePath paramTreePath, MouseEvent paramMouseEvent1, MouseEvent paramMouseEvent2)
  {
    releaseEvent = paramMouseEvent2;
    try
    {
      boolean bool = startEditing(paramTreePath, paramMouseEvent1);
      return bool;
    }
    finally
    {
      releaseEvent = null;
    }
  }
  
  protected boolean startEditing(TreePath paramTreePath, MouseEvent paramMouseEvent)
  {
    if ((isEditing(tree)) && (tree.getInvokesStopCellEditing()) && (!stopEditing(tree))) {
      return false;
    }
    completeEditing();
    if ((cellEditor != null) && (tree.isPathEditable(paramTreePath)))
    {
      int i = getRowForPath(tree, paramTreePath);
      if (cellEditor.isCellEditable(paramMouseEvent))
      {
        editingComponent = cellEditor.getTreeCellEditorComponent(tree, paramTreePath.getLastPathComponent(), tree.isPathSelected(paramTreePath), tree.isExpanded(paramTreePath), treeModel.isLeaf(paramTreePath.getLastPathComponent()), i);
        Rectangle localRectangle = getPathBounds(tree, paramTreePath);
        if (localRectangle == null) {
          return false;
        }
        editingRow = i;
        Dimension localDimension = editingComponent.getPreferredSize();
        if ((height != height) && (getRowHeight() > 0)) {
          height = getRowHeight();
        }
        if ((width != width) || (height != height))
        {
          editorHasDifferentSize = true;
          treeState.invalidatePathBounds(paramTreePath);
          updateSize();
          localRectangle = getPathBounds(tree, paramTreePath);
          if (localRectangle == null) {
            return false;
          }
        }
        else
        {
          editorHasDifferentSize = false;
        }
        tree.add(editingComponent);
        editingComponent.setBounds(x, y, width, height);
        editingPath = paramTreePath;
        AWTAccessor.getComponentAccessor().revalidateSynchronously(editingComponent);
        editingComponent.repaint();
        if (cellEditor.shouldSelectCell(paramMouseEvent))
        {
          stopEditingInCompleteEditing = false;
          tree.setSelectionRow(i);
          stopEditingInCompleteEditing = true;
        }
        Component localComponent1 = SwingUtilities2.compositeRequestFocus(editingComponent);
        int j = 1;
        if (paramMouseEvent != null)
        {
          Point localPoint = SwingUtilities.convertPoint(tree, new Point(paramMouseEvent.getX(), paramMouseEvent.getY()), editingComponent);
          Component localComponent2 = SwingUtilities.getDeepestComponentAt(editingComponent, x, y);
          if (localComponent2 != null)
          {
            MouseInputHandler localMouseInputHandler = new MouseInputHandler(tree, localComponent2, paramMouseEvent, localComponent1);
            if (releaseEvent != null) {
              localMouseInputHandler.mouseReleased(releaseEvent);
            }
            j = 0;
          }
        }
        if ((j != 0) && ((localComponent1 instanceof JTextField))) {
          ((JTextField)localComponent1).selectAll();
        }
        return true;
      }
      editingComponent = null;
    }
    return false;
  }
  
  protected void checkForClickInExpandControl(TreePath paramTreePath, int paramInt1, int paramInt2)
  {
    if (isLocationInExpandControl(paramTreePath, paramInt1, paramInt2)) {
      handleExpandControlClick(paramTreePath, paramInt1, paramInt2);
    }
  }
  
  protected boolean isLocationInExpandControl(TreePath paramTreePath, int paramInt1, int paramInt2)
  {
    if ((paramTreePath != null) && (!treeModel.isLeaf(paramTreePath.getLastPathComponent())))
    {
      Insets localInsets = tree.getInsets();
      int i;
      if (getExpandedIcon() != null) {
        i = getExpandedIcon().getIconWidth();
      } else {
        i = 8;
      }
      int j = getRowX(tree.getRowForPath(paramTreePath), paramTreePath.getPathCount() - 1);
      if (leftToRight) {
        j = j + left - getRightChildIndent() + 1;
      } else {
        j = tree.getWidth() - j - right + getRightChildIndent() - 1;
      }
      j = findCenteredX(j, i);
      return (paramInt1 >= j) && (paramInt1 < j + i);
    }
    return false;
  }
  
  protected void handleExpandControlClick(TreePath paramTreePath, int paramInt1, int paramInt2)
  {
    toggleExpandState(paramTreePath);
  }
  
  protected void toggleExpandState(TreePath paramTreePath)
  {
    if (!tree.isExpanded(paramTreePath))
    {
      int i = getRowForPath(tree, paramTreePath);
      tree.expandPath(paramTreePath);
      updateSize();
      if (i != -1) {
        if (tree.getScrollsOnExpand()) {
          ensureRowsAreVisible(i, i + treeState.getVisibleChildCount(paramTreePath));
        } else {
          ensureRowsAreVisible(i, i);
        }
      }
    }
    else
    {
      tree.collapsePath(paramTreePath);
      updateSize();
    }
  }
  
  protected boolean isToggleSelectionEvent(MouseEvent paramMouseEvent)
  {
    return (SwingUtilities.isLeftMouseButton(paramMouseEvent)) && (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent));
  }
  
  protected boolean isMultiSelectEvent(MouseEvent paramMouseEvent)
  {
    return (SwingUtilities.isLeftMouseButton(paramMouseEvent)) && (paramMouseEvent.isShiftDown());
  }
  
  protected boolean isToggleEvent(MouseEvent paramMouseEvent)
  {
    if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
      return false;
    }
    int i = tree.getToggleClickCount();
    if (i <= 0) {
      return false;
    }
    return paramMouseEvent.getClickCount() % i == 0;
  }
  
  protected void selectPathForEvent(TreePath paramTreePath, MouseEvent paramMouseEvent)
  {
    if (isMultiSelectEvent(paramMouseEvent))
    {
      TreePath localTreePath1 = getAnchorSelectionPath();
      int i = localTreePath1 == null ? -1 : getRowForPath(tree, localTreePath1);
      if ((i == -1) || (tree.getSelectionModel().getSelectionMode() == 1))
      {
        tree.setSelectionPath(paramTreePath);
      }
      else
      {
        int j = getRowForPath(tree, paramTreePath);
        TreePath localTreePath2 = localTreePath1;
        if (isToggleSelectionEvent(paramMouseEvent))
        {
          if (tree.isRowSelected(i))
          {
            tree.addSelectionInterval(i, j);
          }
          else
          {
            tree.removeSelectionInterval(i, j);
            tree.addSelectionInterval(j, j);
          }
        }
        else if (j < i) {
          tree.setSelectionInterval(j, i);
        } else {
          tree.setSelectionInterval(i, j);
        }
        lastSelectedRow = j;
        setAnchorSelectionPath(localTreePath2);
        setLeadSelectionPath(paramTreePath);
      }
    }
    else if (isToggleSelectionEvent(paramMouseEvent))
    {
      if (tree.isPathSelected(paramTreePath)) {
        tree.removeSelectionPath(paramTreePath);
      } else {
        tree.addSelectionPath(paramTreePath);
      }
      lastSelectedRow = getRowForPath(tree, paramTreePath);
      setAnchorSelectionPath(paramTreePath);
      setLeadSelectionPath(paramTreePath);
    }
    else if (SwingUtilities.isLeftMouseButton(paramMouseEvent))
    {
      tree.setSelectionPath(paramTreePath);
      if (isToggleEvent(paramMouseEvent)) {
        toggleExpandState(paramTreePath);
      }
    }
  }
  
  protected boolean isLeaf(int paramInt)
  {
    TreePath localTreePath = getPathForRow(tree, paramInt);
    if (localTreePath != null) {
      return treeModel.isLeaf(localTreePath.getLastPathComponent());
    }
    return true;
  }
  
  /* Error */
  private void setAnchorSelectionPath(TreePath paramTreePath)
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_1
    //   2: putfield 1226	javax/swing/plaf/basic/BasicTreeUI:ignoreLAChange	Z
    //   5: aload_0
    //   6: getfield 1249	javax/swing/plaf/basic/BasicTreeUI:tree	Ljavax/swing/JTree;
    //   9: aload_1
    //   10: invokevirtual 1373	javax/swing/JTree:setAnchorSelectionPath	(Ljavax/swing/tree/TreePath;)V
    //   13: aload_0
    //   14: iconst_0
    //   15: putfield 1226	javax/swing/plaf/basic/BasicTreeUI:ignoreLAChange	Z
    //   18: goto +11 -> 29
    //   21: astore_2
    //   22: aload_0
    //   23: iconst_0
    //   24: putfield 1226	javax/swing/plaf/basic/BasicTreeUI:ignoreLAChange	Z
    //   27: aload_2
    //   28: athrow
    //   29: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	BasicTreeUI
    //   0	30	1	paramTreePath	TreePath
    //   21	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	13	21	finally
  }
  
  private TreePath getAnchorSelectionPath()
  {
    return tree.getAnchorSelectionPath();
  }
  
  private void setLeadSelectionPath(TreePath paramTreePath)
  {
    setLeadSelectionPath(paramTreePath, false);
  }
  
  private void setLeadSelectionPath(TreePath paramTreePath, boolean paramBoolean)
  {
    Rectangle localRectangle = paramBoolean ? getPathBounds(tree, getLeadSelectionPath()) : null;
    ignoreLAChange = true;
    try
    {
      tree.setLeadSelectionPath(paramTreePath);
    }
    finally
    {
      ignoreLAChange = false;
    }
    leadRow = getRowForPath(tree, paramTreePath);
    if (paramBoolean)
    {
      if (localRectangle != null) {
        tree.repaint(getRepaintPathBounds(localRectangle));
      }
      localRectangle = getPathBounds(tree, paramTreePath);
      if (localRectangle != null) {
        tree.repaint(getRepaintPathBounds(localRectangle));
      }
    }
  }
  
  private Rectangle getRepaintPathBounds(Rectangle paramRectangle)
  {
    if (UIManager.getBoolean("Tree.repaintWholeRow"))
    {
      x = 0;
      width = tree.getWidth();
    }
    return paramRectangle;
  }
  
  private TreePath getLeadSelectionPath()
  {
    return tree.getLeadSelectionPath();
  }
  
  protected void updateLeadSelectionRow()
  {
    leadRow = getRowForPath(tree, getLeadSelectionPath());
  }
  
  protected int getLeadSelectionRow()
  {
    return leadRow;
  }
  
  private void extendSelection(TreePath paramTreePath)
  {
    TreePath localTreePath = getAnchorSelectionPath();
    int i = localTreePath == null ? -1 : getRowForPath(tree, localTreePath);
    int j = getRowForPath(tree, paramTreePath);
    if (i == -1)
    {
      tree.setSelectionRow(j);
    }
    else
    {
      if (i < j) {
        tree.setSelectionInterval(i, j);
      } else {
        tree.setSelectionInterval(j, i);
      }
      setAnchorSelectionPath(localTreePath);
      setLeadSelectionPath(paramTreePath);
    }
  }
  
  private void repaintPath(TreePath paramTreePath)
  {
    if (paramTreePath != null)
    {
      Rectangle localRectangle = getPathBounds(tree, paramTreePath);
      if (localRectangle != null) {
        tree.repaint(x, y, width, height);
      }
    }
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String SELECT_PREVIOUS = "selectPrevious";
    private static final String SELECT_PREVIOUS_CHANGE_LEAD = "selectPreviousChangeLead";
    private static final String SELECT_PREVIOUS_EXTEND_SELECTION = "selectPreviousExtendSelection";
    private static final String SELECT_NEXT = "selectNext";
    private static final String SELECT_NEXT_CHANGE_LEAD = "selectNextChangeLead";
    private static final String SELECT_NEXT_EXTEND_SELECTION = "selectNextExtendSelection";
    private static final String SELECT_CHILD = "selectChild";
    private static final String SELECT_CHILD_CHANGE_LEAD = "selectChildChangeLead";
    private static final String SELECT_PARENT = "selectParent";
    private static final String SELECT_PARENT_CHANGE_LEAD = "selectParentChangeLead";
    private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
    private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
    private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
    private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
    private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";
    private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
    private static final String SELECT_FIRST = "selectFirst";
    private static final String SELECT_FIRST_CHANGE_LEAD = "selectFirstChangeLead";
    private static final String SELECT_FIRST_EXTEND_SELECTION = "selectFirstExtendSelection";
    private static final String SELECT_LAST = "selectLast";
    private static final String SELECT_LAST_CHANGE_LEAD = "selectLastChangeLead";
    private static final String SELECT_LAST_EXTEND_SELECTION = "selectLastExtendSelection";
    private static final String TOGGLE = "toggle";
    private static final String CANCEL_EDITING = "cancel";
    private static final String START_EDITING = "startEditing";
    private static final String SELECT_ALL = "selectAll";
    private static final String CLEAR_SELECTION = "clearSelection";
    private static final String SCROLL_LEFT = "scrollLeft";
    private static final String SCROLL_RIGHT = "scrollRight";
    private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
    private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";
    private static final String SCROLL_RIGHT_CHANGE_LEAD = "scrollRightChangeLead";
    private static final String SCROLL_LEFT_CHANGE_LEAD = "scrollLeftChangeLead";
    private static final String EXPAND = "expand";
    private static final String COLLAPSE = "collapse";
    private static final String MOVE_SELECTION_TO_PARENT = "moveSelectionToParent";
    private static final String ADD_TO_SELECTION = "addToSelection";
    private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
    private static final String EXTEND_TO = "extendTo";
    private static final String MOVE_SELECTION_TO = "moveSelectionTo";
    
    Actions()
    {
      super();
    }
    
    Actions(String paramString)
    {
      super();
    }
    
    public boolean isEnabled(Object paramObject)
    {
      if (((paramObject instanceof JTree)) && (getName() == "cancel")) {
        return ((JTree)paramObject).isEditing();
      }
      return true;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTree localJTree = (JTree)paramActionEvent.getSource();
      BasicTreeUI localBasicTreeUI = (BasicTreeUI)BasicLookAndFeel.getUIOfType(localJTree.getUI(), BasicTreeUI.class);
      if (localBasicTreeUI == null) {
        return;
      }
      String str = getName();
      if (str == "selectPrevious")
      {
        increment(localJTree, localBasicTreeUI, -1, false, true);
      }
      else if (str == "selectPreviousChangeLead")
      {
        increment(localJTree, localBasicTreeUI, -1, false, false);
      }
      else if (str == "selectPreviousExtendSelection")
      {
        increment(localJTree, localBasicTreeUI, -1, true, true);
      }
      else if (str == "selectNext")
      {
        increment(localJTree, localBasicTreeUI, 1, false, true);
      }
      else if (str == "selectNextChangeLead")
      {
        increment(localJTree, localBasicTreeUI, 1, false, false);
      }
      else if (str == "selectNextExtendSelection")
      {
        increment(localJTree, localBasicTreeUI, 1, true, true);
      }
      else if (str == "selectChild")
      {
        traverse(localJTree, localBasicTreeUI, 1, true);
      }
      else if (str == "selectChildChangeLead")
      {
        traverse(localJTree, localBasicTreeUI, 1, false);
      }
      else if (str == "selectParent")
      {
        traverse(localJTree, localBasicTreeUI, -1, true);
      }
      else if (str == "selectParentChangeLead")
      {
        traverse(localJTree, localBasicTreeUI, -1, false);
      }
      else if (str == "scrollUpChangeSelection")
      {
        page(localJTree, localBasicTreeUI, -1, false, true);
      }
      else if (str == "scrollUpChangeLead")
      {
        page(localJTree, localBasicTreeUI, -1, false, false);
      }
      else if (str == "scrollUpExtendSelection")
      {
        page(localJTree, localBasicTreeUI, -1, true, true);
      }
      else if (str == "scrollDownChangeSelection")
      {
        page(localJTree, localBasicTreeUI, 1, false, true);
      }
      else if (str == "scrollDownExtendSelection")
      {
        page(localJTree, localBasicTreeUI, 1, true, true);
      }
      else if (str == "scrollDownChangeLead")
      {
        page(localJTree, localBasicTreeUI, 1, false, false);
      }
      else if (str == "selectFirst")
      {
        home(localJTree, localBasicTreeUI, -1, false, true);
      }
      else if (str == "selectFirstChangeLead")
      {
        home(localJTree, localBasicTreeUI, -1, false, false);
      }
      else if (str == "selectFirstExtendSelection")
      {
        home(localJTree, localBasicTreeUI, -1, true, true);
      }
      else if (str == "selectLast")
      {
        home(localJTree, localBasicTreeUI, 1, false, true);
      }
      else if (str == "selectLastChangeLead")
      {
        home(localJTree, localBasicTreeUI, 1, false, false);
      }
      else if (str == "selectLastExtendSelection")
      {
        home(localJTree, localBasicTreeUI, 1, true, true);
      }
      else if (str == "toggle")
      {
        toggle(localJTree, localBasicTreeUI);
      }
      else if (str == "cancel")
      {
        cancelEditing(localJTree, localBasicTreeUI);
      }
      else if (str == "startEditing")
      {
        startEditing(localJTree, localBasicTreeUI);
      }
      else if (str == "selectAll")
      {
        selectAll(localJTree, localBasicTreeUI, true);
      }
      else if (str == "clearSelection")
      {
        selectAll(localJTree, localBasicTreeUI, false);
      }
      else
      {
        int i;
        TreePath localTreePath;
        if (str == "addToSelection")
        {
          if (localBasicTreeUI.getRowCount(localJTree) > 0)
          {
            i = localBasicTreeUI.getLeadSelectionRow();
            if (!localJTree.isRowSelected(i))
            {
              localTreePath = localBasicTreeUI.getAnchorSelectionPath();
              localJTree.addSelectionRow(i);
              localBasicTreeUI.setAnchorSelectionPath(localTreePath);
            }
          }
        }
        else if (str == "toggleAndAnchor")
        {
          if (localBasicTreeUI.getRowCount(localJTree) > 0)
          {
            i = localBasicTreeUI.getLeadSelectionRow();
            localTreePath = localBasicTreeUI.getLeadSelectionPath();
            if (!localJTree.isRowSelected(i))
            {
              localJTree.addSelectionRow(i);
            }
            else
            {
              localJTree.removeSelectionRow(i);
              localBasicTreeUI.setLeadSelectionPath(localTreePath);
            }
            localBasicTreeUI.setAnchorSelectionPath(localTreePath);
          }
        }
        else if (str == "extendTo") {
          extendSelection(localJTree, localBasicTreeUI);
        } else if (str == "moveSelectionTo")
        {
          if (localBasicTreeUI.getRowCount(localJTree) > 0)
          {
            i = localBasicTreeUI.getLeadSelectionRow();
            localJTree.setSelectionInterval(i, i);
          }
        }
        else if (str == "scrollLeft") {
          scroll(localJTree, localBasicTreeUI, 0, -10);
        } else if (str == "scrollRight") {
          scroll(localJTree, localBasicTreeUI, 0, 10);
        } else if (str == "scrollLeftExtendSelection") {
          scrollChangeSelection(localJTree, localBasicTreeUI, -1, true, true);
        } else if (str == "scrollRightExtendSelection") {
          scrollChangeSelection(localJTree, localBasicTreeUI, 1, true, true);
        } else if (str == "scrollRightChangeLead") {
          scrollChangeSelection(localJTree, localBasicTreeUI, 1, false, false);
        } else if (str == "scrollLeftChangeLead") {
          scrollChangeSelection(localJTree, localBasicTreeUI, -1, false, false);
        } else if (str == "expand") {
          expand(localJTree, localBasicTreeUI);
        } else if (str == "collapse") {
          collapse(localJTree, localBasicTreeUI);
        } else if (str == "moveSelectionToParent") {
          moveSelectionToParent(localJTree, localBasicTreeUI);
        }
      }
    }
    
    private void scrollChangeSelection(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      int i;
      if (((i = paramBasicTreeUI.getRowCount(paramJTree)) > 0) && (treeSelectionModel != null))
      {
        Rectangle localRectangle = paramJTree.getVisibleRect();
        TreePath localTreePath;
        if (paramInt == -1)
        {
          localTreePath = paramBasicTreeUI.getClosestPathForLocation(paramJTree, x, y);
          x = Math.max(0, x - width);
        }
        else
        {
          x = Math.min(Math.max(0, paramJTree.getWidth() - width), x + width);
          localTreePath = paramBasicTreeUI.getClosestPathForLocation(paramJTree, x, y + height);
        }
        paramJTree.scrollRectToVisible(localRectangle);
        if (paramBoolean1) {
          paramBasicTreeUI.extendSelection(localTreePath);
        } else if (paramBoolean2) {
          paramJTree.setSelectionPath(localTreePath);
        } else {
          paramBasicTreeUI.setLeadSelectionPath(localTreePath, true);
        }
      }
    }
    
    private void scroll(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt1, int paramInt2)
    {
      Rectangle localRectangle = paramJTree.getVisibleRect();
      Dimension localDimension = paramJTree.getSize();
      if (paramInt1 == 0)
      {
        x += paramInt2;
        x = Math.max(0, x);
        x = Math.min(Math.max(0, width - width), x);
      }
      else
      {
        y += paramInt2;
        y = Math.max(0, y);
        y = Math.min(Math.max(0, width - height), y);
      }
      paramJTree.scrollRectToVisible(localRectangle);
    }
    
    private void extendSelection(JTree paramJTree, BasicTreeUI paramBasicTreeUI)
    {
      if (paramBasicTreeUI.getRowCount(paramJTree) > 0)
      {
        int i = paramBasicTreeUI.getLeadSelectionRow();
        if (i != -1)
        {
          TreePath localTreePath1 = paramBasicTreeUI.getLeadSelectionPath();
          TreePath localTreePath2 = paramBasicTreeUI.getAnchorSelectionPath();
          int j = paramBasicTreeUI.getRowForPath(paramJTree, localTreePath2);
          if (j == -1) {
            j = 0;
          }
          paramJTree.setSelectionInterval(j, i);
          paramBasicTreeUI.setLeadSelectionPath(localTreePath1);
          paramBasicTreeUI.setAnchorSelectionPath(localTreePath2);
        }
      }
    }
    
    private void selectAll(JTree paramJTree, BasicTreeUI paramBasicTreeUI, boolean paramBoolean)
    {
      int i = paramBasicTreeUI.getRowCount(paramJTree);
      if (i > 0)
      {
        TreePath localTreePath1;
        TreePath localTreePath2;
        if (paramBoolean)
        {
          if (paramJTree.getSelectionModel().getSelectionMode() == 1)
          {
            int j = paramBasicTreeUI.getLeadSelectionRow();
            if (j != -1)
            {
              paramJTree.setSelectionRow(j);
            }
            else if (paramJTree.getMinSelectionRow() == -1)
            {
              paramJTree.setSelectionRow(0);
              paramBasicTreeUI.ensureRowsAreVisible(0, 0);
            }
            return;
          }
          localTreePath1 = paramBasicTreeUI.getLeadSelectionPath();
          localTreePath2 = paramBasicTreeUI.getAnchorSelectionPath();
          if ((localTreePath1 != null) && (!paramJTree.isVisible(localTreePath1))) {
            localTreePath1 = null;
          }
          paramJTree.setSelectionInterval(0, i - 1);
          if (localTreePath1 != null) {
            paramBasicTreeUI.setLeadSelectionPath(localTreePath1);
          }
          if ((localTreePath2 != null) && (paramJTree.isVisible(localTreePath2))) {
            paramBasicTreeUI.setAnchorSelectionPath(localTreePath2);
          }
        }
        else
        {
          localTreePath1 = paramBasicTreeUI.getLeadSelectionPath();
          localTreePath2 = paramBasicTreeUI.getAnchorSelectionPath();
          paramJTree.clearSelection();
          paramBasicTreeUI.setAnchorSelectionPath(localTreePath2);
          paramBasicTreeUI.setLeadSelectionPath(localTreePath1);
        }
      }
    }
    
    private void startEditing(JTree paramJTree, BasicTreeUI paramBasicTreeUI)
    {
      TreePath localTreePath = paramBasicTreeUI.getLeadSelectionPath();
      int i = localTreePath != null ? paramBasicTreeUI.getRowForPath(paramJTree, localTreePath) : -1;
      if (i != -1) {
        paramJTree.startEditingAtPath(localTreePath);
      }
    }
    
    private void cancelEditing(JTree paramJTree, BasicTreeUI paramBasicTreeUI)
    {
      paramJTree.cancelEditing();
    }
    
    private void toggle(JTree paramJTree, BasicTreeUI paramBasicTreeUI)
    {
      int i = paramBasicTreeUI.getLeadSelectionRow();
      if ((i != -1) && (!paramBasicTreeUI.isLeaf(i)))
      {
        TreePath localTreePath1 = paramBasicTreeUI.getAnchorSelectionPath();
        TreePath localTreePath2 = paramBasicTreeUI.getLeadSelectionPath();
        paramBasicTreeUI.toggleExpandState(paramBasicTreeUI.getPathForRow(paramJTree, i));
        paramBasicTreeUI.setAnchorSelectionPath(localTreePath1);
        paramBasicTreeUI.setLeadSelectionPath(localTreePath2);
      }
    }
    
    private void expand(JTree paramJTree, BasicTreeUI paramBasicTreeUI)
    {
      int i = paramBasicTreeUI.getLeadSelectionRow();
      paramJTree.expandRow(i);
    }
    
    private void collapse(JTree paramJTree, BasicTreeUI paramBasicTreeUI)
    {
      int i = paramBasicTreeUI.getLeadSelectionRow();
      paramJTree.collapseRow(i);
    }
    
    private void increment(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      if ((!paramBoolean1) && (!paramBoolean2) && (paramJTree.getSelectionModel().getSelectionMode() != 4)) {
        paramBoolean2 = true;
      }
      int i;
      if ((treeSelectionModel != null) && ((i = paramJTree.getRowCount()) > 0))
      {
        int j = paramBasicTreeUI.getLeadSelectionRow();
        int k;
        if (j == -1)
        {
          if (paramInt == 1) {
            k = 0;
          } else {
            k = i - 1;
          }
        }
        else {
          k = Math.min(i - 1, Math.max(0, j + paramInt));
        }
        if ((paramBoolean1) && (treeSelectionModel.getSelectionMode() != 1)) {
          paramBasicTreeUI.extendSelection(paramJTree.getPathForRow(k));
        } else if (paramBoolean2) {
          paramJTree.setSelectionInterval(k, k);
        } else {
          paramBasicTreeUI.setLeadSelectionPath(paramJTree.getPathForRow(k), true);
        }
        paramBasicTreeUI.ensureRowsAreVisible(k, k);
        lastSelectedRow = k;
      }
    }
    
    private void traverse(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean)
    {
      if ((!paramBoolean) && (paramJTree.getSelectionModel().getSelectionMode() != 4)) {
        paramBoolean = true;
      }
      int i;
      if ((i = paramJTree.getRowCount()) > 0)
      {
        int j = paramBasicTreeUI.getLeadSelectionRow();
        int k;
        if (j == -1)
        {
          k = 0;
        }
        else
        {
          TreePath localTreePath;
          if (paramInt == 1)
          {
            localTreePath = paramBasicTreeUI.getPathForRow(paramJTree, j);
            int m = paramJTree.getModel().getChildCount(localTreePath.getLastPathComponent());
            k = -1;
            if (!paramBasicTreeUI.isLeaf(j)) {
              if (!paramJTree.isExpanded(j)) {
                paramBasicTreeUI.toggleExpandState(localTreePath);
              } else if (m > 0) {
                k = Math.min(j + 1, i - 1);
              }
            }
          }
          else if ((!paramBasicTreeUI.isLeaf(j)) && (paramJTree.isExpanded(j)))
          {
            paramBasicTreeUI.toggleExpandState(paramBasicTreeUI.getPathForRow(paramJTree, j));
            k = -1;
          }
          else
          {
            localTreePath = paramBasicTreeUI.getPathForRow(paramJTree, j);
            if ((localTreePath != null) && (localTreePath.getPathCount() > 1)) {
              k = paramBasicTreeUI.getRowForPath(paramJTree, localTreePath.getParentPath());
            } else {
              k = -1;
            }
          }
        }
        if (k != -1)
        {
          if (paramBoolean) {
            paramJTree.setSelectionInterval(k, k);
          } else {
            paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, k), true);
          }
          paramBasicTreeUI.ensureRowsAreVisible(k, k);
        }
      }
    }
    
    private void moveSelectionToParent(JTree paramJTree, BasicTreeUI paramBasicTreeUI)
    {
      int i = paramBasicTreeUI.getLeadSelectionRow();
      TreePath localTreePath = paramBasicTreeUI.getPathForRow(paramJTree, i);
      if ((localTreePath != null) && (localTreePath.getPathCount() > 1))
      {
        int j = paramBasicTreeUI.getRowForPath(paramJTree, localTreePath.getParentPath());
        if (j != -1)
        {
          paramJTree.setSelectionInterval(j, j);
          paramBasicTreeUI.ensureRowsAreVisible(j, j);
        }
      }
    }
    
    private void page(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      if ((!paramBoolean1) && (!paramBoolean2) && (paramJTree.getSelectionModel().getSelectionMode() != 4)) {
        paramBoolean2 = true;
      }
      int i;
      if (((i = paramBasicTreeUI.getRowCount(paramJTree)) > 0) && (treeSelectionModel != null))
      {
        Dimension localDimension = paramJTree.getSize();
        TreePath localTreePath1 = paramBasicTreeUI.getLeadSelectionPath();
        Rectangle localRectangle1 = paramJTree.getVisibleRect();
        TreePath localTreePath2;
        if (paramInt == -1)
        {
          localTreePath2 = paramBasicTreeUI.getClosestPathForLocation(paramJTree, x, y);
          if (localTreePath2.equals(localTreePath1))
          {
            y = Math.max(0, y - height);
            localTreePath2 = paramJTree.getClosestPathForLocation(x, y);
          }
        }
        else
        {
          y = Math.min(height, y + height - 1);
          localTreePath2 = paramJTree.getClosestPathForLocation(x, y);
          if (localTreePath2.equals(localTreePath1))
          {
            y = Math.min(height, y + height - 1);
            localTreePath2 = paramJTree.getClosestPathForLocation(x, y);
          }
        }
        Rectangle localRectangle2 = paramBasicTreeUI.getPathBounds(paramJTree, localTreePath2);
        if (localRectangle2 != null)
        {
          x = x;
          width = width;
          if (paramInt == -1)
          {
            height = height;
          }
          else
          {
            y -= height - height;
            height = height;
          }
          if (paramBoolean1) {
            paramBasicTreeUI.extendSelection(localTreePath2);
          } else if (paramBoolean2) {
            paramJTree.setSelectionPath(localTreePath2);
          } else {
            paramBasicTreeUI.setLeadSelectionPath(localTreePath2, true);
          }
          paramJTree.scrollRectToVisible(localRectangle2);
        }
      }
    }
    
    private void home(JTree paramJTree, final BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      if ((!paramBoolean1) && (!paramBoolean2) && (paramJTree.getSelectionModel().getSelectionMode() != 4)) {
        paramBoolean2 = true;
      }
      final int i = paramBasicTreeUI.getRowCount(paramJTree);
      if (i > 0)
      {
        TreePath localTreePath;
        int j;
        if (paramInt == -1)
        {
          paramBasicTreeUI.ensureRowsAreVisible(0, 0);
          if (paramBoolean1)
          {
            localTreePath = paramBasicTreeUI.getAnchorSelectionPath();
            j = localTreePath == null ? -1 : paramBasicTreeUI.getRowForPath(paramJTree, localTreePath);
            if (j == -1)
            {
              paramJTree.setSelectionInterval(0, 0);
            }
            else
            {
              paramJTree.setSelectionInterval(0, j);
              paramBasicTreeUI.setAnchorSelectionPath(localTreePath);
              paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, 0));
            }
          }
          else if (paramBoolean2)
          {
            paramJTree.setSelectionInterval(0, 0);
          }
          else
          {
            paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, 0), true);
          }
        }
        else
        {
          paramBasicTreeUI.ensureRowsAreVisible(i - 1, i - 1);
          if (paramBoolean1)
          {
            localTreePath = paramBasicTreeUI.getAnchorSelectionPath();
            j = localTreePath == null ? -1 : paramBasicTreeUI.getRowForPath(paramJTree, localTreePath);
            if (j == -1)
            {
              paramJTree.setSelectionInterval(i - 1, i - 1);
            }
            else
            {
              paramJTree.setSelectionInterval(j, i - 1);
              paramBasicTreeUI.setAnchorSelectionPath(localTreePath);
              paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, i - 1));
            }
          }
          else if (paramBoolean2)
          {
            paramJTree.setSelectionInterval(i - 1, i - 1);
          }
          else
          {
            paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, i - 1), true);
          }
          if (paramBasicTreeUI.isLargeModel()) {
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                paramBasicTreeUI.ensureRowsAreVisible(i - 1, i - 1);
              }
            });
          }
        }
      }
    }
  }
  
  public class CellEditorHandler
    implements CellEditorListener
  {
    public CellEditorHandler() {}
    
    public void editingStopped(ChangeEvent paramChangeEvent)
    {
      BasicTreeUI.this.getHandler().editingStopped(paramChangeEvent);
    }
    
    public void editingCanceled(ChangeEvent paramChangeEvent)
    {
      BasicTreeUI.this.getHandler().editingCanceled(paramChangeEvent);
    }
  }
  
  public class ComponentHandler
    extends ComponentAdapter
    implements ActionListener
  {
    protected Timer timer;
    protected JScrollBar scrollBar;
    
    public ComponentHandler() {}
    
    public void componentMoved(ComponentEvent paramComponentEvent)
    {
      if (timer == null)
      {
        JScrollPane localJScrollPane = getScrollPane();
        if (localJScrollPane == null)
        {
          updateSize();
        }
        else
        {
          scrollBar = localJScrollPane.getVerticalScrollBar();
          if ((scrollBar == null) || (!scrollBar.getValueIsAdjusting()))
          {
            if (((scrollBar = localJScrollPane.getHorizontalScrollBar()) != null) && (scrollBar.getValueIsAdjusting())) {
              startTimer();
            } else {
              updateSize();
            }
          }
          else {
            startTimer();
          }
        }
      }
    }
    
    protected void startTimer()
    {
      if (timer == null)
      {
        timer = new Timer(200, this);
        timer.setRepeats(true);
      }
      timer.start();
    }
    
    protected JScrollPane getScrollPane()
    {
      for (Container localContainer = tree.getParent(); (localContainer != null) && (!(localContainer instanceof JScrollPane)); localContainer = localContainer.getParent()) {}
      if ((localContainer instanceof JScrollPane)) {
        return (JScrollPane)localContainer;
      }
      return null;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if ((scrollBar == null) || (!scrollBar.getValueIsAdjusting()))
      {
        if (timer != null) {
          timer.stop();
        }
        updateSize();
        timer = null;
        scrollBar = null;
      }
    }
  }
  
  public class FocusHandler
    implements FocusListener
  {
    public FocusHandler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicTreeUI.this.getHandler().focusGained(paramFocusEvent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicTreeUI.this.getHandler().focusLost(paramFocusEvent);
    }
  }
  
  private class Handler
    implements CellEditorListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, PropertyChangeListener, TreeExpansionListener, TreeModelListener, TreeSelectionListener, DragRecognitionSupport.BeforeDrag
  {
    private String prefix = "";
    private String typedString = "";
    private long lastTime = 0L;
    private boolean dragPressDidSelection;
    private boolean dragStarted;
    private TreePath pressedPath;
    private MouseEvent pressedEvent;
    private boolean valueChangedOnPress;
    
    private Handler() {}
    
    public void keyTyped(KeyEvent paramKeyEvent)
    {
      if ((tree != null) && (tree.getRowCount() > 0) && (tree.hasFocus()) && (tree.isEnabled()))
      {
        if ((paramKeyEvent.isAltDown()) || (BasicGraphicsUtils.isMenuShortcutKeyDown(paramKeyEvent)) || (isNavigationKey(paramKeyEvent))) {
          return;
        }
        int i = 1;
        char c = paramKeyEvent.getKeyChar();
        long l = paramKeyEvent.getWhen();
        int j = tree.getLeadSelectionRow();
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
        if ((j < 0) || (j >= tree.getRowCount()))
        {
          i = 0;
          j = 0;
        }
        TreePath localTreePath = tree.getNextMatch(prefix, j, Position.Bias.Forward);
        int k;
        if (localTreePath != null)
        {
          tree.setSelectionPath(localTreePath);
          k = getRowForPath(tree, localTreePath);
          ensureRowsAreVisible(k, k);
        }
        else if (i != 0)
        {
          localTreePath = tree.getNextMatch(prefix, 0, Position.Bias.Forward);
          if (localTreePath != null)
          {
            tree.setSelectionPath(localTreePath);
            k = getRowForPath(tree, localTreePath);
            ensureRowsAreVisible(k, k);
          }
        }
      }
    }
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      if ((tree != null) && (isNavigationKey(paramKeyEvent)))
      {
        prefix = "";
        typedString = "";
        lastTime = 0L;
      }
    }
    
    public void keyReleased(KeyEvent paramKeyEvent) {}
    
    private boolean isNavigationKey(KeyEvent paramKeyEvent)
    {
      InputMap localInputMap = tree.getInputMap(1);
      KeyStroke localKeyStroke = KeyStroke.getKeyStrokeForEvent(paramKeyEvent);
      return (localInputMap != null) && (localInputMap.get(localKeyStroke) != null);
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (paramPropertyChangeEvent.getSource() == treeSelectionModel)
      {
        treeSelectionModel.resetRowSelection();
      }
      else if (paramPropertyChangeEvent.getSource() == tree)
      {
        String str = paramPropertyChangeEvent.getPropertyName();
        if (str == "leadSelectionPath")
        {
          if (!ignoreLAChange)
          {
            updateLeadSelectionRow();
            BasicTreeUI.this.repaintPath((TreePath)paramPropertyChangeEvent.getOldValue());
            BasicTreeUI.this.repaintPath((TreePath)paramPropertyChangeEvent.getNewValue());
          }
        }
        else if ((str == "anchorSelectionPath") && (!ignoreLAChange))
        {
          BasicTreeUI.this.repaintPath((TreePath)paramPropertyChangeEvent.getOldValue());
          BasicTreeUI.this.repaintPath((TreePath)paramPropertyChangeEvent.getNewValue());
        }
        if (str == "cellRenderer")
        {
          setCellRenderer((TreeCellRenderer)paramPropertyChangeEvent.getNewValue());
          BasicTreeUI.this.redoTheLayout();
        }
        else if (str == "model")
        {
          setModel((TreeModel)paramPropertyChangeEvent.getNewValue());
        }
        else if (str == "rootVisible")
        {
          setRootVisible(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue());
        }
        else if (str == "showsRootHandles")
        {
          setShowsRootHandles(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue());
        }
        else if (str == "rowHeight")
        {
          setRowHeight(((Integer)paramPropertyChangeEvent.getNewValue()).intValue());
        }
        else if (str == "cellEditor")
        {
          setCellEditor((TreeCellEditor)paramPropertyChangeEvent.getNewValue());
        }
        else if (str == "editable")
        {
          setEditable(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue());
        }
        else if (str == "largeModel")
        {
          setLargeModel(tree.isLargeModel());
        }
        else if (str == "selectionModel")
        {
          setSelectionModel(tree.getSelectionModel());
        }
        else if (str == "font")
        {
          completeEditing();
          if (treeState != null) {
            treeState.invalidateSizes();
          }
          updateSize();
        }
        else
        {
          Object localObject;
          if (str == "componentOrientation")
          {
            if (tree != null)
            {
              leftToRight = BasicGraphicsUtils.isLeftToRight(tree);
              BasicTreeUI.this.redoTheLayout();
              tree.treeDidChange();
              localObject = getInputMap(0);
              SwingUtilities.replaceUIInputMap(tree, 0, (InputMap)localObject);
            }
          }
          else if ("dropLocation" == str)
          {
            localObject = (JTree.DropLocation)paramPropertyChangeEvent.getOldValue();
            repaintDropLocation((JTree.DropLocation)localObject);
            repaintDropLocation(tree.getDropLocation());
          }
        }
      }
    }
    
    private void repaintDropLocation(JTree.DropLocation paramDropLocation)
    {
      if (paramDropLocation == null) {
        return;
      }
      Rectangle localRectangle;
      if (isDropLine(paramDropLocation)) {
        localRectangle = getDropLineRect(paramDropLocation);
      } else {
        localRectangle = tree.getPathBounds(paramDropLocation.getPath());
      }
      if (localRectangle != null) {
        tree.repaint(localRectangle);
      }
    }
    
    private boolean isActualPath(TreePath paramTreePath, int paramInt1, int paramInt2)
    {
      if (paramTreePath == null) {
        return false;
      }
      Rectangle localRectangle = getPathBounds(tree, paramTreePath);
      if ((localRectangle == null) || (paramInt2 > y + height)) {
        return false;
      }
      return (paramInt1 >= x) && (paramInt1 <= x + width);
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (SwingUtilities2.shouldIgnore(paramMouseEvent, tree)) {
        return;
      }
      if ((isEditing(tree)) && (tree.getInvokesStopCellEditing()) && (!stopEditing(tree))) {
        return;
      }
      completeEditing();
      pressedPath = getClosestPathForLocation(tree, paramMouseEvent.getX(), paramMouseEvent.getY());
      if (tree.getDragEnabled())
      {
        mousePressedDND(paramMouseEvent);
      }
      else
      {
        SwingUtilities2.adjustFocus(tree);
        handleSelection(paramMouseEvent);
      }
    }
    
    private void mousePressedDND(MouseEvent paramMouseEvent)
    {
      pressedEvent = paramMouseEvent;
      int i = 1;
      dragStarted = false;
      valueChangedOnPress = false;
      if ((isActualPath(pressedPath, paramMouseEvent.getX(), paramMouseEvent.getY())) && (DragRecognitionSupport.mousePressed(paramMouseEvent)))
      {
        dragPressDidSelection = false;
        if (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent)) {
          return;
        }
        if ((!paramMouseEvent.isShiftDown()) && (tree.isPathSelected(pressedPath)))
        {
          BasicTreeUI.this.setAnchorSelectionPath(pressedPath);
          BasicTreeUI.this.setLeadSelectionPath(pressedPath, true);
          return;
        }
        dragPressDidSelection = true;
        i = 0;
      }
      if (i != 0) {
        SwingUtilities2.adjustFocus(tree);
      }
      handleSelection(paramMouseEvent);
    }
    
    void handleSelection(MouseEvent paramMouseEvent)
    {
      if (pressedPath != null)
      {
        Rectangle localRectangle = getPathBounds(tree, pressedPath);
        if ((localRectangle == null) || (paramMouseEvent.getY() >= y + height)) {
          return;
        }
        if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
          checkForClickInExpandControl(pressedPath, paramMouseEvent.getX(), paramMouseEvent.getY());
        }
        int i = paramMouseEvent.getX();
        if ((i >= x) && (i < x + width) && ((tree.getDragEnabled()) || (!startEditing(pressedPath, paramMouseEvent)))) {
          selectPathForEvent(pressedPath, paramMouseEvent);
        }
      }
    }
    
    public void dragStarting(MouseEvent paramMouseEvent)
    {
      dragStarted = true;
      if (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent))
      {
        tree.addSelectionPath(pressedPath);
        BasicTreeUI.this.setAnchorSelectionPath(pressedPath);
        BasicTreeUI.this.setLeadSelectionPath(pressedPath, true);
      }
      pressedEvent = null;
      pressedPath = null;
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (SwingUtilities2.shouldIgnore(paramMouseEvent, tree)) {
        return;
      }
      if (tree.getDragEnabled()) {
        DragRecognitionSupport.mouseDragged(paramMouseEvent, this);
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (SwingUtilities2.shouldIgnore(paramMouseEvent, tree)) {
        return;
      }
      if (tree.getDragEnabled()) {
        mouseReleasedDND(paramMouseEvent);
      }
      pressedEvent = null;
      pressedPath = null;
    }
    
    private void mouseReleasedDND(MouseEvent paramMouseEvent)
    {
      MouseEvent localMouseEvent = DragRecognitionSupport.mouseReleased(paramMouseEvent);
      if (localMouseEvent != null)
      {
        SwingUtilities2.adjustFocus(tree);
        if (!dragPressDidSelection) {
          handleSelection(localMouseEvent);
        }
      }
      if ((!dragStarted) && (pressedPath != null) && (!valueChangedOnPress) && (isActualPath(pressedPath, pressedEvent.getX(), pressedEvent.getY()))) {
        BasicTreeUI.this.startEditingOnRelease(pressedPath, pressedEvent, paramMouseEvent);
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      if (tree != null)
      {
        Rectangle localRectangle = getPathBounds(tree, tree.getLeadSelectionPath());
        if (localRectangle != null) {
          tree.repaint(BasicTreeUI.this.getRepaintPathBounds(localRectangle));
        }
        localRectangle = getPathBounds(tree, BasicTreeUI.this.getLeadSelectionPath());
        if (localRectangle != null) {
          tree.repaint(BasicTreeUI.this.getRepaintPathBounds(localRectangle));
        }
      }
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      focusGained(paramFocusEvent);
    }
    
    public void editingStopped(ChangeEvent paramChangeEvent)
    {
      completeEditing(false, false, true);
    }
    
    public void editingCanceled(ChangeEvent paramChangeEvent)
    {
      completeEditing(false, false, false);
    }
    
    public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
    {
      valueChangedOnPress = true;
      completeEditing();
      if ((tree.getExpandsSelectedPaths()) && (treeSelectionModel != null))
      {
        localObject1 = treeSelectionModel.getSelectionPaths();
        if (localObject1 != null) {
          for (int i = localObject1.length - 1; i >= 0; i--)
          {
            localObject2 = localObject1[i].getParentPath();
            int j = 1;
            while (localObject2 != null) {
              if (treeModel.isLeaf(((TreePath)localObject2).getLastPathComponent()))
              {
                j = 0;
                localObject2 = null;
              }
              else
              {
                localObject2 = ((TreePath)localObject2).getParentPath();
              }
            }
            if (j != 0) {
              tree.makeVisible(localObject1[i]);
            }
          }
        }
      }
      Object localObject1 = BasicTreeUI.this.getLeadSelectionPath();
      lastSelectedRow = tree.getMinSelectionRow();
      TreePath localTreePath = tree.getSelectionModel().getLeadSelectionPath();
      BasicTreeUI.this.setAnchorSelectionPath(localTreePath);
      BasicTreeUI.this.setLeadSelectionPath(localTreePath);
      Object localObject2 = paramTreeSelectionEvent.getPaths();
      Rectangle localRectangle2 = tree.getVisibleRect();
      int k = 1;
      int m = tree.getWidth();
      Rectangle localRectangle1;
      if (localObject2 != null)
      {
        int i1 = localObject2.length;
        if (i1 > 4)
        {
          tree.repaint();
          k = 0;
        }
        else
        {
          for (int n = 0; n < i1; n++)
          {
            localRectangle1 = getPathBounds(tree, localObject2[n]);
            if ((localRectangle1 != null) && (localRectangle2.intersects(localRectangle1))) {
              tree.repaint(0, y, m, height);
            }
          }
        }
      }
      if (k != 0)
      {
        localRectangle1 = getPathBounds(tree, (TreePath)localObject1);
        if ((localRectangle1 != null) && (localRectangle2.intersects(localRectangle1))) {
          tree.repaint(0, y, m, height);
        }
        localRectangle1 = getPathBounds(tree, localTreePath);
        if ((localRectangle1 != null) && (localRectangle2.intersects(localRectangle1))) {
          tree.repaint(0, y, m, height);
        }
      }
    }
    
    public void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent)
    {
      if ((paramTreeExpansionEvent != null) && (tree != null))
      {
        TreePath localTreePath = paramTreeExpansionEvent.getPath();
        updateExpandedDescendants(localTreePath);
      }
    }
    
    public void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent)
    {
      if ((paramTreeExpansionEvent != null) && (tree != null))
      {
        TreePath localTreePath = paramTreeExpansionEvent.getPath();
        completeEditing();
        if ((localTreePath != null) && (tree.isVisible(localTreePath)))
        {
          treeState.setExpandedState(localTreePath, false);
          updateLeadSelectionRow();
          updateSize();
        }
      }
    }
    
    public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
    {
      if ((treeState != null) && (paramTreeModelEvent != null))
      {
        TreePath localTreePath1 = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
        int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
        if ((arrayOfInt == null) || (arrayOfInt.length == 0))
        {
          treeState.treeNodesChanged(paramTreeModelEvent);
          updateSize();
        }
        else if (treeState.isExpanded(localTreePath1))
        {
          int i = arrayOfInt[0];
          for (int j = arrayOfInt.length - 1; j > 0; j--) {
            i = Math.min(arrayOfInt[j], i);
          }
          Object localObject = treeModel.getChild(localTreePath1.getLastPathComponent(), i);
          TreePath localTreePath2 = localTreePath1.pathByAddingChild(localObject);
          Rectangle localRectangle1 = getPathBounds(tree, localTreePath2);
          treeState.treeNodesChanged(paramTreeModelEvent);
          BasicTreeUI.this.updateSize0();
          Rectangle localRectangle2 = getPathBounds(tree, localTreePath2);
          if ((localRectangle1 == null) || (localRectangle2 == null)) {
            return;
          }
          if ((arrayOfInt.length == 1) && (height == height)) {
            tree.repaint(0, y, tree.getWidth(), height);
          } else {
            tree.repaint(0, y, tree.getWidth(), tree.getHeight() - y);
          }
        }
        else
        {
          treeState.treeNodesChanged(paramTreeModelEvent);
        }
      }
    }
    
    public void treeNodesInserted(TreeModelEvent paramTreeModelEvent)
    {
      if ((treeState != null) && (paramTreeModelEvent != null))
      {
        treeState.treeNodesInserted(paramTreeModelEvent);
        updateLeadSelectionRow();
        TreePath localTreePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
        if (treeState.isExpanded(localTreePath))
        {
          updateSize();
        }
        else
        {
          int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
          int i = treeModel.getChildCount(localTreePath.getLastPathComponent());
          if ((arrayOfInt != null) && (i - arrayOfInt.length == 0)) {
            updateSize();
          }
        }
      }
    }
    
    public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent)
    {
      if ((treeState != null) && (paramTreeModelEvent != null))
      {
        treeState.treeNodesRemoved(paramTreeModelEvent);
        updateLeadSelectionRow();
        TreePath localTreePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
        if ((treeState.isExpanded(localTreePath)) || (treeModel.getChildCount(localTreePath.getLastPathComponent()) == 0)) {
          updateSize();
        }
      }
    }
    
    public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
    {
      if ((treeState != null) && (paramTreeModelEvent != null))
      {
        treeState.treeStructureChanged(paramTreeModelEvent);
        updateLeadSelectionRow();
        TreePath localTreePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
        if (localTreePath != null) {
          localTreePath = localTreePath.getParentPath();
        }
        if ((localTreePath == null) || (treeState.isExpanded(localTreePath))) {
          updateSize();
        }
      }
    }
  }
  
  public class KeyHandler
    extends KeyAdapter
  {
    protected Action repeatKeyAction;
    protected boolean isKeyDown;
    
    public KeyHandler() {}
    
    public void keyTyped(KeyEvent paramKeyEvent)
    {
      BasicTreeUI.this.getHandler().keyTyped(paramKeyEvent);
    }
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      BasicTreeUI.this.getHandler().keyPressed(paramKeyEvent);
    }
    
    public void keyReleased(KeyEvent paramKeyEvent)
    {
      BasicTreeUI.this.getHandler().keyReleased(paramKeyEvent);
    }
  }
  
  public class MouseHandler
    extends MouseAdapter
    implements MouseMotionListener
  {
    public MouseHandler() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      BasicTreeUI.this.getHandler().mousePressed(paramMouseEvent);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      BasicTreeUI.this.getHandler().mouseDragged(paramMouseEvent);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      BasicTreeUI.this.getHandler().mouseMoved(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      BasicTreeUI.this.getHandler().mouseReleased(paramMouseEvent);
    }
  }
  
  public class MouseInputHandler
    implements MouseInputListener
  {
    protected Component source;
    protected Component destination;
    private Component focusComponent;
    private boolean dispatchedEvent;
    
    public MouseInputHandler(Component paramComponent1, Component paramComponent2, MouseEvent paramMouseEvent)
    {
      this(paramComponent1, paramComponent2, paramMouseEvent, null);
    }
    
    MouseInputHandler(Component paramComponent1, Component paramComponent2, MouseEvent paramMouseEvent, Component paramComponent3)
    {
      source = paramComponent1;
      destination = paramComponent2;
      source.addMouseListener(this);
      source.addMouseMotionListener(this);
      SwingUtilities2.setSkipClickCount(paramComponent2, paramMouseEvent.getClickCount() - 1);
      paramComponent2.dispatchEvent(SwingUtilities.convertMouseEvent(paramComponent1, paramMouseEvent, paramComponent2));
      focusComponent = paramComponent3;
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      if (destination != null)
      {
        dispatchedEvent = true;
        destination.dispatchEvent(SwingUtilities.convertMouseEvent(source, paramMouseEvent, destination));
      }
    }
    
    public void mousePressed(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (destination != null) {
        destination.dispatchEvent(SwingUtilities.convertMouseEvent(source, paramMouseEvent, destination));
      }
      removeFromSource();
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
        removeFromSource();
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
        removeFromSource();
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (destination != null)
      {
        dispatchedEvent = true;
        destination.dispatchEvent(SwingUtilities.convertMouseEvent(source, paramMouseEvent, destination));
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      removeFromSource();
    }
    
    protected void removeFromSource()
    {
      if (source != null)
      {
        source.removeMouseListener(this);
        source.removeMouseMotionListener(this);
        if ((focusComponent != null) && (focusComponent == destination) && (!dispatchedEvent) && ((focusComponent instanceof JTextField))) {
          ((JTextField)focusComponent).selectAll();
        }
      }
      source = (destination = null);
    }
  }
  
  public class NodeDimensionsHandler
    extends AbstractLayoutCache.NodeDimensions
  {
    public NodeDimensionsHandler() {}
    
    public Rectangle getNodeDimensions(Object paramObject, int paramInt1, int paramInt2, boolean paramBoolean, Rectangle paramRectangle)
    {
      Object localObject;
      if ((editingComponent != null) && (editingRow == paramInt1))
      {
        localObject = editingComponent.getPreferredSize();
        int i = getRowHeight();
        if ((i > 0) && (i != height)) {
          height = i;
        }
        if (paramRectangle != null)
        {
          x = getRowX(paramInt1, paramInt2);
          width = width;
          height = height;
        }
        else
        {
          paramRectangle = new Rectangle(getRowX(paramInt1, paramInt2), 0, width, height);
        }
        return paramRectangle;
      }
      if (currentCellRenderer != null)
      {
        localObject = currentCellRenderer.getTreeCellRendererComponent(tree, paramObject, tree.isRowSelected(paramInt1), paramBoolean, treeModel.isLeaf(paramObject), paramInt1, false);
        if (tree != null)
        {
          rendererPane.add((Component)localObject);
          ((Component)localObject).validate();
        }
        Dimension localDimension = ((Component)localObject).getPreferredSize();
        if (paramRectangle != null)
        {
          x = getRowX(paramInt1, paramInt2);
          width = width;
          height = height;
        }
        else
        {
          paramRectangle = new Rectangle(getRowX(paramInt1, paramInt2), 0, width, height);
        }
        return paramRectangle;
      }
      return null;
    }
    
    protected int getRowX(int paramInt1, int paramInt2)
    {
      return BasicTreeUI.this.getRowX(paramInt1, paramInt2);
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicTreeUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  public class SelectionModelPropertyChangeHandler
    implements PropertyChangeListener
  {
    public SelectionModelPropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicTreeUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  public class TreeCancelEditingAction
    extends AbstractAction
  {
    public TreeCancelEditingAction(String paramString) {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (tree != null) {
        BasicTreeUI.SHARED_ACTION.cancelEditing(tree, BasicTreeUI.this);
      }
    }
    
    public boolean isEnabled()
    {
      return (tree != null) && (tree.isEnabled()) && (isEditing(tree));
    }
  }
  
  public class TreeExpansionHandler
    implements TreeExpansionListener
  {
    public TreeExpansionHandler() {}
    
    public void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent)
    {
      BasicTreeUI.this.getHandler().treeExpanded(paramTreeExpansionEvent);
    }
    
    public void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent)
    {
      BasicTreeUI.this.getHandler().treeCollapsed(paramTreeExpansionEvent);
    }
  }
  
  public class TreeHomeAction
    extends AbstractAction
  {
    protected int direction;
    private boolean addToSelection;
    private boolean changeSelection;
    
    public TreeHomeAction(int paramInt, String paramString)
    {
      this(paramInt, paramString, false, true);
    }
    
    private TreeHomeAction(int paramInt, String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      direction = paramInt;
      changeSelection = paramBoolean2;
      addToSelection = paramBoolean1;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (tree != null) {
        BasicTreeUI.SHARED_ACTION.home(tree, BasicTreeUI.this, direction, addToSelection, changeSelection);
      }
    }
    
    public boolean isEnabled()
    {
      return (tree != null) && (tree.isEnabled());
    }
  }
  
  public class TreeIncrementAction
    extends AbstractAction
  {
    protected int direction;
    private boolean addToSelection;
    private boolean changeSelection;
    
    public TreeIncrementAction(int paramInt, String paramString)
    {
      this(paramInt, paramString, false, true);
    }
    
    private TreeIncrementAction(int paramInt, String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      direction = paramInt;
      addToSelection = paramBoolean1;
      changeSelection = paramBoolean2;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (tree != null) {
        BasicTreeUI.SHARED_ACTION.increment(tree, BasicTreeUI.this, direction, addToSelection, changeSelection);
      }
    }
    
    public boolean isEnabled()
    {
      return (tree != null) && (tree.isEnabled());
    }
  }
  
  public class TreeModelHandler
    implements TreeModelListener
  {
    public TreeModelHandler() {}
    
    public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
    {
      BasicTreeUI.this.getHandler().treeNodesChanged(paramTreeModelEvent);
    }
    
    public void treeNodesInserted(TreeModelEvent paramTreeModelEvent)
    {
      BasicTreeUI.this.getHandler().treeNodesInserted(paramTreeModelEvent);
    }
    
    public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent)
    {
      BasicTreeUI.this.getHandler().treeNodesRemoved(paramTreeModelEvent);
    }
    
    public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
    {
      BasicTreeUI.this.getHandler().treeStructureChanged(paramTreeModelEvent);
    }
  }
  
  public class TreePageAction
    extends AbstractAction
  {
    protected int direction;
    private boolean addToSelection;
    private boolean changeSelection;
    
    public TreePageAction(int paramInt, String paramString)
    {
      this(paramInt, paramString, false, true);
    }
    
    private TreePageAction(int paramInt, String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      direction = paramInt;
      addToSelection = paramBoolean1;
      changeSelection = paramBoolean2;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (tree != null) {
        BasicTreeUI.SHARED_ACTION.page(tree, BasicTreeUI.this, direction, addToSelection, changeSelection);
      }
    }
    
    public boolean isEnabled()
    {
      return (tree != null) && (tree.isEnabled());
    }
  }
  
  public class TreeSelectionHandler
    implements TreeSelectionListener
  {
    public TreeSelectionHandler() {}
    
    public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
    {
      BasicTreeUI.this.getHandler().valueChanged(paramTreeSelectionEvent);
    }
  }
  
  public class TreeToggleAction
    extends AbstractAction
  {
    public TreeToggleAction(String paramString) {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (tree != null) {
        BasicTreeUI.SHARED_ACTION.toggle(tree, BasicTreeUI.this);
      }
    }
    
    public boolean isEnabled()
    {
      return (tree != null) && (tree.isEnabled());
    }
  }
  
  static class TreeTransferHandler
    extends TransferHandler
    implements UIResource, Comparator<TreePath>
  {
    private JTree tree;
    
    TreeTransferHandler() {}
    
    protected Transferable createTransferable(JComponent paramJComponent)
    {
      if ((paramJComponent instanceof JTree))
      {
        tree = ((JTree)paramJComponent);
        TreePath[] arrayOfTreePath1 = tree.getSelectionPaths();
        if ((arrayOfTreePath1 == null) || (arrayOfTreePath1.length == 0)) {
          return null;
        }
        StringBuffer localStringBuffer1 = new StringBuffer();
        StringBuffer localStringBuffer2 = new StringBuffer();
        localStringBuffer2.append("<html>\n<body>\n<ul>\n");
        TreeModel localTreeModel = tree.getModel();
        Object localObject1 = null;
        TreePath[] arrayOfTreePath2 = getDisplayOrderPaths(arrayOfTreePath1);
        for (TreePath localTreePath : arrayOfTreePath2)
        {
          Object localObject2 = localTreePath.getLastPathComponent();
          boolean bool = localTreeModel.isLeaf(localObject2);
          String str = getDisplayString(localTreePath, true, bool);
          localStringBuffer1.append(str + "\n");
          localStringBuffer2.append("  <li>" + str + "\n");
        }
        localStringBuffer1.deleteCharAt(localStringBuffer1.length() - 1);
        localStringBuffer2.append("</ul>\n</body>\n</html>");
        tree = null;
        return new BasicTransferable(localStringBuffer1.toString(), localStringBuffer2.toString());
      }
      return null;
    }
    
    public int compare(TreePath paramTreePath1, TreePath paramTreePath2)
    {
      int i = tree.getRowForPath(paramTreePath1);
      int j = tree.getRowForPath(paramTreePath2);
      return i - j;
    }
    
    String getDisplayString(TreePath paramTreePath, boolean paramBoolean1, boolean paramBoolean2)
    {
      int i = tree.getRowForPath(paramTreePath);
      boolean bool = tree.getLeadSelectionRow() == i;
      Object localObject = paramTreePath.getLastPathComponent();
      return tree.convertValueToText(localObject, paramBoolean1, tree.isExpanded(i), paramBoolean2, i, bool);
    }
    
    TreePath[] getDisplayOrderPaths(TreePath[] paramArrayOfTreePath)
    {
      ArrayList localArrayList = new ArrayList();
      for (TreePath localTreePath : paramArrayOfTreePath) {
        localArrayList.add(localTreePath);
      }
      Collections.sort(localArrayList, this);
      int i = localArrayList.size();
      TreePath[] arrayOfTreePath2 = new TreePath[i];
      for (??? = 0; ??? < i; ???++) {
        arrayOfTreePath2[???] = ((TreePath)localArrayList.get(???));
      }
      return arrayOfTreePath2;
    }
    
    public int getSourceActions(JComponent paramJComponent)
    {
      return 1;
    }
  }
  
  public class TreeTraverseAction
    extends AbstractAction
  {
    protected int direction;
    private boolean changeSelection;
    
    public TreeTraverseAction(int paramInt, String paramString)
    {
      this(paramInt, paramString, true);
    }
    
    private TreeTraverseAction(int paramInt, String paramString, boolean paramBoolean)
    {
      direction = paramInt;
      changeSelection = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (tree != null) {
        BasicTreeUI.SHARED_ACTION.traverse(tree, BasicTreeUI.this, direction, changeSelection);
      }
    }
    
    public boolean isEnabled()
    {
      return (tree != null) && (tree.isEnabled());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */