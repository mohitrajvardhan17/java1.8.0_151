package com.sun.corba.se.spi.copyobject;

public abstract interface ObjectCopier
{
  public abstract Object copy(Object paramObject)
    throws ReflectiveCopyException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\copyobject\ObjectCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */