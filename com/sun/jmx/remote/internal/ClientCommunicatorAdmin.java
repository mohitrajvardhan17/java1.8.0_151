package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.InterruptedIOException;

public abstract class ClientCommunicatorAdmin
{
  private static volatile long threadNo = 1L;
  private final Checker checker;
  private long period;
  private static final int CONNECTED = 0;
  private static final int RE_CONNECTING = 1;
  private static final int FAILED = 2;
  private static final int TERMINATED = 3;
  private int state = 0;
  private final int[] lock = new int[0];
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ClientCommunicatorAdmin");
  
  public ClientCommunicatorAdmin(long paramLong)
  {
    period = paramLong;
    if (paramLong > 0L)
    {
      checker = new Checker(null);
      Thread localThread = new Thread(checker, "JMX client heartbeat " + ++threadNo);
      localThread.setDaemon(true);
      localThread.start();
    }
    else
    {
      checker = null;
    }
  }
  
  public void gotIOException(IOException paramIOException)
    throws IOException
  {
    restart(paramIOException);
  }
  
  protected abstract void checkConnection()
    throws IOException;
  
  protected abstract void doStart()
    throws IOException;
  
  protected abstract void doStop();
  
  public void terminate()
  {
    synchronized (lock)
    {
      if (state == 3) {
        return;
      }
      state = 3;
      lock.notifyAll();
      if (checker != null) {
        checker.stop();
      }
    }
  }
  
  private void restart(IOException paramIOException)
    throws IOException
  {
    synchronized (lock)
    {
      if (state == 3) {
        throw new IOException("The client has been closed.");
      }
      if (state == 2) {
        throw paramIOException;
      }
      if (state == 1)
      {
        while (state == 1) {
          try
          {
            lock.wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            InterruptedIOException localInterruptedIOException = new InterruptedIOException(localInterruptedException.toString());
            EnvHelp.initCause(localInterruptedIOException, localInterruptedException);
            throw localInterruptedIOException;
          }
        }
        if (state == 3) {
          throw new IOException("The client has been closed.");
        }
        if (state != 0) {
          throw paramIOException;
        }
        return;
      }
      state = 1;
      lock.notifyAll();
    }
    try
    {
      doStart();
      synchronized (lock)
      {
        if (state == 3) {
          throw new IOException("The client has been closed.");
        }
        state = 0;
        lock.notifyAll();
      }
      return;
    }
    catch (Exception localException1)
    {
      logger.warning("restart", "Failed to restart: " + localException1);
      logger.debug("restart", localException1);
      synchronized (lock)
      {
        if (state == 3) {
          throw new IOException("The client has been closed.");
        }
        state = 2;
        lock.notifyAll();
      }
      try
      {
        doStop();
      }
      catch (Exception localException2) {}
      terminate();
      throw paramIOException;
    }
  }
  
  private class Checker
    implements Runnable
  {
    private Thread myThread;
    
    private Checker() {}
    
    public void run()
    {
      myThread = Thread.currentThread();
      while ((state != 3) && (!myThread.isInterrupted()))
      {
        try
        {
          Thread.sleep(period);
        }
        catch (InterruptedException localInterruptedException) {}
        if ((state == 3) || (myThread.isInterrupted())) {
          break;
        }
        try
        {
          checkConnection();
        }
        catch (Exception localException1)
        {
          synchronized (lock)
          {
            if ((state == 3) || (myThread.isInterrupted())) {
              break;
            }
          }
          Exception localException2 = (Exception)EnvHelp.getCause(localException1);
          if (((localException2 instanceof IOException)) && (!(localException2 instanceof InterruptedIOException)))
          {
            try
            {
              gotIOException((IOException)localException2);
            }
            catch (Exception localException3)
            {
              ClientCommunicatorAdmin.logger.warning("Checker-run", "Failed to check connection: " + localException2);
              ClientCommunicatorAdmin.logger.warning("Checker-run", "stopping");
              ClientCommunicatorAdmin.logger.debug("Checker-run", localException2);
              break;
            }
          }
          else
          {
            ClientCommunicatorAdmin.logger.warning("Checker-run", "Failed to check the connection: " + localException2);
            ClientCommunicatorAdmin.logger.debug("Checker-run", localException2);
            break;
          }
        }
      }
      if (ClientCommunicatorAdmin.logger.traceOn()) {
        ClientCommunicatorAdmin.logger.trace("Checker-run", "Finished.");
      }
    }
    
    private void stop()
    {
      if ((myThread != null) && (myThread != Thread.currentThread())) {
        myThread.interrupt();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\ClientCommunicatorAdmin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */