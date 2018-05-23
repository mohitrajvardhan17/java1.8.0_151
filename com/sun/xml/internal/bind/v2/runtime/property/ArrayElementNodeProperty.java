package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ArrayElementNodeProperty<BeanT, ListT, ItemT>
  extends ArrayElementProperty<BeanT, ListT, ItemT>
{
  public ArrayElementNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
  }
  
  public void serializeItem(JaxBeanInfo paramJaxBeanInfo, ItemT paramItemT, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    if (paramItemT == null) {
      paramXMLSerializer.writeXsiNilTrue();
    } else {
      paramXMLSerializer.childAsXsiType(paramItemT, fieldName, paramJaxBeanInfo, false);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayElementNodeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */