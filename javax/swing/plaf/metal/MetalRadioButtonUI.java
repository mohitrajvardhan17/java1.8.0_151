package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalRadioButtonUI
  extends BasicRadioButtonUI
{
  private static final Object METAL_RADIO_BUTTON_UI_KEY = new Object();
  protected Color focusColor;
  protected Color selectColor;
  protected Color disabledTextColor;
  private boolean defaults_initialized = false;
  
  public MetalRadioButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    MetalRadioButtonUI localMetalRadioButtonUI = (MetalRadioButtonUI)localAppContext.get(METAL_RADIO_BUTTON_UI_KEY);
    if (localMetalRadioButtonUI == null)
    {
      localMetalRadioButtonUI = new MetalRadioButtonUI();
      localAppContext.put(METAL_RADIO_BUTTON_UI_KEY, localMetalRadioButtonUI);
    }
    return localMetalRadioButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
    if (!defaults_initialized)
    {
      focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
      selectColor = UIManager.getColor(getPropertyPrefix() + "select");
      disabledTextColor = UIManager.getColor(getPropertyPrefix() + "disabledText");
      defaults_initialized = true;
    }
    LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
    defaults_initialized = false;
  }
  
  protected Color getSelectColor()
  {
    return selectColor;
  }
  
  protected Color getDisabledTextColor()
  {
    return disabledTextColor;
  }
  
  protected Color getFocusColor()
  {
    return focusColor;
  }
  
  public synchronized void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();
    Dimension localDimension = paramJComponent.getSize();
    int i = width;
    int j = height;
    Font localFont = paramJComponent.getFont();
    paramGraphics.setFont(localFont);
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, localFont);
    Rectangle localRectangle1 = new Rectangle(localDimension);
    Rectangle localRectangle2 = new Rectangle();
    Rectangle localRectangle3 = new Rectangle();
    Insets localInsets = paramJComponent.getInsets();
    x += left;
    y += top;
    width -= right + x;
    height -= bottom + y;
    Icon localIcon = localAbstractButton.getIcon();
    Object localObject1 = null;
    Object localObject2 = null;
    String str = SwingUtilities.layoutCompoundLabel(paramJComponent, localFontMetrics, localAbstractButton.getText(), localIcon != null ? localIcon : getDefaultIcon(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getHorizontalTextPosition(), localRectangle1, localRectangle2, localRectangle3, localAbstractButton.getIconTextGap());
    if (paramJComponent.isOpaque())
    {
      paramGraphics.setColor(localAbstractButton.getBackground());
      paramGraphics.fillRect(0, 0, width, height);
    }
    if (localIcon != null)
    {
      if (!localButtonModel.isEnabled())
      {
        if (localButtonModel.isSelected()) {
          localIcon = localAbstractButton.getDisabledSelectedIcon();
        } else {
          localIcon = localAbstractButton.getDisabledIcon();
        }
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localIcon = localAbstractButton.getPressedIcon();
        if (localIcon == null) {
          localIcon = localAbstractButton.getSelectedIcon();
        }
      }
      else if (localButtonModel.isSelected())
      {
        if ((localAbstractButton.isRolloverEnabled()) && (localButtonModel.isRollover()))
        {
          localIcon = localAbstractButton.getRolloverSelectedIcon();
          if (localIcon == null) {
            localIcon = localAbstractButton.getSelectedIcon();
          }
        }
        else
        {
          localIcon = localAbstractButton.getSelectedIcon();
        }
      }
      else if ((localAbstractButton.isRolloverEnabled()) && (localButtonModel.isRollover()))
      {
        localIcon = localAbstractButton.getRolloverIcon();
      }
      if (localIcon == null) {
        localIcon = localAbstractButton.getIcon();
      }
      localIcon.paintIcon(paramJComponent, paramGraphics, x, y);
    }
    else
    {
      getDefaultIcon().paintIcon(paramJComponent, paramGraphics, x, y);
    }
    if (str != null)
    {
      View localView = (View)paramJComponent.getClientProperty("html");
      if (localView != null)
      {
        localView.paint(paramGraphics, localRectangle3);
      }
      else
      {
        int k = localAbstractButton.getDisplayedMnemonicIndex();
        if (localButtonModel.isEnabled()) {
          paramGraphics.setColor(localAbstractButton.getForeground());
        } else {
          paramGraphics.setColor(getDisabledTextColor());
        }
        SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, str, k, x, y + localFontMetrics.getAscent());
      }
      if ((localAbstractButton.hasFocus()) && (localAbstractButton.isFocusPainted()) && (width > 0) && (height > 0)) {
        paintFocus(paramGraphics, localRectangle3, localDimension);
      }
    }
  }
  
  protected void paintFocus(Graphics paramGraphics, Rectangle paramRectangle, Dimension paramDimension)
  {
    paramGraphics.setColor(getFocusColor());
    paramGraphics.drawRect(x - 1, y - 1, width + 1, height + 1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalRadioButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */