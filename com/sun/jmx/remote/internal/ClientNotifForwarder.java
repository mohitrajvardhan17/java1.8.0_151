package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.NotSerializableException;
import java.rmi.UnmarshalException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;
import javax.security.auth.Subject;

public abstract class ClientNotifForwarder
{
  private final AccessControlContext acc;
  private static int threadId;
  private final ClassLoader defaultClassLoader;
  private final Executor executor;
  private final Map<Integer, ClientListenerInfo> infoList = new HashMap();
  private long clientSequenceNumber = -1L;
  private final int maxNotifications;
  private final long timeout;
  private Integer mbeanRemovedNotifID = null;
  private Thread currentFetchThread;
  private static final int STARTING = 0;
  private static final int STARTED = 1;
  private static final int STOPPING = 2;
  private static final int STOPPED = 3;
  private static final int TERMINATED = 4;
  private int state = 3;
  private boolean beingReconnected = false;
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ClientNotifForwarder");
  
  public ClientNotifForwarder(Map paramMap)
  {
    this(null, paramMap);
  }
  
  public ClientNotifForwarder(ClassLoader paramClassLoader, Map<String, ?> paramMap)
  {
    maxNotifications = EnvHelp.getMaxFetchNotifNumber(paramMap);
    timeout = EnvHelp.getFetchTimeout(paramMap);
    Object localObject = (Executor)paramMap.get("jmx.remote.x.fetch.notifications.executor");
    if (localObject == null) {
      localObject = new LinearExecutor(null);
    } else if (logger.traceOn()) {
      logger.trace("ClientNotifForwarder", "executor is " + localObject);
    }
    defaultClassLoader = paramClassLoader;
    executor = ((Executor)localObject);
    acc = AccessController.getContext();
  }
  
  protected abstract NotificationResult fetchNotifs(long paramLong1, int paramInt, long paramLong2)
    throws IOException, ClassNotFoundException;
  
  protected abstract Integer addListenerForMBeanRemovedNotif()
    throws IOException, InstanceNotFoundException;
  
  protected abstract void removeListenerForMBeanRemovedNotif(Integer paramInteger)
    throws IOException, InstanceNotFoundException, ListenerNotFoundException;
  
  protected abstract void lostNotifs(String paramString, long paramLong);
  
  public synchronized void addNotificationListener(Integer paramInteger, ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject, Subject paramSubject)
    throws IOException, InstanceNotFoundException
  {
    if (logger.traceOn()) {
      logger.trace("addNotificationListener", "Add the listener " + paramNotificationListener + " at " + paramObjectName);
    }
    infoList.put(paramInteger, new ClientListenerInfo(paramInteger, paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject, paramSubject));
    init(false);
  }
  
  public synchronized Integer[] removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener)
    throws ListenerNotFoundException, IOException
  {
    beforeRemove();
    if (logger.traceOn()) {
      logger.trace("removeNotificationListener", "Remove the listener " + paramNotificationListener + " from " + paramObjectName);
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList(infoList.values());
    for (int i = localArrayList2.size() - 1; i >= 0; i--)
    {
      ClientListenerInfo localClientListenerInfo = (ClientListenerInfo)localArrayList2.get(i);
      if (localClientListenerInfo.sameAs(paramObjectName, paramNotificationListener))
      {
        localArrayList1.add(localClientListenerInfo.getListenerID());
        infoList.remove(localClientListenerInfo.getListenerID());
      }
    }
    if (localArrayList1.isEmpty()) {
      throw new ListenerNotFoundException("Listener not found");
    }
    return (Integer[])localArrayList1.toArray(new Integer[0]);
  }
  
  public synchronized Integer removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException, IOException
  {
    if (logger.traceOn()) {
      logger.trace("removeNotificationListener", "Remove the listener " + paramNotificationListener + " from " + paramObjectName);
    }
    beforeRemove();
    Integer localInteger = null;
    ArrayList localArrayList = new ArrayList(infoList.values());
    for (int i = localArrayList.size() - 1; i >= 0; i--)
    {
      ClientListenerInfo localClientListenerInfo = (ClientListenerInfo)localArrayList.get(i);
      if (localClientListenerInfo.sameAs(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject))
      {
        localInteger = localClientListenerInfo.getListenerID();
        infoList.remove(localInteger);
        break;
      }
    }
    if (localInteger == null) {
      throw new ListenerNotFoundException("Listener not found");
    }
    return localInteger;
  }
  
  public synchronized Integer[] removeNotificationListener(ObjectName paramObjectName)
  {
    if (logger.traceOn()) {
      logger.trace("removeNotificationListener", "Remove all listeners registered at " + paramObjectName);
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList(infoList.values());
    for (int i = localArrayList2.size() - 1; i >= 0; i--)
    {
      ClientListenerInfo localClientListenerInfo = (ClientListenerInfo)localArrayList2.get(i);
      if (localClientListenerInfo.sameAs(paramObjectName))
      {
        localArrayList1.add(localClientListenerInfo.getListenerID());
        infoList.remove(localClientListenerInfo.getListenerID());
      }
    }
    return (Integer[])localArrayList1.toArray(new Integer[0]);
  }
  
  public synchronized ClientListenerInfo[] preReconnection()
    throws IOException
  {
    if ((state == 4) || (beingReconnected)) {
      throw new IOException("Illegal state.");
    }
    ClientListenerInfo[] arrayOfClientListenerInfo = (ClientListenerInfo[])infoList.values().toArray(new ClientListenerInfo[0]);
    beingReconnected = true;
    infoList.clear();
    return arrayOfClientListenerInfo;
  }
  
  public synchronized void postReconnection(ClientListenerInfo[] paramArrayOfClientListenerInfo)
    throws IOException
  {
    if (state == 4) {
      return;
    }
    while (state == 2) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException1)
      {
        IOException localIOException1 = new IOException(localInterruptedException1.toString());
        EnvHelp.initCause(localIOException1, localInterruptedException1);
        throw localIOException1;
      }
    }
    boolean bool = logger.traceOn();
    int i = paramArrayOfClientListenerInfo.length;
    for (int j = 0; j < i; j++)
    {
      if (bool) {
        logger.trace("addNotificationListeners", "Add a listener at " + paramArrayOfClientListenerInfo[j].getListenerID());
      }
      infoList.put(paramArrayOfClientListenerInfo[j].getListenerID(), paramArrayOfClientListenerInfo[j]);
    }
    beingReconnected = false;
    notifyAll();
    if ((currentFetchThread == Thread.currentThread()) || (state == 0) || (state == 1))
    {
      try
      {
        mbeanRemovedNotifID = addListenerForMBeanRemovedNotif();
      }
      catch (Exception localException)
      {
        if (logger.traceOn()) {
          logger.trace("init", "Failed to register a listener to the mbean server: the client will not do clean when an MBean is unregistered", localException);
        }
      }
    }
    else
    {
      while (state == 2) {
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException2)
        {
          IOException localIOException2 = new IOException(localInterruptedException2.toString());
          EnvHelp.initCause(localIOException2, localInterruptedException2);
          throw localIOException2;
        }
      }
      if (paramArrayOfClientListenerInfo.length > 0) {
        init(true);
      } else if (infoList.size() > 0) {
        init(false);
      }
    }
  }
  
  public synchronized void terminate()
  {
    if (state == 4) {
      return;
    }
    if (logger.traceOn()) {
      logger.trace("terminate", "Terminating...");
    }
    if (state == 1) {
      infoList.clear();
    }
    setState(4);
  }
  
  private synchronized void setState(int paramInt)
  {
    if (state == 4) {
      return;
    }
    state = paramInt;
    notifyAll();
  }
  
  private synchronized void init(boolean paramBoolean)
    throws IOException
  {
    switch (state)
    {
    case 1: 
      return;
    case 0: 
      return;
    case 4: 
      throw new IOException("The ClientNotifForwarder has been terminated.");
    case 2: 
      if (beingReconnected == true) {
        return;
      }
      while (state == 2) {
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException)
        {
          IOException localIOException = new IOException(localInterruptedException.toString());
          EnvHelp.initCause(localIOException, localInterruptedException);
          throw localIOException;
        }
      }
      init(paramBoolean);
      return;
    case 3: 
      if (beingReconnected == true) {
        return;
      }
      if (logger.traceOn()) {
        logger.trace("init", "Initializing...");
      }
      if (!paramBoolean) {
        try
        {
          NotificationResult localNotificationResult = fetchNotifs(-1L, 0, 0L);
          if (state != 3) {
            return;
          }
          clientSequenceNumber = localNotificationResult.getNextSequenceNumber();
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          logger.warning("init", "Impossible exception: " + localClassNotFoundException);
          logger.debug("init", localClassNotFoundException);
        }
      }
      try
      {
        mbeanRemovedNotifID = addListenerForMBeanRemovedNotif();
      }
      catch (Exception localException)
      {
        if (logger.traceOn()) {
          logger.trace("init", "Failed to register a listener to the mbean server: the client will not do clean when an MBean is unregistered", localException);
        }
      }
      setState(0);
      executor.execute(new NotifFetcher(null));
      return;
    }
    throw new IOException("Unknown state.");
  }
  
  private synchronized void beforeRemove()
    throws IOException
  {
    while (beingReconnected)
    {
      if (state == 4) {
        throw new IOException("Terminated.");
      }
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException)
      {
        IOException localIOException = new IOException(localInterruptedException.toString());
        EnvHelp.initCause(localIOException, localInterruptedException);
        throw localIOException;
      }
    }
    if (state == 4) {
      throw new IOException("Terminated.");
    }
  }
  
  private static class LinearExecutor
    implements Executor
  {
    private Runnable command;
    private Thread thread;
    
    private LinearExecutor() {}
    
    public synchronized void execute(Runnable paramRunnable)
    {
      if (command != null) {
        throw new IllegalArgumentException("More than one command");
      }
      command = paramRunnable;
      if (thread == null)
      {
        thread = new Thread()
        {
          public void run()
          {
            for (;;)
            {
              Runnable localRunnable;
              synchronized (ClientNotifForwarder.LinearExecutor.this)
              {
                if (command == null)
                {
                  thread = null;
                  return;
                }
                localRunnable = command;
                command = null;
              }
              localRunnable.run();
            }
          }
        };
        thread.setDaemon(true);
        thread.setName("ClientNotifForwarder-" + ClientNotifForwarder.access$204());
        thread.start();
      }
    }
  }
  
  private class NotifFetcher
    implements Runnable
  {
    private volatile boolean alreadyLogged = false;
    
    private NotifFetcher() {}
    
    private void logOnce(String paramString, SecurityException paramSecurityException)
    {
      if (alreadyLogged) {
        return;
      }
      ClientNotifForwarder.logger.config("setContextClassLoader", paramString);
      if (paramSecurityException != null) {
        ClientNotifForwarder.logger.fine("setContextClassLoader", paramSecurityException);
      }
      alreadyLogged = true;
    }
    
    private final ClassLoader setContextClassLoader(final ClassLoader paramClassLoader)
    {
      AccessControlContext localAccessControlContext = acc;
      if (localAccessControlContext == null)
      {
        logOnce("AccessControlContext must not be null.", null);
        throw new SecurityException("AccessControlContext must not be null");
      }
      (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
      {
        public ClassLoader run()
        {
          try
          {
            ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
            if (paramClassLoader == localClassLoader) {
              return localClassLoader;
            }
            Thread.currentThread().setContextClassLoader(paramClassLoader);
            return localClassLoader;
          }
          catch (SecurityException localSecurityException)
          {
            ClientNotifForwarder.NotifFetcher.this.logOnce("Permission to set ContextClassLoader missing. Notifications will not be dispatched. Please check your Java policy configuration: " + localSecurityException, localSecurityException);
            throw localSecurityException;
          }
        }
      }, localAccessControlContext);
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 287	com/sun/jmx/remote/internal/ClientNotifForwarder$NotifFetcher:this$0	Lcom/sun/jmx/remote/internal/ClientNotifForwarder;
      //   4: invokestatic 300	com/sun/jmx/remote/internal/ClientNotifForwarder:access$700	(Lcom/sun/jmx/remote/internal/ClientNotifForwarder;)Ljava/lang/ClassLoader;
      //   7: ifnull +18 -> 25
      //   10: aload_0
      //   11: aload_0
      //   12: getfield 287	com/sun/jmx/remote/internal/ClientNotifForwarder$NotifFetcher:this$0	Lcom/sun/jmx/remote/internal/ClientNotifForwarder;
      //   15: invokestatic 300	com/sun/jmx/remote/internal/ClientNotifForwarder:access$700	(Lcom/sun/jmx/remote/internal/ClientNotifForwarder;)Ljava/lang/ClassLoader;
      //   18: invokespecial 312	com/sun/jmx/remote/internal/ClientNotifForwarder$NotifFetcher:setContextClassLoader	(Ljava/lang/ClassLoader;)Ljava/lang/ClassLoader;
      //   21: astore_1
      //   22: goto +5 -> 27
      //   25: aconst_null
      //   26: astore_1
      //   27: aload_0
      //   28: invokespecial 307	com/sun/jmx/remote/internal/ClientNotifForwarder$NotifFetcher:doRun	()V
      //   31: aload_0
      //   32: getfield 287	com/sun/jmx/remote/internal/ClientNotifForwarder$NotifFetcher:this$0	Lcom/sun/jmx/remote/internal/ClientNotifForwarder;
      //   35: invokestatic 300	com/sun/jmx/remote/internal/ClientNotifForwarder:access$700	(Lcom/sun/jmx/remote/internal/ClientNotifForwarder;)Ljava/lang/ClassLoader;
      //   38: ifnull +31 -> 69
      //   41: aload_0
      //   42: aload_1
      //   43: invokespecial 312	com/sun/jmx/remote/internal/ClientNotifForwarder$NotifFetcher:setContextClassLoader	(Ljava/lang/ClassLoader;)Ljava/lang/ClassLoader;
      //   46: pop
      //   47: goto +22 -> 69
      //   50: astore_2
      //   51: aload_0
      //   52: getfield 287	com/sun/jmx/remote/internal/ClientNotifForwarder$NotifFetcher:this$0	Lcom/sun/jmx/remote/internal/ClientNotifForwarder;
      //   55: invokestatic 300	com/sun/jmx/remote/internal/ClientNotifForwarder:access$700	(Lcom/sun/jmx/remote/internal/ClientNotifForwarder;)Ljava/lang/ClassLoader;
      //   58: ifnull +9 -> 67
      //   61: aload_0
      //   62: aload_1
      //   63: invokespecial 312	com/sun/jmx/remote/internal/ClientNotifForwarder$NotifFetcher:setContextClassLoader	(Ljava/lang/ClassLoader;)Ljava/lang/ClassLoader;
      //   66: pop
      //   67: aload_2
      //   68: athrow
      //   69: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	70	0	this	NotifFetcher
      //   21	42	1	localClassLoader	ClassLoader
      //   50	18	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   27	31	50	finally
    }
    
    private void doRun()
    {
      synchronized (ClientNotifForwarder.this)
      {
        currentFetchThread = Thread.currentThread();
        if (state == 0) {
          ClientNotifForwarder.this.setState(1);
        }
      }
      ??? = null;
      if ((!shouldStop()) && ((??? = fetchNotifs()) != null))
      {
        TargetedNotification[] arrayOfTargetedNotification = ((NotificationResult)???).getTargetedNotifications();
        Object localObject2 = arrayOfTargetedNotification.length;
        long l = 0L;
        HashMap localHashMap;
        Object localObject3;
        Integer localInteger1;
        synchronized (ClientNotifForwarder.this)
        {
          if (clientSequenceNumber >= 0L) {
            l = ((NotificationResult)???).getEarliestSequenceNumber() - clientSequenceNumber;
          }
          clientSequenceNumber = ((NotificationResult)???).getNextSequenceNumber();
          localHashMap = new HashMap();
          for (localObject3 = 0; localObject3 < localObject2; localObject3++)
          {
            TargetedNotification localTargetedNotification = arrayOfTargetedNotification[localObject3];
            Integer localInteger2 = localTargetedNotification.getListenerID();
            Object localObject4;
            if (!localInteger2.equals(mbeanRemovedNotifID))
            {
              localObject4 = (ClientListenerInfo)infoList.get(localInteger2);
              if (localObject4 != null) {
                localHashMap.put(localInteger2, localObject4);
              }
            }
            else
            {
              localObject4 = localTargetedNotification.getNotification();
              if (((localObject4 instanceof MBeanServerNotification)) && (((Notification)localObject4).getType().equals("JMX.mbean.unregistered")))
              {
                MBeanServerNotification localMBeanServerNotification = (MBeanServerNotification)localObject4;
                ObjectName localObjectName = localMBeanServerNotification.getMBeanName();
                removeNotificationListener(localObjectName);
              }
            }
          }
          localInteger1 = mbeanRemovedNotifID;
        }
        if (l > 0L)
        {
          ??? = "May have lost up to " + l + " notification" + (l == 1L ? "" : "s");
          lostNotifs((String)???, l);
          ClientNotifForwarder.logger.trace("NotifFetcher.run", (String)???);
        }
        for (??? = 0; ??? < localObject2; ???++)
        {
          localObject3 = arrayOfTargetedNotification[???];
          dispatchNotification((TargetedNotification)localObject3, localInteger1, localHashMap);
        }
      }
      synchronized (ClientNotifForwarder.this)
      {
        currentFetchThread = null;
      }
      if ((??? == null) && (ClientNotifForwarder.logger.traceOn())) {
        ClientNotifForwarder.logger.trace("NotifFetcher-run", "Recieved null object as notifs, stops fetching because the notification server is terminated.");
      }
      if ((??? == null) || (shouldStop()))
      {
        ClientNotifForwarder.this.setState(3);
        try
        {
          removeListenerForMBeanRemovedNotif(mbeanRemovedNotifID);
        }
        catch (Exception localException)
        {
          if (ClientNotifForwarder.logger.traceOn()) {
            ClientNotifForwarder.logger.trace("NotifFetcher-run", "removeListenerForMBeanRemovedNotif", localException);
          }
        }
      }
      else
      {
        executor.execute(this);
      }
    }
    
    void dispatchNotification(TargetedNotification paramTargetedNotification, Integer paramInteger, Map<Integer, ClientListenerInfo> paramMap)
    {
      Notification localNotification = paramTargetedNotification.getNotification();
      Integer localInteger = paramTargetedNotification.getListenerID();
      if (localInteger.equals(paramInteger)) {
        return;
      }
      ClientListenerInfo localClientListenerInfo = (ClientListenerInfo)paramMap.get(localInteger);
      if (localClientListenerInfo == null)
      {
        ClientNotifForwarder.logger.trace("NotifFetcher.dispatch", "Listener ID not in map");
        return;
      }
      NotificationListener localNotificationListener = localClientListenerInfo.getListener();
      Object localObject = localClientListenerInfo.getHandback();
      try
      {
        localNotificationListener.handleNotification(localNotification, localObject);
      }
      catch (RuntimeException localRuntimeException)
      {
        ClientNotifForwarder.logger.trace("NotifFetcher-run", "Failed to forward a notification to a listener", localRuntimeException);
      }
    }
    
    private NotificationResult fetchNotifs()
    {
      try
      {
        NotificationResult localNotificationResult = fetchNotifs(clientSequenceNumber, maxNotifications, timeout);
        if (ClientNotifForwarder.logger.traceOn()) {
          ClientNotifForwarder.logger.trace("NotifFetcher-run", "Got notifications from the server: " + localNotificationResult);
        }
        return localNotificationResult;
      }
      catch (ClassNotFoundException|NotSerializableException|UnmarshalException localClassNotFoundException)
      {
        ClientNotifForwarder.logger.trace("NotifFetcher.fetchNotifs", localClassNotFoundException);
        return fetchOneNotif();
      }
      catch (IOException localIOException)
      {
        if (!shouldStop())
        {
          ClientNotifForwarder.logger.error("NotifFetcher-run", "Failed to fetch notification, stopping thread. Error is: " + localIOException, localIOException);
          ClientNotifForwarder.logger.debug("NotifFetcher-run", localIOException);
        }
      }
      return null;
    }
    
    private NotificationResult fetchOneNotif()
    {
      ClientNotifForwarder localClientNotifForwarder = ClientNotifForwarder.this;
      long l1 = clientSequenceNumber;
      int i = 0;
      NotificationResult localNotificationResult = null;
      long l2 = -1L;
      Object localObject;
      while ((localNotificationResult == null) && (!shouldStop()))
      {
        try
        {
          localObject = localClientNotifForwarder.fetchNotifs(l1, 0, 0L);
        }
        catch (ClassNotFoundException localClassNotFoundException1)
        {
          ClientNotifForwarder.logger.warning("NotifFetcher.fetchOneNotif", "Impossible exception: " + localClassNotFoundException1);
          ClientNotifForwarder.logger.debug("NotifFetcher.fetchOneNotif", localClassNotFoundException1);
          return null;
        }
        catch (IOException localIOException)
        {
          if (!shouldStop()) {
            ClientNotifForwarder.logger.trace("NotifFetcher.fetchOneNotif", localIOException);
          }
          return null;
        }
        if ((shouldStop()) || (localObject == null)) {
          return null;
        }
        l1 = ((NotificationResult)localObject).getNextSequenceNumber();
        if (l2 < 0L) {
          l2 = ((NotificationResult)localObject).getEarliestSequenceNumber();
        }
        try
        {
          localNotificationResult = localClientNotifForwarder.fetchNotifs(l1, 1, 0L);
        }
        catch (ClassNotFoundException|NotSerializableException|UnmarshalException localClassNotFoundException2)
        {
          ClientNotifForwarder.logger.warning("NotifFetcher.fetchOneNotif", "Failed to deserialize a notification: " + localClassNotFoundException2.toString());
          if (ClientNotifForwarder.logger.traceOn()) {
            ClientNotifForwarder.logger.trace("NotifFetcher.fetchOneNotif", "Failed to deserialize a notification.", localClassNotFoundException2);
          }
          i++;
          l1 += 1L;
        }
        catch (Exception localException)
        {
          if (!shouldStop()) {
            ClientNotifForwarder.logger.trace("NotifFetcher.fetchOneNotif", localException);
          }
          return null;
        }
      }
      if (i > 0)
      {
        localObject = "Dropped " + i + " notification" + (i == 1 ? "" : "s") + " because classes were missing locally or incompatible";
        lostNotifs((String)localObject, i);
        if (localNotificationResult != null) {
          localNotificationResult = new NotificationResult(l2, localNotificationResult.getNextSequenceNumber(), localNotificationResult.getTargetedNotifications());
        }
      }
      return localNotificationResult;
    }
    
    private boolean shouldStop()
    {
      synchronized (ClientNotifForwarder.this)
      {
        if (state != 1) {
          return true;
        }
        if (infoList.size() == 0)
        {
          ClientNotifForwarder.this.setState(2);
          return true;
        }
        return false;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\ClientNotifForwarder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */