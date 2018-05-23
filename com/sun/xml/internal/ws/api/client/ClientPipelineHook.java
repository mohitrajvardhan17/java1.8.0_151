package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Pipe;

public abstract class ClientPipelineHook
{
  public ClientPipelineHook() {}
  
  @NotNull
  public Pipe createSecurityPipe(ClientPipeAssemblerContext paramClientPipeAssemblerContext, @NotNull Pipe paramPipe)
  {
    return paramPipe;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\client\ClientPipelineHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */