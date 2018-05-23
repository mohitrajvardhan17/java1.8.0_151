package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.util.EventListener;
import sun.awt.dnd.SunDragSourceContextPeer;
import sun.security.action.GetIntegerAction;

public class DragSource
  implements Serializable
{
  private static final long serialVersionUID = 6236096958971414066L;
  public static final Cursor DefaultCopyDrop = load("DnD.Cursor.CopyDrop");
  public static final Cursor DefaultMoveDrop = load("DnD.Cursor.MoveDrop");
  public static final Cursor DefaultLinkDrop = load("DnD.Cursor.LinkDrop");
  public static final Cursor DefaultCopyNoDrop = load("DnD.Cursor.CopyNoDrop");
  public static final Cursor DefaultMoveNoDrop = load("DnD.Cursor.MoveNoDrop");
  public static final Cursor DefaultLinkNoDrop = load("DnD.Cursor.LinkNoDrop");
  private static final DragSource dflt = GraphicsEnvironment.isHeadless() ? null : new DragSource();
  static final String dragSourceListenerK = "dragSourceL";
  static final String dragSourceMotionListenerK = "dragSourceMotionL";
  private transient FlavorMap flavorMap = SystemFlavorMap.getDefaultFlavorMap();
  private transient DragSourceListener listener;
  private transient DragSourceMotionListener motionListener;
  
  private static Cursor load(String paramString)
  {
    if (GraphicsEnvironment.isHeadless()) {
      return null;
    }
    try
    {
      return (Cursor)Toolkit.getDefaultToolkit().getDesktopProperty(paramString);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      throw new RuntimeException("failed to load system cursor: " + paramString + " : " + localException.getMessage());
    }
  }
  
  public static DragSource getDefaultDragSource()
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    return dflt;
  }
  
  public static boolean isDragImageSupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    try
    {
      Boolean localBoolean = (Boolean)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.isDragImageSupported");
      return localBoolean.booleanValue();
    }
    catch (Exception localException) {}
    return false;
  }
  
  public DragSource()
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
  }
  
  public void startDrag(DragGestureEvent paramDragGestureEvent, Cursor paramCursor, Image paramImage, Point paramPoint, Transferable paramTransferable, DragSourceListener paramDragSourceListener, FlavorMap paramFlavorMap)
    throws InvalidDnDOperationException
  {
    SunDragSourceContextPeer.setDragDropInProgress(true);
    try
    {
      if (paramFlavorMap != null) {
        flavorMap = paramFlavorMap;
      }
      DragSourceContextPeer localDragSourceContextPeer = Toolkit.getDefaultToolkit().createDragSourceContextPeer(paramDragGestureEvent);
      DragSourceContext localDragSourceContext = createDragSourceContext(localDragSourceContextPeer, paramDragGestureEvent, paramCursor, paramImage, paramPoint, paramTransferable, paramDragSourceListener);
      if (localDragSourceContext == null) {
        throw new InvalidDnDOperationException();
      }
      localDragSourceContextPeer.startDrag(localDragSourceContext, localDragSourceContext.getCursor(), paramImage, paramPoint);
    }
    catch (RuntimeException localRuntimeException)
    {
      SunDragSourceContextPeer.setDragDropInProgress(false);
      throw localRuntimeException;
    }
  }
  
  public void startDrag(DragGestureEvent paramDragGestureEvent, Cursor paramCursor, Transferable paramTransferable, DragSourceListener paramDragSourceListener, FlavorMap paramFlavorMap)
    throws InvalidDnDOperationException
  {
    startDrag(paramDragGestureEvent, paramCursor, null, null, paramTransferable, paramDragSourceListener, paramFlavorMap);
  }
  
  public void startDrag(DragGestureEvent paramDragGestureEvent, Cursor paramCursor, Image paramImage, Point paramPoint, Transferable paramTransferable, DragSourceListener paramDragSourceListener)
    throws InvalidDnDOperationException
  {
    startDrag(paramDragGestureEvent, paramCursor, paramImage, paramPoint, paramTransferable, paramDragSourceListener, null);
  }
  
  public void startDrag(DragGestureEvent paramDragGestureEvent, Cursor paramCursor, Transferable paramTransferable, DragSourceListener paramDragSourceListener)
    throws InvalidDnDOperationException
  {
    startDrag(paramDragGestureEvent, paramCursor, null, null, paramTransferable, paramDragSourceListener, null);
  }
  
  protected DragSourceContext createDragSourceContext(DragSourceContextPeer paramDragSourceContextPeer, DragGestureEvent paramDragGestureEvent, Cursor paramCursor, Image paramImage, Point paramPoint, Transferable paramTransferable, DragSourceListener paramDragSourceListener)
  {
    return new DragSourceContext(paramDragSourceContextPeer, paramDragGestureEvent, paramCursor, paramImage, paramPoint, paramTransferable, paramDragSourceListener);
  }
  
  public FlavorMap getFlavorMap()
  {
    return flavorMap;
  }
  
  public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
  {
    return Toolkit.getDefaultToolkit().createDragGestureRecognizer(paramClass, this, paramComponent, paramInt, paramDragGestureListener);
  }
  
  public DragGestureRecognizer createDefaultDragGestureRecognizer(Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
  {
    return Toolkit.getDefaultToolkit().createDragGestureRecognizer(MouseDragGestureRecognizer.class, this, paramComponent, paramInt, paramDragGestureListener);
  }
  
  public void addDragSourceListener(DragSourceListener paramDragSourceListener)
  {
    if (paramDragSourceListener != null) {
      synchronized (this)
      {
        listener = DnDEventMulticaster.add(listener, paramDragSourceListener);
      }
    }
  }
  
  public void removeDragSourceListener(DragSourceListener paramDragSourceListener)
  {
    if (paramDragSourceListener != null) {
      synchronized (this)
      {
        listener = DnDEventMulticaster.remove(listener, paramDragSourceListener);
      }
    }
  }
  
  public DragSourceListener[] getDragSourceListeners()
  {
    return (DragSourceListener[])getListeners(DragSourceListener.class);
  }
  
  public void addDragSourceMotionListener(DragSourceMotionListener paramDragSourceMotionListener)
  {
    if (paramDragSourceMotionListener != null) {
      synchronized (this)
      {
        motionListener = DnDEventMulticaster.add(motionListener, paramDragSourceMotionListener);
      }
    }
  }
  
  public void removeDragSourceMotionListener(DragSourceMotionListener paramDragSourceMotionListener)
  {
    if (paramDragSourceMotionListener != null) {
      synchronized (this)
      {
        motionListener = DnDEventMulticaster.remove(motionListener, paramDragSourceMotionListener);
      }
    }
  }
  
  public DragSourceMotionListener[] getDragSourceMotionListeners()
  {
    return (DragSourceMotionListener[])getListeners(DragSourceMotionListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    Object localObject = null;
    if (paramClass == DragSourceListener.class) {
      localObject = listener;
    } else if (paramClass == DragSourceMotionListener.class) {
      localObject = motionListener;
    }
    return DnDEventMulticaster.getListeners((EventListener)localObject, paramClass);
  }
  
  void processDragEnter(DragSourceDragEvent paramDragSourceDragEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dragEnter(paramDragSourceDragEvent);
    }
  }
  
  void processDragOver(DragSourceDragEvent paramDragSourceDragEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dragOver(paramDragSourceDragEvent);
    }
  }
  
  void processDropActionChanged(DragSourceDragEvent paramDragSourceDragEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dropActionChanged(paramDragSourceDragEvent);
    }
  }
  
  void processDragExit(DragSourceEvent paramDragSourceEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dragExit(paramDragSourceEvent);
    }
  }
  
  void processDragDropEnd(DragSourceDropEvent paramDragSourceDropEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dragDropEnd(paramDragSourceDropEvent);
    }
  }
  
  void processDragMouseMoved(DragSourceDragEvent paramDragSourceDragEvent)
  {
    DragSourceMotionListener localDragSourceMotionListener = motionListener;
    if (localDragSourceMotionListener != null) {
      localDragSourceMotionListener.dragMouseMoved(paramDragSourceDragEvent);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(SerializationTester.test(flavorMap) ? flavorMap : null);
    DnDEventMulticaster.save(paramObjectOutputStream, "dragSourceL", listener);
    DnDEventMulticaster.save(paramObjectOutputStream, "dragSourceMotionL", motionListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    flavorMap = ((FlavorMap)paramObjectInputStream.readObject());
    if (flavorMap == null) {
      flavorMap = SystemFlavorMap.getDefaultFlavorMap();
    }
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject()))
    {
      String str = ((String)localObject).intern();
      if ("dragSourceL" == str) {
        addDragSourceListener((DragSourceListener)paramObjectInputStream.readObject());
      } else if ("dragSourceMotionL" == str) {
        addDragSourceMotionListener((DragSourceMotionListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
  }
  
  public static int getDragThreshold()
  {
    int i = ((Integer)AccessController.doPrivileged(new GetIntegerAction("awt.dnd.drag.threshold", 0))).intValue();
    if (i > 0) {
      return i;
    }
    Integer localInteger = (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.gestureMotionThreshold");
    if (localInteger != null) {
      return localInteger.intValue();
    }
    return 5;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DragSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */