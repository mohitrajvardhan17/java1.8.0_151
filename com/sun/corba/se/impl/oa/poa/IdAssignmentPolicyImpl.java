package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdAssignmentPolicyValue;

final class IdAssignmentPolicyImpl
  extends LocalObject
  implements IdAssignmentPolicy
{
  private IdAssignmentPolicyValue value;
  
  public IdAssignmentPolicyImpl(IdAssignmentPolicyValue paramIdAssignmentPolicyValue)
  {
    value = paramIdAssignmentPolicyValue;
  }
  
  public IdAssignmentPolicyValue value()
  {
    return value;
  }
  
  public int policy_type()
  {
    return 19;
  }
  
  public Policy copy()
  {
    return new IdAssignmentPolicyImpl(value);
  }
  
  public void destroy()
  {
    value = null;
  }
  
  public String toString()
  {
    return "IdAssignmentPolicy[" + (value.value() == 0 ? "USER_ID" : "SYSTEM_ID]");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\IdAssignmentPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */