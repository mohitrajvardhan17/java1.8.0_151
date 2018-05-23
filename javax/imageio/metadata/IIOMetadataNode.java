package javax.imageio.metadata;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class IIOMetadataNode
  implements Element, NodeList
{
  private String nodeName = null;
  private String nodeValue = null;
  private Object userObject = null;
  private IIOMetadataNode parent = null;
  private int numChildren = 0;
  private IIOMetadataNode firstChild = null;
  private IIOMetadataNode lastChild = null;
  private IIOMetadataNode nextSibling = null;
  private IIOMetadataNode previousSibling = null;
  private List attributes = new ArrayList();
  
  public IIOMetadataNode() {}
  
  public IIOMetadataNode(String paramString)
  {
    nodeName = paramString;
  }
  
  private void checkNode(Node paramNode)
    throws DOMException
  {
    if (paramNode == null) {
      return;
    }
    if (!(paramNode instanceof IIOMetadataNode)) {
      throw new IIODOMException((short)4, "Node not an IIOMetadataNode!");
    }
  }
  
  public String getNodeName()
  {
    return nodeName;
  }
  
  public String getNodeValue()
  {
    return nodeValue;
  }
  
  public void setNodeValue(String paramString)
  {
    nodeValue = paramString;
  }
  
  public short getNodeType()
  {
    return 1;
  }
  
  public Node getParentNode()
  {
    return parent;
  }
  
  public NodeList getChildNodes()
  {
    return this;
  }
  
  public Node getFirstChild()
  {
    return firstChild;
  }
  
  public Node getLastChild()
  {
    return lastChild;
  }
  
  public Node getPreviousSibling()
  {
    return previousSibling;
  }
  
  public Node getNextSibling()
  {
    return nextSibling;
  }
  
  public NamedNodeMap getAttributes()
  {
    return new IIONamedNodeMap(attributes);
  }
  
  public Document getOwnerDocument()
  {
    return null;
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2)
  {
    if (paramNode1 == null) {
      throw new IllegalArgumentException("newChild == null!");
    }
    checkNode(paramNode1);
    checkNode(paramNode2);
    IIOMetadataNode localIIOMetadataNode1 = (IIOMetadataNode)paramNode1;
    IIOMetadataNode localIIOMetadataNode2 = (IIOMetadataNode)paramNode2;
    IIOMetadataNode localIIOMetadataNode3 = null;
    IIOMetadataNode localIIOMetadataNode4 = null;
    if (paramNode2 == null)
    {
      localIIOMetadataNode3 = lastChild;
      localIIOMetadataNode4 = null;
      lastChild = localIIOMetadataNode1;
    }
    else
    {
      localIIOMetadataNode3 = previousSibling;
      localIIOMetadataNode4 = localIIOMetadataNode2;
    }
    if (localIIOMetadataNode3 != null) {
      nextSibling = localIIOMetadataNode1;
    }
    if (localIIOMetadataNode4 != null) {
      previousSibling = localIIOMetadataNode1;
    }
    parent = this;
    previousSibling = localIIOMetadataNode3;
    nextSibling = localIIOMetadataNode4;
    if (firstChild == localIIOMetadataNode2) {
      firstChild = localIIOMetadataNode1;
    }
    numChildren += 1;
    return localIIOMetadataNode1;
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2)
  {
    if (paramNode1 == null) {
      throw new IllegalArgumentException("newChild == null!");
    }
    checkNode(paramNode1);
    checkNode(paramNode2);
    IIOMetadataNode localIIOMetadataNode1 = (IIOMetadataNode)paramNode1;
    IIOMetadataNode localIIOMetadataNode2 = (IIOMetadataNode)paramNode2;
    IIOMetadataNode localIIOMetadataNode3 = previousSibling;
    IIOMetadataNode localIIOMetadataNode4 = nextSibling;
    if (localIIOMetadataNode3 != null) {
      nextSibling = localIIOMetadataNode1;
    }
    if (localIIOMetadataNode4 != null) {
      previousSibling = localIIOMetadataNode1;
    }
    parent = this;
    previousSibling = localIIOMetadataNode3;
    nextSibling = localIIOMetadataNode4;
    if (firstChild == localIIOMetadataNode2) {
      firstChild = localIIOMetadataNode1;
    }
    if (lastChild == localIIOMetadataNode2) {
      lastChild = localIIOMetadataNode1;
    }
    parent = null;
    previousSibling = null;
    nextSibling = null;
    return localIIOMetadataNode2;
  }
  
  public Node removeChild(Node paramNode)
  {
    if (paramNode == null) {
      throw new IllegalArgumentException("oldChild == null!");
    }
    checkNode(paramNode);
    IIOMetadataNode localIIOMetadataNode1 = (IIOMetadataNode)paramNode;
    IIOMetadataNode localIIOMetadataNode2 = previousSibling;
    IIOMetadataNode localIIOMetadataNode3 = nextSibling;
    if (localIIOMetadataNode2 != null) {
      nextSibling = localIIOMetadataNode3;
    }
    if (localIIOMetadataNode3 != null) {
      previousSibling = localIIOMetadataNode2;
    }
    if (firstChild == localIIOMetadataNode1) {
      firstChild = localIIOMetadataNode3;
    }
    if (lastChild == localIIOMetadataNode1) {
      lastChild = localIIOMetadataNode2;
    }
    parent = null;
    previousSibling = null;
    nextSibling = null;
    numChildren -= 1;
    return localIIOMetadataNode1;
  }
  
  public Node appendChild(Node paramNode)
  {
    if (paramNode == null) {
      throw new IllegalArgumentException("newChild == null!");
    }
    checkNode(paramNode);
    return insertBefore(paramNode, null);
  }
  
  public boolean hasChildNodes()
  {
    return numChildren > 0;
  }
  
  public Node cloneNode(boolean paramBoolean)
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode(nodeName);
    localIIOMetadataNode1.setUserObject(getUserObject());
    if (paramBoolean) {
      for (IIOMetadataNode localIIOMetadataNode2 = firstChild; localIIOMetadataNode2 != null; localIIOMetadataNode2 = nextSibling) {
        localIIOMetadataNode1.appendChild(localIIOMetadataNode2.cloneNode(true));
      }
    }
    return localIIOMetadataNode1;
  }
  
  public void normalize() {}
  
  public boolean isSupported(String paramString1, String paramString2)
  {
    return false;
  }
  
  public String getNamespaceURI()
    throws DOMException
  {
    return null;
  }
  
  public String getPrefix()
  {
    return null;
  }
  
  public void setPrefix(String paramString) {}
  
  public String getLocalName()
  {
    return nodeName;
  }
  
  public String getTagName()
  {
    return nodeName;
  }
  
  public String getAttribute(String paramString)
  {
    Attr localAttr = getAttributeNode(paramString);
    if (localAttr == null) {
      return "";
    }
    return localAttr.getValue();
  }
  
  public String getAttributeNS(String paramString1, String paramString2)
  {
    return getAttribute(paramString2);
  }
  
  public void setAttribute(String paramString1, String paramString2)
  {
    int i = 1;
    char[] arrayOfChar = paramString1.toCharArray();
    for (int j = 0; j < arrayOfChar.length; j++) {
      if (arrayOfChar[j] >= 65534)
      {
        i = 0;
        break;
      }
    }
    if (i == 0) {
      throw new IIODOMException((short)5, "Attribute name is illegal!");
    }
    removeAttribute(paramString1, false);
    attributes.add(new IIOAttr(this, paramString1, paramString2));
  }
  
  public void setAttributeNS(String paramString1, String paramString2, String paramString3)
  {
    setAttribute(paramString2, paramString3);
  }
  
  public void removeAttribute(String paramString)
  {
    removeAttribute(paramString, true);
  }
  
  private void removeAttribute(String paramString, boolean paramBoolean)
  {
    int i = attributes.size();
    for (int j = 0; j < i; j++)
    {
      IIOAttr localIIOAttr = (IIOAttr)attributes.get(j);
      if (paramString.equals(localIIOAttr.getName()))
      {
        localIIOAttr.setOwnerElement(null);
        attributes.remove(j);
        return;
      }
    }
    if (paramBoolean) {
      throw new IIODOMException((short)8, "No such attribute!");
    }
  }
  
  public void removeAttributeNS(String paramString1, String paramString2)
  {
    removeAttribute(paramString2);
  }
  
  public Attr getAttributeNode(String paramString)
  {
    Node localNode = getAttributes().getNamedItem(paramString);
    return (Attr)localNode;
  }
  
  public Attr getAttributeNodeNS(String paramString1, String paramString2)
  {
    return getAttributeNode(paramString2);
  }
  
  public Attr setAttributeNode(Attr paramAttr)
    throws DOMException
  {
    Element localElement = paramAttr.getOwnerElement();
    if (localElement != null)
    {
      if (localElement == this) {
        return null;
      }
      throw new DOMException((short)10, "Attribute is already in use");
    }
    IIOAttr localIIOAttr;
    if ((paramAttr instanceof IIOAttr))
    {
      localIIOAttr = (IIOAttr)paramAttr;
      localIIOAttr.setOwnerElement(this);
    }
    else
    {
      localIIOAttr = new IIOAttr(this, paramAttr.getName(), paramAttr.getValue());
    }
    Attr localAttr = getAttributeNode(localIIOAttr.getName());
    if (localAttr != null) {
      removeAttributeNode(localAttr);
    }
    attributes.add(localIIOAttr);
    return localAttr;
  }
  
  public Attr setAttributeNodeNS(Attr paramAttr)
  {
    return setAttributeNode(paramAttr);
  }
  
  public Attr removeAttributeNode(Attr paramAttr)
  {
    removeAttribute(paramAttr.getName());
    return paramAttr;
  }
  
  public NodeList getElementsByTagName(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    getElementsByTagName(paramString, localArrayList);
    return new IIONodeList(localArrayList);
  }
  
  private void getElementsByTagName(String paramString, List paramList)
  {
    if (nodeName.equals(paramString)) {
      paramList.add(this);
    }
    for (Node localNode = getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      ((IIOMetadataNode)localNode).getElementsByTagName(paramString, paramList);
    }
  }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2)
  {
    return getElementsByTagName(paramString2);
  }
  
  public boolean hasAttributes()
  {
    return attributes.size() > 0;
  }
  
  public boolean hasAttribute(String paramString)
  {
    return getAttributeNode(paramString) != null;
  }
  
  public boolean hasAttributeNS(String paramString1, String paramString2)
  {
    return hasAttribute(paramString2);
  }
  
  public int getLength()
  {
    return numChildren;
  }
  
  public Node item(int paramInt)
  {
    if (paramInt < 0) {
      return null;
    }
    for (Node localNode = getFirstChild(); (localNode != null) && (paramInt-- > 0); localNode = localNode.getNextSibling()) {}
    return localNode;
  }
  
  public Object getUserObject()
  {
    return userObject;
  }
  
  public void setUserObject(Object paramObject)
  {
    userObject = paramObject;
  }
  
  public void setIdAttribute(String paramString, boolean paramBoolean)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public void setIdAttributeNode(Attr paramAttr, boolean paramBoolean)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public TypeInfo getSchemaTypeInfo()
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public Object getUserData(String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public Object getFeature(String paramString1, String paramString2)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public boolean isSameNode(Node paramNode)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public boolean isEqualNode(Node paramNode)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public String lookupNamespaceURI(String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public boolean isDefaultNamespace(String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public String lookupPrefix(String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public String getTextContent()
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public void setTextContent(String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public short compareDocumentPosition(Node paramNode)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public String getBaseURI()
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\metadata\IIOMetadataNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */