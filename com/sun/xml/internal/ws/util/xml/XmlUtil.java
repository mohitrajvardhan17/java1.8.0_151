package com.sun.xml.internal.ws.util.xml;

import com.sun.istack.internal.Nullable;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.ws.WebServiceException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class XmlUtil
{
  private static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
  private static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
  private static final String EXTERNAL_GE = "http://xml.org/sax/features/external-general-entities";
  private static final String EXTERNAL_PE = "http://xml.org/sax/features/external-parameter-entities";
  private static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  private static final Logger LOGGER = Logger.getLogger(XmlUtil.class.getName());
  private static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.ws.disableXmlSecurity";
  private static boolean XML_SECURITY_DISABLED = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      return Boolean.valueOf(Boolean.getBoolean("com.sun.xml.internal.ws.disableXmlSecurity"));
    }
  })).booleanValue();
  static final ContextClassloaderLocal<TransformerFactory> transformerFactory = new ContextClassloaderLocal()
  {
    protected TransformerFactory initialValue()
      throws Exception
    {
      return TransformerFactory.newInstance();
    }
  };
  static final ContextClassloaderLocal<SAXParserFactory> saxParserFactory = new ContextClassloaderLocal()
  {
    protected SAXParserFactory initialValue()
      throws Exception
    {
      SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
      localSAXParserFactory.setNamespaceAware(true);
      return localSAXParserFactory;
    }
  };
  public static final ErrorHandler DRACONIAN_ERROR_HANDLER = new ErrorHandler()
  {
    public void warning(SAXParseException paramAnonymousSAXParseException) {}
    
    public void error(SAXParseException paramAnonymousSAXParseException)
      throws SAXException
    {
      throw paramAnonymousSAXParseException;
    }
    
    public void fatalError(SAXParseException paramAnonymousSAXParseException)
      throws SAXException
    {
      throw paramAnonymousSAXParseException;
    }
  };
  
  public XmlUtil() {}
  
  public static String getPrefix(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i == -1) {
      return null;
    }
    return paramString.substring(0, i);
  }
  
  public static String getLocalPart(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i == -1) {
      return paramString;
    }
    return paramString.substring(i + 1);
  }
  
  public static String getAttributeOrNull(Element paramElement, String paramString)
  {
    Attr localAttr = paramElement.getAttributeNode(paramString);
    if (localAttr == null) {
      return null;
    }
    return localAttr.getValue();
  }
  
  public static String getAttributeNSOrNull(Element paramElement, String paramString1, String paramString2)
  {
    Attr localAttr = paramElement.getAttributeNodeNS(paramString2, paramString1);
    if (localAttr == null) {
      return null;
    }
    return localAttr.getValue();
  }
  
  public static String getAttributeNSOrNull(Element paramElement, QName paramQName)
  {
    Attr localAttr = paramElement.getAttributeNodeNS(paramQName.getNamespaceURI(), paramQName.getLocalPart());
    if (localAttr == null) {
      return null;
    }
    return localAttr.getValue();
  }
  
  public static Iterator getAllChildren(Element paramElement)
  {
    return new NodeListIterator(paramElement.getChildNodes());
  }
  
  public static Iterator getAllAttributes(Element paramElement)
  {
    return new NamedNodeMapIterator(paramElement.getAttributes());
  }
  
  public static List<String> parseTokenList(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
    while (localStringTokenizer.hasMoreTokens()) {
      localArrayList.add(localStringTokenizer.nextToken());
    }
    return localArrayList;
  }
  
  public static String getTextForNode(Node paramNode)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    NodeList localNodeList = paramNode.getChildNodes();
    if (localNodeList.getLength() == 0) {
      return null;
    }
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      Node localNode = localNodeList.item(i);
      if ((localNode instanceof Text))
      {
        localStringBuilder.append(localNode.getNodeValue());
      }
      else if ((localNode instanceof EntityReference))
      {
        String str = getTextForNode(localNode);
        if (str == null) {
          return null;
        }
        localStringBuilder.append(str);
      }
      else
      {
        return null;
      }
    }
    return localStringBuilder.toString();
  }
  
  public static InputStream getUTF8Stream(String paramString)
  {
    try
    {
      ByteArrayBuffer localByteArrayBuffer = new ByteArrayBuffer();
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localByteArrayBuffer, "utf-8");
      localOutputStreamWriter.write(paramString);
      localOutputStreamWriter.close();
      return localByteArrayBuffer.newInputStream();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException("should not happen");
    }
  }
  
  public static Transformer newTransformer()
  {
    try
    {
      return ((TransformerFactory)transformerFactory.get()).newTransformer();
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      throw new IllegalStateException("Unable to create a JAXP transformer");
    }
  }
  
  public static <T extends Result> T identityTransform(Source paramSource, T paramT)
    throws TransformerException, SAXException, ParserConfigurationException, IOException
  {
    if ((paramSource instanceof StreamSource))
    {
      StreamSource localStreamSource = (StreamSource)paramSource;
      TransformerHandler localTransformerHandler = ((SAXTransformerFactory)transformerFactory.get()).newTransformerHandler();
      localTransformerHandler.setResult(paramT);
      XMLReader localXMLReader = ((SAXParserFactory)saxParserFactory.get()).newSAXParser().getXMLReader();
      localXMLReader.setContentHandler(localTransformerHandler);
      localXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", localTransformerHandler);
      localXMLReader.parse(toInputSource(localStreamSource));
    }
    else
    {
      newTransformer().transform(paramSource, paramT);
    }
    return paramT;
  }
  
  private static InputSource toInputSource(StreamSource paramStreamSource)
  {
    InputSource localInputSource = new InputSource();
    localInputSource.setByteStream(paramStreamSource.getInputStream());
    localInputSource.setCharacterStream(paramStreamSource.getReader());
    localInputSource.setPublicId(paramStreamSource.getPublicId());
    localInputSource.setSystemId(paramStreamSource.getSystemId());
    return localInputSource;
  }
  
  public static EntityResolver createEntityResolver(@Nullable URL paramURL)
  {
    CatalogManager localCatalogManager = new CatalogManager();
    localCatalogManager.setIgnoreMissingProperties(true);
    localCatalogManager.setUseStaticCatalog(false);
    Catalog localCatalog = localCatalogManager.getCatalog();
    try
    {
      if (paramURL != null) {
        localCatalog.parseCatalog(paramURL);
      }
    }
    catch (IOException localIOException)
    {
      throw new ServerRtException("server.rt.err", new Object[] { localIOException });
    }
    return workaroundCatalogResolver(localCatalog);
  }
  
  public static EntityResolver createDefaultCatalogResolver()
  {
    CatalogManager localCatalogManager = new CatalogManager();
    localCatalogManager.setIgnoreMissingProperties(true);
    localCatalogManager.setUseStaticCatalog(false);
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    Catalog localCatalog = localCatalogManager.getCatalog();
    try
    {
      Enumeration localEnumeration;
      if (localClassLoader == null) {
        localEnumeration = ClassLoader.getSystemResources("META-INF/jax-ws-catalog.xml");
      } else {
        localEnumeration = localClassLoader.getResources("META-INF/jax-ws-catalog.xml");
      }
      while (localEnumeration.hasMoreElements())
      {
        URL localURL = (URL)localEnumeration.nextElement();
        localCatalog.parseCatalog(localURL);
      }
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
    return workaroundCatalogResolver(localCatalog);
  }
  
  private static CatalogResolver workaroundCatalogResolver(Catalog paramCatalog)
  {
    CatalogManager local4 = new CatalogManager()
    {
      public Catalog getCatalog()
      {
        return val$catalog;
      }
    };
    local4.setIgnoreMissingProperties(true);
    local4.setUseStaticCatalog(false);
    return new CatalogResolver(local4);
  }
  
  public static DocumentBuilderFactory newDocumentBuilderFactory()
  {
    return newDocumentBuilderFactory(false);
  }
  
  public static DocumentBuilderFactory newDocumentBuilderFactory(boolean paramBoolean)
  {
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    String str = "http://javax.xml.XMLConstants/feature/secure-processing";
    try
    {
      boolean bool = !isXMLSecurityDisabled(paramBoolean);
      localDocumentBuilderFactory.setFeature(str, bool);
      localDocumentBuilderFactory.setNamespaceAware(true);
      if (bool)
      {
        localDocumentBuilderFactory.setExpandEntityReferences(false);
        str = "http://apache.org/xml/features/disallow-doctype-decl";
        localDocumentBuilderFactory.setFeature(str, true);
        str = "http://xml.org/sax/features/external-general-entities";
        localDocumentBuilderFactory.setFeature(str, false);
        str = "http://xml.org/sax/features/external-parameter-entities";
        localDocumentBuilderFactory.setFeature(str, false);
        str = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        localDocumentBuilderFactory.setFeature(str, false);
      }
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support " + str + " feature!", new Object[] { localDocumentBuilderFactory.getClass().getName() });
    }
    return localDocumentBuilderFactory;
  }
  
  public static TransformerFactory newTransformerFactory(boolean paramBoolean)
  {
    TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
    try
    {
      localTransformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", isXMLSecurityDisabled(paramBoolean));
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support secure xml processing!", new Object[] { localTransformerFactory.getClass().getName() });
    }
    return localTransformerFactory;
  }
  
  public static TransformerFactory newTransformerFactory()
  {
    return newTransformerFactory(true);
  }
  
  public static SAXParserFactory newSAXParserFactory(boolean paramBoolean)
  {
    SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
    String str = "http://javax.xml.XMLConstants/feature/secure-processing";
    try
    {
      boolean bool = !isXMLSecurityDisabled(paramBoolean);
      localSAXParserFactory.setFeature(str, bool);
      localSAXParserFactory.setNamespaceAware(true);
      if (bool)
      {
        str = "http://apache.org/xml/features/disallow-doctype-decl";
        localSAXParserFactory.setFeature(str, true);
        str = "http://xml.org/sax/features/external-general-entities";
        localSAXParserFactory.setFeature(str, false);
        str = "http://xml.org/sax/features/external-parameter-entities";
        localSAXParserFactory.setFeature(str, false);
        str = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        localSAXParserFactory.setFeature(str, false);
      }
    }
    catch (ParserConfigurationException|SAXNotRecognizedException|SAXNotSupportedException localParserConfigurationException)
    {
      LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support " + str + " feature!", new Object[] { localSAXParserFactory.getClass().getName() });
    }
    return localSAXParserFactory;
  }
  
  public static XPathFactory newXPathFactory(boolean paramBoolean)
  {
    XPathFactory localXPathFactory = XPathFactory.newInstance();
    try
    {
      localXPathFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", isXMLSecurityDisabled(paramBoolean));
    }
    catch (XPathFactoryConfigurationException localXPathFactoryConfigurationException)
    {
      LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support secure xml processing!", new Object[] { localXPathFactory.getClass().getName() });
    }
    return localXPathFactory;
  }
  
  public static XMLInputFactory newXMLInputFactory(boolean paramBoolean)
  {
    XMLInputFactory localXMLInputFactory = XMLInputFactory.newInstance();
    if (isXMLSecurityDisabled(paramBoolean))
    {
      localXMLInputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.valueOf(false));
      localXMLInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.valueOf(false));
    }
    return localXMLInputFactory;
  }
  
  private static boolean isXMLSecurityDisabled(boolean paramBoolean)
  {
    return (XML_SECURITY_DISABLED) || (paramBoolean);
  }
  
  public static SchemaFactory allowExternalAccess(SchemaFactory paramSchemaFactory, String paramString, boolean paramBoolean)
  {
    if (isXMLSecurityDisabled(paramBoolean))
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Xml Security disabled, no JAXP xsd external access configuration necessary.");
      }
      return paramSchemaFactory;
    }
    if (System.getProperty("javax.xml.accessExternalSchema") != null)
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Detected explicitly JAXP configuration, no JAXP xsd external access configuration necessary.");
      }
      return paramSchemaFactory;
    }
    try
    {
      paramSchemaFactory.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", paramString);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Property \"{0}\" is supported and has been successfully set by used JAXP implementation.", new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" });
      }
    }
    catch (SAXException localSAXException)
    {
      if (LOGGER.isLoggable(Level.CONFIG)) {
        LOGGER.log(Level.CONFIG, "Property \"{0}\" is not supported by used JAXP implementation.", new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" });
      }
    }
    return paramSchemaFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\XmlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */