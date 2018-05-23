package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.message.MessageContext;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.ClientCallBridge;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.Packet.State;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.sei.StubAsyncHandler;
import com.sun.xml.internal.ws.client.sei.StubHandler;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.server.sei.TieHandler;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import com.sun.xml.internal.ws.wsdl.writer.WSDLGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.WebServiceFeature;

public final class DatabindingImpl
  implements Databinding
{
  AbstractSEIModelImpl seiModel;
  Map<Method, StubHandler> stubHandlers;
  Map<JavaMethodImpl, TieHandler> wsdlOpMap = new HashMap();
  Map<Method, TieHandler> tieHandlers = new HashMap();
  OperationDispatcher operationDispatcher;
  OperationDispatcher operationDispatcherNoWsdl;
  boolean clientConfig = false;
  Codec codec;
  MessageContextFactory packetFactory = null;
  
  public DatabindingImpl(DatabindingProviderImpl paramDatabindingProviderImpl, DatabindingConfig paramDatabindingConfig)
  {
    RuntimeModeler localRuntimeModeler = new RuntimeModeler(paramDatabindingConfig);
    localRuntimeModeler.setClassLoader(paramDatabindingConfig.getClassLoader());
    seiModel = localRuntimeModeler.buildRuntimeModel();
    WSDLPort localWSDLPort = paramDatabindingConfig.getWsdlPort();
    packetFactory = new MessageContextFactory(seiModel.getWSBinding().getFeatures());
    clientConfig = isClientConfig(paramDatabindingConfig);
    if (clientConfig) {
      initStubHandlers();
    }
    seiModel.setDatabinding(this);
    if (localWSDLPort != null) {
      freeze(localWSDLPort);
    }
    if (operationDispatcher == null) {
      operationDispatcherNoWsdl = new OperationDispatcher(null, seiModel.getWSBinding(), seiModel);
    }
    Iterator localIterator = seiModel.getJavaMethods().iterator();
    while (localIterator.hasNext())
    {
      JavaMethodImpl localJavaMethodImpl = (JavaMethodImpl)localIterator.next();
      if (!localJavaMethodImpl.isAsync())
      {
        TieHandler localTieHandler = new TieHandler(localJavaMethodImpl, seiModel.getWSBinding(), packetFactory);
        wsdlOpMap.put(localJavaMethodImpl, localTieHandler);
        tieHandlers.put(localTieHandler.getMethod(), localTieHandler);
      }
    }
  }
  
  private boolean isClientConfig(DatabindingConfig paramDatabindingConfig)
  {
    if (paramDatabindingConfig.getContractClass() == null) {
      return false;
    }
    if (!paramDatabindingConfig.getContractClass().isInterface()) {
      return false;
    }
    return (paramDatabindingConfig.getEndpointClass() == null) || (paramDatabindingConfig.getEndpointClass().isInterface());
  }
  
  public void freeze(WSDLPort paramWSDLPort)
  {
    if (clientConfig) {
      return;
    }
    synchronized (this)
    {
      if (operationDispatcher == null) {
        operationDispatcher = (paramWSDLPort == null ? null : new OperationDispatcher(paramWSDLPort, seiModel.getWSBinding(), seiModel));
      }
    }
  }
  
  public SEIModel getModel()
  {
    return seiModel;
  }
  
  private void initStubHandlers()
  {
    stubHandlers = new HashMap();
    HashMap localHashMap = new HashMap();
    Iterator localIterator = seiModel.getJavaMethods().iterator();
    JavaMethodImpl localJavaMethodImpl;
    Object localObject;
    while (localIterator.hasNext())
    {
      localJavaMethodImpl = (JavaMethodImpl)localIterator.next();
      if (!getMEPisAsync)
      {
        localObject = new StubHandler(localJavaMethodImpl, packetFactory);
        localHashMap.put(localJavaMethodImpl.getOperationSignature(), localJavaMethodImpl);
        stubHandlers.put(localJavaMethodImpl.getMethod(), localObject);
      }
    }
    localIterator = seiModel.getJavaMethods().iterator();
    while (localIterator.hasNext())
    {
      localJavaMethodImpl = (JavaMethodImpl)localIterator.next();
      localObject = (JavaMethodImpl)localHashMap.get(localJavaMethodImpl.getOperationSignature());
      if ((localJavaMethodImpl.getMEP() == MEP.ASYNC_CALLBACK) || (localJavaMethodImpl.getMEP() == MEP.ASYNC_POLL))
      {
        Method localMethod = localJavaMethodImpl.getMethod();
        StubAsyncHandler localStubAsyncHandler = new StubAsyncHandler(localJavaMethodImpl, (JavaMethodImpl)localObject, packetFactory);
        stubHandlers.put(localMethod, localStubAsyncHandler);
      }
    }
  }
  
  JavaMethodImpl resolveJavaMethod(Packet paramPacket)
    throws DispatchException
  {
    WSDLOperationMapping localWSDLOperationMapping = paramPacket.getWSDLOperationMapping();
    if (localWSDLOperationMapping == null) {
      synchronized (this)
      {
        localWSDLOperationMapping = operationDispatcher != null ? operationDispatcher.getWSDLOperationMapping(paramPacket) : operationDispatcherNoWsdl.getWSDLOperationMapping(paramPacket);
      }
    }
    return (JavaMethodImpl)localWSDLOperationMapping.getJavaMethod();
  }
  
  public com.oracle.webservices.internal.api.databinding.JavaCallInfo deserializeRequest(Packet paramPacket)
  {
    com.sun.xml.internal.ws.api.databinding.JavaCallInfo localJavaCallInfo = new com.sun.xml.internal.ws.api.databinding.JavaCallInfo();
    try
    {
      JavaMethodImpl localJavaMethodImpl = resolveJavaMethod(paramPacket);
      TieHandler localTieHandler = (TieHandler)wsdlOpMap.get(localJavaMethodImpl);
      localJavaCallInfo.setMethod(localTieHandler.getMethod());
      Object[] arrayOfObject = localTieHandler.readRequest(paramPacket.getMessage());
      localJavaCallInfo.setParameters(arrayOfObject);
    }
    catch (DispatchException localDispatchException)
    {
      localJavaCallInfo.setException(localDispatchException);
    }
    return localJavaCallInfo;
  }
  
  public com.oracle.webservices.internal.api.databinding.JavaCallInfo deserializeResponse(Packet paramPacket, com.oracle.webservices.internal.api.databinding.JavaCallInfo paramJavaCallInfo)
  {
    StubHandler localStubHandler = (StubHandler)stubHandlers.get(paramJavaCallInfo.getMethod());
    try
    {
      return localStubHandler.readResponse(paramPacket, paramJavaCallInfo);
    }
    catch (Throwable localThrowable)
    {
      paramJavaCallInfo.setException(localThrowable);
    }
    return paramJavaCallInfo;
  }
  
  public WebServiceFeature[] getFeatures()
  {
    return null;
  }
  
  public Packet serializeRequest(com.oracle.webservices.internal.api.databinding.JavaCallInfo paramJavaCallInfo)
  {
    StubHandler localStubHandler = (StubHandler)stubHandlers.get(paramJavaCallInfo.getMethod());
    Packet localPacket = localStubHandler.createRequestPacket(paramJavaCallInfo);
    localPacket.setState(Packet.State.ClientRequest);
    return localPacket;
  }
  
  public Packet serializeResponse(com.oracle.webservices.internal.api.databinding.JavaCallInfo paramJavaCallInfo)
  {
    Method localMethod = paramJavaCallInfo.getMethod();
    Message localMessage = null;
    if (localMethod != null)
    {
      localObject = (TieHandler)tieHandlers.get(localMethod);
      if (localObject != null) {
        return ((TieHandler)localObject).serializeResponse(paramJavaCallInfo);
      }
    }
    if ((paramJavaCallInfo.getException() instanceof DispatchException)) {
      localMessage = getExceptionfault;
    }
    Object localObject = (Packet)packetFactory.createContext(localMessage);
    ((Packet)localObject).setState(Packet.State.ServerResponse);
    return (Packet)localObject;
  }
  
  public ClientCallBridge getClientBridge(Method paramMethod)
  {
    return (ClientCallBridge)stubHandlers.get(paramMethod);
  }
  
  public void generateWSDL(WSDLGenInfo paramWSDLGenInfo)
  {
    WSDLGenerator localWSDLGenerator = new WSDLGenerator(seiModel, paramWSDLGenInfo.getWsdlResolver(), seiModel.getWSBinding(), paramWSDLGenInfo.getContainer(), seiModel.getEndpointClass(), paramWSDLGenInfo.isInlineSchemas(), paramWSDLGenInfo.isSecureXmlProcessingDisabled(), paramWSDLGenInfo.getExtensions());
    localWSDLGenerator.doGeneration();
  }
  
  public EndpointCallBridge getEndpointBridge(Packet paramPacket)
    throws DispatchException
  {
    JavaMethodImpl localJavaMethodImpl = resolveJavaMethod(paramPacket);
    return (EndpointCallBridge)wsdlOpMap.get(localJavaMethodImpl);
  }
  
  Codec getCodec()
  {
    if (codec == null) {
      codec = ((BindingImpl)seiModel.getWSBinding()).createCodec();
    }
    return codec;
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
    throws IOException
  {
    return getCodec().encode(paramPacket, paramOutputStream);
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException
  {
    getCodec().decode(paramInputStream, paramString, paramPacket);
  }
  
  public com.oracle.webservices.internal.api.databinding.JavaCallInfo createJavaCallInfo(Method paramMethod, Object[] paramArrayOfObject)
  {
    return new com.sun.xml.internal.ws.api.databinding.JavaCallInfo(paramMethod, paramArrayOfObject);
  }
  
  public com.oracle.webservices.internal.api.databinding.JavaCallInfo deserializeResponse(MessageContext paramMessageContext, com.oracle.webservices.internal.api.databinding.JavaCallInfo paramJavaCallInfo)
  {
    return deserializeResponse((Packet)paramMessageContext, paramJavaCallInfo);
  }
  
  public com.oracle.webservices.internal.api.databinding.JavaCallInfo deserializeRequest(MessageContext paramMessageContext)
  {
    return deserializeRequest((Packet)paramMessageContext);
  }
  
  public MessageContextFactory getMessageContextFactory()
  {
    return packetFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\db\DatabindingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */