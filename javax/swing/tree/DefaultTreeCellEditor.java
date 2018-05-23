package javax.swing.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.FontUIResource;

public class DefaultTreeCellEditor
  implements ActionListener, TreeCellEditor, TreeSelectionListener
{
  protected TreeCellEditor realEditor;
  protected DefaultTreeCellRenderer renderer;
  protected Container editingContainer;
  protected transient Component editingComponent;
  protected boolean canEdit;
  protected transient int offset;
  protected transient JTree tree;
  protected transient TreePath lastPath;
  protected transient Timer timer;
  protected transient int lastRow;
  protected Color borderSelectionColor;
  protected transient Icon editingIcon;
  protected Font font;
  
  public DefaultTreeCellEditor(JTree paramJTree, DefaultTreeCellRenderer paramDefaultTreeCellRenderer)
  {
    this(paramJTree, paramDefaultTreeCellRenderer, null);
  }
  
  public DefaultTreeCellEditor(JTree paramJTree, DefaultTreeCellRenderer paramDefaultTreeCellRenderer, TreeCellEditor paramTreeCellEditor)
  {
    renderer = paramDefaultTreeCellRenderer;
    realEditor = paramTreeCellEditor;
    if (realEditor == null) {
      realEditor = createTreeCellEditor();
    }
    editingContainer = createContainer();
    setTree(paramJTree);
    setBorderSelectionColor(UIManager.getColor("Tree.editorBorderSelectionColor"));
  }
  
  public void setBorderSelectionColor(Color paramColor)
  {
    borderSelectionColor = paramColor;
  }
  
  public Color getBorderSelectionColor()
  {
    return borderSelectionColor;
  }
  
  public void setFont(Font paramFont)
  {
    font = paramFont;
  }
  
  public Font getFont()
  {
    return font;
  }
  
  public Component getTreeCellEditorComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    setTree(paramJTree);
    lastRow = paramInt;
    determineOffset(paramJTree, paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt);
    if (editingComponent != null) {
      editingContainer.remove(editingComponent);
    }
    editingComponent = realEditor.getTreeCellEditorComponent(paramJTree, paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt);
    TreePath localTreePath = paramJTree.getPathForRow(paramInt);
    canEdit = ((lastPath != null) && (localTreePath != null) && (lastPath.equals(localTreePath)));
    Font localFont = getFont();
    if (localFont == null)
    {
      if (renderer != null) {
        localFont = renderer.getFont();
      }
      if (localFont == null) {
        localFont = paramJTree.getFont();
      }
    }
    editingContainer.setFont(localFont);
    prepareForEditing();
    return editingContainer;
  }
  
  public Object getCellEditorValue()
  {
    return realEditor.getCellEditorValue();
  }
  
  public boolean isCellEditable(EventObject paramEventObject)
  {
    boolean bool1 = false;
    int i = 0;
    if ((paramEventObject != null) && ((paramEventObject.getSource() instanceof JTree)))
    {
      setTree((JTree)paramEventObject.getSource());
      if ((paramEventObject instanceof MouseEvent))
      {
        TreePath localTreePath = tree.getPathForLocation(((MouseEvent)paramEventObject).getX(), ((MouseEvent)paramEventObject).getY());
        i = (lastPath != null) && (localTreePath != null) && (lastPath.equals(localTreePath)) ? 1 : 0;
        if (localTreePath != null)
        {
          lastRow = tree.getRowForPath(localTreePath);
          Object localObject = localTreePath.getLastPathComponent();
          boolean bool2 = tree.isRowSelected(lastRow);
          boolean bool3 = tree.isExpanded(localTreePath);
          TreeModel localTreeModel = tree.getModel();
          boolean bool4 = localTreeModel.isLeaf(localObject);
          determineOffset(tree, localObject, bool2, bool3, bool4, lastRow);
        }
      }
    }
    if (!realEditor.isCellEditable(paramEventObject)) {
      return false;
    }
    if (canEditImmediately(paramEventObject)) {
      bool1 = true;
    } else if ((i != 0) && (shouldStartEditingTimer(paramEventObject))) {
      startEditingTimer();
    } else if ((timer != null) && (timer.isRunning())) {
      timer.stop();
    }
    if (bool1) {
      prepareForEditing();
    }
    return bool1;
  }
  
  public boolean shouldSelectCell(EventObject paramEventObject)
  {
    return realEditor.shouldSelectCell(paramEventObject);
  }
  
  public boolean stopCellEditing()
  {
    if (realEditor.stopCellEditing())
    {
      cleanupAfterEditing();
      return true;
    }
    return false;
  }
  
  public void cancelCellEditing()
  {
    realEditor.cancelCellEditing();
    cleanupAfterEditing();
  }
  
  public void addCellEditorListener(CellEditorListener paramCellEditorListener)
  {
    realEditor.addCellEditorListener(paramCellEditorListener);
  }
  
  public void removeCellEditorListener(CellEditorListener paramCellEditorListener)
  {
    realEditor.removeCellEditorListener(paramCellEditorListener);
  }
  
  public CellEditorListener[] getCellEditorListeners()
  {
    return ((DefaultCellEditor)realEditor).getCellEditorListeners();
  }
  
  public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
  {
    if (tree != null) {
      if (tree.getSelectionCount() == 1) {
        lastPath = tree.getSelectionPath();
      } else {
        lastPath = null;
      }
    }
    if (timer != null) {
      timer.stop();
    }
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    if ((tree != null) && (lastPath != null)) {
      tree.startEditingAtPath(lastPath);
    }
  }
  
  protected void setTree(JTree paramJTree)
  {
    if (tree != paramJTree)
    {
      if (tree != null) {
        tree.removeTreeSelectionListener(this);
      }
      tree = paramJTree;
      if (tree != null) {
        tree.addTreeSelectionListener(this);
      }
      if (timer != null) {
        timer.stop();
      }
    }
  }
  
  protected boolean shouldStartEditingTimer(EventObject paramEventObject)
  {
    if (((paramEventObject instanceof MouseEvent)) && (SwingUtilities.isLeftMouseButton((MouseEvent)paramEventObject)))
    {
      MouseEvent localMouseEvent = (MouseEvent)paramEventObject;
      return (localMouseEvent.getClickCount() == 1) && (inHitRegion(localMouseEvent.getX(), localMouseEvent.getY()));
    }
    return false;
  }
  
  protected void startEditingTimer()
  {
    if (timer == null)
    {
      timer = new Timer(1200, this);
      timer.setRepeats(false);
    }
    timer.start();
  }
  
  protected boolean canEditImmediately(EventObject paramEventObject)
  {
    if (((paramEventObject instanceof MouseEvent)) && (SwingUtilities.isLeftMouseButton((MouseEvent)paramEventObject)))
    {
      MouseEvent localMouseEvent = (MouseEvent)paramEventObject;
      return (localMouseEvent.getClickCount() > 2) && (inHitRegion(localMouseEvent.getX(), localMouseEvent.getY()));
    }
    return paramEventObject == null;
  }
  
  protected boolean inHitRegion(int paramInt1, int paramInt2)
  {
    if ((lastRow != -1) && (tree != null))
    {
      Rectangle localRectangle = tree.getRowBounds(lastRow);
      ComponentOrientation localComponentOrientation = tree.getComponentOrientation();
      if (localComponentOrientation.isLeftToRight())
      {
        if ((localRectangle != null) && (paramInt1 <= x + offset) && (offset < width - 5)) {
          return false;
        }
      }
      else if ((localRectangle != null) && ((paramInt1 >= x + width - offset + 5) || (paramInt1 <= x + 5)) && (offset < width - 5)) {
        return false;
      }
    }
    return true;
  }
  
  protected void determineOffset(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    if (renderer != null)
    {
      if (paramBoolean3) {
        editingIcon = renderer.getLeafIcon();
      } else if (paramBoolean2) {
        editingIcon = renderer.getOpenIcon();
      } else {
        editingIcon = renderer.getClosedIcon();
      }
      if (editingIcon != null) {
        offset = (renderer.getIconTextGap() + editingIcon.getIconWidth());
      } else {
        offset = renderer.getIconTextGap();
      }
    }
    else
    {
      editingIcon = null;
      offset = 0;
    }
  }
  
  protected void prepareForEditing()
  {
    if (editingComponent != null) {
      editingContainer.add(editingComponent);
    }
  }
  
  protected Container createContainer()
  {
    return new EditorContainer();
  }
  
  protected TreeCellEditor createTreeCellEditor()
  {
    Border localBorder = UIManager.getBorder("Tree.editorBorder");
    DefaultCellEditor local1 = new DefaultCellEditor(new DefaultTextField(localBorder))
    {
      public boolean shouldSelectCell(EventObject paramAnonymousEventObject)
      {
        boolean bool = super.shouldSelectCell(paramAnonymousEventObject);
        return bool;
      }
    };
    local1.setClickCountToStart(1);
    return local1;
  }
  
  private void cleanupAfterEditing()
  {
    if (editingComponent != null) {
      editingContainer.remove(editingComponent);
    }
    editingComponent = null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Vector localVector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if ((realEditor != null) && ((realEditor instanceof Serializable)))
    {
      localVector.addElement("realEditor");
      localVector.addElement(realEditor);
    }
    paramObjectOutputStream.writeObject(localVector);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Vector localVector = (Vector)paramObjectInputStream.readObject();
    int i = 0;
    int j = localVector.size();
    if ((i < j) && (localVector.elementAt(i).equals("realEditor")))
    {
      realEditor = ((TreeCellEditor)localVector.elementAt(++i));
      i++;
    }
  }
  
  public class DefaultTextField
    extends JTextField
  {
    protected Border border;
    
    public DefaultTextField(Border paramBorder)
    {
      setBorder(paramBorder);
    }
    
    public void setBorder(Border paramBorder)
    {
      super.setBorder(paramBorder);
      border = paramBorder;
    }
    
    public Border getBorder()
    {
      return border;
    }
    
    public Font getFont()
    {
      Font localFont = super.getFont();
      if ((localFont instanceof FontUIResource))
      {
        Container localContainer = getParent();
        if ((localContainer != null) && (localContainer.getFont() != null)) {
          localFont = localContainer.getFont();
        }
      }
      return localFont;
    }
    
    public Dimension getPreferredSize()
    {
      Dimension localDimension1 = super.getPreferredSize();
      if ((renderer != null) && (DefaultTreeCellEditor.this.getFont() == null))
      {
        Dimension localDimension2 = renderer.getPreferredSize();
        height = height;
      }
      return localDimension1;
    }
  }
  
  public class EditorContainer
    extends Container
  {
    public EditorContainer()
    {
      setLayout(null);
    }
    
    public void EditorContainer()
    {
      setLayout(null);
    }
    
    public void paint(Graphics paramGraphics)
    {
      int i = getWidth();
      int j = getHeight();
      if (editingIcon != null)
      {
        int k = calculateIconY(editingIcon);
        if (getComponentOrientation().isLeftToRight()) {
          editingIcon.paintIcon(this, paramGraphics, 0, k);
        } else {
          editingIcon.paintIcon(this, paramGraphics, i - editingIcon.getIconWidth(), k);
        }
      }
      Color localColor = getBorderSelectionColor();
      if (localColor != null)
      {
        paramGraphics.setColor(localColor);
        paramGraphics.drawRect(0, 0, i - 1, j - 1);
      }
      super.paint(paramGraphics);
    }
    
    public void doLayout()
    {
      if (editingComponent != null)
      {
        int i = getWidth();
        int j = getHeight();
        if (getComponentOrientation().isLeftToRight()) {
          editingComponent.setBounds(offset, 0, i - offset, j);
        } else {
          editingComponent.setBounds(0, 0, i - offset, j);
        }
      }
    }
    
    private int calculateIconY(Icon paramIcon)
    {
      int i = paramIcon.getIconHeight();
      int j = editingComponent.getFontMetrics(editingComponent.getFont()).getHeight();
      int k = i / 2 - j / 2;
      int m = Math.min(0, k);
      int n = Math.max(i, k + j) - m;
      return getHeight() / 2 - (m + n / 2);
    }
    
    public Dimension getPreferredSize()
    {
      if (editingComponent != null)
      {
        Dimension localDimension = editingComponent.getPreferredSize();
        width += offset + 5;
        Object localObject = renderer != null ? renderer.getPreferredSize() : null;
        if (localObject != null) {
          height = Math.max(height, height);
        }
        if (editingIcon != null) {
          height = Math.max(height, editingIcon.getIconHeight());
        }
        width = Math.max(width, 100);
        return localDimension;
      }
      return new Dimension(0, 0);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\DefaultTreeCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */