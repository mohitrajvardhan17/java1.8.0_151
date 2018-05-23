package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import java.lang.reflect.Method;
import java.util.Map;
import javax.rmi.CORBA.Tie;

public abstract interface PresentationManager
{
  public abstract ClassData getClassData(Class paramClass);
  
  public abstract DynamicMethodMarshaller getDynamicMethodMarshaller(Method paramMethod);
  
  public abstract StubFactoryFactory getStubFactoryFactory(boolean paramBoolean);
  
  public abstract void setStubFactoryFactory(boolean paramBoolean, StubFactoryFactory paramStubFactoryFactory);
  
  public abstract Tie getTie();
  
  public abstract boolean useDynamicStubs();
  
  public static abstract interface ClassData
  {
    public abstract Class getMyClass();
    
    public abstract IDLNameTranslator getIDLNameTranslator();
    
    public abstract String[] getTypeIds();
    
    public abstract InvocationHandlerFactory getInvocationHandlerFactory();
    
    public abstract Map getDictionary();
  }
  
  public static abstract interface StubFactory
  {
    public abstract org.omg.CORBA.Object makeStub();
    
    public abstract String[] getTypeIds();
  }
  
  public static abstract interface StubFactoryFactory
  {
    public abstract String getStubName(String paramString);
    
    public abstract PresentationManager.StubFactory createStubFactory(String paramString1, boolean paramBoolean, String paramString2, Class paramClass, ClassLoader paramClassLoader);
    
    public abstract Tie getTie(Class paramClass);
    
    public abstract boolean createsDynamicStubs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\presentation\rmi\PresentationManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */