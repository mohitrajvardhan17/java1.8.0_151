package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.server.Container;

public abstract interface WSDLParserExtensionContext
{
  public abstract boolean isClientSide();
  
  public abstract EditableWSDLModel getWSDLModel();
  
  @NotNull
  public abstract Container getContainer();
  
  @NotNull
  public abstract PolicyResolver getPolicyResolver();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\wsdl\parser\WSDLParserExtensionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */