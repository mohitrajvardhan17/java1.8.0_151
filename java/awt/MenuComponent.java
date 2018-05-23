package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.peer.MenuComponentPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.MenuComponentAccessor;
import sun.awt.AppContext;

public abstract class MenuComponent
  implements Serializable
{
  transient MenuComponentPeer peer;
  transient MenuContainer parent;
  transient AppContext appContext;
  volatile Font font;
  private String name;
  private boolean nameExplicitlySet = false;
  boolean newEventsOnly = false;
  private volatile transient AccessControlContext acc = AccessController.getContext();
  static final String actionListenerK = "actionL";
  static final String itemListenerK = "itemL";
  private static final long serialVersionUID = -4536902356223894379L;
  AccessibleContext accessibleContext = null;
  
  final AccessControlContext getAccessControlContext()
  {
    if (acc == null) {
      throw new SecurityException("MenuComponent is missing AccessControlContext");
    }
    return acc;
  }
  
  public MenuComponent()
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    appContext = AppContext.getAppContext();
  }
  
  String constructComponentName()
  {
    return null;
  }
  
  public String getName()
  {
    if ((name == null) && (!nameExplicitlySet)) {
      synchronized (this)
      {
        if ((name == null) && (!nameExplicitlySet)) {
          name = constructComponentName();
        }
      }
    }
    return name;
  }
  
  public void setName(String paramString)
  {
    synchronized (this)
    {
      name = paramString;
      nameExplicitlySet = true;
    }
  }
  
  public MenuContainer getParent()
  {
    return getParent_NoClientCode();
  }
  
  final MenuContainer getParent_NoClientCode()
  {
    return parent;
  }
  
  @Deprecated
  public MenuComponentPeer getPeer()
  {
    return peer;
  }
  
  public Font getFont()
  {
    Font localFont = font;
    if (localFont != null) {
      return localFont;
    }
    MenuContainer localMenuContainer = parent;
    if (localMenuContainer != null) {
      return localMenuContainer.getFont();
    }
    return null;
  }
  
  final Font getFont_NoClientCode()
  {
    Font localFont = font;
    if (localFont != null) {
      return localFont;
    }
    MenuContainer localMenuContainer = parent;
    if (localMenuContainer != null) {
      if ((localMenuContainer instanceof Component)) {
        localFont = ((Component)localMenuContainer).getFont_NoClientCode();
      } else if ((localMenuContainer instanceof MenuComponent)) {
        localFont = ((MenuComponent)localMenuContainer).getFont_NoClientCode();
      }
    }
    return localFont;
  }
  
  public void setFont(Font paramFont)
  {
    synchronized (getTreeLock())
    {
      font = paramFont;
      MenuComponentPeer localMenuComponentPeer = peer;
      if (localMenuComponentPeer != null) {
        localMenuComponentPeer.setFont(paramFont);
      }
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      MenuComponentPeer localMenuComponentPeer = peer;
      if (localMenuComponentPeer != null)
      {
        Toolkit.getEventQueue().removeSourceEvents(this, true);
        peer = null;
        localMenuComponentPeer.dispose();
      }
    }
  }
  
  @Deprecated
  public boolean postEvent(Event paramEvent)
  {
    MenuContainer localMenuContainer = parent;
    if (localMenuContainer != null) {
      localMenuContainer.postEvent(paramEvent);
    }
    return false;
  }
  
  public final void dispatchEvent(AWTEvent paramAWTEvent)
  {
    dispatchEventImpl(paramAWTEvent);
  }
  
  void dispatchEventImpl(AWTEvent paramAWTEvent)
  {
    EventQueue.setCurrentEventAndMostRecentTime(paramAWTEvent);
    Toolkit.getDefaultToolkit().notifyAWTEventListeners(paramAWTEvent);
    if ((newEventsOnly) || ((parent != null) && ((parent instanceof MenuComponent)) && (parent).newEventsOnly)))
    {
      if (eventEnabled(paramAWTEvent))
      {
        processEvent(paramAWTEvent);
      }
      else if (((paramAWTEvent instanceof ActionEvent)) && (parent != null))
      {
        paramAWTEvent.setSource(parent);
        ((MenuComponent)parent).dispatchEvent(paramAWTEvent);
      }
    }
    else
    {
      Event localEvent = paramAWTEvent.convertToOld();
      if (localEvent != null) {
        postEvent(localEvent);
      }
    }
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    return false;
  }
  
  protected void processEvent(AWTEvent paramAWTEvent) {}
  
  protected String paramString()
  {
    String str = getName();
    return str != null ? str : "";
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + paramString() + "]";
  }
  
  protected final Object getTreeLock()
  {
    return Component.LOCK;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    acc = AccessController.getContext();
    paramObjectInputStream.defaultReadObject();
    appContext = AppContext.getAppContext();
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    return accessibleContext;
  }
  
  int getAccessibleIndexInParent()
  {
    MenuContainer localMenuContainer = parent;
    if (!(localMenuContainer instanceof MenuComponent)) {
      return -1;
    }
    MenuComponent localMenuComponent = (MenuComponent)localMenuContainer;
    return localMenuComponent.getAccessibleChildIndex(this);
  }
  
  int getAccessibleChildIndex(MenuComponent paramMenuComponent)
  {
    return -1;
  }
  
  AccessibleStateSet getAccessibleStateSet()
  {
    AccessibleStateSet localAccessibleStateSet = new AccessibleStateSet();
    return localAccessibleStateSet;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setMenuComponentAccessor(new AWTAccessor.MenuComponentAccessor()
    {
      public AppContext getAppContext(MenuComponent paramAnonymousMenuComponent)
      {
        return appContext;
      }
      
      public void setAppContext(MenuComponent paramAnonymousMenuComponent, AppContext paramAnonymousAppContext)
      {
        appContext = paramAnonymousAppContext;
      }
      
      public MenuContainer getParent(MenuComponent paramAnonymousMenuComponent)
      {
        return parent;
      }
      
      public Font getFont_NoClientCode(MenuComponent paramAnonymousMenuComponent)
      {
        return paramAnonymousMenuComponent.getFont_NoClientCode();
      }
      
      public <T extends MenuComponentPeer> T getPeer(MenuComponent paramAnonymousMenuComponent)
      {
        return peer;
      }
    });
  }
  
  protected abstract class AccessibleAWTMenuComponent
    extends AccessibleContext
    implements Serializable, AccessibleComponent, AccessibleSelection
  {
    private static final long serialVersionUID = -4269533416223798698L;
    
    protected AccessibleAWTMenuComponent() {}
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public String getAccessibleName()
    {
      return accessibleName;
    }
    
    public String getAccessibleDescription()
    {
      return accessibleDescription;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.AWT_COMPONENT;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      return MenuComponent.this.getAccessibleStateSet();
    }
    
    public Accessible getAccessibleParent()
    {
      if (accessibleParent != null) {
        return accessibleParent;
      }
      MenuContainer localMenuContainer = getParent();
      if ((localMenuContainer instanceof Accessible)) {
        return (Accessible)localMenuContainer;
      }
      return null;
    }
    
    public int getAccessibleIndexInParent()
    {
      return MenuComponent.this.getAccessibleIndexInParent();
    }
    
    public int getAccessibleChildrenCount()
    {
      return 0;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      return null;
    }
    
    public Locale getLocale()
    {
      MenuContainer localMenuContainer = getParent();
      if ((localMenuContainer instanceof Component)) {
        return ((Component)localMenuContainer).getLocale();
      }
      return Locale.getDefault();
    }
    
    public AccessibleComponent getAccessibleComponent()
    {
      return this;
    }
    
    public Color getBackground()
    {
      return null;
    }
    
    public void setBackground(Color paramColor) {}
    
    public Color getForeground()
    {
      return null;
    }
    
    public void setForeground(Color paramColor) {}
    
    public Cursor getCursor()
    {
      return null;
    }
    
    public void setCursor(Cursor paramCursor) {}
    
    public Font getFont()
    {
      return MenuComponent.this.getFont();
    }
    
    public void setFont(Font paramFont)
    {
      MenuComponent.this.setFont(paramFont);
    }
    
    public FontMetrics getFontMetrics(Font paramFont)
    {
      return null;
    }
    
    public boolean isEnabled()
    {
      return true;
    }
    
    public void setEnabled(boolean paramBoolean) {}
    
    public boolean isVisible()
    {
      return true;
    }
    
    public void setVisible(boolean paramBoolean) {}
    
    public boolean isShowing()
    {
      return true;
    }
    
    public boolean contains(Point paramPoint)
    {
      return false;
    }
    
    public Point getLocationOnScreen()
    {
      return null;
    }
    
    public Point getLocation()
    {
      return null;
    }
    
    public void setLocation(Point paramPoint) {}
    
    public Rectangle getBounds()
    {
      return null;
    }
    
    public void setBounds(Rectangle paramRectangle) {}
    
    public Dimension getSize()
    {
      return null;
    }
    
    public void setSize(Dimension paramDimension) {}
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      return null;
    }
    
    public boolean isFocusTraversable()
    {
      return true;
    }
    
    public void requestFocus() {}
    
    public void addFocusListener(FocusListener paramFocusListener) {}
    
    public void removeFocusListener(FocusListener paramFocusListener) {}
    
    public int getAccessibleSelectionCount()
    {
      return 0;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      return null;
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      return false;
    }
    
    public void addAccessibleSelection(int paramInt) {}
    
    public void removeAccessibleSelection(int paramInt) {}
    
    public void clearAccessibleSelection() {}
    
    public void selectAllAccessibleSelection() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\MenuComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */