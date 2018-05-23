package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

public class SynthPanelUI
  extends BasicPanelUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  
  public SynthPanelUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthPanelUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    JPanel localJPanel = (JPanel)paramJComponent;
    super.installUI(paramJComponent);
    installListeners(localJPanel);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    JPanel localJPanel = (JPanel)paramJComponent;
    uninstallListeners(localJPanel);
    super.uninstallUI(paramJComponent);
  }
  
  protected void installListeners(JPanel paramJPanel)
  {
    paramJPanel.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners(JPanel paramJPanel)
  {
    paramJPanel.removePropertyChangeListener(this);
  }
  
  protected void installDefaults(JPanel paramJPanel)
  {
    updateStyle(paramJPanel);
  }
  
  protected void uninstallDefaults(JPanel paramJPanel)
  {
    SynthContext localSynthContext = getContext(paramJPanel, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  private void updateStyle(JPanel paramJPanel)
  {
    SynthContext localSynthContext = getContext(paramJPanel, 1);
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
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
    localSynthContext.getPainter().paintPanelBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
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
    paramSynthContext.getPainter().paintPanelBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JPanel)paramPropertyChangeEvent.getSource());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthPanelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */