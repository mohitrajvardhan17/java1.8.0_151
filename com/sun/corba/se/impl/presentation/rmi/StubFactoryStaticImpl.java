package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.Object;

public class StubFactoryStaticImpl
  extends StubFactoryBase
{
  private Class stubClass;
  
  public StubFactoryStaticImpl(Class paramClass)
  {
    super(null);
    stubClass = paramClass;
  }
  
  public Object makeStub()
  {
    Object localObject = null;
    try
    {
      localObject = (Object)stubClass.newInstance();
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new RuntimeException(localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    return localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryStaticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */