package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;

public class XSCMLeaf
  extends CMNode
{
  private Object fLeaf = null;
  private int fParticleId = -1;
  private int fPosition = -1;
  
  public XSCMLeaf(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
  {
    super(paramInt1);
    fLeaf = paramObject;
    fParticleId = paramInt2;
    fPosition = paramInt3;
  }
  
  final Object getLeaf()
  {
    return fLeaf;
  }
  
  final int getParticleId()
  {
    return fParticleId;
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
    StringBuffer localStringBuffer = new StringBuffer(fLeaf.toString());
    if (fPosition >= 0) {
      localStringBuffer.append(" (Pos:" + Integer.toString(fPosition) + ")");
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSCMLeaf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */