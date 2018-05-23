package org.w3c.dom.traversal;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public abstract interface DocumentTraversal
{
  public abstract NodeIterator createNodeIterator(Node paramNode, int paramInt, NodeFilter paramNodeFilter, boolean paramBoolean)
    throws DOMException;
  
  public abstract TreeWalker createTreeWalker(Node paramNode, int paramInt, NodeFilter paramNodeFilter, boolean paramBoolean)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\traversal\DocumentTraversal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */