package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.security.NotificationAccessController;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanPermission;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;
import javax.security.auth.Subject;

public class ServerNotifForwarder
{
  private final NotifForwarderBufferFilter bufferFilter = new NotifForwarderBufferFilter();
  private MBeanServer mbeanServer;
  private final String connectionId;
  private final long connectionTimeout;
  private static int listenerCounter = 0;
  private static final int[] listenerCounterLock = new int[0];
  private NotificationBuffer notifBuffer;
  private final Map<ObjectName, Set<IdAndFilter>> listenerMap = new HashMap();
  private boolean terminated = false;
  private final int[] terminationLock = new int[0];
  static final String broadcasterClass = NotificationBroadcaster.class.getName();
  private final boolean checkNotificationEmission;
  private final NotificationAccessController notificationAccessController;
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ServerNotifForwarder");
  
  public ServerNotifForwarder(MBeanServer paramMBeanServer, Map<String, ?> paramMap, NotificationBuffer paramNotificationBuffer, String paramString)
  {
    mbeanServer = paramMBeanServer;
    notifBuffer = paramNotificationBuffer;
    connectionId = paramString;
    connectionTimeout = EnvHelp.getServerConnectionTimeout(paramMap);
    String str = (String)paramMap.get("jmx.remote.x.check.notification.emission");
    checkNotificationEmission = EnvHelp.computeBooleanFromString(str);
    notificationAccessController = EnvHelp.getNotificationAccessController(paramMap);
  }
  
  public Integer addNotificationListener(final ObjectName paramObjectName, NotificationFilter paramNotificationFilter)
    throws InstanceNotFoundException, IOException
  {
    if (logger.traceOn()) {
      logger.trace("addNotificationListener", "Add a listener at " + paramObjectName);
    }
    checkState();
    checkMBeanPermission(paramObjectName, "addNotificationListener");
    if (notificationAccessController != null) {
      notificationAccessController.addNotificationListener(connectionId, paramObjectName, getSubject());
    }
    try
    {
      boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Boolean run()
          throws InstanceNotFoundException
        {
          return Boolean.valueOf(mbeanServer.isInstanceOf(paramObjectName, ServerNotifForwarder.broadcasterClass));
        }
      })).booleanValue();
      if (!bool) {
        throw new IllegalArgumentException("The specified MBean [" + paramObjectName + "] is not a NotificationBroadcaster object.");
      }
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((InstanceNotFoundException)extractException(localPrivilegedActionException));
    }
    Integer localInteger = getListenerID();
    ObjectName localObjectName = paramObjectName;
    Object localObject1;
    if ((paramObjectName.getDomain() == null) || (paramObjectName.getDomain().equals(""))) {
      try
      {
        localObjectName = ObjectName.getInstance(mbeanServer.getDefaultDomain(), paramObjectName.getKeyPropertyList());
      }
      catch (MalformedObjectNameException localMalformedObjectNameException)
      {
        localObject1 = new IOException(localMalformedObjectNameException.getMessage());
        ((IOException)localObject1).initCause(localMalformedObjectNameException);
        throw ((Throwable)localObject1);
      }
    }
    synchronized (listenerMap)
    {
      localObject1 = new IdAndFilter(localInteger, paramNotificationFilter);
      Object localObject2 = (Set)listenerMap.get(localObjectName);
      if (localObject2 == null)
      {
        localObject2 = Collections.singleton(localObject1);
      }
      else
      {
        if (((Set)localObject2).size() == 1) {
          localObject2 = new HashSet((Collection)localObject2);
        }
        ((Set)localObject2).add(localObject1);
      }
      listenerMap.put(localObjectName, localObject2);
    }
    return localInteger;
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, Integer[] paramArrayOfInteger)
    throws Exception
  {
    if (logger.traceOn()) {
      logger.trace("removeNotificationListener", "Remove some listeners from " + paramObjectName);
    }
    checkState();
    checkMBeanPermission(paramObjectName, "removeNotificationListener");
    if (notificationAccessController != null) {
      notificationAccessController.removeNotificationListener(connectionId, paramObjectName, getSubject());
    }
    Object localObject = null;
    for (int i = 0; i < paramArrayOfInteger.length; i++) {
      try
      {
        removeNotificationListener(paramObjectName, paramArrayOfInteger[i]);
      }
      catch (Exception localException)
      {
        if (localObject != null) {
          localObject = localException;
        }
      }
    }
    if (localObject != null) {
      throw ((Throwable)localObject);
    }
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, Integer paramInteger)
    throws InstanceNotFoundException, ListenerNotFoundException, IOException
  {
    if (logger.traceOn()) {
      logger.trace("removeNotificationListener", "Remove the listener " + paramInteger + " from " + paramObjectName);
    }
    checkState();
    if ((paramObjectName != null) && (!paramObjectName.isPattern()) && (!mbeanServer.isRegistered(paramObjectName))) {
      throw new InstanceNotFoundException("The MBean " + paramObjectName + " is not registered.");
    }
    synchronized (listenerMap)
    {
      Set localSet = (Set)listenerMap.get(paramObjectName);
      IdAndFilter localIdAndFilter = new IdAndFilter(paramInteger, null);
      if ((localSet == null) || (!localSet.contains(localIdAndFilter))) {
        throw new ListenerNotFoundException("Listener not found");
      }
      if (localSet.size() == 1) {
        listenerMap.remove(paramObjectName);
      } else {
        localSet.remove(localIdAndFilter);
      }
    }
  }
  
  public NotificationResult fetchNotifs(long paramLong1, long paramLong2, int paramInt)
  {
    if (logger.traceOn()) {
      logger.trace("fetchNotifs", "Fetching notifications, the startSequenceNumber is " + paramLong1 + ", the timeout is " + paramLong2 + ", the maxNotifications is " + paramInt);
    }
    long l = Math.min(connectionTimeout, paramLong2);
    NotificationResult localNotificationResult;
    try
    {
      localNotificationResult = notifBuffer.fetchNotifications(bufferFilter, paramLong1, l, paramInt);
      snoopOnUnregister(localNotificationResult);
    }
    catch (InterruptedException localInterruptedException)
    {
      localNotificationResult = new NotificationResult(0L, 0L, new TargetedNotification[0]);
    }
    if (logger.traceOn()) {
      logger.trace("fetchNotifs", "Forwarding the notifs: " + localNotificationResult);
    }
    return localNotificationResult;
  }
  
  private void snoopOnUnregister(NotificationResult paramNotificationResult)
  {
    ArrayList localArrayList = null;
    synchronized (listenerMap)
    {
      Set localSet = (Set)listenerMap.get(MBeanServerDelegate.DELEGATE_NAME);
      if ((localSet == null) || (localSet.isEmpty())) {
        return;
      }
      localArrayList = new ArrayList(localSet);
    }
    for (Object localObject2 : paramNotificationResult.getTargetedNotifications())
    {
      Integer localInteger = ((TargetedNotification)localObject2).getListenerID();
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        IdAndFilter localIdAndFilter = (IdAndFilter)localIterator.next();
        if (id == localInteger)
        {
          Notification localNotification = ((TargetedNotification)localObject2).getNotification();
          if (((localNotification instanceof MBeanServerNotification)) && (localNotification.getType().equals("JMX.mbean.unregistered")))
          {
            MBeanServerNotification localMBeanServerNotification = (MBeanServerNotification)localNotification;
            ObjectName localObjectName = localMBeanServerNotification.getMBeanName();
            synchronized (listenerMap)
            {
              listenerMap.remove(localObjectName);
            }
          }
        }
      }
    }
  }
  
  public void terminate()
  {
    if (logger.traceOn()) {
      logger.trace("terminate", "Be called.");
    }
    synchronized (terminationLock)
    {
      if (terminated) {
        return;
      }
      terminated = true;
      synchronized (listenerMap)
      {
        listenerMap.clear();
      }
    }
    if (logger.traceOn()) {
      logger.trace("terminate", "Terminated.");
    }
  }
  
  private Subject getSubject()
  {
    return Subject.getSubject(AccessController.getContext());
  }
  
  private void checkState()
    throws IOException
  {
    synchronized (terminationLock)
    {
      if (terminated) {
        throw new IOException("The connection has been terminated.");
      }
    }
  }
  
  /* Error */
  private Integer getListenerID()
  {
    // Byte code:
    //   0: getstatic 406	com/sun/jmx/remote/internal/ServerNotifForwarder:listenerCounterLock	[I
    //   3: dup
    //   4: astore_1
    //   5: monitorenter
    //   6: getstatic 402	com/sun/jmx/remote/internal/ServerNotifForwarder:listenerCounter	I
    //   9: dup
    //   10: iconst_1
    //   11: iadd
    //   12: putstatic 402	com/sun/jmx/remote/internal/ServerNotifForwarder:listenerCounter	I
    //   15: invokestatic 444	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   18: aload_1
    //   19: monitorexit
    //   20: areturn
    //   21: astore_2
    //   22: aload_1
    //   23: monitorexit
    //   24: aload_2
    //   25: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	26	0	this	ServerNotifForwarder
    //   4	19	1	Ljava/lang/Object;	Object
    //   21	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   6	20	21	finally
    //   21	24	21	finally
  }
  
  public final void checkMBeanPermission(ObjectName paramObjectName, String paramString)
    throws InstanceNotFoundException, SecurityException
  {
    checkMBeanPermission(mbeanServer, paramObjectName, paramString);
  }
  
  static void checkMBeanPermission(MBeanServer paramMBeanServer, final ObjectName paramObjectName, String paramString)
    throws InstanceNotFoundException, SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      AccessControlContext localAccessControlContext = AccessController.getContext();
      ObjectInstance localObjectInstance;
      try
      {
        localObjectInstance = (ObjectInstance)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public ObjectInstance run()
            throws InstanceNotFoundException
          {
            return val$mbs.getObjectInstance(paramObjectName);
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw ((InstanceNotFoundException)extractException(localPrivilegedActionException));
      }
      String str = localObjectInstance.getClassName();
      MBeanPermission localMBeanPermission = new MBeanPermission(str, null, paramObjectName, paramString);
      localSecurityManager.checkPermission(localMBeanPermission, localAccessControlContext);
    }
  }
  
  private boolean allowNotificationEmission(ObjectName paramObjectName, TargetedNotification paramTargetedNotification)
  {
    try
    {
      if (checkNotificationEmission) {
        checkMBeanPermission(paramObjectName, "addNotificationListener");
      }
      if (notificationAccessController != null) {
        notificationAccessController.fetchNotification(connectionId, paramObjectName, paramTargetedNotification.getNotification(), getSubject());
      }
      return true;
    }
    catch (SecurityException localSecurityException)
    {
      if (logger.debugOn()) {
        logger.debug("fetchNotifs", "Notification " + paramTargetedNotification.getNotification() + " not forwarded: the caller didn't have the required access rights");
      }
      return false;
    }
    catch (Exception localException)
    {
      if (logger.debugOn()) {
        logger.debug("fetchNotifs", "Notification " + paramTargetedNotification.getNotification() + " not forwarded: got an unexpected exception: " + localException);
      }
    }
    return false;
  }
  
  private static Exception extractException(Exception paramException)
  {
    while ((paramException instanceof PrivilegedActionException)) {
      paramException = ((PrivilegedActionException)paramException).getException();
    }
    return paramException;
  }
  
  private static class IdAndFilter
  {
    private Integer id;
    private NotificationFilter filter;
    
    IdAndFilter(Integer paramInteger, NotificationFilter paramNotificationFilter)
    {
      id = paramInteger;
      filter = paramNotificationFilter;
    }
    
    Integer getId()
    {
      return id;
    }
    
    NotificationFilter getFilter()
    {
      return filter;
    }
    
    public int hashCode()
    {
      return id.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof IdAndFilter)) && (((IdAndFilter)paramObject).getId().equals(getId()));
    }
  }
  
  final class NotifForwarderBufferFilter
    implements NotificationBufferFilter
  {
    NotifForwarderBufferFilter() {}
    
    public void apply(List<TargetedNotification> paramList, ObjectName paramObjectName, Notification paramNotification)
    {
      ServerNotifForwarder.IdAndFilter[] arrayOfIdAndFilter;
      synchronized (listenerMap)
      {
        Set localSet = (Set)listenerMap.get(paramObjectName);
        if (localSet == null)
        {
          ServerNotifForwarder.logger.debug("bufferFilter", "no listeners for this name");
          return;
        }
        arrayOfIdAndFilter = new ServerNotifForwarder.IdAndFilter[localSet.size()];
        localSet.toArray(arrayOfIdAndFilter);
      }
      for (Object localObject2 : arrayOfIdAndFilter)
      {
        NotificationFilter localNotificationFilter = ((ServerNotifForwarder.IdAndFilter)localObject2).getFilter();
        if ((localNotificationFilter == null) || (localNotificationFilter.isNotificationEnabled(paramNotification)))
        {
          ServerNotifForwarder.logger.debug("bufferFilter", "filter matches");
          TargetedNotification localTargetedNotification = new TargetedNotification(paramNotification, ((ServerNotifForwarder.IdAndFilter)localObject2).getId());
          if (ServerNotifForwarder.this.allowNotificationEmission(paramObjectName, localTargetedNotification)) {
            paramList.add(localTargetedNotification);
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\ServerNotifForwarder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */