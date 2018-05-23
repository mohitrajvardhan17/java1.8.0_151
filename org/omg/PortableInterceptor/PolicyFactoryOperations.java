package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;

public abstract interface PolicyFactoryOperations
{
  public abstract Policy create_policy(int paramInt, Any paramAny)
    throws PolicyError;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\PolicyFactoryOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */