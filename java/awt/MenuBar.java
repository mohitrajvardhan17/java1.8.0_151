package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.MenuBarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.MenuBarAccessor;

public class MenuBar
  extends MenuComponent
  implements MenuContainer, Accessible
{
  Vector<Menu> menus = new Vector();
  Menu helpMenu;
  private static final String base = "menubar";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -4930327919388951260L;
  private int menuBarSerializedDataVersion = 1;
  
  public MenuBar()
    throws HeadlessException
  {}
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 2
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 121	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 244	java/lang/StringBuilder:<init>	()V
    //   12: ldc 1
    //   14: invokevirtual 247	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 210	java/awt/MenuBar:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 210	java/awt/MenuBar:nameCounter	I
    //   26: invokevirtual 246	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 245	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	MenuBar
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
        peer = Toolkit.getDefaultToolkit().createMenuBar(this);
      }
      int i = getMenuCount();
      for (int j = 0; j < i; j++) {
        getMenu(j).addNotify();
      }
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      int i = getMenuCount();
      for (int j = 0; j < i; j++) {
        getMenu(j).removeNotify();
      }
      super.removeNotify();
    }
  }
  
  public Menu getHelpMenu()
  {
    return helpMenu;
  }
  
  public void setHelpMenu(Menu paramMenu)
  {
    synchronized (getTreeLock())
    {
      if (helpMenu == paramMenu) {
        return;
      }
      if (helpMenu != null) {
        remove(helpMenu);
      }
      helpMenu = paramMenu;
      if (paramMenu != null)
      {
        if (parent != this) {
          add(paramMenu);
        }
        isHelpMenu = true;
        parent = this;
        MenuBarPeer localMenuBarPeer = (MenuBarPeer)peer;
        if (localMenuBarPeer != null)
        {
          if (peer == null) {
            paramMenu.addNotify();
          }
          localMenuBarPeer.addHelpMenu(paramMenu);
        }
      }
    }
  }
  
  public Menu add(Menu paramMenu)
  {
    synchronized (getTreeLock())
    {
      if (parent != null) {
        parent.remove(paramMenu);
      }
      parent = this;
      MenuBarPeer localMenuBarPeer = (MenuBarPeer)peer;
      if (localMenuBarPeer != null)
      {
        if (peer == null) {
          paramMenu.addNotify();
        }
        menus.addElement(paramMenu);
        localMenuBarPeer.addMenu(paramMenu);
      }
      else
      {
        menus.addElement(paramMenu);
      }
      return paramMenu;
    }
  }
  
  public void remove(int paramInt)
  {
    synchronized (getTreeLock())
    {
      Menu localMenu = getMenu(paramInt);
      menus.removeElementAt(paramInt);
      MenuBarPeer localMenuBarPeer = (MenuBarPeer)peer;
      if (localMenuBarPeer != null)
      {
        localMenuBarPeer.delMenu(paramInt);
        localMenu.removeNotify();
        parent = null;
      }
      if (helpMenu == localMenu)
      {
        helpMenu = null;
        isHelpMenu = false;
      }
    }
  }
  
  public void remove(MenuComponent paramMenuComponent)
  {
    synchronized (getTreeLock())
    {
      int i = menus.indexOf(paramMenuComponent);
      if (i >= 0) {
        remove(i);
      }
    }
  }
  
  public int getMenuCount()
  {
    return countMenus();
  }
  
  @Deprecated
  public int countMenus()
  {
    return getMenuCountImpl();
  }
  
  final int getMenuCountImpl()
  {
    return menus.size();
  }
  
  public Menu getMenu(int paramInt)
  {
    return getMenuImpl(paramInt);
  }
  
  final Menu getMenuImpl(int paramInt)
  {
    return (Menu)menus.elementAt(paramInt);
  }
  
  public synchronized Enumeration<MenuShortcut> shortcuts()
  {
    Vector localVector = new Vector();
    int i = getMenuCount();
    for (int j = 0; j < i; j++)
    {
      Enumeration localEnumeration = getMenu(j).shortcuts();
      while (localEnumeration.hasMoreElements()) {
        localVector.addElement(localEnumeration.nextElement());
      }
    }
    return localVector.elements();
  }
  
  public MenuItem getShortcutMenuItem(MenuShortcut paramMenuShortcut)
  {
    int i = getMenuCount();
    for (int j = 0; j < i; j++)
    {
      MenuItem localMenuItem = getMenu(j).getShortcutMenuItem(paramMenuShortcut);
      if (localMenuItem != null) {
        return localMenuItem;
      }
    }
    return null;
  }
  
  boolean handleShortcut(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getID();
    if ((i != 401) && (i != 402)) {
      return false;
    }
    int j = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    if ((paramKeyEvent.getModifiers() & j) == 0) {
      return false;
    }
    int k = getMenuCount();
    for (int m = 0; m < k; m++)
    {
      Menu localMenu = getMenu(m);
      if (localMenu.handleShortcut(paramKeyEvent)) {
        return true;
      }
    }
    return false;
  }
  
  public void deleteShortcut(MenuShortcut paramMenuShortcut)
  {
    int i = getMenuCount();
    for (int j = 0; j < i; j++) {
      getMenu(j).deleteShortcut(paramMenuShortcut);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    paramObjectInputStream.defaultReadObject();
    for (int i = 0; i < menus.size(); i++)
    {
      Menu localMenu = (Menu)menus.elementAt(i);
      parent = this;
    }
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTMenuBar();
    }
    return accessibleContext;
  }
  
  int getAccessibleChildIndex(MenuComponent paramMenuComponent)
  {
    return menus.indexOf(paramMenuComponent);
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setMenuBarAccessor(new AWTAccessor.MenuBarAccessor()
    {
      public Menu getHelpMenu(MenuBar paramAnonymousMenuBar)
      {
        return helpMenu;
      }
      
      public Vector<Menu> getMenus(MenuBar paramAnonymousMenuBar)
      {
        return menus;
      }
    });
  }
  
  protected class AccessibleAWTMenuBar
    extends MenuComponent.AccessibleAWTMenuComponent
  {
    private static final long serialVersionUID = -8577604491830083815L;
    
    protected AccessibleAWTMenuBar()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.MENU_BAR;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\MenuBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */