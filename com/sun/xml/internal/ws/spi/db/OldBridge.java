package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.Pool;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.v2.runtime.BridgeContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
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

public abstract class OldBridge<T>
{
  protected final JAXBContextImpl context;
  
  protected OldBridge(JAXBContextImpl paramJAXBContextImpl)
  {
    context = paramJAXBContextImpl;
  }
  
  @NotNull
  public BindingContext getContext()
  {
    return null;
  }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    marshal(paramT, paramXMLStreamWriter, null);
  }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)context.marshallerPool.take();
    localMarshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
    marshal(localMarshaller, paramT, paramXMLStreamWriter);
    localMarshaller.setAttachmentMarshaller(null);
    context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    marshal(marshaller, paramT, paramXMLStreamWriter);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException;
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    marshal(paramT, paramOutputStream, paramNamespaceContext, null);
  }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)context.marshallerPool.take();
    localMarshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
    marshal(localMarshaller, paramT, paramOutputStream, paramNamespaceContext);
    localMarshaller.setAttachmentMarshaller(null);
    context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    marshal(marshaller, paramT, paramOutputStream, paramNamespaceContext);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException;
  
  public final void marshal(T paramT, Node paramNode)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)context.marshallerPool.take();
    marshal(localMarshaller, paramT, paramNode);
    context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, Node paramNode)
    throws JAXBException
  {
    marshal(marshaller, paramT, paramNode);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, Node paramNode)
    throws JAXBException;
  
  public final void marshal(T paramT, ContentHandler paramContentHandler)
    throws JAXBException
  {
    marshal(paramT, paramContentHandler, null);
  }
  
  public final void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)context.marshallerPool.take();
    localMarshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
    marshal(localMarshaller, paramT, paramContentHandler);
    localMarshaller.setAttachmentMarshaller(null);
    context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, ContentHandler paramContentHandler)
    throws JAXBException
  {
    marshal(marshaller, paramT, paramContentHandler);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, ContentHandler paramContentHandler)
    throws JAXBException;
  
  public final void marshal(T paramT, Result paramResult)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)context.marshallerPool.take();
    marshal(localMarshaller, paramT, paramResult);
    context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, Result paramResult)
    throws JAXBException
  {
    marshal(marshaller, paramT, paramResult);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, Result paramResult)
    throws JAXBException;
  
  private T exit(T paramT, Unmarshaller paramUnmarshaller)
  {
    paramUnmarshaller.setAttachmentUnmarshaller(null);
    context.unmarshallerPool.recycle(paramUnmarshaller);
    return paramT;
  }
  
  @NotNull
  public final T unmarshal(@NotNull XMLStreamReader paramXMLStreamReader)
    throws JAXBException
  {
    return (T)unmarshal(paramXMLStreamReader, null);
  }
  
  @NotNull
  public final T unmarshal(@NotNull XMLStreamReader paramXMLStreamReader, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    Unmarshaller localUnmarshaller = (Unmarshaller)context.unmarshallerPool.take();
    localUnmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
    return (T)exit(unmarshal(localUnmarshaller, paramXMLStreamReader), localUnmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull XMLStreamReader paramXMLStreamReader)
    throws JAXBException
  {
    return (T)unmarshal(unmarshaller, paramXMLStreamReader);
  }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull XMLStreamReader paramXMLStreamReader)
    throws JAXBException;
  
  @NotNull
  public final T unmarshal(@NotNull Source paramSource)
    throws JAXBException
  {
    return (T)unmarshal(paramSource, null);
  }
  
  @NotNull
  public final T unmarshal(@NotNull Source paramSource, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    Unmarshaller localUnmarshaller = (Unmarshaller)context.unmarshallerPool.take();
    localUnmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
    return (T)exit(unmarshal(localUnmarshaller, paramSource), localUnmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull Source paramSource)
    throws JAXBException
  {
    return (T)unmarshal(unmarshaller, paramSource);
  }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull Source paramSource)
    throws JAXBException;
  
  @NotNull
  public final T unmarshal(@NotNull InputStream paramInputStream)
    throws JAXBException
  {
    Unmarshaller localUnmarshaller = (Unmarshaller)context.unmarshallerPool.take();
    return (T)exit(unmarshal(localUnmarshaller, paramInputStream), localUnmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull InputStream paramInputStream)
    throws JAXBException
  {
    return (T)unmarshal(unmarshaller, paramInputStream);
  }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull InputStream paramInputStream)
    throws JAXBException;
  
  @NotNull
  public final T unmarshal(@NotNull Node paramNode)
    throws JAXBException
  {
    return (T)unmarshal(paramNode, null);
  }
  
  @NotNull
  public final T unmarshal(@NotNull Node paramNode, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    Unmarshaller localUnmarshaller = (Unmarshaller)context.unmarshallerPool.take();
    localUnmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
    return (T)exit(unmarshal(localUnmarshaller, paramNode), localUnmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull Node paramNode)
    throws JAXBException
  {
    return (T)unmarshal(unmarshaller, paramNode);
  }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull Node paramNode)
    throws JAXBException;
  
  public abstract TypeInfo getTypeReference();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\OldBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */