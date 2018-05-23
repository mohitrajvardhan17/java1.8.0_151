package javax.imageio.metadata;

import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class IIONodeList
  implements NodeList
{
  List nodes;
  
  public IIONodeList(List paramList)
  {
    nodes = paramList;
  }
  
  public int getLength()
  {
    return nodes.size();
  }
  
  public Node item(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > nodes.size())) {
      return null;
    }
    return (Node)nodes.get(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\metadata\IIONodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */