package com.sun.xml.internal.ws.spi.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class WrapperBridge<T>
  implements XMLBridge<T>
{
  BindingContext parent;
  TypeInfo typeInfo;
  static final String WrapperPrefix = "w";
  static final String WrapperPrefixColon = "w:";
  
  public WrapperBridge(BindingContext paramBindingContext, TypeInfo paramTypeInfo)
  {
    parent = paramBindingContext;
    typeInfo = paramTypeInfo;
  }
  
  public BindingContext context()
  {
    return parent;
  }
  
  public TypeInfo getTypeInfo()
  {
    return typeInfo;
  }
  
  public final void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    WrapperComposite localWrapperComposite = (WrapperComposite)paramT;
    Attributes local1 = new Attributes()
    {
      public int getLength()
      {
        return 0;
      }
      
      public String getURI(int paramAnonymousInt)
      {
        return null;
      }
      
      public String getLocalName(int paramAnonymousInt)
      {
        return null;
      }
      
      public String getQName(int paramAnonymousInt)
      {
        return null;
      }
      
      public String getType(int paramAnonymousInt)
      {
        return null;
      }
      
      public String getValue(int paramAnonymousInt)
      {
        return null;
      }
      
      public int getIndex(String paramAnonymousString1, String paramAnonymousString2)
      {
        return 0;
      }
      
      public int getIndex(String paramAnonymousString)
      {
        return 0;
      }
      
      public String getType(String paramAnonymousString1, String paramAnonymousString2)
      {
        return null;
      }
      
      public String getType(String paramAnonymousString)
      {
        return null;
      }
      
      public String getValue(String paramAnonymousString1, String paramAnonymousString2)
      {
        return null;
      }
      
      public String getValue(String paramAnonymousString)
      {
        return null;
      }
    };
    try
    {
      paramContentHandler.startPrefixMapping("w", typeInfo.tagName.getNamespaceURI());
      paramContentHandler.startElement(typeInfo.tagName.getNamespaceURI(), typeInfo.tagName.getLocalPart(), "w:" + typeInfo.tagName.getLocalPart(), local1);
    }
    catch (SAXException localSAXException1)
    {
      throw new JAXBException(localSAXException1);
    }
    if (bridges != null) {
      for (int i = 0; i < bridges.length; i++) {
        if ((bridges[i] instanceof RepeatedElementBridge))
        {
          RepeatedElementBridge localRepeatedElementBridge = (RepeatedElementBridge)bridges[i];
          Iterator localIterator = localRepeatedElementBridge.collectionHandler().iterator(values[i]);
          while (localIterator.hasNext()) {
            localRepeatedElementBridge.marshal(localIterator.next(), paramContentHandler, paramAttachmentMarshaller);
          }
        }
        else
        {
          bridges[i].marshal(values[i], paramContentHandler, paramAttachmentMarshaller);
        }
      }
    }
    try
    {
      paramContentHandler.endElement(typeInfo.tagName.getNamespaceURI(), typeInfo.tagName.getLocalPart(), null);
      paramContentHandler.endPrefixMapping("w");
    }
    catch (SAXException localSAXException2)
    {
      throw new JAXBException(localSAXException2);
    }
  }
  
  public void marshal(T paramT, Node paramNode)
    throws JAXBException
  {
    throw new UnsupportedOperationException();
  }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {}
  
  public final void marshal(T paramT, Result paramResult)
    throws JAXBException
  {
    throw new UnsupportedOperationException();
  }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    WrapperComposite localWrapperComposite = (WrapperComposite)paramT;
    try
    {
      String str = paramXMLStreamWriter.getPrefix(typeInfo.tagName.getNamespaceURI());
      if (str == null) {
        str = "w";
      }
      paramXMLStreamWriter.writeStartElement(str, typeInfo.tagName.getLocalPart(), typeInfo.tagName.getNamespaceURI());
      paramXMLStreamWriter.writeNamespace(str, typeInfo.tagName.getNamespaceURI());
    }
    catch (XMLStreamException localXMLStreamException1)
    {
      localXMLStreamException1.printStackTrace();
      throw new DatabindingException(localXMLStreamException1);
    }
    if (bridges != null) {
      for (int i = 0; i < bridges.length; i++) {
        if ((bridges[i] instanceof RepeatedElementBridge))
        {
          RepeatedElementBridge localRepeatedElementBridge = (RepeatedElementBridge)bridges[i];
          Iterator localIterator = localRepeatedElementBridge.collectionHandler().iterator(values[i]);
          while (localIterator.hasNext()) {
            localRepeatedElementBridge.marshal(localIterator.next(), paramXMLStreamWriter, paramAttachmentMarshaller);
          }
        }
        else
        {
          bridges[i].marshal(values[i], paramXMLStreamWriter, paramAttachmentMarshaller);
        }
      }
    }
    try
    {
      paramXMLStreamWriter.writeEndElement();
    }
    catch (XMLStreamException localXMLStreamException2)
    {
      throw new DatabindingException(localXMLStreamException2);
    }
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
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\WrapperBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */