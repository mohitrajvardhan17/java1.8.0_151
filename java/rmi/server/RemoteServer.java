package java.rmi.server;

import java.io.OutputStream;
import java.io.PrintStream;
import sun.rmi.runtime.Log;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.transport.tcp.TCPTransport;

public abstract class RemoteServer
  extends RemoteObject
{
  private static final long serialVersionUID = -4100238210092549637L;
  private static boolean logNull = !UnicastServerRef.logCalls;
  
  protected RemoteServer() {}
  
  protected RemoteServer(RemoteRef paramRemoteRef)
  {
    super(paramRemoteRef);
  }
  
  public static String getClientHost()
    throws ServerNotActiveException
  {
    return TCPTransport.getClientHost();
  }
  
  public static void setLog(OutputStream paramOutputStream)
  {
    logNull = paramOutputStream == null;
    UnicastServerRef.callLog.setOutputStream(paramOutputStream);
  }
  
  public static PrintStream getLog()
  {
    return logNull ? null : UnicastServerRef.callLog.getPrintStream();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\RemoteServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */