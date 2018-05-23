package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public abstract interface WSDLPortType
  extends WSDLObject, WSDLExtensible
{
  public abstract QName getName();
  
  public abstract WSDLOperation get(String paramString);
  
  public abstract Iterable<? extends WSDLOperation> getOperations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLPortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */