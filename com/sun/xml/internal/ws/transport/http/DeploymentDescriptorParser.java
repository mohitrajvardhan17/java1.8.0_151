package com.sun.xml.internal.ws.transport.http;

import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature.Builder;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.streaming.Attributes;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;
import org.xml.sax.EntityResolver;

public class DeploymentDescriptorParser<A>
{
  public static final String NS_RUNTIME = "http://java.sun.com/xml/ns/jax-ws/ri/runtime";
  public static final String JAXWS_WSDL_DD_DIR = "WEB-INF/wsdl";
  public static final QName QNAME_ENDPOINTS = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoints");
  public static final QName QNAME_ENDPOINT = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoint");
  public static final QName QNAME_EXT_METADA = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "external-metadata");
  public static final String ATTR_FILE = "file";
  public static final String ATTR_RESOURCE = "resource";
  public static final String ATTR_VERSION = "version";
  public static final String ATTR_NAME = "name";
  public static final String ATTR_IMPLEMENTATION = "implementation";
  public static final String ATTR_WSDL = "wsdl";
  public static final String ATTR_SERVICE = "service";
  public static final String ATTR_PORT = "port";
  public static final String ATTR_URL_PATTERN = "url-pattern";
  public static final String ATTR_ENABLE_MTOM = "enable-mtom";
  public static final String ATTR_MTOM_THRESHOLD_VALUE = "mtom-threshold-value";
  public static final String ATTR_BINDING = "binding";
  public static final String ATTR_DATABINDING = "databinding";
  public static final List<String> ATTRVALUE_SUPPORTED_VERSIONS = Arrays.asList(new String[] { "2.0", "2.1" });
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
  private final Container container;
  private final ClassLoader classLoader;
  private final ResourceLoader loader;
  private final AdapterFactory<A> adapterFactory;
  private final Set<String> names = new HashSet();
  private final Map<String, SDDocumentSource> docs = new HashMap();
  
  public DeploymentDescriptorParser(ClassLoader paramClassLoader, ResourceLoader paramResourceLoader, Container paramContainer, AdapterFactory<A> paramAdapterFactory)
    throws MalformedURLException
  {
    classLoader = paramClassLoader;
    loader = paramResourceLoader;
    container = paramContainer;
    adapterFactory = paramAdapterFactory;
    collectDocs("/WEB-INF/wsdl/");
    logger.log(Level.FINE, "war metadata={0}", docs);
  }
  
  @NotNull
  public List<A> parse(String paramString, InputStream paramInputStream)
  {
    TidyXMLStreamReader localTidyXMLStreamReader = null;
    try
    {
      localTidyXMLStreamReader = new TidyXMLStreamReader(XMLStreamReaderFactory.create(paramString, paramInputStream, true), paramInputStream);
      XMLStreamReaderUtil.nextElementContent(localTidyXMLStreamReader);
      List localList = parseAdapters(localTidyXMLStreamReader);
      return localList;
    }
    finally
    {
      if (localTidyXMLStreamReader != null) {
        try
        {
          localTidyXMLStreamReader.close();
        }
        catch (XMLStreamException localXMLStreamException2)
        {
          throw new ServerRtException("runtime.parser.xmlReader", new Object[] { localXMLStreamException2 });
        }
      }
      try
      {
        paramInputStream.close();
      }
      catch (IOException localIOException2) {}
    }
  }
  
  @NotNull
  public List<A> parse(File paramFile)
    throws IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    try
    {
      List localList = parse(paramFile.getPath(), localFileInputStream);
      return localList;
    }
    finally
    {
      localFileInputStream.close();
    }
  }
  
  private void collectDocs(String paramString)
    throws MalformedURLException
  {
    Set localSet = loader.getResourcePaths(paramString);
    if (localSet != null)
    {
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str.endsWith("/"))
        {
          if ((!str.endsWith("/CVS/")) && (!str.endsWith("/.svn/"))) {
            collectDocs(str);
          }
        }
        else
        {
          URL localURL = loader.getResource(str);
          docs.put(localURL.toString(), SDDocumentSource.create(localURL));
        }
      }
    }
  }
  
  private List<A> parseAdapters(XMLStreamReader paramXMLStreamReader)
  {
    if (!paramXMLStreamReader.getName().equals(QNAME_ENDPOINTS)) {
      failWithFullName("runtime.parser.invalidElement", paramXMLStreamReader);
    }
    ArrayList localArrayList = new ArrayList();
    Attributes localAttributes = XMLStreamReaderUtil.getAttributes(paramXMLStreamReader);
    String str1 = getMandatoryNonEmptyAttribute(paramXMLStreamReader, localAttributes, "version");
    if (!ATTRVALUE_SUPPORTED_VERSIONS.contains(str1)) {
      failWithLocalName("runtime.parser.invalidVersionNumber", paramXMLStreamReader, str1);
    }
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      if (paramXMLStreamReader.getName().equals(QNAME_ENDPOINT))
      {
        localAttributes = XMLStreamReaderUtil.getAttributes(paramXMLStreamReader);
        String str2 = getMandatoryNonEmptyAttribute(paramXMLStreamReader, localAttributes, "name");
        if (!names.add(str2)) {
          logger.warning(WsservletMessages.SERVLET_WARNING_DUPLICATE_ENDPOINT_NAME());
        }
        String str3 = getMandatoryNonEmptyAttribute(paramXMLStreamReader, localAttributes, "implementation");
        Class localClass = getImplementorClass(str3, paramXMLStreamReader);
        MetadataReader localMetadataReader = null;
        ExternalMetadataFeature localExternalMetadataFeature = null;
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
        if (paramXMLStreamReader.getEventType() != 2)
        {
          localExternalMetadataFeature = configureExternalMetadataReader(paramXMLStreamReader);
          if (localExternalMetadataFeature != null) {
            localMetadataReader = localExternalMetadataFeature.getMetadataReader(localClass.getClassLoader(), false);
          }
        }
        QName localQName1 = getQNameAttribute(localAttributes, "service");
        if (localQName1 == null) {
          localQName1 = EndpointFactory.getDefaultServiceName(localClass, localMetadataReader);
        }
        QName localQName2 = getQNameAttribute(localAttributes, "port");
        if (localQName2 == null) {
          localQName2 = EndpointFactory.getDefaultPortName(localQName1, localClass, localMetadataReader);
        }
        String str4 = getAttribute(localAttributes, "enable-mtom");
        String str5 = getAttribute(localAttributes, "mtom-threshold-value");
        String str6 = getAttribute(localAttributes, "databinding");
        String str7 = getAttribute(localAttributes, "binding");
        if (str7 != null) {
          str7 = getBindingIdForToken(str7);
        }
        WSBinding localWSBinding = createBinding(str7, localClass, str4, str5, str6);
        if (localExternalMetadataFeature != null) {
          localWSBinding.getFeatures().mergeFeatures(new WebServiceFeature[] { localExternalMetadataFeature }, true);
        }
        String str8 = getMandatoryNonEmptyAttribute(paramXMLStreamReader, localAttributes, "url-pattern");
        boolean bool = setHandlersAndRoles(localWSBinding, paramXMLStreamReader, localQName1, localQName2);
        EndpointFactory.verifyImplementorClass(localClass, localMetadataReader);
        SDDocumentSource localSDDocumentSource = getPrimaryWSDL(paramXMLStreamReader, localAttributes, localClass, localMetadataReader);
        WSEndpoint localWSEndpoint = WSEndpoint.create(localClass, !bool, null, localQName1, localQName2, container, localWSBinding, localSDDocumentSource, docs.values(), createEntityResolver(), false);
        localArrayList.add(adapterFactory.createAdapter(str2, str8, localWSEndpoint));
      }
      else
      {
        failWithLocalName("runtime.parser.invalidElement", paramXMLStreamReader);
      }
    }
    return localArrayList;
  }
  
  private static WSBinding createBinding(String paramString1, Class paramClass, String paramString2, String paramString3, String paramString4)
  {
    MTOMFeature localMTOMFeature = null;
    if (paramString2 != null) {
      if (paramString3 != null) {
        localMTOMFeature = new MTOMFeature(Boolean.valueOf(paramString2).booleanValue(), Integer.valueOf(paramString3).intValue());
      } else {
        localMTOMFeature = new MTOMFeature(Boolean.valueOf(paramString2).booleanValue());
      }
    }
    BindingID localBindingID;
    WebServiceFeatureList localWebServiceFeatureList;
    if (paramString1 != null)
    {
      localBindingID = BindingID.parse(paramString1);
      localWebServiceFeatureList = localBindingID.createBuiltinFeatureList();
      if (checkMtomConflict((MTOMFeature)localWebServiceFeatureList.get(MTOMFeature.class), localMTOMFeature)) {
        throw new ServerRtException(ServerMessages.DD_MTOM_CONFLICT(paramString1, paramString2), new Object[0]);
      }
    }
    else
    {
      localBindingID = BindingID.parse(paramClass);
      localWebServiceFeatureList = new WebServiceFeatureList();
      if (localMTOMFeature != null) {
        localWebServiceFeatureList.add(localMTOMFeature);
      }
      localWebServiceFeatureList.addAll(localBindingID.createBuiltinFeatureList());
    }
    if (paramString4 != null) {
      localWebServiceFeatureList.add(new DatabindingModeFeature(paramString4));
    }
    return localBindingID.createBinding(localWebServiceFeatureList.toArray());
  }
  
  private static boolean checkMtomConflict(MTOMFeature paramMTOMFeature1, MTOMFeature paramMTOMFeature2)
  {
    if ((paramMTOMFeature1 == null) || (paramMTOMFeature2 == null)) {
      return false;
    }
    return paramMTOMFeature1.isEnabled() ^ paramMTOMFeature2.isEnabled();
  }
  
  @NotNull
  public static String getBindingIdForToken(@NotNull String paramString)
  {
    if (paramString.equals("##SOAP11_HTTP")) {
      return "http://schemas.xmlsoap.org/wsdl/soap/http";
    }
    if (paramString.equals("##SOAP11_HTTP_MTOM")) {
      return "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
    }
    if (paramString.equals("##SOAP12_HTTP")) {
      return "http://www.w3.org/2003/05/soap/bindings/HTTP/";
    }
    if (paramString.equals("##SOAP12_HTTP_MTOM")) {
      return "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";
    }
    if (paramString.equals("##XML_HTTP")) {
      return "http://www.w3.org/2004/08/wsdl/http";
    }
    return paramString;
  }
  
  private SDDocumentSource getPrimaryWSDL(XMLStreamReader paramXMLStreamReader, Attributes paramAttributes, Class<?> paramClass, MetadataReader paramMetadataReader)
  {
    String str = getAttribute(paramAttributes, "wsdl");
    if (str == null) {
      str = EndpointFactory.getWsdlLocation(paramClass, paramMetadataReader);
    }
    if (str != null)
    {
      if (!str.startsWith("WEB-INF/wsdl"))
      {
        logger.log(Level.WARNING, "Ignoring wrong wsdl={0}. It should start with {1}. Going to generate and publish a new WSDL.", new Object[] { str, "WEB-INF/wsdl" });
        return null;
      }
      URL localURL;
      try
      {
        localURL = loader.getResource('/' + str);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(str), localMalformedURLException, paramXMLStreamReader);
      }
      if (localURL == null) {
        throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(str), paramXMLStreamReader);
      }
      SDDocumentSource localSDDocumentSource = (SDDocumentSource)docs.get(localURL.toExternalForm());
      assert (localSDDocumentSource != null);
      return localSDDocumentSource;
    }
    return null;
  }
  
  private EntityResolver createEntityResolver()
  {
    try
    {
      return XmlUtil.createEntityResolver(loader.getCatalogFile());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new WebServiceException(localMalformedURLException);
    }
  }
  
  protected String getAttribute(Attributes paramAttributes, String paramString)
  {
    String str = paramAttributes.getValue(paramString);
    if (str != null) {
      str = str.trim();
    }
    return str;
  }
  
  protected QName getQNameAttribute(Attributes paramAttributes, String paramString)
  {
    String str = getAttribute(paramAttributes, paramString);
    if ((str == null) || (str.equals(""))) {
      return null;
    }
    return QName.valueOf(str);
  }
  
  protected String getNonEmptyAttribute(XMLStreamReader paramXMLStreamReader, Attributes paramAttributes, String paramString)
  {
    String str = getAttribute(paramAttributes, paramString);
    if ((str != null) && (str.equals(""))) {
      failWithLocalName("runtime.parser.invalidAttributeValue", paramXMLStreamReader, paramString);
    }
    return str;
  }
  
  protected String getMandatoryAttribute(XMLStreamReader paramXMLStreamReader, Attributes paramAttributes, String paramString)
  {
    String str = getAttribute(paramAttributes, paramString);
    if (str == null) {
      failWithLocalName("runtime.parser.missing.attribute", paramXMLStreamReader, paramString);
    }
    return str;
  }
  
  protected String getMandatoryNonEmptyAttribute(XMLStreamReader paramXMLStreamReader, Attributes paramAttributes, String paramString)
  {
    String str = getAttribute(paramAttributes, paramString);
    if (str == null) {
      failWithLocalName("runtime.parser.missing.attribute", paramXMLStreamReader, paramString);
    } else if (str.equals("")) {
      failWithLocalName("runtime.parser.invalidAttributeValue", paramXMLStreamReader, paramString);
    }
    return str;
  }
  
  protected boolean setHandlersAndRoles(WSBinding paramWSBinding, XMLStreamReader paramXMLStreamReader, QName paramQName1, QName paramQName2)
  {
    if ((paramXMLStreamReader.getEventType() == 2) || (!paramXMLStreamReader.getName().equals(HandlerChainsModel.QNAME_HANDLER_CHAINS))) {
      return false;
    }
    HandlerAnnotationInfo localHandlerAnnotationInfo = HandlerChainsModel.parseHandlerFile(paramXMLStreamReader, classLoader, paramQName1, paramQName2, paramWSBinding);
    paramWSBinding.setHandlerChain(localHandlerAnnotationInfo.getHandlers());
    if ((paramWSBinding instanceof SOAPBinding)) {
      ((SOAPBinding)paramWSBinding).setRoles(localHandlerAnnotationInfo.getRoles());
    }
    XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    return true;
  }
  
  protected ExternalMetadataFeature configureExternalMetadataReader(XMLStreamReader paramXMLStreamReader)
  {
    ExternalMetadataFeature.Builder localBuilder = null;
    while (QNAME_EXT_METADA.equals(paramXMLStreamReader.getName()))
    {
      if (paramXMLStreamReader.getEventType() == 1)
      {
        Attributes localAttributes = XMLStreamReaderUtil.getAttributes(paramXMLStreamReader);
        String str1 = getAttribute(localAttributes, "file");
        if (str1 != null)
        {
          if (localBuilder == null) {
            localBuilder = ExternalMetadataFeature.builder();
          }
          localBuilder.addFiles(new File[] { new File(str1) });
        }
        String str2 = getAttribute(localAttributes, "resource");
        if (str2 != null)
        {
          if (localBuilder == null) {
            localBuilder = ExternalMetadataFeature.builder();
          }
          localBuilder.addResources(new String[] { str2 });
        }
      }
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    }
    return buildFeature(localBuilder);
  }
  
  private ExternalMetadataFeature buildFeature(ExternalMetadataFeature.Builder paramBuilder)
  {
    return paramBuilder != null ? paramBuilder.build() : null;
  }
  
  protected static void fail(String paramString, XMLStreamReader paramXMLStreamReader)
  {
    logger.log(Level.SEVERE, "{0}{1}", new Object[] { paramString, Integer.valueOf(paramXMLStreamReader.getLocation().getLineNumber()) });
    throw new ServerRtException(paramString, new Object[] { Integer.toString(paramXMLStreamReader.getLocation().getLineNumber()) });
  }
  
  protected static void failWithFullName(String paramString, XMLStreamReader paramXMLStreamReader)
  {
    throw new ServerRtException(paramString, new Object[] { Integer.valueOf(paramXMLStreamReader.getLocation().getLineNumber()), paramXMLStreamReader.getName() });
  }
  
  protected static void failWithLocalName(String paramString, XMLStreamReader paramXMLStreamReader)
  {
    throw new ServerRtException(paramString, new Object[] { Integer.valueOf(paramXMLStreamReader.getLocation().getLineNumber()), paramXMLStreamReader.getLocalName() });
  }
  
  protected static void failWithLocalName(String paramString1, XMLStreamReader paramXMLStreamReader, String paramString2)
  {
    throw new ServerRtException(paramString1, new Object[] { Integer.valueOf(paramXMLStreamReader.getLocation().getLineNumber()), paramXMLStreamReader.getLocalName(), paramString2 });
  }
  
  protected Class loadClass(String paramString)
  {
    try
    {
      return Class.forName(paramString, true, classLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      logger.log(Level.SEVERE, localClassNotFoundException.getMessage(), localClassNotFoundException);
      throw new ServerRtException("runtime.parser.classNotFound", new Object[] { paramString });
    }
  }
  
  private Class getImplementorClass(String paramString, XMLStreamReader paramXMLStreamReader)
  {
    try
    {
      return Class.forName(paramString, true, classLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      logger.log(Level.SEVERE, localClassNotFoundException.getMessage(), localClassNotFoundException);
      throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_CLASS_NOT_FOUND(paramString), localClassNotFoundException, paramXMLStreamReader);
    }
  }
  
  public static abstract interface AdapterFactory<A>
  {
    public abstract A createAdapter(String paramString1, String paramString2, WSEndpoint<?> paramWSEndpoint);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\DeploymentDescriptorParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */