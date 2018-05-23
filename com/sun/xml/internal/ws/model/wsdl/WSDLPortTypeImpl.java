package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPortTypeImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLPortType
{
  private QName name;
  private final Map<String, EditableWSDLOperation> portTypeOperations;
  private EditableWSDLModel owner;
  
  public WSDLPortTypeImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLModel paramEditableWSDLModel, QName paramQName)
  {
    super(paramXMLStreamReader);
    name = paramQName;
    owner = paramEditableWSDLModel;
    portTypeOperations = new Hashtable();
  }
  
  public QName getName()
  {
    return name;
  }
  
  public EditableWSDLOperation get(String paramString)
  {
    return (EditableWSDLOperation)portTypeOperations.get(paramString);
  }
  
  public Iterable<EditableWSDLOperation> getOperations()
  {
    return portTypeOperations.values();
  }
  
  public void put(String paramString, EditableWSDLOperation paramEditableWSDLOperation)
  {
    portTypeOperations.put(paramString, paramEditableWSDLOperation);
  }
  
  EditableWSDLModel getOwner()
  {
    return owner;
  }
  
  public void freeze()
  {
    Iterator localIterator = portTypeOperations.values().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLOperation localEditableWSDLOperation = (EditableWSDLOperation)localIterator.next();
      localEditableWSDLOperation.freeze(owner);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLPortTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */