package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class MotifInternalFrameTitlePane
  extends BasicInternalFrameTitlePane
  implements LayoutManager, ActionListener, PropertyChangeListener
{
  SystemButton systemButton;
  MinimizeButton minimizeButton;
  MaximizeButton maximizeButton;
  JPopupMenu systemMenu;
  Title title;
  Color color;
  Color highlight;
  Color shadow;
  public static final int BUTTON_SIZE = 19;
  static Dimension buttonDimension = new Dimension(19, 19);
  
  public MotifInternalFrameTitlePane(JInternalFrame paramJInternalFrame)
  {
    super(paramJInternalFrame);
  }
  
  protected void installDefaults()
  {
    setFont(UIManager.getFont("InternalFrame.titleFont"));
    setPreferredSize(new Dimension(100, 19));
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return this;
  }
  
  protected LayoutManager createLayout()
  {
    return this;
  }
  
  JPopupMenu getSystemMenu()
  {
    return systemMenu;
  }
  
  protected void assembleSystemMenu()
  {
    systemMenu = new JPopupMenu();
    JMenuItem localJMenuItem = systemMenu.add(restoreAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("restore"));
    localJMenuItem = systemMenu.add(moveAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("move"));
    localJMenuItem = systemMenu.add(sizeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("size"));
    localJMenuItem = systemMenu.add(iconifyAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("minimize"));
    localJMenuItem = systemMenu.add(maximizeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("maximize"));
    systemMenu.add(new JSeparator());
    localJMenuItem = systemMenu.add(closeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("close"));
    systemButton = new SystemButton(null);
    systemButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        systemMenu.show(systemButton, 0, 19);
      }
    });
    systemButton.addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent paramAnonymousMouseEvent)
      {
        try
        {
          frame.setSelected(true);
        }
        catch (PropertyVetoException localPropertyVetoException) {}
        if (paramAnonymousMouseEvent.getClickCount() == 2)
        {
          closeAction.actionPerformed(new ActionEvent(paramAnonymousMouseEvent.getSource(), 1001, null, paramAnonymousMouseEvent.getWhen(), 0));
          systemMenu.setVisible(false);
        }
      }
    });
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
  
  protected void createButtons()
  {
    minimizeButton = new MinimizeButton(null);
    minimizeButton.addActionListener(iconifyAction);
    maximizeButton = new MaximizeButton(null);
    maximizeButton.addActionListener(maximizeAction);
  }
  
  protected void addSubComponents()
  {
    title = new Title(frame.getTitle());
    title.setFont(getFont());
    add(systemButton);
    add(title);
    add(minimizeButton);
    add(maximizeButton);
  }
  
  public void paintComponent(Graphics paramGraphics) {}
  
  void setColors(Color paramColor1, Color paramColor2, Color paramColor3)
  {
    color = paramColor1;
    highlight = paramColor2;
    shadow = paramColor3;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {}
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    JInternalFrame localJInternalFrame = (JInternalFrame)paramPropertyChangeEvent.getSource();
    int i = 0;
    if ("selected".equals(str))
    {
      repaint();
    }
    else if (str.equals("maximizable"))
    {
      if ((Boolean)paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
        add(maximizeButton);
      } else {
        remove(maximizeButton);
      }
      revalidate();
      repaint();
    }
    else if (str.equals("iconable"))
    {
      if ((Boolean)paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
        add(minimizeButton);
      } else {
        remove(minimizeButton);
      }
      revalidate();
      repaint();
    }
    else if (str.equals("title"))
    {
      repaint();
    }
    enableActions();
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    return minimumLayoutSize(paramContainer);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    return new Dimension(100, 19);
  }
  
  public void layoutContainer(Container paramContainer)
  {
    int i = getWidth();
    systemButton.setBounds(0, 0, 19, 19);
    int j = i - 19;
    if (frame.isMaximizable())
    {
      maximizeButton.setBounds(j, 0, 19, 19);
      j -= 19;
    }
    else if (maximizeButton.getParent() != null)
    {
      maximizeButton.getParent().remove(maximizeButton);
    }
    if (frame.isIconifiable())
    {
      minimizeButton.setBounds(j, 0, 19, 19);
      j -= 19;
    }
    else if (minimizeButton.getParent() != null)
    {
      minimizeButton.getParent().remove(minimizeButton);
    }
    title.setBounds(19, 0, j, 19);
  }
  
  protected void showSystemMenu()
  {
    systemMenu.show(systemButton, 0, 19);
  }
  
  protected void hideSystemMenu()
  {
    systemMenu.setVisible(false);
  }
  
  private abstract class FrameButton
    extends JButton
  {
    FrameButton()
    {
      setFocusPainted(false);
      setBorderPainted(false);
    }
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    public void requestFocus() {}
    
    public Dimension getMinimumSize()
    {
      return MotifInternalFrameTitlePane.buttonDimension;
    }
    
    public Dimension getPreferredSize()
    {
      return MotifInternalFrameTitlePane.buttonDimension;
    }
    
    public void paintComponent(Graphics paramGraphics)
    {
      Dimension localDimension = getSize();
      int i = width - 1;
      int j = height - 1;
      paramGraphics.setColor(color);
      paramGraphics.fillRect(1, 1, width, height);
      boolean bool = getModel().isPressed();
      paramGraphics.setColor(bool ? shadow : highlight);
      paramGraphics.drawLine(0, 0, i, 0);
      paramGraphics.drawLine(0, 0, 0, j);
      paramGraphics.setColor(bool ? highlight : shadow);
      paramGraphics.drawLine(1, j, i, j);
      paramGraphics.drawLine(i, 1, i, j);
    }
  }
  
  private class MaximizeButton
    extends MotifInternalFrameTitlePane.FrameButton
  {
    private MaximizeButton()
    {
      super();
    }
    
    public void paintComponent(Graphics paramGraphics)
    {
      super.paintComponent(paramGraphics);
      int i = 14;
      boolean bool = frame.isMaximum();
      paramGraphics.setColor(bool ? shadow : highlight);
      paramGraphics.drawLine(4, 4, 4, i);
      paramGraphics.drawLine(4, 4, i, 4);
      paramGraphics.setColor(bool ? highlight : shadow);
      paramGraphics.drawLine(5, i, i, i);
      paramGraphics.drawLine(i, 5, i, i);
    }
  }
  
  private class MinimizeButton
    extends MotifInternalFrameTitlePane.FrameButton
  {
    private MinimizeButton()
    {
      super();
    }
    
    public void paintComponent(Graphics paramGraphics)
    {
      super.paintComponent(paramGraphics);
      paramGraphics.setColor(highlight);
      paramGraphics.drawLine(7, 8, 7, 11);
      paramGraphics.drawLine(7, 8, 10, 8);
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(8, 11, 10, 11);
      paramGraphics.drawLine(11, 9, 11, 11);
    }
  }
  
  private class SystemButton
    extends MotifInternalFrameTitlePane.FrameButton
  {
    private SystemButton()
    {
      super();
    }
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    public void requestFocus() {}
    
    public void paintComponent(Graphics paramGraphics)
    {
      super.paintComponent(paramGraphics);
      paramGraphics.setColor(highlight);
      paramGraphics.drawLine(4, 8, 4, 11);
      paramGraphics.drawLine(4, 8, 14, 8);
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(5, 11, 14, 11);
      paramGraphics.drawLine(14, 9, 14, 11);
    }
  }
  
  private class Title
    extends MotifInternalFrameTitlePane.FrameButton
  {
    Title(String paramString)
    {
      super();
      setText(paramString);
      setHorizontalAlignment(0);
      setBorder(BorderFactory.createBevelBorder(0, UIManager.getColor("activeCaptionBorder"), UIManager.getColor("inactiveCaptionBorder")));
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
    
    public void paintComponent(Graphics paramGraphics)
    {
      super.paintComponent(paramGraphics);
      if (frame.isSelected()) {
        paramGraphics.setColor(UIManager.getColor("activeCaptionText"));
      } else {
        paramGraphics.setColor(UIManager.getColor("inactiveCaptionText"));
      }
      Dimension localDimension = getSize();
      String str = frame.getTitle();
      if (str != null) {
        MotifGraphicsUtils.drawStringInRect(frame, paramGraphics, str, 0, 0, width, height, 0);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */