package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;

public class XML_DOM_SAX_FI
  extends TransformInputOutput
{
  public XML_DOM_SAX_FI() {}
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString)
    throws Exception
  {
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localDocumentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
    if (paramString != null) {
      localDocumentBuilder.setEntityResolver(createRelativePathResolver(paramString));
    }
    Document localDocument = localDocumentBuilder.parse(paramInputStream);
    TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
    Transformer localTransformer = localTransformerFactory.newTransformer();
    localTransformer.transform(new DOMSource(localDocument), new FastInfosetResult(paramOutputStream));
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception
  {
    parse(paramInputStream, paramOutputStream, null);
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    XML_DOM_SAX_FI localXML_DOM_SAX_FI = new XML_DOM_SAX_FI();
    localXML_DOM_SAX_FI.parse(paramArrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\tools\XML_DOM_SAX_FI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */