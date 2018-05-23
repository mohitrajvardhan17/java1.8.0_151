package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;

public abstract interface PipelineAssembler
{
  @NotNull
  public abstract Pipe createClient(@NotNull ClientPipeAssemblerContext paramClientPipeAssemblerContext);
  
  @NotNull
  public abstract Pipe createServer(@NotNull ServerPipeAssemblerContext paramServerPipeAssemblerContext);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\PipelineAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */