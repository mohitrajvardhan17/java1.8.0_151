package javax.imageio.metadata;

import java.util.Iterator;
import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class IIONamedNodeMap
  implements NamedNodeMap
{
  List nodes;
  
  public IIONamedNodeMap(List paramList)
  {
    nodes = paramList;
  }
  
  public int getLength()
  {
    return nodes.size();
  }
  
  public Node getNamedItem(String paramString)
  {
    Iterator localIterator = nodes.iterator();
    while (localIterator.hasNext())
    {
      Node localNode = (Node)localIterator.next();
      if (paramString.equals(localNode.getNodeName())) {
        return localNode;
      }
    }
    return null;
  }
  
  public Node item(int paramInt)
  {
    Node localNode = (Node)nodes.get(paramInt);
    return localNode;
  }
  
  public Node removeNamedItem(String paramString)
  {
    throw new DOMException((short)7, "This NamedNodeMap is read-only!");
  }
  
  public Node setNamedItem(Node paramNode)
  {
    throw new DOMException((short)7, "This NamedNodeMap is read-only!");
  }
  
  public Node getNamedItemNS(String paramString1, String paramString2)
  {
    return getNamedItem(paramString2);
  }
  
  public Node setNamedItemNS(Node paramNode)
  {
    return setNamedItem(paramNode);
  }
  
  public Node removeNamedItemNS(String paramString1, String paramString2)
  {
    return removeNamedItem(paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\metadata\IIONamedNodeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */