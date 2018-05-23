package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.extension.RequestPartitioningPolicy;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.spi.transport.ORBSocketFactory;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import sun.corba.OutputStreamFactory;

public class SocketOrChannelAcceptorImpl
  extends EventHandlerBase
  implements CorbaAcceptor, SocketOrChannelAcceptor, Work, SocketInfo, LegacyServerSocketEndPointInfo
{
  protected ServerSocketChannel serverSocketChannel;
  protected ServerSocket serverSocket;
  protected int port;
  protected long enqueueTime;
  protected boolean initialized;
  protected ORBUtilSystemException wrapper;
  protected InboundConnectionCache connectionCache;
  protected String type = "";
  protected String name = "";
  protected String hostname;
  protected int locatorPort;
  
  public SocketOrChannelAcceptorImpl(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    setWork(this);
    initialized = false;
    hostname = paramORB.getORBData().getORBServerHost();
    name = "NO_NAME";
    locatorPort = -1;
  }
  
  public SocketOrChannelAcceptorImpl(ORB paramORB, int paramInt)
  {
    this(paramORB);
    port = paramInt;
  }
  
  public SocketOrChannelAcceptorImpl(ORB paramORB, int paramInt, String paramString1, String paramString2)
  {
    this(paramORB, paramInt);
    name = paramString1;
  }
  
  public boolean initialize()
  {
    if (initialized) {
      return false;
    }
    if (orb.transportDebugFlag) {
      dprint(".initialize: " + this);
    }
    InetSocketAddress localInetSocketAddress = null;
    try
    {
      if (orb.getORBData().getListenOnAllInterfaces().equals("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces"))
      {
        localInetSocketAddress = new InetSocketAddress(port);
      }
      else
      {
        String str = orb.getORBData().getORBServerHost();
        localInetSocketAddress = new InetSocketAddress(str, port);
      }
      serverSocket = orb.getORBData().getSocketFactory().createServerSocket(type, localInetSocketAddress);
      internalInitialize();
    }
    catch (Throwable localThrowable)
    {
      throw wrapper.createListenerFailed(localThrowable, Integer.toString(port));
    }
    initialized = true;
    return true;
  }
  
  protected void internalInitialize()
    throws Exception
  {
    port = serverSocket.getLocalPort();
    orb.getCorbaTransportManager().getInboundConnectionCache(this);
    serverSocketChannel = serverSocket.getChannel();
    if (serverSocketChannel != null)
    {
      setUseSelectThreadToWait(orb.getORBData().acceptorSocketUseSelectThreadToWait());
      serverSocketChannel.configureBlocking(!orb.getORBData().acceptorSocketUseSelectThreadToWait());
    }
    else
    {
      setUseSelectThreadToWait(false);
    }
    setUseWorkerThreadForEvent(orb.getORBData().acceptorSocketUseWorkerThreadForEvent());
  }
  
  public boolean initialized()
  {
    return initialized;
  }
  
  public String getConnectionCacheType()
  {
    return getClass().toString();
  }
  
  public void setConnectionCache(InboundConnectionCache paramInboundConnectionCache)
  {
    connectionCache = paramInboundConnectionCache;
  }
  
  public InboundConnectionCache getConnectionCache()
  {
    return connectionCache;
  }
  
  public boolean shouldRegisterAcceptEvent()
  {
    return true;
  }
  
  public void accept()
  {
    try
    {
      SocketChannel localSocketChannel = null;
      localObject = null;
      if (serverSocketChannel == null)
      {
        localObject = serverSocket.accept();
      }
      else
      {
        localSocketChannel = serverSocketChannel.accept();
        localObject = localSocketChannel.socket();
      }
      orb.getORBData().getSocketFactory().setAcceptedSocketOptions(this, serverSocket, (Socket)localObject);
      if (orb.transportDebugFlag) {
        dprint(".accept: " + (serverSocketChannel == null ? serverSocket.toString() : serverSocketChannel.toString()));
      }
      SocketOrChannelConnectionImpl localSocketOrChannelConnectionImpl = new SocketOrChannelConnectionImpl(orb, this, (Socket)localObject);
      if (orb.transportDebugFlag) {
        dprint(".accept: new: " + localSocketOrChannelConnectionImpl);
      }
      getConnectionCache().stampTime(localSocketOrChannelConnectionImpl);
      getConnectionCache().put(this, localSocketOrChannelConnectionImpl);
      if (localSocketOrChannelConnectionImpl.shouldRegisterServerReadEvent())
      {
        Selector localSelector = orb.getTransportManager().getSelector(0);
        if (localSelector != null)
        {
          if (orb.transportDebugFlag) {
            dprint(".accept: registerForEvent: " + localSocketOrChannelConnectionImpl);
          }
          localSelector.registerForEvent(localSocketOrChannelConnectionImpl.getEventHandler());
        }
      }
      getConnectionCache().reclaim();
    }
    catch (IOException localIOException)
    {
      if (orb.transportDebugFlag) {
        dprint(".accept:", localIOException);
      }
      Object localObject = orb.getTransportManager().getSelector(0);
      if (localObject != null)
      {
        ((Selector)localObject).unregisterForEvent(this);
        ((Selector)localObject).registerForEvent(this);
      }
    }
  }
  
  public void close()
  {
    try
    {
      if (orb.transportDebugFlag) {
        dprint(".close->:");
      }
      Selector localSelector = orb.getTransportManager().getSelector(0);
      if (localSelector != null) {
        localSelector.unregisterForEvent(this);
      }
      if (serverSocketChannel != null) {
        serverSocketChannel.close();
      }
      if (serverSocket != null) {
        serverSocket.close();
      }
    }
    catch (IOException localIOException)
    {
      if (orb.transportDebugFlag) {
        dprint(".close:", localIOException);
      }
    }
    finally
    {
      if (orb.transportDebugFlag) {
        dprint(".close<-:");
      }
    }
  }
  
  public EventHandler getEventHandler()
  {
    return this;
  }
  
  public String getObjectAdapterId()
  {
    return null;
  }
  
  public String getObjectAdapterManagerId()
  {
    return null;
  }
  
  public void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString)
  {
    Iterator localIterator = paramIORTemplate.iteratorById(0);
    String str = orb.getORBData().getORBServerHost();
    Object localObject1;
    Object localObject2;
    if (localIterator.hasNext())
    {
      localObject1 = IIOPFactories.makeIIOPAddress(orb, str, port);
      AlternateIIOPAddressComponent localAlternateIIOPAddressComponent = IIOPFactories.makeAlternateIIOPAddressComponent((IIOPAddress)localObject1);
      while (localIterator.hasNext())
      {
        localObject2 = (TaggedProfileTemplate)localIterator.next();
        ((TaggedProfileTemplate)localObject2).add(localAlternateIIOPAddressComponent);
      }
    }
    else
    {
      localObject1 = orb.getORBData().getGIOPVersion();
      int i;
      if (paramPolicies.forceZeroPort()) {
        i = 0;
      } else if (paramPolicies.isTransient()) {
        i = port;
      } else {
        i = orb.getLegacyServerSocketManager().legacyGetPersistentServerPort("IIOP_CLEAR_TEXT");
      }
      localObject2 = IIOPFactories.makeIIOPAddress(orb, str, i);
      IIOPProfileTemplate localIIOPProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(orb, (GIOPVersion)localObject1, (IIOPAddress)localObject2);
      if (((GIOPVersion)localObject1).supportsIORIIOPProfileComponents())
      {
        localIIOPProfileTemplate.add(IIOPFactories.makeCodeSetsComponent(orb));
        localIIOPProfileTemplate.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
        RequestPartitioningPolicy localRequestPartitioningPolicy = (RequestPartitioningPolicy)paramPolicies.get_effective_policy(1398079491);
        if (localRequestPartitioningPolicy != null) {
          localIIOPProfileTemplate.add(IIOPFactories.makeRequestPartitioningComponent(localRequestPartitioningPolicy.getValue()));
        }
        if ((paramString != null) && (paramString != "")) {
          localIIOPProfileTemplate.add(IIOPFactories.makeJavaCodebaseComponent(paramString));
        }
        if (orb.getORBData().isJavaSerializationEnabled()) {
          localIIOPProfileTemplate.add(IIOPFactories.makeJavaSerializationComponent());
        }
      }
      paramIORTemplate.add(localIIOPProfileTemplate);
    }
  }
  
  public String getMonitoringName()
  {
    return "AcceptedConnections";
  }
  
  public SelectableChannel getChannel()
  {
    return serverSocketChannel;
  }
  
  public int getInterestOps()
  {
    return 16;
  }
  
  public Acceptor getAcceptor()
  {
    return this;
  }
  
  public Connection getConnection()
  {
    throw new RuntimeException("Should not happen.");
  }
  
  public void doWork()
  {
    try
    {
      if (orb.transportDebugFlag) {
        dprint(".doWork->: " + this);
      }
      if (selectionKey.isAcceptable()) {
        accept();
      } else if (orb.transportDebugFlag) {
        dprint(".doWork: ! selectionKey.isAcceptable: " + this);
      }
    }
    catch (SecurityException localSecurityException)
    {
      Selector localSelector1;
      if (orb.transportDebugFlag) {
        dprint(".doWork: ignoring SecurityException: " + localSecurityException + " " + this);
      }
      String str = ORBUtility.getClassSecurityInfo(getClass());
      wrapper.securityExceptionInAccept(localSecurityException, str);
    }
    catch (Exception localException)
    {
      Selector localSelector2;
      if (orb.transportDebugFlag) {
        dprint(".doWork: ignoring Exception: " + localException + " " + this);
      }
      wrapper.exceptionInAccept(localException);
    }
    catch (Throwable localThrowable)
    {
      Selector localSelector3;
      if (orb.transportDebugFlag) {
        dprint(".doWork: ignoring Throwable: " + localThrowable + " " + this);
      }
    }
    finally
    {
      Selector localSelector4;
      Selector localSelector5 = orb.getTransportManager().getSelector(0);
      if (localSelector5 != null) {
        localSelector5.registerInterestOps(this);
      }
      if (orb.transportDebugFlag) {
        dprint(".doWork<-:" + this);
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
  
  public MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection)
  {
    SocketOrChannelContactInfoImpl localSocketOrChannelContactInfoImpl = new SocketOrChannelContactInfoImpl();
    return localSocketOrChannelContactInfoImpl.createMessageMediator(paramBroker, paramConnection);
  }
  
  public MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator)
  {
    SocketOrChannelContactInfoImpl localSocketOrChannelContactInfoImpl = new SocketOrChannelContactInfoImpl();
    return localSocketOrChannelContactInfoImpl.finishCreatingMessageMediator(paramBroker, paramConnection, paramMessageMediator);
  }
  
  public InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    return new CDRInputObject((ORB)paramBroker, (CorbaConnection)paramMessageMediator.getConnection(), localCorbaMessageMediator.getDispatchBuffer(), localCorbaMessageMediator.getDispatchHeader());
  }
  
  public OutputObject createOutputObject(Broker paramBroker, MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    return OutputStreamFactory.newCDROutputObject((ORB)paramBroker, localCorbaMessageMediator, localCorbaMessageMediator.getReplyHeader(), localCorbaMessageMediator.getStreamFormatVersion());
  }
  
  public ServerSocket getServerSocket()
  {
    return serverSocket;
  }
  
  public String toString()
  {
    String str;
    if (serverSocketChannel == null)
    {
      if (serverSocket == null) {
        str = "(not initialized)";
      } else {
        str = serverSocket.toString();
      }
    }
    else {
      str = serverSocketChannel.toString();
    }
    return toStringName() + "[" + str + " " + type + " " + shouldUseSelectThreadToWait() + " " + shouldUseWorkerThreadForEvent() + "]";
  }
  
  protected String toStringName()
  {
    return "SocketOrChannelAcceptorImpl";
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint(toStringName(), paramString);
  }
  
  protected void dprint(String paramString, Throwable paramThrowable)
  {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
  
  public String getType()
  {
    return type;
  }
  
  public String getHostName()
  {
    return hostname;
  }
  
  public String getHost()
  {
    return hostname;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public int getLocatorPort()
  {
    return locatorPort;
  }
  
  public void setLocatorPort(int paramInt)
  {
    locatorPort = paramInt;
  }
  
  public String getName()
  {
    String str = name.equals("NO_NAME") ? toString() : name;
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\SocketOrChannelAcceptorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */