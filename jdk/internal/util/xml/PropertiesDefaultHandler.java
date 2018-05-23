package jdk.internal.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import jdk.internal.org.xml.sax.Attributes;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.SAXParseException;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import jdk.internal.util.xml.impl.SAXParserImpl;
import jdk.internal.util.xml.impl.XMLStreamWriterImpl;

public class PropertiesDefaultHandler
  extends DefaultHandler
{
  private static final String ELEMENT_ROOT = "properties";
  private static final String ELEMENT_COMMENT = "comment";
  private static final String ELEMENT_ENTRY = "entry";
  private static final String ATTR_KEY = "key";
  private static final String PROPS_DTD_DECL = "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">";
  private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
  private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>";
  private static final String EXTERNAL_XML_VERSION = "1.0";
  private Properties properties;
  static final String ALLOWED_ELEMENTS = "properties, comment, entry";
  static final String ALLOWED_COMMENT = "comment";
  StringBuffer buf = new StringBuffer();
  boolean sawComment = false;
  boolean validEntry = false;
  int rootElem = 0;
  String key;
  String rootElm;
  
  public PropertiesDefaultHandler() {}
  
  public void load(Properties paramProperties, InputStream paramInputStream)
    throws IOException, InvalidPropertiesFormatException, UnsupportedEncodingException
  {
    properties = paramProperties;
    try
    {
      SAXParserImpl localSAXParserImpl = new SAXParserImpl();
      localSAXParserImpl.parse(paramInputStream, this);
    }
    catch (SAXException localSAXException)
    {
      throw new InvalidPropertiesFormatException(localSAXException);
    }
  }
  
  public void store(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2)
    throws IOException
  {
    try
    {
      XMLStreamWriterImpl localXMLStreamWriterImpl = new XMLStreamWriterImpl(paramOutputStream, paramString2);
      localXMLStreamWriterImpl.writeStartDocument();
      localXMLStreamWriterImpl.writeDTD("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
      localXMLStreamWriterImpl.writeStartElement("properties");
      if ((paramString1 != null) && (paramString1.length() > 0))
      {
        localXMLStreamWriterImpl.writeStartElement("comment");
        localXMLStreamWriterImpl.writeCharacters(paramString1);
        localXMLStreamWriterImpl.writeEndElement();
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
            localXMLStreamWriterImpl.writeStartElement("entry");
            localXMLStreamWriterImpl.writeAttribute("key", (String)localObject1);
            localXMLStreamWriterImpl.writeCharacters((String)localObject2);
            localXMLStreamWriterImpl.writeEndElement();
          }
        }
      }
      localXMLStreamWriterImpl.writeEndElement();
      localXMLStreamWriterImpl.writeEndDocument();
      localXMLStreamWriterImpl.close();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      if ((localXMLStreamException.getCause() instanceof UnsupportedEncodingException)) {
        throw ((UnsupportedEncodingException)localXMLStreamException.getCause());
      }
      throw new IOException(localXMLStreamException);
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (rootElem < 2) {
      rootElem += 1;
    }
    if (rootElm == null) {
      fatalError(new SAXParseException("An XML properties document must contain the DOCTYPE declaration as defined by java.util.Properties.", null));
    }
    if ((rootElem == 1) && (!rootElm.equals(paramString3))) {
      fatalError(new SAXParseException("Document root element \"" + paramString3 + "\", must match DOCTYPE root \"" + rootElm + "\"", null));
    }
    if (!"properties, comment, entry".contains(paramString3)) {
      fatalError(new SAXParseException("Element type \"" + paramString3 + "\" must be declared.", null));
    }
    if (paramString3.equals("entry"))
    {
      validEntry = true;
      key = paramAttributes.getValue("key");
      if (key == null) {
        fatalError(new SAXParseException("Attribute \"key\" is required and must be specified for element type \"entry\"", null));
      }
    }
    else if (paramString3.equals("comment"))
    {
      if (sawComment) {
        fatalError(new SAXParseException("Only one comment element may be allowed. The content of element type \"properties\" must match \"(comment?,entry*)\"", null));
      }
      sawComment = true;
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (validEntry) {
      buf.append(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (!"properties, comment, entry".contains(paramString3)) {
      fatalError(new SAXParseException("Element: " + paramString3 + " is invalid, must match  \"(comment?,entry*)\".", null));
    }
    if (validEntry)
    {
      properties.setProperty(key, buf.toString());
      buf.delete(0, buf.length());
      validEntry = false;
    }
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    rootElm = paramString1;
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
    throws SAXException, IOException
  {
    if (paramString2.equals("http://java.sun.com/dtd/properties.dtd"))
    {
      InputSource localInputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>"));
      localInputSource.setSystemId("http://java.sun.com/dtd/properties.dtd");
      return localInputSource;
    }
    throw new SAXException("Invalid system identifier: " + paramString2);
  }
  
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\PropertiesDefaultHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */