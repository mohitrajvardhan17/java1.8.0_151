package javax.swing.plaf.basic;

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
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;
import sun.awt.AppContext;

public class BasicToggleButtonUI
  extends BasicButtonUI
{
  private static final Object BASIC_TOGGLE_BUTTON_UI_KEY = new Object();
  private static final String propertyPrefix = "ToggleButton.";
  
  public BasicToggleButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    BasicToggleButtonUI localBasicToggleButtonUI = (BasicToggleButtonUI)localAppContext.get(BASIC_TOGGLE_BUTTON_UI_KEY);
    if (localBasicToggleButtonUI == null)
    {
      localBasicToggleButtonUI = new BasicToggleButtonUI();
      localAppContext.put(BASIC_TOGGLE_BUTTON_UI_KEY, localBasicToggleButtonUI);
    }
    return localBasicToggleButtonUI;
  }
  
  protected String getPropertyPrefix()
  {
    return "ToggleButton.";
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();
    Dimension localDimension = localAbstractButton.getSize();
    FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
    Insets localInsets = paramJComponent.getInsets();
    Rectangle localRectangle1 = new Rectangle(localDimension);
    x += left;
    y += top;
    width -= right + x;
    height -= bottom + y;
    Rectangle localRectangle2 = new Rectangle();
    Rectangle localRectangle3 = new Rectangle();
    Font localFont = paramJComponent.getFont();
    paramGraphics.setFont(localFont);
    String str = SwingUtilities.layoutCompoundLabel(paramJComponent, localFontMetrics, localAbstractButton.getText(), localAbstractButton.getIcon(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getHorizontalTextPosition(), localRectangle1, localRectangle2, localRectangle3, localAbstractButton.getText() == null ? 0 : localAbstractButton.getIconTextGap());
    paramGraphics.setColor(localAbstractButton.getBackground());
    if (((localButtonModel.isArmed()) && (localButtonModel.isPressed())) || (localButtonModel.isSelected())) {
      paintButtonPressed(paramGraphics, localAbstractButton);
    }
    if (localAbstractButton.getIcon() != null) {
      paintIcon(paramGraphics, localAbstractButton, localRectangle2);
    }
    if ((str != null) && (!str.equals("")))
    {
      View localView = (View)paramJComponent.getClientProperty("html");
      if (localView != null) {
        localView.paint(paramGraphics, localRectangle3);
      } else {
        paintText(paramGraphics, localAbstractButton, localRectangle3, str);
      }
    }
    if ((localAbstractButton.isFocusPainted()) && (localAbstractButton.hasFocus())) {
      paintFocus(paramGraphics, localAbstractButton, localRectangle1, localRectangle3, localRectangle2);
    }
  }
  
  protected void paintIcon(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle)
  {
    ButtonModel localButtonModel = paramAbstractButton.getModel();
    Icon localIcon = null;
    if (!localButtonModel.isEnabled())
    {
      if (localButtonModel.isSelected()) {
        localIcon = paramAbstractButton.getDisabledSelectedIcon();
      } else {
        localIcon = paramAbstractButton.getDisabledIcon();
      }
    }
    else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
    {
      localIcon = paramAbstractButton.getPressedIcon();
      if (localIcon == null) {
        localIcon = paramAbstractButton.getSelectedIcon();
      }
    }
    else if (localButtonModel.isSelected())
    {
      if ((paramAbstractButton.isRolloverEnabled()) && (localButtonModel.isRollover()))
      {
        localIcon = paramAbstractButton.getRolloverSelectedIcon();
        if (localIcon == null) {
          localIcon = paramAbstractButton.getSelectedIcon();
        }
      }
      else
      {
        localIcon = paramAbstractButton.getSelectedIcon();
      }
    }
    else if ((paramAbstractButton.isRolloverEnabled()) && (localButtonModel.isRollover()))
    {
      localIcon = paramAbstractButton.getRolloverIcon();
    }
    if (localIcon == null) {
      localIcon = paramAbstractButton.getIcon();
    }
    localIcon.paintIcon(paramAbstractButton, paramGraphics, x, y);
  }
  
  protected int getTextShiftOffset()
  {
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicToggleButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */