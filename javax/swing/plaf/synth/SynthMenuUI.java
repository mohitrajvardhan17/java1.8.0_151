package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuUI;
import sun.swing.MenuItemLayoutHelper;

public class SynthMenuUI
  extends BasicMenuUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private SynthStyle accStyle;
  
  public SynthMenuUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthMenuUI();
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
    SynthStyle localSynthStyle = style;
    SynthContext localSynthContext = getContext(paramJMenuItem, 1);
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (localSynthStyle != style)
    {
      localObject = getPropertyPrefix();
      defaultTextIconGap = style.getInt(localSynthContext, (String)localObject + ".textIconGap", 4);
      if ((menuItem.getMargin() == null) || ((menuItem.getMargin() instanceof UIResource)))
      {
        Insets localInsets = (Insets)style.get(localSynthContext, (String)localObject + ".margin");
        if (localInsets == null) {
          localInsets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
        }
        menuItem.setMargin(localInsets);
      }
      acceleratorDelimiter = style.getString(localSynthContext, (String)localObject + ".acceleratorDelimiter", "+");
      if (MenuItemLayoutHelper.useCheckAndArrow(menuItem))
      {
        checkIcon = style.getIcon(localSynthContext, (String)localObject + ".checkIcon");
        arrowIcon = style.getIcon(localSynthContext, (String)localObject + ".arrowIcon");
      }
      else
      {
        checkIcon = null;
        arrowIcon = null;
      }
      ((JMenu)menuItem).setDelay(style.getInt(localSynthContext, (String)localObject + ".delay", 200));
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    localSynthContext.dispose();
    Object localObject = getContext(paramJMenuItem, Region.MENU_ITEM_ACCELERATOR, 1);
    accStyle = SynthLookAndFeel.updateStyle((SynthContext)localObject, this);
    ((SynthContext)localObject).dispose();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    super.uninstallUI(paramJComponent);
    JComponent localJComponent = MenuItemLayoutHelper.getMenuItemParent((JMenuItem)paramJComponent);
    if (localJComponent != null) {
      localJComponent.putClientProperty(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, null);
    }
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
    if (!paramJComponent.isEnabled()) {
      return 8;
    }
    int i;
    if (menuItem.isArmed()) {
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
    localSynthContext.getPainter().paintMenuBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
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
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintMenuBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if ((SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) || ((paramPropertyChangeEvent.getPropertyName().equals("ancestor")) && (UIManager.getBoolean("Menu.useMenuBarForTopLevelMenus")))) {
      updateStyle((JMenu)paramPropertyChangeEvent.getSource());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */