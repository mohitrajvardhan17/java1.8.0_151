package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SnmpTimerServer
  extends Thread
{
  private SnmpInformRequest req = null;
  SnmpQManager snmpq = null;
  boolean isBeingDestroyed = false;
  
  public SnmpTimerServer(ThreadGroup paramThreadGroup, SnmpQManager paramSnmpQManager)
  {
    super(paramThreadGroup, "SnmpTimerServer");
    setName("SnmpTimerServer");
    snmpq = paramSnmpQManager;
    start();
  }
  
  public synchronized void stopTimerServer()
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
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpTimerServer.class.getName(), "run", "Timer Thread started");
    }
    for (;;)
    {
      try
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpTimerServer.class.getName(), "run", "Blocking for inform requests");
        }
        if (req == null) {
          req = snmpq.getTimeoutRequests();
        }
        if ((req != null) && (req.inProgress()))
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpTimerServer.class.getName(), "run", "Handle timeout inform request " + req.getRequestId());
          }
          req.action();
          req = null;
        }
        if (isBeingDestroyed == true) {
          break;
        }
      }
      catch (Exception localException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", "Got unexpected exception", localException);
        }
      }
      catch (ThreadDeath localThreadDeath)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", "ThreadDeath, timer server unexpectedly shutting down", localThreadDeath);
        }
        throw localThreadDeath;
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", "OutOfMemoryError", localOutOfMemoryError);
        }
        yield();
      }
      catch (Error localError)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", "Received Internal error", localError);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpTimerServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */