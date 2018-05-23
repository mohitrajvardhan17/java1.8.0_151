package sun.security.jca;

import java.security.Provider;
import sun.security.util.Debug;

public class Providers
{
  private static final ThreadLocal<ProviderList> threadLists = new InheritableThreadLocal();
  private static volatile int threadListsUsed;
  private static volatile ProviderList providerList = ProviderList.fromSecurityProperties();
  private static final String BACKUP_PROVIDER_CLASSNAME = "sun.security.provider.VerificationProvider";
  private static final String[] jarVerificationProviders = { "sun.security.provider.Sun", "sun.security.rsa.SunRsaSign", "sun.security.ec.SunEC", "sun.security.provider.VerificationProvider" };
  
  private Providers() {}
  
  public static Provider getSunProvider()
  {
    try
    {
      Class localClass1 = Class.forName(jarVerificationProviders[0]);
      return (Provider)localClass1.newInstance();
    }
    catch (Exception localException1)
    {
      try
      {
        Class localClass2 = Class.forName("sun.security.provider.VerificationProvider");
        return (Provider)localClass2.newInstance();
      }
      catch (Exception localException2)
      {
        throw new RuntimeException("Sun provider not found", localException1);
      }
    }
  }
  
  public static Object startJarVerification()
  {
    ProviderList localProviderList1 = getProviderList();
    ProviderList localProviderList2 = localProviderList1.getJarList(jarVerificationProviders);
    return beginThreadProviderList(localProviderList2);
  }
  
  public static void stopJarVerification(Object paramObject)
  {
    endThreadProviderList((ProviderList)paramObject);
  }
  
  public static ProviderList getProviderList()
  {
    ProviderList localProviderList = getThreadProviderList();
    if (localProviderList == null) {
      localProviderList = getSystemProviderList();
    }
    return localProviderList;
  }
  
  public static void setProviderList(ProviderList paramProviderList)
  {
    if (getThreadProviderList() == null) {
      setSystemProviderList(paramProviderList);
    } else {
      changeThreadProviderList(paramProviderList);
    }
  }
  
  public static ProviderList getFullProviderList()
  {
    synchronized (Providers.class)
    {
      localObject1 = getThreadProviderList();
      if (localObject1 != null)
      {
        ProviderList localProviderList = ((ProviderList)localObject1).removeInvalid();
        if (localProviderList != localObject1)
        {
          changeThreadProviderList(localProviderList);
          localObject1 = localProviderList;
        }
        return (ProviderList)localObject1;
      }
    }
    Object localObject1 = getSystemProviderList();
    ??? = ((ProviderList)localObject1).removeInvalid();
    if (??? != localObject1)
    {
      setSystemProviderList((ProviderList)???);
      localObject1 = ???;
    }
    return (ProviderList)localObject1;
  }
  
  private static ProviderList getSystemProviderList()
  {
    return providerList;
  }
  
  private static void setSystemProviderList(ProviderList paramProviderList)
  {
    providerList = paramProviderList;
  }
  
  public static ProviderList getThreadProviderList()
  {
    if (threadListsUsed == 0) {
      return null;
    }
    return (ProviderList)threadLists.get();
  }
  
  private static void changeThreadProviderList(ProviderList paramProviderList)
  {
    threadLists.set(paramProviderList);
  }
  
  public static synchronized ProviderList beginThreadProviderList(ProviderList paramProviderList)
  {
    if (ProviderList.debug != null) {
      ProviderList.debug.println("ThreadLocal providers: " + paramProviderList);
    }
    ProviderList localProviderList = (ProviderList)threadLists.get();
    threadListsUsed += 1;
    threadLists.set(paramProviderList);
    return localProviderList;
  }
  
  public static synchronized void endThreadProviderList(ProviderList paramProviderList)
  {
    if (paramProviderList == null)
    {
      if (ProviderList.debug != null) {
        ProviderList.debug.println("Disabling ThreadLocal providers");
      }
      threadLists.remove();
    }
    else
    {
      if (ProviderList.debug != null) {
        ProviderList.debug.println("Restoring previous ThreadLocal providers: " + paramProviderList);
      }
      threadLists.set(paramProviderList);
    }
    threadListsUsed -= 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jca\Providers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */