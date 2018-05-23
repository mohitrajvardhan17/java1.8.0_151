package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactoryFactory;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.IDLEntity;

public abstract class StubFactoryFactoryDynamicBase
  extends StubFactoryFactoryBase
{
  protected final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");
  
  public StubFactoryFactoryDynamicBase() {}
  
  public PresentationManager.StubFactory createStubFactory(String paramString1, boolean paramBoolean, String paramString2, Class paramClass, ClassLoader paramClassLoader)
  {
    Class localClass = null;
    try
    {
      localClass = Util.loadClass(paramString1, paramString2, paramClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw wrapper.classNotFound3(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException, paramString1);
    }
    PresentationManager localPresentationManager = ORB.getPresentationManager();
    if ((IDLEntity.class.isAssignableFrom(localClass)) && (!Remote.class.isAssignableFrom(localClass)))
    {
      localObject = localPresentationManager.getStubFactoryFactory(false);
      PresentationManager.StubFactory localStubFactory = ((PresentationManager.StubFactoryFactory)localObject).createStubFactory(paramString1, true, paramString2, paramClass, paramClassLoader);
      return localStubFactory;
    }
    Object localObject = localPresentationManager.getClassData(localClass);
    return makeDynamicStubFactory(localPresentationManager, (PresentationManager.ClassData)localObject, paramClassLoader);
  }
  
  public abstract PresentationManager.StubFactory makeDynamicStubFactory(PresentationManager paramPresentationManager, PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader);
  
  public Tie getTie(Class paramClass)
  {
    PresentationManager localPresentationManager = ORB.getPresentationManager();
    return new ReflectiveTie(localPresentationManager, wrapper);
  }
  
  public boolean createsDynamicStubs()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryFactoryDynamicBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */