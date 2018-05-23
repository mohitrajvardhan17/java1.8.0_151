package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxMenuItemPeer;
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
import sun.awt.AWTAccessor.CheckboxMenuItemAccessor;

public class CheckboxMenuItem
  extends MenuItem
  implements ItemSelectable, Accessible
{
  boolean state = false;
  transient ItemListener itemListener;
  private static final String base = "chkmenuitem";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 6190621106981774043L;
  private int checkboxMenuItemSerializedDataVersion = 1;
  
  public CheckboxMenuItem()
    throws HeadlessException
  {
    this("", false);
  }
  
  public CheckboxMenuItem(String paramString)
    throws HeadlessException
  {
    this(paramString, false);
  }
  
  public CheckboxMenuItem(String paramString, boolean paramBoolean)
    throws HeadlessException
  {
    super(paramString);
    state = paramBoolean;
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 5
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 131	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 251	java/lang/StringBuilder:<init>	()V
    //   12: ldc 3
    //   14: invokevirtual 255	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 211	java/awt/CheckboxMenuItem:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 211	java/awt/CheckboxMenuItem:nameCounter	I
    //   26: invokevirtual 253	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	CheckboxMenuItem
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
        peer = Toolkit.getDefaultToolkit().createCheckboxMenuItem(this);
      }
      super.addNotify();
    }
  }
  
  public boolean getState()
  {
    return state;
  }
  
  public synchronized void setState(boolean paramBoolean)
  {
    state = paramBoolean;
    CheckboxMenuItemPeer localCheckboxMenuItemPeer = (CheckboxMenuItemPeer)peer;
    if (localCheckboxMenuItemPeer != null) {
      localCheckboxMenuItemPeer.setState(paramBoolean);
    }
  }
  
  public synchronized Object[] getSelectedObjects()
  {
    if (state)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = label;
      return arrayOfObject;
    }
    return null;
  }
  
  public synchronized void addItemListener(ItemListener paramItemListener)
  {
    if (paramItemListener == null) {
      return;
    }
    itemListener = AWTEventMulticaster.add(itemListener, paramItemListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeItemListener(ItemListener paramItemListener)
  {
    if (paramItemListener == null) {
      return;
    }
    itemListener = AWTEventMulticaster.remove(itemListener, paramItemListener);
  }
  
  public synchronized ItemListener[] getItemListeners()
  {
    return (ItemListener[])getListeners(ItemListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    ItemListener localItemListener = null;
    if (paramClass == ItemListener.class) {
      localItemListener = itemListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners(localItemListener, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    if (id == 701) {
      return ((eventMask & 0x200) != 0L) || (itemListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof ItemEvent))
    {
      processItemEvent((ItemEvent)paramAWTEvent);
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processItemEvent(ItemEvent paramItemEvent)
  {
    ItemListener localItemListener = itemListener;
    if (localItemListener != null) {
      localItemListener.itemStateChanged(paramItemEvent);
    }
  }
  
  void doMenuEvent(long paramLong, int paramInt)
  {
    setState(!state);
    Toolkit.getEventQueue().postEvent(new ItemEvent(this, 701, getLabel(), state ? 1 : 2));
  }
  
  public String paramString()
  {
    return super.paramString() + ",state=" + state;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "itemL", itemListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject()))
    {
      String str = ((String)localObject).intern();
      if ("itemL" == str) {
        addItemListener((ItemListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTCheckboxMenuItem();
    }
    return accessibleContext;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setCheckboxMenuItemAccessor(new AWTAccessor.CheckboxMenuItemAccessor()
    {
      public boolean getState(CheckboxMenuItem paramAnonymousCheckboxMenuItem)
      {
        return state;
      }
    });
  }
  
  protected class AccessibleAWTCheckboxMenuItem
    extends MenuItem.AccessibleAWTMenuItem
    implements AccessibleAction, AccessibleValue
  {
    private static final long serialVersionUID = -1122642964303476L;
    
    protected AccessibleAWTCheckboxMenuItem()
    {
      super();
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
      return 0;
    }
    
    public String getAccessibleActionDescription(int paramInt)
    {
      return null;
    }
    
    public boolean doAccessibleAction(int paramInt)
    {
      return false;
    }
    
    public Number getCurrentAccessibleValue()
    {
      return null;
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      return false;
    }
    
    public Number getMinimumAccessibleValue()
    {
      return null;
    }
    
    public Number getMaximumAccessibleValue()
    {
      return null;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.CHECK_BOX;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\CheckboxMenuItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */