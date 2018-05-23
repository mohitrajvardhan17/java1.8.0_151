package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

final class BridgeAdapter<OnWire, InMemory>
  extends InternalBridge<InMemory>
{
  private final InternalBridge<OnWire> core;
  private final Class<? extends XmlAdapter<OnWire, InMemory>> adapter;
  
  public BridgeAdapter(InternalBridge<OnWire> paramInternalBridge, Class<? extends XmlAdapter<OnWire, InMemory>> paramClass)
  {
    super(paramInternalBridge.getContext());
    core = paramInternalBridge;
    adapter = paramClass;
  }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramXMLStreamWriter);
  }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramOutputStream, paramNamespaceContext);
  }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, Node paramNode)
    throws JAXBException
  {
    core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramNode);
  }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, ContentHandler paramContentHandler)
    throws JAXBException
  {
    core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramContentHandler);
  }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, Result paramResult)
    throws JAXBException
  {
    core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramResult);
  }
  
  private OnWire adaptM(Marshaller paramMarshaller, InMemory paramInMemory)
    throws JAXBException
  {
    XMLSerializer localXMLSerializer = serializer;
    localXMLSerializer.pushCoordinator();
    try
    {
      Object localObject1 = _adaptM(localXMLSerializer, paramInMemory);
      return (OnWire)localObject1;
    }
    finally
    {
      localXMLSerializer.popCoordinator();
    }
  }
  
  private OnWire _adaptM(XMLSerializer paramXMLSerializer, InMemory paramInMemory)
    throws MarshalException
  {
    XmlAdapter localXmlAdapter = paramXMLSerializer.getAdapter(adapter);
    try
    {
      return (OnWire)localXmlAdapter.marshal(paramInMemory);
    }
    catch (Exception localException)
    {
      paramXMLSerializer.handleError(localException, paramInMemory, null);
      throw new MarshalException(localException);
    }
  }
  
  @NotNull
  public InMemory unmarshal(Unmarshaller paramUnmarshaller, XMLStreamReader paramXMLStreamReader)
    throws JAXBException
  {
    return (InMemory)adaptU(paramUnmarshaller, core.unmarshal(paramUnmarshaller, paramXMLStreamReader));
  }
  
  @NotNull
  public InMemory unmarshal(Unmarshaller paramUnmarshaller, Source paramSource)
    throws JAXBException
  {
    return (InMemory)adaptU(paramUnmarshaller, core.unmarshal(paramUnmarshaller, paramSource));
  }
  
  @NotNull
  public InMemory unmarshal(Unmarshaller paramUnmarshaller, InputStream paramInputStream)
    throws JAXBException
  {
    return (InMemory)adaptU(paramUnmarshaller, core.unmarshal(paramUnmarshaller, paramInputStream));
  }
  
  @NotNull
  public InMemory unmarshal(Unmarshaller paramUnmarshaller, Node paramNode)
    throws JAXBException
  {
    return (InMemory)adaptU(paramUnmarshaller, core.unmarshal(paramUnmarshaller, paramNode));
  }
  
  public TypeReference getTypeReference()
  {
    return core.getTypeReference();
  }
  
  @NotNull
  private InMemory adaptU(Unmarshaller paramUnmarshaller, OnWire paramOnWire)
    throws JAXBException
  {
    UnmarshallerImpl localUnmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    XmlAdapter localXmlAdapter = coordinator.getAdapter(adapter);
    coordinator.pushCoordinator();
    try
    {
      Object localObject1 = localXmlAdapter.unmarshal(paramOnWire);
      return (InMemory)localObject1;
    }
    catch (Exception localException)
    {
      throw new UnmarshalException(localException);
    }
    finally
    {
      coordinator.popCoordinator();
    }
  }
  
  void marshal(InMemory paramInMemory, XMLSerializer paramXMLSerializer)
    throws IOException, SAXException, XMLStreamException
  {
    try
    {
      core.marshal(_adaptM(XMLSerializer.getInstance(), paramInMemory), paramXMLSerializer);
    }
    catch (MarshalException localMarshalException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\BridgeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */