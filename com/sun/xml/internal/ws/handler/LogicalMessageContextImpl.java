package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalMessageContext;

class LogicalMessageContextImpl
  extends MessageUpdatableContext
  implements LogicalMessageContext
{
  private LogicalMessageImpl lm;
  private WSBinding binding;
  private BindingContext defaultJaxbContext;
  
  public LogicalMessageContextImpl(WSBinding paramWSBinding, BindingContext paramBindingContext, Packet paramPacket)
  {
    super(paramPacket);
    binding = paramWSBinding;
    defaultJaxbContext = paramBindingContext;
  }
  
  public LogicalMessage getMessage()
  {
    if (lm == null) {
      lm = new LogicalMessageImpl(defaultJaxbContext, packet);
    }
    return lm;
  }
  
  void setPacketMessage(Message paramMessage)
  {
    if (paramMessage != null)
    {
      packet.setMessage(paramMessage);
      lm = null;
    }
  }
  
  protected void updateMessage()
  {
    if (lm != null)
    {
      if (lm.isPayloadModifed())
      {
        Message localMessage1 = packet.getMessage();
        Message localMessage2 = lm.getMessage(localMessage1.getHeaders(), localMessage1.getAttachments(), binding);
        packet.setMessage(localMessage2);
      }
      lm = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\LogicalMessageContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */