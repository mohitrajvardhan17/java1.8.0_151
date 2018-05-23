package javax.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTable.DropLocation;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;

public class DefaultTableCellRenderer
  extends JLabel
  implements TableCellRenderer, Serializable
{
  private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;
  private Color unselectedForeground;
  private Color unselectedBackground;
  
  public DefaultTableCellRenderer()
  {
    setOpaque(true);
    setBorder(getNoFocusBorder());
    setName("Table.cellRenderer");
  }
  
  private Border getNoFocusBorder()
  {
    Border localBorder = DefaultLookup.getBorder(this, ui, "Table.cellNoFocusBorder");
    if (System.getSecurityManager() != null)
    {
      if (localBorder != null) {
        return localBorder;
      }
      return SAFE_NO_FOCUS_BORDER;
    }
    if ((localBorder != null) && ((noFocusBorder == null) || (noFocusBorder == DEFAULT_NO_FOCUS_BORDER))) {
      return localBorder;
    }
    return noFocusBorder;
  }
  
  public void setForeground(Color paramColor)
  {
    super.setForeground(paramColor);
    unselectedForeground = paramColor;
  }
  
  public void setBackground(Color paramColor)
  {
    super.setBackground(paramColor);
    unselectedBackground = paramColor;
  }
  
  public void updateUI()
  {
    super.updateUI();
    setForeground(null);
    setBackground(null);
  }
  
  public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
  {
    if (paramJTable == null) {
      return this;
    }
    Color localColor1 = null;
    Color localColor2 = null;
    JTable.DropLocation localDropLocation = paramJTable.getDropLocation();
    if ((localDropLocation != null) && (!localDropLocation.isInsertRow()) && (!localDropLocation.isInsertColumn()) && (localDropLocation.getRow() == paramInt1) && (localDropLocation.getColumn() == paramInt2))
    {
      localColor1 = DefaultLookup.getColor(this, ui, "Table.dropCellForeground");
      localColor2 = DefaultLookup.getColor(this, ui, "Table.dropCellBackground");
      paramBoolean1 = true;
    }
    Object localObject;
    Color localColor3;
    if (paramBoolean1)
    {
      super.setForeground(localColor1 == null ? paramJTable.getSelectionForeground() : localColor1);
      super.setBackground(localColor2 == null ? paramJTable.getSelectionBackground() : localColor2);
    }
    else
    {
      localObject = unselectedBackground != null ? unselectedBackground : paramJTable.getBackground();
      if ((localObject == null) || ((localObject instanceof UIResource)))
      {
        localColor3 = DefaultLookup.getColor(this, ui, "Table.alternateRowColor");
        if ((localColor3 != null) && (paramInt1 % 2 != 0)) {
          localObject = localColor3;
        }
      }
      super.setForeground(unselectedForeground != null ? unselectedForeground : paramJTable.getForeground());
      super.setBackground((Color)localObject);
    }
    setFont(paramJTable.getFont());
    if (paramBoolean2)
    {
      localObject = null;
      if (paramBoolean1) {
        localObject = DefaultLookup.getBorder(this, ui, "Table.focusSelectedCellHighlightBorder");
      }
      if (localObject == null) {
        localObject = DefaultLookup.getBorder(this, ui, "Table.focusCellHighlightBorder");
      }
      setBorder((Border)localObject);
      if ((!paramBoolean1) && (paramJTable.isCellEditable(paramInt1, paramInt2)))
      {
        localColor3 = DefaultLookup.getColor(this, ui, "Table.focusCellForeground");
        if (localColor3 != null) {
          super.setForeground(localColor3);
        }
        localColor3 = DefaultLookup.getColor(this, ui, "Table.focusCellBackground");
        if (localColor3 != null) {
          super.setBackground(localColor3);
        }
      }
    }
    else
    {
      setBorder(getNoFocusBorder());
    }
    setValue(paramObject);
    return this;
  }
  
  public boolean isOpaque()
  {
    Color localColor = getBackground();
    Container localContainer = getParent();
    if (localContainer != null) {
      localContainer = localContainer.getParent();
    }
    int i = (localColor != null) && (localContainer != null) && (localColor.equals(localContainer.getBackground())) && (localContainer.isOpaque()) ? 1 : 0;
    return (i == 0) && (super.isOpaque());
  }
  
  public void invalidate() {}
  
  public void validate() {}
  
  public void revalidate() {}
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void repaint(Rectangle paramRectangle) {}
  
  public void repaint() {}
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if ((paramString == "text") || (paramString == "labelFor") || (paramString == "displayedMnemonic") || (((paramString == "font") || (paramString == "foreground")) && (paramObject1 != paramObject2) && (getClientProperty("html") != null))) {
      super.firePropertyChange(paramString, paramObject1, paramObject2);
    }
  }
  
  public void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) {}
  
  protected void setValue(Object paramObject)
  {
    setText(paramObject == null ? "" : paramObject.toString());
  }
  
  public static class UIResource
    extends DefaultTableCellRenderer
    implements UIResource
  {
    public UIResource() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\table\DefaultTableCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */