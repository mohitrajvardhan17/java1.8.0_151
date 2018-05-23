package com.sun.xml.internal.ws.server.sei;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;

public abstract interface InvokerSource<T extends Invoker>
{
  @NotNull
  public abstract T getInvoker(Packet paramPacket);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\sei\InvokerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */