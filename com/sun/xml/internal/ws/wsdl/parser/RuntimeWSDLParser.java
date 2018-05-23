package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.BindingIDFactory;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSDLLocator;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLDescriptorKind;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.MetaDataResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.MetadataResolverFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.ServiceDescriptor;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver.Parser;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundFaultImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundOperationImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundPortTypeImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLFaultImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLInputImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLMessageImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLModelImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLOperationImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLOutputImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPartDescriptorImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPartImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortTypeImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLServiceImpl;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.resources.WsdlmodelMessages;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class RuntimeWSDLParser
{
  private final EditableWSDLModel wsdlDoc;
  private String targetNamespace;
  private final Set<String> importedWSDLs = new HashSet();
  private final XMLEntityResolver resolver;
  private final PolicyResolver policyResolver;
  private final WSDLParserExtension extensionFacade;
  private final WSDLParserExtensionContextImpl context;
  List<WSDLParserExtension> extensions;
  Map<String, String> wsdldef_nsdecl = new HashMap();
  Map<String, String> service_nsdecl = new HashMap();
  Map<String, String> port_nsdecl = new HashMap();
  private static final Logger LOGGER = Logger.getLogger(RuntimeWSDLParser.class.getName());
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, WSDLParserExtension... paramVarArgs)
    throws IOException, XMLStreamException, SAXException
  {
    return parse(paramURL, paramSource, paramEntityResolver, paramBoolean, paramContainer, Service.class, PolicyResolverFactory.create(), paramVarArgs);
  }
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, Class paramClass, WSDLParserExtension... paramVarArgs)
    throws IOException, XMLStreamException, SAXException
  {
    return parse(paramURL, paramSource, paramEntityResolver, paramBoolean, paramContainer, paramClass, PolicyResolverFactory.create(), paramVarArgs);
  }
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, @NotNull PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs)
    throws IOException, XMLStreamException, SAXException
  {
    return parse(paramURL, paramSource, paramEntityResolver, paramBoolean, paramContainer, Service.class, paramPolicyResolver, paramVarArgs);
  }
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, Class paramClass, @NotNull PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs)
    throws IOException, XMLStreamException, SAXException
  {
    return parse(paramURL, paramSource, paramEntityResolver, paramBoolean, paramContainer, paramClass, paramPolicyResolver, false, paramVarArgs);
  }
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean1, Container paramContainer, Class paramClass, @NotNull PolicyResolver paramPolicyResolver, boolean paramBoolean2, WSDLParserExtension... paramVarArgs)
    throws IOException, XMLStreamException, SAXException
  {
    assert (paramEntityResolver != null);
    RuntimeWSDLParser localRuntimeWSDLParser = new RuntimeWSDLParser(paramSource.getSystemId(), new EntityResolverWrapper(paramEntityResolver, paramBoolean2), paramBoolean1, paramContainer, paramPolicyResolver, paramVarArgs);
    XMLEntityResolver.Parser localParser;
    try
    {
      localParser = localRuntimeWSDLParser.resolveWSDL(paramURL, paramSource, paramClass);
      if (!hasWSDLDefinitions(parser)) {
        throw new XMLStreamException(ClientMessages.RUNTIME_WSDLPARSER_INVALID_WSDL(systemId, WSDLConstants.QNAME_DEFINITIONS, parser.getName(), parser.getLocation()));
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      if (paramURL == null) {
        throw localXMLStreamException;
      }
      return tryWithMex(localRuntimeWSDLParser, paramURL, paramEntityResolver, paramBoolean1, paramContainer, localXMLStreamException, paramClass, paramPolicyResolver, paramVarArgs);
    }
    catch (IOException localIOException)
    {
      if (paramURL == null) {
        throw localIOException;
      }
      return tryWithMex(localRuntimeWSDLParser, paramURL, paramEntityResolver, paramBoolean1, paramContainer, localIOException, paramClass, paramPolicyResolver, paramVarArgs);
    }
    extensionFacade.start(context);
    localRuntimeWSDLParser.parseWSDL(localParser, false);
    wsdlDoc.freeze();
    extensionFacade.finished(context);
    extensionFacade.postFinished(context);
    if (wsdlDoc.getServices().isEmpty()) {
      throw new WebServiceException(ClientMessages.WSDL_CONTAINS_NO_SERVICE(paramURL));
    }
    return wsdlDoc;
  }
  
  private static WSDLModel tryWithMex(@NotNull RuntimeWSDLParser paramRuntimeWSDLParser, @NotNull URL paramURL, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, Throwable paramThrowable, Class paramClass, PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs)
    throws SAXException, XMLStreamException
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      WSDLModel localWSDLModel = paramRuntimeWSDLParser.parseUsingMex(paramURL, paramEntityResolver, paramBoolean, paramContainer, paramClass, paramPolicyResolver, paramVarArgs);
      if (localWSDLModel == null) {
        throw new WebServiceException(ClientMessages.FAILED_TO_PARSE(paramURL.toExternalForm(), paramThrowable.getMessage()), paramThrowable);
      }
      return localWSDLModel;
    }
    catch (URISyntaxException localURISyntaxException)
    {
      localArrayList.add(paramThrowable);
      localArrayList.add(localURISyntaxException);
    }
    catch (IOException localIOException)
    {
      localArrayList.add(paramThrowable);
      localArrayList.add(localIOException);
    }
    throw new InaccessibleWSDLException(localArrayList);
  }
  
  private WSDLModel parseUsingMex(@NotNull URL paramURL, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, Class paramClass, PolicyResolver paramPolicyResolver, WSDLParserExtension[] paramArrayOfWSDLParserExtension)
    throws IOException, SAXException, XMLStreamException, URISyntaxException
  {
    MetaDataResolver localMetaDataResolver = null;
    ServiceDescriptor localServiceDescriptor = null;
    RuntimeWSDLParser localRuntimeWSDLParser = null;
    Object localObject1 = ServiceFinder.find(MetadataResolverFactory.class).iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (MetadataResolverFactory)((Iterator)localObject1).next();
      localMetaDataResolver = ((MetadataResolverFactory)localObject2).metadataResolver(paramEntityResolver);
      localServiceDescriptor = localMetaDataResolver.resolve(paramURL.toURI());
      if (localServiceDescriptor != null) {
        break;
      }
    }
    if (localServiceDescriptor != null)
    {
      localObject1 = localServiceDescriptor.getWSDLs();
      localRuntimeWSDLParser = new RuntimeWSDLParser(paramURL.toExternalForm(), new MexEntityResolver((List)localObject1), paramBoolean, paramContainer, paramPolicyResolver, paramArrayOfWSDLParserExtension);
      extensionFacade.start(context);
      localObject2 = ((List)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Source localSource = (Source)((Iterator)localObject2).next();
        String str = localSource.getSystemId();
        XMLEntityResolver.Parser localParser = resolver.resolveEntity(null, str);
        localRuntimeWSDLParser.parseWSDL(localParser, false);
      }
    }
    if (((localMetaDataResolver == null) || (localServiceDescriptor == null)) && ((paramURL.getProtocol().equals("http")) || (paramURL.getProtocol().equals("https"))) && (paramURL.getQuery() == null))
    {
      localObject1 = paramURL.toExternalForm();
      localObject1 = (String)localObject1 + "?wsdl";
      paramURL = new URL((String)localObject1);
      localRuntimeWSDLParser = new RuntimeWSDLParser(paramURL.toExternalForm(), new EntityResolverWrapper(paramEntityResolver), paramBoolean, paramContainer, paramPolicyResolver, paramArrayOfWSDLParserExtension);
      extensionFacade.start(context);
      localObject2 = resolveWSDL(paramURL, new StreamSource(paramURL.toExternalForm()), paramClass);
      localRuntimeWSDLParser.parseWSDL((XMLEntityResolver.Parser)localObject2, false);
    }
    if (localRuntimeWSDLParser == null) {
      return null;
    }
    wsdlDoc.freeze();
    extensionFacade.finished(context);
    extensionFacade.postFinished(context);
    return wsdlDoc;
  }
  
  private static boolean hasWSDLDefinitions(XMLStreamReader paramXMLStreamReader)
  {
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    return paramXMLStreamReader.getName().equals(WSDLConstants.QNAME_DEFINITIONS);
  }
  
  public static WSDLModel parse(XMLEntityResolver.Parser paramParser, XMLEntityResolver paramXMLEntityResolver, boolean paramBoolean, Container paramContainer, PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs)
    throws IOException, XMLStreamException, SAXException
  {
    assert (paramXMLEntityResolver != null);
    RuntimeWSDLParser localRuntimeWSDLParser = new RuntimeWSDLParser(systemId.toExternalForm(), paramXMLEntityResolver, paramBoolean, paramContainer, paramPolicyResolver, paramVarArgs);
    extensionFacade.start(context);
    localRuntimeWSDLParser.parseWSDL(paramParser, false);
    wsdlDoc.freeze();
    extensionFacade.finished(context);
    extensionFacade.postFinished(context);
    return wsdlDoc;
  }
  
  public static WSDLModel parse(XMLEntityResolver.Parser paramParser, XMLEntityResolver paramXMLEntityResolver, boolean paramBoolean, Container paramContainer, WSDLParserExtension... paramVarArgs)
    throws IOException, XMLStreamException, SAXException
  {
    assert (paramXMLEntityResolver != null);
    RuntimeWSDLParser localRuntimeWSDLParser = new RuntimeWSDLParser(systemId.toExternalForm(), paramXMLEntityResolver, paramBoolean, paramContainer, PolicyResolverFactory.create(), paramVarArgs);
    extensionFacade.start(context);
    localRuntimeWSDLParser.parseWSDL(paramParser, false);
    wsdlDoc.freeze();
    extensionFacade.finished(context);
    extensionFacade.postFinished(context);
    return wsdlDoc;
  }
  
  private RuntimeWSDLParser(@NotNull String paramString, XMLEntityResolver paramXMLEntityResolver, boolean paramBoolean, Container paramContainer, PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs)
  {
    wsdlDoc = (paramString != null ? new WSDLModelImpl(paramString) : new WSDLModelImpl());
    resolver = paramXMLEntityResolver;
    policyResolver = paramPolicyResolver;
    extensions = new ArrayList();
    context = new WSDLParserExtensionContextImpl(wsdlDoc, paramBoolean, paramContainer, paramPolicyResolver);
    int i = 0;
    for (WSDLParserExtension localWSDLParserExtension : paramVarArgs)
    {
      if ((localWSDLParserExtension instanceof com.sun.xml.internal.ws.api.wsdl.parser.PolicyWSDLParserExtension)) {
        i = 1;
      }
      register(localWSDLParserExtension);
    }
    if (i == 0) {
      register(new com.sun.xml.internal.ws.policy.jaxws.PolicyWSDLParserExtension());
    }
    register(new MemberSubmissionAddressingWSDLParserExtension());
    register(new W3CAddressingWSDLParserExtension());
    register(new W3CAddressingMetadataWSDLParserExtension());
    extensionFacade = new WSDLParserExtensionFacade((WSDLParserExtension[])extensions.toArray(new WSDLParserExtension[0]));
  }
  
  private XMLEntityResolver.Parser resolveWSDL(@Nullable URL paramURL, @NotNull Source paramSource, Class paramClass)
    throws IOException, SAXException, XMLStreamException
  {
    String str1 = paramSource.getSystemId();
    XMLEntityResolver.Parser localParser = resolver.resolveEntity(null, str1);
    if ((localParser == null) && (paramURL != null))
    {
      String str2 = paramURL.toExternalForm();
      localParser = resolver.resolveEntity(null, str2);
      if ((localParser == null) && (paramClass != null))
      {
        URL localURL = paramClass.getResource(".");
        if (localURL != null)
        {
          String str3 = localURL.toExternalForm();
          if (str2.startsWith(str3)) {
            localParser = resolver.resolveEntity(null, str2.substring(str3.length()));
          }
        }
      }
    }
    if (localParser == null)
    {
      if (isKnownReadableSource(paramSource)) {
        localParser = new XMLEntityResolver.Parser(paramURL, createReader(paramSource));
      } else if (paramURL != null) {
        localParser = new XMLEntityResolver.Parser(paramURL, createReader(paramURL, paramClass));
      }
      if (localParser == null) {
        localParser = new XMLEntityResolver.Parser(paramURL, createReader(paramSource));
      }
    }
    return localParser;
  }
  
  private boolean isKnownReadableSource(Source paramSource)
  {
    if ((paramSource instanceof StreamSource)) {
      return (((StreamSource)paramSource).getInputStream() != null) || (((StreamSource)paramSource).getReader() != null);
    }
    return false;
  }
  
  private XMLStreamReader createReader(@NotNull Source paramSource)
    throws XMLStreamException
  {
    return new TidyXMLStreamReader(SourceReaderFactory.createSourceReader(paramSource, true), null);
  }
  
  private void parseImport(@NotNull URL paramURL)
    throws XMLStreamException, IOException, SAXException
  {
    String str = paramURL.toExternalForm();
    XMLEntityResolver.Parser localParser = resolver.resolveEntity(null, str);
    if (localParser == null) {
      localParser = new XMLEntityResolver.Parser(paramURL, createReader(paramURL));
    }
    parseWSDL(localParser, true);
  }
  
  private void parseWSDL(XMLEntityResolver.Parser paramParser, boolean paramBoolean)
    throws XMLStreamException, IOException, SAXException
  {
    XMLStreamReader localXMLStreamReader = parser;
    try
    {
      if ((systemId != null) && (!importedWSDLs.add(systemId.toExternalForm()))) {
        return;
      }
      if (localXMLStreamReader.getEventType() == 7) {
        XMLStreamReaderUtil.nextElementContent(localXMLStreamReader);
      }
      if (WSDLConstants.QNAME_DEFINITIONS.equals(localXMLStreamReader.getName())) {
        readNSDecl(wsdldef_nsdecl, localXMLStreamReader);
      }
      if ((localXMLStreamReader.getEventType() != 8) && (localXMLStreamReader.getName().equals(WSDLConstants.QNAME_SCHEMA)) && (paramBoolean))
      {
        LOGGER.warning(WsdlmodelMessages.WSDL_IMPORT_SHOULD_BE_WSDL(systemId));
        return;
      }
      String str1 = ParserUtil.getMandatoryNonEmptyAttribute(localXMLStreamReader, "targetNamespace");
      String str2 = targetNamespace;
      targetNamespace = str1;
      while ((XMLStreamReaderUtil.nextElementContent(localXMLStreamReader) != 2) && (localXMLStreamReader.getEventType() != 8))
      {
        QName localQName = localXMLStreamReader.getName();
        if (WSDLConstants.QNAME_IMPORT.equals(localQName)) {
          parseImport(systemId, localXMLStreamReader);
        } else if (WSDLConstants.QNAME_MESSAGE.equals(localQName)) {
          parseMessage(localXMLStreamReader);
        } else if (WSDLConstants.QNAME_PORT_TYPE.equals(localQName)) {
          parsePortType(localXMLStreamReader);
        } else if (WSDLConstants.QNAME_BINDING.equals(localQName)) {
          parseBinding(localXMLStreamReader);
        } else if (WSDLConstants.QNAME_SERVICE.equals(localQName)) {
          parseService(localXMLStreamReader);
        } else {
          extensionFacade.definitionsElements(localXMLStreamReader);
        }
      }
      targetNamespace = str2;
    }
    finally
    {
      wsdldef_nsdecl = new HashMap();
      localXMLStreamReader.close();
    }
  }
  
  private void parseService(XMLStreamReader paramXMLStreamReader)
  {
    service_nsdecl.putAll(wsdldef_nsdecl);
    readNSDecl(service_nsdecl, paramXMLStreamReader);
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    WSDLServiceImpl localWSDLServiceImpl = new WSDLServiceImpl(paramXMLStreamReader, wsdlDoc, new QName(targetNamespace, str));
    extensionFacade.serviceAttributes(localWSDLServiceImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName = paramXMLStreamReader.getName();
      if (WSDLConstants.QNAME_PORT.equals(localQName))
      {
        parsePort(paramXMLStreamReader, localWSDLServiceImpl);
        if (paramXMLStreamReader.getEventType() != 2) {
          XMLStreamReaderUtil.next(paramXMLStreamReader);
        }
      }
      else
      {
        extensionFacade.serviceElements(localWSDLServiceImpl, paramXMLStreamReader);
      }
    }
    wsdlDoc.addService(localWSDLServiceImpl);
    service_nsdecl = new HashMap();
  }
  
  private void parsePort(XMLStreamReader paramXMLStreamReader, EditableWSDLService paramEditableWSDLService)
  {
    port_nsdecl.putAll(service_nsdecl);
    readNSDecl(port_nsdecl, paramXMLStreamReader);
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    String str2 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "binding");
    QName localQName1 = ParserUtil.getQName(paramXMLStreamReader, str2);
    QName localQName2 = new QName(paramEditableWSDLService.getName().getNamespaceURI(), str1);
    WSDLPortImpl localWSDLPortImpl = new WSDLPortImpl(paramXMLStreamReader, paramEditableWSDLService, localQName2, localQName1);
    extensionFacade.portAttributes(localWSDLPortImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName3 = paramXMLStreamReader.getName();
      if ((SOAPConstants.QNAME_ADDRESS.equals(localQName3)) || (SOAPConstants.QNAME_SOAP12ADDRESS.equals(localQName3)))
      {
        String str3 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "location");
        if (str3 != null) {
          try
          {
            localWSDLPortImpl.setAddress(new EndpointAddress(str3));
          }
          catch (URISyntaxException localURISyntaxException2) {}
        }
        XMLStreamReaderUtil.next(paramXMLStreamReader);
      }
      else
      {
        if ((W3CnsUri.equals(localQName3.getNamespaceURI())) && ("EndpointReference".equals(localQName3.getLocalPart()))) {
          try
          {
            StreamReaderBufferCreator localStreamReaderBufferCreator = new StreamReaderBufferCreator(new MutableXMLStreamBuffer());
            XMLStreamBufferMark localXMLStreamBufferMark = new XMLStreamBufferMark(port_nsdecl, localStreamReaderBufferCreator);
            localStreamReaderBufferCreator.createElementFragment(paramXMLStreamReader, false);
            WSEndpointReference localWSEndpointReference = new WSEndpointReference(localXMLStreamBufferMark, AddressingVersion.W3C);
            localWSDLPortImpl.setEPR(localWSEndpointReference);
            if ((paramXMLStreamReader.getEventType() == 2) && (paramXMLStreamReader.getName().equals(WSDLConstants.QNAME_PORT))) {
              break;
            }
          }
          catch (XMLStreamException localXMLStreamException)
          {
            throw new WebServiceException(localXMLStreamException);
          }
        }
        extensionFacade.portElements(localWSDLPortImpl, paramXMLStreamReader);
      }
    }
    if (localWSDLPortImpl.getAddress() == null) {
      try
      {
        localWSDLPortImpl.setAddress(new EndpointAddress(""));
      }
      catch (URISyntaxException localURISyntaxException1) {}
    }
    paramEditableWSDLService.put(localQName2, localWSDLPortImpl);
    port_nsdecl = new HashMap();
  }
  
  private void parseBinding(XMLStreamReader paramXMLStreamReader)
  {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    String str2 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "type");
    if ((str1 == null) || (str2 == null))
    {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return;
    }
    WSDLBoundPortTypeImpl localWSDLBoundPortTypeImpl = new WSDLBoundPortTypeImpl(paramXMLStreamReader, wsdlDoc, new QName(targetNamespace, str1), ParserUtil.getQName(paramXMLStreamReader, str2));
    extensionFacade.bindingAttributes(localWSDLBoundPortTypeImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName = paramXMLStreamReader.getName();
      String str3;
      String str4;
      if (WSDLConstants.NS_SOAP_BINDING.equals(localQName))
      {
        str3 = paramXMLStreamReader.getAttributeValue(null, "transport");
        localWSDLBoundPortTypeImpl.setBindingId(createBindingId(str3, SOAPVersion.SOAP_11));
        str4 = paramXMLStreamReader.getAttributeValue(null, "style");
        if ((str4 != null) && (str4.equals("rpc"))) {
          localWSDLBoundPortTypeImpl.setStyle(SOAPBinding.Style.RPC);
        } else {
          localWSDLBoundPortTypeImpl.setStyle(SOAPBinding.Style.DOCUMENT);
        }
        goToEnd(paramXMLStreamReader);
      }
      else if (WSDLConstants.NS_SOAP12_BINDING.equals(localQName))
      {
        str3 = paramXMLStreamReader.getAttributeValue(null, "transport");
        localWSDLBoundPortTypeImpl.setBindingId(createBindingId(str3, SOAPVersion.SOAP_12));
        str4 = paramXMLStreamReader.getAttributeValue(null, "style");
        if ((str4 != null) && (str4.equals("rpc"))) {
          localWSDLBoundPortTypeImpl.setStyle(SOAPBinding.Style.RPC);
        } else {
          localWSDLBoundPortTypeImpl.setStyle(SOAPBinding.Style.DOCUMENT);
        }
        goToEnd(paramXMLStreamReader);
      }
      else if (WSDLConstants.QNAME_OPERATION.equals(localQName))
      {
        parseBindingOperation(paramXMLStreamReader, localWSDLBoundPortTypeImpl);
      }
      else
      {
        extensionFacade.bindingElements(localWSDLBoundPortTypeImpl, paramXMLStreamReader);
      }
    }
  }
  
  private static BindingID createBindingId(String paramString, SOAPVersion paramSOAPVersion)
  {
    if (!paramString.equals("http://schemas.xmlsoap.org/soap/http"))
    {
      Iterator localIterator = ServiceFinder.find(BindingIDFactory.class).iterator();
      while (localIterator.hasNext())
      {
        BindingIDFactory localBindingIDFactory = (BindingIDFactory)localIterator.next();
        BindingID localBindingID = localBindingIDFactory.create(paramString, paramSOAPVersion);
        if (localBindingID != null) {
          return localBindingID;
        }
      }
    }
    return paramSOAPVersion.equals(SOAPVersion.SOAP_11) ? BindingID.SOAP11_HTTP : BindingID.SOAP12_HTTP;
  }
  
  private void parseBindingOperation(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundPortType paramEditableWSDLBoundPortType)
  {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    if (str1 == null)
    {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return;
    }
    QName localQName1 = new QName(paramEditableWSDLBoundPortType.getPortTypeName().getNamespaceURI(), str1);
    WSDLBoundOperationImpl localWSDLBoundOperationImpl = new WSDLBoundOperationImpl(paramXMLStreamReader, paramEditableWSDLBoundPortType, localQName1);
    paramEditableWSDLBoundPortType.put(localQName1, localWSDLBoundOperationImpl);
    extensionFacade.bindingOperationAttributes(localWSDLBoundOperationImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName2 = paramXMLStreamReader.getName();
      String str2 = null;
      if (WSDLConstants.QNAME_INPUT.equals(localQName2))
      {
        parseInputBinding(paramXMLStreamReader, localWSDLBoundOperationImpl);
      }
      else if (WSDLConstants.QNAME_OUTPUT.equals(localQName2))
      {
        parseOutputBinding(paramXMLStreamReader, localWSDLBoundOperationImpl);
      }
      else if (WSDLConstants.QNAME_FAULT.equals(localQName2))
      {
        parseFaultBinding(paramXMLStreamReader, localWSDLBoundOperationImpl);
      }
      else if ((SOAPConstants.QNAME_OPERATION.equals(localQName2)) || (SOAPConstants.QNAME_SOAP12OPERATION.equals(localQName2)))
      {
        str2 = paramXMLStreamReader.getAttributeValue(null, "style");
        String str3 = paramXMLStreamReader.getAttributeValue(null, "soapAction");
        if (str3 != null) {
          localWSDLBoundOperationImpl.setSoapAction(str3);
        }
        goToEnd(paramXMLStreamReader);
      }
      else
      {
        extensionFacade.bindingOperationElements(localWSDLBoundOperationImpl, paramXMLStreamReader);
      }
      if (str2 != null)
      {
        if (str2.equals("rpc")) {
          localWSDLBoundOperationImpl.setStyle(SOAPBinding.Style.RPC);
        } else {
          localWSDLBoundOperationImpl.setStyle(SOAPBinding.Style.DOCUMENT);
        }
      }
      else {
        localWSDLBoundOperationImpl.setStyle(paramEditableWSDLBoundPortType.getStyle());
      }
    }
  }
  
  private void parseInputBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation)
  {
    int i = 0;
    extensionFacade.bindingOperationInputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName = paramXMLStreamReader.getName();
      if (((SOAPConstants.QNAME_BODY.equals(localQName)) || (SOAPConstants.QNAME_SOAP12BODY.equals(localQName))) && (i == 0))
      {
        i = 1;
        paramEditableWSDLBoundOperation.setInputExplicitBodyParts(parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, BindingMode.INPUT));
        goToEnd(paramXMLStreamReader);
      }
      else if ((SOAPConstants.QNAME_HEADER.equals(localQName)) || (SOAPConstants.QNAME_SOAP12HEADER.equals(localQName)))
      {
        parseSOAPHeaderBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation.getInputParts());
      }
      else if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(localQName))
      {
        parseMimeMultipartBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, BindingMode.INPUT);
      }
      else
      {
        extensionFacade.bindingOperationInputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader);
      }
    }
  }
  
  private void parseOutputBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation)
  {
    int i = 0;
    extensionFacade.bindingOperationOutputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName = paramXMLStreamReader.getName();
      if (((SOAPConstants.QNAME_BODY.equals(localQName)) || (SOAPConstants.QNAME_SOAP12BODY.equals(localQName))) && (i == 0))
      {
        i = 1;
        paramEditableWSDLBoundOperation.setOutputExplicitBodyParts(parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, BindingMode.OUTPUT));
        goToEnd(paramXMLStreamReader);
      }
      else if ((SOAPConstants.QNAME_HEADER.equals(localQName)) || (SOAPConstants.QNAME_SOAP12HEADER.equals(localQName)))
      {
        parseSOAPHeaderBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation.getOutputParts());
      }
      else if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(localQName))
      {
        parseMimeMultipartBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, BindingMode.OUTPUT);
      }
      else
      {
        extensionFacade.bindingOperationOutputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader);
      }
    }
  }
  
  private void parseFaultBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation)
  {
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    WSDLBoundFaultImpl localWSDLBoundFaultImpl = new WSDLBoundFaultImpl(paramXMLStreamReader, str, paramEditableWSDLBoundOperation);
    paramEditableWSDLBoundOperation.addFault(localWSDLBoundFaultImpl);
    extensionFacade.bindingOperationFaultAttributes(localWSDLBoundFaultImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      extensionFacade.bindingOperationFaultElements(localWSDLBoundFaultImpl, paramXMLStreamReader);
    }
  }
  
  private static boolean parseSOAPBodyBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation, BindingMode paramBindingMode)
  {
    String str = paramXMLStreamReader.getAttributeValue(null, "namespace");
    if (paramBindingMode == BindingMode.INPUT)
    {
      paramEditableWSDLBoundOperation.setRequestNamespace(str);
      return parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation.getInputParts());
    }
    paramEditableWSDLBoundOperation.setResponseNamespace(str);
    return parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation.getOutputParts());
  }
  
  private static boolean parseSOAPBodyBinding(XMLStreamReader paramXMLStreamReader, Map<String, ParameterBinding> paramMap)
  {
    String str1 = paramXMLStreamReader.getAttributeValue(null, "parts");
    if (str1 != null)
    {
      List localList = XmlUtil.parseTokenList(str1);
      if (localList.isEmpty())
      {
        paramMap.put(" ", ParameterBinding.BODY);
      }
      else
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          String str2 = (String)localIterator.next();
          paramMap.put(str2, ParameterBinding.BODY);
        }
      }
      return true;
    }
    return false;
  }
  
  private static void parseSOAPHeaderBinding(XMLStreamReader paramXMLStreamReader, Map<String, ParameterBinding> paramMap)
  {
    String str = paramXMLStreamReader.getAttributeValue(null, "part");
    if ((str == null) || (str.equals(""))) {
      return;
    }
    paramMap.put(str, ParameterBinding.HEADER);
    goToEnd(paramXMLStreamReader);
  }
  
  private static void parseMimeMultipartBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation, BindingMode paramBindingMode)
  {
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName = paramXMLStreamReader.getName();
      if (MIMEConstants.QNAME_PART.equals(localQName)) {
        parseMIMEPart(paramXMLStreamReader, paramEditableWSDLBoundOperation, paramBindingMode);
      } else {
        XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      }
    }
  }
  
  private static void parseMIMEPart(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation, BindingMode paramBindingMode)
  {
    int i = 0;
    Map localMap = null;
    if (paramBindingMode == BindingMode.INPUT) {
      localMap = paramEditableWSDLBoundOperation.getInputParts();
    } else if (paramBindingMode == BindingMode.OUTPUT) {
      localMap = paramEditableWSDLBoundOperation.getOutputParts();
    } else if (paramBindingMode == BindingMode.FAULT) {
      localMap = paramEditableWSDLBoundOperation.getFaultParts();
    }
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName = paramXMLStreamReader.getName();
      if ((SOAPConstants.QNAME_BODY.equals(localQName)) && (i == 0))
      {
        i = 1;
        parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, paramBindingMode);
        XMLStreamReaderUtil.next(paramXMLStreamReader);
      }
      else if (SOAPConstants.QNAME_HEADER.equals(localQName))
      {
        i = 1;
        parseSOAPHeaderBinding(paramXMLStreamReader, localMap);
        XMLStreamReaderUtil.next(paramXMLStreamReader);
      }
      else if (MIMEConstants.QNAME_CONTENT.equals(localQName))
      {
        String str1 = paramXMLStreamReader.getAttributeValue(null, "part");
        String str2 = paramXMLStreamReader.getAttributeValue(null, "type");
        if ((str1 == null) || (str2 == null))
        {
          XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
        }
        else
        {
          ParameterBinding localParameterBinding = ParameterBinding.createAttachment(str2);
          if ((localMap != null) && (localParameterBinding != null) && (str1 != null)) {
            localMap.put(str1, localParameterBinding);
          }
          XMLStreamReaderUtil.next(paramXMLStreamReader);
        }
      }
      else
      {
        XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      }
    }
  }
  
  protected void parseImport(@Nullable URL paramURL, XMLStreamReader paramXMLStreamReader)
    throws IOException, SAXException, XMLStreamException
  {
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "location");
    URL localURL;
    if (paramURL != null) {
      localURL = new URL(paramURL, str);
    } else {
      localURL = new URL(str);
    }
    parseImport(localURL);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    }
  }
  
  private void parsePortType(XMLStreamReader paramXMLStreamReader)
  {
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    if (str == null)
    {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return;
    }
    WSDLPortTypeImpl localWSDLPortTypeImpl = new WSDLPortTypeImpl(paramXMLStreamReader, wsdlDoc, new QName(targetNamespace, str));
    extensionFacade.portTypeAttributes(localWSDLPortTypeImpl, paramXMLStreamReader);
    wsdlDoc.addPortType(localWSDLPortTypeImpl);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName = paramXMLStreamReader.getName();
      if (WSDLConstants.QNAME_OPERATION.equals(localQName)) {
        parsePortTypeOperation(paramXMLStreamReader, localWSDLPortTypeImpl);
      } else {
        extensionFacade.portTypeElements(localWSDLPortTypeImpl, paramXMLStreamReader);
      }
    }
  }
  
  private void parsePortTypeOperation(XMLStreamReader paramXMLStreamReader, EditableWSDLPortType paramEditableWSDLPortType)
  {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    if (str1 == null)
    {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return;
    }
    QName localQName1 = new QName(paramEditableWSDLPortType.getName().getNamespaceURI(), str1);
    WSDLOperationImpl localWSDLOperationImpl = new WSDLOperationImpl(paramXMLStreamReader, paramEditableWSDLPortType, localQName1);
    extensionFacade.portTypeOperationAttributes(localWSDLOperationImpl, paramXMLStreamReader);
    String str2 = ParserUtil.getAttribute(paramXMLStreamReader, "parameterOrder");
    localWSDLOperationImpl.setParameterOrder(str2);
    paramEditableWSDLPortType.put(str1, localWSDLOperationImpl);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName2 = paramXMLStreamReader.getName();
      if (localQName2.equals(WSDLConstants.QNAME_INPUT)) {
        parsePortTypeOperationInput(paramXMLStreamReader, localWSDLOperationImpl);
      } else if (localQName2.equals(WSDLConstants.QNAME_OUTPUT)) {
        parsePortTypeOperationOutput(paramXMLStreamReader, localWSDLOperationImpl);
      } else if (localQName2.equals(WSDLConstants.QNAME_FAULT)) {
        parsePortTypeOperationFault(paramXMLStreamReader, localWSDLOperationImpl);
      } else {
        extensionFacade.portTypeOperationElements(localWSDLOperationImpl, paramXMLStreamReader);
      }
    }
  }
  
  private void parsePortTypeOperationFault(XMLStreamReader paramXMLStreamReader, EditableWSDLOperation paramEditableWSDLOperation)
  {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "message");
    QName localQName = ParserUtil.getQName(paramXMLStreamReader, str1);
    String str2 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    WSDLFaultImpl localWSDLFaultImpl = new WSDLFaultImpl(paramXMLStreamReader, str2, localQName, paramEditableWSDLOperation);
    paramEditableWSDLOperation.addFault(localWSDLFaultImpl);
    extensionFacade.portTypeOperationFaultAttributes(localWSDLFaultImpl, paramXMLStreamReader);
    extensionFacade.portTypeOperationFault(paramEditableWSDLOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      extensionFacade.portTypeOperationFaultElements(localWSDLFaultImpl, paramXMLStreamReader);
    }
  }
  
  private void parsePortTypeOperationInput(XMLStreamReader paramXMLStreamReader, EditableWSDLOperation paramEditableWSDLOperation)
  {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "message");
    QName localQName = ParserUtil.getQName(paramXMLStreamReader, str1);
    String str2 = ParserUtil.getAttribute(paramXMLStreamReader, "name");
    WSDLInputImpl localWSDLInputImpl = new WSDLInputImpl(paramXMLStreamReader, str2, localQName, paramEditableWSDLOperation);
    paramEditableWSDLOperation.setInput(localWSDLInputImpl);
    extensionFacade.portTypeOperationInputAttributes(localWSDLInputImpl, paramXMLStreamReader);
    extensionFacade.portTypeOperationInput(paramEditableWSDLOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      extensionFacade.portTypeOperationInputElements(localWSDLInputImpl, paramXMLStreamReader);
    }
  }
  
  private void parsePortTypeOperationOutput(XMLStreamReader paramXMLStreamReader, EditableWSDLOperation paramEditableWSDLOperation)
  {
    String str1 = ParserUtil.getAttribute(paramXMLStreamReader, "message");
    QName localQName = ParserUtil.getQName(paramXMLStreamReader, str1);
    String str2 = ParserUtil.getAttribute(paramXMLStreamReader, "name");
    WSDLOutputImpl localWSDLOutputImpl = new WSDLOutputImpl(paramXMLStreamReader, str2, localQName, paramEditableWSDLOperation);
    paramEditableWSDLOperation.setOutput(localWSDLOutputImpl);
    extensionFacade.portTypeOperationOutputAttributes(localWSDLOutputImpl, paramXMLStreamReader);
    extensionFacade.portTypeOperationOutput(paramEditableWSDLOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      extensionFacade.portTypeOperationOutputElements(localWSDLOutputImpl, paramXMLStreamReader);
    }
  }
  
  private void parseMessage(XMLStreamReader paramXMLStreamReader)
  {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    WSDLMessageImpl localWSDLMessageImpl = new WSDLMessageImpl(paramXMLStreamReader, new QName(targetNamespace, str1));
    extensionFacade.messageAttributes(localWSDLMessageImpl, paramXMLStreamReader);
    int i = 0;
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
    {
      QName localQName1 = paramXMLStreamReader.getName();
      if (WSDLConstants.QNAME_PART.equals(localQName1))
      {
        String str2 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
        String str3 = null;
        int j = paramXMLStreamReader.getAttributeCount();
        WSDLDescriptorKind localWSDLDescriptorKind = WSDLDescriptorKind.ELEMENT;
        for (int k = 0; k < j; k++)
        {
          QName localQName2 = paramXMLStreamReader.getAttributeName(k);
          if (localQName2.getLocalPart().equals("element")) {
            localWSDLDescriptorKind = WSDLDescriptorKind.ELEMENT;
          } else if (localQName2.getLocalPart().equals("type")) {
            localWSDLDescriptorKind = WSDLDescriptorKind.TYPE;
          }
          if ((localQName2.getLocalPart().equals("element")) || (localQName2.getLocalPart().equals("type")))
          {
            str3 = paramXMLStreamReader.getAttributeValue(k);
            break;
          }
        }
        if (str3 != null)
        {
          WSDLPartImpl localWSDLPartImpl = new WSDLPartImpl(paramXMLStreamReader, str2, i, new WSDLPartDescriptorImpl(paramXMLStreamReader, ParserUtil.getQName(paramXMLStreamReader, str3), localWSDLDescriptorKind));
          localWSDLMessageImpl.add(localWSDLPartImpl);
        }
        if (paramXMLStreamReader.getEventType() != 2) {
          goToEnd(paramXMLStreamReader);
        }
      }
      else
      {
        extensionFacade.messageElements(localWSDLMessageImpl, paramXMLStreamReader);
      }
    }
    wsdlDoc.addMessage(localWSDLMessageImpl);
    if (paramXMLStreamReader.getEventType() != 2) {
      goToEnd(paramXMLStreamReader);
    }
  }
  
  private static void goToEnd(XMLStreamReader paramXMLStreamReader)
  {
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    }
  }
  
  private static XMLStreamReader createReader(URL paramURL)
    throws IOException, XMLStreamException
  {
    return createReader(paramURL, null);
  }
  
  private static XMLStreamReader createReader(URL paramURL, Class<Service> paramClass)
    throws IOException, XMLStreamException
  {
    Object localObject;
    try
    {
      localObject = paramURL.openStream();
    }
    catch (IOException localIOException)
    {
      if (paramClass != null)
      {
        WSDLLocator localWSDLLocator = (WSDLLocator)ContainerResolver.getInstance().getContainer().getSPI(WSDLLocator.class);
        if (localWSDLLocator != null)
        {
          String str1 = paramURL.toExternalForm();
          URL localURL = paramClass.getResource(".");
          String str2 = paramURL.getPath();
          if (localURL != null)
          {
            String str3 = localURL.toExternalForm();
            if (str1.startsWith(str3)) {
              str2 = str1.substring(str3.length());
            }
          }
          paramURL = localWSDLLocator.locateWSDL(paramClass, str2);
          if (paramURL != null)
          {
            localObject = new FilterInputStream(paramURL.openStream())
            {
              boolean closed;
              
              public void close()
                throws IOException
              {
                if (!closed)
                {
                  closed = true;
                  byte[] arrayOfByte = new byte[' '];
                  while (read(arrayOfByte) != -1) {}
                  super.close();
                }
              }
            };
            break label119;
          }
        }
      }
      throw localIOException;
    }
    label119:
    return new TidyXMLStreamReader(XMLStreamReaderFactory.create(paramURL.toExternalForm(), (InputStream)localObject, false), (Closeable)localObject);
  }
  
  private void register(WSDLParserExtension paramWSDLParserExtension)
  {
    extensions.add(new FoolProofParserExtension(paramWSDLParserExtension));
  }
  
  private static void readNSDecl(Map<String, String> paramMap, XMLStreamReader paramXMLStreamReader)
  {
    if (paramXMLStreamReader.getNamespaceCount() > 0) {
      for (int i = 0; i < paramXMLStreamReader.getNamespaceCount(); i++) {
        paramMap.put(paramXMLStreamReader.getNamespacePrefix(i), paramXMLStreamReader.getNamespaceURI(i));
      }
    }
  }
  
  private static enum BindingMode
  {
    INPUT,  OUTPUT,  FAULT;
    
    private BindingMode() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\RuntimeWSDLParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */