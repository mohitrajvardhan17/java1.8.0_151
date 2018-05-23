package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class WrapperBridge<T>
  implements XMLBridge<T>
{
  private JAXBRIContextWrapper parent;
  private Bridge<T> bridge;
  
  public WrapperBridge(JAXBRIContextWrapper paramJAXBRIContextWrapper, Bridge<T> paramBridge)
  {
    parent = paramJAXBRIContextWrapper;
    bridge = paramBridge;
  }
  
  public BindingContext context()
  {
    return parent;
  }
  
  public boolean equals(Object paramObject)
  {
    return bridge.equals(paramObject);
  }
  
  public TypeInfo getTypeInfo()
  {
    return parent.typeInfo(bridge.getTypeReference());
  }
  
  public int hashCode()
  {
    return bridge.hashCode();
  }
  
  static CompositeStructure convert(Object paramObject)
  {
    WrapperComposite localWrapperComposite = (WrapperComposite)paramObject;
    CompositeStructure localCompositeStructure = new CompositeStructure();
    values = values;
    bridges = new Bridge[bridges.length];
    for (int i = 0; i < bridges.length; i++) {
      bridges[i] = ((BridgeWrapper)bridges[i]).getBridge();
    }
    return localCompositeStructure;
  }
  
  public final void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    bridge.marshal(convert(paramT), paramContentHandler, paramAttachmentMarshaller);
  }
  
  public void marshal(T paramT, Node paramNode)
    throws JAXBException
  {
    throw new UnsupportedOperationException();
  }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    bridge.marshal(convert(paramT), paramOutputStream, paramNamespaceContext, paramAttachmentMarshaller);
  }
  
  public final void marshal(T paramT, Result paramResult)
    throws JAXBException
  {
    throw new UnsupportedOperationException();
  }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    bridge.marshal(convert(paramT), paramXMLStreamWriter, paramAttachmentMarshaller);
  }
  
  public String toString()
  {
    return BridgeWrapper.class.getName() + " : " + bridge.toString();
  }
  
  public final T unmarshal(InputStream paramInputStream)
    throws JAXBException
  {
    throw new UnsupportedOperationException();
  }
  
  public final T unmarshal(Node paramNode, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    throw new UnsupportedOperationException();
  }
  
  public final T unmarshal(Source paramSource, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    throw new UnsupportedOperationException();
  }
  
  public final T unmarshal(XMLStreamReader paramXMLStreamReader, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean supportOutputStream()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\db\glassfish\WrapperBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */