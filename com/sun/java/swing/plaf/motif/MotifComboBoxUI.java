package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI.ComboBoxLayoutManager;
import javax.swing.plaf.basic.BasicComboBoxUI.PropertyChangeHandler;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicComboPopup.InvocationKeyHandler;
import javax.swing.plaf.basic.ComboPopup;

public class MotifComboBoxUI
  extends BasicComboBoxUI
  implements Serializable
{
  Icon arrowIcon;
  static final int HORIZ_MARGIN = 3;
  
  public MotifComboBoxUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifComboBoxUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    arrowIcon = new MotifComboBoxArrowIcon(UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"), UIManager.getColor("control"));
    Runnable local1 = new Runnable()
    {
      public void run()
      {
        if (MotifComboBoxUI.this.motifGetEditor() != null) {
          MotifComboBoxUI.this.motifGetEditor().setBackground(UIManager.getColor("text"));
        }
      }
    };
    SwingUtilities.invokeLater(local1);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    if (!isMinimumSizeDirty) {
      return new Dimension(cachedMinimumSize);
    }
    Insets localInsets = getInsets();
    Dimension localDimension = getDisplaySize();
    height += top + bottom;
    int i = iconAreaWidth();
    width += left + right + i;
    cachedMinimumSize.setSize(width, height);
    isMinimumSizeDirty = false;
    return localDimension;
  }
  
  protected ComboPopup createPopup()
  {
    return new MotifComboPopup(comboBox);
  }
  
  protected void installComponents()
  {
    if (comboBox.isEditable()) {
      addEditor();
    }
    comboBox.add(currentValuePane);
  }
  
  protected void uninstallComponents()
  {
    removeEditor();
    comboBox.removeAll();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    boolean bool = comboBox.hasFocus();
    if (comboBox.isEnabled()) {
      paramGraphics.setColor(comboBox.getBackground());
    } else {
      paramGraphics.setColor(UIManager.getColor("ComboBox.disabledBackground"));
    }
    paramGraphics.fillRect(0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    if (!comboBox.isEditable())
    {
      localRectangle = rectangleForCurrentValue();
      paintCurrentValue(paramGraphics, localRectangle, bool);
    }
    Rectangle localRectangle = rectangleForArrowIcon();
    arrowIcon.paintIcon(paramJComponent, paramGraphics, x, y);
    if (!comboBox.isEditable())
    {
      Border localBorder = comboBox.getBorder();
      Insets localInsets;
      if (localBorder != null) {
        localInsets = localBorder.getBorderInsets(comboBox);
      } else {
        localInsets = new Insets(0, 0, 0, 0);
      }
      if (MotifGraphicsUtils.isLeftToRight(comboBox)) {
        x -= 5;
      } else {
        x += width + 3 + 1;
      }
      y = top;
      width = 1;
      height = (comboBox.getBounds().height - bottom - top);
      paramGraphics.setColor(UIManager.getColor("controlShadow"));
      paramGraphics.fillRect(x, y, width, height);
      x += 1;
      paramGraphics.setColor(UIManager.getColor("controlHighlight"));
      paramGraphics.fillRect(x, y, width, height);
    }
  }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
  {
    ListCellRenderer localListCellRenderer = comboBox.getRenderer();
    Component localComponent = localListCellRenderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
    localComponent.setFont(comboBox.getFont());
    if (comboBox.isEnabled())
    {
      localComponent.setForeground(comboBox.getForeground());
      localComponent.setBackground(comboBox.getBackground());
    }
    else
    {
      localComponent.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
      localComponent.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
    }
    Dimension localDimension = localComponent.getPreferredSize();
    currentValuePane.paintComponent(paramGraphics, localComponent, comboBox, x, y, width, height);
  }
  
  protected Rectangle rectangleForArrowIcon()
  {
    Rectangle localRectangle = comboBox.getBounds();
    Border localBorder = comboBox.getBorder();
    Insets localInsets;
    if (localBorder != null) {
      localInsets = localBorder.getBorderInsets(comboBox);
    } else {
      localInsets = new Insets(0, 0, 0, 0);
    }
    x = left;
    y = top;
    width -= left + right;
    height -= top + bottom;
    if (MotifGraphicsUtils.isLeftToRight(comboBox)) {
      x = (x + width - 3 - arrowIcon.getIconWidth());
    } else {
      x += 3;
    }
    y += (height - arrowIcon.getIconHeight()) / 2;
    width = arrowIcon.getIconWidth();
    height = arrowIcon.getIconHeight();
    return localRectangle;
  }
  
  protected Rectangle rectangleForCurrentValue()
  {
    int i = comboBox.getWidth();
    int j = comboBox.getHeight();
    Insets localInsets = getInsets();
    if (MotifGraphicsUtils.isLeftToRight(comboBox)) {
      return new Rectangle(left, top, i - (left + right) - iconAreaWidth(), j - (top + bottom));
    }
    return new Rectangle(left + iconAreaWidth(), top, i - (left + right) - iconAreaWidth(), j - (top + bottom));
  }
  
  public int iconAreaWidth()
  {
    if (comboBox.isEditable()) {
      return arrowIcon.getIconWidth() + 6;
    }
    return arrowIcon.getIconWidth() + 9 + 2;
  }
  
  public void configureEditor()
  {
    super.configureEditor();
    editor.setBackground(UIManager.getColor("text"));
  }
  
  protected LayoutManager createLayoutManager()
  {
    return new ComboBoxLayoutManager();
  }
  
  private Component motifGetEditor()
  {
    return editor;
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return new MotifPropertyChangeListener(null);
  }
  
  public class ComboBoxLayoutManager
    extends BasicComboBoxUI.ComboBoxLayoutManager
  {
    public ComboBoxLayoutManager()
    {
      super();
    }
    
    public void layoutContainer(Container paramContainer)
    {
      if (MotifComboBoxUI.this.motifGetEditor() != null)
      {
        Rectangle localRectangle = rectangleForCurrentValue();
        x += 1;
        y += 1;
        width -= 1;
        height -= 2;
        MotifComboBoxUI.this.motifGetEditor().setBounds(localRectangle);
      }
    }
  }
  
  static class MotifComboBoxArrowIcon
    implements Icon, Serializable
  {
    private Color lightShadow;
    private Color darkShadow;
    private Color fill;
    
    public MotifComboBoxArrowIcon(Color paramColor1, Color paramColor2, Color paramColor3)
    {
      lightShadow = paramColor1;
      darkShadow = paramColor2;
      fill = paramColor3;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      int i = getIconWidth();
      int j = getIconHeight();
      paramGraphics.setColor(lightShadow);
      paramGraphics.drawLine(paramInt1, paramInt2, paramInt1 + i - 1, paramInt2);
      paramGraphics.drawLine(paramInt1, paramInt2 + 1, paramInt1 + i - 3, paramInt2 + 1);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt1 + i - 2, paramInt2 + 1, paramInt1 + i - 1, paramInt2 + 1);
      int k = paramInt1 + 1;
      int m = paramInt2 + 2;
      int n = i - 6;
      while (m + 1 < paramInt2 + j)
      {
        paramGraphics.setColor(lightShadow);
        paramGraphics.drawLine(k, m, k + 1, m);
        paramGraphics.drawLine(k, m + 1, k + 1, m + 1);
        if (n > 0)
        {
          paramGraphics.setColor(fill);
          paramGraphics.drawLine(k + 2, m, k + 1 + n, m);
          paramGraphics.drawLine(k + 2, m + 1, k + 1 + n, m + 1);
        }
        paramGraphics.setColor(darkShadow);
        paramGraphics.drawLine(k + n + 2, m, k + n + 3, m);
        paramGraphics.drawLine(k + n + 2, m + 1, k + n + 3, m + 1);
        k++;
        n -= 2;
        m += 2;
      }
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt1 + i / 2, paramInt2 + j - 1, paramInt1 + i / 2, paramInt2 + j - 1);
    }
    
    public int getIconWidth()
    {
      return 11;
    }
    
    public int getIconHeight()
    {
      return 11;
    }
  }
  
  protected class MotifComboPopup
    extends BasicComboPopup
  {
    public MotifComboPopup(JComboBox paramJComboBox)
    {
      super();
    }
    
    public MouseMotionListener createListMouseMotionListener()
    {
      new MouseMotionAdapter() {};
    }
    
    public KeyListener createKeyListener()
    {
      return super.createKeyListener();
    }
    
    protected class InvocationKeyHandler
      extends BasicComboPopup.InvocationKeyHandler
    {
      protected InvocationKeyHandler()
      {
        super();
      }
    }
  }
  
  private class MotifPropertyChangeListener
    extends BasicComboBoxUI.PropertyChangeHandler
  {
    private MotifPropertyChangeListener()
    {
      super();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      super.propertyChange(paramPropertyChangeEvent);
      String str = paramPropertyChangeEvent.getPropertyName();
      if ((str == "enabled") && (comboBox.isEnabled()))
      {
        Component localComponent = MotifComboBoxUI.this.motifGetEditor();
        if (localComponent != null) {
          localComponent.setBackground(UIManager.getColor("text"));
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */