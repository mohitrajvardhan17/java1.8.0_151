package com.sun.org.apache.xerces.internal.dom;

public class DeferredProcessingInstructionImpl
  extends ProcessingInstructionImpl
  implements DeferredNode
{
  static final long serialVersionUID = -4643577954293565388L;
  protected transient int fNodeIndex;
  
  DeferredProcessingInstructionImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
  {
    super(paramDeferredDocumentImpl, null, null);
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
    target = localDeferredDocumentImpl.getNodeName(fNodeIndex);
    data = localDeferredDocumentImpl.getNodeValueString(fNodeIndex);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredProcessingInstructionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */