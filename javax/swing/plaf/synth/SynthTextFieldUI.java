package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

public class SynthTextFieldUI
  extends BasicTextFieldUI
  implements SynthUI
{
  private Handler handler = new Handler(null);
  private SynthStyle style;
  
  public SynthTextFieldUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthTextFieldUI();
  }
  
  private void updateStyle(JTextComponent paramJTextComponent)
  {
    SynthContext localSynthContext = getContext(paramJTextComponent, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      updateStyle(paramJTextComponent, localSynthContext, getPropertyPrefix());
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    localSynthContext.dispose();
  }
  
  static void updateStyle(JTextComponent paramJTextComponent, SynthContext paramSynthContext, String paramString)
  {
    SynthStyle localSynthStyle = paramSynthContext.getStyle();
    Color localColor1 = paramJTextComponent.getCaretColor();
    if ((localColor1 == null) || ((localColor1 instanceof UIResource))) {
      paramJTextComponent.setCaretColor((Color)localSynthStyle.get(paramSynthContext, paramString + ".caretForeground"));
    }
    Color localColor2 = paramJTextComponent.getForeground();
    if ((localColor2 == null) || ((localColor2 instanceof UIResource)))
    {
      localColor2 = localSynthStyle.getColorForState(paramSynthContext, ColorType.TEXT_FOREGROUND);
      if (localColor2 != null) {
        paramJTextComponent.setForeground(localColor2);
      }
    }
    Object localObject1 = localSynthStyle.get(paramSynthContext, paramString + ".caretAspectRatio");
    if ((localObject1 instanceof Number)) {
      paramJTextComponent.putClientProperty("caretAspectRatio", localObject1);
    }
    paramSynthContext.setComponentState(768);
    Color localColor3 = paramJTextComponent.getSelectionColor();
    if ((localColor3 == null) || ((localColor3 instanceof UIResource))) {
      paramJTextComponent.setSelectionColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_BACKGROUND));
    }
    Color localColor4 = paramJTextComponent.getSelectedTextColor();
    if ((localColor4 == null) || ((localColor4 instanceof UIResource))) {
      paramJTextComponent.setSelectedTextColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
    }
    paramSynthContext.setComponentState(8);
    Color localColor5 = paramJTextComponent.getDisabledTextColor();
    if ((localColor5 == null) || ((localColor5 instanceof UIResource))) {
      paramJTextComponent.setDisabledTextColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
    }
    Insets localInsets = paramJTextComponent.getMargin();
    if ((localInsets == null) || ((localInsets instanceof UIResource)))
    {
      localInsets = (Insets)localSynthStyle.get(paramSynthContext, paramString + ".margin");
      if (localInsets == null) {
        localInsets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
      }
      paramJTextComponent.setMargin(localInsets);
    }
    Caret localCaret = paramJTextComponent.getCaret();
    if ((localCaret instanceof UIResource))
    {
      Object localObject2 = localSynthStyle.get(paramSynthContext, paramString + ".caretBlinkRate");
      if ((localObject2 != null) && ((localObject2 instanceof Integer)))
      {
        Integer localInteger = (Integer)localObject2;
        localCaret.setBlinkRate(localInteger.intValue());
      }
    }
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
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
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    paramSynthContext.getPainter().paintTextFieldBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintTextFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void paintBackground(Graphics paramGraphics) {}
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JTextComponent)paramPropertyChangeEvent.getSource());
    }
    super.propertyChange(paramPropertyChangeEvent);
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    updateStyle(getComponent());
    getComponent().addFocusListener(handler);
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(getComponent(), 1);
    getComponent().putClientProperty("caretAspectRatio", null);
    getComponent().removeFocusListener(handler);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    super.uninstallDefaults();
  }
  
  private final class Handler
    implements FocusListener
  {
    private Handler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      getComponent().repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      getComponent().repaint();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */