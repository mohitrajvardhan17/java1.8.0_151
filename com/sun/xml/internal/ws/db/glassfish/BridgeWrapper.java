package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class BridgeWrapper<T>
  implements XMLBridge<T>
{
  private JAXBRIContextWrapper parent;
  private Bridge<T> bridge;
  
  public BridgeWrapper(JAXBRIContextWrapper paramJAXBRIContextWrapper, Bridge<T> paramBridge)
  {
    parent = paramJAXBRIContextWrapper;
    bridge = paramBridge;
  }
  
  public BindingContext context()
  {
    return parent;
  }
  
  Bridge getBridge()
  {
    return bridge;
  }
  
  public boolean equals(Object paramObject)
  {
    return bridge.equals(paramObject);
  }
  
  public JAXBRIContext getContext()
  {
    return bridge.getContext();
  }
  
  public TypeInfo getTypeInfo()
  {
    return parent.typeInfo(bridge.getTypeReference());
  }
  
  public int hashCode()
  {
    return bridge.hashCode();
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, ContentHandler paramContentHandler)
    throws JAXBException
  {
    bridge.marshal(paramMarshaller, paramT, paramContentHandler);
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, Node paramNode)
    throws JAXBException
  {
    bridge.marshal(paramMarshaller, paramT, paramNode);
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    bridge.marshal(paramMarshaller, paramT, paramOutputStream, paramNamespaceContext);
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, Result paramResult)
    throws JAXBException
  {
    bridge.marshal(paramMarshaller, paramT, paramResult);
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    bridge.marshal(paramMarshaller, paramT, paramXMLStreamWriter);
  }
  
  public final void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    bridge.marshal(paramT, paramContentHandler, paramAttachmentMarshaller);
  }
  
  public void marshal(T paramT, ContentHandler paramContentHandler)
    throws JAXBException
  {
    bridge.marshal(paramT, paramContentHandler);
  }
  
  public void marshal(T paramT, Node paramNode)
    throws JAXBException
  {
    bridge.marshal(paramT, paramNode);
  }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    bridge.marshal(paramT, paramOutputStream, paramNamespaceContext, paramAttachmentMarshaller);
  }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    bridge.marshal(paramT, paramOutputStream, paramNamespaceContext);
  }
  
  public final void marshal(T paramT, Result paramResult)
    throws JAXBException
  {
    bridge.marshal(paramT, paramResult);
  }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    bridge.marshal(paramT, paramXMLStreamWriter, paramAttachmentMarshaller);
  }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    bridge.marshal(paramT, paramXMLStreamWriter);
  }
  
  public String toString()
  {
    return BridgeWrapper.class.getName() + " : " + bridge.toString();
  }
  
  public final T unmarshal(InputStream paramInputStream)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramInputStream);
  }
  
  public final T unmarshal(Node paramNode, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramNode, paramAttachmentUnmarshaller);
  }
  
  public final T unmarshal(Node paramNode)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramNode);
  }
  
  public final T unmarshal(Source paramSource, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramSource, paramAttachmentUnmarshaller);
  }
  
  public final T unmarshal(Source paramSource)
    throws DatabindingException
  {
    try
    {
      return (T)bridge.unmarshal(paramSource);
    }
    catch (JAXBException localJAXBException)
    {
      throw new DatabindingException(localJAXBException);
    }
  }
  
  public T unmarshal(Unmarshaller paramUnmarshaller, InputStream paramInputStream)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramUnmarshaller, paramInputStream);
  }
  
  public T unmarshal(Unmarshaller paramUnmarshaller, Node paramNode)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramUnmarshaller, paramNode);
  }
  
  public T unmarshal(Unmarshaller paramUnmarshaller, Source paramSource)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramUnmarshaller, paramSource);
  }
  
  public T unmarshal(Unmarshaller paramUnmarshaller, XMLStreamReader paramXMLStreamReader)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramUnmarshaller, paramXMLStreamReader);
  }
  
  public final T unmarshal(XMLStreamReader paramXMLStreamReader, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramXMLStreamReader, paramAttachmentUnmarshaller);
  }
  
  public final T unmarshal(XMLStreamReader paramXMLStreamReader)
    throws JAXBException
  {
    return (T)bridge.unmarshal(paramXMLStreamReader);
  }
  
  public boolean supportOutputStream()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\db\glassfish\BridgeWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */