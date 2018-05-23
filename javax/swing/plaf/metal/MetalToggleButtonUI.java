package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalToggleButtonUI
  extends BasicToggleButtonUI
{
  private static final Object METAL_TOGGLE_BUTTON_UI_KEY = new Object();
  protected Color focusColor;
  protected Color selectColor;
  protected Color disabledTextColor;
  private boolean defaults_initialized = false;
  
  public MetalToggleButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    MetalToggleButtonUI localMetalToggleButtonUI = (MetalToggleButtonUI)localAppContext.get(METAL_TOGGLE_BUTTON_UI_KEY);
    if (localMetalToggleButtonUI == null)
    {
      localMetalToggleButtonUI = new MetalToggleButtonUI();
      localAppContext.put(METAL_TOGGLE_BUTTON_UI_KEY, localMetalToggleButtonUI);
    }
    return localMetalToggleButtonUI;
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
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    if (((paramJComponent.getBackground() instanceof UIResource)) && (localAbstractButton.isContentAreaFilled()) && (paramJComponent.isEnabled()))
    {
      ButtonModel localButtonModel = localAbstractButton.getModel();
      if (!MetalUtils.isToolBarButton(paramJComponent))
      {
        if ((!localButtonModel.isArmed()) && (!localButtonModel.isPressed()) && (MetalUtils.drawGradient(paramJComponent, paramGraphics, "ToggleButton.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true))) {
          paint(paramGraphics, paramJComponent);
        }
      }
      else if (((localButtonModel.isRollover()) || (localButtonModel.isSelected())) && (MetalUtils.drawGradient(paramJComponent, paramGraphics, "ToggleButton.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true)))
      {
        paint(paramGraphics, paramJComponent);
        return;
      }
    }
    super.update(paramGraphics, paramJComponent);
  }
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton)
  {
    if (paramAbstractButton.isContentAreaFilled())
    {
      paramGraphics.setColor(getSelectColor());
      paramGraphics.fillRect(0, 0, paramAbstractButton.getWidth(), paramAbstractButton.getHeight());
    }
  }
  
  protected void paintText(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle, String paramString)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(localAbstractButton, paramGraphics);
    int i = localAbstractButton.getDisplayedMnemonicIndex();
    if (localButtonModel.isEnabled()) {
      paramGraphics.setColor(localAbstractButton.getForeground());
    } else if (localButtonModel.isSelected()) {
      paramGraphics.setColor(paramJComponent.getBackground());
    } else {
      paramGraphics.setColor(getDisabledTextColor());
    }
    SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, x, y + localFontMetrics.getAscent());
  }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3)
  {
    Rectangle localRectangle = new Rectangle();
    String str = paramAbstractButton.getText();
    int i = paramAbstractButton.getIcon() != null ? 1 : 0;
    if ((str != null) && (!str.equals("")))
    {
      if (i == 0) {
        localRectangle.setBounds(paramRectangle2);
      } else {
        localRectangle.setBounds(paramRectangle3.union(paramRectangle2));
      }
    }
    else if (i != 0) {
      localRectangle.setBounds(paramRectangle3);
    }
    paramGraphics.setColor(getFocusColor());
    paramGraphics.drawRect(x - 1, y - 1, width + 1, height + 1);
  }
  
  protected void paintIcon(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle)
  {
    super.paintIcon(paramGraphics, paramAbstractButton, paramRectangle);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalToggleButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */