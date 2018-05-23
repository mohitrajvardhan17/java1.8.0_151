package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLMessageImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLMessage
{
  private final QName name;
  private final ArrayList<EditableWSDLPart> parts;
  
  public WSDLMessageImpl(XMLStreamReader paramXMLStreamReader, QName paramQName)
  {
    super(paramXMLStreamReader);
    name = paramQName;
    parts = new ArrayList();
  }
  
  public QName getName()
  {
    return name;
  }
  
  public void add(EditableWSDLPart paramEditableWSDLPart)
  {
    parts.add(paramEditableWSDLPart);
  }
  
  public Iterable<EditableWSDLPart> parts()
  {
    return parts;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */