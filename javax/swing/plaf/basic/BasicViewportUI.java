package javax.swing.plaf.basic;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ViewportUI;

public class BasicViewportUI
  extends ViewportUI
{
  private static ViewportUI viewportUI;
  
  public BasicViewportUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    if (viewportUI == null) {
      viewportUI = new BasicViewportUI();
    }
    return viewportUI;
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    installDefaults(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults(paramJComponent);
    super.uninstallUI(paramJComponent);
  }
  
  protected void installDefaults(JComponent paramJComponent)
  {
    LookAndFeel.installColorsAndFont(paramJComponent, "Viewport.background", "Viewport.foreground", "Viewport.font");
    LookAndFeel.installProperty(paramJComponent, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults(JComponent paramJComponent) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicViewportUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */