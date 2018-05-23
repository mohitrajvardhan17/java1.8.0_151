package sun.rmi.transport.tcp;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.transport.Connection;

class ConnectionAcceptor
  implements Runnable
{
  private TCPTransport transport;
  private List<Connection> queue = new ArrayList();
  private static int threadNum = 0;
  
  public ConnectionAcceptor(TCPTransport paramTCPTransport)
  {
    transport = paramTCPTransport;
  }
  
  public void startNewAcceptor()
  {
    Thread localThread = (Thread)AccessController.doPrivileged(new NewThreadAction(this, "Multiplex Accept-" + ++threadNum, true));
    localThread.start();
  }
  
  public void accept(Connection paramConnection)
  {
    synchronized (queue)
    {
      queue.add(paramConnection);
      queue.notify();
    }
  }
  
  public void run()
  {
    Connection localConnection;
    synchronized (queue)
    {
      while (queue.size() == 0) {
        try
        {
          queue.wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      startNewAcceptor();
      localConnection = (Connection)queue.remove(0);
    }
    transport.handleMessages(localConnection, true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\tcp\ConnectionAcceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */