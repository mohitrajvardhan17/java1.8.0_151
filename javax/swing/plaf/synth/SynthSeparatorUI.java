package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar.Separator;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.SeparatorUI;
import javax.swing.plaf.UIResource;

public class SynthSeparatorUI
  extends SeparatorUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  
  public SynthSeparatorUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthSeparatorUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    installDefaults((JSeparator)paramJComponent);
    installListeners((JSeparator)paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallListeners((JSeparator)paramJComponent);
    uninstallDefaults((JSeparator)paramJComponent);
  }
  
  public void installDefaults(JSeparator paramJSeparator)
  {
    updateStyle(paramJSeparator);
  }
  
  private void updateStyle(JSeparator paramJSeparator)
  {
    SynthContext localSynthContext = getContext(paramJSeparator, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if ((style != localSynthStyle) && ((paramJSeparator instanceof JToolBar.Separator)))
    {
      Object localObject = ((JToolBar.Separator)paramJSeparator).getSeparatorSize();
      if ((localObject == null) || ((localObject instanceof UIResource)))
      {
        localObject = (DimensionUIResource)style.get(localSynthContext, "ToolBar.separatorSize");
        if (localObject == null) {
          localObject = new DimensionUIResource(10, 10);
        }
        ((JToolBar.Separator)paramJSeparator).setSeparatorSize((Dimension)localObject);
      }
    }
    localSynthContext.dispose();
  }
  
  public void uninstallDefaults(JSeparator paramJSeparator)
  {
    SynthContext localSynthContext = getContext(paramJSeparator, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  public void installListeners(JSeparator paramJSeparator)
  {
    paramJSeparator.addPropertyChangeListener(this);
  }
  
  public void uninstallListeners(JSeparator paramJSeparator)
  {
    paramJSeparator.removePropertyChangeListener(this);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    JSeparator localJSeparator = (JSeparator)localSynthContext.getComponent();
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintSeparatorBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), localJSeparator.getOrientation());
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
    JSeparator localJSeparator = (JSeparator)paramSynthContext.getComponent();
    paramSynthContext.getPainter().paintSeparatorForeground(paramSynthContext, paramGraphics, 0, 0, localJSeparator.getWidth(), localJSeparator.getHeight(), localJSeparator.getOrientation());
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    JSeparator localJSeparator = (JSeparator)paramSynthContext.getComponent();
    paramSynthContext.getPainter().paintSeparatorBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, localJSeparator.getOrientation());
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    int i = style.getInt(localSynthContext, "Separator.thickness", 2);
    Insets localInsets = paramJComponent.getInsets();
    Dimension localDimension;
    if (((JSeparator)paramJComponent).getOrientation() == 1) {
      localDimension = new Dimension(left + right + i, top + bottom);
    } else {
      localDimension = new Dimension(left + right, top + bottom + i);
    }
    localSynthContext.dispose();
    return localDimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    return getPreferredSize(paramJComponent);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return new Dimension(32767, 32767);
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JSeparator)paramPropertyChangeEvent.getSource());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */