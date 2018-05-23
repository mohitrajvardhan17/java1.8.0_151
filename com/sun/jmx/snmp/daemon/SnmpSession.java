package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBindList;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

class SnmpSession
  implements SnmpDefinitions, Runnable
{
  protected transient SnmpAdaptorServer adaptor;
  protected transient SnmpSocket informSocket = null;
  private transient Hashtable<SnmpInformRequest, SnmpInformRequest> informRequestList = new Hashtable();
  private transient Stack<SnmpInformRequest> informRespq = new Stack();
  private transient Thread myThread = null;
  private transient SnmpInformRequest syncInformReq;
  SnmpQManager snmpQman = null;
  private boolean isBeingCancelled = false;
  
  public SnmpSession(SnmpAdaptorServer paramSnmpAdaptorServer)
    throws SocketException
  {
    adaptor = paramSnmpAdaptorServer;
    snmpQman = new SnmpQManager();
    SnmpResponseHandler localSnmpResponseHandler = new SnmpResponseHandler(paramSnmpAdaptorServer, snmpQman);
    initialize(paramSnmpAdaptorServer, localSnmpResponseHandler);
  }
  
  public SnmpSession()
    throws SocketException
  {}
  
  protected synchronized void initialize(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpResponseHandler paramSnmpResponseHandler)
    throws SocketException
  {
    informSocket = new SnmpSocket(paramSnmpResponseHandler, paramSnmpAdaptorServer.getAddress(), paramSnmpAdaptorServer.getBufferSize().intValue());
    myThread = new Thread(this, "SnmpSession");
    myThread.start();
  }
  
  synchronized boolean isSessionActive()
  {
    return (adaptor.isActive()) && (myThread != null) && (myThread.isAlive());
  }
  
  SnmpSocket getSocket()
  {
    return informSocket;
  }
  
  SnmpQManager getSnmpQManager()
  {
    return snmpQman;
  }
  
  private synchronized boolean syncInProgress()
  {
    return syncInformReq != null;
  }
  
  private synchronized void setSyncMode(SnmpInformRequest paramSnmpInformRequest)
  {
    syncInformReq = paramSnmpInformRequest;
  }
  
  private synchronized void resetSyncMode()
  {
    if (syncInformReq == null) {
      return;
    }
    syncInformReq = null;
    if (thisSessionContext()) {
      return;
    }
    notifyAll();
  }
  
  boolean thisSessionContext()
  {
    return Thread.currentThread() == myThread;
  }
  
  SnmpInformRequest makeAsyncRequest(InetAddress paramInetAddress, String paramString, SnmpInformHandler paramSnmpInformHandler, SnmpVarBindList paramSnmpVarBindList, int paramInt)
    throws SnmpStatusException
  {
    if (!isSessionActive()) {
      throw new SnmpStatusException("SNMP adaptor server not ONLINE");
    }
    SnmpInformRequest localSnmpInformRequest = new SnmpInformRequest(this, adaptor, paramInetAddress, paramString, paramInt, paramSnmpInformHandler);
    localSnmpInformRequest.start(paramSnmpVarBindList);
    return localSnmpInformRequest;
  }
  
  void waitForResponse(SnmpInformRequest paramSnmpInformRequest, long paramLong)
  {
    if (!paramSnmpInformRequest.inProgress()) {
      return;
    }
    setSyncMode(paramSnmpInformRequest);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "waitForResponse", "Session switching to sync mode for inform request " + paramSnmpInformRequest.getRequestId());
    }
    long l;
    if (paramLong <= 0L) {
      l = System.currentTimeMillis() + 6000000L;
    } else {
      l = System.currentTimeMillis() + paramLong;
    }
    while ((paramSnmpInformRequest.inProgress()) || (syncInProgress()))
    {
      paramLong = l - System.currentTimeMillis();
      if (paramLong <= 0L) {
        break;
      }
      synchronized (this)
      {
        if (!informRespq.removeElement(paramSnmpInformRequest))
        {
          try
          {
            wait(paramLong);
          }
          catch (InterruptedException localInterruptedException) {}
          continue;
        }
      }
      try
      {
        processResponse(paramSnmpInformRequest);
      }
      catch (Exception localException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "waitForResponse", "Got unexpected exception", localException);
        }
      }
    }
    resetSyncMode();
  }
  
  public void run()
  {
    myThread = Thread.currentThread();
    myThread.setPriority(5);
    SnmpInformRequest localSnmpInformRequest = null;
    while (myThread != null) {
      try
      {
        localSnmpInformRequest = nextResponse();
        if (localSnmpInformRequest != null) {
          processResponse(localSnmpInformRequest);
        }
      }
      catch (ThreadDeath localThreadDeath)
      {
        myThread = null;
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "run", "ThreadDeath, session thread unexpectedly shutting down");
        }
        throw localThreadDeath;
      }
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "run", "Session thread shutting down");
    }
    myThread = null;
  }
  
  private void processResponse(SnmpInformRequest paramSnmpInformRequest)
  {
    while ((paramSnmpInformRequest != null) && (myThread != null)) {
      try
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "processResponse", "Processing response to req = " + paramSnmpInformRequest.getRequestId());
        }
        paramSnmpInformRequest.processResponse();
        paramSnmpInformRequest = null;
      }
      catch (Exception localException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "processResponse", "Got unexpected exception", localException);
        }
        paramSnmpInformRequest = null;
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "processResponse", "Out of memory error in session thread", localOutOfMemoryError);
        }
        Thread.yield();
      }
    }
  }
  
  synchronized void addInformRequest(SnmpInformRequest paramSnmpInformRequest)
    throws SnmpStatusException
  {
    if (!isSessionActive()) {
      throw new SnmpStatusException("SNMP adaptor is not ONLINE or session is dead...");
    }
    informRequestList.put(paramSnmpInformRequest, paramSnmpInformRequest);
  }
  
  synchronized void removeInformRequest(SnmpInformRequest paramSnmpInformRequest)
  {
    if (!isBeingCancelled) {
      informRequestList.remove(paramSnmpInformRequest);
    }
    if ((syncInformReq != null) && (syncInformReq == paramSnmpInformRequest)) {
      resetSyncMode();
    }
  }
  
  private void cancelAllRequests()
  {
    SnmpInformRequest[] arrayOfSnmpInformRequest;
    synchronized (this)
    {
      if (informRequestList.isEmpty()) {
        return;
      }
      isBeingCancelled = true;
      arrayOfSnmpInformRequest = new SnmpInformRequest[informRequestList.size()];
      Iterator localIterator = informRequestList.values().iterator();
      int j = 0;
      while (localIterator.hasNext())
      {
        SnmpInformRequest localSnmpInformRequest = (SnmpInformRequest)localIterator.next();
        arrayOfSnmpInformRequest[(j++)] = localSnmpInformRequest;
        localIterator.remove();
      }
      informRequestList.clear();
    }
    for (int i = 0; i < arrayOfSnmpInformRequest.length; i++) {
      arrayOfSnmpInformRequest[i].cancelRequest();
    }
  }
  
  void addResponse(SnmpInformRequest paramSnmpInformRequest)
  {
    SnmpInformRequest localSnmpInformRequest = paramSnmpInformRequest;
    if (isSessionActive()) {
      synchronized (this)
      {
        informRespq.push(paramSnmpInformRequest);
        notifyAll();
      }
    } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "addResponse", "Adaptor not ONLINE or session thread dead, so inform response is dropped..." + paramSnmpInformRequest.getRequestId());
    }
  }
  
  private synchronized SnmpInformRequest nextResponse()
  {
    if (informRespq.isEmpty()) {
      try
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "nextResponse", "Blocking for response");
        }
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
    if (informRespq.isEmpty()) {
      return null;
    }
    SnmpInformRequest localSnmpInformRequest = (SnmpInformRequest)informRespq.firstElement();
    informRespq.removeElementAt(0);
    return localSnmpInformRequest;
  }
  
  private synchronized void cancelAllResponses()
  {
    if (informRespq != null)
    {
      syncInformReq = null;
      informRespq.removeAllElements();
      notifyAll();
    }
  }
  
  final void destroySession()
  {
    cancelAllRequests();
    cancelAllResponses();
    synchronized (this)
    {
      informSocket.close();
      informSocket = null;
    }
    snmpQman.stopQThreads();
    snmpQman = null;
    killSessionThread();
  }
  
  private synchronized void killSessionThread()
  {
    if ((myThread != null) && (myThread.isAlive()))
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "killSessionThread", "Destroying session");
      }
      if (!thisSessionContext())
      {
        myThread = null;
        notifyAll();
      }
      else
      {
        myThread = null;
      }
    }
  }
  
  protected void finalize()
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "finalize", "Shutting all servers");
    }
    if (informRespq != null) {
      informRespq.removeAllElements();
    }
    informRespq = null;
    if (informSocket != null) {
      informSocket.close();
    }
    informSocket = null;
    snmpQman = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */