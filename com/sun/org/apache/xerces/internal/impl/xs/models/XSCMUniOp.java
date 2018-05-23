package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;

public class XSCMUniOp
  extends CMNode
{
  private CMNode fChild;
  
  public XSCMUniOp(int paramInt, CMNode paramCMNode)
  {
    super(paramInt);
    if ((type() != 5) && (type() != 4) && (type() != 6)) {
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
    if (type() == 6) {
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
  
  public void setUserData(Object paramObject)
  {
    super.setUserData(paramObject);
    fChild.setUserData(paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSCMUniOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */