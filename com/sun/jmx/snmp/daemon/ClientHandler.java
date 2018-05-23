package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.ObjectName;

abstract class ClientHandler
  implements Runnable
{
  protected CommunicatorServer adaptorServer = null;
  protected int requestId = -1;
  protected MBeanServer mbs = null;
  protected ObjectName objectName = null;
  protected Thread thread = null;
  protected boolean interruptCalled = false;
  protected String dbgTag = null;
  
  public ClientHandler(CommunicatorServer paramCommunicatorServer, int paramInt, MBeanServer paramMBeanServer, ObjectName paramObjectName)
  {
    adaptorServer = paramCommunicatorServer;
    requestId = paramInt;
    mbs = paramMBeanServer;
    objectName = paramObjectName;
    interruptCalled = false;
    dbgTag = makeDebugTag();
    thread = createThread(this);
  }
  
  Thread createThread(Runnable paramRunnable)
  {
    return new Thread(this);
  }
  
  public void interrupt()
  {
    JmxProperties.SNMP_ADAPTOR_LOGGER.entering(dbgTag, "interrupt");
    interruptCalled = true;
    if (thread != null) {
      thread.interrupt();
    }
    JmxProperties.SNMP_ADAPTOR_LOGGER.exiting(dbgTag, "interrupt");
  }
  
  public void join()
  {
    if (thread != null) {
      try
      {
        thread.join();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 100	com/sun/jmx/snmp/daemon/ClientHandler:adaptorServer	Lcom/sun/jmx/snmp/daemon/CommunicatorServer;
    //   4: aload_0
    //   5: invokevirtual 109	com/sun/jmx/snmp/daemon/CommunicatorServer:notifyClientHandlerCreated	(Lcom/sun/jmx/snmp/daemon/ClientHandler;)V
    //   8: aload_0
    //   9: invokevirtual 105	com/sun/jmx/snmp/daemon/ClientHandler:doRun	()V
    //   12: aload_0
    //   13: getfield 100	com/sun/jmx/snmp/daemon/ClientHandler:adaptorServer	Lcom/sun/jmx/snmp/daemon/CommunicatorServer;
    //   16: aload_0
    //   17: invokevirtual 110	com/sun/jmx/snmp/daemon/CommunicatorServer:notifyClientHandlerDeleted	(Lcom/sun/jmx/snmp/daemon/ClientHandler;)V
    //   20: goto +14 -> 34
    //   23: astore_1
    //   24: aload_0
    //   25: getfield 100	com/sun/jmx/snmp/daemon/ClientHandler:adaptorServer	Lcom/sun/jmx/snmp/daemon/CommunicatorServer;
    //   28: aload_0
    //   29: invokevirtual 110	com/sun/jmx/snmp/daemon/CommunicatorServer:notifyClientHandlerDeleted	(Lcom/sun/jmx/snmp/daemon/ClientHandler;)V
    //   32: aload_1
    //   33: athrow
    //   34: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	ClientHandler
    //   23	10	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	12	23	finally
  }
  
  public abstract void doRun();
  
  protected String makeDebugTag()
  {
    return "ClientHandler[" + adaptorServer.getProtocol() + ":" + adaptorServer.getPort() + "][" + requestId + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\ClientHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */