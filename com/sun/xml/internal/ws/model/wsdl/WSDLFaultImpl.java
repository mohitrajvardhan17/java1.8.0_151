package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLFaultImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLFault
{
  private final String name;
  private final QName messageName;
  private EditableWSDLMessage message;
  private EditableWSDLOperation operation;
  private String action = "";
  private boolean defaultAction = true;
  
  public WSDLFaultImpl(XMLStreamReader paramXMLStreamReader, String paramString, QName paramQName, EditableWSDLOperation paramEditableWSDLOperation)
  {
    super(paramXMLStreamReader);
    name = paramString;
    messageName = paramQName;
    operation = paramEditableWSDLOperation;
  }
  
  public String getName()
  {
    return name;
  }
  
  public EditableWSDLMessage getMessage()
  {
    return message;
  }
  
  @NotNull
  public EditableWSDLOperation getOperation()
  {
    return operation;
  }
  
  @NotNull
  public QName getQName()
  {
    return new QName(operation.getName().getNamespaceURI(), name);
  }
  
  @NotNull
  public String getAction()
  {
    return action;
  }
  
  public void setAction(String paramString)
  {
    action = paramString;
  }
  
  public boolean isDefaultAction()
  {
    return defaultAction;
  }
  
  public void setDefaultAction(boolean paramBoolean)
  {
    defaultAction = paramBoolean;
  }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel)
  {
    message = paramEditableWSDLModel.getMessage(messageName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLFaultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */