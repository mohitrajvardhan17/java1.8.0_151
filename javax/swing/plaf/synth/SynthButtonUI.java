package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class SynthButtonUI
  extends BasicButtonUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  
  public SynthButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthButtonUI();
  }
  
  protected void installDefaults(AbstractButton paramAbstractButton)
  {
    updateStyle(paramAbstractButton);
    LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", Boolean.TRUE);
  }
  
  protected void installListeners(AbstractButton paramAbstractButton)
  {
    super.installListeners(paramAbstractButton);
    paramAbstractButton.addPropertyChangeListener(this);
  }
  
  void updateStyle(AbstractButton paramAbstractButton)
  {
    SynthContext localSynthContext = getContext(paramAbstractButton, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      if ((paramAbstractButton.getMargin() == null) || ((paramAbstractButton.getMargin() instanceof UIResource)))
      {
        localObject = (Insets)style.get(localSynthContext, getPropertyPrefix() + "margin");
        if (localObject == null) {
          localObject = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
        }
        paramAbstractButton.setMargin((Insets)localObject);
      }
      Object localObject = style.get(localSynthContext, getPropertyPrefix() + "iconTextGap");
      if (localObject != null) {
        LookAndFeel.installProperty(paramAbstractButton, "iconTextGap", localObject);
      }
      localObject = style.get(localSynthContext, getPropertyPrefix() + "contentAreaFilled");
      LookAndFeel.installProperty(paramAbstractButton, "contentAreaFilled", localObject != null ? localObject : Boolean.TRUE);
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions(paramAbstractButton);
        installKeyboardActions(paramAbstractButton);
      }
    }
    localSynthContext.dispose();
  }
  
  protected void uninstallListeners(AbstractButton paramAbstractButton)
  {
    super.uninstallListeners(paramAbstractButton);
    paramAbstractButton.removePropertyChangeListener(this);
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    SynthContext localSynthContext = getContext(paramAbstractButton, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, getComponentState(paramJComponent));
  }
  
  SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    int i = 1;
    if (!paramJComponent.isEnabled()) {
      i = 8;
    }
    if (SynthLookAndFeel.getSelectedUI() == this) {
      return SynthLookAndFeel.getSelectedUIState() | 0x1;
    }
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();
    if (localButtonModel.isPressed()) {
      if (localButtonModel.isArmed()) {
        i = 4;
      } else {
        i = 2;
      }
    }
    if (localButtonModel.isRollover()) {
      i |= 0x2;
    }
    if (localButtonModel.isSelected()) {
      i |= 0x200;
    }
    if ((paramJComponent.isFocusOwner()) && (localAbstractButton.isFocusPainted())) {
      i |= 0x100;
    }
    if (((paramJComponent instanceof JButton)) && (((JButton)paramJComponent).isDefaultButton())) {
      i |= 0x400;
    }
    return i;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    if (paramJComponent == null) {
      throw new NullPointerException("Component must be non-null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("Width and height must be >= 0");
    }
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    String str = localAbstractButton.getText();
    if ((str == null) || ("".equals(str))) {
      return -1;
    }
    Insets localInsets = localAbstractButton.getInsets();
    Rectangle localRectangle1 = new Rectangle();
    Rectangle localRectangle2 = new Rectangle();
    Rectangle localRectangle3 = new Rectangle();
    x = left;
    y = top;
    width = (paramInt1 - (right + x));
    height = (paramInt2 - (bottom + y));
    SynthContext localSynthContext = getContext(localAbstractButton);
    FontMetrics localFontMetrics = localSynthContext.getComponent().getFontMetrics(localSynthContext.getStyle().getFont(localSynthContext));
    localSynthContext.getStyle().getGraphicsUtils(localSynthContext).layoutText(localSynthContext, localFontMetrics, localAbstractButton.getText(), localAbstractButton.getIcon(), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalTextPosition(), localAbstractButton.getVerticalTextPosition(), localRectangle1, localRectangle3, localRectangle2, localAbstractButton.getIconTextGap());
    View localView = (View)localAbstractButton.getClientProperty("html");
    int i;
    if (localView != null)
    {
      i = BasicHTML.getHTMLBaseline(localView, width, height);
      if (i >= 0) {
        i += y;
      }
    }
    else
    {
      i = y + localFontMetrics.getAscent();
    }
    localSynthContext.dispose();
    return i;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    paintBackground(localSynthContext, paramGraphics, paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramSynthContext.getComponent();
    paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
    paramGraphics.setFont(style.getFont(paramSynthContext));
    paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, localAbstractButton.getText(), getIcon(localAbstractButton), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalTextPosition(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getIconTextGap(), localAbstractButton.getDisplayedMnemonicIndex(), getTextShiftOffset(paramSynthContext));
  }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    if (((AbstractButton)paramJComponent).isContentAreaFilled()) {
      paramSynthContext.getPainter().paintButtonBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    }
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected Icon getDefaultIcon(AbstractButton paramAbstractButton)
  {
    SynthContext localSynthContext = getContext(paramAbstractButton);
    Icon localIcon = localSynthContext.getStyle().getIcon(localSynthContext, getPropertyPrefix() + "icon");
    localSynthContext.dispose();
    return localIcon;
  }
  
  protected Icon getIcon(AbstractButton paramAbstractButton)
  {
    Icon localIcon = paramAbstractButton.getIcon();
    ButtonModel localButtonModel = paramAbstractButton.getModel();
    if (!localButtonModel.isEnabled()) {
      localIcon = getSynthDisabledIcon(paramAbstractButton, localIcon);
    } else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed())) {
      localIcon = getPressedIcon(paramAbstractButton, getSelectedIcon(paramAbstractButton, localIcon));
    } else if ((paramAbstractButton.isRolloverEnabled()) && (localButtonModel.isRollover())) {
      localIcon = getRolloverIcon(paramAbstractButton, getSelectedIcon(paramAbstractButton, localIcon));
    } else if (localButtonModel.isSelected()) {
      localIcon = getSelectedIcon(paramAbstractButton, localIcon);
    } else {
      localIcon = getEnabledIcon(paramAbstractButton, localIcon);
    }
    if (localIcon == null) {
      return getDefaultIcon(paramAbstractButton);
    }
    return localIcon;
  }
  
  private Icon getIcon(AbstractButton paramAbstractButton, Icon paramIcon1, Icon paramIcon2, int paramInt)
  {
    Icon localIcon = paramIcon1;
    if (localIcon == null) {
      if ((paramIcon2 instanceof UIResource))
      {
        localIcon = getSynthIcon(paramAbstractButton, paramInt);
        if (localIcon == null) {
          localIcon = paramIcon2;
        }
      }
      else
      {
        localIcon = paramIcon2;
      }
    }
    return localIcon;
  }
  
  private Icon getSynthIcon(AbstractButton paramAbstractButton, int paramInt)
  {
    return style.getIcon(getContext(paramAbstractButton, paramInt), getPropertyPrefix() + "icon");
  }
  
  private Icon getEnabledIcon(AbstractButton paramAbstractButton, Icon paramIcon)
  {
    if (paramIcon == null) {
      paramIcon = getSynthIcon(paramAbstractButton, 1);
    }
    return paramIcon;
  }
  
  private Icon getSelectedIcon(AbstractButton paramAbstractButton, Icon paramIcon)
  {
    return getIcon(paramAbstractButton, paramAbstractButton.getSelectedIcon(), paramIcon, 512);
  }
  
  private Icon getRolloverIcon(AbstractButton paramAbstractButton, Icon paramIcon)
  {
    ButtonModel localButtonModel = paramAbstractButton.getModel();
    Icon localIcon;
    if (localButtonModel.isSelected()) {
      localIcon = getIcon(paramAbstractButton, paramAbstractButton.getRolloverSelectedIcon(), paramIcon, 514);
    } else {
      localIcon = getIcon(paramAbstractButton, paramAbstractButton.getRolloverIcon(), paramIcon, 2);
    }
    return localIcon;
  }
  
  private Icon getPressedIcon(AbstractButton paramAbstractButton, Icon paramIcon)
  {
    return getIcon(paramAbstractButton, paramAbstractButton.getPressedIcon(), paramIcon, 4);
  }
  
  private Icon getSynthDisabledIcon(AbstractButton paramAbstractButton, Icon paramIcon)
  {
    ButtonModel localButtonModel = paramAbstractButton.getModel();
    Icon localIcon;
    if (localButtonModel.isSelected()) {
      localIcon = getIcon(paramAbstractButton, paramAbstractButton.getDisabledSelectedIcon(), paramIcon, 520);
    } else {
      localIcon = getIcon(paramAbstractButton, paramAbstractButton.getDisabledIcon(), paramIcon, 8);
    }
    return localIcon;
  }
  
  private int getTextShiftOffset(SynthContext paramSynthContext)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramSynthContext.getComponent();
    ButtonModel localButtonModel = localAbstractButton.getModel();
    if ((localButtonModel.isArmed()) && (localButtonModel.isPressed()) && (localAbstractButton.getPressedIcon() == null)) {
      return paramSynthContext.getStyle().getInt(paramSynthContext, getPropertyPrefix() + "textShiftOffset", 0);
    }
    return 0;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    if ((paramJComponent.getComponentCount() > 0) && (paramJComponent.getLayout() != null)) {
      return null;
    }
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    SynthContext localSynthContext = getContext(paramJComponent);
    Dimension localDimension = localSynthContext.getStyle().getGraphicsUtils(localSynthContext).getMinimumSize(localSynthContext, localSynthContext.getStyle().getFont(localSynthContext), localAbstractButton.getText(), getSizingIcon(localAbstractButton), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalTextPosition(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getIconTextGap(), localAbstractButton.getDisplayedMnemonicIndex());
    localSynthContext.dispose();
    return localDimension;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    if ((paramJComponent.getComponentCount() > 0) && (paramJComponent.getLayout() != null)) {
      return null;
    }
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    SynthContext localSynthContext = getContext(paramJComponent);
    Dimension localDimension = localSynthContext.getStyle().getGraphicsUtils(localSynthContext).getPreferredSize(localSynthContext, localSynthContext.getStyle().getFont(localSynthContext), localAbstractButton.getText(), getSizingIcon(localAbstractButton), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalTextPosition(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getIconTextGap(), localAbstractButton.getDisplayedMnemonicIndex());
    localSynthContext.dispose();
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    if ((paramJComponent.getComponentCount() > 0) && (paramJComponent.getLayout() != null)) {
      return null;
    }
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    SynthContext localSynthContext = getContext(paramJComponent);
    Dimension localDimension = localSynthContext.getStyle().getGraphicsUtils(localSynthContext).getMaximumSize(localSynthContext, localSynthContext.getStyle().getFont(localSynthContext), localAbstractButton.getText(), getSizingIcon(localAbstractButton), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalTextPosition(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getIconTextGap(), localAbstractButton.getDisplayedMnemonicIndex());
    localSynthContext.dispose();
    return localDimension;
  }
  
  protected Icon getSizingIcon(AbstractButton paramAbstractButton)
  {
    Icon localIcon = getEnabledIcon(paramAbstractButton, paramAbstractButton.getIcon());
    if (localIcon == null) {
      localIcon = getDefaultIcon(paramAbstractButton);
    }
    return localIcon;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((AbstractButton)paramPropertyChangeEvent.getSource());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */