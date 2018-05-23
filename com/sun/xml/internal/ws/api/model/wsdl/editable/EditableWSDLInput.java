package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;

public abstract interface EditableWSDLInput
  extends WSDLInput
{
  public abstract EditableWSDLMessage getMessage();
  
  @NotNull
  public abstract EditableWSDLOperation getOperation();
  
  public abstract void setAction(String paramString);
  
  public abstract void setDefaultAction(boolean paramBoolean);
  
  public abstract void freeze(EditableWSDLModel paramEditableWSDLModel);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */