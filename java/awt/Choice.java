package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ChoicePeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Choice
  extends Component
  implements ItemSelectable, Accessible
{
  Vector<String> pItems;
  int selectedIndex = -1;
  transient ItemListener itemListener;
  private static final String base = "choice";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -4075310674757313071L;
  private int choiceSerializedDataVersion = 1;
  
  public Choice()
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    pItems = new Vector();
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 9
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 157	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 297	java/lang/StringBuilder:<init>	()V
    //   12: ldc 4
    //   14: invokevirtual 300	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 251	java/awt/Choice:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 251	java/awt/Choice:nameCounter	I
    //   26: invokevirtual 299	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 298	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	Choice
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
        peer = getToolkit().createChoice(this);
      }
      super.addNotify();
    }
  }
  
  public int getItemCount()
  {
    return countItems();
  }
  
  @Deprecated
  public int countItems()
  {
    return pItems.size();
  }
  
  public String getItem(int paramInt)
  {
    return getItemImpl(paramInt);
  }
  
  final String getItemImpl(int paramInt)
  {
    return (String)pItems.elementAt(paramInt);
  }
  
  public void add(String paramString)
  {
    addItem(paramString);
  }
  
  public void addItem(String paramString)
  {
    synchronized (this)
    {
      insertNoInvalidate(paramString, pItems.size());
    }
    invalidateIfValid();
  }
  
  private void insertNoInvalidate(String paramString, int paramInt)
  {
    if (paramString == null) {
      throw new NullPointerException("cannot add null item to Choice");
    }
    pItems.insertElementAt(paramString, paramInt);
    ChoicePeer localChoicePeer = (ChoicePeer)peer;
    if (localChoicePeer != null) {
      localChoicePeer.add(paramString, paramInt);
    }
    if ((selectedIndex < 0) || (selectedIndex >= paramInt)) {
      select(0);
    }
  }
  
  public void insert(String paramString, int paramInt)
  {
    synchronized (this)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("index less than zero.");
      }
      paramInt = Math.min(paramInt, pItems.size());
      insertNoInvalidate(paramString, paramInt);
    }
    invalidateIfValid();
  }
  
  public void remove(String paramString)
  {
    synchronized (this)
    {
      int i = pItems.indexOf(paramString);
      if (i < 0) {
        throw new IllegalArgumentException("item " + paramString + " not found in choice");
      }
      removeNoInvalidate(i);
    }
    invalidateIfValid();
  }
  
  public void remove(int paramInt)
  {
    synchronized (this)
    {
      removeNoInvalidate(paramInt);
    }
    invalidateIfValid();
  }
  
  private void removeNoInvalidate(int paramInt)
  {
    pItems.removeElementAt(paramInt);
    ChoicePeer localChoicePeer = (ChoicePeer)peer;
    if (localChoicePeer != null) {
      localChoicePeer.remove(paramInt);
    }
    if (pItems.size() == 0) {
      selectedIndex = -1;
    } else if (selectedIndex == paramInt) {
      select(0);
    } else if (selectedIndex > paramInt) {
      select(selectedIndex - 1);
    }
  }
  
  public void removeAll()
  {
    synchronized (this)
    {
      if (peer != null) {
        ((ChoicePeer)peer).removeAll();
      }
      pItems.removeAllElements();
      selectedIndex = -1;
    }
    invalidateIfValid();
  }
  
  public synchronized String getSelectedItem()
  {
    return selectedIndex >= 0 ? getItem(selectedIndex) : null;
  }
  
  public synchronized Object[] getSelectedObjects()
  {
    if (selectedIndex >= 0)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = getItem(selectedIndex);
      return arrayOfObject;
    }
    return null;
  }
  
  public int getSelectedIndex()
  {
    return selectedIndex;
  }
  
  public synchronized void select(int paramInt)
  {
    if ((paramInt >= pItems.size()) || (paramInt < 0)) {
      throw new IllegalArgumentException("illegal Choice item position: " + paramInt);
    }
    if (pItems.size() > 0)
    {
      selectedIndex = paramInt;
      ChoicePeer localChoicePeer = (ChoicePeer)peer;
      if (localChoicePeer != null) {
        localChoicePeer.select(paramInt);
      }
    }
  }
  
  public synchronized void select(String paramString)
  {
    int i = pItems.indexOf(paramString);
    if (i >= 0) {
      select(i);
    }
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
  
  protected String paramString()
  {
    return super.paramString() + ",current=" + getSelectedItem();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "itemL", itemListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
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
      accessibleContext = new AccessibleAWTChoice();
    }
    return accessibleContext;
  }
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
  
  protected class AccessibleAWTChoice
    extends Component.AccessibleAWTComponent
    implements AccessibleAction
  {
    private static final long serialVersionUID = 7175603582428509322L;
    
    public AccessibleAWTChoice()
    {
      super();
    }
    
    public AccessibleAction getAccessibleAction()
    {
      return this;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.COMBO_BOX;
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
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Choice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */