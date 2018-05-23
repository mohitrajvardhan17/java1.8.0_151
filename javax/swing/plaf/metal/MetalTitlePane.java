package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import sun.awt.SunToolkit;
import sun.swing.SwingUtilities2;

class MetalTitlePane
  extends JComponent
{
  private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
  private static final int IMAGE_HEIGHT = 16;
  private static final int IMAGE_WIDTH = 16;
  private PropertyChangeListener propertyChangeListener;
  private JMenuBar menuBar;
  private Action closeAction;
  private Action iconifyAction;
  private Action restoreAction;
  private Action maximizeAction;
  private JButton toggleButton;
  private JButton iconifyButton;
  private JButton closeButton;
  private Icon maximizeIcon;
  private Icon minimizeIcon;
  private Image systemIcon;
  private WindowListener windowListener;
  private Window window;
  private JRootPane rootPane;
  private int buttonsWidth;
  private int state;
  private MetalRootPaneUI rootPaneUI;
  private Color inactiveBackground = UIManager.getColor("inactiveCaption");
  private Color inactiveForeground = UIManager.getColor("inactiveCaptionText");
  private Color inactiveShadow = UIManager.getColor("inactiveCaptionBorder");
  private Color activeBumpsHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
  private Color activeBumpsShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
  private Color activeBackground = null;
  private Color activeForeground = null;
  private Color activeShadow = null;
  private MetalBumps activeBumps = new MetalBumps(0, 0, activeBumpsHighlight, activeBumpsShadow, MetalLookAndFeel.getPrimaryControl());
  private MetalBumps inactiveBumps = new MetalBumps(0, 0, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
  
  public MetalTitlePane(JRootPane paramJRootPane, MetalRootPaneUI paramMetalRootPaneUI)
  {
    rootPane = paramJRootPane;
    rootPaneUI = paramMetalRootPaneUI;
    state = -1;
    installSubcomponents();
    determineColors();
    installDefaults();
    setLayout(createLayout());
  }
  
  private void uninstall()
  {
    uninstallListeners();
    window = null;
    removeAll();
  }
  
  private void installListeners()
  {
    if (window != null)
    {
      windowListener = createWindowListener();
      window.addWindowListener(windowListener);
      propertyChangeListener = createWindowPropertyChangeListener();
      window.addPropertyChangeListener(propertyChangeListener);
    }
  }
  
  private void uninstallListeners()
  {
    if (window != null)
    {
      window.removeWindowListener(windowListener);
      window.removePropertyChangeListener(propertyChangeListener);
    }
  }
  
  private WindowListener createWindowListener()
  {
    return new WindowHandler(null);
  }
  
  private PropertyChangeListener createWindowPropertyChangeListener()
  {
    return new PropertyChangeHandler(null);
  }
  
  public JRootPane getRootPane()
  {
    return rootPane;
  }
  
  private int getWindowDecorationStyle()
  {
    return getRootPane().getWindowDecorationStyle();
  }
  
  public void addNotify()
  {
    super.addNotify();
    uninstallListeners();
    window = SwingUtilities.getWindowAncestor(this);
    if (window != null)
    {
      if ((window instanceof Frame)) {
        setState(((Frame)window).getExtendedState());
      } else {
        setState(0);
      }
      setActive(window.isActive());
      installListeners();
      updateSystemIcon();
    }
  }
  
  public void removeNotify()
  {
    super.removeNotify();
    uninstallListeners();
    window = null;
  }
  
  private void installSubcomponents()
  {
    int i = getWindowDecorationStyle();
    if (i == 1)
    {
      createActions();
      menuBar = createMenuBar();
      add(menuBar);
      createButtons();
      add(iconifyButton);
      add(toggleButton);
      add(closeButton);
    }
    else if ((i == 2) || (i == 3) || (i == 4) || (i == 5) || (i == 6) || (i == 7) || (i == 8))
    {
      createActions();
      createButtons();
      add(closeButton);
    }
  }
  
  private void determineColors()
  {
    switch (getWindowDecorationStyle())
    {
    case 1: 
      activeBackground = UIManager.getColor("activeCaption");
      activeForeground = UIManager.getColor("activeCaptionText");
      activeShadow = UIManager.getColor("activeCaptionBorder");
      break;
    case 4: 
      activeBackground = UIManager.getColor("OptionPane.errorDialog.titlePane.background");
      activeForeground = UIManager.getColor("OptionPane.errorDialog.titlePane.foreground");
      activeShadow = UIManager.getColor("OptionPane.errorDialog.titlePane.shadow");
      break;
    case 5: 
    case 6: 
    case 7: 
      activeBackground = UIManager.getColor("OptionPane.questionDialog.titlePane.background");
      activeForeground = UIManager.getColor("OptionPane.questionDialog.titlePane.foreground");
      activeShadow = UIManager.getColor("OptionPane.questionDialog.titlePane.shadow");
      break;
    case 8: 
      activeBackground = UIManager.getColor("OptionPane.warningDialog.titlePane.background");
      activeForeground = UIManager.getColor("OptionPane.warningDialog.titlePane.foreground");
      activeShadow = UIManager.getColor("OptionPane.warningDialog.titlePane.shadow");
      break;
    case 2: 
    case 3: 
    default: 
      activeBackground = UIManager.getColor("activeCaption");
      activeForeground = UIManager.getColor("activeCaptionText");
      activeShadow = UIManager.getColor("activeCaptionBorder");
    }
    activeBumps.setBumpColors(activeBumpsHighlight, activeBumpsShadow, activeBackground);
  }
  
  private void installDefaults()
  {
    setFont(UIManager.getFont("InternalFrame.titleFont", getLocale()));
  }
  
  private void uninstallDefaults() {}
  
  protected JMenuBar createMenuBar()
  {
    menuBar = new SystemMenuBar(null);
    menuBar.setFocusable(false);
    menuBar.setBorderPainted(true);
    menuBar.add(createMenu());
    return menuBar;
  }
  
  private void close()
  {
    Window localWindow = getWindow();
    if (localWindow != null) {
      localWindow.dispatchEvent(new WindowEvent(localWindow, 201));
    }
  }
  
  private void iconify()
  {
    Frame localFrame = getFrame();
    if (localFrame != null) {
      localFrame.setExtendedState(state | 0x1);
    }
  }
  
  private void maximize()
  {
    Frame localFrame = getFrame();
    if (localFrame != null) {
      localFrame.setExtendedState(state | 0x6);
    }
  }
  
  private void restore()
  {
    Frame localFrame = getFrame();
    if (localFrame == null) {
      return;
    }
    if ((state & 0x1) != 0) {
      localFrame.setExtendedState(state & 0xFFFFFFFE);
    } else {
      localFrame.setExtendedState(state & 0xFFFFFFF9);
    }
  }
  
  private void createActions()
  {
    closeAction = new CloseAction();
    if (getWindowDecorationStyle() == 1)
    {
      iconifyAction = new IconifyAction();
      restoreAction = new RestoreAction();
      maximizeAction = new MaximizeAction();
    }
  }
  
  private JMenu createMenu()
  {
    JMenu localJMenu = new JMenu("");
    if (getWindowDecorationStyle() == 1) {
      addMenuItems(localJMenu);
    }
    return localJMenu;
  }
  
  private void addMenuItems(JMenu paramJMenu)
  {
    Locale localLocale = getRootPane().getLocale();
    JMenuItem localJMenuItem = paramJMenu.add(restoreAction);
    int i = MetalUtils.getInt("MetalTitlePane.restoreMnemonic", -1);
    if (i != -1) {
      localJMenuItem.setMnemonic(i);
    }
    localJMenuItem = paramJMenu.add(iconifyAction);
    i = MetalUtils.getInt("MetalTitlePane.iconifyMnemonic", -1);
    if (i != -1) {
      localJMenuItem.setMnemonic(i);
    }
    if (Toolkit.getDefaultToolkit().isFrameStateSupported(6))
    {
      localJMenuItem = paramJMenu.add(maximizeAction);
      i = MetalUtils.getInt("MetalTitlePane.maximizeMnemonic", -1);
      if (i != -1) {
        localJMenuItem.setMnemonic(i);
      }
    }
    paramJMenu.add(new JSeparator());
    localJMenuItem = paramJMenu.add(closeAction);
    i = MetalUtils.getInt("MetalTitlePane.closeMnemonic", -1);
    if (i != -1) {
      localJMenuItem.setMnemonic(i);
    }
  }
  
  private JButton createTitleButton()
  {
    JButton localJButton = new JButton();
    localJButton.setFocusPainted(false);
    localJButton.setFocusable(false);
    localJButton.setOpaque(true);
    return localJButton;
  }
  
  private void createButtons()
  {
    closeButton = createTitleButton();
    closeButton.setAction(closeAction);
    closeButton.setText(null);
    closeButton.putClientProperty("paintActive", Boolean.TRUE);
    closeButton.setBorder(handyEmptyBorder);
    closeButton.putClientProperty("AccessibleName", "Close");
    closeButton.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));
    if (getWindowDecorationStyle() == 1)
    {
      maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
      minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
      iconifyButton = createTitleButton();
      iconifyButton.setAction(iconifyAction);
      iconifyButton.setText(null);
      iconifyButton.putClientProperty("paintActive", Boolean.TRUE);
      iconifyButton.setBorder(handyEmptyBorder);
      iconifyButton.putClientProperty("AccessibleName", "Iconify");
      iconifyButton.setIcon(UIManager.getIcon("InternalFrame.iconifyIcon"));
      toggleButton = createTitleButton();
      toggleButton.setAction(restoreAction);
      toggleButton.putClientProperty("paintActive", Boolean.TRUE);
      toggleButton.setBorder(handyEmptyBorder);
      toggleButton.putClientProperty("AccessibleName", "Maximize");
      toggleButton.setIcon(maximizeIcon);
    }
  }
  
  private LayoutManager createLayout()
  {
    return new TitlePaneLayout(null);
  }
  
  private void setActive(boolean paramBoolean)
  {
    Boolean localBoolean = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    closeButton.putClientProperty("paintActive", localBoolean);
    if (getWindowDecorationStyle() == 1)
    {
      iconifyButton.putClientProperty("paintActive", localBoolean);
      toggleButton.putClientProperty("paintActive", localBoolean);
    }
    getRootPane().repaint();
  }
  
  private void setState(int paramInt)
  {
    setState(paramInt, false);
  }
  
  private void setState(int paramInt, boolean paramBoolean)
  {
    Window localWindow = getWindow();
    if ((localWindow != null) && (getWindowDecorationStyle() == 1))
    {
      if ((state == paramInt) && (!paramBoolean)) {
        return;
      }
      Frame localFrame = getFrame();
      if (localFrame != null)
      {
        JRootPane localJRootPane = getRootPane();
        if (((paramInt & 0x6) != 0) && ((localJRootPane.getBorder() == null) || ((localJRootPane.getBorder() instanceof UIResource))) && (localFrame.isShowing())) {
          localJRootPane.setBorder(null);
        } else if ((paramInt & 0x6) == 0) {
          rootPaneUI.installBorder(localJRootPane);
        }
        if (localFrame.isResizable())
        {
          if ((paramInt & 0x6) != 0)
          {
            updateToggleButton(restoreAction, minimizeIcon);
            maximizeAction.setEnabled(false);
            restoreAction.setEnabled(true);
          }
          else
          {
            updateToggleButton(maximizeAction, maximizeIcon);
            maximizeAction.setEnabled(true);
            restoreAction.setEnabled(false);
          }
          if ((toggleButton.getParent() == null) || (iconifyButton.getParent() == null))
          {
            add(toggleButton);
            add(iconifyButton);
            revalidate();
            repaint();
          }
          toggleButton.setText(null);
        }
        else
        {
          maximizeAction.setEnabled(false);
          restoreAction.setEnabled(false);
          if (toggleButton.getParent() != null)
          {
            remove(toggleButton);
            revalidate();
            repaint();
          }
        }
      }
      else
      {
        maximizeAction.setEnabled(false);
        restoreAction.setEnabled(false);
        iconifyAction.setEnabled(false);
        remove(toggleButton);
        remove(iconifyButton);
        revalidate();
        repaint();
      }
      closeAction.setEnabled(true);
      state = paramInt;
    }
  }
  
  private void updateToggleButton(Action paramAction, Icon paramIcon)
  {
    toggleButton.setAction(paramAction);
    toggleButton.setIcon(paramIcon);
    toggleButton.setText(null);
  }
  
  private Frame getFrame()
  {
    Window localWindow = getWindow();
    if ((localWindow instanceof Frame)) {
      return (Frame)localWindow;
    }
    return null;
  }
  
  private Window getWindow()
  {
    return window;
  }
  
  private String getTitle()
  {
    Window localWindow = getWindow();
    if ((localWindow instanceof Frame)) {
      return ((Frame)localWindow).getTitle();
    }
    if ((localWindow instanceof Dialog)) {
      return ((Dialog)localWindow).getTitle();
    }
    return null;
  }
  
  public void paintComponent(Graphics paramGraphics)
  {
    if (getFrame() != null) {
      setState(getFrame().getExtendedState());
    }
    JRootPane localJRootPane = getRootPane();
    Window localWindow = getWindow();
    boolean bool1 = localWindow == null ? localJRootPane.getComponentOrientation().isLeftToRight() : localWindow.getComponentOrientation().isLeftToRight();
    boolean bool2 = localWindow == null ? true : localWindow.isActive();
    int i = getWidth();
    int j = getHeight();
    Color localColor1;
    Color localColor2;
    Color localColor3;
    MetalBumps localMetalBumps;
    if (bool2)
    {
      localColor1 = activeBackground;
      localColor2 = activeForeground;
      localColor3 = activeShadow;
      localMetalBumps = activeBumps;
    }
    else
    {
      localColor1 = inactiveBackground;
      localColor2 = inactiveForeground;
      localColor3 = inactiveShadow;
      localMetalBumps = inactiveBumps;
    }
    paramGraphics.setColor(localColor1);
    paramGraphics.fillRect(0, 0, i, j);
    paramGraphics.setColor(localColor3);
    paramGraphics.drawLine(0, j - 1, i, j - 1);
    paramGraphics.drawLine(0, 0, 0, 0);
    paramGraphics.drawLine(i - 1, 0, i - 1, 0);
    FontMetrics localFontMetrics1 = bool1 ? 5 : i - 5;
    if (getWindowDecorationStyle() == 1) {
      localFontMetrics1 += (bool1 ? 21 : -21);
    }
    String str = getTitle();
    FontMetrics localFontMetrics2;
    int m;
    if (str != null)
    {
      localFontMetrics2 = SwingUtilities2.getFontMetrics(localJRootPane, paramGraphics);
      paramGraphics.setColor(localColor2);
      m = (j - localFontMetrics2.getHeight()) / 2 + localFontMetrics2.getAscent();
      Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
      if ((iconifyButton != null) && (iconifyButton.getParent() != null)) {
        localRectangle = iconifyButton.getBounds();
      }
      if (bool1)
      {
        if (x == 0) {
          x = (localWindow.getWidth() - getInsetsright - 2);
        }
        i1 = x - localFontMetrics1 - 4;
        str = SwingUtilities2.clipStringIfNecessary(localJRootPane, localFontMetrics2, str, i1);
      }
      else
      {
        i1 = localFontMetrics1 - x - width - 4;
        str = SwingUtilities2.clipStringIfNecessary(localJRootPane, localFontMetrics2, str, i1);
        localFontMetrics1 -= SwingUtilities2.stringWidth(localJRootPane, localFontMetrics2, str);
      }
      int i2 = SwingUtilities2.stringWidth(localJRootPane, localFontMetrics2, str);
      SwingUtilities2.drawString(localJRootPane, paramGraphics, str, localFontMetrics1, m);
      localFontMetrics1 += (bool1 ? i2 + 5 : -5);
    }
    int k;
    if (bool1)
    {
      m = i - buttonsWidth - localFontMetrics1 - 5;
      localFontMetrics2 = localFontMetrics1;
    }
    else
    {
      m = localFontMetrics1 - buttonsWidth - 5;
      k = buttonsWidth + 5;
    }
    int n = 3;
    int i1 = getHeight() - 2 * n;
    localMetalBumps.setBumpArea(m, i1);
    localMetalBumps.paintIcon(this, paramGraphics, k, n);
  }
  
  private void updateSystemIcon()
  {
    Window localWindow = getWindow();
    if (localWindow == null)
    {
      systemIcon = null;
      return;
    }
    List localList = localWindow.getIconImages();
    assert (localList != null);
    if (localList.size() == 0) {
      systemIcon = null;
    } else if (localList.size() == 1) {
      systemIcon = ((Image)localList.get(0));
    } else {
      systemIcon = SunToolkit.getScaledIconImage(localList, 16, 16);
    }
  }
  
  private class CloseAction
    extends AbstractAction
  {
    public CloseAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      MetalTitlePane.this.close();
    }
  }
  
  private class IconifyAction
    extends AbstractAction
  {
    public IconifyAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      MetalTitlePane.this.iconify();
    }
  }
  
  private class MaximizeAction
    extends AbstractAction
  {
    public MaximizeAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      MetalTitlePane.this.maximize();
    }
  }
  
  private class PropertyChangeHandler
    implements PropertyChangeListener
  {
    private PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (("resizable".equals(str)) || ("state".equals(str)))
      {
        Frame localFrame = MetalTitlePane.this.getFrame();
        if (localFrame != null) {
          MetalTitlePane.this.setState(localFrame.getExtendedState(), true);
        }
        if ("resizable".equals(str)) {
          getRootPane().repaint();
        }
      }
      else if ("title".equals(str))
      {
        repaint();
      }
      else if ("componentOrientation" == str)
      {
        revalidate();
        repaint();
      }
      else if ("iconImage" == str)
      {
        MetalTitlePane.this.updateSystemIcon();
        revalidate();
        repaint();
      }
    }
  }
  
  private class RestoreAction
    extends AbstractAction
  {
    public RestoreAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      MetalTitlePane.this.restore();
    }
  }
  
  private class SystemMenuBar
    extends JMenuBar
  {
    private SystemMenuBar() {}
    
    public void paint(Graphics paramGraphics)
    {
      if (isOpaque())
      {
        paramGraphics.setColor(getBackground());
        paramGraphics.fillRect(0, 0, getWidth(), getHeight());
      }
      if (systemIcon != null)
      {
        paramGraphics.drawImage(systemIcon, 0, 0, 16, 16, null);
      }
      else
      {
        Icon localIcon = UIManager.getIcon("InternalFrame.icon");
        if (localIcon != null) {
          localIcon.paintIcon(this, paramGraphics, 0, 0);
        }
      }
    }
    
    public Dimension getMinimumSize()
    {
      return getPreferredSize();
    }
    
    public Dimension getPreferredSize()
    {
      Dimension localDimension = super.getPreferredSize();
      return new Dimension(Math.max(16, width), Math.max(height, 16));
    }
  }
  
  private class TitlePaneLayout
    implements LayoutManager
  {
    private TitlePaneLayout() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      int i = computeHeight();
      return new Dimension(i, i);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return preferredLayoutSize(paramContainer);
    }
    
    private int computeHeight()
    {
      FontMetrics localFontMetrics = rootPane.getFontMetrics(getFont());
      int i = localFontMetrics.getHeight();
      i += 7;
      int j = 0;
      if (MetalTitlePane.this.getWindowDecorationStyle() == 1) {
        j = 16;
      }
      int k = Math.max(i, j);
      return k;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      boolean bool = window == null ? getRootPane().getComponentOrientation().isLeftToRight() : window.getComponentOrientation().isLeftToRight();
      int i = getWidth();
      int k = 3;
      int n;
      int i1;
      if ((closeButton != null) && (closeButton.getIcon() != null))
      {
        n = closeButton.getIcon().getIconHeight();
        i1 = closeButton.getIcon().getIconWidth();
      }
      else
      {
        n = 16;
        i1 = 16;
      }
      int j = bool ? i : 0;
      int m = 5;
      j = bool ? m : i - i1 - m;
      if (menuBar != null) {
        menuBar.setBounds(j, k, i1, n);
      }
      j = bool ? i : 0;
      m = 4;
      j += (bool ? -m - i1 : m);
      if (closeButton != null) {
        closeButton.setBounds(j, k, i1, n);
      }
      if (!bool) {
        j += i1;
      }
      if (MetalTitlePane.this.getWindowDecorationStyle() == 1)
      {
        if ((Toolkit.getDefaultToolkit().isFrameStateSupported(6)) && (toggleButton.getParent() != null))
        {
          m = 10;
          j += (bool ? -m - i1 : m);
          toggleButton.setBounds(j, k, i1, n);
          if (!bool) {
            j += i1;
          }
        }
        if ((iconifyButton != null) && (iconifyButton.getParent() != null))
        {
          m = 2;
          j += (bool ? -m - i1 : m);
          iconifyButton.setBounds(j, k, i1, n);
          if (!bool) {
            j += i1;
          }
        }
      }
      buttonsWidth = (bool ? i - j : j);
    }
  }
  
  private class WindowHandler
    extends WindowAdapter
  {
    private WindowHandler() {}
    
    public void windowActivated(WindowEvent paramWindowEvent)
    {
      MetalTitlePane.this.setActive(true);
    }
    
    public void windowDeactivated(WindowEvent paramWindowEvent)
    {
      MetalTitlePane.this.setActive(false);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */