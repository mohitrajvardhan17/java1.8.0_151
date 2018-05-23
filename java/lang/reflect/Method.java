package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.nio.ByteBuffer;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.MethodAccessor;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.ExceptionProxy;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.scope.MethodScope;

public final class Method
  extends Executable
{
  private Class<?> clazz;
  private int slot;
  private String name;
  private Class<?> returnType;
  private Class<?>[] parameterTypes;
  private Class<?>[] exceptionTypes;
  private int modifiers;
  private transient String signature;
  private transient MethodRepository genericInfo;
  private byte[] annotations;
  private byte[] parameterAnnotations;
  private byte[] annotationDefault;
  private volatile MethodAccessor methodAccessor;
  private Method root;
  
  private String getGenericSignature()
  {
    return signature;
  }
  
  private GenericsFactory getFactory()
  {
    return CoreReflectionFactory.make(this, MethodScope.make(this));
  }
  
  MethodRepository getGenericInfo()
  {
    if (genericInfo == null) {
      genericInfo = MethodRepository.make(getGenericSignature(), getFactory());
    }
    return genericInfo;
  }
  
  Method(Class<?> paramClass1, String paramString1, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    clazz = paramClass1;
    name = paramString1;
    parameterTypes = paramArrayOfClass1;
    returnType = paramClass2;
    exceptionTypes = paramArrayOfClass2;
    modifiers = paramInt1;
    slot = paramInt2;
    signature = paramString2;
    annotations = paramArrayOfByte1;
    parameterAnnotations = paramArrayOfByte2;
    annotationDefault = paramArrayOfByte3;
  }
  
  Method copy()
  {
    if (root != null) {
      throw new IllegalArgumentException("Can not copy a non-root Method");
    }
    Method localMethod = new Method(clazz, name, parameterTypes, returnType, exceptionTypes, modifiers, slot, signature, annotations, parameterAnnotations, annotationDefault);
    root = this;
    methodAccessor = methodAccessor;
    return localMethod;
  }
  
  Executable getRoot()
  {
    return root;
  }
  
  boolean hasGenericInformation()
  {
    return getGenericSignature() != null;
  }
  
  byte[] getAnnotationBytes()
  {
    return annotations;
  }
  
  public Class<?> getDeclaringClass()
  {
    return clazz;
  }
  
  public String getName()
  {
    return name;
  }
  
  public int getModifiers()
  {
    return modifiers;
  }
  
  public TypeVariable<Method>[] getTypeParameters()
  {
    if (getGenericSignature() != null) {
      return (TypeVariable[])getGenericInfo().getTypeParameters();
    }
    return (TypeVariable[])new TypeVariable[0];
  }
  
  public Class<?> getReturnType()
  {
    return returnType;
  }
  
  public Type getGenericReturnType()
  {
    if (getGenericSignature() != null) {
      return getGenericInfo().getReturnType();
    }
    return getReturnType();
  }
  
  public Class<?>[] getParameterTypes()
  {
    return (Class[])parameterTypes.clone();
  }
  
  public int getParameterCount()
  {
    return parameterTypes.length;
  }
  
  public Type[] getGenericParameterTypes()
  {
    return super.getGenericParameterTypes();
  }
  
  public Class<?>[] getExceptionTypes()
  {
    return (Class[])exceptionTypes.clone();
  }
  
  public Type[] getGenericExceptionTypes()
  {
    return super.getGenericExceptionTypes();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Method)))
    {
      Method localMethod = (Method)paramObject;
      if ((getDeclaringClass() == localMethod.getDeclaringClass()) && (getName() == localMethod.getName()))
      {
        if (!returnType.equals(localMethod.getReturnType())) {
          return false;
        }
        return equalParamTypes(parameterTypes, parameterTypes);
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
  }
  
  public String toString()
  {
    return sharedToString(Modifier.methodModifiers(), isDefault(), parameterTypes, exceptionTypes);
  }
  
  void specificToStringHeader(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(getReturnType().getTypeName()).append(' ');
    paramStringBuilder.append(getDeclaringClass().getTypeName()).append('.');
    paramStringBuilder.append(getName());
  }
  
  public String toGenericString()
  {
    return sharedToGenericString(Modifier.methodModifiers(), isDefault());
  }
  
  void specificToGenericStringHeader(StringBuilder paramStringBuilder)
  {
    Type localType = getGenericReturnType();
    paramStringBuilder.append(localType.getTypeName()).append(' ');
    paramStringBuilder.append(getDeclaringClass().getTypeName()).append('.');
    paramStringBuilder.append(getName());
  }
  
  @CallerSensitive
  public Object invoke(Object paramObject, Object... paramVarArgs)
    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      localObject = Reflection.getCallerClass();
      checkAccess((Class)localObject, clazz, paramObject, modifiers);
    }
    Object localObject = methodAccessor;
    if (localObject == null) {
      localObject = acquireMethodAccessor();
    }
    return ((MethodAccessor)localObject).invoke(paramObject, paramVarArgs);
  }
  
  public boolean isBridge()
  {
    return (getModifiers() & 0x40) != 0;
  }
  
  public boolean isVarArgs()
  {
    return super.isVarArgs();
  }
  
  public boolean isSynthetic()
  {
    return super.isSynthetic();
  }
  
  public boolean isDefault()
  {
    return ((getModifiers() & 0x409) == 1) && (getDeclaringClass().isInterface());
  }
  
  private MethodAccessor acquireMethodAccessor()
  {
    MethodAccessor localMethodAccessor = null;
    if (root != null) {
      localMethodAccessor = root.getMethodAccessor();
    }
    if (localMethodAccessor != null)
    {
      methodAccessor = localMethodAccessor;
    }
    else
    {
      localMethodAccessor = reflectionFactory.newMethodAccessor(this);
      setMethodAccessor(localMethodAccessor);
    }
    return localMethodAccessor;
  }
  
  MethodAccessor getMethodAccessor()
  {
    return methodAccessor;
  }
  
  void setMethodAccessor(MethodAccessor paramMethodAccessor)
  {
    methodAccessor = paramMethodAccessor;
    if (root != null) {
      root.setMethodAccessor(paramMethodAccessor);
    }
  }
  
  public Object getDefaultValue()
  {
    if (annotationDefault == null) {
      return null;
    }
    Class localClass = AnnotationType.invocationHandlerReturnType(getReturnType());
    Object localObject = AnnotationParser.parseMemberValue(localClass, ByteBuffer.wrap(annotationDefault), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
    if ((localObject instanceof ExceptionProxy)) {
      throw new AnnotationFormatError("Invalid default: " + this);
    }
    return localObject;
  }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass)
  {
    return super.getAnnotation(paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return super.getDeclaredAnnotations();
  }
  
  public Annotation[][] getParameterAnnotations()
  {
    return sharedGetParameterAnnotations(parameterTypes, parameterAnnotations);
  }
  
  public AnnotatedType getAnnotatedReturnType()
  {
    return getAnnotatedReturnType0(getGenericReturnType());
  }
  
  void handleParameterNumberMismatch(int paramInt1, int paramInt2)
  {
    throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\Method.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */