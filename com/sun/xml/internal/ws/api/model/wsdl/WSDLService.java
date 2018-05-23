package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public abstract interface WSDLService
  extends WSDLObject, WSDLExtensible
{
  @NotNull
  public abstract WSDLModel getParent();
  
  @NotNull
  public abstract QName getName();
  
  public abstract WSDLPort get(QName paramQName);
  
  public abstract WSDLPort getFirstPort();
  
  @Nullable
  public abstract WSDLPort getMatchingPort(QName paramQName);
  
  public abstract Iterable<? extends WSDLPort> getPorts();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */