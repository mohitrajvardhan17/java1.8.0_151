package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLOutputImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLOutput
{
  private String name;
  private QName messageName;
  private EditableWSDLOperation operation;
  private EditableWSDLMessage message;
  private String action;
  private boolean defaultAction = true;
  
  public WSDLOutputImpl(XMLStreamReader paramXMLStreamReader, String paramString, QName paramQName, EditableWSDLOperation paramEditableWSDLOperation)
  {
    super(paramXMLStreamReader);
    name = paramString;
    messageName = paramQName;
    operation = paramEditableWSDLOperation;
  }
  
  public String getName()
  {
    return name == null ? operation.getName().getLocalPart() + "Response" : name;
  }
  
  public EditableWSDLMessage getMessage()
  {
    return message;
  }
  
  public String getAction()
  {
    return action;
  }
  
  public boolean isDefaultAction()
  {
    return defaultAction;
  }
  
  public void setDefaultAction(boolean paramBoolean)
  {
    defaultAction = paramBoolean;
  }
  
  @NotNull
  public EditableWSDLOperation getOperation()
  {
    return operation;
  }
  
  @NotNull
  public QName getQName()
  {
    return new QName(operation.getName().getNamespaceURI(), getName());
  }
  
  public void setAction(String paramString)
  {
    action = paramString;
  }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel)
  {
    message = paramEditableWSDLModel.getMessage(messageName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLOutputImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */