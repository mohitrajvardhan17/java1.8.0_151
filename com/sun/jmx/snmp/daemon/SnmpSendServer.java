package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SnmpSendServer
  extends Thread
{
  private int intervalRange = 5000;
  private Vector<SnmpInformRequest> readyPool;
  SnmpQManager snmpq = null;
  boolean isBeingDestroyed = false;
  
  public SnmpSendServer(ThreadGroup paramThreadGroup, SnmpQManager paramSnmpQManager)
  {
    super(paramThreadGroup, "SnmpSendServer");
    snmpq = paramSnmpQManager;
    start();
  }
  
  public synchronized void stopSendServer()
  {
    if (isAlive())
    {
      interrupt();
      try
      {
        join();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public void run()
  {
    Thread.currentThread().setPriority(5);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "run", "Thread Started");
    }
    try
    {
      for (;;)
      {
        prepareAndSendRequest();
        if (isBeingDestroyed == true) {
          break;
        }
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Exception in send server", localException);
        }
      }
    }
    catch (ThreadDeath localThreadDeath)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Exiting... Fatal error");
      }
      throw localThreadDeath;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      for (;;)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Out of memory");
        }
      }
    }
    catch (Error localError)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Got unexpected error", localError);
      }
      throw localError;
    }
  }
  
  private void prepareAndSendRequest()
  {
    if ((readyPool == null) || (readyPool.isEmpty()))
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "prepareAndSendRequest", "Blocking for inform requests");
      }
      readyPool = snmpq.getAllOutstandingRequest(intervalRange);
      if (isBeingDestroyed != true) {}
    }
    else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
    {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "prepareAndSendRequest", "Inform requests from a previous block left unprocessed. Will try again");
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "prepareAndSendRequest", "List of inform requests to send : " + reqListToString(readyPool));
    }
    synchronized (this)
    {
      if (readyPool.size() < 2)
      {
        fireRequestList(readyPool);
        return;
      }
      while (!readyPool.isEmpty())
      {
        SnmpInformRequest localSnmpInformRequest = (SnmpInformRequest)readyPool.lastElement();
        if ((localSnmpInformRequest != null) && (localSnmpInformRequest.inProgress())) {
          fireRequest(localSnmpInformRequest);
        }
        readyPool.removeElementAt(readyPool.size() - 1);
      }
      readyPool.removeAllElements();
    }
  }
  
  private void fireRequest(SnmpInformRequest paramSnmpInformRequest)
  {
    if ((paramSnmpInformRequest != null) && (paramSnmpInformRequest.inProgress()))
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "fireRequest", "Firing inform request directly. -> " + paramSnmpInformRequest.getRequestId());
      }
      paramSnmpInformRequest.action();
    }
  }
  
  private void fireRequestList(Vector<SnmpInformRequest> paramVector)
  {
    while (!paramVector.isEmpty())
    {
      SnmpInformRequest localSnmpInformRequest = (SnmpInformRequest)paramVector.lastElement();
      if ((localSnmpInformRequest != null) && (localSnmpInformRequest.inProgress())) {
        fireRequest(localSnmpInformRequest);
      }
      paramVector.removeElementAt(paramVector.size() - 1);
    }
  }
  
  private final String reqListToString(Vector<SnmpInformRequest> paramVector)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramVector.size() * 100);
    Enumeration localEnumeration = paramVector.elements();
    while (localEnumeration.hasMoreElements())
    {
      localObject = (SnmpInformRequest)localEnumeration.nextElement();
      localStringBuilder.append("InformRequestId -> ");
      localStringBuilder.append(((SnmpInformRequest)localObject).getRequestId());
      localStringBuilder.append(" / Destination -> ");
      localStringBuilder.append(((SnmpInformRequest)localObject).getAddress());
      localStringBuilder.append(". ");
    }
    Object localObject = localStringBuilder.toString();
    return (String)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSendServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */