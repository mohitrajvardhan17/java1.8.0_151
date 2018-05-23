package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public class DragGestureEvent
  extends EventObject
{
  private static final long serialVersionUID = 9080172649166731306L;
  private transient List events;
  private DragSource dragSource;
  private Component component;
  private Point origin;
  private int action;
  
  public DragGestureEvent(DragGestureRecognizer paramDragGestureRecognizer, int paramInt, Point paramPoint, List<? extends InputEvent> paramList)
  {
    super(paramDragGestureRecognizer);
    if ((component = paramDragGestureRecognizer.getComponent()) == null) {
      throw new IllegalArgumentException("null component");
    }
    if ((dragSource = paramDragGestureRecognizer.getDragSource()) == null) {
      throw new IllegalArgumentException("null DragSource");
    }
    if ((paramList == null) || (paramList.isEmpty())) {
      throw new IllegalArgumentException("null or empty list of events");
    }
    if ((paramInt != 1) && (paramInt != 2) && (paramInt != 1073741824)) {
      throw new IllegalArgumentException("bad action");
    }
    if (paramPoint == null) {
      throw new IllegalArgumentException("null origin");
    }
    events = paramList;
    action = paramInt;
    origin = paramPoint;
  }
  
  public DragGestureRecognizer getSourceAsDragGestureRecognizer()
  {
    return (DragGestureRecognizer)getSource();
  }
  
  public Component getComponent()
  {
    return component;
  }
  
  public DragSource getDragSource()
  {
    return dragSource;
  }
  
  public Point getDragOrigin()
  {
    return origin;
  }
  
  public Iterator<InputEvent> iterator()
  {
    return events.iterator();
  }
  
  public Object[] toArray()
  {
    return events.toArray();
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    return events.toArray(paramArrayOfObject);
  }
  
  public int getDragAction()
  {
    return action;
  }
  
  public InputEvent getTriggerEvent()
  {
    return getSourceAsDragGestureRecognizer().getTriggerEvent();
  }
  
  public void startDrag(Cursor paramCursor, Transferable paramTransferable)
    throws InvalidDnDOperationException
  {
    dragSource.startDrag(this, paramCursor, paramTransferable, null);
  }
  
  public void startDrag(Cursor paramCursor, Transferable paramTransferable, DragSourceListener paramDragSourceListener)
    throws InvalidDnDOperationException
  {
    dragSource.startDrag(this, paramCursor, paramTransferable, paramDragSourceListener);
  }
  
  public void startDrag(Cursor paramCursor, Image paramImage, Point paramPoint, Transferable paramTransferable, DragSourceListener paramDragSourceListener)
    throws InvalidDnDOperationException
  {
    dragSource.startDrag(this, paramCursor, paramImage, paramPoint, paramTransferable, paramDragSourceListener);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(SerializationTester.test(events) ? events : null);
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
    Component localComponent = (Component)localGetField.get("component", null);
    if (localComponent == null) {
      throw new InvalidObjectException("null component");
    }
    component = localComponent;
    Point localPoint = (Point)localGetField.get("origin", null);
    if (localPoint == null) {
      throw new InvalidObjectException("null origin");
    }
    origin = localPoint;
    int i = localGetField.get("action", 0);
    if ((i != 1) && (i != 2) && (i != 1073741824)) {
      throw new InvalidObjectException("bad action");
    }
    action = i;
    List localList;
    try
    {
      localList = (List)localGetField.get("events", null);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      localList = (List)paramObjectInputStream.readObject();
    }
    if ((localList != null) && (localList.isEmpty())) {
      throw new InvalidObjectException("empty list of events");
    }
    if (localList == null) {
      localList = Collections.emptyList();
    }
    events = localList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DragGestureEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */