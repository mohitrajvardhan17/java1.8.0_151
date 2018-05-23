package org.w3c.dom.traversal;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public abstract interface NodeIterator
{
  public abstract Node getRoot();
  
  public abstract int getWhatToShow();
  
  public abstract NodeFilter getFilter();
  
  public abstract boolean getExpandEntityReferences();
  
  public abstract Node nextNode()
    throws DOMException;
  
  public abstract Node previousNode()
    throws DOMException;
  
  public abstract void detach();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\traversal\NodeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */