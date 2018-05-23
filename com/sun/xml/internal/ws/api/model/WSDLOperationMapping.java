package com.sun.xml.internal.ws.api.model;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import javax.xml.namespace.QName;

public abstract interface WSDLOperationMapping
{
  public abstract WSDLBoundOperation getWSDLBoundOperation();
  
  public abstract JavaMethod getJavaMethod();
  
  public abstract QName getOperationName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\WSDLOperationMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */