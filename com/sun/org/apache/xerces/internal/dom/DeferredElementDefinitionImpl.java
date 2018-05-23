package com.sun.org.apache.xerces.internal.dom;

public class DeferredElementDefinitionImpl
  extends ElementDefinitionImpl
  implements DeferredNode
{
  static final long serialVersionUID = 6703238199538041591L;
  protected transient int fNodeIndex;
  
  DeferredElementDefinitionImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
  {
    super(paramDeferredDocumentImpl, null);
    fNodeIndex = paramInt;
    needsSyncData(true);
    needsSyncChildren(true);
  }
  
  public int getNodeIndex()
  {
    return fNodeIndex;
  }
  
  protected void synchronizeData()
  {
    needsSyncData(false);
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument;
    name = localDeferredDocumentImpl.getNodeName(fNodeIndex);
  }
  
  protected void synchronizeChildren()
  {
    boolean bool = ownerDocument.getMutationEvents();
    ownerDocument.setMutationEvents(false);
    needsSyncChildren(false);
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument;
    attributes = new NamedNodeMapImpl(localDeferredDocumentImpl);
    for (int i = localDeferredDocumentImpl.getLastChild(fNodeIndex); i != -1; i = localDeferredDocumentImpl.getPrevSibling(i))
    {
      DeferredNode localDeferredNode = localDeferredDocumentImpl.getNodeObject(i);
      attributes.setNamedItem(localDeferredNode);
    }
    localDeferredDocumentImpl.setMutationEvents(bool);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredElementDefinitionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */