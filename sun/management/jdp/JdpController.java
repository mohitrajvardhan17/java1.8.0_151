package sun.management.jdp;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import sun.management.VMManagement;

public final class JdpController
{
  private static JDPControllerRunner controller = null;
  
  private JdpController() {}
  
  private static int getInteger(String paramString1, int paramInt, String paramString2)
    throws JdpException
  {
    try
    {
      return paramString1 == null ? paramInt : Integer.parseInt(paramString1);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new JdpException(paramString2);
    }
  }
  
  private static InetAddress getInetAddress(String paramString1, InetAddress paramInetAddress, String paramString2)
    throws JdpException
  {
    try
    {
      return paramString1 == null ? paramInetAddress : InetAddress.getByName(paramString1);
    }
    catch (UnknownHostException localUnknownHostException)
    {
      throw new JdpException(paramString2);
    }
  }
  
  private static Integer getProcessId()
  {
    try
    {
      RuntimeMXBean localRuntimeMXBean = ManagementFactory.getRuntimeMXBean();
      Field localField = localRuntimeMXBean.getClass().getDeclaredField("jvm");
      localField.setAccessible(true);
      VMManagement localVMManagement = (VMManagement)localField.get(localRuntimeMXBean);
      Method localMethod = localVMManagement.getClass().getDeclaredMethod("getProcessId", new Class[0]);
      localMethod.setAccessible(true);
      Integer localInteger = (Integer)localMethod.invoke(localVMManagement, new Object[0]);
      return localInteger;
    }
    catch (Exception localException) {}
    return null;
  }
  
  public static synchronized void startDiscoveryService(InetAddress paramInetAddress, int paramInt, String paramString1, String paramString2)
    throws IOException, JdpException
  {
    int i = getInteger(System.getProperty("com.sun.management.jdp.ttl"), 1, "Invalid jdp packet ttl");
    int j = getInteger(System.getProperty("com.sun.management.jdp.pause"), 5, "Invalid jdp pause");
    j *= 1000;
    InetAddress localInetAddress = getInetAddress(System.getProperty("com.sun.management.jdp.source_addr"), null, "Invalid source address provided");
    UUID localUUID = UUID.randomUUID();
    JdpJmxPacket localJdpJmxPacket = new JdpJmxPacket(localUUID, paramString2);
    String str = System.getProperty("sun.java.command");
    if (str != null)
    {
      localObject = str.split(" ", 2);
      localJdpJmxPacket.setMainClass(localObject[0]);
    }
    localJdpJmxPacket.setInstanceName(paramString1);
    Object localObject = System.getProperty("java.rmi.server.hostname");
    localJdpJmxPacket.setRmiHostname((String)localObject);
    localJdpJmxPacket.setBroadcastInterval(new Integer(j).toString());
    Integer localInteger = getProcessId();
    if (localInteger != null) {
      localJdpJmxPacket.setProcessId(localInteger.toString());
    }
    JdpBroadcaster localJdpBroadcaster = new JdpBroadcaster(paramInetAddress, localInetAddress, paramInt, i);
    stopDiscoveryService();
    controller = new JDPControllerRunner(localJdpBroadcaster, localJdpJmxPacket, j, null);
    Thread localThread = new Thread(controller, "JDP broadcaster");
    localThread.setDaemon(true);
    localThread.start();
  }
  
  public static synchronized void stopDiscoveryService()
  {
    if (controller != null)
    {
      controller.stop();
      controller = null;
    }
  }
  
  private static class JDPControllerRunner
    implements Runnable
  {
    private final JdpJmxPacket packet;
    private final JdpBroadcaster bcast;
    private final int pause;
    private volatile boolean shutdown = false;
    
    private JDPControllerRunner(JdpBroadcaster paramJdpBroadcaster, JdpJmxPacket paramJdpJmxPacket, int paramInt)
    {
      bcast = paramJdpBroadcaster;
      packet = paramJdpJmxPacket;
      pause = paramInt;
    }
    
    public void run()
    {
      try
      {
        while (!shutdown)
        {
          bcast.sendPacket(packet);
          try
          {
            Thread.sleep(pause);
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }
      catch (IOException localIOException1) {}
      try
      {
        stop();
        bcast.shutdown();
      }
      catch (IOException localIOException2) {}
    }
    
    public void stop()
    {
      shutdown = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\jdp\JdpController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */