package javax.imageio.spi;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class DigraphNode
  implements Cloneable, Serializable
{
  protected Object data;
  protected Set outNodes = new HashSet();
  protected int inDegree = 0;
  private Set inNodes = new HashSet();
  
  public DigraphNode(Object paramObject)
  {
    data = paramObject;
  }
  
  public Object getData()
  {
    return data;
  }
  
  public Iterator getOutNodes()
  {
    return outNodes.iterator();
  }
  
  public boolean addEdge(DigraphNode paramDigraphNode)
  {
    if (outNodes.contains(paramDigraphNode)) {
      return false;
    }
    outNodes.add(paramDigraphNode);
    inNodes.add(this);
    paramDigraphNode.incrementInDegree();
    return true;
  }
  
  public boolean hasEdge(DigraphNode paramDigraphNode)
  {
    return outNodes.contains(paramDigraphNode);
  }
  
  public boolean removeEdge(DigraphNode paramDigraphNode)
  {
    if (!outNodes.contains(paramDigraphNode)) {
      return false;
    }
    outNodes.remove(paramDigraphNode);
    inNodes.remove(this);
    paramDigraphNode.decrementInDegree();
    return true;
  }
  
  public void dispose()
  {
    Object[] arrayOfObject1 = inNodes.toArray();
    for (int i = 0; i < arrayOfObject1.length; i++)
    {
      DigraphNode localDigraphNode1 = (DigraphNode)arrayOfObject1[i];
      localDigraphNode1.removeEdge(this);
    }
    Object[] arrayOfObject2 = outNodes.toArray();
    for (int j = 0; j < arrayOfObject2.length; j++)
    {
      DigraphNode localDigraphNode2 = (DigraphNode)arrayOfObject2[j];
      removeEdge(localDigraphNode2);
    }
  }
  
  public int getInDegree()
  {
    return inDegree;
  }
  
  private void incrementInDegree()
  {
    inDegree += 1;
  }
  
  private void decrementInDegree()
  {
    inDegree -= 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\DigraphNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */