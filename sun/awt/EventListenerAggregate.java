package sun.awt;

import java.lang.reflect.Array;
import java.util.EventListener;

public class EventListenerAggregate
{
  private EventListener[] listenerList;
  
  public EventListenerAggregate(Class<? extends EventListener> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException("listener class is null");
    }
    listenerList = ((EventListener[])Array.newInstance(paramClass, 0));
  }
  
  private Class<?> getListenerClass()
  {
    return listenerList.getClass().getComponentType();
  }
  
  public synchronized void add(EventListener paramEventListener)
  {
    Class localClass = getListenerClass();
    if (!localClass.isInstance(paramEventListener)) {
      throw new ClassCastException("listener " + paramEventListener + " is not an instance of listener class " + localClass);
    }
    EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(localClass, listenerList.length + 1);
    System.arraycopy(listenerList, 0, arrayOfEventListener, 0, listenerList.length);
    arrayOfEventListener[listenerList.length] = paramEventListener;
    listenerList = arrayOfEventListener;
  }
  
  public synchronized boolean remove(EventListener paramEventListener)
  {
    Class localClass = getListenerClass();
    if (!localClass.isInstance(paramEventListener)) {
      throw new ClassCastException("listener " + paramEventListener + " is not an instance of listener class " + localClass);
    }
    for (int i = 0; i < listenerList.length; i++) {
      if (listenerList[i].equals(paramEventListener))
      {
        EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(localClass, listenerList.length - 1);
        System.arraycopy(listenerList, 0, arrayOfEventListener, 0, i);
        System.arraycopy(listenerList, i + 1, arrayOfEventListener, i, listenerList.length - i - 1);
        listenerList = arrayOfEventListener;
        return true;
      }
    }
    return false;
  }
  
  public synchronized EventListener[] getListenersInternal()
  {
    return listenerList;
  }
  
  public synchronized EventListener[] getListenersCopy()
  {
    return listenerList.length == 0 ? listenerList : (EventListener[])listenerList.clone();
  }
  
  public synchronized int size()
  {
    return listenerList.length;
  }
  
  public synchronized boolean isEmpty()
  {
    return listenerList.length == 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\EventListenerAggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */