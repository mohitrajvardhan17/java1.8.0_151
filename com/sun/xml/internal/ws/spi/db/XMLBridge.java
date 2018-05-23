package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
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

public abstract interface XMLBridge<T>
{
  @NotNull
  public abstract BindingContext context();
  
  public abstract void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException;
  
  public abstract void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException;
  
  public abstract void marshal(T paramT, Node paramNode)
    throws JAXBException;
  
  public abstract void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException;
  
  public abstract void marshal(T paramT, Result paramResult)
    throws JAXBException;
  
  @NotNull
  public abstract T unmarshal(@NotNull XMLStreamReader paramXMLStreamReader, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException;
  
  @NotNull
  public abstract T unmarshal(@NotNull Source paramSource, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException;
  
  @NotNull
  public abstract T unmarshal(@NotNull InputStream paramInputStream)
    throws JAXBException;
  
  @NotNull
  public abstract T unmarshal(@NotNull Node paramNode, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException;
  
  public abstract TypeInfo getTypeInfo();
  
  public abstract boolean supportOutputStream();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\XMLBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */