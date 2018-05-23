package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.handler.MessageHandlerContext;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import java.util.Set;

public class MessageHandlerContextImpl
  extends MessageUpdatableContext
  implements MessageHandlerContext
{
  @Nullable
  private SEIModel seiModel;
  private Set<String> roles;
  private WSBinding binding;
  @Nullable
  private WSDLPort wsdlModel;
  
  public MessageHandlerContextImpl(@Nullable SEIModel paramSEIModel, WSBinding paramWSBinding, @Nullable WSDLPort paramWSDLPort, Packet paramPacket, Set<String> paramSet)
  {
    super(paramPacket);
    seiModel = paramSEIModel;
    binding = paramWSBinding;
    wsdlModel = paramWSDLPort;
    roles = paramSet;
  }
  
  public Message getMessage()
  {
    return packet.getMessage();
  }
  
  public void setMessage(Message paramMessage)
  {
    packet.setMessage(paramMessage);
  }
  
  public Set<String> getRoles()
  {
    return roles;
  }
  
  public WSBinding getWSBinding()
  {
    return binding;
  }
  
  @Nullable
  public SEIModel getSEIModel()
  {
    return seiModel;
  }
  
  @Nullable
  public WSDLPort getPort()
  {
    return wsdlModel;
  }
  
  void updateMessage() {}
  
  void setPacketMessage(Message paramMessage)
  {
    setMessage(paramMessage);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\MessageHandlerContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */