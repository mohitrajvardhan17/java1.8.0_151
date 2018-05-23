package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;

final class ImplicitActivationPolicyImpl
  extends LocalObject
  implements ImplicitActivationPolicy
{
  private ImplicitActivationPolicyValue value;
  
  public ImplicitActivationPolicyImpl(ImplicitActivationPolicyValue paramImplicitActivationPolicyValue)
  {
    value = paramImplicitActivationPolicyValue;
  }
  
  public ImplicitActivationPolicyValue value()
  {
    return value;
  }
  
  public int policy_type()
  {
    return 20;
  }
  
  public Policy copy()
  {
    return new ImplicitActivationPolicyImpl(value);
  }
  
  public void destroy()
  {
    value = null;
  }
  
  public String toString()
  {
    return "ImplicitActivationPolicy[" + (value.value() == 0 ? "IMPLICIT_ACTIVATION" : "NO_IMPLICIT_ACTIVATION]");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\ImplicitActivationPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */