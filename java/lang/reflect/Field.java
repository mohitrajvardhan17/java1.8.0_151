package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.FieldAccessor;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.TypeAnnotation.TypeAnnotationTarget;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.FieldRepository;
import sun.reflect.generics.scope.ClassScope;

public final class Field
  extends AccessibleObject
  implements Member
{
  private Class<?> clazz;
  private int slot;
  private String name;
  private Class<?> type;
  private int modifiers;
  private transient String signature;
  private transient FieldRepository genericInfo;
  private byte[] annotations;
  private FieldAccessor fieldAccessor;
  private FieldAccessor overrideFieldAccessor;
  private Field root;
  private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
  
  private String getGenericSignature()
  {
    return signature;
  }
  
  private GenericsFactory getFactory()
  {
    Class localClass = getDeclaringClass();
    return CoreReflectionFactory.make(localClass, ClassScope.make(localClass));
  }
  
  private FieldRepository getGenericInfo()
  {
    if (genericInfo == null) {
      genericInfo = FieldRepository.make(getGenericSignature(), getFactory());
    }
    return genericInfo;
  }
  
  Field(Class<?> paramClass1, String paramString1, Class<?> paramClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte)
  {
    clazz = paramClass1;
    name = paramString1;
    type = paramClass2;
    modifiers = paramInt1;
    slot = paramInt2;
    signature = paramString2;
    annotations = paramArrayOfByte;
  }
  
  Field copy()
  {
    if (root != null) {
      throw new IllegalArgumentException("Can not copy a non-root Field");
    }
    Field localField = new Field(clazz, name, type, modifiers, slot, signature, annotations);
    root = this;
    fieldAccessor = fieldAccessor;
    overrideFieldAccessor = overrideFieldAccessor;
    return localField;
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
  
  public boolean isEnumConstant()
  {
    return (getModifiers() & 0x4000) != 0;
  }
  
  public boolean isSynthetic()
  {
    return Modifier.isSynthetic(getModifiers());
  }
  
  public Class<?> getType()
  {
    return type;
  }
  
  public Type getGenericType()
  {
    if (getGenericSignature() != null) {
      return getGenericInfo().getGenericType();
    }
    return getType();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Field)))
    {
      Field localField = (Field)paramObject;
      return (getDeclaringClass() == localField.getDeclaringClass()) && (getName() == localField.getName()) && (getType() == localField.getType());
    }
    return false;
  }
  
  public int hashCode()
  {
    return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
  }
  
  public String toString()
  {
    int i = getModifiers();
    return (i == 0 ? "" : new StringBuilder().append(Modifier.toString(i)).append(" ").toString()) + getType().getTypeName() + " " + getDeclaringClass().getTypeName() + "." + getName();
  }
  
  public String toGenericString()
  {
    int i = getModifiers();
    Type localType = getGenericType();
    return (i == 0 ? "" : new StringBuilder().append(Modifier.toString(i)).append(" ").toString()) + localType.getTypeName() + " " + getDeclaringClass().getTypeName() + "." + getName();
  }
  
  @CallerSensitive
  public Object get(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    return getFieldAccessor(paramObject).get(paramObject);
  }
  
  @CallerSensitive
  public boolean getBoolean(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    return getFieldAccessor(paramObject).getBoolean(paramObject);
  }
  
  @CallerSensitive
  public byte getByte(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    return getFieldAccessor(paramObject).getByte(paramObject);
  }
  
  @CallerSensitive
  public char getChar(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    return getFieldAccessor(paramObject).getChar(paramObject);
  }
  
  @CallerSensitive
  public short getShort(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    return getFieldAccessor(paramObject).getShort(paramObject);
  }
  
  @CallerSensitive
  public int getInt(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    return getFieldAccessor(paramObject).getInt(paramObject);
  }
  
  @CallerSensitive
  public long getLong(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    return getFieldAccessor(paramObject).getLong(paramObject);
  }
  
  @CallerSensitive
  public float getFloat(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    return getFieldAccessor(paramObject).getFloat(paramObject);
  }
  
  @CallerSensitive
  public double getDouble(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    return getFieldAccessor(paramObject).getDouble(paramObject);
  }
  
  @CallerSensitive
  public void set(Object paramObject1, Object paramObject2)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject1, modifiers);
    }
    getFieldAccessor(paramObject1).set(paramObject1, paramObject2);
  }
  
  @CallerSensitive
  public void setBoolean(Object paramObject, boolean paramBoolean)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    getFieldAccessor(paramObject).setBoolean(paramObject, paramBoolean);
  }
  
  @CallerSensitive
  public void setByte(Object paramObject, byte paramByte)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    getFieldAccessor(paramObject).setByte(paramObject, paramByte);
  }
  
  @CallerSensitive
  public void setChar(Object paramObject, char paramChar)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    getFieldAccessor(paramObject).setChar(paramObject, paramChar);
  }
  
  @CallerSensitive
  public void setShort(Object paramObject, short paramShort)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    getFieldAccessor(paramObject).setShort(paramObject, paramShort);
  }
  
  @CallerSensitive
  public void setInt(Object paramObject, int paramInt)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    getFieldAccessor(paramObject).setInt(paramObject, paramInt);
  }
  
  @CallerSensitive
  public void setLong(Object paramObject, long paramLong)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    getFieldAccessor(paramObject).setLong(paramObject, paramLong);
  }
  
  @CallerSensitive
  public void setFloat(Object paramObject, float paramFloat)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    getFieldAccessor(paramObject).setFloat(paramObject, paramFloat);
  }
  
  @CallerSensitive
  public void setDouble(Object paramObject, double paramDouble)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!override) && (!Reflection.quickCheckMemberAccess(clazz, modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, clazz, paramObject, modifiers);
    }
    getFieldAccessor(paramObject).setDouble(paramObject, paramDouble);
  }
  
  private FieldAccessor getFieldAccessor(Object paramObject)
    throws IllegalAccessException
  {
    boolean bool = override;
    FieldAccessor localFieldAccessor = bool ? overrideFieldAccessor : fieldAccessor;
    return localFieldAccessor != null ? localFieldAccessor : acquireFieldAccessor(bool);
  }
  
  private FieldAccessor acquireFieldAccessor(boolean paramBoolean)
  {
    FieldAccessor localFieldAccessor = null;
    if (root != null) {
      localFieldAccessor = root.getFieldAccessor(paramBoolean);
    }
    if (localFieldAccessor != null)
    {
      if (paramBoolean) {
        overrideFieldAccessor = localFieldAccessor;
      } else {
        fieldAccessor = localFieldAccessor;
      }
    }
    else
    {
      localFieldAccessor = reflectionFactory.newFieldAccessor(this, paramBoolean);
      setFieldAccessor(localFieldAccessor, paramBoolean);
    }
    return localFieldAccessor;
  }
  
  private FieldAccessor getFieldAccessor(boolean paramBoolean)
  {
    return paramBoolean ? overrideFieldAccessor : fieldAccessor;
  }
  
  private void setFieldAccessor(FieldAccessor paramFieldAccessor, boolean paramBoolean)
  {
    if (paramBoolean) {
      overrideFieldAccessor = paramFieldAccessor;
    } else {
      fieldAccessor = paramFieldAccessor;
    }
    if (root != null) {
      root.setFieldAccessor(paramFieldAccessor, paramBoolean);
    }
  }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return (Annotation)paramClass.cast(declaredAnnotations().get(paramClass));
  }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return AnnotationSupport.getDirectlyAndIndirectlyPresent(declaredAnnotations(), paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return AnnotationParser.toArray(declaredAnnotations());
  }
  
  private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations()
  {
    if (declaredAnnotations == null)
    {
      Field localField = root;
      if (localField != null) {
        declaredAnnotations = localField.declaredAnnotations();
      } else {
        declaredAnnotations = AnnotationParser.parseAnnotations(annotations, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
      }
    }
    return declaredAnnotations;
  }
  
  private native byte[] getTypeAnnotationBytes0();
  
  public AnnotatedType getAnnotatedType()
  {
    return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getGenericType(), TypeAnnotation.TypeAnnotationTarget.FIELD);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\Field.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */