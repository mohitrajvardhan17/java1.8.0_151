package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.io.PrintStream;

/**
 * @deprecated
 */
public final class ServerPipeAssemblerContext
  extends ServerTubeAssemblerContext
{
  public ServerPipeAssemblerContext(@Nullable SEIModel paramSEIModel, @Nullable WSDLPort paramWSDLPort, @NotNull WSEndpoint paramWSEndpoint, @NotNull Tube paramTube, boolean paramBoolean)
  {
    super(paramSEIModel, paramWSDLPort, paramWSEndpoint, paramTube, paramBoolean);
  }
  
  @NotNull
  public Pipe createServerMUPipe(@NotNull Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createServerMUTube(PipeAdapter.adapt(paramPipe)));
  }
  
  public Pipe createDumpPipe(String paramString, PrintStream paramPrintStream, Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createDumpTube(paramString, paramPrintStream, PipeAdapter.adapt(paramPipe)));
  }
  
  @NotNull
  public Pipe createMonitoringPipe(@NotNull Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createMonitoringTube(PipeAdapter.adapt(paramPipe)));
  }
  
  @NotNull
  public Pipe createSecurityPipe(@NotNull Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createSecurityTube(PipeAdapter.adapt(paramPipe)));
  }
  
  @NotNull
  public Pipe createValidationPipe(@NotNull Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createValidationTube(PipeAdapter.adapt(paramPipe)));
  }
  
  @NotNull
  public Pipe createHandlerPipe(@NotNull Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createHandlerTube(PipeAdapter.adapt(paramPipe)));
  }
  
  @NotNull
  public Pipe getTerminalPipe()
  {
    return PipeAdapter.adapt(super.getTerminalTube());
  }
  
  public Pipe createWsaPipe(Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createWsaTube(PipeAdapter.adapt(paramPipe)));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\ServerPipeAssemblerContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */