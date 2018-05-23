package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public abstract interface WSDLInput
  extends WSDLObject, WSDLExtensible
{
  public abstract String getName();
  
  public abstract WSDLMessage getMessage();
  
  public abstract String getAction();
  
  @NotNull
  public abstract WSDLOperation getOperation();
  
  @NotNull
  public abstract QName getQName();
  
  public abstract boolean isDefaultAction();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */