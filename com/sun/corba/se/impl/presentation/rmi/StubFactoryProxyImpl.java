package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import java.lang.reflect.Proxy;
import org.omg.CORBA.Object;

public class StubFactoryProxyImpl
  extends StubFactoryDynamicBase
{
  public StubFactoryProxyImpl(PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader)
  {
    super(paramClassData, paramClassLoader);
  }
  
  public Object makeStub()
  {
    InvocationHandlerFactory localInvocationHandlerFactory = classData.getInvocationHandlerFactory();
    LinkedInvocationHandler localLinkedInvocationHandler = (LinkedInvocationHandler)localInvocationHandlerFactory.getInvocationHandler();
    Class[] arrayOfClass = localInvocationHandlerFactory.getProxyInterfaces();
    DynamicStub localDynamicStub = (DynamicStub)Proxy.newProxyInstance(loader, arrayOfClass, localLinkedInvocationHandler);
    localLinkedInvocationHandler.setProxy((Proxy)localDynamicStub);
    return localDynamicStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryProxyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */