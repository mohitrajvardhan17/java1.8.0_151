package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class MetalComboBoxButton
  extends JButton
{
  protected JComboBox comboBox;
  protected JList listBox;
  protected CellRendererPane rendererPane;
  protected Icon comboIcon;
  protected boolean iconOnly = false;
  
  public final JComboBox getComboBox()
  {
    return comboBox;
  }
  
  public final void setComboBox(JComboBox paramJComboBox)
  {
    comboBox = paramJComboBox;
  }
  
  public final Icon getComboIcon()
  {
    return comboIcon;
  }
  
  public final void setComboIcon(Icon paramIcon)
  {
    comboIcon = paramIcon;
  }
  
  public final boolean isIconOnly()
  {
    return iconOnly;
  }
  
  public final void setIconOnly(boolean paramBoolean)
  {
    iconOnly = paramBoolean;
  }
  
  MetalComboBoxButton()
  {
    super("");
    DefaultButtonModel local1 = new DefaultButtonModel()
    {
      public void setArmed(boolean paramAnonymousBoolean)
      {
        super.setArmed(isPressed() ? true : paramAnonymousBoolean);
      }
    };
    setModel(local1);
  }
  
  public MetalComboBoxButton(JComboBox paramJComboBox, Icon paramIcon, CellRendererPane paramCellRendererPane, JList paramJList)
  {
    this();
    comboBox = paramJComboBox;
    comboIcon = paramIcon;
    rendererPane = paramCellRendererPane;
    listBox = paramJList;
    setEnabled(comboBox.isEnabled());
  }
  
  public MetalComboBoxButton(JComboBox paramJComboBox, Icon paramIcon, boolean paramBoolean, CellRendererPane paramCellRendererPane, JList paramJList)
  {
    this(paramJComboBox, paramIcon, paramCellRendererPane, paramJList);
  }
  
  public boolean isFocusTraversable()
  {
    return false;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    if (paramBoolean)
    {
      setBackground(comboBox.getBackground());
      setForeground(comboBox.getForeground());
    }
    else
    {
      setBackground(UIManager.getColor("ComboBox.disabledBackground"));
      setForeground(UIManager.getColor("ComboBox.disabledForeground"));
    }
  }
  
  public void paintComponent(Graphics paramGraphics)
  {
    boolean bool1 = MetalUtils.isLeftToRight(comboBox);
    super.paintComponent(paramGraphics);
    Insets localInsets = getInsets();
    int i = getWidth() - (left + right);
    int j = getHeight() - (top + bottom);
    if ((j <= 0) || (i <= 0)) {
      return;
    }
    int k = left;
    int m = top;
    int n = k + (i - 1);
    int i1 = m + (j - 1);
    int i2 = 0;
    int i3 = bool1 ? n : k;
    if (comboIcon != null)
    {
      i2 = comboIcon.getIconWidth();
      int i4 = comboIcon.getIconHeight();
      int i5 = 0;
      if (iconOnly)
      {
        i3 = getWidth() / 2 - i2 / 2;
        i5 = getHeight() / 2 - i4 / 2;
      }
      else
      {
        if (bool1) {
          i3 = k + (i - 1) - i2;
        } else {
          i3 = k;
        }
        i5 = m + (i1 - m) / 2 - i4 / 2;
      }
      comboIcon.paintIcon(this, paramGraphics, i3, i5);
      if ((comboBox.hasFocus()) && ((!MetalLookAndFeel.usingOcean()) || (comboBox.isEditable())))
      {
        paramGraphics.setColor(MetalLookAndFeel.getFocusColor());
        paramGraphics.drawRect(k - 1, m - 1, i + 3, j + 1);
      }
    }
    if (MetalLookAndFeel.usingOcean()) {
      return;
    }
    if ((!iconOnly) && (comboBox != null))
    {
      ListCellRenderer localListCellRenderer = comboBox.getRenderer();
      boolean bool2 = getModel().isPressed();
      Component localComponent = localListCellRenderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, bool2, false);
      localComponent.setFont(rendererPane.getFont());
      if ((model.isArmed()) && (model.isPressed()))
      {
        if (isOpaque()) {
          localComponent.setBackground(UIManager.getColor("Button.select"));
        }
        localComponent.setForeground(comboBox.getForeground());
      }
      else if (!comboBox.isEnabled())
      {
        if (isOpaque()) {
          localComponent.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
        }
        localComponent.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
      }
      else
      {
        localComponent.setForeground(comboBox.getForeground());
        localComponent.setBackground(comboBox.getBackground());
      }
      int i6 = i - (right + i2);
      boolean bool3 = false;
      if ((localComponent instanceof JPanel)) {
        bool3 = true;
      }
      if (bool1) {
        rendererPane.paintComponent(paramGraphics, localComponent, this, k, m, i6, j, bool3);
      } else {
        rendererPane.paintComponent(paramGraphics, localComponent, this, k + i2, m, i6, j, bool3);
      }
    }
  }
  
  public Dimension getMinimumSize()
  {
    Dimension localDimension = new Dimension();
    Insets localInsets = getInsets();
    width = (left + getComboIcon().getIconWidth() + right);
    height = (bottom + getComboIcon().getIconHeight() + top);
    return localDimension;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalComboBoxButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */