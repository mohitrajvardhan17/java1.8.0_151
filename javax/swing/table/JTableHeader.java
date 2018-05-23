package javax.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;
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
import javax.swing.JComponent;
import javax.swing.JComponent.AccessibleJComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class JTableHeader
  extends JComponent
  implements TableColumnModelListener, Accessible
{
  private static final String uiClassID = "TableHeaderUI";
  protected JTable table;
  protected TableColumnModel columnModel;
  protected boolean reorderingAllowed;
  protected boolean resizingAllowed;
  protected boolean updateTableInRealTime;
  protected transient TableColumn resizingColumn;
  protected transient TableColumn draggedColumn;
  protected transient int draggedDistance;
  private TableCellRenderer defaultRenderer;
  
  public JTableHeader()
  {
    this(null);
  }
  
  public JTableHeader(TableColumnModel paramTableColumnModel)
  {
    if (paramTableColumnModel == null) {
      paramTableColumnModel = createDefaultColumnModel();
    }
    setColumnModel(paramTableColumnModel);
    initializeLocalVars();
    updateUI();
  }
  
  public void setTable(JTable paramJTable)
  {
    JTable localJTable = table;
    table = paramJTable;
    firePropertyChange("table", localJTable, paramJTable);
  }
  
  public JTable getTable()
  {
    return table;
  }
  
  public void setReorderingAllowed(boolean paramBoolean)
  {
    boolean bool = reorderingAllowed;
    reorderingAllowed = paramBoolean;
    firePropertyChange("reorderingAllowed", bool, paramBoolean);
  }
  
  public boolean getReorderingAllowed()
  {
    return reorderingAllowed;
  }
  
  public void setResizingAllowed(boolean paramBoolean)
  {
    boolean bool = resizingAllowed;
    resizingAllowed = paramBoolean;
    firePropertyChange("resizingAllowed", bool, paramBoolean);
  }
  
  public boolean getResizingAllowed()
  {
    return resizingAllowed;
  }
  
  public TableColumn getDraggedColumn()
  {
    return draggedColumn;
  }
  
  public int getDraggedDistance()
  {
    return draggedDistance;
  }
  
  public TableColumn getResizingColumn()
  {
    return resizingColumn;
  }
  
  public void setUpdateTableInRealTime(boolean paramBoolean)
  {
    updateTableInRealTime = paramBoolean;
  }
  
  public boolean getUpdateTableInRealTime()
  {
    return updateTableInRealTime;
  }
  
  public void setDefaultRenderer(TableCellRenderer paramTableCellRenderer)
  {
    defaultRenderer = paramTableCellRenderer;
  }
  
  @Transient
  public TableCellRenderer getDefaultRenderer()
  {
    return defaultRenderer;
  }
  
  public int columnAtPoint(Point paramPoint)
  {
    int i = x;
    if (!getComponentOrientation().isLeftToRight()) {
      i = getWidthInRightToLeft() - i - 1;
    }
    return getColumnModel().getColumnIndexAtX(i);
  }
  
  public Rectangle getHeaderRect(int paramInt)
  {
    Rectangle localRectangle = new Rectangle();
    TableColumnModel localTableColumnModel = getColumnModel();
    height = getHeight();
    if (paramInt < 0)
    {
      if (!getComponentOrientation().isLeftToRight()) {
        x = getWidthInRightToLeft();
      }
    }
    else if (paramInt >= localTableColumnModel.getColumnCount())
    {
      if (getComponentOrientation().isLeftToRight()) {
        x = getWidth();
      }
    }
    else
    {
      for (int i = 0; i < paramInt; i++) {
        x += localTableColumnModel.getColumn(i).getWidth();
      }
      if (!getComponentOrientation().isLeftToRight()) {
        x = (getWidthInRightToLeft() - x - localTableColumnModel.getColumn(paramInt).getWidth());
      }
      width = localTableColumnModel.getColumn(paramInt).getWidth();
    }
    return localRectangle;
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent)
  {
    String str = null;
    Point localPoint = paramMouseEvent.getPoint();
    int i;
    if ((i = columnAtPoint(localPoint)) != -1)
    {
      TableColumn localTableColumn = columnModel.getColumn(i);
      TableCellRenderer localTableCellRenderer = localTableColumn.getHeaderRenderer();
      if (localTableCellRenderer == null) {
        localTableCellRenderer = defaultRenderer;
      }
      Component localComponent = localTableCellRenderer.getTableCellRendererComponent(getTable(), localTableColumn.getHeaderValue(), false, false, -1, i);
      if ((localComponent instanceof JComponent))
      {
        Rectangle localRectangle = getHeaderRect(i);
        localPoint.translate(-x, -y);
        MouseEvent localMouseEvent = new MouseEvent(localComponent, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), x, y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
        str = ((JComponent)localComponent).getToolTipText(localMouseEvent);
      }
    }
    if (str == null) {
      str = getToolTipText();
    }
    return str;
  }
  
  public TableHeaderUI getUI()
  {
    return (TableHeaderUI)ui;
  }
  
  public void setUI(TableHeaderUI paramTableHeaderUI)
  {
    if (ui != paramTableHeaderUI)
    {
      super.setUI(paramTableHeaderUI);
      repaint();
    }
  }
  
  public void updateUI()
  {
    setUI((TableHeaderUI)UIManager.getUI(this));
    TableCellRenderer localTableCellRenderer = getDefaultRenderer();
    if ((localTableCellRenderer instanceof Component)) {
      SwingUtilities.updateComponentTreeUI((Component)localTableCellRenderer);
    }
  }
  
  public String getUIClassID()
  {
    return "TableHeaderUI";
  }
  
  public void setColumnModel(TableColumnModel paramTableColumnModel)
  {
    if (paramTableColumnModel == null) {
      throw new IllegalArgumentException("Cannot set a null ColumnModel");
    }
    TableColumnModel localTableColumnModel = columnModel;
    if (paramTableColumnModel != localTableColumnModel)
    {
      if (localTableColumnModel != null) {
        localTableColumnModel.removeColumnModelListener(this);
      }
      columnModel = paramTableColumnModel;
      paramTableColumnModel.addColumnModelListener(this);
      firePropertyChange("columnModel", localTableColumnModel, paramTableColumnModel);
      resizeAndRepaint();
    }
  }
  
  public TableColumnModel getColumnModel()
  {
    return columnModel;
  }
  
  public void columnAdded(TableColumnModelEvent paramTableColumnModelEvent)
  {
    resizeAndRepaint();
  }
  
  public void columnRemoved(TableColumnModelEvent paramTableColumnModelEvent)
  {
    resizeAndRepaint();
  }
  
  public void columnMoved(TableColumnModelEvent paramTableColumnModelEvent)
  {
    repaint();
  }
  
  public void columnMarginChanged(ChangeEvent paramChangeEvent)
  {
    resizeAndRepaint();
  }
  
  public void columnSelectionChanged(ListSelectionEvent paramListSelectionEvent) {}
  
  protected TableColumnModel createDefaultColumnModel()
  {
    return new DefaultTableColumnModel();
  }
  
  protected TableCellRenderer createDefaultRenderer()
  {
    return new DefaultTableCellHeaderRenderer();
  }
  
  protected void initializeLocalVars()
  {
    setOpaque(true);
    table = null;
    reorderingAllowed = true;
    resizingAllowed = true;
    draggedColumn = null;
    draggedDistance = 0;
    resizingColumn = null;
    updateTableInRealTime = true;
    ToolTipManager localToolTipManager = ToolTipManager.sharedInstance();
    localToolTipManager.registerComponent(this);
    setDefaultRenderer(createDefaultRenderer());
  }
  
  public void resizeAndRepaint()
  {
    revalidate();
    repaint();
  }
  
  public void setDraggedColumn(TableColumn paramTableColumn)
  {
    draggedColumn = paramTableColumn;
  }
  
  public void setDraggedDistance(int paramInt)
  {
    draggedDistance = paramInt;
  }
  
  public void setResizingColumn(TableColumn paramTableColumn)
  {
    resizingColumn = paramTableColumn;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if ((ui != null) && (getUIClassID().equals("TableHeaderUI"))) {
      ui.installUI(this);
    }
  }
  
  private int getWidthInRightToLeft()
  {
    if ((table != null) && (table.getAutoResizeMode() != 0)) {
      return table.getWidth();
    }
    return super.getWidth();
  }
  
  protected String paramString()
  {
    String str1 = reorderingAllowed ? "true" : "false";
    String str2 = resizingAllowed ? "true" : "false";
    String str3 = updateTableInRealTime ? "true" : "false";
    return super.paramString() + ",draggedDistance=" + draggedDistance + ",reorderingAllowed=" + str1 + ",resizingAllowed=" + str2 + ",updateTableInRealTime=" + str3;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJTableHeader();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJTableHeader
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJTableHeader()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PANEL;
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      int i;
      if ((i = columnAtPoint(paramPoint)) != -1)
      {
        TableColumn localTableColumn = columnModel.getColumn(i);
        TableCellRenderer localTableCellRenderer = localTableColumn.getHeaderRenderer();
        if (localTableCellRenderer == null) {
          if (defaultRenderer != null) {
            localTableCellRenderer = defaultRenderer;
          } else {
            return null;
          }
        }
        Component localComponent = localTableCellRenderer.getTableCellRendererComponent(getTable(), localTableColumn.getHeaderValue(), false, false, -1, i);
        return new AccessibleJTableHeaderEntry(i, JTableHeader.this, table);
      }
      return null;
    }
    
    public int getAccessibleChildrenCount()
    {
      return columnModel.getColumnCount();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= getAccessibleChildrenCount())) {
        return null;
      }
      TableColumn localTableColumn = columnModel.getColumn(paramInt);
      TableCellRenderer localTableCellRenderer = localTableColumn.getHeaderRenderer();
      if (localTableCellRenderer == null) {
        if (defaultRenderer != null) {
          localTableCellRenderer = defaultRenderer;
        } else {
          return null;
        }
      }
      Component localComponent = localTableCellRenderer.getTableCellRendererComponent(getTable(), localTableColumn.getHeaderValue(), false, false, -1, paramInt);
      return new AccessibleJTableHeaderEntry(paramInt, JTableHeader.this, table);
    }
    
    protected class AccessibleJTableHeaderEntry
      extends AccessibleContext
      implements Accessible, AccessibleComponent
    {
      private JTableHeader parent;
      private int column;
      private JTable table;
      
      public AccessibleJTableHeaderEntry(int paramInt, JTableHeader paramJTableHeader, JTable paramJTable)
      {
        parent = paramJTableHeader;
        column = paramInt;
        table = paramJTable;
        setAccessibleParent(parent);
      }
      
      public AccessibleContext getAccessibleContext()
      {
        return this;
      }
      
      private AccessibleContext getCurrentAccessibleContext()
      {
        TableColumnModel localTableColumnModel = table.getColumnModel();
        if (localTableColumnModel != null)
        {
          if ((column < 0) || (column >= localTableColumnModel.getColumnCount())) {
            return null;
          }
          TableColumn localTableColumn = localTableColumnModel.getColumn(column);
          TableCellRenderer localTableCellRenderer = localTableColumn.getHeaderRenderer();
          if (localTableCellRenderer == null) {
            if (defaultRenderer != null) {
              localTableCellRenderer = defaultRenderer;
            } else {
              return null;
            }
          }
          Component localComponent = localTableCellRenderer.getTableCellRendererComponent(getTable(), localTableColumn.getHeaderValue(), false, false, -1, column);
          if ((localComponent instanceof Accessible)) {
            return ((Accessible)localComponent).getAccessibleContext();
          }
        }
        return null;
      }
      
      private Component getCurrentComponent()
      {
        TableColumnModel localTableColumnModel = table.getColumnModel();
        if (localTableColumnModel != null)
        {
          if ((column < 0) || (column >= localTableColumnModel.getColumnCount())) {
            return null;
          }
          TableColumn localTableColumn = localTableColumnModel.getColumn(column);
          TableCellRenderer localTableCellRenderer = localTableColumn.getHeaderRenderer();
          if (localTableCellRenderer == null) {
            if (defaultRenderer != null) {
              localTableCellRenderer = defaultRenderer;
            } else {
              return null;
            }
          }
          return localTableCellRenderer.getTableCellRendererComponent(getTable(), localTableColumn.getHeaderValue(), false, false, -1, column);
        }
        return null;
      }
      
      public String getAccessibleName()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null)
        {
          str = localAccessibleContext.getAccessibleName();
          if ((str != null) && (str != "")) {
            return str;
          }
        }
        if ((accessibleName != null) && (accessibleName != "")) {
          return accessibleName;
        }
        String str = (String)getClientProperty("AccessibleName");
        if (str != null) {
          return str;
        }
        return table.getColumnName(column);
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
        return AccessibleRole.COLUMN_HEADER;
      }
      
      public AccessibleStateSet getAccessibleStateSet()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null)
        {
          AccessibleStateSet localAccessibleStateSet = localAccessibleContext.getAccessibleStateSet();
          if (isShowing()) {
            localAccessibleStateSet.add(AccessibleState.SHOWING);
          }
          return localAccessibleStateSet;
        }
        return new AccessibleStateSet();
      }
      
      public int getAccessibleIndexInParent()
      {
        return column;
      }
      
      public int getAccessibleChildrenCount()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleChildrenCount();
        }
        return 0;
      }
      
      public Accessible getAccessibleChild(int paramInt)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null)
        {
          Accessible localAccessible = localAccessibleContext.getAccessibleChild(paramInt);
          localAccessibleContext.setAccessibleParent(this);
          return localAccessible;
        }
        return null;
      }
      
      public Locale getLocale()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getLocale();
        }
        return null;
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
        return getCurrentAccessibleContext().getAccessibleAction();
      }
      
      public AccessibleComponent getAccessibleComponent()
      {
        return this;
      }
      
      public AccessibleSelection getAccessibleSelection()
      {
        return getCurrentAccessibleContext().getAccessibleSelection();
      }
      
      public AccessibleText getAccessibleText()
      {
        return getCurrentAccessibleContext().getAccessibleText();
      }
      
      public AccessibleValue getAccessibleValue()
      {
        return getCurrentAccessibleContext().getAccessibleValue();
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
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).isVisible();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.isVisible();
        }
        return false;
      }
      
      public void setVisible(boolean paramBoolean)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setVisible(paramBoolean);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setVisible(paramBoolean);
          }
        }
      }
      
      public boolean isShowing()
      {
        return (isVisible()) && (JTableHeader.this.isShowing());
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
        if (parent != null)
        {
          Point localPoint1 = parent.getLocationOnScreen();
          Point localPoint2 = getLocation();
          localPoint2.translate(x, y);
          return localPoint2;
        }
        return null;
      }
      
      public Point getLocation()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          localObject = ((AccessibleComponent)localAccessibleContext).getBounds();
          return ((Rectangle)localObject).getLocation();
        }
        Object localObject = getCurrentComponent();
        if (localObject != null)
        {
          Rectangle localRectangle = ((Component)localObject).getBounds();
          return localRectangle.getLocation();
        }
        return getBounds().getLocation();
      }
      
      public void setLocation(Point paramPoint) {}
      
      public Rectangle getBounds()
      {
        Rectangle localRectangle = table.getCellRect(-1, column, false);
        y = 0;
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
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\table\JTableHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */