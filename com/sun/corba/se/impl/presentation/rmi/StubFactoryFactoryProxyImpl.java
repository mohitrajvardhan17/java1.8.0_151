package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class StubFactoryFactoryProxyImpl
  extends StubFactoryFactoryDynamicBase
{
  public StubFactoryFactoryProxyImpl() {}
  
  public PresentationManager.StubFactory makeDynamicStubFactory(PresentationManager paramPresentationManager, final PresentationManager.ClassData paramClassData, final ClassLoader paramClassLoader)
  {
    (PresentationManager.StubFactory)AccessController.doPrivileged(new PrivilegedAction()
    {
      public StubFactoryProxyImpl run()
      {
        return new StubFactoryProxyImpl(paramClassData, paramClassLoader);
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryFactoryProxyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */