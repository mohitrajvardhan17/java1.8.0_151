package java.lang.reflect;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import sun.misc.ProxyGenerator;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class Proxy
  implements Serializable
{
  private static final long serialVersionUID = -2222568056686623797L;
  private static final Class<?>[] constructorParams = { InvocationHandler.class };
  private static final WeakCache<ClassLoader, Class<?>[], Class<?>> proxyClassCache = new WeakCache(new KeyFactory(null), new ProxyClassFactory(null));
  protected InvocationHandler h;
  private static final Object key0 = new Object();
  
  private Proxy() {}
  
  protected Proxy(InvocationHandler paramInvocationHandler)
  {
    Objects.requireNonNull(paramInvocationHandler);
    h = paramInvocationHandler;
  }
  
  @CallerSensitive
  public static Class<?> getProxyClass(ClassLoader paramClassLoader, Class<?>... paramVarArgs)
    throws IllegalArgumentException
  {
    Class[] arrayOfClass = (Class[])paramVarArgs.clone();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      checkProxyAccess(Reflection.getCallerClass(), paramClassLoader, arrayOfClass);
    }
    return getProxyClass0(paramClassLoader, arrayOfClass);
  }
  
  private static void checkProxyAccess(Class<?> paramClass, ClassLoader paramClassLoader, Class<?>... paramVarArgs)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      ClassLoader localClassLoader = paramClass.getClassLoader();
      if ((VM.isSystemDomainLoader(paramClassLoader)) && (!VM.isSystemDomainLoader(localClassLoader))) {
        localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
      }
      ReflectUtil.checkProxyPackageAccess(localClassLoader, paramVarArgs);
    }
  }
  
  private static Class<?> getProxyClass0(ClassLoader paramClassLoader, Class<?>... paramVarArgs)
  {
    if (paramVarArgs.length > 65535) {
      throw new IllegalArgumentException("interface limit exceeded");
    }
    return (Class)proxyClassCache.get(paramClassLoader, paramVarArgs);
  }
  
  @CallerSensitive
  public static Object newProxyInstance(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass, InvocationHandler paramInvocationHandler)
    throws IllegalArgumentException
  {
    Objects.requireNonNull(paramInvocationHandler);
    Class[] arrayOfClass = (Class[])paramArrayOfClass.clone();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      checkProxyAccess(Reflection.getCallerClass(), paramClassLoader, arrayOfClass);
    }
    Class localClass = getProxyClass0(paramClassLoader, arrayOfClass);
    try
    {
      if (localSecurityManager != null) {
        checkNewProxyPermission(Reflection.getCallerClass(), localClass);
      }
      Constructor localConstructor = localClass.getConstructor(constructorParams);
      localObject = paramInvocationHandler;
      if (!Modifier.isPublic(localClass.getModifiers())) {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            val$cons.setAccessible(true);
            return null;
          }
        });
      }
      return localConstructor.newInstance(new Object[] { paramInvocationHandler });
    }
    catch (IllegalAccessException|InstantiationException localIllegalAccessException)
    {
      throw new InternalError(localIllegalAccessException.toString(), localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Object localObject = localInvocationTargetException.getCause();
      if ((localObject instanceof RuntimeException)) {
        throw ((RuntimeException)localObject);
      }
      throw new InternalError(((Throwable)localObject).toString(), (Throwable)localObject);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new InternalError(localNoSuchMethodException.toString(), localNoSuchMethodException);
    }
  }
  
  private static void checkNewProxyPermission(Class<?> paramClass1, Class<?> paramClass2)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localSecurityManager != null) && (ReflectUtil.isNonPublicProxyClass(paramClass2)))
    {
      ClassLoader localClassLoader1 = paramClass1.getClassLoader();
      ClassLoader localClassLoader2 = paramClass2.getClassLoader();
      int i = paramClass2.getName().lastIndexOf('.');
      String str1 = i == -1 ? "" : paramClass2.getName().substring(0, i);
      i = paramClass1.getName().lastIndexOf('.');
      String str2 = i == -1 ? "" : paramClass1.getName().substring(0, i);
      if ((localClassLoader2 != localClassLoader1) || (!str1.equals(str2))) {
        localSecurityManager.checkPermission(new ReflectPermission("newProxyInPackage." + str1));
      }
    }
  }
  
  public static boolean isProxyClass(Class<?> paramClass)
  {
    return (Proxy.class.isAssignableFrom(paramClass)) && (proxyClassCache.containsValue(paramClass));
  }
  
  @CallerSensitive
  public static InvocationHandler getInvocationHandler(Object paramObject)
    throws IllegalArgumentException
  {
    if (!isProxyClass(paramObject.getClass())) {
      throw new IllegalArgumentException("not a proxy instance");
    }
    Proxy localProxy = (Proxy)paramObject;
    InvocationHandler localInvocationHandler = h;
    if (System.getSecurityManager() != null)
    {
      Class localClass1 = localInvocationHandler.getClass();
      Class localClass2 = Reflection.getCallerClass();
      if (ReflectUtil.needsPackageAccessCheck(localClass2.getClassLoader(), localClass1.getClassLoader())) {
        ReflectUtil.checkPackageAccess(localClass1);
      }
    }
    return localInvocationHandler;
  }
  
  private static native Class<?> defineClass0(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static final class Key1
    extends WeakReference<Class<?>>
  {
    private final int hash;
    
    Key1(Class<?> paramClass)
    {
      super();
      hash = paramClass.hashCode();
    }
    
    public int hashCode()
    {
      return hash;
    }
    
    public boolean equals(Object paramObject)
    {
      Class localClass;
      return (this == paramObject) || ((paramObject != null) && (paramObject.getClass() == Key1.class) && ((localClass = (Class)get()) != null) && (localClass == ((Key1)paramObject).get()));
    }
  }
  
  private static final class Key2
    extends WeakReference<Class<?>>
  {
    private final int hash;
    private final WeakReference<Class<?>> ref2;
    
    Key2(Class<?> paramClass1, Class<?> paramClass2)
    {
      super();
      hash = (31 * paramClass1.hashCode() + paramClass2.hashCode());
      ref2 = new WeakReference(paramClass2);
    }
    
    public int hashCode()
    {
      return hash;
    }
    
    public boolean equals(Object paramObject)
    {
      Class localClass1;
      Class localClass2;
      return (this == paramObject) || ((paramObject != null) && (paramObject.getClass() == Key2.class) && ((localClass1 = (Class)get()) != null) && (localClass1 == ((Key2)paramObject).get()) && ((localClass2 = (Class)ref2.get()) != null) && (localClass2 == ref2.get()));
    }
  }
  
  private static final class KeyFactory
    implements BiFunction<ClassLoader, Class<?>[], Object>
  {
    private KeyFactory() {}
    
    public Object apply(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass)
    {
      switch (paramArrayOfClass.length)
      {
      case 1: 
        return new Proxy.Key1(paramArrayOfClass[0]);
      case 2: 
        return new Proxy.Key2(paramArrayOfClass[0], paramArrayOfClass[1]);
      case 0: 
        return Proxy.key0;
      }
      return new Proxy.KeyX(paramArrayOfClass);
    }
  }
  
  private static final class KeyX
  {
    private final int hash;
    private final WeakReference<Class<?>>[] refs;
    
    KeyX(Class<?>[] paramArrayOfClass)
    {
      hash = Arrays.hashCode(paramArrayOfClass);
      refs = ((WeakReference[])new WeakReference[paramArrayOfClass.length]);
      for (int i = 0; i < paramArrayOfClass.length; i++) {
        refs[i] = new WeakReference(paramArrayOfClass[i]);
      }
    }
    
    public int hashCode()
    {
      return hash;
    }
    
    public boolean equals(Object paramObject)
    {
      return (this == paramObject) || ((paramObject != null) && (paramObject.getClass() == KeyX.class) && (equals(refs, refs)));
    }
    
    private static boolean equals(WeakReference<Class<?>>[] paramArrayOfWeakReference1, WeakReference<Class<?>>[] paramArrayOfWeakReference2)
    {
      if (paramArrayOfWeakReference1.length != paramArrayOfWeakReference2.length) {
        return false;
      }
      for (int i = 0; i < paramArrayOfWeakReference1.length; i++)
      {
        Class localClass = (Class)paramArrayOfWeakReference1[i].get();
        if ((localClass == null) || (localClass != paramArrayOfWeakReference2[i].get())) {
          return false;
        }
      }
      return true;
    }
  }
  
  private static final class ProxyClassFactory
    implements BiFunction<ClassLoader, Class<?>[], Class<?>>
  {
    private static final String proxyClassNamePrefix = "$Proxy";
    private static final AtomicLong nextUniqueNumber = new AtomicLong();
    
    private ProxyClassFactory() {}
    
    public Class<?> apply(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass)
    {
      IdentityHashMap localIdentityHashMap = new IdentityHashMap(paramArrayOfClass.length);
      for (Object localObject2 : paramArrayOfClass)
      {
        Class localClass = null;
        try
        {
          localClass = Class.forName(((Class)localObject2).getName(), false, paramClassLoader);
        }
        catch (ClassNotFoundException localClassNotFoundException) {}
        if (localClass != localObject2) {
          throw new IllegalArgumentException(localObject2 + " is not visible from class loader");
        }
        if (!localClass.isInterface()) {
          throw new IllegalArgumentException(localClass.getName() + " is not an interface");
        }
        if (localIdentityHashMap.put(localClass, Boolean.TRUE) != null) {
          throw new IllegalArgumentException("repeated interface: " + localClass.getName());
        }
      }
      ??? = null;
      ??? = 17;
      for (localObject3 : paramArrayOfClass)
      {
        int n = ((Class)localObject3).getModifiers();
        if (!Modifier.isPublic(n))
        {
          ??? = 16;
          String str2 = ((Class)localObject3).getName();
          int i1 = str2.lastIndexOf('.');
          String str3 = i1 == -1 ? "" : str2.substring(0, i1 + 1);
          if (??? == null) {
            ??? = str3;
          } else if (!str3.equals(???)) {
            throw new IllegalArgumentException("non-public interfaces from different packages");
          }
        }
      }
      if (??? == null) {
        ??? = "com.sun.proxy.";
      }
      long l = nextUniqueNumber.getAndIncrement();
      String str1 = (String)??? + "$Proxy" + l;
      Object localObject3 = ProxyGenerator.generateProxyClass(str1, paramArrayOfClass, ???);
      try
      {
        return Proxy.defineClass0(paramClassLoader, str1, (byte[])localObject3, 0, localObject3.length);
      }
      catch (ClassFormatError localClassFormatError)
      {
        throw new IllegalArgumentException(localClassFormatError.toString());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\Proxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */