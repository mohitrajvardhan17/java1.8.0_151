package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;

public abstract class ServerPipelineHook
{
  public ServerPipelineHook() {}
  
  @NotNull
  public Pipe createMonitoringPipe(ServerPipeAssemblerContext paramServerPipeAssemblerContext, @NotNull Pipe paramPipe)
  {
    return paramPipe;
  }
  
  @NotNull
  public Pipe createSecurityPipe(ServerPipeAssemblerContext paramServerPipeAssemblerContext, @NotNull Pipe paramPipe)
  {
    return paramPipe;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\ServerPipelineHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */