package com.sun.corba.se.spi.extension;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

public class CopyObjectPolicy
  extends LocalObject
  implements Policy
{
  private final int value;
  
  public CopyObjectPolicy(int paramInt)
  {
    value = paramInt;
  }
  
  public int getValue()
  {
    return value;
  }
  
  public int policy_type()
  {
    return 1398079490;
  }
  
  public Policy copy()
  {
    return this;
  }
  
  public void destroy() {}
  
  public String toString()
  {
    return "CopyObjectPolicy[" + value + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\extension\CopyObjectPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */