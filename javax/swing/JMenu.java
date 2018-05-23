package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuItemUI;
import javax.swing.plaf.PopupMenuUI;

public class JMenu
  extends JMenuItem
  implements Accessible, MenuElement
{
  private static final String uiClassID = "MenuUI";
  private JPopupMenu popupMenu;
  private ChangeListener menuChangeListener = null;
  private MenuEvent menuEvent = null;
  private int delay;
  private Point customMenuLocation = null;
  private static final boolean TRACE = false;
  private static final boolean VERBOSE = false;
  private static final boolean DEBUG = false;
  protected WinListener popupListener;
  
  public JMenu()
  {
    this("");
  }
  
  public JMenu(String paramString)
  {
    super(paramString);
  }
  
  public JMenu(Action paramAction)
  {
    this();
    setAction(paramAction);
  }
  
  public JMenu(String paramString, boolean paramBoolean)
  {
    this(paramString);
  }
  
  void initFocusability() {}
  
  public void updateUI()
  {
    setUI((MenuItemUI)UIManager.getUI(this));
    if (popupMenu != null) {
      popupMenu.setUI((PopupMenuUI)UIManager.getUI(popupMenu));
    }
  }
  
  public String getUIClassID()
  {
    return "MenuUI";
  }
  
  public void setModel(ButtonModel paramButtonModel)
  {
    ButtonModel localButtonModel = getModel();
    super.setModel(paramButtonModel);
    if ((localButtonModel != null) && (menuChangeListener != null))
    {
      localButtonModel.removeChangeListener(menuChangeListener);
      menuChangeListener = null;
    }
    model = paramButtonModel;
    if (paramButtonModel != null)
    {
      menuChangeListener = createMenuChangeListener();
      paramButtonModel.addChangeListener(menuChangeListener);
    }
  }
  
  public boolean isSelected()
  {
    return getModel().isSelected();
  }
  
  public void setSelected(boolean paramBoolean)
  {
    ButtonModel localButtonModel = getModel();
    boolean bool = localButtonModel.isSelected();
    if (paramBoolean != localButtonModel.isSelected()) {
      getModel().setSelected(paramBoolean);
    }
  }
  
  public boolean isPopupMenuVisible()
  {
    ensurePopupMenuCreated();
    return popupMenu.isVisible();
  }
  
  public void setPopupMenuVisible(boolean paramBoolean)
  {
    boolean bool = isPopupMenuVisible();
    if ((paramBoolean != bool) && ((isEnabled()) || (!paramBoolean)))
    {
      ensurePopupMenuCreated();
      if ((paramBoolean == true) && (isShowing()))
      {
        Point localPoint = getCustomMenuLocation();
        if (localPoint == null) {
          localPoint = getPopupMenuOrigin();
        }
        getPopupMenu().show(this, x, y);
      }
      else
      {
        getPopupMenu().setVisible(false);
      }
    }
  }
  
  protected Point getPopupMenuOrigin()
  {
    JPopupMenu localJPopupMenu = getPopupMenu();
    Dimension localDimension1 = getSize();
    Dimension localDimension2 = localJPopupMenu.getSize();
    if (width == 0) {
      localDimension2 = localJPopupMenu.getPreferredSize();
    }
    Point localPoint = getLocationOnScreen();
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    Object localObject1 = getGraphicsConfiguration();
    Rectangle localRectangle = new Rectangle(localToolkit.getScreenSize());
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] arrayOfGraphicsDevice = localGraphicsEnvironment.getScreenDevices();
    for (int k = 0; k < arrayOfGraphicsDevice.length; k++) {
      if (arrayOfGraphicsDevice[k].getType() == 0)
      {
        GraphicsConfiguration localGraphicsConfiguration = arrayOfGraphicsDevice[k].getDefaultConfiguration();
        if (localGraphicsConfiguration.getBounds().contains(localPoint))
        {
          localObject1 = localGraphicsConfiguration;
          break;
        }
      }
    }
    if (localObject1 != null)
    {
      localRectangle = ((GraphicsConfiguration)localObject1).getBounds();
      localObject2 = localToolkit.getScreenInsets((GraphicsConfiguration)localObject1);
      width -= Math.abs(left + right);
      height -= Math.abs(top + bottom);
      x -= Math.abs(left);
      y -= Math.abs(top);
    }
    Object localObject2 = getParent();
    int m;
    int n;
    int i;
    int j;
    if ((localObject2 instanceof JPopupMenu))
    {
      m = UIManager.getInt("Menu.submenuPopupOffsetX");
      n = UIManager.getInt("Menu.submenuPopupOffsetY");
      if (SwingUtilities.isLeftToRight(this))
      {
        i = width + m;
        if ((x + i + width >= width + x) && (width - width < 2 * (x - x))) {
          i = 0 - m - width;
        }
      }
      else
      {
        i = 0 - m - width;
        if ((x + i < x) && (width - width > 2 * (x - x))) {
          i = width + m;
        }
      }
      j = n;
      if ((y + j + height >= height + y) && (height - height < 2 * (y - y))) {
        j = height - n - height;
      }
    }
    else
    {
      m = UIManager.getInt("Menu.menuPopupOffsetX");
      n = UIManager.getInt("Menu.menuPopupOffsetY");
      if (SwingUtilities.isLeftToRight(this))
      {
        i = m;
        if ((x + i + width >= width + x) && (width - width < 2 * (x - x))) {
          i = width - m - width;
        }
      }
      else
      {
        i = width - m - width;
        if ((x + i < x) && (width - width > 2 * (x - x))) {
          i = m;
        }
      }
      j = height + n;
      if ((y + j + height >= height + y) && (height - height < 2 * (y - y))) {
        j = 0 - n - height;
      }
    }
    return new Point(i, j);
  }
  
  public int getDelay()
  {
    return delay;
  }
  
  public void setDelay(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Delay must be a positive integer");
    }
    delay = paramInt;
  }
  
  private void ensurePopupMenuCreated()
  {
    if (popupMenu == null)
    {
      JMenu localJMenu = this;
      popupMenu = new JPopupMenu();
      popupMenu.setInvoker(this);
      popupListener = createWinListener(popupMenu);
    }
  }
  
  private Point getCustomMenuLocation()
  {
    return customMenuLocation;
  }
  
  public void setMenuLocation(int paramInt1, int paramInt2)
  {
    customMenuLocation = new Point(paramInt1, paramInt2);
    if (popupMenu != null) {
      popupMenu.setLocation(paramInt1, paramInt2);
    }
  }
  
  public JMenuItem add(JMenuItem paramJMenuItem)
  {
    ensurePopupMenuCreated();
    return popupMenu.add(paramJMenuItem);
  }
  
  public Component add(Component paramComponent)
  {
    ensurePopupMenuCreated();
    popupMenu.add(paramComponent);
    return paramComponent;
  }
  
  public Component add(Component paramComponent, int paramInt)
  {
    ensurePopupMenuCreated();
    popupMenu.add(paramComponent, paramInt);
    return paramComponent;
  }
  
  public JMenuItem add(String paramString)
  {
    return add(new JMenuItem(paramString));
  }
  
  public JMenuItem add(Action paramAction)
  {
    JMenuItem localJMenuItem = createActionComponent(paramAction);
    localJMenuItem.setAction(paramAction);
    add(localJMenuItem);
    return localJMenuItem;
  }
  
  protected JMenuItem createActionComponent(Action paramAction)
  {
    JMenuItem local1 = new JMenuItem()
    {
      protected PropertyChangeListener createActionPropertyChangeListener(Action paramAnonymousAction)
      {
        PropertyChangeListener localPropertyChangeListener = createActionChangeListener(this);
        if (localPropertyChangeListener == null) {
          localPropertyChangeListener = super.createActionPropertyChangeListener(paramAnonymousAction);
        }
        return localPropertyChangeListener;
      }
    };
    local1.setHorizontalTextPosition(11);
    local1.setVerticalTextPosition(0);
    return local1;
  }
  
  protected PropertyChangeListener createActionChangeListener(JMenuItem paramJMenuItem)
  {
    return paramJMenuItem.createActionPropertyChangeListener0(paramJMenuItem.getAction());
  }
  
  public void addSeparator()
  {
    ensurePopupMenuCreated();
    popupMenu.addSeparator();
  }
  
  public void insert(String paramString, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index less than zero.");
    }
    ensurePopupMenuCreated();
    popupMenu.insert(new JMenuItem(paramString), paramInt);
  }
  
  public JMenuItem insert(JMenuItem paramJMenuItem, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index less than zero.");
    }
    ensurePopupMenuCreated();
    popupMenu.insert(paramJMenuItem, paramInt);
    return paramJMenuItem;
  }
  
  public JMenuItem insert(Action paramAction, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index less than zero.");
    }
    ensurePopupMenuCreated();
    JMenuItem localJMenuItem = new JMenuItem(paramAction);
    localJMenuItem.setHorizontalTextPosition(11);
    localJMenuItem.setVerticalTextPosition(0);
    popupMenu.insert(localJMenuItem, paramInt);
    return localJMenuItem;
  }
  
  public void insertSeparator(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index less than zero.");
    }
    ensurePopupMenuCreated();
    popupMenu.insert(new JPopupMenu.Separator(), paramInt);
  }
  
  public JMenuItem getItem(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index less than zero.");
    }
    Component localComponent = getMenuComponent(paramInt);
    if ((localComponent instanceof JMenuItem))
    {
      JMenuItem localJMenuItem = (JMenuItem)localComponent;
      return localJMenuItem;
    }
    return null;
  }
  
  public int getItemCount()
  {
    return getMenuComponentCount();
  }
  
  public boolean isTearOff()
  {
    throw new Error("boolean isTearOff() {} not yet implemented");
  }
  
  public void remove(JMenuItem paramJMenuItem)
  {
    if (popupMenu != null) {
      popupMenu.remove(paramJMenuItem);
    }
  }
  
  public void remove(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index less than zero.");
    }
    if (paramInt > getItemCount()) {
      throw new IllegalArgumentException("index greater than the number of items.");
    }
    if (popupMenu != null) {
      popupMenu.remove(paramInt);
    }
  }
  
  public void remove(Component paramComponent)
  {
    if (popupMenu != null) {
      popupMenu.remove(paramComponent);
    }
  }
  
  public void removeAll()
  {
    if (popupMenu != null) {
      popupMenu.removeAll();
    }
  }
  
  public int getMenuComponentCount()
  {
    int i = 0;
    if (popupMenu != null) {
      i = popupMenu.getComponentCount();
    }
    return i;
  }
  
  public Component getMenuComponent(int paramInt)
  {
    if (popupMenu != null) {
      return popupMenu.getComponent(paramInt);
    }
    return null;
  }
  
  public Component[] getMenuComponents()
  {
    if (popupMenu != null) {
      return popupMenu.getComponents();
    }
    return new Component[0];
  }
  
  public boolean isTopLevelMenu()
  {
    return getParent() instanceof JMenuBar;
  }
  
  public boolean isMenuComponent(Component paramComponent)
  {
    if (paramComponent == this) {
      return true;
    }
    if ((paramComponent instanceof JPopupMenu))
    {
      JPopupMenu localJPopupMenu = (JPopupMenu)paramComponent;
      if (localJPopupMenu == getPopupMenu()) {
        return true;
      }
    }
    int i = getMenuComponentCount();
    Component[] arrayOfComponent = getMenuComponents();
    for (int j = 0; j < i; j++)
    {
      Component localComponent = arrayOfComponent[j];
      if (localComponent == paramComponent) {
        return true;
      }
      if ((localComponent instanceof JMenu))
      {
        JMenu localJMenu = (JMenu)localComponent;
        if (localJMenu.isMenuComponent(paramComponent)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private Point translateToPopupMenu(Point paramPoint)
  {
    return translateToPopupMenu(x, y);
  }
  
  private Point translateToPopupMenu(int paramInt1, int paramInt2)
  {
    int i;
    int j;
    if ((getParent() instanceof JPopupMenu))
    {
      i = paramInt1 - getSizewidth;
      j = paramInt2;
    }
    else
    {
      i = paramInt1;
      j = paramInt2 - getSizeheight;
    }
    return new Point(i, j);
  }
  
  public JPopupMenu getPopupMenu()
  {
    ensurePopupMenuCreated();
    return popupMenu;
  }
  
  public void addMenuListener(MenuListener paramMenuListener)
  {
    listenerList.add(MenuListener.class, paramMenuListener);
  }
  
  public void removeMenuListener(MenuListener paramMenuListener)
  {
    listenerList.remove(MenuListener.class, paramMenuListener);
  }
  
  public MenuListener[] getMenuListeners()
  {
    return (MenuListener[])listenerList.getListeners(MenuListener.class);
  }
  
  protected void fireMenuSelected()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuListener.class)
      {
        if (arrayOfObject[(i + 1)] == null) {
          throw new Error(getText() + " has a NULL Listener!! " + i);
        }
        if (menuEvent == null) {
          menuEvent = new MenuEvent(this);
        }
        ((MenuListener)arrayOfObject[(i + 1)]).menuSelected(menuEvent);
      }
    }
  }
  
  protected void fireMenuDeselected()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuListener.class)
      {
        if (arrayOfObject[(i + 1)] == null) {
          throw new Error(getText() + " has a NULL Listener!! " + i);
        }
        if (menuEvent == null) {
          menuEvent = new MenuEvent(this);
        }
        ((MenuListener)arrayOfObject[(i + 1)]).menuDeselected(menuEvent);
      }
    }
  }
  
  protected void fireMenuCanceled()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuListener.class)
      {
        if (arrayOfObject[(i + 1)] == null) {
          throw new Error(getText() + " has a NULL Listener!! " + i);
        }
        if (menuEvent == null) {
          menuEvent = new MenuEvent(this);
        }
        ((MenuListener)arrayOfObject[(i + 1)]).menuCanceled(menuEvent);
      }
    }
  }
  
  void configureAcceleratorFromAction(Action paramAction) {}
  
  private ChangeListener createMenuChangeListener()
  {
    return new MenuChangeListener();
  }
  
  protected WinListener createWinListener(JPopupMenu paramJPopupMenu)
  {
    return new WinListener(paramJPopupMenu);
  }
  
  public void menuSelectionChanged(boolean paramBoolean)
  {
    setSelected(paramBoolean);
  }
  
  public MenuElement[] getSubElements()
  {
    if (popupMenu == null) {
      return new MenuElement[0];
    }
    MenuElement[] arrayOfMenuElement = new MenuElement[1];
    arrayOfMenuElement[0] = popupMenu;
    return arrayOfMenuElement;
  }
  
  public Component getComponent()
  {
    return this;
  }
  
  public void applyComponentOrientation(ComponentOrientation paramComponentOrientation)
  {
    super.applyComponentOrientation(paramComponentOrientation);
    if (popupMenu != null)
    {
      int i = getMenuComponentCount();
      for (int j = 0; j < i; j++) {
        getMenuComponent(j).applyComponentOrientation(paramComponentOrientation);
      }
      popupMenu.setComponentOrientation(paramComponentOrientation);
    }
  }
  
  public void setComponentOrientation(ComponentOrientation paramComponentOrientation)
  {
    super.setComponentOrientation(paramComponentOrientation);
    if (popupMenu != null) {
      popupMenu.setComponentOrientation(paramComponentOrientation);
    }
  }
  
  public void setAccelerator(KeyStroke paramKeyStroke)
  {
    throw new Error("setAccelerator() is not defined for JMenu.  Use setMnemonic() instead.");
  }
  
  protected void processKeyEvent(KeyEvent paramKeyEvent)
  {
    MenuSelectionManager.defaultManager().processKeyEvent(paramKeyEvent);
    if (paramKeyEvent.isConsumed()) {
      return;
    }
    super.processKeyEvent(paramKeyEvent);
  }
  
  public void doClick(int paramInt)
  {
    MenuElement[] arrayOfMenuElement = buildMenuElementArray(this);
    MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
  }
  
  private MenuElement[] buildMenuElementArray(JMenu paramJMenu)
  {
    Vector localVector = new Vector();
    Object localObject = paramJMenu.getPopupMenu();
    do
    {
      for (;;)
      {
        if ((localObject instanceof JPopupMenu))
        {
          JPopupMenu localJPopupMenu = (JPopupMenu)localObject;
          localVector.insertElementAt(localJPopupMenu, 0);
          localObject = localJPopupMenu.getInvoker();
        }
        else
        {
          if (!(localObject instanceof JMenu)) {
            break;
          }
          JMenu localJMenu = (JMenu)localObject;
          localVector.insertElementAt(localJMenu, 0);
          localObject = localJMenu.getParent();
        }
      }
    } while (!(localObject instanceof JMenuBar));
    JMenuBar localJMenuBar = (JMenuBar)localObject;
    localVector.insertElementAt(localJMenuBar, 0);
    MenuElement[] arrayOfMenuElement = new MenuElement[localVector.size()];
    localVector.copyInto(arrayOfMenuElement);
    return arrayOfMenuElement;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("MenuUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    return super.paramString();
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJMenu();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJMenu
    extends JMenuItem.AccessibleJMenuItem
    implements AccessibleSelection
  {
    protected AccessibleJMenu()
    {
      super();
    }
    
    public int getAccessibleChildrenCount()
    {
      Component[] arrayOfComponent1 = getMenuComponents();
      int i = 0;
      for (Component localComponent : arrayOfComponent1) {
        if ((localComponent instanceof Accessible)) {
          i++;
        }
      }
      return i;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      Component[] arrayOfComponent1 = getMenuComponents();
      int i = 0;
      for (Component localComponent : arrayOfComponent1) {
        if ((localComponent instanceof Accessible))
        {
          if (i == paramInt)
          {
            if ((localComponent instanceof JComponent))
            {
              AccessibleContext localAccessibleContext = localComponent.getAccessibleContext();
              localAccessibleContext.setAccessibleParent(JMenu.this);
            }
            return (Accessible)localComponent;
          }
          i++;
        }
      }
      return null;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.MENU;
    }
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public int getAccessibleSelectionCount()
    {
      MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement != null) {
        for (int i = 0; i < arrayOfMenuElement.length; i++) {
          if ((arrayOfMenuElement[i] == JMenu.this) && (i + 1 < arrayOfMenuElement.length)) {
            return 1;
          }
        }
      }
      return 0;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= getItemCount())) {
        return null;
      }
      MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement != null) {
        for (int i = 0; i < arrayOfMenuElement.length; i++) {
          if (arrayOfMenuElement[i] == JMenu.this)
          {
            do
            {
              i++;
              if (i >= arrayOfMenuElement.length) {
                break;
              }
            } while (!(arrayOfMenuElement[i] instanceof JMenuItem));
            return (Accessible)arrayOfMenuElement[i];
          }
        }
      }
      return null;
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement != null)
      {
        JMenuItem localJMenuItem = getItem(paramInt);
        for (int i = 0; i < arrayOfMenuElement.length; i++) {
          if (arrayOfMenuElement[i] == localJMenuItem) {
            return true;
          }
        }
      }
      return false;
    }
    
    public void addAccessibleSelection(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= getItemCount())) {
        return;
      }
      JMenuItem localJMenuItem = getItem(paramInt);
      if (localJMenuItem != null) {
        if ((localJMenuItem instanceof JMenu))
        {
          MenuElement[] arrayOfMenuElement = JMenu.this.buildMenuElementArray((JMenu)localJMenuItem);
          MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
        }
        else
        {
          MenuSelectionManager.defaultManager().setSelectedPath(null);
        }
      }
    }
    
    public void removeAccessibleSelection(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= getItemCount())) {
        return;
      }
      JMenuItem localJMenuItem = getItem(paramInt);
      if ((localJMenuItem != null) && ((localJMenuItem instanceof JMenu)) && (localJMenuItem.isSelected()))
      {
        MenuElement[] arrayOfMenuElement1 = MenuSelectionManager.defaultManager().getSelectedPath();
        MenuElement[] arrayOfMenuElement2 = new MenuElement[arrayOfMenuElement1.length - 2];
        for (int i = 0; i < arrayOfMenuElement1.length - 2; i++) {
          arrayOfMenuElement2[i] = arrayOfMenuElement1[i];
        }
        MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement2);
      }
    }
    
    public void clearAccessibleSelection()
    {
      MenuElement[] arrayOfMenuElement1 = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement1 != null) {
        for (int i = 0; i < arrayOfMenuElement1.length; i++) {
          if (arrayOfMenuElement1[i] == JMenu.this)
          {
            MenuElement[] arrayOfMenuElement2 = new MenuElement[i + 1];
            System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, i);
            arrayOfMenuElement2[i] = getPopupMenu();
            MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement2);
          }
        }
      }
    }
    
    public void selectAllAccessibleSelection() {}
  }
  
  class MenuChangeListener
    implements ChangeListener, Serializable
  {
    boolean isSelected = false;
    
    MenuChangeListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      ButtonModel localButtonModel = (ButtonModel)paramChangeEvent.getSource();
      boolean bool = localButtonModel.isSelected();
      if (bool != isSelected)
      {
        if (bool == true) {
          fireMenuSelected();
        } else {
          fireMenuDeselected();
        }
        isSelected = bool;
      }
    }
  }
  
  protected class WinListener
    extends WindowAdapter
    implements Serializable
  {
    JPopupMenu popupMenu;
    
    public WinListener(JPopupMenu paramJPopupMenu)
    {
      popupMenu = paramJPopupMenu;
    }
    
    public void windowClosing(WindowEvent paramWindowEvent)
    {
      setSelected(false);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */