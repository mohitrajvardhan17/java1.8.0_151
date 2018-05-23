package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class SynthMenuBarUI
  extends BasicMenuBarUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  
  public SynthMenuBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthMenuBarUI();
  }
  
  protected void installDefaults()
  {
    if ((menuBar.getLayout() == null) || ((menuBar.getLayout() instanceof UIResource))) {
      menuBar.setLayout(new SynthMenuLayout(menuBar, 2));
    }
    updateStyle(menuBar);
  }
  
  protected void installListeners()
  {
    super.installListeners();
    menuBar.addPropertyChangeListener(this);
  }
  
  private void updateStyle(JMenuBar paramJMenuBar)
  {
    SynthContext localSynthContext = getContext(paramJMenuBar, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if ((style != localSynthStyle) && (localSynthStyle != null))
    {
      uninstallKeyboardActions();
      installKeyboardActions();
    }
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(menuBar, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    menuBar.removePropertyChangeListener(this);
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintMenuBarBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintMenuBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JMenuBar)paramPropertyChangeEvent.getSource());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthMenuBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */