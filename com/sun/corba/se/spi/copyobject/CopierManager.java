package com.sun.corba.se.spi.copyobject;

public abstract interface CopierManager
{
  public abstract void setDefaultId(int paramInt);
  
  public abstract int getDefaultId();
  
  public abstract ObjectCopierFactory getObjectCopierFactory(int paramInt);
  
  public abstract ObjectCopierFactory getDefaultObjectCopierFactory();
  
  public abstract void registerObjectCopierFactory(ObjectCopierFactory paramObjectCopierFactory, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\copyobject\CopierManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */