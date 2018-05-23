package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI.ComboBoxLayoutManager;
import javax.swing.plaf.basic.BasicComboBoxUI.PropertyChangeHandler;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class MetalComboBoxUI
  extends BasicComboBoxUI
{
  public MetalComboBoxUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalComboBoxUI();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (MetalLookAndFeel.usingOcean()) {
      super.paint(paramGraphics, paramJComponent);
    }
  }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
  {
    if (MetalLookAndFeel.usingOcean())
    {
      x += 2;
      width -= 3;
      if (arrowButton != null)
      {
        Insets localInsets = arrowButton.getInsets();
        y += top;
        height -= top + bottom;
      }
      else
      {
        y += 2;
        height -= 4;
      }
      super.paintCurrentValue(paramGraphics, paramRectangle, paramBoolean);
    }
    else if ((paramGraphics == null) || (paramRectangle == null))
    {
      throw new NullPointerException("Must supply a non-null Graphics and Rectangle");
    }
  }
  
  public void paintCurrentValueBackground(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
  {
    if (MetalLookAndFeel.usingOcean())
    {
      paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      paramGraphics.drawRect(x, y, width, height - 1);
      paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
      paramGraphics.drawRect(x + 1, y + 1, width - 2, height - 3);
      if ((paramBoolean) && (!isPopupVisible(comboBox)) && (arrowButton != null))
      {
        paramGraphics.setColor(listBox.getSelectionBackground());
        Insets localInsets = arrowButton.getInsets();
        if (top > 2) {
          paramGraphics.fillRect(x + 2, y + 2, width - 3, top - 2);
        }
        if (bottom > 2) {
          paramGraphics.fillRect(x + 2, y + height - bottom, width - 3, bottom - 2);
        }
      }
    }
    else if ((paramGraphics == null) || (paramRectangle == null))
    {
      throw new NullPointerException("Must supply a non-null Graphics and Rectangle");
    }
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    int i;
    if ((MetalLookAndFeel.usingOcean()) && (paramInt2 >= 4))
    {
      paramInt2 -= 4;
      i = super.getBaseline(paramJComponent, paramInt1, paramInt2);
      if (i >= 0) {
        i += 2;
      }
    }
    else
    {
      i = super.getBaseline(paramJComponent, paramInt1, paramInt2);
    }
    return i;
  }
  
  protected ComboBoxEditor createEditor()
  {
    return new MetalComboBoxEditor.UIResource();
  }
  
  protected ComboPopup createPopup()
  {
    return super.createPopup();
  }
  
  protected JButton createArrowButton()
  {
    boolean bool = (comboBox.isEditable()) || (MetalLookAndFeel.usingOcean());
    MetalComboBoxButton localMetalComboBoxButton = new MetalComboBoxButton(comboBox, new MetalComboBoxIcon(), bool, currentValuePane, listBox);
    localMetalComboBoxButton.setMargin(new Insets(0, 1, 1, 3));
    if (MetalLookAndFeel.usingOcean()) {
      localMetalComboBoxButton.putClientProperty(MetalBorders.NO_BUTTON_ROLLOVER, Boolean.TRUE);
    }
    updateButtonForOcean(localMetalComboBoxButton);
    return localMetalComboBoxButton;
  }
  
  private void updateButtonForOcean(JButton paramJButton)
  {
    if (MetalLookAndFeel.usingOcean()) {
      paramJButton.setFocusPainted(comboBox.isEditable());
    }
  }
  
  public PropertyChangeListener createPropertyChangeListener()
  {
    return new MetalPropertyChangeListener();
  }
  
  @Deprecated
  protected void editablePropertyChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected LayoutManager createLayoutManager()
  {
    return new MetalComboBoxLayoutManager();
  }
  
  public void layoutComboBox(Container paramContainer, MetalComboBoxLayoutManager paramMetalComboBoxLayoutManager)
  {
    if ((comboBox.isEditable()) && (!MetalLookAndFeel.usingOcean()))
    {
      paramMetalComboBoxLayoutManager.superLayout(paramContainer);
      return;
    }
    Object localObject;
    if (arrowButton != null)
    {
      int i;
      if (MetalLookAndFeel.usingOcean())
      {
        localObject = comboBox.getInsets();
        i = arrowButton.getMinimumSize().width;
        arrowButton.setBounds(MetalUtils.isLeftToRight(comboBox) ? comboBox.getWidth() - right - i : left, top, i, comboBox.getHeight() - top - bottom);
      }
      else
      {
        localObject = comboBox.getInsets();
        i = comboBox.getWidth();
        int j = comboBox.getHeight();
        arrowButton.setBounds(left, top, i - (left + right), j - (top + bottom));
      }
    }
    if ((editor != null) && (MetalLookAndFeel.usingOcean()))
    {
      localObject = rectangleForCurrentValue();
      editor.setBounds((Rectangle)localObject);
    }
  }
  
  @Deprecated
  protected void removeListeners()
  {
    if (propertyChangeListener != null) {
      comboBox.removePropertyChangeListener(propertyChangeListener);
    }
  }
  
  public void configureEditor()
  {
    super.configureEditor();
  }
  
  public void unconfigureEditor()
  {
    super.unconfigureEditor();
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    if (!isMinimumSizeDirty) {
      return new Dimension(cachedMinimumSize);
    }
    Dimension localDimension = null;
    Insets localInsets1;
    if ((!comboBox.isEditable()) && (arrowButton != null))
    {
      localInsets1 = arrowButton.getInsets();
      Insets localInsets2 = comboBox.getInsets();
      localDimension = getDisplaySize();
      width += left + right;
      width += right;
      width += arrowButton.getMinimumSize().width;
      height += top + bottom;
      height += top + bottom;
    }
    else if ((comboBox.isEditable()) && (arrowButton != null) && (editor != null))
    {
      localDimension = super.getMinimumSize(paramJComponent);
      localInsets1 = arrowButton.getMargin();
      height += top + bottom;
      width += left + right;
    }
    else
    {
      localDimension = super.getMinimumSize(paramJComponent);
    }
    cachedMinimumSize.setSize(width, height);
    isMinimumSizeDirty = false;
    return new Dimension(cachedMinimumSize);
  }
  
  public class MetalComboBoxLayoutManager
    extends BasicComboBoxUI.ComboBoxLayoutManager
  {
    public MetalComboBoxLayoutManager()
    {
      super();
    }
    
    public void layoutContainer(Container paramContainer)
    {
      layoutComboBox(paramContainer, this);
    }
    
    public void superLayout(Container paramContainer)
    {
      super.layoutContainer(paramContainer);
    }
  }
  
  @Deprecated
  public class MetalComboPopup
    extends BasicComboPopup
  {
    public MetalComboPopup(JComboBox paramJComboBox)
    {
      super();
    }
    
    public void delegateFocus(MouseEvent paramMouseEvent)
    {
      super.delegateFocus(paramMouseEvent);
    }
  }
  
  public class MetalPropertyChangeListener
    extends BasicComboBoxUI.PropertyChangeHandler
  {
    public MetalPropertyChangeListener()
    {
      super();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      super.propertyChange(paramPropertyChangeEvent);
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject;
      if (str == "editable")
      {
        if ((arrowButton instanceof MetalComboBoxButton))
        {
          localObject = (MetalComboBoxButton)arrowButton;
          ((MetalComboBoxButton)localObject).setIconOnly((comboBox.isEditable()) || (MetalLookAndFeel.usingOcean()));
        }
        comboBox.repaint();
        MetalComboBoxUI.this.updateButtonForOcean(arrowButton);
      }
      else if (str == "background")
      {
        localObject = (Color)paramPropertyChangeEvent.getNewValue();
        arrowButton.setBackground((Color)localObject);
        listBox.setBackground((Color)localObject);
      }
      else if (str == "foreground")
      {
        localObject = (Color)paramPropertyChangeEvent.getNewValue();
        arrowButton.setForeground((Color)localObject);
        listBox.setForeground((Color)localObject);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */