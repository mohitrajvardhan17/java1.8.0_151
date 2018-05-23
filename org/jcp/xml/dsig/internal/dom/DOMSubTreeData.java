package org.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import javax.xml.crypto.NodeSetData;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DOMSubTreeData
  implements NodeSetData
{
  private boolean excludeComments;
  private Node root;
  
  public DOMSubTreeData(Node paramNode, boolean paramBoolean)
  {
    root = paramNode;
    excludeComments = paramBoolean;
  }
  
  public Iterator iterator()
  {
    return new DelayedNodeIterator(root, excludeComments);
  }
  
  public Node getRoot()
  {
    return root;
  }
  
  public boolean excludeComments()
  {
    return excludeComments;
  }
  
  static class DelayedNodeIterator
    implements Iterator<Node>
  {
    private Node root;
    private List<Node> nodeSet;
    private ListIterator<Node> li;
    private boolean withComments;
    
    DelayedNodeIterator(Node paramNode, boolean paramBoolean)
    {
      root = paramNode;
      withComments = (!paramBoolean);
    }
    
    public boolean hasNext()
    {
      if (nodeSet == null)
      {
        nodeSet = dereferenceSameDocumentURI(root);
        li = nodeSet.listIterator();
      }
      return li.hasNext();
    }
    
    public Node next()
    {
      if (nodeSet == null)
      {
        nodeSet = dereferenceSameDocumentURI(root);
        li = nodeSet.listIterator();
      }
      if (li.hasNext()) {
        return (Node)li.next();
      }
      throw new NoSuchElementException();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
    
    private List<Node> dereferenceSameDocumentURI(Node paramNode)
    {
      ArrayList localArrayList = new ArrayList();
      if (paramNode != null) {
        nodeSetMinusCommentNodes(paramNode, localArrayList, null);
      }
      return localArrayList;
    }
    
    private void nodeSetMinusCommentNodes(Node paramNode1, List<Node> paramList, Node paramNode2)
    {
      Object localObject;
      Node localNode;
      switch (paramNode1.getNodeType())
      {
      case 1: 
        NamedNodeMap localNamedNodeMap = paramNode1.getAttributes();
        if (localNamedNodeMap != null)
        {
          int i = 0;
          int j = localNamedNodeMap.getLength();
          while (i < j)
          {
            paramList.add(localNamedNodeMap.item(i));
            i++;
          }
        }
        paramList.add(paramNode1);
        localObject = null;
        for (localNode = paramNode1.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
        {
          nodeSetMinusCommentNodes(localNode, paramList, (Node)localObject);
          localObject = localNode;
        }
        break;
      case 9: 
        localObject = null;
        for (localNode = paramNode1.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
        {
          nodeSetMinusCommentNodes(localNode, paramList, (Node)localObject);
          localObject = localNode;
        }
        break;
      case 3: 
      case 4: 
        if ((paramNode2 != null) && ((paramNode2.getNodeType() == 3) || (paramNode2.getNodeType() == 4))) {
          return;
        }
        paramList.add(paramNode1);
        break;
      case 7: 
        paramList.add(paramNode1);
        break;
      case 8: 
        if (withComments) {
          paramList.add(paramNode1);
        }
        break;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMSubTreeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */