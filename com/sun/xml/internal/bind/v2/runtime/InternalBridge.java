package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.Bridge;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class InternalBridge<T>
  extends Bridge<T>
{
  protected InternalBridge(JAXBContextImpl paramJAXBContextImpl)
  {
    super(paramJAXBContextImpl);
  }
  
  public JAXBContextImpl getContext()
  {
    return context;
  }
  
  abstract void marshal(T paramT, XMLSerializer paramXMLSerializer)
    throws IOException, SAXException, XMLStreamException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\InternalBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */