package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;

final class WSDLParserExtensionContextImpl
  implements WSDLParserExtensionContext
{
  private final boolean isClientSide;
  private final EditableWSDLModel wsdlModel;
  private final Container container;
  private final PolicyResolver policyResolver;
  
  protected WSDLParserExtensionContextImpl(EditableWSDLModel paramEditableWSDLModel, boolean paramBoolean, Container paramContainer, PolicyResolver paramPolicyResolver)
  {
    wsdlModel = paramEditableWSDLModel;
    isClientSide = paramBoolean;
    container = paramContainer;
    policyResolver = paramPolicyResolver;
  }
  
  public boolean isClientSide()
  {
    return isClientSide;
  }
  
  public EditableWSDLModel getWSDLModel()
  {
    return wsdlModel;
  }
  
  public Container getContainer()
  {
    return container;
  }
  
  public PolicyResolver getPolicyResolver()
  {
    return policyResolver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\WSDLParserExtensionContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */