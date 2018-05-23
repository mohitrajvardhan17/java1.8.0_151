package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import sun.swing.SwingUtilities2;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class WindowsTableHeaderUI
  extends BasicTableHeaderUI
{
  private TableCellRenderer originalHeaderRenderer;
  
  public WindowsTableHeaderUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsTableHeaderUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    if (XPStyle.getXP() != null)
    {
      originalHeaderRenderer = header.getDefaultRenderer();
      if ((originalHeaderRenderer instanceof UIResource)) {
        header.setDefaultRenderer(new XPDefaultRenderer());
      }
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    if ((header.getDefaultRenderer() instanceof XPDefaultRenderer)) {
      header.setDefaultRenderer(originalHeaderRenderer);
    }
    super.uninstallUI(paramJComponent);
  }
  
  protected void rolloverColumnUpdated(int paramInt1, int paramInt2)
  {
    if (XPStyle.getXP() != null)
    {
      header.repaint(header.getHeaderRect(paramInt1));
      header.repaint(header.getHeaderRect(paramInt2));
    }
  }
  
  private static class IconBorder
    implements Border, UIResource
  {
    private final Icon icon;
    private final int top;
    private final int left;
    private final int bottom;
    private final int right;
    
    public IconBorder(Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      icon = paramIcon;
      top = paramInt1;
      left = paramInt2;
      bottom = paramInt3;
      right = paramInt4;
    }
    
    public Insets getBorderInsets(Component paramComponent)
    {
      return new Insets(icon.getIconHeight() + top, left, bottom, right);
    }
    
    public boolean isBorderOpaque()
    {
      return false;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      icon.paintIcon(paramComponent, paramGraphics, paramInt1 + left + (paramInt3 - left - right - icon.getIconWidth()) / 2, paramInt2 + top);
    }
  }
  
  private class XPDefaultRenderer
    extends DefaultTableCellHeaderRenderer
  {
    XPStyle.Skin skin;
    boolean isSelected;
    boolean hasFocus;
    boolean hasRollover;
    int column;
    
    XPDefaultRenderer()
    {
      setHorizontalAlignment(10);
    }
    
    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
      isSelected = paramBoolean1;
      hasFocus = paramBoolean2;
      column = paramInt2;
      hasRollover = (paramInt2 == getRolloverColumn());
      if (skin == null)
      {
        localXPStyle = XPStyle.getXP();
        skin = (localXPStyle != null ? localXPStyle.getSkin(header, TMSchema.Part.HP_HEADERITEM) : null);
      }
      XPStyle localXPStyle = skin != null ? skin.getContentMargin() : null;
      Object localObject = null;
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      if (localXPStyle != null)
      {
        i = top;
        j = left;
        k = bottom;
        m = right;
      }
      j += 5;
      k += 4;
      m += 5;
      Icon localIcon;
      if ((WindowsLookAndFeel.isOnVista()) && ((((localIcon = getIcon()) instanceof UIResource)) || (localIcon == null)))
      {
        i++;
        setIcon(null);
        localIcon = null;
        SortOrder localSortOrder = getColumnSortOrder(paramJTable, paramInt2);
        if (localSortOrder != null) {
          switch (WindowsTableHeaderUI.1.$SwitchMap$javax$swing$SortOrder[localSortOrder.ordinal()])
          {
          case 1: 
            localIcon = UIManager.getIcon("Table.ascendingSortIcon");
            break;
          case 2: 
            localIcon = UIManager.getIcon("Table.descendingSortIcon");
          }
        }
        if (localIcon != null)
        {
          k = localIcon.getIconHeight();
          localObject = new WindowsTableHeaderUI.IconBorder(localIcon, i, j, k, m);
        }
        else
        {
          localIcon = UIManager.getIcon("Table.ascendingSortIcon");
          int n = localIcon != null ? localIcon.getIconHeight() : 0;
          if (n != 0) {
            k = n;
          }
          localObject = new EmptyBorder(n + i, j, k, m);
        }
      }
      else
      {
        i += 3;
        localObject = new EmptyBorder(i, j, k, m);
      }
      setBorder((Border)localObject);
      return this;
    }
    
    public void paint(Graphics paramGraphics)
    {
      Dimension localDimension = getSize();
      TMSchema.State localState = TMSchema.State.NORMAL;
      TableColumn localTableColumn = header.getDraggedColumn();
      if ((localTableColumn != null) && (column == SwingUtilities2.convertColumnIndexToView(header.getColumnModel(), localTableColumn.getModelIndex()))) {
        localState = TMSchema.State.PRESSED;
      } else if ((isSelected) || (hasFocus) || (hasRollover)) {
        localState = TMSchema.State.HOT;
      }
      if (WindowsLookAndFeel.isOnVista())
      {
        SortOrder localSortOrder = getColumnSortOrder(header.getTable(), column);
        if (localSortOrder != null) {
          switch (WindowsTableHeaderUI.1.$SwitchMap$javax$swing$SortOrder[localSortOrder.ordinal()])
          {
          case 1: 
          case 2: 
            switch (WindowsTableHeaderUI.1.$SwitchMap$com$sun$java$swing$plaf$windows$TMSchema$State[localState.ordinal()])
            {
            case 1: 
              localState = TMSchema.State.SORTEDNORMAL;
              break;
            case 2: 
              localState = TMSchema.State.SORTEDPRESSED;
              break;
            case 3: 
              localState = TMSchema.State.SORTEDHOT;
            }
            break;
          }
        }
      }
      skin.paintSkin(paramGraphics, 0, 0, width - 1, height - 1, localState);
      super.paint(paramGraphics);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTableHeaderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */