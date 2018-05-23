package sun.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import sun.swing.DefaultLookup;

public class DefaultTableCellHeaderRenderer
  extends DefaultTableCellRenderer
  implements UIResource
{
  private boolean horizontalTextPositionSet;
  private Icon sortArrow;
  private EmptyIcon emptyIcon = new EmptyIcon(null);
  
  public DefaultTableCellHeaderRenderer()
  {
    setHorizontalAlignment(0);
  }
  
  public void setHorizontalTextPosition(int paramInt)
  {
    horizontalTextPositionSet = true;
    super.setHorizontalTextPosition(paramInt);
  }
  
  public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
  {
    Icon localIcon = null;
    boolean bool = false;
    if (paramJTable != null)
    {
      localObject1 = paramJTable.getTableHeader();
      Object localObject2;
      if (localObject1 != null)
      {
        localObject2 = null;
        Color localColor = null;
        if (paramBoolean2)
        {
          localObject2 = DefaultLookup.getColor(this, ui, "TableHeader.focusCellForeground");
          localColor = DefaultLookup.getColor(this, ui, "TableHeader.focusCellBackground");
        }
        if (localObject2 == null) {
          localObject2 = ((JTableHeader)localObject1).getForeground();
        }
        if (localColor == null) {
          localColor = ((JTableHeader)localObject1).getBackground();
        }
        setForeground((Color)localObject2);
        setBackground(localColor);
        setFont(((JTableHeader)localObject1).getFont());
        bool = ((JTableHeader)localObject1).isPaintingForPrint();
      }
      if ((!bool) && (paramJTable.getRowSorter() != null))
      {
        if (!horizontalTextPositionSet) {
          setHorizontalTextPosition(10);
        }
        localObject2 = getColumnSortOrder(paramJTable, paramInt2);
        if (localObject2 != null) {
          switch (localObject2)
          {
          case ASCENDING: 
            localIcon = DefaultLookup.getIcon(this, ui, "Table.ascendingSortIcon");
            break;
          case DESCENDING: 
            localIcon = DefaultLookup.getIcon(this, ui, "Table.descendingSortIcon");
            break;
          case UNSORTED: 
            localIcon = DefaultLookup.getIcon(this, ui, "Table.naturalSortIcon");
          }
        }
      }
    }
    setText(paramObject == null ? "" : paramObject.toString());
    setIcon(localIcon);
    sortArrow = localIcon;
    Object localObject1 = null;
    if (paramBoolean2) {
      localObject1 = DefaultLookup.getBorder(this, ui, "TableHeader.focusCellBorder");
    }
    if (localObject1 == null) {
      localObject1 = DefaultLookup.getBorder(this, ui, "TableHeader.cellBorder");
    }
    setBorder((Border)localObject1);
    return this;
  }
  
  public static SortOrder getColumnSortOrder(JTable paramJTable, int paramInt)
  {
    SortOrder localSortOrder = null;
    if ((paramJTable == null) || (paramJTable.getRowSorter() == null)) {
      return localSortOrder;
    }
    List localList = paramJTable.getRowSorter().getSortKeys();
    if ((localList.size() > 0) && (((RowSorter.SortKey)localList.get(0)).getColumn() == paramJTable.convertColumnIndexToModel(paramInt))) {
      localSortOrder = ((RowSorter.SortKey)localList.get(0)).getSortOrder();
    }
    return localSortOrder;
  }
  
  public void paintComponent(Graphics paramGraphics)
  {
    boolean bool = DefaultLookup.getBoolean(this, ui, "TableHeader.rightAlignSortArrow", false);
    if ((bool) && (sortArrow != null))
    {
      emptyIcon.width = sortArrow.getIconWidth();
      emptyIcon.height = sortArrow.getIconHeight();
      setIcon(emptyIcon);
      super.paintComponent(paramGraphics);
      Point localPoint = computeIconPosition(paramGraphics);
      sortArrow.paintIcon(this, paramGraphics, x, y);
    }
    else
    {
      super.paintComponent(paramGraphics);
    }
  }
  
  private Point computeIconPosition(Graphics paramGraphics)
  {
    FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
    Rectangle localRectangle1 = new Rectangle();
    Rectangle localRectangle2 = new Rectangle();
    Rectangle localRectangle3 = new Rectangle();
    Insets localInsets = getInsets();
    x = left;
    y = top;
    width = (getWidth() - (left + right));
    height = (getHeight() - (top + bottom));
    SwingUtilities.layoutCompoundLabel(this, localFontMetrics, getText(), sortArrow, getVerticalAlignment(), getHorizontalAlignment(), getVerticalTextPosition(), getHorizontalTextPosition(), localRectangle1, localRectangle3, localRectangle2, getIconTextGap());
    int i = getWidth() - right - sortArrow.getIconWidth();
    int j = y;
    return new Point(i, j);
  }
  
  private class EmptyIcon
    implements Icon, Serializable
  {
    int width = 0;
    int height = 0;
    
    private EmptyIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return width;
    }
    
    public int getIconHeight()
    {
      return height;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\table\DefaultTableCellHeaderRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */