package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseMotionListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class BasicButtonUI
  extends ButtonUI
{
  protected int defaultTextIconGap;
  private int shiftOffset = 0;
  protected int defaultTextShiftOffset;
  private static final String propertyPrefix = "Button.";
  private static final Object BASIC_BUTTON_UI_KEY = new Object();
  private static Rectangle viewRect = new Rectangle();
  private static Rectangle textRect = new Rectangle();
  private static Rectangle iconRect = new Rectangle();
  
  public BasicButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    BasicButtonUI localBasicButtonUI = (BasicButtonUI)localAppContext.get(BASIC_BUTTON_UI_KEY);
    if (localBasicButtonUI == null)
    {
      localBasicButtonUI = new BasicButtonUI();
      localAppContext.put(BASIC_BUTTON_UI_KEY, localBasicButtonUI);
    }
    return localBasicButtonUI;
  }
  
  protected String getPropertyPrefix()
  {
    return "Button.";
  }
  
  public void installUI(JComponent paramJComponent)
  {
    installDefaults((AbstractButton)paramJComponent);
    installListeners((AbstractButton)paramJComponent);
    installKeyboardActions((AbstractButton)paramJComponent);
    BasicHTML.updateRenderer(paramJComponent, ((AbstractButton)paramJComponent).getText());
  }
  
  protected void installDefaults(AbstractButton paramAbstractButton)
  {
    String str = getPropertyPrefix();
    defaultTextShiftOffset = UIManager.getInt(str + "textShiftOffset");
    if (paramAbstractButton.isContentAreaFilled()) {
      LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.TRUE);
    } else {
      LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.FALSE);
    }
    if ((paramAbstractButton.getMargin() == null) || ((paramAbstractButton.getMargin() instanceof UIResource))) {
      paramAbstractButton.setMargin(UIManager.getInsets(str + "margin"));
    }
    LookAndFeel.installColorsAndFont(paramAbstractButton, str + "background", str + "foreground", str + "font");
    LookAndFeel.installBorder(paramAbstractButton, str + "border");
    Object localObject = UIManager.get(str + "rollover");
    if (localObject != null) {
      LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", localObject);
    }
    LookAndFeel.installProperty(paramAbstractButton, "iconTextGap", Integer.valueOf(4));
  }
  
  protected void installListeners(AbstractButton paramAbstractButton)
  {
    BasicButtonListener localBasicButtonListener = createButtonListener(paramAbstractButton);
    if (localBasicButtonListener != null)
    {
      paramAbstractButton.addMouseListener(localBasicButtonListener);
      paramAbstractButton.addMouseMotionListener(localBasicButtonListener);
      paramAbstractButton.addFocusListener(localBasicButtonListener);
      paramAbstractButton.addPropertyChangeListener(localBasicButtonListener);
      paramAbstractButton.addChangeListener(localBasicButtonListener);
    }
  }
  
  protected void installKeyboardActions(AbstractButton paramAbstractButton)
  {
    BasicButtonListener localBasicButtonListener = getButtonListener(paramAbstractButton);
    if (localBasicButtonListener != null) {
      localBasicButtonListener.installKeyboardActions(paramAbstractButton);
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallKeyboardActions((AbstractButton)paramJComponent);
    uninstallListeners((AbstractButton)paramJComponent);
    uninstallDefaults((AbstractButton)paramJComponent);
    BasicHTML.updateRenderer(paramJComponent, "");
  }
  
  protected void uninstallKeyboardActions(AbstractButton paramAbstractButton)
  {
    BasicButtonListener localBasicButtonListener = getButtonListener(paramAbstractButton);
    if (localBasicButtonListener != null) {
      localBasicButtonListener.uninstallKeyboardActions(paramAbstractButton);
    }
  }
  
  protected void uninstallListeners(AbstractButton paramAbstractButton)
  {
    BasicButtonListener localBasicButtonListener = getButtonListener(paramAbstractButton);
    if (localBasicButtonListener != null)
    {
      paramAbstractButton.removeMouseListener(localBasicButtonListener);
      paramAbstractButton.removeMouseMotionListener(localBasicButtonListener);
      paramAbstractButton.removeFocusListener(localBasicButtonListener);
      paramAbstractButton.removeChangeListener(localBasicButtonListener);
      paramAbstractButton.removePropertyChangeListener(localBasicButtonListener);
    }
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    LookAndFeel.uninstallBorder(paramAbstractButton);
  }
  
  protected BasicButtonListener createButtonListener(AbstractButton paramAbstractButton)
  {
    return new BasicButtonListener(paramAbstractButton);
  }
  
  public int getDefaultTextIconGap(AbstractButton paramAbstractButton)
  {
    return defaultTextIconGap;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();
    String str = layout(localAbstractButton, SwingUtilities2.getFontMetrics(localAbstractButton, paramGraphics), localAbstractButton.getWidth(), localAbstractButton.getHeight());
    clearTextShiftOffset();
    if ((localButtonModel.isArmed()) && (localButtonModel.isPressed())) {
      paintButtonPressed(paramGraphics, localAbstractButton);
    }
    if (localAbstractButton.getIcon() != null) {
      paintIcon(paramGraphics, paramJComponent, iconRect);
    }
    if ((str != null) && (!str.equals("")))
    {
      View localView = (View)paramJComponent.getClientProperty("html");
      if (localView != null) {
        localView.paint(paramGraphics, textRect);
      } else {
        paintText(paramGraphics, localAbstractButton, textRect, str);
      }
    }
    if ((localAbstractButton.isFocusPainted()) && (localAbstractButton.hasFocus())) {
      paintFocus(paramGraphics, localAbstractButton, viewRect, textRect, iconRect);
    }
  }
  
  protected void paintIcon(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();
    Object localObject1 = localAbstractButton.getIcon();
    Object localObject2 = null;
    if (localObject1 == null) {
      return;
    }
    Icon localIcon = null;
    if (localButtonModel.isSelected())
    {
      localIcon = localAbstractButton.getSelectedIcon();
      if (localIcon != null) {
        localObject1 = localIcon;
      }
    }
    if (!localButtonModel.isEnabled())
    {
      if (localButtonModel.isSelected())
      {
        localObject2 = localAbstractButton.getDisabledSelectedIcon();
        if (localObject2 == null) {
          localObject2 = localIcon;
        }
      }
      if (localObject2 == null) {
        localObject2 = localAbstractButton.getDisabledIcon();
      }
    }
    else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
    {
      localObject2 = localAbstractButton.getPressedIcon();
      if (localObject2 != null) {
        clearTextShiftOffset();
      }
    }
    else if ((localAbstractButton.isRolloverEnabled()) && (localButtonModel.isRollover()))
    {
      if (localButtonModel.isSelected())
      {
        localObject2 = localAbstractButton.getRolloverSelectedIcon();
        if (localObject2 == null) {
          localObject2 = localIcon;
        }
      }
      if (localObject2 == null) {
        localObject2 = localAbstractButton.getRolloverIcon();
      }
    }
    if (localObject2 != null) {
      localObject1 = localObject2;
    }
    if ((localButtonModel.isPressed()) && (localButtonModel.isArmed())) {
      ((Icon)localObject1).paintIcon(paramJComponent, paramGraphics, x + getTextShiftOffset(), y + getTextShiftOffset());
    } else {
      ((Icon)localObject1).paintIcon(paramJComponent, paramGraphics, x, y);
    }
  }
  
  protected void paintText(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle, String paramString)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics);
    int i = localAbstractButton.getDisplayedMnemonicIndex();
    if (localButtonModel.isEnabled())
    {
      paramGraphics.setColor(localAbstractButton.getForeground());
      SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, x + getTextShiftOffset(), y + localFontMetrics.getAscent() + getTextShiftOffset());
    }
    else
    {
      paramGraphics.setColor(localAbstractButton.getBackground().brighter());
      SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, x, y + localFontMetrics.getAscent());
      paramGraphics.setColor(localAbstractButton.getBackground().darker());
      SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, x - 1, y + localFontMetrics.getAscent() - 1);
    }
  }
  
  protected void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString)
  {
    paintText(paramGraphics, paramAbstractButton, paramRectangle, paramString);
  }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3) {}
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton) {}
  
  protected void clearTextShiftOffset()
  {
    shiftOffset = 0;
  }
  
  protected void setTextShiftOffset()
  {
    shiftOffset = defaultTextShiftOffset;
  }
  
  protected int getTextShiftOffset()
  {
    return shiftOffset;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    Dimension localDimension = getPreferredSize(paramJComponent);
    View localView = (View)paramJComponent.getClientProperty("html");
    if (localView != null)
    {
      Dimension tmp21_20 = localDimension;
      2120width = ((int)(2120width - (localView.getPreferredSpan(0) - localView.getMinimumSpan(0))));
    }
    return localDimension;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    return BasicGraphicsUtils.getPreferredButtonSize(localAbstractButton, localAbstractButton.getIconTextGap());
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    Dimension localDimension = getPreferredSize(paramJComponent);
    View localView = (View)paramJComponent.getClientProperty("html");
    if (localView != null)
    {
      Dimension tmp21_20 = localDimension;
      2120width = ((int)(2120width + (localView.getMaximumSpan(0) - localView.getPreferredSpan(0))));
    }
    return localDimension;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    String str = localAbstractButton.getText();
    if ((str == null) || ("".equals(str))) {
      return -1;
    }
    FontMetrics localFontMetrics = localAbstractButton.getFontMetrics(localAbstractButton.getFont());
    layout(localAbstractButton, localFontMetrics, paramInt1, paramInt2);
    return BasicHTML.getBaseline(localAbstractButton, textRecty, localFontMetrics.getAscent(), textRectwidth, textRectheight);
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    if (paramJComponent.getClientProperty("html") != null) {
      return Component.BaselineResizeBehavior.OTHER;
    }
    switch (((AbstractButton)paramJComponent).getVerticalAlignment())
    {
    case 1: 
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    case 3: 
      return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
    case 0: 
      return Component.BaselineResizeBehavior.CENTER_OFFSET;
    }
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  private String layout(AbstractButton paramAbstractButton, FontMetrics paramFontMetrics, int paramInt1, int paramInt2)
  {
    Insets localInsets = paramAbstractButton.getInsets();
    viewRectx = left;
    viewRecty = top;
    viewRectwidth = (paramInt1 - (right + viewRectx));
    viewRectheight = (paramInt2 - (bottom + viewRecty));
    textRectx = (textRecty = textRectwidth = textRectheight = 0);
    iconRectx = (iconRecty = iconRectwidth = iconRectheight = 0);
    return SwingUtilities.layoutCompoundLabel(paramAbstractButton, paramFontMetrics, paramAbstractButton.getText(), paramAbstractButton.getIcon(), paramAbstractButton.getVerticalAlignment(), paramAbstractButton.getHorizontalAlignment(), paramAbstractButton.getVerticalTextPosition(), paramAbstractButton.getHorizontalTextPosition(), viewRect, iconRect, textRect, paramAbstractButton.getText() == null ? 0 : paramAbstractButton.getIconTextGap());
  }
  
  private BasicButtonListener getButtonListener(AbstractButton paramAbstractButton)
  {
    MouseMotionListener[] arrayOfMouseMotionListener1 = paramAbstractButton.getMouseMotionListeners();
    if (arrayOfMouseMotionListener1 != null) {
      for (MouseMotionListener localMouseMotionListener : arrayOfMouseMotionListener1) {
        if ((localMouseMotionListener instanceof BasicButtonListener)) {
          return (BasicButtonListener)localMouseMotionListener;
        }
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */