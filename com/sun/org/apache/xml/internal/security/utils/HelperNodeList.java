package com.sun.org.apache.xml.internal.security.utils;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HelperNodeList
  implements NodeList
{
  List<Node> nodes = new ArrayList();
  boolean allNodesMustHaveSameParent = false;
  
  public HelperNodeList()
  {
    this(false);
  }
  
  public HelperNodeList(boolean paramBoolean)
  {
    allNodesMustHaveSameParent = paramBoolean;
  }
  
  public Node item(int paramInt)
  {
    return (Node)nodes.get(paramInt);
  }
  
  public int getLength()
  {
    return nodes.size();
  }
  
  public void appendChild(Node paramNode)
    throws IllegalArgumentException
  {
    if ((allNodesMustHaveSameParent) && (getLength() > 0) && (item(0).getParentNode() != paramNode.getParentNode())) {
      throw new IllegalArgumentException("Nodes have not the same Parent");
    }
    nodes.add(paramNode);
  }
  
  public Document getOwnerDocument()
  {
    if (getLength() == 0) {
      return null;
    }
    return XMLUtils.getOwnerDocument(item(0));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\HelperNodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */