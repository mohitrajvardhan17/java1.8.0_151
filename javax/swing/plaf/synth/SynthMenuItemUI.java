package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuItemUI;
import sun.swing.MenuItemLayoutHelper;

public class SynthMenuItemUI
  extends BasicMenuItemUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private SynthStyle accStyle;
  
  public SynthMenuItemUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthMenuItemUI();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    super.uninstallUI(paramJComponent);
    JComponent localJComponent = MenuItemLayoutHelper.getMenuItemParent((JMenuItem)paramJComponent);
    if (localJComponent != null) {
      localJComponent.putClientProperty(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, null);
    }
  }
  
  protected void installDefaults()
  {
    updateStyle(menuItem);
  }
  
  protected void installListeners()
  {
    super.installListeners();
    menuItem.addPropertyChangeListener(this);
  }
  
  private void updateStyle(JMenuItem paramJMenuItem)
  {
    SynthContext localSynthContext = getContext(paramJMenuItem, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (localSynthStyle != style)
    {
      localObject1 = getPropertyPrefix();
      Object localObject2 = style.get(localSynthContext, (String)localObject1 + ".textIconGap");
      if (localObject2 != null) {
        LookAndFeel.installProperty(paramJMenuItem, "iconTextGap", localObject2);
      }
      defaultTextIconGap = paramJMenuItem.getIconTextGap();
      if ((menuItem.getMargin() == null) || ((menuItem.getMargin() instanceof UIResource)))
      {
        Insets localInsets = (Insets)style.get(localSynthContext, (String)localObject1 + ".margin");
        if (localInsets == null) {
          localInsets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
        }
        menuItem.setMargin(localInsets);
      }
      acceleratorDelimiter = style.getString(localSynthContext, (String)localObject1 + ".acceleratorDelimiter", "+");
      arrowIcon = style.getIcon(localSynthContext, (String)localObject1 + ".arrowIcon");
      checkIcon = style.getIcon(localSynthContext, (String)localObject1 + ".checkIcon");
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    localSynthContext.dispose();
    Object localObject1 = getContext(paramJMenuItem, Region.MENU_ITEM_ACCELERATOR, 1);
    accStyle = SynthLookAndFeel.updateStyle((SynthContext)localObject1, this);
    ((SynthContext)localObject1).dispose();
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext1 = getContext(menuItem, 1);
    style.uninstallDefaults(localSynthContext1);
    localSynthContext1.dispose();
    style = null;
    SynthContext localSynthContext2 = getContext(menuItem, Region.MENU_ITEM_ACCELERATOR, 1);
    accStyle.uninstallDefaults(localSynthContext2);
    localSynthContext2.dispose();
    accStyle = null;
    super.uninstallDefaults();
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    menuItem.removePropertyChangeListener(this);
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, getComponentState(paramJComponent));
  }
  
  SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  SynthContext getContext(JComponent paramJComponent, Region paramRegion)
  {
    return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion));
  }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, paramRegion, accStyle, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    int i;
    if (!paramJComponent.isEnabled()) {
      i = 8;
    } else if (menuItem.isArmed()) {
      i = 2;
    } else {
      i = SynthLookAndFeel.getComponentState(paramJComponent);
    }
    if (menuItem.isSelected()) {
      i |= 0x200;
    }
    return i;
  }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion)
  {
    return getComponentState(paramJComponent);
  }
  
  protected Dimension getPreferredMenuItemSize(JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, int paramInt)
  {
    SynthContext localSynthContext1 = getContext(paramJComponent);
    SynthContext localSynthContext2 = getContext(paramJComponent, Region.MENU_ITEM_ACCELERATOR);
    Dimension localDimension = SynthGraphicsUtils.getPreferredMenuItemSize(localSynthContext1, localSynthContext2, paramJComponent, paramIcon1, paramIcon2, paramInt, acceleratorDelimiter, MenuItemLayoutHelper.useCheckAndArrow(menuItem), getPropertyPrefix());
    localSynthContext1.dispose();
    localSynthContext2.dispose();
    return localDimension;
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
    SynthContext localSynthContext = getContext(menuItem, Region.MENU_ITEM_ACCELERATOR);
    String str = getPropertyPrefix();
    Icon localIcon1 = style.getIcon(paramSynthContext, str + ".checkIcon");
    Icon localIcon2 = style.getIcon(paramSynthContext, str + ".arrowIcon");
    SynthGraphicsUtils.paint(paramSynthContext, localSynthContext, paramGraphics, localIcon1, localIcon2, acceleratorDelimiter, defaultTextIconGap, getPropertyPrefix());
    localSynthContext.dispose();
  }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthGraphicsUtils.paintBackground(paramSynthContext, paramGraphics, paramJComponent);
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintMenuItemBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JMenuItem)paramPropertyChangeEvent.getSource());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */