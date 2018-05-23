package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.dtm.ref.dom2dtm.DOM2DTM.CharacterNodeHandler;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

public class TreeWalker
{
  private ContentHandler m_contentHandler = null;
  protected DOMHelper m_dh;
  private LocatorImpl m_locator = new LocatorImpl();
  boolean nextIsRaw = false;
  
  public ContentHandler getContentHandler()
  {
    return m_contentHandler;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    m_contentHandler = paramContentHandler;
  }
  
  public TreeWalker(ContentHandler paramContentHandler, DOMHelper paramDOMHelper, String paramString)
  {
    m_contentHandler = paramContentHandler;
    m_contentHandler.setDocumentLocator(m_locator);
    if (paramString != null) {
      m_locator.setSystemId(paramString);
    }
    m_dh = paramDOMHelper;
  }
  
  public TreeWalker(ContentHandler paramContentHandler, DOMHelper paramDOMHelper)
  {
    m_contentHandler = paramContentHandler;
    m_contentHandler.setDocumentLocator(m_locator);
    m_dh = paramDOMHelper;
  }
  
  public TreeWalker(ContentHandler paramContentHandler)
  {
    m_contentHandler = paramContentHandler;
    if (m_contentHandler != null) {
      m_contentHandler.setDocumentLocator(m_locator);
    }
    m_dh = new DOM2Helper();
  }
  
  public void traverse(Node paramNode)
    throws SAXException
  {
    m_contentHandler.startDocument();
    traverseFragment(paramNode);
    m_contentHandler.endDocument();
  }
  
  public void traverseFragment(Node paramNode)
    throws SAXException
  {
    Node localNode1 = paramNode;
    while (null != paramNode)
    {
      startNode(paramNode);
      Node localNode2 = paramNode.getFirstChild();
      while (null == localNode2)
      {
        endNode(paramNode);
        if (!localNode1.equals(paramNode))
        {
          localNode2 = paramNode.getNextSibling();
          if (null == localNode2)
          {
            paramNode = paramNode.getParentNode();
            if ((null == paramNode) || (localNode1.equals(paramNode)))
            {
              if (null != paramNode) {
                endNode(paramNode);
              }
              localNode2 = null;
            }
          }
        }
      }
      paramNode = localNode2;
    }
  }
  
  public void traverse(Node paramNode1, Node paramNode2)
    throws SAXException
  {
    m_contentHandler.startDocument();
    while (null != paramNode1)
    {
      startNode(paramNode1);
      Node localNode = paramNode1.getFirstChild();
      while (null == localNode)
      {
        endNode(paramNode1);
        if ((null == paramNode2) || (!paramNode2.equals(paramNode1)))
        {
          localNode = paramNode1.getNextSibling();
          if (null == localNode)
          {
            paramNode1 = paramNode1.getParentNode();
            if ((null == paramNode1) || ((null != paramNode2) && (paramNode2.equals(paramNode1)))) {
              localNode = null;
            }
          }
        }
      }
      paramNode1 = localNode;
    }
    m_contentHandler.endDocument();
  }
  
  private final void dispatachChars(Node paramNode)
    throws SAXException
  {
    if ((m_contentHandler instanceof DOM2DTM.CharacterNodeHandler))
    {
      ((DOM2DTM.CharacterNodeHandler)m_contentHandler).characters(paramNode);
    }
    else
    {
      String str = ((Text)paramNode).getData();
      m_contentHandler.characters(str.toCharArray(), 0, str.length());
    }
  }
  
  protected void startNode(Node paramNode)
    throws SAXException
  {
    if ((m_contentHandler instanceof NodeConsumer)) {
      ((NodeConsumer)m_contentHandler).setOriginatingNode(paramNode);
    }
    Object localObject1;
    if ((paramNode instanceof Locator))
    {
      localObject1 = (Locator)paramNode;
      m_locator.setColumnNumber(((Locator)localObject1).getColumnNumber());
      m_locator.setLineNumber(((Locator)localObject1).getLineNumber());
      m_locator.setPublicId(((Locator)localObject1).getPublicId());
      m_locator.setSystemId(((Locator)localObject1).getSystemId());
    }
    else
    {
      m_locator.setColumnNumber(0);
      m_locator.setLineNumber(0);
    }
    Object localObject2;
    String str2;
    switch (paramNode.getNodeType())
    {
    case 8: 
      localObject1 = ((Comment)paramNode).getData();
      if ((m_contentHandler instanceof LexicalHandler))
      {
        LexicalHandler localLexicalHandler = (LexicalHandler)m_contentHandler;
        localLexicalHandler.comment(((String)localObject1).toCharArray(), 0, ((String)localObject1).length());
      }
      break;
    case 11: 
      break;
    case 9: 
      break;
    case 1: 
      localObject1 = ((Element)paramNode).getAttributes();
      int i = ((NamedNodeMap)localObject1).getLength();
      for (int j = 0; j < i; j++)
      {
        localObject2 = ((NamedNodeMap)localObject1).item(j);
        str2 = ((Node)localObject2).getNodeName();
        if ((str2.equals("xmlns")) || (str2.startsWith("xmlns:")))
        {
          int k;
          String str3 = (k = str2.indexOf(":")) < 0 ? "" : str2.substring(k + 1);
          m_contentHandler.startPrefixMapping(str3, ((Node)localObject2).getNodeValue());
        }
      }
      String str1 = m_dh.getNamespaceOfNode(paramNode);
      if (null == str1) {
        str1 = "";
      }
      m_contentHandler.startElement(str1, m_dh.getLocalNameOfNode(paramNode), paramNode.getNodeName(), new AttList((NamedNodeMap)localObject1, m_dh));
      break;
    case 7: 
      localObject2 = (ProcessingInstruction)paramNode;
      str2 = ((ProcessingInstruction)localObject2).getNodeName();
      if (str2.equals("xslt-next-is-raw")) {
        nextIsRaw = true;
      } else {
        m_contentHandler.processingInstruction(((ProcessingInstruction)localObject2).getNodeName(), ((ProcessingInstruction)localObject2).getData());
      }
      break;
    case 4: 
      boolean bool = m_contentHandler instanceof LexicalHandler;
      str2 = bool ? (LexicalHandler)m_contentHandler : null;
      if (bool) {
        str2.startCDATA();
      }
      dispatachChars(paramNode);
      if (bool) {
        str2.endCDATA();
      }
      break;
    case 3: 
      if (nextIsRaw)
      {
        nextIsRaw = false;
        m_contentHandler.processingInstruction("javax.xml.transform.disable-output-escaping", "");
        dispatachChars(paramNode);
        m_contentHandler.processingInstruction("javax.xml.transform.enable-output-escaping", "");
      }
      else
      {
        dispatachChars(paramNode);
      }
      break;
    case 5: 
      EntityReference localEntityReference = (EntityReference)paramNode;
      if ((m_contentHandler instanceof LexicalHandler)) {
        ((LexicalHandler)m_contentHandler).startEntity(localEntityReference.getNodeName());
      }
      break;
    }
  }
  
  protected void endNode(Node paramNode)
    throws SAXException
  {
    Object localObject;
    switch (paramNode.getNodeType())
    {
    case 9: 
      break;
    case 1: 
      String str1 = m_dh.getNamespaceOfNode(paramNode);
      if (null == str1) {
        str1 = "";
      }
      m_contentHandler.endElement(str1, m_dh.getLocalNameOfNode(paramNode), paramNode.getNodeName());
      NamedNodeMap localNamedNodeMap = ((Element)paramNode).getAttributes();
      int i = localNamedNodeMap.getLength();
      for (int j = 0; j < i; j++)
      {
        localObject = localNamedNodeMap.item(j);
        String str2 = ((Node)localObject).getNodeName();
        if ((str2.equals("xmlns")) || (str2.startsWith("xmlns:")))
        {
          int k;
          String str3 = (k = str2.indexOf(":")) < 0 ? "" : str2.substring(k + 1);
          m_contentHandler.endPrefixMapping(str3);
        }
      }
      break;
    case 4: 
      break;
    case 5: 
      EntityReference localEntityReference = (EntityReference)paramNode;
      if ((m_contentHandler instanceof LexicalHandler))
      {
        localObject = (LexicalHandler)m_contentHandler;
        ((LexicalHandler)localObject).endEntity(localEntityReference.getNodeName());
      }
      break;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\TreeWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */