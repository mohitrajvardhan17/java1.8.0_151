package javax.swing.plaf.metal;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class MetalMenuBarUI
  extends BasicMenuBarUI
{
  public MetalMenuBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    if (paramJComponent == null) {
      throw new NullPointerException("Must pass in a non-null component");
    }
    return new MetalMenuBarUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    MetalToolBarUI.register(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    super.uninstallUI(paramJComponent);
    MetalToolBarUI.unregister(paramJComponent);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    boolean bool = paramJComponent.isOpaque();
    if (paramGraphics == null) {
      throw new NullPointerException("Graphics must be non-null");
    }
    if ((bool) && ((paramJComponent.getBackground() instanceof UIResource)) && (UIManager.get("MenuBar.gradient") != null))
    {
      if (MetalToolBarUI.doesMenuBarBorderToolBar((JMenuBar)paramJComponent))
      {
        JToolBar localJToolBar = (JToolBar)MetalToolBarUI.findRegisteredComponentOfType(paramJComponent, JToolBar.class);
        if ((localJToolBar.isOpaque()) && ((localJToolBar.getBackground() instanceof UIResource)))
        {
          MetalUtils.drawGradient(paramJComponent, paramGraphics, "MenuBar.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight() + localJToolBar.getHeight(), true);
          paint(paramGraphics, paramJComponent);
          return;
        }
      }
      MetalUtils.drawGradient(paramJComponent, paramGraphics, "MenuBar.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true);
      paint(paramGraphics, paramJComponent);
    }
    else
    {
      super.update(paramGraphics, paramJComponent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalMenuBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */