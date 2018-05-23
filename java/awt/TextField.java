package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.TextFieldPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class TextField
  extends TextComponent
{
  int columns;
  char echoChar;
  transient ActionListener actionListener;
  private static final String base = "textfield";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -2966288784432217853L;
  private int textFieldSerializedDataVersion = 1;
  
  private static native void initIDs();
  
  public TextField()
    throws HeadlessException
  {
    this("", 0);
  }
  
  public TextField(String paramString)
    throws HeadlessException
  {
    this(paramString, paramString != null ? paramString.length() : 0);
  }
  
  public TextField(int paramInt)
    throws HeadlessException
  {
    this("", paramInt);
  }
  
  public TextField(String paramString, int paramInt)
    throws HeadlessException
  {
    super(paramString);
    columns = (paramInt >= 0 ? paramInt : 0);
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 6
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 138	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 267	java/lang/StringBuilder:<init>	()V
    //   12: ldc 5
    //   14: invokevirtual 271	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 222	java/awt/TextField:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 222	java/awt/TextField:nameCounter	I
    //   26: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 268	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	TextField
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
        peer = getToolkit().createTextField(this);
      }
      super.addNotify();
    }
  }
  
  public char getEchoChar()
  {
    return echoChar;
  }
  
  public void setEchoChar(char paramChar)
  {
    setEchoCharacter(paramChar);
  }
  
  @Deprecated
  public synchronized void setEchoCharacter(char paramChar)
  {
    if (echoChar != paramChar)
    {
      echoChar = paramChar;
      TextFieldPeer localTextFieldPeer = (TextFieldPeer)peer;
      if (localTextFieldPeer != null) {
        localTextFieldPeer.setEchoChar(paramChar);
      }
    }
  }
  
  public void setText(String paramString)
  {
    super.setText(paramString);
    invalidateIfValid();
  }
  
  public boolean echoCharIsSet()
  {
    return echoChar != 0;
  }
  
  public int getColumns()
  {
    return columns;
  }
  
  public void setColumns(int paramInt)
  {
    int i;
    synchronized (this)
    {
      i = columns;
      if (paramInt < 0) {
        throw new IllegalArgumentException("columns less than zero.");
      }
      if (paramInt != i) {
        columns = paramInt;
      }
    }
    if (paramInt != i) {
      invalidate();
    }
  }
  
  public Dimension getPreferredSize(int paramInt)
  {
    return preferredSize(paramInt);
  }
  
  @Deprecated
  public Dimension preferredSize(int paramInt)
  {
    synchronized (getTreeLock())
    {
      TextFieldPeer localTextFieldPeer = (TextFieldPeer)peer;
      return localTextFieldPeer != null ? localTextFieldPeer.getPreferredSize(paramInt) : super.preferredSize();
    }
  }
  
  public Dimension getPreferredSize()
  {
    return preferredSize();
  }
  
  @Deprecated
  public Dimension preferredSize()
  {
    synchronized (getTreeLock())
    {
      return columns > 0 ? preferredSize(columns) : super.preferredSize();
    }
  }
  
  public Dimension getMinimumSize(int paramInt)
  {
    return minimumSize(paramInt);
  }
  
  @Deprecated
  public Dimension minimumSize(int paramInt)
  {
    synchronized (getTreeLock())
    {
      TextFieldPeer localTextFieldPeer = (TextFieldPeer)peer;
      return localTextFieldPeer != null ? localTextFieldPeer.getMinimumSize(paramInt) : super.minimumSize();
    }
  }
  
  public Dimension getMinimumSize()
  {
    return minimumSize();
  }
  
  @Deprecated
  public Dimension minimumSize()
  {
    synchronized (getTreeLock())
    {
      return columns > 0 ? minimumSize(columns) : super.minimumSize();
    }
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
    String str = super.paramString();
    if (echoChar != 0) {
      str = str + ",echo=" + echoChar;
    }
    return str;
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
    if (columns < 0) {
      columns = 0;
    }
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
      accessibleContext = new AccessibleAWTTextField();
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
  
  protected class AccessibleAWTTextField
    extends TextComponent.AccessibleAWTTextComponent
  {
    private static final long serialVersionUID = 6219164359235943158L;
    
    protected AccessibleAWTTextField()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      localAccessibleStateSet.add(AccessibleState.SINGLE_LINE);
      return localAccessibleStateSet;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\TextField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */