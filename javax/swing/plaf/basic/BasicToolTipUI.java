package javax.swing.plaf.basic;

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
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolTipUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class BasicToolTipUI
  extends ToolTipUI
{
  static BasicToolTipUI sharedInstance = new BasicToolTipUI();
  private static PropertyChangeListener sharedPropertyChangedListener;
  private PropertyChangeListener propertyChangeListener;
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return sharedInstance;
  }
  
  public BasicToolTipUI() {}
  
  public void installUI(JComponent paramJComponent)
  {
    installDefaults(paramJComponent);
    installComponents(paramJComponent);
    installListeners(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults(paramJComponent);
    uninstallComponents(paramJComponent);
    uninstallListeners(paramJComponent);
  }
  
  protected void installDefaults(JComponent paramJComponent)
  {
    LookAndFeel.installColorsAndFont(paramJComponent, "ToolTip.background", "ToolTip.foreground", "ToolTip.font");
    LookAndFeel.installProperty(paramJComponent, "opaque", Boolean.TRUE);
    componentChanged(paramJComponent);
  }
  
  protected void uninstallDefaults(JComponent paramJComponent)
  {
    LookAndFeel.uninstallBorder(paramJComponent);
  }
  
  private void installComponents(JComponent paramJComponent)
  {
    BasicHTML.updateRenderer(paramJComponent, ((JToolTip)paramJComponent).getTipText());
  }
  
  private void uninstallComponents(JComponent paramJComponent)
  {
    BasicHTML.updateRenderer(paramJComponent, "");
  }
  
  protected void installListeners(JComponent paramJComponent)
  {
    propertyChangeListener = createPropertyChangeListener(paramJComponent);
    paramJComponent.addPropertyChangeListener(propertyChangeListener);
  }
  
  protected void uninstallListeners(JComponent paramJComponent)
  {
    paramJComponent.removePropertyChangeListener(propertyChangeListener);
    propertyChangeListener = null;
  }
  
  private PropertyChangeListener createPropertyChangeListener(JComponent paramJComponent)
  {
    if (sharedPropertyChangedListener == null) {
      sharedPropertyChangedListener = new PropertyChangeHandler(null);
    }
    return sharedPropertyChangedListener;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    Font localFont = paramJComponent.getFont();
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, localFont);
    Dimension localDimension = paramJComponent.getSize();
    paramGraphics.setColor(paramJComponent.getForeground());
    String str = ((JToolTip)paramJComponent).getTipText();
    if (str == null) {
      str = "";
    }
    Insets localInsets = paramJComponent.getInsets();
    Rectangle localRectangle = new Rectangle(left + 3, top, width - (left + right) - 6, height - (top + bottom));
    View localView = (View)paramJComponent.getClientProperty("html");
    if (localView != null)
    {
      localView.paint(paramGraphics, localRectangle);
    }
    else
    {
      paramGraphics.setFont(localFont);
      SwingUtilities2.drawString(paramJComponent, paramGraphics, str, x, y + localFontMetrics.getAscent());
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Font localFont = paramJComponent.getFont();
    FontMetrics localFontMetrics = paramJComponent.getFontMetrics(localFont);
    Insets localInsets = paramJComponent.getInsets();
    Dimension localDimension = new Dimension(left + right, top + bottom);
    String str = ((JToolTip)paramJComponent).getTipText();
    if ((str == null) || (str.equals("")))
    {
      str = "";
    }
    else
    {
      Object localObject = paramJComponent != null ? (View)paramJComponent.getClientProperty("html") : null;
      if (localObject != null)
      {
        width += (int)((View)localObject).getPreferredSpan(0) + 6;
        height += (int)((View)localObject).getPreferredSpan(1);
      }
      else
      {
        width += SwingUtilities2.stringWidth(paramJComponent, localFontMetrics, str) + 6;
        height += localFontMetrics.getHeight();
      }
    }
    return localDimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    Dimension localDimension = getPreferredSize(paramJComponent);
    View localView = (View)paramJComponent.getClientProperty("html");
    if (localView != null)
    {
      Dimension tmp21_20 = localDimension;
      2120width = ((int)(2120width - (localView.getPreferredSpan(0) - localView.getMinimumSpan(0))));
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    Dimension localDimension = getPreferredSize(paramJComponent);
    View localView = (View)paramJComponent.getClientProperty("html");
    if (localView != null)
    {
      Dimension tmp21_20 = localDimension;
      2120width = ((int)(2120width + (localView.getMaximumSpan(0) - localView.getPreferredSpan(0))));
    }
    return localDimension;
  }
  
  private void componentChanged(JComponent paramJComponent)
  {
    JComponent localJComponent = ((JToolTip)paramJComponent).getComponent();
    if ((localJComponent != null) && (!localJComponent.isEnabled()))
    {
      if (UIManager.getBorder("ToolTip.borderInactive") != null) {
        LookAndFeel.installBorder(paramJComponent, "ToolTip.borderInactive");
      } else {
        LookAndFeel.installBorder(paramJComponent, "ToolTip.border");
      }
      if (UIManager.getColor("ToolTip.backgroundInactive") != null) {
        LookAndFeel.installColors(paramJComponent, "ToolTip.backgroundInactive", "ToolTip.foregroundInactive");
      } else {
        LookAndFeel.installColors(paramJComponent, "ToolTip.background", "ToolTip.foreground");
      }
    }
    else
    {
      LookAndFeel.installBorder(paramJComponent, "ToolTip.border");
      LookAndFeel.installColors(paramJComponent, "ToolTip.background", "ToolTip.foreground");
    }
  }
  
  private static class PropertyChangeHandler
    implements PropertyChangeListener
  {
    private PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str1 = paramPropertyChangeEvent.getPropertyName();
      JToolTip localJToolTip;
      if ((str1.equals("tiptext")) || ("font".equals(str1)) || ("foreground".equals(str1)))
      {
        localJToolTip = (JToolTip)paramPropertyChangeEvent.getSource();
        String str2 = localJToolTip.getTipText();
        BasicHTML.updateRenderer(localJToolTip, str2);
      }
      else if ("component".equals(str1))
      {
        localJToolTip = (JToolTip)paramPropertyChangeEvent.getSource();
        if ((localJToolTip.getUI() instanceof BasicToolTipUI)) {
          ((BasicToolTipUI)localJToolTip.getUI()).componentChanged(localJToolTip);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicToolTipUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */