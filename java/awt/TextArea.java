package java.awt;

import java.awt.peer.TextAreaPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class TextArea
  extends TextComponent
{
  int rows;
  int columns;
  private static final String base = "text";
  private static int nameCounter = 0;
  public static final int SCROLLBARS_BOTH = 0;
  public static final int SCROLLBARS_VERTICAL_ONLY = 1;
  public static final int SCROLLBARS_HORIZONTAL_ONLY = 2;
  public static final int SCROLLBARS_NONE = 3;
  private int scrollbarVisibility;
  private static Set<AWTKeyStroke> forwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet("ctrl TAB", new HashSet());
  private static Set<AWTKeyStroke> backwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet("ctrl shift TAB", new HashSet());
  private static final long serialVersionUID = 3692302836626095722L;
  private int textAreaSerializedDataVersion = 2;
  
  private static native void initIDs();
  
  public TextArea()
    throws HeadlessException
  {
    this("", 0, 0, 0);
  }
  
  public TextArea(String paramString)
    throws HeadlessException
  {
    this(paramString, 0, 0, 0);
  }
  
  public TextArea(int paramInt1, int paramInt2)
    throws HeadlessException
  {
    this("", paramInt1, paramInt2, 0);
  }
  
  public TextArea(String paramString, int paramInt1, int paramInt2)
    throws HeadlessException
  {
    this(paramString, paramInt1, paramInt2, 0);
  }
  
  public TextArea(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws HeadlessException
  {
    super(paramString);
    rows = (paramInt1 >= 0 ? paramInt1 : 0);
    columns = (paramInt2 >= 0 ? paramInt2 : 0);
    if ((paramInt3 >= 0) && (paramInt3 <= 3)) {
      scrollbarVisibility = paramInt3;
    } else {
      scrollbarVisibility = 0;
    }
    setFocusTraversalKeys(0, forwardTraversalKeys);
    setFocusTraversalKeys(1, backwardTraversalKeys);
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 15
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 141	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 245	java/lang/StringBuilder:<init>	()V
    //   12: ldc 13
    //   14: invokevirtual 248	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 207	java/awt/TextArea:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 207	java/awt/TextArea:nameCounter	I
    //   26: invokevirtual 247	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 246	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	TextArea
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
        peer = getToolkit().createTextArea(this);
      }
      super.addNotify();
    }
  }
  
  public void insert(String paramString, int paramInt)
  {
    insertText(paramString, paramInt);
  }
  
  @Deprecated
  public synchronized void insertText(String paramString, int paramInt)
  {
    TextAreaPeer localTextAreaPeer = (TextAreaPeer)peer;
    if (localTextAreaPeer != null) {
      localTextAreaPeer.insert(paramString, paramInt);
    } else {
      text = (text.substring(0, paramInt) + paramString + text.substring(paramInt));
    }
  }
  
  public void append(String paramString)
  {
    appendText(paramString);
  }
  
  @Deprecated
  public synchronized void appendText(String paramString)
  {
    if (peer != null) {
      insertText(paramString, getText().length());
    } else {
      text += paramString;
    }
  }
  
  public void replaceRange(String paramString, int paramInt1, int paramInt2)
  {
    replaceText(paramString, paramInt1, paramInt2);
  }
  
  @Deprecated
  public synchronized void replaceText(String paramString, int paramInt1, int paramInt2)
  {
    TextAreaPeer localTextAreaPeer = (TextAreaPeer)peer;
    if (localTextAreaPeer != null) {
      localTextAreaPeer.replaceRange(paramString, paramInt1, paramInt2);
    } else {
      text = (text.substring(0, paramInt1) + paramString + text.substring(paramInt2));
    }
  }
  
  public int getRows()
  {
    return rows;
  }
  
  public void setRows(int paramInt)
  {
    int i = rows;
    if (paramInt < 0) {
      throw new IllegalArgumentException("rows less than zero.");
    }
    if (paramInt != i)
    {
      rows = paramInt;
      invalidate();
    }
  }
  
  public int getColumns()
  {
    return columns;
  }
  
  public void setColumns(int paramInt)
  {
    int i = columns;
    if (paramInt < 0) {
      throw new IllegalArgumentException("columns less than zero.");
    }
    if (paramInt != i)
    {
      columns = paramInt;
      invalidate();
    }
  }
  
  public int getScrollbarVisibility()
  {
    return scrollbarVisibility;
  }
  
  public Dimension getPreferredSize(int paramInt1, int paramInt2)
  {
    return preferredSize(paramInt1, paramInt2);
  }
  
  @Deprecated
  public Dimension preferredSize(int paramInt1, int paramInt2)
  {
    synchronized (getTreeLock())
    {
      TextAreaPeer localTextAreaPeer = (TextAreaPeer)peer;
      return localTextAreaPeer != null ? localTextAreaPeer.getPreferredSize(paramInt1, paramInt2) : super.preferredSize();
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
      return (rows > 0) && (columns > 0) ? preferredSize(rows, columns) : super.preferredSize();
    }
  }
  
  public Dimension getMinimumSize(int paramInt1, int paramInt2)
  {
    return minimumSize(paramInt1, paramInt2);
  }
  
  @Deprecated
  public Dimension minimumSize(int paramInt1, int paramInt2)
  {
    synchronized (getTreeLock())
    {
      TextAreaPeer localTextAreaPeer = (TextAreaPeer)peer;
      return localTextAreaPeer != null ? localTextAreaPeer.getMinimumSize(paramInt1, paramInt2) : super.minimumSize();
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
      return (rows > 0) && (columns > 0) ? minimumSize(rows, columns) : super.minimumSize();
    }
  }
  
  protected String paramString()
  {
    String str;
    switch (scrollbarVisibility)
    {
    case 0: 
      str = "both";
      break;
    case 1: 
      str = "vertical-only";
      break;
    case 2: 
      str = "horizontal-only";
      break;
    case 3: 
      str = "none";
      break;
    default: 
      str = "invalid display policy";
    }
    return super.paramString() + ",rows=" + rows + ",columns=" + columns + ",scrollbarVisibility=" + str;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    paramObjectInputStream.defaultReadObject();
    if (columns < 0) {
      columns = 0;
    }
    if (rows < 0) {
      rows = 0;
    }
    if ((scrollbarVisibility < 0) || (scrollbarVisibility > 3)) {
      scrollbarVisibility = 0;
    }
    if (textAreaSerializedDataVersion < 2)
    {
      setFocusTraversalKeys(0, forwardTraversalKeys);
      setFocusTraversalKeys(1, backwardTraversalKeys);
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTTextArea();
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
  
  protected class AccessibleAWTTextArea
    extends TextComponent.AccessibleAWTTextComponent
  {
    private static final long serialVersionUID = 3472827823632144419L;
    
    protected AccessibleAWTTextArea()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      localAccessibleStateSet.add(AccessibleState.MULTI_LINE);
      return localAccessibleStateSet;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\TextArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */