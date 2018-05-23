package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMDOMException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.NodeSet;
import java.util.Objects;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class DTMNodeProxy
  implements Node, Document, Text, Element, Attr, ProcessingInstruction, Comment, DocumentFragment
{
  public DTM dtm;
  int node;
  private static final String EMPTYSTRING = "";
  static final DOMImplementation implementation = new DTMNodeProxyImplementation();
  protected String fDocumentURI;
  protected String actualEncoding;
  private String xmlEncoding;
  private boolean xmlStandalone;
  private String xmlVersion;
  
  public DTMNodeProxy(DTM paramDTM, int paramInt)
  {
    dtm = paramDTM;
    node = paramInt;
  }
  
  public final DTM getDTM()
  {
    return dtm;
  }
  
  public final int getDTMNodeNumber()
  {
    return node;
  }
  
  public final boolean equals(Node paramNode)
  {
    try
    {
      DTMNodeProxy localDTMNodeProxy = (DTMNodeProxy)paramNode;
      return (node == node) && (dtm == dtm);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public final boolean equals(Object paramObject)
  {
    return ((paramObject instanceof Node)) && (equals((Node)paramObject));
  }
  
  public int hashCode()
  {
    int i = 7;
    i = 29 * i + Objects.hashCode(dtm);
    i = 29 * i + node;
    return i;
  }
  
  public final boolean sameNodeAs(Node paramNode)
  {
    if (!(paramNode instanceof DTMNodeProxy)) {
      return false;
    }
    DTMNodeProxy localDTMNodeProxy = (DTMNodeProxy)paramNode;
    return (dtm == dtm) && (node == node);
  }
  
  public final String getNodeName()
  {
    return dtm.getNodeName(node);
  }
  
  public final String getTarget()
  {
    return dtm.getNodeName(node);
  }
  
  public final String getLocalName()
  {
    return dtm.getLocalName(node);
  }
  
  public final String getPrefix()
  {
    return dtm.getPrefix(node);
  }
  
  public final void setPrefix(String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)7);
  }
  
  public final String getNamespaceURI()
  {
    return dtm.getNamespaceURI(node);
  }
  
  public final boolean supports(String paramString1, String paramString2)
  {
    return implementation.hasFeature(paramString1, paramString2);
  }
  
  public final boolean isSupported(String paramString1, String paramString2)
  {
    return implementation.hasFeature(paramString1, paramString2);
  }
  
  public final String getNodeValue()
    throws DOMException
  {
    return dtm.getNodeValue(node);
  }
  
  public final String getStringValue()
    throws DOMException
  {
    return dtm.getStringValue(node).toString();
  }
  
  public final void setNodeValue(String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)7);
  }
  
  public final short getNodeType()
  {
    return dtm.getNodeType(node);
  }
  
  public final Node getParentNode()
  {
    if (getNodeType() == 2) {
      return null;
    }
    int i = dtm.getParent(node);
    return i == -1 ? null : dtm.getNode(i);
  }
  
  public final Node getOwnerNode()
  {
    int i = dtm.getParent(node);
    return i == -1 ? null : dtm.getNode(i);
  }
  
  public final NodeList getChildNodes()
  {
    return new DTMChildIterNodeList(dtm, node);
  }
  
  public final Node getFirstChild()
  {
    int i = dtm.getFirstChild(node);
    return i == -1 ? null : dtm.getNode(i);
  }
  
  public final Node getLastChild()
  {
    int i = dtm.getLastChild(node);
    return i == -1 ? null : dtm.getNode(i);
  }
  
  public final Node getPreviousSibling()
  {
    int i = dtm.getPreviousSibling(node);
    return i == -1 ? null : dtm.getNode(i);
  }
  
  public final Node getNextSibling()
  {
    if (dtm.getNodeType(node) == 2) {
      return null;
    }
    int i = dtm.getNextSibling(node);
    return i == -1 ? null : dtm.getNode(i);
  }
  
  public final NamedNodeMap getAttributes()
  {
    return new DTMNamedNodeMap(dtm, node);
  }
  
  public boolean hasAttribute(String paramString)
  {
    return -1 != dtm.getAttributeNode(node, null, paramString);
  }
  
  public boolean hasAttributeNS(String paramString1, String paramString2)
  {
    return -1 != dtm.getAttributeNode(node, paramString1, paramString2);
  }
  
  public final Document getOwnerDocument()
  {
    return (Document)dtm.getNode(dtm.getOwnerDocument(node));
  }
  
  public final Node insertBefore(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    throw new DTMDOMException((short)7);
  }
  
  public final Node replaceChild(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    throw new DTMDOMException((short)7);
  }
  
  public final Node removeChild(Node paramNode)
    throws DOMException
  {
    throw new DTMDOMException((short)7);
  }
  
  public final Node appendChild(Node paramNode)
    throws DOMException
  {
    throw new DTMDOMException((short)7);
  }
  
  public final boolean hasChildNodes()
  {
    return -1 != dtm.getFirstChild(node);
  }
  
  public final Node cloneNode(boolean paramBoolean)
  {
    throw new DTMDOMException((short)9);
  }
  
  public final DocumentType getDoctype()
  {
    return null;
  }
  
  public final DOMImplementation getImplementation()
  {
    return implementation;
  }
  
  public final Element getDocumentElement()
  {
    int i = dtm.getDocument();
    int j = -1;
    for (int k = dtm.getFirstChild(i); k != -1; k = dtm.getNextSibling(k)) {
      switch (dtm.getNodeType(k))
      {
      case 1: 
        if (j != -1)
        {
          j = -1;
          k = dtm.getLastChild(i);
        }
        else
        {
          j = k;
        }
        break;
      case 7: 
      case 8: 
      case 10: 
        break;
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 9: 
      default: 
        j = -1;
        k = dtm.getLastChild(i);
      }
    }
    if (j == -1) {
      throw new DTMDOMException((short)9);
    }
    return (Element)dtm.getNode(j);
  }
  
  public final Element createElement(String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final DocumentFragment createDocumentFragment()
  {
    throw new DTMDOMException((short)9);
  }
  
  public final Text createTextNode(String paramString)
  {
    throw new DTMDOMException((short)9);
  }
  
  public final Comment createComment(String paramString)
  {
    throw new DTMDOMException((short)9);
  }
  
  public final CDATASection createCDATASection(String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final Attr createAttribute(String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final EntityReference createEntityReference(String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final NodeList getElementsByTagName(String paramString)
  {
    Vector localVector = new Vector();
    Node localNode = dtm.getNode(node);
    if (localNode != null)
    {
      boolean bool = "*".equals(paramString);
      if (1 == localNode.getNodeType())
      {
        localObject = localNode.getChildNodes();
        for (j = 0; j < ((NodeList)localObject).getLength(); j++) {
          traverseChildren(localVector, ((NodeList)localObject).item(j), paramString, bool);
        }
      }
      else if (9 == localNode.getNodeType())
      {
        traverseChildren(localVector, dtm.getNode(node), paramString, bool);
      }
    }
    int i = localVector.size();
    Object localObject = new NodeSet(i);
    for (int j = 0; j < i; j++) {
      ((NodeSet)localObject).addNode((Node)localVector.elementAt(j));
    }
    return (NodeList)localObject;
  }
  
  private final void traverseChildren(Vector paramVector, Node paramNode, String paramString, boolean paramBoolean)
  {
    if (paramNode == null) {
      return;
    }
    if ((paramNode.getNodeType() == 1) && ((paramBoolean) || (paramNode.getNodeName().equals(paramString)))) {
      paramVector.add(paramNode);
    }
    if (paramNode.hasChildNodes())
    {
      NodeList localNodeList = paramNode.getChildNodes();
      for (int i = 0; i < localNodeList.getLength(); i++) {
        traverseChildren(paramVector, localNodeList.item(i), paramString, paramBoolean);
      }
    }
  }
  
  public final Node importNode(Node paramNode, boolean paramBoolean)
    throws DOMException
  {
    throw new DTMDOMException((short)7);
  }
  
  public final Element createElementNS(String paramString1, String paramString2)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final Attr createAttributeNS(String paramString1, String paramString2)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final NodeList getElementsByTagNameNS(String paramString1, String paramString2)
  {
    Vector localVector = new Vector();
    Node localNode = dtm.getNode(node);
    if (localNode != null)
    {
      boolean bool1 = "*".equals(paramString1);
      boolean bool2 = "*".equals(paramString2);
      if (1 == localNode.getNodeType())
      {
        NodeList localNodeList = localNode.getChildNodes();
        for (int k = 0; k < localNodeList.getLength(); k++) {
          traverseChildren(localVector, localNodeList.item(k), paramString1, paramString2, bool1, bool2);
        }
      }
      else if (9 == localNode.getNodeType())
      {
        traverseChildren(localVector, dtm.getNode(node), paramString1, paramString2, bool1, bool2);
      }
    }
    int i = localVector.size();
    NodeSet localNodeSet = new NodeSet(i);
    for (int j = 0; j < i; j++) {
      localNodeSet.addNode((Node)localVector.elementAt(j));
    }
    return localNodeSet;
  }
  
  private final void traverseChildren(Vector paramVector, Node paramNode, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramNode == null) {
      return;
    }
    Object localObject;
    if ((paramNode.getNodeType() == 1) && ((paramBoolean2) || (paramNode.getLocalName().equals(paramString2))))
    {
      localObject = paramNode.getNamespaceURI();
      if (((paramString1 == null) && (localObject == null)) || (paramBoolean1) || ((paramString1 != null) && (paramString1.equals(localObject)))) {
        paramVector.add(paramNode);
      }
    }
    if (paramNode.hasChildNodes())
    {
      localObject = paramNode.getChildNodes();
      for (int i = 0; i < ((NodeList)localObject).getLength(); i++) {
        traverseChildren(paramVector, ((NodeList)localObject).item(i), paramString1, paramString2, paramBoolean1, paramBoolean2);
      }
    }
  }
  
  public final Element getElementById(String paramString)
  {
    return (Element)dtm.getNode(dtm.getElementById(paramString));
  }
  
  public final Text splitText(int paramInt)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final String getData()
    throws DOMException
  {
    return dtm.getNodeValue(node);
  }
  
  public final void setData(String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final int getLength()
  {
    return dtm.getNodeValue(node).length();
  }
  
  public final String substringData(int paramInt1, int paramInt2)
    throws DOMException
  {
    return getData().substring(paramInt1, paramInt1 + paramInt2);
  }
  
  public final void appendData(String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final void insertData(int paramInt, String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final void deleteData(int paramInt1, int paramInt2)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final void replaceData(int paramInt1, int paramInt2, String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final String getTagName()
  {
    return dtm.getNodeName(node);
  }
  
  public final String getAttribute(String paramString)
  {
    DTMNamedNodeMap localDTMNamedNodeMap = new DTMNamedNodeMap(dtm, node);
    Node localNode = localDTMNamedNodeMap.getNamedItem(paramString);
    return null == localNode ? "" : localNode.getNodeValue();
  }
  
  public final void setAttribute(String paramString1, String paramString2)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final void removeAttribute(String paramString)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final Attr getAttributeNode(String paramString)
  {
    DTMNamedNodeMap localDTMNamedNodeMap = new DTMNamedNodeMap(dtm, node);
    return (Attr)localDTMNamedNodeMap.getNamedItem(paramString);
  }
  
  public final Attr setAttributeNode(Attr paramAttr)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final Attr removeAttributeNode(Attr paramAttr)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public boolean hasAttributes()
  {
    return -1 != dtm.getFirstAttribute(node);
  }
  
  public final void normalize()
  {
    throw new DTMDOMException((short)9);
  }
  
  public final String getAttributeNS(String paramString1, String paramString2)
  {
    Node localNode = null;
    int i = dtm.getAttributeNode(node, paramString1, paramString2);
    if (i != -1) {
      localNode = dtm.getNode(i);
    }
    return null == localNode ? "" : localNode.getNodeValue();
  }
  
  public final void setAttributeNS(String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final void removeAttributeNS(String paramString1, String paramString2)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final Attr getAttributeNodeNS(String paramString1, String paramString2)
  {
    Attr localAttr = null;
    int i = dtm.getAttributeNode(node, paramString1, paramString2);
    if (i != -1) {
      localAttr = (Attr)dtm.getNode(i);
    }
    return localAttr;
  }
  
  public final Attr setAttributeNodeNS(Attr paramAttr)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public final String getName()
  {
    return dtm.getNodeName(node);
  }
  
  public final boolean getSpecified()
  {
    return true;
  }
  
  public final String getValue()
  {
    return dtm.getNodeValue(node);
  }
  
  public final void setValue(String paramString)
  {
    throw new DTMDOMException((short)9);
  }
  
  public final Element getOwnerElement()
  {
    if (getNodeType() != 2) {
      return null;
    }
    int i = dtm.getParent(node);
    return i == -1 ? null : (Element)dtm.getNode(i);
  }
  
  public Node adoptNode(Node paramNode)
    throws DOMException
  {
    throw new DTMDOMException((short)9);
  }
  
  public String getInputEncoding()
  {
    throw new DTMDOMException((short)9);
  }
  
  public void setEncoding(String paramString)
  {
    throw new DTMDOMException((short)9);
  }
  
  public boolean getStandalone()
  {
    throw new DTMDOMException((short)9);
  }
  
  public void setStandalone(boolean paramBoolean)
  {
    throw new DTMDOMException((short)9);
  }
  
  public boolean getStrictErrorChecking()
  {
    throw new DTMDOMException((short)9);
  }
  
  public void setStrictErrorChecking(boolean paramBoolean)
  {
    throw new DTMDOMException((short)9);
  }
  
  public String getVersion()
  {
    throw new DTMDOMException((short)9);
  }
  
  public void setVersion(String paramString)
  {
    throw new DTMDOMException((short)9);
  }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler)
  {
    return getOwnerDocument().setUserData(paramString, paramObject, paramUserDataHandler);
  }
  
  public Object getUserData(String paramString)
  {
    return getOwnerDocument().getUserData(paramString);
  }
  
  public Object getFeature(String paramString1, String paramString2)
  {
    return isSupported(paramString1, paramString2) ? this : null;
  }
  
  public boolean isEqualNode(Node paramNode)
  {
    if (paramNode == this) {
      return true;
    }
    if (paramNode.getNodeType() != getNodeType()) {
      return false;
    }
    if (getNodeName() == null)
    {
      if (paramNode.getNodeName() != null) {
        return false;
      }
    }
    else if (!getNodeName().equals(paramNode.getNodeName())) {
      return false;
    }
    if (getLocalName() == null)
    {
      if (paramNode.getLocalName() != null) {
        return false;
      }
    }
    else if (!getLocalName().equals(paramNode.getLocalName())) {
      return false;
    }
    if (getNamespaceURI() == null)
    {
      if (paramNode.getNamespaceURI() != null) {
        return false;
      }
    }
    else if (!getNamespaceURI().equals(paramNode.getNamespaceURI())) {
      return false;
    }
    if (getPrefix() == null)
    {
      if (paramNode.getPrefix() != null) {
        return false;
      }
    }
    else if (!getPrefix().equals(paramNode.getPrefix())) {
      return false;
    }
    if (getNodeValue() == null)
    {
      if (paramNode.getNodeValue() != null) {
        return false;
      }
    }
    else if (!getNodeValue().equals(paramNode.getNodeValue())) {
      return false;
    }
    return true;
  }
  
  public String lookupNamespaceURI(String paramString)
  {
    int i = getNodeType();
    switch (i)
    {
    case 1: 
      String str1 = getNamespaceURI();
      String str2 = getPrefix();
      if (str1 != null)
      {
        if ((paramString == null) && (str2 == paramString)) {
          return str1;
        }
        if ((str2 != null) && (str2.equals(paramString))) {
          return str1;
        }
      }
      if (hasAttributes())
      {
        NamedNodeMap localNamedNodeMap = getAttributes();
        int j = localNamedNodeMap.getLength();
        for (int k = 0; k < j; k++)
        {
          Node localNode = localNamedNodeMap.item(k);
          String str3 = localNode.getPrefix();
          String str4 = localNode.getNodeValue();
          str1 = localNode.getNamespaceURI();
          if ((str1 != null) && (str1.equals("http://www.w3.org/2000/xmlns/")))
          {
            if ((paramString == null) && (localNode.getNodeName().equals("xmlns"))) {
              return str4;
            }
            if ((str3 != null) && (str3.equals("xmlns")) && (localNode.getLocalName().equals(paramString))) {
              return str4;
            }
          }
        }
      }
      return null;
    case 6: 
    case 10: 
    case 11: 
    case 12: 
      return null;
    case 2: 
      if (getOwnerElement().getNodeType() == 1) {
        return getOwnerElement().lookupNamespaceURI(paramString);
      }
      return null;
    }
    return null;
  }
  
  public boolean isDefaultNamespace(String paramString)
  {
    return false;
  }
  
  public String lookupPrefix(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = getNodeType();
    switch (i)
    {
    case 6: 
    case 10: 
    case 11: 
    case 12: 
      return null;
    case 2: 
      if (getOwnerElement().getNodeType() == 1) {
        return getOwnerElement().lookupPrefix(paramString);
      }
      return null;
    }
    return null;
  }
  
  public boolean isSameNode(Node paramNode)
  {
    return this == paramNode;
  }
  
  public void setTextContent(String paramString)
    throws DOMException
  {
    setNodeValue(paramString);
  }
  
  public String getTextContent()
    throws DOMException
  {
    return dtm.getStringValue(node).toString();
  }
  
  public short compareDocumentPosition(Node paramNode)
    throws DOMException
  {
    return 0;
  }
  
  public String getBaseURI()
  {
    return null;
  }
  
  public Node renameNode(Node paramNode, String paramString1, String paramString2)
    throws DOMException
  {
    return paramNode;
  }
  
  public void normalizeDocument() {}
  
  public DOMConfiguration getDomConfig()
  {
    return null;
  }
  
  public void setDocumentURI(String paramString)
  {
    fDocumentURI = paramString;
  }
  
  public String getDocumentURI()
  {
    return fDocumentURI;
  }
  
  public String getActualEncoding()
  {
    return actualEncoding;
  }
  
  public void setActualEncoding(String paramString)
  {
    actualEncoding = paramString;
  }
  
  public Text replaceWholeText(String paramString)
    throws DOMException
  {
    return null;
  }
  
  public String getWholeText()
  {
    return null;
  }
  
  public boolean isElementContentWhitespace()
  {
    return false;
  }
  
  public void setIdAttribute(boolean paramBoolean) {}
  
  public void setIdAttribute(String paramString, boolean paramBoolean) {}
  
  public void setIdAttributeNode(Attr paramAttr, boolean paramBoolean) {}
  
  public void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean) {}
  
  public TypeInfo getSchemaTypeInfo()
  {
    return null;
  }
  
  public boolean isId()
  {
    return false;
  }
  
  public String getXmlEncoding()
  {
    return xmlEncoding;
  }
  
  public void setXmlEncoding(String paramString)
  {
    xmlEncoding = paramString;
  }
  
  public boolean getXmlStandalone()
  {
    return xmlStandalone;
  }
  
  public void setXmlStandalone(boolean paramBoolean)
    throws DOMException
  {
    xmlStandalone = paramBoolean;
  }
  
  public String getXmlVersion()
  {
    return xmlVersion;
  }
  
  public void setXmlVersion(String paramString)
    throws DOMException
  {
    xmlVersion = paramString;
  }
  
  static class DTMNodeProxyImplementation
    implements DOMImplementation
  {
    DTMNodeProxyImplementation() {}
    
    public DocumentType createDocumentType(String paramString1, String paramString2, String paramString3)
    {
      throw new DTMDOMException((short)9);
    }
    
    public Document createDocument(String paramString1, String paramString2, DocumentType paramDocumentType)
    {
      throw new DTMDOMException((short)9);
    }
    
    public boolean hasFeature(String paramString1, String paramString2)
    {
      return (("CORE".equals(paramString1.toUpperCase())) || ("XML".equals(paramString1.toUpperCase()))) && (("1.0".equals(paramString2)) || ("2.0".equals(paramString2)));
    }
    
    public Object getFeature(String paramString1, String paramString2)
    {
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMNodeProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */