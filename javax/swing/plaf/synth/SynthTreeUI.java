package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.CellRendererPane;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JTree.DropLocation;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import sun.swing.plaf.synth.SynthIcon;

public class SynthTreeUI
  extends BasicTreeUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private SynthStyle cellStyle;
  private SynthContext paintContext;
  private boolean drawHorizontalLines;
  private boolean drawVerticalLines;
  private Object linesStyle;
  private int padding;
  private boolean useTreeColors;
  private Icon expandedIconWrapper = new ExpandedIconWrapper(null);
  
  public SynthTreeUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthTreeUI();
  }
  
  public Icon getExpandedIcon()
  {
    return expandedIconWrapper;
  }
  
  protected void installDefaults()
  {
    updateStyle(tree);
  }
  
  private void updateStyle(JTree paramJTree)
  {
    SynthContext localSynthContext = getContext(paramJTree, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      setExpandedIcon(style.getIcon(localSynthContext, "Tree.expandedIcon"));
      setCollapsedIcon(style.getIcon(localSynthContext, "Tree.collapsedIcon"));
      setLeftChildIndent(style.getInt(localSynthContext, "Tree.leftChildIndent", 0));
      setRightChildIndent(style.getInt(localSynthContext, "Tree.rightChildIndent", 0));
      drawHorizontalLines = style.getBoolean(localSynthContext, "Tree.drawHorizontalLines", true);
      drawVerticalLines = style.getBoolean(localSynthContext, "Tree.drawVerticalLines", true);
      linesStyle = style.get(localSynthContext, "Tree.linesStyle");
      Object localObject = style.get(localSynthContext, "Tree.rowHeight");
      if (localObject != null) {
        LookAndFeel.installProperty(paramJTree, "rowHeight", localObject);
      }
      localObject = style.get(localSynthContext, "Tree.scrollsOnExpand");
      LookAndFeel.installProperty(paramJTree, "scrollsOnExpand", localObject != null ? localObject : Boolean.TRUE);
      padding = style.getInt(localSynthContext, "Tree.padding", 0);
      largeModel = ((paramJTree.isLargeModel()) && (paramJTree.getRowHeight() > 0));
      useTreeColors = style.getBoolean(localSynthContext, "Tree.rendererUseTreeColors", true);
      Boolean localBoolean = Boolean.valueOf(style.getBoolean(localSynthContext, "Tree.showsRootHandles", Boolean.TRUE.booleanValue()));
      LookAndFeel.installProperty(paramJTree, "showsRootHandles", localBoolean);
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    localSynthContext.dispose();
    localSynthContext = getContext(paramJTree, Region.TREE_CELL, 1);
    cellStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    tree.addPropertyChangeListener(this);
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion)
  {
    return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion));
  }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, paramRegion, cellStyle, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion)
  {
    return 513;
  }
  
  protected TreeCellEditor createDefaultCellEditor()
  {
    TreeCellRenderer localTreeCellRenderer = tree.getCellRenderer();
    SynthTreeCellEditor localSynthTreeCellEditor;
    if ((localTreeCellRenderer != null) && ((localTreeCellRenderer instanceof DefaultTreeCellRenderer))) {
      localSynthTreeCellEditor = new SynthTreeCellEditor(tree, (DefaultTreeCellRenderer)localTreeCellRenderer);
    } else {
      localSynthTreeCellEditor = new SynthTreeCellEditor(tree, null);
    }
    return localSynthTreeCellEditor;
  }
  
  protected TreeCellRenderer createDefaultCellRenderer()
  {
    return new SynthTreeCellRenderer();
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(tree, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    localSynthContext = getContext(tree, Region.TREE_CELL, 1);
    cellStyle.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    cellStyle = null;
    if ((tree.getTransferHandler() instanceof UIResource)) {
      tree.setTransferHandler(null);
    }
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    tree.removePropertyChangeListener(this);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintTreeBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintTreeBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    paintContext = paramSynthContext;
    updateLeadSelectionRow();
    Rectangle localRectangle1 = paramGraphics.getClipBounds();
    Insets localInsets = tree.getInsets();
    TreePath localTreePath1 = getClosestPathForLocation(tree, 0, y);
    Enumeration localEnumeration = treeState.getVisiblePathsFrom(localTreePath1);
    int i = treeState.getRowForPath(localTreePath1);
    int j = y + height;
    TreeModel localTreeModel = tree.getModel();
    SynthContext localSynthContext = getContext(tree, Region.TREE_CELL);
    drawingCache.clear();
    setHashColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.FOREGROUND));
    if (localEnumeration != null)
    {
      int k = 0;
      Rectangle localRectangle2 = new Rectangle(0, 0, tree.getWidth(), 0);
      TreeCellRenderer localTreeCellRenderer = tree.getCellRenderer();
      DefaultTreeCellRenderer localDefaultTreeCellRenderer = (localTreeCellRenderer instanceof DefaultTreeCellRenderer) ? (DefaultTreeCellRenderer)localTreeCellRenderer : null;
      configureRenderer(localSynthContext);
      TreePath localTreePath2;
      Rectangle localRectangle3;
      boolean bool3;
      boolean bool2;
      boolean bool1;
      while ((k == 0) && (localEnumeration.hasMoreElements()))
      {
        localTreePath2 = (TreePath)localEnumeration.nextElement();
        localRectangle3 = getPathBounds(tree, localTreePath2);
        if ((localTreePath2 != null) && (localRectangle3 != null))
        {
          bool3 = localTreeModel.isLeaf(localTreePath2.getLastPathComponent());
          if (bool3)
          {
            bool1 = bool2 = 0;
          }
          else
          {
            bool1 = treeState.getExpandedState(localTreePath2);
            bool2 = tree.hasBeenExpanded(localTreePath2);
          }
          y = y;
          height = height;
          paintRow(localTreeCellRenderer, localDefaultTreeCellRenderer, paramSynthContext, localSynthContext, paramGraphics, localRectangle1, localInsets, localRectangle3, localRectangle2, localTreePath2, i, bool1, bool2, bool3);
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
      boolean bool4 = tree.isRootVisible();
      TreePath localTreePath3 = localTreePath1;
      for (localTreePath3 = localTreePath3.getParentPath(); localTreePath3 != null; localTreePath3 = localTreePath3.getParentPath())
      {
        paintVerticalPartOfLeg(paramGraphics, localRectangle1, localInsets, localTreePath3);
        drawingCache.put(localTreePath3, Boolean.TRUE);
      }
      k = 0;
      localEnumeration = treeState.getVisiblePathsFrom(localTreePath1);
      while ((k == 0) && (localEnumeration.hasMoreElements()))
      {
        localTreePath2 = (TreePath)localEnumeration.nextElement();
        localRectangle3 = getPathBounds(tree, localTreePath2);
        if ((localTreePath2 != null) && (localRectangle3 != null))
        {
          bool3 = localTreeModel.isLeaf(localTreePath2.getLastPathComponent());
          if (bool3)
          {
            bool1 = bool2 = 0;
          }
          else
          {
            bool1 = treeState.getExpandedState(localTreePath2);
            bool2 = tree.hasBeenExpanded(localTreePath2);
          }
          localTreePath3 = localTreePath2.getParentPath();
          if (localTreePath3 != null)
          {
            if (drawingCache.get(localTreePath3) == null)
            {
              paintVerticalPartOfLeg(paramGraphics, localRectangle1, localInsets, localTreePath3);
              drawingCache.put(localTreePath3, Boolean.TRUE);
            }
            paintHorizontalPartOfLeg(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath2, i, bool1, bool2, bool3);
          }
          else if ((bool4) && (i == 0))
          {
            paintHorizontalPartOfLeg(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath2, i, bool1, bool2, bool3);
          }
          if (shouldPaintExpandControl(localTreePath2, i, bool1, bool2, bool3)) {
            paintExpandControl(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath2, i, bool1, bool2, bool3);
          }
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
    localSynthContext.dispose();
    paintDropLine(paramGraphics);
    rendererPane.removeAll();
    paintContext = null;
  }
  
  private void configureRenderer(SynthContext paramSynthContext)
  {
    TreeCellRenderer localTreeCellRenderer = tree.getCellRenderer();
    if ((localTreeCellRenderer instanceof DefaultTreeCellRenderer))
    {
      DefaultTreeCellRenderer localDefaultTreeCellRenderer = (DefaultTreeCellRenderer)localTreeCellRenderer;
      SynthStyle localSynthStyle = paramSynthContext.getStyle();
      paramSynthContext.setComponentState(513);
      Color localColor = localDefaultTreeCellRenderer.getTextSelectionColor();
      if ((localColor == null) || ((localColor instanceof UIResource))) {
        localDefaultTreeCellRenderer.setTextSelectionColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
      }
      localColor = localDefaultTreeCellRenderer.getBackgroundSelectionColor();
      if ((localColor == null) || ((localColor instanceof UIResource))) {
        localDefaultTreeCellRenderer.setBackgroundSelectionColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_BACKGROUND));
      }
      paramSynthContext.setComponentState(1);
      localColor = localDefaultTreeCellRenderer.getTextNonSelectionColor();
      if ((localColor == null) || ((localColor instanceof UIResource))) {
        localDefaultTreeCellRenderer.setTextNonSelectionColor(localSynthStyle.getColorForState(paramSynthContext, ColorType.TEXT_FOREGROUND));
      }
      localColor = localDefaultTreeCellRenderer.getBackgroundNonSelectionColor();
      if ((localColor == null) || ((localColor instanceof UIResource))) {
        localDefaultTreeCellRenderer.setBackgroundNonSelectionColor(localSynthStyle.getColorForState(paramSynthContext, ColorType.TEXT_BACKGROUND));
      }
    }
  }
  
  protected void paintHorizontalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (drawHorizontalLines) {
      super.paintHorizontalPartOfLeg(paramGraphics, paramRectangle1, paramInsets, paramRectangle2, paramTreePath, paramInt, paramBoolean1, paramBoolean2, paramBoolean3);
    }
  }
  
  protected void paintHorizontalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3)
  {
    paintContext.getStyle().getGraphicsUtils(paintContext).drawLine(paintContext, "Tree.horizontalLine", paramGraphics, paramInt2, paramInt1, paramInt3, paramInt1, linesStyle);
  }
  
  protected void paintVerticalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle, Insets paramInsets, TreePath paramTreePath)
  {
    if (drawVerticalLines) {
      super.paintVerticalPartOfLeg(paramGraphics, paramRectangle, paramInsets, paramTreePath);
    }
  }
  
  protected void paintVerticalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3)
  {
    paintContext.getStyle().getGraphicsUtils(paintContext).drawLine(paintContext, "Tree.verticalLine", paramGraphics, paramInt1, paramInt2, paramInt1, paramInt3, linesStyle);
  }
  
  private void paintRow(TreeCellRenderer paramTreeCellRenderer, DefaultTreeCellRenderer paramDefaultTreeCellRenderer, SynthContext paramSynthContext1, SynthContext paramSynthContext2, Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, Rectangle paramRectangle3, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool = tree.isRowSelected(paramInt);
    JTree.DropLocation localDropLocation = tree.getDropLocation();
    int i = (localDropLocation != null) && (localDropLocation.getChildIndex() == -1) && (paramTreePath == localDropLocation.getPath()) ? 1 : 0;
    int j = 1;
    if ((bool) || (i != 0)) {
      j |= 0x200;
    }
    if ((tree.isFocusOwner()) && (paramInt == getLeadSelectionRow())) {
      j |= 0x100;
    }
    paramSynthContext2.setComponentState(j);
    if ((paramDefaultTreeCellRenderer != null) && ((paramDefaultTreeCellRenderer.getBorderSelectionColor() instanceof UIResource))) {
      paramDefaultTreeCellRenderer.setBorderSelectionColor(style.getColor(paramSynthContext2, ColorType.FOCUS));
    }
    SynthLookAndFeel.updateSubregion(paramSynthContext2, paramGraphics, paramRectangle3);
    paramSynthContext2.getPainter().paintTreeCellBackground(paramSynthContext2, paramGraphics, x, y, width, height);
    paramSynthContext2.getPainter().paintTreeCellBorder(paramSynthContext2, paramGraphics, x, y, width, height);
    if ((editingComponent != null) && (editingRow == paramInt)) {
      return;
    }
    int k;
    if (tree.hasFocus()) {
      k = getLeadSelectionRow();
    } else {
      k = -1;
    }
    Component localComponent = paramTreeCellRenderer.getTreeCellRendererComponent(tree, paramTreePath.getLastPathComponent(), bool, paramBoolean1, paramBoolean3, paramInt, k == paramInt);
    rendererPane.paintComponent(paramGraphics, localComponent, tree, x, y, width, height, true);
  }
  
  private int findCenteredX(int paramInt1, int paramInt2)
  {
    return tree.getComponentOrientation().isLeftToRight() ? paramInt1 - (int)Math.ceil(paramInt2 / 2.0D) : paramInt1 - (int)Math.floor(paramInt2 / 2.0D);
  }
  
  protected void paintExpandControl(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool = tree.getSelectionModel().isPathSelected(paramTreePath);
    int i = paintContext.getComponentState();
    if (bool) {
      paintContext.setComponentState(i | 0x200);
    }
    super.paintExpandControl(paramGraphics, paramRectangle1, paramInsets, paramRectangle2, paramTreePath, paramInt, paramBoolean1, paramBoolean2, paramBoolean3);
    paintContext.setComponentState(i);
  }
  
  protected void drawCentered(Component paramComponent, Graphics paramGraphics, Icon paramIcon, int paramInt1, int paramInt2)
  {
    int i = SynthIcon.getIconWidth(paramIcon, paintContext);
    int j = SynthIcon.getIconHeight(paramIcon, paintContext);
    SynthIcon.paintIcon(paramIcon, paintContext, paramGraphics, findCenteredX(paramInt1, i), paramInt2 - j / 2, i, j);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JTree)paramPropertyChangeEvent.getSource());
    }
    if ("dropLocation" == paramPropertyChangeEvent.getPropertyName())
    {
      JTree.DropLocation localDropLocation = (JTree.DropLocation)paramPropertyChangeEvent.getOldValue();
      repaintDropLocation(localDropLocation);
      repaintDropLocation(tree.getDropLocation());
    }
  }
  
  protected void paintDropLine(Graphics paramGraphics)
  {
    JTree.DropLocation localDropLocation = tree.getDropLocation();
    if (!isDropLine(localDropLocation)) {
      return;
    }
    Color localColor = (Color)style.get(paintContext, "Tree.dropLineColor");
    if (localColor != null)
    {
      paramGraphics.setColor(localColor);
      Rectangle localRectangle = getDropLineRect(localDropLocation);
      paramGraphics.fillRect(x, y, width, height);
    }
  }
  
  private void repaintDropLocation(JTree.DropLocation paramDropLocation)
  {
    if (paramDropLocation == null) {
      return;
    }
    Rectangle localRectangle;
    if (isDropLine(paramDropLocation))
    {
      localRectangle = getDropLineRect(paramDropLocation);
    }
    else
    {
      localRectangle = tree.getPathBounds(paramDropLocation.getPath());
      if (localRectangle != null)
      {
        x = 0;
        width = tree.getWidth();
      }
    }
    if (localRectangle != null) {
      tree.repaint(localRectangle);
    }
  }
  
  protected int getRowX(int paramInt1, int paramInt2)
  {
    return super.getRowX(paramInt1, paramInt2) + padding;
  }
  
  private class ExpandedIconWrapper
    extends SynthIcon
  {
    private ExpandedIconWrapper() {}
    
    public void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (paramSynthContext == null)
      {
        paramSynthContext = getContext(tree);
        SynthIcon.paintIcon(expandedIcon, paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
        paramSynthContext.dispose();
      }
      else
      {
        SynthIcon.paintIcon(expandedIcon, paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public int getIconWidth(SynthContext paramSynthContext)
    {
      int i;
      if (paramSynthContext == null)
      {
        paramSynthContext = getContext(tree);
        i = SynthIcon.getIconWidth(expandedIcon, paramSynthContext);
        paramSynthContext.dispose();
      }
      else
      {
        i = SynthIcon.getIconWidth(expandedIcon, paramSynthContext);
      }
      return i;
    }
    
    public int getIconHeight(SynthContext paramSynthContext)
    {
      int i;
      if (paramSynthContext == null)
      {
        paramSynthContext = getContext(tree);
        i = SynthIcon.getIconHeight(expandedIcon, paramSynthContext);
        paramSynthContext.dispose();
      }
      else
      {
        i = SynthIcon.getIconHeight(expandedIcon, paramSynthContext);
      }
      return i;
    }
  }
  
  private static class SynthTreeCellEditor
    extends DefaultTreeCellEditor
  {
    public SynthTreeCellEditor(JTree paramJTree, DefaultTreeCellRenderer paramDefaultTreeCellRenderer)
    {
      super(paramDefaultTreeCellRenderer);
      setBorderSelectionColor(null);
    }
    
    protected TreeCellEditor createTreeCellEditor()
    {
      JTextField local1 = new JTextField()
      {
        public String getName()
        {
          return "Tree.cellEditor";
        }
      };
      DefaultCellEditor localDefaultCellEditor = new DefaultCellEditor(local1);
      localDefaultCellEditor.setClickCountToStart(1);
      return localDefaultCellEditor;
    }
  }
  
  private class SynthTreeCellRenderer
    extends DefaultTreeCellRenderer
    implements UIResource
  {
    SynthTreeCellRenderer() {}
    
    public String getName()
    {
      return "Tree.cellRenderer";
    }
    
    public Component getTreeCellRendererComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4)
    {
      if ((!useTreeColors) && ((paramBoolean1) || (paramBoolean4))) {
        SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), paramBoolean1, paramBoolean4, paramJTree.isEnabled(), false);
      } else {
        SynthLookAndFeel.resetSelectedUI();
      }
      return super.getTreeCellRendererComponent(paramJTree, paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, paramBoolean4);
    }
    
    public void paint(Graphics paramGraphics)
    {
      paintComponent(paramGraphics);
      if (hasFocus)
      {
        SynthContext localSynthContext = SynthTreeUI.this.getContext(tree, Region.TREE_CELL);
        if (localSynthContext.getStyle() == null)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError("SynthTreeCellRenderer is being used outside of UI that created it");
          }
          return;
        }
        int i = 0;
        Icon localIcon = getIcon();
        if ((localIcon != null) && (getText() != null)) {
          i = localIcon.getIconWidth() + Math.max(0, getIconTextGap() - 1);
        }
        if (selected) {
          localSynthContext.setComponentState(513);
        } else {
          localSynthContext.setComponentState(1);
        }
        if (getComponentOrientation().isLeftToRight()) {
          localSynthContext.getPainter().paintTreeCellFocus(localSynthContext, paramGraphics, i, 0, getWidth() - i, getHeight());
        } else {
          localSynthContext.getPainter().paintTreeCellFocus(localSynthContext, paramGraphics, 0, 0, getWidth() - i, getHeight());
        }
        localSynthContext.dispose();
      }
      SynthLookAndFeel.resetSelectedUI();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */