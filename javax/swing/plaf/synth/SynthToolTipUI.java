package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.View;

public class SynthToolTipUI
  extends BasicToolTipUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  
  public SynthToolTipUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthToolTipUI();
  }
  
  protected void installDefaults(JComponent paramJComponent)
  {
    updateStyle(paramJComponent);
  }
  
  private void updateStyle(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent, 1);
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  protected void installListeners(JComponent paramJComponent)
  {
    paramJComponent.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners(JComponent paramJComponent)
  {
    paramJComponent.removePropertyChangeListener(this);
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
    JComponent localJComponent = ((JToolTip)paramJComponent).getComponent();
    if ((localJComponent != null) && (!localJComponent.isEnabled())) {
      return 8;
    }
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintToolTipBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintToolTipBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    JToolTip localJToolTip = (JToolTip)paramSynthContext.getComponent();
    Insets localInsets = localJToolTip.getInsets();
    View localView = (View)localJToolTip.getClientProperty("html");
    if (localView != null)
    {
      Rectangle localRectangle = new Rectangle(left, top, localJToolTip.getWidth() - (left + right), localJToolTip.getHeight() - (top + bottom));
      localView.paint(paramGraphics, localRectangle);
    }
    else
    {
      paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
      paramGraphics.setFont(style.getFont(paramSynthContext));
      paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, localJToolTip.getTipText(), left, top, -1);
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    Insets localInsets = paramJComponent.getInsets();
    Dimension localDimension = new Dimension(left + right, top + bottom);
    String str = ((JToolTip)paramJComponent).getTipText();
    if (str != null)
    {
      Object localObject = paramJComponent != null ? (View)paramJComponent.getClientProperty("html") : null;
      if (localObject != null)
      {
        width += (int)((View)localObject).getPreferredSpan(0);
        height += (int)((View)localObject).getPreferredSpan(1);
      }
      else
      {
        Font localFont = localSynthContext.getStyle().getFont(localSynthContext);
        FontMetrics localFontMetrics = paramJComponent.getFontMetrics(localFont);
        width += localSynthContext.getStyle().getGraphicsUtils(localSynthContext).computeStringWidth(localSynthContext, localFont, localFontMetrics, str);
        height += localFontMetrics.getHeight();
      }
    }
    localSynthContext.dispose();
    return localDimension;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JToolTip)paramPropertyChangeEvent.getSource());
    }
    String str1 = paramPropertyChangeEvent.getPropertyName();
    if ((str1.equals("tiptext")) || ("font".equals(str1)) || ("foreground".equals(str1)))
    {
      JToolTip localJToolTip = (JToolTip)paramPropertyChangeEvent.getSource();
      String str2 = localJToolTip.getTipText();
      BasicHTML.updateRenderer(localJToolTip, str2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthToolTipUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */