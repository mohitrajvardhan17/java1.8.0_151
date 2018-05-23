package com.sun.xml.internal.ws.util.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipelineAssembler;
import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;

public class StandalonePipeAssembler
  implements PipelineAssembler
{
  private static final boolean dump;
  
  public StandalonePipeAssembler() {}
  
  @NotNull
  public Pipe createClient(ClientPipeAssemblerContext paramClientPipeAssemblerContext)
  {
    Pipe localPipe = paramClientPipeAssemblerContext.createTransportPipe();
    localPipe = paramClientPipeAssemblerContext.createSecurityPipe(localPipe);
    if (dump) {
      localPipe = paramClientPipeAssemblerContext.createDumpPipe("client", System.out, localPipe);
    }
    localPipe = paramClientPipeAssemblerContext.createWsaPipe(localPipe);
    localPipe = paramClientPipeAssemblerContext.createClientMUPipe(localPipe);
    return paramClientPipeAssemblerContext.createHandlerPipe(localPipe);
  }
  
  public Pipe createServer(ServerPipeAssemblerContext paramServerPipeAssemblerContext)
  {
    Pipe localPipe = paramServerPipeAssemblerContext.getTerminalPipe();
    localPipe = paramServerPipeAssemblerContext.createHandlerPipe(localPipe);
    localPipe = paramServerPipeAssemblerContext.createMonitoringPipe(localPipe);
    localPipe = paramServerPipeAssemblerContext.createServerMUPipe(localPipe);
    localPipe = paramServerPipeAssemblerContext.createWsaPipe(localPipe);
    localPipe = paramServerPipeAssemblerContext.createSecurityPipe(localPipe);
    return localPipe;
  }
  
  static
  {
    boolean bool = false;
    try
    {
      bool = Boolean.getBoolean(StandalonePipeAssembler.class.getName() + ".dump");
    }
    catch (Throwable localThrowable) {}
    dump = bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\pipe\StandalonePipeAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */