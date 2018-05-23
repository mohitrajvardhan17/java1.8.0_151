package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.sax.SAXDocumentSerializer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XML_SAX_FI
  extends TransformInputOutput
{
  public XML_SAX_FI() {}
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString)
    throws Exception
  {
    SAXParser localSAXParser = getParser();
    SAXDocumentSerializer localSAXDocumentSerializer = getSerializer(paramOutputStream);
    XMLReader localXMLReader = localSAXParser.getXMLReader();
    localXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", localSAXDocumentSerializer);
    localXMLReader.setContentHandler(localSAXDocumentSerializer);
    if (paramString != null) {
      localXMLReader.setEntityResolver(createRelativePathResolver(paramString));
    }
    localXMLReader.parse(new InputSource(paramInputStream));
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception
  {
    parse(paramInputStream, paramOutputStream, null);
  }
  
  public void convert(Reader paramReader, OutputStream paramOutputStream)
    throws Exception
  {
    InputSource localInputSource = new InputSource(paramReader);
    SAXParser localSAXParser = getParser();
    SAXDocumentSerializer localSAXDocumentSerializer = getSerializer(paramOutputStream);
    localSAXParser.setProperty("http://xml.org/sax/properties/lexical-handler", localSAXDocumentSerializer);
    localSAXParser.parse(localInputSource, localSAXDocumentSerializer);
  }
  
  private SAXParser getParser()
  {
    SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
    localSAXParserFactory.setNamespaceAware(true);
    try
    {
      return localSAXParserFactory.newSAXParser();
    }
    catch (Exception localException) {}
    return null;
  }
  
  private SAXDocumentSerializer getSerializer(OutputStream paramOutputStream)
  {
    SAXDocumentSerializer localSAXDocumentSerializer = new SAXDocumentSerializer();
    localSAXDocumentSerializer.setOutputStream(paramOutputStream);
    return localSAXDocumentSerializer;
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    XML_SAX_FI localXML_SAX_FI = new XML_SAX_FI();
    localXML_SAX_FI.parse(paramArrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\tools\XML_SAX_FI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */