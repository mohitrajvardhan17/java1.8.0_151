package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.spi.copyobject.ObjectCopier;
import com.sun.corba.se.spi.copyobject.ReflectiveCopyException;

public class FallbackObjectCopierImpl
  implements ObjectCopier
{
  private ObjectCopier first;
  private ObjectCopier second;
  
  public FallbackObjectCopierImpl(ObjectCopier paramObjectCopier1, ObjectCopier paramObjectCopier2)
  {
    first = paramObjectCopier1;
    second = paramObjectCopier2;
  }
  
  public Object copy(Object paramObject)
    throws ReflectiveCopyException
  {
    try
    {
      return first.copy(paramObject);
    }
    catch (ReflectiveCopyException localReflectiveCopyException) {}
    return second.copy(paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\copyobject\FallbackObjectCopierImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */