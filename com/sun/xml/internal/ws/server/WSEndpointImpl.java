package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.EPRSDDocumentFilter;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.EPRExtension;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.Fiber.CompletionCallback;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.EndpointAwareCodec;
import com.sun.xml.internal.ws.api.server.EndpointComponent;
import com.sun.xml.internal.ws.api.server.EndpointReferenceExtensionContributor;
import com.sun.xml.internal.ws.api.server.LazyMOMProvider;
import com.sun.xml.internal.ws.api.server.LazyMOMProvider.Scope;
import com.sun.xml.internal.ws.api.server.LazyMOMProvider.WSEndpointScopeChangeListener;
import com.sun.xml.internal.ws.api.server.ThreadLocalContainerResolver;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSEndpoint.CompletionCallback;
import com.sun.xml.internal.ws.api.server.WSEndpoint.PipeHead;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLProperties;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.resources.HandlerMessages;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.xml.internal.ws.util.Pool.TubePool;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import org.w3c.dom.Element;

public class WSEndpointImpl<T>
  extends WSEndpoint<T>
  implements LazyMOMProvider.WSEndpointScopeChangeListener
{
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.endpoint");
  @NotNull
  private final QName serviceName;
  @NotNull
  private final QName portName;
  protected final WSBinding binding;
  private final SEIModel seiModel;
  @NotNull
  private final Container container;
  private final WSDLPort port;
  protected final Tube masterTubeline;
  private final ServiceDefinitionImpl serviceDef;
  private final SOAPVersion soapVersion;
  private final Engine engine;
  @NotNull
  private final Codec masterCodec;
  @NotNull
  private final PolicyMap endpointPolicy;
  private final Pool<Tube> tubePool;
  private final OperationDispatcher operationDispatcher;
  @NotNull
  private ManagedObjectManager managedObjectManager;
  private boolean managedObjectManagerClosed = false;
  private final Object managedObjectManagerLock = new Object();
  private LazyMOMProvider.Scope lazyMOMProviderScope = LazyMOMProvider.Scope.STANDALONE;
  @NotNull
  private final ServerTubeAssemblerContext context;
  private Map<QName, WSEndpointReference.EPRExtension> endpointReferenceExtensions = new HashMap();
  private boolean disposed;
  private final Class<T> implementationClass;
  @NotNull
  private final WSDLProperties wsdlProperties;
  private final Set<Component> componentRegistry = new CopyOnWriteArraySet();
  private static final Logger monitoringLogger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");
  
  protected WSEndpointImpl(@NotNull QName paramQName1, @NotNull QName paramQName2, WSBinding paramWSBinding, Container paramContainer, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Class<T> paramClass, @Nullable ServiceDefinitionImpl paramServiceDefinitionImpl, EndpointAwareTube paramEndpointAwareTube, boolean paramBoolean, PolicyMap paramPolicyMap)
  {
    serviceName = paramQName1;
    portName = paramQName2;
    binding = paramWSBinding;
    soapVersion = paramWSBinding.getSOAPVersion();
    container = paramContainer;
    port = paramWSDLPort;
    implementationClass = paramClass;
    serviceDef = paramServiceDefinitionImpl;
    seiModel = paramSEIModel;
    endpointPolicy = paramPolicyMap;
    LazyMOMProvider.INSTANCE.registerEndpoint(this);
    initManagedObjectManager();
    if (paramServiceDefinitionImpl != null) {
      paramServiceDefinitionImpl.setOwner(this);
    }
    ComponentFeature localComponentFeature = (ComponentFeature)paramWSBinding.getFeature(ComponentFeature.class);
    if (localComponentFeature != null) {
      switch (localComponentFeature.getTarget())
      {
      case ENDPOINT: 
        componentRegistry.add(localComponentFeature.getComponent());
        break;
      case CONTAINER: 
        paramContainer.getComponents().add(localComponentFeature.getComponent());
        break;
      default: 
        throw new IllegalArgumentException();
      }
    }
    ComponentsFeature localComponentsFeature = (ComponentsFeature)paramWSBinding.getFeature(ComponentsFeature.class);
    if (localComponentsFeature != null)
    {
      localObject1 = localComponentsFeature.getComponentFeatures().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ComponentFeature)((Iterator)localObject1).next();
        switch (localObject2.getTarget())
        {
        case ENDPOINT: 
          componentRegistry.add(((ComponentFeature)localObject2).getComponent());
          break;
        case CONTAINER: 
          paramContainer.getComponents().add(((ComponentFeature)localObject2).getComponent());
          break;
        default: 
          throw new IllegalArgumentException();
        }
      }
    }
    Object localObject1 = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), paramWSBinding.getBindingId(), paramContainer);
    assert (localObject1 != null);
    operationDispatcher = (paramWSDLPort == null ? null : new OperationDispatcher(paramWSDLPort, paramWSBinding, paramSEIModel));
    context = createServerTubeAssemblerContext(paramEndpointAwareTube, paramBoolean);
    masterTubeline = ((TubelineAssembler)localObject1).createServer(context);
    Object localObject2 = context.getCodec();
    if ((localObject2 instanceof EndpointAwareCodec))
    {
      localObject2 = ((Codec)localObject2).copy();
      ((EndpointAwareCodec)localObject2).setEndpoint(this);
    }
    masterCodec = ((Codec)localObject2);
    tubePool = new Pool.TubePool(masterTubeline);
    paramEndpointAwareTube.setEndpoint(this);
    engine = new Engine(toString(), paramContainer);
    wsdlProperties = (paramWSDLPort == null ? new WSDLDirectProperties(paramQName1, paramQName2, paramSEIModel) : new WSDLPortProperties(paramWSDLPort, paramSEIModel));
    HashMap localHashMap = new HashMap();
    try
    {
      if (paramWSDLPort != null)
      {
        localObject3 = paramWSDLPort.getEPR();
        if (localObject3 != null)
        {
          localObject4 = ((WSEndpointReference)localObject3).getEPRExtensions().iterator();
          while (((Iterator)localObject4).hasNext())
          {
            WSEndpointReference.EPRExtension localEPRExtension1 = (WSEndpointReference.EPRExtension)((Iterator)localObject4).next();
            localHashMap.put(localEPRExtension1.getQName(), localEPRExtension1);
          }
        }
      }
      Object localObject3 = (EndpointReferenceExtensionContributor[])ServiceFinder.find(EndpointReferenceExtensionContributor.class).toArray();
      for (Object localObject5 : localObject3)
      {
        WSEndpointReference.EPRExtension localEPRExtension3 = (WSEndpointReference.EPRExtension)localHashMap.remove(((EndpointReferenceExtensionContributor)localObject5).getQName());
        WSEndpointReference.EPRExtension localEPRExtension4 = ((EndpointReferenceExtensionContributor)localObject5).getEPRExtension(this, localEPRExtension3);
        if (localEPRExtension4 != null) {
          localHashMap.put(localEPRExtension4.getQName(), localEPRExtension4);
        }
      }
      Object localObject4 = localHashMap.values().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        WSEndpointReference.EPRExtension localEPRExtension2 = (WSEndpointReference.EPRExtension)((Iterator)localObject4).next();
        endpointReferenceExtensions.put(localEPRExtension2.getQName(), new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader(localEPRExtension2.readAsXMLStreamReader()), localEPRExtension2.getQName()));
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
    if (!localHashMap.isEmpty()) {
      paramServiceDefinitionImpl.addFilter(new EPRSDDocumentFilter(this));
    }
  }
  
  protected ServerTubeAssemblerContext createServerTubeAssemblerContext(EndpointAwareTube paramEndpointAwareTube, boolean paramBoolean)
  {
    ServerPipeAssemblerContext localServerPipeAssemblerContext = new ServerPipeAssemblerContext(seiModel, port, this, paramEndpointAwareTube, paramBoolean);
    return localServerPipeAssemblerContext;
  }
  
  protected WSEndpointImpl(@NotNull QName paramQName1, @NotNull QName paramQName2, WSBinding paramWSBinding, Container paramContainer, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Tube paramTube)
  {
    serviceName = paramQName1;
    portName = paramQName2;
    binding = paramWSBinding;
    soapVersion = paramWSBinding.getSOAPVersion();
    container = paramContainer;
    endpointPolicy = null;
    port = paramWSDLPort;
    seiModel = paramSEIModel;
    serviceDef = null;
    implementationClass = null;
    masterTubeline = paramTube;
    masterCodec = ((BindingImpl)binding).createCodec();
    LazyMOMProvider.INSTANCE.registerEndpoint(this);
    initManagedObjectManager();
    operationDispatcher = (paramWSDLPort == null ? null : new OperationDispatcher(paramWSDLPort, paramWSBinding, paramSEIModel));
    context = new ServerPipeAssemblerContext(paramSEIModel, paramWSDLPort, this, null, false);
    tubePool = new Pool.TubePool(paramTube);
    engine = new Engine(toString(), paramContainer);
    wsdlProperties = (paramWSDLPort == null ? new WSDLDirectProperties(paramQName1, paramQName2, paramSEIModel) : new WSDLPortProperties(paramWSDLPort, paramSEIModel));
  }
  
  public Collection<WSEndpointReference.EPRExtension> getEndpointReferenceExtensions()
  {
    return endpointReferenceExtensions.values();
  }
  
  @Nullable
  public OperationDispatcher getOperationDispatcher()
  {
    return operationDispatcher;
  }
  
  public PolicyMap getPolicyMap()
  {
    return endpointPolicy;
  }
  
  @NotNull
  public Class<T> getImplementationClass()
  {
    return implementationClass;
  }
  
  @NotNull
  public WSBinding getBinding()
  {
    return binding;
  }
  
  @NotNull
  public Container getContainer()
  {
    return container;
  }
  
  public WSDLPort getPort()
  {
    return port;
  }
  
  @Nullable
  public SEIModel getSEIModel()
  {
    return seiModel;
  }
  
  public void setExecutor(Executor paramExecutor)
  {
    engine.setExecutor(paramExecutor);
  }
  
  public Engine getEngine()
  {
    return engine;
  }
  
  public void schedule(Packet paramPacket, WSEndpoint.CompletionCallback paramCompletionCallback, FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor)
  {
    processAsync(paramPacket, paramCompletionCallback, paramFiberContextSwitchInterceptor, true);
  }
  
  private void processAsync(final Packet paramPacket, final WSEndpoint.CompletionCallback paramCompletionCallback, FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor, boolean paramBoolean)
  {
    Container localContainer = ContainerResolver.getDefault().enterContainer(container);
    try
    {
      endpoint = this;
      paramPacket.addSatellite(wsdlProperties);
      Fiber localFiber = engine.createFiber();
      localFiber.setDeliverThrowableInPacket(true);
      if (paramFiberContextSwitchInterceptor != null) {
        localFiber.addInterceptor(paramFiberContextSwitchInterceptor);
      }
      final Tube localTube = (Tube)tubePool.take();
      Fiber.CompletionCallback local1 = new Fiber.CompletionCallback()
      {
        public void onCompletion(@NotNull Packet paramAnonymousPacket)
        {
          ThrowableContainerPropertySet localThrowableContainerPropertySet = (ThrowableContainerPropertySet)paramAnonymousPacket.getSatellite(ThrowableContainerPropertySet.class);
          if (localThrowableContainerPropertySet == null) {
            tubePool.recycle(localTube);
          }
          if (paramCompletionCallback != null)
          {
            if (localThrowableContainerPropertySet != null) {
              paramAnonymousPacket = createServiceResponseForException(localThrowableContainerPropertySet, paramAnonymousPacket, soapVersion, paramPacketendpoint.getPort(), null, paramPacketendpoint.getBinding());
            }
            paramCompletionCallback.onCompletion(paramAnonymousPacket);
          }
        }
        
        public void onCompletion(@NotNull Throwable paramAnonymousThrowable)
        {
          throw new IllegalStateException();
        }
      };
      localFiber.start(localTube, paramPacket, local1, (binding.isFeatureEnabled(SyncStartForAsyncFeature.class)) || (!paramBoolean));
    }
    finally
    {
      ContainerResolver.getDefault().exitContainer(localContainer);
    }
  }
  
  public Packet createServiceResponseForException(ThrowableContainerPropertySet paramThrowableContainerPropertySet, Packet paramPacket, SOAPVersion paramSOAPVersion, WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding)
  {
    if (paramThrowableContainerPropertySet.isFaultCreated()) {
      return paramPacket;
    }
    Message localMessage = SOAPFaultBuilder.createSOAPFaultMessage(paramSOAPVersion, null, paramThrowableContainerPropertySet.getThrowable());
    Packet localPacket = paramPacket.createServerResponse(localMessage, paramWSDLPort, paramSEIModel, paramWSBinding);
    paramThrowableContainerPropertySet.setFaultMessage(localMessage);
    paramThrowableContainerPropertySet.setResponsePacket(paramPacket);
    paramThrowableContainerPropertySet.setFaultCreated(true);
    return localPacket;
  }
  
  public void process(Packet paramPacket, WSEndpoint.CompletionCallback paramCompletionCallback, FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor)
  {
    processAsync(paramPacket, paramCompletionCallback, paramFiberContextSwitchInterceptor, false);
  }
  
  @NotNull
  public WSEndpoint.PipeHead createPipeHead()
  {
    new WSEndpoint.PipeHead()
    {
      private final Tube tube = TubeCloner.clone(masterTubeline);
      
      @NotNull
      public Packet process(Packet paramAnonymousPacket, WebServiceContextDelegate paramAnonymousWebServiceContextDelegate, TransportBackChannel paramAnonymousTransportBackChannel)
      {
        Container localContainer = ContainerResolver.getDefault().enterContainer(container);
        try
        {
          webServiceContextDelegate = paramAnonymousWebServiceContextDelegate;
          transportBackChannel = paramAnonymousTransportBackChannel;
          endpoint = WSEndpointImpl.this;
          paramAnonymousPacket.addSatellite(wsdlProperties);
          Fiber localFiber = engine.createFiber();
          Packet localPacket1;
          try
          {
            localPacket1 = localFiber.runSync(tube, paramAnonymousPacket);
          }
          catch (RuntimeException localRuntimeException)
          {
            Message localMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, localRuntimeException);
            localPacket1 = paramAnonymousPacket.createServerResponse(localMessage, endpoint.getPort(), null, endpoint.getBinding());
          }
          Packet localPacket2 = localPacket1;
          return localPacket2;
        }
        finally
        {
          ContainerResolver.getDefault().exitContainer(localContainer);
        }
      }
    };
  }
  
  public synchronized void dispose()
  {
    if (disposed) {
      return;
    }
    disposed = true;
    masterTubeline.preDestroy();
    Iterator localIterator = binding.getHandlerChain().iterator();
    while (localIterator.hasNext())
    {
      Handler localHandler = (Handler)localIterator.next();
      for (Method localMethod : localHandler.getClass().getMethods()) {
        if (localMethod.getAnnotation(PreDestroy.class) != null) {
          try
          {
            localMethod.invoke(localHandler, new Object[0]);
          }
          catch (Exception localException)
          {
            logger.log(Level.WARNING, HandlerMessages.HANDLER_PREDESTROY_IGNORE(localException.getMessage()), localException);
          }
        }
      }
    }
    closeManagedObjectManager();
    LazyMOMProvider.INSTANCE.unregisterEndpoint(this);
  }
  
  public ServiceDefinitionImpl getServiceDefinition()
  {
    return serviceDef;
  }
  
  public Set<EndpointComponent> getComponentRegistry()
  {
    EndpointComponentSet localEndpointComponentSet = new EndpointComponentSet(null);
    Iterator localIterator = componentRegistry.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      localEndpointComponentSet.add((localComponent instanceof EndpointComponentWrapper) ? component : new ComponentWrapper(localComponent));
    }
    return localEndpointComponentSet;
  }
  
  @NotNull
  public Set<Component> getComponents()
  {
    return componentRegistry;
  }
  
  public <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, String paramString1, String paramString2, Element... paramVarArgs)
  {
    List localList = null;
    if (paramVarArgs != null) {
      localList = Arrays.asList(paramVarArgs);
    }
    return getEndpointReference(paramClass, paramString1, paramString2, null, localList);
  }
  
  public <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, String paramString1, String paramString2, List<Element> paramList1, List<Element> paramList2)
  {
    QName localQName = null;
    if (port != null) {
      localQName = port.getBinding().getPortTypeName();
    }
    AddressingVersion localAddressingVersion = AddressingVersion.fromSpecClass(paramClass);
    return new WSEndpointReference(localAddressingVersion, paramString1, serviceName, portName, localQName, paramList1, paramString2, paramList2, endpointReferenceExtensions.values(), null).toSpec(paramClass);
  }
  
  @NotNull
  public QName getPortName()
  {
    return portName;
  }
  
  @NotNull
  public Codec createCodec()
  {
    return masterCodec.copy();
  }
  
  @NotNull
  public QName getServiceName()
  {
    return serviceName;
  }
  
  private void initManagedObjectManager()
  {
    synchronized (managedObjectManagerLock)
    {
      if (managedObjectManager == null) {
        switch (lazyMOMProviderScope)
        {
        case GLASSFISH_NO_JMX: 
          managedObjectManager = new WSEndpointMOMProxy(this);
          break;
        default: 
          managedObjectManager = obtainManagedObjectManager();
        }
      }
    }
  }
  
  @NotNull
  public ManagedObjectManager getManagedObjectManager()
  {
    return managedObjectManager;
  }
  
  @NotNull
  ManagedObjectManager obtainManagedObjectManager()
  {
    MonitorRootService localMonitorRootService = new MonitorRootService(this);
    ManagedObjectManager localManagedObjectManager = localMonitorRootService.createManagedObjectManager(this);
    localManagedObjectManager.resumeJMXRegistration();
    return localManagedObjectManager;
  }
  
  public void scopeChanged(LazyMOMProvider.Scope paramScope)
  {
    synchronized (managedObjectManagerLock)
    {
      if (managedObjectManagerClosed) {
        return;
      }
      lazyMOMProviderScope = paramScope;
      if (managedObjectManager == null)
      {
        if (paramScope != LazyMOMProvider.Scope.GLASSFISH_NO_JMX) {
          managedObjectManager = obtainManagedObjectManager();
        } else {
          managedObjectManager = new WSEndpointMOMProxy(this);
        }
      }
      else if (((managedObjectManager instanceof WSEndpointMOMProxy)) && (!((WSEndpointMOMProxy)managedObjectManager).isInitialized())) {
        ((WSEndpointMOMProxy)managedObjectManager).setManagedObjectManager(obtainManagedObjectManager());
      }
    }
  }
  
  public void closeManagedObjectManager()
  {
    synchronized (managedObjectManagerLock)
    {
      if (managedObjectManagerClosed == true) {
        return;
      }
      if (managedObjectManager != null)
      {
        int i = 1;
        if (((managedObjectManager instanceof WSEndpointMOMProxy)) && (!((WSEndpointMOMProxy)managedObjectManager).isInitialized())) {
          i = 0;
        }
        if (i != 0) {
          try
          {
            ObjectName localObjectName = managedObjectManager.getObjectName(managedObjectManager.getRoot());
            if (localObjectName != null) {
              monitoringLogger.log(Level.INFO, "Closing Metro monitoring root: {0}", localObjectName);
            }
            managedObjectManager.close();
          }
          catch (IOException localIOException)
          {
            monitoringLogger.log(Level.WARNING, "Ignoring error when closing Managed Object Manager", localIOException);
          }
        }
      }
      managedObjectManagerClosed = true;
    }
  }
  
  @NotNull
  public ServerTubeAssemblerContext getAssemblerContext()
  {
    return context;
  }
  
  private static class ComponentWrapper
    implements EndpointComponent
  {
    private final Component component;
    
    public ComponentWrapper(Component paramComponent)
    {
      component = paramComponent;
    }
    
    public <S> S getSPI(Class<S> paramClass)
    {
      return (S)component.getSPI(paramClass);
    }
    
    public int hashCode()
    {
      return component.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      return component.equals(paramObject);
    }
  }
  
  private class EndpointComponentSet
    extends HashSet<EndpointComponent>
  {
    private EndpointComponentSet() {}
    
    public Iterator<EndpointComponent> iterator()
    {
      final Iterator localIterator = super.iterator();
      new Iterator()
      {
        private EndpointComponent last = null;
        
        public boolean hasNext()
        {
          return localIterator.hasNext();
        }
        
        public EndpointComponent next()
        {
          last = ((EndpointComponent)localIterator.next());
          return last;
        }
        
        public void remove()
        {
          localIterator.remove();
          if (last != null) {
            componentRegistry.remove((last instanceof WSEndpointImpl.ComponentWrapper) ? last).component : new WSEndpointImpl.EndpointComponentWrapper(last));
          }
          last = null;
        }
      };
    }
    
    public boolean add(EndpointComponent paramEndpointComponent)
    {
      boolean bool = super.add(paramEndpointComponent);
      if (bool) {
        componentRegistry.add(new WSEndpointImpl.EndpointComponentWrapper(paramEndpointComponent));
      }
      return bool;
    }
    
    public boolean remove(Object paramObject)
    {
      boolean bool = super.remove(paramObject);
      if (bool) {
        componentRegistry.remove((paramObject instanceof WSEndpointImpl.ComponentWrapper) ? component : new WSEndpointImpl.EndpointComponentWrapper((EndpointComponent)paramObject));
      }
      return bool;
    }
  }
  
  private static class EndpointComponentWrapper
    implements Component
  {
    private final EndpointComponent component;
    
    public EndpointComponentWrapper(EndpointComponent paramEndpointComponent)
    {
      component = paramEndpointComponent;
    }
    
    public <S> S getSPI(Class<S> paramClass)
    {
      return (S)component.getSPI(paramClass);
    }
    
    public int hashCode()
    {
      return component.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      return component.equals(paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\WSEndpointImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */