package com.sun.org.apache.xerces.internal.dom;

public class DeferredEntityImpl
  extends EntityImpl
  implements DeferredNode
{
  static final long serialVersionUID = 4760180431078941638L;
  protected transient int fNodeIndex;
  
  DeferredEntityImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
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
    publicId = localDeferredDocumentImpl.getNodeValue(fNodeIndex);
    systemId = localDeferredDocumentImpl.getNodeURI(fNodeIndex);
    int i = localDeferredDocumentImpl.getNodeExtra(fNodeIndex);
    localDeferredDocumentImpl.getNodeType(i);
    notationName = localDeferredDocumentImpl.getNodeName(i);
    version = localDeferredDocumentImpl.getNodeValue(i);
    encoding = localDeferredDocumentImpl.getNodeURI(i);
    int j = localDeferredDocumentImpl.getNodeExtra(i);
    baseURI = localDeferredDocumentImpl.getNodeName(j);
    inputEncoding = localDeferredDocumentImpl.getNodeValue(j);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredEntityImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */