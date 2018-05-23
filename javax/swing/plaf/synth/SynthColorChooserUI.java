package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicColorChooserUI;

public class SynthColorChooserUI
  extends BasicColorChooserUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  
  public SynthColorChooserUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthColorChooserUI();
  }
  
  protected AbstractColorChooserPanel[] createDefaultChoosers()
  {
    SynthContext localSynthContext = getContext(chooser, 1);
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = (AbstractColorChooserPanel[])localSynthContext.getStyle().get(localSynthContext, "ColorChooser.panels");
    localSynthContext.dispose();
    if (arrayOfAbstractColorChooserPanel == null) {
      arrayOfAbstractColorChooserPanel = ColorChooserComponentFactory.getDefaultChooserPanels();
    }
    return arrayOfAbstractColorChooserPanel;
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    updateStyle(chooser);
  }
  
  private void updateStyle(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent, 1);
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(chooser, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    super.uninstallDefaults();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    chooser.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners()
  {
    chooser.removePropertyChangeListener(this);
    super.uninstallListeners();
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
    localSynthContext.getPainter().paintColorChooserBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
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
    paramSynthContext.getPainter().paintColorChooserBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JColorChooser)paramPropertyChangeEvent.getSource());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthColorChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */