package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xml.internal.utils.DOMHelper;
import com.sun.org.apache.xpath.internal.NodeSet;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExsltSets
  extends ExsltBase
{
  public ExsltSets() {}
  
  public static NodeList leading(NodeList paramNodeList1, NodeList paramNodeList2)
  {
    if (paramNodeList2.getLength() == 0) {
      return paramNodeList1;
    }
    NodeSet localNodeSet1 = new NodeSet(paramNodeList1);
    NodeSet localNodeSet2 = new NodeSet();
    Node localNode1 = paramNodeList2.item(0);
    if (!localNodeSet1.contains(localNode1)) {
      return localNodeSet2;
    }
    for (int i = 0; i < paramNodeList1.getLength(); i++)
    {
      Node localNode2 = paramNodeList1.item(i);
      if ((DOMHelper.isNodeAfter(localNode2, localNode1)) && (!DOMHelper.isNodeTheSame(localNode2, localNode1))) {
        localNodeSet2.addElement(localNode2);
      }
    }
    return localNodeSet2;
  }
  
  public static NodeList trailing(NodeList paramNodeList1, NodeList paramNodeList2)
  {
    if (paramNodeList2.getLength() == 0) {
      return paramNodeList1;
    }
    NodeSet localNodeSet1 = new NodeSet(paramNodeList1);
    NodeSet localNodeSet2 = new NodeSet();
    Node localNode1 = paramNodeList2.item(0);
    if (!localNodeSet1.contains(localNode1)) {
      return localNodeSet2;
    }
    for (int i = 0; i < paramNodeList1.getLength(); i++)
    {
      Node localNode2 = paramNodeList1.item(i);
      if ((DOMHelper.isNodeAfter(localNode1, localNode2)) && (!DOMHelper.isNodeTheSame(localNode1, localNode2))) {
        localNodeSet2.addElement(localNode2);
      }
    }
    return localNodeSet2;
  }
  
  public static NodeList intersection(NodeList paramNodeList1, NodeList paramNodeList2)
  {
    NodeSet localNodeSet1 = new NodeSet(paramNodeList1);
    NodeSet localNodeSet2 = new NodeSet(paramNodeList2);
    NodeSet localNodeSet3 = new NodeSet();
    localNodeSet3.setShouldCacheNodes(true);
    for (int i = 0; i < localNodeSet1.getLength(); i++)
    {
      Node localNode = localNodeSet1.elementAt(i);
      if (localNodeSet2.contains(localNode)) {
        localNodeSet3.addElement(localNode);
      }
    }
    return localNodeSet3;
  }
  
  public static NodeList difference(NodeList paramNodeList1, NodeList paramNodeList2)
  {
    NodeSet localNodeSet1 = new NodeSet(paramNodeList1);
    NodeSet localNodeSet2 = new NodeSet(paramNodeList2);
    NodeSet localNodeSet3 = new NodeSet();
    localNodeSet3.setShouldCacheNodes(true);
    for (int i = 0; i < localNodeSet1.getLength(); i++)
    {
      Node localNode = localNodeSet1.elementAt(i);
      if (!localNodeSet2.contains(localNode)) {
        localNodeSet3.addElement(localNode);
      }
    }
    return localNodeSet3;
  }
  
  public static NodeList distinct(NodeList paramNodeList)
  {
    NodeSet localNodeSet = new NodeSet();
    localNodeSet.setShouldCacheNodes(true);
    HashMap localHashMap = new HashMap();
    for (int i = 0; i < paramNodeList.getLength(); i++)
    {
      Node localNode = paramNodeList.item(i);
      String str = toString(localNode);
      if (str == null)
      {
        localNodeSet.addElement(localNode);
      }
      else if (!localHashMap.containsKey(str))
      {
        localHashMap.put(str, localNode);
        localNodeSet.addElement(localNode);
      }
    }
    return localNodeSet;
  }
  
  public static boolean hasSameNode(NodeList paramNodeList1, NodeList paramNodeList2)
  {
    NodeSet localNodeSet1 = new NodeSet(paramNodeList1);
    NodeSet localNodeSet2 = new NodeSet(paramNodeList2);
    for (int i = 0; i < localNodeSet1.getLength(); i++) {
      if (localNodeSet2.contains(localNodeSet1.elementAt(i))) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltSets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */