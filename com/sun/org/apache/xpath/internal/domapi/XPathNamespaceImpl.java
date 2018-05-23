package com.sun.org.apache.xpath.internal.domapi;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.xpath.XPathNamespace;

class XPathNamespaceImpl
  implements XPathNamespace
{
  private final Node m_attributeNode;
  private String textContent;
  
  XPathNamespaceImpl(Node paramNode)
  {
    m_attributeNode = paramNode;
  }
  
  public Element getOwnerElement()
  {
    return ((Attr)m_attributeNode).getOwnerElement();
  }
  
  public String getNodeName()
  {
    return "#namespace";
  }
  
  public String getNodeValue()
    throws DOMException
  {
    return m_attributeNode.getNodeValue();
  }
  
  public void setNodeValue(String paramString)
    throws DOMException
  {}
  
  public short getNodeType()
  {
    return 13;
  }
  
  public Node getParentNode()
  {
    return m_attributeNode.getParentNode();
  }
  
  public NodeList getChildNodes()
  {
    return m_attributeNode.getChildNodes();
  }
  
  public Node getFirstChild()
  {
    return m_attributeNode.getFirstChild();
  }
  
  public Node getLastChild()
  {
    return m_attributeNode.getLastChild();
  }
  
  public Node getPreviousSibling()
  {
    return m_attributeNode.getPreviousSibling();
  }
  
  public Node getNextSibling()
  {
    return m_attributeNode.getNextSibling();
  }
  
  public NamedNodeMap getAttributes()
  {
    return m_attributeNode.getAttributes();
  }
  
  public Document getOwnerDocument()
  {
    return m_attributeNode.getOwnerDocument();
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    return null;
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    return null;
  }
  
  public Node removeChild(Node paramNode)
    throws DOMException
  {
    return null;
  }
  
  public Node appendChild(Node paramNode)
    throws DOMException
  {
    return null;
  }
  
  public boolean hasChildNodes()
  {
    return false;
  }
  
  public Node cloneNode(boolean paramBoolean)
  {
    throw new DOMException((short)9, null);
  }
  
  public void normalize()
  {
    m_attributeNode.normalize();
  }
  
  public boolean isSupported(String paramString1, String paramString2)
  {
    return m_attributeNode.isSupported(paramString1, paramString2);
  }
  
  public String getNamespaceURI()
  {
    return m_attributeNode.getNodeValue();
  }
  
  public String getPrefix()
  {
    return m_attributeNode.getPrefix();
  }
  
  public void setPrefix(String paramString)
    throws DOMException
  {}
  
  public String getLocalName()
  {
    return m_attributeNode.getPrefix();
  }
  
  public boolean hasAttributes()
  {
    return m_attributeNode.hasAttributes();
  }
  
  public String getBaseURI()
  {
    return null;
  }
  
  public short compareDocumentPosition(Node paramNode)
    throws DOMException
  {
    return 0;
  }
  
  public String getTextContent()
    throws DOMException
  {
    return textContent;
  }
  
  public void setTextContent(String paramString)
    throws DOMException
  {
    textContent = paramString;
  }
  
  public boolean isSameNode(Node paramNode)
  {
    return false;
  }
  
  public String lookupPrefix(String paramString)
  {
    return "";
  }
  
  public boolean isDefaultNamespace(String paramString)
  {
    return false;
  }
  
  public String lookupNamespaceURI(String paramString)
  {
    return null;
  }
  
  public boolean isEqualNode(Node paramNode)
  {
    return false;
  }
  
  public Object getFeature(String paramString1, String paramString2)
  {
    return null;
  }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler)
  {
    return null;
  }
  
  public Object getUserData(String paramString)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathNamespaceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */