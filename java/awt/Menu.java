package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.MenuPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.MenuAccessor;

public class Menu
  extends MenuItem
  implements MenuContainer, Accessible
{
  Vector<MenuComponent> items = new Vector();
  boolean tearOff;
  boolean isHelpMenu;
  private static final String base = "menu";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -8809584163345499784L;
  private int menuSerializedDataVersion = 1;
  
  public Menu()
    throws HeadlessException
  {
    this("", false);
  }
  
  public Menu(String paramString)
    throws HeadlessException
  {
    this(paramString, false);
  }
  
  public Menu(String paramString, boolean paramBoolean)
    throws HeadlessException
  {
    super(paramString);
    tearOff = paramBoolean;
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 7
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 132	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 262	java/lang/StringBuilder:<init>	()V
    //   12: ldc 6
    //   14: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 224	java/awt/Menu:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 224	java/awt/Menu:nameCounter	I
    //   26: invokevirtual 264	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 263	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	Menu
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
        peer = Toolkit.getDefaultToolkit().createMenu(this);
      }
      int i = getItemCount();
      for (int j = 0; j < i; j++)
      {
        MenuItem localMenuItem = getItem(j);
        parent = this;
        localMenuItem.addNotify();
      }
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      int i = getItemCount();
      for (int j = 0; j < i; j++) {
        getItem(j).removeNotify();
      }
      super.removeNotify();
    }
  }
  
  public boolean isTearOff()
  {
    return tearOff;
  }
  
  public int getItemCount()
  {
    return countItems();
  }
  
  @Deprecated
  public int countItems()
  {
    return countItemsImpl();
  }
  
  final int countItemsImpl()
  {
    return items.size();
  }
  
  public MenuItem getItem(int paramInt)
  {
    return getItemImpl(paramInt);
  }
  
  final MenuItem getItemImpl(int paramInt)
  {
    return (MenuItem)items.elementAt(paramInt);
  }
  
  public MenuItem add(MenuItem paramMenuItem)
  {
    synchronized (getTreeLock())
    {
      if (parent != null) {
        parent.remove(paramMenuItem);
      }
      items.addElement(paramMenuItem);
      parent = this;
      MenuPeer localMenuPeer = (MenuPeer)peer;
      if (localMenuPeer != null)
      {
        paramMenuItem.addNotify();
        localMenuPeer.addItem(paramMenuItem);
      }
      return paramMenuItem;
    }
  }
  
  public void add(String paramString)
  {
    add(new MenuItem(paramString));
  }
  
  public void insert(MenuItem paramMenuItem, int paramInt)
  {
    synchronized (getTreeLock())
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("index less than zero.");
      }
      int i = getItemCount();
      Vector localVector = new Vector();
      for (int j = paramInt; j < i; j++)
      {
        localVector.addElement(getItem(paramInt));
        remove(paramInt);
      }
      add(paramMenuItem);
      for (j = 0; j < localVector.size(); j++) {
        add((MenuItem)localVector.elementAt(j));
      }
    }
  }
  
  public void insert(String paramString, int paramInt)
  {
    insert(new MenuItem(paramString), paramInt);
  }
  
  public void addSeparator()
  {
    add("-");
  }
  
  public void insertSeparator(int paramInt)
  {
    synchronized (getTreeLock())
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("index less than zero.");
      }
      int i = getItemCount();
      Vector localVector = new Vector();
      for (int j = paramInt; j < i; j++)
      {
        localVector.addElement(getItem(paramInt));
        remove(paramInt);
      }
      addSeparator();
      for (j = 0; j < localVector.size(); j++) {
        add((MenuItem)localVector.elementAt(j));
      }
    }
  }
  
  public void remove(int paramInt)
  {
    synchronized (getTreeLock())
    {
      MenuItem localMenuItem = getItem(paramInt);
      items.removeElementAt(paramInt);
      MenuPeer localMenuPeer = (MenuPeer)peer;
      if (localMenuPeer != null)
      {
        localMenuPeer.delItem(paramInt);
        localMenuItem.removeNotify();
        parent = null;
      }
    }
  }
  
  public void remove(MenuComponent paramMenuComponent)
  {
    synchronized (getTreeLock())
    {
      int i = items.indexOf(paramMenuComponent);
      if (i >= 0) {
        remove(i);
      }
    }
  }
  
  public void removeAll()
  {
    synchronized (getTreeLock())
    {
      int i = getItemCount();
      for (int j = i - 1; j >= 0; j--) {
        remove(j);
      }
    }
  }
  
  boolean handleShortcut(KeyEvent paramKeyEvent)
  {
    int i = getItemCount();
    for (int j = 0; j < i; j++)
    {
      MenuItem localMenuItem = getItem(j);
      if (localMenuItem.handleShortcut(paramKeyEvent)) {
        return true;
      }
    }
    return false;
  }
  
  MenuItem getShortcutMenuItem(MenuShortcut paramMenuShortcut)
  {
    int i = getItemCount();
    for (int j = 0; j < i; j++)
    {
      MenuItem localMenuItem = getItem(j).getShortcutMenuItem(paramMenuShortcut);
      if (localMenuItem != null) {
        return localMenuItem;
      }
    }
    return null;
  }
  
  synchronized Enumeration<MenuShortcut> shortcuts()
  {
    Vector localVector = new Vector();
    int i = getItemCount();
    for (int j = 0; j < i; j++)
    {
      MenuItem localMenuItem = getItem(j);
      Object localObject;
      if ((localMenuItem instanceof Menu))
      {
        localObject = ((Menu)localMenuItem).shortcuts();
        while (((Enumeration)localObject).hasMoreElements()) {
          localVector.addElement(((Enumeration)localObject).nextElement());
        }
      }
      else
      {
        localObject = localMenuItem.getShortcut();
        if (localObject != null) {
          localVector.addElement(localObject);
        }
      }
    }
    return localVector.elements();
  }
  
  void deleteShortcut(MenuShortcut paramMenuShortcut)
  {
    int i = getItemCount();
    for (int j = 0; j < i; j++) {
      getItem(j).deleteShortcut(paramMenuShortcut);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException, HeadlessException
  {
    paramObjectInputStream.defaultReadObject();
    for (int i = 0; i < items.size(); i++)
    {
      MenuItem localMenuItem = (MenuItem)items.elementAt(i);
      parent = this;
    }
  }
  
  public String paramString()
  {
    String str = ",tearOff=" + tearOff + ",isHelpMenu=" + isHelpMenu;
    return super.paramString() + str;
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTMenu();
    }
    return accessibleContext;
  }
  
  int getAccessibleChildIndex(MenuComponent paramMenuComponent)
  {
    return items.indexOf(paramMenuComponent);
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setMenuAccessor(new AWTAccessor.MenuAccessor()
    {
      public Vector<MenuComponent> getItems(Menu paramAnonymousMenu)
      {
        return items;
      }
    });
  }
  
  protected class AccessibleAWTMenu
    extends MenuItem.AccessibleAWTMenuItem
  {
    private static final long serialVersionUID = 5228160894980069094L;
    
    protected AccessibleAWTMenu()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.MENU;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Menu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */