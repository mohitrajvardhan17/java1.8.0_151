package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.copyobject.CopierManagerImpl;
import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.corba.AsynchInvoke;
import com.sun.corba.se.impl.corba.ContextListImpl;
import com.sun.corba.se.impl.corba.EnvironmentImpl;
import com.sun.corba.se.impl.corba.ExceptionListImpl;
import com.sun.corba.se.impl.corba.NVListImpl;
import com.sun.corba.se.impl.corba.NamedValueImpl;
import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.encoding.CachedCodeBase;
import com.sun.corba.se.impl.interceptors.PIHandlerImpl;
import com.sun.corba.se.impl.interceptors.PINoOpHandlerImpl;
import com.sun.corba.se.impl.ior.TaggedComponentFactoryFinderImpl;
import com.sun.corba.se.impl.ior.TaggedProfileFactoryFinderImpl;
import com.sun.corba.se.impl.ior.TaggedProfileTemplateFactoryFinderImpl;
import com.sun.corba.se.impl.legacy.connection.LegacyServerSocketManagerImpl;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.impl.oa.poa.POAFactory;
import com.sun.corba.se.impl.oa.toa.TOA;
import com.sun.corba.se.impl.oa.toa.TOAFactory;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.StackImpl;
import com.sun.corba.se.impl.orbutil.threadpool.ThreadPoolManagerImpl;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.impl.protocol.RequestDispatcherRegistryImpl;
import com.sun.corba.se.impl.transport.CorbaTransportManagerImpl;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.ORBConfigurator;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.ParserImplBase;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.org.omg.SendingContext.CodeBase;
import java.applet.Applet;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Current;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Request;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.PortableServer.Servant;
import sun.corba.OutputStreamFactory;

public class ORBImpl
  extends com.sun.corba.se.spi.orb.ORB
{
  protected TransportManager transportManager;
  protected LegacyServerSocketManager legacyServerSocketManager;
  private ThreadLocal OAInvocationInfoStack;
  private ThreadLocal clientInvocationInfoStack;
  private static IOR codeBaseIOR;
  private Vector dynamicRequests;
  private SynchVariable svResponseReceived;
  private Object runObj = new Object();
  private Object shutdownObj = new Object();
  private Object waitForCompletionObj = new Object();
  private static final byte STATUS_OPERATING = 1;
  private static final byte STATUS_SHUTTING_DOWN = 2;
  private static final byte STATUS_SHUTDOWN = 3;
  private static final byte STATUS_DESTROYED = 4;
  private byte status = 1;
  private Object invocationObj = new Object();
  private int numInvocations = 0;
  private ThreadLocal isProcessingInvocation = new ThreadLocal()
  {
    protected Object initialValue()
    {
      return Boolean.FALSE;
    }
  };
  private Map typeCodeForClassMap;
  private Hashtable valueFactoryCache = new Hashtable();
  private ThreadLocal orbVersionThreadLocal;
  private RequestDispatcherRegistry requestDispatcherRegistry;
  private CopierManager copierManager;
  private int transientServerId;
  private ServiceContextRegistry serviceContextRegistry;
  private TOAFactory toaFactory;
  private POAFactory poaFactory;
  private PIHandler pihandler;
  private ORBData configData;
  private BadServerIdHandler badServerIdHandler;
  private ClientDelegateFactory clientDelegateFactory;
  private CorbaContactInfoListFactory corbaContactInfoListFactory;
  private Resolver resolver;
  private LocalResolver localResolver;
  private Operation urlOperation;
  private final Object urlOperationLock = new Object();
  private CorbaServerRequestDispatcher insNamingDelegate;
  private final Object resolverLock = new Object();
  private TaggedComponentFactoryFinder taggedComponentFactoryFinder;
  private IdentifiableFactoryFinder taggedProfileFactoryFinder;
  private IdentifiableFactoryFinder taggedProfileTemplateFactoryFinder;
  private ObjectKeyFactory objectKeyFactory;
  private boolean orbOwnsThreadPoolManager = false;
  private ThreadPoolManager threadpoolMgr;
  private Object badServerIdHandlerAccessLock = new Object();
  private static String localHostString = null;
  private Object clientDelegateFactoryAccessorLock = new Object();
  private Object corbaContactInfoListFactoryAccessLock = new Object();
  private Object objectKeyFactoryAccessLock = new Object();
  private Object transportManagerAccessorLock = new Object();
  private Object legacyServerSocketManagerAccessLock = new Object();
  private Object threadPoolManagerAccessLock = new Object();
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint(this, paramString);
  }
  
  public ORBData getORBData()
  {
    return configData;
  }
  
  public PIHandler getPIHandler()
  {
    return pihandler;
  }
  
  public ORBImpl() {}
  
  public ORBVersion getORBVersion()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    return (ORBVersion)orbVersionThreadLocal.get();
  }
  
  public void setORBVersion(ORBVersion paramORBVersion)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    orbVersionThreadLocal.set(paramORBVersion);
  }
  
  private void preInit(String[] paramArrayOfString, Properties paramProperties)
  {
    pihandler = new PINoOpHandlerImpl();
    transientServerId = ((int)System.currentTimeMillis());
    orbVersionThreadLocal = new ThreadLocal()
    {
      protected Object initialValue()
      {
        return ORBVersionFactory.getORBVersion();
      }
    };
    requestDispatcherRegistry = new RequestDispatcherRegistryImpl(this, 2);
    copierManager = new CopierManagerImpl(this);
    taggedComponentFactoryFinder = new TaggedComponentFactoryFinderImpl(this);
    taggedProfileFactoryFinder = new TaggedProfileFactoryFinderImpl(this);
    taggedProfileTemplateFactoryFinder = new TaggedProfileTemplateFactoryFinderImpl(this);
    dynamicRequests = new Vector();
    svResponseReceived = new SynchVariable();
    OAInvocationInfoStack = new ThreadLocal()
    {
      protected Object initialValue()
      {
        return new StackImpl();
      }
    };
    clientInvocationInfoStack = new ThreadLocal()
    {
      protected Object initialValue()
      {
        return new StackImpl();
      }
    };
    serviceContextRegistry = new ServiceContextRegistry(this);
  }
  
  protected void setDebugFlags(String[] paramArrayOfString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str = paramArrayOfString[i];
      try
      {
        Field localField = getClass().getField(str + "DebugFlag");
        int j = localField.getModifiers();
        if ((Modifier.isPublic(j)) && (!Modifier.isStatic(j)) && (localField.getType() == Boolean.TYPE)) {
          localField.setBoolean(this, true);
        }
      }
      catch (Exception localException) {}
    }
  }
  
  private void postInit(String[] paramArrayOfString, DataCollector paramDataCollector)
  {
    configData = new ORBDataParserImpl(this, paramDataCollector);
    setDebugFlags(configData.getORBDebugFlags());
    getTransportManager();
    getLegacyServerSocketManager();
    ConfigParser localConfigParser = new ConfigParser(null);
    localConfigParser.init(paramDataCollector);
    ORBConfigurator localORBConfigurator = null;
    try
    {
      localORBConfigurator = (ORBConfigurator)configurator.newInstance();
    }
    catch (Exception localException1)
    {
      throw wrapper.badOrbConfigurator(localException1, configurator.getName());
    }
    try
    {
      localORBConfigurator.configure(paramDataCollector, this);
    }
    catch (Exception localException2)
    {
      throw wrapper.orbConfiguratorError(localException2);
    }
    pihandler = new PIHandlerImpl(this, paramArrayOfString);
    pihandler.initialize();
    getThreadPoolManager();
    super.getByteBufferPool();
  }
  
  private synchronized POAFactory getPOAFactory()
  {
    if (poaFactory == null) {
      poaFactory = ((POAFactory)requestDispatcherRegistry.getObjectAdapterFactory(32));
    }
    return poaFactory;
  }
  
  private synchronized TOAFactory getTOAFactory()
  {
    if (toaFactory == null) {
      toaFactory = ((TOAFactory)requestDispatcherRegistry.getObjectAdapterFactory(2));
    }
    return toaFactory;
  }
  
  public void set_parameters(Properties paramProperties)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    preInit(null, paramProperties);
    ??? = DataCollectorFactory.create(paramProperties, getLocalHostName());
    postInit(null, (DataCollector)???);
  }
  
  protected void set_parameters(Applet paramApplet, Properties paramProperties)
  {
    preInit(null, paramProperties);
    DataCollector localDataCollector = DataCollectorFactory.create(paramApplet, paramProperties, getLocalHostName());
    postInit(null, localDataCollector);
  }
  
  protected void set_parameters(String[] paramArrayOfString, Properties paramProperties)
  {
    preInit(paramArrayOfString, paramProperties);
    DataCollector localDataCollector = DataCollectorFactory.create(paramArrayOfString, paramProperties, getLocalHostName());
    postInit(paramArrayOfString, localDataCollector);
  }
  
  public synchronized OutputStream create_output_stream()
  {
    checkShutdownState();
    return OutputStreamFactory.newEncapsOutputStream(this);
  }
  
  /**
   * @deprecated
   */
  public synchronized Current get_current()
  {
    checkShutdownState();
    throw wrapper.genericNoImpl();
  }
  
  public synchronized NVList create_list(int paramInt)
  {
    checkShutdownState();
    return new NVListImpl(this, paramInt);
  }
  
  public synchronized NVList create_operation_list(org.omg.CORBA.Object paramObject)
  {
    checkShutdownState();
    throw wrapper.genericNoImpl();
  }
  
  public synchronized NamedValue create_named_value(String paramString, Any paramAny, int paramInt)
  {
    checkShutdownState();
    return new NamedValueImpl(this, paramString, paramAny, paramInt);
  }
  
  public synchronized ExceptionList create_exception_list()
  {
    checkShutdownState();
    return new ExceptionListImpl();
  }
  
  public synchronized ContextList create_context_list()
  {
    checkShutdownState();
    return new ContextListImpl(this);
  }
  
  public synchronized Context get_default_context()
  {
    checkShutdownState();
    throw wrapper.genericNoImpl();
  }
  
  public synchronized Environment create_environment()
  {
    checkShutdownState();
    return new EnvironmentImpl();
  }
  
  public synchronized void send_multiple_requests_oneway(Request[] paramArrayOfRequest)
  {
    checkShutdownState();
    for (int i = 0; i < paramArrayOfRequest.length; i++) {
      paramArrayOfRequest[i].send_oneway();
    }
  }
  
  public synchronized void send_multiple_requests_deferred(Request[] paramArrayOfRequest)
  {
    checkShutdownState();
    for (int i = 0; i < paramArrayOfRequest.length; i++) {
      dynamicRequests.addElement(paramArrayOfRequest[i]);
    }
    for (i = 0; i < paramArrayOfRequest.length; i++)
    {
      AsynchInvoke localAsynchInvoke = new AsynchInvoke(this, (RequestImpl)paramArrayOfRequest[i], true);
      new Thread(localAsynchInvoke).start();
    }
  }
  
  public synchronized boolean poll_next_response()
  {
    checkShutdownState();
    Enumeration localEnumeration = dynamicRequests.elements();
    while (localEnumeration.hasMoreElements() == true)
    {
      Request localRequest = (Request)localEnumeration.nextElement();
      if (localRequest.poll_response() == true) {
        return true;
      }
    }
    return false;
  }
  
  public Request get_next_response()
    throws WrongTransaction
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    for (;;)
    {
      synchronized (dynamicRequests)
      {
        Enumeration localEnumeration = dynamicRequests.elements();
        if (localEnumeration.hasMoreElements())
        {
          Request localRequest = (Request)localEnumeration.nextElement();
          if (localRequest.poll_response())
          {
            localRequest.get_response();
            dynamicRequests.removeElement(localRequest);
            return localRequest;
          }
          continue;
        }
      }
      synchronized (svResponseReceived)
      {
        while (!svResponseReceived.value()) {
          try
          {
            svResponseReceived.wait();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        svResponseReceived.reset();
      }
    }
  }
  
  public void notifyORB()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (svResponseReceived)
    {
      svResponseReceived.set();
      svResponseReceived.notify();
    }
  }
  
  public synchronized String object_to_string(org.omg.CORBA.Object paramObject)
  {
    checkShutdownState();
    if (paramObject == null)
    {
      localIOR = IORFactories.makeIOR(this);
      return localIOR.stringify();
    }
    IOR localIOR = null;
    try
    {
      localIOR = ORBUtility.connectAndGetIOR(this, paramObject);
    }
    catch (BAD_PARAM localBAD_PARAM)
    {
      if (minor == 1398079694) {
        throw omgWrapper.notAnObjectImpl(localBAD_PARAM);
      }
      throw localBAD_PARAM;
    }
    return localIOR.stringify();
  }
  
  public org.omg.CORBA.Object string_to_object(String paramString)
  {
    Operation localOperation;
    synchronized (this)
    {
      checkShutdownState();
      localOperation = urlOperation;
    }
    if (paramString == null) {
      throw wrapper.nullParam();
    }
    synchronized (urlOperationLock)
    {
      org.omg.CORBA.Object localObject = (org.omg.CORBA.Object)localOperation.operate(paramString);
      return localObject;
    }
  }
  
  public synchronized IOR getFVDCodeBaseIOR()
  {
    checkShutdownState();
    if (codeBaseIOR != null) {
      return codeBaseIOR;
    }
    ValueHandler localValueHandler = ORBUtility.createValueHandler();
    CodeBase localCodeBase = (CodeBase)localValueHandler.getRunTimeCodeBase();
    return ORBUtility.connectAndGetIOR(this, localCodeBase);
  }
  
  public synchronized TypeCode get_primitive_tc(TCKind paramTCKind)
  {
    checkShutdownState();
    return get_primitive_tc(paramTCKind.value());
  }
  
  public synchronized TypeCode create_struct_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 15, paramString1, paramString2, paramArrayOfStructMember);
  }
  
  public synchronized TypeCode create_union_tc(String paramString1, String paramString2, TypeCode paramTypeCode, UnionMember[] paramArrayOfUnionMember)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 16, paramString1, paramString2, paramTypeCode, paramArrayOfUnionMember);
  }
  
  public synchronized TypeCode create_enum_tc(String paramString1, String paramString2, String[] paramArrayOfString)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 17, paramString1, paramString2, paramArrayOfString);
  }
  
  public synchronized TypeCode create_alias_tc(String paramString1, String paramString2, TypeCode paramTypeCode)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 21, paramString1, paramString2, paramTypeCode);
  }
  
  public synchronized TypeCode create_exception_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 22, paramString1, paramString2, paramArrayOfStructMember);
  }
  
  public synchronized TypeCode create_interface_tc(String paramString1, String paramString2)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 14, paramString1, paramString2);
  }
  
  public synchronized TypeCode create_string_tc(int paramInt)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 18, paramInt);
  }
  
  public synchronized TypeCode create_wstring_tc(int paramInt)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 27, paramInt);
  }
  
  public synchronized TypeCode create_sequence_tc(int paramInt, TypeCode paramTypeCode)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 19, paramInt, paramTypeCode);
  }
  
  public synchronized TypeCode create_recursive_sequence_tc(int paramInt1, int paramInt2)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 19, paramInt1, paramInt2);
  }
  
  public synchronized TypeCode create_array_tc(int paramInt, TypeCode paramTypeCode)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 20, paramInt, paramTypeCode);
  }
  
  public synchronized TypeCode create_native_tc(String paramString1, String paramString2)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 31, paramString1, paramString2);
  }
  
  public synchronized TypeCode create_abstract_interface_tc(String paramString1, String paramString2)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 32, paramString1, paramString2);
  }
  
  public synchronized TypeCode create_fixed_tc(short paramShort1, short paramShort2)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 28, paramShort1, paramShort2);
  }
  
  public synchronized TypeCode create_value_tc(String paramString1, String paramString2, short paramShort, TypeCode paramTypeCode, ValueMember[] paramArrayOfValueMember)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 29, paramString1, paramString2, paramShort, paramTypeCode, paramArrayOfValueMember);
  }
  
  public synchronized TypeCode create_recursive_tc(String paramString)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, paramString);
  }
  
  public synchronized TypeCode create_value_box_tc(String paramString1, String paramString2, TypeCode paramTypeCode)
  {
    checkShutdownState();
    return new TypeCodeImpl(this, 30, paramString1, paramString2, paramTypeCode);
  }
  
  public synchronized Any create_any()
  {
    checkShutdownState();
    return new AnyImpl(this);
  }
  
  public synchronized void setTypeCodeForClass(Class paramClass, TypeCodeImpl paramTypeCodeImpl)
  {
    checkShutdownState();
    if (typeCodeForClassMap == null) {
      typeCodeForClassMap = Collections.synchronizedMap(new WeakHashMap(64));
    }
    if (!typeCodeForClassMap.containsKey(paramClass)) {
      typeCodeForClassMap.put(paramClass, paramTypeCodeImpl);
    }
  }
  
  public synchronized TypeCodeImpl getTypeCodeForClass(Class paramClass)
  {
    checkShutdownState();
    if (typeCodeForClassMap == null) {
      return null;
    }
    return (TypeCodeImpl)typeCodeForClassMap.get(paramClass);
  }
  
  public String[] list_initial_services()
  {
    Resolver localResolver1;
    synchronized (this)
    {
      checkShutdownState();
      localResolver1 = resolver;
    }
    synchronized (resolverLock)
    {
      Set localSet = localResolver1.list();
      return (String[])localSet.toArray(new String[localSet.size()]);
    }
  }
  
  public org.omg.CORBA.Object resolve_initial_references(String paramString)
    throws InvalidName
  {
    Resolver localResolver1;
    synchronized (this)
    {
      checkShutdownState();
      localResolver1 = resolver;
    }
    synchronized (resolverLock)
    {
      org.omg.CORBA.Object localObject = localResolver1.resolve(paramString);
      if (localObject == null) {
        throw new InvalidName();
      }
      return localObject;
    }
  }
  
  public void register_initial_reference(String paramString, org.omg.CORBA.Object paramObject)
    throws InvalidName
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new InvalidName();
    }
    synchronized (this)
    {
      checkShutdownState();
    }
    CorbaServerRequestDispatcher localCorbaServerRequestDispatcher;
    synchronized (resolverLock)
    {
      localCorbaServerRequestDispatcher = insNamingDelegate;
      org.omg.CORBA.Object localObject = localResolver.resolve(paramString);
      if (localObject != null) {
        throw new InvalidName(paramString + " already registered");
      }
      localResolver.register(paramString, ClosureFactory.makeConstant(paramObject));
    }
    synchronized (this)
    {
      if (StubAdapter.isStub(paramObject)) {
        requestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher, paramString);
      }
    }
  }
  
  public void run()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (runObj)
    {
      try
      {
        runObj.wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public void shutdown(boolean paramBoolean)
  {
    int i = 0;
    synchronized (this)
    {
      checkShutdownState();
      if ((paramBoolean) && (isProcessingInvocation.get() == Boolean.TRUE)) {
        throw omgWrapper.shutdownWaitForCompletionDeadlock();
      }
      if (status == 2) {
        if (paramBoolean) {
          i = 1;
        } else {
          return;
        }
      }
      status = 2;
    }
    synchronized (shutdownObj)
    {
      if (i != 0) {
        for (;;)
        {
          synchronized (this)
          {
            if (status == 3) {
              break;
            }
          }
          try
          {
            shutdownObj.wait();
          }
          catch (InterruptedException localObject1) {}
        }
      }
      shutdownServants(paramBoolean);
      if (paramBoolean) {
        synchronized (waitForCompletionObj)
        {
          while (numInvocations > 0) {
            try
            {
              waitForCompletionObj.wait();
            }
            catch (InterruptedException localInterruptedException) {}
          }
        }
      }
      synchronized (runObj)
      {
        runObj.notifyAll();
      }
      status = 3;
      shutdownObj.notifyAll();
    }
  }
  
  protected void shutdownServants(boolean paramBoolean)
  {
    HashSet localHashSet;
    synchronized (this)
    {
      localHashSet = new HashSet(requestDispatcherRegistry.getObjectAdapterFactories());
    }
    ??? = localHashSet.iterator();
    while (((Iterator)???).hasNext())
    {
      ObjectAdapterFactory localObjectAdapterFactory = (ObjectAdapterFactory)((Iterator)???).next();
      localObjectAdapterFactory.shutdown(paramBoolean);
    }
  }
  
  public void checkShutdownState()
  {
    if (status == 4) {
      throw wrapper.orbDestroyed();
    }
    if (status == 3) {
      throw omgWrapper.badOperationAfterShutdown();
    }
  }
  
  public boolean isDuringDispatch()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    ??? = (Boolean)isProcessingInvocation.get();
    return ((Boolean)???).booleanValue();
  }
  
  public void startingDispatch()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (invocationObj)
    {
      isProcessingInvocation.set(Boolean.TRUE);
      numInvocations += 1;
    }
  }
  
  public void finishedDispatch()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (invocationObj)
    {
      numInvocations -= 1;
      isProcessingInvocation.set(Boolean.valueOf(false));
      if (numInvocations == 0) {
        synchronized (waitForCompletionObj)
        {
          waitForCompletionObj.notifyAll();
        }
      } else if (numInvocations < 0) {
        throw wrapper.numInvocationsAlreadyZero(CompletionStatus.COMPLETED_YES);
      }
    }
  }
  
  public void destroy()
  {
    int i = 0;
    synchronized (this)
    {
      i = status == 1 ? 1 : 0;
    }
    if (i != 0) {
      shutdown(true);
    }
    synchronized (this)
    {
      if (status < 4)
      {
        getCorbaTransportManager().close();
        getPIHandler().destroyInterceptors();
        status = 4;
      }
    }
    synchronized (threadPoolManagerAccessLock)
    {
      if (orbOwnsThreadPoolManager) {
        try
        {
          threadpoolMgr.close();
          threadpoolMgr = null;
        }
        catch (IOException localIOException3)
        {
          wrapper.ioExceptionOnClose(localIOException3);
        }
      }
    }
    try
    {
      monitoringManager.close();
      monitoringManager = null;
    }
    catch (IOException localIOException1)
    {
      wrapper.ioExceptionOnClose(localIOException1);
    }
    CachedCodeBase.cleanCache(this);
    try
    {
      pihandler.close();
    }
    catch (IOException localIOException2)
    {
      wrapper.ioExceptionOnClose(localIOException2);
    }
    super.destroy();
    badServerIdHandlerAccessLock = null;
    clientDelegateFactoryAccessorLock = null;
    corbaContactInfoListFactoryAccessLock = null;
    objectKeyFactoryAccessLock = null;
    legacyServerSocketManagerAccessLock = null;
    threadPoolManagerAccessLock = null;
    transportManager = null;
    legacyServerSocketManager = null;
    OAInvocationInfoStack = null;
    clientInvocationInfoStack = null;
    codeBaseIOR = null;
    dynamicRequests = null;
    svResponseReceived = null;
    runObj = null;
    shutdownObj = null;
    waitForCompletionObj = null;
    invocationObj = null;
    isProcessingInvocation = null;
    typeCodeForClassMap = null;
    valueFactoryCache = null;
    orbVersionThreadLocal = null;
    requestDispatcherRegistry = null;
    copierManager = null;
    toaFactory = null;
    poaFactory = null;
    pihandler = null;
    configData = null;
    badServerIdHandler = null;
    clientDelegateFactory = null;
    corbaContactInfoListFactory = null;
    resolver = null;
    localResolver = null;
    insNamingDelegate = null;
    urlOperation = null;
    taggedComponentFactoryFinder = null;
    taggedProfileFactoryFinder = null;
    taggedProfileTemplateFactoryFinder = null;
    objectKeyFactory = null;
  }
  
  public synchronized ValueFactory register_value_factory(String paramString, ValueFactory paramValueFactory)
  {
    checkShutdownState();
    if ((paramString == null) || (paramValueFactory == null)) {
      throw omgWrapper.unableRegisterValueFactory();
    }
    return (ValueFactory)valueFactoryCache.put(paramString, paramValueFactory);
  }
  
  public synchronized void unregister_value_factory(String paramString)
  {
    checkShutdownState();
    if (valueFactoryCache.remove(paramString) == null) {
      throw wrapper.nullParam();
    }
  }
  
  public synchronized ValueFactory lookup_value_factory(String paramString)
  {
    checkShutdownState();
    ValueFactory localValueFactory = (ValueFactory)valueFactoryCache.get(paramString);
    if (localValueFactory == null) {
      try
      {
        localValueFactory = Utility.getFactory(null, null, null, paramString);
      }
      catch (MARSHAL localMARSHAL)
      {
        throw wrapper.unableFindValueFactory(localMARSHAL);
      }
    }
    return localValueFactory;
  }
  
  public OAInvocationInfo peekInvocationInfo()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    ??? = (StackImpl)OAInvocationInfoStack.get();
    return (OAInvocationInfo)((StackImpl)???).peek();
  }
  
  public void pushInvocationInfo(OAInvocationInfo paramOAInvocationInfo)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    ??? = (StackImpl)OAInvocationInfoStack.get();
    ((StackImpl)???).push(paramOAInvocationInfo);
  }
  
  public OAInvocationInfo popInvocationInfo()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    ??? = (StackImpl)OAInvocationInfoStack.get();
    return (OAInvocationInfo)((StackImpl)???).pop();
  }
  
  public void initBadServerIdHandler()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (badServerIdHandlerAccessLock)
    {
      Class localClass = configData.getBadServerIdHandler();
      if (localClass != null) {
        try
        {
          Class[] arrayOfClass = { org.omg.CORBA.ORB.class };
          Object[] arrayOfObject = { this };
          Constructor localConstructor = localClass.getConstructor(arrayOfClass);
          badServerIdHandler = ((BadServerIdHandler)localConstructor.newInstance(arrayOfObject));
        }
        catch (Exception localException)
        {
          throw wrapper.errorInitBadserveridhandler(localException);
        }
      }
    }
  }
  
  public void setBadServerIdHandler(BadServerIdHandler paramBadServerIdHandler)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (badServerIdHandlerAccessLock)
    {
      badServerIdHandler = paramBadServerIdHandler;
    }
  }
  
  public void handleBadServerId(ObjectKey paramObjectKey)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (badServerIdHandlerAccessLock)
    {
      if (badServerIdHandler == null) {
        throw wrapper.badServerId();
      }
      badServerIdHandler.handle(paramObjectKey);
    }
  }
  
  public synchronized Policy create_policy(int paramInt, Any paramAny)
    throws PolicyError
  {
    checkShutdownState();
    return pihandler.create_policy(paramInt, paramAny);
  }
  
  public synchronized void connect(org.omg.CORBA.Object paramObject)
  {
    checkShutdownState();
    if (getTOAFactory() == null) {
      throw wrapper.noToa();
    }
    try
    {
      String str = Util.getCodebase(paramObject.getClass());
      getTOAFactory().getTOA(str).connect(paramObject);
    }
    catch (Exception localException)
    {
      throw wrapper.orbConnectError(localException);
    }
  }
  
  public synchronized void disconnect(org.omg.CORBA.Object paramObject)
  {
    checkShutdownState();
    if (getTOAFactory() == null) {
      throw wrapper.noToa();
    }
    try
    {
      getTOAFactory().getTOA().disconnect(paramObject);
    }
    catch (Exception localException)
    {
      throw wrapper.orbConnectError(localException);
    }
  }
  
  public int getTransientServerId()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    if (configData.getORBServerIdPropertySpecified()) {
      return configData.getPersistentServerId();
    }
    return transientServerId;
  }
  
  public RequestDispatcherRegistry getRequestDispatcherRegistry()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    return requestDispatcherRegistry;
  }
  
  public ServiceContextRegistry getServiceContextRegistry()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    return serviceContextRegistry;
  }
  
  public boolean isLocalHost(String paramString)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    return (paramString.equals(configData.getORBServerHost())) || (paramString.equals(getLocalHostName()));
  }
  
  public boolean isLocalServerId(int paramInt1, int paramInt2)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    if ((paramInt1 < 32) || (paramInt1 > 63)) {
      return paramInt2 == getTransientServerId();
    }
    if (ORBConstants.isTransient(paramInt1)) {
      return paramInt2 == getTransientServerId();
    }
    if (configData.getPersistentServerIdInitialized()) {
      return paramInt2 == configData.getPersistentServerId();
    }
    return false;
  }
  
  private String getHostName(String paramString)
    throws UnknownHostException
  {
    return InetAddress.getByName(paramString).getHostAddress();
  }
  
  private synchronized String getLocalHostName()
  {
    if (localHostString == null) {
      try
      {
        localHostString = InetAddress.getLocalHost().getHostAddress();
      }
      catch (Exception localException)
      {
        throw wrapper.getLocalHostFailed(localException);
      }
    }
    return localHostString;
  }
  
  public synchronized boolean work_pending()
  {
    checkShutdownState();
    throw wrapper.genericNoImpl();
  }
  
  public synchronized void perform_work()
  {
    checkShutdownState();
    throw wrapper.genericNoImpl();
  }
  
  public synchronized void set_delegate(Object paramObject)
  {
    checkShutdownState();
    POAFactory localPOAFactory = getPOAFactory();
    if (localPOAFactory != null) {
      ((Servant)paramObject)._set_delegate(localPOAFactory.getDelegateImpl());
    } else {
      throw wrapper.noPoa();
    }
  }
  
  public ClientInvocationInfo createOrIncrementInvocationInfo()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    ??? = (StackImpl)clientInvocationInfoStack.get();
    Object localObject2 = null;
    if (!((StackImpl)???).empty()) {
      localObject2 = (ClientInvocationInfo)((StackImpl)???).peek();
    }
    if ((localObject2 == null) || (!((ClientInvocationInfo)localObject2).isRetryInvocation()))
    {
      localObject2 = new CorbaInvocationInfo(this);
      startingDispatch();
      ((StackImpl)???).push(localObject2);
    }
    ((ClientInvocationInfo)localObject2).setIsRetryInvocation(false);
    ((ClientInvocationInfo)localObject2).incrementEntryCount();
    return (ClientInvocationInfo)localObject2;
  }
  
  public void releaseOrDecrementInvocationInfo()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    int i = -1;
    ClientInvocationInfo localClientInvocationInfo = null;
    StackImpl localStackImpl = (StackImpl)clientInvocationInfoStack.get();
    if (!localStackImpl.empty()) {
      localClientInvocationInfo = (ClientInvocationInfo)localStackImpl.peek();
    } else {
      throw wrapper.invocationInfoStackEmpty();
    }
    localClientInvocationInfo.decrementEntryCount();
    i = localClientInvocationInfo.getEntryCount();
    if (localClientInvocationInfo.getEntryCount() == 0)
    {
      if (!localClientInvocationInfo.isRetryInvocation()) {
        localStackImpl.pop();
      }
      finishedDispatch();
    }
  }
  
  public ClientInvocationInfo getInvocationInfo()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    ??? = (StackImpl)clientInvocationInfoStack.get();
    return (ClientInvocationInfo)((StackImpl)???).peek();
  }
  
  public void setClientDelegateFactory(ClientDelegateFactory paramClientDelegateFactory)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (clientDelegateFactoryAccessorLock)
    {
      clientDelegateFactory = paramClientDelegateFactory;
    }
  }
  
  /* Error */
  public ClientDelegateFactory getClientDelegateFactory()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: invokevirtual 1043	com/sun/corba/se/impl/orb/ORBImpl:checkShutdownState	()V
    //   8: aload_1
    //   9: monitorexit
    //   10: goto +8 -> 18
    //   13: astore_2
    //   14: aload_1
    //   15: monitorexit
    //   16: aload_2
    //   17: athrow
    //   18: aload_0
    //   19: getfield 965	com/sun/corba/se/impl/orb/ORBImpl:clientDelegateFactoryAccessorLock	Ljava/lang/Object;
    //   22: dup
    //   23: astore_1
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 956	com/sun/corba/se/impl/orb/ORBImpl:clientDelegateFactory	Lcom/sun/corba/se/spi/protocol/ClientDelegateFactory;
    //   29: aload_1
    //   30: monitorexit
    //   31: areturn
    //   32: astore_3
    //   33: aload_1
    //   34: monitorexit
    //   35: aload_3
    //   36: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	ORBImpl
    //   2	32	1	Ljava/lang/Object;	Object
    //   13	4	2	localObject1	Object
    //   32	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   4	10	13	finally
    //   13	16	13	finally
    //   25	31	32	finally
    //   32	35	32	finally
  }
  
  public void setCorbaContactInfoListFactory(CorbaContactInfoListFactory paramCorbaContactInfoListFactory)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (corbaContactInfoListFactoryAccessLock)
    {
      corbaContactInfoListFactory = paramCorbaContactInfoListFactory;
    }
  }
  
  public synchronized CorbaContactInfoListFactory getCorbaContactInfoListFactory()
  {
    checkShutdownState();
    return corbaContactInfoListFactory;
  }
  
  public void setResolver(Resolver paramResolver)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (resolverLock)
    {
      resolver = paramResolver;
    }
  }
  
  /* Error */
  public Resolver getResolver()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: invokevirtual 1043	com/sun/corba/se/impl/orb/ORBImpl:checkShutdownState	()V
    //   8: aload_1
    //   9: monitorexit
    //   10: goto +8 -> 18
    //   13: astore_2
    //   14: aload_1
    //   15: monitorexit
    //   16: aload_2
    //   17: athrow
    //   18: aload_0
    //   19: getfield 970	com/sun/corba/se/impl/orb/ORBImpl:resolverLock	Ljava/lang/Object;
    //   22: dup
    //   23: astore_1
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 961	com/sun/corba/se/impl/orb/ORBImpl:resolver	Lcom/sun/corba/se/spi/resolver/Resolver;
    //   29: aload_1
    //   30: monitorexit
    //   31: areturn
    //   32: astore_3
    //   33: aload_1
    //   34: monitorexit
    //   35: aload_3
    //   36: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	ORBImpl
    //   2	32	1	Ljava/lang/Object;	Object
    //   13	4	2	localObject1	Object
    //   32	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   4	10	13	finally
    //   13	16	13	finally
    //   25	31	32	finally
    //   32	35	32	finally
  }
  
  public void setLocalResolver(LocalResolver paramLocalResolver)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (resolverLock)
    {
      localResolver = paramLocalResolver;
    }
  }
  
  /* Error */
  public LocalResolver getLocalResolver()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: invokevirtual 1043	com/sun/corba/se/impl/orb/ORBImpl:checkShutdownState	()V
    //   8: aload_1
    //   9: monitorexit
    //   10: goto +8 -> 18
    //   13: astore_2
    //   14: aload_1
    //   15: monitorexit
    //   16: aload_2
    //   17: athrow
    //   18: aload_0
    //   19: getfield 970	com/sun/corba/se/impl/orb/ORBImpl:resolverLock	Ljava/lang/Object;
    //   22: dup
    //   23: astore_1
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 960	com/sun/corba/se/impl/orb/ORBImpl:localResolver	Lcom/sun/corba/se/spi/resolver/LocalResolver;
    //   29: aload_1
    //   30: monitorexit
    //   31: areturn
    //   32: astore_3
    //   33: aload_1
    //   34: monitorexit
    //   35: aload_3
    //   36: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	ORBImpl
    //   2	32	1	Ljava/lang/Object;	Object
    //   13	4	2	localObject1	Object
    //   32	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   4	10	13	finally
    //   13	16	13	finally
    //   25	31	32	finally
    //   32	35	32	finally
  }
  
  public void setURLOperation(Operation paramOperation)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (urlOperationLock)
    {
      urlOperation = paramOperation;
    }
  }
  
  /* Error */
  public Operation getURLOperation()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: invokevirtual 1043	com/sun/corba/se/impl/orb/ORBImpl:checkShutdownState	()V
    //   8: aload_1
    //   9: monitorexit
    //   10: goto +8 -> 18
    //   13: astore_2
    //   14: aload_1
    //   15: monitorexit
    //   16: aload_2
    //   17: athrow
    //   18: aload_0
    //   19: getfield 975	com/sun/corba/se/impl/orb/ORBImpl:urlOperationLock	Ljava/lang/Object;
    //   22: dup
    //   23: astore_1
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 954	com/sun/corba/se/impl/orb/ORBImpl:urlOperation	Lcom/sun/corba/se/spi/orb/Operation;
    //   29: aload_1
    //   30: monitorexit
    //   31: areturn
    //   32: astore_3
    //   33: aload_1
    //   34: monitorexit
    //   35: aload_3
    //   36: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	ORBImpl
    //   2	32	1	Ljava/lang/Object;	Object
    //   13	4	2	localObject1	Object
    //   32	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   4	10	13	finally
    //   13	16	13	finally
    //   25	31	32	finally
    //   32	35	32	finally
  }
  
  public void setINSDelegate(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (resolverLock)
    {
      insNamingDelegate = paramCorbaServerRequestDispatcher;
    }
  }
  
  public TaggedComponentFactoryFinder getTaggedComponentFactoryFinder()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    return taggedComponentFactoryFinder;
  }
  
  public IdentifiableFactoryFinder getTaggedProfileFactoryFinder()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    return taggedProfileFactoryFinder;
  }
  
  public IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    return taggedProfileTemplateFactoryFinder;
  }
  
  /* Error */
  public ObjectKeyFactory getObjectKeyFactory()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: invokevirtual 1043	com/sun/corba/se/impl/orb/ORBImpl:checkShutdownState	()V
    //   8: aload_1
    //   9: monitorexit
    //   10: goto +8 -> 18
    //   13: astore_2
    //   14: aload_1
    //   15: monitorexit
    //   16: aload_2
    //   17: athrow
    //   18: aload_0
    //   19: getfield 969	com/sun/corba/se/impl/orb/ORBImpl:objectKeyFactoryAccessLock	Ljava/lang/Object;
    //   22: dup
    //   23: astore_1
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 949	com/sun/corba/se/impl/orb/ORBImpl:objectKeyFactory	Lcom/sun/corba/se/spi/ior/ObjectKeyFactory;
    //   29: aload_1
    //   30: monitorexit
    //   31: areturn
    //   32: astore_3
    //   33: aload_1
    //   34: monitorexit
    //   35: aload_3
    //   36: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	ORBImpl
    //   2	32	1	Ljava/lang/Object;	Object
    //   13	4	2	localObject1	Object
    //   32	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   4	10	13	finally
    //   13	16	13	finally
    //   25	31	32	finally
    //   32	35	32	finally
  }
  
  public void setObjectKeyFactory(ObjectKeyFactory paramObjectKeyFactory)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (objectKeyFactoryAccessLock)
    {
      objectKeyFactory = paramObjectKeyFactory;
    }
  }
  
  public TransportManager getTransportManager()
  {
    synchronized (transportManagerAccessorLock)
    {
      if (transportManager == null) {
        transportManager = new CorbaTransportManagerImpl(this);
      }
      return transportManager;
    }
  }
  
  public CorbaTransportManager getCorbaTransportManager()
  {
    return (CorbaTransportManager)getTransportManager();
  }
  
  public LegacyServerSocketManager getLegacyServerSocketManager()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (legacyServerSocketManagerAccessLock)
    {
      if (legacyServerSocketManager == null) {
        legacyServerSocketManager = new LegacyServerSocketManagerImpl(this);
      }
      return legacyServerSocketManager;
    }
  }
  
  public void setThreadPoolManager(ThreadPoolManager paramThreadPoolManager)
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (threadPoolManagerAccessLock)
    {
      threadpoolMgr = paramThreadPoolManager;
    }
  }
  
  public ThreadPoolManager getThreadPoolManager()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    synchronized (threadPoolManagerAccessLock)
    {
      if (threadpoolMgr == null)
      {
        threadpoolMgr = new ThreadPoolManagerImpl();
        orbOwnsThreadPoolManager = true;
      }
      return threadpoolMgr;
    }
  }
  
  public CopierManager getCopierManager()
  {
    synchronized (this)
    {
      checkShutdownState();
    }
    return copierManager;
  }
  
  private static class ConfigParser
    extends ParserImplBase
  {
    public Class configurator = ORBConfiguratorImpl.class;
    
    private ConfigParser() {}
    
    public PropertyParser makeParser()
    {
      PropertyParser localPropertyParser = new PropertyParser();
      localPropertyParser.add("com.sun.CORBA.ORBConfigurator", OperationFactory.classAction(), "configurator");
      return localPropertyParser;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\ORBImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */