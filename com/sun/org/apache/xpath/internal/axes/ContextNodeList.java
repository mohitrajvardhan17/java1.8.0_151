package com.sun.org.apache.xpath.internal.axes;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public abstract interface ContextNodeList
{
  public abstract Node getCurrentNode();
  
  public abstract int getCurrentPos();
  
  public abstract void reset();
  
  public abstract void setShouldCacheNodes(boolean paramBoolean);
  
  public abstract void runTo(int paramInt);
  
  public abstract void setCurrentPos(int paramInt);
  
  public abstract int size();
  
  public abstract boolean isFresh();
  
  public abstract NodeIterator cloneWithReset()
    throws CloneNotSupportedException;
  
  public abstract Object clone()
    throws CloneNotSupportedException;
  
  public abstract int getLast();
  
  public abstract void setLast(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\ContextNodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */