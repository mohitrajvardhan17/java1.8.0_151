package sun.reflect;

import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Objects;
import sun.reflect.misc.ReflectUtil;

public class ReflectionFactory
{
  private static boolean initted = false;
  private static final Permission reflectionFactoryAccessPerm = new RuntimePermission("reflectionFactoryAccess");
  private static final ReflectionFactory soleInstance = new ReflectionFactory();
  private static volatile LangReflectAccess langReflectAccess;
  private static volatile Method hasStaticInitializerMethod;
  private static boolean noInflation = false;
  private static int inflationThreshold = 15;
  
  private ReflectionFactory() {}
  
  public static ReflectionFactory getReflectionFactory()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(reflectionFactoryAccessPerm);
    }
    return soleInstance;
  }
  
  public void setLangReflectAccess(LangReflectAccess paramLangReflectAccess)
  {
    langReflectAccess = paramLangReflectAccess;
  }
  
  public FieldAccessor newFieldAccessor(Field paramField, boolean paramBoolean)
  {
    checkInitted();
    return UnsafeFieldAccessorFactory.newFieldAccessor(paramField, paramBoolean);
  }
  
  public MethodAccessor newMethodAccessor(Method paramMethod)
  {
    
    if ((noInflation) && (!ReflectUtil.isVMAnonymousClass(paramMethod.getDeclaringClass()))) {
      return new MethodAccessorGenerator().generateMethod(paramMethod.getDeclaringClass(), paramMethod.getName(), paramMethod.getParameterTypes(), paramMethod.getReturnType(), paramMethod.getExceptionTypes(), paramMethod.getModifiers());
    }
    NativeMethodAccessorImpl localNativeMethodAccessorImpl = new NativeMethodAccessorImpl(paramMethod);
    DelegatingMethodAccessorImpl localDelegatingMethodAccessorImpl = new DelegatingMethodAccessorImpl(localNativeMethodAccessorImpl);
    localNativeMethodAccessorImpl.setParent(localDelegatingMethodAccessorImpl);
    return localDelegatingMethodAccessorImpl;
  }
  
  public ConstructorAccessor newConstructorAccessor(Constructor<?> paramConstructor)
  {
    checkInitted();
    Class localClass = paramConstructor.getDeclaringClass();
    if (Modifier.isAbstract(localClass.getModifiers())) {
      return new InstantiationExceptionConstructorAccessorImpl(null);
    }
    if (localClass == Class.class) {
      return new InstantiationExceptionConstructorAccessorImpl("Can not instantiate java.lang.Class");
    }
    if (Reflection.isSubclassOf(localClass, ConstructorAccessorImpl.class)) {
      return new BootstrapConstructorAccessorImpl(paramConstructor);
    }
    if ((noInflation) && (!ReflectUtil.isVMAnonymousClass(paramConstructor.getDeclaringClass()))) {
      return new MethodAccessorGenerator().generateConstructor(paramConstructor.getDeclaringClass(), paramConstructor.getParameterTypes(), paramConstructor.getExceptionTypes(), paramConstructor.getModifiers());
    }
    NativeConstructorAccessorImpl localNativeConstructorAccessorImpl = new NativeConstructorAccessorImpl(paramConstructor);
    DelegatingConstructorAccessorImpl localDelegatingConstructorAccessorImpl = new DelegatingConstructorAccessorImpl(localNativeConstructorAccessorImpl);
    localNativeConstructorAccessorImpl.setParent(localDelegatingConstructorAccessorImpl);
    return localDelegatingConstructorAccessorImpl;
  }
  
  public Field newField(Class<?> paramClass1, String paramString1, Class<?> paramClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte)
  {
    return langReflectAccess().newField(paramClass1, paramString1, paramClass2, paramInt1, paramInt2, paramString2, paramArrayOfByte);
  }
  
  public Method newMethod(Class<?> paramClass1, String paramString1, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    return langReflectAccess().newMethod(paramClass1, paramString1, paramArrayOfClass1, paramClass2, paramArrayOfClass2, paramInt1, paramInt2, paramString2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
  }
  
  public Constructor<?> newConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    return langReflectAccess().newConstructor(paramClass, paramArrayOfClass1, paramArrayOfClass2, paramInt1, paramInt2, paramString, paramArrayOfByte1, paramArrayOfByte2);
  }
  
  public MethodAccessor getMethodAccessor(Method paramMethod)
  {
    return langReflectAccess().getMethodAccessor(paramMethod);
  }
  
  public void setMethodAccessor(Method paramMethod, MethodAccessor paramMethodAccessor)
  {
    langReflectAccess().setMethodAccessor(paramMethod, paramMethodAccessor);
  }
  
  public ConstructorAccessor getConstructorAccessor(Constructor<?> paramConstructor)
  {
    return langReflectAccess().getConstructorAccessor(paramConstructor);
  }
  
  public void setConstructorAccessor(Constructor<?> paramConstructor, ConstructorAccessor paramConstructorAccessor)
  {
    langReflectAccess().setConstructorAccessor(paramConstructor, paramConstructorAccessor);
  }
  
  public Method copyMethod(Method paramMethod)
  {
    return langReflectAccess().copyMethod(paramMethod);
  }
  
  public Field copyField(Field paramField)
  {
    return langReflectAccess().copyField(paramField);
  }
  
  public <T> Constructor<T> copyConstructor(Constructor<T> paramConstructor)
  {
    return langReflectAccess().copyConstructor(paramConstructor);
  }
  
  public byte[] getExecutableTypeAnnotationBytes(Executable paramExecutable)
  {
    return langReflectAccess().getExecutableTypeAnnotationBytes(paramExecutable);
  }
  
  public Constructor<?> newConstructorForSerialization(Class<?> paramClass, Constructor<?> paramConstructor)
  {
    if (paramConstructor.getDeclaringClass() == paramClass) {
      return paramConstructor;
    }
    return generateConstructor(paramClass, paramConstructor);
  }
  
  public final Constructor<?> newConstructorForSerialization(Class<?> paramClass)
  {
    Object localObject = paramClass;
    while (Serializable.class.isAssignableFrom((Class)localObject)) {
      if ((localObject = ((Class)localObject).getSuperclass()) == null) {
        return null;
      }
    }
    Constructor localConstructor;
    try
    {
      localConstructor = ((Class)localObject).getDeclaredConstructor(new Class[0]);
      int i = localConstructor.getModifiers();
      if (((i & 0x2) != 0) || (((i & 0x5) == 0) && (!packageEquals(paramClass, (Class)localObject)))) {
        return null;
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      return null;
    }
    return generateConstructor(paramClass, localConstructor);
  }
  
  private final Constructor<?> generateConstructor(Class<?> paramClass, Constructor<?> paramConstructor)
  {
    SerializationConstructorAccessorImpl localSerializationConstructorAccessorImpl = new MethodAccessorGenerator().generateSerializationConstructor(paramClass, paramConstructor.getParameterTypes(), paramConstructor.getExceptionTypes(), paramConstructor.getModifiers(), paramConstructor.getDeclaringClass());
    Constructor localConstructor = newConstructor(paramConstructor.getDeclaringClass(), paramConstructor.getParameterTypes(), paramConstructor.getExceptionTypes(), paramConstructor.getModifiers(), langReflectAccess().getConstructorSlot(paramConstructor), langReflectAccess().getConstructorSignature(paramConstructor), langReflectAccess().getConstructorAnnotations(paramConstructor), langReflectAccess().getConstructorParameterAnnotations(paramConstructor));
    setConstructorAccessor(localConstructor, localSerializationConstructorAccessorImpl);
    localConstructor.setAccessible(true);
    return localConstructor;
  }
  
  public final Constructor<?> newConstructorForExternalization(Class<?> paramClass)
  {
    if (!Externalizable.class.isAssignableFrom(paramClass)) {
      return null;
    }
    try
    {
      Constructor localConstructor = paramClass.getConstructor(new Class[0]);
      localConstructor.setAccessible(true);
      return localConstructor;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return null;
  }
  
  public final MethodHandle readObjectForSerialization(Class<?> paramClass)
  {
    return findReadWriteObjectForSerialization(paramClass, "readObject", ObjectInputStream.class);
  }
  
  public final MethodHandle readObjectNoDataForSerialization(Class<?> paramClass)
  {
    return findReadWriteObjectForSerialization(paramClass, "readObjectNoData", ObjectInputStream.class);
  }
  
  public final MethodHandle writeObjectForSerialization(Class<?> paramClass)
  {
    return findReadWriteObjectForSerialization(paramClass, "writeObject", ObjectOutputStream.class);
  }
  
  private final MethodHandle findReadWriteObjectForSerialization(Class<?> paramClass1, String paramString, Class<?> paramClass2)
  {
    if (!Serializable.class.isAssignableFrom(paramClass1)) {
      return null;
    }
    try
    {
      Method localMethod = paramClass1.getDeclaredMethod(paramString, new Class[] { paramClass2 });
      int i = localMethod.getModifiers();
      if ((localMethod.getReturnType() != Void.TYPE) || (Modifier.isStatic(i)) || (!Modifier.isPrivate(i))) {
        return null;
      }
      localMethod.setAccessible(true);
      return MethodHandles.lookup().unreflect(localMethod);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      return null;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new InternalError("Error", localIllegalAccessException);
    }
  }
  
  public final MethodHandle readResolveForSerialization(Class<?> paramClass)
  {
    return getReplaceResolveForSerialization(paramClass, "readResolve");
  }
  
  public final MethodHandle writeReplaceForSerialization(Class<?> paramClass)
  {
    return getReplaceResolveForSerialization(paramClass, "writeReplace");
  }
  
  private MethodHandle getReplaceResolveForSerialization(Class<?> paramClass, String paramString)
  {
    if (!Serializable.class.isAssignableFrom(paramClass)) {
      return null;
    }
    Object localObject = paramClass;
    for (;;)
    {
      if (localObject != null) {
        try
        {
          Method localMethod = ((Class)localObject).getDeclaredMethod(paramString, new Class[0]);
          if (localMethod.getReturnType() != Object.class) {
            return null;
          }
          int i = localMethod.getModifiers();
          if ((Modifier.isStatic(i) | Modifier.isAbstract(i))) {
            return null;
          }
          if (!(Modifier.isPublic(i) | Modifier.isProtected(i)))
          {
            if ((Modifier.isPrivate(i)) && (paramClass != localObject)) {
              return null;
            }
            if (!packageEquals(paramClass, (Class)localObject)) {
              return null;
            }
          }
          try
          {
            localMethod.setAccessible(true);
            return MethodHandles.lookup().unreflect(localMethod);
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new InternalError("Error", localIllegalAccessException);
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          localObject = ((Class)localObject).getSuperclass();
        }
      }
    }
    return null;
  }
  
  public final boolean hasStaticInitializerForSerialization(Class<?> paramClass)
  {
    Method localMethod = hasStaticInitializerMethod;
    if (localMethod == null) {
      try
      {
        localMethod = ObjectStreamClass.class.getDeclaredMethod("hasStaticInitializer", new Class[] { Class.class });
        localMethod.setAccessible(true);
        hasStaticInitializerMethod = localMethod;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new InternalError("No such method hasStaticInitializer on " + ObjectStreamClass.class, localNoSuchMethodException);
      }
    }
    try
    {
      return ((Boolean)localMethod.invoke(null, new Object[] { paramClass })).booleanValue();
    }
    catch (InvocationTargetException|IllegalAccessException localInvocationTargetException)
    {
      throw new InternalError("Exception invoking hasStaticInitializer", localInvocationTargetException);
    }
  }
  
  public final OptionalDataException newOptionalDataExceptionForSerialization(boolean paramBoolean)
  {
    try
    {
      Constructor localConstructor = OptionalDataException.class.getDeclaredConstructor(new Class[] { Boolean.TYPE });
      localConstructor.setAccessible(true);
      return (OptionalDataException)localConstructor.newInstance(new Object[] { Boolean.valueOf(paramBoolean) });
    }
    catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException localNoSuchMethodException)
    {
      throw new InternalError("unable to create OptionalDataException", localNoSuchMethodException);
    }
  }
  
  static int inflationThreshold()
  {
    return inflationThreshold;
  }
  
  private static void checkInitted()
  {
    if (initted) {
      return;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        if (System.out == null) {
          return null;
        }
        String str = System.getProperty("sun.reflect.noInflation");
        if ((str != null) && (str.equals("true"))) {
          ReflectionFactory.access$002(true);
        }
        str = System.getProperty("sun.reflect.inflationThreshold");
        if (str != null) {
          try
          {
            ReflectionFactory.access$102(Integer.parseInt(str));
          }
          catch (NumberFormatException localNumberFormatException)
          {
            throw new RuntimeException("Unable to parse property sun.reflect.inflationThreshold", localNumberFormatException);
          }
        }
        ReflectionFactory.access$202(true);
        return null;
      }
    });
  }
  
  private static LangReflectAccess langReflectAccess()
  {
    if (langReflectAccess == null) {
      Modifier.isPublic(1);
    }
    return langReflectAccess;
  }
  
  private static boolean packageEquals(Class<?> paramClass1, Class<?> paramClass2)
  {
    return (paramClass1.getClassLoader() == paramClass2.getClassLoader()) && (Objects.equals(paramClass1.getPackage(), paramClass2.getPackage()));
  }
  
  public static final class GetReflectionFactoryAction
    implements PrivilegedAction<ReflectionFactory>
  {
    public GetReflectionFactoryAction() {}
    
    public ReflectionFactory run()
    {
      return ReflectionFactory.getReflectionFactory();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\ReflectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */