package com.sun.org.apache.xerces.internal.dom;

public class DeferredEntityReferenceImpl
  extends EntityReferenceImpl
  implements DeferredNode
{
  static final long serialVersionUID = 390319091370032223L;
  protected transient int fNodeIndex;
  
  DeferredEntityReferenceImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
  {
    super(paramDeferredDocumentImpl, null);
    fNodeIndex = paramInt;
    needsSyncData(true);
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
    baseURI = localDeferredDocumentImpl.getNodeValue(fNodeIndex);
  }
  
  protected void synchronizeChildren()
  {
    needsSyncChildren(false);
    isReadOnly(false);
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    localDeferredDocumentImpl.synchronizeChildren(this, fNodeIndex);
    setReadOnly(true, true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredEntityReferenceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */