package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.ConstructorProperties;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import javax.swing.text.Position.Bias;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import sun.swing.SwingUtilities2;
import sun.swing.SwingUtilities2.Section;

public class JTree
  extends JComponent
  implements Scrollable, Accessible
{
  private static final String uiClassID = "TreeUI";
  protected transient TreeModel treeModel;
  protected transient TreeSelectionModel selectionModel;
  protected boolean rootVisible;
  protected transient TreeCellRenderer cellRenderer;
  protected int rowHeight;
  private boolean rowHeightSet = false;
  private transient Hashtable<TreePath, Boolean> expandedState = new Hashtable();
  protected boolean showsRootHandles;
  private boolean showsRootHandlesSet = false;
  protected transient TreeSelectionRedirector selectionRedirector;
  protected transient TreeCellEditor cellEditor;
  protected boolean editable;
  protected boolean largeModel;
  protected int visibleRowCount;
  protected boolean invokesStopCellEditing;
  protected boolean scrollsOnExpand;
  private boolean scrollsOnExpandSet = false;
  protected int toggleClickCount = 2;
  protected transient TreeModelListener treeModelListener;
  private transient Stack<Stack<TreePath>> expandedStack = new Stack();
  private TreePath leadPath;
  private TreePath anchorPath;
  private boolean expandsSelectedPaths;
  private boolean settingUI;
  private boolean dragEnabled;
  private DropMode dropMode = DropMode.USE_SELECTION;
  private transient DropLocation dropLocation;
  private int expandRow = -1;
  private TreeTimer dropTimer;
  private transient TreeExpansionListener uiTreeExpansionListener;
  private static int TEMP_STACK_SIZE = 11;
  public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
  public static final String TREE_MODEL_PROPERTY = "model";
  public static final String ROOT_VISIBLE_PROPERTY = "rootVisible";
  public static final String SHOWS_ROOT_HANDLES_PROPERTY = "showsRootHandles";
  public static final String ROW_HEIGHT_PROPERTY = "rowHeight";
  public static final String CELL_EDITOR_PROPERTY = "cellEditor";
  public static final String EDITABLE_PROPERTY = "editable";
  public static final String LARGE_MODEL_PROPERTY = "largeModel";
  public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
  public static final String VISIBLE_ROW_COUNT_PROPERTY = "visibleRowCount";
  public static final String INVOKES_STOP_CELL_EDITING_PROPERTY = "invokesStopCellEditing";
  public static final String SCROLLS_ON_EXPAND_PROPERTY = "scrollsOnExpand";
  public static final String TOGGLE_CLICK_COUNT_PROPERTY = "toggleClickCount";
  public static final String LEAD_SELECTION_PATH_PROPERTY = "leadSelectionPath";
  public static final String ANCHOR_SELECTION_PATH_PROPERTY = "anchorSelectionPath";
  public static final String EXPANDS_SELECTED_PATHS_PROPERTY = "expandsSelectedPaths";
  
  protected static TreeModel getDefaultTreeModel()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode1 = new DefaultMutableTreeNode("JTree");
    DefaultMutableTreeNode localDefaultMutableTreeNode2 = new DefaultMutableTreeNode("colors");
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode2);
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("blue"));
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("violet"));
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("red"));
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("yellow"));
    localDefaultMutableTreeNode2 = new DefaultMutableTreeNode("sports");
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode2);
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("basketball"));
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("soccer"));
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("football"));
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("hockey"));
    localDefaultMutableTreeNode2 = new DefaultMutableTreeNode("food");
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode2);
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("hot dogs"));
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("pizza"));
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("ravioli"));
    localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("bananas"));
    return new DefaultTreeModel(localDefaultMutableTreeNode1);
  }
  
  protected static TreeModel createTreeModel(Object paramObject)
  {
    Object localObject;
    if (((paramObject instanceof Object[])) || ((paramObject instanceof Hashtable)) || ((paramObject instanceof Vector)))
    {
      localObject = new DefaultMutableTreeNode("root");
      DynamicUtilTreeNode.createChildren((DefaultMutableTreeNode)localObject, paramObject);
    }
    else
    {
      localObject = new DynamicUtilTreeNode("root", paramObject);
    }
    return new DefaultTreeModel((TreeNode)localObject, false);
  }
  
  public JTree()
  {
    this(getDefaultTreeModel());
  }
  
  public JTree(Object[] paramArrayOfObject)
  {
    this(createTreeModel(paramArrayOfObject));
    setRootVisible(false);
    setShowsRootHandles(true);
    expandRoot();
  }
  
  public JTree(Vector<?> paramVector)
  {
    this(createTreeModel(paramVector));
    setRootVisible(false);
    setShowsRootHandles(true);
    expandRoot();
  }
  
  public JTree(Hashtable<?, ?> paramHashtable)
  {
    this(createTreeModel(paramHashtable));
    setRootVisible(false);
    setShowsRootHandles(true);
    expandRoot();
  }
  
  public JTree(TreeNode paramTreeNode)
  {
    this(paramTreeNode, false);
  }
  
  public JTree(TreeNode paramTreeNode, boolean paramBoolean)
  {
    this(new DefaultTreeModel(paramTreeNode, paramBoolean));
  }
  
  @ConstructorProperties({"model"})
  public JTree(TreeModel paramTreeModel)
  {
    setLayout(null);
    rowHeight = 16;
    visibleRowCount = 20;
    rootVisible = true;
    selectionModel = new DefaultTreeSelectionModel();
    cellRenderer = null;
    scrollsOnExpand = true;
    setOpaque(true);
    expandsSelectedPaths = true;
    updateUI();
    setModel(paramTreeModel);
  }
  
  public TreeUI getUI()
  {
    return (TreeUI)ui;
  }
  
  /* Error */
  public void setUI(TreeUI paramTreeUI)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 1007	javax/swing/JTree:ui	Ljavax/swing/plaf/ComponentUI;
    //   4: aload_1
    //   5: if_acmpeq +34 -> 39
    //   8: aload_0
    //   9: iconst_1
    //   10: putfield 994	javax/swing/JTree:settingUI	Z
    //   13: aload_0
    //   14: aconst_null
    //   15: putfield 1005	javax/swing/JTree:uiTreeExpansionListener	Ljavax/swing/event/TreeExpansionListener;
    //   18: aload_0
    //   19: aload_1
    //   20: invokespecial 1081	javax/swing/JComponent:setUI	(Ljavax/swing/plaf/ComponentUI;)V
    //   23: aload_0
    //   24: iconst_0
    //   25: putfield 994	javax/swing/JTree:settingUI	Z
    //   28: goto +11 -> 39
    //   31: astore_2
    //   32: aload_0
    //   33: iconst_0
    //   34: putfield 994	javax/swing/JTree:settingUI	Z
    //   37: aload_2
    //   38: athrow
    //   39: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	JTree
    //   0	40	1	paramTreeUI	TreeUI
    //   31	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   18	23	31	finally
  }
  
  public void updateUI()
  {
    setUI((TreeUI)UIManager.getUI(this));
    SwingUtilities.updateRendererOrEditorUI(getCellRenderer());
    SwingUtilities.updateRendererOrEditorUI(getCellEditor());
  }
  
  public String getUIClassID()
  {
    return "TreeUI";
  }
  
  public TreeCellRenderer getCellRenderer()
  {
    return cellRenderer;
  }
  
  public void setCellRenderer(TreeCellRenderer paramTreeCellRenderer)
  {
    TreeCellRenderer localTreeCellRenderer = cellRenderer;
    cellRenderer = paramTreeCellRenderer;
    firePropertyChange("cellRenderer", localTreeCellRenderer, cellRenderer);
    invalidate();
  }
  
  public void setEditable(boolean paramBoolean)
  {
    boolean bool = editable;
    editable = paramBoolean;
    firePropertyChange("editable", bool, paramBoolean);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleState", bool ? AccessibleState.EDITABLE : null, paramBoolean ? AccessibleState.EDITABLE : null);
    }
  }
  
  public boolean isEditable()
  {
    return editable;
  }
  
  public void setCellEditor(TreeCellEditor paramTreeCellEditor)
  {
    TreeCellEditor localTreeCellEditor = cellEditor;
    cellEditor = paramTreeCellEditor;
    firePropertyChange("cellEditor", localTreeCellEditor, paramTreeCellEditor);
    invalidate();
  }
  
  public TreeCellEditor getCellEditor()
  {
    return cellEditor;
  }
  
  public TreeModel getModel()
  {
    return treeModel;
  }
  
  public void setModel(TreeModel paramTreeModel)
  {
    clearSelection();
    TreeModel localTreeModel = treeModel;
    if ((treeModel != null) && (treeModelListener != null)) {
      treeModel.removeTreeModelListener(treeModelListener);
    }
    if (accessibleContext != null)
    {
      if (treeModel != null) {
        treeModel.removeTreeModelListener((TreeModelListener)accessibleContext);
      }
      if (paramTreeModel != null) {
        paramTreeModel.addTreeModelListener((TreeModelListener)accessibleContext);
      }
    }
    treeModel = paramTreeModel;
    clearToggledPaths();
    if (treeModel != null)
    {
      if (treeModelListener == null) {
        treeModelListener = createTreeModelListener();
      }
      if (treeModelListener != null) {
        treeModel.addTreeModelListener(treeModelListener);
      }
      Object localObject = treeModel.getRoot();
      if ((localObject != null) && (!treeModel.isLeaf(localObject))) {
        expandedState.put(new TreePath(localObject), Boolean.TRUE);
      }
    }
    firePropertyChange("model", localTreeModel, treeModel);
    invalidate();
  }
  
  public boolean isRootVisible()
  {
    return rootVisible;
  }
  
  public void setRootVisible(boolean paramBoolean)
  {
    boolean bool = rootVisible;
    rootVisible = paramBoolean;
    firePropertyChange("rootVisible", bool, rootVisible);
    if (accessibleContext != null) {
      ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
    }
  }
  
  public void setShowsRootHandles(boolean paramBoolean)
  {
    boolean bool = showsRootHandles;
    TreeModel localTreeModel = getModel();
    showsRootHandles = paramBoolean;
    showsRootHandlesSet = true;
    firePropertyChange("showsRootHandles", bool, showsRootHandles);
    if (accessibleContext != null) {
      ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
    }
    invalidate();
  }
  
  public boolean getShowsRootHandles()
  {
    return showsRootHandles;
  }
  
  public void setRowHeight(int paramInt)
  {
    int i = rowHeight;
    rowHeight = paramInt;
    rowHeightSet = true;
    firePropertyChange("rowHeight", i, rowHeight);
    invalidate();
  }
  
  public int getRowHeight()
  {
    return rowHeight;
  }
  
  public boolean isFixedRowHeight()
  {
    return rowHeight > 0;
  }
  
  public void setLargeModel(boolean paramBoolean)
  {
    boolean bool = largeModel;
    largeModel = paramBoolean;
    firePropertyChange("largeModel", bool, paramBoolean);
  }
  
  public boolean isLargeModel()
  {
    return largeModel;
  }
  
  public void setInvokesStopCellEditing(boolean paramBoolean)
  {
    boolean bool = invokesStopCellEditing;
    invokesStopCellEditing = paramBoolean;
    firePropertyChange("invokesStopCellEditing", bool, paramBoolean);
  }
  
  public boolean getInvokesStopCellEditing()
  {
    return invokesStopCellEditing;
  }
  
  public void setScrollsOnExpand(boolean paramBoolean)
  {
    boolean bool = scrollsOnExpand;
    scrollsOnExpand = paramBoolean;
    scrollsOnExpandSet = true;
    firePropertyChange("scrollsOnExpand", bool, paramBoolean);
  }
  
  public boolean getScrollsOnExpand()
  {
    return scrollsOnExpand;
  }
  
  public void setToggleClickCount(int paramInt)
  {
    int i = toggleClickCount;
    toggleClickCount = paramInt;
    firePropertyChange("toggleClickCount", i, paramInt);
  }
  
  public int getToggleClickCount()
  {
    return toggleClickCount;
  }
  
  public void setExpandsSelectedPaths(boolean paramBoolean)
  {
    boolean bool = expandsSelectedPaths;
    expandsSelectedPaths = paramBoolean;
    firePropertyChange("expandsSelectedPaths", bool, paramBoolean);
  }
  
  public boolean getExpandsSelectedPaths()
  {
    return expandsSelectedPaths;
  }
  
  public void setDragEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (GraphicsEnvironment.isHeadless())) {
      throw new HeadlessException();
    }
    dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled()
  {
    return dragEnabled;
  }
  
  public final void setDropMode(DropMode paramDropMode)
  {
    if (paramDropMode != null) {
      switch (paramDropMode)
      {
      case USE_SELECTION: 
      case ON: 
      case INSERT: 
      case ON_OR_INSERT: 
        dropMode = paramDropMode;
        return;
      }
    }
    throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for tree");
  }
  
  public final DropMode getDropMode()
  {
    return dropMode;
  }
  
  DropLocation dropLocationForPoint(Point paramPoint)
  {
    DropLocation localDropLocation = null;
    int i = getClosestRowForLocation(x, y);
    Rectangle localRectangle = getRowBounds(i);
    TreeModel localTreeModel = getModel();
    Object localObject = localTreeModel == null ? null : localTreeModel.getRoot();
    TreePath localTreePath1 = localObject == null ? null : new TreePath(localObject);
    int j = (i == -1) || (y < y) || (y >= y + height) ? 1 : 0;
    switch (dropMode)
    {
    case USE_SELECTION: 
    case ON: 
      if (j != 0) {
        localDropLocation = new DropLocation(paramPoint, null, -1, null);
      } else {
        localDropLocation = new DropLocation(paramPoint, getPathForRow(i), -1, null);
      }
      break;
    case INSERT: 
    case ON_OR_INSERT: 
      if (i == -1)
      {
        if ((localObject != null) && (!localTreeModel.isLeaf(localObject)) && (isExpanded(localTreePath1))) {
          localDropLocation = new DropLocation(paramPoint, localTreePath1, 0, null);
        } else {
          localDropLocation = new DropLocation(paramPoint, null, -1, null);
        }
      }
      else
      {
        boolean bool = (dropMode == DropMode.ON_OR_INSERT) || (!localTreeModel.isLeaf(getPathForRow(i).getLastPathComponent()));
        SwingUtilities2.Section localSection = SwingUtilities2.liesInVertical(localRectangle, paramPoint, bool);
        TreePath localTreePath2;
        TreePath localTreePath3;
        if (localSection == SwingUtilities2.Section.LEADING)
        {
          localTreePath2 = getPathForRow(i);
          localTreePath3 = localTreePath2.getParentPath();
        }
        else if (localSection == SwingUtilities2.Section.TRAILING)
        {
          int k = i + 1;
          if (k >= getRowCount())
          {
            if ((localTreeModel.isLeaf(localObject)) || (!isExpanded(localTreePath1)))
            {
              localDropLocation = new DropLocation(paramPoint, null, -1, null);
              break;
            }
            localTreePath3 = localTreePath1;
            k = localTreeModel.getChildCount(localObject);
            localDropLocation = new DropLocation(paramPoint, localTreePath3, k, null);
            break;
          }
          localTreePath2 = getPathForRow(k);
          localTreePath3 = localTreePath2.getParentPath();
        }
        else
        {
          assert (bool);
          localDropLocation = new DropLocation(paramPoint, getPathForRow(i), -1, null);
          break;
        }
        if (localTreePath3 != null) {
          localDropLocation = new DropLocation(paramPoint, localTreePath3, localTreeModel.getIndexOfChild(localTreePath3.getLastPathComponent(), localTreePath2.getLastPathComponent()), null);
        } else if ((bool) || (!localTreeModel.isLeaf(localObject))) {
          localDropLocation = new DropLocation(paramPoint, localTreePath1, -1, null);
        } else {
          localDropLocation = new DropLocation(paramPoint, null, -1, null);
        }
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError("Unexpected drop mode");
      }
      break;
    }
    if ((j != 0) || (i != expandRow)) {
      cancelDropTimer();
    }
    if ((j == 0) && (i != expandRow) && (isCollapsed(i)))
    {
      expandRow = i;
      startDropTimer();
    }
    return localDropLocation;
  }
  
  Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean)
  {
    Object localObject1 = null;
    DropLocation localDropLocation = (DropLocation)paramDropLocation;
    if (dropMode == DropMode.USE_SELECTION) {
      if (localDropLocation == null)
      {
        if ((!paramBoolean) && (paramObject != null))
        {
          setSelectionPaths(((TreePath[][])(TreePath[][])paramObject)[0]);
          setAnchorSelectionPath(((TreePath[][])(TreePath[][])paramObject)[1][0]);
          setLeadSelectionPath(((TreePath[][])(TreePath[][])paramObject)[1][1]);
        }
      }
      else
      {
        if (dropLocation == null)
        {
          localObject2 = getSelectionPaths();
          if (localObject2 == null) {
            localObject2 = new TreePath[0];
          }
          localObject1 = new TreePath[][] { localObject2, { getAnchorSelectionPath(), getLeadSelectionPath() } };
        }
        else
        {
          localObject1 = paramObject;
        }
        setSelectionPath(localDropLocation.getPath());
      }
    }
    Object localObject2 = dropLocation;
    dropLocation = localDropLocation;
    firePropertyChange("dropLocation", localObject2, dropLocation);
    return localObject1;
  }
  
  void dndDone()
  {
    cancelDropTimer();
    dropTimer = null;
  }
  
  public final DropLocation getDropLocation()
  {
    return dropLocation;
  }
  
  private void startDropTimer()
  {
    if (dropTimer == null) {
      dropTimer = new TreeTimer();
    }
    dropTimer.start();
  }
  
  private void cancelDropTimer()
  {
    if ((dropTimer != null) && (dropTimer.isRunning()))
    {
      expandRow = -1;
      dropTimer.stop();
    }
  }
  
  public boolean isPathEditable(TreePath paramTreePath)
  {
    return isEditable();
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent)
  {
    String str = null;
    if (paramMouseEvent != null)
    {
      Point localPoint = paramMouseEvent.getPoint();
      int i = getRowForLocation(x, y);
      TreeCellRenderer localTreeCellRenderer = getCellRenderer();
      if ((i != -1) && (localTreeCellRenderer != null))
      {
        TreePath localTreePath = getPathForRow(i);
        Object localObject = localTreePath.getLastPathComponent();
        Component localComponent = localTreeCellRenderer.getTreeCellRendererComponent(this, localObject, isRowSelected(i), isExpanded(i), getModel().isLeaf(localObject), i, true);
        if ((localComponent instanceof JComponent))
        {
          Rectangle localRectangle = getPathBounds(localTreePath);
          localPoint.translate(-x, -y);
          MouseEvent localMouseEvent = new MouseEvent(localComponent, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), x, y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
          str = ((JComponent)localComponent).getToolTipText(localMouseEvent);
        }
      }
    }
    if (str == null) {
      str = getToolTipText();
    }
    return str;
  }
  
  public String convertValueToText(Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4)
  {
    if (paramObject != null)
    {
      String str = paramObject.toString();
      if (str != null) {
        return str;
      }
    }
    return "";
  }
  
  public int getRowCount()
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      return localTreeUI.getRowCount(this);
    }
    return 0;
  }
  
  public void setSelectionPath(TreePath paramTreePath)
  {
    getSelectionModel().setSelectionPath(paramTreePath);
  }
  
  public void setSelectionPaths(TreePath[] paramArrayOfTreePath)
  {
    getSelectionModel().setSelectionPaths(paramArrayOfTreePath);
  }
  
  public void setLeadSelectionPath(TreePath paramTreePath)
  {
    TreePath localTreePath = leadPath;
    leadPath = paramTreePath;
    firePropertyChange("leadSelectionPath", localTreePath, paramTreePath);
    if (accessibleContext != null) {
      ((AccessibleJTree)accessibleContext).fireActiveDescendantPropertyChange(localTreePath, paramTreePath);
    }
  }
  
  public void setAnchorSelectionPath(TreePath paramTreePath)
  {
    TreePath localTreePath = anchorPath;
    anchorPath = paramTreePath;
    firePropertyChange("anchorSelectionPath", localTreePath, paramTreePath);
  }
  
  public void setSelectionRow(int paramInt)
  {
    int[] arrayOfInt = { paramInt };
    setSelectionRows(arrayOfInt);
  }
  
  public void setSelectionRows(int[] paramArrayOfInt)
  {
    TreeUI localTreeUI = getUI();
    if ((localTreeUI != null) && (paramArrayOfInt != null))
    {
      int i = paramArrayOfInt.length;
      TreePath[] arrayOfTreePath = new TreePath[i];
      for (int j = 0; j < i; j++) {
        arrayOfTreePath[j] = localTreeUI.getPathForRow(this, paramArrayOfInt[j]);
      }
      setSelectionPaths(arrayOfTreePath);
    }
  }
  
  public void addSelectionPath(TreePath paramTreePath)
  {
    getSelectionModel().addSelectionPath(paramTreePath);
  }
  
  public void addSelectionPaths(TreePath[] paramArrayOfTreePath)
  {
    getSelectionModel().addSelectionPaths(paramArrayOfTreePath);
  }
  
  public void addSelectionRow(int paramInt)
  {
    int[] arrayOfInt = { paramInt };
    addSelectionRows(arrayOfInt);
  }
  
  public void addSelectionRows(int[] paramArrayOfInt)
  {
    TreeUI localTreeUI = getUI();
    if ((localTreeUI != null) && (paramArrayOfInt != null))
    {
      int i = paramArrayOfInt.length;
      TreePath[] arrayOfTreePath = new TreePath[i];
      for (int j = 0; j < i; j++) {
        arrayOfTreePath[j] = localTreeUI.getPathForRow(this, paramArrayOfInt[j]);
      }
      addSelectionPaths(arrayOfTreePath);
    }
  }
  
  public Object getLastSelectedPathComponent()
  {
    TreePath localTreePath = getSelectionModel().getSelectionPath();
    if (localTreePath != null) {
      return localTreePath.getLastPathComponent();
    }
    return null;
  }
  
  public TreePath getLeadSelectionPath()
  {
    return leadPath;
  }
  
  public TreePath getAnchorSelectionPath()
  {
    return anchorPath;
  }
  
  public TreePath getSelectionPath()
  {
    return getSelectionModel().getSelectionPath();
  }
  
  public TreePath[] getSelectionPaths()
  {
    TreePath[] arrayOfTreePath = getSelectionModel().getSelectionPaths();
    return (arrayOfTreePath != null) && (arrayOfTreePath.length > 0) ? arrayOfTreePath : null;
  }
  
  public int[] getSelectionRows()
  {
    return getSelectionModel().getSelectionRows();
  }
  
  public int getSelectionCount()
  {
    return selectionModel.getSelectionCount();
  }
  
  public int getMinSelectionRow()
  {
    return getSelectionModel().getMinSelectionRow();
  }
  
  public int getMaxSelectionRow()
  {
    return getSelectionModel().getMaxSelectionRow();
  }
  
  public int getLeadSelectionRow()
  {
    TreePath localTreePath = getLeadSelectionPath();
    if (localTreePath != null) {
      return getRowForPath(localTreePath);
    }
    return -1;
  }
  
  public boolean isPathSelected(TreePath paramTreePath)
  {
    return getSelectionModel().isPathSelected(paramTreePath);
  }
  
  public boolean isRowSelected(int paramInt)
  {
    return getSelectionModel().isRowSelected(paramInt);
  }
  
  public Enumeration<TreePath> getExpandedDescendants(TreePath paramTreePath)
  {
    if (!isExpanded(paramTreePath)) {
      return null;
    }
    Enumeration localEnumeration = expandedState.keys();
    Vector localVector = null;
    if (localEnumeration != null) {
      while (localEnumeration.hasMoreElements())
      {
        TreePath localTreePath = (TreePath)localEnumeration.nextElement();
        Object localObject = expandedState.get(localTreePath);
        if ((localTreePath != paramTreePath) && (localObject != null) && (((Boolean)localObject).booleanValue()) && (paramTreePath.isDescendant(localTreePath)) && (isVisible(localTreePath)))
        {
          if (localVector == null) {
            localVector = new Vector();
          }
          localVector.addElement(localTreePath);
        }
      }
    }
    if (localVector == null)
    {
      Set localSet = Collections.emptySet();
      return Collections.enumeration(localSet);
    }
    return localVector.elements();
  }
  
  public boolean hasBeenExpanded(TreePath paramTreePath)
  {
    return (paramTreePath != null) && (expandedState.get(paramTreePath) != null);
  }
  
  public boolean isExpanded(TreePath paramTreePath)
  {
    if (paramTreePath == null) {
      return false;
    }
    do
    {
      Object localObject = expandedState.get(paramTreePath);
      if ((localObject == null) || (!((Boolean)localObject).booleanValue())) {
        return false;
      }
    } while ((paramTreePath = paramTreePath.getParentPath()) != null);
    return true;
  }
  
  public boolean isExpanded(int paramInt)
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null)
    {
      TreePath localTreePath = localTreeUI.getPathForRow(this, paramInt);
      if (localTreePath != null)
      {
        Boolean localBoolean = (Boolean)expandedState.get(localTreePath);
        return (localBoolean != null) && (localBoolean.booleanValue());
      }
    }
    return false;
  }
  
  public boolean isCollapsed(TreePath paramTreePath)
  {
    return !isExpanded(paramTreePath);
  }
  
  public boolean isCollapsed(int paramInt)
  {
    return !isExpanded(paramInt);
  }
  
  public void makeVisible(TreePath paramTreePath)
  {
    if (paramTreePath != null)
    {
      TreePath localTreePath = paramTreePath.getParentPath();
      if (localTreePath != null) {
        expandPath(localTreePath);
      }
    }
  }
  
  public boolean isVisible(TreePath paramTreePath)
  {
    if (paramTreePath != null)
    {
      TreePath localTreePath = paramTreePath.getParentPath();
      if (localTreePath != null) {
        return isExpanded(localTreePath);
      }
      return true;
    }
    return false;
  }
  
  public Rectangle getPathBounds(TreePath paramTreePath)
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      return localTreeUI.getPathBounds(this, paramTreePath);
    }
    return null;
  }
  
  public Rectangle getRowBounds(int paramInt)
  {
    return getPathBounds(getPathForRow(paramInt));
  }
  
  public void scrollPathToVisible(TreePath paramTreePath)
  {
    if (paramTreePath != null)
    {
      makeVisible(paramTreePath);
      Rectangle localRectangle = getPathBounds(paramTreePath);
      if (localRectangle != null)
      {
        scrollRectToVisible(localRectangle);
        if (accessibleContext != null) {
          ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
        }
      }
    }
  }
  
  public void scrollRowToVisible(int paramInt)
  {
    scrollPathToVisible(getPathForRow(paramInt));
  }
  
  public TreePath getPathForRow(int paramInt)
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      return localTreeUI.getPathForRow(this, paramInt);
    }
    return null;
  }
  
  public int getRowForPath(TreePath paramTreePath)
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      return localTreeUI.getRowForPath(this, paramTreePath);
    }
    return -1;
  }
  
  public void expandPath(TreePath paramTreePath)
  {
    TreeModel localTreeModel = getModel();
    if ((paramTreePath != null) && (localTreeModel != null) && (!localTreeModel.isLeaf(paramTreePath.getLastPathComponent()))) {
      setExpandedState(paramTreePath, true);
    }
  }
  
  public void expandRow(int paramInt)
  {
    expandPath(getPathForRow(paramInt));
  }
  
  public void collapsePath(TreePath paramTreePath)
  {
    setExpandedState(paramTreePath, false);
  }
  
  public void collapseRow(int paramInt)
  {
    collapsePath(getPathForRow(paramInt));
  }
  
  public TreePath getPathForLocation(int paramInt1, int paramInt2)
  {
    TreePath localTreePath = getClosestPathForLocation(paramInt1, paramInt2);
    if (localTreePath != null)
    {
      Rectangle localRectangle = getPathBounds(localTreePath);
      if ((localRectangle != null) && (paramInt1 >= x) && (paramInt1 < x + width) && (paramInt2 >= y) && (paramInt2 < y + height)) {
        return localTreePath;
      }
    }
    return null;
  }
  
  public int getRowForLocation(int paramInt1, int paramInt2)
  {
    return getRowForPath(getPathForLocation(paramInt1, paramInt2));
  }
  
  public TreePath getClosestPathForLocation(int paramInt1, int paramInt2)
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      return localTreeUI.getClosestPathForLocation(this, paramInt1, paramInt2);
    }
    return null;
  }
  
  public int getClosestRowForLocation(int paramInt1, int paramInt2)
  {
    return getRowForPath(getClosestPathForLocation(paramInt1, paramInt2));
  }
  
  public boolean isEditing()
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      return localTreeUI.isEditing(this);
    }
    return false;
  }
  
  public boolean stopEditing()
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      return localTreeUI.stopEditing(this);
    }
    return false;
  }
  
  public void cancelEditing()
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      localTreeUI.cancelEditing(this);
    }
  }
  
  public void startEditingAtPath(TreePath paramTreePath)
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      localTreeUI.startEditingAtPath(this, paramTreePath);
    }
  }
  
  public TreePath getEditingPath()
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null) {
      return localTreeUI.getEditingPath(this);
    }
    return null;
  }
  
  public void setSelectionModel(TreeSelectionModel paramTreeSelectionModel)
  {
    if (paramTreeSelectionModel == null) {
      paramTreeSelectionModel = EmptySelectionModel.sharedInstance();
    }
    TreeSelectionModel localTreeSelectionModel = selectionModel;
    if ((selectionModel != null) && (selectionRedirector != null)) {
      selectionModel.removeTreeSelectionListener(selectionRedirector);
    }
    if (accessibleContext != null)
    {
      selectionModel.removeTreeSelectionListener((TreeSelectionListener)accessibleContext);
      paramTreeSelectionModel.addTreeSelectionListener((TreeSelectionListener)accessibleContext);
    }
    selectionModel = paramTreeSelectionModel;
    if (selectionRedirector != null) {
      selectionModel.addTreeSelectionListener(selectionRedirector);
    }
    firePropertyChange("selectionModel", localTreeSelectionModel, selectionModel);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
    }
  }
  
  public TreeSelectionModel getSelectionModel()
  {
    return selectionModel;
  }
  
  protected TreePath[] getPathBetweenRows(int paramInt1, int paramInt2)
  {
    TreeUI localTreeUI = getUI();
    if (localTreeUI != null)
    {
      int i = getRowCount();
      if ((i > 0) && ((paramInt1 >= 0) || (paramInt2 >= 0)) && ((paramInt1 < i) || (paramInt2 < i)))
      {
        paramInt1 = Math.min(i - 1, Math.max(paramInt1, 0));
        paramInt2 = Math.min(i - 1, Math.max(paramInt2, 0));
        int j = Math.min(paramInt1, paramInt2);
        int k = Math.max(paramInt1, paramInt2);
        TreePath[] arrayOfTreePath = new TreePath[k - j + 1];
        for (int m = j; m <= k; m++) {
          arrayOfTreePath[(m - j)] = localTreeUI.getPathForRow(this, m);
        }
        return arrayOfTreePath;
      }
    }
    return new TreePath[0];
  }
  
  public void setSelectionInterval(int paramInt1, int paramInt2)
  {
    TreePath[] arrayOfTreePath = getPathBetweenRows(paramInt1, paramInt2);
    getSelectionModel().setSelectionPaths(arrayOfTreePath);
  }
  
  public void addSelectionInterval(int paramInt1, int paramInt2)
  {
    TreePath[] arrayOfTreePath = getPathBetweenRows(paramInt1, paramInt2);
    if ((arrayOfTreePath != null) && (arrayOfTreePath.length > 0)) {
      getSelectionModel().addSelectionPaths(arrayOfTreePath);
    }
  }
  
  public void removeSelectionInterval(int paramInt1, int paramInt2)
  {
    TreePath[] arrayOfTreePath = getPathBetweenRows(paramInt1, paramInt2);
    if ((arrayOfTreePath != null) && (arrayOfTreePath.length > 0)) {
      getSelectionModel().removeSelectionPaths(arrayOfTreePath);
    }
  }
  
  public void removeSelectionPath(TreePath paramTreePath)
  {
    getSelectionModel().removeSelectionPath(paramTreePath);
  }
  
  public void removeSelectionPaths(TreePath[] paramArrayOfTreePath)
  {
    getSelectionModel().removeSelectionPaths(paramArrayOfTreePath);
  }
  
  public void removeSelectionRow(int paramInt)
  {
    int[] arrayOfInt = { paramInt };
    removeSelectionRows(arrayOfInt);
  }
  
  public void removeSelectionRows(int[] paramArrayOfInt)
  {
    TreeUI localTreeUI = getUI();
    if ((localTreeUI != null) && (paramArrayOfInt != null))
    {
      int i = paramArrayOfInt.length;
      TreePath[] arrayOfTreePath = new TreePath[i];
      for (int j = 0; j < i; j++) {
        arrayOfTreePath[j] = localTreeUI.getPathForRow(this, paramArrayOfInt[j]);
      }
      removeSelectionPaths(arrayOfTreePath);
    }
  }
  
  public void clearSelection()
  {
    getSelectionModel().clearSelection();
  }
  
  public boolean isSelectionEmpty()
  {
    return getSelectionModel().isSelectionEmpty();
  }
  
  public void addTreeExpansionListener(TreeExpansionListener paramTreeExpansionListener)
  {
    if (settingUI) {
      uiTreeExpansionListener = paramTreeExpansionListener;
    }
    listenerList.add(TreeExpansionListener.class, paramTreeExpansionListener);
  }
  
  public void removeTreeExpansionListener(TreeExpansionListener paramTreeExpansionListener)
  {
    listenerList.remove(TreeExpansionListener.class, paramTreeExpansionListener);
    if (uiTreeExpansionListener == paramTreeExpansionListener) {
      uiTreeExpansionListener = null;
    }
  }
  
  public TreeExpansionListener[] getTreeExpansionListeners()
  {
    return (TreeExpansionListener[])listenerList.getListeners(TreeExpansionListener.class);
  }
  
  public void addTreeWillExpandListener(TreeWillExpandListener paramTreeWillExpandListener)
  {
    listenerList.add(TreeWillExpandListener.class, paramTreeWillExpandListener);
  }
  
  public void removeTreeWillExpandListener(TreeWillExpandListener paramTreeWillExpandListener)
  {
    listenerList.remove(TreeWillExpandListener.class, paramTreeWillExpandListener);
  }
  
  public TreeWillExpandListener[] getTreeWillExpandListeners()
  {
    return (TreeWillExpandListener[])listenerList.getListeners(TreeWillExpandListener.class);
  }
  
  public void fireTreeExpanded(TreePath paramTreePath)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    TreeExpansionEvent localTreeExpansionEvent = null;
    if (uiTreeExpansionListener != null)
    {
      localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
      uiTreeExpansionListener.treeExpanded(localTreeExpansionEvent);
    }
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if ((arrayOfObject[i] == TreeExpansionListener.class) && (arrayOfObject[(i + 1)] != uiTreeExpansionListener))
      {
        if (localTreeExpansionEvent == null) {
          localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
        }
        ((TreeExpansionListener)arrayOfObject[(i + 1)]).treeExpanded(localTreeExpansionEvent);
      }
    }
  }
  
  public void fireTreeCollapsed(TreePath paramTreePath)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    TreeExpansionEvent localTreeExpansionEvent = null;
    if (uiTreeExpansionListener != null)
    {
      localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
      uiTreeExpansionListener.treeCollapsed(localTreeExpansionEvent);
    }
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if ((arrayOfObject[i] == TreeExpansionListener.class) && (arrayOfObject[(i + 1)] != uiTreeExpansionListener))
      {
        if (localTreeExpansionEvent == null) {
          localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
        }
        ((TreeExpansionListener)arrayOfObject[(i + 1)]).treeCollapsed(localTreeExpansionEvent);
      }
    }
  }
  
  public void fireTreeWillExpand(TreePath paramTreePath)
    throws ExpandVetoException
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    TreeExpansionEvent localTreeExpansionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeWillExpandListener.class)
      {
        if (localTreeExpansionEvent == null) {
          localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
        }
        ((TreeWillExpandListener)arrayOfObject[(i + 1)]).treeWillExpand(localTreeExpansionEvent);
      }
    }
  }
  
  public void fireTreeWillCollapse(TreePath paramTreePath)
    throws ExpandVetoException
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    TreeExpansionEvent localTreeExpansionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeWillExpandListener.class)
      {
        if (localTreeExpansionEvent == null) {
          localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
        }
        ((TreeWillExpandListener)arrayOfObject[(i + 1)]).treeWillCollapse(localTreeExpansionEvent);
      }
    }
  }
  
  public void addTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener)
  {
    listenerList.add(TreeSelectionListener.class, paramTreeSelectionListener);
    if ((listenerList.getListenerCount(TreeSelectionListener.class) != 0) && (selectionRedirector == null))
    {
      selectionRedirector = new TreeSelectionRedirector();
      selectionModel.addTreeSelectionListener(selectionRedirector);
    }
  }
  
  public void removeTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener)
  {
    listenerList.remove(TreeSelectionListener.class, paramTreeSelectionListener);
    if ((listenerList.getListenerCount(TreeSelectionListener.class) == 0) && (selectionRedirector != null))
    {
      selectionModel.removeTreeSelectionListener(selectionRedirector);
      selectionRedirector = null;
    }
  }
  
  public TreeSelectionListener[] getTreeSelectionListeners()
  {
    return (TreeSelectionListener[])listenerList.getListeners(TreeSelectionListener.class);
  }
  
  protected void fireValueChanged(TreeSelectionEvent paramTreeSelectionEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeSelectionListener.class) {
        ((TreeSelectionListener)arrayOfObject[(i + 1)]).valueChanged(paramTreeSelectionEvent);
      }
    }
  }
  
  public void treeDidChange()
  {
    revalidate();
    repaint();
  }
  
  public void setVisibleRowCount(int paramInt)
  {
    int i = visibleRowCount;
    visibleRowCount = paramInt;
    firePropertyChange("visibleRowCount", i, visibleRowCount);
    invalidate();
    if (accessibleContext != null) {
      ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
    }
  }
  
  public int getVisibleRowCount()
  {
    return visibleRowCount;
  }
  
  private void expandRoot()
  {
    TreeModel localTreeModel = getModel();
    if ((localTreeModel != null) && (localTreeModel.getRoot() != null)) {
      expandPath(new TreePath(localTreeModel.getRoot()));
    }
  }
  
  public TreePath getNextMatch(String paramString, int paramInt, Position.Bias paramBias)
  {
    int i = getRowCount();
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    if ((paramInt < 0) || (paramInt >= i)) {
      throw new IllegalArgumentException();
    }
    paramString = paramString.toUpperCase();
    int j = paramBias == Position.Bias.Forward ? 1 : -1;
    int k = paramInt;
    do
    {
      TreePath localTreePath = getPathForRow(k);
      String str = convertValueToText(localTreePath.getLastPathComponent(), isRowSelected(k), isExpanded(k), true, k, false);
      if (str.toUpperCase().startsWith(paramString)) {
        return localTreePath;
      }
      k = (k + j + i) % i;
    } while (k != paramInt);
    return null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Vector localVector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if ((cellRenderer != null) && ((cellRenderer instanceof Serializable)))
    {
      localVector.addElement("cellRenderer");
      localVector.addElement(cellRenderer);
    }
    if ((cellEditor != null) && ((cellEditor instanceof Serializable)))
    {
      localVector.addElement("cellEditor");
      localVector.addElement(cellEditor);
    }
    if ((treeModel != null) && ((treeModel instanceof Serializable)))
    {
      localVector.addElement("treeModel");
      localVector.addElement(treeModel);
    }
    if ((selectionModel != null) && ((selectionModel instanceof Serializable)))
    {
      localVector.addElement("selectionModel");
      localVector.addElement(selectionModel);
    }
    Object localObject = getArchivableExpandedState();
    if (localObject != null)
    {
      localVector.addElement("expandedState");
      localVector.addElement(localObject);
    }
    paramObjectOutputStream.writeObject(localVector);
    if (getUIClassID().equals("TreeUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    expandedState = new Hashtable();
    expandedStack = new Stack();
    Vector localVector = (Vector)paramObjectInputStream.readObject();
    int i = 0;
    int j = localVector.size();
    if ((i < j) && (localVector.elementAt(i).equals("cellRenderer")))
    {
      cellRenderer = ((TreeCellRenderer)localVector.elementAt(++i));
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("cellEditor")))
    {
      cellEditor = ((TreeCellEditor)localVector.elementAt(++i));
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("treeModel")))
    {
      treeModel = ((TreeModel)localVector.elementAt(++i));
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("selectionModel")))
    {
      selectionModel = ((TreeSelectionModel)localVector.elementAt(++i));
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("expandedState")))
    {
      unarchiveExpandedState(localVector.elementAt(++i));
      i++;
    }
    if (listenerList.getListenerCount(TreeSelectionListener.class) != 0)
    {
      selectionRedirector = new TreeSelectionRedirector();
      selectionModel.addTreeSelectionListener(selectionRedirector);
    }
    if (treeModel != null)
    {
      treeModelListener = createTreeModelListener();
      if (treeModelListener != null) {
        treeModel.addTreeModelListener(treeModelListener);
      }
    }
  }
  
  private Object getArchivableExpandedState()
  {
    TreeModel localTreeModel = getModel();
    if (localTreeModel != null)
    {
      Enumeration localEnumeration = expandedState.keys();
      if (localEnumeration != null)
      {
        Vector localVector = new Vector();
        while (localEnumeration.hasMoreElements())
        {
          TreePath localTreePath = (TreePath)localEnumeration.nextElement();
          int[] arrayOfInt;
          try
          {
            arrayOfInt = getModelIndexsForPath(localTreePath);
          }
          catch (Error localError)
          {
            arrayOfInt = null;
          }
          if (arrayOfInt != null)
          {
            localVector.addElement(arrayOfInt);
            localVector.addElement(expandedState.get(localTreePath));
          }
        }
        return localVector;
      }
    }
    return null;
  }
  
  private void unarchiveExpandedState(Object paramObject)
  {
    if ((paramObject instanceof Vector))
    {
      Vector localVector = (Vector)paramObject;
      for (int i = localVector.size() - 1; i >= 0; i--)
      {
        Boolean localBoolean = (Boolean)localVector.elementAt(i--);
        try
        {
          TreePath localTreePath = getPathForIndexs((int[])localVector.elementAt(i));
          if (localTreePath != null) {
            expandedState.put(localTreePath, localBoolean);
          }
        }
        catch (Error localError) {}
      }
    }
  }
  
  private int[] getModelIndexsForPath(TreePath paramTreePath)
  {
    if (paramTreePath != null)
    {
      TreeModel localTreeModel = getModel();
      int i = paramTreePath.getPathCount();
      int[] arrayOfInt = new int[i - 1];
      Object localObject = localTreeModel.getRoot();
      for (int j = 1; j < i; j++)
      {
        arrayOfInt[(j - 1)] = localTreeModel.getIndexOfChild(localObject, paramTreePath.getPathComponent(j));
        localObject = paramTreePath.getPathComponent(j);
        if (arrayOfInt[(j - 1)] < 0) {
          return null;
        }
      }
      return arrayOfInt;
    }
    return null;
  }
  
  private TreePath getPathForIndexs(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    TreeModel localTreeModel = getModel();
    if (localTreeModel == null) {
      return null;
    }
    int i = paramArrayOfInt.length;
    Object localObject = localTreeModel.getRoot();
    if (localObject == null) {
      return null;
    }
    TreePath localTreePath = new TreePath(localObject);
    for (int j = 0; j < i; j++)
    {
      localObject = localTreeModel.getChild(localObject, paramArrayOfInt[j]);
      if (localObject == null) {
        return null;
      }
      localTreePath = localTreePath.pathByAddingChild(localObject);
    }
    return localTreePath;
  }
  
  public Dimension getPreferredScrollableViewportSize()
  {
    int i = getPreferredSizewidth;
    int j = getVisibleRowCount();
    int k = -1;
    if (isFixedRowHeight())
    {
      k = j * getRowHeight();
    }
    else
    {
      TreeUI localTreeUI = getUI();
      if ((localTreeUI != null) && (j > 0))
      {
        int m = localTreeUI.getRowCount(this);
        Rectangle localRectangle;
        if (m >= j)
        {
          localRectangle = getRowBounds(j - 1);
          if (localRectangle != null) {
            k = y + height;
          }
        }
        else if (m > 0)
        {
          localRectangle = getRowBounds(0);
          if (localRectangle != null) {
            k = height * j;
          }
        }
      }
      if (k == -1) {
        k = 16 * j;
      }
    }
    return new Dimension(i, k);
  }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    if (paramInt1 == 1)
    {
      int i = getClosestRowForLocation(0, y);
      if (i != -1)
      {
        Rectangle localRectangle = getRowBounds(i);
        if (y != y)
        {
          if (paramInt2 < 0) {
            return Math.max(0, y - y);
          }
          return y + height - y;
        }
        if (paramInt2 < 0)
        {
          if (i != 0)
          {
            localRectangle = getRowBounds(i - 1);
            return height;
          }
        }
        else {
          return height;
        }
      }
      return 0;
    }
    return 4;
  }
  
  public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    return paramInt1 == 1 ? height : width;
  }
  
  public boolean getScrollableTracksViewportWidth()
  {
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport)) {
      return localContainer.getWidth() > getPreferredSizewidth;
    }
    return false;
  }
  
  public boolean getScrollableTracksViewportHeight()
  {
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport)) {
      return localContainer.getHeight() > getPreferredSizeheight;
    }
    return false;
  }
  
  protected void setExpandedState(TreePath paramTreePath, boolean paramBoolean)
  {
    if (paramTreePath != null)
    {
      TreePath localTreePath = paramTreePath.getParentPath();
      Stack localStack;
      if (expandedStack.size() == 0) {
        localStack = new Stack();
      } else {
        localStack = (Stack)expandedStack.pop();
      }
      try
      {
        while (localTreePath != null) {
          if (isExpanded(localTreePath))
          {
            localTreePath = null;
          }
          else
          {
            localStack.push(localTreePath);
            localTreePath = localTreePath.getParentPath();
          }
        }
        for (int i = localStack.size() - 1; i >= 0; i--)
        {
          localTreePath = (TreePath)localStack.pop();
          if (!isExpanded(localTreePath))
          {
            try
            {
              fireTreeWillExpand(localTreePath);
            }
            catch (ExpandVetoException localExpandVetoException1)
            {
              return;
            }
            expandedState.put(localTreePath, Boolean.TRUE);
            fireTreeExpanded(localTreePath);
            if (accessibleContext != null) {
              ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
            }
          }
        }
      }
      finally
      {
        if (expandedStack.size() < TEMP_STACK_SIZE)
        {
          localStack.removeAllElements();
          expandedStack.push(localStack);
        }
      }
      Object localObject1;
      if (!paramBoolean)
      {
        localObject1 = expandedState.get(paramTreePath);
        if ((localObject1 != null) && (((Boolean)localObject1).booleanValue()))
        {
          try
          {
            fireTreeWillCollapse(paramTreePath);
          }
          catch (ExpandVetoException localExpandVetoException2)
          {
            return;
          }
          expandedState.put(paramTreePath, Boolean.FALSE);
          fireTreeCollapsed(paramTreePath);
          if ((removeDescendantSelectedPaths(paramTreePath, false)) && (!isPathSelected(paramTreePath))) {
            addSelectionPath(paramTreePath);
          }
          if (accessibleContext != null) {
            ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
          }
        }
      }
      else
      {
        localObject1 = expandedState.get(paramTreePath);
        if ((localObject1 == null) || (!((Boolean)localObject1).booleanValue()))
        {
          try
          {
            fireTreeWillExpand(paramTreePath);
          }
          catch (ExpandVetoException localExpandVetoException3)
          {
            return;
          }
          expandedState.put(paramTreePath, Boolean.TRUE);
          fireTreeExpanded(paramTreePath);
          if (accessibleContext != null) {
            ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
          }
        }
      }
    }
  }
  
  protected Enumeration<TreePath> getDescendantToggledPaths(TreePath paramTreePath)
  {
    if (paramTreePath == null) {
      return null;
    }
    Vector localVector = new Vector();
    Enumeration localEnumeration = expandedState.keys();
    while (localEnumeration.hasMoreElements())
    {
      TreePath localTreePath = (TreePath)localEnumeration.nextElement();
      if (paramTreePath.isDescendant(localTreePath)) {
        localVector.addElement(localTreePath);
      }
    }
    return localVector.elements();
  }
  
  protected void removeDescendantToggledPaths(Enumeration<TreePath> paramEnumeration)
  {
    if (paramEnumeration != null) {
      while (paramEnumeration.hasMoreElements())
      {
        Enumeration localEnumeration = getDescendantToggledPaths((TreePath)paramEnumeration.nextElement());
        if (localEnumeration != null) {
          while (localEnumeration.hasMoreElements()) {
            expandedState.remove(localEnumeration.nextElement());
          }
        }
      }
    }
  }
  
  protected void clearToggledPaths()
  {
    expandedState.clear();
  }
  
  protected TreeModelListener createTreeModelListener()
  {
    return new TreeModelHandler();
  }
  
  protected boolean removeDescendantSelectedPaths(TreePath paramTreePath, boolean paramBoolean)
  {
    TreePath[] arrayOfTreePath = getDescendantSelectedPaths(paramTreePath, paramBoolean);
    if (arrayOfTreePath != null)
    {
      getSelectionModel().removeSelectionPaths(arrayOfTreePath);
      return true;
    }
    return false;
  }
  
  private TreePath[] getDescendantSelectedPaths(TreePath paramTreePath, boolean paramBoolean)
  {
    TreeSelectionModel localTreeSelectionModel = getSelectionModel();
    TreePath[] arrayOfTreePath = localTreeSelectionModel != null ? localTreeSelectionModel.getSelectionPaths() : null;
    if (arrayOfTreePath != null)
    {
      int i = 0;
      for (int j = arrayOfTreePath.length - 1; j >= 0; j--) {
        if ((arrayOfTreePath[j] != null) && (paramTreePath.isDescendant(arrayOfTreePath[j])) && ((!paramTreePath.equals(arrayOfTreePath[j])) || (paramBoolean))) {
          i = 1;
        } else {
          arrayOfTreePath[j] = null;
        }
      }
      if (i == 0) {
        arrayOfTreePath = null;
      }
      return arrayOfTreePath;
    }
    return null;
  }
  
  void removeDescendantSelectedPaths(TreeModelEvent paramTreeModelEvent)
  {
    TreePath localTreePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
    Object[] arrayOfObject = paramTreeModelEvent.getChildren();
    TreeSelectionModel localTreeSelectionModel = getSelectionModel();
    if ((localTreeSelectionModel != null) && (localTreePath != null) && (arrayOfObject != null) && (arrayOfObject.length > 0)) {
      for (int i = arrayOfObject.length - 1; i >= 0; i--) {
        removeDescendantSelectedPaths(localTreePath.pathByAddingChild(arrayOfObject[i]), true);
      }
    }
  }
  
  void setUIProperty(String paramString, Object paramObject)
  {
    if (paramString == "rowHeight")
    {
      if (!rowHeightSet)
      {
        setRowHeight(((Number)paramObject).intValue());
        rowHeightSet = false;
      }
    }
    else if (paramString == "scrollsOnExpand")
    {
      if (!scrollsOnExpandSet)
      {
        setScrollsOnExpand(((Boolean)paramObject).booleanValue());
        scrollsOnExpandSet = false;
      }
    }
    else if (paramString == "showsRootHandles")
    {
      if (!showsRootHandlesSet)
      {
        setShowsRootHandles(((Boolean)paramObject).booleanValue());
        showsRootHandlesSet = false;
      }
    }
    else {
      super.setUIProperty(paramString, paramObject);
    }
  }
  
  protected String paramString()
  {
    String str1 = rootVisible ? "true" : "false";
    String str2 = showsRootHandles ? "true" : "false";
    String str3 = editable ? "true" : "false";
    String str4 = largeModel ? "true" : "false";
    String str5 = invokesStopCellEditing ? "true" : "false";
    String str6 = scrollsOnExpand ? "true" : "false";
    return super.paramString() + ",editable=" + str3 + ",invokesStopCellEditing=" + str5 + ",largeModel=" + str4 + ",rootVisible=" + str1 + ",rowHeight=" + rowHeight + ",scrollsOnExpand=" + str6 + ",showsRootHandles=" + str2 + ",toggleClickCount=" + toggleClickCount + ",visibleRowCount=" + visibleRowCount;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJTree();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJTree
    extends JComponent.AccessibleJComponent
    implements AccessibleSelection, TreeSelectionListener, TreeModelListener, TreeExpansionListener
  {
    TreePath leadSelectionPath;
    Accessible leadSelectionAccessible;
    
    public AccessibleJTree()
    {
      super();
      TreeModel localTreeModel = getModel();
      if (localTreeModel != null) {
        localTreeModel.addTreeModelListener(this);
      }
      addTreeExpansionListener(this);
      addTreeSelectionListener(this);
      leadSelectionPath = getLeadSelectionPath();
      leadSelectionAccessible = (leadSelectionPath != null ? new AccessibleJTreeNode(JTree.this, leadSelectionPath, JTree.this) : null);
    }
    
    public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
    {
      firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public void fireVisibleDataPropertyChange()
    {
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
    {
      fireVisibleDataPropertyChange();
    }
    
    public void treeNodesInserted(TreeModelEvent paramTreeModelEvent)
    {
      fireVisibleDataPropertyChange();
    }
    
    public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent)
    {
      fireVisibleDataPropertyChange();
    }
    
    public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
    {
      fireVisibleDataPropertyChange();
    }
    
    public void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent)
    {
      fireVisibleDataPropertyChange();
      TreePath localTreePath = paramTreeExpansionEvent.getPath();
      if (localTreePath != null)
      {
        AccessibleJTreeNode localAccessibleJTreeNode = new AccessibleJTreeNode(JTree.this, localTreePath, null);
        PropertyChangeEvent localPropertyChangeEvent = new PropertyChangeEvent(localAccessibleJTreeNode, "AccessibleState", AccessibleState.EXPANDED, AccessibleState.COLLAPSED);
        firePropertyChange("AccessibleState", null, localPropertyChangeEvent);
      }
    }
    
    public void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent)
    {
      fireVisibleDataPropertyChange();
      TreePath localTreePath = paramTreeExpansionEvent.getPath();
      if (localTreePath != null)
      {
        AccessibleJTreeNode localAccessibleJTreeNode = new AccessibleJTreeNode(JTree.this, localTreePath, null);
        PropertyChangeEvent localPropertyChangeEvent = new PropertyChangeEvent(localAccessibleJTreeNode, "AccessibleState", AccessibleState.COLLAPSED, AccessibleState.EXPANDED);
        firePropertyChange("AccessibleState", null, localPropertyChangeEvent);
      }
    }
    
    void fireActiveDescendantPropertyChange(TreePath paramTreePath1, TreePath paramTreePath2)
    {
      if (paramTreePath1 != paramTreePath2)
      {
        Object localObject1 = paramTreePath1 != null ? new AccessibleJTreeNode(JTree.this, paramTreePath1, null) : null;
        Object localObject2 = paramTreePath2 != null ? new AccessibleJTreeNode(JTree.this, paramTreePath2, null) : null;
        firePropertyChange("AccessibleActiveDescendant", localObject1, localObject2);
      }
    }
    
    private AccessibleContext getCurrentAccessibleContext()
    {
      Component localComponent = getCurrentComponent();
      if ((localComponent instanceof Accessible)) {
        return localComponent.getAccessibleContext();
      }
      return null;
    }
    
    private Component getCurrentComponent()
    {
      TreeModel localTreeModel = getModel();
      if (localTreeModel == null) {
        return null;
      }
      Object localObject = localTreeModel.getRoot();
      if (localObject == null) {
        return null;
      }
      TreePath localTreePath = new TreePath(localObject);
      if (isVisible(localTreePath))
      {
        TreeCellRenderer localTreeCellRenderer = getCellRenderer();
        TreeUI localTreeUI = getUI();
        if (localTreeUI != null)
        {
          int i = localTreeUI.getRowForPath(JTree.this, localTreePath);
          int j = getLeadSelectionRow();
          boolean bool1 = (isFocusOwner()) && (j == i);
          boolean bool2 = isPathSelected(localTreePath);
          boolean bool3 = isExpanded(localTreePath);
          return localTreeCellRenderer.getTreeCellRendererComponent(JTree.this, localObject, bool2, bool3, localTreeModel.isLeaf(localObject), i, bool1);
        }
      }
      return null;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.TREE;
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      TreePath localTreePath = getClosestPathForLocation(x, y);
      if (localTreePath != null) {
        return new AccessibleJTreeNode(JTree.this, localTreePath, null);
      }
      return null;
    }
    
    public int getAccessibleChildrenCount()
    {
      TreeModel localTreeModel = getModel();
      if (localTreeModel == null) {
        return 0;
      }
      if (isRootVisible()) {
        return 1;
      }
      Object localObject = localTreeModel.getRoot();
      if (localObject == null) {
        return 0;
      }
      return localTreeModel.getChildCount(localObject);
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      TreeModel localTreeModel = getModel();
      if (localTreeModel == null) {
        return null;
      }
      Object localObject1 = localTreeModel.getRoot();
      if (localObject1 == null) {
        return null;
      }
      if (isRootVisible())
      {
        if (paramInt == 0)
        {
          Object[] arrayOfObject1 = { localObject1 };
          if (arrayOfObject1[0] == null) {
            return null;
          }
          localObject2 = new TreePath(arrayOfObject1);
          return new AccessibleJTreeNode(JTree.this, (TreePath)localObject2, JTree.this);
        }
        return null;
      }
      int i = localTreeModel.getChildCount(localObject1);
      if ((paramInt < 0) || (paramInt >= i)) {
        return null;
      }
      Object localObject2 = localTreeModel.getChild(localObject1, paramInt);
      if (localObject2 == null) {
        return null;
      }
      Object[] arrayOfObject2 = { localObject1, localObject2 };
      TreePath localTreePath = new TreePath(arrayOfObject2);
      return new AccessibleJTreeNode(JTree.this, localTreePath, JTree.this);
    }
    
    public int getAccessibleIndexInParent()
    {
      return super.getAccessibleIndexInParent();
    }
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public int getAccessibleSelectionCount()
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = treeModel.getRoot();
      if (arrayOfObject[0] == null) {
        return 0;
      }
      TreePath localTreePath = new TreePath(arrayOfObject);
      if (isPathSelected(localTreePath)) {
        return 1;
      }
      return 0;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      if (paramInt == 0)
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = treeModel.getRoot();
        if (arrayOfObject[0] == null) {
          return null;
        }
        TreePath localTreePath = new TreePath(arrayOfObject);
        if (isPathSelected(localTreePath)) {
          return new AccessibleJTreeNode(JTree.this, localTreePath, JTree.this);
        }
      }
      return null;
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      if (paramInt == 0)
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = treeModel.getRoot();
        if (arrayOfObject[0] == null) {
          return false;
        }
        TreePath localTreePath = new TreePath(arrayOfObject);
        return isPathSelected(localTreePath);
      }
      return false;
    }
    
    public void addAccessibleSelection(int paramInt)
    {
      TreeModel localTreeModel = getModel();
      if ((localTreeModel != null) && (paramInt == 0))
      {
        Object[] arrayOfObject = { localTreeModel.getRoot() };
        if (arrayOfObject[0] == null) {
          return;
        }
        TreePath localTreePath = new TreePath(arrayOfObject);
        addSelectionPath(localTreePath);
      }
    }
    
    public void removeAccessibleSelection(int paramInt)
    {
      TreeModel localTreeModel = getModel();
      if ((localTreeModel != null) && (paramInt == 0))
      {
        Object[] arrayOfObject = { localTreeModel.getRoot() };
        if (arrayOfObject[0] == null) {
          return;
        }
        TreePath localTreePath = new TreePath(arrayOfObject);
        removeSelectionPath(localTreePath);
      }
    }
    
    public void clearAccessibleSelection()
    {
      int i = getAccessibleChildrenCount();
      for (int j = 0; j < i; j++) {
        removeAccessibleSelection(j);
      }
    }
    
    public void selectAllAccessibleSelection()
    {
      TreeModel localTreeModel = getModel();
      if (localTreeModel != null)
      {
        Object[] arrayOfObject = { localTreeModel.getRoot() };
        if (arrayOfObject[0] == null) {
          return;
        }
        TreePath localTreePath = new TreePath(arrayOfObject);
        addSelectionPath(localTreePath);
      }
    }
    
    protected class AccessibleJTreeNode
      extends AccessibleContext
      implements Accessible, AccessibleComponent, AccessibleSelection, AccessibleAction
    {
      private JTree tree = null;
      private TreeModel treeModel = null;
      private Object obj = null;
      private TreePath path = null;
      private Accessible accessibleParent = null;
      private int index = 0;
      private boolean isLeaf = false;
      
      public AccessibleJTreeNode(JTree paramJTree, TreePath paramTreePath, Accessible paramAccessible)
      {
        tree = paramJTree;
        path = paramTreePath;
        accessibleParent = paramAccessible;
        treeModel = paramJTree.getModel();
        obj = paramTreePath.getLastPathComponent();
        if (treeModel != null) {
          isLeaf = treeModel.isLeaf(obj);
        }
      }
      
      private TreePath getChildTreePath(int paramInt)
      {
        if ((paramInt < 0) || (paramInt >= getAccessibleChildrenCount())) {
          return null;
        }
        Object localObject = treeModel.getChild(obj, paramInt);
        Object[] arrayOfObject1 = path.getPath();
        Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
        arrayOfObject2[(arrayOfObject2.length - 1)] = localObject;
        return new TreePath(arrayOfObject2);
      }
      
      public AccessibleContext getAccessibleContext()
      {
        return this;
      }
      
      private AccessibleContext getCurrentAccessibleContext()
      {
        Component localComponent = getCurrentComponent();
        if ((localComponent instanceof Accessible)) {
          return localComponent.getAccessibleContext();
        }
        return null;
      }
      
      private Component getCurrentComponent()
      {
        if (tree.isVisible(path))
        {
          TreeCellRenderer localTreeCellRenderer = tree.getCellRenderer();
          if (localTreeCellRenderer == null) {
            return null;
          }
          TreeUI localTreeUI = tree.getUI();
          if (localTreeUI != null)
          {
            int i = localTreeUI.getRowForPath(JTree.this, path);
            boolean bool1 = tree.isPathSelected(path);
            boolean bool2 = tree.isExpanded(path);
            boolean bool3 = false;
            return localTreeCellRenderer.getTreeCellRendererComponent(tree, obj, bool1, bool2, isLeaf, i, bool3);
          }
        }
        return null;
      }
      
      public String getAccessibleName()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null)
        {
          String str = localAccessibleContext.getAccessibleName();
          if ((str != null) && (str != "")) {
            return localAccessibleContext.getAccessibleName();
          }
          return null;
        }
        if ((accessibleName != null) && (accessibleName != "")) {
          return accessibleName;
        }
        return (String)getClientProperty("AccessibleName");
      }
      
      public void setAccessibleName(String paramString)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.setAccessibleName(paramString);
        } else {
          super.setAccessibleName(paramString);
        }
      }
      
      public String getAccessibleDescription()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleDescription();
        }
        return super.getAccessibleDescription();
      }
      
      public void setAccessibleDescription(String paramString)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.setAccessibleDescription(paramString);
        } else {
          super.setAccessibleDescription(paramString);
        }
      }
      
      public AccessibleRole getAccessibleRole()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleRole();
        }
        return AccessibleRole.UNKNOWN;
      }
      
      public AccessibleStateSet getAccessibleStateSet()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        AccessibleStateSet localAccessibleStateSet;
        if (localAccessibleContext != null) {
          localAccessibleStateSet = localAccessibleContext.getAccessibleStateSet();
        } else {
          localAccessibleStateSet = new AccessibleStateSet();
        }
        if (isShowing()) {
          localAccessibleStateSet.add(AccessibleState.SHOWING);
        } else if (localAccessibleStateSet.contains(AccessibleState.SHOWING)) {
          localAccessibleStateSet.remove(AccessibleState.SHOWING);
        }
        if (isVisible()) {
          localAccessibleStateSet.add(AccessibleState.VISIBLE);
        } else if (localAccessibleStateSet.contains(AccessibleState.VISIBLE)) {
          localAccessibleStateSet.remove(AccessibleState.VISIBLE);
        }
        if (tree.isPathSelected(path)) {
          localAccessibleStateSet.add(AccessibleState.SELECTED);
        }
        if (path == getLeadSelectionPath()) {
          localAccessibleStateSet.add(AccessibleState.ACTIVE);
        }
        if (!isLeaf) {
          localAccessibleStateSet.add(AccessibleState.EXPANDABLE);
        }
        if (tree.isExpanded(path)) {
          localAccessibleStateSet.add(AccessibleState.EXPANDED);
        } else {
          localAccessibleStateSet.add(AccessibleState.COLLAPSED);
        }
        if (tree.isEditable()) {
          localAccessibleStateSet.add(AccessibleState.EDITABLE);
        }
        return localAccessibleStateSet;
      }
      
      public Accessible getAccessibleParent()
      {
        if (accessibleParent == null)
        {
          Object[] arrayOfObject1 = path.getPath();
          if (arrayOfObject1.length > 1)
          {
            Object localObject = arrayOfObject1[(arrayOfObject1.length - 2)];
            if (treeModel != null) {
              index = treeModel.getIndexOfChild(localObject, obj);
            }
            Object[] arrayOfObject2 = new Object[arrayOfObject1.length - 1];
            System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length - 1);
            TreePath localTreePath = new TreePath(arrayOfObject2);
            accessibleParent = new AccessibleJTreeNode(JTree.AccessibleJTree.this, tree, localTreePath, null);
            setAccessibleParent(accessibleParent);
          }
          else if (treeModel != null)
          {
            accessibleParent = tree;
            index = 0;
            setAccessibleParent(accessibleParent);
          }
        }
        return accessibleParent;
      }
      
      public int getAccessibleIndexInParent()
      {
        if (accessibleParent == null) {
          getAccessibleParent();
        }
        Object[] arrayOfObject = path.getPath();
        if (arrayOfObject.length > 1)
        {
          Object localObject = arrayOfObject[(arrayOfObject.length - 2)];
          if (treeModel != null) {
            index = treeModel.getIndexOfChild(localObject, obj);
          }
        }
        return index;
      }
      
      public int getAccessibleChildrenCount()
      {
        return treeModel.getChildCount(obj);
      }
      
      public Accessible getAccessibleChild(int paramInt)
      {
        if ((paramInt < 0) || (paramInt >= getAccessibleChildrenCount())) {
          return null;
        }
        Object localObject = treeModel.getChild(obj, paramInt);
        Object[] arrayOfObject1 = path.getPath();
        Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
        arrayOfObject2[(arrayOfObject2.length - 1)] = localObject;
        TreePath localTreePath = new TreePath(arrayOfObject2);
        return new AccessibleJTreeNode(JTree.AccessibleJTree.this, JTree.this, localTreePath, this);
      }
      
      public Locale getLocale()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getLocale();
        }
        return tree.getLocale();
      }
      
      public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.addPropertyChangeListener(paramPropertyChangeListener);
        } else {
          super.addPropertyChangeListener(paramPropertyChangeListener);
        }
      }
      
      public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.removePropertyChangeListener(paramPropertyChangeListener);
        } else {
          super.removePropertyChangeListener(paramPropertyChangeListener);
        }
      }
      
      public AccessibleAction getAccessibleAction()
      {
        return this;
      }
      
      public AccessibleComponent getAccessibleComponent()
      {
        return this;
      }
      
      public AccessibleSelection getAccessibleSelection()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext != null) && (isLeaf)) {
          return getCurrentAccessibleContext().getAccessibleSelection();
        }
        return this;
      }
      
      public AccessibleText getAccessibleText()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return getCurrentAccessibleContext().getAccessibleText();
        }
        return null;
      }
      
      public AccessibleValue getAccessibleValue()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return getCurrentAccessibleContext().getAccessibleValue();
        }
        return null;
      }
      
      public Color getBackground()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getBackground();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getBackground();
        }
        return null;
      }
      
      public void setBackground(Color paramColor)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setBackground(paramColor);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setBackground(paramColor);
          }
        }
      }
      
      public Color getForeground()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getForeground();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getForeground();
        }
        return null;
      }
      
      public void setForeground(Color paramColor)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setForeground(paramColor);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setForeground(paramColor);
          }
        }
      }
      
      public Cursor getCursor()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getCursor();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getCursor();
        }
        Accessible localAccessible = getAccessibleParent();
        if ((localAccessible instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessible).getCursor();
        }
        return null;
      }
      
      public void setCursor(Cursor paramCursor)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setCursor(paramCursor);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setCursor(paramCursor);
          }
        }
      }
      
      public Font getFont()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getFont();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getFont();
        }
        return null;
      }
      
      public void setFont(Font paramFont)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setFont(paramFont);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setFont(paramFont);
          }
        }
      }
      
      public FontMetrics getFontMetrics(Font paramFont)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getFontMetrics(paramFont);
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getFontMetrics(paramFont);
        }
        return null;
      }
      
      public boolean isEnabled()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).isEnabled();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.isEnabled();
        }
        return false;
      }
      
      public void setEnabled(boolean paramBoolean)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setEnabled(paramBoolean);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setEnabled(paramBoolean);
          }
        }
      }
      
      public boolean isVisible()
      {
        Rectangle localRectangle1 = tree.getPathBounds(path);
        Rectangle localRectangle2 = tree.getVisibleRect();
        return (localRectangle1 != null) && (localRectangle2 != null) && (localRectangle2.intersects(localRectangle1));
      }
      
      public void setVisible(boolean paramBoolean) {}
      
      public boolean isShowing()
      {
        return (tree.isShowing()) && (isVisible());
      }
      
      public boolean contains(Point paramPoint)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          localObject = ((AccessibleComponent)localAccessibleContext).getBounds();
          return ((Rectangle)localObject).contains(paramPoint);
        }
        Object localObject = getCurrentComponent();
        if (localObject != null)
        {
          Rectangle localRectangle = ((Component)localObject).getBounds();
          return localRectangle.contains(paramPoint);
        }
        return getBounds().contains(paramPoint);
      }
      
      public Point getLocationOnScreen()
      {
        if (tree != null)
        {
          Point localPoint1 = tree.getLocationOnScreen();
          Rectangle localRectangle = tree.getPathBounds(path);
          if ((localPoint1 != null) && (localRectangle != null))
          {
            Point localPoint2 = new Point(x, y);
            localPoint2.translate(x, y);
            return localPoint2;
          }
          return null;
        }
        return null;
      }
      
      protected Point getLocationInJTree()
      {
        Rectangle localRectangle = tree.getPathBounds(path);
        if (localRectangle != null) {
          return localRectangle.getLocation();
        }
        return null;
      }
      
      public Point getLocation()
      {
        Rectangle localRectangle = getBounds();
        if (localRectangle != null) {
          return localRectangle.getLocation();
        }
        return null;
      }
      
      public void setLocation(Point paramPoint) {}
      
      public Rectangle getBounds()
      {
        Rectangle localRectangle = tree.getPathBounds(path);
        Accessible localAccessible = getAccessibleParent();
        if ((localAccessible != null) && ((localAccessible instanceof AccessibleJTreeNode)))
        {
          Point localPoint = ((AccessibleJTreeNode)localAccessible).getLocationInJTree();
          if ((localPoint != null) && (localRectangle != null)) {
            localRectangle.translate(-x, -y);
          } else {
            return null;
          }
        }
        return localRectangle;
      }
      
      public void setBounds(Rectangle paramRectangle)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setBounds(paramRectangle);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setBounds(paramRectangle);
          }
        }
      }
      
      public Dimension getSize()
      {
        return getBounds().getSize();
      }
      
      public void setSize(Dimension paramDimension)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setSize(paramDimension);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setSize(paramDimension);
          }
        }
      }
      
      public Accessible getAccessibleAt(Point paramPoint)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getAccessibleAt(paramPoint);
        }
        return null;
      }
      
      public boolean isFocusTraversable()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).isFocusTraversable();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.isFocusTraversable();
        }
        return false;
      }
      
      public void requestFocus()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).requestFocus();
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.requestFocus();
          }
        }
      }
      
      public void addFocusListener(FocusListener paramFocusListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).addFocusListener(paramFocusListener);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.addFocusListener(paramFocusListener);
          }
        }
      }
      
      public void removeFocusListener(FocusListener paramFocusListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).removeFocusListener(paramFocusListener);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.removeFocusListener(paramFocusListener);
          }
        }
      }
      
      public int getAccessibleSelectionCount()
      {
        int i = 0;
        int j = getAccessibleChildrenCount();
        for (int k = 0; k < j; k++)
        {
          TreePath localTreePath = getChildTreePath(k);
          if (tree.isPathSelected(localTreePath)) {
            i++;
          }
        }
        return i;
      }
      
      public Accessible getAccessibleSelection(int paramInt)
      {
        int i = getAccessibleChildrenCount();
        if ((paramInt < 0) || (paramInt >= i)) {
          return null;
        }
        int j = 0;
        for (int k = 0; (k < i) && (paramInt >= j); k++)
        {
          TreePath localTreePath = getChildTreePath(k);
          if (tree.isPathSelected(localTreePath))
          {
            if (j == paramInt) {
              return new AccessibleJTreeNode(JTree.AccessibleJTree.this, tree, localTreePath, this);
            }
            j++;
          }
        }
        return null;
      }
      
      public boolean isAccessibleChildSelected(int paramInt)
      {
        int i = getAccessibleChildrenCount();
        if ((paramInt < 0) || (paramInt >= i)) {
          return false;
        }
        TreePath localTreePath = getChildTreePath(paramInt);
        return tree.isPathSelected(localTreePath);
      }
      
      public void addAccessibleSelection(int paramInt)
      {
        TreeModel localTreeModel = getModel();
        if ((localTreeModel != null) && (paramInt >= 0) && (paramInt < getAccessibleChildrenCount()))
        {
          TreePath localTreePath = getChildTreePath(paramInt);
          addSelectionPath(localTreePath);
        }
      }
      
      public void removeAccessibleSelection(int paramInt)
      {
        TreeModel localTreeModel = getModel();
        if ((localTreeModel != null) && (paramInt >= 0) && (paramInt < getAccessibleChildrenCount()))
        {
          TreePath localTreePath = getChildTreePath(paramInt);
          removeSelectionPath(localTreePath);
        }
      }
      
      public void clearAccessibleSelection()
      {
        int i = getAccessibleChildrenCount();
        for (int j = 0; j < i; j++) {
          removeAccessibleSelection(j);
        }
      }
      
      public void selectAllAccessibleSelection()
      {
        TreeModel localTreeModel = getModel();
        if (localTreeModel != null)
        {
          int i = getAccessibleChildrenCount();
          for (int j = 0; j < i; j++)
          {
            TreePath localTreePath = getChildTreePath(j);
            addSelectionPath(localTreePath);
          }
        }
      }
      
      public int getAccessibleActionCount()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null)
        {
          AccessibleAction localAccessibleAction = localAccessibleContext.getAccessibleAction();
          if (localAccessibleAction != null) {
            return localAccessibleAction.getAccessibleActionCount() + (isLeaf ? 0 : 1);
          }
        }
        return isLeaf ? 0 : 1;
      }
      
      public String getAccessibleActionDescription(int paramInt)
      {
        if ((paramInt < 0) || (paramInt >= getAccessibleActionCount())) {
          return null;
        }
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (paramInt == 0) {
          return AccessibleAction.TOGGLE_EXPAND;
        }
        if (localAccessibleContext != null)
        {
          AccessibleAction localAccessibleAction = localAccessibleContext.getAccessibleAction();
          if (localAccessibleAction != null) {
            return localAccessibleAction.getAccessibleActionDescription(paramInt - 1);
          }
        }
        return null;
      }
      
      public boolean doAccessibleAction(int paramInt)
      {
        if ((paramInt < 0) || (paramInt >= getAccessibleActionCount())) {
          return false;
        }
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (paramInt == 0)
        {
          if (isExpanded(path)) {
            collapsePath(path);
          } else {
            expandPath(path);
          }
          return true;
        }
        if (localAccessibleContext != null)
        {
          AccessibleAction localAccessibleAction = localAccessibleContext.getAccessibleAction();
          if (localAccessibleAction != null) {
            return localAccessibleAction.doAccessibleAction(paramInt - 1);
          }
        }
        return false;
      }
    }
  }
  
  public static final class DropLocation
    extends TransferHandler.DropLocation
  {
    private final TreePath path;
    private final int index;
    
    private DropLocation(Point paramPoint, TreePath paramTreePath, int paramInt)
    {
      super();
      path = paramTreePath;
      index = paramInt;
    }
    
    public int getChildIndex()
    {
      return index;
    }
    
    public TreePath getPath()
    {
      return path;
    }
    
    public String toString()
    {
      return getClass().getName() + "[dropPoint=" + getDropPoint() + ",path=" + path + ",childIndex=" + index + "]";
    }
  }
  
  public static class DynamicUtilTreeNode
    extends DefaultMutableTreeNode
  {
    protected boolean hasChildren;
    protected Object childValue;
    protected boolean loadedChildren = false;
    
    public static void createChildren(DefaultMutableTreeNode paramDefaultMutableTreeNode, Object paramObject)
    {
      Object localObject1;
      if ((paramObject instanceof Vector))
      {
        localObject1 = (Vector)paramObject;
        int i = 0;
        int k = ((Vector)localObject1).size();
        while (i < k)
        {
          paramDefaultMutableTreeNode.add(new DynamicUtilTreeNode(((Vector)localObject1).elementAt(i), ((Vector)localObject1).elementAt(i)));
          i++;
        }
      }
      else if ((paramObject instanceof Hashtable))
      {
        localObject1 = (Hashtable)paramObject;
        Enumeration localEnumeration = ((Hashtable)localObject1).keys();
        while (localEnumeration.hasMoreElements())
        {
          Object localObject2 = localEnumeration.nextElement();
          paramDefaultMutableTreeNode.add(new DynamicUtilTreeNode(localObject2, ((Hashtable)localObject1).get(localObject2)));
        }
      }
      else if ((paramObject instanceof Object[]))
      {
        localObject1 = (Object[])paramObject;
        int j = 0;
        int m = localObject1.length;
        while (j < m)
        {
          paramDefaultMutableTreeNode.add(new DynamicUtilTreeNode(localObject1[j], localObject1[j]));
          j++;
        }
      }
    }
    
    public DynamicUtilTreeNode(Object paramObject1, Object paramObject2)
    {
      super();
      childValue = paramObject2;
      if (paramObject2 != null)
      {
        if ((paramObject2 instanceof Vector)) {
          setAllowsChildren(true);
        } else if ((paramObject2 instanceof Hashtable)) {
          setAllowsChildren(true);
        } else if ((paramObject2 instanceof Object[])) {
          setAllowsChildren(true);
        } else {
          setAllowsChildren(false);
        }
      }
      else {
        setAllowsChildren(false);
      }
    }
    
    public boolean isLeaf()
    {
      return !getAllowsChildren();
    }
    
    public int getChildCount()
    {
      if (!loadedChildren) {
        loadChildren();
      }
      return super.getChildCount();
    }
    
    protected void loadChildren()
    {
      loadedChildren = true;
      createChildren(this, childValue);
    }
    
    public TreeNode getChildAt(int paramInt)
    {
      if (!loadedChildren) {
        loadChildren();
      }
      return super.getChildAt(paramInt);
    }
    
    public Enumeration children()
    {
      if (!loadedChildren) {
        loadChildren();
      }
      return super.children();
    }
  }
  
  protected static class EmptySelectionModel
    extends DefaultTreeSelectionModel
  {
    protected static final EmptySelectionModel sharedInstance = new EmptySelectionModel();
    
    protected EmptySelectionModel() {}
    
    public static EmptySelectionModel sharedInstance()
    {
      return sharedInstance;
    }
    
    public void setSelectionPaths(TreePath[] paramArrayOfTreePath) {}
    
    public void addSelectionPaths(TreePath[] paramArrayOfTreePath) {}
    
    public void removeSelectionPaths(TreePath[] paramArrayOfTreePath) {}
    
    public void setSelectionMode(int paramInt) {}
    
    public void setRowMapper(RowMapper paramRowMapper) {}
    
    public void addTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener) {}
    
    public void removeTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener) {}
    
    public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {}
    
    public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {}
  }
  
  protected class TreeModelHandler
    implements TreeModelListener
  {
    protected TreeModelHandler() {}
    
    public void treeNodesChanged(TreeModelEvent paramTreeModelEvent) {}
    
    public void treeNodesInserted(TreeModelEvent paramTreeModelEvent) {}
    
    public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
    {
      if (paramTreeModelEvent == null) {
        return;
      }
      TreePath localTreePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
      if (localTreePath == null) {
        return;
      }
      Object localObject;
      if (localTreePath.getPathCount() == 1)
      {
        clearToggledPaths();
        localObject = treeModel.getRoot();
        if ((localObject != null) && (!treeModel.isLeaf(localObject))) {
          expandedState.put(localTreePath, Boolean.TRUE);
        }
      }
      else if (expandedState.get(localTreePath) != null)
      {
        localObject = new Vector(1);
        boolean bool = isExpanded(localTreePath);
        ((Vector)localObject).addElement(localTreePath);
        removeDescendantToggledPaths(((Vector)localObject).elements());
        if (bool)
        {
          TreeModel localTreeModel = getModel();
          if ((localTreeModel == null) || (localTreeModel.isLeaf(localTreePath.getLastPathComponent()))) {
            collapsePath(localTreePath);
          } else {
            expandedState.put(localTreePath, Boolean.TRUE);
          }
        }
      }
      removeDescendantSelectedPaths(localTreePath, false);
    }
    
    public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent)
    {
      if (paramTreeModelEvent == null) {
        return;
      }
      TreePath localTreePath1 = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
      Object[] arrayOfObject = paramTreeModelEvent.getChildren();
      if (arrayOfObject == null) {
        return;
      }
      Vector localVector = new Vector(Math.max(1, arrayOfObject.length));
      for (int i = arrayOfObject.length - 1; i >= 0; i--)
      {
        TreePath localTreePath2 = localTreePath1.pathByAddingChild(arrayOfObject[i]);
        if (expandedState.get(localTreePath2) != null) {
          localVector.addElement(localTreePath2);
        }
      }
      if (localVector.size() > 0) {
        removeDescendantToggledPaths(localVector.elements());
      }
      TreeModel localTreeModel = getModel();
      if ((localTreeModel == null) || (localTreeModel.isLeaf(localTreePath1.getLastPathComponent()))) {
        expandedState.remove(localTreePath1);
      }
      removeDescendantSelectedPaths(paramTreeModelEvent);
    }
  }
  
  protected class TreeSelectionRedirector
    implements Serializable, TreeSelectionListener
  {
    protected TreeSelectionRedirector() {}
    
    public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
    {
      TreeSelectionEvent localTreeSelectionEvent = (TreeSelectionEvent)paramTreeSelectionEvent.cloneWithSource(JTree.this);
      fireValueChanged(localTreeSelectionEvent);
    }
  }
  
  private class TreeTimer
    extends Timer
  {
    public TreeTimer()
    {
      super(null);
      setRepeats(false);
    }
    
    public void fireActionPerformed(ActionEvent paramActionEvent)
    {
      expandRow(expandRow);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */