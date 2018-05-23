package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollbarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;

public class Scrollbar
  extends Component
  implements Adjustable, Accessible
{
  public static final int HORIZONTAL = 0;
  public static final int VERTICAL = 1;
  int value;
  int maximum;
  int minimum;
  int visibleAmount;
  int orientation;
  int lineIncrement = 1;
  int pageIncrement = 10;
  transient boolean isAdjusting;
  transient AdjustmentListener adjustmentListener;
  private static final String base = "scrollbar";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 8451667562882310543L;
  private int scrollbarSerializedDataVersion = 1;
  
  private static native void initIDs();
  
  public Scrollbar()
    throws HeadlessException
  {
    this(1, 0, 10, 0, 100);
  }
  
  public Scrollbar(int paramInt)
    throws HeadlessException
  {
    this(paramInt, 0, 10, 0, 100);
  }
  
  public Scrollbar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    switch (paramInt1)
    {
    case 0: 
    case 1: 
      orientation = paramInt1;
      break;
    default: 
      throw new IllegalArgumentException("illegal scrollbar orientation");
    }
    setValues(paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 17
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 187	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 336	java/lang/StringBuilder:<init>	()V
    //   12: ldc 16
    //   14: invokevirtual 340	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 283	java/awt/Scrollbar:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 283	java/awt/Scrollbar:nameCounter	I
    //   26: invokevirtual 338	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 337	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	Scrollbar
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
        peer = getToolkit().createScrollbar(this);
      }
      super.addNotify();
    }
  }
  
  public int getOrientation()
  {
    return orientation;
  }
  
  public void setOrientation(int paramInt)
  {
    synchronized (getTreeLock())
    {
      if (paramInt == orientation) {
        return;
      }
      switch (paramInt)
      {
      case 0: 
      case 1: 
        orientation = paramInt;
        break;
      default: 
        throw new IllegalArgumentException("illegal scrollbar orientation");
      }
      if (peer != null)
      {
        removeNotify();
        addNotify();
        invalidate();
      }
    }
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleState", paramInt == 1 ? AccessibleState.HORIZONTAL : AccessibleState.VERTICAL, paramInt == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
    }
  }
  
  public int getValue()
  {
    return value;
  }
  
  public void setValue(int paramInt)
  {
    setValues(paramInt, visibleAmount, minimum, maximum);
  }
  
  public int getMinimum()
  {
    return minimum;
  }
  
  public void setMinimum(int paramInt)
  {
    setValues(value, visibleAmount, paramInt, maximum);
  }
  
  public int getMaximum()
  {
    return maximum;
  }
  
  public void setMaximum(int paramInt)
  {
    if (paramInt == Integer.MIN_VALUE) {
      paramInt = -2147483647;
    }
    if (minimum >= paramInt) {
      minimum = (paramInt - 1);
    }
    setValues(value, visibleAmount, minimum, paramInt);
  }
  
  public int getVisibleAmount()
  {
    return getVisible();
  }
  
  @Deprecated
  public int getVisible()
  {
    return visibleAmount;
  }
  
  public void setVisibleAmount(int paramInt)
  {
    setValues(value, paramInt, minimum, maximum);
  }
  
  public void setUnitIncrement(int paramInt)
  {
    setLineIncrement(paramInt);
  }
  
  @Deprecated
  public synchronized void setLineIncrement(int paramInt)
  {
    int i = paramInt < 1 ? 1 : paramInt;
    if (lineIncrement == i) {
      return;
    }
    lineIncrement = i;
    ScrollbarPeer localScrollbarPeer = (ScrollbarPeer)peer;
    if (localScrollbarPeer != null) {
      localScrollbarPeer.setLineIncrement(lineIncrement);
    }
  }
  
  public int getUnitIncrement()
  {
    return getLineIncrement();
  }
  
  @Deprecated
  public int getLineIncrement()
  {
    return lineIncrement;
  }
  
  public void setBlockIncrement(int paramInt)
  {
    setPageIncrement(paramInt);
  }
  
  @Deprecated
  public synchronized void setPageIncrement(int paramInt)
  {
    int i = paramInt < 1 ? 1 : paramInt;
    if (pageIncrement == i) {
      return;
    }
    pageIncrement = i;
    ScrollbarPeer localScrollbarPeer = (ScrollbarPeer)peer;
    if (localScrollbarPeer != null) {
      localScrollbarPeer.setPageIncrement(pageIncrement);
    }
  }
  
  public int getBlockIncrement()
  {
    return getPageIncrement();
  }
  
  @Deprecated
  public int getPageIncrement()
  {
    return pageIncrement;
  }
  
  public void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    synchronized (this)
    {
      if (paramInt3 == Integer.MAX_VALUE) {
        paramInt3 = 2147483646;
      }
      if (paramInt4 <= paramInt3) {
        paramInt4 = paramInt3 + 1;
      }
      long l = paramInt4 - paramInt3;
      if (l > 2147483647L)
      {
        l = 2147483647L;
        paramInt4 = paramInt3 + (int)l;
      }
      if (paramInt2 > (int)l) {
        paramInt2 = (int)l;
      }
      if (paramInt2 < 1) {
        paramInt2 = 1;
      }
      if (paramInt1 < paramInt3) {
        paramInt1 = paramInt3;
      }
      if (paramInt1 > paramInt4 - paramInt2) {
        paramInt1 = paramInt4 - paramInt2;
      }
      i = value;
      value = paramInt1;
      visibleAmount = paramInt2;
      minimum = paramInt3;
      maximum = paramInt4;
      ScrollbarPeer localScrollbarPeer = (ScrollbarPeer)peer;
      if (localScrollbarPeer != null) {
        localScrollbarPeer.setValues(paramInt1, visibleAmount, paramInt3, paramInt4);
      }
    }
    if ((i != paramInt1) && (accessibleContext != null)) {
      accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(paramInt1));
    }
  }
  
  public boolean getValueIsAdjusting()
  {
    return isAdjusting;
  }
  
  public void setValueIsAdjusting(boolean paramBoolean)
  {
    boolean bool;
    synchronized (this)
    {
      bool = isAdjusting;
      isAdjusting = paramBoolean;
    }
    if ((bool != paramBoolean) && (accessibleContext != null)) {
      accessibleContext.firePropertyChange("AccessibleState", bool ? AccessibleState.BUSY : null, paramBoolean ? AccessibleState.BUSY : null);
    }
  }
  
  public synchronized void addAdjustmentListener(AdjustmentListener paramAdjustmentListener)
  {
    if (paramAdjustmentListener == null) {
      return;
    }
    adjustmentListener = AWTEventMulticaster.add(adjustmentListener, paramAdjustmentListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeAdjustmentListener(AdjustmentListener paramAdjustmentListener)
  {
    if (paramAdjustmentListener == null) {
      return;
    }
    adjustmentListener = AWTEventMulticaster.remove(adjustmentListener, paramAdjustmentListener);
  }
  
  public synchronized AdjustmentListener[] getAdjustmentListeners()
  {
    return (AdjustmentListener[])getListeners(AdjustmentListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    AdjustmentListener localAdjustmentListener = null;
    if (paramClass == AdjustmentListener.class) {
      localAdjustmentListener = adjustmentListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners(localAdjustmentListener, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    if (id == 601) {
      return ((eventMask & 0x100) != 0L) || (adjustmentListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof AdjustmentEvent))
    {
      processAdjustmentEvent((AdjustmentEvent)paramAWTEvent);
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processAdjustmentEvent(AdjustmentEvent paramAdjustmentEvent)
  {
    AdjustmentListener localAdjustmentListener = adjustmentListener;
    if (localAdjustmentListener != null) {
      localAdjustmentListener.adjustmentValueChanged(paramAdjustmentEvent);
    }
  }
  
  protected String paramString()
  {
    return super.paramString() + ",val=" + value + ",vis=" + visibleAmount + ",min=" + minimum + ",max=" + maximum + (orientation == 1 ? ",vert" : ",horz") + ",isAdjusting=" + isAdjusting;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "adjustmentL", adjustmentListener);
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
      if ("adjustmentL" == str) {
        addAdjustmentListener((AdjustmentListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTScrollBar();
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
  
  protected class AccessibleAWTScrollBar
    extends Component.AccessibleAWTComponent
    implements AccessibleValue
  {
    private static final long serialVersionUID = -344337268523697807L;
    
    protected AccessibleAWTScrollBar()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (getValueIsAdjusting()) {
        localAccessibleStateSet.add(AccessibleState.BUSY);
      }
      if (getOrientation() == 1) {
        localAccessibleStateSet.add(AccessibleState.VERTICAL);
      } else {
        localAccessibleStateSet.add(AccessibleState.HORIZONTAL);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SCROLL_BAR;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }
    
    public Number getCurrentAccessibleValue()
    {
      return Integer.valueOf(getValue());
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      if ((paramNumber instanceof Integer))
      {
        setValue(paramNumber.intValue());
        return true;
      }
      return false;
    }
    
    public Number getMinimumAccessibleValue()
    {
      return Integer.valueOf(getMinimum());
    }
    
    public Number getMaximumAccessibleValue()
    {
      return Integer.valueOf(getMaximum());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Scrollbar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */