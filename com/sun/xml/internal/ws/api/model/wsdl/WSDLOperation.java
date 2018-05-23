package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public abstract interface WSDLOperation
  extends WSDLObject, WSDLExtensible
{
  @NotNull
  public abstract QName getName();
  
  @NotNull
  public abstract WSDLInput getInput();
  
  @Nullable
  public abstract WSDLOutput getOutput();
  
  public abstract boolean isOneWay();
  
  public abstract Iterable<? extends WSDLFault> getFaults();
  
  @Nullable
  public abstract WSDLFault getFault(QName paramQName);
  
  @NotNull
  public abstract QName getPortTypeName();
  
  public abstract String getParameterOrder();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */