package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.ComponentFeature.Target;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.EPRExtension;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.Fiber.CompletionCallback;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptorFactory;
import com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.ThreadLocalContainerResolver;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLProperties;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.xml.internal.ws.util.Pool.TubePool;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

public abstract class Stub
  implements WSBindingProvider, ResponseContextReceiver, ComponentRegistry
{
  public static final String PREVENT_SYNC_START_FOR_ASYNC_INVOKE = "com.sun.xml.internal.ws.client.StubRequestSyncStartForAsyncInvoke";
  private Pool<Tube> tubes;
  private final Engine engine;
  protected final WSServiceDelegate owner;
  @Nullable
  protected WSEndpointReference endpointReference;
  protected final BindingImpl binding;
  protected final WSPortInfo portInfo;
  protected AddressingVersion addrVersion;
  public RequestContext requestContext = new RequestContext();
  private final RequestContext cleanRequestContext;
  private ResponseContext responseContext;
  @Nullable
  protected final WSDLPort wsdlPort;
  protected QName portname;
  @Nullable
  private volatile Header[] userOutboundHeaders;
  @NotNull
  private final WSDLProperties wsdlProperties;
  protected OperationDispatcher operationDispatcher = null;
  @NotNull
  private final ManagedObjectManager managedObjectManager;
  private boolean managedObjectManagerClosed = false;
  private final Set<Component> components = new CopyOnWriteArraySet();
  private static final Logger monitoringLogger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");
  
  @Deprecated
  protected Stub(WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSDLPort paramWSDLPort, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference)
  {
    this(paramWSServiceDelegate, paramTube, null, null, paramBindingImpl, paramWSDLPort, paramEndpointAddress, paramWSEndpointReference);
  }
  
  @Deprecated
  protected Stub(QName paramQName, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSDLPort paramWSDLPort, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference)
  {
    this(paramWSServiceDelegate, paramTube, null, paramQName, paramBindingImpl, paramWSDLPort, paramEndpointAddress, paramWSEndpointReference);
  }
  
  protected Stub(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl, Tube paramTube, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference)
  {
    this((WSServiceDelegate)paramWSPortInfo.getOwner(), paramTube, paramWSPortInfo, null, paramBindingImpl, paramWSPortInfo.getPort(), paramEndpointAddress, paramWSEndpointReference);
  }
  
  protected Stub(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference)
  {
    this(paramWSPortInfo, paramBindingImpl, null, paramEndpointAddress, paramWSEndpointReference);
  }
  
  private Stub(WSServiceDelegate paramWSServiceDelegate, @Nullable Tube paramTube, @Nullable WSPortInfo paramWSPortInfo, QName paramQName, BindingImpl paramBindingImpl, @Nullable WSDLPort paramWSDLPort, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference)
  {
    Container localContainer = ContainerResolver.getDefault().enterContainer(paramWSServiceDelegate.getContainer());
    try
    {
      owner = paramWSServiceDelegate;
      portInfo = paramWSPortInfo;
      wsdlPort = (paramWSPortInfo != null ? paramWSPortInfo.getPort() : paramWSDLPort != null ? paramWSDLPort : null);
      portname = paramQName;
      if (paramQName == null) {
        if (paramWSPortInfo != null) {
          portname = paramWSPortInfo.getPortName();
        } else if (paramWSDLPort != null) {
          portname = paramWSDLPort.getName();
        }
      }
      binding = paramBindingImpl;
      ComponentFeature localComponentFeature1 = (ComponentFeature)paramBindingImpl.getFeature(ComponentFeature.class);
      if ((localComponentFeature1 != null) && (ComponentFeature.Target.STUB.equals(localComponentFeature1.getTarget()))) {
        components.add(localComponentFeature1.getComponent());
      }
      ComponentsFeature localComponentsFeature = (ComponentsFeature)paramBindingImpl.getFeature(ComponentsFeature.class);
      if (localComponentsFeature != null)
      {
        Iterator localIterator = localComponentsFeature.getComponentFeatures().iterator();
        while (localIterator.hasNext())
        {
          ComponentFeature localComponentFeature2 = (ComponentFeature)localIterator.next();
          if (ComponentFeature.Target.STUB.equals(localComponentFeature2.getTarget())) {
            components.add(localComponentFeature2.getComponent());
          }
        }
      }
      if (paramWSEndpointReference != null) {
        requestContext.setEndPointAddressString(paramWSEndpointReference.getAddress());
      } else {
        requestContext.setEndpointAddress(paramEndpointAddress);
      }
      engine = new Engine(getStringId(), paramWSServiceDelegate.getContainer(), paramWSServiceDelegate.getExecutor());
      endpointReference = paramWSEndpointReference;
      wsdlProperties = (paramWSDLPort == null ? new WSDLDirectProperties(paramWSServiceDelegate.getServiceName(), paramQName) : new WSDLPortProperties(paramWSDLPort));
      cleanRequestContext = requestContext.copy();
      managedObjectManager = new MonitorRootClient(this).createManagedObjectManager(this);
      if (paramTube != null) {
        tubes = new Pool.TubePool(paramTube);
      } else {
        tubes = new Pool.TubePool(createPipeline(paramWSPortInfo, paramBindingImpl));
      }
      addrVersion = paramBindingImpl.getAddressingVersion();
      managedObjectManager.resumeJMXRegistration();
    }
    finally
    {
      ContainerResolver.getDefault().exitContainer(localContainer);
    }
  }
  
  private Tube createPipeline(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding)
  {
    checkAllWSDLExtensionsUnderstood(paramWSPortInfo, paramWSBinding);
    SOAPSEIModel localSOAPSEIModel = null;
    Class localClass = null;
    if ((paramWSPortInfo instanceof SEIPortInfo))
    {
      localObject = (SEIPortInfo)paramWSPortInfo;
      localSOAPSEIModel = model;
      localClass = sei;
    }
    Object localObject = paramWSPortInfo.getBindingId();
    TubelineAssembler localTubelineAssembler = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), (BindingID)localObject, owner.getContainer());
    if (localTubelineAssembler == null) {
      throw new WebServiceException("Unable to process bindingID=" + localObject);
    }
    return localTubelineAssembler.createClient(new ClientTubeAssemblerContext(paramWSPortInfo.getEndpointAddress(), paramWSPortInfo.getPort(), this, paramWSBinding, owner.getContainer(), ((BindingImpl)paramWSBinding).createCodec(), localSOAPSEIModel, localClass));
  }
  
  public WSDLPort getWSDLPort()
  {
    return wsdlPort;
  }
  
  public WSService getService()
  {
    return owner;
  }
  
  public Pool<Tube> getTubes()
  {
    return tubes;
  }
  
  private static void checkAllWSDLExtensionsUnderstood(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding)
  {
    if ((paramWSPortInfo.getPort() != null) && (paramWSBinding.isFeatureEnabled(RespectBindingFeature.class))) {
      paramWSPortInfo.getPort().areRequiredExtensionsUnderstood();
    }
  }
  
  public WSPortInfo getPortInfo()
  {
    return portInfo;
  }
  
  @Nullable
  public OperationDispatcher getOperationDispatcher()
  {
    if ((operationDispatcher == null) && (wsdlPort != null)) {
      operationDispatcher = new OperationDispatcher(wsdlPort, binding, null);
    }
    return operationDispatcher;
  }
  
  @NotNull
  protected abstract QName getPortName();
  
  @NotNull
  protected final QName getServiceName()
  {
    return owner.getServiceName();
  }
  
  public final Executor getExecutor()
  {
    return owner.getExecutor();
  }
  
  protected final Packet process(Packet paramPacket, RequestContext paramRequestContext, ResponseContextReceiver paramResponseContextReceiver)
  {
    isSynchronousMEP = Boolean.valueOf(true);
    component = this;
    configureRequestPacket(paramPacket, paramRequestContext);
    Pool localPool = tubes;
    if (localPool == null) {
      throw new WebServiceException("close method has already been invoked");
    }
    Fiber localFiber = engine.createFiber();
    configureFiber(localFiber);
    Tube localTube = (Tube)localPool.take();
    try
    {
      Packet localPacket1 = localFiber.runSync(localTube, paramPacket);
      Packet localPacket2;
      return localPacket1;
    }
    finally
    {
      Packet localPacket3 = localFiber.getPacket() == null ? paramPacket : localFiber.getPacket();
      paramResponseContextReceiver.setResponseContext(new ResponseContext(localPacket3));
      localPool.recycle(localTube);
    }
  }
  
  private void configureRequestPacket(Packet paramPacket, RequestContext paramRequestContext)
  {
    proxy = this;
    handlerConfig = binding.getHandlerConfig();
    Header[] arrayOfHeader1 = userOutboundHeaders;
    MessageHeaders localMessageHeaders;
    if (arrayOfHeader1 != null)
    {
      localMessageHeaders = paramPacket.getMessage().getHeaders();
      for (Header localHeader : arrayOfHeader1) {
        localMessageHeaders.add(localHeader);
      }
    }
    paramRequestContext.fill(paramPacket, binding.getAddressingVersion() != null);
    paramPacket.addSatellite(wsdlProperties);
    if (addrVersion != null)
    {
      localMessageHeaders = paramPacket.getMessage().getHeaders();
      AddressingUtils.fillRequestAddressingHeaders(localMessageHeaders, wsdlPort, binding, paramPacket);
      if (endpointReference != null) {
        endpointReference.addReferenceParametersToList(paramPacket.getMessage().getHeaders());
      }
    }
  }
  
  protected final void processAsync(AsyncResponseImpl<?> paramAsyncResponseImpl, Packet paramPacket, RequestContext paramRequestContext, final Fiber.CompletionCallback paramCompletionCallback)
  {
    component = this;
    configureRequestPacket(paramPacket, paramRequestContext);
    final Pool localPool = tubes;
    if (localPool == null) {
      throw new WebServiceException("close method has already been invoked");
    }
    Fiber localFiber = engine.createFiber();
    configureFiber(localFiber);
    paramAsyncResponseImpl.setCancelable(localFiber);
    if (paramAsyncResponseImpl.isCancelled()) {
      return;
    }
    FiberContextSwitchInterceptorFactory localFiberContextSwitchInterceptorFactory = (FiberContextSwitchInterceptorFactory)owner.getSPI(FiberContextSwitchInterceptorFactory.class);
    if (localFiberContextSwitchInterceptorFactory != null) {
      localFiber.addInterceptor(localFiberContextSwitchInterceptorFactory.create());
    }
    final Tube localTube = (Tube)localPool.take();
    Fiber.CompletionCallback local1 = new Fiber.CompletionCallback()
    {
      public void onCompletion(@NotNull Packet paramAnonymousPacket)
      {
        localPool.recycle(localTube);
        paramCompletionCallback.onCompletion(paramAnonymousPacket);
      }
      
      public void onCompletion(@NotNull Throwable paramAnonymousThrowable)
      {
        paramCompletionCallback.onCompletion(paramAnonymousThrowable);
      }
    };
    localFiber.start(localTube, paramPacket, local1, (getBinding().isFeatureEnabled(SyncStartForAsyncFeature.class)) && (!paramRequestContext.containsKey("com.sun.xml.internal.ws.client.StubRequestSyncStartForAsyncInvoke")));
  }
  
  protected void configureFiber(Fiber paramFiber) {}
  
  public void close()
  {
    Pool.TubePool localTubePool = (Pool.TubePool)tubes;
    Object localObject;
    if (localTubePool != null)
    {
      localObject = localTubePool.takeMaster();
      ((Tube)localObject).preDestroy();
      tubes = null;
    }
    if (!managedObjectManagerClosed)
    {
      try
      {
        localObject = managedObjectManager.getObjectName(managedObjectManager.getRoot());
        if (localObject != null) {
          monitoringLogger.log(Level.INFO, "Closing Metro monitoring root: {0}", localObject);
        }
        managedObjectManager.close();
      }
      catch (IOException localIOException)
      {
        monitoringLogger.log(Level.WARNING, "Ignoring error when closing Managed Object Manager", localIOException);
      }
      managedObjectManagerClosed = true;
    }
  }
  
  public final WSBinding getBinding()
  {
    return binding;
  }
  
  public final Map<String, Object> getRequestContext()
  {
    return requestContext.asMap();
  }
  
  public void resetRequestContext()
  {
    requestContext = cleanRequestContext.copy();
  }
  
  public final ResponseContext getResponseContext()
  {
    return responseContext;
  }
  
  public void setResponseContext(ResponseContext paramResponseContext)
  {
    responseContext = paramResponseContext;
  }
  
  private String getStringId()
  {
    return RuntimeVersion.VERSION + ": Stub for " + getRequestContext().get("javax.xml.ws.service.endpoint.address");
  }
  
  public String toString()
  {
    return getStringId();
  }
  
  public final WSEndpointReference getWSEndpointReference()
  {
    if (binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http")) {
      throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference(Class<T> class)", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding"));
    }
    if (endpointReference != null) {
      return endpointReference;
    }
    String str1 = requestContext.getEndpointAddress().toString();
    QName localQName = null;
    String str2 = null;
    ArrayList localArrayList = new ArrayList();
    if (wsdlPort != null)
    {
      localQName = wsdlPort.getBinding().getPortTypeName();
      str2 = str1 + "?wsdl";
      try
      {
        WSEndpointReference localWSEndpointReference = wsdlPort.getEPR();
        if (localWSEndpointReference != null)
        {
          Iterator localIterator = localWSEndpointReference.getEPRExtensions().iterator();
          while (localIterator.hasNext())
          {
            WSEndpointReference.EPRExtension localEPRExtension = (WSEndpointReference.EPRExtension)localIterator.next();
            localArrayList.add(new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader(localEPRExtension.readAsXMLStreamReader()), localEPRExtension.getQName()));
          }
        }
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(localXMLStreamException);
      }
    }
    AddressingVersion localAddressingVersion = AddressingVersion.W3C;
    endpointReference = new WSEndpointReference(localAddressingVersion, str1, getServiceName(), getPortName(), localQName, null, str2, null, localArrayList, null);
    return endpointReference;
  }
  
  public final W3CEndpointReference getEndpointReference()
  {
    if (binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http")) {
      throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference()", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding"));
    }
    return (W3CEndpointReference)getEndpointReference(W3CEndpointReference.class);
  }
  
  public final <T extends EndpointReference> T getEndpointReference(Class<T> paramClass)
  {
    return getWSEndpointReference().toSpec(paramClass);
  }
  
  @NotNull
  public ManagedObjectManager getManagedObjectManager()
  {
    return managedObjectManager;
  }
  
  public final void setOutboundHeaders(List<Header> paramList)
  {
    if (paramList == null)
    {
      userOutboundHeaders = null;
    }
    else
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        Header localHeader = (Header)localIterator.next();
        if (localHeader == null) {
          throw new IllegalArgumentException();
        }
      }
      userOutboundHeaders = ((Header[])paramList.toArray(new Header[paramList.size()]));
    }
  }
  
  public final void setOutboundHeaders(Header... paramVarArgs)
  {
    if (paramVarArgs == null)
    {
      userOutboundHeaders = null;
    }
    else
    {
      for (Header localHeader : paramVarArgs) {
        if (localHeader == null) {
          throw new IllegalArgumentException();
        }
      }
      ??? = new Header[paramVarArgs.length];
      System.arraycopy(paramVarArgs, 0, ???, 0, paramVarArgs.length);
      userOutboundHeaders = ???;
    }
  }
  
  public final List<Header> getInboundHeaders()
  {
    return Collections.unmodifiableList(((MessageHeaders)responseContext.get("com.sun.xml.internal.ws.api.message.HeaderList")).asList());
  }
  
  public final void setAddress(String paramString)
  {
    requestContext.put("javax.xml.ws.service.endpoint.address", paramString);
  }
  
  public <S> S getSPI(Class<S> paramClass)
  {
    Iterator localIterator = components.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      Object localObject = localComponent.getSPI(paramClass);
      if (localObject != null) {
        return (S)localObject;
      }
    }
    return (S)owner.getSPI(paramClass);
  }
  
  public Set<Component> getComponents()
  {
    return components;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\Stub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */