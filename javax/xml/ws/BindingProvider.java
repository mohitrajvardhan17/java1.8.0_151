package javax.xml.ws;

import java.util.Map;

public abstract interface BindingProvider
{
  public static final String USERNAME_PROPERTY = "javax.xml.ws.security.auth.username";
  public static final String PASSWORD_PROPERTY = "javax.xml.ws.security.auth.password";
  public static final String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.ws.service.endpoint.address";
  public static final String SESSION_MAINTAIN_PROPERTY = "javax.xml.ws.session.maintain";
  public static final String SOAPACTION_USE_PROPERTY = "javax.xml.ws.soap.http.soapaction.use";
  public static final String SOAPACTION_URI_PROPERTY = "javax.xml.ws.soap.http.soapaction.uri";
  
  public abstract Map<String, Object> getRequestContext();
  
  public abstract Map<String, Object> getResponseContext();
  
  public abstract Binding getBinding();
  
  public abstract EndpointReference getEndpointReference();
  
  public abstract <T extends EndpointReference> T getEndpointReference(Class<T> paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\BindingProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */