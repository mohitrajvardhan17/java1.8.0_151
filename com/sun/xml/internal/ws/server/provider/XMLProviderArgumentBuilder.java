package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage.MessageDataSource;
import com.sun.xml.internal.ws.resources.ServerMessages;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPException;

abstract class XMLProviderArgumentBuilder<T>
  extends ProviderArgumentsBuilder<T>
{
  XMLProviderArgumentBuilder() {}
  
  protected Packet getResponse(Packet paramPacket, Exception paramException, WSDLPort paramWSDLPort, WSBinding paramWSBinding)
  {
    Packet localPacket = super.getResponse(paramPacket, paramException, paramWSDLPort, paramWSBinding);
    if (((paramException instanceof HTTPException)) && (localPacket.supports("javax.xml.ws.http.response.code"))) {
      localPacket.put("javax.xml.ws.http.response.code", Integer.valueOf(((HTTPException)paramException).getStatusCode()));
    }
    return localPacket;
  }
  
  static XMLProviderArgumentBuilder createBuilder(ProviderEndpointModel paramProviderEndpointModel, WSBinding paramWSBinding)
  {
    if (mode == Service.Mode.PAYLOAD) {
      return new PayloadSource(null);
    }
    if (datatype == Source.class) {
      return new PayloadSource(null);
    }
    if (datatype == DataSource.class) {
      return new DataSourceParameter(paramWSBinding);
    }
    throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(implClass, datatype));
  }
  
  private static final class DataSourceParameter
    extends XMLProviderArgumentBuilder<DataSource>
  {
    private final WSBinding binding;
    
    DataSourceParameter(WSBinding paramWSBinding)
    {
      binding = paramWSBinding;
    }
    
    public DataSource getParameter(Packet paramPacket)
    {
      Message localMessage = paramPacket.getInternalMessage();
      return (localMessage instanceof XMLMessage.MessageDataSource) ? ((XMLMessage.MessageDataSource)localMessage).getDataSource() : XMLMessage.getDataSource(localMessage, binding.getFeatures());
    }
    
    public Message getResponseMessage(DataSource paramDataSource)
    {
      return XMLMessage.create(paramDataSource, binding.getFeatures());
    }
    
    protected Message getResponseMessage(Exception paramException)
    {
      return XMLMessage.create(paramException);
    }
  }
  
  private static final class PayloadSource
    extends XMLProviderArgumentBuilder<Source>
  {
    private PayloadSource() {}
    
    public Source getParameter(Packet paramPacket)
    {
      return paramPacket.getMessage().readPayloadAsSource();
    }
    
    public Message getResponseMessage(Source paramSource)
    {
      return Messages.createUsingPayload(paramSource, SOAPVersion.SOAP_11);
    }
    
    protected Message getResponseMessage(Exception paramException)
    {
      return XMLMessage.create(paramException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\provider\XMLProviderArgumentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */