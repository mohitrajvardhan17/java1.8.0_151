package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.AttList;
import com.sun.org.apache.xml.internal.serializer.utils.DOM2Helper;
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

public final class TreeWalker
{
  private final ContentHandler m_contentHandler;
  private final SerializationHandler m_Serializer;
  protected final DOM2Helper m_dh;
  private final LocatorImpl m_locator = new LocatorImpl();
  boolean nextIsRaw = false;
  
  public ContentHandler getContentHandler()
  {
    return m_contentHandler;
  }
  
  public TreeWalker(ContentHandler paramContentHandler)
  {
    this(paramContentHandler, null);
  }
  
  public TreeWalker(ContentHandler paramContentHandler, String paramString)
  {
    m_contentHandler = paramContentHandler;
    if ((m_contentHandler instanceof SerializationHandler)) {
      m_Serializer = ((SerializationHandler)m_contentHandler);
    } else {
      m_Serializer = null;
    }
    m_contentHandler.setDocumentLocator(m_locator);
    if (paramString != null) {
      m_locator.setSystemId(paramString);
    }
    m_dh = new DOM2Helper();
  }
  
  public void traverse(Node paramNode)
    throws SAXException
  {
    m_contentHandler.startDocument();
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
    m_contentHandler.endDocument();
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
    if (m_Serializer != null)
    {
      m_Serializer.characters(paramNode);
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
    Object localObject3;
    String str3;
    switch (paramNode.getNodeType())
    {
    case 8: 
      localObject1 = ((Comment)paramNode).getData();
      if ((m_contentHandler instanceof LexicalHandler))
      {
        localObject2 = (LexicalHandler)m_contentHandler;
        ((LexicalHandler)localObject2).comment(((String)localObject1).toCharArray(), 0, ((String)localObject1).length());
      }
      break;
    case 11: 
      break;
    case 9: 
      break;
    case 1: 
      localObject1 = (Element)paramNode;
      localObject2 = ((Element)localObject1).getNamespaceURI();
      if (localObject2 != null)
      {
        String str1 = ((Element)localObject1).getPrefix();
        if (str1 == null) {
          str1 = "";
        }
        m_contentHandler.startPrefixMapping(str1, (String)localObject2);
      }
      localObject2 = ((Element)localObject1).getAttributes();
      int i = ((NamedNodeMap)localObject2).getLength();
      for (int j = 0; j < i; j++)
      {
        localObject3 = ((NamedNodeMap)localObject2).item(j);
        str3 = ((Node)localObject3).getNodeName();
        int k = str3.indexOf(':');
        String str4;
        if ((str3.equals("xmlns")) || (str3.startsWith("xmlns:")))
        {
          if (k < 0) {
            str4 = "";
          } else {
            str4 = str3.substring(k + 1);
          }
          m_contentHandler.startPrefixMapping(str4, ((Node)localObject3).getNodeValue());
        }
        else if (k > 0)
        {
          str4 = str3.substring(0, k);
          String str5 = ((Node)localObject3).getNamespaceURI();
          if (str5 != null) {
            m_contentHandler.startPrefixMapping(str4, str5);
          }
        }
      }
      String str2 = m_dh.getNamespaceOfNode(paramNode);
      if (null == str2) {
        str2 = "";
      }
      m_contentHandler.startElement(str2, m_dh.getLocalNameOfNode(paramNode), paramNode.getNodeName(), new AttList((NamedNodeMap)localObject2, m_dh));
      break;
    case 7: 
      localObject3 = (ProcessingInstruction)paramNode;
      str3 = ((ProcessingInstruction)localObject3).getNodeName();
      if (str3.equals("xslt-next-is-raw")) {
        nextIsRaw = true;
      } else {
        m_contentHandler.processingInstruction(((ProcessingInstruction)localObject3).getNodeName(), ((ProcessingInstruction)localObject3).getData());
      }
      break;
    case 4: 
      boolean bool = m_contentHandler instanceof LexicalHandler;
      str3 = bool ? (LexicalHandler)m_contentHandler : null;
      if (bool) {
        str3.startCDATA();
      }
      dispatachChars(paramNode);
      if (bool) {
        str3.endCDATA();
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
    Object localObject1;
    Object localObject2;
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
      if (m_Serializer == null)
      {
        localObject1 = (Element)paramNode;
        localObject2 = ((Element)localObject1).getAttributes();
        int i = ((NamedNodeMap)localObject2).getLength();
        Object localObject3;
        for (int j = i - 1; 0 <= j; j--)
        {
          localObject3 = ((NamedNodeMap)localObject2).item(j);
          String str3 = ((Node)localObject3).getNodeName();
          int k = str3.indexOf(':');
          String str4;
          if ((str3.equals("xmlns")) || (str3.startsWith("xmlns:")))
          {
            if (k < 0) {
              str4 = "";
            } else {
              str4 = str3.substring(k + 1);
            }
            m_contentHandler.endPrefixMapping(str4);
          }
          else if (k > 0)
          {
            str4 = str3.substring(0, k);
            m_contentHandler.endPrefixMapping(str4);
          }
        }
        String str2 = ((Element)localObject1).getNamespaceURI();
        if (str2 != null)
        {
          localObject3 = ((Element)localObject1).getPrefix();
          if (localObject3 == null) {
            localObject3 = "";
          }
          m_contentHandler.endPrefixMapping((String)localObject3);
        }
      }
      break;
    case 4: 
      break;
    case 5: 
      localObject1 = (EntityReference)paramNode;
      if ((m_contentHandler instanceof LexicalHandler))
      {
        localObject2 = (LexicalHandler)m_contentHandler;
        ((LexicalHandler)localObject2).endEntity(((EntityReference)localObject1).getNodeName());
      }
      break;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\TreeWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */