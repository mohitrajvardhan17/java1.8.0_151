package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.PropertyChangeHandler;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.TitlePaneLayout;
import sun.swing.SwingUtilities2;

public class WindowsInternalFrameTitlePane
  extends BasicInternalFrameTitlePane
{
  private Color selectedTitleGradientColor;
  private Color notSelectedTitleGradientColor;
  private JPopupMenu systemPopupMenu;
  private JLabel systemLabel;
  private Font titleFont;
  private int titlePaneHeight;
  private int buttonWidth;
  private int buttonHeight;
  private boolean hotTrackingOn;
  
  public WindowsInternalFrameTitlePane(JInternalFrame paramJInternalFrame)
  {
    super(paramJInternalFrame);
  }
  
  protected void addSubComponents()
  {
    add(systemLabel);
    add(iconButton);
    add(maxButton);
    add(closeButton);
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    titlePaneHeight = UIManager.getInt("InternalFrame.titlePaneHeight");
    buttonWidth = (UIManager.getInt("InternalFrame.titleButtonWidth") - 4);
    buttonHeight = (UIManager.getInt("InternalFrame.titleButtonHeight") - 4);
    Object localObject1 = UIManager.get("InternalFrame.titleButtonToolTipsOn");
    hotTrackingOn = ((localObject1 instanceof Boolean) ? ((Boolean)localObject1).booleanValue() : true);
    Object localObject2;
    if (XPStyle.getXP() != null)
    {
      buttonWidth = buttonHeight;
      localObject2 = XPStyle.getPartSize(TMSchema.Part.WP_CLOSEBUTTON, TMSchema.State.NORMAL);
      if ((localObject2 != null) && (width != 0) && (height != 0)) {
        buttonWidth = ((int)(buttonWidth * width / height));
      }
    }
    else
    {
      buttonWidth += 2;
      localObject2 = UIManager.getColor("InternalFrame.activeBorderColor");
      setBorder(BorderFactory.createLineBorder((Color)localObject2, 1));
    }
    selectedTitleGradientColor = UIManager.getColor("InternalFrame.activeTitleGradient");
    notSelectedTitleGradientColor = UIManager.getColor("InternalFrame.inactiveTitleGradient");
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
  }
  
  protected void createButtons()
  {
    super.createButtons();
    if (XPStyle.getXP() != null)
    {
      iconButton.setContentAreaFilled(false);
      maxButton.setContentAreaFilled(false);
      closeButton.setContentAreaFilled(false);
    }
  }
  
  protected void setButtonIcons()
  {
    super.setButtonIcons();
    if (!hotTrackingOn)
    {
      iconButton.setToolTipText(null);
      maxButton.setToolTipText(null);
      closeButton.setToolTipText(null);
    }
  }
  
  public void paintComponent(Graphics paramGraphics)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    paintTitleBackground(paramGraphics);
    String str1 = frame.getTitle();
    if (str1 != null)
    {
      boolean bool = frame.isSelected();
      Font localFont1 = paramGraphics.getFont();
      Font localFont2 = titleFont != null ? titleFont : getFont();
      paramGraphics.setFont(localFont2);
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(frame, paramGraphics, localFont2);
      int i = (getHeight() + localFontMetrics.getAscent() - localFontMetrics.getLeading() - localFontMetrics.getDescent()) / 2;
      Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
      if (frame.isIconifiable()) {
        localRectangle = iconButton.getBounds();
      } else if (frame.isMaximizable()) {
        localRectangle = maxButton.getBounds();
      } else if (frame.isClosable()) {
        localRectangle = closeButton.getBounds();
      }
      int m = 2;
      int j;
      int k;
      if (WindowsGraphicsUtils.isLeftToRight(frame))
      {
        if (x == 0) {
          x = (frame.getWidth() - frame.getInsets().right);
        }
        j = systemLabel.getX() + systemLabel.getWidth() + m;
        if (localXPStyle != null) {
          j += 2;
        }
        k = x - j - m;
      }
      else
      {
        if (x == 0) {
          x = frame.getInsets().left;
        }
        k = SwingUtilities2.stringWidth(frame, localFontMetrics, str1);
        int n = x + width + m;
        if (localXPStyle != null) {
          n += 2;
        }
        int i1 = systemLabel.getX() - m - n;
        if (i1 > k)
        {
          j = systemLabel.getX() - m - k;
        }
        else
        {
          j = n;
          k = i1;
        }
      }
      str1 = getTitle(frame.getTitle(), localFontMetrics, k);
      if (localXPStyle != null)
      {
        String str2 = null;
        if (bool) {
          str2 = localXPStyle.getString(this, TMSchema.Part.WP_CAPTION, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWTYPE);
        }
        if ("single".equalsIgnoreCase(str2))
        {
          Point localPoint = localXPStyle.getPoint(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWOFFSET);
          Color localColor = localXPStyle.getColor(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWCOLOR, null);
          if ((localPoint != null) && (localColor != null))
          {
            paramGraphics.setColor(localColor);
            SwingUtilities2.drawString(frame, paramGraphics, str1, j + x, i + y);
          }
        }
      }
      paramGraphics.setColor(bool ? selectedTextColor : notSelectedTextColor);
      SwingUtilities2.drawString(frame, paramGraphics, str1, j, i);
      paramGraphics.setFont(localFont1);
    }
  }
  
  public Dimension getPreferredSize()
  {
    return getMinimumSize();
  }
  
  public Dimension getMinimumSize()
  {
    Dimension localDimension = new Dimension(super.getMinimumSize());
    height = (titlePaneHeight + 2);
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      if (frame.isMaximum()) {
        height -= 1;
      } else {
        height += 3;
      }
    }
    return localDimension;
  }
  
  protected void paintTitleBackground(Graphics paramGraphics)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (localXPStyle != null)
    {
      localObject1 = frame.isMaximum() ? TMSchema.Part.WP_MAXCAPTION : frame.isIcon() ? TMSchema.Part.WP_MINCAPTION : TMSchema.Part.WP_CAPTION;
      localObject2 = frame.isSelected() ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
      localObject3 = localXPStyle.getSkin(this, (TMSchema.Part)localObject1);
      ((XPStyle.Skin)localObject3).paintSkin(paramGraphics, 0, 0, getWidth(), getHeight(), (TMSchema.State)localObject2);
    }
    else
    {
      localObject1 = (Boolean)LookAndFeel.getDesktopPropertyValue("win.frame.captionGradientsOn", Boolean.valueOf(false));
      if ((((Boolean)localObject1).booleanValue()) && ((paramGraphics instanceof Graphics2D)))
      {
        localObject2 = (Graphics2D)paramGraphics;
        localObject3 = ((Graphics2D)localObject2).getPaint();
        boolean bool = frame.isSelected();
        int i = getWidth();
        GradientPaint localGradientPaint;
        if (bool)
        {
          localGradientPaint = new GradientPaint(0.0F, 0.0F, selectedTitleColor, (int)(i * 0.75D), 0.0F, selectedTitleGradientColor);
          ((Graphics2D)localObject2).setPaint(localGradientPaint);
        }
        else
        {
          localGradientPaint = new GradientPaint(0.0F, 0.0F, notSelectedTitleColor, (int)(i * 0.75D), 0.0F, notSelectedTitleGradientColor);
          ((Graphics2D)localObject2).setPaint(localGradientPaint);
        }
        ((Graphics2D)localObject2).fillRect(0, 0, getWidth(), getHeight());
        ((Graphics2D)localObject2).setPaint((Paint)localObject3);
      }
      else
      {
        super.paintTitleBackground(paramGraphics);
      }
    }
  }
  
  protected void assembleSystemMenu()
  {
    systemPopupMenu = new JPopupMenu();
    addSystemMenuItems(systemPopupMenu);
    enableActions();
    systemLabel = new JLabel(frame.getFrameIcon())
    {
      protected void paintComponent(Graphics paramAnonymousGraphics)
      {
        int i = 0;
        int j = 0;
        int k = getWidth();
        int m = getHeight();
        paramAnonymousGraphics = paramAnonymousGraphics.create();
        if (isOpaque())
        {
          paramAnonymousGraphics.setColor(getBackground());
          paramAnonymousGraphics.fillRect(0, 0, k, m);
        }
        Icon localIcon = getIcon();
        int n;
        int i1;
        if ((localIcon != null) && ((n = localIcon.getIconWidth()) > 0) && ((i1 = localIcon.getIconHeight()) > 0))
        {
          double d;
          if (n > i1)
          {
            j = (m - k * i1 / n) / 2;
            d = k / n;
          }
          else
          {
            i = (k - m * n / i1) / 2;
            d = m / i1;
          }
          ((Graphics2D)paramAnonymousGraphics).translate(i, j);
          ((Graphics2D)paramAnonymousGraphics).scale(d, d);
          localIcon.paintIcon(this, paramAnonymousGraphics, 0, 0);
        }
        paramAnonymousGraphics.dispose();
      }
    };
    systemLabel.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
      {
        if ((paramAnonymousMouseEvent.getClickCount() == 2) && (frame.isClosable()) && (!frame.isIcon()))
        {
          systemPopupMenu.setVisible(false);
          frame.doDefaultCloseAction();
        }
        else
        {
          super.mouseClicked(paramAnonymousMouseEvent);
        }
      }
      
      public void mousePressed(MouseEvent paramAnonymousMouseEvent)
      {
        try
        {
          frame.setSelected(true);
        }
        catch (PropertyVetoException localPropertyVetoException) {}
        WindowsInternalFrameTitlePane.this.showSystemPopupMenu(paramAnonymousMouseEvent.getComponent());
      }
    });
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
    showSystemPopupMenu(systemLabel);
  }
  
  private void showSystemPopupMenu(Component paramComponent)
  {
    Dimension localDimension = new Dimension();
    Border localBorder = frame.getBorder();
    if (localBorder != null)
    {
      width += getBorderInsetsframe).left + getBorderInsetsframe).right;
      height += getBorderInsetsframe).bottom + getBorderInsetsframe).top;
    }
    if (!frame.isIcon()) {
      systemPopupMenu.show(paramComponent, getX() - width, getY() + getHeight() - height);
    } else {
      systemPopupMenu.show(paramComponent, getX() - width, getY() - systemPopupMenu.getPreferredSize().height - height);
    }
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return new WindowsPropertyChangeHandler();
  }
  
  protected LayoutManager createLayout()
  {
    return new WindowsTitlePaneLayout();
  }
  
  public static class ScalableIconUIResource
    implements Icon, UIResource
  {
    private static final int SIZE = 16;
    private Icon[] icons;
    
    public ScalableIconUIResource(Object[] paramArrayOfObject)
    {
      icons = new Icon[paramArrayOfObject.length];
      for (int i = 0; i < paramArrayOfObject.length; i++) {
        if ((paramArrayOfObject[i] instanceof UIDefaults.LazyValue)) {
          icons[i] = ((Icon)((UIDefaults.LazyValue)paramArrayOfObject[i]).createValue(null));
        } else {
          icons[i] = ((Icon)paramArrayOfObject[i]);
        }
      }
    }
    
    protected Icon getBestIcon(int paramInt)
    {
      if ((icons != null) && (icons.length > 0))
      {
        int i = 0;
        int j = Integer.MAX_VALUE;
        for (int k = 0; k < icons.length; k++)
        {
          Icon localIcon = icons[k];
          int m;
          if ((localIcon != null) && ((m = localIcon.getIconWidth()) > 0))
          {
            int n = Math.abs(m - paramInt);
            if (n < j)
            {
              j = n;
              i = k;
            }
          }
        }
        return icons[i];
      }
      return null;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
      int i = getIconWidth();
      double d1 = localGraphics2D.getTransform().getScaleX();
      Icon localIcon = getBestIcon((int)(i * d1));
      int j;
      if ((localIcon != null) && ((j = localIcon.getIconWidth()) > 0))
      {
        double d2 = i / j;
        localGraphics2D.translate(paramInt1, paramInt2);
        localGraphics2D.scale(d2, d2);
        localIcon.paintIcon(paramComponent, localGraphics2D, 0, 0);
      }
      localGraphics2D.dispose();
    }
    
    public int getIconWidth()
    {
      return 16;
    }
    
    public int getIconHeight()
    {
      return 16;
    }
  }
  
  public class WindowsPropertyChangeHandler
    extends BasicInternalFrameTitlePane.PropertyChangeHandler
  {
    public WindowsPropertyChangeHandler()
    {
      super();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (("frameIcon".equals(str)) && (systemLabel != null)) {
        systemLabel.setIcon(frame.getFrameIcon());
      }
      super.propertyChange(paramPropertyChangeEvent);
    }
  }
  
  public class WindowsTitlePaneLayout
    extends BasicInternalFrameTitlePane.TitlePaneLayout
  {
    private Insets captionMargin = null;
    private Insets contentMargin = null;
    private XPStyle xp = XPStyle.getXP();
    
    WindowsTitlePaneLayout()
    {
      super();
      if (xp != null)
      {
        WindowsInternalFrameTitlePane localWindowsInternalFrameTitlePane = WindowsInternalFrameTitlePane.this;
        captionMargin = xp.getMargin(localWindowsInternalFrameTitlePane, TMSchema.Part.WP_CAPTION, null, TMSchema.Prop.CAPTIONMARGINS);
        contentMargin = xp.getMargin(localWindowsInternalFrameTitlePane, TMSchema.Part.WP_CAPTION, null, TMSchema.Prop.CONTENTMARGINS);
      }
      if (captionMargin == null) {
        captionMargin = new Insets(0, 2, 0, 2);
      }
      if (contentMargin == null) {
        contentMargin = new Insets(0, 0, 0, 0);
      }
    }
    
    private int layoutButton(JComponent paramJComponent, TMSchema.Part paramPart, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
    {
      if (!paramBoolean) {
        paramInt1 -= paramInt3;
      }
      paramJComponent.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      if (paramBoolean) {
        paramInt1 += paramInt3 + 2;
      } else {
        paramInt1 -= 2;
      }
      return paramInt1;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      boolean bool = WindowsGraphicsUtils.isLeftToRight(frame);
      int k = getWidth();
      int m = getHeight();
      int n = xp != null ? (m - 2) * 6 / 10 : m - 4;
      int i;
      if (xp != null) {
        i = bool ? captionMargin.left + 2 : k - captionMargin.right - 2;
      } else {
        i = bool ? captionMargin.left : k - captionMargin.right;
      }
      int j = (m - n) / 2;
      layoutButton(systemLabel, TMSchema.Part.WP_SYSBUTTON, i, j, n, n, 0, bool);
      if (xp != null)
      {
        i = bool ? k - captionMargin.right - 2 : captionMargin.left + 2;
        j = 1;
        if (frame.isMaximum()) {
          j++;
        } else {
          j += 5;
        }
      }
      else
      {
        i = bool ? k - captionMargin.right : captionMargin.left;
        j = (m - buttonHeight) / 2;
      }
      if (frame.isClosable()) {
        i = layoutButton(closeButton, TMSchema.Part.WP_CLOSEBUTTON, i, j, buttonWidth, buttonHeight, 2, !bool);
      }
      if (frame.isMaximizable()) {
        i = layoutButton(maxButton, TMSchema.Part.WP_MAXBUTTON, i, j, buttonWidth, buttonHeight, xp != null ? 2 : 0, !bool);
      }
      if (frame.isIconifiable()) {
        layoutButton(iconButton, TMSchema.Part.WP_MINBUTTON, i, j, buttonWidth, buttonHeight, 0, !bool);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */