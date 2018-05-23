package com.sun.org.apache.xerces.internal.impl.xs.models;

public final class XSCMRepeatingLeaf
  extends XSCMLeaf
{
  private final int fMinOccurs;
  private final int fMaxOccurs;
  
  public XSCMRepeatingLeaf(int paramInt1, Object paramObject, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramObject, paramInt4, paramInt5);
    fMinOccurs = paramInt2;
    fMaxOccurs = paramInt3;
  }
  
  final int getMinOccurs()
  {
    return fMinOccurs;
  }
  
  final int getMaxOccurs()
  {
    return fMaxOccurs;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSCMRepeatingLeaf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */