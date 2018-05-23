package sun.net;

import java.net.SocketException;
import java.security.AccessController;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.action.GetPropertyAction;

public class ResourceManager
{
  private static final int DEFAULT_MAX_SOCKETS = 25;
  private static final int maxSockets;
  private static final AtomicInteger numSockets = new AtomicInteger(0);
  
  public ResourceManager() {}
  
  public static void beforeUdpCreate()
    throws SocketException
  {
    if ((System.getSecurityManager() != null) && (numSockets.incrementAndGet() > maxSockets))
    {
      numSockets.decrementAndGet();
      throw new SocketException("maximum number of DatagramSockets reached");
    }
  }
  
  public static void afterUdpClose()
  {
    if (System.getSecurityManager() != null) {
      numSockets.decrementAndGet();
    }
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.net.maxDatagramSockets"));
    int i = 25;
    try
    {
      if (str != null) {
        i = Integer.parseInt(str);
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
    maxSockets = i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ResourceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */