package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage.MessageDataSource;
import javax.activation.DataSource;
import javax.xml.namespace.QName;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;

public class DataSourceDispatch
  extends DispatchImpl<DataSource>
{
  @Deprecated
  public DataSourceDispatch(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
  }
  
  public DataSourceDispatch(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
  }
  
  Packet createPacket(DataSource paramDataSource)
  {
    switch (mode)
    {
    case PAYLOAD: 
      throw new IllegalArgumentException("DataSource use is not allowed in Service.Mode.PAYLOAD\n");
    case MESSAGE: 
      return new Packet(XMLMessage.create(paramDataSource, binding.getFeatures()));
    }
    throw new WebServiceException("Unrecognized message mode");
  }
  
  DataSource toReturnValue(Packet paramPacket)
  {
    Message localMessage = paramPacket.getInternalMessage();
    return (localMessage instanceof XMLMessage.MessageDataSource) ? ((XMLMessage.MessageDataSource)localMessage).getDataSource() : XMLMessage.getDataSource(localMessage, binding.getFeatures());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\dispatch\DataSourceDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */