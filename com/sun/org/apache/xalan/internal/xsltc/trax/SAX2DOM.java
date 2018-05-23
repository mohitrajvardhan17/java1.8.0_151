package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Constants;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import java.util.Stack;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;

public class SAX2DOM
  implements ContentHandler, LexicalHandler, Constants
{
  private Node _root = null;
  private Document _document = null;
  private Node _nextSibling = null;
  private Stack _nodeStk = new Stack();
  private Vector _namespaceDecls = null;
  private Node _lastSibling = null;
  private Locator locator = null;
  private boolean needToSetDocumentInfo = true;
  private StringBuilder _textBuffer = new StringBuilder();
  private Node _nextSiblingCache = null;
  private DocumentBuilderFactory _factory = DocumentBuilderFactory.newInstance();
  private boolean _internal = true;
  
  public SAX2DOM(boolean paramBoolean)
    throws ParserConfigurationException
  {
    _document = createDocument(paramBoolean);
    _root = _document;
  }
  
  public SAX2DOM(Node paramNode1, Node paramNode2, boolean paramBoolean)
    throws ParserConfigurationException
  {
    _root = paramNode1;
    if ((paramNode1 instanceof Document))
    {
      _document = ((Document)paramNode1);
    }
    else if (paramNode1 != null)
    {
      _document = paramNode1.getOwnerDocument();
    }
    else
    {
      _document = createDocument(paramBoolean);
      _root = _document;
    }
    _nextSibling = paramNode2;
  }
  
  public SAX2DOM(Node paramNode, boolean paramBoolean)
    throws ParserConfigurationException
  {
    this(paramNode, null, paramBoolean);
  }
  
  public Node getDOM()
  {
    return _root;
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return;
    }
    Node localNode = (Node)_nodeStk.peek();
    if (localNode != _document)
    {
      _nextSiblingCache = _nextSibling;
      _textBuffer.append(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  private void appendTextNode()
  {
    if (_textBuffer.length() > 0)
    {
      Node localNode = (Node)_nodeStk.peek();
      if ((localNode == _root) && (_nextSiblingCache != null)) {
        _lastSibling = localNode.insertBefore(_document.createTextNode(_textBuffer.toString()), _nextSiblingCache);
      } else {
        _lastSibling = localNode.appendChild(_document.createTextNode(_textBuffer.toString()));
      }
      _textBuffer.setLength(0);
    }
  }
  
  public void startDocument()
  {
    _nodeStk.push(_root);
  }
  
  public void endDocument()
  {
    _nodeStk.pop();
  }
  
  private void setDocumentInfo()
  {
    if (locator == null) {
      return;
    }
    try
    {
      _document.setXmlVersion(((Locator2)locator).getXMLVersion());
    }
    catch (ClassCastException localClassCastException) {}
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
  {
    appendTextNode();
    if (needToSetDocumentInfo)
    {
      setDocumentInfo();
      needToSetDocumentInfo = false;
    }
    Element localElement = _document.createElementNS(paramString1, paramString3);
    String str1;
    if (_namespaceDecls != null)
    {
      i = _namespaceDecls.size();
      for (j = 0; j < i; j++)
      {
        str1 = (String)_namespaceDecls.elementAt(j++);
        if ((str1 == null) || (str1.equals(""))) {
          localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", (String)_namespaceDecls.elementAt(j));
        } else {
          localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str1, (String)_namespaceDecls.elementAt(j));
        }
      }
      _namespaceDecls.clear();
    }
    int i = paramAttributes.getLength();
    for (int j = 0; j < i; j++)
    {
      str1 = paramAttributes.getQName(j);
      String str2 = paramAttributes.getURI(j);
      if (paramAttributes.getLocalName(j).equals(""))
      {
        localElement.setAttribute(str1, paramAttributes.getValue(j));
        if (paramAttributes.getType(j).equals("ID")) {
          localElement.setIdAttribute(str1, true);
        }
      }
      else
      {
        localElement.setAttributeNS(str2, str1, paramAttributes.getValue(j));
        if (paramAttributes.getType(j).equals("ID")) {
          localElement.setIdAttributeNS(str2, paramAttributes.getLocalName(j), true);
        }
      }
    }
    Node localNode = (Node)_nodeStk.peek();
    if ((localNode == _root) && (_nextSibling != null)) {
      localNode.insertBefore(localElement, _nextSibling);
    } else {
      localNode.appendChild(localElement);
    }
    _nodeStk.push(localElement);
    _lastSibling = null;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    appendTextNode();
    _nodeStk.pop();
    _lastSibling = null;
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    if (_namespaceDecls == null) {
      _namespaceDecls = new Vector(2);
    }
    _namespaceDecls.addElement(paramString1);
    _namespaceDecls.addElement(paramString2);
  }
  
  public void endPrefixMapping(String paramString) {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {}
  
  public void processingInstruction(String paramString1, String paramString2)
  {
    appendTextNode();
    Node localNode = (Node)_nodeStk.peek();
    ProcessingInstruction localProcessingInstruction = _document.createProcessingInstruction(paramString1, paramString2);
    if (localProcessingInstruction != null)
    {
      if ((localNode == _root) && (_nextSibling != null)) {
        localNode.insertBefore(localProcessingInstruction, _nextSibling);
      } else {
        localNode.appendChild(localProcessingInstruction);
      }
      _lastSibling = localProcessingInstruction;
    }
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    locator = paramLocator;
  }
  
  public void skippedEntity(String paramString) {}
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    appendTextNode();
    Node localNode = (Node)_nodeStk.peek();
    Comment localComment = _document.createComment(new String(paramArrayOfChar, paramInt1, paramInt2));
    if (localComment != null)
    {
      if ((localNode == _root) && (_nextSibling != null)) {
        localNode.insertBefore(localComment, _nextSibling);
      } else {
        localNode.appendChild(localComment);
      }
      _lastSibling = localComment;
    }
  }
  
  public void startCDATA() {}
  
  public void endCDATA() {}
  
  public void startEntity(String paramString) {}
  
  public void endDTD() {}
  
  public void endEntity(String paramString) {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  private Document createDocument(boolean paramBoolean)
    throws ParserConfigurationException
  {
    if (_factory == null) {
      if (paramBoolean)
      {
        _factory = DocumentBuilderFactory.newInstance();
        if (!(_factory instanceof DocumentBuilderFactoryImpl)) {
          _internal = false;
        }
      }
      else
      {
        _factory = DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", SAX2DOM.class.getClassLoader());
      }
    }
    Document localDocument;
    if (_internal) {
      localDocument = _factory.newDocumentBuilder().newDocument();
    } else {
      synchronized (SAX2DOM.class)
      {
        localDocument = _factory.newDocumentBuilder().newDocument();
      }
    }
    return localDocument;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SAX2DOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */