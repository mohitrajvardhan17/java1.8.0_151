package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ServantRetentionPolicyValue;

final class ServantRetentionPolicyImpl
  extends LocalObject
  implements ServantRetentionPolicy
{
  private ServantRetentionPolicyValue value;
  
  public ServantRetentionPolicyImpl(ServantRetentionPolicyValue paramServantRetentionPolicyValue)
  {
    value = paramServantRetentionPolicyValue;
  }
  
  public ServantRetentionPolicyValue value()
  {
    return value;
  }
  
  public int policy_type()
  {
    return 21;
  }
  
  public Policy copy()
  {
    return new ServantRetentionPolicyImpl(value);
  }
  
  public void destroy()
  {
    value = null;
  }
  
  public String toString()
  {
    return "ServantRetentionPolicy[" + (value.value() == 0 ? "RETAIN" : "NON_RETAIN]");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\ServantRetentionPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */