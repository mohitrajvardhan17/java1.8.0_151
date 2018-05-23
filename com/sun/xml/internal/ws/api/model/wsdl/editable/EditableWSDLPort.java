package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;

public abstract interface EditableWSDLPort
  extends WSDLPort
{
  @NotNull
  public abstract EditableWSDLBoundPortType getBinding();
  
  @NotNull
  public abstract EditableWSDLService getOwner();
  
  public abstract void setAddress(EndpointAddress paramEndpointAddress);
  
  public abstract void setEPR(@NotNull WSEndpointReference paramWSEndpointReference);
  
  public abstract void freeze(EditableWSDLModel paramEditableWSDLModel);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLPort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */