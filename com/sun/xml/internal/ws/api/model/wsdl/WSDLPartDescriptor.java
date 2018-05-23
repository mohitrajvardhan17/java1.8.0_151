package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public abstract interface WSDLPartDescriptor
  extends WSDLObject
{
  public abstract QName name();
  
  public abstract WSDLDescriptorKind type();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLPartDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */