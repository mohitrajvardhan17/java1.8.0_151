package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import javax.xml.namespace.QName;

public abstract interface WSDLPort
  extends WSDLFeaturedObject, WSDLExtensible
{
  public abstract QName getName();
  
  @NotNull
  public abstract WSDLBoundPortType getBinding();
  
  public abstract EndpointAddress getAddress();
  
  @NotNull
  public abstract WSDLService getOwner();
  
  @Nullable
  public abstract WSEndpointReference getEPR();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLPort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */