package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.ConstructorAccessor;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.annotation.TypeAnnotation.TypeAnnotationTarget;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.scope.ConstructorScope;

public final class Constructor<T>
  extends Executable
{
  private Class<T> clazz;
  private int slot;
  private Class<?>[] parameterTypes;
  private Class<?>[] exceptionTypes;
  private int modifiers;
  private transient String signature;
  private transient ConstructorRepository genericInfo;
  private byte[] annotations;
  private byte[] parameterAnnotations;
  private volatile ConstructorAccessor constructorAccessor;
  private Constructor<T> root;
  
  private GenericsFactory getFactory()
  {
    return CoreReflectionFactory.make(this, ConstructorScope.make(this));
  }
  
  ConstructorRepository getGenericInfo()
  {
    if (genericInfo == null) {
      genericInfo = ConstructorRepository.make(getSignature(), getFactory());
    }
    return genericInfo;
  }
  
  Executable getRoot()
  {
    return root;
  }
  
  Constructor(Class<T> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    clazz = paramClass;
    parameterTypes = paramArrayOfClass1;
    exceptionTypes = paramArrayOfClass2;
    modifiers = paramInt1;
    slot = paramInt2;
    signature = paramString;
    annotations = paramArrayOfByte1;
    parameterAnnotations = paramArrayOfByte2;
  }
  
  Constructor<T> copy()
  {
    if (root != null) {
      throw new IllegalArgumentException("Can not copy a non-root Constructor");
    }
    Constructor localConstructor = new Constructor(clazz, parameterTypes, exceptionTypes, modifiers, slot, signature, annotations, parameterAnnotations);
    root = this;
    constructorAccessor = constructorAccessor;
    return localConstructor;
  }
  
  boolean hasGenericInformation()
  {
    return getSignature() != null;
  }
  
  byte[] getAnnotationBytes()
  {
    return annotations;
  }
  
  public Class<T> getDeclaringClass()
  {
    return clazz;
  }
  
  public String getName()
  {
    return getDeclaringClass().getName();
  }
  
  public int getModifiers()
  {
    return modifiers;
  }
  
  public TypeVariable<Constructor<T>>[] getTypeParameters()
  {
    if (getSignature() != null) {
      return (TypeVariable[])getGenericInfo().getTypeParameters();
    }
    return (TypeVariable[])new TypeVariable[0];
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
    if ((paramObject != null) && ((paramObject instanceof Constructor)))
    {
      Constructor localConstructor = (Constructor)paramObject;
      if (getDeclaringClass() == localConstructor.getDeclaringClass()) {
        return equalParamTypes(parameterTypes, parameterTypes);
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return getDeclaringClass().getName().hashCode();
  }
  
  public String toString()
  {
    return sharedToString(Modifier.constructorModifiers(), false, parameterTypes, exceptionTypes);
  }
  
  void specificToStringHeader(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(getDeclaringClass().getTypeName());
  }
  
  public String toGenericString()
  {
    return sharedToGenericString(Modifier.constructorModifiers(), false);
  }
  
  void specificToGenericStringHeader(StringBuilder paramStringBuilder)
  {
    specificToStringHeader(paramStringBuilder);
  }
  
  @CallerSensitive
  public T newInstance(Object... paramVarArgs)
    throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      localObject1 = Reflection.getCallerClass();
      checkAccess((Class)localObject1, clazz, null, modifiers);
    }
    if ((clazz.getModifiers() & 0x4000) != 0) {
      throw new IllegalArgumentException("Cannot reflectively create enum objects");
    }
    Object localObject1 = constructorAccessor;
    if (localObject1 == null) {
      localObject1 = acquireConstructorAccessor();
    }
    Object localObject2 = ((ConstructorAccessor)localObject1).newInstance(paramVarArgs);
    return (T)localObject2;
  }
  
  public boolean isVarArgs()
  {
    return super.isVarArgs();
  }
  
  public boolean isSynthetic()
  {
    return super.isSynthetic();
  }
  
  private ConstructorAccessor acquireConstructorAccessor()
  {
    ConstructorAccessor localConstructorAccessor = null;
    if (root != null) {
      localConstructorAccessor = root.getConstructorAccessor();
    }
    if (localConstructorAccessor != null)
    {
      constructorAccessor = localConstructorAccessor;
    }
    else
    {
      localConstructorAccessor = reflectionFactory.newConstructorAccessor(this);
      setConstructorAccessor(localConstructorAccessor);
    }
    return localConstructorAccessor;
  }
  
  ConstructorAccessor getConstructorAccessor()
  {
    return constructorAccessor;
  }
  
  void setConstructorAccessor(ConstructorAccessor paramConstructorAccessor)
  {
    constructorAccessor = paramConstructorAccessor;
    if (root != null) {
      root.setConstructorAccessor(paramConstructorAccessor);
    }
  }
  
  int getSlot()
  {
    return slot;
  }
  
  String getSignature()
  {
    return signature;
  }
  
  byte[] getRawAnnotations()
  {
    return annotations;
  }
  
  byte[] getRawParameterAnnotations()
  {
    return parameterAnnotations;
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
  
  void handleParameterNumberMismatch(int paramInt1, int paramInt2)
  {
    Class localClass = getDeclaringClass();
    if ((localClass.isEnum()) || (localClass.isAnonymousClass()) || (localClass.isLocalClass())) {
      return;
    }
    if ((!localClass.isMemberClass()) || ((localClass.isMemberClass()) && ((localClass.getModifiers() & 0x8) == 0) && (paramInt1 + 1 != paramInt2))) {
      throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
    }
  }
  
  public AnnotatedType getAnnotatedReturnType()
  {
    return getAnnotatedReturnType0(getDeclaringClass());
  }
  
  public AnnotatedType getAnnotatedReceiverType()
  {
    if (getDeclaringClass().getEnclosingClass() == null) {
      return super.getAnnotatedReceiverType();
    }
    return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getDeclaringClass().getEnclosingClass(), TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\Constructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */