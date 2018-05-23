package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.ActionMapUIResource;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class BasicInternalFrameTitlePane
  extends JComponent
{
  protected JMenuBar menuBar;
  protected JButton iconButton;
  protected JButton maxButton;
  protected JButton closeButton;
  protected JMenu windowMenu;
  protected JInternalFrame frame;
  protected Color selectedTitleColor;
  protected Color selectedTextColor;
  protected Color notSelectedTitleColor;
  protected Color notSelectedTextColor;
  protected Icon maxIcon;
  protected Icon minIcon;
  protected Icon iconIcon;
  protected Icon closeIcon;
  protected PropertyChangeListener propertyChangeListener;
  protected Action closeAction;
  protected Action maximizeAction;
  protected Action iconifyAction;
  protected Action restoreAction;
  protected Action moveAction;
  protected Action sizeAction;
  protected static final String CLOSE_CMD = UIManager.getString("InternalFrameTitlePane.closeButtonText");
  protected static final String ICONIFY_CMD = UIManager.getString("InternalFrameTitlePane.minimizeButtonText");
  protected static final String RESTORE_CMD = UIManager.getString("InternalFrameTitlePane.restoreButtonText");
  protected static final String MAXIMIZE_CMD = UIManager.getString("InternalFrameTitlePane.maximizeButtonText");
  protected static final String MOVE_CMD = UIManager.getString("InternalFrameTitlePane.moveButtonText");
  protected static final String SIZE_CMD = UIManager.getString("InternalFrameTitlePane.sizeButtonText");
  private String closeButtonToolTip;
  private String iconButtonToolTip;
  private String restoreButtonToolTip;
  private String maxButtonToolTip;
  private Handler handler;
  
  public BasicInternalFrameTitlePane(JInternalFrame paramJInternalFrame)
  {
    frame = paramJInternalFrame;
    installTitlePane();
  }
  
  protected void installTitlePane()
  {
    installDefaults();
    installListeners();
    createActions();
    enableActions();
    createActionMap();
    setLayout(createLayout());
    assembleSystemMenu();
    createButtons();
    addSubComponents();
    updateProperties();
  }
  
  private void updateProperties()
  {
    Object localObject = frame.getClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
    putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, localObject);
  }
  
  protected void addSubComponents()
  {
    add(menuBar);
    add(iconButton);
    add(maxButton);
    add(closeButton);
  }
  
  protected void createActions()
  {
    maximizeAction = new MaximizeAction();
    iconifyAction = new IconifyAction();
    closeAction = new CloseAction();
    restoreAction = new RestoreAction();
    moveAction = new MoveAction();
    sizeAction = new SizeAction();
  }
  
  ActionMap createActionMap()
  {
    ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
    localActionMapUIResource.put("showSystemMenu", new ShowSystemMenuAction(true));
    localActionMapUIResource.put("hideSystemMenu", new ShowSystemMenuAction(false));
    return localActionMapUIResource;
  }
  
  protected void installListeners()
  {
    if (propertyChangeListener == null) {
      propertyChangeListener = createPropertyChangeListener();
    }
    frame.addPropertyChangeListener(propertyChangeListener);
  }
  
  protected void uninstallListeners()
  {
    frame.removePropertyChangeListener(propertyChangeListener);
    handler = null;
  }
  
  protected void installDefaults()
  {
    maxIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
    minIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
    iconIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
    closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
    selectedTitleColor = UIManager.getColor("InternalFrame.activeTitleBackground");
    selectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
    notSelectedTitleColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
    notSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
    setFont(UIManager.getFont("InternalFrame.titleFont"));
    closeButtonToolTip = UIManager.getString("InternalFrame.closeButtonToolTip");
    iconButtonToolTip = UIManager.getString("InternalFrame.iconButtonToolTip");
    restoreButtonToolTip = UIManager.getString("InternalFrame.restoreButtonToolTip");
    maxButtonToolTip = UIManager.getString("InternalFrame.maxButtonToolTip");
  }
  
  protected void uninstallDefaults() {}
  
  protected void createButtons()
  {
    iconButton = new NoFocusButton("InternalFrameTitlePane.iconifyButtonAccessibleName", "InternalFrameTitlePane.iconifyButtonOpacity");
    iconButton.addActionListener(iconifyAction);
    if ((iconButtonToolTip != null) && (iconButtonToolTip.length() != 0)) {
      iconButton.setToolTipText(iconButtonToolTip);
    }
    maxButton = new NoFocusButton("InternalFrameTitlePane.maximizeButtonAccessibleName", "InternalFrameTitlePane.maximizeButtonOpacity");
    maxButton.addActionListener(maximizeAction);
    closeButton = new NoFocusButton("InternalFrameTitlePane.closeButtonAccessibleName", "InternalFrameTitlePane.closeButtonOpacity");
    closeButton.addActionListener(closeAction);
    if ((closeButtonToolTip != null) && (closeButtonToolTip.length() != 0)) {
      closeButton.setToolTipText(closeButtonToolTip);
    }
    setButtonIcons();
  }
  
  protected void setButtonIcons()
  {
    if (frame.isIcon())
    {
      if (minIcon != null) {
        iconButton.setIcon(minIcon);
      }
      if ((restoreButtonToolTip != null) && (restoreButtonToolTip.length() != 0)) {
        iconButton.setToolTipText(restoreButtonToolTip);
      }
      if (maxIcon != null) {
        maxButton.setIcon(maxIcon);
      }
      if ((maxButtonToolTip != null) && (maxButtonToolTip.length() != 0)) {
        maxButton.setToolTipText(maxButtonToolTip);
      }
    }
    else if (frame.isMaximum())
    {
      if (iconIcon != null) {
        iconButton.setIcon(iconIcon);
      }
      if ((iconButtonToolTip != null) && (iconButtonToolTip.length() != 0)) {
        iconButton.setToolTipText(iconButtonToolTip);
      }
      if (minIcon != null) {
        maxButton.setIcon(minIcon);
      }
      if ((restoreButtonToolTip != null) && (restoreButtonToolTip.length() != 0)) {
        maxButton.setToolTipText(restoreButtonToolTip);
      }
    }
    else
    {
      if (iconIcon != null) {
        iconButton.setIcon(iconIcon);
      }
      if ((iconButtonToolTip != null) && (iconButtonToolTip.length() != 0)) {
        iconButton.setToolTipText(iconButtonToolTip);
      }
      if (maxIcon != null) {
        maxButton.setIcon(maxIcon);
      }
      if ((maxButtonToolTip != null) && (maxButtonToolTip.length() != 0)) {
        maxButton.setToolTipText(maxButtonToolTip);
      }
    }
    if (closeIcon != null) {
      closeButton.setIcon(closeIcon);
    }
  }
  
  protected void assembleSystemMenu()
  {
    menuBar = createSystemMenuBar();
    windowMenu = createSystemMenu();
    menuBar.add(windowMenu);
    addSystemMenuItems(windowMenu);
    enableActions();
  }
  
  protected void addSystemMenuItems(JMenu paramJMenu)
  {
    JMenuItem localJMenuItem = paramJMenu.add(restoreAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("restore"));
    localJMenuItem = paramJMenu.add(moveAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("move"));
    localJMenuItem = paramJMenu.add(sizeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("size"));
    localJMenuItem = paramJMenu.add(iconifyAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("minimize"));
    localJMenuItem = paramJMenu.add(maximizeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("maximize"));
    paramJMenu.add(new JSeparator());
    localJMenuItem = paramJMenu.add(closeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("close"));
  }
  
  private static int getButtonMnemonic(String paramString)
  {
    try
    {
      return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + paramString + "Button.mnemonic"));
    }
    catch (NumberFormatException localNumberFormatException) {}
    return -1;
  }
  
  protected JMenu createSystemMenu()
  {
    return new JMenu("    ");
  }
  
  protected JMenuBar createSystemMenuBar()
  {
    menuBar = new SystemMenuBar();
    menuBar.setBorderPainted(false);
    return menuBar;
  }
  
  protected void showSystemMenu()
  {
    windowMenu.doClick();
  }
  
  public void paintComponent(Graphics paramGraphics)
  {
    paintTitleBackground(paramGraphics);
    if (frame.getTitle() != null)
    {
      boolean bool = frame.isSelected();
      Font localFont = paramGraphics.getFont();
      paramGraphics.setFont(getFont());
      if (bool) {
        paramGraphics.setColor(selectedTextColor);
      } else {
        paramGraphics.setColor(notSelectedTextColor);
      }
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(frame, paramGraphics);
      int i = (getHeight() + localFontMetrics.getAscent() - localFontMetrics.getLeading() - localFontMetrics.getDescent()) / 2;
      Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
      if (frame.isIconifiable()) {
        localRectangle = iconButton.getBounds();
      } else if (frame.isMaximizable()) {
        localRectangle = maxButton.getBounds();
      } else if (frame.isClosable()) {
        localRectangle = closeButton.getBounds();
      }
      String str = frame.getTitle();
      int j;
      if (BasicGraphicsUtils.isLeftToRight(frame))
      {
        if (x == 0) {
          x = (frame.getWidth() - frame.getInsets().right);
        }
        j = menuBar.getX() + menuBar.getWidth() + 2;
        int k = x - j - 3;
        str = getTitle(frame.getTitle(), localFontMetrics, k);
      }
      else
      {
        j = menuBar.getX() - 2 - SwingUtilities2.stringWidth(frame, localFontMetrics, str);
      }
      SwingUtilities2.drawString(frame, paramGraphics, str, j, i);
      paramGraphics.setFont(localFont);
    }
  }
  
  protected void paintTitleBackground(Graphics paramGraphics)
  {
    boolean bool = frame.isSelected();
    if (bool) {
      paramGraphics.setColor(selectedTitleColor);
    } else {
      paramGraphics.setColor(notSelectedTitleColor);
    }
    paramGraphics.fillRect(0, 0, getWidth(), getHeight());
  }
  
  protected String getTitle(String paramString, FontMetrics paramFontMetrics, int paramInt)
  {
    return SwingUtilities2.clipStringIfNecessary(frame, paramFontMetrics, paramString, paramInt);
  }
  
  protected void postClosingEvent(JInternalFrame paramJInternalFrame)
  {
    InternalFrameEvent localInternalFrameEvent = new InternalFrameEvent(paramJInternalFrame, 25550);
    try
    {
      Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(localInternalFrameEvent);
    }
    catch (SecurityException localSecurityException)
    {
      paramJInternalFrame.dispatchEvent(localInternalFrameEvent);
    }
  }
  
  protected void enableActions()
  {
    restoreAction.setEnabled((frame.isMaximum()) || (frame.isIcon()));
    maximizeAction.setEnabled(((frame.isMaximizable()) && (!frame.isMaximum()) && (!frame.isIcon())) || ((frame.isMaximizable()) && (frame.isIcon())));
    iconifyAction.setEnabled((frame.isIconifiable()) && (!frame.isIcon()));
    closeAction.setEnabled(frame.isClosable());
    sizeAction.setEnabled(false);
    moveAction.setEnabled(false);
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  protected LayoutManager createLayout()
  {
    return getHandler();
  }
  
  public class CloseAction
    extends AbstractAction
  {
    public CloseAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (frame.isClosable()) {
        frame.doDefaultCloseAction();
      }
    }
  }
  
  private class Handler
    implements LayoutManager, PropertyChangeListener
  {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str == "selected")
      {
        repaint();
        return;
      }
      if ((str == "icon") || (str == "maximum"))
      {
        setButtonIcons();
        enableActions();
        return;
      }
      if ("closable" == str)
      {
        if (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          add(closeButton);
        } else {
          remove(closeButton);
        }
      }
      else if ("maximizable" == str)
      {
        if (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          add(maxButton);
        } else {
          remove(maxButton);
        }
      }
      else if ("iconable" == str) {
        if (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          add(iconButton);
        } else {
          remove(iconButton);
        }
      }
      enableActions();
      revalidate();
      repaint();
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return minimumLayoutSize(paramContainer);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      int i = 22;
      if (frame.isClosable()) {
        i += 19;
      }
      if (frame.isMaximizable()) {
        i += 19;
      }
      if (frame.isIconifiable()) {
        i += 19;
      }
      FontMetrics localFontMetrics = frame.getFontMetrics(getFont());
      String str = frame.getTitle();
      int j = str != null ? SwingUtilities2.stringWidth(frame, localFontMetrics, str) : 0;
      int k = str != null ? str.length() : 0;
      if (k > 3)
      {
        int m = SwingUtilities2.stringWidth(frame, localFontMetrics, str.substring(0, 3) + "...");
        i += (j < m ? j : m);
      }
      else
      {
        i += j;
      }
      Icon localIcon = frame.getFrameIcon();
      int n = localFontMetrics.getHeight();
      n += 2;
      int i1 = 0;
      if (localIcon != null) {
        i1 = Math.min(localIcon.getIconHeight(), 16);
      }
      i1 += 2;
      int i2 = Math.max(n, i1);
      Dimension localDimension = new Dimension(i, i2);
      if (getBorder() != null)
      {
        Insets localInsets = getBorder().getBorderInsets(paramContainer);
        height += top + bottom;
        width += left + right;
      }
      return localDimension;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      boolean bool = BasicGraphicsUtils.isLeftToRight(frame);
      int i = getWidth();
      int j = getHeight();
      int m = closeButton.getIcon().getIconHeight();
      Icon localIcon = frame.getFrameIcon();
      int n = 0;
      if (localIcon != null) {
        n = localIcon.getIconHeight();
      }
      int k = bool ? 2 : i - 16 - 2;
      menuBar.setBounds(k, (j - n) / 2, 16, 16);
      k = bool ? i - 16 - 2 : 2;
      if (frame.isClosable())
      {
        closeButton.setBounds(k, (j - m) / 2, 16, 14);
        k += (bool ? -18 : 18);
      }
      if (frame.isMaximizable())
      {
        maxButton.setBounds(k, (j - m) / 2, 16, 14);
        k += (bool ? -18 : 18);
      }
      if (frame.isIconifiable()) {
        iconButton.setBounds(k, (j - m) / 2, 16, 14);
      }
    }
  }
  
  public class IconifyAction
    extends AbstractAction
  {
    public IconifyAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (frame.isIconifiable()) {
        if (!frame.isIcon()) {
          try
          {
            frame.setIcon(true);
          }
          catch (PropertyVetoException localPropertyVetoException1) {}
        } else {
          try
          {
            frame.setIcon(false);
          }
          catch (PropertyVetoException localPropertyVetoException2) {}
        }
      }
    }
  }
  
  public class MaximizeAction
    extends AbstractAction
  {
    public MaximizeAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (frame.isMaximizable()) {
        if ((frame.isMaximum()) && (frame.isIcon())) {
          try
          {
            frame.setIcon(false);
          }
          catch (PropertyVetoException localPropertyVetoException1) {}
        } else if (!frame.isMaximum()) {
          try
          {
            frame.setMaximum(true);
          }
          catch (PropertyVetoException localPropertyVetoException2) {}
        } else {
          try
          {
            frame.setMaximum(false);
          }
          catch (PropertyVetoException localPropertyVetoException3) {}
        }
      }
    }
  }
  
  public class MoveAction
    extends AbstractAction
  {
    public MoveAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent) {}
  }
  
  private class NoFocusButton
    extends JButton
  {
    private String uiKey;
    
    public NoFocusButton(String paramString1, String paramString2)
    {
      setFocusPainted(false);
      setMargin(new Insets(0, 0, 0, 0));
      uiKey = paramString1;
      Object localObject = UIManager.get(paramString2);
      if ((localObject instanceof Boolean)) {
        setOpaque(((Boolean)localObject).booleanValue());
      }
    }
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    public void requestFocus() {}
    
    public AccessibleContext getAccessibleContext()
    {
      AccessibleContext localAccessibleContext = super.getAccessibleContext();
      if (uiKey != null)
      {
        localAccessibleContext.setAccessibleName(UIManager.getString(uiKey));
        uiKey = null;
      }
      return localAccessibleContext;
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicInternalFrameTitlePane.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  public class RestoreAction
    extends AbstractAction
  {
    public RestoreAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if ((frame.isMaximizable()) && (frame.isMaximum()) && (frame.isIcon())) {
        try
        {
          frame.setIcon(false);
        }
        catch (PropertyVetoException localPropertyVetoException1) {}
      } else if ((frame.isMaximizable()) && (frame.isMaximum())) {
        try
        {
          frame.setMaximum(false);
        }
        catch (PropertyVetoException localPropertyVetoException2) {}
      } else if ((frame.isIconifiable()) && (frame.isIcon())) {
        try
        {
          frame.setIcon(false);
        }
        catch (PropertyVetoException localPropertyVetoException3) {}
      }
    }
  }
  
  private class ShowSystemMenuAction
    extends AbstractAction
  {
    private boolean show;
    
    public ShowSystemMenuAction(boolean paramBoolean)
    {
      show = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (show) {
        windowMenu.doClick();
      } else {
        windowMenu.setVisible(false);
      }
    }
  }
  
  public class SizeAction
    extends AbstractAction
  {
    public SizeAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent) {}
  }
  
  public class SystemMenuBar
    extends JMenuBar
  {
    public SystemMenuBar() {}
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    public void requestFocus() {}
    
    public void paint(Graphics paramGraphics)
    {
      Icon localIcon = frame.getFrameIcon();
      if (localIcon == null) {
        localIcon = (Icon)DefaultLookup.get(frame, frame.getUI(), "InternalFrame.icon");
      }
      if (localIcon != null)
      {
        if (((localIcon instanceof ImageIcon)) && ((localIcon.getIconWidth() > 16) || (localIcon.getIconHeight() > 16)))
        {
          Image localImage = ((ImageIcon)localIcon).getImage();
          ((ImageIcon)localIcon).setImage(localImage.getScaledInstance(16, 16, 4));
        }
        localIcon.paintIcon(this, paramGraphics, 0, 0);
      }
    }
    
    public boolean isOpaque()
    {
      return true;
    }
  }
  
  public class TitlePaneLayout
    implements LayoutManager
  {
    public TitlePaneLayout() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent)
    {
      BasicInternalFrameTitlePane.this.getHandler().addLayoutComponent(paramString, paramComponent);
    }
    
    public void removeLayoutComponent(Component paramComponent)
    {
      BasicInternalFrameTitlePane.this.getHandler().removeLayoutComponent(paramComponent);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return BasicInternalFrameTitlePane.this.getHandler().preferredLayoutSize(paramContainer);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return BasicInternalFrameTitlePane.this.getHandler().minimumLayoutSize(paramContainer);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      BasicInternalFrameTitlePane.this.getHandler().layoutContainer(paramContainer);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */