package javax.swing.plaf.basic;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PopupMenuUI;
import javax.swing.plaf.UIResource;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.UngrabEvent;
import sun.swing.UIAction;

public class BasicPopupMenuUI
  extends PopupMenuUI
{
  static final StringBuilder MOUSE_GRABBER_KEY = new StringBuilder("javax.swing.plaf.basic.BasicPopupMenuUI.MouseGrabber");
  static final StringBuilder MENU_KEYBOARD_HELPER_KEY = new StringBuilder("javax.swing.plaf.basic.BasicPopupMenuUI.MenuKeyboardHelper");
  protected JPopupMenu popupMenu = null;
  private transient PopupMenuListener popupMenuListener = null;
  private MenuKeyListener menuKeyListener = null;
  private static boolean checkedUnpostPopup;
  private static boolean unpostPopup;
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicPopupMenuUI();
  }
  
  public BasicPopupMenuUI()
  {
    BasicLookAndFeel.needsEventHelper = true;
    LookAndFeel localLookAndFeel = UIManager.getLookAndFeel();
    if ((localLookAndFeel instanceof BasicLookAndFeel)) {
      ((BasicLookAndFeel)localLookAndFeel).installAWTEventListener();
    }
  }
  
  public void installUI(JComponent paramJComponent)
  {
    popupMenu = ((JPopupMenu)paramJComponent);
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  public void installDefaults()
  {
    if ((popupMenu.getLayout() == null) || ((popupMenu.getLayout() instanceof UIResource))) {
      popupMenu.setLayout(new DefaultMenuLayout(popupMenu, 1));
    }
    LookAndFeel.installProperty(popupMenu, "opaque", Boolean.TRUE);
    LookAndFeel.installBorder(popupMenu, "PopupMenu.border");
    LookAndFeel.installColorsAndFont(popupMenu, "PopupMenu.background", "PopupMenu.foreground", "PopupMenu.font");
  }
  
  protected void installListeners()
  {
    if (popupMenuListener == null) {
      popupMenuListener = new BasicPopupMenuListener(null);
    }
    popupMenu.addPopupMenuListener(popupMenuListener);
    if (menuKeyListener == null) {
      menuKeyListener = new BasicMenuKeyListener(null);
    }
    popupMenu.addMenuKeyListener(menuKeyListener);
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject1;
    synchronized (MOUSE_GRABBER_KEY)
    {
      localObject1 = (MouseGrabber)localAppContext.get(MOUSE_GRABBER_KEY);
      if (localObject1 == null)
      {
        localObject1 = new MouseGrabber();
        localAppContext.put(MOUSE_GRABBER_KEY, localObject1);
      }
    }
    synchronized (MENU_KEYBOARD_HELPER_KEY)
    {
      localObject1 = (MenuKeyboardHelper)localAppContext.get(MENU_KEYBOARD_HELPER_KEY);
      if (localObject1 == null)
      {
        localObject1 = new MenuKeyboardHelper();
        localAppContext.put(MENU_KEYBOARD_HELPER_KEY, localObject1);
        MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
        localMenuSelectionManager.addChangeListener((ChangeListener)localObject1);
      }
    }
  }
  
  protected void installKeyboardActions() {}
  
  static InputMap getInputMap(JPopupMenu paramJPopupMenu, JComponent paramJComponent)
  {
    Object localObject = null;
    Object[] arrayOfObject1 = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings");
    if (arrayOfObject1 != null)
    {
      localObject = LookAndFeel.makeComponentInputMap(paramJComponent, arrayOfObject1);
      if (!paramJPopupMenu.getComponentOrientation().isLeftToRight())
      {
        Object[] arrayOfObject2 = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings.RightToLeft");
        if (arrayOfObject2 != null)
        {
          ComponentInputMap localComponentInputMap = LookAndFeel.makeComponentInputMap(paramJComponent, arrayOfObject2);
          localComponentInputMap.setParent((InputMap)localObject);
          localObject = localComponentInputMap;
        }
      }
    }
    return (InputMap)localObject;
  }
  
  static ActionMap getActionMap()
  {
    return LazyActionMap.getActionMap(BasicPopupMenuUI.class, "PopupMenu.actionMap");
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("cancel"));
    paramLazyActionMap.put(new Actions("selectNext"));
    paramLazyActionMap.put(new Actions("selectPrevious"));
    paramLazyActionMap.put(new Actions("selectParent"));
    paramLazyActionMap.put(new Actions("selectChild"));
    paramLazyActionMap.put(new Actions("return"));
    BasicLookAndFeel.installAudioActionMap(paramLazyActionMap);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    popupMenu = null;
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(popupMenu);
  }
  
  protected void uninstallListeners()
  {
    if (popupMenuListener != null) {
      popupMenu.removePopupMenuListener(popupMenuListener);
    }
    if (menuKeyListener != null) {
      popupMenu.removeMenuKeyListener(menuKeyListener);
    }
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIActionMap(popupMenu, null);
    SwingUtilities.replaceUIInputMap(popupMenu, 2, null);
  }
  
  static MenuElement getFirstPopup()
  {
    MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement = localMenuSelectionManager.getSelectedPath();
    MenuElement localMenuElement = null;
    for (int i = 0; (localMenuElement == null) && (i < arrayOfMenuElement.length); i++) {
      if ((arrayOfMenuElement[i] instanceof JPopupMenu)) {
        localMenuElement = arrayOfMenuElement[i];
      }
    }
    return localMenuElement;
  }
  
  static JPopupMenu getLastPopup()
  {
    MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement = localMenuSelectionManager.getSelectedPath();
    JPopupMenu localJPopupMenu = null;
    for (int i = arrayOfMenuElement.length - 1; (localJPopupMenu == null) && (i >= 0); i--) {
      if ((arrayOfMenuElement[i] instanceof JPopupMenu)) {
        localJPopupMenu = (JPopupMenu)arrayOfMenuElement[i];
      }
    }
    return localJPopupMenu;
  }
  
  static List<JPopupMenu> getPopups()
  {
    MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement1 = localMenuSelectionManager.getSelectedPath();
    ArrayList localArrayList = new ArrayList(arrayOfMenuElement1.length);
    for (MenuElement localMenuElement : arrayOfMenuElement1) {
      if ((localMenuElement instanceof JPopupMenu)) {
        localArrayList.add((JPopupMenu)localMenuElement);
      }
    }
    return localArrayList;
  }
  
  public boolean isPopupTrigger(MouseEvent paramMouseEvent)
  {
    return (paramMouseEvent.getID() == 502) && ((paramMouseEvent.getModifiers() & 0x4) != 0);
  }
  
  private static boolean checkInvokerEqual(MenuElement paramMenuElement1, MenuElement paramMenuElement2)
  {
    Component localComponent1 = paramMenuElement1.getComponent();
    Component localComponent2 = paramMenuElement2.getComponent();
    if ((localComponent1 instanceof JPopupMenu)) {
      localComponent1 = ((JPopupMenu)localComponent1).getInvoker();
    }
    if ((localComponent2 instanceof JPopupMenu)) {
      localComponent2 = ((JPopupMenu)localComponent2).getInvoker();
    }
    return localComponent1 == localComponent2;
  }
  
  private static MenuElement nextEnabledChild(MenuElement[] paramArrayOfMenuElement, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i <= paramInt2; i++) {
      if (paramArrayOfMenuElement[i] != null)
      {
        Component localComponent = paramArrayOfMenuElement[i].getComponent();
        if ((localComponent != null) && ((localComponent.isEnabled()) || (UIManager.getBoolean("MenuItem.disabledAreNavigable"))) && (localComponent.isVisible())) {
          return paramArrayOfMenuElement[i];
        }
      }
    }
    return null;
  }
  
  private static MenuElement previousEnabledChild(MenuElement[] paramArrayOfMenuElement, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i >= paramInt2; i--) {
      if (paramArrayOfMenuElement[i] != null)
      {
        Component localComponent = paramArrayOfMenuElement[i].getComponent();
        if ((localComponent != null) && ((localComponent.isEnabled()) || (UIManager.getBoolean("MenuItem.disabledAreNavigable"))) && (localComponent.isVisible())) {
          return paramArrayOfMenuElement[i];
        }
      }
    }
    return null;
  }
  
  static MenuElement findEnabledChild(MenuElement[] paramArrayOfMenuElement, int paramInt, boolean paramBoolean)
  {
    MenuElement localMenuElement;
    if (paramBoolean)
    {
      localMenuElement = nextEnabledChild(paramArrayOfMenuElement, paramInt + 1, paramArrayOfMenuElement.length - 1);
      if (localMenuElement == null) {
        localMenuElement = nextEnabledChild(paramArrayOfMenuElement, 0, paramInt - 1);
      }
    }
    else
    {
      localMenuElement = previousEnabledChild(paramArrayOfMenuElement, paramInt - 1, 0);
      if (localMenuElement == null) {
        localMenuElement = previousEnabledChild(paramArrayOfMenuElement, paramArrayOfMenuElement.length - 1, paramInt + 1);
      }
    }
    return localMenuElement;
  }
  
  static MenuElement findEnabledChild(MenuElement[] paramArrayOfMenuElement, MenuElement paramMenuElement, boolean paramBoolean)
  {
    for (int i = 0; i < paramArrayOfMenuElement.length; i++) {
      if (paramArrayOfMenuElement[i] == paramMenuElement) {
        return findEnabledChild(paramArrayOfMenuElement, i, paramBoolean);
      }
    }
    return null;
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String CANCEL = "cancel";
    private static final String SELECT_NEXT = "selectNext";
    private static final String SELECT_PREVIOUS = "selectPrevious";
    private static final String SELECT_PARENT = "selectParent";
    private static final String SELECT_CHILD = "selectChild";
    private static final String RETURN = "return";
    private static final boolean FORWARD = true;
    private static final boolean BACKWARD = false;
    private static final boolean PARENT = false;
    private static final boolean CHILD = true;
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      String str = getName();
      if (str == "cancel") {
        cancel();
      } else if (str == "selectNext") {
        selectItem(true);
      } else if (str == "selectPrevious") {
        selectItem(false);
      } else if (str == "selectParent") {
        selectParentChild(false);
      } else if (str == "selectChild") {
        selectParentChild(true);
      } else if (str == "return") {
        doReturn();
      }
    }
    
    private void doReturn()
    {
      KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      Component localComponent = localKeyboardFocusManager.getFocusOwner();
      if ((localComponent != null) && (!(localComponent instanceof JRootPane))) {
        return;
      }
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement = localMenuSelectionManager.getSelectedPath();
      if (arrayOfMenuElement.length > 0)
      {
        MenuElement localMenuElement = arrayOfMenuElement[(arrayOfMenuElement.length - 1)];
        Object localObject;
        if ((localMenuElement instanceof JMenu))
        {
          localObject = new MenuElement[arrayOfMenuElement.length + 1];
          System.arraycopy(arrayOfMenuElement, 0, localObject, 0, arrayOfMenuElement.length);
          localObject[arrayOfMenuElement.length] = ((JMenu)localMenuElement).getPopupMenu();
          localMenuSelectionManager.setSelectedPath((MenuElement[])localObject);
        }
        else if ((localMenuElement instanceof JMenuItem))
        {
          localObject = (JMenuItem)localMenuElement;
          if ((((JMenuItem)localObject).getUI() instanceof BasicMenuItemUI))
          {
            ((BasicMenuItemUI)((JMenuItem)localObject).getUI()).doClick(localMenuSelectionManager);
          }
          else
          {
            localMenuSelectionManager.clearSelectedPath();
            ((JMenuItem)localObject).doClick(0);
          }
        }
      }
    }
    
    private void selectParentChild(boolean paramBoolean)
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement1 = localMenuSelectionManager.getSelectedPath();
      int i = arrayOfMenuElement1.length;
      Object localObject2;
      Object localObject1;
      MenuElement[] arrayOfMenuElement2;
      if (!paramBoolean)
      {
        int j = i - 1;
        if ((i > 2) && (((arrayOfMenuElement1[j] instanceof JPopupMenu)) || ((arrayOfMenuElement1[(--j)] instanceof JPopupMenu))) && (!((JMenu)arrayOfMenuElement1[(j - 1)]).isTopLevelMenu()))
        {
          localObject2 = new MenuElement[j];
          System.arraycopy(arrayOfMenuElement1, 0, localObject2, 0, j);
          localMenuSelectionManager.setSelectedPath((MenuElement[])localObject2);
          return;
        }
      }
      else if ((i > 0) && ((arrayOfMenuElement1[(i - 1)] instanceof JMenu)) && (!((JMenu)arrayOfMenuElement1[(i - 1)]).isTopLevelMenu()))
      {
        localObject1 = (JMenu)arrayOfMenuElement1[(i - 1)];
        localObject2 = ((JMenu)localObject1).getPopupMenu();
        arrayOfMenuElement2 = ((JPopupMenu)localObject2).getSubElements();
        MenuElement localMenuElement = BasicPopupMenuUI.findEnabledChild(arrayOfMenuElement2, -1, true);
        MenuElement[] arrayOfMenuElement3;
        if (localMenuElement == null)
        {
          arrayOfMenuElement3 = new MenuElement[i + 1];
        }
        else
        {
          arrayOfMenuElement3 = new MenuElement[i + 2];
          arrayOfMenuElement3[(i + 1)] = localMenuElement;
        }
        System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement3, 0, i);
        arrayOfMenuElement3[i] = localObject2;
        localMenuSelectionManager.setSelectedPath(arrayOfMenuElement3);
        return;
      }
      if ((i > 1) && ((arrayOfMenuElement1[0] instanceof JMenuBar)))
      {
        localObject1 = arrayOfMenuElement1[1];
        localObject2 = BasicPopupMenuUI.findEnabledChild(arrayOfMenuElement1[0].getSubElements(), (MenuElement)localObject1, paramBoolean);
        if ((localObject2 != null) && (localObject2 != localObject1))
        {
          if (i == 2)
          {
            arrayOfMenuElement2 = new MenuElement[2];
            arrayOfMenuElement2[0] = arrayOfMenuElement1[0];
            arrayOfMenuElement2[1] = localObject2;
          }
          else
          {
            arrayOfMenuElement2 = new MenuElement[3];
            arrayOfMenuElement2[0] = arrayOfMenuElement1[0];
            arrayOfMenuElement2[1] = localObject2;
            arrayOfMenuElement2[2] = ((JMenu)localObject2).getPopupMenu();
          }
          localMenuSelectionManager.setSelectedPath(arrayOfMenuElement2);
        }
      }
    }
    
    private void selectItem(boolean paramBoolean)
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement1 = localMenuSelectionManager.getSelectedPath();
      if (arrayOfMenuElement1.length == 0) {
        return;
      }
      int i = arrayOfMenuElement1.length;
      Object localObject1;
      Object localObject2;
      if ((i == 1) && ((arrayOfMenuElement1[0] instanceof JPopupMenu)))
      {
        localObject1 = (JPopupMenu)arrayOfMenuElement1[0];
        localObject2 = new MenuElement[2];
        localObject2[0] = localObject1;
        localObject2[1] = BasicPopupMenuUI.findEnabledChild(((JPopupMenu)localObject1).getSubElements(), -1, paramBoolean);
        localMenuSelectionManager.setSelectedPath((MenuElement[])localObject2);
      }
      else
      {
        Object localObject3;
        if ((i == 2) && ((arrayOfMenuElement1[0] instanceof JMenuBar)) && ((arrayOfMenuElement1[1] instanceof JMenu)))
        {
          localObject1 = ((JMenu)arrayOfMenuElement1[1]).getPopupMenu();
          localObject2 = BasicPopupMenuUI.findEnabledChild(((JPopupMenu)localObject1).getSubElements(), -1, true);
          if (localObject2 != null)
          {
            localObject3 = new MenuElement[4];
            localObject3[3] = localObject2;
          }
          else
          {
            localObject3 = new MenuElement[3];
          }
          System.arraycopy(arrayOfMenuElement1, 0, localObject3, 0, 2);
          localObject3[2] = localObject1;
          localMenuSelectionManager.setSelectedPath((MenuElement[])localObject3);
        }
        else if (((arrayOfMenuElement1[(i - 1)] instanceof JPopupMenu)) && ((arrayOfMenuElement1[(i - 2)] instanceof JMenu)))
        {
          localObject1 = (JMenu)arrayOfMenuElement1[(i - 2)];
          localObject2 = ((JMenu)localObject1).getPopupMenu();
          localObject3 = BasicPopupMenuUI.findEnabledChild(((JPopupMenu)localObject2).getSubElements(), -1, paramBoolean);
          MenuElement[] arrayOfMenuElement2;
          if (localObject3 != null)
          {
            arrayOfMenuElement2 = new MenuElement[i + 1];
            System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, i);
            arrayOfMenuElement2[i] = localObject3;
            localMenuSelectionManager.setSelectedPath(arrayOfMenuElement2);
          }
          else if ((i > 2) && ((arrayOfMenuElement1[(i - 3)] instanceof JPopupMenu)))
          {
            localObject2 = (JPopupMenu)arrayOfMenuElement1[(i - 3)];
            localObject3 = BasicPopupMenuUI.findEnabledChild(((JPopupMenu)localObject2).getSubElements(), (MenuElement)localObject1, paramBoolean);
            if ((localObject3 != null) && (localObject3 != localObject1))
            {
              arrayOfMenuElement2 = new MenuElement[i - 1];
              System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, i - 2);
              arrayOfMenuElement2[(i - 2)] = localObject3;
              localMenuSelectionManager.setSelectedPath(arrayOfMenuElement2);
            }
          }
        }
        else
        {
          localObject1 = arrayOfMenuElement1[(i - 2)].getSubElements();
          localObject2 = BasicPopupMenuUI.findEnabledChild((MenuElement[])localObject1, arrayOfMenuElement1[(i - 1)], paramBoolean);
          if (localObject2 == null) {
            localObject2 = BasicPopupMenuUI.findEnabledChild((MenuElement[])localObject1, -1, paramBoolean);
          }
          if (localObject2 != null)
          {
            arrayOfMenuElement1[(i - 1)] = localObject2;
            localMenuSelectionManager.setSelectedPath(arrayOfMenuElement1);
          }
        }
      }
    }
    
    private void cancel()
    {
      JPopupMenu localJPopupMenu = BasicPopupMenuUI.getLastPopup();
      if (localJPopupMenu != null) {
        localJPopupMenu.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE);
      }
      String str = UIManager.getString("Menu.cancelMode");
      if ("hideMenuTree".equals(str)) {
        MenuSelectionManager.defaultManager().clearSelectedPath();
      } else {
        shortenSelectedPath();
      }
    }
    
    private void shortenSelectedPath()
    {
      MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement.length <= 2)
      {
        MenuSelectionManager.defaultManager().clearSelectedPath();
        return;
      }
      int i = 2;
      MenuElement localMenuElement = arrayOfMenuElement[(arrayOfMenuElement.length - 1)];
      JPopupMenu localJPopupMenu = BasicPopupMenuUI.getLastPopup();
      if (localMenuElement == localJPopupMenu)
      {
        localObject = arrayOfMenuElement[(arrayOfMenuElement.length - 2)];
        if ((localObject instanceof JMenu))
        {
          JMenu localJMenu = (JMenu)localObject;
          if ((localJMenu.isEnabled()) && (localJPopupMenu.getComponentCount() > 0)) {
            i = 1;
          } else {
            i = 3;
          }
        }
      }
      if ((arrayOfMenuElement.length - i <= 2) && (!UIManager.getBoolean("Menu.preserveTopLevelSelection"))) {
        i = arrayOfMenuElement.length;
      }
      Object localObject = new MenuElement[arrayOfMenuElement.length - i];
      System.arraycopy(arrayOfMenuElement, 0, localObject, 0, arrayOfMenuElement.length - i);
      MenuSelectionManager.defaultManager().setSelectedPath((MenuElement[])localObject);
    }
  }
  
  private class BasicMenuKeyListener
    implements MenuKeyListener
  {
    MenuElement menuToOpen = null;
    
    private BasicMenuKeyListener() {}
    
    public void menuKeyTyped(MenuKeyEvent paramMenuKeyEvent)
    {
      if (menuToOpen != null)
      {
        JPopupMenu localJPopupMenu = ((JMenu)menuToOpen).getPopupMenu();
        MenuElement localMenuElement = BasicPopupMenuUI.findEnabledChild(localJPopupMenu.getSubElements(), -1, true);
        ArrayList localArrayList = new ArrayList(Arrays.asList(paramMenuKeyEvent.getPath()));
        localArrayList.add(menuToOpen);
        localArrayList.add(localJPopupMenu);
        if (localMenuElement != null) {
          localArrayList.add(localMenuElement);
        }
        MenuElement[] arrayOfMenuElement = new MenuElement[0];
        arrayOfMenuElement = (MenuElement[])localArrayList.toArray(arrayOfMenuElement);
        MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
        paramMenuKeyEvent.consume();
      }
      menuToOpen = null;
    }
    
    public void menuKeyPressed(MenuKeyEvent paramMenuKeyEvent)
    {
      char c = paramMenuKeyEvent.getKeyChar();
      if (!Character.isLetterOrDigit(c)) {
        return;
      }
      MenuSelectionManager localMenuSelectionManager = paramMenuKeyEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement1 = paramMenuKeyEvent.getPath();
      MenuElement[] arrayOfMenuElement2 = popupMenu.getSubElements();
      int i = -1;
      int j = 0;
      int k = -1;
      int[] arrayOfInt = null;
      Object localObject2;
      for (int m = 0; m < arrayOfMenuElement2.length; m++) {
        if ((arrayOfMenuElement2[m] instanceof JMenuItem))
        {
          localObject2 = (JMenuItem)arrayOfMenuElement2[m];
          int n = ((JMenuItem)localObject2).getMnemonic();
          if ((((JMenuItem)localObject2).isEnabled()) && (((JMenuItem)localObject2).isVisible()) && (lower(c) == lower(n))) {
            if (j == 0)
            {
              k = m;
              j++;
            }
            else
            {
              if (arrayOfInt == null)
              {
                arrayOfInt = new int[arrayOfMenuElement2.length];
                arrayOfInt[0] = k;
              }
              arrayOfInt[(j++)] = m;
            }
          }
          if ((((JMenuItem)localObject2).isArmed()) || (((JMenuItem)localObject2).isSelected())) {
            i = j - 1;
          }
        }
      }
      if (j != 0)
      {
        Object localObject1;
        if (j == 1)
        {
          localObject1 = (JMenuItem)arrayOfMenuElement2[k];
          if ((localObject1 instanceof JMenu))
          {
            menuToOpen = ((MenuElement)localObject1);
          }
          else if (((JMenuItem)localObject1).isEnabled())
          {
            localMenuSelectionManager.clearSelectedPath();
            ((JMenuItem)localObject1).doClick();
          }
          paramMenuKeyEvent.consume();
        }
        else
        {
          localObject1 = arrayOfMenuElement2[arrayOfInt[((i + 1) % j)]];
          localObject2 = new MenuElement[arrayOfMenuElement1.length + 1];
          System.arraycopy(arrayOfMenuElement1, 0, localObject2, 0, arrayOfMenuElement1.length);
          localObject2[arrayOfMenuElement1.length] = localObject1;
          localMenuSelectionManager.setSelectedPath((MenuElement[])localObject2);
          paramMenuKeyEvent.consume();
        }
      }
    }
    
    public void menuKeyReleased(MenuKeyEvent paramMenuKeyEvent) {}
    
    private char lower(char paramChar)
    {
      return Character.toLowerCase(paramChar);
    }
    
    private char lower(int paramInt)
    {
      return Character.toLowerCase((char)paramInt);
    }
  }
  
  private class BasicPopupMenuListener
    implements PopupMenuListener
  {
    private BasicPopupMenuListener() {}
    
    public void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent) {}
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent) {}
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent paramPopupMenuEvent)
    {
      BasicLookAndFeel.playSound((JPopupMenu)paramPopupMenuEvent.getSource(), "PopupMenu.popupSound");
    }
  }
  
  static class MenuKeyboardHelper
    implements ChangeListener, KeyListener
  {
    private Component lastFocused = null;
    private MenuElement[] lastPathSelected = new MenuElement[0];
    private JPopupMenu lastPopup;
    private JRootPane invokerRootPane;
    private ActionMap menuActionMap = BasicPopupMenuUI.getActionMap();
    private InputMap menuInputMap;
    private boolean focusTraversalKeysEnabled;
    private boolean receivedKeyPressed = false;
    private FocusListener rootPaneFocusListener = new FocusAdapter()
    {
      public void focusGained(FocusEvent paramAnonymousFocusEvent)
      {
        Component localComponent = paramAnonymousFocusEvent.getOppositeComponent();
        if (localComponent != null) {
          lastFocused = localComponent;
        }
        paramAnonymousFocusEvent.getComponent().removeFocusListener(this);
      }
    };
    
    MenuKeyboardHelper() {}
    
    void removeItems()
    {
      if (lastFocused != null)
      {
        if (!lastFocused.requestFocusInWindow())
        {
          Window localWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
          if ((localWindow != null) && ("###focusableSwingPopup###".equals(localWindow.getName()))) {
            lastFocused.requestFocus();
          }
        }
        lastFocused = null;
      }
      if (invokerRootPane != null)
      {
        invokerRootPane.removeKeyListener(this);
        invokerRootPane.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
        removeUIInputMap(invokerRootPane, menuInputMap);
        removeUIActionMap(invokerRootPane, menuActionMap);
        invokerRootPane = null;
      }
      receivedKeyPressed = false;
    }
    
    JPopupMenu getActivePopup(MenuElement[] paramArrayOfMenuElement)
    {
      for (int i = paramArrayOfMenuElement.length - 1; i >= 0; i--)
      {
        MenuElement localMenuElement = paramArrayOfMenuElement[i];
        if ((localMenuElement instanceof JPopupMenu)) {
          return (JPopupMenu)localMenuElement;
        }
      }
      return null;
    }
    
    void addUIInputMap(JComponent paramJComponent, InputMap paramInputMap)
    {
      Object localObject = null;
      for (InputMap localInputMap = paramJComponent.getInputMap(2); (localInputMap != null) && (!(localInputMap instanceof UIResource)); localInputMap = localInputMap.getParent()) {
        localObject = localInputMap;
      }
      if (localObject == null) {
        paramJComponent.setInputMap(2, paramInputMap);
      } else {
        ((InputMap)localObject).setParent(paramInputMap);
      }
      paramInputMap.setParent(localInputMap);
    }
    
    void addUIActionMap(JComponent paramJComponent, ActionMap paramActionMap)
    {
      Object localObject = null;
      for (ActionMap localActionMap = paramJComponent.getActionMap(); (localActionMap != null) && (!(localActionMap instanceof UIResource)); localActionMap = localActionMap.getParent()) {
        localObject = localActionMap;
      }
      if (localObject == null) {
        paramJComponent.setActionMap(paramActionMap);
      } else {
        ((ActionMap)localObject).setParent(paramActionMap);
      }
      paramActionMap.setParent(localActionMap);
    }
    
    void removeUIInputMap(JComponent paramJComponent, InputMap paramInputMap)
    {
      Object localObject = null;
      for (InputMap localInputMap = paramJComponent.getInputMap(2); localInputMap != null; localInputMap = localInputMap.getParent())
      {
        if (localInputMap == paramInputMap)
        {
          if (localObject == null)
          {
            paramJComponent.setInputMap(2, paramInputMap.getParent());
            break;
          }
          ((InputMap)localObject).setParent(paramInputMap.getParent());
          break;
        }
        localObject = localInputMap;
      }
    }
    
    void removeUIActionMap(JComponent paramJComponent, ActionMap paramActionMap)
    {
      Object localObject = null;
      for (ActionMap localActionMap = paramJComponent.getActionMap(); localActionMap != null; localActionMap = localActionMap.getParent())
      {
        if (localActionMap == paramActionMap)
        {
          if (localObject == null)
          {
            paramJComponent.setActionMap(paramActionMap.getParent());
            break;
          }
          ((ActionMap)localObject).setParent(paramActionMap.getParent());
          break;
        }
        localObject = localActionMap;
      }
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (!(UIManager.getLookAndFeel() instanceof BasicLookAndFeel))
      {
        uninstall();
        return;
      }
      MenuSelectionManager localMenuSelectionManager = (MenuSelectionManager)paramChangeEvent.getSource();
      MenuElement[] arrayOfMenuElement = localMenuSelectionManager.getSelectedPath();
      JPopupMenu localJPopupMenu = getActivePopup(arrayOfMenuElement);
      if ((localJPopupMenu != null) && (!localJPopupMenu.isFocusable())) {
        return;
      }
      if ((lastPathSelected.length != 0) && (arrayOfMenuElement.length != 0) && (!BasicPopupMenuUI.checkInvokerEqual(arrayOfMenuElement[0], lastPathSelected[0])))
      {
        removeItems();
        lastPathSelected = new MenuElement[0];
      }
      if ((lastPathSelected.length == 0) && (arrayOfMenuElement.length > 0))
      {
        Object localObject1;
        if (localJPopupMenu == null)
        {
          if ((arrayOfMenuElement.length == 2) && ((arrayOfMenuElement[0] instanceof JMenuBar)) && ((arrayOfMenuElement[1] instanceof JMenu)))
          {
            localObject1 = (JComponent)arrayOfMenuElement[1];
            localJPopupMenu = ((JMenu)localObject1).getPopupMenu();
          }
        }
        else
        {
          Object localObject2 = localJPopupMenu.getInvoker();
          if ((localObject2 instanceof JFrame))
          {
            localObject1 = ((JFrame)localObject2).getRootPane();
          }
          else if ((localObject2 instanceof JDialog))
          {
            localObject1 = ((JDialog)localObject2).getRootPane();
          }
          else if ((localObject2 instanceof JApplet))
          {
            localObject1 = ((JApplet)localObject2).getRootPane();
          }
          else
          {
            while (!(localObject2 instanceof JComponent))
            {
              if (localObject2 == null) {
                return;
              }
              localObject2 = ((Component)localObject2).getParent();
            }
            localObject1 = (JComponent)localObject2;
          }
        }
        lastFocused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        invokerRootPane = SwingUtilities.getRootPane((Component)localObject1);
        if (invokerRootPane != null)
        {
          invokerRootPane.addFocusListener(rootPaneFocusListener);
          invokerRootPane.requestFocus(true);
          invokerRootPane.addKeyListener(this);
          focusTraversalKeysEnabled = invokerRootPane.getFocusTraversalKeysEnabled();
          invokerRootPane.setFocusTraversalKeysEnabled(false);
          menuInputMap = BasicPopupMenuUI.getInputMap(localJPopupMenu, invokerRootPane);
          addUIInputMap(invokerRootPane, menuInputMap);
          addUIActionMap(invokerRootPane, menuActionMap);
        }
      }
      else if ((lastPathSelected.length != 0) && (arrayOfMenuElement.length == 0))
      {
        removeItems();
      }
      else if (localJPopupMenu != lastPopup)
      {
        receivedKeyPressed = false;
      }
      lastPathSelected = arrayOfMenuElement;
      lastPopup = localJPopupMenu;
    }
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      receivedKeyPressed = true;
      MenuSelectionManager.defaultManager().processKeyEvent(paramKeyEvent);
    }
    
    public void keyReleased(KeyEvent paramKeyEvent)
    {
      if (receivedKeyPressed)
      {
        receivedKeyPressed = false;
        MenuSelectionManager.defaultManager().processKeyEvent(paramKeyEvent);
      }
    }
    
    public void keyTyped(KeyEvent paramKeyEvent)
    {
      if (receivedKeyPressed) {
        MenuSelectionManager.defaultManager().processKeyEvent(paramKeyEvent);
      }
    }
    
    void uninstall()
    {
      synchronized (BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY)
      {
        MenuSelectionManager.defaultManager().removeChangeListener(this);
        AppContext.getAppContext().remove(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY);
      }
    }
  }
  
  static class MouseGrabber
    implements ChangeListener, AWTEventListener, ComponentListener, WindowListener
  {
    Window grabbedWindow;
    MenuElement[] lastPathSelected;
    
    public MouseGrabber()
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      localMenuSelectionManager.addChangeListener(this);
      lastPathSelected = localMenuSelectionManager.getSelectedPath();
      if (lastPathSelected.length != 0) {
        grabWindow(lastPathSelected);
      }
    }
    
    void uninstall()
    {
      synchronized (BasicPopupMenuUI.MOUSE_GRABBER_KEY)
      {
        MenuSelectionManager.defaultManager().removeChangeListener(this);
        ungrabWindow();
        AppContext.getAppContext().remove(BasicPopupMenuUI.MOUSE_GRABBER_KEY);
      }
    }
    
    void grabWindow(MenuElement[] paramArrayOfMenuElement)
    {
      final Toolkit localToolkit = Toolkit.getDefaultToolkit();
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          localToolkit.addAWTEventListener(BasicPopupMenuUI.MouseGrabber.this, -2147352464L);
          return null;
        }
      });
      Component localComponent = paramArrayOfMenuElement[0].getComponent();
      if ((localComponent instanceof JPopupMenu)) {
        localComponent = ((JPopupMenu)localComponent).getInvoker();
      }
      grabbedWindow = ((localComponent instanceof Window) ? (Window)localComponent : SwingUtilities.getWindowAncestor(localComponent));
      if (grabbedWindow != null) {
        if ((localToolkit instanceof SunToolkit))
        {
          ((SunToolkit)localToolkit).grab(grabbedWindow);
        }
        else
        {
          grabbedWindow.addComponentListener(this);
          grabbedWindow.addWindowListener(this);
        }
      }
    }
    
    void ungrabWindow()
    {
      final Toolkit localToolkit = Toolkit.getDefaultToolkit();
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          localToolkit.removeAWTEventListener(BasicPopupMenuUI.MouseGrabber.this);
          return null;
        }
      });
      realUngrabWindow();
    }
    
    void realUngrabWindow()
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      if (grabbedWindow != null)
      {
        if ((localToolkit instanceof SunToolkit))
        {
          ((SunToolkit)localToolkit).ungrab(grabbedWindow);
        }
        else
        {
          grabbedWindow.removeComponentListener(this);
          grabbedWindow.removeWindowListener(this);
        }
        grabbedWindow = null;
      }
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement = localMenuSelectionManager.getSelectedPath();
      if ((lastPathSelected.length == 0) && (arrayOfMenuElement.length != 0)) {
        grabWindow(arrayOfMenuElement);
      }
      if ((lastPathSelected.length != 0) && (arrayOfMenuElement.length == 0)) {
        ungrabWindow();
      }
      lastPathSelected = arrayOfMenuElement;
    }
    
    public void eventDispatched(AWTEvent paramAWTEvent)
    {
      if ((paramAWTEvent instanceof UngrabEvent))
      {
        cancelPopupMenu();
        return;
      }
      if (!(paramAWTEvent instanceof MouseEvent)) {
        return;
      }
      MouseEvent localMouseEvent = (MouseEvent)paramAWTEvent;
      Component localComponent = localMouseEvent.getComponent();
      switch (localMouseEvent.getID())
      {
      case 501: 
        if ((isInPopup(localComponent)) || (((localComponent instanceof JMenu)) && (((JMenu)localComponent).isSelected()))) {
          return;
        }
        if ((!(localComponent instanceof JComponent)) || (((JComponent)localComponent).getClientProperty("doNotCancelPopup") != BasicComboBoxUI.HIDE_POPUP_KEY))
        {
          cancelPopupMenu();
          boolean bool = UIManager.getBoolean("PopupMenu.consumeEventOnClose");
          if ((bool) && (!(localComponent instanceof MenuElement))) {
            localMouseEvent.consume();
          }
        }
        break;
      case 502: 
        if ((((localComponent instanceof MenuElement)) || (!isInPopup(localComponent))) && (((localComponent instanceof JMenu)) || (!(localComponent instanceof JMenuItem)))) {
          MenuSelectionManager.defaultManager().processMouseEvent(localMouseEvent);
        }
        break;
      case 506: 
        if (((localComponent instanceof MenuElement)) || (!isInPopup(localComponent))) {
          MenuSelectionManager.defaultManager().processMouseEvent(localMouseEvent);
        }
        break;
      case 507: 
        if ((isInPopup(localComponent)) || (((localComponent instanceof JComboBox)) && (((JComboBox)localComponent).isPopupVisible()))) {
          return;
        }
        cancelPopupMenu();
      }
    }
    
    boolean isInPopup(Component paramComponent)
    {
      for (Object localObject = paramComponent; (localObject != null) && (!(localObject instanceof Applet)) && (!(localObject instanceof Window)); localObject = ((Component)localObject).getParent()) {
        if ((localObject instanceof JPopupMenu)) {
          return true;
        }
      }
      return false;
    }
    
    void cancelPopupMenu()
    {
      try
      {
        List localList = BasicPopupMenuUI.getPopups();
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          JPopupMenu localJPopupMenu = (JPopupMenu)localIterator.next();
          localJPopupMenu.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE);
        }
        MenuSelectionManager.defaultManager().clearSelectedPath();
      }
      catch (RuntimeException localRuntimeException)
      {
        realUngrabWindow();
        throw localRuntimeException;
      }
      catch (Error localError)
      {
        realUngrabWindow();
        throw localError;
      }
    }
    
    public void componentResized(ComponentEvent paramComponentEvent)
    {
      cancelPopupMenu();
    }
    
    public void componentMoved(ComponentEvent paramComponentEvent)
    {
      cancelPopupMenu();
    }
    
    public void componentShown(ComponentEvent paramComponentEvent)
    {
      cancelPopupMenu();
    }
    
    public void componentHidden(ComponentEvent paramComponentEvent)
    {
      cancelPopupMenu();
    }
    
    public void windowClosing(WindowEvent paramWindowEvent)
    {
      cancelPopupMenu();
    }
    
    public void windowClosed(WindowEvent paramWindowEvent)
    {
      cancelPopupMenu();
    }
    
    public void windowIconified(WindowEvent paramWindowEvent)
    {
      cancelPopupMenu();
    }
    
    public void windowDeactivated(WindowEvent paramWindowEvent)
    {
      cancelPopupMenu();
    }
    
    public void windowOpened(WindowEvent paramWindowEvent) {}
    
    public void windowDeiconified(WindowEvent paramWindowEvent) {}
    
    public void windowActivated(WindowEvent paramWindowEvent) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicPopupMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */