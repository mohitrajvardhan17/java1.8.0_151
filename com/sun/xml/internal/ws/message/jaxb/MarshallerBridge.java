package com.sun.xml.internal.ws.message.jaxb;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * @deprecated
 */
final class MarshallerBridge
  extends Bridge
{
  public MarshallerBridge(JAXBRIContext paramJAXBRIContext)
  {
    super((JAXBContextImpl)paramJAXBRIContext);
  }
  
  public void marshal(Marshaller paramMarshaller, Object paramObject, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(true));
    try
    {
      paramMarshaller.marshal(paramObject, paramXMLStreamWriter);
    }
    finally
    {
      paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(false));
    }
  }
  
  public void marshal(Marshaller paramMarshaller, Object paramObject, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(true));
    try
    {
      ((MarshallerImpl)paramMarshaller).marshal(paramObject, paramOutputStream, paramNamespaceContext);
    }
    finally
    {
      paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(false));
    }
  }
  
  public void marshal(Marshaller paramMarshaller, Object paramObject, Node paramNode)
    throws JAXBException
  {
    paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(true));
    try
    {
      paramMarshaller.marshal(paramObject, paramNode);
    }
    finally
    {
      paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(false));
    }
  }
  
  public void marshal(Marshaller paramMarshaller, Object paramObject, ContentHandler paramContentHandler)
    throws JAXBException
  {
    paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(true));
    try
    {
      paramMarshaller.marshal(paramObject, paramContentHandler);
    }
    finally
    {
      paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(false));
    }
  }
  
  public void marshal(Marshaller paramMarshaller, Object paramObject, Result paramResult)
    throws JAXBException
  {
    paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(true));
    try
    {
      paramMarshaller.marshal(paramObject, paramResult);
    }
    finally
    {
      paramMarshaller.setProperty("jaxb.fragment", Boolean.valueOf(false));
    }
  }
  
  public Object unmarshal(Unmarshaller paramUnmarshaller, XMLStreamReader paramXMLStreamReader)
  {
    throw new UnsupportedOperationException();
  }
  
  public Object unmarshal(Unmarshaller paramUnmarshaller, Source paramSource)
  {
    throw new UnsupportedOperationException();
  }
  
  public Object unmarshal(Unmarshaller paramUnmarshaller, InputStream paramInputStream)
  {
    throw new UnsupportedOperationException();
  }
  
  public Object unmarshal(Unmarshaller paramUnmarshaller, Node paramNode)
  {
    throw new UnsupportedOperationException();
  }
  
  public TypeReference getTypeReference()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\jaxb\MarshallerBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */