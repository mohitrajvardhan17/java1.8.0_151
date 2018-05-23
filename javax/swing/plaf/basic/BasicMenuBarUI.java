package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SingleSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicMenuBarUI
  extends MenuBarUI
{
  protected JMenuBar menuBar = null;
  protected ContainerListener containerListener;
  protected ChangeListener changeListener;
  private Handler handler;
  
  public BasicMenuBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicMenuBarUI();
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("takeFocus"));
  }
  
  public void installUI(JComponent paramJComponent)
  {
    menuBar = ((JMenuBar)paramJComponent);
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  protected void installDefaults()
  {
    if ((menuBar.getLayout() == null) || ((menuBar.getLayout() instanceof UIResource))) {
      menuBar.setLayout(new DefaultMenuLayout(menuBar, 2));
    }
    LookAndFeel.installProperty(menuBar, "opaque", Boolean.TRUE);
    LookAndFeel.installBorder(menuBar, "MenuBar.border");
    LookAndFeel.installColorsAndFont(menuBar, "MenuBar.background", "MenuBar.foreground", "MenuBar.font");
  }
  
  protected void installListeners()
  {
    containerListener = createContainerListener();
    changeListener = createChangeListener();
    for (int i = 0; i < menuBar.getMenuCount(); i++)
    {
      JMenu localJMenu = menuBar.getMenu(i);
      if (localJMenu != null) {
        localJMenu.getModel().addChangeListener(changeListener);
      }
    }
    menuBar.addContainerListener(containerListener);
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(2);
    SwingUtilities.replaceUIInputMap(menuBar, 2, localInputMap);
    LazyActionMap.installLazyActionMap(menuBar, BasicMenuBarUI.class, "MenuBar.actionMap");
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 2)
    {
      Object[] arrayOfObject = (Object[])DefaultLookup.get(menuBar, this, "MenuBar.windowBindings");
      if (arrayOfObject != null) {
        return LookAndFeel.makeComponentInputMap(menuBar, arrayOfObject);
      }
    }
    return null;
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    menuBar = null;
  }
  
  protected void uninstallDefaults()
  {
    if (menuBar != null) {
      LookAndFeel.uninstallBorder(menuBar);
    }
  }
  
  protected void uninstallListeners()
  {
    menuBar.removeContainerListener(containerListener);
    for (int i = 0; i < menuBar.getMenuCount(); i++)
    {
      JMenu localJMenu = menuBar.getMenu(i);
      if (localJMenu != null) {
        localJMenu.getModel().removeChangeListener(changeListener);
      }
    }
    containerListener = null;
    changeListener = null;
    handler = null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIInputMap(menuBar, 2, null);
    SwingUtilities.replaceUIActionMap(menuBar, null);
  }
  
  protected ContainerListener createContainerListener()
  {
    return getHandler();
  }
  
  protected ChangeListener createChangeListener()
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
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    return null;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return null;
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String TAKE_FOCUS = "takeFocus";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JMenuBar localJMenuBar = (JMenuBar)paramActionEvent.getSource();
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      JMenu localJMenu = localJMenuBar.getMenu(0);
      if (localJMenu != null)
      {
        MenuElement[] arrayOfMenuElement = new MenuElement[3];
        arrayOfMenuElement[0] = localJMenuBar;
        arrayOfMenuElement[1] = localJMenu;
        arrayOfMenuElement[2] = localJMenu.getPopupMenu();
        localMenuSelectionManager.setSelectedPath(arrayOfMenuElement);
      }
    }
  }
  
  private class Handler
    implements ChangeListener, ContainerListener
  {
    private Handler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      int i = 0;
      int j = menuBar.getMenuCount();
      while (i < j)
      {
        JMenu localJMenu = menuBar.getMenu(i);
        if ((localJMenu != null) && (localJMenu.isSelected()))
        {
          menuBar.getSelectionModel().setSelectedIndex(i);
          break;
        }
        i++;
      }
    }
    
    public void componentAdded(ContainerEvent paramContainerEvent)
    {
      Component localComponent = paramContainerEvent.getChild();
      if ((localComponent instanceof JMenu)) {
        ((JMenu)localComponent).getModel().addChangeListener(changeListener);
      }
    }
    
    public void componentRemoved(ContainerEvent paramContainerEvent)
    {
      Component localComponent = paramContainerEvent.getChild();
      if ((localComponent instanceof JMenu)) {
        ((JMenu)localComponent).getModel().removeChangeListener(changeListener);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicMenuBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */