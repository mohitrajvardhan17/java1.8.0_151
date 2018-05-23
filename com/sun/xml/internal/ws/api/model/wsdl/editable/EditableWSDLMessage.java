package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;

public abstract interface EditableWSDLMessage
  extends WSDLMessage
{
  public abstract Iterable<? extends EditableWSDLPart> parts();
  
  public abstract void add(EditableWSDLPart paramEditableWSDLPart);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */