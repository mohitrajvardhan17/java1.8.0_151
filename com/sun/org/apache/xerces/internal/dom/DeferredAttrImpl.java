package com.sun.org.apache.xerces.internal.dom;

public final class DeferredAttrImpl
  extends AttrImpl
  implements DeferredNode
{
  static final long serialVersionUID = 6903232312469148636L;
  protected transient int fNodeIndex;
  
  DeferredAttrImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
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
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    name = localDeferredDocumentImpl.getNodeName(fNodeIndex);
    int i = localDeferredDocumentImpl.getNodeExtra(fNodeIndex);
    isSpecified((i & 0x20) != 0);
    isIdAttribute((i & 0x200) != 0);
    int j = localDeferredDocumentImpl.getLastChild(fNodeIndex);
    type = localDeferredDocumentImpl.getTypeInfo(j);
  }
  
  protected void synchronizeChildren()
  {
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    localDeferredDocumentImpl.synchronizeChildren(this, fNodeIndex);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredAttrImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */