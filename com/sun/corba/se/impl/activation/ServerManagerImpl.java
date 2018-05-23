package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.NoSuchEndPoint;
import com.sun.corba.se.spi.activation.ORBAlreadyRegistered;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.Server;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerAlreadyInstalled;
import com.sun.corba.se.spi.activation.ServerAlreadyUninstalled;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotActive;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation._ServerManagerImplBase;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class ServerManagerImpl
  extends _ServerManagerImplBase
  implements BadServerIdHandler
{
  HashMap serverTable;
  Repository repository;
  CorbaTransportManager transportManager;
  int initialPort;
  ORB orb;
  ActivationSystemException wrapper;
  String dbDirName;
  boolean debug = false;
  private int serverStartupDelay;
  
  ServerManagerImpl(ORB paramORB, CorbaTransportManager paramCorbaTransportManager, Repository paramRepository, String paramString, boolean paramBoolean)
  {
    orb = paramORB;
    wrapper = ActivationSystemException.get(paramORB, "orbd.activator");
    transportManager = paramCorbaTransportManager;
    repository = paramRepository;
    dbDirName = paramString;
    debug = paramBoolean;
    LegacyServerSocketEndPointInfo localLegacyServerSocketEndPointInfo = paramORB.getLegacyServerSocketManager().legacyGetEndpoint("BOOT_NAMING");
    initialPort = ((SocketOrChannelAcceptor)localLegacyServerSocketEndPointInfo).getServerSocket().getLocalPort();
    serverTable = new HashMap(256);
    serverStartupDelay = 1000;
    String str = System.getProperty("com.sun.CORBA.activation.ServerStartupDelay");
    if (str != null) {
      try
      {
        serverStartupDelay = Integer.parseInt(str);
      }
      catch (Exception localException) {}
    }
    Class localClass = paramORB.getORBData().getBadServerIdHandler();
    if (localClass == null) {
      paramORB.setBadServerIdHandler(this);
    } else {
      paramORB.initBadServerIdHandler();
    }
    paramORB.connect(this);
    ProcessMonitorThread.start(serverTable);
  }
  
  public void activate(int paramInt)
    throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown
  {
    Integer localInteger = new Integer(paramInt);
    ServerTableEntry localServerTableEntry;
    synchronized (serverTable)
    {
      localServerTableEntry = (ServerTableEntry)serverTable.get(localInteger);
    }
    if ((localServerTableEntry != null) && (localServerTableEntry.isActive()))
    {
      if (debug) {
        System.out.println("ServerManagerImpl: activate for server Id " + paramInt + " failed because server is already active. entry = " + localServerTableEntry);
      }
      throw new ServerAlreadyActive(paramInt);
    }
    try
    {
      localServerTableEntry = getEntry(paramInt);
      if (debug) {
        System.out.println("ServerManagerImpl: locateServer called with  serverId=" + paramInt + " endpointType=" + "IIOP_CLEAR_TEXT" + " block=false");
      }
      ServerLocation localServerLocation = locateServer(localServerTableEntry, "IIOP_CLEAR_TEXT", false);
      if (debug) {
        System.out.println("ServerManagerImpl: activate for server Id " + paramInt + " found location " + hostname + " and activated it");
      }
    }
    catch (NoSuchEndPoint localNoSuchEndPoint)
    {
      if (debug) {
        System.out.println("ServerManagerImpl: activate for server Id  threw NoSuchEndpoint exception, which was ignored");
      }
    }
  }
  
  public void active(int paramInt, Server paramServer)
    throws ServerNotRegistered
  {
    Integer localInteger = new Integer(paramInt);
    synchronized (serverTable)
    {
      ServerTableEntry localServerTableEntry = (ServerTableEntry)serverTable.get(localInteger);
      if (localServerTableEntry == null)
      {
        if (debug) {
          System.out.println("ServerManagerImpl: active for server Id " + paramInt + " called, but no such server is registered.");
        }
        throw wrapper.serverNotExpectedToRegister();
      }
      if (debug) {
        System.out.println("ServerManagerImpl: active for server Id " + paramInt + " called.  This server is now active.");
      }
      localServerTableEntry.register(paramServer);
    }
  }
  
  public void registerEndpoints(int paramInt, String paramString, EndPointInfo[] paramArrayOfEndPointInfo)
    throws NoSuchEndPoint, ServerNotRegistered, ORBAlreadyRegistered
  {
    Integer localInteger = new Integer(paramInt);
    synchronized (serverTable)
    {
      ServerTableEntry localServerTableEntry = (ServerTableEntry)serverTable.get(localInteger);
      if (localServerTableEntry == null)
      {
        if (debug) {
          System.out.println("ServerManagerImpl: registerEndpoint for server Id " + paramInt + " called, but no such server is registered.");
        }
        throw wrapper.serverNotExpectedToRegister();
      }
      if (debug) {
        System.out.println("ServerManagerImpl: registerEndpoints for server Id " + paramInt + " called.  This server is now active.");
      }
      localServerTableEntry.registerPorts(paramString, paramArrayOfEndPointInfo);
    }
  }
  
  public int[] getActiveServers()
  {
    int[] arrayOfInt = null;
    synchronized (serverTable)
    {
      ArrayList localArrayList = new ArrayList(0);
      Iterator localIterator = serverTable.keySet().iterator();
      ServerTableEntry localServerTableEntry;
      try
      {
        while (localIterator.hasNext())
        {
          Integer localInteger = (Integer)localIterator.next();
          localServerTableEntry = (ServerTableEntry)serverTable.get(localInteger);
          if ((localServerTableEntry.isValid()) && (localServerTableEntry.isActive())) {
            localArrayList.add(localServerTableEntry);
          }
        }
      }
      catch (NoSuchElementException localNoSuchElementException) {}
      arrayOfInt = new int[localArrayList.size()];
      for (int j = 0; j < localArrayList.size(); j++)
      {
        localServerTableEntry = (ServerTableEntry)localArrayList.get(j);
        arrayOfInt[j] = localServerTableEntry.getServerId();
      }
    }
    if (debug)
    {
      ??? = new StringBuffer();
      for (int i = 0; i < arrayOfInt.length; i++)
      {
        ((StringBuffer)???).append(' ');
        ((StringBuffer)???).append(arrayOfInt[i]);
      }
      System.out.println("ServerManagerImpl: getActiveServers returns" + ((StringBuffer)???).toString());
    }
    return arrayOfInt;
  }
  
  public void shutdown(int paramInt)
    throws ServerNotActive
  {
    Integer localInteger = new Integer(paramInt);
    synchronized (serverTable)
    {
      ServerTableEntry localServerTableEntry = (ServerTableEntry)serverTable.remove(localInteger);
      if (localServerTableEntry == null)
      {
        if (debug) {
          System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " throws ServerNotActive.");
        }
        throw new ServerNotActive(paramInt);
      }
      try
      {
        localServerTableEntry.destroy();
        if (debug) {
          System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " completed.");
        }
      }
      catch (Exception localException)
      {
        if (debug) {
          System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " threw exception " + localException);
        }
      }
    }
  }
  
  private ServerTableEntry getEntry(int paramInt)
    throws ServerNotRegistered
  {
    Integer localInteger = new Integer(paramInt);
    ServerTableEntry localServerTableEntry = null;
    synchronized (serverTable)
    {
      localServerTableEntry = (ServerTableEntry)serverTable.get(localInteger);
      if (debug) {
        if (localServerTableEntry == null) {
          System.out.println("ServerManagerImpl: getEntry: no active server found.");
        } else {
          System.out.println("ServerManagerImpl: getEntry:  active server found " + localServerTableEntry + ".");
        }
      }
      if ((localServerTableEntry != null) && (!localServerTableEntry.isValid()))
      {
        serverTable.remove(localInteger);
        localServerTableEntry = null;
      }
      if (localServerTableEntry == null)
      {
        ServerDef localServerDef = repository.getServer(paramInt);
        localServerTableEntry = new ServerTableEntry(wrapper, paramInt, localServerDef, initialPort, dbDirName, false, debug);
        serverTable.put(localInteger, localServerTableEntry);
        localServerTableEntry.activate();
      }
    }
    return localServerTableEntry;
  }
  
  private ServerLocation locateServer(ServerTableEntry paramServerTableEntry, String paramString, boolean paramBoolean)
    throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown
  {
    ServerLocation localServerLocation = new ServerLocation();
    if (paramBoolean)
    {
      ORBPortInfo[] arrayOfORBPortInfo;
      try
      {
        arrayOfORBPortInfo = paramServerTableEntry.lookup(paramString);
      }
      catch (Exception localException)
      {
        if (debug) {
          System.out.println("ServerManagerImpl: locateServer: server held down");
        }
        throw new ServerHeldDown(paramServerTableEntry.getServerId());
      }
      String str = orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
      hostname = str;
      int i;
      if (arrayOfORBPortInfo != null) {
        i = arrayOfORBPortInfo.length;
      } else {
        i = 0;
      }
      ports = new ORBPortInfo[i];
      for (int j = 0; j < i; j++)
      {
        ports[j] = new ORBPortInfo(orbId, port);
        if (debug) {
          System.out.println("ServerManagerImpl: locateServer: server located at location " + hostname + " ORBid  " + orbId + " Port " + port);
        }
      }
    }
    return localServerLocation;
  }
  
  private ServerLocationPerORB locateServerForORB(ServerTableEntry paramServerTableEntry, String paramString, boolean paramBoolean)
    throws InvalidORBid, ServerNotRegistered, ServerHeldDown
  {
    ServerLocationPerORB localServerLocationPerORB = new ServerLocationPerORB();
    if (paramBoolean)
    {
      EndPointInfo[] arrayOfEndPointInfo;
      try
      {
        arrayOfEndPointInfo = paramServerTableEntry.lookupForORB(paramString);
      }
      catch (InvalidORBid localInvalidORBid)
      {
        throw localInvalidORBid;
      }
      catch (Exception localException)
      {
        if (debug) {
          System.out.println("ServerManagerImpl: locateServerForORB: server held down");
        }
        throw new ServerHeldDown(paramServerTableEntry.getServerId());
      }
      String str = orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
      hostname = str;
      int i;
      if (arrayOfEndPointInfo != null) {
        i = arrayOfEndPointInfo.length;
      } else {
        i = 0;
      }
      ports = new EndPointInfo[i];
      for (int j = 0; j < i; j++)
      {
        ports[j] = new EndPointInfo(endpointType, port);
        if (debug) {
          System.out.println("ServerManagerImpl: locateServer: server located at location " + hostname + " endpointType  " + endpointType + " Port " + port);
        }
      }
    }
    return localServerLocationPerORB;
  }
  
  public String[] getORBNames(int paramInt)
    throws ServerNotRegistered
  {
    try
    {
      ServerTableEntry localServerTableEntry = getEntry(paramInt);
      return localServerTableEntry.getORBList();
    }
    catch (Exception localException)
    {
      throw new ServerNotRegistered(paramInt);
    }
  }
  
  private ServerTableEntry getRunningEntry(int paramInt)
    throws ServerNotRegistered
  {
    ServerTableEntry localServerTableEntry = getEntry(paramInt);
    try
    {
      ORBPortInfo[] arrayOfORBPortInfo = localServerTableEntry.lookup("IIOP_CLEAR_TEXT");
    }
    catch (Exception localException)
    {
      return null;
    }
    return localServerTableEntry;
  }
  
  public void install(int paramInt)
    throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled
  {
    ServerTableEntry localServerTableEntry = getRunningEntry(paramInt);
    if (localServerTableEntry != null)
    {
      repository.install(paramInt);
      localServerTableEntry.install();
    }
  }
  
  public void uninstall(int paramInt)
    throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled
  {
    ServerTableEntry localServerTableEntry = (ServerTableEntry)serverTable.get(new Integer(paramInt));
    if (localServerTableEntry != null)
    {
      localServerTableEntry = (ServerTableEntry)serverTable.remove(new Integer(paramInt));
      if (localServerTableEntry == null)
      {
        if (debug) {
          System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " throws ServerNotActive.");
        }
        throw new ServerHeldDown(paramInt);
      }
      localServerTableEntry.uninstall();
    }
  }
  
  public ServerLocation locateServer(int paramInt, String paramString)
    throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown
  {
    ServerTableEntry localServerTableEntry = getEntry(paramInt);
    if (debug) {
      System.out.println("ServerManagerImpl: locateServer called with  serverId=" + paramInt + " endpointType=" + paramString + " block=true");
    }
    return locateServer(localServerTableEntry, paramString, true);
  }
  
  public ServerLocationPerORB locateServerForORB(int paramInt, String paramString)
    throws InvalidORBid, ServerNotRegistered, ServerHeldDown
  {
    ServerTableEntry localServerTableEntry = getEntry(paramInt);
    if (debug) {
      System.out.println("ServerManagerImpl: locateServerForORB called with  serverId=" + paramInt + " orbId=" + paramString + " block=true");
    }
    return locateServerForORB(localServerTableEntry, paramString, true);
  }
  
  public void handle(ObjectKey paramObjectKey)
  {
    IOR localIOR = null;
    ObjectKeyTemplate localObjectKeyTemplate = paramObjectKey.getTemplate();
    int i = localObjectKeyTemplate.getServerId();
    String str = localObjectKeyTemplate.getORBId();
    try
    {
      ServerTableEntry localServerTableEntry = getEntry(i);
      ServerLocationPerORB localServerLocationPerORB = locateServerForORB(localServerTableEntry, str, true);
      if (debug) {
        System.out.println("ServerManagerImpl: handle called for server id" + i + "  orbid  " + str);
      }
      int j = 0;
      EndPointInfo[] arrayOfEndPointInfo = ports;
      for (int k = 0; k < arrayOfEndPointInfo.length; k++) {
        if (endpointType.equals("IIOP_CLEAR_TEXT"))
        {
          j = port;
          break;
        }
      }
      IIOPAddress localIIOPAddress = IIOPFactories.makeIIOPAddress(orb, hostname, j);
      IIOPProfileTemplate localIIOPProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(orb, GIOPVersion.V1_2, localIIOPAddress);
      if (GIOPVersion.V1_2.supportsIORIIOPProfileComponents())
      {
        localIIOPProfileTemplate.add(IIOPFactories.makeCodeSetsComponent(orb));
        localIIOPProfileTemplate.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
      }
      IORTemplate localIORTemplate = IORFactories.makeIORTemplate(localObjectKeyTemplate);
      localIORTemplate.add(localIIOPProfileTemplate);
      localIOR = localIORTemplate.makeIOR(orb, "IDL:org/omg/CORBA/Object:1.0", paramObjectKey.getId());
    }
    catch (Exception localException1)
    {
      throw wrapper.errorInBadServerIdHandler(localException1);
    }
    if (debug) {
      System.out.println("ServerManagerImpl: handle throws ForwardException");
    }
    try
    {
      Thread.sleep(serverStartupDelay);
    }
    catch (Exception localException2)
    {
      System.out.println("Exception = " + localException2);
      localException2.printStackTrace();
    }
    throw new ForwardException(orb, localIOR);
  }
  
  public int getEndpoint(String paramString)
    throws NoSuchEndPoint
  {
    return orb.getLegacyServerSocketManager().legacyGetTransientServerPort(paramString);
  }
  
  public int getServerPortForType(ServerLocationPerORB paramServerLocationPerORB, String paramString)
    throws NoSuchEndPoint
  {
    EndPointInfo[] arrayOfEndPointInfo = ports;
    for (int i = 0; i < arrayOfEndPointInfo.length; i++) {
      if (endpointType.equals(paramString)) {
        return port;
      }
    }
    throw new NoSuchEndPoint();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ServerManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */