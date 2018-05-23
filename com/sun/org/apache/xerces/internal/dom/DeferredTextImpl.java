package com.sun.org.apache.xerces.internal.dom;

public class DeferredTextImpl
  extends TextImpl
  implements DeferredNode
{
  static final long serialVersionUID = 2310613872100393425L;
  protected transient int fNodeIndex;
  
  DeferredTextImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
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
    isIgnorableWhitespace(localDeferredDocumentImpl.getNodeExtra(fNodeIndex) == 1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredTextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */