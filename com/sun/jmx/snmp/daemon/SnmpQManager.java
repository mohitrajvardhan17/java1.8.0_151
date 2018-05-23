package com.sun.jmx.snmp.daemon;

import java.io.Serializable;
import java.util.Vector;

final class SnmpQManager
  implements Serializable
{
  private static final long serialVersionUID = 2163709017015248264L;
  private SendQ newq = new SendQ(20, 5);
  private WaitQ waitq = new WaitQ(20, 5);
  private ThreadGroup queueThreadGroup = null;
  private Thread requestQThread = null;
  private Thread timerQThread = null;
  
  SnmpQManager()
  {
    startQThreads();
  }
  
  public void startQThreads()
  {
    if ((timerQThread == null) || (!timerQThread.isAlive())) {
      timerQThread = new SnmpTimerServer(queueThreadGroup, this);
    }
    if ((requestQThread == null) || (!requestQThread.isAlive())) {
      requestQThread = new SnmpSendServer(queueThreadGroup, this);
    }
  }
  
  public void stopQThreads()
  {
    timerQThread).isBeingDestroyed = true;
    waitq.isBeingDestroyed = true;
    requestQThread).isBeingDestroyed = true;
    newq.isBeingDestroyed = true;
    if ((timerQThread != null) && (timerQThread.isAlive() == true)) {
      ((SnmpTimerServer)timerQThread).stopTimerServer();
    }
    waitq = null;
    timerQThread = null;
    if ((requestQThread != null) && (requestQThread.isAlive() == true)) {
      ((SnmpSendServer)requestQThread).stopSendServer();
    }
    newq = null;
    requestQThread = null;
  }
  
  public void addRequest(SnmpInformRequest paramSnmpInformRequest)
  {
    newq.addRequest(paramSnmpInformRequest);
  }
  
  public void addWaiting(SnmpInformRequest paramSnmpInformRequest)
  {
    waitq.addWaiting(paramSnmpInformRequest);
  }
  
  public Vector<SnmpInformRequest> getAllOutstandingRequest(long paramLong)
  {
    return newq.getAllOutstandingRequest(paramLong);
  }
  
  public SnmpInformRequest getTimeoutRequests()
  {
    return waitq.getTimeoutRequests();
  }
  
  public void removeRequest(SnmpInformRequest paramSnmpInformRequest)
  {
    newq.removeElement(paramSnmpInformRequest);
    waitq.removeElement(paramSnmpInformRequest);
  }
  
  public SnmpInformRequest removeRequest(long paramLong)
  {
    SnmpInformRequest localSnmpInformRequest;
    if ((localSnmpInformRequest = newq.removeRequest(paramLong)) == null) {
      localSnmpInformRequest = waitq.removeRequest(paramLong);
    }
    return localSnmpInformRequest;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpQManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */