package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;

public abstract class ServerCommunicatorAdmin
{
  private long timestamp;
  private final int[] lock = new int[0];
  private int currentJobs = 0;
  private long timeout;
  private boolean terminated = false;
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ServerCommunicatorAdmin");
  private static final ClassLogger timelogger = new ClassLogger("javax.management.remote.timeout", "ServerCommunicatorAdmin");
  
  public ServerCommunicatorAdmin(long paramLong)
  {
    if (logger.traceOn()) {
      logger.trace("Constructor", "Creates a new ServerCommunicatorAdmin object with the timeout " + paramLong);
    }
    timeout = paramLong;
    timestamp = 0L;
    if (paramLong < Long.MAX_VALUE)
    {
      Timeout localTimeout = new Timeout(null);
      Thread localThread = new Thread(localTimeout);
      localThread.setName("JMX server connection timeout " + localThread.getId());
      localThread.setDaemon(true);
      localThread.start();
    }
  }
  
  public boolean reqIncoming()
  {
    if (logger.traceOn()) {
      logger.trace("reqIncoming", "Receive a new request.");
    }
    synchronized (lock)
    {
      if (terminated) {
        logger.warning("reqIncoming", "The server has decided to close this client connection.");
      }
      currentJobs += 1;
      return terminated;
    }
  }
  
  public boolean rspOutgoing()
  {
    if (logger.traceOn()) {
      logger.trace("reqIncoming", "Finish a request.");
    }
    synchronized (lock)
    {
      if (--currentJobs == 0)
      {
        timestamp = System.currentTimeMillis();
        logtime("Admin: Timestamp=", timestamp);
        lock.notify();
      }
      return terminated;
    }
  }
  
  protected abstract void doStop();
  
  public void terminate()
  {
    if (logger.traceOn()) {
      logger.trace("terminate", "terminate the ServerCommunicatorAdmin object.");
    }
    synchronized (lock)
    {
      if (terminated) {
        return;
      }
      terminated = true;
      lock.notify();
    }
  }
  
  private void logtime(String paramString, long paramLong)
  {
    timelogger.trace("synchro", paramString + paramLong);
  }
  
  private class Timeout
    implements Runnable
  {
    private Timeout() {}
    
    public void run()
    {
      int i = 0;
      synchronized (lock)
      {
        if (timestamp == 0L) {
          timestamp = System.currentTimeMillis();
        }
        ServerCommunicatorAdmin.this.logtime("Admin: timeout=", timeout);
        ServerCommunicatorAdmin.this.logtime("Admin: Timestamp=", timestamp);
        while (!terminated) {
          try
          {
            while ((!terminated) && (currentJobs != 0))
            {
              if (ServerCommunicatorAdmin.logger.traceOn()) {
                ServerCommunicatorAdmin.logger.trace("Timeout-run", "Waiting without timeout.");
              }
              lock.wait();
            }
            if (terminated) {
              return;
            }
            long l1 = timeout - (System.currentTimeMillis() - timestamp);
            ServerCommunicatorAdmin.this.logtime("Admin: remaining timeout=", l1);
            if (l1 > 0L)
            {
              if (ServerCommunicatorAdmin.logger.traceOn()) {
                ServerCommunicatorAdmin.logger.trace("Timeout-run", "Waiting with timeout: " + l1 + " ms remaining");
              }
              lock.wait(l1);
            }
            if (currentJobs <= 0)
            {
              long l2 = System.currentTimeMillis() - timestamp;
              ServerCommunicatorAdmin.this.logtime("Admin: elapsed=", l2);
              if ((!terminated) && (l2 > timeout))
              {
                if (ServerCommunicatorAdmin.logger.traceOn()) {
                  ServerCommunicatorAdmin.logger.trace("Timeout-run", "timeout elapsed");
                }
                ServerCommunicatorAdmin.this.logtime("Admin: timeout elapsed! " + l2 + ">", timeout);
                terminated = true;
                i = 1;
              }
            }
          }
          catch (InterruptedException localInterruptedException)
          {
            ServerCommunicatorAdmin.logger.warning("Timeout-run", "Unexpected Exception: " + localInterruptedException);
            ServerCommunicatorAdmin.logger.debug("Timeout-run", localInterruptedException);
            return;
          }
        }
      }
      if (i != 0)
      {
        if (ServerCommunicatorAdmin.logger.traceOn()) {
          ServerCommunicatorAdmin.logger.trace("Timeout-run", "Call the doStop.");
        }
        doStop();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\ServerCommunicatorAdmin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */