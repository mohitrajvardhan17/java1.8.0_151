package com.sun.java.swing.plaf.motif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.util.EventListener;
import javax.swing.DesktopManager;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;
import sun.swing.SwingUtilities2;

public class MotifDesktopIconUI
  extends BasicDesktopIconUI
{
  protected DesktopIconActionListener desktopIconActionListener;
  protected DesktopIconMouseListener desktopIconMouseListener;
  protected Icon defaultIcon;
  protected IconButton iconButton;
  protected IconLabel iconLabel;
  private MotifInternalFrameTitlePane sysMenuTitlePane;
  JPopupMenu systemMenu;
  EventListener mml;
  static final int LABEL_HEIGHT = 18;
  static final int LABEL_DIVIDER = 4;
  static final Font defaultTitleFont = new Font("SansSerif", 0, 12);
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifDesktopIconUI();
  }
  
  public MotifDesktopIconUI() {}
  
  protected void installDefaults()
  {
    super.installDefaults();
    setDefaultIcon(UIManager.getIcon("DesktopIcon.icon"));
    iconButton = createIconButton(defaultIcon);
    sysMenuTitlePane = new MotifInternalFrameTitlePane(frame);
    systemMenu = sysMenuTitlePane.getSystemMenu();
    MotifBorders.FrameBorder localFrameBorder = new MotifBorders.FrameBorder(desktopIcon);
    desktopIcon.setLayout(new BorderLayout());
    iconButton.setBorder(localFrameBorder);
    desktopIcon.add(iconButton, "Center");
    iconLabel = createIconLabel(frame);
    iconLabel.setBorder(localFrameBorder);
    desktopIcon.add(iconLabel, "South");
    desktopIcon.setSize(desktopIcon.getPreferredSize());
    desktopIcon.validate();
    JLayeredPane.putLayer(desktopIcon, JLayeredPane.getLayer(frame));
  }
  
  protected void installComponents() {}
  
  protected void uninstallComponents() {}
  
  protected void installListeners()
  {
    super.installListeners();
    desktopIconActionListener = createDesktopIconActionListener();
    desktopIconMouseListener = createDesktopIconMouseListener();
    iconButton.addActionListener(desktopIconActionListener);
    iconButton.addMouseListener(desktopIconMouseListener);
    iconLabel.addMouseListener(desktopIconMouseListener);
  }
  
  JInternalFrame.JDesktopIcon getDesktopIcon()
  {
    return desktopIcon;
  }
  
  void setDesktopIcon(JInternalFrame.JDesktopIcon paramJDesktopIcon)
  {
    desktopIcon = paramJDesktopIcon;
  }
  
  JInternalFrame getFrame()
  {
    return frame;
  }
  
  void setFrame(JInternalFrame paramJInternalFrame)
  {
    frame = paramJInternalFrame;
  }
  
  protected void showSystemMenu()
  {
    systemMenu.show(iconButton, 0, getDesktopIcon().getHeight());
  }
  
  protected void hideSystemMenu()
  {
    systemMenu.setVisible(false);
  }
  
  protected IconLabel createIconLabel(JInternalFrame paramJInternalFrame)
  {
    return new IconLabel(paramJInternalFrame);
  }
  
  protected IconButton createIconButton(Icon paramIcon)
  {
    return new IconButton(paramIcon);
  }
  
  protected DesktopIconActionListener createDesktopIconActionListener()
  {
    return new DesktopIconActionListener();
  }
  
  protected DesktopIconMouseListener createDesktopIconMouseListener()
  {
    return new DesktopIconMouseListener();
  }
  
  protected void uninstallDefaults()
  {
    super.uninstallDefaults();
    desktopIcon.setLayout(null);
    desktopIcon.remove(iconButton);
    desktopIcon.remove(iconLabel);
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    iconButton.removeActionListener(desktopIconActionListener);
    iconButton.removeMouseListener(desktopIconMouseListener);
    sysMenuTitlePane.uninstallListeners();
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    JInternalFrame localJInternalFrame = desktopIcon.getInternalFrame();
    int i = defaultIcon.getIconWidth();
    int j = defaultIcon.getIconHeight() + 18 + 4;
    Border localBorder = localJInternalFrame.getBorder();
    if (localBorder != null)
    {
      i += getBorderInsetsleft + getBorderInsetsright;
      j += getBorderInsetsbottom + getBorderInsetstop;
    }
    return new Dimension(i, j);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return getMinimumSize(paramJComponent);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return getMinimumSize(paramJComponent);
  }
  
  public Icon getDefaultIcon()
  {
    return defaultIcon;
  }
  
  public void setDefaultIcon(Icon paramIcon)
  {
    defaultIcon = paramIcon;
  }
  
  protected class DesktopIconActionListener
    implements ActionListener
  {
    protected DesktopIconActionListener() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      systemMenu.show(iconButton, 0, getDesktopIcon().getHeight());
    }
  }
  
  protected class DesktopIconMouseListener
    extends MouseAdapter
  {
    protected DesktopIconMouseListener() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getClickCount() > 1)
      {
        try
        {
          getFrame().setIcon(false);
        }
        catch (PropertyVetoException localPropertyVetoException) {}
        systemMenu.setVisible(false);
        getFrame().getDesktopPane().getDesktopManager().endDraggingFrame((JComponent)paramMouseEvent.getSource());
      }
    }
  }
  
  protected class IconButton
    extends JButton
  {
    Icon icon;
    
    IconButton(Icon paramIcon)
    {
      super();
      icon = paramIcon;
      addMouseMotionListener(new MouseMotionListener()
      {
        public void mouseDragged(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
        
        public void mouseMoved(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
      });
      addMouseListener(new MouseListener()
      {
        public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
        
        public void mousePressed(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
        
        public void mouseReleased(MouseEvent paramAnonymousMouseEvent)
        {
          if (!systemMenu.isShowing()) {
            forwardEventToParent(paramAnonymousMouseEvent);
          }
        }
        
        public void mouseEntered(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
        
        public void mouseExited(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
      });
    }
    
    void forwardEventToParent(MouseEvent paramMouseEvent)
    {
      getParent().dispatchEvent(new MouseEvent(getParent(), paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), paramMouseEvent.getX(), paramMouseEvent.getY(), paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0));
    }
    
    public boolean isFocusTraversable()
    {
      return false;
    }
  }
  
  protected class IconLabel
    extends JPanel
  {
    JInternalFrame frame;
    
    IconLabel(JInternalFrame paramJInternalFrame)
    {
      frame = paramJInternalFrame;
      setFont(MotifDesktopIconUI.defaultTitleFont);
      addMouseMotionListener(new MouseMotionListener()
      {
        public void mouseDragged(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
        
        public void mouseMoved(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
      });
      addMouseListener(new MouseListener()
      {
        public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
        
        public void mousePressed(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
        
        public void mouseReleased(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
        
        public void mouseEntered(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
        
        public void mouseExited(MouseEvent paramAnonymousMouseEvent)
        {
          forwardEventToParent(paramAnonymousMouseEvent);
        }
      });
    }
    
    void forwardEventToParent(MouseEvent paramMouseEvent)
    {
      getParent().dispatchEvent(new MouseEvent(getParent(), paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), paramMouseEvent.getX(), paramMouseEvent.getY(), paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0));
    }
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    public Dimension getMinimumSize()
    {
      return new Dimension(defaultIcon.getIconWidth() + 1, 22);
    }
    
    public Dimension getPreferredSize()
    {
      String str = frame.getTitle();
      FontMetrics localFontMetrics = frame.getFontMetrics(MotifDesktopIconUI.defaultTitleFont);
      int i = 4;
      if (str != null) {
        i += SwingUtilities2.stringWidth(frame, localFontMetrics, str);
      }
      return new Dimension(i, 22);
    }
    
    public void paint(Graphics paramGraphics)
    {
      super.paint(paramGraphics);
      int i = getWidth() - 1;
      Color localColor = UIManager.getColor("inactiveCaptionBorder").darker().darker();
      paramGraphics.setColor(localColor);
      paramGraphics.setClip(0, 0, getWidth(), getHeight());
      paramGraphics.drawLine(i - 1, 1, i - 1, 1);
      paramGraphics.drawLine(i, 0, i, 0);
      paramGraphics.setColor(UIManager.getColor("inactiveCaption"));
      paramGraphics.fillRect(2, 1, i - 3, 19);
      paramGraphics.setClip(2, 1, i - 4, 18);
      int j = 18 - SwingUtilities2.getFontMetrics(frame, paramGraphics).getDescent();
      paramGraphics.setColor(UIManager.getColor("inactiveCaptionText"));
      String str = frame.getTitle();
      if (str != null) {
        SwingUtilities2.drawString(frame, paramGraphics, str, 4, j);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */