package sun.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import sun.util.spi.XmlPropertiesProvider;

public class PlatformXmlPropertiesProvider
  extends XmlPropertiesProvider
{
  private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
  private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>";
  private static final String EXTERNAL_XML_VERSION = "1.0";
  
  public PlatformXmlPropertiesProvider() {}
  
  public void load(Properties paramProperties, InputStream paramInputStream)
    throws IOException, InvalidPropertiesFormatException
  {
    Document localDocument = null;
    try
    {
      localDocument = getLoadingDoc(paramInputStream);
    }
    catch (SAXException localSAXException)
    {
      throw new InvalidPropertiesFormatException(localSAXException);
    }
    Element localElement = localDocument.getDocumentElement();
    String str = localElement.getAttribute("version");
    if (str.compareTo("1.0") > 0) {
      throw new InvalidPropertiesFormatException("Exported Properties file format version " + str + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK.");
    }
    importProperties(paramProperties, localElement);
  }
  
  static Document getLoadingDoc(InputStream paramInputStream)
    throws SAXException, IOException
  {
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localDocumentBuilderFactory.setIgnoringElementContentWhitespace(true);
    localDocumentBuilderFactory.setValidating(true);
    localDocumentBuilderFactory.setCoalescing(true);
    localDocumentBuilderFactory.setIgnoringComments(true);
    try
    {
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      localDocumentBuilder.setEntityResolver(new Resolver(null));
      localDocumentBuilder.setErrorHandler(new EH(null));
      InputSource localInputSource = new InputSource(paramInputStream);
      return localDocumentBuilder.parse(localInputSource);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new Error(localParserConfigurationException);
    }
  }
  
  static void importProperties(Properties paramProperties, Element paramElement)
  {
    NodeList localNodeList = paramElement.getChildNodes();
    int i = localNodeList.getLength();
    int j = (i > 0) && (localNodeList.item(0).getNodeName().equals("comment")) ? 1 : 0;
    for (int k = j; k < i; k++)
    {
      Element localElement = (Element)localNodeList.item(k);
      if (localElement.hasAttribute("key"))
      {
        Node localNode = localElement.getFirstChild();
        String str = localNode == null ? "" : localNode.getNodeValue();
        paramProperties.setProperty(localElement.getAttribute("key"), str);
      }
    }
  }
  
  public void store(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2)
    throws IOException
  {
    try
    {
      Charset.forName(paramString2);
    }
    catch (IllegalCharsetNameException|UnsupportedCharsetException localIllegalCharsetNameException)
    {
      throw new UnsupportedEncodingException(paramString2);
    }
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder localDocumentBuilder = null;
    try
    {
      localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    Document localDocument = localDocumentBuilder.newDocument();
    Element localElement1 = (Element)localDocument.appendChild(localDocument.createElement("properties"));
    if (paramString1 != null)
    {
      Element localElement2 = (Element)localElement1.appendChild(localDocument.createElement("comment"));
      localElement2.appendChild(localDocument.createTextNode(paramString1));
    }
    synchronized (paramProperties)
    {
      Iterator localIterator = paramProperties.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Object localObject1 = localEntry.getKey();
        Object localObject2 = localEntry.getValue();
        if (((localObject1 instanceof String)) && ((localObject2 instanceof String)))
        {
          Element localElement3 = (Element)localElement1.appendChild(localDocument.createElement("entry"));
          localElement3.setAttribute("key", (String)localObject1);
          localElement3.appendChild(localDocument.createTextNode((String)localObject2));
        }
      }
    }
    emitDocument(localDocument, paramOutputStream, paramString2);
  }
  
  static void emitDocument(Document paramDocument, OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
    Transformer localTransformer = null;
    try
    {
      localTransformer = localTransformerFactory.newTransformer();
      localTransformer.setOutputProperty("doctype-system", "http://java.sun.com/dtd/properties.dtd");
      localTransformer.setOutputProperty("indent", "yes");
      localTransformer.setOutputProperty("method", "xml");
      localTransformer.setOutputProperty("encoding", paramString);
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    DOMSource localDOMSource = new DOMSource(paramDocument);
    StreamResult localStreamResult = new StreamResult(paramOutputStream);
    try
    {
      localTransformer.transform(localDOMSource, localStreamResult);
    }
    catch (TransformerException localTransformerException)
    {
      throw new IOException(localTransformerException);
    }
  }
  
  private static class EH
    implements ErrorHandler
  {
    private EH() {}
    
    public void error(SAXParseException paramSAXParseException)
      throws SAXException
    {
      throw paramSAXParseException;
    }
    
    public void fatalError(SAXParseException paramSAXParseException)
      throws SAXException
    {
      throw paramSAXParseException;
    }
    
    public void warning(SAXParseException paramSAXParseException)
      throws SAXException
    {
      throw paramSAXParseException;
    }
  }
  
  private static class Resolver
    implements EntityResolver
  {
    private Resolver() {}
    
    public InputSource resolveEntity(String paramString1, String paramString2)
      throws SAXException
    {
      if (paramString2.equals("http://java.sun.com/dtd/properties.dtd"))
      {
        InputSource localInputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>"));
        localInputSource.setSystemId("http://java.sun.com/dtd/properties.dtd");
        return localInputSource;
      }
      throw new SAXException("Invalid system identifier: " + paramString2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\xml\PlatformXmlPropertiesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */