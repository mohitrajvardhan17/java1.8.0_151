package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;

final class ThreadPolicyImpl
  extends LocalObject
  implements ThreadPolicy
{
  private ThreadPolicyValue value;
  
  public ThreadPolicyImpl(ThreadPolicyValue paramThreadPolicyValue)
  {
    value = paramThreadPolicyValue;
  }
  
  public ThreadPolicyValue value()
  {
    return value;
  }
  
  public int policy_type()
  {
    return 16;
  }
  
  public Policy copy()
  {
    return new ThreadPolicyImpl(value);
  }
  
  public void destroy()
  {
    value = null;
  }
  
  public String toString()
  {
    return "ThreadPolicy[" + (value.value() == 1 ? "SINGLE_THREAD_MODEL" : "ORB_CTRL_MODEL]");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\ThreadPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */