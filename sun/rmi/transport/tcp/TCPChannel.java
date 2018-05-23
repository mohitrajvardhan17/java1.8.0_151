package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.Socket;
import java.rmi.ConnectIOException;
import java.rmi.RemoteException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.WeakHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.RuntimeUtil;
import sun.rmi.runtime.RuntimeUtil.GetInstanceAction;
import sun.rmi.transport.Channel;
import sun.rmi.transport.Connection;
import sun.rmi.transport.Endpoint;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetLongAction;

public class TCPChannel
  implements Channel
{
  private final TCPEndpoint ep;
  private final TCPTransport tr;
  private final List<TCPConnection> freeList = new ArrayList();
  private Future<?> reaper = null;
  private boolean usingMultiplexer = false;
  private ConnectionMultiplexer multiplexer = null;
  private ConnectionAcceptor acceptor;
  private AccessControlContext okContext;
  private WeakHashMap<AccessControlContext, Reference<AccessControlContext>> authcache;
  private SecurityManager cacheSecurityManager = null;
  private static final long idleTimeout = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.connectionTimeout", 15000L))).longValue();
  private static final int handshakeTimeout = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.handshakeTimeout", 60000))).intValue();
  private static final int responseTimeout = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.responseTimeout", 0))).intValue();
  private static final ScheduledExecutorService scheduler = ((RuntimeUtil)AccessController.doPrivileged(new RuntimeUtil.GetInstanceAction())).getScheduler();
  
  TCPChannel(TCPTransport paramTCPTransport, TCPEndpoint paramTCPEndpoint)
  {
    tr = paramTCPTransport;
    ep = paramTCPEndpoint;
  }
  
  public Endpoint getEndpoint()
  {
    return ep;
  }
  
  private void checkConnectPermission()
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null) {
      return;
    }
    if (localSecurityManager != cacheSecurityManager)
    {
      okContext = null;
      authcache = new WeakHashMap();
      cacheSecurityManager = localSecurityManager;
    }
    AccessControlContext localAccessControlContext = AccessController.getContext();
    if ((okContext == null) || ((!okContext.equals(localAccessControlContext)) && (!authcache.containsKey(localAccessControlContext))))
    {
      localSecurityManager.checkConnect(ep.getHost(), ep.getPort());
      authcache.put(localAccessControlContext, new SoftReference(localAccessControlContext));
    }
    okContext = localAccessControlContext;
  }
  
  public Connection newConnection()
    throws RemoteException
  {
    TCPConnection localTCPConnection;
    do
    {
      localTCPConnection = null;
      synchronized (freeList)
      {
        int i = freeList.size() - 1;
        if (i >= 0)
        {
          checkConnectPermission();
          localTCPConnection = (TCPConnection)freeList.get(i);
          freeList.remove(i);
        }
      }
      if (localTCPConnection != null)
      {
        if (!localTCPConnection.isDead())
        {
          TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
          return localTCPConnection;
        }
        free(localTCPConnection, false);
      }
    } while (localTCPConnection != null);
    return createConnection();
  }
  
  private Connection createConnection()
    throws RemoteException
  {
    TCPTransport.tcpLog.log(Log.BRIEF, "create connection");
    TCPConnection localTCPConnection;
    if (!usingMultiplexer)
    {
      Socket localSocket = ep.newSocket();
      localTCPConnection = new TCPConnection(this, localSocket);
      try
      {
        DataOutputStream localDataOutputStream = new DataOutputStream(localTCPConnection.getOutputStream());
        writeTransportHeader(localDataOutputStream);
        if (!localTCPConnection.isReusable())
        {
          localDataOutputStream.writeByte(76);
        }
        else
        {
          localDataOutputStream.writeByte(75);
          localDataOutputStream.flush();
          int i = 0;
          try
          {
            i = localSocket.getSoTimeout();
            localSocket.setSoTimeout(handshakeTimeout);
          }
          catch (Exception localException1) {}
          DataInputStream localDataInputStream = new DataInputStream(localTCPConnection.getInputStream());
          int j = localDataInputStream.readByte();
          if (j != 78) {
            throw new ConnectIOException(j == 79 ? "JRMP StreamProtocol not supported by server" : "non-JRMP server at remote endpoint");
          }
          String str = localDataInputStream.readUTF();
          int k = localDataInputStream.readInt();
          if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
            TCPTransport.tcpLog.log(Log.VERBOSE, "server suggested " + str + ":" + k);
          }
          TCPEndpoint.setLocalHost(str);
          TCPEndpoint localTCPEndpoint = TCPEndpoint.getLocalEndpoint(0, null, null);
          localDataOutputStream.writeUTF(localTCPEndpoint.getHost());
          localDataOutputStream.writeInt(localTCPEndpoint.getPort());
          if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
            TCPTransport.tcpLog.log(Log.VERBOSE, "using " + localTCPEndpoint.getHost() + ":" + localTCPEndpoint.getPort());
          }
          try
          {
            localSocket.setSoTimeout(i != 0 ? i : responseTimeout);
          }
          catch (Exception localException2) {}
          localDataOutputStream.flush();
        }
      }
      catch (IOException localIOException2)
      {
        if ((localIOException2 instanceof RemoteException)) {
          throw ((RemoteException)localIOException2);
        }
        throw new ConnectIOException("error during JRMP connection establishment", localIOException2);
      }
    }
    else
    {
      try
      {
        localTCPConnection = multiplexer.openConnection();
      }
      catch (IOException localIOException1)
      {
        synchronized (this)
        {
          usingMultiplexer = false;
          multiplexer = null;
        }
        throw new ConnectIOException("error opening virtual connection over multiplexed connection", localIOException1);
      }
    }
    return localTCPConnection;
  }
  
  public void free(Connection paramConnection, boolean paramBoolean)
  {
    if (paramConnection == null) {
      return;
    }
    if ((paramBoolean) && (paramConnection.isReusable()))
    {
      long l = System.currentTimeMillis();
      TCPConnection localTCPConnection = (TCPConnection)paramConnection;
      TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
      synchronized (freeList)
      {
        freeList.add(localTCPConnection);
        if (reaper == null)
        {
          TCPTransport.tcpLog.log(Log.BRIEF, "create reaper");
          reaper = scheduler.scheduleWithFixedDelay(new Runnable()
          {
            public void run()
            {
              TCPTransport.tcpLog.log(Log.VERBOSE, "wake up");
              TCPChannel.this.freeCachedConnections();
            }
          }, idleTimeout, idleTimeout, TimeUnit.MILLISECONDS);
        }
      }
      localTCPConnection.setLastUseTime(l);
      localTCPConnection.setExpiration(l + idleTimeout);
    }
    else
    {
      TCPTransport.tcpLog.log(Log.BRIEF, "close connection");
      try
      {
        paramConnection.close();
      }
      catch (IOException localIOException) {}
    }
  }
  
  private void writeTransportHeader(DataOutputStream paramDataOutputStream)
    throws RemoteException
  {
    try
    {
      DataOutputStream localDataOutputStream = new DataOutputStream(paramDataOutputStream);
      localDataOutputStream.writeInt(1246907721);
      localDataOutputStream.writeShort(2);
    }
    catch (IOException localIOException)
    {
      throw new ConnectIOException("error writing JRMP transport header", localIOException);
    }
  }
  
  synchronized void useMultiplexer(ConnectionMultiplexer paramConnectionMultiplexer)
  {
    multiplexer = paramConnectionMultiplexer;
    usingMultiplexer = true;
  }
  
  void acceptMultiplexConnection(Connection paramConnection)
  {
    if (acceptor == null)
    {
      acceptor = new ConnectionAcceptor(tr);
      acceptor.startNewAcceptor();
    }
    acceptor.accept(paramConnection);
  }
  
  public void shedCache()
  {
    Connection[] arrayOfConnection;
    synchronized (freeList)
    {
      arrayOfConnection = (Connection[])freeList.toArray(new Connection[freeList.size()]);
      freeList.clear();
    }
    int i = arrayOfConnection.length;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      Connection localConnection = arrayOfConnection[i];
      arrayOfConnection[i] = null;
      try
      {
        localConnection.close();
      }
      catch (IOException localIOException) {}
    }
  }
  
  private void freeCachedConnections()
  {
    synchronized (freeList)
    {
      int i = freeList.size();
      if (i > 0)
      {
        long l = System.currentTimeMillis();
        ListIterator localListIterator = freeList.listIterator(i);
        while (localListIterator.hasPrevious())
        {
          TCPConnection localTCPConnection = (TCPConnection)localListIterator.previous();
          if (localTCPConnection.expired(l))
          {
            TCPTransport.tcpLog.log(Log.VERBOSE, "connection timeout expired");
            try
            {
              localTCPConnection.close();
            }
            catch (IOException localIOException) {}
            localListIterator.remove();
          }
        }
      }
      if (freeList.isEmpty())
      {
        reaper.cancel(false);
        reaper = null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\tcp\TCPChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */