package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import java.io.SerializablePermission;
import org.omg.CORBA.Object;

public abstract class StubFactoryDynamicBase
  extends StubFactoryBase
{
  protected final ClassLoader loader;
  
  private static Void checkPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new SerializablePermission("enableSubclassImplementation"));
    }
    return null;
  }
  
  private StubFactoryDynamicBase(Void paramVoid, PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader)
  {
    super(paramClassData);
    if (paramClassLoader == null)
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = ClassLoader.getSystemClassLoader();
      }
      loader = localClassLoader;
    }
    else
    {
      loader = paramClassLoader;
    }
  }
  
  public StubFactoryDynamicBase(PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader)
  {
    this(checkPermission(), paramClassData, paramClassLoader);
  }
  
  public abstract Object makeStub();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryDynamicBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */