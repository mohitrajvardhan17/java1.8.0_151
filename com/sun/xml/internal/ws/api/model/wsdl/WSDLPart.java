package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.xml.internal.ws.api.model.ParameterBinding;

public abstract interface WSDLPart
  extends WSDLObject
{
  public abstract String getName();
  
  public abstract ParameterBinding getBinding();
  
  public abstract int getIndex();
  
  public abstract WSDLPartDescriptor getDescriptor();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */