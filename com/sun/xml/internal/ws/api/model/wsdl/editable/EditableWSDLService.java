package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import javax.xml.namespace.QName;

public abstract interface EditableWSDLService
  extends WSDLService
{
  @NotNull
  public abstract EditableWSDLModel getParent();
  
  public abstract EditableWSDLPort get(QName paramQName);
  
  public abstract EditableWSDLPort getFirstPort();
  
  @Nullable
  public abstract EditableWSDLPort getMatchingPort(QName paramQName);
  
  public abstract Iterable<? extends EditableWSDLPort> getPorts();
  
  public abstract void put(QName paramQName, EditableWSDLPort paramEditableWSDLPort);
  
  public abstract void freeze(EditableWSDLModel paramEditableWSDLModel);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */