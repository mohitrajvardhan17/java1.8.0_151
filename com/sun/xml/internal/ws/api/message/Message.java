package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class Message
{
  protected AttachmentSet attachmentSet;
  private WSDLBoundOperation operation = null;
  private WSDLOperationMapping wsdlOperationMapping = null;
  private MessageMetadata messageMetadata = null;
  private Boolean isOneWay;
  
  public Message() {}
  
  public abstract boolean hasHeaders();
  
  @NotNull
  public abstract MessageHeaders getHeaders();
  
  @NotNull
  public AttachmentSet getAttachments()
  {
    if (attachmentSet == null) {
      attachmentSet = new AttachmentSetImpl();
    }
    return attachmentSet;
  }
  
  protected boolean hasAttachments()
  {
    return attachmentSet != null;
  }
  
  public void setMessageMedadata(MessageMetadata paramMessageMetadata)
  {
    messageMetadata = paramMessageMetadata;
  }
  
  @Deprecated
  @Nullable
  public final WSDLBoundOperation getOperation(@NotNull WSDLBoundPortType paramWSDLBoundPortType)
  {
    if ((operation == null) && (messageMetadata != null))
    {
      if (wsdlOperationMapping == null) {
        wsdlOperationMapping = messageMetadata.getWSDLOperationMapping();
      }
      if (wsdlOperationMapping != null) {
        operation = wsdlOperationMapping.getWSDLBoundOperation();
      }
    }
    if (operation == null) {
      operation = paramWSDLBoundPortType.getOperation(getPayloadNamespaceURI(), getPayloadLocalPart());
    }
    return operation;
  }
  
  @Deprecated
  @Nullable
  public final WSDLBoundOperation getOperation(@NotNull WSDLPort paramWSDLPort)
  {
    return getOperation(paramWSDLPort.getBinding());
  }
  
  @Deprecated
  @Nullable
  public final JavaMethod getMethod(@NotNull SEIModel paramSEIModel)
  {
    if ((wsdlOperationMapping == null) && (messageMetadata != null)) {
      wsdlOperationMapping = messageMetadata.getWSDLOperationMapping();
    }
    if (wsdlOperationMapping != null) {
      return wsdlOperationMapping.getJavaMethod();
    }
    String str1 = getPayloadLocalPart();
    String str2;
    if (str1 == null)
    {
      str1 = "";
      str2 = "";
    }
    else
    {
      str2 = getPayloadNamespaceURI();
    }
    QName localQName = new QName(str2, str1);
    return paramSEIModel.getJavaMethod(localQName);
  }
  
  public boolean isOneWay(@NotNull WSDLPort paramWSDLPort)
  {
    if (isOneWay == null)
    {
      WSDLBoundOperation localWSDLBoundOperation = getOperation(paramWSDLPort);
      if (localWSDLBoundOperation != null) {
        isOneWay = Boolean.valueOf(localWSDLBoundOperation.getOperation().isOneWay());
      } else {
        isOneWay = Boolean.valueOf(false);
      }
    }
    return isOneWay.booleanValue();
  }
  
  public final void assertOneWay(boolean paramBoolean)
  {
    assert ((isOneWay == null) || (isOneWay.booleanValue() == paramBoolean));
    isOneWay = Boolean.valueOf(paramBoolean);
  }
  
  @Nullable
  public abstract String getPayloadLocalPart();
  
  public abstract String getPayloadNamespaceURI();
  
  public abstract boolean hasPayload();
  
  public boolean isFault()
  {
    String str1 = getPayloadLocalPart();
    if ((str1 == null) || (!str1.equals("Fault"))) {
      return false;
    }
    String str2 = getPayloadNamespaceURI();
    return (str2.equals(SOAP_11nsUri)) || (str2.equals(SOAP_12nsUri));
  }
  
  @Nullable
  public QName getFirstDetailEntryName()
  {
    assert (isFault());
    Message localMessage = copy();
    try
    {
      SOAPFaultBuilder localSOAPFaultBuilder = SOAPFaultBuilder.create(localMessage);
      return localSOAPFaultBuilder.getFirstDetailEntryName();
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
  }
  
  public abstract Source readEnvelopeAsSource();
  
  public abstract Source readPayloadAsSource();
  
  public abstract SOAPMessage readAsSOAPMessage()
    throws SOAPException;
  
  public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean)
    throws SOAPException
  {
    return readAsSOAPMessage();
  }
  
  public static Map<String, List<String>> getTransportHeaders(Packet paramPacket)
  {
    return getTransportHeaders(paramPacket, paramPacket.getState().isInbound());
  }
  
  public static Map<String, List<String>> getTransportHeaders(Packet paramPacket, boolean paramBoolean)
  {
    Map localMap = null;
    String str = paramBoolean ? "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers" : "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers";
    if (paramPacket.supports(str)) {
      localMap = (Map)paramPacket.get(str);
    }
    return localMap;
  }
  
  public static void addSOAPMimeHeaders(MimeHeaders paramMimeHeaders, Map<String, List<String>> paramMap)
  {
    Iterator localIterator1 = paramMap.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      if (!((String)localEntry.getKey()).equalsIgnoreCase("Content-Type"))
      {
        Iterator localIterator2 = ((List)localEntry.getValue()).iterator();
        while (localIterator2.hasNext())
        {
          String str = (String)localIterator2.next();
          paramMimeHeaders.addHeader((String)localEntry.getKey(), str);
        }
      }
    }
  }
  
  public abstract <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException;
  
  /**
   * @deprecated
   */
  public abstract <T> T readPayloadAsJAXB(Bridge<T> paramBridge)
    throws JAXBException;
  
  public abstract <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge)
    throws JAXBException;
  
  public abstract XMLStreamReader readPayload()
    throws XMLStreamException;
  
  public void consume() {}
  
  public abstract void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException;
  
  public abstract void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException;
  
  public abstract void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException;
  
  public abstract Message copy();
  
  /**
   * @deprecated
   */
  @NotNull
  public String getID(@NotNull WSBinding paramWSBinding)
  {
    return getID(paramWSBinding.getAddressingVersion(), paramWSBinding.getSOAPVersion());
  }
  
  /**
   * @deprecated
   */
  @NotNull
  public String getID(AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion)
  {
    String str = null;
    if (paramAddressingVersion != null) {
      str = AddressingUtils.getMessageID(getHeaders(), paramAddressingVersion, paramSOAPVersion);
    }
    if (str == null)
    {
      str = generateMessageID();
      getHeaders().add(new StringHeader(messageIDTag, str));
    }
    return str;
  }
  
  public static String generateMessageID()
  {
    return "uuid:" + UUID.randomUUID().toString();
  }
  
  public SOAPVersion getSOAPVersion()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */