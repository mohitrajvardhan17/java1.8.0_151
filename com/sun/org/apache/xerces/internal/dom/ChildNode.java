package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;

public abstract class ChildNode
  extends NodeImpl
{
  static final long serialVersionUID = -6112455738802414002L;
  transient StringBuffer fBufferStr = null;
  protected ChildNode previousSibling;
  protected ChildNode nextSibling;
  
  protected ChildNode(CoreDocumentImpl paramCoreDocumentImpl)
  {
    super(paramCoreDocumentImpl);
  }
  
  public ChildNode() {}
  
  public Node cloneNode(boolean paramBoolean)
  {
    ChildNode localChildNode = (ChildNode)super.cloneNode(paramBoolean);
    previousSibling = null;
    nextSibling = null;
    localChildNode.isFirstChild(false);
    return localChildNode;
  }
  
  public Node getParentNode()
  {
    return isOwned() ? ownerNode : null;
  }
  
  final NodeImpl parentNode()
  {
    return isOwned() ? ownerNode : null;
  }
  
  public Node getNextSibling()
  {
    return nextSibling;
  }
  
  public Node getPreviousSibling()
  {
    return isFirstChild() ? null : previousSibling;
  }
  
  final ChildNode previousSibling()
  {
    return isFirstChild() ? null : previousSibling;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\ChildNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */