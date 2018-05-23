package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import java.security.Principal;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;

public abstract class AbstractWebServiceContext
  implements WSWebServiceContext
{
  private final WSEndpoint endpoint;
  
  public AbstractWebServiceContext(@NotNull WSEndpoint paramWSEndpoint)
  {
    endpoint = paramWSEndpoint;
  }
  
  public MessageContext getMessageContext()
  {
    Packet localPacket = getRequestPacket();
    if (localPacket == null) {
      throw new IllegalStateException("getMessageContext() can only be called while servicing a request");
    }
    return new EndpointMessageContextImpl(localPacket);
  }
  
  public Principal getUserPrincipal()
  {
    Packet localPacket = getRequestPacket();
    if (localPacket == null) {
      throw new IllegalStateException("getUserPrincipal() can only be called while servicing a request");
    }
    return webServiceContextDelegate.getUserPrincipal(localPacket);
  }
  
  public boolean isUserInRole(String paramString)
  {
    Packet localPacket = getRequestPacket();
    if (localPacket == null) {
      throw new IllegalStateException("isUserInRole() can only be called while servicing a request");
    }
    return webServiceContextDelegate.isUserInRole(localPacket, paramString);
  }
  
  public EndpointReference getEndpointReference(Element... paramVarArgs)
  {
    return getEndpointReference(W3CEndpointReference.class, paramVarArgs);
  }
  
  public <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, Element... paramVarArgs)
  {
    Packet localPacket = getRequestPacket();
    if (localPacket == null) {
      throw new IllegalStateException("getEndpointReference() can only be called while servicing a request");
    }
    String str1 = webServiceContextDelegate.getEPRAddress(localPacket, endpoint);
    String str2 = null;
    if (endpoint.getServiceDefinition() != null) {
      str2 = webServiceContextDelegate.getWSDLAddress(localPacket, endpoint);
    }
    return (EndpointReference)paramClass.cast(endpoint.getEndpointReference(paramClass, str1, str2, paramVarArgs));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\AbstractWebServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */