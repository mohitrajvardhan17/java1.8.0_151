package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.AbstractAction;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SortingFocusTraversalPolicy;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopPaneUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicDesktopPaneUI
  extends DesktopPaneUI
{
  private static final Actions SHARED_ACTION = new Actions();
  private Handler handler;
  private PropertyChangeListener pcl;
  protected JDesktopPane desktop;
  protected DesktopManager desktopManager;
  @Deprecated
  protected KeyStroke minimizeKey;
  @Deprecated
  protected KeyStroke maximizeKey;
  @Deprecated
  protected KeyStroke closeKey;
  @Deprecated
  protected KeyStroke navigateKey;
  @Deprecated
  protected KeyStroke navigateKey2;
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicDesktopPaneUI();
  }
  
  public BasicDesktopPaneUI() {}
  
  public void installUI(JComponent paramJComponent)
  {
    desktop = ((JDesktopPane)paramJComponent);
    installDefaults();
    installDesktopManager();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallKeyboardActions();
    uninstallListeners();
    uninstallDesktopManager();
    uninstallDefaults();
    desktop = null;
    handler = null;
  }
  
  protected void installDefaults()
  {
    if ((desktop.getBackground() == null) || ((desktop.getBackground() instanceof UIResource))) {
      desktop.setBackground(UIManager.getColor("Desktop.background"));
    }
    LookAndFeel.installProperty(desktop, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults() {}
  
  protected void installListeners()
  {
    pcl = createPropertyChangeListener();
    desktop.addPropertyChangeListener(pcl);
  }
  
  protected void uninstallListeners()
  {
    desktop.removePropertyChangeListener(pcl);
    pcl = null;
  }
  
  protected void installDesktopManager()
  {
    desktopManager = desktop.getDesktopManager();
    if (desktopManager == null)
    {
      desktopManager = new BasicDesktopManager(null);
      desktop.setDesktopManager(desktopManager);
    }
  }
  
  protected void uninstallDesktopManager()
  {
    if ((desktop.getDesktopManager() instanceof UIResource)) {
      desktop.setDesktopManager(null);
    }
    desktopManager = null;
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(2);
    if (localInputMap != null) {
      SwingUtilities.replaceUIInputMap(desktop, 2, localInputMap);
    }
    localInputMap = getInputMap(1);
    if (localInputMap != null) {
      SwingUtilities.replaceUIInputMap(desktop, 1, localInputMap);
    }
    LazyActionMap.installLazyActionMap(desktop, BasicDesktopPaneUI.class, "DesktopPane.actionMap");
    registerKeyboardActions();
  }
  
  protected void registerKeyboardActions() {}
  
  protected void unregisterKeyboardActions() {}
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 2) {
      return createInputMap(paramInt);
    }
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(desktop, this, "Desktop.ancestorInputMap");
    }
    return null;
  }
  
  InputMap createInputMap(int paramInt)
  {
    if (paramInt == 2)
    {
      Object[] arrayOfObject = (Object[])DefaultLookup.get(desktop, this, "Desktop.windowBindings");
      if (arrayOfObject != null) {
        return LookAndFeel.makeComponentInputMap(desktop, arrayOfObject);
      }
    }
    return null;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions(Actions.RESTORE));
    paramLazyActionMap.put(new Actions(Actions.CLOSE));
    paramLazyActionMap.put(new Actions(Actions.MOVE));
    paramLazyActionMap.put(new Actions(Actions.RESIZE));
    paramLazyActionMap.put(new Actions(Actions.LEFT));
    paramLazyActionMap.put(new Actions(Actions.SHRINK_LEFT));
    paramLazyActionMap.put(new Actions(Actions.RIGHT));
    paramLazyActionMap.put(new Actions(Actions.SHRINK_RIGHT));
    paramLazyActionMap.put(new Actions(Actions.UP));
    paramLazyActionMap.put(new Actions(Actions.SHRINK_UP));
    paramLazyActionMap.put(new Actions(Actions.DOWN));
    paramLazyActionMap.put(new Actions(Actions.SHRINK_DOWN));
    paramLazyActionMap.put(new Actions(Actions.ESCAPE));
    paramLazyActionMap.put(new Actions(Actions.MINIMIZE));
    paramLazyActionMap.put(new Actions(Actions.MAXIMIZE));
    paramLazyActionMap.put(new Actions(Actions.NEXT_FRAME));
    paramLazyActionMap.put(new Actions(Actions.PREVIOUS_FRAME));
    paramLazyActionMap.put(new Actions(Actions.NAVIGATE_NEXT));
    paramLazyActionMap.put(new Actions(Actions.NAVIGATE_PREVIOUS));
  }
  
  protected void uninstallKeyboardActions()
  {
    unregisterKeyboardActions();
    SwingUtilities.replaceUIInputMap(desktop, 2, null);
    SwingUtilities.replaceUIInputMap(desktop, 1, null);
    SwingUtilities.replaceUIActionMap(desktop, null);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {}
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return null;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    return new Dimension(0, 0);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  private static class Actions
    extends UIAction
  {
    private static String CLOSE = "close";
    private static String ESCAPE = "escape";
    private static String MAXIMIZE = "maximize";
    private static String MINIMIZE = "minimize";
    private static String MOVE = "move";
    private static String RESIZE = "resize";
    private static String RESTORE = "restore";
    private static String LEFT = "left";
    private static String RIGHT = "right";
    private static String UP = "up";
    private static String DOWN = "down";
    private static String SHRINK_LEFT = "shrinkLeft";
    private static String SHRINK_RIGHT = "shrinkRight";
    private static String SHRINK_UP = "shrinkUp";
    private static String SHRINK_DOWN = "shrinkDown";
    private static String NEXT_FRAME = "selectNextFrame";
    private static String PREVIOUS_FRAME = "selectPreviousFrame";
    private static String NAVIGATE_NEXT = "navigateNext";
    private static String NAVIGATE_PREVIOUS = "navigatePrevious";
    private final int MOVE_RESIZE_INCREMENT = 10;
    private static boolean moving = false;
    private static boolean resizing = false;
    private static JInternalFrame sourceFrame = null;
    private static Component focusOwner = null;
    
    Actions()
    {
      super();
    }
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JDesktopPane localJDesktopPane = (JDesktopPane)paramActionEvent.getSource();
      String str = getName();
      if ((CLOSE == str) || (MAXIMIZE == str) || (MINIMIZE == str) || (RESTORE == str))
      {
        setState(localJDesktopPane, str);
      }
      else if (ESCAPE == str)
      {
        if ((sourceFrame == localJDesktopPane.getSelectedFrame()) && (focusOwner != null)) {
          focusOwner.requestFocus();
        }
        moving = false;
        resizing = false;
        sourceFrame = null;
        focusOwner = null;
      }
      else if ((MOVE == str) || (RESIZE == str))
      {
        sourceFrame = localJDesktopPane.getSelectedFrame();
        if (sourceFrame == null) {
          return;
        }
        moving = str == MOVE;
        resizing = str == RESIZE;
        focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (!SwingUtilities.isDescendingFrom(focusOwner, sourceFrame)) {
          focusOwner = null;
        }
        sourceFrame.requestFocus();
      }
      else
      {
        Object localObject1;
        Object localObject2;
        Object localObject3;
        if ((LEFT == str) || (RIGHT == str) || (UP == str) || (DOWN == str) || (SHRINK_RIGHT == str) || (SHRINK_LEFT == str) || (SHRINK_UP == str) || (SHRINK_DOWN == str))
        {
          JInternalFrame localJInternalFrame = localJDesktopPane.getSelectedFrame();
          if ((sourceFrame == null) || (localJInternalFrame != sourceFrame) || (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != sourceFrame)) {
            return;
          }
          localObject1 = UIManager.getInsets("Desktop.minOnScreenInsets");
          localObject2 = localJInternalFrame.getSize();
          localObject3 = localJInternalFrame.getMinimumSize();
          int j = localJDesktopPane.getWidth();
          int k = localJDesktopPane.getHeight();
          Point localPoint = localJInternalFrame.getLocation();
          if (LEFT == str)
          {
            if (moving)
            {
              localJInternalFrame.setLocation(x + width - 10 < right ? -width + right : x - 10, y);
            }
            else if (resizing)
            {
              localJInternalFrame.setLocation(x - 10, y);
              localJInternalFrame.setSize(width + 10, height);
            }
          }
          else if (RIGHT == str)
          {
            if (moving) {
              localJInternalFrame.setLocation(x + 10 > j - left ? j - left : x + 10, y);
            } else if (resizing) {
              localJInternalFrame.setSize(width + 10, height);
            }
          }
          else if (UP == str)
          {
            if (moving)
            {
              localJInternalFrame.setLocation(x, y + height - 10 < bottom ? -height + bottom : y - 10);
            }
            else if (resizing)
            {
              localJInternalFrame.setLocation(x, y - 10);
              localJInternalFrame.setSize(width, height + 10);
            }
          }
          else if (DOWN == str)
          {
            if (moving) {
              localJInternalFrame.setLocation(x, y + 10 > k - top ? k - top : y + 10);
            } else if (resizing) {
              localJInternalFrame.setSize(width, height + 10);
            }
          }
          else
          {
            int m;
            if ((SHRINK_LEFT == str) && (resizing))
            {
              if (width < width - 10) {
                m = 10;
              } else {
                m = width - width;
              }
              if (x + width - m < left) {
                m = x + width - left;
              }
              localJInternalFrame.setSize(width - m, height);
            }
            else if ((SHRINK_RIGHT == str) && (resizing))
            {
              if (width < width - 10) {
                m = 10;
              } else {
                m = width - width;
              }
              if (x + m > j - right) {
                m = j - right - x;
              }
              localJInternalFrame.setLocation(x + m, y);
              localJInternalFrame.setSize(width - m, height);
            }
            else if ((SHRINK_UP == str) && (resizing))
            {
              if (height < height - 10) {
                m = 10;
              } else {
                m = height - height;
              }
              if (y + height - m < bottom) {
                m = y + height - bottom;
              }
              localJInternalFrame.setSize(width, height - m);
            }
            else if ((SHRINK_DOWN == str) && (resizing))
            {
              if (height < height - 10) {
                m = 10;
              } else {
                m = height - height;
              }
              if (y + m > k - top) {
                m = k - top - y;
              }
              localJInternalFrame.setLocation(x, y + m);
              localJInternalFrame.setSize(width, height - m);
            }
          }
        }
        else if ((NEXT_FRAME == str) || (PREVIOUS_FRAME == str))
        {
          localJDesktopPane.selectFrame(str == NEXT_FRAME);
        }
        else if ((NAVIGATE_NEXT == str) || (NAVIGATE_PREVIOUS == str))
        {
          int i = 1;
          if (NAVIGATE_PREVIOUS == str) {
            i = 0;
          }
          localObject1 = localJDesktopPane.getFocusCycleRootAncestor();
          if (localObject1 != null)
          {
            localObject2 = ((Container)localObject1).getFocusTraversalPolicy();
            if ((localObject2 != null) && ((localObject2 instanceof SortingFocusTraversalPolicy)))
            {
              localObject3 = (SortingFocusTraversalPolicy)localObject2;
              boolean bool = ((SortingFocusTraversalPolicy)localObject3).getImplicitDownCycleTraversal();
              try
              {
                ((SortingFocusTraversalPolicy)localObject3).setImplicitDownCycleTraversal(false);
                if (i != 0) {
                  KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(localJDesktopPane);
                } else {
                  KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(localJDesktopPane);
                }
              }
              finally
              {
                ((SortingFocusTraversalPolicy)localObject3).setImplicitDownCycleTraversal(bool);
              }
            }
          }
        }
      }
    }
    
    private void setState(JDesktopPane paramJDesktopPane, String paramString)
    {
      JInternalFrame localJInternalFrame;
      if (paramString == CLOSE)
      {
        localJInternalFrame = paramJDesktopPane.getSelectedFrame();
        if (localJInternalFrame == null) {
          return;
        }
        localJInternalFrame.doDefaultCloseAction();
      }
      else if (paramString == MAXIMIZE)
      {
        localJInternalFrame = paramJDesktopPane.getSelectedFrame();
        if (localJInternalFrame == null) {
          return;
        }
        if (!localJInternalFrame.isMaximum()) {
          if (localJInternalFrame.isIcon()) {
            try
            {
              localJInternalFrame.setIcon(false);
              localJInternalFrame.setMaximum(true);
            }
            catch (PropertyVetoException localPropertyVetoException1) {}
          } else {
            try
            {
              localJInternalFrame.setMaximum(true);
            }
            catch (PropertyVetoException localPropertyVetoException2) {}
          }
        }
      }
      else if (paramString == MINIMIZE)
      {
        localJInternalFrame = paramJDesktopPane.getSelectedFrame();
        if (localJInternalFrame == null) {
          return;
        }
        if (!localJInternalFrame.isIcon()) {
          try
          {
            localJInternalFrame.setIcon(true);
          }
          catch (PropertyVetoException localPropertyVetoException3) {}
        }
      }
      else if (paramString == RESTORE)
      {
        localJInternalFrame = paramJDesktopPane.getSelectedFrame();
        if (localJInternalFrame == null) {
          return;
        }
        try
        {
          if (localJInternalFrame.isIcon()) {
            localJInternalFrame.setIcon(false);
          } else if (localJInternalFrame.isMaximum()) {
            localJInternalFrame.setMaximum(false);
          }
          localJInternalFrame.setSelected(true);
        }
        catch (PropertyVetoException localPropertyVetoException4) {}
      }
    }
    
    public boolean isEnabled(Object paramObject)
    {
      if ((paramObject instanceof JDesktopPane))
      {
        JDesktopPane localJDesktopPane = (JDesktopPane)paramObject;
        String str = getName();
        if ((str == NEXT_FRAME) || (str == PREVIOUS_FRAME)) {
          return true;
        }
        JInternalFrame localJInternalFrame = localJDesktopPane.getSelectedFrame();
        if (localJInternalFrame == null) {
          return false;
        }
        if (str == CLOSE) {
          return localJInternalFrame.isClosable();
        }
        if (str == MINIMIZE) {
          return localJInternalFrame.isIconifiable();
        }
        if (str == MAXIMIZE) {
          return localJInternalFrame.isMaximizable();
        }
        return true;
      }
      return false;
    }
  }
  
  private class BasicDesktopManager
    extends DefaultDesktopManager
    implements UIResource
  {
    private BasicDesktopManager() {}
  }
  
  protected class CloseAction
    extends AbstractAction
  {
    protected CloseAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JDesktopPane localJDesktopPane = (JDesktopPane)paramActionEvent.getSource();
      BasicDesktopPaneUI.SHARED_ACTION.setState(localJDesktopPane, BasicDesktopPaneUI.Actions.CLOSE);
    }
    
    public boolean isEnabled()
    {
      JInternalFrame localJInternalFrame = desktop.getSelectedFrame();
      if (localJInternalFrame != null) {
        return localJInternalFrame.isClosable();
      }
      return false;
    }
  }
  
  private class Handler
    implements PropertyChangeListener
  {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if ("desktopManager" == str) {
        installDesktopManager();
      }
    }
  }
  
  protected class MaximizeAction
    extends AbstractAction
  {
    protected MaximizeAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JDesktopPane localJDesktopPane = (JDesktopPane)paramActionEvent.getSource();
      BasicDesktopPaneUI.SHARED_ACTION.setState(localJDesktopPane, BasicDesktopPaneUI.Actions.MAXIMIZE);
    }
    
    public boolean isEnabled()
    {
      JInternalFrame localJInternalFrame = desktop.getSelectedFrame();
      if (localJInternalFrame != null) {
        return localJInternalFrame.isMaximizable();
      }
      return false;
    }
  }
  
  protected class MinimizeAction
    extends AbstractAction
  {
    protected MinimizeAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JDesktopPane localJDesktopPane = (JDesktopPane)paramActionEvent.getSource();
      BasicDesktopPaneUI.SHARED_ACTION.setState(localJDesktopPane, BasicDesktopPaneUI.Actions.MINIMIZE);
    }
    
    public boolean isEnabled()
    {
      JInternalFrame localJInternalFrame = desktop.getSelectedFrame();
      if (localJInternalFrame != null) {
        return localJInternalFrame.isIconifiable();
      }
      return false;
    }
  }
  
  protected class NavigateAction
    extends AbstractAction
  {
    protected NavigateAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JDesktopPane localJDesktopPane = (JDesktopPane)paramActionEvent.getSource();
      localJDesktopPane.selectFrame(true);
    }
    
    public boolean isEnabled()
    {
      return true;
    }
  }
  
  protected class OpenAction
    extends AbstractAction
  {
    protected OpenAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JDesktopPane localJDesktopPane = (JDesktopPane)paramActionEvent.getSource();
      BasicDesktopPaneUI.SHARED_ACTION.setState(localJDesktopPane, BasicDesktopPaneUI.Actions.RESTORE);
    }
    
    public boolean isEnabled()
    {
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicDesktopPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */