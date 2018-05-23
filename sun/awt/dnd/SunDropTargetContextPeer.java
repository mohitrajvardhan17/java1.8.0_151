package sun.awt.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DropTargetContextPeer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import sun.security.util.SecurityConstants.AWT;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class SunDropTargetContextPeer
  implements DropTargetContextPeer, Transferable
{
  public static final boolean DISPATCH_SYNC = true;
  private DropTarget currentDT;
  private DropTargetContext currentDTC;
  private long[] currentT;
  private int currentA;
  private int currentSA;
  private int currentDA;
  private int previousDA;
  private long nativeDragContext;
  private Transferable local;
  private boolean dragRejected = false;
  protected int dropStatus = 0;
  protected boolean dropComplete = false;
  boolean dropInProcess = false;
  protected static final Object _globalLock = new Object();
  private static final PlatformLogger dndLog = PlatformLogger.getLogger("sun.awt.dnd.SunDropTargetContextPeer");
  protected static Transferable currentJVMLocalSourceTransferable = null;
  protected static final int STATUS_NONE = 0;
  protected static final int STATUS_WAIT = 1;
  protected static final int STATUS_ACCEPT = 2;
  protected static final int STATUS_REJECT = -1;
  
  public static void setCurrentJVMLocalSourceTransferable(Transferable paramTransferable)
    throws InvalidDnDOperationException
  {
    synchronized (_globalLock)
    {
      if ((paramTransferable != null) && (currentJVMLocalSourceTransferable != null)) {
        throw new InvalidDnDOperationException();
      }
      currentJVMLocalSourceTransferable = paramTransferable;
    }
  }
  
  private static Transferable getJVMLocalSourceTransferable()
  {
    return currentJVMLocalSourceTransferable;
  }
  
  public SunDropTargetContextPeer() {}
  
  public DropTarget getDropTarget()
  {
    return currentDT;
  }
  
  public synchronized void setTargetActions(int paramInt)
  {
    currentA = (paramInt & 0x40000003);
  }
  
  public int getTargetActions()
  {
    return currentA;
  }
  
  public Transferable getTransferable()
  {
    return this;
  }
  
  public DataFlavor[] getTransferDataFlavors()
  {
    Transferable localTransferable = local;
    if (localTransferable != null) {
      return localTransferable.getTransferDataFlavors();
    }
    return DataTransferer.getInstance().getFlavorsForFormatsAsArray(currentT, DataTransferer.adaptFlavorMap(currentDT.getFlavorMap()));
  }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
  {
    Transferable localTransferable = local;
    if (localTransferable != null) {
      return localTransferable.isDataFlavorSupported(paramDataFlavor);
    }
    return DataTransferer.getInstance().getFlavorsForFormats(currentT, DataTransferer.adaptFlavorMap(currentDT.getFlavorMap())).containsKey(paramDataFlavor);
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor)
    throws UnsupportedFlavorException, IOException, InvalidDnDOperationException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    try
    {
      if ((!dropInProcess) && (localSecurityManager != null)) {
        localSecurityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
      }
    }
    catch (Exception localException)
    {
      localObject1 = Thread.currentThread();
      ((Thread)localObject1).getUncaughtExceptionHandler().uncaughtException((Thread)localObject1, localException);
      return null;
    }
    Long localLong = null;
    Object localObject1 = local;
    if (localObject1 != null) {
      return ((Transferable)localObject1).getTransferData(paramDataFlavor);
    }
    if ((dropStatus != 2) || (dropComplete)) {
      throw new InvalidDnDOperationException("No drop current");
    }
    Map localMap = DataTransferer.getInstance().getFlavorsForFormats(currentT, DataTransferer.adaptFlavorMap(currentDT.getFlavorMap()));
    localLong = (Long)localMap.get(paramDataFlavor);
    if (localLong == null) {
      throw new UnsupportedFlavorException(paramDataFlavor);
    }
    if ((paramDataFlavor.isRepresentationClassRemote()) && (currentDA != 1073741824)) {
      throw new InvalidDnDOperationException("only ACTION_LINK is permissable for transfer of java.rmi.Remote objects");
    }
    long l = localLong.longValue();
    Object localObject2 = getNativeData(l);
    if ((localObject2 instanceof byte[])) {
      try
      {
        return DataTransferer.getInstance().translateBytes((byte[])localObject2, paramDataFlavor, l, this);
      }
      catch (IOException localIOException1)
      {
        throw new InvalidDnDOperationException(localIOException1.getMessage());
      }
    }
    if ((localObject2 instanceof InputStream)) {
      try
      {
        return DataTransferer.getInstance().translateStream((InputStream)localObject2, paramDataFlavor, l, this);
      }
      catch (IOException localIOException2)
      {
        throw new InvalidDnDOperationException(localIOException2.getMessage());
      }
    }
    throw new IOException("no native data was transfered");
  }
  
  protected abstract Object getNativeData(long paramLong)
    throws IOException;
  
  public boolean isTransferableJVMLocal()
  {
    return (local != null) || (getJVMLocalSourceTransferable() != null);
  }
  
  private int handleEnterMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong)
  {
    return postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 504, true);
  }
  
  protected void processEnterMessage(SunDropTargetEvent paramSunDropTargetEvent)
  {
    Component localComponent = (Component)paramSunDropTargetEvent.getSource();
    DropTarget localDropTarget = localComponent.getDropTarget();
    Point localPoint = paramSunDropTargetEvent.getPoint();
    local = getJVMLocalSourceTransferable();
    if (currentDTC != null)
    {
      currentDTC.removeNotify();
      currentDTC = null;
    }
    if ((localComponent.isShowing()) && (localDropTarget != null) && (localDropTarget.isActive()))
    {
      currentDT = localDropTarget;
      currentDTC = currentDT.getDropTargetContext();
      currentDTC.addNotify(this);
      currentA = localDropTarget.getDefaultActions();
      try
      {
        localDropTarget.dragEnter(new DropTargetDragEvent(currentDTC, localPoint, currentDA, currentSA));
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
        currentDA = 0;
      }
    }
    else
    {
      currentDT = null;
      currentDTC = null;
      currentDA = 0;
      currentSA = 0;
      currentA = 0;
    }
  }
  
  private void handleExitMessage(Component paramComponent, long paramLong)
  {
    postDropTargetEvent(paramComponent, 0, 0, 0, 0, null, paramLong, 505, true);
  }
  
  protected void processExitMessage(SunDropTargetEvent paramSunDropTargetEvent)
  {
    Component localComponent = (Component)paramSunDropTargetEvent.getSource();
    DropTarget localDropTarget = localComponent.getDropTarget();
    DropTargetContext localDropTargetContext = null;
    if (localDropTarget == null)
    {
      currentDT = null;
      currentT = null;
      if (currentDTC != null) {
        currentDTC.removeNotify();
      }
      currentDTC = null;
      return;
    }
    if (localDropTarget != currentDT)
    {
      if (currentDTC != null) {
        currentDTC.removeNotify();
      }
      currentDT = localDropTarget;
      currentDTC = localDropTarget.getDropTargetContext();
      currentDTC.addNotify(this);
    }
    localDropTargetContext = currentDTC;
    if (localDropTarget.isActive()) {
      try
      {
        localDropTarget.dragExit(new DropTargetEvent(localDropTargetContext));
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      finally
      {
        currentA = 0;
        currentSA = 0;
        currentDA = 0;
        currentDT = null;
        currentT = null;
        currentDTC.removeNotify();
        currentDTC = null;
        local = null;
        dragRejected = false;
      }
    }
  }
  
  private int handleMotionMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong)
  {
    return postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 506, true);
  }
  
  protected void processMotionMessage(SunDropTargetEvent paramSunDropTargetEvent, boolean paramBoolean)
  {
    Component localComponent = (Component)paramSunDropTargetEvent.getSource();
    Point localPoint = paramSunDropTargetEvent.getPoint();
    int i = paramSunDropTargetEvent.getID();
    DropTarget localDropTarget1 = localComponent.getDropTarget();
    DropTargetContext localDropTargetContext = null;
    if ((localComponent.isShowing()) && (localDropTarget1 != null) && (localDropTarget1.isActive()))
    {
      if (currentDT != localDropTarget1)
      {
        if (currentDTC != null) {
          currentDTC.removeNotify();
        }
        currentDT = localDropTarget1;
        currentDTC = null;
      }
      localDropTargetContext = currentDT.getDropTargetContext();
      if (localDropTargetContext != currentDTC)
      {
        if (currentDTC != null) {
          currentDTC.removeNotify();
        }
        currentDTC = localDropTargetContext;
        currentDTC.addNotify(this);
      }
      currentA = currentDT.getDefaultActions();
      try
      {
        DropTargetDragEvent localDropTargetDragEvent = new DropTargetDragEvent(localDropTargetContext, localPoint, currentDA, currentSA);
        DropTarget localDropTarget2 = localDropTarget1;
        if (paramBoolean) {
          localDropTarget2.dropActionChanged(localDropTargetDragEvent);
        } else {
          localDropTarget2.dragOver(localDropTargetDragEvent);
        }
        if (dragRejected) {
          currentDA = 0;
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
        currentDA = 0;
      }
    }
    else
    {
      currentDA = 0;
    }
  }
  
  private void handleDropMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong)
  {
    postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 502, false);
  }
  
  protected void processDropMessage(SunDropTargetEvent paramSunDropTargetEvent)
  {
    Component localComponent = (Component)paramSunDropTargetEvent.getSource();
    Point localPoint = paramSunDropTargetEvent.getPoint();
    DropTarget localDropTarget = localComponent.getDropTarget();
    dropStatus = 1;
    dropComplete = false;
    if ((localComponent.isShowing()) && (localDropTarget != null) && (localDropTarget.isActive()))
    {
      DropTargetContext localDropTargetContext = localDropTarget.getDropTargetContext();
      currentDT = localDropTarget;
      if (currentDTC != null) {
        currentDTC.removeNotify();
      }
      currentDTC = localDropTargetContext;
      currentDTC.addNotify(this);
      currentA = localDropTarget.getDefaultActions();
      synchronized (_globalLock)
      {
        if ((local = getJVMLocalSourceTransferable()) != null) {
          setCurrentJVMLocalSourceTransferable(null);
        }
      }
      dropInProcess = true;
      try
      {
        localDropTarget.drop(new DropTargetDropEvent(localDropTargetContext, localPoint, currentDA, currentSA, local != null));
      }
      finally
      {
        if (dropStatus == 1) {
          rejectDrop();
        } else if (!dropComplete) {
          dropComplete(false);
        }
        dropInProcess = false;
      }
    }
    else
    {
      rejectDrop();
    }
  }
  
  protected int postDropTargetEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong, int paramInt5, boolean paramBoolean)
  {
    AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
    EventDispatcher localEventDispatcher = new EventDispatcher(this, paramInt3, paramInt4, paramArrayOfLong, paramLong, paramBoolean);
    SunDropTargetEvent localSunDropTargetEvent = new SunDropTargetEvent(paramComponent, paramInt5, paramInt1, paramInt2, localEventDispatcher);
    if (paramBoolean == true) {
      DataTransferer.getInstance().getToolkitThreadBlockedHandler().lock();
    }
    SunToolkit.postEvent(localAppContext, localSunDropTargetEvent);
    eventPosted(localSunDropTargetEvent);
    if (paramBoolean == true)
    {
      while (!localEventDispatcher.isDone()) {
        DataTransferer.getInstance().getToolkitThreadBlockedHandler().enter();
      }
      DataTransferer.getInstance().getToolkitThreadBlockedHandler().unlock();
      return localEventDispatcher.getReturnValue();
    }
    return 0;
  }
  
  public synchronized void acceptDrag(int paramInt)
  {
    if (currentDT == null) {
      throw new InvalidDnDOperationException("No Drag pending");
    }
    currentDA = mapOperation(paramInt);
    if (currentDA != 0) {
      dragRejected = false;
    }
  }
  
  public synchronized void rejectDrag()
  {
    if (currentDT == null) {
      throw new InvalidDnDOperationException("No Drag pending");
    }
    currentDA = 0;
    dragRejected = true;
  }
  
  public synchronized void acceptDrop(int paramInt)
  {
    if (paramInt == 0) {
      throw new IllegalArgumentException("invalid acceptDrop() action");
    }
    if ((dropStatus == 1) || (dropStatus == 2))
    {
      currentDA = (currentA = mapOperation(paramInt & currentSA));
      dropStatus = 2;
      dropComplete = false;
    }
    else
    {
      throw new InvalidDnDOperationException("invalid acceptDrop()");
    }
  }
  
  public synchronized void rejectDrop()
  {
    if (dropStatus != 1) {
      throw new InvalidDnDOperationException("invalid rejectDrop()");
    }
    dropStatus = -1;
    currentDA = 0;
    dropComplete(false);
  }
  
  private int mapOperation(int paramInt)
  {
    int[] arrayOfInt = { 2, 1, 1073741824 };
    int i = 0;
    for (int j = 0; j < arrayOfInt.length; j++) {
      if ((paramInt & arrayOfInt[j]) == arrayOfInt[j])
      {
        i = arrayOfInt[j];
        break;
      }
    }
    return i;
  }
  
  public synchronized void dropComplete(boolean paramBoolean)
  {
    if (dropStatus == 0) {
      throw new InvalidDnDOperationException("No Drop pending");
    }
    if (currentDTC != null) {
      currentDTC.removeNotify();
    }
    currentDT = null;
    currentDTC = null;
    currentT = null;
    currentA = 0;
    synchronized (_globalLock)
    {
      currentJVMLocalSourceTransferable = null;
    }
    dropStatus = 0;
    dropComplete = true;
    try
    {
      doDropDone(paramBoolean, currentDA, local != null);
    }
    finally
    {
      currentDA = 0;
      nativeDragContext = 0L;
    }
  }
  
  protected abstract void doDropDone(boolean paramBoolean1, int paramInt, boolean paramBoolean2);
  
  protected synchronized long getNativeDragContext()
  {
    return nativeDragContext;
  }
  
  protected void eventPosted(SunDropTargetEvent paramSunDropTargetEvent) {}
  
  protected void eventProcessed(SunDropTargetEvent paramSunDropTargetEvent, int paramInt, boolean paramBoolean) {}
  
  protected static class EventDispatcher
  {
    private final SunDropTargetContextPeer peer;
    private final int dropAction;
    private final int actions;
    private final long[] formats;
    private long nativeCtxt;
    private final boolean dispatchType;
    private boolean dispatcherDone = false;
    private int returnValue = 0;
    private final HashSet eventSet = new HashSet(3);
    static final ToolkitThreadBlockedHandler handler = DataTransferer.getInstance().getToolkitThreadBlockedHandler();
    
    EventDispatcher(SunDropTargetContextPeer paramSunDropTargetContextPeer, int paramInt1, int paramInt2, long[] paramArrayOfLong, long paramLong, boolean paramBoolean)
    {
      peer = paramSunDropTargetContextPeer;
      nativeCtxt = paramLong;
      dropAction = paramInt1;
      actions = paramInt2;
      formats = (null == paramArrayOfLong ? null : Arrays.copyOf(paramArrayOfLong, paramArrayOfLong.length));
      dispatchType = paramBoolean;
    }
    
    void dispatchEvent(SunDropTargetEvent paramSunDropTargetEvent)
    {
      int i = paramSunDropTargetEvent.getID();
      switch (i)
      {
      case 504: 
        dispatchEnterEvent(paramSunDropTargetEvent);
        break;
      case 506: 
        dispatchMotionEvent(paramSunDropTargetEvent);
        break;
      case 505: 
        dispatchExitEvent(paramSunDropTargetEvent);
        break;
      case 502: 
        dispatchDropEvent(paramSunDropTargetEvent);
        break;
      case 503: 
      default: 
        throw new InvalidDnDOperationException();
      }
    }
    
    /* Error */
    private void dispatchEnterEvent(SunDropTargetEvent paramSunDropTargetEvent)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   11: aload_0
      //   12: getfield 173	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dropAction	I
      //   15: invokestatic 199	sun/awt/dnd/SunDropTargetContextPeer:access$002	(Lsun/awt/dnd/SunDropTargetContextPeer;I)I
      //   18: pop
      //   19: aload_0
      //   20: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   23: aload_0
      //   24: getfield 175	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:nativeCtxt	J
      //   27: invokestatic 202	sun/awt/dnd/SunDropTargetContextPeer:access$102	(Lsun/awt/dnd/SunDropTargetContextPeer;J)J
      //   30: pop2
      //   31: aload_0
      //   32: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   35: aload_0
      //   36: getfield 178	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:formats	[J
      //   39: invokestatic 203	sun/awt/dnd/SunDropTargetContextPeer:access$202	(Lsun/awt/dnd/SunDropTargetContextPeer;[J)[J
      //   42: pop
      //   43: aload_0
      //   44: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   47: aload_0
      //   48: getfield 172	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:actions	I
      //   51: invokestatic 200	sun/awt/dnd/SunDropTargetContextPeer:access$302	(Lsun/awt/dnd/SunDropTargetContextPeer;I)I
      //   54: pop
      //   55: aload_0
      //   56: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   59: aload_0
      //   60: getfield 173	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dropAction	I
      //   63: invokestatic 201	sun/awt/dnd/SunDropTargetContextPeer:access$402	(Lsun/awt/dnd/SunDropTargetContextPeer;I)I
      //   66: pop
      //   67: aload_0
      //   68: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   71: iconst_2
      //   72: putfield 170	sun/awt/dnd/SunDropTargetContextPeer:dropStatus	I
      //   75: aload_0
      //   76: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   79: iconst_0
      //   80: putfield 171	sun/awt/dnd/SunDropTargetContextPeer:dropComplete	Z
      //   83: aload_0
      //   84: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   87: aload_1
      //   88: invokevirtual 205	sun/awt/dnd/SunDropTargetContextPeer:processEnterMessage	(Lsun/awt/dnd/SunDropTargetEvent;)V
      //   91: aload_0
      //   92: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   95: iconst_0
      //   96: putfield 170	sun/awt/dnd/SunDropTargetContextPeer:dropStatus	I
      //   99: goto +14 -> 113
      //   102: astore_3
      //   103: aload_0
      //   104: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   107: iconst_0
      //   108: putfield 170	sun/awt/dnd/SunDropTargetContextPeer:dropStatus	I
      //   111: aload_3
      //   112: athrow
      //   113: aload_0
      //   114: aload_0
      //   115: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   118: invokestatic 198	sun/awt/dnd/SunDropTargetContextPeer:access$400	(Lsun/awt/dnd/SunDropTargetContextPeer;)I
      //   121: invokevirtual 210	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:setReturnValue	(I)V
      //   124: aload_2
      //   125: monitorexit
      //   126: goto +10 -> 136
      //   129: astore 4
      //   131: aload_2
      //   132: monitorexit
      //   133: aload 4
      //   135: athrow
      //   136: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	137	0	this	EventDispatcher
      //   0	137	1	paramSunDropTargetEvent	SunDropTargetEvent
      //   5	127	2	Ljava/lang/Object;	Object
      //   102	10	3	localObject1	Object
      //   129	5	4	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   83	91	102	finally
      //   7	126	129	finally
      //   129	133	129	finally
    }
    
    private void dispatchMotionEvent(SunDropTargetEvent paramSunDropTargetEvent)
    {
      synchronized (peer)
      {
        boolean bool = peer.previousDA != dropAction;
        peer.previousDA = dropAction;
        peer.nativeDragContext = nativeCtxt;
        peer.currentT = formats;
        peer.currentSA = actions;
        peer.currentDA = dropAction;
        peer.dropStatus = 2;
        peer.dropComplete = false;
        try
        {
          peer.processMotionMessage(paramSunDropTargetEvent, bool);
        }
        finally
        {
          peer.dropStatus = 0;
        }
        setReturnValue(peer.currentDA);
      }
    }
    
    private void dispatchExitEvent(SunDropTargetEvent paramSunDropTargetEvent)
    {
      synchronized (peer)
      {
        peer.nativeDragContext = nativeCtxt;
        peer.processExitMessage(paramSunDropTargetEvent);
      }
    }
    
    private void dispatchDropEvent(SunDropTargetEvent paramSunDropTargetEvent)
    {
      synchronized (peer)
      {
        peer.nativeDragContext = nativeCtxt;
        peer.currentT = formats;
        peer.currentSA = actions;
        peer.currentDA = dropAction;
        peer.processDropMessage(paramSunDropTargetEvent);
      }
    }
    
    void setReturnValue(int paramInt)
    {
      returnValue = paramInt;
    }
    
    int getReturnValue()
    {
      return returnValue;
    }
    
    boolean isDone()
    {
      return eventSet.isEmpty();
    }
    
    void registerEvent(SunDropTargetEvent paramSunDropTargetEvent)
    {
      handler.lock();
      if ((!eventSet.add(paramSunDropTargetEvent)) && (SunDropTargetContextPeer.dndLog.isLoggable(PlatformLogger.Level.FINE))) {
        SunDropTargetContextPeer.dndLog.fine("Event is already registered: " + paramSunDropTargetEvent);
      }
      handler.unlock();
    }
    
    /* Error */
    void unregisterEvent(SunDropTargetEvent paramSunDropTargetEvent)
    {
      // Byte code:
      //   0: getstatic 180	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
      //   3: invokeinterface 220 1 0
      //   8: aload_0
      //   9: getfield 179	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:eventSet	Ljava/util/HashSet;
      //   12: aload_1
      //   13: invokevirtual 194	java/util/HashSet:remove	(Ljava/lang/Object;)Z
      //   16: ifne +12 -> 28
      //   19: getstatic 180	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
      //   22: invokeinterface 221 1 0
      //   27: return
      //   28: aload_0
      //   29: getfield 179	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:eventSet	Ljava/util/HashSet;
      //   32: invokevirtual 190	java/util/HashSet:isEmpty	()Z
      //   35: ifeq +31 -> 66
      //   38: aload_0
      //   39: getfield 177	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
      //   42: ifne +19 -> 61
      //   45: aload_0
      //   46: getfield 176	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatchType	Z
      //   49: iconst_1
      //   50: if_icmpne +11 -> 61
      //   53: getstatic 180	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
      //   56: invokeinterface 219 1 0
      //   61: aload_0
      //   62: iconst_1
      //   63: putfield 177	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
      //   66: getstatic 180	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
      //   69: invokeinterface 221 1 0
      //   74: goto +14 -> 88
      //   77: astore_2
      //   78: getstatic 180	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
      //   81: invokeinterface 221 1 0
      //   86: aload_2
      //   87: athrow
      //   88: aload_0
      //   89: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   92: aload_1
      //   93: aload_0
      //   94: getfield 174	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:returnValue	I
      //   97: aload_0
      //   98: getfield 177	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
      //   101: invokevirtual 207	sun/awt/dnd/SunDropTargetContextPeer:eventProcessed	(Lsun/awt/dnd/SunDropTargetEvent;IZ)V
      //   104: aload_0
      //   105: getfield 177	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
      //   108: ifeq +44 -> 152
      //   111: aload_0
      //   112: lconst_0
      //   113: putfield 175	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:nativeCtxt	J
      //   116: aload_0
      //   117: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   120: lconst_0
      //   121: invokestatic 202	sun/awt/dnd/SunDropTargetContextPeer:access$102	(Lsun/awt/dnd/SunDropTargetContextPeer;J)J
      //   124: pop2
      //   125: goto +27 -> 152
      //   128: astore_3
      //   129: aload_0
      //   130: getfield 177	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
      //   133: ifeq +17 -> 150
      //   136: aload_0
      //   137: lconst_0
      //   138: putfield 175	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:nativeCtxt	J
      //   141: aload_0
      //   142: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
      //   145: lconst_0
      //   146: invokestatic 202	sun/awt/dnd/SunDropTargetContextPeer:access$102	(Lsun/awt/dnd/SunDropTargetContextPeer;J)J
      //   149: pop2
      //   150: aload_3
      //   151: athrow
      //   152: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	153	0	this	EventDispatcher
      //   0	153	1	paramSunDropTargetEvent	SunDropTargetEvent
      //   77	10	2	localObject1	Object
      //   128	23	3	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   8	19	77	finally
      //   28	66	77	finally
      //   88	104	128	finally
    }
    
    /* Error */
    public void unregisterAllEvents()
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_1
      //   2: getstatic 180	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
      //   5: invokeinterface 220 1 0
      //   10: aload_0
      //   11: getfield 179	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:eventSet	Ljava/util/HashSet;
      //   14: invokevirtual 192	java/util/HashSet:toArray	()[Ljava/lang/Object;
      //   17: astore_1
      //   18: getstatic 180	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
      //   21: invokeinterface 221 1 0
      //   26: goto +14 -> 40
      //   29: astore_2
      //   30: getstatic 180	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
      //   33: invokeinterface 221 1 0
      //   38: aload_2
      //   39: athrow
      //   40: aload_1
      //   41: ifnull +27 -> 68
      //   44: iconst_0
      //   45: istore_2
      //   46: iload_2
      //   47: aload_1
      //   48: arraylength
      //   49: if_icmpge +19 -> 68
      //   52: aload_0
      //   53: aload_1
      //   54: iload_2
      //   55: aaload
      //   56: checkcast 98	sun/awt/dnd/SunDropTargetEvent
      //   59: invokevirtual 215	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:unregisterEvent	(Lsun/awt/dnd/SunDropTargetEvent;)V
      //   62: iinc 2 1
      //   65: goto -19 -> 46
      //   68: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	69	0	this	EventDispatcher
      //   1	53	1	arrayOfObject	Object[]
      //   29	10	2	localObject	Object
      //   45	18	2	i	int
      // Exception table:
      //   from	to	target	type
      //   10	18	29	finally
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\dnd\SunDropTargetContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */