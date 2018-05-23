package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XMLStreamWriterOutput;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

final class BridgeImpl<T>
  extends InternalBridge<T>
{
  private final Name tagName;
  private final JaxBeanInfo<T> bi;
  private final TypeReference typeRef;
  
  public BridgeImpl(JAXBContextImpl paramJAXBContextImpl, Name paramName, JaxBeanInfo<T> paramJaxBeanInfo, TypeReference paramTypeReference)
  {
    super(paramJAXBContextImpl);
    tagName = paramName;
    bi = paramJaxBeanInfo;
    typeRef = paramTypeReference;
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    MarshallerImpl localMarshallerImpl = (MarshallerImpl)paramMarshaller;
    localMarshallerImpl.write(tagName, bi, paramT, XMLStreamWriterOutput.create(paramXMLStreamWriter, context), new StAXPostInitAction(paramXMLStreamWriter, serializer));
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    MarshallerImpl localMarshallerImpl = (MarshallerImpl)paramMarshaller;
    StAXPostInitAction localStAXPostInitAction = null;
    if (paramNamespaceContext != null) {
      localStAXPostInitAction = new StAXPostInitAction(paramNamespaceContext, serializer);
    }
    localMarshallerImpl.write(tagName, bi, paramT, localMarshallerImpl.createWriter(paramOutputStream), localStAXPostInitAction);
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, Node paramNode)
    throws JAXBException
  {
    MarshallerImpl localMarshallerImpl = (MarshallerImpl)paramMarshaller;
    localMarshallerImpl.write(tagName, bi, paramT, new SAXOutput(new SAX2DOMEx(paramNode)), new DomPostInitAction(paramNode, serializer));
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, ContentHandler paramContentHandler)
    throws JAXBException
  {
    MarshallerImpl localMarshallerImpl = (MarshallerImpl)paramMarshaller;
    localMarshallerImpl.write(tagName, bi, paramT, new SAXOutput(paramContentHandler), null);
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, Result paramResult)
    throws JAXBException
  {
    MarshallerImpl localMarshallerImpl = (MarshallerImpl)paramMarshaller;
    localMarshallerImpl.write(tagName, bi, paramT, localMarshallerImpl.createXmlOutput(paramResult), localMarshallerImpl.createPostInitAction(paramResult));
  }
  
  @NotNull
  public T unmarshal(Unmarshaller paramUnmarshaller, XMLStreamReader paramXMLStreamReader)
    throws JAXBException
  {
    UnmarshallerImpl localUnmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    return (T)((JAXBElement)localUnmarshallerImpl.unmarshal0(paramXMLStreamReader, bi)).getValue();
  }
  
  @NotNull
  public T unmarshal(Unmarshaller paramUnmarshaller, Source paramSource)
    throws JAXBException
  {
    UnmarshallerImpl localUnmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    return (T)((JAXBElement)localUnmarshallerImpl.unmarshal0(paramSource, bi)).getValue();
  }
  
  @NotNull
  public T unmarshal(Unmarshaller paramUnmarshaller, InputStream paramInputStream)
    throws JAXBException
  {
    UnmarshallerImpl localUnmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    return (T)((JAXBElement)localUnmarshallerImpl.unmarshal0(paramInputStream, bi)).getValue();
  }
  
  @NotNull
  public T unmarshal(Unmarshaller paramUnmarshaller, Node paramNode)
    throws JAXBException
  {
    UnmarshallerImpl localUnmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    return (T)((JAXBElement)localUnmarshallerImpl.unmarshal0(paramNode, bi)).getValue();
  }
  
  public TypeReference getTypeReference()
  {
    return typeRef;
  }
  
  public void marshal(T paramT, XMLSerializer paramXMLSerializer)
    throws IOException, SAXException, XMLStreamException
  {
    paramXMLSerializer.startElement(tagName, null);
    if (paramT == null) {
      paramXMLSerializer.writeXsiNilTrue();
    } else {
      paramXMLSerializer.childAsXsiType(paramT, null, bi, false);
    }
    paramXMLSerializer.endElement();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\BridgeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */