package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactoryFactory;

public abstract class StubFactoryFactoryBase
  implements PresentationManager.StubFactoryFactory
{
  public StubFactoryFactoryBase() {}
  
  public String getStubName(String paramString)
  {
    return Utility.stubName(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryFactoryBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */