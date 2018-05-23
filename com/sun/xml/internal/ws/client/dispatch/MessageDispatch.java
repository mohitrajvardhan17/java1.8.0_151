package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.namespace.QName;
import javax.xml.ws.Service.Mode;

public class MessageDispatch
  extends DispatchImpl<Message>
{
  @Deprecated
  public MessageDispatch(QName paramQName, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramQName, Service.Mode.MESSAGE, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
  }
  
  public MessageDispatch(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramWSPortInfo, Service.Mode.MESSAGE, paramBindingImpl, paramWSEndpointReference, true);
  }
  
  Message toReturnValue(Packet paramPacket)
  {
    return paramPacket.getMessage();
  }
  
  Packet createPacket(Message paramMessage)
  {
    return new Packet(paramMessage);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\dispatch\MessageDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */