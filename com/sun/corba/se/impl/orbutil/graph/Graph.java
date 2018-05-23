package com.sun.corba.se.impl.orbutil.graph;

import java.util.Set;

public abstract interface Graph
  extends Set
{
  public abstract NodeData getNodeData(Node paramNode);
  
  public abstract Set getRoots();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\graph\Graph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */