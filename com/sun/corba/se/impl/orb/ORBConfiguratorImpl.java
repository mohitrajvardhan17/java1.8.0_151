package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.dynamicany.DynAnyFactoryImpl;
import com.sun.corba.se.impl.legacy.connection.SocketFactoryAcceptorImpl;
import com.sun.corba.se.impl.legacy.connection.SocketFactoryContactInfoListImpl;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.CopyobjectDefaults;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.oa.OADefault;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBConfigurator;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.ParserImplBase;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.protocol.RequestDispatcherDefault;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.resolver.ResolverDefault;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.spi.transport.TransportDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;

public class ORBConfiguratorImpl
  implements ORBConfigurator
{
  private ORBUtilSystemException wrapper;
  private static final int ORB_STREAM = 0;
  
  public ORBConfiguratorImpl() {}
  
  public void configure(DataCollector paramDataCollector, ORB paramORB)
  {
    ORB localORB = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "orb.lifecycle");
    initObjectCopiers(localORB);
    initIORFinders(localORB);
    localORB.setClientDelegateFactory(TransportDefault.makeClientDelegateFactory(localORB));
    initializeTransport(localORB);
    initializeNaming(localORB);
    initServiceContextRegistry(localORB);
    initRequestDispatcherRegistry(localORB);
    registerInitialReferences(localORB);
    persistentServerInitialization(localORB);
    runUserConfigurators(paramDataCollector, localORB);
  }
  
  private void runUserConfigurators(DataCollector paramDataCollector, ORB paramORB)
  {
    ConfigParser localConfigParser = new ConfigParser();
    localConfigParser.init(paramDataCollector);
    if (userConfigurators != null) {
      for (int i = 0; i < userConfigurators.length; i++)
      {
        Class localClass = userConfigurators[i];
        try
        {
          ORBConfigurator localORBConfigurator = (ORBConfigurator)localClass.newInstance();
          localORBConfigurator.configure(paramDataCollector, paramORB);
        }
        catch (Exception localException) {}
      }
    }
  }
  
  private void persistentServerInitialization(ORB paramORB)
  {
    ORBData localORBData = paramORB.getORBData();
    if (localORBData.getServerIsORBActivated()) {
      try
      {
        Locator localLocator = LocatorHelper.narrow(paramORB.resolve_initial_references("ServerLocator"));
        Activator localActivator = ActivatorHelper.narrow(paramORB.resolve_initial_references("ServerActivator"));
        Collection localCollection = paramORB.getCorbaTransportManager().getAcceptors(null, null);
        EndPointInfo[] arrayOfEndPointInfo = new EndPointInfo[localCollection.size()];
        Iterator localIterator = localCollection.iterator();
        int i = 0;
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          if ((localObject instanceof LegacyServerSocketEndPointInfo))
          {
            LegacyServerSocketEndPointInfo localLegacyServerSocketEndPointInfo = (LegacyServerSocketEndPointInfo)localObject;
            int j = localLocator.getEndpoint(localLegacyServerSocketEndPointInfo.getType());
            if (j == -1)
            {
              j = localLocator.getEndpoint("IIOP_CLEAR_TEXT");
              if (j == -1) {
                throw new Exception("ORBD must support IIOP_CLEAR_TEXT");
              }
            }
            localLegacyServerSocketEndPointInfo.setLocatorPort(j);
            arrayOfEndPointInfo[(i++)] = new EndPointInfo(localLegacyServerSocketEndPointInfo.getType(), localLegacyServerSocketEndPointInfo.getPort());
          }
        }
        localActivator.registerEndpoints(localORBData.getPersistentServerId(), localORBData.getORBId(), arrayOfEndPointInfo);
      }
      catch (Exception localException)
      {
        throw wrapper.persistentServerInitError(CompletionStatus.COMPLETED_MAYBE, localException);
      }
    }
  }
  
  private void initializeTransport(final ORB paramORB)
  {
    ORBData localORBData = paramORB.getORBData();
    Object localObject = localORBData.getCorbaContactInfoListFactory();
    Acceptor[] arrayOfAcceptor = localORBData.getAcceptors();
    com.sun.corba.se.spi.legacy.connection.ORBSocketFactory localORBSocketFactory = localORBData.getLegacySocketFactory();
    USLPort[] arrayOfUSLPort1 = localORBData.getUserSpecifiedListenPorts();
    setLegacySocketFactoryORB(paramORB, localORBSocketFactory);
    if ((localORBSocketFactory != null) && (localObject != null)) {
      throw wrapper.socketFactoryAndContactInfoListAtSameTime();
    }
    if ((arrayOfAcceptor.length != 0) && (localORBSocketFactory != null)) {
      throw wrapper.acceptorsAndLegacySocketFactoryAtSameTime();
    }
    localORBData.getSocketFactory().setORB(paramORB);
    if (localORBSocketFactory != null) {
      localObject = new CorbaContactInfoListFactory()
      {
        public void setORB(ORB paramAnonymousORB) {}
        
        public CorbaContactInfoList create(IOR paramAnonymousIOR)
        {
          return new SocketFactoryContactInfoListImpl(paramORB, paramAnonymousIOR);
        }
      };
    } else if (localObject != null) {
      ((CorbaContactInfoListFactory)localObject).setORB(paramORB);
    } else {
      localObject = TransportDefault.makeCorbaContactInfoListFactory(paramORB);
    }
    paramORB.setCorbaContactInfoListFactory((CorbaContactInfoListFactory)localObject);
    int i = -1;
    if (localORBData.getORBServerPort() != 0) {
      i = localORBData.getORBServerPort();
    } else if (localORBData.getPersistentPortInitialized()) {
      i = localORBData.getPersistentServerPort();
    } else if (arrayOfAcceptor.length == 0) {
      i = 0;
    }
    if (i != -1) {
      createAndRegisterAcceptor(paramORB, localORBSocketFactory, i, "DEFAULT_ENDPOINT", "IIOP_CLEAR_TEXT");
    }
    for (int j = 0; j < arrayOfAcceptor.length; j++) {
      paramORB.getCorbaTransportManager().registerAcceptor(arrayOfAcceptor[j]);
    }
    USLPort[] arrayOfUSLPort2 = localORBData.getUserSpecifiedListenPorts();
    if (arrayOfUSLPort2 != null) {
      for (int k = 0; k < arrayOfUSLPort2.length; k++) {
        createAndRegisterAcceptor(paramORB, localORBSocketFactory, arrayOfUSLPort2[k].getPort(), "NO_NAME", arrayOfUSLPort2[k].getType());
      }
    }
  }
  
  private void createAndRegisterAcceptor(ORB paramORB, com.sun.corba.se.spi.legacy.connection.ORBSocketFactory paramORBSocketFactory, int paramInt, String paramString1, String paramString2)
  {
    Object localObject;
    if (paramORBSocketFactory == null) {
      localObject = new SocketOrChannelAcceptorImpl(paramORB, paramInt, paramString1, paramString2);
    } else {
      localObject = new SocketFactoryAcceptorImpl(paramORB, paramInt, paramString1, paramString2);
    }
    paramORB.getTransportManager().registerAcceptor((Acceptor)localObject);
  }
  
  private void setLegacySocketFactoryORB(final ORB paramORB, final com.sun.corba.se.spi.legacy.connection.ORBSocketFactory paramORBSocketFactory)
  {
    if (paramORBSocketFactory == null) {
      return;
    }
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws InstantiationException, IllegalAccessException
        {
          try
          {
            Class[] arrayOfClass = { ORB.class };
            localObject = paramORBSocketFactory.getClass().getMethod("setORB", arrayOfClass);
            Object[] arrayOfObject = { paramORB };
            ((Method)localObject).invoke(paramORBSocketFactory, arrayOfObject);
          }
          catch (NoSuchMethodException localNoSuchMethodException) {}catch (IllegalAccessException localIllegalAccessException)
          {
            localObject = new RuntimeException();
            ((RuntimeException)localObject).initCause(localIllegalAccessException);
            throw ((Throwable)localObject);
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            Object localObject = new RuntimeException();
            ((RuntimeException)localObject).initCause(localInvocationTargetException);
            throw ((Throwable)localObject);
          }
          return null;
        }
      });
    }
    catch (Throwable localThrowable)
    {
      throw wrapper.unableToSetSocketFactoryOrb(localThrowable);
    }
  }
  
  private void initializeNaming(ORB paramORB)
  {
    LocalResolver localLocalResolver = ResolverDefault.makeLocalResolver();
    paramORB.setLocalResolver(localLocalResolver);
    Resolver localResolver1 = ResolverDefault.makeBootstrapResolver(paramORB, paramORB.getORBData().getORBInitialHost(), paramORB.getORBData().getORBInitialPort());
    Operation localOperation = ResolverDefault.makeINSURLOperation(paramORB, localResolver1);
    paramORB.setURLOperation(localOperation);
    Resolver localResolver2 = ResolverDefault.makeORBInitRefResolver(localOperation, paramORB.getORBData().getORBInitialReferences());
    Resolver localResolver3 = ResolverDefault.makeORBDefaultInitRefResolver(localOperation, paramORB.getORBData().getORBDefaultInitialReference());
    Resolver localResolver4 = ResolverDefault.makeCompositeResolver(localLocalResolver, ResolverDefault.makeCompositeResolver(localResolver2, ResolverDefault.makeCompositeResolver(localResolver3, localResolver1)));
    paramORB.setResolver(localResolver4);
  }
  
  private void initServiceContextRegistry(ORB paramORB)
  {
    ServiceContextRegistry localServiceContextRegistry = paramORB.getServiceContextRegistry();
    localServiceContextRegistry.register(UEInfoServiceContext.class);
    localServiceContextRegistry.register(CodeSetServiceContext.class);
    localServiceContextRegistry.register(SendingContextServiceContext.class);
    localServiceContextRegistry.register(ORBVersionServiceContext.class);
    localServiceContextRegistry.register(MaxStreamFormatVersionServiceContext.class);
  }
  
  private void registerInitialReferences(final ORB paramORB)
  {
    Closure local3 = new Closure()
    {
      public Object evaluate()
      {
        return new DynAnyFactoryImpl(paramORB);
      }
    };
    Closure localClosure = ClosureFactory.makeFuture(local3);
    paramORB.getLocalResolver().register("DynAnyFactory", localClosure);
  }
  
  private void initObjectCopiers(ORB paramORB)
  {
    ObjectCopierFactory localObjectCopierFactory = CopyobjectDefaults.makeORBStreamObjectCopierFactory(paramORB);
    CopierManager localCopierManager = paramORB.getCopierManager();
    localCopierManager.setDefaultId(0);
    localCopierManager.registerObjectCopierFactory(localObjectCopierFactory, 0);
  }
  
  private void initIORFinders(ORB paramORB)
  {
    IdentifiableFactoryFinder localIdentifiableFactoryFinder1 = paramORB.getTaggedProfileFactoryFinder();
    localIdentifiableFactoryFinder1.registerFactory(IIOPFactories.makeIIOPProfileFactory());
    IdentifiableFactoryFinder localIdentifiableFactoryFinder2 = paramORB.getTaggedProfileTemplateFactoryFinder();
    localIdentifiableFactoryFinder2.registerFactory(IIOPFactories.makeIIOPProfileTemplateFactory());
    TaggedComponentFactoryFinder localTaggedComponentFactoryFinder = paramORB.getTaggedComponentFactoryFinder();
    localTaggedComponentFactoryFinder.registerFactory(IIOPFactories.makeCodeSetsComponentFactory());
    localTaggedComponentFactoryFinder.registerFactory(IIOPFactories.makeJavaCodebaseComponentFactory());
    localTaggedComponentFactoryFinder.registerFactory(IIOPFactories.makeORBTypeComponentFactory());
    localTaggedComponentFactoryFinder.registerFactory(IIOPFactories.makeMaxStreamFormatVersionComponentFactory());
    localTaggedComponentFactoryFinder.registerFactory(IIOPFactories.makeAlternateIIOPAddressComponentFactory());
    localTaggedComponentFactoryFinder.registerFactory(IIOPFactories.makeRequestPartitioningComponentFactory());
    localTaggedComponentFactoryFinder.registerFactory(IIOPFactories.makeJavaSerializationComponentFactory());
    IORFactories.registerValueFactories(paramORB);
    paramORB.setObjectKeyFactory(IORFactories.makeObjectKeyFactory(paramORB));
  }
  
  private void initRequestDispatcherRegistry(ORB paramORB)
  {
    RequestDispatcherRegistry localRequestDispatcherRegistry = paramORB.getRequestDispatcherRegistry();
    ClientRequestDispatcher localClientRequestDispatcher = RequestDispatcherDefault.makeClientRequestDispatcher();
    localRequestDispatcherRegistry.registerClientRequestDispatcher(localClientRequestDispatcher, 2);
    localRequestDispatcherRegistry.registerClientRequestDispatcher(localClientRequestDispatcher, 32);
    localRequestDispatcherRegistry.registerClientRequestDispatcher(localClientRequestDispatcher, ORBConstants.PERSISTENT_SCID);
    localRequestDispatcherRegistry.registerClientRequestDispatcher(localClientRequestDispatcher, 36);
    localRequestDispatcherRegistry.registerClientRequestDispatcher(localClientRequestDispatcher, ORBConstants.SC_PERSISTENT_SCID);
    localRequestDispatcherRegistry.registerClientRequestDispatcher(localClientRequestDispatcher, 40);
    localRequestDispatcherRegistry.registerClientRequestDispatcher(localClientRequestDispatcher, ORBConstants.IISC_PERSISTENT_SCID);
    localRequestDispatcherRegistry.registerClientRequestDispatcher(localClientRequestDispatcher, 44);
    localRequestDispatcherRegistry.registerClientRequestDispatcher(localClientRequestDispatcher, ORBConstants.MINSC_PERSISTENT_SCID);
    CorbaServerRequestDispatcher localCorbaServerRequestDispatcher1 = RequestDispatcherDefault.makeServerRequestDispatcher(paramORB);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher1, 2);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher1, 32);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher1, ORBConstants.PERSISTENT_SCID);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher1, 36);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher1, ORBConstants.SC_PERSISTENT_SCID);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher1, 40);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher1, ORBConstants.IISC_PERSISTENT_SCID);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher1, 44);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher1, ORBConstants.MINSC_PERSISTENT_SCID);
    paramORB.setINSDelegate(RequestDispatcherDefault.makeINSServerRequestDispatcher(paramORB));
    LocalClientRequestDispatcherFactory localLocalClientRequestDispatcherFactory = RequestDispatcherDefault.makeJIDLLocalClientRequestDispatcherFactory(paramORB);
    localRequestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localLocalClientRequestDispatcherFactory, 2);
    localLocalClientRequestDispatcherFactory = RequestDispatcherDefault.makePOALocalClientRequestDispatcherFactory(paramORB);
    localRequestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localLocalClientRequestDispatcherFactory, 32);
    localRequestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localLocalClientRequestDispatcherFactory, ORBConstants.PERSISTENT_SCID);
    localLocalClientRequestDispatcherFactory = RequestDispatcherDefault.makeFullServantCacheLocalClientRequestDispatcherFactory(paramORB);
    localRequestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localLocalClientRequestDispatcherFactory, 36);
    localRequestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localLocalClientRequestDispatcherFactory, ORBConstants.SC_PERSISTENT_SCID);
    localLocalClientRequestDispatcherFactory = RequestDispatcherDefault.makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory(paramORB);
    localRequestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localLocalClientRequestDispatcherFactory, 40);
    localRequestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localLocalClientRequestDispatcherFactory, ORBConstants.IISC_PERSISTENT_SCID);
    localLocalClientRequestDispatcherFactory = RequestDispatcherDefault.makeMinimalServantCacheLocalClientRequestDispatcherFactory(paramORB);
    localRequestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localLocalClientRequestDispatcherFactory, 44);
    localRequestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localLocalClientRequestDispatcherFactory, ORBConstants.MINSC_PERSISTENT_SCID);
    CorbaServerRequestDispatcher localCorbaServerRequestDispatcher2 = RequestDispatcherDefault.makeBootstrapServerRequestDispatcher(paramORB);
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher2, "INIT");
    localRequestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher2, "TINI");
    ObjectAdapterFactory localObjectAdapterFactory = OADefault.makeTOAFactory(paramORB);
    localRequestDispatcherRegistry.registerObjectAdapterFactory(localObjectAdapterFactory, 2);
    localObjectAdapterFactory = OADefault.makePOAFactory(paramORB);
    localRequestDispatcherRegistry.registerObjectAdapterFactory(localObjectAdapterFactory, 32);
    localRequestDispatcherRegistry.registerObjectAdapterFactory(localObjectAdapterFactory, ORBConstants.PERSISTENT_SCID);
    localRequestDispatcherRegistry.registerObjectAdapterFactory(localObjectAdapterFactory, 36);
    localRequestDispatcherRegistry.registerObjectAdapterFactory(localObjectAdapterFactory, ORBConstants.SC_PERSISTENT_SCID);
    localRequestDispatcherRegistry.registerObjectAdapterFactory(localObjectAdapterFactory, 40);
    localRequestDispatcherRegistry.registerObjectAdapterFactory(localObjectAdapterFactory, ORBConstants.IISC_PERSISTENT_SCID);
    localRequestDispatcherRegistry.registerObjectAdapterFactory(localObjectAdapterFactory, 44);
    localRequestDispatcherRegistry.registerObjectAdapterFactory(localObjectAdapterFactory, ORBConstants.MINSC_PERSISTENT_SCID);
  }
  
  public static class ConfigParser
    extends ParserImplBase
  {
    public Class[] userConfigurators = null;
    
    public ConfigParser() {}
    
    public PropertyParser makeParser()
    {
      PropertyParser localPropertyParser = new PropertyParser();
      Operation localOperation = OperationFactory.compose(OperationFactory.suffixAction(), OperationFactory.classAction());
      localPropertyParser.addPrefix("com.sun.CORBA.ORBUserConfigurators", localOperation, "userConfigurators", Class.class);
      return localPropertyParser;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\ORBConfiguratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */