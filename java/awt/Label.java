package java.awt;

import java.awt.peer.LabelPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Label
  extends Component
  implements Accessible
{
  public static final int LEFT = 0;
  public static final int CENTER = 1;
  public static final int RIGHT = 2;
  String text;
  int alignment = 0;
  private static final String base = "label";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 3094126758329070636L;
  
  public Label()
    throws HeadlessException
  {
    this("", 0);
  }
  
  public Label(String paramString)
    throws HeadlessException
  {
    this(paramString, 0);
  }
  
  public Label(String paramString, int paramInt)
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    text = paramString;
    setAlignment(paramInt);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 9
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 101	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 161	java/lang/StringBuilder:<init>	()V
    //   12: ldc 6
    //   14: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 140	java/awt/Label:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 140	java/awt/Label:nameCounter	I
    //   26: invokevirtual 163	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	Label
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
        peer = getToolkit().createLabel(this);
      }
      super.addNotify();
    }
  }
  
  public int getAlignment()
  {
    return alignment;
  }
  
  public synchronized void setAlignment(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
      alignment = paramInt;
      LabelPeer localLabelPeer = (LabelPeer)peer;
      if (localLabelPeer != null) {
        localLabelPeer.setAlignment(paramInt);
      }
      return;
    }
    throw new IllegalArgumentException("improper alignment: " + paramInt);
  }
  
  public String getText()
  {
    return text;
  }
  
  public void setText(String paramString)
  {
    int i = 0;
    synchronized (this)
    {
      if ((paramString != text) && ((text == null) || (!text.equals(paramString))))
      {
        text = paramString;
        LabelPeer localLabelPeer = (LabelPeer)peer;
        if (localLabelPeer != null) {
          localLabelPeer.setText(paramString);
        }
        i = 1;
      }
    }
    if (i != 0) {
      invalidateIfValid();
    }
  }
  
  protected String paramString()
  {
    String str = "";
    switch (alignment)
    {
    case 0: 
      str = "left";
      break;
    case 1: 
      str = "center";
      break;
    case 2: 
      str = "right";
    }
    return super.paramString() + ",align=" + str + ",text=" + text;
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTLabel();
    }
    return accessibleContext;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
  
  protected class AccessibleAWTLabel
    extends Component.AccessibleAWTComponent
  {
    private static final long serialVersionUID = -3568967560160480438L;
    
    public AccessibleAWTLabel()
    {
      super();
    }
    
    public String getAccessibleName()
    {
      if (accessibleName != null) {
        return accessibleName;
      }
      if (getText() == null) {
        return super.getAccessibleName();
      }
      return getText();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.LABEL;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Label.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */