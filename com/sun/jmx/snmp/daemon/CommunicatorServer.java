package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.MBeanServerForwarder;

public abstract class CommunicatorServer
  implements Runnable, MBeanRegistration, NotificationBroadcaster, CommunicatorServerMBean
{
  public static final int ONLINE = 0;
  public static final int OFFLINE = 1;
  public static final int STOPPING = 2;
  public static final int STARTING = 3;
  public static final int SNMP_TYPE = 4;
  volatile transient int state = 1;
  ObjectName objectName;
  MBeanServer topMBS;
  MBeanServer bottomMBS;
  transient String dbgTag = null;
  int maxActiveClientCount = 1;
  transient int servedClientCount = 0;
  String host = null;
  int port = -1;
  private transient Object stateLock = new Object();
  private transient Vector<ClientHandler> clientHandlerVector = new Vector();
  private transient Thread mainThread = null;
  private volatile boolean stopRequested = false;
  private boolean interrupted = false;
  private transient Exception startException = null;
  private transient long notifCount = 0L;
  private transient NotificationBroadcasterSupport notifBroadcaster = new NotificationBroadcasterSupport();
  private transient MBeanNotificationInfo[] notifInfos = null;
  
  public CommunicatorServer(int paramInt)
    throws IllegalArgumentException
  {
    switch (paramInt)
    {
    case 4: 
      break;
    default: 
      throw new IllegalArgumentException("Invalid connector Type");
    }
    dbgTag = makeDebugTag();
  }
  
  protected Thread createMainThread()
  {
    return new Thread(this, makeThreadName());
  }
  
  public void start(long paramLong)
    throws CommunicationException, InterruptedException
  {
    int i;
    synchronized (stateLock)
    {
      if (state == 2) {
        waitState(1, 60000L);
      }
      i = state == 1 ? 1 : 0;
      if (i != 0)
      {
        changeState(3);
        stopRequested = false;
        interrupted = false;
        startException = null;
      }
    }
    if (i == 0)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "start", "Connector is not OFFLINE");
      }
      return;
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "start", "--> Start connector ");
    }
    mainThread = createMainThread();
    mainThread.start();
    if (paramLong > 0L) {
      waitForStart(paramLong);
    }
  }
  
  public void start()
  {
    try
    {
      start(0L);
    }
    catch (InterruptedException localInterruptedException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "start", "interrupted", localInterruptedException);
      }
    }
  }
  
  public void stop()
  {
    synchronized (stateLock)
    {
      if ((state == 1) || (state == 2))
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "stop", "Connector is not ONLINE");
        }
        return;
      }
      changeState(2);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "stop", "Interrupt main thread");
      }
      stopRequested = true;
      if (!interrupted)
      {
        interrupted = true;
        mainThread.interrupt();
      }
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "stop", "terminateAllClient");
    }
    terminateAllClient();
    synchronized (stateLock)
    {
      if (state == 3) {
        changeState(1);
      }
    }
  }
  
  public boolean isActive()
  {
    synchronized (stateLock)
    {
      return state == 0;
    }
  }
  
  public boolean waitState(int paramInt, long paramLong)
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitState", paramInt + "(0on,1off,2st) TO=" + paramLong + " ; current state = " + getStateString());
    }
    long l1 = 0L;
    if (paramLong > 0L) {
      l1 = System.currentTimeMillis() + paramLong;
    }
    synchronized (stateLock)
    {
      while (state != paramInt)
      {
        if (paramLong < 0L)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitState", "timeOut < 0, return without wait");
          }
          return false;
        }
        try
        {
          if (paramLong > 0L)
          {
            long l2 = l1 - System.currentTimeMillis();
            if (l2 <= 0L)
            {
              if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitState", "timed out");
              }
              return false;
            }
            stateLock.wait(l2);
          }
          else
          {
            stateLock.wait();
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitState", "wait interrupted");
          }
          return state == paramInt;
        }
      }
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitState", "returning in desired state");
      }
      return true;
    }
  }
  
  private void waitForStart(long paramLong)
    throws CommunicationException, InterruptedException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitForStart", "Timeout=" + paramLong + " ; current state = " + getStateString());
    }
    long l1 = System.currentTimeMillis();
    synchronized (stateLock)
    {
      while (state == 3)
      {
        long l2 = System.currentTimeMillis() - l1;
        long l3 = paramLong - l2;
        if (l3 < 0L)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitForStart", "timeout < 0, return without wait");
          }
          throw new InterruptedException("Timeout expired");
        }
        try
        {
          stateLock.wait(l3);
        }
        catch (InterruptedException localInterruptedException)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitForStart", "wait interrupted");
          }
          if (state != 0) {
            throw localInterruptedException;
          }
        }
      }
      if (state == 0)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitForStart", "started");
        }
        return;
      }
      if ((startException instanceof CommunicationException)) {
        throw ((CommunicationException)startException);
      }
      if ((startException instanceof InterruptedException)) {
        throw ((InterruptedException)startException);
      }
      if (startException != null) {
        throw new CommunicationException(startException, "Failed to start: " + startException);
      }
      throw new CommunicationException("Failed to start: state is " + getStringForState(state));
    }
  }
  
  /* Error */
  public int getState()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 467	com/sun/jmx/snmp/daemon/CommunicatorServer:stateLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 462	com/sun/jmx/snmp/daemon/CommunicatorServer:state	I
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	CommunicatorServer
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public String getStateString()
  {
    return getStringForState(state);
  }
  
  public String getHost()
  {
    try
    {
      host = InetAddress.getLocalHost().getHostName();
    }
    catch (Exception localException)
    {
      host = "Unknown host";
    }
    return host;
  }
  
  /* Error */
  public int getPort()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 467	com/sun/jmx/snmp/daemon/CommunicatorServer:stateLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 460	com/sun/jmx/snmp/daemon/CommunicatorServer:port	I
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	CommunicatorServer
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void setPort(int paramInt)
    throws IllegalStateException
  {
    synchronized (stateLock)
    {
      if ((state == 0) || (state == 3)) {
        throw new IllegalStateException("Stop server before carrying out this operation");
      }
      port = paramInt;
      dbgTag = makeDebugTag();
    }
  }
  
  public abstract String getProtocol();
  
  int getServedClientCount()
  {
    return servedClientCount;
  }
  
  int getActiveClientCount()
  {
    int i = clientHandlerVector.size();
    return i;
  }
  
  int getMaxActiveClientCount()
  {
    return maxActiveClientCount;
  }
  
  void setMaxActiveClientCount(int paramInt)
    throws IllegalStateException
  {
    synchronized (stateLock)
    {
      if ((state == 0) || (state == 3)) {
        throw new IllegalStateException("Stop server before carrying out this operation");
      }
      maxActiveClientCount = paramInt;
    }
  }
  
  void notifyClientHandlerCreated(ClientHandler paramClientHandler)
  {
    clientHandlerVector.addElement(paramClientHandler);
  }
  
  synchronized void notifyClientHandlerDeleted(ClientHandler paramClientHandler)
  {
    clientHandlerVector.removeElement(paramClientHandler);
    notifyAll();
  }
  
  protected int getBindTries()
  {
    return 50;
  }
  
  protected long getBindSleepTime()
  {
    return 100L;
  }
  
  public void run()
  {
    int i = 0;
    int j = 0;
    try
    {
      int k = getBindTries();
      long l = getBindSleepTime();
      while ((i < k) && (j == 0)) {
        try
        {
          doBind();
          j = 1;
        }
        catch (CommunicationException localCommunicationException)
        {
          i++;
          try
          {
            Thread.sleep(l);
          }
          catch (InterruptedException localInterruptedException)
          {
            throw localInterruptedException;
          }
        }
      }
      if (j == 0) {
        doBind();
      }
    }
    catch (Exception localException1)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "run", "Got unexpected exception", localException1);
      }
      synchronized (stateLock)
      {
        startException = localException1;
        changeState(1);
      }
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "run", "State is OFFLINE");
      }
      doError(localException1);
      return;
    }
    try
    {
      changeState(0);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "run", "State is ONLINE");
      }
      while (!stopRequested)
      {
        servedClientCount += 1;
        doReceive();
        waitIfTooManyClients();
        doProcess();
      }
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "run", "Stop has been requested");
      }
      synchronized (stateLock)
      {
        interrupted = true;
        Thread.interrupted();
      }
      try
      {
        doUnbind();
        waitClientTermination();
        changeState(1);
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "run", "State is OFFLINE");
        }
      }
      catch (Exception localException1)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "run", "Got unexpected exception", (Throwable)???);
        }
        changeState(1);
      }
      return;
    }
    catch (InterruptedException localException1)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "run", "Interrupt caught");
      }
      changeState(2);
      synchronized (stateLock)
      {
        interrupted = true;
        Thread.interrupted();
      }
      try
      {
        doUnbind();
        waitClientTermination();
        changeState(1);
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "run", "State is OFFLINE");
        }
      }
      catch (Exception localException1)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "run", "Got unexpected exception", (Throwable)???);
        }
        changeState(1);
      }
    }
    catch (Exception localException1)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "run", "Got unexpected exception", (Throwable)???);
      }
      changeState(2);
    }
    finally
    {
      synchronized (stateLock)
      {
        interrupted = true;
        Thread.interrupted();
      }
      try
      {
        doUnbind();
        waitClientTermination();
        changeState(1);
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "run", "State is OFFLINE");
        }
      }
      catch (Exception localException3)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "run", "Got unexpected exception", localException3);
        }
        changeState(1);
      }
    }
  }
  
  protected abstract void doError(Exception paramException)
    throws CommunicationException;
  
  protected abstract void doBind()
    throws CommunicationException, InterruptedException;
  
  protected abstract void doReceive()
    throws CommunicationException, InterruptedException;
  
  protected abstract void doProcess()
    throws CommunicationException, InterruptedException;
  
  protected abstract void doUnbind()
    throws CommunicationException, InterruptedException;
  
  public synchronized MBeanServer getMBeanServer()
  {
    return topMBS;
  }
  
  public synchronized void setMBeanServer(MBeanServer paramMBeanServer)
    throws IllegalArgumentException, IllegalStateException
  {
    synchronized (stateLock)
    {
      if ((state == 0) || (state == 3)) {
        throw new IllegalStateException("Stop server before carrying out this operation");
      }
    }
    Vector localVector = new Vector();
    for (MBeanServer localMBeanServer = paramMBeanServer; localMBeanServer != bottomMBS; localMBeanServer = ((MBeanServerForwarder)localMBeanServer).getMBeanServer())
    {
      if (!(localMBeanServer instanceof MBeanServerForwarder)) {
        throw new IllegalArgumentException("MBeanServer argument must be MBean server where this server is registered, or an MBeanServerForwarder leading to that server");
      }
      if (localVector.contains(localMBeanServer)) {
        throw new IllegalArgumentException("MBeanServerForwarder loop");
      }
      localVector.addElement(localMBeanServer);
    }
    topMBS = paramMBeanServer;
  }
  
  ObjectName getObjectName()
  {
    return objectName;
  }
  
  void changeState(int paramInt)
  {
    int i;
    synchronized (stateLock)
    {
      if (state == paramInt) {
        return;
      }
      i = state;
      state = paramInt;
      stateLock.notifyAll();
    }
    sendStateChangeNotification(i, paramInt);
  }
  
  String makeDebugTag()
  {
    return "CommunicatorServer[" + getProtocol() + ":" + getPort() + "]";
  }
  
  String makeThreadName()
  {
    String str;
    if (objectName == null) {
      str = "CommunicatorServer";
    } else {
      str = objectName.toString();
    }
    return str;
  }
  
  private synchronized void waitIfTooManyClients()
    throws InterruptedException
  {
    while (getActiveClientCount() >= maxActiveClientCount)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitIfTooManyClients", "Waiting for a client to terminate");
      }
      wait();
    }
  }
  
  private void waitClientTermination()
  {
    int i = clientHandlerVector.size();
    if ((JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) && (i >= 1)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitClientTermination", "waiting for " + i + " clients to terminate");
    }
    while (!clientHandlerVector.isEmpty()) {
      try
      {
        ((ClientHandler)clientHandlerVector.firstElement()).join();
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitClientTermination", "No elements left", localNoSuchElementException);
        }
      }
    }
    if ((JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) && (i >= 1)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "waitClientTermination", "Ok, let's go...");
    }
  }
  
  private void terminateAllClient()
  {
    int i = clientHandlerVector.size();
    if ((JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) && (i >= 1)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "terminateAllClient", "Interrupting " + i + " clients");
    }
    ClientHandler[] arrayOfClientHandler1 = (ClientHandler[])clientHandlerVector.toArray(new ClientHandler[0]);
    for (ClientHandler localClientHandler : arrayOfClientHandler1) {
      try
      {
        localClientHandler.interrupt();
      }
      catch (Exception localException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, dbgTag, "terminateAllClient", "Failed to interrupt pending request. Ignore the exception.", localException);
        }
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    stateLock = new Object();
    state = 1;
    stopRequested = false;
    servedClientCount = 0;
    clientHandlerVector = new Vector();
    mainThread = null;
    notifCount = 0L;
    notifInfos = null;
    notifBroadcaster = new NotificationBroadcasterSupport();
    dbgTag = makeDebugTag();
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws IllegalArgumentException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "addNotificationListener", "Adding listener " + paramNotificationListener + " with filter " + paramNotificationFilter + " and handback " + paramObject);
    }
    notifBroadcaster.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "removeNotificationListener", "Removing listener " + paramNotificationListener);
    }
    notifBroadcaster.removeNotificationListener(paramNotificationListener);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    if (notifInfos == null)
    {
      notifInfos = new MBeanNotificationInfo[1];
      String[] arrayOfString = { "jmx.attribute.change" };
      notifInfos[0] = new MBeanNotificationInfo(arrayOfString, AttributeChangeNotification.class.getName(), "Sent to notify that the value of the State attribute of this CommunicatorServer instance has changed.");
    }
    return (MBeanNotificationInfo[])notifInfos.clone();
  }
  
  private void sendStateChangeNotification(int paramInt1, int paramInt2)
  {
    String str1 = getStringForState(paramInt1);
    String str2 = getStringForState(paramInt2);
    String str3 = dbgTag + " The value of attribute State has changed from " + paramInt1 + " (" + str1 + ") to " + paramInt2 + " (" + str2 + ").";
    notifCount += 1L;
    AttributeChangeNotification localAttributeChangeNotification = new AttributeChangeNotification(this, notifCount, System.currentTimeMillis(), str3, "State", "int", new Integer(paramInt1), new Integer(paramInt2));
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, dbgTag, "sendStateChangeNotification", "Sending AttributeChangeNotification #" + notifCount + " with message: " + str3);
    }
    notifBroadcaster.sendNotification(localAttributeChangeNotification);
  }
  
  private static String getStringForState(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return "ONLINE";
    case 3: 
      return "STARTING";
    case 1: 
      return "OFFLINE";
    case 2: 
      return "STOPPING";
    }
    return "UNDEFINED";
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    objectName = paramObjectName;
    synchronized (this)
    {
      if (bottomMBS != null) {
        throw new IllegalArgumentException("connector already registered in an MBean server");
      }
      topMBS = (bottomMBS = paramMBeanServer);
    }
    dbgTag = makeDebugTag();
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean)
  {
    if (!paramBoolean.booleanValue()) {
      synchronized (this)
      {
        topMBS = (bottomMBS = null);
      }
    }
  }
  
  public void preDeregister()
    throws Exception
  {
    synchronized (this)
    {
      topMBS = (bottomMBS = null);
    }
    objectName = null;
    int i = getState();
    if ((i == 0) || (i == 3)) {
      stop();
    }
  }
  
  public void postDeregister() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\CommunicatorServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */