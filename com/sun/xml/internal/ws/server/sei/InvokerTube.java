package com.sun.xml.internal.ws.server.sei;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;

public abstract class InvokerTube<T extends Invoker>
  extends AbstractTubeImpl
  implements InvokerSource<T>
{
  protected final T invoker;
  
  protected InvokerTube(T paramT)
  {
    invoker = paramT;
  }
  
  protected InvokerTube(InvokerTube<T> paramInvokerTube, TubeCloner paramTubeCloner)
  {
    paramTubeCloner.add(paramInvokerTube, this);
    invoker = invoker;
  }
  
  @NotNull
  public T getInvoker(Packet paramPacket)
  {
    return invoker;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\sei\InvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */