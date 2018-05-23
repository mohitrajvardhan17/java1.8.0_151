package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import javax.jws.WebParam.Mode;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.namespace.QName;

public abstract interface WSDLBoundPortType
  extends WSDLFeaturedObject, WSDLExtensible
{
  public abstract QName getName();
  
  @NotNull
  public abstract WSDLModel getOwner();
  
  public abstract WSDLBoundOperation get(QName paramQName);
  
  public abstract QName getPortTypeName();
  
  public abstract WSDLPortType getPortType();
  
  public abstract Iterable<? extends WSDLBoundOperation> getBindingOperations();
  
  @NotNull
  public abstract SOAPBinding.Style getStyle();
  
  public abstract BindingID getBindingId();
  
  @Nullable
  public abstract WSDLBoundOperation getOperation(String paramString1, String paramString2);
  
  public abstract ParameterBinding getBinding(QName paramQName, String paramString, WebParam.Mode paramMode);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLBoundPortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */