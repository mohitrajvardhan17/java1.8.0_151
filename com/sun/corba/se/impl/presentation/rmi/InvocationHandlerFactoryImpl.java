package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandler;
import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl;
import com.sun.corba.se.spi.orbutil.proxy.DelegateInvocationHandlerImpl;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class InvocationHandlerFactoryImpl
  implements InvocationHandlerFactory
{
  private final PresentationManager.ClassData classData;
  private final PresentationManager pm;
  private Class[] proxyInterfaces;
  
  public InvocationHandlerFactoryImpl(PresentationManager paramPresentationManager, PresentationManager.ClassData paramClassData)
  {
    classData = paramClassData;
    pm = paramPresentationManager;
    Class[] arrayOfClass = paramClassData.getIDLNameTranslator().getInterfaces();
    proxyInterfaces = new Class[arrayOfClass.length + 1];
    for (int i = 0; i < arrayOfClass.length; i++) {
      proxyInterfaces[i] = arrayOfClass[i];
    }
    proxyInterfaces[arrayOfClass.length] = DynamicStub.class;
  }
  
  public InvocationHandler getInvocationHandler()
  {
    DynamicStubImpl localDynamicStubImpl = new DynamicStubImpl(classData.getTypeIds());
    return getInvocationHandler(localDynamicStubImpl);
  }
  
  InvocationHandler getInvocationHandler(DynamicStub paramDynamicStub)
  {
    final InvocationHandler localInvocationHandler = DelegateInvocationHandlerImpl.create(paramDynamicStub);
    StubInvocationHandlerImpl localStubInvocationHandlerImpl = new StubInvocationHandlerImpl(pm, classData, paramDynamicStub);
    final CustomCompositeInvocationHandlerImpl localCustomCompositeInvocationHandlerImpl = new CustomCompositeInvocationHandlerImpl(paramDynamicStub);
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        localCustomCompositeInvocationHandlerImpl.addInvocationHandler(DynamicStub.class, localInvocationHandler);
        localCustomCompositeInvocationHandlerImpl.addInvocationHandler(org.omg.CORBA.Object.class, localInvocationHandler);
        localCustomCompositeInvocationHandlerImpl.addInvocationHandler(Object.class, localInvocationHandler);
        return null;
      }
    });
    localCustomCompositeInvocationHandlerImpl.setDefaultHandler(localStubInvocationHandlerImpl);
    return localCustomCompositeInvocationHandlerImpl;
  }
  
  public Class[] getProxyInterfaces()
  {
    return proxyInterfaces;
  }
  
  private class CustomCompositeInvocationHandlerImpl
    extends CompositeInvocationHandlerImpl
    implements LinkedInvocationHandler, Serializable
  {
    private transient DynamicStub stub;
    
    public void setProxy(Proxy paramProxy)
    {
      ((DynamicStubImpl)stub).setSelf((DynamicStub)paramProxy);
    }
    
    public Proxy getProxy()
    {
      return (Proxy)((DynamicStubImpl)stub).getSelf();
    }
    
    public CustomCompositeInvocationHandlerImpl(DynamicStub paramDynamicStub)
    {
      stub = paramDynamicStub;
    }
    
    public Object writeReplace()
      throws ObjectStreamException
    {
      return stub;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\InvocationHandlerFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */