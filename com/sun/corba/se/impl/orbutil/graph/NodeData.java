package com.sun.corba.se.impl.orbutil.graph;

public class NodeData
{
  private boolean visited;
  private boolean root;
  
  public NodeData()
  {
    clear();
  }
  
  public void clear()
  {
    visited = false;
    root = true;
  }
  
  boolean isVisited()
  {
    return visited;
  }
  
  void visited()
  {
    visited = true;
  }
  
  boolean isRoot()
  {
    return root;
  }
  
  void notRoot()
  {
    root = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\graph\NodeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */