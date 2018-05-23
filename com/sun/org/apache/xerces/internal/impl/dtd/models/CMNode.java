package com.sun.org.apache.xerces.internal.impl.dtd.models;

public abstract class CMNode
{
  private int fType;
  private CMStateSet fFirstPos = null;
  private CMStateSet fFollowPos = null;
  private CMStateSet fLastPos = null;
  private int fMaxStates = -1;
  private Object fUserData = null;
  
  public CMNode(int paramInt)
  {
    fType = paramInt;
  }
  
  public abstract boolean isNullable();
  
  public final int type()
  {
    return fType;
  }
  
  public final CMStateSet firstPos()
  {
    if (fFirstPos == null)
    {
      fFirstPos = new CMStateSet(fMaxStates);
      calcFirstPos(fFirstPos);
    }
    return fFirstPos;
  }
  
  public final CMStateSet lastPos()
  {
    if (fLastPos == null)
    {
      fLastPos = new CMStateSet(fMaxStates);
      calcLastPos(fLastPos);
    }
    return fLastPos;
  }
  
  final void setFollowPos(CMStateSet paramCMStateSet)
  {
    fFollowPos = paramCMStateSet;
  }
  
  public final void setMaxStates(int paramInt)
  {
    fMaxStates = paramInt;
  }
  
  public void setUserData(Object paramObject)
  {
    fUserData = paramObject;
  }
  
  public Object getUserData()
  {
    return fUserData;
  }
  
  protected abstract void calcFirstPos(CMStateSet paramCMStateSet);
  
  protected abstract void calcLastPos(CMStateSet paramCMStateSet);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\CMNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */