package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import javax.xml.namespace.QName;

public abstract interface EditableWSDLOperation
  extends WSDLOperation
{
  @NotNull
  public abstract EditableWSDLInput getInput();
  
  public abstract void setInput(EditableWSDLInput paramEditableWSDLInput);
  
  @Nullable
  public abstract EditableWSDLOutput getOutput();
  
  public abstract void setOutput(EditableWSDLOutput paramEditableWSDLOutput);
  
  public abstract Iterable<? extends EditableWSDLFault> getFaults();
  
  public abstract void addFault(EditableWSDLFault paramEditableWSDLFault);
  
  @Nullable
  public abstract EditableWSDLFault getFault(QName paramQName);
  
  public abstract void setParameterOrder(String paramString);
  
  public abstract void freeze(EditableWSDLModel paramEditableWSDLModel);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */