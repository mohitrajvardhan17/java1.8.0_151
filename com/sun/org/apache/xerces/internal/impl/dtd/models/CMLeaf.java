package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

public class CMLeaf
  extends CMNode
{
  private QName fElement = new QName();
  private int fPosition = -1;
  
  public CMLeaf(QName paramQName, int paramInt)
  {
    super(0);
    fElement.setValues(paramQName);
    fPosition = paramInt;
  }
  
  public CMLeaf(QName paramQName)
  {
    super(0);
    fElement.setValues(paramQName);
  }
  
  final QName getElement()
  {
    return fElement;
  }
  
  final int getPosition()
  {
    return fPosition;
  }
  
  final void setPosition(int paramInt)
  {
    fPosition = paramInt;
  }
  
  public boolean isNullable()
  {
    return fPosition == -1;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(fElement.toString());
    localStringBuffer.append(" (");
    localStringBuffer.append(fElement.uri);
    localStringBuffer.append(',');
    localStringBuffer.append(fElement.localpart);
    localStringBuffer.append(')');
    if (fPosition >= 0) {
      localStringBuffer.append(" (Pos:" + new Integer(fPosition).toString() + ")");
    }
    return localStringBuffer.toString();
  }
  
  protected void calcFirstPos(CMStateSet paramCMStateSet)
  {
    if (fPosition == -1) {
      paramCMStateSet.zeroBits();
    } else {
      paramCMStateSet.setBit(fPosition);
    }
  }
  
  protected void calcLastPos(CMStateSet paramCMStateSet)
  {
    if (fPosition == -1) {
      paramCMStateSet.zeroBits();
    } else {
      paramCMStateSet.setBit(fPosition);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\CMLeaf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */