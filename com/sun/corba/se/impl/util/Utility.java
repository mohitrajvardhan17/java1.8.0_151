package com.sun.corba.se.impl.util;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactoryFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.PortableServer.Servant;
import org.omg.stub.java.rmi._Remote_Stub;

public final class Utility
{
  public static final String STUB_PREFIX = "_";
  public static final String RMI_STUB_SUFFIX = "_Stub";
  public static final String DYNAMIC_STUB_SUFFIX = "_DynamicStub";
  public static final String IDL_STUB_SUFFIX = "Stub";
  public static final String TIE_SUFIX = "_Tie";
  private static IdentityHashtable tieCache = new IdentityHashtable();
  private static IdentityHashtable tieToStubCache = new IdentityHashtable();
  private static IdentityHashtable stubToTieCache = new IdentityHashtable();
  private static Object CACHE_MISS = new Object();
  private static UtilSystemException wrapper = UtilSystemException.get("util");
  private static OMGSystemException omgWrapper = OMGSystemException.get("util");
  
  public Utility() {}
  
  public static Object autoConnect(Object paramObject, org.omg.CORBA.ORB paramORB, boolean paramBoolean)
  {
    if (paramObject == null) {
      return paramObject;
    }
    if (StubAdapter.isStub(paramObject))
    {
      try
      {
        StubAdapter.getDelegate(paramObject);
      }
      catch (BAD_OPERATION localBAD_OPERATION)
      {
        try
        {
          StubAdapter.connect(paramObject, paramORB);
        }
        catch (RemoteException localRemoteException)
        {
          throw wrapper.objectNotConnected(localRemoteException, paramObject.getClass().getName());
        }
      }
      return paramObject;
    }
    if ((paramObject instanceof Remote))
    {
      Remote localRemote1 = (Remote)paramObject;
      Tie localTie = Util.getTie(localRemote1);
      if (localTie != null)
      {
        try
        {
          localTie.orb();
        }
        catch (SystemException localSystemException)
        {
          localTie.orb(paramORB);
        }
        if (paramBoolean)
        {
          Remote localRemote2 = loadStub(localTie, null, null, true);
          if (localRemote2 != null) {
            return localRemote2;
          }
          throw wrapper.couldNotLoadStub(paramObject.getClass().getName());
        }
        return StubAdapter.activateTie(localTie);
      }
      throw wrapper.objectNotExported(paramObject.getClass().getName());
    }
    return paramObject;
  }
  
  public static Tie loadTie(Remote paramRemote)
  {
    Tie localTie = null;
    Class localClass = paramRemote.getClass();
    synchronized (tieCache)
    {
      Object localObject1 = tieCache.get(paramRemote);
      if (localObject1 == null)
      {
        try
        {
          for (localTie = loadTie(localClass); (localTie == null) && ((localClass = localClass.getSuperclass()) != null) && (localClass != PortableRemoteObject.class) && (localClass != Object.class); localTie = loadTie(localClass)) {}
        }
        catch (Exception localException1)
        {
          wrapper.loadTieFailed(localException1, localClass.getName());
        }
        if (localTie == null) {
          tieCache.put(paramRemote, CACHE_MISS);
        } else {
          tieCache.put(paramRemote, localTie);
        }
      }
      else if (localObject1 != CACHE_MISS)
      {
        try
        {
          localTie = (Tie)localObject1.getClass().newInstance();
        }
        catch (Exception localException2) {}
      }
    }
    return localTie;
  }
  
  private static Tie loadTie(Class paramClass)
  {
    return com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory().getTie(paramClass);
  }
  
  public static void clearCaches()
  {
    synchronized (tieToStubCache)
    {
      tieToStubCache.clear();
    }
    synchronized (tieCache)
    {
      tieCache.clear();
    }
    synchronized (stubToTieCache)
    {
      stubToTieCache.clear();
    }
  }
  
  static Class loadClassOfType(String paramString1, String paramString2, ClassLoader paramClassLoader1, Class paramClass, ClassLoader paramClassLoader2)
    throws ClassNotFoundException
  {
    Class localClass = null;
    try
    {
      try
      {
        if (!PackagePrefixChecker.hasOffendingPrefix(PackagePrefixChecker.withoutPackagePrefix(paramString1))) {
          localClass = Util.loadClass(PackagePrefixChecker.withoutPackagePrefix(paramString1), paramString2, paramClassLoader1);
        } else {
          localClass = Util.loadClass(paramString1, paramString2, paramClassLoader1);
        }
      }
      catch (ClassNotFoundException localClassNotFoundException1)
      {
        localClass = Util.loadClass(paramString1, paramString2, paramClassLoader1);
      }
      if (paramClass == null) {
        return localClass;
      }
    }
    catch (ClassNotFoundException localClassNotFoundException2)
    {
      if (paramClass == null) {
        throw localClassNotFoundException2;
      }
    }
    if ((localClass == null) || (!paramClass.isAssignableFrom(localClass)))
    {
      if (paramClass.getClassLoader() != paramClassLoader2) {
        throw new IllegalArgumentException("expectedTypeClassLoader not class loader of expected Type.");
      }
      if (paramClassLoader2 != null)
      {
        localClass = paramClassLoader2.loadClass(paramString1);
      }
      else
      {
        ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
        if (localClassLoader == null) {
          localClassLoader = ClassLoader.getSystemClassLoader();
        }
        localClass = localClassLoader.loadClass(paramString1);
      }
    }
    return localClass;
  }
  
  public static Class loadClassForClass(String paramString1, String paramString2, ClassLoader paramClassLoader1, Class paramClass, ClassLoader paramClassLoader2)
    throws ClassNotFoundException
  {
    if (paramClass == null) {
      return Util.loadClass(paramString1, paramString2, paramClassLoader1);
    }
    Class localClass = null;
    try
    {
      localClass = Util.loadClass(paramString1, paramString2, paramClassLoader1);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (paramClass.getClassLoader() == null) {
        throw localClassNotFoundException;
      }
    }
    if ((localClass == null) || ((localClass.getClassLoader() != null) && (localClass.getClassLoader().loadClass(paramClass.getName()) != paramClass)))
    {
      if (paramClass.getClassLoader() != paramClassLoader2) {
        throw new IllegalArgumentException("relatedTypeClassLoader not class loader of relatedType.");
      }
      if (paramClassLoader2 != null) {
        localClass = paramClassLoader2.loadClass(paramString1);
      }
    }
    return localClass;
  }
  
  public static BoxedValueHelper getHelper(Class paramClass, String paramString1, String paramString2)
  {
    String str = null;
    if (paramClass != null)
    {
      str = paramClass.getName();
      if (paramString1 == null) {
        paramString1 = Util.getCodebase(paramClass);
      }
    }
    else
    {
      if (paramString2 != null) {
        str = RepositoryId.cache.getId(paramString2).getClassName();
      }
      if (str == null) {
        throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE);
      }
    }
    try
    {
      ClassLoader localClassLoader = paramClass == null ? null : paramClass.getClassLoader();
      Class localClass = loadClassForClass(str + "Helper", paramString1, localClassLoader, paramClass, localClassLoader);
      return (BoxedValueHelper)localClass.newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, localInstantiationException);
    }
    catch (ClassCastException localClassCastException)
    {
      throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, localClassCastException);
    }
  }
  
  public static ValueFactory getFactory(Class paramClass, String paramString1, org.omg.CORBA.ORB paramORB, String paramString2)
  {
    ValueFactory localValueFactory = null;
    if ((paramORB != null) && (paramString2 != null)) {
      try
      {
        localValueFactory = ((org.omg.CORBA_2_3.ORB)paramORB).lookup_value_factory(paramString2);
      }
      catch (BAD_PARAM localBAD_PARAM) {}
    }
    String str = null;
    if (paramClass != null)
    {
      str = paramClass.getName();
      if (paramString1 == null) {
        paramString1 = Util.getCodebase(paramClass);
      }
    }
    else
    {
      if (paramString2 != null) {
        str = RepositoryId.cache.getId(paramString2).getClassName();
      }
      if (str == null) {
        throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE);
      }
    }
    if ((localValueFactory != null) && ((!localValueFactory.getClass().getName().equals(str + "DefaultFactory")) || ((paramClass == null) && (paramString1 == null)))) {
      return localValueFactory;
    }
    try
    {
      ClassLoader localClassLoader = paramClass == null ? null : paramClass.getClassLoader();
      Class localClass = loadClassForClass(str + "DefaultFactory", paramString1, localClassLoader, paramClass, localClassLoader);
      return (ValueFactory)localClass.newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, localInstantiationException);
    }
    catch (ClassCastException localClassCastException)
    {
      throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, localClassCastException);
    }
  }
  
  public static Remote loadStub(Tie paramTie, PresentationManager.StubFactory paramStubFactory, String paramString, boolean paramBoolean)
  {
    StubEntry localStubEntry = null;
    synchronized (tieToStubCache)
    {
      Object localObject1 = tieToStubCache.get(paramTie);
      if (localObject1 == null)
      {
        localStubEntry = loadStubAndUpdateCache(paramTie, paramStubFactory, paramString, paramBoolean);
      }
      else if (localObject1 != CACHE_MISS)
      {
        localStubEntry = (StubEntry)localObject1;
        if ((!mostDerived) && (paramBoolean))
        {
          localStubEntry = loadStubAndUpdateCache(paramTie, null, paramString, true);
        }
        else if ((paramStubFactory != null) && (!StubAdapter.getTypeIds(stub)[0].equals(paramStubFactory.getTypeIds()[0])))
        {
          localStubEntry = loadStubAndUpdateCache(paramTie, null, paramString, true);
          if (localStubEntry == null) {
            localStubEntry = loadStubAndUpdateCache(paramTie, paramStubFactory, paramString, paramBoolean);
          }
        }
        else
        {
          try
          {
            org.omg.CORBA.portable.Delegate localDelegate1 = StubAdapter.getDelegate(stub);
          }
          catch (Exception localException1)
          {
            try
            {
              org.omg.CORBA.portable.Delegate localDelegate2 = StubAdapter.getDelegate(paramTie);
              StubAdapter.setDelegate(stub, localDelegate2);
            }
            catch (Exception localException2) {}
          }
        }
      }
    }
    if (localStubEntry != null) {
      return (Remote)stub;
    }
    return null;
  }
  
  private static StubEntry loadStubAndUpdateCache(Tie paramTie, PresentationManager.StubFactory paramStubFactory, String paramString, boolean paramBoolean)
  {
    Object localObject1 = null;
    StubEntry localStubEntry = null;
    boolean bool1 = StubAdapter.isStub(paramTie);
    Object localObject2;
    if (paramStubFactory != null)
    {
      try
      {
        localObject1 = paramStubFactory.makeStub();
      }
      catch (Throwable localThrowable)
      {
        wrapper.stubFactoryCouldNotMakeStub(localThrowable);
        if ((localThrowable instanceof ThreadDeath)) {
          throw ((ThreadDeath)localThrowable);
        }
      }
    }
    else
    {
      localObject2 = null;
      if (bool1) {
        localObject2 = StubAdapter.getTypeIds(paramTie);
      } else {
        localObject2 = ((Servant)paramTie)._all_interfaces(null, null);
      }
      if (paramString == null) {
        paramString = Util.getCodebase(paramTie.getClass());
      }
      if (localObject2.length == 0)
      {
        localObject1 = new _Remote_Stub();
      }
      else
      {
        int i = 0;
        for (;;)
        {
          if (i < localObject2.length) {
            if (localObject2[i].length() == 0) {
              localObject1 = new _Remote_Stub();
            } else {
              try
              {
                PresentationManager.StubFactoryFactory localStubFactoryFactory = com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory();
                RepositoryId localRepositoryId = RepositoryId.cache.getId(localObject2[i]);
                String str = localRepositoryId.getClassName();
                boolean bool2 = localRepositoryId.isIDLType();
                paramStubFactory = localStubFactoryFactory.createStubFactory(str, bool2, paramString, null, paramTie.getClass().getClassLoader());
                localObject1 = paramStubFactory.makeStub();
              }
              catch (Exception localException3)
              {
                wrapper.errorInMakeStubFromRepositoryId(localException3);
                if (!paramBoolean) {
                  i++;
                }
              }
            }
          }
        }
      }
    }
    if (localObject1 == null)
    {
      tieToStubCache.put(paramTie, CACHE_MISS);
    }
    else
    {
      if (bool1) {
        try
        {
          localObject2 = StubAdapter.getDelegate(paramTie);
          StubAdapter.setDelegate(localObject1, (org.omg.CORBA.portable.Delegate)localObject2);
        }
        catch (Exception localException1)
        {
          synchronized (stubToTieCache)
          {
            stubToTieCache.put(localObject1, paramTie);
          }
        }
      } else {
        try
        {
          org.omg.CORBA.portable.Delegate localDelegate = StubAdapter.getDelegate(paramTie);
          StubAdapter.setDelegate(localObject1, localDelegate);
        }
        catch (BAD_INV_ORDER localBAD_INV_ORDER)
        {
          synchronized (stubToTieCache)
          {
            stubToTieCache.put(localObject1, paramTie);
          }
        }
        catch (Exception localException2)
        {
          throw wrapper.noPoa(localException2);
        }
      }
      localStubEntry = new StubEntry((org.omg.CORBA.Object)localObject1, paramBoolean);
      tieToStubCache.put(paramTie, localStubEntry);
    }
    return localStubEntry;
  }
  
  /* Error */
  public static Tie getAndForgetTie(org.omg.CORBA.Object paramObject)
  {
    // Byte code:
    //   0: getstatic 402	com/sun/corba/se/impl/util/Utility:stubToTieCache	Lcom/sun/corba/se/impl/util/IdentityHashtable;
    //   3: dup
    //   4: astore_1
    //   5: monitorenter
    //   6: getstatic 402	com/sun/corba/se/impl/util/Utility:stubToTieCache	Lcom/sun/corba/se/impl/util/IdentityHashtable;
    //   9: aload_0
    //   10: invokevirtual 427	com/sun/corba/se/impl/util/IdentityHashtable:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   13: checkcast 227	javax/rmi/CORBA/Tie
    //   16: aload_1
    //   17: monitorexit
    //   18: areturn
    //   19: astore_2
    //   20: aload_1
    //   21: monitorexit
    //   22: aload_2
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	paramObject	org.omg.CORBA.Object
    //   4	17	1	Ljava/lang/Object;	Object
    //   19	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   6	18	19	finally
    //   19	22	19	finally
  }
  
  public static void purgeStubForTie(Tie paramTie)
  {
    StubEntry localStubEntry;
    synchronized (tieToStubCache)
    {
      localStubEntry = (StubEntry)tieToStubCache.remove(paramTie);
    }
    if (localStubEntry != null) {
      synchronized (stubToTieCache)
      {
        stubToTieCache.remove(stub);
      }
    }
  }
  
  public static void purgeTieAndServant(Tie paramTie)
  {
    synchronized (tieCache)
    {
      Remote localRemote = paramTie.getTarget();
      if (localRemote != null) {
        tieCache.remove(localRemote);
      }
    }
  }
  
  public static String stubNameFromRepID(String paramString)
  {
    RepositoryId localRepositoryId = RepositoryId.cache.getId(paramString);
    String str = localRepositoryId.getClassName();
    if (localRepositoryId.isIDLType()) {
      str = idlStubName(str);
    } else {
      str = stubName(str);
    }
    return str;
  }
  
  public static Remote loadStub(org.omg.CORBA.Object paramObject, Class paramClass)
  {
    Remote localRemote = null;
    try
    {
      String str = null;
      try
      {
        org.omg.CORBA.portable.Delegate localDelegate = StubAdapter.getDelegate(paramObject);
        str = ((org.omg.CORBA_2_3.portable.Delegate)localDelegate).get_codebase(paramObject);
      }
      catch (ClassCastException localClassCastException)
      {
        wrapper.classCastExceptionInLoadStub(localClassCastException);
      }
      PresentationManager.StubFactoryFactory localStubFactoryFactory = com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory();
      PresentationManager.StubFactory localStubFactory = localStubFactoryFactory.createStubFactory(paramClass.getName(), false, str, paramClass, paramClass.getClassLoader());
      localRemote = (Remote)localStubFactory.makeStub();
      StubAdapter.setDelegate(localRemote, StubAdapter.getDelegate(paramObject));
    }
    catch (Exception localException)
    {
      wrapper.exceptionInLoadStub(localException);
    }
    return localRemote;
  }
  
  public static Class loadStubClass(String paramString1, String paramString2, Class paramClass)
    throws ClassNotFoundException
  {
    if (paramString1.length() == 0) {
      throw new ClassNotFoundException();
    }
    String str = stubNameFromRepID(paramString1);
    ClassLoader localClassLoader = paramClass == null ? null : paramClass.getClassLoader();
    try
    {
      return loadClassOfType(str, paramString2, localClassLoader, paramClass, localClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    return loadClassOfType(PackagePrefixChecker.packagePrefix() + str, paramString2, localClassLoader, paramClass, localClassLoader);
  }
  
  public static String stubName(String paramString)
  {
    return stubName(paramString, false);
  }
  
  public static String dynamicStubName(String paramString)
  {
    return stubName(paramString, true);
  }
  
  private static String stubName(String paramString, boolean paramBoolean)
  {
    String str = stubNameForCompiler(paramString, paramBoolean);
    if (PackagePrefixChecker.hasOffendingPrefix(str)) {
      str = PackagePrefixChecker.packagePrefix() + str;
    }
    return str;
  }
  
  public static String stubNameForCompiler(String paramString)
  {
    return stubNameForCompiler(paramString, false);
  }
  
  private static String stubNameForCompiler(String paramString, boolean paramBoolean)
  {
    int i = paramString.indexOf('$');
    if (i < 0) {
      i = paramString.lastIndexOf('.');
    }
    String str = paramBoolean ? "_DynamicStub" : "_Stub";
    if (i > 0) {
      return paramString.substring(0, i + 1) + "_" + paramString.substring(i + 1) + str;
    }
    return "_" + paramString + str;
  }
  
  public static String tieName(String paramString)
  {
    return PackagePrefixChecker.hasOffendingPrefix(tieNameForCompiler(paramString)) ? PackagePrefixChecker.packagePrefix() + tieNameForCompiler(paramString) : tieNameForCompiler(paramString);
  }
  
  public static String tieNameForCompiler(String paramString)
  {
    int i = paramString.indexOf('$');
    if (i < 0) {
      i = paramString.lastIndexOf('.');
    }
    if (i > 0) {
      return paramString.substring(0, i + 1) + "_" + paramString.substring(i + 1) + "_Tie";
    }
    return "_" + paramString + "_Tie";
  }
  
  public static void throwNotSerializableForCorba(String paramString)
  {
    throw omgWrapper.notSerializable(CompletionStatus.COMPLETED_MAYBE, paramString);
  }
  
  public static String idlStubName(String paramString)
  {
    String str = null;
    int i = paramString.lastIndexOf('.');
    if (i > 0) {
      str = paramString.substring(0, i + 1) + "_" + paramString.substring(i + 1) + "Stub";
    } else {
      str = "_" + paramString + "Stub";
    }
    return str;
  }
  
  public static void printStackTrace()
  {
    Throwable localThrowable = new Throwable("Printing stack trace:");
    localThrowable.fillInStackTrace();
    localThrowable.printStackTrace();
  }
  
  public static Object readObjectAndNarrow(org.omg.CORBA.portable.InputStream paramInputStream, Class paramClass)
    throws ClassCastException
  {
    org.omg.CORBA.Object localObject = paramInputStream.read_Object();
    if (localObject != null) {
      return PortableRemoteObject.narrow(localObject, paramClass);
    }
    return null;
  }
  
  public static Object readAbstractAndNarrow(org.omg.CORBA_2_3.portable.InputStream paramInputStream, Class paramClass)
    throws ClassCastException
  {
    Object localObject = paramInputStream.read_abstract_interface();
    if (localObject != null) {
      return PortableRemoteObject.narrow(localObject, paramClass);
    }
    return null;
  }
  
  static int hexOf(char paramChar)
  {
    int i = paramChar - '0';
    if ((i >= 0) && (i <= 9)) {
      return i;
    }
    i = paramChar - 'a' + 10;
    if ((i >= 10) && (i <= 15)) {
      return i;
    }
    i = paramChar - 'A' + 10;
    if ((i >= 10) && (i <= 15)) {
      return i;
    }
    throw wrapper.badHexDigit();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\Utility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */