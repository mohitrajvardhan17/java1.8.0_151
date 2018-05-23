package com.sun.xml.internal.ws.binding;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

public final class SOAPBindingImpl
  extends BindingImpl
  implements SOAPBinding
{
  public static final String X_SOAP12HTTP_BINDING = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/";
  private static final String ROLE_NONE = "http://www.w3.org/2003/05/soap-envelope/role/none";
  protected final SOAPVersion soapVersion;
  private Set<QName> portKnownHeaders = Collections.emptySet();
  private Set<QName> bindingUnderstoodHeaders = new HashSet();
  
  SOAPBindingImpl(BindingID paramBindingID)
  {
    this(paramBindingID, EMPTY_FEATURES);
  }
  
  SOAPBindingImpl(BindingID paramBindingID, WebServiceFeature... paramVarArgs)
  {
    super(paramBindingID, paramVarArgs);
    soapVersion = paramBindingID.getSOAPVersion();
    setRoles(new HashSet());
    features.addAll(paramBindingID.createBuiltinFeatureList());
  }
  
  public void setPortKnownHeaders(@NotNull Set<QName> paramSet)
  {
    portKnownHeaders = paramSet;
  }
  
  public boolean understandsHeader(QName paramQName)
  {
    return (serviceMode == Service.Mode.MESSAGE) || (portKnownHeaders.contains(paramQName)) || (bindingUnderstoodHeaders.contains(paramQName));
  }
  
  public void setHandlerChain(List<Handler> paramList)
  {
    setHandlerConfig(new HandlerConfiguration(getHandlerConfig().getRoles(), paramList));
  }
  
  protected void addRequiredRoles(Set<String> paramSet)
  {
    paramSet.addAll(soapVersion.requiredRoles);
  }
  
  public Set<String> getRoles()
  {
    return getHandlerConfig().getRoles();
  }
  
  public void setRoles(Set<String> paramSet)
  {
    if (paramSet == null) {
      paramSet = new HashSet();
    }
    if (paramSet.contains("http://www.w3.org/2003/05/soap-envelope/role/none")) {
      throw new WebServiceException(ClientMessages.INVALID_SOAP_ROLE_NONE());
    }
    addRequiredRoles(paramSet);
    setHandlerConfig(new HandlerConfiguration(paramSet, getHandlerConfig()));
  }
  
  public boolean isMTOMEnabled()
  {
    return isFeatureEnabled(MTOMFeature.class);
  }
  
  public void setMTOMEnabled(boolean paramBoolean)
  {
    features.setMTOMEnabled(paramBoolean);
  }
  
  public SOAPFactory getSOAPFactory()
  {
    return soapVersion.getSOAPFactory();
  }
  
  public MessageFactory getMessageFactory()
  {
    return soapVersion.getMessageFactory();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\binding\SOAPBindingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */