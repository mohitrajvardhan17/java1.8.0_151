package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.LifespanPolicyValue;

final class LifespanPolicyImpl
  extends LocalObject
  implements LifespanPolicy
{
  private LifespanPolicyValue value;
  
  public LifespanPolicyImpl(LifespanPolicyValue paramLifespanPolicyValue)
  {
    value = paramLifespanPolicyValue;
  }
  
  public LifespanPolicyValue value()
  {
    return value;
  }
  
  public int policy_type()
  {
    return 17;
  }
  
  public Policy copy()
  {
    return new LifespanPolicyImpl(value);
  }
  
  public void destroy()
  {
    value = null;
  }
  
  public String toString()
  {
    return "LifespanPolicy[" + (value.value() == 0 ? "TRANSIENT" : "PERSISTENT]");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\LifespanPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */