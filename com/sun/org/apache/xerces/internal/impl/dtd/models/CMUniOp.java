package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMUniOp
  extends CMNode
{
  private CMNode fChild;
  
  public CMUniOp(int paramInt, CMNode paramCMNode)
  {
    super(paramInt);
    if ((type() != 1) && (type() != 2) && (type() != 3)) {
      throw new RuntimeException("ImplementationMessages.VAL_UST");
    }
    fChild = paramCMNode;
  }
  
  final CMNode getChild()
  {
    return fChild;
  }
  
  public boolean isNullable()
  {
    if (type() == 3) {
      return fChild.isNullable();
    }
    return true;
  }
  
  protected void calcFirstPos(CMStateSet paramCMStateSet)
  {
    paramCMStateSet.setTo(fChild.firstPos());
  }
  
  protected void calcLastPos(CMStateSet paramCMStateSet)
  {
    paramCMStateSet.setTo(fChild.lastPos());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\CMUniOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */