package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.AbstractButton;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class MetalToolTipUI
  extends BasicToolTipUI
{
  static MetalToolTipUI sharedInstance = new MetalToolTipUI();
  private Font smallFont;
  private JToolTip tip;
  public static final int padSpaceBetweenStrings = 12;
  private String acceleratorDelimiter;
  
  public MetalToolTipUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return sharedInstance;
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    tip = ((JToolTip)paramJComponent);
    Font localFont = paramJComponent.getFont();
    smallFont = new Font(localFont.getName(), localFont.getStyle(), localFont.getSize() - 2);
    acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
    if (acceleratorDelimiter == null) {
      acceleratorDelimiter = "-";
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    super.uninstallUI(paramJComponent);
    tip = null;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    JToolTip localJToolTip = (JToolTip)paramJComponent;
    Font localFont = paramJComponent.getFont();
    FontMetrics localFontMetrics1 = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, localFont);
    Dimension localDimension = paramJComponent.getSize();
    paramGraphics.setColor(paramJComponent.getForeground());
    String str1 = localJToolTip.getTipText();
    if (str1 == null) {
      str1 = "";
    }
    String str2 = getAcceleratorString(localJToolTip);
    FontMetrics localFontMetrics2 = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, smallFont);
    int j = calcAccelSpacing(paramJComponent, localFontMetrics2, str2);
    Insets localInsets = localJToolTip.getInsets();
    Rectangle localRectangle = new Rectangle(left + 3, top, width - (left + right) - 6 - j, height - (top + bottom));
    View localView = (View)paramJComponent.getClientProperty("html");
    int i;
    if (localView != null)
    {
      localView.paint(paramGraphics, localRectangle);
      i = BasicHTML.getHTMLBaseline(localView, width, height);
    }
    else
    {
      paramGraphics.setFont(localFont);
      SwingUtilities2.drawString(localJToolTip, paramGraphics, str1, x, y + localFontMetrics1.getAscent());
      i = localFontMetrics1.getAscent();
    }
    if (!str2.equals(""))
    {
      paramGraphics.setFont(smallFont);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      SwingUtilities2.drawString(localJToolTip, paramGraphics, str2, localJToolTip.getWidth() - 1 - right - j + 12 - 3, y + i);
    }
  }
  
  private int calcAccelSpacing(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString)
  {
    return paramString.equals("") ? 0 : 12 + SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics, paramString);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = super.getPreferredSize(paramJComponent);
    String str = getAcceleratorString((JToolTip)paramJComponent);
    if (!str.equals("")) {
      width += calcAccelSpacing(paramJComponent, paramJComponent.getFontMetrics(smallFont), str);
    }
    return localDimension;
  }
  
  protected boolean isAcceleratorHidden()
  {
    Boolean localBoolean = (Boolean)UIManager.get("ToolTip.hideAccelerator");
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  private String getAcceleratorString(JToolTip paramJToolTip)
  {
    tip = paramJToolTip;
    String str = getAcceleratorString();
    tip = null;
    return str;
  }
  
  public String getAcceleratorString()
  {
    if ((tip == null) || (isAcceleratorHidden())) {
      return "";
    }
    JComponent localJComponent = tip.getComponent();
    if (!(localJComponent instanceof AbstractButton)) {
      return "";
    }
    KeyStroke[] arrayOfKeyStroke = localJComponent.getInputMap(2).keys();
    if (arrayOfKeyStroke == null) {
      return "";
    }
    String str = "";
    int i = 0;
    if (i < arrayOfKeyStroke.length)
    {
      int j = arrayOfKeyStroke[i].getModifiers();
      str = KeyEvent.getKeyModifiersText(j) + acceleratorDelimiter + KeyEvent.getKeyText(arrayOfKeyStroke[i].getKeyCode());
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalToolTipUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */