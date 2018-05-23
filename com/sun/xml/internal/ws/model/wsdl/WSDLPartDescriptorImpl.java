package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLDescriptorKind;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPartDescriptorImpl
  extends AbstractObjectImpl
  implements WSDLPartDescriptor
{
  private QName name;
  private WSDLDescriptorKind type;
  
  public WSDLPartDescriptorImpl(XMLStreamReader paramXMLStreamReader, QName paramQName, WSDLDescriptorKind paramWSDLDescriptorKind)
  {
    super(paramXMLStreamReader);
    name = paramQName;
    type = paramWSDLDescriptorKind;
  }
  
  public QName name()
  {
    return name;
  }
  
  public WSDLDescriptorKind type()
  {
    return type;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLPartDescriptorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */