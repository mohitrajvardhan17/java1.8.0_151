package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;

public class Checkbox
  extends Component
  implements ItemSelectable, Accessible
{
  String label;
  boolean state;
  CheckboxGroup group;
  transient ItemListener itemListener;
  private static final String base = "checkbox";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 7270714317450821763L;
  private int checkboxSerializedDataVersion = 1;
  
  void setStateInternal(boolean paramBoolean)
  {
    state = paramBoolean;
    CheckboxPeer localCheckboxPeer = (CheckboxPeer)peer;
    if (localCheckboxPeer != null) {
      localCheckboxPeer.setState(paramBoolean);
    }
  }
  
  public Checkbox()
    throws HeadlessException
  {
    this("", false, null);
  }
  
  public Checkbox(String paramString)
    throws HeadlessException
  {
    this(paramString, false, null);
  }
  
  public Checkbox(String paramString, boolean paramBoolean)
    throws HeadlessException
  {
    this(paramString, paramBoolean, null);
  }
  
  public Checkbox(String paramString, boolean paramBoolean, CheckboxGroup paramCheckboxGroup)
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    label = paramString;
    state = paramBoolean;
    group = paramCheckboxGroup;
    if ((paramBoolean) && (paramCheckboxGroup != null)) {
      paramCheckboxGroup.setSelectedCheckbox(this);
    }
  }
  
  public Checkbox(String paramString, CheckboxGroup paramCheckboxGroup, boolean paramBoolean)
    throws HeadlessException
  {
    this(paramString, paramBoolean, paramCheckboxGroup);
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 6
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 135	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 263	java/lang/StringBuilder:<init>	()V
    //   12: ldc 4
    //   14: invokevirtual 267	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 220	java/awt/Checkbox:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 220	java/awt/Checkbox:nameCounter	I
    //   26: invokevirtual 265	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 264	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	Checkbox
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
        peer = getToolkit().createCheckbox(this);
      }
      super.addNotify();
    }
  }
  
  public String getLabel()
  {
    return label;
  }
  
  public void setLabel(String paramString)
  {
    int i = 0;
    synchronized (this)
    {
      if ((paramString != label) && ((label == null) || (!label.equals(paramString))))
      {
        label = paramString;
        CheckboxPeer localCheckboxPeer = (CheckboxPeer)peer;
        if (localCheckboxPeer != null) {
          localCheckboxPeer.setLabel(paramString);
        }
        i = 1;
      }
    }
    if (i != 0) {
      invalidateIfValid();
    }
  }
  
  public boolean getState()
  {
    return state;
  }
  
  public void setState(boolean paramBoolean)
  {
    CheckboxGroup localCheckboxGroup = group;
    if (localCheckboxGroup != null) {
      if (paramBoolean) {
        localCheckboxGroup.setSelectedCheckbox(this);
      } else if (localCheckboxGroup.getSelectedCheckbox() == this) {
        paramBoolean = true;
      }
    }
    setStateInternal(paramBoolean);
  }
  
  public Object[] getSelectedObjects()
  {
    if (state)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = label;
      return arrayOfObject;
    }
    return null;
  }
  
  public CheckboxGroup getCheckboxGroup()
  {
    return group;
  }
  
  public void setCheckboxGroup(CheckboxGroup paramCheckboxGroup)
  {
    if (group == paramCheckboxGroup) {
      return;
    }
    CheckboxGroup localCheckboxGroup;
    boolean bool;
    synchronized (this)
    {
      localCheckboxGroup = group;
      bool = getState();
      group = paramCheckboxGroup;
      CheckboxPeer localCheckboxPeer = (CheckboxPeer)peer;
      if (localCheckboxPeer != null) {
        localCheckboxPeer.setCheckboxGroup(paramCheckboxGroup);
      }
      if ((group != null) && (getState())) {
        if (group.getSelectedCheckbox() != null) {
          setState(false);
        } else {
          group.setSelectedCheckbox(this);
        }
      }
    }
    if ((localCheckboxGroup != null) && (bool)) {
      localCheckboxGroup.setSelectedCheckbox(null);
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
    String str1 = super.paramString();
    String str2 = label;
    if (str2 != null) {
      str1 = str1 + ",label=" + str2;
    }
    return str1 + ",state=" + state;
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
      accessibleContext = new AccessibleAWTCheckbox();
    }
    return accessibleContext;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
  
  protected class AccessibleAWTCheckbox
    extends Component.AccessibleAWTComponent
    implements ItemListener, AccessibleAction, AccessibleValue
  {
    private static final long serialVersionUID = 7881579233144754107L;
    
    public AccessibleAWTCheckbox()
    {
      super();
      addItemListener(this);
    }
    
    public void itemStateChanged(ItemEvent paramItemEvent)
    {
      Checkbox localCheckbox = (Checkbox)paramItemEvent.getSource();
      if (accessibleContext != null) {
        if (localCheckbox.getState()) {
          accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.CHECKED);
        } else {
          accessibleContext.firePropertyChange("AccessibleState", AccessibleState.CHECKED, null);
        }
      }
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
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (getState()) {
        localAccessibleStateSet.add(AccessibleState.CHECKED);
      }
      return localAccessibleStateSet;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Checkbox.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */