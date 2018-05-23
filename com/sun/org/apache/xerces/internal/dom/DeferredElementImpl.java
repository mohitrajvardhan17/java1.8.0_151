package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;

public class DeferredElementImpl
  extends ElementImpl
  implements DeferredNode
{
  static final long serialVersionUID = -7670981133940934842L;
  protected transient int fNodeIndex;
  
  DeferredElementImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
  {
    super(paramDeferredDocumentImpl, null);
    fNodeIndex = paramInt;
    needsSyncChildren(true);
  }
  
  public final int getNodeIndex()
  {
    return fNodeIndex;
  }
  
  protected final void synchronizeData()
  {
    needsSyncData(false);
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument;
    boolean bool = mutationEvents;
    mutationEvents = false;
    name = localDeferredDocumentImpl.getNodeName(fNodeIndex);
    setupDefaultAttributes();
    int i = localDeferredDocumentImpl.getNodeExtra(fNodeIndex);
    if (i != -1)
    {
      NamedNodeMap localNamedNodeMap = getAttributes();
      do
      {
        NodeImpl localNodeImpl = (NodeImpl)localDeferredDocumentImpl.getNodeObject(i);
        localNamedNodeMap.setNamedItem(localNodeImpl);
        i = localDeferredDocumentImpl.getPrevSibling(i);
      } while (i != -1);
    }
    mutationEvents = bool;
  }
  
  protected final void synchronizeChildren()
  {
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    localDeferredDocumentImpl.synchronizeChildren(this, fNodeIndex);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredElementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */