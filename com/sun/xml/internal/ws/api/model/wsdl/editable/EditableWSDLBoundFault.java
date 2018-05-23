package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;

public abstract interface EditableWSDLBoundFault
  extends WSDLBoundFault
{
  @Nullable
  public abstract EditableWSDLFault getFault();
  
  @NotNull
  public abstract EditableWSDLBoundOperation getBoundOperation();
  
  public abstract void freeze(EditableWSDLBoundOperation paramEditableWSDLBoundOperation);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLBoundFault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */