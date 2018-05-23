package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.peer.ScrollPanePeer;
import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.ScrollPaneWheelScroller;
import sun.awt.SunToolkit;

public class ScrollPane
  extends Container
  implements Accessible
{
  public static final int SCROLLBARS_AS_NEEDED = 0;
  public static final int SCROLLBARS_ALWAYS = 1;
  public static final int SCROLLBARS_NEVER = 2;
  private int scrollbarDisplayPolicy;
  private ScrollPaneAdjustable vAdjustable;
  private ScrollPaneAdjustable hAdjustable;
  private static final String base = "scrollpane";
  private static int nameCounter = 0;
  private static final boolean defaultWheelScroll = true;
  private boolean wheelScrollingEnabled = true;
  private static final long serialVersionUID = 7956609840827222915L;
  
  private static native void initIDs();
  
  public ScrollPane()
    throws HeadlessException
  {
    this(0);
  }
  
  @ConstructorProperties({"scrollbarDisplayPolicy"})
  public ScrollPane(int paramInt)
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    layoutMgr = null;
    width = 100;
    height = 100;
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
      scrollbarDisplayPolicy = paramInt;
      break;
    default: 
      throw new IllegalArgumentException("illegal scrollbar display policy");
    }
    vAdjustable = new ScrollPaneAdjustable(this, new PeerFixer(this), 1);
    hAdjustable = new ScrollPaneAdjustable(this, new PeerFixer(this), 0);
    setWheelScrollingEnabled(true);
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 19
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 207	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 404	java/lang/StringBuilder:<init>	()V
    //   12: ldc 16
    //   14: invokevirtual 408	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 334	java/awt/ScrollPane:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 334	java/awt/ScrollPane:nameCounter	I
    //   26: invokevirtual 406	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 405	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	ScrollPane
    //   3	34	1	Ljava/lang/Object;	Object
    //   35	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   5	34	35	finally
    //   35	38	35	finally
  }
  
  private void addToPanel(Component paramComponent, Object paramObject, int paramInt)
  {
    Panel localPanel = new Panel();
    localPanel.setLayout(new BorderLayout());
    localPanel.add(paramComponent);
    super.addImpl(localPanel, paramObject, paramInt);
    validate();
  }
  
  protected final void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    synchronized (getTreeLock())
    {
      if (getComponentCount() > 0) {
        remove(0);
      }
      if (paramInt > 0) {
        throw new IllegalArgumentException("position greater than 0");
      }
      if (!SunToolkit.isLightweightOrUnknown(paramComponent)) {
        super.addImpl(paramComponent, paramObject, paramInt);
      } else {
        addToPanel(paramComponent, paramObject, paramInt);
      }
    }
  }
  
  public int getScrollbarDisplayPolicy()
  {
    return scrollbarDisplayPolicy;
  }
  
  public Dimension getViewportSize()
  {
    Insets localInsets = getInsets();
    return new Dimension(width - right - left, height - top - bottom);
  }
  
  public int getHScrollbarHeight()
  {
    int i = 0;
    if (scrollbarDisplayPolicy != 2)
    {
      ScrollPanePeer localScrollPanePeer = (ScrollPanePeer)peer;
      if (localScrollPanePeer != null) {
        i = localScrollPanePeer.getHScrollbarHeight();
      }
    }
    return i;
  }
  
  public int getVScrollbarWidth()
  {
    int i = 0;
    if (scrollbarDisplayPolicy != 2)
    {
      ScrollPanePeer localScrollPanePeer = (ScrollPanePeer)peer;
      if (localScrollPanePeer != null) {
        i = localScrollPanePeer.getVScrollbarWidth();
      }
    }
    return i;
  }
  
  public Adjustable getVAdjustable()
  {
    return vAdjustable;
  }
  
  public Adjustable getHAdjustable()
  {
    return hAdjustable;
  }
  
  public void setScrollPosition(int paramInt1, int paramInt2)
  {
    synchronized (getTreeLock())
    {
      if (getComponentCount() == 0) {
        throw new NullPointerException("child is null");
      }
      hAdjustable.setValue(paramInt1);
      vAdjustable.setValue(paramInt2);
    }
  }
  
  public void setScrollPosition(Point paramPoint)
  {
    setScrollPosition(x, y);
  }
  
  @Transient
  public Point getScrollPosition()
  {
    synchronized (getTreeLock())
    {
      if (getComponentCount() == 0) {
        throw new NullPointerException("child is null");
      }
      return new Point(hAdjustable.getValue(), vAdjustable.getValue());
    }
  }
  
  public final void setLayout(LayoutManager paramLayoutManager)
  {
    throw new AWTError("ScrollPane controls layout");
  }
  
  public void doLayout()
  {
    layout();
  }
  
  Dimension calculateChildSize()
  {
    Dimension localDimension1 = getSize();
    Insets localInsets = getInsets();
    int i = width - left * 2;
    int j = height - top * 2;
    Component localComponent = getComponent(0);
    Dimension localDimension2 = new Dimension(localComponent.getPreferredSize());
    int k;
    int m;
    if (scrollbarDisplayPolicy == 0)
    {
      k = height > j ? 1 : 0;
      m = width > i ? 1 : 0;
    }
    else if (scrollbarDisplayPolicy == 1)
    {
      k = m = 1;
    }
    else
    {
      k = m = 0;
    }
    int n = getVScrollbarWidth();
    int i1 = getHScrollbarHeight();
    if (k != 0) {
      i -= n;
    }
    if (m != 0) {
      j -= i1;
    }
    if (width < i) {
      width = i;
    }
    if (height < j) {
      height = j;
    }
    return localDimension2;
  }
  
  @Deprecated
  public void layout()
  {
    if (getComponentCount() == 0) {
      return;
    }
    Component localComponent = getComponent(0);
    Point localPoint = getScrollPosition();
    Dimension localDimension1 = calculateChildSize();
    Dimension localDimension2 = getViewportSize();
    localComponent.reshape(-x, -y, width, height);
    ScrollPanePeer localScrollPanePeer = (ScrollPanePeer)peer;
    if (localScrollPanePeer != null) {
      localScrollPanePeer.childResized(width, height);
    }
    localDimension2 = getViewportSize();
    hAdjustable.setSpan(0, width, width);
    vAdjustable.setSpan(0, height, height);
  }
  
  public void printComponents(Graphics paramGraphics)
  {
    if (getComponentCount() == 0) {
      return;
    }
    Component localComponent = getComponent(0);
    Point localPoint = localComponent.getLocation();
    Dimension localDimension = getViewportSize();
    Insets localInsets = getInsets();
    Graphics localGraphics = paramGraphics.create();
    try
    {
      localGraphics.clipRect(left, top, width, height);
      localGraphics.translate(x, y);
      localComponent.printAll(localGraphics);
    }
    finally
    {
      localGraphics.dispose();
    }
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      int i = 0;
      int j = 0;
      if (getComponentCount() > 0)
      {
        i = vAdjustable.getValue();
        j = hAdjustable.getValue();
        vAdjustable.setValue(0);
        hAdjustable.setValue(0);
      }
      if (peer == null) {
        peer = getToolkit().createScrollPane(this);
      }
      super.addNotify();
      if (getComponentCount() > 0)
      {
        vAdjustable.setValue(i);
        hAdjustable.setValue(j);
      }
    }
  }
  
  public String paramString()
  {
    String str;
    switch (scrollbarDisplayPolicy)
    {
    case 0: 
      str = "as-needed";
      break;
    case 1: 
      str = "always";
      break;
    case 2: 
      str = "never";
      break;
    default: 
      str = "invalid display policy";
    }
    Point localPoint = getComponentCount() > 0 ? getScrollPosition() : new Point(0, 0);
    Insets localInsets = getInsets();
    return super.paramString() + ",ScrollPosition=(" + x + "," + y + "),Insets=(" + top + "," + left + "," + bottom + "," + right + "),ScrollbarDisplayPolicy=" + str + ",wheelScrollingEnabled=" + isWheelScrollingEnabled();
  }
  
  void autoProcessMouseWheel(MouseWheelEvent paramMouseWheelEvent)
  {
    processMouseWheelEvent(paramMouseWheelEvent);
  }
  
  protected void processMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent)
  {
    if (isWheelScrollingEnabled())
    {
      ScrollPaneWheelScroller.handleWheelScrolling(this, paramMouseWheelEvent);
      paramMouseWheelEvent.consume();
    }
    super.processMouseWheelEvent(paramMouseWheelEvent);
  }
  
  protected boolean eventTypeEnabled(int paramInt)
  {
    if ((paramInt == 507) && (isWheelScrollingEnabled())) {
      return true;
    }
    return super.eventTypeEnabled(paramInt);
  }
  
  public void setWheelScrollingEnabled(boolean paramBoolean)
  {
    wheelScrollingEnabled = paramBoolean;
  }
  
  public boolean isWheelScrollingEnabled()
  {
    return wheelScrollingEnabled;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    scrollbarDisplayPolicy = localGetField.get("scrollbarDisplayPolicy", 0);
    hAdjustable = ((ScrollPaneAdjustable)localGetField.get("hAdjustable", null));
    vAdjustable = ((ScrollPaneAdjustable)localGetField.get("vAdjustable", null));
    wheelScrollingEnabled = localGetField.get("wheelScrollingEnabled", true);
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTScrollPane();
    }
    return accessibleContext;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
  
  protected class AccessibleAWTScrollPane
    extends Container.AccessibleAWTContainer
  {
    private static final long serialVersionUID = 6100703663886637L;
    
    protected AccessibleAWTScrollPane()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SCROLL_PANE;
    }
  }
  
  class PeerFixer
    implements AdjustmentListener, Serializable
  {
    private static final long serialVersionUID = 1043664721353696630L;
    private ScrollPane scroller;
    
    PeerFixer(ScrollPane paramScrollPane)
    {
      scroller = paramScrollPane;
    }
    
    public void adjustmentValueChanged(AdjustmentEvent paramAdjustmentEvent)
    {
      Adjustable localAdjustable = paramAdjustmentEvent.getAdjustable();
      int i = paramAdjustmentEvent.getValue();
      ScrollPanePeer localScrollPanePeer = (ScrollPanePeer)scroller.peer;
      if (localScrollPanePeer != null) {
        localScrollPanePeer.setValue(localAdjustable, i);
      }
      Component localComponent = scroller.getComponent(0);
      switch (localAdjustable.getOrientation())
      {
      case 1: 
        localComponent.move(getLocationx, -i);
        break;
      case 0: 
        localComponent.move(-i, getLocationy);
        break;
      default: 
        throw new IllegalArgumentException("Illegal adjustable orientation");
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\ScrollPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */