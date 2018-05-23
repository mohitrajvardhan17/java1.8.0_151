package sun.awt.dnd;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.SortedMap;
import sun.awt.SunToolkit;
import sun.awt.datatransfer.DataTransferer;

public abstract class SunDragSourceContextPeer
  implements DragSourceContextPeer
{
  private DragGestureEvent trigger;
  private Component component;
  private Cursor cursor;
  private Image dragImage;
  private Point dragImageOffset;
  private long nativeCtxt;
  private DragSourceContext dragSourceContext;
  private int sourceActions;
  private static boolean dragDropInProgress = false;
  private static boolean discardingMouseEvents = false;
  protected static final int DISPATCH_ENTER = 1;
  protected static final int DISPATCH_MOTION = 2;
  protected static final int DISPATCH_CHANGED = 3;
  protected static final int DISPATCH_EXIT = 4;
  protected static final int DISPATCH_FINISH = 5;
  protected static final int DISPATCH_MOUSE_MOVED = 6;
  
  public SunDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
  {
    trigger = paramDragGestureEvent;
    if (trigger != null) {
      component = trigger.getComponent();
    } else {
      component = null;
    }
  }
  
  public void startSecondaryEventLoop() {}
  
  public void quitSecondaryEventLoop() {}
  
  public void startDrag(DragSourceContext paramDragSourceContext, Cursor paramCursor, Image paramImage, Point paramPoint)
    throws InvalidDnDOperationException
  {
    if (getTrigger().getTriggerEvent() == null) {
      throw new InvalidDnDOperationException("DragGestureEvent has a null trigger");
    }
    dragSourceContext = paramDragSourceContext;
    cursor = paramCursor;
    sourceActions = getDragSourceContext().getSourceActions();
    dragImage = paramImage;
    dragImageOffset = paramPoint;
    Transferable localTransferable = getDragSourceContext().getTransferable();
    SortedMap localSortedMap = DataTransferer.getInstance().getFormatsForTransferable(localTransferable, DataTransferer.adaptFlavorMap(getTrigger().getDragSource().getFlavorMap()));
    DataTransferer.getInstance();
    long[] arrayOfLong = DataTransferer.keysToLongArray(localSortedMap);
    startDrag(localTransferable, arrayOfLong, localSortedMap);
    discardingMouseEvents = true;
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        SunDragSourceContextPeer.access$002(false);
      }
    });
  }
  
  protected abstract void startDrag(Transferable paramTransferable, long[] paramArrayOfLong, Map paramMap);
  
  public void setCursor(Cursor paramCursor)
    throws InvalidDnDOperationException
  {
    synchronized (this)
    {
      if ((cursor == null) || (!cursor.equals(paramCursor)))
      {
        cursor = paramCursor;
        setNativeCursor(getNativeContext(), paramCursor, paramCursor != null ? paramCursor.getType() : 0);
      }
    }
  }
  
  public Cursor getCursor()
  {
    return cursor;
  }
  
  public Image getDragImage()
  {
    return dragImage;
  }
  
  public Point getDragImageOffset()
  {
    if (dragImageOffset == null) {
      return new Point(0, 0);
    }
    return new Point(dragImageOffset);
  }
  
  protected abstract void setNativeCursor(long paramLong, Cursor paramCursor, int paramInt);
  
  protected synchronized void setTrigger(DragGestureEvent paramDragGestureEvent)
  {
    trigger = paramDragGestureEvent;
    if (trigger != null) {
      component = trigger.getComponent();
    } else {
      component = null;
    }
  }
  
  protected DragGestureEvent getTrigger()
  {
    return trigger;
  }
  
  protected Component getComponent()
  {
    return component;
  }
  
  protected synchronized void setNativeContext(long paramLong)
  {
    nativeCtxt = paramLong;
  }
  
  protected synchronized long getNativeContext()
  {
    return nativeCtxt;
  }
  
  protected DragSourceContext getDragSourceContext()
  {
    return dragSourceContext;
  }
  
  public void transferablesFlavorsChanged() {}
  
  protected final void postDragSourceDragEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = convertModifiersToDropAction(paramInt2, sourceActions);
    DragSourceDragEvent localDragSourceDragEvent = new DragSourceDragEvent(getDragSourceContext(), i, paramInt1 & sourceActions, paramInt2, paramInt3, paramInt4);
    EventDispatcher localEventDispatcher = new EventDispatcher(paramInt5, localDragSourceDragEvent);
    SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(getComponent()), localEventDispatcher);
    startSecondaryEventLoop();
  }
  
  protected void dragEnter(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 1);
  }
  
  private void dragMotion(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 2);
  }
  
  private void operationChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 3);
  }
  
  protected final void dragExit(int paramInt1, int paramInt2)
  {
    DragSourceEvent localDragSourceEvent = new DragSourceEvent(getDragSourceContext(), paramInt1, paramInt2);
    EventDispatcher localEventDispatcher = new EventDispatcher(4, localDragSourceEvent);
    SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(getComponent()), localEventDispatcher);
    startSecondaryEventLoop();
  }
  
  private void dragMouseMoved(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 6);
  }
  
  protected final void dragDropFinished(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    DragSourceDropEvent localDragSourceDropEvent = new DragSourceDropEvent(getDragSourceContext(), paramInt1 & sourceActions, paramBoolean, paramInt2, paramInt3);
    EventDispatcher localEventDispatcher = new EventDispatcher(5, localDragSourceDropEvent);
    SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(getComponent()), localEventDispatcher);
    startSecondaryEventLoop();
    setNativeContext(0L);
    dragImage = null;
    dragImageOffset = null;
  }
  
  public static void setDragDropInProgress(boolean paramBoolean)
    throws InvalidDnDOperationException
  {
    synchronized (SunDragSourceContextPeer.class)
    {
      if (dragDropInProgress == paramBoolean) {
        throw new InvalidDnDOperationException(getExceptionMessage(paramBoolean));
      }
      dragDropInProgress = paramBoolean;
    }
  }
  
  public static boolean checkEvent(AWTEvent paramAWTEvent)
  {
    if ((discardingMouseEvents) && ((paramAWTEvent instanceof MouseEvent)))
    {
      MouseEvent localMouseEvent = (MouseEvent)paramAWTEvent;
      if (!(localMouseEvent instanceof SunDropTargetEvent)) {
        return false;
      }
    }
    return true;
  }
  
  public static void checkDragDropInProgress()
    throws InvalidDnDOperationException
  {
    if (dragDropInProgress) {
      throw new InvalidDnDOperationException(getExceptionMessage(true));
    }
  }
  
  private static String getExceptionMessage(boolean paramBoolean)
  {
    return paramBoolean ? "Drag and drop in progress" : "No drag in progress";
  }
  
  public static int convertModifiersToDropAction(int paramInt1, int paramInt2)
  {
    int i = 0;
    switch (paramInt1 & 0xC0)
    {
    case 192: 
      i = 1073741824;
      break;
    case 128: 
      i = 1;
      break;
    case 64: 
      i = 2;
      break;
    default: 
      if ((paramInt2 & 0x2) != 0) {
        i = 2;
      } else if ((paramInt2 & 0x1) != 0) {
        i = 1;
      } else if ((paramInt2 & 0x40000000) != 0) {
        i = 1073741824;
      }
      break;
    }
    return i & paramInt2;
  }
  
  private void cleanup()
  {
    trigger = null;
    component = null;
    cursor = null;
    dragSourceContext = null;
    SunDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(null);
    setDragDropInProgress(false);
  }
  
  private class EventDispatcher
    implements Runnable
  {
    private final int dispatchType;
    private final DragSourceEvent event;
    
    EventDispatcher(int paramInt, DragSourceEvent paramDragSourceEvent)
    {
      switch (paramInt)
      {
      case 1: 
      case 2: 
      case 3: 
      case 6: 
        if (!(paramDragSourceEvent instanceof DragSourceDragEvent)) {
          throw new IllegalArgumentException("Event: " + paramDragSourceEvent);
        }
        break;
      case 4: 
        break;
      case 5: 
        if (!(paramDragSourceEvent instanceof DragSourceDropEvent)) {
          throw new IllegalArgumentException("Event: " + paramDragSourceEvent);
        }
        break;
      default: 
        throw new IllegalArgumentException("Dispatch type: " + paramInt);
      }
      dispatchType = paramInt;
      event = paramDragSourceEvent;
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 83	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
      //   4: invokevirtual 99	sun/awt/dnd/SunDragSourceContextPeer:getDragSourceContext	()Ljava/awt/dnd/DragSourceContext;
      //   7: astore_1
      //   8: aload_0
      //   9: getfield 81	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:dispatchType	I
      //   12: tableswitch	default:+141->153, 1:+40->52, 2:+54->66, 3:+68->80, 4:+82->94, 5:+107->119, 6:+93->105
      //   52: aload_1
      //   53: aload_0
      //   54: getfield 82	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
      //   57: checkcast 41	java/awt/dnd/DragSourceDragEvent
      //   60: invokevirtual 84	java/awt/dnd/DragSourceContext:dragEnter	(Ljava/awt/dnd/DragSourceDragEvent;)V
      //   63: goto +120 -> 183
      //   66: aload_1
      //   67: aload_0
      //   68: getfield 82	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
      //   71: checkcast 41	java/awt/dnd/DragSourceDragEvent
      //   74: invokevirtual 86	java/awt/dnd/DragSourceContext:dragOver	(Ljava/awt/dnd/DragSourceDragEvent;)V
      //   77: goto +106 -> 183
      //   80: aload_1
      //   81: aload_0
      //   82: getfield 82	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
      //   85: checkcast 41	java/awt/dnd/DragSourceDragEvent
      //   88: invokevirtual 87	java/awt/dnd/DragSourceContext:dropActionChanged	(Ljava/awt/dnd/DragSourceDragEvent;)V
      //   91: goto +92 -> 183
      //   94: aload_1
      //   95: aload_0
      //   96: getfield 82	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
      //   99: invokevirtual 89	java/awt/dnd/DragSourceContext:dragExit	(Ljava/awt/dnd/DragSourceEvent;)V
      //   102: goto +81 -> 183
      //   105: aload_1
      //   106: aload_0
      //   107: getfield 82	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
      //   110: checkcast 41	java/awt/dnd/DragSourceDragEvent
      //   113: invokevirtual 85	java/awt/dnd/DragSourceContext:dragMouseMoved	(Ljava/awt/dnd/DragSourceDragEvent;)V
      //   116: goto +67 -> 183
      //   119: aload_1
      //   120: aload_0
      //   121: getfield 82	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
      //   124: checkcast 42	java/awt/dnd/DragSourceDropEvent
      //   127: invokevirtual 88	java/awt/dnd/DragSourceContext:dragDropEnd	(Ljava/awt/dnd/DragSourceDropEvent;)V
      //   130: aload_0
      //   131: getfield 83	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
      //   134: invokestatic 100	sun/awt/dnd/SunDragSourceContextPeer:access$100	(Lsun/awt/dnd/SunDragSourceContextPeer;)V
      //   137: goto +13 -> 150
      //   140: astore_2
      //   141: aload_0
      //   142: getfield 83	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
      //   145: invokestatic 100	sun/awt/dnd/SunDragSourceContextPeer:access$100	(Lsun/awt/dnd/SunDragSourceContextPeer;)V
      //   148: aload_2
      //   149: athrow
      //   150: goto +33 -> 183
      //   153: new 45	java/lang/IllegalStateException
      //   156: dup
      //   157: new 48	java/lang/StringBuilder
      //   160: dup
      //   161: invokespecial 93	java/lang/StringBuilder:<init>	()V
      //   164: ldc 1
      //   166: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   169: aload_0
      //   170: getfield 81	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:dispatchType	I
      //   173: invokevirtual 95	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   176: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   179: invokespecial 91	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
      //   182: athrow
      //   183: aload_0
      //   184: getfield 83	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
      //   187: invokevirtual 98	sun/awt/dnd/SunDragSourceContextPeer:quitSecondaryEventLoop	()V
      //   190: goto +13 -> 203
      //   193: astore_3
      //   194: aload_0
      //   195: getfield 83	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
      //   198: invokevirtual 98	sun/awt/dnd/SunDragSourceContextPeer:quitSecondaryEventLoop	()V
      //   201: aload_3
      //   202: athrow
      //   203: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	204	0	this	EventDispatcher
      //   7	113	1	localDragSourceContext	DragSourceContext
      //   140	9	2	localObject1	Object
      //   193	9	3	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   119	130	140	finally
      //   8	183	193	finally
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\dnd\SunDragSourceContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */