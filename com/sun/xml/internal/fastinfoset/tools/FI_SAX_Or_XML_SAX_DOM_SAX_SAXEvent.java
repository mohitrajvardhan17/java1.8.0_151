package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent
  extends TransformInputOutput
{
  public FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent() {}
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString)
    throws Exception
  {
    if (!paramInputStream.markSupported()) {
      paramInputStream = new BufferedInputStream(paramInputStream);
    }
    paramInputStream.mark(4);
    boolean bool = Decoder.isFastInfosetDocument(paramInputStream);
    paramInputStream.reset();
    TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
    Transformer localTransformer = localTransformerFactory.newTransformer();
    DOMResult localDOMResult = new DOMResult();
    if (bool)
    {
      localTransformer.transform(new FastInfosetSource(paramInputStream), localDOMResult);
    }
    else if (paramString != null)
    {
      localObject = getParser();
      XMLReader localXMLReader = ((SAXParser)localObject).getXMLReader();
      localXMLReader.setEntityResolver(createRelativePathResolver(paramString));
      SAXSource localSAXSource = new SAXSource(localXMLReader, new InputSource(paramInputStream));
      localTransformer.transform(localSAXSource, localDOMResult);
    }
    else
    {
      localTransformer.transform(new StreamSource(paramInputStream), localDOMResult);
    }
    Object localObject = new SAXEventSerializer(paramOutputStream);
    localTransformer.transform(new DOMSource(localDOMResult.getNode()), new SAXResult((ContentHandler)localObject));
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception
  {
    parse(paramInputStream, paramOutputStream, null);
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
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent localFI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent = new FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent();
    localFI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent.parse(paramArrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\tools\FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */