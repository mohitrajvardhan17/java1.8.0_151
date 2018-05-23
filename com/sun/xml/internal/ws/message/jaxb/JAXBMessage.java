package com.sun.xml.internal.ws.message.jaxb;

import com.sun.istack.internal.FragmentContentHandler;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.StreamingSOAP;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.RootElementSniffer;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite.ElemInfo;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class JAXBMessage
  extends AbstractMessageImpl
  implements StreamingSOAP
{
  private MessageHeaders headers;
  private final Object jaxbObject;
  private final XMLBridge bridge;
  private final JAXBContext rawContext;
  private String nsUri;
  private String localName;
  private XMLStreamBuffer infoset;
  
  public static Message create(BindingContext paramBindingContext, Object paramObject, SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet)
  {
    if (!paramBindingContext.hasSwaRef()) {
      return new JAXBMessage(paramBindingContext, paramObject, paramSOAPVersion, paramMessageHeaders, paramAttachmentSet);
    }
    try
    {
      MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      Marshaller localMarshaller = paramBindingContext.createMarshaller();
      AttachmentMarshallerImpl localAttachmentMarshallerImpl = new AttachmentMarshallerImpl(paramAttachmentSet);
      localMarshaller.setAttachmentMarshaller(localAttachmentMarshallerImpl);
      localAttachmentMarshallerImpl.cleanup();
      localMarshaller.marshal(paramObject, localMutableXMLStreamBuffer.createFromXMLStreamWriter());
      return new StreamMessage(paramMessageHeaders, paramAttachmentSet, localMutableXMLStreamBuffer.readAsXMLStreamReader(), paramSOAPVersion);
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
  }
  
  public static Message create(BindingContext paramBindingContext, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    return create(paramBindingContext, paramObject, paramSOAPVersion, null, null);
  }
  
  /**
   * @deprecated
   */
  public static Message create(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    return create(BindingContextFactory.create(paramJAXBContext), paramObject, paramSOAPVersion, null, null);
  }
  
  /**
   * @deprecated
   */
  public static Message createRaw(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    return new JAXBMessage(paramJAXBContext, paramObject, paramSOAPVersion, null, null);
  }
  
  private JAXBMessage(BindingContext paramBindingContext, Object paramObject, SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet)
  {
    super(paramSOAPVersion);
    bridge = paramBindingContext.createFragmentBridge();
    rawContext = null;
    jaxbObject = paramObject;
    headers = paramMessageHeaders;
    attachmentSet = paramAttachmentSet;
  }
  
  private JAXBMessage(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet)
  {
    super(paramSOAPVersion);
    rawContext = paramJAXBContext;
    bridge = null;
    jaxbObject = paramObject;
    headers = paramMessageHeaders;
    attachmentSet = paramAttachmentSet;
  }
  
  public static Message create(XMLBridge paramXMLBridge, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    if (!paramXMLBridge.context().hasSwaRef()) {
      return new JAXBMessage(paramXMLBridge, paramObject, paramSOAPVersion);
    }
    try
    {
      MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      AttachmentSetImpl localAttachmentSetImpl = new AttachmentSetImpl();
      AttachmentMarshallerImpl localAttachmentMarshallerImpl = new AttachmentMarshallerImpl(localAttachmentSetImpl);
      paramXMLBridge.marshal(paramObject, localMutableXMLStreamBuffer.createFromXMLStreamWriter(), localAttachmentMarshallerImpl);
      localAttachmentMarshallerImpl.cleanup();
      return new StreamMessage(null, localAttachmentSetImpl, localMutableXMLStreamBuffer.readAsXMLStreamReader(), paramSOAPVersion);
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
  }
  
  private JAXBMessage(XMLBridge paramXMLBridge, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    super(paramSOAPVersion);
    bridge = paramXMLBridge;
    rawContext = null;
    jaxbObject = paramObject;
    QName localQName = getTypeInfotagName;
    nsUri = localQName.getNamespaceURI();
    localName = localQName.getLocalPart();
    attachmentSet = new AttachmentSetImpl();
  }
  
  public JAXBMessage(JAXBMessage paramJAXBMessage)
  {
    super(paramJAXBMessage);
    headers = headers;
    if (headers != null) {
      headers = new HeaderList(headers);
    }
    attachmentSet = attachmentSet;
    jaxbObject = jaxbObject;
    bridge = bridge;
    rawContext = rawContext;
  }
  
  public boolean hasHeaders()
  {
    return (headers != null) && (headers.hasHeaders());
  }
  
  public MessageHeaders getHeaders()
  {
    if (headers == null) {
      headers = new HeaderList(getSOAPVersion());
    }
    return headers;
  }
  
  public String getPayloadLocalPart()
  {
    if (localName == null) {
      sniff();
    }
    return localName;
  }
  
  public String getPayloadNamespaceURI()
  {
    if (nsUri == null) {
      sniff();
    }
    return nsUri;
  }
  
  public boolean hasPayload()
  {
    return true;
  }
  
  private void sniff()
  {
    RootElementSniffer localRootElementSniffer = new RootElementSniffer(false);
    try
    {
      if (rawContext != null)
      {
        Marshaller localMarshaller = rawContext.createMarshaller();
        localMarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
        localMarshaller.marshal(jaxbObject, localRootElementSniffer);
      }
      else
      {
        bridge.marshal(jaxbObject, localRootElementSniffer, null);
      }
    }
    catch (JAXBException localJAXBException)
    {
      nsUri = localRootElementSniffer.getNsUri();
      localName = localRootElementSniffer.getLocalName();
    }
  }
  
  public Source readPayloadAsSource()
  {
    return new JAXBBridgeSource(bridge, jaxbObject);
  }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    JAXBResult localJAXBResult = new JAXBResult(paramUnmarshaller);
    try
    {
      localJAXBResult.getHandler().startDocument();
      if (rawContext != null)
      {
        Marshaller localMarshaller = rawContext.createMarshaller();
        localMarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
        localMarshaller.marshal(jaxbObject, localJAXBResult);
      }
      else
      {
        bridge.marshal(jaxbObject, localJAXBResult);
      }
      localJAXBResult.getHandler().endDocument();
    }
    catch (SAXException localSAXException)
    {
      throw new JAXBException(localSAXException);
    }
    return (T)localJAXBResult.getResult();
  }
  
  public XMLStreamReader readPayload()
    throws XMLStreamException
  {
    try
    {
      if (infoset == null) {
        if (rawContext != null)
        {
          localObject = new XMLStreamBufferResult();
          Marshaller localMarshaller = rawContext.createMarshaller();
          localMarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
          localMarshaller.marshal(jaxbObject, (Result)localObject);
          infoset = ((XMLStreamBufferResult)localObject).getXMLStreamBuffer();
        }
        else
        {
          localObject = new MutableXMLStreamBuffer();
          writePayloadTo(((MutableXMLStreamBuffer)localObject).createFromXMLStreamWriter());
          infoset = ((XMLStreamBuffer)localObject);
        }
      }
      Object localObject = infoset.readAsXMLStreamReader();
      if (((XMLStreamReader)localObject).getEventType() == 7) {
        XMLStreamReaderUtil.nextElementContent((XMLStreamReader)localObject);
      }
      return (XMLStreamReader)localObject;
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
  }
  
  protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
    throws SAXException
  {
    try
    {
      if (paramBoolean) {
        paramContentHandler = new FragmentContentHandler(paramContentHandler);
      }
      AttachmentMarshallerImpl localAttachmentMarshallerImpl = new AttachmentMarshallerImpl(attachmentSet);
      if (rawContext != null)
      {
        Marshaller localMarshaller = rawContext.createMarshaller();
        localMarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
        localMarshaller.setAttachmentMarshaller(localAttachmentMarshallerImpl);
        localMarshaller.marshal(jaxbObject, paramContentHandler);
      }
      else
      {
        bridge.marshal(jaxbObject, paramContentHandler, localAttachmentMarshallerImpl);
      }
      localAttachmentMarshallerImpl.cleanup();
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException.getMessage(), localJAXBException);
    }
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    try
    {
      AttachmentMarshallerImpl localAttachmentMarshallerImpl = (paramXMLStreamWriter instanceof MtomStreamWriter) ? ((MtomStreamWriter)paramXMLStreamWriter).getAttachmentMarshaller() : new AttachmentMarshallerImpl(attachmentSet);
      String str = XMLStreamWriterUtil.getEncoding(paramXMLStreamWriter);
      OutputStream localOutputStream = bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(paramXMLStreamWriter) : null;
      if (rawContext != null)
      {
        Marshaller localMarshaller = rawContext.createMarshaller();
        localMarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
        localMarshaller.setAttachmentMarshaller(localAttachmentMarshallerImpl);
        if (localOutputStream != null) {
          localMarshaller.marshal(jaxbObject, localOutputStream);
        } else {
          localMarshaller.marshal(jaxbObject, paramXMLStreamWriter);
        }
      }
      else if ((localOutputStream != null) && (str != null) && (str.equalsIgnoreCase("utf-8")))
      {
        bridge.marshal(jaxbObject, localOutputStream, paramXMLStreamWriter.getNamespaceContext(), localAttachmentMarshallerImpl);
      }
      else
      {
        bridge.marshal(jaxbObject, paramXMLStreamWriter, localAttachmentMarshallerImpl);
      }
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
  }
  
  public Message copy()
  {
    return new JAXBMessage(this);
  }
  
  public XMLStreamReader readEnvelope()
  {
    int i = soapVersion.ordinal() * 3;
    envelopeTag = ((TagInfoset)DEFAULT_TAGS.get(i));
    bodyTag = ((TagInfoset)DEFAULT_TAGS.get(i + 2));
    ArrayList localArrayList = new ArrayList();
    XMLReaderComposite.ElemInfo localElemInfo1 = new XMLReaderComposite.ElemInfo(envelopeTag, null);
    XMLReaderComposite.ElemInfo localElemInfo2 = new XMLReaderComposite.ElemInfo(bodyTag, localElemInfo1);
    Object localObject1 = getHeaders().asList().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Header)((Iterator)localObject1).next();
      try
      {
        localArrayList.add(((Header)localObject2).readHeader());
      }
      catch (XMLStreamException localXMLStreamException2)
      {
        throw new RuntimeException(localXMLStreamException2);
      }
    }
    localObject1 = null;
    if (localArrayList.size() > 0)
    {
      headerTag = ((TagInfoset)DEFAULT_TAGS.get(i + 1));
      localObject2 = new XMLReaderComposite.ElemInfo(headerTag, localElemInfo1);
      localObject1 = new XMLReaderComposite((XMLReaderComposite.ElemInfo)localObject2, (XMLStreamReader[])localArrayList.toArray(new XMLStreamReader[localArrayList.size()]));
    }
    try
    {
      localObject2 = readPayload();
      XMLReaderComposite localXMLReaderComposite = new XMLReaderComposite(localElemInfo2, new XMLStreamReader[] { localObject2 });
      XMLStreamReader[] arrayOfXMLStreamReader = { localObject1 != null ? new XMLStreamReader[] { localObject1, localXMLReaderComposite } : localXMLReaderComposite };
      return new XMLReaderComposite(localElemInfo1, arrayOfXMLStreamReader);
    }
    catch (XMLStreamException localXMLStreamException1)
    {
      throw new RuntimeException(localXMLStreamException1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\jaxb\JAXBMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */