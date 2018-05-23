package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.dom.DOMDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

public class FI_DOM_Or_XML_DOM_SAX_SAXEvent
  extends TransformInputOutput
{
  public FI_DOM_Or_XML_DOM_SAX_SAXEvent() {}
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString)
    throws Exception
  {
    if (!paramInputStream.markSupported()) {
      paramInputStream = new BufferedInputStream(paramInputStream);
    }
    paramInputStream.mark(4);
    boolean bool = Decoder.isFastInfosetDocument(paramInputStream);
    paramInputStream.reset();
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localDocumentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
    Document localDocument;
    if (bool)
    {
      localDocument = localDocumentBuilder.newDocument();
      localObject = new DOMDocumentParser();
      ((DOMDocumentParser)localObject).parse(localDocument, paramInputStream);
    }
    else
    {
      if (paramString != null) {
        localDocumentBuilder.setEntityResolver(createRelativePathResolver(paramString));
      }
      localDocument = localDocumentBuilder.parse(paramInputStream);
    }
    Object localObject = new SAXEventSerializer(paramOutputStream);
    TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
    Transformer localTransformer = localTransformerFactory.newTransformer();
    localTransformer.transform(new DOMSource(localDocument), new SAXResult((ContentHandler)localObject));
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception
  {
    parse(paramInputStream, paramOutputStream, null);
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    FI_DOM_Or_XML_DOM_SAX_SAXEvent localFI_DOM_Or_XML_DOM_SAX_SAXEvent = new FI_DOM_Or_XML_DOM_SAX_SAXEvent();
    localFI_DOM_Or_XML_DOM_SAX_SAXEvent.parse(paramArrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\tools\FI_DOM_Or_XML_DOM_SAX_SAXEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */