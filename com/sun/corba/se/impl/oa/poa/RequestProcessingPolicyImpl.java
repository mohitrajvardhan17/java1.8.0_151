package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;

public class RequestProcessingPolicyImpl
  extends LocalObject
  implements RequestProcessingPolicy
{
  private RequestProcessingPolicyValue value;
  
  public RequestProcessingPolicyImpl(RequestProcessingPolicyValue paramRequestProcessingPolicyValue)
  {
    value = paramRequestProcessingPolicyValue;
  }
  
  public RequestProcessingPolicyValue value()
  {
    return value;
  }
  
  public int policy_type()
  {
    return 22;
  }
  
  public Policy copy()
  {
    return new RequestProcessingPolicyImpl(value);
  }
  
  public void destroy()
  {
    value = null;
  }
  
  public String toString()
  {
    String str = null;
    switch (value.value())
    {
    case 0: 
      str = "USE_ACTIVE_OBJECT_MAP_ONLY";
      break;
    case 1: 
      str = "USE_DEFAULT_SERVANT";
      break;
    case 2: 
      str = "USE_SERVANT_MANAGER";
    }
    return "RequestProcessingPolicy[" + str + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\RequestProcessingPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */