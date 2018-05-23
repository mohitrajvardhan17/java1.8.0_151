package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import sun.swing.SwingUtilities2;

class SynthInternalFrameTitlePane
  extends BasicInternalFrameTitlePane
  implements SynthUI, PropertyChangeListener
{
  protected JPopupMenu systemPopupMenu;
  protected JButton menuButton;
  private SynthStyle style;
  private int titleSpacing;
  private int buttonSpacing;
  private int titleAlignment;
  
  public SynthInternalFrameTitlePane(JInternalFrame paramJInternalFrame)
  {
    super(paramJInternalFrame);
  }
  
  public String getUIClassID()
  {
    return "InternalFrameTitlePaneUI";
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, getComponentState(paramJComponent));
  }
  
  public SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  private Region getRegion(JComponent paramJComponent)
  {
    return SynthLookAndFeel.getRegion(paramJComponent);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    if ((frame != null) && (frame.isSelected())) {
      return 512;
    }
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  protected void addSubComponents()
  {
    menuButton.setName("InternalFrameTitlePane.menuButton");
    iconButton.setName("InternalFrameTitlePane.iconifyButton");
    maxButton.setName("InternalFrameTitlePane.maximizeButton");
    closeButton.setName("InternalFrameTitlePane.closeButton");
    add(menuButton);
    add(iconButton);
    add(maxButton);
    add(closeButton);
  }
  
  protected void installListeners()
  {
    super.installListeners();
    frame.addPropertyChangeListener(this);
    addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners()
  {
    frame.removePropertyChangeListener(this);
    removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  private void updateStyle(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(this, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      maxIcon = style.getIcon(localSynthContext, "InternalFrameTitlePane.maximizeIcon");
      minIcon = style.getIcon(localSynthContext, "InternalFrameTitlePane.minimizeIcon");
      iconIcon = style.getIcon(localSynthContext, "InternalFrameTitlePane.iconifyIcon");
      closeIcon = style.getIcon(localSynthContext, "InternalFrameTitlePane.closeIcon");
      titleSpacing = style.getInt(localSynthContext, "InternalFrameTitlePane.titleSpacing", 2);
      buttonSpacing = style.getInt(localSynthContext, "InternalFrameTitlePane.buttonSpacing", 2);
      String str = (String)style.get(localSynthContext, "InternalFrameTitlePane.titleAlignment");
      titleAlignment = 10;
      if (str != null)
      {
        str = str.toUpperCase();
        if (str.equals("TRAILING")) {
          titleAlignment = 11;
        } else if (str.equals("CENTER")) {
          titleAlignment = 0;
        }
      }
    }
    localSynthContext.dispose();
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    updateStyle(this);
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(this, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    JInternalFrame.JDesktopIcon localJDesktopIcon = frame.getDesktopIcon();
    if ((localJDesktopIcon != null) && (localJDesktopIcon.getComponentPopupMenu() == systemPopupMenu)) {
      localJDesktopIcon.setComponentPopupMenu(null);
    }
    super.uninstallDefaults();
  }
  
  protected void assembleSystemMenu()
  {
    systemPopupMenu = new JPopupMenuUIResource(null);
    addSystemMenuItems(systemPopupMenu);
    enableActions();
    menuButton = createNoFocusButton();
    updateMenuIcon();
    menuButton.addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent paramAnonymousMouseEvent)
      {
        try
        {
          frame.setSelected(true);
        }
        catch (PropertyVetoException localPropertyVetoException) {}
        showSystemMenu();
      }
    });
    JPopupMenu localJPopupMenu = frame.getComponentPopupMenu();
    if ((localJPopupMenu == null) || ((localJPopupMenu instanceof UIResource))) {
      frame.setComponentPopupMenu(systemPopupMenu);
    }
    if (frame.getDesktopIcon() != null)
    {
      localJPopupMenu = frame.getDesktopIcon().getComponentPopupMenu();
      if ((localJPopupMenu == null) || ((localJPopupMenu instanceof UIResource))) {
        frame.getDesktopIcon().setComponentPopupMenu(systemPopupMenu);
      }
    }
    setInheritsPopupMenu(true);
  }
  
  protected void addSystemMenuItems(JPopupMenu paramJPopupMenu)
  {
    JMenuItem localJMenuItem = paramJPopupMenu.add(restoreAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("restore"));
    localJMenuItem = paramJPopupMenu.add(moveAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("move"));
    localJMenuItem = paramJPopupMenu.add(sizeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("size"));
    localJMenuItem = paramJPopupMenu.add(iconifyAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("minimize"));
    localJMenuItem = paramJPopupMenu.add(maximizeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("maximize"));
    paramJPopupMenu.add(new JSeparator());
    localJMenuItem = paramJPopupMenu.add(closeAction);
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
  
  protected void showSystemMenu()
  {
    Insets localInsets = frame.getInsets();
    if (!frame.isIcon()) {
      systemPopupMenu.show(frame, menuButton.getX(), getY() + getHeight());
    } else {
      systemPopupMenu.show(menuButton, getX() - left - right, getY() - systemPopupMenu.getPreferredSize().height - bottom - top);
    }
  }
  
  public void paintComponent(Graphics paramGraphics)
  {
    SynthContext localSynthContext = getContext(this);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintInternalFrameTitlePaneBackground(localSynthContext, paramGraphics, 0, 0, getWidth(), getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    String str1 = frame.getTitle();
    if (str1 != null)
    {
      SynthStyle localSynthStyle = paramSynthContext.getStyle();
      paramGraphics.setColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
      paramGraphics.setFont(localSynthStyle.getFont(paramSynthContext));
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(frame, paramGraphics);
      int i = (getHeight() + localFontMetrics.getAscent() - localFontMetrics.getLeading() - localFontMetrics.getDescent()) / 2;
      JButton localJButton = null;
      if (frame.isIconifiable()) {
        localJButton = iconButton;
      } else if (frame.isMaximizable()) {
        localJButton = maxButton;
      } else if (frame.isClosable()) {
        localJButton = closeButton;
      }
      boolean bool = SynthLookAndFeel.isLeftToRight(frame);
      int m = titleAlignment;
      int j;
      int k;
      if (bool)
      {
        if (localJButton != null) {
          j = localJButton.getX() - titleSpacing;
        } else {
          j = frame.getWidth() - frame.getInsets().right - titleSpacing;
        }
        k = menuButton.getX() + menuButton.getWidth() + titleSpacing;
      }
      else
      {
        if (localJButton != null) {
          k = localJButton.getX() + localJButton.getWidth() + titleSpacing;
        } else {
          k = frame.getInsets().left + titleSpacing;
        }
        j = menuButton.getX() - titleSpacing;
        if (m == 10) {
          m = 11;
        } else if (m == 11) {
          m = 10;
        }
      }
      String str2 = getTitle(str1, localFontMetrics, j - k);
      if (str2 == str1) {
        if (m == 11)
        {
          k = j - localSynthStyle.getGraphicsUtils(paramSynthContext).computeStringWidth(paramSynthContext, paramGraphics.getFont(), localFontMetrics, str1);
        }
        else if (m == 0)
        {
          int n = localSynthStyle.getGraphicsUtils(paramSynthContext).computeStringWidth(paramSynthContext, paramGraphics.getFont(), localFontMetrics, str1);
          k = Math.max(k, (getWidth() - n) / 2);
          k = Math.min(j - n, k);
        }
      }
      localSynthStyle.getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, str2, k, i - localFontMetrics.getAscent(), -1);
    }
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintInternalFrameTitlePaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected LayoutManager createLayout()
  {
    SynthContext localSynthContext = getContext(this);
    LayoutManager localLayoutManager = (LayoutManager)style.get(localSynthContext, "InternalFrameTitlePane.titlePaneLayout");
    localSynthContext.dispose();
    return localLayoutManager != null ? localLayoutManager : new SynthTitlePaneLayout();
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (paramPropertyChangeEvent.getSource() == this)
    {
      if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
        updateStyle(this);
      }
    }
    else if (paramPropertyChangeEvent.getPropertyName() == "frameIcon") {
      updateMenuIcon();
    }
  }
  
  private void updateMenuIcon()
  {
    Object localObject = frame.getFrameIcon();
    SynthContext localSynthContext = getContext(this);
    if (localObject != null)
    {
      Dimension localDimension = (Dimension)localSynthContext.getStyle().get(localSynthContext, "InternalFrameTitlePane.maxFrameIconSize");
      int i = 16;
      int j = 16;
      if (localDimension != null)
      {
        i = width;
        j = height;
      }
      if (((((Icon)localObject).getIconWidth() > i) || (((Icon)localObject).getIconHeight() > j)) && ((localObject instanceof ImageIcon))) {
        localObject = new ImageIcon(((ImageIcon)localObject).getImage().getScaledInstance(i, j, 4));
      }
    }
    localSynthContext.dispose();
    menuButton.setIcon((Icon)localObject);
  }
  
  private JButton createNoFocusButton()
  {
    JButton localJButton = new JButton();
    localJButton.setFocusable(false);
    localJButton.setMargin(new Insets(0, 0, 0, 0));
    return localJButton;
  }
  
  private static class JPopupMenuUIResource
    extends JPopupMenu
    implements UIResource
  {
    private JPopupMenuUIResource() {}
  }
  
  class SynthTitlePaneLayout
    implements LayoutManager
  {
    SynthTitlePaneLayout() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return minimumLayoutSize(paramContainer);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      SynthContext localSynthContext = getContext(SynthInternalFrameTitlePane.this);
      int i = 0;
      int j = 0;
      int k = 0;
      if (frame.isClosable())
      {
        localDimension = closeButton.getPreferredSize();
        i += width;
        j = Math.max(height, j);
        k++;
      }
      if (frame.isMaximizable())
      {
        localDimension = maxButton.getPreferredSize();
        i += width;
        j = Math.max(height, j);
        k++;
      }
      if (frame.isIconifiable())
      {
        localDimension = iconButton.getPreferredSize();
        i += width;
        j = Math.max(height, j);
        k++;
      }
      Dimension localDimension = menuButton.getPreferredSize();
      i += width;
      j = Math.max(height, j);
      i += Math.max(0, (k - 1) * buttonSpacing);
      FontMetrics localFontMetrics = getFontMetrics(getFont());
      SynthGraphicsUtils localSynthGraphicsUtils = localSynthContext.getStyle().getGraphicsUtils(localSynthContext);
      String str = frame.getTitle();
      int m = str != null ? localSynthGraphicsUtils.computeStringWidth(localSynthContext, localFontMetrics.getFont(), localFontMetrics, str) : 0;
      int n = str != null ? str.length() : 0;
      if (n > 3)
      {
        int i1 = localSynthGraphicsUtils.computeStringWidth(localSynthContext, localFontMetrics.getFont(), localFontMetrics, str.substring(0, 3) + "...");
        i += (m < i1 ? m : i1);
      }
      else
      {
        i += m;
      }
      j = Math.max(localFontMetrics.getHeight() + 2, j);
      i += titleSpacing + titleSpacing;
      Insets localInsets = getInsets();
      j += top + bottom;
      i += left + right;
      localSynthContext.dispose();
      return new Dimension(i, j);
    }
    
    private int center(Component paramComponent, Insets paramInsets, int paramInt, boolean paramBoolean)
    {
      Dimension localDimension = paramComponent.getPreferredSize();
      if (paramBoolean) {
        paramInt -= width;
      }
      paramComponent.setBounds(paramInt, top + (getHeight() - top - bottom - height) / 2, width, height);
      if (width > 0)
      {
        if (paramBoolean) {
          return paramInt - buttonSpacing;
        }
        return paramInt + width + buttonSpacing;
      }
      return paramInt;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      Insets localInsets = paramContainer.getInsets();
      int i;
      if (SynthLookAndFeel.isLeftToRight(frame))
      {
        center(menuButton, localInsets, left, false);
        i = getWidth() - right;
        if (frame.isClosable()) {
          i = center(closeButton, localInsets, i, true);
        }
        if (frame.isMaximizable()) {
          i = center(maxButton, localInsets, i, true);
        }
        if (frame.isIconifiable()) {
          i = center(iconButton, localInsets, i, true);
        }
      }
      else
      {
        center(menuButton, localInsets, getWidth() - right, true);
        i = left;
        if (frame.isClosable()) {
          i = center(closeButton, localInsets, i, false);
        }
        if (frame.isMaximizable()) {
          i = center(maxButton, localInsets, i, false);
        }
        if (frame.isIconifiable()) {
          i = center(iconButton, localInsets, i, false);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */