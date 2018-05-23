package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.sax.SAXDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_SAXEvent
  extends TransformInputOutput
{
  public FI_SAX_Or_XML_SAX_SAXEvent() {}
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString)
    throws Exception
  {
    if (!paramInputStream.markSupported()) {
      paramInputStream = new BufferedInputStream(paramInputStream);
    }
    paramInputStream.mark(4);
    boolean bool = Decoder.isFastInfosetDocument(paramInputStream);
    paramInputStream.reset();
    Object localObject1;
    Object localObject2;
    if (bool)
    {
      localObject1 = new SAXDocumentParser();
      localObject2 = new SAXEventSerializer(paramOutputStream);
      ((SAXDocumentParser)localObject1).setContentHandler((ContentHandler)localObject2);
      ((SAXDocumentParser)localObject1).setProperty("http://xml.org/sax/properties/lexical-handler", localObject2);
      ((SAXDocumentParser)localObject1).parse(paramInputStream);
    }
    else
    {
      localObject1 = SAXParserFactory.newInstance();
      ((SAXParserFactory)localObject1).setNamespaceAware(true);
      localObject2 = ((SAXParserFactory)localObject1).newSAXParser();
      SAXEventSerializer localSAXEventSerializer = new SAXEventSerializer(paramOutputStream);
      XMLReader localXMLReader = ((SAXParser)localObject2).getXMLReader();
      localXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", localSAXEventSerializer);
      localXMLReader.setContentHandler(localSAXEventSerializer);
      if (paramString != null) {
        localXMLReader.setEntityResolver(createRelativePathResolver(paramString));
      }
      localXMLReader.parse(new InputSource(paramInputStream));
    }
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception
  {
    parse(paramInputStream, paramOutputStream, null);
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    FI_SAX_Or_XML_SAX_SAXEvent localFI_SAX_Or_XML_SAX_SAXEvent = new FI_SAX_Or_XML_SAX_SAXEvent();
    localFI_SAX_Or_XML_SAX_SAXEvent.parse(paramArrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\tools\FI_SAX_Or_XML_SAX_SAXEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */