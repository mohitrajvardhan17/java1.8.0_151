package com.sun.org.apache.xerces.internal.dom;

public class DeferredNotationImpl
  extends NotationImpl
  implements DeferredNode
{
  static final long serialVersionUID = 5705337172887990848L;
  protected transient int fNodeIndex;
  
  DeferredNotationImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
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
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    name = localDeferredDocumentImpl.getNodeName(fNodeIndex);
    localDeferredDocumentImpl.getNodeType(fNodeIndex);
    publicId = localDeferredDocumentImpl.getNodeValue(fNodeIndex);
    systemId = localDeferredDocumentImpl.getNodeURI(fNodeIndex);
    int i = localDeferredDocumentImpl.getNodeExtra(fNodeIndex);
    localDeferredDocumentImpl.getNodeType(i);
    baseURI = localDeferredDocumentImpl.getNodeName(i);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredNotationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */