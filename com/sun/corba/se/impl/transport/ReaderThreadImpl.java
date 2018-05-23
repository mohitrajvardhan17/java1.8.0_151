package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ReaderThread;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;

public class ReaderThreadImpl
  implements ReaderThread, Work
{
  private ORB orb;
  private Connection connection;
  private Selector selector;
  private boolean keepRunning;
  private long enqueueTime;
  
  public ReaderThreadImpl(ORB paramORB, Connection paramConnection, Selector paramSelector)
  {
    orb = paramORB;
    connection = paramConnection;
    selector = paramSelector;
    keepRunning = true;
  }
  
  public Connection getConnection()
  {
    return connection;
  }
  
  public void close()
  {
    if (orb.transportDebugFlag) {
      dprint(".close: " + connection);
    }
    keepRunning = false;
  }
  
  public void doWork()
  {
    try
    {
      if (orb.transportDebugFlag) {
        dprint(".doWork: Start ReaderThread: " + connection);
      }
      while (keepRunning) {
        try
        {
          if (orb.transportDebugFlag) {
            dprint(".doWork: Start ReaderThread cycle: " + connection);
          }
          if (connection.read()) {
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          if (orb.transportDebugFlag) {
            dprint(".doWork: exception in read: " + connection, localThrowable);
          }
          orb.getTransportManager().getSelector(0).unregisterForEvent(getConnection().getEventHandler());
          getConnection().close();
        }
      }
    }
    finally
    {
      if (orb.transportDebugFlag) {
        dprint(".doWork: Terminated ReaderThread: " + connection);
      }
    }
  }
  
  public void setEnqueueTime(long paramLong)
  {
    enqueueTime = paramLong;
  }
  
  public long getEnqueueTime()
  {
    return enqueueTime;
  }
  
  public String getName()
  {
    return "ReaderThread";
  }
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint("ReaderThreadImpl", paramString);
  }
  
  protected void dprint(String paramString, Throwable paramThrowable)
  {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\ReaderThreadImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */