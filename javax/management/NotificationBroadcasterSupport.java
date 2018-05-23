package javax.management;

import com.sun.jmx.remote.util.ClassLogger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class NotificationBroadcasterSupport
  implements NotificationEmitter
{
  private List<ListenerInfo> listenerList = new CopyOnWriteArrayList();
  private final Executor executor;
  private final MBeanNotificationInfo[] notifInfo;
  private static final Executor defaultExecutor = new Executor()
  {
    public void execute(Runnable paramAnonymousRunnable)
    {
      paramAnonymousRunnable.run();
    }
  };
  private static final MBeanNotificationInfo[] NO_NOTIFICATION_INFO = new MBeanNotificationInfo[0];
  private static final ClassLogger logger = new ClassLogger("javax.management", "NotificationBroadcasterSupport");
  
  public NotificationBroadcasterSupport()
  {
    this(null, (MBeanNotificationInfo[])null);
  }
  
  public NotificationBroadcasterSupport(Executor paramExecutor)
  {
    this(paramExecutor, (MBeanNotificationInfo[])null);
  }
  
  public NotificationBroadcasterSupport(MBeanNotificationInfo... paramVarArgs)
  {
    this(null, paramVarArgs);
  }
  
  public NotificationBroadcasterSupport(Executor paramExecutor, MBeanNotificationInfo... paramVarArgs)
  {
    executor = (paramExecutor != null ? paramExecutor : defaultExecutor);
    notifInfo = (paramVarArgs == null ? NO_NOTIFICATION_INFO : (MBeanNotificationInfo[])paramVarArgs.clone());
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    if (paramNotificationListener == null) {
      throw new IllegalArgumentException("Listener can't be null");
    }
    listenerList.add(new ListenerInfo(paramNotificationListener, paramNotificationFilter, paramObject));
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    WildcardListenerInfo localWildcardListenerInfo = new WildcardListenerInfo(paramNotificationListener);
    boolean bool = listenerList.removeAll(Collections.singleton(localWildcardListenerInfo));
    if (!bool) {
      throw new ListenerNotFoundException("Listener not registered");
    }
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException
  {
    ListenerInfo localListenerInfo = new ListenerInfo(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool = listenerList.remove(localListenerInfo);
    if (!bool) {
      throw new ListenerNotFoundException("Listener not registered (with this filter and handback)");
    }
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    if (notifInfo.length == 0) {
      return notifInfo;
    }
    return (MBeanNotificationInfo[])notifInfo.clone();
  }
  
  public void sendNotification(Notification paramNotification)
  {
    if (paramNotification == null) {
      return;
    }
    Iterator localIterator = listenerList.iterator();
    while (localIterator.hasNext())
    {
      ListenerInfo localListenerInfo = (ListenerInfo)localIterator.next();
      int i;
      try
      {
        i = (filter == null) || (filter.isNotificationEnabled(paramNotification)) ? 1 : 0;
      }
      catch (Exception localException)
      {
        if (logger.debugOn()) {
          logger.debug("sendNotification", localException);
        }
      }
      continue;
      if (i != 0) {
        executor.execute(new SendNotifJob(paramNotification, localListenerInfo));
      }
    }
  }
  
  protected void handleNotification(NotificationListener paramNotificationListener, Notification paramNotification, Object paramObject)
  {
    paramNotificationListener.handleNotification(paramNotification, paramObject);
  }
  
  private static class ListenerInfo
  {
    NotificationListener listener;
    NotificationFilter filter;
    Object handback;
    
    ListenerInfo(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    {
      listener = paramNotificationListener;
      filter = paramNotificationFilter;
      handback = paramObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof ListenerInfo)) {
        return false;
      }
      ListenerInfo localListenerInfo = (ListenerInfo)paramObject;
      if ((localListenerInfo instanceof NotificationBroadcasterSupport.WildcardListenerInfo)) {
        return listener == listener;
      }
      return (listener == listener) && (filter == filter) && (handback == handback);
    }
    
    public int hashCode()
    {
      return Objects.hashCode(listener);
    }
  }
  
  private class SendNotifJob
    implements Runnable
  {
    private final Notification notif;
    private final NotificationBroadcasterSupport.ListenerInfo listenerInfo;
    
    public SendNotifJob(Notification paramNotification, NotificationBroadcasterSupport.ListenerInfo paramListenerInfo)
    {
      notif = paramNotification;
      listenerInfo = paramListenerInfo;
    }
    
    public void run()
    {
      try
      {
        handleNotification(listenerInfo.listener, notif, listenerInfo.handback);
      }
      catch (Exception localException)
      {
        if (NotificationBroadcasterSupport.logger.debugOn()) {
          NotificationBroadcasterSupport.logger.debug("SendNotifJob-run", localException);
        }
      }
    }
  }
  
  private static class WildcardListenerInfo
    extends NotificationBroadcasterSupport.ListenerInfo
  {
    WildcardListenerInfo(NotificationListener paramNotificationListener)
    {
      super(null, null);
    }
    
    public boolean equals(Object paramObject)
    {
      assert (!(paramObject instanceof WildcardListenerInfo));
      return paramObject.equals(this);
    }
    
    public int hashCode()
    {
      return super.hashCode();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\NotificationBroadcasterSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */