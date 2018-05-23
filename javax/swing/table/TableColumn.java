package javax.swing.table;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.SwingPropertyChangeSupport;

public class TableColumn
  implements Serializable
{
  public static final String COLUMN_WIDTH_PROPERTY = "columWidth";
  public static final String HEADER_VALUE_PROPERTY = "headerValue";
  public static final String HEADER_RENDERER_PROPERTY = "headerRenderer";
  public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
  protected int modelIndex;
  protected Object identifier;
  protected int width;
  protected int minWidth;
  private int preferredWidth;
  protected int maxWidth;
  protected TableCellRenderer headerRenderer;
  protected Object headerValue;
  protected TableCellRenderer cellRenderer;
  protected TableCellEditor cellEditor;
  protected boolean isResizable;
  @Deprecated
  protected transient int resizedPostingDisableCount;
  private SwingPropertyChangeSupport changeSupport;
  
  public TableColumn()
  {
    this(0);
  }
  
  public TableColumn(int paramInt)
  {
    this(paramInt, 75, null, null);
  }
  
  public TableColumn(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, null, null);
  }
  
  public TableColumn(int paramInt1, int paramInt2, TableCellRenderer paramTableCellRenderer, TableCellEditor paramTableCellEditor)
  {
    modelIndex = paramInt1;
    preferredWidth = (width = Math.max(paramInt2, 0));
    cellRenderer = paramTableCellRenderer;
    cellEditor = paramTableCellEditor;
    minWidth = Math.min(15, width);
    maxWidth = Integer.MAX_VALUE;
    isResizable = true;
    resizedPostingDisableCount = 0;
    headerValue = null;
  }
  
  private void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if (changeSupport != null) {
      changeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
    }
  }
  
  private void firePropertyChange(String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2) {
      firePropertyChange(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    }
  }
  
  private void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 != paramBoolean2) {
      firePropertyChange(paramString, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2));
    }
  }
  
  public void setModelIndex(int paramInt)
  {
    int i = modelIndex;
    modelIndex = paramInt;
    firePropertyChange("modelIndex", i, paramInt);
  }
  
  public int getModelIndex()
  {
    return modelIndex;
  }
  
  public void setIdentifier(Object paramObject)
  {
    Object localObject = identifier;
    identifier = paramObject;
    firePropertyChange("identifier", localObject, paramObject);
  }
  
  public Object getIdentifier()
  {
    return identifier != null ? identifier : getHeaderValue();
  }
  
  public void setHeaderValue(Object paramObject)
  {
    Object localObject = headerValue;
    headerValue = paramObject;
    firePropertyChange("headerValue", localObject, paramObject);
  }
  
  public Object getHeaderValue()
  {
    return headerValue;
  }
  
  public void setHeaderRenderer(TableCellRenderer paramTableCellRenderer)
  {
    TableCellRenderer localTableCellRenderer = headerRenderer;
    headerRenderer = paramTableCellRenderer;
    firePropertyChange("headerRenderer", localTableCellRenderer, paramTableCellRenderer);
  }
  
  public TableCellRenderer getHeaderRenderer()
  {
    return headerRenderer;
  }
  
  public void setCellRenderer(TableCellRenderer paramTableCellRenderer)
  {
    TableCellRenderer localTableCellRenderer = cellRenderer;
    cellRenderer = paramTableCellRenderer;
    firePropertyChange("cellRenderer", localTableCellRenderer, paramTableCellRenderer);
  }
  
  public TableCellRenderer getCellRenderer()
  {
    return cellRenderer;
  }
  
  public void setCellEditor(TableCellEditor paramTableCellEditor)
  {
    TableCellEditor localTableCellEditor = cellEditor;
    cellEditor = paramTableCellEditor;
    firePropertyChange("cellEditor", localTableCellEditor, paramTableCellEditor);
  }
  
  public TableCellEditor getCellEditor()
  {
    return cellEditor;
  }
  
  public void setWidth(int paramInt)
  {
    int i = width;
    width = Math.min(Math.max(paramInt, minWidth), maxWidth);
    firePropertyChange("width", i, width);
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public void setPreferredWidth(int paramInt)
  {
    int i = preferredWidth;
    preferredWidth = Math.min(Math.max(paramInt, minWidth), maxWidth);
    firePropertyChange("preferredWidth", i, preferredWidth);
  }
  
  public int getPreferredWidth()
  {
    return preferredWidth;
  }
  
  public void setMinWidth(int paramInt)
  {
    int i = minWidth;
    minWidth = Math.max(Math.min(paramInt, maxWidth), 0);
    if (width < minWidth) {
      setWidth(minWidth);
    }
    if (preferredWidth < minWidth) {
      setPreferredWidth(minWidth);
    }
    firePropertyChange("minWidth", i, minWidth);
  }
  
  public int getMinWidth()
  {
    return minWidth;
  }
  
  public void setMaxWidth(int paramInt)
  {
    int i = maxWidth;
    maxWidth = Math.max(minWidth, paramInt);
    if (width > maxWidth) {
      setWidth(maxWidth);
    }
    if (preferredWidth > maxWidth) {
      setPreferredWidth(maxWidth);
    }
    firePropertyChange("maxWidth", i, maxWidth);
  }
  
  public int getMaxWidth()
  {
    return maxWidth;
  }
  
  public void setResizable(boolean paramBoolean)
  {
    boolean bool = isResizable;
    isResizable = paramBoolean;
    firePropertyChange("isResizable", bool, isResizable);
  }
  
  public boolean getResizable()
  {
    return isResizable;
  }
  
  public void sizeWidthToFit()
  {
    if (headerRenderer == null) {
      return;
    }
    Component localComponent = headerRenderer.getTableCellRendererComponent(null, getHeaderValue(), false, false, 0, 0);
    setMinWidth(getMinimumSizewidth);
    setMaxWidth(getMaximumSizewidth);
    setPreferredWidth(getPreferredSizewidth);
    setWidth(getPreferredWidth());
  }
  
  @Deprecated
  public void disableResizedPosting()
  {
    resizedPostingDisableCount += 1;
  }
  
  @Deprecated
  public void enableResizedPosting()
  {
    resizedPostingDisableCount -= 1;
  }
  
  public synchronized void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (changeSupport == null) {
      changeSupport = new SwingPropertyChangeSupport(this);
    }
    changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public synchronized void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (changeSupport != null) {
      changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
    }
  }
  
  public synchronized PropertyChangeListener[] getPropertyChangeListeners()
  {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners();
  }
  
  protected TableCellRenderer createDefaultHeaderRenderer()
  {
    DefaultTableCellRenderer local1 = new DefaultTableCellRenderer()
    {
      public Component getTableCellRendererComponent(JTable paramAnonymousJTable, Object paramAnonymousObject, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (paramAnonymousJTable != null)
        {
          JTableHeader localJTableHeader = paramAnonymousJTable.getTableHeader();
          if (localJTableHeader != null)
          {
            setForeground(localJTableHeader.getForeground());
            setBackground(localJTableHeader.getBackground());
            setFont(localJTableHeader.getFont());
          }
        }
        setText(paramAnonymousObject == null ? "" : paramAnonymousObject.toString());
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return this;
      }
    };
    local1.setHorizontalAlignment(0);
    return local1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\table\TableColumn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */