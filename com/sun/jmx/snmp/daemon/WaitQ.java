package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class WaitQ
  extends Vector<SnmpInformRequest>
{
  boolean isBeingDestroyed = false;
  
  WaitQ(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
  }
  
  public synchronized void addWaiting(SnmpInformRequest paramSnmpInformRequest)
  {
    long l = paramSnmpInformRequest.getAbsMaxTimeToWait();
    for (int i = size(); (i > 0) && (l >= getRequestAt(i - 1).getAbsMaxTimeToWait()); i--) {}
    if (i == size())
    {
      addElement(paramSnmpInformRequest);
      notifyClients();
    }
    else
    {
      insertElementAt(paramSnmpInformRequest, i);
    }
  }
  
  public synchronized boolean waitUntilReady()
  {
    for (;;)
    {
      if (isBeingDestroyed == true) {
        return false;
      }
      long l1 = 0L;
      if (!isEmpty())
      {
        long l2 = System.currentTimeMillis();
        SnmpInformRequest localSnmpInformRequest = (SnmpInformRequest)lastElement();
        l1 = localSnmpInformRequest.getAbsMaxTimeToWait() - l2;
        if (l1 <= 0L) {
          return true;
        }
      }
      waitOnThisQueue(l1);
    }
  }
  
  public synchronized SnmpInformRequest getTimeoutRequests()
  {
    if (waitUntilReady() == true)
    {
      SnmpInformRequest localSnmpInformRequest = (SnmpInformRequest)lastElement();
      elementCount -= 1;
      return localSnmpInformRequest;
    }
    return null;
  }
  
  private synchronized void notifyClients()
  {
    notifyAll();
  }
  
  public synchronized void waitOnThisQueue(long paramLong)
  {
    if ((paramLong == 0L) && (!isEmpty()) && (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpQManager.class.getName(), "waitOnThisQueue", "[" + Thread.currentThread().toString() + "]:Fatal BUG :: Blocking on waitq permenantly. But size = " + size());
    }
    try
    {
      wait(paramLong);
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public SnmpInformRequest getRequestAt(int paramInt)
  {
    return (SnmpInformRequest)elementAt(paramInt);
  }
  
  public synchronized SnmpInformRequest removeRequest(long paramLong)
  {
    int i = size();
    for (int j = 0; j < i; j++)
    {
      SnmpInformRequest localSnmpInformRequest = getRequestAt(j);
      if (paramLong == localSnmpInformRequest.getRequestId())
      {
        removeElementAt(j);
        return localSnmpInformRequest;
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\WaitQ.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */