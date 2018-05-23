package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.PropertyChangeHandler;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.TitlePaneLayout;
import sun.swing.SwingUtilities2;

public class MetalInternalFrameTitlePane
  extends BasicInternalFrameTitlePane
{
  protected boolean isPalette = false;
  protected Icon paletteCloseIcon;
  protected int paletteTitleHeight;
  private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
  private String selectedBackgroundKey;
  private String selectedForegroundKey;
  private String selectedShadowKey;
  private boolean wasClosable;
  int buttonsWidth = 0;
  MetalBumps activeBumps = new MetalBumps(0, 0, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlDarkShadow(), UIManager.get("InternalFrame.activeTitleGradient") != null ? null : MetalLookAndFeel.getPrimaryControl());
  MetalBumps inactiveBumps = new MetalBumps(0, 0, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), UIManager.get("InternalFrame.inactiveTitleGradient") != null ? null : MetalLookAndFeel.getControl());
  MetalBumps paletteBumps;
  private Color activeBumpsHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
  private Color activeBumpsShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
  
  public MetalInternalFrameTitlePane(JInternalFrame paramJInternalFrame)
  {
    super(paramJInternalFrame);
  }
  
  public void addNotify()
  {
    super.addNotify();
    updateOptionPaneState();
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    setFont(UIManager.getFont("InternalFrame.titleFont"));
    paletteTitleHeight = UIManager.getInt("InternalFrame.paletteTitleHeight");
    paletteCloseIcon = UIManager.getIcon("InternalFrame.paletteCloseIcon");
    wasClosable = frame.isClosable();
    selectedForegroundKey = (selectedBackgroundKey = null);
    if (MetalLookAndFeel.usingOcean()) {
      setOpaque(true);
    }
  }
  
  protected void uninstallDefaults()
  {
    super.uninstallDefaults();
    if (wasClosable != frame.isClosable()) {
      frame.setClosable(wasClosable);
    }
  }
  
  protected void createButtons()
  {
    super.createButtons();
    Boolean localBoolean = frame.isSelected() ? Boolean.TRUE : Boolean.FALSE;
    iconButton.putClientProperty("paintActive", localBoolean);
    iconButton.setBorder(handyEmptyBorder);
    maxButton.putClientProperty("paintActive", localBoolean);
    maxButton.setBorder(handyEmptyBorder);
    closeButton.putClientProperty("paintActive", localBoolean);
    closeButton.setBorder(handyEmptyBorder);
    closeButton.setBackground(MetalLookAndFeel.getPrimaryControlShadow());
    if (MetalLookAndFeel.usingOcean())
    {
      iconButton.setContentAreaFilled(false);
      maxButton.setContentAreaFilled(false);
      closeButton.setContentAreaFilled(false);
    }
  }
  
  protected void assembleSystemMenu() {}
  
  protected void addSystemMenuItems(JMenu paramJMenu) {}
  
  protected void showSystemMenu() {}
  
  protected void addSubComponents()
  {
    add(iconButton);
    add(maxButton);
    add(closeButton);
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return new MetalPropertyChangeHandler();
  }
  
  protected LayoutManager createLayout()
  {
    return new MetalTitlePaneLayout();
  }
  
  public void paintPalette(Graphics paramGraphics)
  {
    boolean bool = MetalUtils.isLeftToRight(frame);
    int i = getWidth();
    int j = getHeight();
    if (paletteBumps == null) {
      paletteBumps = new MetalBumps(0, 0, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlShadow());
    }
    ColorUIResource localColorUIResource1 = MetalLookAndFeel.getPrimaryControlShadow();
    ColorUIResource localColorUIResource2 = MetalLookAndFeel.getPrimaryControlDarkShadow();
    paramGraphics.setColor(localColorUIResource1);
    paramGraphics.fillRect(0, 0, i, j);
    paramGraphics.setColor(localColorUIResource2);
    paramGraphics.drawLine(0, j - 1, i, j - 1);
    int k = bool ? 4 : buttonsWidth + 4;
    int m = i - buttonsWidth - 8;
    int n = getHeight() - 4;
    paletteBumps.setBumpArea(m, n);
    paletteBumps.paintIcon(this, paramGraphics, k, 2);
  }
  
  public void paintComponent(Graphics paramGraphics)
  {
    if (isPalette)
    {
      paintPalette(paramGraphics);
      return;
    }
    boolean bool1 = MetalUtils.isLeftToRight(frame);
    boolean bool2 = frame.isSelected();
    int i = getWidth();
    int j = getHeight();
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    MetalBumps localMetalBumps;
    String str1;
    if (bool2)
    {
      if (!MetalLookAndFeel.usingOcean())
      {
        closeButton.setContentAreaFilled(true);
        maxButton.setContentAreaFilled(true);
        iconButton.setContentAreaFilled(true);
      }
      if (selectedBackgroundKey != null) {
        localObject1 = UIManager.getColor(selectedBackgroundKey);
      }
      if (localObject1 == null) {
        localObject1 = MetalLookAndFeel.getWindowTitleBackground();
      }
      if (selectedForegroundKey != null) {
        localObject2 = UIManager.getColor(selectedForegroundKey);
      }
      if (selectedShadowKey != null) {
        localObject3 = UIManager.getColor(selectedShadowKey);
      }
      if (localObject3 == null) {
        localObject3 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      }
      if (localObject2 == null) {
        localObject2 = MetalLookAndFeel.getWindowTitleForeground();
      }
      activeBumps.setBumpColors(activeBumpsHighlight, activeBumpsShadow, UIManager.get("InternalFrame.activeTitleGradient") != null ? null : (Color)localObject1);
      localMetalBumps = activeBumps;
      str1 = "InternalFrame.activeTitleGradient";
    }
    else
    {
      if (!MetalLookAndFeel.usingOcean())
      {
        closeButton.setContentAreaFilled(false);
        maxButton.setContentAreaFilled(false);
        iconButton.setContentAreaFilled(false);
      }
      localObject1 = MetalLookAndFeel.getWindowTitleInactiveBackground();
      localObject2 = MetalLookAndFeel.getWindowTitleInactiveForeground();
      localObject3 = MetalLookAndFeel.getControlDarkShadow();
      localMetalBumps = inactiveBumps;
      str1 = "InternalFrame.inactiveTitleGradient";
    }
    if (!MetalUtils.drawGradient(this, paramGraphics, str1, 0, 0, i, j, true))
    {
      paramGraphics.setColor((Color)localObject1);
      paramGraphics.fillRect(0, 0, i, j);
    }
    paramGraphics.setColor((Color)localObject3);
    paramGraphics.drawLine(0, j - 1, i, j - 1);
    paramGraphics.drawLine(0, 0, 0, 0);
    paramGraphics.drawLine(i - 1, 0, i - 1, 0);
    Font localFont1 = bool1 ? 5 : i - 5;
    String str2 = frame.getTitle();
    Icon localIcon = frame.getFrameIcon();
    if (localIcon != null)
    {
      if (!bool1) {
        localFont1 -= localIcon.getIconWidth();
      }
      int m = j / 2 - localIcon.getIconHeight() / 2;
      localIcon.paintIcon(frame, paramGraphics, localFont1, m);
      localFont1 += (bool1 ? localIcon.getIconWidth() + 5 : -5);
    }
    Font localFont2;
    if (str2 != null)
    {
      localFont2 = getFont();
      paramGraphics.setFont(localFont2);
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(frame, paramGraphics, localFont2);
      i2 = localFontMetrics.getHeight();
      paramGraphics.setColor((Color)localObject2);
      i3 = (j - localFontMetrics.getHeight()) / 2 + localFontMetrics.getAscent();
      Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
      if (frame.isIconifiable()) {
        localRectangle = iconButton.getBounds();
      } else if (frame.isMaximizable()) {
        localRectangle = maxButton.getBounds();
      } else if (frame.isClosable()) {
        localRectangle = closeButton.getBounds();
      }
      int i4;
      if (bool1)
      {
        if (x == 0) {
          x = (frame.getWidth() - frame.getInsets().right - 2);
        }
        i4 = x - localFont1 - 4;
        str2 = getTitle(str2, localFontMetrics, i4);
      }
      else
      {
        i4 = localFont1 - x - width - 4;
        str2 = getTitle(str2, localFontMetrics, i4);
        localFont1 -= SwingUtilities2.stringWidth(frame, localFontMetrics, str2);
      }
      int k = SwingUtilities2.stringWidth(frame, localFontMetrics, str2);
      SwingUtilities2.drawString(frame, paramGraphics, str2, localFont1, i3);
      localFont1 += (bool1 ? k + 5 : -5);
    }
    int i1;
    int n;
    if (bool1)
    {
      i1 = i - buttonsWidth - localFont1 - 5;
      localFont2 = localFont1;
    }
    else
    {
      i1 = localFont1 - buttonsWidth - 5;
      n = buttonsWidth + 5;
    }
    int i2 = 3;
    int i3 = getHeight() - 2 * i2;
    localMetalBumps.setBumpArea(i1, i3);
    localMetalBumps.paintIcon(this, paramGraphics, n, i2);
  }
  
  public void setPalette(boolean paramBoolean)
  {
    isPalette = paramBoolean;
    if (isPalette)
    {
      closeButton.setIcon(paletteCloseIcon);
      if (frame.isMaximizable()) {
        remove(maxButton);
      }
      if (frame.isIconifiable()) {
        remove(iconButton);
      }
    }
    else
    {
      closeButton.setIcon(closeIcon);
      if (frame.isMaximizable()) {
        add(maxButton);
      }
      if (frame.isIconifiable()) {
        add(iconButton);
      }
    }
    revalidate();
    repaint();
  }
  
  private void updateOptionPaneState()
  {
    int i = -2;
    boolean bool = wasClosable;
    Object localObject = frame.getClientProperty("JInternalFrame.messageType");
    if (localObject == null) {
      return;
    }
    if ((localObject instanceof Integer)) {
      i = ((Integer)localObject).intValue();
    }
    switch (i)
    {
    case 0: 
      selectedBackgroundKey = "OptionPane.errorDialog.titlePane.background";
      selectedForegroundKey = "OptionPane.errorDialog.titlePane.foreground";
      selectedShadowKey = "OptionPane.errorDialog.titlePane.shadow";
      bool = false;
      break;
    case 3: 
      selectedBackgroundKey = "OptionPane.questionDialog.titlePane.background";
      selectedForegroundKey = "OptionPane.questionDialog.titlePane.foreground";
      selectedShadowKey = "OptionPane.questionDialog.titlePane.shadow";
      bool = false;
      break;
    case 2: 
      selectedBackgroundKey = "OptionPane.warningDialog.titlePane.background";
      selectedForegroundKey = "OptionPane.warningDialog.titlePane.foreground";
      selectedShadowKey = "OptionPane.warningDialog.titlePane.shadow";
      bool = false;
      break;
    case -1: 
    case 1: 
      selectedBackgroundKey = (selectedForegroundKey = selectedShadowKey = null);
      bool = false;
      break;
    default: 
      selectedBackgroundKey = (selectedForegroundKey = selectedShadowKey = null);
    }
    if (bool != frame.isClosable()) {
      frame.setClosable(bool);
    }
  }
  
  class MetalPropertyChangeHandler
    extends BasicInternalFrameTitlePane.PropertyChangeHandler
  {
    MetalPropertyChangeHandler()
    {
      super();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str.equals("selected"))
      {
        Boolean localBoolean = (Boolean)paramPropertyChangeEvent.getNewValue();
        iconButton.putClientProperty("paintActive", localBoolean);
        closeButton.putClientProperty("paintActive", localBoolean);
        maxButton.putClientProperty("paintActive", localBoolean);
      }
      else if ("JInternalFrame.messageType".equals(str))
      {
        MetalInternalFrameTitlePane.this.updateOptionPaneState();
        frame.repaint();
      }
      super.propertyChange(paramPropertyChangeEvent);
    }
  }
  
  class MetalTitlePaneLayout
    extends BasicInternalFrameTitlePane.TitlePaneLayout
  {
    MetalTitlePaneLayout()
    {
      super();
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return minimumLayoutSize(paramContainer);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      int i = 30;
      if (frame.isClosable()) {
        i += 21;
      }
      if (frame.isMaximizable()) {
        i += 16 + (frame.isClosable() ? 10 : 4);
      }
      if (frame.isIconifiable()) {
        i += 16 + (frame.isClosable() ? 10 : frame.isMaximizable() ? 2 : 4);
      }
      FontMetrics localFontMetrics = frame.getFontMetrics(getFont());
      String str = frame.getTitle();
      int j = str != null ? SwingUtilities2.stringWidth(frame, localFontMetrics, str) : 0;
      int k = str != null ? str.length() : 0;
      int m;
      if (k > 2)
      {
        m = SwingUtilities2.stringWidth(frame, localFontMetrics, frame.getTitle().substring(0, 2) + "...");
        i += (j < m ? j : m);
      }
      else
      {
        i += j;
      }
      if (isPalette)
      {
        m = paletteTitleHeight;
      }
      else
      {
        int n = localFontMetrics.getHeight();
        n += 7;
        Icon localIcon = frame.getFrameIcon();
        int i1 = 0;
        if (localIcon != null) {
          i1 = Math.min(localIcon.getIconHeight(), 16);
        }
        i1 += 5;
        m = Math.max(n, i1);
      }
      return new Dimension(i, m);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      boolean bool = MetalUtils.isLeftToRight(frame);
      int i = getWidth();
      int j = bool ? i : 0;
      int k = 2;
      int n = closeButton.getIcon().getIconHeight();
      int i1 = closeButton.getIcon().getIconWidth();
      int m;
      if (frame.isClosable()) {
        if (isPalette)
        {
          m = 3;
          j += (bool ? -m - (i1 + 2) : m);
          closeButton.setBounds(j, k, i1 + 2, getHeight() - 4);
          if (!bool) {
            j += i1 + 2;
          }
        }
        else
        {
          m = 4;
          j += (bool ? -m - i1 : m);
          closeButton.setBounds(j, k, i1, n);
          if (!bool) {
            j += i1;
          }
        }
      }
      if ((frame.isMaximizable()) && (!isPalette))
      {
        m = frame.isClosable() ? 10 : 4;
        j += (bool ? -m - i1 : m);
        maxButton.setBounds(j, k, i1, n);
        if (!bool) {
          j += i1;
        }
      }
      if ((frame.isIconifiable()) && (!isPalette))
      {
        m = frame.isClosable() ? 10 : frame.isMaximizable() ? 2 : 4;
        j += (bool ? -m - i1 : m);
        iconButton.setBounds(j, k, i1, n);
        if (!bool) {
          j += i1;
        }
      }
      buttonsWidth = (bool ? i - j : j);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */