package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ArrayElementLeafProperty<BeanT, ListT, ItemT>
  extends ArrayElementProperty<BeanT, ListT, ItemT>
{
  private final Transducer<ItemT> xducer;
  
  public ArrayElementLeafProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
    assert (paramRuntimeElementPropertyInfo.getTypes().size() == 1);
    xducer = ((RuntimeTypeRef)paramRuntimeElementPropertyInfo.getTypes().get(0)).getTransducer();
    assert (xducer != null);
  }
  
  public void serializeItem(JaxBeanInfo paramJaxBeanInfo, ItemT paramItemT, XMLSerializer paramXMLSerializer)
    throws SAXException, AccessorException, IOException, XMLStreamException
  {
    xducer.declareNamespace(paramItemT, paramXMLSerializer);
    paramXMLSerializer.endNamespaceDecls(paramItemT);
    paramXMLSerializer.endAttributes();
    xducer.writeText(paramXMLSerializer, paramItemT, fieldName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayElementLeafProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */