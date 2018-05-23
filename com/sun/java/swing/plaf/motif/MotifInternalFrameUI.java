package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class MotifInternalFrameUI
  extends BasicInternalFrameUI
{
  Color color;
  Color highlight;
  Color shadow;
  MotifInternalFrameTitlePane titlePane;
  @Deprecated
  protected KeyStroke closeMenuKey;
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifInternalFrameUI((JInternalFrame)paramJComponent);
  }
  
  public MotifInternalFrameUI(JInternalFrame paramJInternalFrame)
  {
    super(paramJInternalFrame);
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    setColors((JInternalFrame)paramJComponent);
  }
  
  protected void installDefaults()
  {
    Border localBorder = frame.getBorder();
    frame.setLayout(internalFrameLayout = createLayoutManager());
    if ((localBorder == null) || ((localBorder instanceof UIResource))) {
      frame.setBorder(new MotifBorders.InternalFrameBorder(frame));
    }
  }
  
  protected void installKeyboardActions()
  {
    super.installKeyboardActions();
    closeMenuKey = KeyStroke.getKeyStroke(27, 0);
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(frame);
    frame.setLayout(null);
    internalFrameLayout = null;
  }
  
  private JInternalFrame getFrame()
  {
    return frame;
  }
  
  public JComponent createNorthPane(JInternalFrame paramJInternalFrame)
  {
    titlePane = new MotifInternalFrameTitlePane(paramJInternalFrame);
    return titlePane;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return Toolkit.getDefaultToolkit().getScreenSize();
  }
  
  protected void uninstallKeyboardActions()
  {
    super.uninstallKeyboardActions();
    if (isKeyBindingRegistered())
    {
      JInternalFrame.JDesktopIcon localJDesktopIcon = frame.getDesktopIcon();
      SwingUtilities.replaceUIActionMap(localJDesktopIcon, null);
      SwingUtilities.replaceUIInputMap(localJDesktopIcon, 2, null);
    }
  }
  
  protected void setupMenuOpenKey()
  {
    super.setupMenuOpenKey();
    ActionMap localActionMap = SwingUtilities.getUIActionMap(frame);
    if (localActionMap != null) {
      localActionMap.put("showSystemMenu", new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          titlePane.showSystemMenu();
        }
        
        public boolean isEnabled()
        {
          return isKeyBindingActive();
        }
      });
    }
  }
  
  protected void setupMenuCloseKey()
  {
    ActionMap localActionMap = SwingUtilities.getUIActionMap(frame);
    if (localActionMap != null) {
      localActionMap.put("hideSystemMenu", new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          titlePane.hideSystemMenu();
        }
        
        public boolean isEnabled()
        {
          return isKeyBindingActive();
        }
      });
    }
    JInternalFrame.JDesktopIcon localJDesktopIcon = frame.getDesktopIcon();
    Object localObject1 = SwingUtilities.getUIInputMap(localJDesktopIcon, 2);
    if (localObject1 == null)
    {
      localObject2 = (Object[])UIManager.get("DesktopIcon.windowBindings");
      if (localObject2 != null)
      {
        localObject1 = LookAndFeel.makeComponentInputMap(localJDesktopIcon, (Object[])localObject2);
        SwingUtilities.replaceUIInputMap(localJDesktopIcon, 2, (InputMap)localObject1);
      }
    }
    Object localObject2 = SwingUtilities.getUIActionMap(localJDesktopIcon);
    if (localObject2 == null)
    {
      localObject2 = new ActionMapUIResource();
      ((ActionMap)localObject2).put("hideSystemMenu", new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          JInternalFrame.JDesktopIcon localJDesktopIcon = MotifInternalFrameUI.this.getFrame().getDesktopIcon();
          MotifDesktopIconUI localMotifDesktopIconUI = (MotifDesktopIconUI)localJDesktopIcon.getUI();
          localMotifDesktopIconUI.hideSystemMenu();
        }
        
        public boolean isEnabled()
        {
          return isKeyBindingActive();
        }
      });
      SwingUtilities.replaceUIActionMap(localJDesktopIcon, (ActionMap)localObject2);
    }
  }
  
  protected void activateFrame(JInternalFrame paramJInternalFrame)
  {
    super.activateFrame(paramJInternalFrame);
    setColors(paramJInternalFrame);
  }
  
  protected void deactivateFrame(JInternalFrame paramJInternalFrame)
  {
    setColors(paramJInternalFrame);
    super.deactivateFrame(paramJInternalFrame);
  }
  
  void setColors(JInternalFrame paramJInternalFrame)
  {
    if (paramJInternalFrame.isSelected()) {
      color = UIManager.getColor("InternalFrame.activeTitleBackground");
    } else {
      color = UIManager.getColor("InternalFrame.inactiveTitleBackground");
    }
    highlight = color.brighter();
    shadow = color.darker().darker();
    titlePane.setColors(color, highlight, shadow);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifInternalFrameUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */