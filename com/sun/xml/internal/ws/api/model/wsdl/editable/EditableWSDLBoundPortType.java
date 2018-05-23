package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.namespace.QName;

public abstract interface EditableWSDLBoundPortType
  extends WSDLBoundPortType
{
  @NotNull
  public abstract EditableWSDLModel getOwner();
  
  public abstract EditableWSDLBoundOperation get(QName paramQName);
  
  public abstract EditableWSDLPortType getPortType();
  
  public abstract Iterable<? extends EditableWSDLBoundOperation> getBindingOperations();
  
  @Nullable
  public abstract EditableWSDLBoundOperation getOperation(String paramString1, String paramString2);
  
  public abstract void put(QName paramQName, EditableWSDLBoundOperation paramEditableWSDLBoundOperation);
  
  public abstract void setBindingId(BindingID paramBindingID);
  
  public abstract void setStyle(SOAPBinding.Style paramStyle);
  
  public abstract void freeze();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLBoundPortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */