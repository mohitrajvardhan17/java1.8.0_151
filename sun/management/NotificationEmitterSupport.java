package sun.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

abstract class NotificationEmitterSupport
  implements NotificationEmitter
{
  private Object listenerLock = new Object();
  private List<ListenerInfo> listenerList = Collections.emptyList();
  
  protected NotificationEmitterSupport() {}
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    if (paramNotificationListener == null) {
      throw new IllegalArgumentException("Listener can't be null");
    }
    synchronized (listenerLock)
    {
      ArrayList localArrayList = new ArrayList(listenerList.size() + 1);
      localArrayList.addAll(listenerList);
      localArrayList.add(new ListenerInfo(paramNotificationListener, paramNotificationFilter, paramObject));
      listenerList = localArrayList;
    }
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    synchronized (listenerLock)
    {
      ArrayList localArrayList = new ArrayList(listenerList);
      for (int i = localArrayList.size() - 1; i >= 0; i--)
      {
        ListenerInfo localListenerInfo = (ListenerInfo)localArrayList.get(i);
        if (listener == paramNotificationListener) {
          localArrayList.remove(i);
        }
      }
      if (localArrayList.size() == listenerList.size()) {
        throw new ListenerNotFoundException("Listener not registered");
      }
      listenerList = localArrayList;
    }
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException
  {
    int i = 0;
    synchronized (listenerLock)
    {
      ArrayList localArrayList = new ArrayList(listenerList);
      int j = localArrayList.size();
      for (int k = 0; k < j; k++)
      {
        ListenerInfo localListenerInfo = (ListenerInfo)localArrayList.get(k);
        if (listener == paramNotificationListener)
        {
          i = 1;
          if ((filter == paramNotificationFilter) && (handback == paramObject))
          {
            localArrayList.remove(k);
            listenerList = localArrayList;
            return;
          }
        }
      }
    }
    if (i != 0) {
      throw new ListenerNotFoundException("Listener not registered with this filter and handback");
    }
    throw new ListenerNotFoundException("Listener not registered");
  }
  
  void sendNotification(Notification paramNotification)
  {
    if (paramNotification == null) {
      return;
    }
    List localList;
    synchronized (listenerLock)
    {
      localList = listenerList;
    }
    ??? = localList.size();
    for (Object localObject2 = 0; localObject2 < ???; localObject2++)
    {
      ListenerInfo localListenerInfo = (ListenerInfo)localList.get(localObject2);
      if ((filter == null) || (filter.isNotificationEnabled(paramNotification))) {
        try
        {
          listener.handleNotification(paramNotification, handback);
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
          throw new AssertionError("Error in invoking listener");
        }
      }
    }
  }
  
  boolean hasListeners()
  {
    synchronized (listenerLock)
    {
      return !listenerList.isEmpty();
    }
  }
  
  public abstract MBeanNotificationInfo[] getNotificationInfo();
  
  private class ListenerInfo
  {
    public NotificationListener listener;
    NotificationFilter filter;
    Object handback;
    
    public ListenerInfo(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    {
      listener = paramNotificationListener;
      filter = paramNotificationFilter;
      handback = paramObject;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\NotificationEmitterSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */