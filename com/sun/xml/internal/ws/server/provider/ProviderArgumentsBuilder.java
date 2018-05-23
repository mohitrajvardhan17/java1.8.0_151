package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import javax.xml.ws.soap.SOAPBinding;

public abstract class ProviderArgumentsBuilder<T>
{
  public ProviderArgumentsBuilder() {}
  
  protected abstract Message getResponseMessage(Exception paramException);
  
  protected Packet getResponse(Packet paramPacket, Exception paramException, WSDLPort paramWSDLPort, WSBinding paramWSBinding)
  {
    Message localMessage = getResponseMessage(paramException);
    Packet localPacket = paramPacket.createServerResponse(localMessage, paramWSDLPort, null, paramWSBinding);
    return localPacket;
  }
  
  public abstract T getParameter(Packet paramPacket);
  
  protected abstract Message getResponseMessage(T paramT);
  
  protected Packet getResponse(Packet paramPacket, @Nullable T paramT, WSDLPort paramWSDLPort, WSBinding paramWSBinding)
  {
    Message localMessage = null;
    if (paramT != null) {
      localMessage = getResponseMessage(paramT);
    }
    Packet localPacket = paramPacket.createServerResponse(localMessage, paramWSDLPort, null, paramWSBinding);
    return localPacket;
  }
  
  public static ProviderArgumentsBuilder<?> create(ProviderEndpointModel paramProviderEndpointModel, WSBinding paramWSBinding)
  {
    if (datatype == Packet.class) {
      return new PacketProviderArgumentsBuilder(paramWSBinding.getSOAPVersion());
    }
    return (paramWSBinding instanceof SOAPBinding) ? SOAPProviderArgumentBuilder.create(paramProviderEndpointModel, paramWSBinding.getSOAPVersion()) : XMLProviderArgumentBuilder.createBuilder(paramProviderEndpointModel, paramWSBinding);
  }
  
  private static class PacketProviderArgumentsBuilder
    extends ProviderArgumentsBuilder<Packet>
  {
    private final SOAPVersion soapVersion;
    
    public PacketProviderArgumentsBuilder(SOAPVersion paramSOAPVersion)
    {
      soapVersion = paramSOAPVersion;
    }
    
    protected Message getResponseMessage(Exception paramException)
    {
      return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, paramException);
    }
    
    public Packet getParameter(Packet paramPacket)
    {
      return paramPacket;
    }
    
    protected Message getResponseMessage(Packet paramPacket)
    {
      throw new IllegalStateException();
    }
    
    protected Packet getResponse(Packet paramPacket1, @Nullable Packet paramPacket2, WSDLPort paramWSDLPort, WSBinding paramWSBinding)
    {
      return paramPacket2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\provider\ProviderArgumentsBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */