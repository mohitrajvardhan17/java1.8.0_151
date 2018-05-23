package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class CompositeStructureBeanInfo
  extends JaxBeanInfo<CompositeStructure>
{
  public CompositeStructureBeanInfo(JAXBContextImpl paramJAXBContextImpl)
  {
    super(paramJAXBContextImpl, null, CompositeStructure.class, false, true, false);
  }
  
  public String getElementNamespaceURI(CompositeStructure paramCompositeStructure)
  {
    throw new UnsupportedOperationException();
  }
  
  public String getElementLocalName(CompositeStructure paramCompositeStructure)
  {
    throw new UnsupportedOperationException();
  }
  
  public CompositeStructure createInstance(UnmarshallingContext paramUnmarshallingContext)
    throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean reset(CompositeStructure paramCompositeStructure, UnmarshallingContext paramUnmarshallingContext)
    throws SAXException
  {
    throw new UnsupportedOperationException();
  }
  
  public String getId(CompositeStructure paramCompositeStructure, XMLSerializer paramXMLSerializer)
    throws SAXException
  {
    return null;
  }
  
  public Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }
  
  public void serializeRoot(CompositeStructure paramCompositeStructure, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    paramXMLSerializer.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { paramCompositeStructure.getClass().getName() }), null, null));
  }
  
  public void serializeURIs(CompositeStructure paramCompositeStructure, XMLSerializer paramXMLSerializer)
    throws SAXException
  {}
  
  public void serializeAttributes(CompositeStructure paramCompositeStructure, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {}
  
  public void serializeBody(CompositeStructure paramCompositeStructure, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    int i = bridges.length;
    for (int j = 0; j < i; j++)
    {
      Object localObject = values[j];
      InternalBridge localInternalBridge = (InternalBridge)bridges[j];
      localInternalBridge.marshal(localObject, paramXMLSerializer);
    }
  }
  
  public Transducer<CompositeStructure> getTransducer()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\CompositeStructureBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */