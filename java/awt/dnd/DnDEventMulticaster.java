package java.awt.dnd;

import java.awt.AWTEventMulticaster;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.EventListener;

class DnDEventMulticaster
  extends AWTEventMulticaster
  implements DragSourceListener, DragSourceMotionListener
{
  protected DnDEventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2)
  {
    super(paramEventListener1, paramEventListener2);
  }
  
  public void dragEnter(DragSourceDragEvent paramDragSourceDragEvent)
  {
    ((DragSourceListener)a).dragEnter(paramDragSourceDragEvent);
    ((DragSourceListener)b).dragEnter(paramDragSourceDragEvent);
  }
  
  public void dragOver(DragSourceDragEvent paramDragSourceDragEvent)
  {
    ((DragSourceListener)a).dragOver(paramDragSourceDragEvent);
    ((DragSourceListener)b).dragOver(paramDragSourceDragEvent);
  }
  
  public void dropActionChanged(DragSourceDragEvent paramDragSourceDragEvent)
  {
    ((DragSourceListener)a).dropActionChanged(paramDragSourceDragEvent);
    ((DragSourceListener)b).dropActionChanged(paramDragSourceDragEvent);
  }
  
  public void dragExit(DragSourceEvent paramDragSourceEvent)
  {
    ((DragSourceListener)a).dragExit(paramDragSourceEvent);
    ((DragSourceListener)b).dragExit(paramDragSourceEvent);
  }
  
  public void dragDropEnd(DragSourceDropEvent paramDragSourceDropEvent)
  {
    ((DragSourceListener)a).dragDropEnd(paramDragSourceDropEvent);
    ((DragSourceListener)b).dragDropEnd(paramDragSourceDropEvent);
  }
  
  public void dragMouseMoved(DragSourceDragEvent paramDragSourceDragEvent)
  {
    ((DragSourceMotionListener)a).dragMouseMoved(paramDragSourceDragEvent);
    ((DragSourceMotionListener)b).dragMouseMoved(paramDragSourceDragEvent);
  }
  
  public static DragSourceListener add(DragSourceListener paramDragSourceListener1, DragSourceListener paramDragSourceListener2)
  {
    return (DragSourceListener)addInternal(paramDragSourceListener1, paramDragSourceListener2);
  }
  
  public static DragSourceMotionListener add(DragSourceMotionListener paramDragSourceMotionListener1, DragSourceMotionListener paramDragSourceMotionListener2)
  {
    return (DragSourceMotionListener)addInternal(paramDragSourceMotionListener1, paramDragSourceMotionListener2);
  }
  
  public static DragSourceListener remove(DragSourceListener paramDragSourceListener1, DragSourceListener paramDragSourceListener2)
  {
    return (DragSourceListener)removeInternal(paramDragSourceListener1, paramDragSourceListener2);
  }
  
  public static DragSourceMotionListener remove(DragSourceMotionListener paramDragSourceMotionListener1, DragSourceMotionListener paramDragSourceMotionListener2)
  {
    return (DragSourceMotionListener)removeInternal(paramDragSourceMotionListener1, paramDragSourceMotionListener2);
  }
  
  protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2)
  {
    if (paramEventListener1 == null) {
      return paramEventListener2;
    }
    if (paramEventListener2 == null) {
      return paramEventListener1;
    }
    return new DnDEventMulticaster(paramEventListener1, paramEventListener2);
  }
  
  protected EventListener remove(EventListener paramEventListener)
  {
    if (paramEventListener == a) {
      return b;
    }
    if (paramEventListener == b) {
      return a;
    }
    EventListener localEventListener1 = removeInternal(a, paramEventListener);
    EventListener localEventListener2 = removeInternal(b, paramEventListener);
    if ((localEventListener1 == a) && (localEventListener2 == b)) {
      return this;
    }
    return addInternal(localEventListener1, localEventListener2);
  }
  
  protected static EventListener removeInternal(EventListener paramEventListener1, EventListener paramEventListener2)
  {
    if ((paramEventListener1 == paramEventListener2) || (paramEventListener1 == null)) {
      return null;
    }
    if ((paramEventListener1 instanceof DnDEventMulticaster)) {
      return ((DnDEventMulticaster)paramEventListener1).remove(paramEventListener2);
    }
    return paramEventListener1;
  }
  
  protected static void save(ObjectOutputStream paramObjectOutputStream, String paramString, EventListener paramEventListener)
    throws IOException
  {
    AWTEventMulticaster.save(paramObjectOutputStream, paramString, paramEventListener);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DnDEventMulticaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */