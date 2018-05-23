package javax.imageio.spi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

class PartialOrderIterator
  implements Iterator
{
  LinkedList zeroList = new LinkedList();
  Map inDegrees = new HashMap();
  
  public PartialOrderIterator(Iterator paramIterator)
  {
    while (paramIterator.hasNext())
    {
      DigraphNode localDigraphNode = (DigraphNode)paramIterator.next();
      int i = localDigraphNode.getInDegree();
      inDegrees.put(localDigraphNode, new Integer(i));
      if (i == 0) {
        zeroList.add(localDigraphNode);
      }
    }
  }
  
  public boolean hasNext()
  {
    return !zeroList.isEmpty();
  }
  
  public Object next()
  {
    DigraphNode localDigraphNode1 = (DigraphNode)zeroList.removeFirst();
    Iterator localIterator = localDigraphNode1.getOutNodes();
    while (localIterator.hasNext())
    {
      DigraphNode localDigraphNode2 = (DigraphNode)localIterator.next();
      int i = ((Integer)inDegrees.get(localDigraphNode2)).intValue() - 1;
      inDegrees.put(localDigraphNode2, new Integer(i));
      if (i == 0) {
        zeroList.add(localDigraphNode2);
      }
    }
    return localDigraphNode1.getData();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\PartialOrderIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */