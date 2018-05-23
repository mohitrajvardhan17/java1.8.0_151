package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.util.PackagePrefixChecker;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.CompletionStatus;

public class StubFactoryFactoryStaticImpl
  extends StubFactoryFactoryBase
{
  private ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");
  
  public StubFactoryFactoryStaticImpl() {}
  
  public PresentationManager.StubFactory createStubFactory(String paramString1, boolean paramBoolean, String paramString2, Class paramClass, ClassLoader paramClassLoader)
  {
    String str1 = null;
    if (paramBoolean) {
      str1 = Utility.idlStubName(paramString1);
    } else {
      str1 = Utility.stubNameForCompiler(paramString1);
    }
    ClassLoader localClassLoader1 = paramClass == null ? paramClassLoader : paramClass.getClassLoader();
    String str2 = str1;
    String str3 = str1;
    if (PackagePrefixChecker.hasOffendingPrefix(str1)) {
      str2 = PackagePrefixChecker.packagePrefix() + str1;
    } else {
      str3 = PackagePrefixChecker.packagePrefix() + str1;
    }
    Class localClass = null;
    try
    {
      localClass = Util.loadClass(str2, paramString2, localClassLoader1);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      wrapper.classNotFound1(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException1, str2);
      try
      {
        localClass = Util.loadClass(str3, paramString2, localClassLoader1);
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        throw wrapper.classNotFound2(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException2, str3);
      }
    }
    if ((localClass == null) || ((paramClass != null) && (!paramClass.isAssignableFrom(localClass)))) {
      try
      {
        ClassLoader localClassLoader2 = Thread.currentThread().getContextClassLoader();
        if (localClassLoader2 == null) {
          localClassLoader2 = ClassLoader.getSystemClassLoader();
        }
        localClass = localClassLoader2.loadClass(paramString1);
      }
      catch (Exception localException)
      {
        IllegalStateException localIllegalStateException = new IllegalStateException("Could not load class " + str1);
        localIllegalStateException.initCause(localException);
        throw localIllegalStateException;
      }
    }
    return new StubFactoryStaticImpl(localClass);
  }
  
  public Tie getTie(Class paramClass)
  {
    Class localClass = null;
    String str = Utility.tieName(paramClass.getName());
    try
    {
      localClass = Utility.loadClassForClass(str, Util.getCodebase(paramClass), null, paramClass, paramClass.getClassLoader());
      return (Tie)localClass.newInstance();
    }
    catch (Exception localException1)
    {
      localClass = Utility.loadClassForClass(PackagePrefixChecker.packagePrefix() + str, Util.getCodebase(paramClass), null, paramClass, paramClass.getClassLoader());
      return (Tie)localClass.newInstance();
    }
    catch (Exception localException2) {}
    return null;
  }
  
  public boolean createsDynamicStubs()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryFactoryStaticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */