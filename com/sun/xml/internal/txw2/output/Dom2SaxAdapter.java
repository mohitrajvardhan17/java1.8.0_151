package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import java.util.ArrayList;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

class Dom2SaxAdapter
  implements ContentHandler, LexicalHandler
{
  private final Node _node;
  private final Stack _nodeStk = new Stack();
  private boolean inCDATA;
  private final Document _document;
  private ArrayList unprocessedNamespaces = new ArrayList();
  
  public final Element getCurrentElement()
  {
    return (Element)_nodeStk.peek();
  }
  
  public Dom2SaxAdapter(Node paramNode)
  {
    _node = paramNode;
    _nodeStk.push(_node);
    if ((paramNode instanceof Document)) {
      _document = ((Document)paramNode);
    } else {
      _document = paramNode.getOwnerDocument();
    }
  }
  
  public Dom2SaxAdapter()
    throws ParserConfigurationException
  {
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localDocumentBuilderFactory.setNamespaceAware(true);
    localDocumentBuilderFactory.setValidating(false);
    _document = localDocumentBuilderFactory.newDocumentBuilder().newDocument();
    _node = _document;
    _nodeStk.push(_document);
  }
  
  public Node getDOM()
  {
    return _node;
  }
  
  public void startDocument() {}
  
  public void endDocument() {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
  {
    Element localElement = _document.createElementNS(paramString1, paramString3);
    if (localElement == null) {
      throw new TxwException("Your DOM provider doesn't support the createElementNS method properly");
    }
    String str2;
    String str3;
    for (int i = 0; i < unprocessedNamespaces.size(); i += 2)
    {
      String str1 = (String)unprocessedNamespaces.get(i + 0);
      str2 = (String)unprocessedNamespaces.get(i + 1);
      if (("".equals(str1)) || (str1 == null)) {
        str3 = "xmlns";
      } else {
        str3 = "xmlns:" + str1;
      }
      if (localElement.hasAttributeNS("http://www.w3.org/2000/xmlns/", str3)) {
        localElement.removeAttributeNS("http://www.w3.org/2000/xmlns/", str3);
      }
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str3, str2);
    }
    unprocessedNamespaces.clear();
    i = paramAttributes.getLength();
    for (int j = 0; j < i; j++)
    {
      str2 = paramAttributes.getURI(j);
      str3 = paramAttributes.getValue(j);
      String str4 = paramAttributes.getQName(j);
      localElement.setAttributeNS(str2, str4, str3);
    }
    getParent().appendChild(localElement);
    _nodeStk.push(localElement);
  }
  
  private final Node getParent()
  {
    return (Node)_nodeStk.peek();
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    _nodeStk.pop();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    Object localObject;
    if (inCDATA) {
      localObject = _document.createCDATASection(new String(paramArrayOfChar, paramInt1, paramInt2));
    } else {
      localObject = _document.createTextNode(new String(paramArrayOfChar, paramInt1, paramInt2));
    }
    getParent().appendChild((Node)localObject);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    getParent().appendChild(_document.createComment(new String(paramArrayOfChar, paramInt1, paramInt2)));
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {}
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    ProcessingInstruction localProcessingInstruction = _document.createProcessingInstruction(paramString1, paramString2);
    getParent().appendChild(localProcessingInstruction);
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString) {}
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    unprocessedNamespaces.add(paramString1);
    unprocessedNamespaces.add(paramString2);
  }
  
  public void endPrefixMapping(String paramString) {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {
    inCDATA = true;
  }
  
  public void endCDATA()
    throws SAXException
  {
    inCDATA = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\Dom2SaxAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */