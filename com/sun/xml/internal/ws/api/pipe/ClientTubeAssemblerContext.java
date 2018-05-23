package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.W3CWsaClientTube;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionWsaClientTube;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.client.ClientPipelineHook;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.ClientSchemaValidationTube;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.handler.ClientLogicalHandlerTube;
import com.sun.xml.internal.ws.handler.ClientMessageHandlerTube;
import com.sun.xml.internal.ws.handler.ClientSOAPHandlerTube;
import com.sun.xml.internal.ws.handler.HandlerTube;
import com.sun.xml.internal.ws.protocol.soap.ClientMUTube;
import com.sun.xml.internal.ws.transport.DeferredTransportPipe;
import com.sun.xml.internal.ws.util.pipe.DumpTube;
import java.io.PrintStream;
import javax.xml.ws.soap.SOAPBinding;

public class ClientTubeAssemblerContext
{
  @NotNull
  private final EndpointAddress address;
  @Nullable
  private final WSDLPort wsdlModel;
  @Nullable
  private final SEIModel seiModel;
  @Nullable
  private final Class sei;
  @NotNull
  private final WSService rootOwner;
  @NotNull
  private final WSBinding binding;
  @NotNull
  private final Container container;
  @NotNull
  private Codec codec;
  @Nullable
  private final WSBindingProvider bindingProvider;
  
  /**
   * @deprecated
   */
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding)
  {
    this(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, Container.NONE);
  }
  
  /**
   * @deprecated
   */
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer)
  {
    this(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, paramContainer, ((BindingImpl)paramWSBinding).createCodec());
  }
  
  /**
   * @deprecated
   */
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer, Codec paramCodec)
  {
    this(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, paramContainer, paramCodec, null, null);
  }
  
  /**
   * @deprecated
   */
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer, Codec paramCodec, SEIModel paramSEIModel, Class paramClass)
  {
    this(paramEndpointAddress, paramWSDLPort, paramWSService, null, paramWSBinding, paramContainer, paramCodec, paramSEIModel, paramClass);
  }
  
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSBindingProvider paramWSBindingProvider, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer, Codec paramCodec, SEIModel paramSEIModel, Class paramClass)
  {
    this(paramEndpointAddress, paramWSDLPort, paramWSBindingProvider == null ? null : paramWSBindingProvider.getPortInfo().getOwner(), paramWSBindingProvider, paramWSBinding, paramContainer, paramCodec, paramSEIModel, paramClass);
  }
  
  private ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @Nullable WSService paramWSService, @Nullable WSBindingProvider paramWSBindingProvider, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer, Codec paramCodec, SEIModel paramSEIModel, Class paramClass)
  {
    address = paramEndpointAddress;
    wsdlModel = paramWSDLPort;
    rootOwner = paramWSService;
    bindingProvider = paramWSBindingProvider;
    binding = paramWSBinding;
    container = paramContainer;
    codec = paramCodec;
    seiModel = paramSEIModel;
    sei = paramClass;
  }
  
  @NotNull
  public EndpointAddress getAddress()
  {
    return address;
  }
  
  @Nullable
  public WSDLPort getWsdlModel()
  {
    return wsdlModel;
  }
  
  @NotNull
  public WSService getService()
  {
    return rootOwner;
  }
  
  @Nullable
  public WSPortInfo getPortInfo()
  {
    return bindingProvider == null ? null : bindingProvider.getPortInfo();
  }
  
  @Nullable
  public WSBindingProvider getBindingProvider()
  {
    return bindingProvider;
  }
  
  @NotNull
  public WSBinding getBinding()
  {
    return binding;
  }
  
  @Nullable
  public SEIModel getSEIModel()
  {
    return seiModel;
  }
  
  @Nullable
  public Class getSEI()
  {
    return sei;
  }
  
  public Container getContainer()
  {
    return container;
  }
  
  public Tube createDumpTube(String paramString, PrintStream paramPrintStream, Tube paramTube)
  {
    return new DumpTube(paramString, paramPrintStream, paramTube);
  }
  
  @NotNull
  public Tube createSecurityTube(@NotNull Tube paramTube)
  {
    ClientPipelineHook localClientPipelineHook = (ClientPipelineHook)container.getSPI(ClientPipelineHook.class);
    if (localClientPipelineHook != null)
    {
      ClientPipeAssemblerContext localClientPipeAssemblerContext = new ClientPipeAssemblerContext(address, wsdlModel, rootOwner, binding, container);
      return PipeAdapter.adapt(localClientPipelineHook.createSecurityPipe(localClientPipeAssemblerContext, PipeAdapter.adapt(paramTube)));
    }
    return paramTube;
  }
  
  public Tube createWsaTube(Tube paramTube)
  {
    if (((binding instanceof SOAPBinding)) && (AddressingVersion.isEnabled(binding)) && (wsdlModel != null))
    {
      if (AddressingVersion.fromBinding(binding) == AddressingVersion.MEMBER) {
        return new MemberSubmissionWsaClientTube(wsdlModel, binding, paramTube);
      }
      return new W3CWsaClientTube(wsdlModel, binding, paramTube);
    }
    return paramTube;
  }
  
  public Tube createHandlerTube(Tube paramTube)
  {
    Object localObject = null;
    if ((binding instanceof SOAPBinding))
    {
      ClientMessageHandlerTube localClientMessageHandlerTube = new ClientMessageHandlerTube(seiModel, binding, wsdlModel, paramTube);
      paramTube = localObject = localClientMessageHandlerTube;
      ClientSOAPHandlerTube localClientSOAPHandlerTube = new ClientSOAPHandlerTube(binding, paramTube, (HandlerTube)localObject);
      paramTube = localObject = localClientSOAPHandlerTube;
    }
    return new ClientLogicalHandlerTube(binding, seiModel, paramTube, (HandlerTube)localObject);
  }
  
  public Tube createClientMUTube(Tube paramTube)
  {
    if ((binding instanceof SOAPBinding)) {
      return new ClientMUTube(binding, paramTube);
    }
    return paramTube;
  }
  
  public Tube createValidationTube(Tube paramTube)
  {
    if (((binding instanceof SOAPBinding)) && (binding.isFeatureEnabled(SchemaValidationFeature.class)) && (wsdlModel != null)) {
      return new ClientSchemaValidationTube(binding, wsdlModel, paramTube);
    }
    return paramTube;
  }
  
  public Tube createTransportTube()
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    return new DeferredTransportPipe(localClassLoader, this);
  }
  
  @NotNull
  public Codec getCodec()
  {
    return codec;
  }
  
  public void setCodec(@NotNull Codec paramCodec)
  {
    codec = paramCodec;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\ClientTubeAssemblerContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */