package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.internal.ws.addressing.EndpointReferenceUtil;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.spi.ProviderImpl;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public final class WSEndpointReference
  implements WSDLExtension
{
  private final XMLStreamBuffer infoset;
  private final AddressingVersion version;
  @NotNull
  private Header[] referenceParameters;
  @NotNull
  private String address;
  @NotNull
  private QName rootElement;
  private static final OutboundReferenceParameterHeader[] EMPTY_ARRAY = new OutboundReferenceParameterHeader[0];
  private Map<QName, EPRExtension> rootEprExtensions;
  
  public WSEndpointReference(EndpointReference paramEndpointReference, AddressingVersion paramAddressingVersion)
  {
    try
    {
      MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      paramEndpointReference.writeTo(new XMLStreamBufferResult(localMutableXMLStreamBuffer));
      infoset = localMutableXMLStreamBuffer;
      version = paramAddressingVersion;
      rootElement = new QName("EndpointReference", nsUri);
      parse();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(ClientMessages.FAILED_TO_PARSE_EPR(paramEndpointReference), localXMLStreamException);
    }
  }
  
  public WSEndpointReference(EndpointReference paramEndpointReference)
  {
    this(paramEndpointReference, AddressingVersion.fromSpecClass(paramEndpointReference.getClass()));
  }
  
  public WSEndpointReference(XMLStreamBuffer paramXMLStreamBuffer, AddressingVersion paramAddressingVersion)
  {
    try
    {
      infoset = paramXMLStreamBuffer;
      version = paramAddressingVersion;
      rootElement = new QName("EndpointReference", nsUri);
      parse();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new AssertionError(localXMLStreamException);
    }
  }
  
  public WSEndpointReference(InputStream paramInputStream, AddressingVersion paramAddressingVersion)
    throws XMLStreamException
  {
    this(XMLStreamReaderFactory.create(null, paramInputStream, false), paramAddressingVersion);
  }
  
  public WSEndpointReference(XMLStreamReader paramXMLStreamReader, AddressingVersion paramAddressingVersion)
    throws XMLStreamException
  {
    this(XMLStreamBuffer.createNewBufferFromXMLStreamReader(paramXMLStreamReader), paramAddressingVersion);
  }
  
  public WSEndpointReference(URL paramURL, AddressingVersion paramAddressingVersion)
  {
    this(paramURL.toExternalForm(), paramAddressingVersion);
  }
  
  public WSEndpointReference(URI paramURI, AddressingVersion paramAddressingVersion)
  {
    this(paramURI.toString(), paramAddressingVersion);
  }
  
  public WSEndpointReference(String paramString, AddressingVersion paramAddressingVersion)
  {
    infoset = createBufferFromAddress(paramString, paramAddressingVersion);
    version = paramAddressingVersion;
    address = paramString;
    rootElement = new QName("EndpointReference", nsUri);
    referenceParameters = EMPTY_ARRAY;
  }
  
  private static XMLStreamBuffer createBufferFromAddress(String paramString, AddressingVersion paramAddressingVersion)
  {
    try
    {
      MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      StreamWriterBufferCreator localStreamWriterBufferCreator = new StreamWriterBufferCreator(localMutableXMLStreamBuffer);
      localStreamWriterBufferCreator.writeStartDocument();
      localStreamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), "EndpointReference", nsUri);
      localStreamWriterBufferCreator.writeNamespace(paramAddressingVersion.getPrefix(), nsUri);
      localStreamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), eprType.address, nsUri);
      localStreamWriterBufferCreator.writeCharacters(paramString);
      localStreamWriterBufferCreator.writeEndElement();
      localStreamWriterBufferCreator.writeEndElement();
      localStreamWriterBufferCreator.writeEndDocument();
      localStreamWriterBufferCreator.close();
      return localMutableXMLStreamBuffer;
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new AssertionError(localXMLStreamException);
    }
  }
  
  public WSEndpointReference(@NotNull AddressingVersion paramAddressingVersion, @NotNull String paramString1, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable QName paramQName3, @Nullable List<Element> paramList1, @Nullable String paramString2, @Nullable List<Element> paramList2)
  {
    this(paramAddressingVersion, paramString1, paramQName1, paramQName2, paramQName3, paramList1, paramString2, null, paramList2, null, null);
  }
  
  public WSEndpointReference(@NotNull AddressingVersion paramAddressingVersion, @NotNull String paramString1, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable QName paramQName3, @Nullable List<Element> paramList1, @Nullable String paramString2, @Nullable List<Element> paramList2, @Nullable Collection<EPRExtension> paramCollection, @Nullable Map<QName, String> paramMap)
  {
    this(createBufferFromData(paramAddressingVersion, paramString1, paramList2, paramQName1, paramQName2, paramQName3, paramList1, paramString2, null, paramCollection, paramMap), paramAddressingVersion);
  }
  
  public WSEndpointReference(@NotNull AddressingVersion paramAddressingVersion, @NotNull String paramString1, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable QName paramQName3, @Nullable List<Element> paramList1, @Nullable String paramString2, @Nullable String paramString3, @Nullable List<Element> paramList2, @Nullable List<Element> paramList3, @Nullable Map<QName, String> paramMap)
  {
    this(createBufferFromData(paramAddressingVersion, paramString1, paramList2, paramQName1, paramQName2, paramQName3, paramList1, paramString2, paramString3, paramList3, paramMap), paramAddressingVersion);
  }
  
  private static XMLStreamBuffer createBufferFromData(AddressingVersion paramAddressingVersion, String paramString1, List<Element> paramList1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList2, String paramString2, String paramString3, @Nullable List<Element> paramList3, @Nullable Map<QName, String> paramMap)
  {
    StreamWriterBufferCreator localStreamWriterBufferCreator = new StreamWriterBufferCreator();
    try
    {
      localStreamWriterBufferCreator.writeStartDocument();
      localStreamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), "EndpointReference", nsUri);
      localStreamWriterBufferCreator.writeNamespace(paramAddressingVersion.getPrefix(), nsUri);
      writePartialEPRInfoset(localStreamWriterBufferCreator, paramAddressingVersion, paramString1, paramList1, paramQName1, paramQName2, paramQName3, paramList2, paramString2, paramString3, paramMap);
      if (paramList3 != null)
      {
        Iterator localIterator = paramList3.iterator();
        while (localIterator.hasNext())
        {
          Element localElement = (Element)localIterator.next();
          DOMUtil.serializeNode(localElement, localStreamWriterBufferCreator);
        }
      }
      localStreamWriterBufferCreator.writeEndElement();
      localStreamWriterBufferCreator.writeEndDocument();
      localStreamWriterBufferCreator.flush();
      return localStreamWriterBufferCreator.getXMLStreamBuffer();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
  }
  
  private static XMLStreamBuffer createBufferFromData(AddressingVersion paramAddressingVersion, String paramString1, List<Element> paramList1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList2, String paramString2, String paramString3, @Nullable Collection<EPRExtension> paramCollection, @Nullable Map<QName, String> paramMap)
  {
    StreamWriterBufferCreator localStreamWriterBufferCreator = new StreamWriterBufferCreator();
    try
    {
      localStreamWriterBufferCreator.writeStartDocument();
      localStreamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), "EndpointReference", nsUri);
      localStreamWriterBufferCreator.writeNamespace(paramAddressingVersion.getPrefix(), nsUri);
      writePartialEPRInfoset(localStreamWriterBufferCreator, paramAddressingVersion, paramString1, paramList1, paramQName1, paramQName2, paramQName3, paramList2, paramString2, paramString3, paramMap);
      if (paramCollection != null)
      {
        Iterator localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          EPRExtension localEPRExtension = (EPRExtension)localIterator.next();
          XMLStreamReaderToXMLStreamWriter localXMLStreamReaderToXMLStreamWriter = new XMLStreamReaderToXMLStreamWriter();
          XMLStreamReader localXMLStreamReader = localEPRExtension.readAsXMLStreamReader();
          localXMLStreamReaderToXMLStreamWriter.bridge(localXMLStreamReader, localStreamWriterBufferCreator);
          XMLStreamReaderFactory.recycle(localXMLStreamReader);
        }
      }
      localStreamWriterBufferCreator.writeEndElement();
      localStreamWriterBufferCreator.writeEndDocument();
      localStreamWriterBufferCreator.flush();
      return localStreamWriterBufferCreator.getXMLStreamBuffer();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
  }
  
  private static void writePartialEPRInfoset(StreamWriterBufferCreator paramStreamWriterBufferCreator, AddressingVersion paramAddressingVersion, String paramString1, List<Element> paramList1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList2, String paramString2, String paramString3, @Nullable Map<QName, String> paramMap)
    throws XMLStreamException
  {
    Iterator localIterator;
    Object localObject;
    if (paramMap != null)
    {
      localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (Map.Entry)localIterator.next();
        QName localQName = (QName)((Map.Entry)localObject).getKey();
        paramStreamWriterBufferCreator.writeAttribute(localQName.getPrefix(), localQName.getNamespaceURI(), localQName.getLocalPart(), (String)((Map.Entry)localObject).getValue());
      }
    }
    paramStreamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), eprType.address, nsUri);
    paramStreamWriterBufferCreator.writeCharacters(paramString1);
    paramStreamWriterBufferCreator.writeEndElement();
    if ((paramList1 != null) && (paramList1.size() > 0))
    {
      paramStreamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), eprType.referenceParameters, nsUri);
      localIterator = paramList1.iterator();
      while (localIterator.hasNext())
      {
        localObject = (Element)localIterator.next();
        DOMUtil.serializeNode((Element)localObject, paramStreamWriterBufferCreator);
      }
      paramStreamWriterBufferCreator.writeEndElement();
    }
    switch (paramAddressingVersion)
    {
    case W3C: 
      writeW3CMetaData(paramStreamWriterBufferCreator, paramQName1, paramQName2, paramQName3, paramList2, paramString2, paramString3);
      break;
    case MEMBER: 
      writeMSMetaData(paramStreamWriterBufferCreator, paramQName1, paramQName2, paramQName3, paramList2);
      if (paramString2 != null)
      {
        paramStreamWriterBufferCreator.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI());
        paramStreamWriterBufferCreator.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI());
        paramStreamWriterBufferCreator.writeAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
        writeWsdl(paramStreamWriterBufferCreator, paramQName1, paramString2);
        paramStreamWriterBufferCreator.writeEndElement();
        paramStreamWriterBufferCreator.writeEndElement();
      }
      break;
    }
  }
  
  private static boolean isEmty(QName paramQName)
  {
    return (paramQName == null) || (paramQName.toString().trim().length() == 0);
  }
  
  private static void writeW3CMetaData(StreamWriterBufferCreator paramStreamWriterBufferCreator, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList, String paramString1, String paramString2)
    throws XMLStreamException
  {
    if ((isEmty(paramQName1)) && (isEmty(paramQName2)) && (isEmty(paramQName3)) && (paramList == null)) {
      return;
    }
    paramStreamWriterBufferCreator.writeStartElement(AddressingVersion.W3C.getPrefix(), W3CeprType.wsdlMetadata.getLocalPart(), W3CnsUri);
    paramStreamWriterBufferCreator.writeNamespace(AddressingVersion.W3C.getWsdlPrefix(), W3CwsdlNsUri);
    if (paramString1 != null) {
      writeWsdliLocation(paramStreamWriterBufferCreator, paramQName1, paramString1, paramString2);
    }
    Object localObject;
    if (paramQName3 != null)
    {
      paramStreamWriterBufferCreator.writeStartElement("wsam", W3CeprType.portTypeName, "http://www.w3.org/2007/05/addressing/metadata");
      paramStreamWriterBufferCreator.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
      localObject = paramQName3.getPrefix();
      if ((localObject == null) || (((String)localObject).equals(""))) {
        localObject = "wsns";
      }
      paramStreamWriterBufferCreator.writeNamespace((String)localObject, paramQName3.getNamespaceURI());
      paramStreamWriterBufferCreator.writeCharacters((String)localObject + ":" + paramQName3.getLocalPart());
      paramStreamWriterBufferCreator.writeEndElement();
    }
    if ((paramQName1 != null) && (!paramQName1.getNamespaceURI().equals("")) && (!paramQName1.getLocalPart().equals("")))
    {
      paramStreamWriterBufferCreator.writeStartElement("wsam", W3CeprType.serviceName, "http://www.w3.org/2007/05/addressing/metadata");
      paramStreamWriterBufferCreator.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
      localObject = paramQName1.getPrefix();
      if ((localObject == null) || (((String)localObject).equals(""))) {
        localObject = "wsns";
      }
      paramStreamWriterBufferCreator.writeNamespace((String)localObject, paramQName1.getNamespaceURI());
      if (paramQName2 != null) {
        paramStreamWriterBufferCreator.writeAttribute(W3CeprType.portName, paramQName2.getLocalPart());
      }
      paramStreamWriterBufferCreator.writeCharacters((String)localObject + ":" + paramQName1.getLocalPart());
      paramStreamWriterBufferCreator.writeEndElement();
    }
    if (paramList != null)
    {
      localObject = paramList.iterator();
      while (((Iterator)localObject).hasNext())
      {
        Element localElement = (Element)((Iterator)localObject).next();
        DOMUtil.serializeNode(localElement, paramStreamWriterBufferCreator);
      }
    }
    paramStreamWriterBufferCreator.writeEndElement();
  }
  
  private static void writeWsdliLocation(StreamWriterBufferCreator paramStreamWriterBufferCreator, QName paramQName, String paramString1, String paramString2)
    throws XMLStreamException
  {
    String str = "";
    if (paramString2 != null) {
      str = paramString2 + " ";
    } else if (paramQName != null) {
      str = paramQName.getNamespaceURI() + " ";
    } else {
      throw new WebServiceException("WSDL target Namespace cannot be resolved");
    }
    str = str + paramString1;
    paramStreamWriterBufferCreator.writeNamespace("wsdli", "http://www.w3.org/ns/wsdl-instance");
    paramStreamWriterBufferCreator.writeAttribute("wsdli", "http://www.w3.org/ns/wsdl-instance", "wsdlLocation", str);
  }
  
  private static void writeMSMetaData(StreamWriterBufferCreator paramStreamWriterBufferCreator, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList)
    throws XMLStreamException
  {
    String str;
    if (paramQName3 != null)
    {
      paramStreamWriterBufferCreator.writeStartElement(AddressingVersion.MEMBER.getPrefix(), MEMBEReprType.portTypeName, MEMBERnsUri);
      str = paramQName3.getPrefix();
      if ((str == null) || (str.equals(""))) {
        str = "wsns";
      }
      paramStreamWriterBufferCreator.writeNamespace(str, paramQName3.getNamespaceURI());
      paramStreamWriterBufferCreator.writeCharacters(str + ":" + paramQName3.getLocalPart());
      paramStreamWriterBufferCreator.writeEndElement();
    }
    if ((paramQName1 != null) && (!paramQName1.getNamespaceURI().equals("")) && (!paramQName1.getLocalPart().equals("")))
    {
      paramStreamWriterBufferCreator.writeStartElement(AddressingVersion.MEMBER.getPrefix(), MEMBEReprType.serviceName, MEMBERnsUri);
      str = paramQName1.getPrefix();
      if ((str == null) || (str.equals(""))) {
        str = "wsns";
      }
      paramStreamWriterBufferCreator.writeNamespace(str, paramQName1.getNamespaceURI());
      if (paramQName2 != null) {
        paramStreamWriterBufferCreator.writeAttribute(MEMBEReprType.portName, paramQName2.getLocalPart());
      }
      paramStreamWriterBufferCreator.writeCharacters(str + ":" + paramQName1.getLocalPart());
      paramStreamWriterBufferCreator.writeEndElement();
    }
  }
  
  private static void writeWsdl(StreamWriterBufferCreator paramStreamWriterBufferCreator, QName paramQName, String paramString)
    throws XMLStreamException
  {
    paramStreamWriterBufferCreator.writeStartElement("wsdl", WSDLConstants.QNAME_DEFINITIONS.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
    paramStreamWriterBufferCreator.writeNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
    paramStreamWriterBufferCreator.writeStartElement("wsdl", WSDLConstants.QNAME_IMPORT.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
    paramStreamWriterBufferCreator.writeAttribute("namespace", paramQName.getNamespaceURI());
    paramStreamWriterBufferCreator.writeAttribute("location", paramString);
    paramStreamWriterBufferCreator.writeEndElement();
    paramStreamWriterBufferCreator.writeEndElement();
  }
  
  @Nullable
  public static WSEndpointReference create(@Nullable EndpointReference paramEndpointReference)
  {
    if (paramEndpointReference != null) {
      return new WSEndpointReference(paramEndpointReference);
    }
    return null;
  }
  
  @NotNull
  public WSEndpointReference createWithAddress(@NotNull URI paramURI)
  {
    return createWithAddress(paramURI.toString());
  }
  
  @NotNull
  public WSEndpointReference createWithAddress(@NotNull URL paramURL)
  {
    return createWithAddress(paramURL.toString());
  }
  
  @NotNull
  public WSEndpointReference createWithAddress(@NotNull final String paramString)
  {
    MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    XMLFilterImpl local1 = new XMLFilterImpl()
    {
      private boolean inAddress = false;
      
      public void startElement(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, Attributes paramAnonymousAttributes)
        throws SAXException
      {
        if ((paramAnonymousString2.equals("Address")) && (paramAnonymousString1.equals(version.nsUri))) {
          inAddress = true;
        }
        super.startElement(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3, paramAnonymousAttributes);
      }
      
      public void characters(char[] paramAnonymousArrayOfChar, int paramAnonymousInt1, int paramAnonymousInt2)
        throws SAXException
      {
        if (!inAddress) {
          super.characters(paramAnonymousArrayOfChar, paramAnonymousInt1, paramAnonymousInt2);
        }
      }
      
      public void endElement(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        throws SAXException
      {
        if (inAddress) {
          super.characters(paramString.toCharArray(), 0, paramString.length());
        }
        inAddress = false;
        super.endElement(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
      }
    };
    local1.setContentHandler(localMutableXMLStreamBuffer.createFromSAXBufferCreator());
    try
    {
      infoset.writeTo(local1, false);
    }
    catch (SAXException localSAXException)
    {
      throw new AssertionError(localSAXException);
    }
    return new WSEndpointReference(localMutableXMLStreamBuffer, version);
  }
  
  @NotNull
  public EndpointReference toSpec()
  {
    return ProviderImpl.INSTANCE.readEndpointReference(asSource("EndpointReference"));
  }
  
  @NotNull
  public <T extends EndpointReference> T toSpec(Class<T> paramClass)
  {
    return EndpointReferenceUtil.transform(paramClass, toSpec());
  }
  
  @NotNull
  public <T> T getPort(@NotNull Service paramService, @NotNull Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    return (T)paramService.getPort(toSpec(), paramClass, paramVarArgs);
  }
  
  @NotNull
  public <T> Dispatch<T> createDispatch(@NotNull Service paramService, @NotNull Class<T> paramClass, @NotNull Service.Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return paramService.createDispatch(toSpec(), paramClass, paramMode, paramVarArgs);
  }
  
  @NotNull
  public Dispatch<Object> createDispatch(@NotNull Service paramService, @NotNull JAXBContext paramJAXBContext, @NotNull Service.Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return paramService.createDispatch(toSpec(), paramJAXBContext, paramMode, paramVarArgs);
  }
  
  @NotNull
  public AddressingVersion getVersion()
  {
    return version;
  }
  
  @NotNull
  public String getAddress()
  {
    return address;
  }
  
  public boolean isAnonymous()
  {
    return address.equals(version.anonymousUri);
  }
  
  public boolean isNone()
  {
    return address.equals(version.noneUri);
  }
  
  private void parse()
    throws XMLStreamException
  {
    StreamReaderBufferProcessor localStreamReaderBufferProcessor = infoset.readAsXMLStreamReader();
    if (localStreamReaderBufferProcessor.getEventType() == 7) {
      localStreamReaderBufferProcessor.nextTag();
    }
    assert (localStreamReaderBufferProcessor.getEventType() == 1);
    String str1 = localStreamReaderBufferProcessor.getLocalName();
    if (!localStreamReaderBufferProcessor.getNamespaceURI().equals(version.nsUri)) {
      throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(version.nsUri, localStreamReaderBufferProcessor.getNamespaceURI()));
    }
    rootElement = new QName(localStreamReaderBufferProcessor.getNamespaceURI(), str1);
    ArrayList localArrayList = null;
    while (localStreamReaderBufferProcessor.nextTag() == 1)
    {
      String str2 = localStreamReaderBufferProcessor.getLocalName();
      if (version.isReferenceParameter(str2))
      {
        XMLStreamBuffer localXMLStreamBuffer;
        while ((localXMLStreamBuffer = localStreamReaderBufferProcessor.nextTagAndMark()) != null)
        {
          if (localArrayList == null) {
            localArrayList = new ArrayList();
          }
          localArrayList.add(version.createReferenceParameterHeader(localXMLStreamBuffer, localStreamReaderBufferProcessor.getNamespaceURI(), localStreamReaderBufferProcessor.getLocalName()));
          XMLStreamReaderUtil.skipElement(localStreamReaderBufferProcessor);
        }
      }
      else if (str2.equals("Address"))
      {
        if (address != null) {
          throw new InvalidAddressingHeaderException(new QName(version.nsUri, str1), AddressingVersion.fault_duplicateAddressInEpr);
        }
        address = localStreamReaderBufferProcessor.getElementText().trim();
      }
      else
      {
        XMLStreamReaderUtil.skipElement(localStreamReaderBufferProcessor);
      }
    }
    if (localArrayList == null) {
      referenceParameters = EMPTY_ARRAY;
    } else {
      referenceParameters = ((Header[])localArrayList.toArray(new Header[localArrayList.size()]));
    }
    if (address == null) {
      throw new InvalidAddressingHeaderException(new QName(version.nsUri, str1), version.fault_missingAddressInEpr);
    }
  }
  
  public XMLStreamReader read(@NotNull final String paramString)
    throws XMLStreamException
  {
    new StreamReaderBufferProcessor(infoset)
    {
      protected void processElement(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, boolean paramAnonymousBoolean)
      {
        if (_depth == 0) {
          paramAnonymousString3 = paramString;
        }
        super.processElement(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3, WSEndpointReference.this.isInscope(infoset, _depth));
      }
    };
  }
  
  private boolean isInscope(XMLStreamBuffer paramXMLStreamBuffer, int paramInt)
  {
    return (paramXMLStreamBuffer.getInscopeNamespaces().size() > 0) && (paramInt == 0);
  }
  
  public Source asSource(@NotNull String paramString)
  {
    return new SAXSource(new SAXBufferProcessorImpl(paramString), new InputSource());
  }
  
  public void writeTo(@NotNull String paramString, ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
    throws SAXException
  {
    SAXBufferProcessorImpl localSAXBufferProcessorImpl = new SAXBufferProcessorImpl(paramString);
    localSAXBufferProcessorImpl.setContentHandler(paramContentHandler);
    localSAXBufferProcessorImpl.setErrorHandler(paramErrorHandler);
    localSAXBufferProcessorImpl.process(infoset, paramBoolean);
  }
  
  public void writeTo(@NotNull final String paramString, @NotNull XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    infoset.writeToXMLStreamWriter(new XMLStreamWriterFilter(paramXMLStreamWriter)
    {
      private boolean root = true;
      
      public void writeStartDocument()
        throws XMLStreamException
      {}
      
      public void writeStartDocument(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {}
      
      public void writeStartDocument(String paramAnonymousString)
        throws XMLStreamException
      {}
      
      public void writeEndDocument()
        throws XMLStreamException
      {}
      
      private String override(String paramAnonymousString)
      {
        if (root)
        {
          root = false;
          return paramString;
        }
        return paramAnonymousString;
      }
      
      public void writeStartElement(String paramAnonymousString)
        throws XMLStreamException
      {
        super.writeStartElement(override(paramAnonymousString));
      }
      
      public void writeStartElement(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        super.writeStartElement(paramAnonymousString1, override(paramAnonymousString2));
      }
      
      public void writeStartElement(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        throws XMLStreamException
      {
        super.writeStartElement(paramAnonymousString1, override(paramAnonymousString2), paramAnonymousString3);
      }
    }, true);
  }
  
  public Header createHeader(QName paramQName)
  {
    return new EPRHeader(paramQName, this);
  }
  
  /**
   * @deprecated
   */
  public void addReferenceParametersToList(HeaderList paramHeaderList)
  {
    for (Header localHeader : referenceParameters) {
      paramHeaderList.add(localHeader);
    }
  }
  
  public void addReferenceParametersToList(MessageHeaders paramMessageHeaders)
  {
    for (Header localHeader : referenceParameters) {
      paramMessageHeaders.add(localHeader);
    }
  }
  
  public void addReferenceParameters(HeaderList paramHeaderList)
  {
    if (paramHeaderList != null)
    {
      Header[] arrayOfHeader = new Header[referenceParameters.length + paramHeaderList.size()];
      System.arraycopy(referenceParameters, 0, arrayOfHeader, 0, referenceParameters.length);
      int i = referenceParameters.length;
      Iterator localIterator = paramHeaderList.iterator();
      while (localIterator.hasNext())
      {
        Header localHeader = (Header)localIterator.next();
        arrayOfHeader[(i++)] = localHeader;
      }
      referenceParameters = arrayOfHeader;
    }
  }
  
  public String toString()
  {
    try
    {
      StringWriter localStringWriter = new StringWriter();
      XmlUtil.newTransformer().transform(asSource("EndpointReference"), new StreamResult(localStringWriter));
      return localStringWriter.toString();
    }
    catch (TransformerException localTransformerException)
    {
      return localTransformerException.toString();
    }
  }
  
  public QName getName()
  {
    return rootElement;
  }
  
  @Nullable
  public EPRExtension getEPRExtension(QName paramQName)
    throws XMLStreamException
  {
    if (rootEprExtensions == null) {
      parseEPRExtensions();
    }
    return (EPRExtension)rootEprExtensions.get(paramQName);
  }
  
  @NotNull
  public Collection<EPRExtension> getEPRExtensions()
    throws XMLStreamException
  {
    if (rootEprExtensions == null) {
      parseEPRExtensions();
    }
    return rootEprExtensions.values();
  }
  
  private void parseEPRExtensions()
    throws XMLStreamException
  {
    rootEprExtensions = new HashMap();
    StreamReaderBufferProcessor localStreamReaderBufferProcessor = infoset.readAsXMLStreamReader();
    if (localStreamReaderBufferProcessor.getEventType() == 7) {
      localStreamReaderBufferProcessor.nextTag();
    }
    assert (localStreamReaderBufferProcessor.getEventType() == 1);
    if (!localStreamReaderBufferProcessor.getNamespaceURI().equals(version.nsUri)) {
      throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(version.nsUri, localStreamReaderBufferProcessor.getNamespaceURI()));
    }
    XMLStreamBuffer localXMLStreamBuffer;
    while ((localXMLStreamBuffer = localStreamReaderBufferProcessor.nextTagAndMark()) != null)
    {
      String str1 = localStreamReaderBufferProcessor.getLocalName();
      String str2 = localStreamReaderBufferProcessor.getNamespaceURI();
      if (version.nsUri.equals(str2))
      {
        XMLStreamReaderUtil.skipElement(localStreamReaderBufferProcessor);
      }
      else
      {
        QName localQName = new QName(str2, str1);
        rootEprExtensions.put(localQName, new WSEPRExtension(localXMLStreamBuffer, localQName));
        XMLStreamReaderUtil.skipElement(localStreamReaderBufferProcessor);
      }
    }
  }
  
  @NotNull
  public Metadata getMetaData()
  {
    return new Metadata(null);
  }
  
  public static abstract class EPRExtension
  {
    public EPRExtension() {}
    
    public abstract XMLStreamReader readAsXMLStreamReader()
      throws XMLStreamException;
    
    public abstract QName getQName();
  }
  
  public class Metadata
  {
    @Nullable
    private QName serviceName;
    @Nullable
    private QName portName;
    @Nullable
    private QName portTypeName;
    @Nullable
    private Source wsdlSource;
    @Nullable
    private String wsdliLocation;
    
    @Nullable
    public QName getServiceName()
    {
      return serviceName;
    }
    
    @Nullable
    public QName getPortName()
    {
      return portName;
    }
    
    @Nullable
    public QName getPortTypeName()
    {
      return portTypeName;
    }
    
    @Nullable
    public Source getWsdlSource()
    {
      return wsdlSource;
    }
    
    @Nullable
    public String getWsdliLocation()
    {
      return wsdliLocation;
    }
    
    private Metadata()
    {
      try
      {
        parseMetaData();
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(localXMLStreamException);
      }
    }
    
    private void parseMetaData()
      throws XMLStreamException
    {
      StreamReaderBufferProcessor localStreamReaderBufferProcessor = infoset.readAsXMLStreamReader();
      if (localStreamReaderBufferProcessor.getEventType() == 7) {
        localStreamReaderBufferProcessor.nextTag();
      }
      assert (localStreamReaderBufferProcessor.getEventType() == 1);
      String str1 = localStreamReaderBufferProcessor.getLocalName();
      if (!localStreamReaderBufferProcessor.getNamespaceURI().equals(version.nsUri)) {
        throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(version.nsUri, localStreamReaderBufferProcessor.getNamespaceURI()));
      }
      Object localObject;
      String str2;
      String str3;
      if (version == AddressingVersion.W3C)
      {
        do
        {
          if (localStreamReaderBufferProcessor.getLocalName().equals(version.eprType.wsdlMetadata.getLocalPart()))
          {
            localObject = localStreamReaderBufferProcessor.getAttributeValue("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
            if (localObject != null) {
              wsdliLocation = ((String)localObject).trim();
            }
            XMLStreamBuffer localXMLStreamBuffer;
            while ((localXMLStreamBuffer = localStreamReaderBufferProcessor.nextTagAndMark()) != null)
            {
              str2 = localStreamReaderBufferProcessor.getLocalName();
              str3 = localStreamReaderBufferProcessor.getNamespaceURI();
              if (str2.equals(version.eprType.serviceName))
              {
                String str4 = localStreamReaderBufferProcessor.getAttributeValue(null, version.eprType.portName);
                if (serviceName != null) {
                  throw new RuntimeException("More than one " + version.eprType.serviceName + " element in EPR Metadata");
                }
                serviceName = getElementTextAsQName(localStreamReaderBufferProcessor);
                if ((serviceName != null) && (str4 != null)) {
                  portName = new QName(serviceName.getNamespaceURI(), str4);
                }
              }
              else if (str2.equals(version.eprType.portTypeName))
              {
                if (portTypeName != null) {
                  throw new RuntimeException("More than one " + version.eprType.portTypeName + " element in EPR Metadata");
                }
                portTypeName = getElementTextAsQName(localStreamReaderBufferProcessor);
              }
              else if ((str3.equals("http://schemas.xmlsoap.org/wsdl/")) && (str2.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())))
              {
                wsdlSource = new XMLStreamBufferSource(localXMLStreamBuffer);
              }
              else
              {
                XMLStreamReaderUtil.skipElement(localStreamReaderBufferProcessor);
              }
            }
          }
          else if (!localStreamReaderBufferProcessor.getLocalName().equals(str1))
          {
            XMLStreamReaderUtil.skipElement(localStreamReaderBufferProcessor);
          }
        } while (XMLStreamReaderUtil.nextElementContent(localStreamReaderBufferProcessor) == 1);
        if (wsdliLocation != null)
        {
          localObject = wsdliLocation.trim();
          localObject = ((String)localObject).substring(wsdliLocation.lastIndexOf(" "));
          wsdlSource = new StreamSource((String)localObject);
        }
      }
      else if (version == AddressingVersion.MEMBER)
      {
        do
        {
          str2 = localStreamReaderBufferProcessor.getLocalName();
          str3 = localStreamReaderBufferProcessor.getNamespaceURI();
          if ((str2.equals(version.eprType.wsdlMetadata.getLocalPart())) && (str3.equals(version.eprType.wsdlMetadata.getNamespaceURI()))) {}
          while (localStreamReaderBufferProcessor.nextTag() == 1)
          {
            while ((localObject = localStreamReaderBufferProcessor.nextTagAndMark()) != null)
            {
              str2 = localStreamReaderBufferProcessor.getLocalName();
              str3 = localStreamReaderBufferProcessor.getNamespaceURI();
              if ((str3.equals("http://schemas.xmlsoap.org/wsdl/")) && (str2.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart()))) {
                wsdlSource = new XMLStreamBufferSource((XMLStreamBuffer)localObject);
              } else {
                XMLStreamReaderUtil.skipElement(localStreamReaderBufferProcessor);
              }
            }
            continue;
            if (str2.equals(version.eprType.serviceName))
            {
              localObject = localStreamReaderBufferProcessor.getAttributeValue(null, version.eprType.portName);
              serviceName = getElementTextAsQName(localStreamReaderBufferProcessor);
              if ((serviceName != null) && (localObject != null)) {
                portName = new QName(serviceName.getNamespaceURI(), (String)localObject);
              }
            }
            else if (str2.equals(version.eprType.portTypeName))
            {
              portTypeName = getElementTextAsQName(localStreamReaderBufferProcessor);
            }
            else if (!localStreamReaderBufferProcessor.getLocalName().equals(str1))
            {
              XMLStreamReaderUtil.skipElement(localStreamReaderBufferProcessor);
            }
          }
        } while (XMLStreamReaderUtil.nextElementContent(localStreamReaderBufferProcessor) == 1);
      }
    }
    
    private QName getElementTextAsQName(StreamReaderBufferProcessor paramStreamReaderBufferProcessor)
      throws XMLStreamException
    {
      String str1 = paramStreamReaderBufferProcessor.getElementText().trim();
      String str2 = XmlUtil.getPrefix(str1);
      String str3 = XmlUtil.getLocalPart(str1);
      if (str3 != null) {
        if (str2 != null)
        {
          String str4 = paramStreamReaderBufferProcessor.getNamespaceURI(str2);
          if (str4 != null) {
            return new QName(str4, str3, str2);
          }
        }
        else
        {
          return new QName(null, str3);
        }
      }
      return null;
    }
  }
  
  class SAXBufferProcessorImpl
    extends SAXBufferProcessor
  {
    private final String rootLocalName;
    private boolean root = true;
    
    public SAXBufferProcessorImpl(String paramString)
    {
      super(false);
      rootLocalName = paramString;
    }
    
    protected void processElement(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
      throws SAXException
    {
      if (root)
      {
        root = false;
        if (paramString3.equals(paramString2))
        {
          paramString3 = paramString2 = rootLocalName;
        }
        else
        {
          paramString2 = rootLocalName;
          int i = paramString3.indexOf(':');
          paramString3 = paramString3.substring(0, i + 1) + rootLocalName;
        }
      }
      super.processElement(paramString1, paramString2, paramString3, paramBoolean);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\addressing\WSEndpointReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */