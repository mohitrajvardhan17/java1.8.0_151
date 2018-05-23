package java.awt;

import java.awt.image.BufferStrategy;
import java.awt.peer.CanvasPeer;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Canvas
  extends Component
  implements Accessible
{
  private static final String base = "canvas";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -2284879212465893870L;
  
  public Canvas() {}
  
  public Canvas(GraphicsConfiguration paramGraphicsConfiguration)
  {
    this();
    setGraphicsConfiguration(paramGraphicsConfiguration);
  }
  
  void setGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration)
  {
    synchronized (getTreeLock())
    {
      CanvasPeer localCanvasPeer = (CanvasPeer)getPeer();
      if (localCanvasPeer != null) {
        paramGraphicsConfiguration = localCanvasPeer.getAppropriateGraphicsConfiguration(paramGraphicsConfiguration);
      }
      super.setGraphicsConfiguration(paramGraphicsConfiguration);
    }
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 2
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 66	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 125	java/lang/StringBuilder:<init>	()V
    //   12: ldc 1
    //   14: invokevirtual 128	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 106	java/awt/Canvas:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 106	java/awt/Canvas:nameCounter	I
    //   26: invokevirtual 127	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	Canvas
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
        peer = getToolkit().createCanvas(this);
      }
      super.addNotify();
    }
  }
  
  public void paint(Graphics paramGraphics)
  {
    paramGraphics.clearRect(0, 0, width, height);
  }
  
  public void update(Graphics paramGraphics)
  {
    paramGraphics.clearRect(0, 0, width, height);
    paint(paramGraphics);
  }
  
  boolean postsOldMouseEvents()
  {
    return true;
  }
  
  public void createBufferStrategy(int paramInt)
  {
    super.createBufferStrategy(paramInt);
  }
  
  public void createBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities)
    throws AWTException
  {
    super.createBufferStrategy(paramInt, paramBufferCapabilities);
  }
  
  public BufferStrategy getBufferStrategy()
  {
    return super.getBufferStrategy();
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTCanvas();
    }
    return accessibleContext;
  }
  
  protected class AccessibleAWTCanvas
    extends Component.AccessibleAWTComponent
  {
    private static final long serialVersionUID = -6325592262103146699L;
    
    protected AccessibleAWTCanvas()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.CANVAS;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Canvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */