package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.W3CWsaServerTube;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionWsaServerTube;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ServerPipelineHook;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.handler.HandlerTube;
import com.sun.xml.internal.ws.handler.ServerLogicalHandlerTube;
import com.sun.xml.internal.ws.handler.ServerMessageHandlerTube;
import com.sun.xml.internal.ws.handler.ServerSOAPHandlerTube;
import com.sun.xml.internal.ws.protocol.soap.ServerMUTube;
import com.sun.xml.internal.ws.server.ServerSchemaValidationTube;
import com.sun.xml.internal.ws.util.pipe.DumpTube;
import java.io.PrintStream;
import java.util.List;
import javax.xml.ws.soap.SOAPBinding;

public class ServerTubeAssemblerContext
{
  private final SEIModel seiModel;
  private final WSDLPort wsdlModel;
  private final WSEndpoint endpoint;
  private final BindingImpl binding;
  private final Tube terminal;
  private final boolean isSynchronous;
  @NotNull
  private Codec codec;
  
  public ServerTubeAssemblerContext(@Nullable SEIModel paramSEIModel, @Nullable WSDLPort paramWSDLPort, @NotNull WSEndpoint paramWSEndpoint, @NotNull Tube paramTube, boolean paramBoolean)
  {
    seiModel = paramSEIModel;
    wsdlModel = paramWSDLPort;
    endpoint = paramWSEndpoint;
    terminal = paramTube;
    binding = ((BindingImpl)paramWSEndpoint.getBinding());
    isSynchronous = paramBoolean;
    codec = binding.createCodec();
  }
  
  @Nullable
  public SEIModel getSEIModel()
  {
    return seiModel;
  }
  
  @Nullable
  public WSDLPort getWsdlModel()
  {
    return wsdlModel;
  }
  
  @NotNull
  public WSEndpoint<?> getEndpoint()
  {
    return endpoint;
  }
  
  @NotNull
  public Tube getTerminalTube()
  {
    return terminal;
  }
  
  public boolean isSynchronous()
  {
    return isSynchronous;
  }
  
  @NotNull
  public Tube createServerMUTube(@NotNull Tube paramTube)
  {
    if ((binding instanceof SOAPBinding)) {
      return new ServerMUTube(this, paramTube);
    }
    return paramTube;
  }
  
  @NotNull
  public Tube createHandlerTube(@NotNull Tube paramTube)
  {
    if (!binding.getHandlerChain().isEmpty())
    {
      Object localObject = new ServerLogicalHandlerTube(binding, seiModel, wsdlModel, paramTube);
      paramTube = (Tube)localObject;
      if ((binding instanceof SOAPBinding))
      {
        paramTube = localObject = new ServerSOAPHandlerTube(binding, paramTube, (HandlerTube)localObject);
        paramTube = new ServerMessageHandlerTube(seiModel, binding, paramTube, (HandlerTube)localObject);
      }
    }
    return paramTube;
  }
  
  @NotNull
  public Tube createMonitoringTube(@NotNull Tube paramTube)
  {
    ServerPipelineHook localServerPipelineHook = (ServerPipelineHook)endpoint.getContainer().getSPI(ServerPipelineHook.class);
    if (localServerPipelineHook != null)
    {
      ServerPipeAssemblerContext localServerPipeAssemblerContext = new ServerPipeAssemblerContext(seiModel, wsdlModel, endpoint, terminal, isSynchronous);
      return PipeAdapter.adapt(localServerPipelineHook.createMonitoringPipe(localServerPipeAssemblerContext, PipeAdapter.adapt(paramTube)));
    }
    return paramTube;
  }
  
  @NotNull
  public Tube createSecurityTube(@NotNull Tube paramTube)
  {
    ServerPipelineHook localServerPipelineHook = (ServerPipelineHook)endpoint.getContainer().getSPI(ServerPipelineHook.class);
    if (localServerPipelineHook != null)
    {
      ServerPipeAssemblerContext localServerPipeAssemblerContext = new ServerPipeAssemblerContext(seiModel, wsdlModel, endpoint, terminal, isSynchronous);
      return PipeAdapter.adapt(localServerPipelineHook.createSecurityPipe(localServerPipeAssemblerContext, PipeAdapter.adapt(paramTube)));
    }
    return paramTube;
  }
  
  public Tube createDumpTube(String paramString, PrintStream paramPrintStream, Tube paramTube)
  {
    return new DumpTube(paramString, paramPrintStream, paramTube);
  }
  
  public Tube createValidationTube(Tube paramTube)
  {
    if (((binding instanceof SOAPBinding)) && (binding.isFeatureEnabled(SchemaValidationFeature.class)) && (wsdlModel != null)) {
      return new ServerSchemaValidationTube(endpoint, binding, seiModel, wsdlModel, paramTube);
    }
    return paramTube;
  }
  
  public Tube createWsaTube(Tube paramTube)
  {
    if (((binding instanceof SOAPBinding)) && (AddressingVersion.isEnabled(binding)))
    {
      if (AddressingVersion.fromBinding(binding) == AddressingVersion.MEMBER) {
        return new MemberSubmissionWsaServerTube(endpoint, wsdlModel, binding, paramTube);
      }
      return new W3CWsaServerTube(endpoint, wsdlModel, binding, paramTube);
    }
    return paramTube;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\ServerTubeAssemblerContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */