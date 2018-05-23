package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;

final class SOAPSourceDispatch
  extends DispatchImpl<Source>
{
  @Deprecated
  public SOAPSourceDispatch(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
    assert (!isXMLHttp(paramBindingImpl));
  }
  
  public SOAPSourceDispatch(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
    assert (!isXMLHttp(paramBindingImpl));
  }
  
  Source toReturnValue(Packet paramPacket)
  {
    Message localMessage = paramPacket.getMessage();
    switch (mode)
    {
    case PAYLOAD: 
      return localMessage.readPayloadAsSource();
    case MESSAGE: 
      return localMessage.readEnvelopeAsSource();
    }
    throw new WebServiceException("Unrecognized dispatch mode");
  }
  
  Packet createPacket(Source paramSource)
  {
    Object localObject;
    if (paramSource == null) {
      localObject = Messages.createEmpty(soapVersion);
    } else {
      switch (mode)
      {
      case PAYLOAD: 
        localObject = new PayloadSourceMessage(null, paramSource, setOutboundAttachments(), soapVersion);
        break;
      case MESSAGE: 
        localObject = Messages.create(paramSource, soapVersion);
        break;
      default: 
        throw new WebServiceException("Unrecognized message mode");
      }
    }
    return new Packet((Message)localObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\dispatch\SOAPSourceDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */