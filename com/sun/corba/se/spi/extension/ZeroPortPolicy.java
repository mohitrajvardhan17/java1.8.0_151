package com.sun.corba.se.spi.extension;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

public class ZeroPortPolicy
  extends LocalObject
  implements Policy
{
  private static ZeroPortPolicy policy = new ZeroPortPolicy(true);
  private boolean flag = true;
  
  private ZeroPortPolicy(boolean paramBoolean)
  {
    flag = paramBoolean;
  }
  
  public String toString()
  {
    return "ZeroPortPolicy[" + flag + "]";
  }
  
  public boolean forceZeroPort()
  {
    return flag;
  }
  
  public static synchronized ZeroPortPolicy getPolicy()
  {
    return policy;
  }
  
  public int policy_type()
  {
    return 1398079489;
  }
  
  public Policy copy()
  {
    return this;
  }
  
  public void destroy() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\extension\ZeroPortPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */