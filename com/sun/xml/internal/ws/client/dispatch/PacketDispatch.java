package com.sun.xml.internal.ws.client.dispatch;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.ThrowableInPacketCompletionFeature;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.namespace.QName;
import javax.xml.ws.Service.Mode;

public class PacketDispatch
  extends DispatchImpl<Packet>
{
  private final boolean isDeliverThrowableInPacket = calculateIsDeliverThrowableInPacket(paramBindingImpl);
  
  @Deprecated
  public PacketDispatch(QName paramQName, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, @Nullable WSEndpointReference paramWSEndpointReference)
  {
    super(paramQName, Service.Mode.MESSAGE, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
  }
  
  public PacketDispatch(WSPortInfo paramWSPortInfo, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    this(paramWSPortInfo, paramTube, paramBindingImpl, paramWSEndpointReference, true);
  }
  
  public PacketDispatch(WSPortInfo paramWSPortInfo, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference, boolean paramBoolean)
  {
    super(paramWSPortInfo, Service.Mode.MESSAGE, paramTube, paramBindingImpl, paramWSEndpointReference, paramBoolean);
  }
  
  public PacketDispatch(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramWSPortInfo, Service.Mode.MESSAGE, paramBindingImpl, paramWSEndpointReference, true);
  }
  
  private boolean calculateIsDeliverThrowableInPacket(BindingImpl paramBindingImpl)
  {
    return paramBindingImpl.isFeatureEnabled(ThrowableInPacketCompletionFeature.class);
  }
  
  protected void configureFiber(Fiber paramFiber)
  {
    paramFiber.setDeliverThrowableInPacket(isDeliverThrowableInPacket);
  }
  
  Packet toReturnValue(Packet paramPacket)
  {
    return paramPacket;
  }
  
  Packet createPacket(Packet paramPacket)
  {
    return paramPacket;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\dispatch\PacketDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */