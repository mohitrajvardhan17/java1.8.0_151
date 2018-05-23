package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;

public class SynthEditorPaneUI
  extends BasicEditorPaneUI
  implements SynthUI
{
  private SynthStyle style;
  private Boolean localTrue = Boolean.TRUE;
  
  public SynthEditorPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthEditorPaneUI();
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    JTextComponent localJTextComponent = getComponent();
    Object localObject = localJTextComponent.getClientProperty("JEditorPane.honorDisplayProperties");
    if (localObject == null) {
      localJTextComponent.putClientProperty("JEditorPane.honorDisplayProperties", localTrue);
    }
    updateStyle(getComponent());
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(getComponent(), 1);
    JTextComponent localJTextComponent = getComponent();
    localJTextComponent.putClientProperty("caretAspectRatio", null);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    Object localObject = localJTextComponent.getClientProperty("JEditorPane.honorDisplayProperties");
    if (localObject == localTrue) {
      localJTextComponent.putClientProperty("JEditorPane.honorDisplayProperties", Boolean.FALSE);
    }
    super.uninstallDefaults();
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JTextComponent)paramPropertyChangeEvent.getSource());
    }
    super.propertyChange(paramPropertyChangeEvent);
  }
  
  private void updateStyle(JTextComponent paramJTextComponent)
  {
    SynthContext localSynthContext = getContext(paramJTextComponent, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      SynthTextFieldUI.updateStyle(paramJTextComponent, localSynthContext, getPropertyPrefix());
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
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
    paintBackground(localSynthContext, paramGraphics, paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    super.paint(paramGraphics, getComponent());
  }
  
  protected void paintBackground(Graphics paramGraphics) {}
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    paramSynthContext.getPainter().paintEditorPaneBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintEditorPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthEditorPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */