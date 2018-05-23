package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMAny
  extends CMNode
{
  private int fType;
  private String fURI;
  private int fPosition = -1;
  
  public CMAny(int paramInt1, String paramString, int paramInt2)
  {
    super(paramInt1);
    fType = paramInt1;
    fURI = paramString;
    fPosition = paramInt2;
  }
  
  final int getType()
  {
    return fType;
  }
  
  final String getURI()
  {
    return fURI;
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
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    localStringBuffer.append("##any:uri=");
    localStringBuffer.append(fURI);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\CMAny.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */