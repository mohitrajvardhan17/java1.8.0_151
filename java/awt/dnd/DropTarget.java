package java.awt.dnd;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.dnd.peer.DropTargetPeer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TooManyListenersException;
import javax.swing.Timer;

public class DropTarget
  implements DropTargetListener, Serializable
{
  private static final long serialVersionUID = -6283860791671019047L;
  private DropTargetContext dropTargetContext = createDropTargetContext();
  private Component component;
  private transient ComponentPeer componentPeer;
  private transient ComponentPeer nativePeer;
  int actions = 3;
  boolean active = true;
  private transient DropTargetAutoScroller autoScroller;
  private transient DropTargetListener dtListener;
  private transient FlavorMap flavorMap;
  private transient boolean isDraggingInside;
  
  public DropTarget(Component paramComponent, int paramInt, DropTargetListener paramDropTargetListener, boolean paramBoolean, FlavorMap paramFlavorMap)
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    component = paramComponent;
    setDefaultActions(paramInt);
    if (paramDropTargetListener != null) {
      try
      {
        addDropTargetListener(paramDropTargetListener);
      }
      catch (TooManyListenersException localTooManyListenersException) {}
    }
    if (paramComponent != null)
    {
      paramComponent.setDropTarget(this);
      setActive(paramBoolean);
    }
    if (paramFlavorMap != null) {
      flavorMap = paramFlavorMap;
    } else {
      flavorMap = SystemFlavorMap.getDefaultFlavorMap();
    }
  }
  
  public DropTarget(Component paramComponent, int paramInt, DropTargetListener paramDropTargetListener, boolean paramBoolean)
    throws HeadlessException
  {
    this(paramComponent, paramInt, paramDropTargetListener, paramBoolean, null);
  }
  
  public DropTarget()
    throws HeadlessException
  {
    this(null, 3, null, true, null);
  }
  
  public DropTarget(Component paramComponent, DropTargetListener paramDropTargetListener)
    throws HeadlessException
  {
    this(paramComponent, 3, paramDropTargetListener, true, null);
  }
  
  public DropTarget(Component paramComponent, int paramInt, DropTargetListener paramDropTargetListener)
    throws HeadlessException
  {
    this(paramComponent, paramInt, paramDropTargetListener, true);
  }
  
  public synchronized void setComponent(Component paramComponent)
  {
    if ((component == paramComponent) || ((component != null) && (component.equals(paramComponent)))) {
      return;
    }
    ComponentPeer localComponentPeer = null;
    Component localComponent;
    if ((localComponent = component) != null)
    {
      clearAutoscroll();
      component = null;
      if (componentPeer != null)
      {
        localComponentPeer = componentPeer;
        removeNotify(componentPeer);
      }
      localComponent.setDropTarget(null);
    }
    if ((component = paramComponent) != null) {
      try
      {
        paramComponent.setDropTarget(this);
      }
      catch (Exception localException)
      {
        if (localComponent != null)
        {
          localComponent.setDropTarget(this);
          addNotify(localComponentPeer);
        }
      }
    }
  }
  
  public synchronized Component getComponent()
  {
    return component;
  }
  
  public void setDefaultActions(int paramInt)
  {
    getDropTargetContext().setTargetActions(paramInt & 0x40000003);
  }
  
  void doSetDefaultActions(int paramInt)
  {
    actions = paramInt;
  }
  
  public int getDefaultActions()
  {
    return actions;
  }
  
  public synchronized void setActive(boolean paramBoolean)
  {
    if (paramBoolean != active) {
      active = paramBoolean;
    }
    if (!active) {
      clearAutoscroll();
    }
  }
  
  public boolean isActive()
  {
    return active;
  }
  
  public synchronized void addDropTargetListener(DropTargetListener paramDropTargetListener)
    throws TooManyListenersException
  {
    if (paramDropTargetListener == null) {
      return;
    }
    if (equals(paramDropTargetListener)) {
      throw new IllegalArgumentException("DropTarget may not be its own Listener");
    }
    if (dtListener == null) {
      dtListener = paramDropTargetListener;
    } else {
      throw new TooManyListenersException();
    }
  }
  
  public synchronized void removeDropTargetListener(DropTargetListener paramDropTargetListener)
  {
    if ((paramDropTargetListener != null) && (dtListener != null)) {
      if (dtListener.equals(paramDropTargetListener)) {
        dtListener = null;
      } else {
        throw new IllegalArgumentException("listener mismatch");
      }
    }
  }
  
  public synchronized void dragEnter(DropTargetDragEvent paramDropTargetDragEvent)
  {
    isDraggingInside = true;
    if (!active) {
      return;
    }
    if (dtListener != null) {
      dtListener.dragEnter(paramDropTargetDragEvent);
    } else {
      paramDropTargetDragEvent.getDropTargetContext().setTargetActions(0);
    }
    initializeAutoscrolling(paramDropTargetDragEvent.getLocation());
  }
  
  public synchronized void dragOver(DropTargetDragEvent paramDropTargetDragEvent)
  {
    if (!active) {
      return;
    }
    if ((dtListener != null) && (active)) {
      dtListener.dragOver(paramDropTargetDragEvent);
    }
    updateAutoscroll(paramDropTargetDragEvent.getLocation());
  }
  
  public synchronized void dropActionChanged(DropTargetDragEvent paramDropTargetDragEvent)
  {
    if (!active) {
      return;
    }
    if (dtListener != null) {
      dtListener.dropActionChanged(paramDropTargetDragEvent);
    }
    updateAutoscroll(paramDropTargetDragEvent.getLocation());
  }
  
  public synchronized void dragExit(DropTargetEvent paramDropTargetEvent)
  {
    isDraggingInside = false;
    if (!active) {
      return;
    }
    if ((dtListener != null) && (active)) {
      dtListener.dragExit(paramDropTargetEvent);
    }
    clearAutoscroll();
  }
  
  public synchronized void drop(DropTargetDropEvent paramDropTargetDropEvent)
  {
    isDraggingInside = false;
    clearAutoscroll();
    if ((dtListener != null) && (active)) {
      dtListener.drop(paramDropTargetDropEvent);
    } else {
      paramDropTargetDropEvent.rejectDrop();
    }
  }
  
  public FlavorMap getFlavorMap()
  {
    return flavorMap;
  }
  
  public void setFlavorMap(FlavorMap paramFlavorMap)
  {
    flavorMap = (paramFlavorMap == null ? SystemFlavorMap.getDefaultFlavorMap() : paramFlavorMap);
  }
  
  public void addNotify(ComponentPeer paramComponentPeer)
  {
    if (paramComponentPeer == componentPeer) {
      return;
    }
    componentPeer = paramComponentPeer;
    for (Object localObject = component; (localObject != null) && ((paramComponentPeer instanceof LightweightPeer)); localObject = ((Component)localObject).getParent()) {
      paramComponentPeer = ((Component)localObject).getPeer();
    }
    if ((paramComponentPeer instanceof DropTargetPeer))
    {
      nativePeer = paramComponentPeer;
      ((DropTargetPeer)paramComponentPeer).addDropTarget(this);
    }
    else
    {
      nativePeer = null;
    }
  }
  
  public void removeNotify(ComponentPeer paramComponentPeer)
  {
    if (nativePeer != null) {
      ((DropTargetPeer)nativePeer).removeDropTarget(this);
    }
    componentPeer = (nativePeer = null);
    synchronized (this)
    {
      if (isDraggingInside) {
        dragExit(new DropTargetEvent(getDropTargetContext()));
      }
    }
  }
  
  public DropTargetContext getDropTargetContext()
  {
    return dropTargetContext;
  }
  
  protected DropTargetContext createDropTargetContext()
  {
    return new DropTargetContext(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(SerializationTester.test(dtListener) ? dtListener : null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    try
    {
      dropTargetContext = ((DropTargetContext)localGetField.get("dropTargetContext", null));
    }
    catch (IllegalArgumentException localIllegalArgumentException1) {}
    if (dropTargetContext == null) {
      dropTargetContext = createDropTargetContext();
    }
    component = ((Component)localGetField.get("component", null));
    actions = localGetField.get("actions", 3);
    active = localGetField.get("active", true);
    try
    {
      dtListener = ((DropTargetListener)localGetField.get("dtListener", null));
    }
    catch (IllegalArgumentException localIllegalArgumentException2)
    {
      dtListener = ((DropTargetListener)paramObjectInputStream.readObject());
    }
  }
  
  protected DropTargetAutoScroller createDropTargetAutoScroller(Component paramComponent, Point paramPoint)
  {
    return new DropTargetAutoScroller(paramComponent, paramPoint);
  }
  
  protected void initializeAutoscrolling(Point paramPoint)
  {
    if ((component == null) || (!(component instanceof Autoscroll))) {
      return;
    }
    autoScroller = createDropTargetAutoScroller(component, paramPoint);
  }
  
  protected void updateAutoscroll(Point paramPoint)
  {
    if (autoScroller != null) {
      autoScroller.updateLocation(paramPoint);
    }
  }
  
  protected void clearAutoscroll()
  {
    if (autoScroller != null)
    {
      autoScroller.stop();
      autoScroller = null;
    }
  }
  
  protected static class DropTargetAutoScroller
    implements ActionListener
  {
    private Component component;
    private Autoscroll autoScroll;
    private Timer timer;
    private Point locn;
    private Point prev;
    private Rectangle outer = new Rectangle();
    private Rectangle inner = new Rectangle();
    private int hysteresis = 10;
    
    protected DropTargetAutoScroller(Component paramComponent, Point paramPoint)
    {
      component = paramComponent;
      autoScroll = ((Autoscroll)component);
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      Integer localInteger1 = Integer.valueOf(100);
      Integer localInteger2 = Integer.valueOf(100);
      try
      {
        localInteger1 = (Integer)localToolkit.getDesktopProperty("DnD.Autoscroll.initialDelay");
      }
      catch (Exception localException1) {}
      try
      {
        localInteger2 = (Integer)localToolkit.getDesktopProperty("DnD.Autoscroll.interval");
      }
      catch (Exception localException2) {}
      timer = new Timer(localInteger2.intValue(), this);
      timer.setCoalesce(true);
      timer.setInitialDelay(localInteger1.intValue());
      locn = paramPoint;
      prev = paramPoint;
      try
      {
        hysteresis = ((Integer)localToolkit.getDesktopProperty("DnD.Autoscroll.cursorHysteresis")).intValue();
      }
      catch (Exception localException3) {}
      timer.start();
    }
    
    private void updateRegion()
    {
      Insets localInsets = autoScroll.getAutoscrollInsets();
      Dimension localDimension = component.getSize();
      if ((width != outer.width) || (height != outer.height)) {
        outer.reshape(0, 0, width, height);
      }
      if ((inner.x != left) || (inner.y != top)) {
        inner.setLocation(left, top);
      }
      int i = width - (left + right);
      int j = height - (top + bottom);
      if ((i != inner.width) || (j != inner.height)) {
        inner.setSize(i, j);
      }
    }
    
    protected synchronized void updateLocation(Point paramPoint)
    {
      prev = locn;
      locn = paramPoint;
      if ((Math.abs(locn.x - prev.x) > hysteresis) || (Math.abs(locn.y - prev.y) > hysteresis))
      {
        if (timer.isRunning()) {
          timer.stop();
        }
      }
      else if (!timer.isRunning()) {
        timer.start();
      }
    }
    
    protected void stop()
    {
      timer.stop();
    }
    
    public synchronized void actionPerformed(ActionEvent paramActionEvent)
    {
      updateRegion();
      if ((outer.contains(locn)) && (!inner.contains(locn))) {
        autoScroll.autoscroll(locn);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DropTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */