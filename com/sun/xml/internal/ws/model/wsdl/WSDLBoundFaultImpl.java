package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class WSDLBoundFaultImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLBoundFault
{
  private final String name;
  private EditableWSDLFault fault;
  private EditableWSDLBoundOperation owner;
  
  public WSDLBoundFaultImpl(XMLStreamReader paramXMLStreamReader, String paramString, EditableWSDLBoundOperation paramEditableWSDLBoundOperation)
  {
    super(paramXMLStreamReader);
    name = paramString;
    owner = paramEditableWSDLBoundOperation;
  }
  
  @NotNull
  public String getName()
  {
    return name;
  }
  
  public QName getQName()
  {
    if (owner.getOperation() != null) {
      return new QName(owner.getOperation().getName().getNamespaceURI(), name);
    }
    return null;
  }
  
  public EditableWSDLFault getFault()
  {
    return fault;
  }
  
  @NotNull
  public EditableWSDLBoundOperation getBoundOperation()
  {
    return owner;
  }
  
  public void freeze(EditableWSDLBoundOperation paramEditableWSDLBoundOperation)
  {
    assert (paramEditableWSDLBoundOperation != null);
    EditableWSDLOperation localEditableWSDLOperation = paramEditableWSDLBoundOperation.getOperation();
    if (localEditableWSDLOperation != null)
    {
      Iterator localIterator = localEditableWSDLOperation.getFaults().iterator();
      while (localIterator.hasNext())
      {
        EditableWSDLFault localEditableWSDLFault = (EditableWSDLFault)localIterator.next();
        if (localEditableWSDLFault.getName().equals(name))
        {
          fault = localEditableWSDLFault;
          break;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLBoundFaultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */