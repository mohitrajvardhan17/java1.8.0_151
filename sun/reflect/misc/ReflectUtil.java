package sun.reflect.misc;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import sun.reflect.Reflection;
import sun.security.util.SecurityConstants;

public final class ReflectUtil
{
  public static final String PROXY_PACKAGE = "com.sun.proxy";
  
  private ReflectUtil() {}
  
  public static Class<?> forName(String paramString)
    throws ClassNotFoundException
  {
    checkPackageAccess(paramString);
    return Class.forName(paramString);
  }
  
  public static Object newInstance(Class<?> paramClass)
    throws InstantiationException, IllegalAccessException
  {
    checkPackageAccess(paramClass);
    return paramClass.newInstance();
  }
  
  public static void ensureMemberAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt)
    throws IllegalAccessException
  {
    if ((paramObject == null) && (Modifier.isProtected(paramInt)))
    {
      int i = paramInt;
      i &= 0xFFFFFFFB;
      i |= 0x1;
      Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, i);
      try
      {
        i &= 0xFFFFFFFE;
        Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, i);
        return;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        if (isSubclassOf(paramClass1, paramClass2)) {
          return;
        }
        throw localIllegalAccessException;
      }
    }
    Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, paramInt);
  }
  
  private static boolean isSubclassOf(Class<?> paramClass1, Class<?> paramClass2)
  {
    while (paramClass1 != null)
    {
      if (paramClass1 == paramClass2) {
        return true;
      }
      paramClass1 = paramClass1.getSuperclass();
    }
    return false;
  }
  
  public static void conservativeCheckMemberAccess(Member paramMember)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null) {
      return;
    }
    Class localClass = paramMember.getDeclaringClass();
    checkPackageAccess(localClass);
    if ((Modifier.isPublic(paramMember.getModifiers())) && (Modifier.isPublic(localClass.getModifiers()))) {
      return;
    }
    localSecurityManager.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
  }
  
  public static void checkPackageAccess(Class<?> paramClass)
  {
    checkPackageAccess(paramClass.getName());
    if (isNonPublicProxyClass(paramClass)) {
      checkProxyPackageAccess(paramClass);
    }
  }
  
  public static void checkPackageAccess(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      String str = paramString.replace('/', '.');
      if (str.startsWith("["))
      {
        i = str.lastIndexOf('[') + 2;
        if ((i > 1) && (i < str.length())) {
          str = str.substring(i);
        }
      }
      int i = str.lastIndexOf('.');
      if (i != -1) {
        localSecurityManager.checkPackageAccess(str.substring(0, i));
      }
    }
  }
  
  public static boolean isPackageAccessible(Class<?> paramClass)
  {
    try
    {
      checkPackageAccess(paramClass);
    }
    catch (SecurityException localSecurityException)
    {
      return false;
    }
    return true;
  }
  
  private static boolean isAncestor(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
  {
    ClassLoader localClassLoader = paramClassLoader2;
    do
    {
      localClassLoader = localClassLoader.getParent();
      if (paramClassLoader1 == localClassLoader) {
        return true;
      }
    } while (localClassLoader != null);
    return false;
  }
  
  public static boolean needsPackageAccessCheck(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
  {
    if ((paramClassLoader1 == null) || (paramClassLoader1 == paramClassLoader2)) {
      return false;
    }
    if (paramClassLoader2 == null) {
      return true;
    }
    return !isAncestor(paramClassLoader1, paramClassLoader2);
  }
  
  public static void checkProxyPackageAccess(Class<?> paramClass)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localSecurityManager != null) && (Proxy.isProxyClass(paramClass))) {
      for (Class localClass : paramClass.getInterfaces()) {
        checkPackageAccess(localClass);
      }
    }
  }
  
  public static void checkProxyPackageAccess(ClassLoader paramClassLoader, Class<?>... paramVarArgs)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      for (Class<?> localClass : paramVarArgs)
      {
        ClassLoader localClassLoader = localClass.getClassLoader();
        if (needsPackageAccessCheck(paramClassLoader, localClassLoader)) {
          checkPackageAccess(localClass);
        }
      }
    }
  }
  
  public static boolean isNonPublicProxyClass(Class<?> paramClass)
  {
    String str1 = paramClass.getName();
    int i = str1.lastIndexOf('.');
    String str2 = i != -1 ? str1.substring(0, i) : "";
    return (Proxy.isProxyClass(paramClass)) && (!str2.equals("com.sun.proxy"));
  }
  
  public static void checkProxyMethod(Object paramObject, Method paramMethod)
  {
    if ((paramObject == null) || (!Proxy.isProxyClass(paramObject.getClass()))) {
      throw new IllegalArgumentException("Not a Proxy instance");
    }
    if (Modifier.isStatic(paramMethod.getModifiers())) {
      throw new IllegalArgumentException("Can't handle static method");
    }
    Class localClass = paramMethod.getDeclaringClass();
    if (localClass == Object.class)
    {
      String str = paramMethod.getName();
      if ((str.equals("hashCode")) || (str.equals("equals")) || (str.equals("toString"))) {
        return;
      }
    }
    if (isSuperInterface(paramObject.getClass(), localClass)) {
      return;
    }
    throw new IllegalArgumentException("Can't handle: " + paramMethod);
  }
  
  private static boolean isSuperInterface(Class<?> paramClass1, Class<?> paramClass2)
  {
    for (Class localClass : paramClass1.getInterfaces())
    {
      if (localClass == paramClass2) {
        return true;
      }
      if (isSuperInterface(localClass, paramClass2)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isVMAnonymousClass(Class<?> paramClass)
  {
    return paramClass.getName().indexOf("/") > -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\misc\ReflectUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */