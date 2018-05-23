package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.corba.TypeCodeFactory;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.impl.presentation.rmi.PresentationManagerImpl;
import com.sun.corba.se.impl.transport.ByteBufferPoolImpl;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactoryFactory;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import sun.awt.AppContext;
import sun.corba.JavaCorbaAccess;
import sun.corba.SharedSecrets;

public abstract class ORB
  extends com.sun.corba.se.org.omg.CORBA.ORB
  implements Broker, TypeCodeFactory
{
  public static boolean ORBInitDebug = false;
  public boolean transportDebugFlag = false;
  public boolean subcontractDebugFlag = false;
  public boolean poaDebugFlag = false;
  public boolean poaConcurrencyDebugFlag = false;
  public boolean poaFSMDebugFlag = false;
  public boolean orbdDebugFlag = false;
  public boolean namingDebugFlag = false;
  public boolean serviceContextDebugFlag = false;
  public boolean transientObjectManagerDebugFlag = false;
  public boolean giopVersionDebugFlag = false;
  public boolean shutdownDebugFlag = false;
  public boolean giopDebugFlag = false;
  public boolean invocationTimingDebugFlag = false;
  protected static ORBUtilSystemException staticWrapper;
  protected ORBUtilSystemException wrapper = ORBUtilSystemException.get(this, "rpc.presentation");
  protected OMGSystemException omgWrapper = OMGSystemException.get(this, "rpc.presentation");
  private Map typeCodeMap = new HashMap();
  private TypeCodeImpl[] primitiveTypeCodeConstants = { new TypeCodeImpl(this, 0), new TypeCodeImpl(this, 1), new TypeCodeImpl(this, 2), new TypeCodeImpl(this, 3), new TypeCodeImpl(this, 4), new TypeCodeImpl(this, 5), new TypeCodeImpl(this, 6), new TypeCodeImpl(this, 7), new TypeCodeImpl(this, 8), new TypeCodeImpl(this, 9), new TypeCodeImpl(this, 10), new TypeCodeImpl(this, 11), new TypeCodeImpl(this, 12), new TypeCodeImpl(this, 13), new TypeCodeImpl(this, 14), null, null, null, new TypeCodeImpl(this, 18), null, null, null, null, new TypeCodeImpl(this, 23), new TypeCodeImpl(this, 24), new TypeCodeImpl(this, 25), new TypeCodeImpl(this, 26), new TypeCodeImpl(this, 27), new TypeCodeImpl(this, 28), new TypeCodeImpl(this, 29), new TypeCodeImpl(this, 30), new TypeCodeImpl(this, 31), new TypeCodeImpl(this, 32) };
  ByteBufferPool byteBufferPool;
  private Map wrapperMap = new ConcurrentHashMap();
  private static final Object pmLock = new Object();
  private static Map staticWrapperMap = new ConcurrentHashMap();
  protected MonitoringManager monitoringManager = MonitoringFactories.getMonitoringManagerFactory().createMonitoringManager("orb", "ORB Management and Monitoring Root");
  
  public abstract boolean isLocalHost(String paramString);
  
  public abstract boolean isLocalServerId(int paramInt1, int paramInt2);
  
  public abstract OAInvocationInfo peekInvocationInfo();
  
  public abstract void pushInvocationInfo(OAInvocationInfo paramOAInvocationInfo);
  
  public abstract OAInvocationInfo popInvocationInfo();
  
  public abstract CorbaTransportManager getCorbaTransportManager();
  
  public abstract LegacyServerSocketManager getLegacyServerSocketManager();
  
  private static PresentationManager setupPresentationManager()
  {
    staticWrapper = ORBUtilSystemException.get("rpc.presentation");
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Boolean.valueOf(Boolean.getBoolean("com.sun.CORBA.ORBUseDynamicStub"));
      }
    })).booleanValue();
    PresentationManager.StubFactoryFactory localStubFactoryFactory = (PresentationManager.StubFactoryFactory)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        PresentationManager.StubFactoryFactory localStubFactoryFactory = PresentationDefaults.getProxyStubFactoryFactory();
        String str = System.getProperty("com.sun.CORBA.ORBDynamicStubFactoryFactoryClass", "com.sun.corba.se.impl.presentation.rmi.bcel.StubFactoryFactoryBCELImpl");
        try
        {
          Class localClass = SharedSecrets.getJavaCorbaAccess().loadClass(str);
          localStubFactoryFactory = (PresentationManager.StubFactoryFactory)localClass.newInstance();
        }
        catch (Exception localException)
        {
          ORB.staticWrapper.errorInSettingDynamicStubFactoryFactory(localException, str);
        }
        return localStubFactoryFactory;
      }
    });
    PresentationManagerImpl localPresentationManagerImpl = new PresentationManagerImpl(bool);
    localPresentationManagerImpl.setStubFactoryFactory(false, PresentationDefaults.getStaticStubFactoryFactory());
    localPresentationManagerImpl.setStubFactoryFactory(true, localStubFactoryFactory);
    return localPresentationManagerImpl;
  }
  
  public void destroy()
  {
    wrapper = null;
    omgWrapper = null;
    typeCodeMap = null;
    primitiveTypeCodeConstants = null;
    byteBufferPool = null;
  }
  
  public static PresentationManager getPresentationManager()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localSecurityManager != null) && (AppContext.getAppContexts().size() > 0))
    {
      AppContext localAppContext = AppContext.getAppContext();
      if (localAppContext != null) {
        synchronized (pmLock)
        {
          PresentationManager localPresentationManager = (PresentationManager)localAppContext.get(PresentationManager.class);
          if (localPresentationManager == null)
          {
            localPresentationManager = setupPresentationManager();
            localAppContext.put(PresentationManager.class, localPresentationManager);
          }
          return localPresentationManager;
        }
      }
    }
    return Holder.defaultPresentationManager;
  }
  
  public static PresentationManager.StubFactoryFactory getStubFactoryFactory()
  {
    PresentationManager localPresentationManager = getPresentationManager();
    boolean bool = localPresentationManager.useDynamicStubs();
    return localPresentationManager.getStubFactoryFactory(bool);
  }
  
  protected ORB() {}
  
  public TypeCodeImpl get_primitive_tc(int paramInt)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    try
    {
      return primitiveTypeCodeConstants[paramInt];
    }
    catch (Throwable localThrowable)
    {
      throw wrapper.invalidTypecodeKind(localThrowable, new Integer(paramInt));
    }
  }
  
  public synchronized void setTypeCode(String paramString, TypeCodeImpl paramTypeCodeImpl)
  {
    checkShutdownState();
    typeCodeMap.put(paramString, paramTypeCodeImpl);
  }
  
  public synchronized TypeCodeImpl getTypeCode(String paramString)
  {
    checkShutdownState();
    return (TypeCodeImpl)typeCodeMap.get(paramString);
  }
  
  public MonitoringManager getMonitoringManager()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    return monitoringManager;
  }
  
  public abstract void set_parameters(Properties paramProperties);
  
  public abstract ORBVersion getORBVersion();
  
  public abstract void setORBVersion(ORBVersion paramORBVersion);
  
  public abstract IOR getFVDCodeBaseIOR();
  
  public abstract void handleBadServerId(ObjectKey paramObjectKey);
  
  public abstract void setBadServerIdHandler(BadServerIdHandler paramBadServerIdHandler);
  
  public abstract void initBadServerIdHandler();
  
  public abstract void notifyORB();
  
  public abstract PIHandler getPIHandler();
  
  public abstract void checkShutdownState();
  
  public abstract boolean isDuringDispatch();
  
  public abstract void startingDispatch();
  
  public abstract void finishedDispatch();
  
  public abstract int getTransientServerId();
  
  public abstract ServiceContextRegistry getServiceContextRegistry();
  
  public abstract RequestDispatcherRegistry getRequestDispatcherRegistry();
  
  public abstract ORBData getORBData();
  
  public abstract void setClientDelegateFactory(ClientDelegateFactory paramClientDelegateFactory);
  
  public abstract ClientDelegateFactory getClientDelegateFactory();
  
  public abstract void setCorbaContactInfoListFactory(CorbaContactInfoListFactory paramCorbaContactInfoListFactory);
  
  public abstract CorbaContactInfoListFactory getCorbaContactInfoListFactory();
  
  public abstract void setResolver(Resolver paramResolver);
  
  public abstract Resolver getResolver();
  
  public abstract void setLocalResolver(LocalResolver paramLocalResolver);
  
  public abstract LocalResolver getLocalResolver();
  
  public abstract void setURLOperation(Operation paramOperation);
  
  public abstract Operation getURLOperation();
  
  public abstract void setINSDelegate(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher);
  
  public abstract TaggedComponentFactoryFinder getTaggedComponentFactoryFinder();
  
  public abstract IdentifiableFactoryFinder getTaggedProfileFactoryFinder();
  
  public abstract IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder();
  
  public abstract ObjectKeyFactory getObjectKeyFactory();
  
  public abstract void setObjectKeyFactory(ObjectKeyFactory paramObjectKeyFactory);
  
  public Logger getLogger(String paramString)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    ??? = getORBData();
    String str;
    if (??? == null)
    {
      str = "_INITIALIZING_";
    }
    else
    {
      str = ((ORBData)???).getORBId();
      if (str.equals("")) {
        str = "_DEFAULT_";
      }
    }
    return getCORBALogger(str, paramString);
  }
  
  public static Logger staticGetLogger(String paramString)
  {
    return getCORBALogger("_CORBA_", paramString);
  }
  
  private static Logger getCORBALogger(String paramString1, String paramString2)
  {
    String str = "javax.enterprise.resource.corba." + paramString1 + "." + paramString2;
    return Logger.getLogger(str, "com.sun.corba.se.impl.logging.LogStrings");
  }
  
  public LogWrapperBase getLogWrapper(String paramString1, String paramString2, LogWrapperFactory paramLogWrapperFactory)
  {
    StringPair localStringPair = new StringPair(paramString1, paramString2);
    LogWrapperBase localLogWrapperBase = (LogWrapperBase)wrapperMap.get(localStringPair);
    if (localLogWrapperBase == null)
    {
      localLogWrapperBase = paramLogWrapperFactory.create(getLogger(paramString1));
      wrapperMap.put(localStringPair, localLogWrapperBase);
    }
    return localLogWrapperBase;
  }
  
  public static LogWrapperBase staticGetLogWrapper(String paramString1, String paramString2, LogWrapperFactory paramLogWrapperFactory)
  {
    StringPair localStringPair = new StringPair(paramString1, paramString2);
    LogWrapperBase localLogWrapperBase = (LogWrapperBase)staticWrapperMap.get(localStringPair);
    if (localLogWrapperBase == null)
    {
      localLogWrapperBase = paramLogWrapperFactory.create(staticGetLogger(paramString1));
      staticWrapperMap.put(localStringPair, localLogWrapperBase);
    }
    return localLogWrapperBase;
  }
  
  public ByteBufferPool getByteBufferPool()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    if (byteBufferPool == null) {
      byteBufferPool = new ByteBufferPoolImpl(this);
    }
    return byteBufferPool;
  }
  
  public abstract void setThreadPoolManager(ThreadPoolManager paramThreadPoolManager);
  
  public abstract ThreadPoolManager getThreadPoolManager();
  
  public abstract CopierManager getCopierManager();
  
  static class Holder
  {
    static final PresentationManager defaultPresentationManager = ;
    
    Holder() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orb\ORB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */