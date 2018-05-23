package sun.management;

import com.sun.management.VMOption;
import java.lang.management.PlatformManagedObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import jdk.internal.cmm.SystemResourcePressureImpl;
import jdk.management.cmm.SystemResourcePressureMXBean;

public final class ExtendedPlatformComponent
{
  private static SystemResourcePressureMXBean cmmBeanImpl = null;
  
  private ExtendedPlatformComponent() {}
  
  private static synchronized SystemResourcePressureMXBean getCMMBean()
  {
    if (cmmBeanImpl == null) {
      cmmBeanImpl = new SystemResourcePressureImpl();
    }
    return cmmBeanImpl;
  }
  
  public static List<? extends PlatformManagedObject> getMXBeans()
  {
    if (shouldRegisterCMMBean()) {
      return Collections.singletonList(getCMMBean());
    }
    return Collections.emptyList();
  }
  
  public static <T extends PlatformManagedObject> T getMXBean(Class<T> paramClass)
  {
    if ((paramClass != null) && ("jdk.management.cmm.SystemResourcePressureMXBean".equals(paramClass.getName())))
    {
      if (isUnlockCommercialFeaturesEnabled()) {
        return (PlatformManagedObject)paramClass.cast(getCMMBean());
      }
      throw new IllegalArgumentException("Cooperative Memory Management is a commercial feature which must be unlocked before being used.  To learn more about commercial features and how to unlock them visit http://www.oracle.com/technetwork/java/javaseproducts/");
    }
    return null;
  }
  
  private static boolean shouldRegisterCMMBean()
  {
    if (!isUnlockCommercialFeaturesEnabled()) {
      return false;
    }
    boolean bool = false;
    Class localClass = null;
    try
    {
      ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
      if (localClassLoader == null) {
        return false;
      }
      localClassLoader = localClassLoader.getParent();
      localClass = Class.forName("com.oracle.exalogic.ExaManager", false, localClassLoader);
      Object localObject1 = localClass.getMethod("instance", new Class[0]).invoke(null, new Object[0]);
      if (localObject1 != null)
      {
        Object localObject2 = localClass.getMethod("isExalogicSystem", new Class[0]).invoke(localObject1, new Object[0]);
        bool = ((Boolean)localObject2).booleanValue();
      }
      return bool;
    }
    catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException|IllegalArgumentException|InvocationTargetException localClassNotFoundException) {}
    return false;
  }
  
  private static boolean isUnlockCommercialFeaturesEnabled()
  {
    Flag localFlag = Flag.getFlag("UnlockCommercialFeatures");
    return (localFlag != null) && ("true".equals(localFlag.getVMOption().getValue()));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\ExtendedPlatformComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */