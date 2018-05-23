package java.awt;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Panel
  extends Container
  implements Accessible
{
  private static final String base = "panel";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -2728009084054400034L;
  
  public Panel()
  {
    this(new FlowLayout());
  }
  
  public Panel(LayoutManager paramLayoutManager)
  {
    setLayout(paramLayoutManager);
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 2
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 46	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 84	java/lang/StringBuilder:<init>	()V
    //   12: ldc 1
    //   14: invokevirtual 87	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 72	java/awt/Panel:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 72	java/awt/Panel:nameCounter	I
    //   26: invokevirtual 86	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 85	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	Panel
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
        peer = getToolkit().createPanel(this);
      }
      super.addNotify();
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTPanel();
    }
    return accessibleContext;
  }
  
  protected class AccessibleAWTPanel
    extends Container.AccessibleAWTContainer
  {
    private static final long serialVersionUID = -6409552226660031050L;
    
    protected AccessibleAWTPanel()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PANEL;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Panel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */