package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.IdUniquenessPolicyValue;

final class IdUniquenessPolicyImpl
  extends LocalObject
  implements IdUniquenessPolicy
{
  private IdUniquenessPolicyValue value;
  
  public IdUniquenessPolicyImpl(IdUniquenessPolicyValue paramIdUniquenessPolicyValue)
  {
    value = paramIdUniquenessPolicyValue;
  }
  
  public IdUniquenessPolicyValue value()
  {
    return value;
  }
  
  public int policy_type()
  {
    return 18;
  }
  
  public Policy copy()
  {
    return new IdUniquenessPolicyImpl(value);
  }
  
  public void destroy()
  {
    value = null;
  }
  
  public String toString()
  {
    return "IdUniquenessPolicy[" + (value.value() == 0 ? "UNIQUE_ID" : "MULTIPLE_ID]");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\IdUniquenessPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */