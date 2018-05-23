package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.plaf.LabelUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicLabelUI
  extends LabelUI
  implements PropertyChangeListener
{
  protected static BasicLabelUI labelUI = new BasicLabelUI();
  private static final Object BASIC_LABEL_UI_KEY = new Object();
  private Rectangle paintIconR = new Rectangle();
  private Rectangle paintTextR = new Rectangle();
  
  public BasicLabelUI() {}
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("press"));
    paramLazyActionMap.put(new Actions("release"));
  }
  
  protected String layoutCL(JLabel paramJLabel, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3)
  {
    return SwingUtilities.layoutCompoundLabel(paramJLabel, paramFontMetrics, paramString, paramIcon, paramJLabel.getVerticalAlignment(), paramJLabel.getHorizontalAlignment(), paramJLabel.getVerticalTextPosition(), paramJLabel.getHorizontalTextPosition(), paramRectangle1, paramRectangle2, paramRectangle3, paramJLabel.getIconTextGap());
  }
  
  protected void paintEnabledText(JLabel paramJLabel, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2)
  {
    int i = paramJLabel.getDisplayedMnemonicIndex();
    paramGraphics.setColor(paramJLabel.getForeground());
    SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1, paramInt2);
  }
  
  protected void paintDisabledText(JLabel paramJLabel, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2)
  {
    int i = paramJLabel.getDisplayedMnemonicIndex();
    Color localColor = paramJLabel.getBackground();
    paramGraphics.setColor(localColor.brighter());
    SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1 + 1, paramInt2 + 1);
    paramGraphics.setColor(localColor.darker());
    SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1, paramInt2);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    JLabel localJLabel = (JLabel)paramJComponent;
    String str1 = localJLabel.getText();
    Icon localIcon = localJLabel.isEnabled() ? localJLabel.getIcon() : localJLabel.getDisabledIcon();
    if ((localIcon == null) && (str1 == null)) {
      return;
    }
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(localJLabel, paramGraphics);
    String str2 = layout(localJLabel, localFontMetrics, paramJComponent.getWidth(), paramJComponent.getHeight());
    if (localIcon != null) {
      localIcon.paintIcon(paramJComponent, paramGraphics, paintIconR.x, paintIconR.y);
    }
    if (str1 != null)
    {
      View localView = (View)paramJComponent.getClientProperty("html");
      if (localView != null)
      {
        localView.paint(paramGraphics, paintTextR);
      }
      else
      {
        int i = paintTextR.x;
        int j = paintTextR.y + localFontMetrics.getAscent();
        if (localJLabel.isEnabled()) {
          paintEnabledText(localJLabel, paramGraphics, str2, i, j);
        } else {
          paintDisabledText(localJLabel, paramGraphics, str2, i, j);
        }
      }
    }
  }
  
  private String layout(JLabel paramJLabel, FontMetrics paramFontMetrics, int paramInt1, int paramInt2)
  {
    Insets localInsets = paramJLabel.getInsets(null);
    String str = paramJLabel.getText();
    Icon localIcon = paramJLabel.isEnabled() ? paramJLabel.getIcon() : paramJLabel.getDisabledIcon();
    Rectangle localRectangle = new Rectangle();
    x = left;
    y = top;
    width = (paramInt1 - (left + right));
    height = (paramInt2 - (top + bottom));
    paintIconR.x = (paintIconR.y = paintIconR.width = paintIconR.height = 0);
    paintTextR.x = (paintTextR.y = paintTextR.width = paintTextR.height = 0);
    return layoutCL(paramJLabel, paramFontMetrics, str, localIcon, localRectangle, paintIconR, paintTextR);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    JLabel localJLabel = (JLabel)paramJComponent;
    String str = localJLabel.getText();
    Icon localIcon = localJLabel.isEnabled() ? localJLabel.getIcon() : localJLabel.getDisabledIcon();
    Insets localInsets = localJLabel.getInsets(null);
    Font localFont = localJLabel.getFont();
    int i = left + right;
    int j = top + bottom;
    if ((localIcon == null) && ((str == null) || ((str != null) && (localFont == null)))) {
      return new Dimension(i, j);
    }
    if ((str == null) || ((localIcon != null) && (localFont == null))) {
      return new Dimension(localIcon.getIconWidth() + i, localIcon.getIconHeight() + j);
    }
    FontMetrics localFontMetrics = localJLabel.getFontMetrics(localFont);
    Rectangle localRectangle1 = new Rectangle();
    Rectangle localRectangle2 = new Rectangle();
    Rectangle localRectangle3 = new Rectangle();
    x = (y = width = height = 0);
    x = (y = width = height = 0);
    x = i;
    y = j;
    width = (height = '翿');
    layoutCL(localJLabel, localFontMetrics, str, localIcon, localRectangle3, localRectangle1, localRectangle2);
    int k = Math.min(x, x);
    int m = Math.max(x + width, x + width);
    int n = Math.min(y, y);
    int i1 = Math.max(y + height, y + height);
    Dimension localDimension = new Dimension(m - k, i1 - n);
    width += i;
    height += j;
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
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    JLabel localJLabel = (JLabel)paramJComponent;
    String str = localJLabel.getText();
    if ((str == null) || ("".equals(str)) || (localJLabel.getFont() == null)) {
      return -1;
    }
    FontMetrics localFontMetrics = localJLabel.getFontMetrics(localJLabel.getFont());
    layout(localJLabel, localFontMetrics, paramInt1, paramInt2);
    return BasicHTML.getBaseline(localJLabel, paintTextR.y, localFontMetrics.getAscent(), paintTextR.width, paintTextR.height);
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    if (paramJComponent.getClientProperty("html") != null) {
      return Component.BaselineResizeBehavior.OTHER;
    }
    switch (((JLabel)paramJComponent).getVerticalAlignment())
    {
    case 1: 
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    case 3: 
      return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
    case 0: 
      return Component.BaselineResizeBehavior.CENTER_OFFSET;
    }
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  public void installUI(JComponent paramJComponent)
  {
    installDefaults((JLabel)paramJComponent);
    installComponents((JLabel)paramJComponent);
    installListeners((JLabel)paramJComponent);
    installKeyboardActions((JLabel)paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults((JLabel)paramJComponent);
    uninstallComponents((JLabel)paramJComponent);
    uninstallListeners((JLabel)paramJComponent);
    uninstallKeyboardActions((JLabel)paramJComponent);
  }
  
  protected void installDefaults(JLabel paramJLabel)
  {
    LookAndFeel.installColorsAndFont(paramJLabel, "Label.background", "Label.foreground", "Label.font");
    LookAndFeel.installProperty(paramJLabel, "opaque", Boolean.FALSE);
  }
  
  protected void installListeners(JLabel paramJLabel)
  {
    paramJLabel.addPropertyChangeListener(this);
  }
  
  protected void installComponents(JLabel paramJLabel)
  {
    BasicHTML.updateRenderer(paramJLabel, paramJLabel.getText());
    paramJLabel.setInheritsPopupMenu(true);
  }
  
  protected void installKeyboardActions(JLabel paramJLabel)
  {
    int i = paramJLabel.getDisplayedMnemonic();
    Component localComponent = paramJLabel.getLabelFor();
    Object localObject;
    if ((i != 0) && (localComponent != null))
    {
      LazyActionMap.installLazyActionMap(paramJLabel, BasicLabelUI.class, "Label.actionMap");
      localObject = SwingUtilities.getUIInputMap(paramJLabel, 2);
      if (localObject == null)
      {
        localObject = new ComponentInputMapUIResource(paramJLabel);
        SwingUtilities.replaceUIInputMap(paramJLabel, 2, (InputMap)localObject);
      }
      ((InputMap)localObject).clear();
      ((InputMap)localObject).put(KeyStroke.getKeyStroke(i, BasicLookAndFeel.getFocusAcceleratorKeyMask(), false), "press");
    }
    else
    {
      localObject = SwingUtilities.getUIInputMap(paramJLabel, 2);
      if (localObject != null) {
        ((InputMap)localObject).clear();
      }
    }
  }
  
  protected void uninstallDefaults(JLabel paramJLabel) {}
  
  protected void uninstallListeners(JLabel paramJLabel)
  {
    paramJLabel.removePropertyChangeListener(this);
  }
  
  protected void uninstallComponents(JLabel paramJLabel)
  {
    BasicHTML.updateRenderer(paramJLabel, "");
  }
  
  protected void uninstallKeyboardActions(JLabel paramJLabel)
  {
    SwingUtilities.replaceUIInputMap(paramJLabel, 0, null);
    SwingUtilities.replaceUIInputMap(paramJLabel, 2, null);
    SwingUtilities.replaceUIActionMap(paramJLabel, null);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    if (System.getSecurityManager() != null)
    {
      AppContext localAppContext = AppContext.getAppContext();
      BasicLabelUI localBasicLabelUI = (BasicLabelUI)localAppContext.get(BASIC_LABEL_UI_KEY);
      if (localBasicLabelUI == null)
      {
        localBasicLabelUI = new BasicLabelUI();
        localAppContext.put(BASIC_LABEL_UI_KEY, localBasicLabelUI);
      }
      return localBasicLabelUI;
    }
    return labelUI;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str1 = paramPropertyChangeEvent.getPropertyName();
    if ((str1 == "text") || ("font" == str1) || ("foreground" == str1))
    {
      JLabel localJLabel = (JLabel)paramPropertyChangeEvent.getSource();
      String str2 = localJLabel.getText();
      BasicHTML.updateRenderer(localJLabel, str2);
    }
    else if ((str1 == "labelFor") || (str1 == "displayedMnemonic"))
    {
      installKeyboardActions((JLabel)paramPropertyChangeEvent.getSource());
    }
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String PRESS = "press";
    private static final String RELEASE = "release";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JLabel localJLabel = (JLabel)paramActionEvent.getSource();
      String str = getName();
      if (str == "press") {
        doPress(localJLabel);
      } else if (str == "release") {
        doRelease(localJLabel);
      }
    }
    
    private void doPress(JLabel paramJLabel)
    {
      Component localComponent = paramJLabel.getLabelFor();
      if ((localComponent != null) && (localComponent.isEnabled()))
      {
        Object localObject = SwingUtilities.getUIInputMap(paramJLabel, 0);
        if (localObject == null)
        {
          localObject = new InputMapUIResource();
          SwingUtilities.replaceUIInputMap(paramJLabel, 0, (InputMap)localObject);
        }
        int i = paramJLabel.getDisplayedMnemonic();
        ((InputMap)localObject).put(KeyStroke.getKeyStroke(i, BasicLookAndFeel.getFocusAcceleratorKeyMask(), true), "release");
        ((InputMap)localObject).put(KeyStroke.getKeyStroke(i, 0, true), "release");
        ((InputMap)localObject).put(KeyStroke.getKeyStroke(18, 0, true), "release");
        paramJLabel.requestFocus();
      }
    }
    
    private void doRelease(JLabel paramJLabel)
    {
      Component localComponent = paramJLabel.getLabelFor();
      if ((localComponent != null) && (localComponent.isEnabled()))
      {
        InputMap localInputMap = SwingUtilities.getUIInputMap(paramJLabel, 0);
        if (localInputMap != null)
        {
          int i = paramJLabel.getDisplayedMnemonic();
          localInputMap.remove(KeyStroke.getKeyStroke(i, BasicLookAndFeel.getFocusAcceleratorKeyMask(), true));
          localInputMap.remove(KeyStroke.getKeyStroke(i, 0, true));
          localInputMap.remove(KeyStroke.getKeyStroke(18, 0, true));
        }
        if (((localComponent instanceof Container)) && (((Container)localComponent).isFocusCycleRoot())) {
          localComponent.requestFocus();
        } else {
          SwingUtilities2.compositeRequestFocus(localComponent);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicLabelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */