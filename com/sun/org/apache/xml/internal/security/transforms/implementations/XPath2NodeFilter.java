package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XPath2NodeFilter
  implements NodeFilter
{
  boolean hasUnionFilter;
  boolean hasSubtractFilter;
  boolean hasIntersectFilter;
  Set<Node> unionNodes;
  Set<Node> subtractNodes;
  Set<Node> intersectNodes;
  int inSubtract = -1;
  int inIntersect = -1;
  int inUnion = -1;
  
  XPath2NodeFilter(List<NodeList> paramList1, List<NodeList> paramList2, List<NodeList> paramList3)
  {
    hasUnionFilter = (!paramList1.isEmpty());
    unionNodes = convertNodeListToSet(paramList1);
    hasSubtractFilter = (!paramList2.isEmpty());
    subtractNodes = convertNodeListToSet(paramList2);
    hasIntersectFilter = (!paramList3.isEmpty());
    intersectNodes = convertNodeListToSet(paramList3);
  }
  
  public int isNodeInclude(Node paramNode)
  {
    int i = 1;
    if ((hasSubtractFilter) && (rooted(paramNode, subtractNodes))) {
      i = -1;
    } else if ((hasIntersectFilter) && (!rooted(paramNode, intersectNodes))) {
      i = 0;
    }
    if (i == 1) {
      return 1;
    }
    if (hasUnionFilter)
    {
      if (rooted(paramNode, unionNodes)) {
        return 1;
      }
      i = 0;
    }
    return i;
  }
  
  public int isNodeIncludeDO(Node paramNode, int paramInt)
  {
    int i = 1;
    if (hasSubtractFilter)
    {
      if ((inSubtract == -1) || (paramInt <= inSubtract)) {
        if (inList(paramNode, subtractNodes)) {
          inSubtract = paramInt;
        } else {
          inSubtract = -1;
        }
      }
      if (inSubtract != -1) {
        i = -1;
      }
    }
    if ((i != -1) && (hasIntersectFilter) && ((inIntersect == -1) || (paramInt <= inIntersect))) {
      if (!inList(paramNode, intersectNodes))
      {
        inIntersect = -1;
        i = 0;
      }
      else
      {
        inIntersect = paramInt;
      }
    }
    if (paramInt <= inUnion) {
      inUnion = -1;
    }
    if (i == 1) {
      return 1;
    }
    if (hasUnionFilter)
    {
      if ((inUnion == -1) && (inList(paramNode, unionNodes))) {
        inUnion = paramInt;
      }
      if (inUnion != -1) {
        return 1;
      }
      i = 0;
    }
    return i;
  }
  
  static boolean rooted(Node paramNode, Set<Node> paramSet)
  {
    if (paramSet.isEmpty()) {
      return false;
    }
    if (paramSet.contains(paramNode)) {
      return true;
    }
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      Node localNode = (Node)localIterator.next();
      if (XMLUtils.isDescendantOrSelf(localNode, paramNode)) {
        return true;
      }
    }
    return false;
  }
  
  static boolean inList(Node paramNode, Set<Node> paramSet)
  {
    return paramSet.contains(paramNode);
  }
  
  private static Set<Node> convertNodeListToSet(List<NodeList> paramList)
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      NodeList localNodeList = (NodeList)localIterator.next();
      int i = localNodeList.getLength();
      for (int j = 0; j < i; j++)
      {
        Node localNode = localNodeList.item(j);
        localHashSet.add(localNode);
      }
    }
    return localHashSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\XPath2NodeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */