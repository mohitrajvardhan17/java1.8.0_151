package com.sun.xml.internal.ws.util.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;

public class StandaloneTubeAssembler
  implements TubelineAssembler
{
  public static final boolean dump;
  
  public StandaloneTubeAssembler() {}
  
  @NotNull
  public Tube createClient(ClientTubeAssemblerContext paramClientTubeAssemblerContext)
  {
    Tube localTube = paramClientTubeAssemblerContext.createTransportTube();
    localTube = paramClientTubeAssemblerContext.createSecurityTube(localTube);
    if (dump) {
      localTube = paramClientTubeAssemblerContext.createDumpTube("client", System.out, localTube);
    }
    localTube = paramClientTubeAssemblerContext.createWsaTube(localTube);
    localTube = paramClientTubeAssemblerContext.createClientMUTube(localTube);
    localTube = paramClientTubeAssemblerContext.createValidationTube(localTube);
    return paramClientTubeAssemblerContext.createHandlerTube(localTube);
  }
  
  public Tube createServer(ServerTubeAssemblerContext paramServerTubeAssemblerContext)
  {
    Tube localTube = paramServerTubeAssemblerContext.getTerminalTube();
    localTube = paramServerTubeAssemblerContext.createValidationTube(localTube);
    localTube = paramServerTubeAssemblerContext.createHandlerTube(localTube);
    localTube = paramServerTubeAssemblerContext.createMonitoringTube(localTube);
    localTube = paramServerTubeAssemblerContext.createServerMUTube(localTube);
    localTube = paramServerTubeAssemblerContext.createWsaTube(localTube);
    if (dump) {
      localTube = paramServerTubeAssemblerContext.createDumpTube("server", System.out, localTube);
    }
    localTube = paramServerTubeAssemblerContext.createSecurityTube(localTube);
    return localTube;
  }
  
  static
  {
    boolean bool = false;
    try
    {
      bool = Boolean.getBoolean(StandaloneTubeAssembler.class.getName() + ".dump");
    }
    catch (Throwable localThrowable) {}
    dump = bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\pipe\StandaloneTubeAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */