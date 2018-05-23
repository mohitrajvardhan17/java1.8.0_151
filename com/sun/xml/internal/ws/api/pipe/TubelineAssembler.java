package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;

public abstract interface TubelineAssembler
{
  @NotNull
  public abstract Tube createClient(@NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext);
  
  @NotNull
  public abstract Tube createServer(@NotNull ServerTubeAssemblerContext paramServerTubeAssemblerContext);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\TubelineAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */