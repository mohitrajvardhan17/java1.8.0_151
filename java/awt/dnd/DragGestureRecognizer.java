package java.awt.dnd;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TooManyListenersException;

public abstract class DragGestureRecognizer
  implements Serializable
{
  private static final long serialVersionUID = 8996673345831063337L;
  protected DragSource dragSource;
  protected Component component;
  protected transient DragGestureListener dragGestureListener;
  protected int sourceActions;
  protected ArrayList<InputEvent> events = new ArrayList(1);
  
  protected DragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
  {
    if (paramDragSource == null) {
      throw new IllegalArgumentException("null DragSource");
    }
    dragSource = paramDragSource;
    component = paramComponent;
    sourceActions = (paramInt & 0x40000003);
    try
    {
      if (paramDragGestureListener != null) {
        addDragGestureListener(paramDragGestureListener);
      }
    }
    catch (TooManyListenersException localTooManyListenersException) {}
  }
  
  protected DragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt)
  {
    this(paramDragSource, paramComponent, paramInt, null);
  }
  
  protected DragGestureRecognizer(DragSource paramDragSource, Component paramComponent)
  {
    this(paramDragSource, paramComponent, 0);
  }
  
  protected DragGestureRecognizer(DragSource paramDragSource)
  {
    this(paramDragSource, null);
  }
  
  protected abstract void registerListeners();
  
  protected abstract void unregisterListeners();
  
  public DragSource getDragSource()
  {
    return dragSource;
  }
  
  public synchronized Component getComponent()
  {
    return component;
  }
  
  public synchronized void setComponent(Component paramComponent)
  {
    if ((component != null) && (dragGestureListener != null)) {
      unregisterListeners();
    }
    component = paramComponent;
    if ((component != null) && (dragGestureListener != null)) {
      registerListeners();
    }
  }
  
  public synchronized int getSourceActions()
  {
    return sourceActions;
  }
  
  public synchronized void setSourceActions(int paramInt)
  {
    sourceActions = (paramInt & 0x40000003);
  }
  
  public InputEvent getTriggerEvent()
  {
    return events.isEmpty() ? null : (InputEvent)events.get(0);
  }
  
  public void resetRecognizer()
  {
    events.clear();
  }
  
  public synchronized void addDragGestureListener(DragGestureListener paramDragGestureListener)
    throws TooManyListenersException
  {
    if (dragGestureListener != null) {
      throw new TooManyListenersException();
    }
    dragGestureListener = paramDragGestureListener;
    if (component != null) {
      registerListeners();
    }
  }
  
  public synchronized void removeDragGestureListener(DragGestureListener paramDragGestureListener)
  {
    if ((dragGestureListener == null) || (!dragGestureListener.equals(paramDragGestureListener))) {
      throw new IllegalArgumentException();
    }
    dragGestureListener = null;
    if (component != null) {
      unregisterListeners();
    }
  }
  
  /* Error */
  protected synchronized void fireDragGestureRecognized(int paramInt, java.awt.Point paramPoint)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 149	java/awt/dnd/DragGestureRecognizer:dragGestureListener	Ljava/awt/dnd/DragGestureListener;
    //   4: ifnull +26 -> 30
    //   7: aload_0
    //   8: getfield 149	java/awt/dnd/DragGestureRecognizer:dragGestureListener	Ljava/awt/dnd/DragGestureListener;
    //   11: new 78	java/awt/dnd/DragGestureEvent
    //   14: dup
    //   15: aload_0
    //   16: iload_1
    //   17: aload_2
    //   18: aload_0
    //   19: getfield 151	java/awt/dnd/DragGestureRecognizer:events	Ljava/util/ArrayList;
    //   22: invokespecial 152	java/awt/dnd/DragGestureEvent:<init>	(Ljava/awt/dnd/DragGestureRecognizer;ILjava/awt/Point;Ljava/util/List;)V
    //   25: invokeinterface 177 2 0
    //   30: aload_0
    //   31: getfield 151	java/awt/dnd/DragGestureRecognizer:events	Ljava/util/ArrayList;
    //   34: invokevirtual 171	java/util/ArrayList:clear	()V
    //   37: goto +13 -> 50
    //   40: astore_3
    //   41: aload_0
    //   42: getfield 151	java/awt/dnd/DragGestureRecognizer:events	Ljava/util/ArrayList;
    //   45: invokevirtual 171	java/util/ArrayList:clear	()V
    //   48: aload_3
    //   49: athrow
    //   50: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	51	0	this	DragGestureRecognizer
    //   0	51	1	paramInt	int
    //   0	51	2	paramPoint	java.awt.Point
    //   40	9	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	30	40	finally
  }
  
  protected synchronized void appendEvent(InputEvent paramInputEvent)
  {
    events.add(paramInputEvent);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(SerializationTester.test(dragGestureListener) ? dragGestureListener : null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    DragSource localDragSource = (DragSource)localGetField.get("dragSource", null);
    if (localDragSource == null) {
      throw new InvalidObjectException("null DragSource");
    }
    dragSource = localDragSource;
    component = ((Component)localGetField.get("component", null));
    sourceActions = (localGetField.get("sourceActions", 0) & 0x40000003);
    events = ((ArrayList)localGetField.get("events", new ArrayList(1)));
    dragGestureListener = ((DragGestureListener)paramObjectInputStream.readObject());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DragGestureRecognizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */