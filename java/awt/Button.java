package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ButtonPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;

public class Button
  extends Component
  implements Accessible
{
  String label;
  String actionCommand;
  transient ActionListener actionListener;
  private static final String base = "button";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -8774683716313001058L;
  private int buttonSerializedDataVersion = 1;
  
  private static native void initIDs();
  
  public Button()
    throws HeadlessException
  {
    this("");
  }
  
  public Button(String paramString)
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    label = paramString;
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 5
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 120	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   12: ldc 4
    //   14: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 189	java/awt/Button:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 189	java/awt/Button:nameCounter	I
    //   26: invokevirtual 228	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 227	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	Button
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
        peer = getToolkit().createButton(this);
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
        ButtonPeer localButtonPeer = (ButtonPeer)peer;
        if (localButtonPeer != null) {
          localButtonPeer.setLabel(paramString);
        }
        i = 1;
      }
    }
    if (i != 0) {
      invalidateIfValid();
    }
  }
  
  public void setActionCommand(String paramString)
  {
    actionCommand = paramString;
  }
  
  public String getActionCommand()
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
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners(localActionListener, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    if (id == 1001) {
      return ((eventMask & 0x80) != 0L) || (actionListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof ActionEvent))
    {
      processActionEvent((ActionEvent)paramAWTEvent);
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processActionEvent(ActionEvent paramActionEvent)
  {
    ActionListener localActionListener = actionListener;
    if (localActionListener != null) {
      localActionListener.actionPerformed(paramActionEvent);
    }
  }
  
  protected String paramString()
  {
    return super.paramString() + ",label=" + label;
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
    GraphicsEnvironment.checkHeadless();
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
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTButton();
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
  
  protected class AccessibleAWTButton
    extends Component.AccessibleAWTComponent
    implements AccessibleAction, AccessibleValue
  {
    private static final long serialVersionUID = -5932203980244017102L;
    
    protected AccessibleAWTButton()
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
        Toolkit.getEventQueue().postEvent(new ActionEvent(Button.this, 1001, getActionCommand()));
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
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PUSH_BUTTON;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Button.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */