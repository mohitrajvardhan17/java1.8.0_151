package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;

public abstract class TransportPipeFactory
{
  public TransportPipeFactory() {}
  
  public abstract Pipe doCreate(@NotNull ClientPipeAssemblerContext paramClientPipeAssemblerContext);
  
  /**
   * @deprecated
   */
  public static Pipe create(@Nullable ClassLoader paramClassLoader, @NotNull ClientPipeAssemblerContext paramClientPipeAssemblerContext)
  {
    return PipeAdapter.adapt(TransportTubeFactory.create(paramClassLoader, paramClientPipeAssemblerContext));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\TransportPipeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */