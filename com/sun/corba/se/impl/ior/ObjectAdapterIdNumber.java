package com.sun.corba.se.impl.ior;

public class ObjectAdapterIdNumber
  extends ObjectAdapterIdArray
{
  private int poaid;
  
  public ObjectAdapterIdNumber(int paramInt)
  {
    super("OldRootPOA", Integer.toString(paramInt));
    poaid = paramInt;
  }
  
  public int getOldPOAId()
  {
    return poaid;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectAdapterIdNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */