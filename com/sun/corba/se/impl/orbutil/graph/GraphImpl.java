package com.sun.corba.se.impl.orbutil.graph;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GraphImpl
  extends AbstractSet
  implements Graph
{
  private Map nodeToData = new HashMap();
  
  public GraphImpl() {}
  
  public GraphImpl(Collection paramCollection)
  {
    this();
    addAll(paramCollection);
  }
  
  public boolean add(Object paramObject)
  {
    if (!(paramObject instanceof Node)) {
      throw new IllegalArgumentException("Graphs must contain only Node instances");
    }
    Node localNode = (Node)paramObject;
    boolean bool = nodeToData.keySet().contains(paramObject);
    if (!bool)
    {
      NodeData localNodeData = new NodeData();
      nodeToData.put(localNode, localNodeData);
    }
    return !bool;
  }
  
  public Iterator iterator()
  {
    return nodeToData.keySet().iterator();
  }
  
  public int size()
  {
    return nodeToData.keySet().size();
  }
  
  public NodeData getNodeData(Node paramNode)
  {
    return (NodeData)nodeToData.get(paramNode);
  }
  
  private void clearNodeData()
  {
    Iterator localIterator = nodeToData.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      NodeData localNodeData = (NodeData)localEntry.getValue();
      localNodeData.clear();
    }
  }
  
  void visitAll(NodeVisitor paramNodeVisitor)
  {
    int i = 0;
    do
    {
      i = 1;
      Map.Entry[] arrayOfEntry = (Map.Entry[])nodeToData.entrySet().toArray(new Map.Entry[0]);
      for (int j = 0; j < arrayOfEntry.length; j++)
      {
        Map.Entry localEntry = arrayOfEntry[j];
        Node localNode = (Node)localEntry.getKey();
        NodeData localNodeData = (NodeData)localEntry.getValue();
        if (!localNodeData.isVisited())
        {
          localNodeData.visited();
          i = 0;
          paramNodeVisitor.visit(this, localNode, localNodeData);
        }
      }
    } while (i == 0);
  }
  
  private void markNonRoots()
  {
    visitAll(new NodeVisitor()
    {
      public void visit(Graph paramAnonymousGraph, Node paramAnonymousNode, NodeData paramAnonymousNodeData)
      {
        Iterator localIterator = paramAnonymousNode.getChildren().iterator();
        while (localIterator.hasNext())
        {
          Node localNode = (Node)localIterator.next();
          paramAnonymousGraph.add(localNode);
          NodeData localNodeData = paramAnonymousGraph.getNodeData(localNode);
          localNodeData.notRoot();
        }
      }
    });
  }
  
  private Set collectRootSet()
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = nodeToData.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Node localNode = (Node)localEntry.getKey();
      NodeData localNodeData = (NodeData)localEntry.getValue();
      if (localNodeData.isRoot()) {
        localHashSet.add(localNode);
      }
    }
    return localHashSet;
  }
  
  public Set getRoots()
  {
    clearNodeData();
    markNonRoots();
    return collectRootSet();
  }
  
  static abstract interface NodeVisitor
  {
    public abstract void visit(Graph paramGraph, Node paramNode, NodeData paramNodeData);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\graph\GraphImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */