package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;

abstract class SOAPProviderArgumentBuilder<T>
  extends ProviderArgumentsBuilder<T>
{
  protected final SOAPVersion soapVersion;
  
  private SOAPProviderArgumentBuilder(SOAPVersion paramSOAPVersion)
  {
    soapVersion = paramSOAPVersion;
  }
  
  static ProviderArgumentsBuilder create(ProviderEndpointModel paramProviderEndpointModel, SOAPVersion paramSOAPVersion)
  {
    if (mode == Service.Mode.PAYLOAD) {
      return new PayloadSource(paramSOAPVersion);
    }
    if (datatype == Source.class) {
      return new MessageSource(paramSOAPVersion);
    }
    if (datatype == SOAPMessage.class) {
      return new SOAPMessageParameter(paramSOAPVersion);
    }
    if (datatype == Message.class) {
      return new MessageProviderArgumentBuilder(paramSOAPVersion);
    }
    throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(implClass, datatype));
  }
  
  private static final class MessageSource
    extends SOAPProviderArgumentBuilder<Source>
  {
    MessageSource(SOAPVersion paramSOAPVersion)
    {
      super(null);
    }
    
    public Source getParameter(Packet paramPacket)
    {
      return paramPacket.getMessage().readEnvelopeAsSource();
    }
    
    protected Message getResponseMessage(Source paramSource)
    {
      return Messages.create(paramSource, soapVersion);
    }
    
    protected Message getResponseMessage(Exception paramException)
    {
      return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, paramException);
    }
  }
  
  private static final class PayloadSource
    extends SOAPProviderArgumentBuilder<Source>
  {
    PayloadSource(SOAPVersion paramSOAPVersion)
    {
      super(null);
    }
    
    public Source getParameter(Packet paramPacket)
    {
      return paramPacket.getMessage().readPayloadAsSource();
    }
    
    protected Message getResponseMessage(Source paramSource)
    {
      return Messages.createUsingPayload(paramSource, soapVersion);
    }
    
    protected Message getResponseMessage(Exception paramException)
    {
      return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, paramException);
    }
  }
  
  private static final class SOAPMessageParameter
    extends SOAPProviderArgumentBuilder<SOAPMessage>
  {
    SOAPMessageParameter(SOAPVersion paramSOAPVersion)
    {
      super(null);
    }
    
    public SOAPMessage getParameter(Packet paramPacket)
    {
      try
      {
        return paramPacket.getMessage().readAsSOAPMessage(paramPacket, true);
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    
    protected Message getResponseMessage(SOAPMessage paramSOAPMessage)
    {
      return Messages.create(paramSOAPMessage);
    }
    
    protected Message getResponseMessage(Exception paramException)
    {
      return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, paramException);
    }
    
    protected Packet getResponse(Packet paramPacket, @Nullable SOAPMessage paramSOAPMessage, WSDLPort paramWSDLPort, WSBinding paramWSBinding)
    {
      Packet localPacket = super.getResponse(paramPacket, paramSOAPMessage, paramWSDLPort, paramWSBinding);
      if ((paramSOAPMessage != null) && (localPacket.supports("com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers")))
      {
        MimeHeaders localMimeHeaders = paramSOAPMessage.getMimeHeaders();
        HashMap localHashMap = new HashMap();
        Iterator localIterator = localMimeHeaders.getAllHeaders();
        while (localIterator.hasNext())
        {
          MimeHeader localMimeHeader = (MimeHeader)localIterator.next();
          if (!localMimeHeader.getName().equalsIgnoreCase("SOAPAction"))
          {
            Object localObject = (List)localHashMap.get(localMimeHeader.getName());
            if (localObject == null)
            {
              localObject = new ArrayList();
              localHashMap.put(localMimeHeader.getName(), localObject);
            }
            ((List)localObject).add(localMimeHeader.getValue());
          }
        }
        localPacket.put("com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers", localHashMap);
      }
      return localPacket;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\provider\SOAPProviderArgumentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */