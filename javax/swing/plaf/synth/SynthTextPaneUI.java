package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SynthTextPaneUI
  extends SynthEditorPaneUI
{
  public SynthTextPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthTextPaneUI();
  }
  
  protected String getPropertyPrefix()
  {
    return "TextPane";
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    updateForeground(paramJComponent.getForeground());
    updateFont(paramJComponent.getFont());
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.propertyChange(paramPropertyChangeEvent);
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str.equals("foreground"))
    {
      updateForeground((Color)paramPropertyChangeEvent.getNewValue());
    }
    else if (str.equals("font"))
    {
      updateFont((Font)paramPropertyChangeEvent.getNewValue());
    }
    else if (str.equals("document"))
    {
      JTextComponent localJTextComponent = getComponent();
      updateForeground(localJTextComponent.getForeground());
      updateFont(localJTextComponent.getFont());
    }
  }
  
  private void updateForeground(Color paramColor)
  {
    StyledDocument localStyledDocument = (StyledDocument)getComponent().getDocument();
    Style localStyle = localStyledDocument.getStyle("default");
    if (localStyle == null) {
      return;
    }
    if (paramColor == null) {
      localStyle.removeAttribute(StyleConstants.Foreground);
    } else {
      StyleConstants.setForeground(localStyle, paramColor);
    }
  }
  
  private void updateFont(Font paramFont)
  {
    StyledDocument localStyledDocument = (StyledDocument)getComponent().getDocument();
    Style localStyle = localStyledDocument.getStyle("default");
    if (localStyle == null) {
      return;
    }
    if (paramFont == null)
    {
      localStyle.removeAttribute(StyleConstants.FontFamily);
      localStyle.removeAttribute(StyleConstants.FontSize);
      localStyle.removeAttribute(StyleConstants.Bold);
      localStyle.removeAttribute(StyleConstants.Italic);
    }
    else
    {
      StyleConstants.setFontFamily(localStyle, paramFont.getName());
      StyleConstants.setFontSize(localStyle, paramFont.getSize());
      StyleConstants.setBold(localStyle, paramFont.isBold());
      StyleConstants.setItalic(localStyle, paramFont.isItalic());
    }
  }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    paramSynthContext.getPainter().paintTextPaneBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintTextPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthTextPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */