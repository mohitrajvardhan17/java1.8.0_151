package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DeepNodeListImpl
  implements NodeList
{
  protected NodeImpl rootNode;
  protected String tagName;
  protected int changes = 0;
  protected Vector nodes;
  protected String nsName;
  protected boolean enableNS = false;
  
  public DeepNodeListImpl(NodeImpl paramNodeImpl, String paramString)
  {
    rootNode = paramNodeImpl;
    tagName = paramString;
    nodes = new Vector();
  }
  
  public DeepNodeListImpl(NodeImpl paramNodeImpl, String paramString1, String paramString2)
  {
    this(paramNodeImpl, paramString2);
    nsName = ((paramString1 != null) && (!paramString1.equals("")) ? paramString1 : null);
    enableNS = true;
  }
  
  public int getLength()
  {
    item(Integer.MAX_VALUE);
    return nodes.size();
  }
  
  public Node item(int paramInt)
  {
    if (rootNode.changes() != changes)
    {
      nodes = new Vector();
      changes = rootNode.changes();
    }
    if (paramInt < nodes.size()) {
      return (Node)nodes.elementAt(paramInt);
    }
    Object localObject;
    if (nodes.size() == 0) {
      localObject = rootNode;
    } else {
      localObject = (NodeImpl)nodes.lastElement();
    }
    while ((localObject != null) && (paramInt >= nodes.size()))
    {
      localObject = nextMatchingElementAfter((Node)localObject);
      if (localObject != null) {
        nodes.addElement(localObject);
      }
    }
    return (Node)localObject;
  }
  
  protected Node nextMatchingElementAfter(Node paramNode)
  {
    while (paramNode != null)
    {
      if (paramNode.hasChildNodes())
      {
        paramNode = paramNode.getFirstChild();
      }
      else
      {
        Node localNode;
        if ((paramNode != rootNode) && (null != (localNode = paramNode.getNextSibling())))
        {
          paramNode = localNode;
        }
        else
        {
          localNode = null;
          while (paramNode != rootNode)
          {
            localNode = paramNode.getNextSibling();
            if (localNode != null) {
              break;
            }
            paramNode = paramNode.getParentNode();
          }
          paramNode = localNode;
        }
      }
      if ((paramNode != rootNode) && (paramNode != null) && (paramNode.getNodeType() == 1)) {
        if (!enableNS)
        {
          if ((tagName.equals("*")) || (((ElementImpl)paramNode).getTagName().equals(tagName))) {
            return paramNode;
          }
        }
        else
        {
          ElementImpl localElementImpl;
          if (tagName.equals("*"))
          {
            if ((nsName != null) && (nsName.equals("*"))) {
              return paramNode;
            }
            localElementImpl = (ElementImpl)paramNode;
            if (((nsName == null) && (localElementImpl.getNamespaceURI() == null)) || ((nsName != null) && (nsName.equals(localElementImpl.getNamespaceURI())))) {
              return paramNode;
            }
          }
          else
          {
            localElementImpl = (ElementImpl)paramNode;
            if ((localElementImpl.getLocalName() != null) && (localElementImpl.getLocalName().equals(tagName)))
            {
              if ((nsName != null) && (nsName.equals("*"))) {
                return paramNode;
              }
              if (((nsName == null) && (localElementImpl.getNamespaceURI() == null)) || ((nsName != null) && (nsName.equals(localElementImpl.getNamespaceURI())))) {
                return paramNode;
              }
            }
          }
        }
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeepNodeListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */