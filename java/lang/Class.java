package java.lang;

import java.io.InputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.ConstantPool;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ClassRepository;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.scope.ClassScope;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public final class Class<T>
  implements Serializable, GenericDeclaration, Type, AnnotatedElement
{
  private static final int ANNOTATION = 8192;
  private static final int ENUM = 16384;
  private static final int SYNTHETIC = 4096;
  private volatile transient Constructor<T> cachedConstructor;
  private volatile transient Class<?> newInstanceCallerCache;
  private transient String name;
  private final ClassLoader classLoader;
  private static ProtectionDomain allPermDomain;
  private static boolean useCaches = true;
  private volatile transient SoftReference<ReflectionData<T>> reflectionData;
  private volatile transient int classRedefinedCount = 0;
  private volatile transient ClassRepository genericInfo;
  private static final long serialVersionUID = 3206093459760846163L;
  private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];
  private static ReflectionFactory reflectionFactory;
  private static boolean initted = false;
  private volatile transient T[] enumConstants = null;
  private volatile transient Map<String, T> enumConstantDirectory = null;
  private volatile transient AnnotationData annotationData;
  private volatile transient AnnotationType annotationType;
  transient ClassValue.ClassValueMap classValueMap;
  
  private static native void registerNatives();
  
  private Class(ClassLoader paramClassLoader)
  {
    classLoader = paramClassLoader;
  }
  
  public String toString()
  {
    return (isPrimitive() ? "" : isInterface() ? "interface " : "class ") + getName();
  }
  
  public String toGenericString()
  {
    if (isPrimitive()) {
      return toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = getModifiers() & Modifier.classModifiers();
    if (i != 0)
    {
      localStringBuilder.append(Modifier.toString(i));
      localStringBuilder.append(' ');
    }
    if (isAnnotation()) {
      localStringBuilder.append('@');
    }
    if (isInterface()) {
      localStringBuilder.append("interface");
    } else if (isEnum()) {
      localStringBuilder.append("enum");
    } else {
      localStringBuilder.append("class");
    }
    localStringBuilder.append(' ');
    localStringBuilder.append(getName());
    TypeVariable[] arrayOfTypeVariable1 = getTypeParameters();
    if (arrayOfTypeVariable1.length > 0)
    {
      int j = 1;
      localStringBuilder.append('<');
      for (TypeVariable localTypeVariable : arrayOfTypeVariable1)
      {
        if (j == 0) {
          localStringBuilder.append(',');
        }
        localStringBuilder.append(localTypeVariable.getTypeName());
        j = 0;
      }
      localStringBuilder.append('>');
    }
    return localStringBuilder.toString();
  }
  
  @CallerSensitive
  public static Class<?> forName(String paramString)
    throws ClassNotFoundException
  {
    Class localClass = Reflection.getCallerClass();
    return forName0(paramString, true, ClassLoader.getClassLoader(localClass), localClass);
  }
  
  @CallerSensitive
  public static Class<?> forName(String paramString, boolean paramBoolean, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    Class localClass = null;
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localClass = Reflection.getCallerClass();
      if (VM.isSystemDomainLoader(paramClassLoader))
      {
        ClassLoader localClassLoader = ClassLoader.getClassLoader(localClass);
        if (!VM.isSystemDomainLoader(localClassLoader)) {
          localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
        }
      }
    }
    return forName0(paramString, paramBoolean, paramClassLoader, localClass);
  }
  
  private static native Class<?> forName0(String paramString, boolean paramBoolean, ClassLoader paramClassLoader, Class<?> paramClass)
    throws ClassNotFoundException;
  
  @CallerSensitive
  public T newInstance()
    throws InstantiationException, IllegalAccessException
  {
    if (System.getSecurityManager() != null) {
      checkMemberAccess(0, Reflection.getCallerClass(), false);
    }
    if (cachedConstructor == null)
    {
      if (this == Class.class) {
        throw new IllegalAccessException("Can not call newInstance() on the Class for java.lang.Class");
      }
      try
      {
        Class[] arrayOfClass = new Class[0];
        final Constructor localConstructor2 = getConstructor0(arrayOfClass, 1);
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            localConstructor2.setAccessible(true);
            return null;
          }
        });
        cachedConstructor = localConstructor2;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw ((InstantiationException)new InstantiationException(getName()).initCause(localNoSuchMethodException));
      }
    }
    Constructor localConstructor1 = cachedConstructor;
    int i = localConstructor1.getModifiers();
    if (!Reflection.quickCheckMemberAccess(this, i))
    {
      Class localClass = Reflection.getCallerClass();
      if (newInstanceCallerCache != localClass)
      {
        Reflection.ensureMemberAccess(localClass, this, null, i);
        newInstanceCallerCache = localClass;
      }
    }
    try
    {
      return (T)localConstructor1.newInstance((Object[])null);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Unsafe.getUnsafe().throwException(localInvocationTargetException.getTargetException());
    }
    return null;
  }
  
  public native boolean isInstance(Object paramObject);
  
  public native boolean isAssignableFrom(Class<?> paramClass);
  
  public native boolean isInterface();
  
  public native boolean isArray();
  
  public native boolean isPrimitive();
  
  public boolean isAnnotation()
  {
    return (getModifiers() & 0x2000) != 0;
  }
  
  public boolean isSynthetic()
  {
    return (getModifiers() & 0x1000) != 0;
  }
  
  public String getName()
  {
    String str = name;
    if (str == null) {
      name = (str = getName0());
    }
    return str;
  }
  
  private native String getName0();
  
  @CallerSensitive
  public ClassLoader getClassLoader()
  {
    ClassLoader localClassLoader = getClassLoader0();
    if (localClassLoader == null) {
      return null;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      ClassLoader.checkClassLoaderPermission(localClassLoader, Reflection.getCallerClass());
    }
    return localClassLoader;
  }
  
  ClassLoader getClassLoader0()
  {
    return classLoader;
  }
  
  public TypeVariable<Class<T>>[] getTypeParameters()
  {
    ClassRepository localClassRepository = getGenericInfo();
    if (localClassRepository != null) {
      return (TypeVariable[])localClassRepository.getTypeParameters();
    }
    return (TypeVariable[])new TypeVariable[0];
  }
  
  public native Class<? super T> getSuperclass();
  
  public Type getGenericSuperclass()
  {
    ClassRepository localClassRepository = getGenericInfo();
    if (localClassRepository == null) {
      return getSuperclass();
    }
    if (isInterface()) {
      return null;
    }
    return localClassRepository.getSuperclass();
  }
  
  public Package getPackage()
  {
    return Package.getPackage(this);
  }
  
  public Class<?>[] getInterfaces()
  {
    ReflectionData localReflectionData = reflectionData();
    if (localReflectionData == null) {
      return getInterfaces0();
    }
    Class[] arrayOfClass = interfaces;
    if (arrayOfClass == null)
    {
      arrayOfClass = getInterfaces0();
      interfaces = arrayOfClass;
    }
    return (Class[])arrayOfClass.clone();
  }
  
  private native Class<?>[] getInterfaces0();
  
  public Type[] getGenericInterfaces()
  {
    ClassRepository localClassRepository = getGenericInfo();
    return localClassRepository == null ? getInterfaces() : localClassRepository.getSuperInterfaces();
  }
  
  public native Class<?> getComponentType();
  
  public native int getModifiers();
  
  public native Object[] getSigners();
  
  native void setSigners(Object[] paramArrayOfObject);
  
  @CallerSensitive
  public Method getEnclosingMethod()
    throws SecurityException
  {
    EnclosingMethodInfo localEnclosingMethodInfo = getEnclosingMethodInfo();
    if (localEnclosingMethodInfo == null) {
      return null;
    }
    if (!localEnclosingMethodInfo.isMethod()) {
      return null;
    }
    MethodRepository localMethodRepository = MethodRepository.make(localEnclosingMethodInfo.getDescriptor(), getFactory());
    Class localClass1 = toClass(localMethodRepository.getReturnType());
    Type[] arrayOfType = localMethodRepository.getParameterTypes();
    Class[] arrayOfClass1 = new Class[arrayOfType.length];
    for (int i = 0; i < arrayOfClass1.length; i++) {
      arrayOfClass1[i] = toClass(arrayOfType[i]);
    }
    Class localClass2 = localEnclosingMethodInfo.getEnclosingClass();
    localClass2.checkMemberAccess(1, Reflection.getCallerClass(), true);
    for (Method localMethod : localClass2.getDeclaredMethods()) {
      if (localMethod.getName().equals(localEnclosingMethodInfo.getName()))
      {
        Class[] arrayOfClass2 = localMethod.getParameterTypes();
        if (arrayOfClass2.length == arrayOfClass1.length)
        {
          int m = 1;
          for (int n = 0; n < arrayOfClass2.length; n++) {
            if (!arrayOfClass2[n].equals(arrayOfClass1[n]))
            {
              m = 0;
              break;
            }
          }
          if ((m != 0) && (localMethod.getReturnType().equals(localClass1))) {
            return localMethod;
          }
        }
      }
    }
    throw new InternalError("Enclosing method not found");
  }
  
  private native Object[] getEnclosingMethod0();
  
  private EnclosingMethodInfo getEnclosingMethodInfo()
  {
    Object[] arrayOfObject = getEnclosingMethod0();
    if (arrayOfObject == null) {
      return null;
    }
    return new EnclosingMethodInfo(arrayOfObject, null);
  }
  
  private static Class<?> toClass(Type paramType)
  {
    if ((paramType instanceof GenericArrayType)) {
      return Array.newInstance(toClass(((GenericArrayType)paramType).getGenericComponentType()), 0).getClass();
    }
    return (Class)paramType;
  }
  
  @CallerSensitive
  public Constructor<?> getEnclosingConstructor()
    throws SecurityException
  {
    EnclosingMethodInfo localEnclosingMethodInfo = getEnclosingMethodInfo();
    if (localEnclosingMethodInfo == null) {
      return null;
    }
    if (!localEnclosingMethodInfo.isConstructor()) {
      return null;
    }
    ConstructorRepository localConstructorRepository = ConstructorRepository.make(localEnclosingMethodInfo.getDescriptor(), getFactory());
    Type[] arrayOfType = localConstructorRepository.getParameterTypes();
    Class[] arrayOfClass1 = new Class[arrayOfType.length];
    for (int i = 0; i < arrayOfClass1.length; i++) {
      arrayOfClass1[i] = toClass(arrayOfType[i]);
    }
    Class localClass = localEnclosingMethodInfo.getEnclosingClass();
    localClass.checkMemberAccess(1, Reflection.getCallerClass(), true);
    for (Constructor localConstructor : localClass.getDeclaredConstructors())
    {
      Class[] arrayOfClass2 = localConstructor.getParameterTypes();
      if (arrayOfClass2.length == arrayOfClass1.length)
      {
        int m = 1;
        for (int n = 0; n < arrayOfClass2.length; n++) {
          if (!arrayOfClass2[n].equals(arrayOfClass1[n]))
          {
            m = 0;
            break;
          }
        }
        if (m != 0) {
          return localConstructor;
        }
      }
    }
    throw new InternalError("Enclosing constructor not found");
  }
  
  @CallerSensitive
  public Class<?> getDeclaringClass()
    throws SecurityException
  {
    Class localClass = getDeclaringClass0();
    if (localClass != null) {
      localClass.checkPackageAccess(ClassLoader.getClassLoader(Reflection.getCallerClass()), true);
    }
    return localClass;
  }
  
  private native Class<?> getDeclaringClass0();
  
  @CallerSensitive
  public Class<?> getEnclosingClass()
    throws SecurityException
  {
    EnclosingMethodInfo localEnclosingMethodInfo = getEnclosingMethodInfo();
    Object localObject;
    if (localEnclosingMethodInfo == null)
    {
      localObject = getDeclaringClass();
    }
    else
    {
      Class localClass = localEnclosingMethodInfo.getEnclosingClass();
      if ((localClass == this) || (localClass == null)) {
        throw new InternalError("Malformed enclosing method information");
      }
      localObject = localClass;
    }
    if (localObject != null) {
      ((Class)localObject).checkPackageAccess(ClassLoader.getClassLoader(Reflection.getCallerClass()), true);
    }
    return (Class<?>)localObject;
  }
  
  public String getSimpleName()
  {
    if (isArray()) {
      return getComponentType().getSimpleName() + "[]";
    }
    String str = getSimpleBinaryName();
    if (str == null)
    {
      str = getName();
      return str.substring(str.lastIndexOf(".") + 1);
    }
    int i = str.length();
    if ((i < 1) || (str.charAt(0) != '$')) {
      throw new InternalError("Malformed class name");
    }
    for (int j = 1; (j < i) && (isAsciiDigit(str.charAt(j))); j++) {}
    return str.substring(j);
  }
  
  public String getTypeName()
  {
    if (isArray()) {
      try
      {
        Class localClass = this;
        int i = 0;
        while (localClass.isArray())
        {
          i++;
          localClass = localClass.getComponentType();
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(localClass.getName());
        for (int j = 0; j < i; j++) {
          localStringBuilder.append("[]");
        }
        return localStringBuilder.toString();
      }
      catch (Throwable localThrowable) {}
    }
    return getName();
  }
  
  private static boolean isAsciiDigit(char paramChar)
  {
    return ('0' <= paramChar) && (paramChar <= '9');
  }
  
  public String getCanonicalName()
  {
    if (isArray())
    {
      localObject = getComponentType().getCanonicalName();
      if (localObject != null) {
        return (String)localObject + "[]";
      }
      return null;
    }
    if (isLocalOrAnonymousClass()) {
      return null;
    }
    Object localObject = getEnclosingClass();
    if (localObject == null) {
      return getName();
    }
    String str = ((Class)localObject).getCanonicalName();
    if (str == null) {
      return null;
    }
    return str + "." + getSimpleName();
  }
  
  public boolean isAnonymousClass()
  {
    return "".equals(getSimpleName());
  }
  
  public boolean isLocalClass()
  {
    return (isLocalOrAnonymousClass()) && (!isAnonymousClass());
  }
  
  public boolean isMemberClass()
  {
    return (getSimpleBinaryName() != null) && (!isLocalOrAnonymousClass());
  }
  
  private String getSimpleBinaryName()
  {
    Class localClass = getEnclosingClass();
    if (localClass == null) {
      return null;
    }
    try
    {
      return getName().substring(localClass.getName().length());
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new InternalError("Malformed class name", localIndexOutOfBoundsException);
    }
  }
  
  private boolean isLocalOrAnonymousClass()
  {
    return getEnclosingMethodInfo() != null;
  }
  
  @CallerSensitive
  public Class<?>[] getClasses()
  {
    checkMemberAccess(0, Reflection.getCallerClass(), false);
    (Class[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Class<?>[] run()
      {
        ArrayList localArrayList = new ArrayList();
        for (Class localClass = Class.this; localClass != null; localClass = localClass.getSuperclass())
        {
          Class[] arrayOfClass = localClass.getDeclaredClasses();
          for (int i = 0; i < arrayOfClass.length; i++) {
            if (Modifier.isPublic(arrayOfClass[i].getModifiers())) {
              localArrayList.add(arrayOfClass[i]);
            }
          }
        }
        return (Class[])localArrayList.toArray(new Class[0]);
      }
    });
  }
  
  @CallerSensitive
  public Field[] getFields()
    throws SecurityException
  {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    return copyFields(privateGetPublicFields(null));
  }
  
  @CallerSensitive
  public Method[] getMethods()
    throws SecurityException
  {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    return copyMethods(privateGetPublicMethods());
  }
  
  @CallerSensitive
  public Constructor<?>[] getConstructors()
    throws SecurityException
  {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    return copyConstructors(privateGetDeclaredConstructors(true));
  }
  
  @CallerSensitive
  public Field getField(String paramString)
    throws NoSuchFieldException, SecurityException
  {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    Field localField = getField0(paramString);
    if (localField == null) {
      throw new NoSuchFieldException(paramString);
    }
    return localField;
  }
  
  @CallerSensitive
  public Method getMethod(String paramString, Class<?>... paramVarArgs)
    throws NoSuchMethodException, SecurityException
  {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    Method localMethod = getMethod0(paramString, paramVarArgs, true);
    if (localMethod == null) {
      throw new NoSuchMethodException(getName() + "." + paramString + argumentTypesToString(paramVarArgs));
    }
    return localMethod;
  }
  
  @CallerSensitive
  public Constructor<T> getConstructor(Class<?>... paramVarArgs)
    throws NoSuchMethodException, SecurityException
  {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    return getConstructor0(paramVarArgs, 0);
  }
  
  @CallerSensitive
  public Class<?>[] getDeclaredClasses()
    throws SecurityException
  {
    checkMemberAccess(1, Reflection.getCallerClass(), false);
    return getDeclaredClasses0();
  }
  
  @CallerSensitive
  public Field[] getDeclaredFields()
    throws SecurityException
  {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    return copyFields(privateGetDeclaredFields(false));
  }
  
  @CallerSensitive
  public Method[] getDeclaredMethods()
    throws SecurityException
  {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    return copyMethods(privateGetDeclaredMethods(false));
  }
  
  @CallerSensitive
  public Constructor<?>[] getDeclaredConstructors()
    throws SecurityException
  {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    return copyConstructors(privateGetDeclaredConstructors(false));
  }
  
  @CallerSensitive
  public Field getDeclaredField(String paramString)
    throws NoSuchFieldException, SecurityException
  {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    Field localField = searchFields(privateGetDeclaredFields(false), paramString);
    if (localField == null) {
      throw new NoSuchFieldException(paramString);
    }
    return localField;
  }
  
  @CallerSensitive
  public Method getDeclaredMethod(String paramString, Class<?>... paramVarArgs)
    throws NoSuchMethodException, SecurityException
  {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    Method localMethod = searchMethods(privateGetDeclaredMethods(false), paramString, paramVarArgs);
    if (localMethod == null) {
      throw new NoSuchMethodException(getName() + "." + paramString + argumentTypesToString(paramVarArgs));
    }
    return localMethod;
  }
  
  @CallerSensitive
  public Constructor<T> getDeclaredConstructor(Class<?>... paramVarArgs)
    throws NoSuchMethodException, SecurityException
  {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    return getConstructor0(paramVarArgs, 1);
  }
  
  public InputStream getResourceAsStream(String paramString)
  {
    paramString = resolveName(paramString);
    ClassLoader localClassLoader = getClassLoader0();
    if (localClassLoader == null) {
      return ClassLoader.getSystemResourceAsStream(paramString);
    }
    return localClassLoader.getResourceAsStream(paramString);
  }
  
  public URL getResource(String paramString)
  {
    paramString = resolveName(paramString);
    ClassLoader localClassLoader = getClassLoader0();
    if (localClassLoader == null) {
      return ClassLoader.getSystemResource(paramString);
    }
    return localClassLoader.getResource(paramString);
  }
  
  public ProtectionDomain getProtectionDomain()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.GET_PD_PERMISSION);
    }
    ProtectionDomain localProtectionDomain = getProtectionDomain0();
    if (localProtectionDomain == null)
    {
      if (allPermDomain == null)
      {
        Permissions localPermissions = new Permissions();
        localPermissions.add(SecurityConstants.ALL_PERMISSION);
        allPermDomain = new ProtectionDomain(null, localPermissions);
      }
      localProtectionDomain = allPermDomain;
    }
    return localProtectionDomain;
  }
  
  private native ProtectionDomain getProtectionDomain0();
  
  static native Class<?> getPrimitiveClass(String paramString);
  
  private void checkMemberAccess(int paramInt, Class<?> paramClass, boolean paramBoolean)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      ClassLoader localClassLoader1 = ClassLoader.getClassLoader(paramClass);
      ClassLoader localClassLoader2 = getClassLoader0();
      if ((paramInt != 0) && (localClassLoader1 != localClassLoader2)) {
        localSecurityManager.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
      }
      checkPackageAccess(localClassLoader1, paramBoolean);
    }
  }
  
  private void checkPackageAccess(ClassLoader paramClassLoader, boolean paramBoolean)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      ClassLoader localClassLoader = getClassLoader0();
      if (ReflectUtil.needsPackageAccessCheck(paramClassLoader, localClassLoader))
      {
        String str1 = getName();
        int i = str1.lastIndexOf('.');
        if (i != -1)
        {
          String str2 = str1.substring(0, i);
          if ((!Proxy.isProxyClass(this)) || (ReflectUtil.isNonPublicProxyClass(this))) {
            localSecurityManager.checkPackageAccess(str2);
          }
        }
      }
      if ((paramBoolean) && (Proxy.isProxyClass(this))) {
        ReflectUtil.checkProxyPackageAccess(paramClassLoader, getInterfaces());
      }
    }
  }
  
  private String resolveName(String paramString)
  {
    if (paramString == null) {
      return paramString;
    }
    if (!paramString.startsWith("/"))
    {
      for (Class localClass = this; localClass.isArray(); localClass = localClass.getComponentType()) {}
      String str = localClass.getName();
      int i = str.lastIndexOf('.');
      if (i != -1) {
        paramString = str.substring(0, i).replace('.', '/') + "/" + paramString;
      }
    }
    else
    {
      paramString = paramString.substring(1);
    }
    return paramString;
  }
  
  private ReflectionData<T> reflectionData()
  {
    SoftReference localSoftReference = reflectionData;
    int i = classRedefinedCount;
    ReflectionData localReflectionData;
    if ((useCaches) && (localSoftReference != null) && ((localReflectionData = (ReflectionData)localSoftReference.get()) != null) && (redefinedCount == i)) {
      return localReflectionData;
    }
    return newReflectionData(localSoftReference, i);
  }
  
  private ReflectionData<T> newReflectionData(SoftReference<ReflectionData<T>> paramSoftReference, int paramInt)
  {
    if (!useCaches) {
      return null;
    }
    for (;;)
    {
      ReflectionData localReflectionData = new ReflectionData(paramInt);
      if (Atomic.casReflectionData(this, paramSoftReference, new SoftReference(localReflectionData))) {
        return localReflectionData;
      }
      paramSoftReference = reflectionData;
      paramInt = classRedefinedCount;
      if ((paramSoftReference != null) && ((localReflectionData = (ReflectionData)paramSoftReference.get()) != null) && (redefinedCount == paramInt)) {
        return localReflectionData;
      }
    }
  }
  
  private native String getGenericSignature0();
  
  private GenericsFactory getFactory()
  {
    return CoreReflectionFactory.make(this, ClassScope.make(this));
  }
  
  private ClassRepository getGenericInfo()
  {
    ClassRepository localClassRepository = genericInfo;
    if (localClassRepository == null)
    {
      String str = getGenericSignature0();
      if (str == null) {
        localClassRepository = ClassRepository.NONE;
      } else {
        localClassRepository = ClassRepository.make(str, getFactory());
      }
      genericInfo = localClassRepository;
    }
    return localClassRepository != ClassRepository.NONE ? localClassRepository : null;
  }
  
  native byte[] getRawAnnotations();
  
  native byte[] getRawTypeAnnotations();
  
  static byte[] getExecutableTypeAnnotationBytes(Executable paramExecutable)
  {
    return getReflectionFactory().getExecutableTypeAnnotationBytes(paramExecutable);
  }
  
  native ConstantPool getConstantPool();
  
  private Field[] privateGetDeclaredFields(boolean paramBoolean)
  {
    checkInitted();
    ReflectionData localReflectionData = reflectionData();
    if (localReflectionData != null)
    {
      arrayOfField = paramBoolean ? declaredPublicFields : declaredFields;
      if (arrayOfField != null) {
        return arrayOfField;
      }
    }
    Field[] arrayOfField = Reflection.filterFields(this, getDeclaredFields0(paramBoolean));
    if (localReflectionData != null) {
      if (paramBoolean) {
        declaredPublicFields = arrayOfField;
      } else {
        declaredFields = arrayOfField;
      }
    }
    return arrayOfField;
  }
  
  private Field[] privateGetPublicFields(Set<Class<?>> paramSet)
  {
    checkInitted();
    ReflectionData localReflectionData = reflectionData();
    if (localReflectionData != null)
    {
      arrayOfField1 = publicFields;
      if (arrayOfField1 != null) {
        return arrayOfField1;
      }
    }
    ArrayList localArrayList = new ArrayList();
    if (paramSet == null) {
      paramSet = new HashSet();
    }
    Field[] arrayOfField2 = privateGetDeclaredFields(true);
    addAll(localArrayList, arrayOfField2);
    for (Object localObject2 : getInterfaces()) {
      if (!paramSet.contains(localObject2))
      {
        paramSet.add(localObject2);
        addAll(localArrayList, ((Class)localObject2).privateGetPublicFields(paramSet));
      }
    }
    if (!isInterface())
    {
      ??? = getSuperclass();
      if (??? != null) {
        addAll(localArrayList, ((Class)???).privateGetPublicFields(paramSet));
      }
    }
    Field[] arrayOfField1 = new Field[localArrayList.size()];
    localArrayList.toArray(arrayOfField1);
    if (localReflectionData != null) {
      publicFields = arrayOfField1;
    }
    return arrayOfField1;
  }
  
  private static void addAll(Collection<Field> paramCollection, Field[] paramArrayOfField)
  {
    for (int i = 0; i < paramArrayOfField.length; i++) {
      paramCollection.add(paramArrayOfField[i]);
    }
  }
  
  private Constructor<T>[] privateGetDeclaredConstructors(boolean paramBoolean)
  {
    checkInitted();
    ReflectionData localReflectionData = reflectionData();
    Object localObject;
    if (localReflectionData != null)
    {
      localObject = paramBoolean ? publicConstructors : declaredConstructors;
      if (localObject != null) {
        return (Constructor<T>[])localObject;
      }
    }
    if (isInterface())
    {
      Constructor[] arrayOfConstructor = (Constructor[])new Constructor[0];
      localObject = arrayOfConstructor;
    }
    else
    {
      localObject = getDeclaredConstructors0(paramBoolean);
    }
    if (localReflectionData != null) {
      if (paramBoolean) {
        publicConstructors = ((Constructor[])localObject);
      } else {
        declaredConstructors = ((Constructor[])localObject);
      }
    }
    return (Constructor<T>[])localObject;
  }
  
  private Method[] privateGetDeclaredMethods(boolean paramBoolean)
  {
    checkInitted();
    ReflectionData localReflectionData = reflectionData();
    if (localReflectionData != null)
    {
      arrayOfMethod = paramBoolean ? declaredPublicMethods : declaredMethods;
      if (arrayOfMethod != null) {
        return arrayOfMethod;
      }
    }
    Method[] arrayOfMethod = Reflection.filterMethods(this, getDeclaredMethods0(paramBoolean));
    if (localReflectionData != null) {
      if (paramBoolean) {
        declaredPublicMethods = arrayOfMethod;
      } else {
        declaredMethods = arrayOfMethod;
      }
    }
    return arrayOfMethod;
  }
  
  private Method[] privateGetPublicMethods()
  {
    checkInitted();
    ReflectionData localReflectionData = reflectionData();
    if (localReflectionData != null)
    {
      arrayOfMethod = publicMethods;
      if (arrayOfMethod != null) {
        return arrayOfMethod;
      }
    }
    MethodArray localMethodArray = new MethodArray();
    Object localObject1 = privateGetDeclaredMethods(true);
    localMethodArray.addAll((Method[])localObject1);
    localObject1 = new MethodArray();
    Method localMethod;
    for (localMethod : getInterfaces()) {
      ((MethodArray)localObject1).addInterfaceMethods(localMethod.privateGetPublicMethods());
    }
    Object localObject3;
    if (!isInterface())
    {
      ??? = getSuperclass();
      if (??? != null)
      {
        localObject3 = new MethodArray();
        ((MethodArray)localObject3).addAll(((Class)???).privateGetPublicMethods());
        for (??? = 0; ??? < ((MethodArray)localObject3).length(); ???++)
        {
          localMethod = ((MethodArray)localObject3).get(???);
          if ((localMethod != null) && (!Modifier.isAbstract(localMethod.getModifiers())) && (!localMethod.isDefault())) {
            ((MethodArray)localObject1).removeByNameAndDescriptor(localMethod);
          }
        }
        ((MethodArray)localObject3).addAll((MethodArray)localObject1);
        localObject1 = localObject3;
      }
    }
    for (int i = 0; i < localMethodArray.length(); i++)
    {
      localObject3 = localMethodArray.get(i);
      ((MethodArray)localObject1).removeByNameAndDescriptor((Method)localObject3);
    }
    localMethodArray.addAllIfNotPresent((MethodArray)localObject1);
    localMethodArray.removeLessSpecifics();
    localMethodArray.compactAndTrim();
    Method[] arrayOfMethod = localMethodArray.getArray();
    if (localReflectionData != null) {
      publicMethods = arrayOfMethod;
    }
    return arrayOfMethod;
  }
  
  private static Field searchFields(Field[] paramArrayOfField, String paramString)
  {
    String str = paramString.intern();
    for (int i = 0; i < paramArrayOfField.length; i++) {
      if (paramArrayOfField[i].getName() == str) {
        return getReflectionFactory().copyField(paramArrayOfField[i]);
      }
    }
    return null;
  }
  
  private Field getField0(String paramString)
    throws NoSuchFieldException
  {
    Field localField;
    if ((localField = searchFields(privateGetDeclaredFields(true), paramString)) != null) {
      return localField;
    }
    Class[] arrayOfClass = getInterfaces();
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      Class localClass2 = arrayOfClass[i];
      if ((localField = localClass2.getField0(paramString)) != null) {
        return localField;
      }
    }
    if (!isInterface())
    {
      Class localClass1 = getSuperclass();
      if ((localClass1 != null) && ((localField = localClass1.getField0(paramString)) != null)) {
        return localField;
      }
    }
    return null;
  }
  
  private static Method searchMethods(Method[] paramArrayOfMethod, String paramString, Class<?>[] paramArrayOfClass)
  {
    Object localObject = null;
    String str = paramString.intern();
    for (int i = 0; i < paramArrayOfMethod.length; i++)
    {
      Method localMethod = paramArrayOfMethod[i];
      if ((localMethod.getName() == str) && (arrayContentsEq(paramArrayOfClass, localMethod.getParameterTypes())) && ((localObject == null) || (((Method)localObject).getReturnType().isAssignableFrom(localMethod.getReturnType())))) {
        localObject = localMethod;
      }
    }
    return (Method)(localObject == null ? localObject : getReflectionFactory().copyMethod((Method)localObject));
  }
  
  private Method getMethod0(String paramString, Class<?>[] paramArrayOfClass, boolean paramBoolean)
  {
    MethodArray localMethodArray = new MethodArray(2);
    Method localMethod = privateGetMethodRecursive(paramString, paramArrayOfClass, paramBoolean, localMethodArray);
    if (localMethod != null) {
      return localMethod;
    }
    localMethodArray.removeLessSpecifics();
    return localMethodArray.getFirst();
  }
  
  private Method privateGetMethodRecursive(String paramString, Class<?>[] paramArrayOfClass, boolean paramBoolean, MethodArray paramMethodArray)
  {
    Method localMethod;
    if (((localMethod = searchMethods(privateGetDeclaredMethods(true), paramString, paramArrayOfClass)) != null) && ((paramBoolean) || (!Modifier.isStatic(localMethod.getModifiers())))) {
      return localMethod;
    }
    if (!isInterface())
    {
      localObject1 = getSuperclass();
      if ((localObject1 != null) && ((localMethod = ((Class)localObject1).getMethod0(paramString, paramArrayOfClass, true)) != null)) {
        return localMethod;
      }
    }
    Object localObject1 = getInterfaces();
    for (Object localObject3 : localObject1) {
      if ((localMethod = ((Class)localObject3).getMethod0(paramString, paramArrayOfClass, false)) != null) {
        paramMethodArray.add(localMethod);
      }
    }
    return null;
  }
  
  private Constructor<T> getConstructor0(Class<?>[] paramArrayOfClass, int paramInt)
    throws NoSuchMethodException
  {
    Constructor[] arrayOfConstructor1 = privateGetDeclaredConstructors(paramInt == 0);
    for (Constructor localConstructor : arrayOfConstructor1) {
      if (arrayContentsEq(paramArrayOfClass, localConstructor.getParameterTypes())) {
        return getReflectionFactory().copyConstructor(localConstructor);
      }
    }
    throw new NoSuchMethodException(getName() + ".<init>" + argumentTypesToString(paramArrayOfClass));
  }
  
  private static boolean arrayContentsEq(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2)
  {
    if (paramArrayOfObject1 == null) {
      return (paramArrayOfObject2 == null) || (paramArrayOfObject2.length == 0);
    }
    if (paramArrayOfObject2 == null) {
      return paramArrayOfObject1.length == 0;
    }
    if (paramArrayOfObject1.length != paramArrayOfObject2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfObject1.length; i++) {
      if (paramArrayOfObject1[i] != paramArrayOfObject2[i]) {
        return false;
      }
    }
    return true;
  }
  
  private static Field[] copyFields(Field[] paramArrayOfField)
  {
    Field[] arrayOfField = new Field[paramArrayOfField.length];
    ReflectionFactory localReflectionFactory = getReflectionFactory();
    for (int i = 0; i < paramArrayOfField.length; i++) {
      arrayOfField[i] = localReflectionFactory.copyField(paramArrayOfField[i]);
    }
    return arrayOfField;
  }
  
  private static Method[] copyMethods(Method[] paramArrayOfMethod)
  {
    Method[] arrayOfMethod = new Method[paramArrayOfMethod.length];
    ReflectionFactory localReflectionFactory = getReflectionFactory();
    for (int i = 0; i < paramArrayOfMethod.length; i++) {
      arrayOfMethod[i] = localReflectionFactory.copyMethod(paramArrayOfMethod[i]);
    }
    return arrayOfMethod;
  }
  
  private static <U> Constructor<U>[] copyConstructors(Constructor<U>[] paramArrayOfConstructor)
  {
    Constructor[] arrayOfConstructor = (Constructor[])paramArrayOfConstructor.clone();
    ReflectionFactory localReflectionFactory = getReflectionFactory();
    for (int i = 0; i < arrayOfConstructor.length; i++) {
      arrayOfConstructor[i] = localReflectionFactory.copyConstructor(arrayOfConstructor[i]);
    }
    return arrayOfConstructor;
  }
  
  private native Field[] getDeclaredFields0(boolean paramBoolean);
  
  private native Method[] getDeclaredMethods0(boolean paramBoolean);
  
  private native Constructor<T>[] getDeclaredConstructors0(boolean paramBoolean);
  
  private native Class<?>[] getDeclaredClasses0();
  
  private static String argumentTypesToString(Class<?>[] paramArrayOfClass)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("(");
    if (paramArrayOfClass != null) {
      for (int i = 0; i < paramArrayOfClass.length; i++)
      {
        if (i > 0) {
          localStringBuilder.append(", ");
        }
        Class<?> localClass = paramArrayOfClass[i];
        localStringBuilder.append(localClass == null ? "null" : localClass.getName());
      }
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public boolean desiredAssertionStatus()
  {
    ClassLoader localClassLoader = getClassLoader();
    if (localClassLoader == null) {
      return desiredAssertionStatus0(this);
    }
    synchronized (assertionLock)
    {
      if (classAssertionStatus != null) {
        return localClassLoader.desiredAssertionStatus(getName());
      }
    }
    return desiredAssertionStatus0(this);
  }
  
  private static native boolean desiredAssertionStatus0(Class<?> paramClass);
  
  public boolean isEnum()
  {
    return ((getModifiers() & 0x4000) != 0) && (getSuperclass() == Enum.class);
  }
  
  private static ReflectionFactory getReflectionFactory()
  {
    if (reflectionFactory == null) {
      reflectionFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
    }
    return reflectionFactory;
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
        String str = System.getProperty("sun.reflect.noCaches");
        if ((str != null) && (str.equals("true"))) {
          Class.access$402(false);
        }
        Class.access$502(true);
        return null;
      }
    });
  }
  
  public T[] getEnumConstants()
  {
    Object[] arrayOfObject = getEnumConstantsShared();
    return arrayOfObject != null ? (Object[])arrayOfObject.clone() : null;
  }
  
  T[] getEnumConstantsShared()
  {
    if (enumConstants == null)
    {
      if (!isEnum()) {
        return null;
      }
      try
      {
        final Method localMethod = getMethod("values", new Class[0]);
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            localMethod.setAccessible(true);
            return null;
          }
        });
        Object[] arrayOfObject = (Object[])localMethod.invoke(null, new Object[0]);
        enumConstants = arrayOfObject;
      }
      catch (InvocationTargetException|NoSuchMethodException|IllegalAccessException localInvocationTargetException)
      {
        return null;
      }
    }
    return enumConstants;
  }
  
  Map<String, T> enumConstantDirectory()
  {
    if (enumConstantDirectory == null)
    {
      Object[] arrayOfObject1 = getEnumConstantsShared();
      if (arrayOfObject1 == null) {
        throw new IllegalArgumentException(getName() + " is not an enum type");
      }
      HashMap localHashMap = new HashMap(2 * arrayOfObject1.length);
      for (Object localObject : arrayOfObject1) {
        localHashMap.put(((Enum)localObject).name(), localObject);
      }
      enumConstantDirectory = localHashMap;
    }
    return enumConstantDirectory;
  }
  
  public T cast(Object paramObject)
  {
    if ((paramObject != null) && (!isInstance(paramObject))) {
      throw new ClassCastException(cannotCastMsg(paramObject));
    }
    return (T)paramObject;
  }
  
  private String cannotCastMsg(Object paramObject)
  {
    return "Cannot cast " + paramObject.getClass().getName() + " to " + getName();
  }
  
  public <U> Class<? extends U> asSubclass(Class<U> paramClass)
  {
    if (paramClass.isAssignableFrom(this)) {
      return this;
    }
    throw new ClassCastException(toString());
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return (Annotation)annotationDataannotations.get(paramClass);
  }
  
  public boolean isAnnotationPresent(Class<? extends Annotation> paramClass)
  {
    return super.isAnnotationPresent(paramClass);
  }
  
  public <A extends Annotation> A[] getAnnotationsByType(Class<A> paramClass)
  {
    Objects.requireNonNull(paramClass);
    AnnotationData localAnnotationData = annotationData();
    return AnnotationSupport.getAssociatedAnnotations(declaredAnnotations, this, paramClass);
  }
  
  public Annotation[] getAnnotations()
  {
    return AnnotationParser.toArray(annotationDataannotations);
  }
  
  public <A extends Annotation> A getDeclaredAnnotation(Class<A> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return (Annotation)annotationDatadeclaredAnnotations.get(paramClass);
  }
  
  public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return AnnotationSupport.getDirectlyAndIndirectlyPresent(annotationDatadeclaredAnnotations, paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return AnnotationParser.toArray(annotationDatadeclaredAnnotations);
  }
  
  private AnnotationData annotationData()
  {
    for (;;)
    {
      AnnotationData localAnnotationData1 = annotationData;
      int i = classRedefinedCount;
      if ((localAnnotationData1 != null) && (redefinedCount == i)) {
        return localAnnotationData1;
      }
      AnnotationData localAnnotationData2 = createAnnotationData(i);
      if (Atomic.casAnnotationData(this, localAnnotationData1, localAnnotationData2)) {
        return localAnnotationData2;
      }
    }
  }
  
  private AnnotationData createAnnotationData(int paramInt)
  {
    Map localMap1 = AnnotationParser.parseAnnotations(getRawAnnotations(), getConstantPool(), this);
    Class localClass1 = getSuperclass();
    Object localObject = null;
    if (localClass1 != null)
    {
      Map localMap2 = annotationDataannotations;
      Iterator localIterator = localMap2.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Class localClass2 = (Class)localEntry.getKey();
        if (AnnotationType.getInstance(localClass2).isInherited())
        {
          if (localObject == null) {
            localObject = new LinkedHashMap((Math.max(localMap1.size(), Math.min(12, localMap1.size() + localMap2.size())) * 4 + 2) / 3);
          }
          ((Map)localObject).put(localClass2, localEntry.getValue());
        }
      }
    }
    if (localObject == null) {
      localObject = localMap1;
    } else {
      ((Map)localObject).putAll(localMap1);
    }
    return new AnnotationData((Map)localObject, localMap1, paramInt);
  }
  
  boolean casAnnotationType(AnnotationType paramAnnotationType1, AnnotationType paramAnnotationType2)
  {
    return Atomic.casAnnotationType(this, paramAnnotationType1, paramAnnotationType2);
  }
  
  AnnotationType getAnnotationType()
  {
    return annotationType;
  }
  
  Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotationMap()
  {
    return annotationDatadeclaredAnnotations;
  }
  
  public AnnotatedType getAnnotatedSuperclass()
  {
    if ((this == Object.class) || (isInterface()) || (isArray()) || (isPrimitive()) || (this == Void.TYPE)) {
      return null;
    }
    return TypeAnnotationParser.buildAnnotatedSuperclass(getRawTypeAnnotations(), getConstantPool(), this);
  }
  
  public AnnotatedType[] getAnnotatedInterfaces()
  {
    return TypeAnnotationParser.buildAnnotatedInterfaces(getRawTypeAnnotations(), getConstantPool(), this);
  }
  
  static {}
  
  private static class AnnotationData
  {
    final Map<Class<? extends Annotation>, Annotation> annotations;
    final Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
    final int redefinedCount;
    
    AnnotationData(Map<Class<? extends Annotation>, Annotation> paramMap1, Map<Class<? extends Annotation>, Annotation> paramMap2, int paramInt)
    {
      annotations = paramMap1;
      declaredAnnotations = paramMap2;
      redefinedCount = paramInt;
    }
  }
  
  private static class Atomic
  {
    private static final Unsafe unsafe = ;
    private static final long reflectionDataOffset;
    private static final long annotationTypeOffset;
    private static final long annotationDataOffset;
    
    private Atomic() {}
    
    private static long objectFieldOffset(Field[] paramArrayOfField, String paramString)
    {
      Field localField = Class.searchFields(paramArrayOfField, paramString);
      if (localField == null) {
        throw new Error("No " + paramString + " field found in java.lang.Class");
      }
      return unsafe.objectFieldOffset(localField);
    }
    
    static <T> boolean casReflectionData(Class<?> paramClass, SoftReference<Class.ReflectionData<T>> paramSoftReference1, SoftReference<Class.ReflectionData<T>> paramSoftReference2)
    {
      return unsafe.compareAndSwapObject(paramClass, reflectionDataOffset, paramSoftReference1, paramSoftReference2);
    }
    
    static <T> boolean casAnnotationType(Class<?> paramClass, AnnotationType paramAnnotationType1, AnnotationType paramAnnotationType2)
    {
      return unsafe.compareAndSwapObject(paramClass, annotationTypeOffset, paramAnnotationType1, paramAnnotationType2);
    }
    
    static <T> boolean casAnnotationData(Class<?> paramClass, Class.AnnotationData paramAnnotationData1, Class.AnnotationData paramAnnotationData2)
    {
      return unsafe.compareAndSwapObject(paramClass, annotationDataOffset, paramAnnotationData1, paramAnnotationData2);
    }
    
    static
    {
      Field[] arrayOfField = Class.class.getDeclaredFields0(false);
      reflectionDataOffset = objectFieldOffset(arrayOfField, "reflectionData");
      annotationTypeOffset = objectFieldOffset(arrayOfField, "annotationType");
      annotationDataOffset = objectFieldOffset(arrayOfField, "annotationData");
    }
  }
  
  private static final class EnclosingMethodInfo
  {
    private Class<?> enclosingClass;
    private String name;
    private String descriptor;
    
    private EnclosingMethodInfo(Object[] paramArrayOfObject)
    {
      if (paramArrayOfObject.length != 3) {
        throw new InternalError("Malformed enclosing method information");
      }
      try
      {
        enclosingClass = ((Class)paramArrayOfObject[0]);
        assert (enclosingClass != null);
        name = ((String)paramArrayOfObject[1]);
        descriptor = ((String)paramArrayOfObject[2]);
        if ((!$assertionsDisabled) && ((name == null) || (descriptor == null)) && (name != descriptor)) {
          throw new AssertionError();
        }
      }
      catch (ClassCastException localClassCastException)
      {
        throw new InternalError("Invalid type in enclosing method information", localClassCastException);
      }
    }
    
    boolean isPartial()
    {
      return (enclosingClass == null) || (name == null) || (descriptor == null);
    }
    
    boolean isConstructor()
    {
      return (!isPartial()) && ("<init>".equals(name));
    }
    
    boolean isMethod()
    {
      return (!isPartial()) && (!isConstructor()) && (!"<clinit>".equals(name));
    }
    
    Class<?> getEnclosingClass()
    {
      return enclosingClass;
    }
    
    String getName()
    {
      return name;
    }
    
    String getDescriptor()
    {
      return descriptor;
    }
  }
  
  static class MethodArray
  {
    private Method[] methods;
    private int length;
    private int defaults;
    
    MethodArray()
    {
      this(20);
    }
    
    MethodArray(int paramInt)
    {
      if (paramInt < 2) {
        throw new IllegalArgumentException("Size should be 2 or more");
      }
      methods = new Method[paramInt];
      length = 0;
      defaults = 0;
    }
    
    boolean hasDefaults()
    {
      return defaults != 0;
    }
    
    void add(Method paramMethod)
    {
      if (length == methods.length) {
        methods = ((Method[])Arrays.copyOf(methods, 2 * methods.length));
      }
      methods[(length++)] = paramMethod;
      if ((paramMethod != null) && (paramMethod.isDefault())) {
        defaults += 1;
      }
    }
    
    void addAll(Method[] paramArrayOfMethod)
    {
      for (int i = 0; i < paramArrayOfMethod.length; i++) {
        add(paramArrayOfMethod[i]);
      }
    }
    
    void addAll(MethodArray paramMethodArray)
    {
      for (int i = 0; i < paramMethodArray.length(); i++) {
        add(paramMethodArray.get(i));
      }
    }
    
    void addIfNotPresent(Method paramMethod)
    {
      for (int i = 0; i < length; i++)
      {
        Method localMethod = methods[i];
        if ((localMethod == paramMethod) || ((localMethod != null) && (localMethod.equals(paramMethod)))) {
          return;
        }
      }
      add(paramMethod);
    }
    
    void addAllIfNotPresent(MethodArray paramMethodArray)
    {
      for (int i = 0; i < paramMethodArray.length(); i++)
      {
        Method localMethod = paramMethodArray.get(i);
        if (localMethod != null) {
          addIfNotPresent(localMethod);
        }
      }
    }
    
    void addInterfaceMethods(Method[] paramArrayOfMethod)
    {
      for (Method localMethod : paramArrayOfMethod) {
        if (!Modifier.isStatic(localMethod.getModifiers())) {
          add(localMethod);
        }
      }
    }
    
    int length()
    {
      return length;
    }
    
    Method get(int paramInt)
    {
      return methods[paramInt];
    }
    
    Method getFirst()
    {
      for (Method localMethod : methods) {
        if (localMethod != null) {
          return localMethod;
        }
      }
      return null;
    }
    
    void removeByNameAndDescriptor(Method paramMethod)
    {
      for (int i = 0; i < length; i++)
      {
        Method localMethod = methods[i];
        if ((localMethod != null) && (matchesNameAndDescriptor(localMethod, paramMethod))) {
          remove(i);
        }
      }
    }
    
    private void remove(int paramInt)
    {
      if ((methods[paramInt] != null) && (methods[paramInt].isDefault())) {
        defaults -= 1;
      }
      methods[paramInt] = null;
    }
    
    private boolean matchesNameAndDescriptor(Method paramMethod1, Method paramMethod2)
    {
      return (paramMethod1.getReturnType() == paramMethod2.getReturnType()) && (paramMethod1.getName() == paramMethod2.getName()) && (Class.arrayContentsEq(paramMethod1.getParameterTypes(), paramMethod2.getParameterTypes()));
    }
    
    void compactAndTrim()
    {
      int i = 0;
      for (int j = 0; j < length; j++)
      {
        Method localMethod = methods[j];
        if (localMethod != null)
        {
          if (j != i) {
            methods[i] = localMethod;
          }
          i++;
        }
      }
      if (i != methods.length) {
        methods = ((Method[])Arrays.copyOf(methods, i));
      }
    }
    
    void removeLessSpecifics()
    {
      if (!hasDefaults()) {
        return;
      }
      for (int i = 0; i < length; i++)
      {
        Method localMethod1 = get(i);
        if ((localMethod1 != null) && (localMethod1.isDefault())) {
          for (int j = 0; j < length; j++) {
            if (i != j)
            {
              Method localMethod2 = get(j);
              if ((localMethod2 != null) && (matchesNameAndDescriptor(localMethod1, localMethod2)) && (hasMoreSpecificClass(localMethod1, localMethod2))) {
                remove(j);
              }
            }
          }
        }
      }
    }
    
    Method[] getArray()
    {
      return methods;
    }
    
    static boolean hasMoreSpecificClass(Method paramMethod1, Method paramMethod2)
    {
      Class localClass1 = paramMethod1.getDeclaringClass();
      Class localClass2 = paramMethod2.getDeclaringClass();
      return (localClass1 != localClass2) && (localClass2.isAssignableFrom(localClass1));
    }
  }
  
  private static class ReflectionData<T>
  {
    volatile Field[] declaredFields;
    volatile Field[] publicFields;
    volatile Method[] declaredMethods;
    volatile Method[] publicMethods;
    volatile Constructor<T>[] declaredConstructors;
    volatile Constructor<T>[] publicConstructors;
    volatile Field[] declaredPublicFields;
    volatile Method[] declaredPublicMethods;
    volatile Class<?>[] interfaces;
    final int redefinedCount;
    
    ReflectionData(int paramInt)
    {
      redefinedCount = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Class.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */