package javax.imageio.spi;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class PartiallyOrderedSet
  extends AbstractSet
{
  private Map poNodes = new HashMap();
  private Set nodes = poNodes.keySet();
  
  public PartiallyOrderedSet() {}
  
  public int size()
  {
    return nodes.size();
  }
  
  public boolean contains(Object paramObject)
  {
    return nodes.contains(paramObject);
  }
  
  public Iterator iterator()
  {
    return new PartialOrderIterator(poNodes.values().iterator());
  }
  
  public boolean add(Object paramObject)
  {
    if (nodes.contains(paramObject)) {
      return false;
    }
    DigraphNode localDigraphNode = new DigraphNode(paramObject);
    poNodes.put(paramObject, localDigraphNode);
    return true;
  }
  
  public boolean remove(Object paramObject)
  {
    DigraphNode localDigraphNode = (DigraphNode)poNodes.get(paramObject);
    if (localDigraphNode == null) {
      return false;
    }
    poNodes.remove(paramObject);
    localDigraphNode.dispose();
    return true;
  }
  
  public void clear()
  {
    poNodes.clear();
  }
  
  public boolean setOrdering(Object paramObject1, Object paramObject2)
  {
    DigraphNode localDigraphNode1 = (DigraphNode)poNodes.get(paramObject1);
    DigraphNode localDigraphNode2 = (DigraphNode)poNodes.get(paramObject2);
    localDigraphNode2.removeEdge(localDigraphNode1);
    return localDigraphNode1.addEdge(localDigraphNode2);
  }
  
  public boolean unsetOrdering(Object paramObject1, Object paramObject2)
  {
    DigraphNode localDigraphNode1 = (DigraphNode)poNodes.get(paramObject1);
    DigraphNode localDigraphNode2 = (DigraphNode)poNodes.get(paramObject2);
    return (localDigraphNode1.removeEdge(localDigraphNode2)) || (localDigraphNode2.removeEdge(localDigraphNode1));
  }
  
  public boolean hasOrdering(Object paramObject1, Object paramObject2)
  {
    DigraphNode localDigraphNode1 = (DigraphNode)poNodes.get(paramObject1);
    DigraphNode localDigraphNode2 = (DigraphNode)poNodes.get(paramObject2);
    return localDigraphNode1.hasEdge(localDigraphNode2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\PartiallyOrderedSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */