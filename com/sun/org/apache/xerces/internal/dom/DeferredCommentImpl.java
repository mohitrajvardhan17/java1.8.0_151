package com.sun.org.apache.xerces.internal.dom;

public class DeferredCommentImpl
  extends CommentImpl
  implements DeferredNode
{
  static final long serialVersionUID = 6498796371083589338L;
  protected transient int fNodeIndex;
  
  DeferredCommentImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
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
    data = localDeferredDocumentImpl.getNodeValueString(fNodeIndex);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredCommentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */