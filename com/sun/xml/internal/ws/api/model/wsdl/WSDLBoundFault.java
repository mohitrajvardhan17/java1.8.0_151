package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public abstract interface WSDLBoundFault
  extends WSDLObject, WSDLExtensible
{
  @NotNull
  public abstract String getName();
  
  @Nullable
  public abstract QName getQName();
  
  @Nullable
  public abstract WSDLFault getFault();
  
  @NotNull
  public abstract WSDLBoundOperation getBoundOperation();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLBoundFault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */