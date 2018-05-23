package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.peer.MenuItemPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.MenuItemAccessor;

public class MenuItem
  extends MenuComponent
  implements Accessible
{
  boolean enabled = true;
  String label;
  String actionCommand;
  long eventMask;
  transient ActionListener actionListener;
  private MenuShortcut shortcut = null;
  private static final String base = "menuitem";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -21757335363267194L;
  private int menuItemSerializedDataVersion = 1;
  
  public MenuItem()
    throws HeadlessException
  {
    this("", null);
  }
  
  public MenuItem(String paramString)
    throws HeadlessException
  {
    this(paramString, null);
  }
  
  public MenuItem(String paramString, MenuShortcut paramMenuShortcut)
    throws HeadlessException
  {
    label = paramString;
    shortcut = paramMenuShortcut;
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 6
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 170	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 330	java/lang/StringBuilder:<init>	()V
    //   12: ldc 5
    //   14: invokevirtual 334	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 275	java/awt/MenuItem:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 275	java/awt/MenuItem:nameCounter	I
    //   26: invokevirtual 332	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 331	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: aload_1
    //   33: monitorexit
    //   34: areturn
    //   35: astore_2
    //   36: aload_1
    //   37: monitorexit
    //   38: aload_2
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	MenuItem
    //   3	34	1	Ljava/lang/Object;	Object
    //   35	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   5	34	35	finally
    //   35	38	35	finally
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if (peer == null) {
        peer = Toolkit.getDefaultToolkit().createMenuItem(this);
      }
    }
  }
  
  public String getLabel()
  {
    return label;
  }
  
  public synchronized void setLabel(String paramString)
  {
    label = paramString;
    MenuItemPeer localMenuItemPeer = (MenuItemPeer)peer;
    if (localMenuItemPeer != null) {
      localMenuItemPeer.setLabel(paramString);
    }
  }
  
  public boolean isEnabled()
  {
    return enabled;
  }
  
  public synchronized void setEnabled(boolean paramBoolean)
  {
    enable(paramBoolean);
  }
  
  @Deprecated
  public synchronized void enable()
  {
    enabled = true;
    MenuItemPeer localMenuItemPeer = (MenuItemPeer)peer;
    if (localMenuItemPeer != null) {
      localMenuItemPeer.setEnabled(true);
    }
  }
  
  @Deprecated
  public void enable(boolean paramBoolean)
  {
    if (paramBoolean) {
      enable();
    } else {
      disable();
    }
  }
  
  @Deprecated
  public synchronized void disable()
  {
    enabled = false;
    MenuItemPeer localMenuItemPeer = (MenuItemPeer)peer;
    if (localMenuItemPeer != null) {
      localMenuItemPeer.setEnabled(false);
    }
  }
  
  public MenuShortcut getShortcut()
  {
    return shortcut;
  }
  
  public void setShortcut(MenuShortcut paramMenuShortcut)
  {
    shortcut = paramMenuShortcut;
    MenuItemPeer localMenuItemPeer = (MenuItemPeer)peer;
    if (localMenuItemPeer != null) {
      localMenuItemPeer.setLabel(label);
    }
  }
  
  public void deleteShortcut()
  {
    shortcut = null;
    MenuItemPeer localMenuItemPeer = (MenuItemPeer)peer;
    if (localMenuItemPeer != null) {
      localMenuItemPeer.setLabel(label);
    }
  }
  
  void deleteShortcut(MenuShortcut paramMenuShortcut)
  {
    if (paramMenuShortcut.equals(shortcut))
    {
      shortcut = null;
      MenuItemPeer localMenuItemPeer = (MenuItemPeer)peer;
      if (localMenuItemPeer != null) {
        localMenuItemPeer.setLabel(label);
      }
    }
  }
  
  void doMenuEvent(long paramLong, int paramInt)
  {
    Toolkit.getEventQueue().postEvent(new ActionEvent(this, 1001, getActionCommand(), paramLong, paramInt));
  }
  
  private final boolean isItemEnabled()
  {
    if (!isEnabled()) {
      return false;
    }
    MenuContainer localMenuContainer = getParent_NoClientCode();
    do
    {
      if (!(localMenuContainer instanceof Menu)) {
        return true;
      }
      Menu localMenu = (Menu)localMenuContainer;
      if (!localMenu.isEnabled()) {
        return false;
      }
      localMenuContainer = localMenu.getParent_NoClientCode();
    } while (localMenuContainer != null);
    return true;
  }
  
  boolean handleShortcut(KeyEvent paramKeyEvent)
  {
    MenuShortcut localMenuShortcut1 = new MenuShortcut(paramKeyEvent.getKeyCode(), (paramKeyEvent.getModifiers() & 0x1) > 0);
    MenuShortcut localMenuShortcut2 = new MenuShortcut(paramKeyEvent.getExtendedKeyCode(), (paramKeyEvent.getModifiers() & 0x1) > 0);
    if (((localMenuShortcut1.equals(shortcut)) || (localMenuShortcut2.equals(shortcut))) && (isItemEnabled()))
    {
      if (paramKeyEvent.getID() == 401) {
        doMenuEvent(paramKeyEvent.getWhen(), paramKeyEvent.getModifiers());
      }
      return true;
    }
    return false;
  }
  
  MenuItem getShortcutMenuItem(MenuShortcut paramMenuShortcut)
  {
    return paramMenuShortcut.equals(shortcut) ? this : null;
  }
  
  protected final void enableEvents(long paramLong)
  {
    eventMask |= paramLong;
    newEventsOnly = true;
  }
  
  protected final void disableEvents(long paramLong)
  {
    eventMask &= (paramLong ^ 0xFFFFFFFFFFFFFFFF);
  }
  
  public void setActionCommand(String paramString)
  {
    actionCommand = paramString;
  }
  
  public String getActionCommand()
  {
    return getActionCommandImpl();
  }
  
  final String getActionCommandImpl()
  {
    return actionCommand == null ? label : actionCommand;
  }
  
  public synchronized void addActionListener(ActionListener paramActionListener)
  {
    if (paramActionListener == null) {
      return;
    }
    actionListener = AWTEventMulticaster.add(actionListener, paramActionListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeActionListener(ActionListener paramActionListener)
  {
    if (paramActionListener == null) {
      return;
    }
    actionListener = AWTEventMulticaster.remove(actionListener, paramActionListener);
  }
  
  public synchronized ActionListener[] getActionListeners()
  {
    return (ActionListener[])getListeners(ActionListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    ActionListener localActionListener = null;
    if (paramClass == ActionListener.class) {
      localActionListener = actionListener;
    }
    return AWTEventMulticaster.getListeners(localActionListener, paramClass);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof ActionEvent)) {
      processActionEvent((ActionEvent)paramAWTEvent);
    }
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    if (id == 1001) {
      return ((eventMask & 0x80) != 0L) || (actionListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processActionEvent(ActionEvent paramActionEvent)
  {
    ActionListener localActionListener = actionListener;
    if (localActionListener != null) {
      localActionListener.actionPerformed(paramActionEvent);
    }
  }
  
  public String paramString()
  {
    String str = ",label=" + label;
    if (shortcut != null) {
      str = str + ",shortcut=" + shortcut;
    }
    return super.paramString() + str;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "actionL", actionListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    paramObjectInputStream.defaultReadObject();
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject()))
    {
      String str = ((String)localObject).intern();
      if ("actionL" == str) {
        addActionListener((ActionListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTMenuItem();
    }
    return accessibleContext;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setMenuItemAccessor(new AWTAccessor.MenuItemAccessor()
    {
      public boolean isEnabled(MenuItem paramAnonymousMenuItem)
      {
        return enabled;
      }
      
      public String getLabel(MenuItem paramAnonymousMenuItem)
      {
        return label;
      }
      
      public MenuShortcut getShortcut(MenuItem paramAnonymousMenuItem)
      {
        return shortcut;
      }
      
      public String getActionCommandImpl(MenuItem paramAnonymousMenuItem)
      {
        return paramAnonymousMenuItem.getActionCommandImpl();
      }
      
      public boolean isItemEnabled(MenuItem paramAnonymousMenuItem)
      {
        return paramAnonymousMenuItem.isItemEnabled();
      }
    });
  }
  
  protected class AccessibleAWTMenuItem
    extends MenuComponent.AccessibleAWTMenuComponent
    implements AccessibleAction, AccessibleValue
  {
    private static final long serialVersionUID = -217847831945965825L;
    
    protected AccessibleAWTMenuItem()
    {
      super();
    }
    
    public String getAccessibleName()
    {
      if (accessibleName != null) {
        return accessibleName;
      }
      if (getLabel() == null) {
        return super.getAccessibleName();
      }
      return getLabel();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.MENU_ITEM;
    }
    
    public AccessibleAction getAccessibleAction()
    {
      return this;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }
    
    public int getAccessibleActionCount()
    {
      return 1;
    }
    
    public String getAccessibleActionDescription(int paramInt)
    {
      if (paramInt == 0) {
        return "click";
      }
      return null;
    }
    
    public boolean doAccessibleAction(int paramInt)
    {
      if (paramInt == 0)
      {
        Toolkit.getEventQueue().postEvent(new ActionEvent(MenuItem.this, 1001, getActionCommand(), EventQueue.getMostRecentEventTime(), 0));
        return true;
      }
      return false;
    }
    
    public Number getCurrentAccessibleValue()
    {
      return Integer.valueOf(0);
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      return false;
    }
    
    public Number getMinimumAccessibleValue()
    {
      return Integer.valueOf(0);
    }
    
    public Number getMaximumAccessibleValue()
    {
      return Integer.valueOf(0);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\MenuItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */