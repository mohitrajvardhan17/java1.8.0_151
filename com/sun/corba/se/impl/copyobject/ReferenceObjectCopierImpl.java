package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.spi.copyobject.ObjectCopier;

public class ReferenceObjectCopierImpl
  implements ObjectCopier
{
  public ReferenceObjectCopierImpl() {}
  
  public Object copy(Object paramObject)
  {
    return paramObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\copyobject\ReferenceObjectCopierImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */