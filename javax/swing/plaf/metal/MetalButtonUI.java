package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalButtonUI
  extends BasicButtonUI
{
  protected Color focusColor;
  protected Color selectColor;
  protected Color disabledTextColor;
  private static final Object METAL_BUTTON_UI_KEY = new Object();
  
  public MetalButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    MetalButtonUI localMetalButtonUI = (MetalButtonUI)localAppContext.get(METAL_BUTTON_UI_KEY);
    if (localMetalButtonUI == null)
    {
      localMetalButtonUI = new MetalButtonUI();
      localAppContext.put(METAL_BUTTON_UI_KEY, localMetalButtonUI);
    }
    return localMetalButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
  }
  
  public void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
  }
  
  protected BasicButtonListener createButtonListener(AbstractButton paramAbstractButton)
  {
    return super.createButtonListener(paramAbstractButton);
  }
  
  protected Color getSelectColor()
  {
    selectColor = UIManager.getColor(getPropertyPrefix() + "select");
    return selectColor;
  }
  
  protected Color getDisabledTextColor()
  {
    disabledTextColor = UIManager.getColor(getPropertyPrefix() + "disabledText");
    return disabledTextColor;
  }
  
  protected Color getFocusColor()
  {
    focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
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
        if ((!localButtonModel.isArmed()) && (!localButtonModel.isPressed()) && (MetalUtils.drawGradient(paramJComponent, paramGraphics, "Button.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true))) {
          paint(paramGraphics, paramJComponent);
        }
      }
      else if ((localButtonModel.isRollover()) && (MetalUtils.drawGradient(paramJComponent, paramGraphics, "Button.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true)))
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
      Dimension localDimension = paramAbstractButton.getSize();
      paramGraphics.setColor(getSelectColor());
      paramGraphics.fillRect(0, 0, width, height);
    }
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
  
  protected void paintText(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle, String paramString)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics);
    int i = localAbstractButton.getDisplayedMnemonicIndex();
    if (localButtonModel.isEnabled()) {
      paramGraphics.setColor(localAbstractButton.getForeground());
    } else {
      paramGraphics.setColor(getDisabledTextColor());
    }
    SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, x, y + localFontMetrics.getAscent());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */