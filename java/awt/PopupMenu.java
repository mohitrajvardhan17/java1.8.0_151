package java.awt;

import java.awt.peer.PopupMenuPeer;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.PopupMenuAccessor;

public class PopupMenu
  extends Menu
{
  private static final String base = "popup";
  static int nameCounter = 0;
  transient boolean isTrayIconPopup = false;
  private static final long serialVersionUID = -4620452533522760060L;
  
  public PopupMenu()
    throws HeadlessException
  {
    this("");
  }
  
  public PopupMenu(String paramString)
    throws HeadlessException
  {
    super(paramString);
  }
  
  public MenuContainer getParent()
  {
    if (isTrayIconPopup) {
      return null;
    }
    return super.getParent();
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 7
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 91	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   12: ldc 6
    //   14: invokevirtual 165	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 137	java/awt/PopupMenu:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 137	java/awt/PopupMenu:nameCounter	I
    //   26: invokevirtual 164	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 163	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	PopupMenu
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
      if ((parent != null) && (!(parent instanceof Component)))
      {
        super.addNotify();
      }
      else
      {
        if (peer == null) {
          peer = Toolkit.getDefaultToolkit().createPopupMenu(this);
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
  }
  
  public void show(Component paramComponent, int paramInt1, int paramInt2)
  {
    MenuContainer localMenuContainer = parent;
    if (localMenuContainer == null) {
      throw new NullPointerException("parent is null");
    }
    if (!(localMenuContainer instanceof Component)) {
      throw new IllegalArgumentException("PopupMenus with non-Component parents cannot be shown");
    }
    Component localComponent = (Component)localMenuContainer;
    if (localComponent != paramComponent) {
      if ((localComponent instanceof Container))
      {
        if (!((Container)localComponent).isAncestorOf(paramComponent)) {
          throw new IllegalArgumentException("origin not in parent's hierarchy");
        }
      }
      else {
        throw new IllegalArgumentException("origin not in parent's hierarchy");
      }
    }
    if ((localComponent.getPeer() == null) || (!localComponent.isShowing())) {
      throw new RuntimeException("parent not showing on screen");
    }
    if (peer == null) {
      addNotify();
    }
    synchronized (getTreeLock())
    {
      if (peer != null) {
        ((PopupMenuPeer)peer).show(new Event(paramComponent, 0L, 501, paramInt1, paramInt2, 0, 0));
      }
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTPopupMenu();
    }
    return accessibleContext;
  }
  
  static
  {
    AWTAccessor.setPopupMenuAccessor(new AWTAccessor.PopupMenuAccessor()
    {
      public boolean isTrayIconPopup(PopupMenu paramAnonymousPopupMenu)
      {
        return isTrayIconPopup;
      }
    });
  }
  
  protected class AccessibleAWTPopupMenu
    extends Menu.AccessibleAWTMenu
  {
    private static final long serialVersionUID = -4282044795947239955L;
    
    protected AccessibleAWTPopupMenu()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.POPUP_MENU;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\PopupMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */