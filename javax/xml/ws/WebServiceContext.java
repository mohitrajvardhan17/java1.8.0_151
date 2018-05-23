package javax.xml.ws;

import java.security.Principal;
import javax.xml.ws.handler.MessageContext;
import org.w3c.dom.Element;

public abstract interface WebServiceContext
{
  public abstract MessageContext getMessageContext();
  
  public abstract Principal getUserPrincipal();
  
  public abstract boolean isUserInRole(String paramString);
  
  public abstract EndpointReference getEndpointReference(Element... paramVarArgs);
  
  public abstract <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, Element... paramVarArgs);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\WebServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */