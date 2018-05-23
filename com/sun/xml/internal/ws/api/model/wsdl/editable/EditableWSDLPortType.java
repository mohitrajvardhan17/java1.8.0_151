package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;

public abstract interface EditableWSDLPortType
  extends WSDLPortType
{
  public abstract EditableWSDLOperation get(String paramString);
  
  public abstract Iterable<? extends EditableWSDLOperation> getOperations();
  
  public abstract void put(String paramString, EditableWSDLOperation paramEditableWSDLOperation);
  
  public abstract void freeze();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLPortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */