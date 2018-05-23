package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;

final class MessageProviderArgumentBuilder
  extends ProviderArgumentsBuilder<Message>
{
  private final SOAPVersion soapVersion;
  
  public MessageProviderArgumentBuilder(SOAPVersion paramSOAPVersion)
  {
    soapVersion = paramSOAPVersion;
  }
  
  public Message getParameter(Packet paramPacket)
  {
    return paramPacket.getMessage();
  }
  
  protected Message getResponseMessage(Message paramMessage)
  {
    return paramMessage;
  }
  
  protected Message getResponseMessage(Exception paramException)
  {
    return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\provider\MessageProviderArgumentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */