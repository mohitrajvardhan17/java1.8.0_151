package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DomSerializer
  implements XmlSerializer
{
  private final SaxSerializer serializer;
  
  public DomSerializer(Node paramNode)
  {
    Dom2SaxAdapter localDom2SaxAdapter = new Dom2SaxAdapter(paramNode);
    serializer = new SaxSerializer(localDom2SaxAdapter, localDom2SaxAdapter, false);
  }
  
  public DomSerializer(DOMResult paramDOMResult)
  {
    Node localNode = paramDOMResult.getNode();
    if (localNode == null) {
      try
      {
        DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        localDocumentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
        Document localDocument = localDocumentBuilder.newDocument();
        paramDOMResult.setNode(localDocument);
        serializer = new SaxSerializer(new Dom2SaxAdapter(localDocument), null, false);
      }
      catch (ParserConfigurationException localParserConfigurationException)
      {
        throw new TxwException(localParserConfigurationException);
      }
    } else {
      serializer = new SaxSerializer(new Dom2SaxAdapter(localNode), null, false);
    }
  }
  
  public void startDocument()
  {
    serializer.startDocument();
  }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3)
  {
    serializer.beginStartTag(paramString1, paramString2, paramString3);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder)
  {
    serializer.writeAttribute(paramString1, paramString2, paramString3, paramStringBuilder);
  }
  
  public void writeXmlns(String paramString1, String paramString2)
  {
    serializer.writeXmlns(paramString1, paramString2);
  }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3)
  {
    serializer.endStartTag(paramString1, paramString2, paramString3);
  }
  
  public void endTag()
  {
    serializer.endTag();
  }
  
  public void text(StringBuilder paramStringBuilder)
  {
    serializer.text(paramStringBuilder);
  }
  
  public void cdata(StringBuilder paramStringBuilder)
  {
    serializer.cdata(paramStringBuilder);
  }
  
  public void comment(StringBuilder paramStringBuilder)
  {
    serializer.comment(paramStringBuilder);
  }
  
  public void endDocument()
  {
    serializer.endDocument();
  }
  
  public void flush() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\DomSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */