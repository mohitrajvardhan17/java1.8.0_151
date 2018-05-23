package sun.rmi.transport.tcp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ConnectIOException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.security.AccessController;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.transport.Channel;
import sun.rmi.transport.Endpoint;
import sun.rmi.transport.Target;
import sun.rmi.transport.Transport;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;

public class TCPEndpoint
  implements Endpoint
{
  private String host;
  private int port;
  private final RMIClientSocketFactory csf;
  private final RMIServerSocketFactory ssf;
  private int listenPort = -1;
  private TCPTransport transport = null;
  private static String localHost;
  private static boolean localHostKnown = true;
  private static final Map<TCPEndpoint, LinkedList<TCPEndpoint>> localEndpoints = new HashMap();
  private static final int FORMAT_HOST_PORT = 0;
  private static final int FORMAT_HOST_PORT_FACTORY = 1;
  
  private static int getInt(String paramString, int paramInt)
  {
    return ((Integer)AccessController.doPrivileged(new GetIntegerAction(paramString, paramInt))).intValue();
  }
  
  private static boolean getBoolean(String paramString)
  {
    return ((Boolean)AccessController.doPrivileged(new GetBooleanAction(paramString))).booleanValue();
  }
  
  private static String getHostnameProperty()
  {
    return (String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.hostname"));
  }
  
  public TCPEndpoint(String paramString, int paramInt)
  {
    this(paramString, paramInt, null, null);
  }
  
  public TCPEndpoint(String paramString, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
  {
    if (paramString == null) {
      paramString = "";
    }
    host = paramString;
    port = paramInt;
    csf = paramRMIClientSocketFactory;
    ssf = paramRMIServerSocketFactory;
  }
  
  public static TCPEndpoint getLocalEndpoint(int paramInt)
  {
    return getLocalEndpoint(paramInt, null, null);
  }
  
  public static TCPEndpoint getLocalEndpoint(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
  {
    TCPEndpoint localTCPEndpoint1 = null;
    synchronized (localEndpoints)
    {
      TCPEndpoint localTCPEndpoint2 = new TCPEndpoint(null, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
      LinkedList localLinkedList = (LinkedList)localEndpoints.get(localTCPEndpoint2);
      String str1 = resampleLocalHost();
      if (localLinkedList == null)
      {
        localTCPEndpoint1 = new TCPEndpoint(str1, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
        localLinkedList = new LinkedList();
        localLinkedList.add(localTCPEndpoint1);
        listenPort = paramInt;
        transport = new TCPTransport(localLinkedList);
        localEndpoints.put(localTCPEndpoint2, localLinkedList);
        if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
          TCPTransport.tcpLog.log(Log.BRIEF, "created local endpoint for socket factory " + paramRMIServerSocketFactory + " on port " + paramInt);
        }
      }
      else
      {
        synchronized (localLinkedList)
        {
          localTCPEndpoint1 = (TCPEndpoint)localLinkedList.getLast();
          String str2 = host;
          int i = port;
          TCPTransport localTCPTransport = transport;
          if ((str1 != null) && (!str1.equals(str2)))
          {
            if (i != 0) {
              localLinkedList.clear();
            }
            localTCPEndpoint1 = new TCPEndpoint(str1, i, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
            listenPort = paramInt;
            transport = localTCPTransport;
            localLinkedList.add(localTCPEndpoint1);
          }
        }
      }
    }
    return localTCPEndpoint1;
  }
  
  private static String resampleLocalHost()
  {
    String str = getHostnameProperty();
    synchronized (localEndpoints)
    {
      if (str != null) {
        if (!localHostKnown)
        {
          setLocalHost(str);
        }
        else if (!str.equals(localHost))
        {
          localHost = str;
          if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
            TCPTransport.tcpLog.log(Log.BRIEF, "updated local hostname to: " + localHost);
          }
        }
      }
      return localHost;
    }
  }
  
  static void setLocalHost(String paramString)
  {
    synchronized (localEndpoints)
    {
      if (!localHostKnown)
      {
        localHost = paramString;
        localHostKnown = true;
        if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
          TCPTransport.tcpLog.log(Log.BRIEF, "local host set to " + paramString);
        }
        Iterator localIterator1 = localEndpoints.values().iterator();
        while (localIterator1.hasNext())
        {
          LinkedList localLinkedList = (LinkedList)localIterator1.next();
          synchronized (localLinkedList)
          {
            Iterator localIterator2 = localLinkedList.iterator();
            while (localIterator2.hasNext())
            {
              TCPEndpoint localTCPEndpoint = (TCPEndpoint)localIterator2.next();
              host = paramString;
            }
          }
        }
      }
    }
  }
  
  static void setDefaultPort(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
  {
    TCPEndpoint localTCPEndpoint1 = new TCPEndpoint(null, 0, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
    synchronized (localEndpoints)
    {
      LinkedList localLinkedList = (LinkedList)localEndpoints.get(localTCPEndpoint1);
      synchronized (localLinkedList)
      {
        int i = localLinkedList.size();
        TCPEndpoint localTCPEndpoint2 = (TCPEndpoint)localLinkedList.getLast();
        Iterator localIterator = localLinkedList.iterator();
        while (localIterator.hasNext())
        {
          TCPEndpoint localTCPEndpoint3 = (TCPEndpoint)localIterator.next();
          port = paramInt;
        }
        if (i > 1)
        {
          localLinkedList.clear();
          localLinkedList.add(localTCPEndpoint2);
        }
      }
      ??? = new TCPEndpoint(null, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
      localEndpoints.put(???, localLinkedList);
      if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
        TCPTransport.tcpLog.log(Log.BRIEF, "default port for server socket factory " + paramRMIServerSocketFactory + " and client socket factory " + paramRMIClientSocketFactory + " set to " + paramInt);
      }
    }
  }
  
  public Transport getOutboundTransport()
  {
    TCPEndpoint localTCPEndpoint = getLocalEndpoint(0, null, null);
    return transport;
  }
  
  private static Collection<TCPTransport> allKnownTransports()
  {
    HashSet localHashSet;
    synchronized (localEndpoints)
    {
      localHashSet = new HashSet(localEndpoints.size());
      Iterator localIterator = localEndpoints.values().iterator();
      while (localIterator.hasNext())
      {
        LinkedList localLinkedList = (LinkedList)localIterator.next();
        TCPEndpoint localTCPEndpoint = (TCPEndpoint)localLinkedList.getFirst();
        localHashSet.add(transport);
      }
    }
    return localHashSet;
  }
  
  public static void shedConnectionCaches()
  {
    Iterator localIterator = allKnownTransports().iterator();
    while (localIterator.hasNext())
    {
      TCPTransport localTCPTransport = (TCPTransport)localIterator.next();
      localTCPTransport.shedConnectionCaches();
    }
  }
  
  public void exportObject(Target paramTarget)
    throws RemoteException
  {
    transport.exportObject(paramTarget);
  }
  
  public Channel getChannel()
  {
    return getOutboundTransport().getChannel(this);
  }
  
  public String getHost()
  {
    return host;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public int getListenPort()
  {
    return listenPort;
  }
  
  public Transport getInboundTransport()
  {
    return transport;
  }
  
  public RMIClientSocketFactory getClientSocketFactory()
  {
    return csf;
  }
  
  public RMIServerSocketFactory getServerSocketFactory()
  {
    return ssf;
  }
  
  public String toString()
  {
    return "[" + host + ":" + port + (ssf != null ? "," + ssf : "") + (csf != null ? "," + csf : "") + "]";
  }
  
  public int hashCode()
  {
    return port;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof TCPEndpoint)))
    {
      TCPEndpoint localTCPEndpoint = (TCPEndpoint)paramObject;
      if ((port != port) || (!host.equals(host))) {
        return false;
      }
      if (((csf == null ? 1 : 0) ^ (csf == null ? 1 : 0)) == 0)
      {
        if (((ssf == null ? 1 : 0) ^ (ssf == null ? 1 : 0)) == 0) {}
      }
      else {
        return false;
      }
      if ((csf != null) && ((csf.getClass() != csf.getClass()) || (!csf.equals(csf)))) {
        return false;
      }
      return (ssf == null) || ((ssf.getClass() == ssf.getClass()) && (ssf.equals(ssf)));
    }
    return false;
  }
  
  public void write(ObjectOutput paramObjectOutput)
    throws IOException
  {
    if (csf == null)
    {
      paramObjectOutput.writeByte(0);
      paramObjectOutput.writeUTF(host);
      paramObjectOutput.writeInt(port);
    }
    else
    {
      paramObjectOutput.writeByte(1);
      paramObjectOutput.writeUTF(host);
      paramObjectOutput.writeInt(port);
      paramObjectOutput.writeObject(csf);
    }
  }
  
  public static TCPEndpoint read(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    RMIClientSocketFactory localRMIClientSocketFactory = null;
    int j = paramObjectInput.readByte();
    String str;
    int i;
    switch (j)
    {
    case 0: 
      str = paramObjectInput.readUTF();
      i = paramObjectInput.readInt();
      break;
    case 1: 
      str = paramObjectInput.readUTF();
      i = paramObjectInput.readInt();
      localRMIClientSocketFactory = (RMIClientSocketFactory)paramObjectInput.readObject();
      break;
    default: 
      throw new IOException("invalid endpoint format");
    }
    return new TCPEndpoint(str, i, localRMIClientSocketFactory, null);
  }
  
  public void writeHostPortFormat(DataOutput paramDataOutput)
    throws IOException
  {
    if (csf != null) {
      throw new InternalError("TCPEndpoint.writeHostPortFormat: called for endpoint with non-null socket factory");
    }
    paramDataOutput.writeUTF(host);
    paramDataOutput.writeInt(port);
  }
  
  public static TCPEndpoint readHostPortFormat(DataInput paramDataInput)
    throws IOException
  {
    String str = paramDataInput.readUTF();
    int i = paramDataInput.readInt();
    return new TCPEndpoint(str, i);
  }
  
  private static RMISocketFactory chooseFactory()
  {
    RMISocketFactory localRMISocketFactory = RMISocketFactory.getSocketFactory();
    if (localRMISocketFactory == null) {
      localRMISocketFactory = TCPTransport.defaultSocketFactory;
    }
    return localRMISocketFactory;
  }
  
  Socket newSocket()
    throws RemoteException
  {
    if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
      TCPTransport.tcpLog.log(Log.VERBOSE, "opening socket to " + this);
    }
    Socket localSocket;
    try
    {
      Object localObject = csf;
      if (localObject == null) {
        localObject = chooseFactory();
      }
      localSocket = ((RMIClientSocketFactory)localObject).createSocket(host, port);
    }
    catch (java.net.UnknownHostException localUnknownHostException)
    {
      throw new java.rmi.UnknownHostException("Unknown host: " + host, localUnknownHostException);
    }
    catch (java.net.ConnectException localConnectException)
    {
      throw new java.rmi.ConnectException("Connection refused to host: " + host, localConnectException);
    }
    catch (IOException localIOException)
    {
      try
      {
        shedConnectionCaches();
      }
      catch (OutOfMemoryError|Exception localOutOfMemoryError) {}
      throw new ConnectIOException("Exception creating connection to: " + host, localIOException);
    }
    try
    {
      localSocket.setTcpNoDelay(true);
    }
    catch (Exception localException1) {}
    try
    {
      localSocket.setKeepAlive(true);
    }
    catch (Exception localException2) {}
    return localSocket;
  }
  
  ServerSocket newServerSocket()
    throws IOException
  {
    if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
      TCPTransport.tcpLog.log(Log.VERBOSE, "creating server socket on " + this);
    }
    Object localObject = ssf;
    if (localObject == null) {
      localObject = chooseFactory();
    }
    ServerSocket localServerSocket = ((RMIServerSocketFactory)localObject).createServerSocket(listenPort);
    if (listenPort == 0) {
      setDefaultPort(localServerSocket.getLocalPort(), csf, ssf);
    }
    return localServerSocket;
  }
  
  static
  {
    localHost = getHostnameProperty();
    if (localHost == null) {
      try
      {
        InetAddress localInetAddress = InetAddress.getLocalHost();
        byte[] arrayOfByte = localInetAddress.getAddress();
        if ((arrayOfByte[0] == Byte.MAX_VALUE) && (arrayOfByte[1] == 0) && (arrayOfByte[2] == 0) && (arrayOfByte[3] == 1)) {
          localHostKnown = false;
        }
        if (getBoolean("java.rmi.server.useLocalHostName")) {
          localHost = FQDN.attemptFQDN(localInetAddress);
        } else {
          localHost = localInetAddress.getHostAddress();
        }
      }
      catch (Exception localException)
      {
        localHostKnown = false;
        localHost = null;
      }
    }
    if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
      TCPTransport.tcpLog.log(Log.BRIEF, "localHostKnown = " + localHostKnown + ", localHost = " + localHost);
    }
  }
  
  private static class FQDN
    implements Runnable
  {
    private String reverseLookup;
    private String hostAddress;
    
    private FQDN(String paramString)
    {
      hostAddress = paramString;
    }
    
    static String attemptFQDN(InetAddress paramInetAddress)
      throws java.net.UnknownHostException
    {
      Object localObject1 = paramInetAddress.getHostName();
      if (((String)localObject1).indexOf('.') < 0)
      {
        String str = paramInetAddress.getHostAddress();
        FQDN localFQDN = new FQDN(str);
        int i = TCPEndpoint.getInt("sun.rmi.transport.tcp.localHostNameTimeOut", 10000);
        try
        {
          synchronized (localFQDN)
          {
            localFQDN.getFQDN();
            localFQDN.wait(i);
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          Thread.currentThread().interrupt();
        }
        localObject1 = localFQDN.getHost();
        if ((localObject1 == null) || (((String)localObject1).equals("")) || (((String)localObject1).indexOf('.') < 0)) {
          localObject1 = str;
        }
      }
      return (String)localObject1;
    }
    
    private void getFQDN()
    {
      Thread localThread = (Thread)AccessController.doPrivileged(new NewThreadAction(this, "FQDN Finder", true));
      localThread.start();
    }
    
    private synchronized String getHost()
    {
      return reverseLookup;
    }
    
    public void run()
    {
      String str = null;
      try
      {
        str = InetAddress.getByName(hostAddress).getHostName();
      }
      catch (java.net.UnknownHostException ???) {}finally
      {
        synchronized (this)
        {
          reverseLookup = str;
          notify();
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\tcp\TCPEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */