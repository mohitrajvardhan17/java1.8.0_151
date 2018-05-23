package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;

public abstract interface EditableWSDLPart
  extends WSDLPart
{
  public abstract void setBinding(ParameterBinding paramParameterBinding);
  
  public abstract void setIndex(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */