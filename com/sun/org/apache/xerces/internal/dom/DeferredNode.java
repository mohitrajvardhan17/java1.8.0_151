package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;

public abstract interface DeferredNode
  extends Node
{
  public static final short TYPE_NODE = 20;
  
  public abstract int getNodeIndex();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */