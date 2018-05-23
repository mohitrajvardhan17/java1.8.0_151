package com.sun.xml.internal.bind.marshaller;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAX2DOMEx
  implements ContentHandler
{
  private Node node = null;
  private boolean isConsolidate;
  protected final Stack<Node> nodeStack = new Stack();
  private final FinalArrayList<String> unprocessedNamespaces = new FinalArrayList();
  protected final Document document;
  
  public SAX2DOMEx(Node paramNode)
  {
    this(paramNode, false);
  }
  
  public SAX2DOMEx(Node paramNode, boolean paramBoolean)
  {
    node = paramNode;
    isConsolidate = paramBoolean;
    nodeStack.push(node);
    if ((paramNode instanceof Document)) {
      document = ((Document)paramNode);
    } else {
      document = paramNode.getOwnerDocument();
    }
  }
  
  public SAX2DOMEx(DocumentBuilderFactory paramDocumentBuilderFactory)
    throws ParserConfigurationException
  {
    paramDocumentBuilderFactory.setValidating(false);
    document = paramDocumentBuilderFactory.newDocumentBuilder().newDocument();
    node = document;
    nodeStack.push(document);
  }
  
  /**
   * @deprecated
   */
  public SAX2DOMEx()
    throws ParserConfigurationException
  {
    DocumentBuilderFactory localDocumentBuilderFactory = XmlFactory.createDocumentBuilderFactory(false);
    localDocumentBuilderFactory.setValidating(false);
    document = localDocumentBuilderFactory.newDocumentBuilder().newDocument();
    node = document;
    nodeStack.push(document);
  }
  
  public final Element getCurrentElement()
  {
    return (Element)nodeStack.peek();
  }
  
  public Node getDOM()
  {
    return node;
  }
  
  public void startDocument() {}
  
  public void endDocument() {}
  
  protected void namespace(Element paramElement, String paramString1, String paramString2)
  {
    String str;
    if (("".equals(paramString1)) || (paramString1 == null)) {
      str = "xmlns";
    } else {
      str = "xmlns:" + paramString1;
    }
    if (paramElement.hasAttributeNS("http://www.w3.org/2000/xmlns/", str)) {
      paramElement.removeAttributeNS("http://www.w3.org/2000/xmlns/", str);
    }
    paramElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str, paramString2);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
  {
    Node localNode = (Node)nodeStack.peek();
    Element localElement = document.createElementNS(paramString1, paramString3);
    if (localElement == null) {
      throw new AssertionError(Messages.format("SAX2DOMEx.DomImplDoesntSupportCreateElementNs", document.getClass().getName(), Which.which(document.getClass())));
    }
    String str2;
    for (int i = 0; i < unprocessedNamespaces.size(); i += 2)
    {
      String str1 = (String)unprocessedNamespaces.get(i);
      str2 = (String)unprocessedNamespaces.get(i + 1);
      namespace(localElement, str1, str2);
    }
    unprocessedNamespaces.clear();
    if (paramAttributes != null)
    {
      i = paramAttributes.getLength();
      for (int j = 0; j < i; j++)
      {
        str2 = paramAttributes.getURI(j);
        String str3 = paramAttributes.getValue(j);
        String str4 = paramAttributes.getQName(j);
        localElement.setAttributeNS(str2, str4, str3);
      }
    }
    localNode.appendChild(localElement);
    nodeStack.push(localElement);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    nodeStack.pop();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    characters(new String(paramArrayOfChar, paramInt1, paramInt2));
  }
  
  protected Text characters(String paramString)
  {
    Node localNode1 = (Node)nodeStack.peek();
    Node localNode2 = localNode1.getLastChild();
    Text localText;
    if ((isConsolidate) && (localNode2 != null) && (localNode2.getNodeType() == 3))
    {
      localText = (Text)localNode2;
      localText.appendData(paramString);
    }
    else
    {
      localText = document.createTextNode(paramString);
      localNode1.appendChild(localText);
    }
    return localText;
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {}
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    Node localNode = (Node)nodeStack.peek();
    ProcessingInstruction localProcessingInstruction = document.createProcessingInstruction(paramString1, paramString2);
    localNode.appendChild(localProcessingInstruction);
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString) {}
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    unprocessedNamespaces.add(paramString1);
    unprocessedNamespaces.add(paramString2);
  }
  
  public void endPrefixMapping(String paramString) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\marshaller\SAX2DOMEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */