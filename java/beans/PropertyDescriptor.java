package java.beans;

import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;

public class PropertyDescriptor
  extends FeatureDescriptor
{
  private Reference<? extends Class<?>> propertyTypeRef;
  private final MethodRef readMethodRef = new MethodRef();
  private final MethodRef writeMethodRef = new MethodRef();
  private Reference<? extends Class<?>> propertyEditorClassRef;
  private boolean bound;
  private boolean constrained;
  private String baseName;
  private String writeMethodName;
  private String readMethodName;
  
  public PropertyDescriptor(String paramString, Class<?> paramClass)
    throws IntrospectionException
  {
    this(paramString, paramClass, "is" + NameGenerator.capitalize(paramString), "set" + NameGenerator.capitalize(paramString));
  }
  
  public PropertyDescriptor(String paramString1, Class<?> paramClass, String paramString2, String paramString3)
    throws IntrospectionException
  {
    if (paramClass == null) {
      throw new IntrospectionException("Target Bean class is null");
    }
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      throw new IntrospectionException("bad property name");
    }
    if (("".equals(paramString2)) || ("".equals(paramString3))) {
      throw new IntrospectionException("read or write method name should not be the empty string");
    }
    setName(paramString1);
    setClass0(paramClass);
    readMethodName = paramString2;
    if ((paramString2 != null) && (getReadMethod() == null)) {
      throw new IntrospectionException("Method not found: " + paramString2);
    }
    writeMethodName = paramString3;
    if ((paramString3 != null) && (getWriteMethod() == null)) {
      throw new IntrospectionException("Method not found: " + paramString3);
    }
    Class[] arrayOfClass = { PropertyChangeListener.class };
    bound = (null != Introspector.findMethod(paramClass, "addPropertyChangeListener", arrayOfClass.length, arrayOfClass));
  }
  
  public PropertyDescriptor(String paramString, Method paramMethod1, Method paramMethod2)
    throws IntrospectionException
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IntrospectionException("bad property name");
    }
    setName(paramString);
    setReadMethod(paramMethod1);
    setWriteMethod(paramMethod2);
  }
  
  PropertyDescriptor(Class<?> paramClass, String paramString, Method paramMethod1, Method paramMethod2)
    throws IntrospectionException
  {
    if (paramClass == null) {
      throw new IntrospectionException("Target Bean class is null");
    }
    setClass0(paramClass);
    setName(Introspector.decapitalize(paramString));
    setReadMethod(paramMethod1);
    setWriteMethod(paramMethod2);
    baseName = paramString;
  }
  
  public synchronized Class<?> getPropertyType()
  {
    Class localClass = getPropertyType0();
    if (localClass == null) {
      try
      {
        localClass = findPropertyType(getReadMethod(), getWriteMethod());
        setPropertyType(localClass);
      }
      catch (IntrospectionException localIntrospectionException) {}
    }
    return localClass;
  }
  
  private void setPropertyType(Class<?> paramClass)
  {
    propertyTypeRef = getWeakReference(paramClass);
  }
  
  private Class<?> getPropertyType0()
  {
    return propertyTypeRef != null ? (Class)propertyTypeRef.get() : null;
  }
  
  public synchronized Method getReadMethod()
  {
    Method localMethod = readMethodRef.get();
    if (localMethod == null)
    {
      Class localClass1 = getClass0();
      if ((localClass1 == null) || ((readMethodName == null) && (!readMethodRef.isSet()))) {
        return null;
      }
      String str = "get" + getBaseName();
      if (readMethodName == null)
      {
        Class localClass2 = getPropertyType0();
        if ((localClass2 == Boolean.TYPE) || (localClass2 == null)) {
          readMethodName = ("is" + getBaseName());
        } else {
          readMethodName = str;
        }
      }
      localMethod = Introspector.findMethod(localClass1, readMethodName, 0);
      if ((localMethod == null) && (!readMethodName.equals(str)))
      {
        readMethodName = str;
        localMethod = Introspector.findMethod(localClass1, readMethodName, 0);
      }
      try
      {
        setReadMethod(localMethod);
      }
      catch (IntrospectionException localIntrospectionException) {}
    }
    return localMethod;
  }
  
  public synchronized void setReadMethod(Method paramMethod)
    throws IntrospectionException
  {
    readMethodRef.set(paramMethod);
    if (paramMethod == null)
    {
      readMethodName = null;
      return;
    }
    setPropertyType(findPropertyType(paramMethod, writeMethodRef.get()));
    setClass0(paramMethod.getDeclaringClass());
    readMethodName = paramMethod.getName();
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public synchronized Method getWriteMethod()
  {
    Method localMethod = writeMethodRef.get();
    if (localMethod == null)
    {
      Class localClass1 = getClass0();
      if ((localClass1 == null) || ((writeMethodName == null) && (!writeMethodRef.isSet()))) {
        return null;
      }
      Class localClass2 = getPropertyType0();
      if (localClass2 == null) {
        try
        {
          localClass2 = findPropertyType(getReadMethod(), null);
          setPropertyType(localClass2);
        }
        catch (IntrospectionException localIntrospectionException1)
        {
          return null;
        }
      }
      if (writeMethodName == null) {
        writeMethodName = ("set" + getBaseName());
      }
      Class[] arrayOfClass = { localClass2 == null ? null : localClass2 };
      localMethod = Introspector.findMethod(localClass1, writeMethodName, 1, arrayOfClass);
      if ((localMethod != null) && (!localMethod.getReturnType().equals(Void.TYPE))) {
        localMethod = null;
      }
      try
      {
        setWriteMethod(localMethod);
      }
      catch (IntrospectionException localIntrospectionException2) {}
    }
    return localMethod;
  }
  
  public synchronized void setWriteMethod(Method paramMethod)
    throws IntrospectionException
  {
    writeMethodRef.set(paramMethod);
    if (paramMethod == null)
    {
      writeMethodName = null;
      return;
    }
    setPropertyType(findPropertyType(getReadMethod(), paramMethod));
    setClass0(paramMethod.getDeclaringClass());
    writeMethodName = paramMethod.getName();
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  void setClass0(Class<?> paramClass)
  {
    if ((getClass0() != null) && (paramClass.isAssignableFrom(getClass0()))) {
      return;
    }
    super.setClass0(paramClass);
  }
  
  public boolean isBound()
  {
    return bound;
  }
  
  public void setBound(boolean paramBoolean)
  {
    bound = paramBoolean;
  }
  
  public boolean isConstrained()
  {
    return constrained;
  }
  
  public void setConstrained(boolean paramBoolean)
  {
    constrained = paramBoolean;
  }
  
  public void setPropertyEditorClass(Class<?> paramClass)
  {
    propertyEditorClassRef = getWeakReference(paramClass);
  }
  
  public Class<?> getPropertyEditorClass()
  {
    return propertyEditorClassRef != null ? (Class)propertyEditorClassRef.get() : null;
  }
  
  public PropertyEditor createPropertyEditor(Object paramObject)
  {
    Object localObject = null;
    Class localClass = getPropertyEditorClass();
    if ((localClass != null) && (PropertyEditor.class.isAssignableFrom(localClass)) && (ReflectUtil.isPackageAccessible(localClass)))
    {
      Constructor localConstructor = null;
      if (paramObject != null) {
        try
        {
          localConstructor = localClass.getConstructor(new Class[] { Object.class });
        }
        catch (Exception localException1) {}
      }
      try
      {
        if (localConstructor == null) {
          localObject = localClass.newInstance();
        } else {
          localObject = localConstructor.newInstance(new Object[] { paramObject });
        }
      }
      catch (Exception localException2) {}
    }
    return (PropertyEditor)localObject;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && ((paramObject instanceof PropertyDescriptor)))
    {
      PropertyDescriptor localPropertyDescriptor = (PropertyDescriptor)paramObject;
      Method localMethod1 = localPropertyDescriptor.getReadMethod();
      Method localMethod2 = localPropertyDescriptor.getWriteMethod();
      if (!compareMethods(getReadMethod(), localMethod1)) {
        return false;
      }
      if (!compareMethods(getWriteMethod(), localMethod2)) {
        return false;
      }
      if ((getPropertyType() == localPropertyDescriptor.getPropertyType()) && (getPropertyEditorClass() == localPropertyDescriptor.getPropertyEditorClass()) && (bound == localPropertyDescriptor.isBound()) && (constrained == localPropertyDescriptor.isConstrained()) && (writeMethodName == writeMethodName) && (readMethodName == readMethodName)) {
        return true;
      }
    }
    return false;
  }
  
  boolean compareMethods(Method paramMethod1, Method paramMethod2)
  {
    if ((paramMethod1 == null ? 1 : 0) != (paramMethod2 == null ? 1 : 0)) {
      return false;
    }
    return (paramMethod1 == null) || (paramMethod2 == null) || (paramMethod1.equals(paramMethod2));
  }
  
  PropertyDescriptor(PropertyDescriptor paramPropertyDescriptor1, PropertyDescriptor paramPropertyDescriptor2)
  {
    super(paramPropertyDescriptor1, paramPropertyDescriptor2);
    if (baseName != null) {
      baseName = baseName;
    } else {
      baseName = baseName;
    }
    if (readMethodName != null) {
      readMethodName = readMethodName;
    } else {
      readMethodName = readMethodName;
    }
    if (writeMethodName != null) {
      writeMethodName = writeMethodName;
    } else {
      writeMethodName = writeMethodName;
    }
    if (propertyTypeRef != null) {
      propertyTypeRef = propertyTypeRef;
    } else {
      propertyTypeRef = propertyTypeRef;
    }
    Method localMethod1 = paramPropertyDescriptor1.getReadMethod();
    Method localMethod2 = paramPropertyDescriptor2.getReadMethod();
    try
    {
      if (isAssignable(localMethod1, localMethod2)) {
        setReadMethod(localMethod2);
      } else {
        setReadMethod(localMethod1);
      }
    }
    catch (IntrospectionException localIntrospectionException1) {}
    if ((localMethod1 != null) && (localMethod2 != null) && (localMethod1.getDeclaringClass() == localMethod2.getDeclaringClass()) && (getReturnType(getClass0(), localMethod1) == Boolean.TYPE) && (getReturnType(getClass0(), localMethod2) == Boolean.TYPE) && (localMethod1.getName().indexOf("is") == 0) && (localMethod2.getName().indexOf("get") == 0)) {
      try
      {
        setReadMethod(localMethod1);
      }
      catch (IntrospectionException localIntrospectionException2) {}
    }
    Method localMethod3 = paramPropertyDescriptor1.getWriteMethod();
    Method localMethod4 = paramPropertyDescriptor2.getWriteMethod();
    try
    {
      if (localMethod4 != null) {
        setWriteMethod(localMethod4);
      } else {
        setWriteMethod(localMethod3);
      }
    }
    catch (IntrospectionException localIntrospectionException3) {}
    if (paramPropertyDescriptor2.getPropertyEditorClass() != null) {
      setPropertyEditorClass(paramPropertyDescriptor2.getPropertyEditorClass());
    } else {
      setPropertyEditorClass(paramPropertyDescriptor1.getPropertyEditorClass());
    }
    bound |= bound;
    constrained |= constrained;
  }
  
  PropertyDescriptor(PropertyDescriptor paramPropertyDescriptor)
  {
    super(paramPropertyDescriptor);
    propertyTypeRef = propertyTypeRef;
    readMethodRef.set(readMethodRef.get());
    writeMethodRef.set(writeMethodRef.get());
    propertyEditorClassRef = propertyEditorClassRef;
    writeMethodName = writeMethodName;
    readMethodName = readMethodName;
    baseName = baseName;
    bound = bound;
    constrained = constrained;
  }
  
  void updateGenericsFor(Class<?> paramClass)
  {
    setClass0(paramClass);
    try
    {
      setPropertyType(findPropertyType(readMethodRef.get(), writeMethodRef.get()));
    }
    catch (IntrospectionException localIntrospectionException)
    {
      setPropertyType(null);
    }
  }
  
  private Class<?> findPropertyType(Method paramMethod1, Method paramMethod2)
    throws IntrospectionException
  {
    Class localClass = null;
    try
    {
      Class[] arrayOfClass;
      if (paramMethod1 != null)
      {
        arrayOfClass = getParameterTypes(getClass0(), paramMethod1);
        if (arrayOfClass.length != 0) {
          throw new IntrospectionException("bad read method arg count: " + paramMethod1);
        }
        localClass = getReturnType(getClass0(), paramMethod1);
        if (localClass == Void.TYPE) {
          throw new IntrospectionException("read method " + paramMethod1.getName() + " returns void");
        }
      }
      if (paramMethod2 != null)
      {
        arrayOfClass = getParameterTypes(getClass0(), paramMethod2);
        if (arrayOfClass.length != 1) {
          throw new IntrospectionException("bad write method arg count: " + paramMethod2);
        }
        if ((localClass != null) && (!arrayOfClass[0].isAssignableFrom(localClass))) {
          throw new IntrospectionException("type mismatch between read and write methods");
        }
        localClass = arrayOfClass[0];
      }
    }
    catch (IntrospectionException localIntrospectionException)
    {
      throw localIntrospectionException;
    }
    return localClass;
  }
  
  public int hashCode()
  {
    int i = 7;
    i = 37 * i + (getPropertyType() == null ? 0 : getPropertyType().hashCode());
    i = 37 * i + (getReadMethod() == null ? 0 : getReadMethod().hashCode());
    i = 37 * i + (getWriteMethod() == null ? 0 : getWriteMethod().hashCode());
    i = 37 * i + (getPropertyEditorClass() == null ? 0 : getPropertyEditorClass().hashCode());
    i = 37 * i + (writeMethodName == null ? 0 : writeMethodName.hashCode());
    i = 37 * i + (readMethodName == null ? 0 : readMethodName.hashCode());
    i = 37 * i + getName().hashCode();
    i = 37 * i + (!bound ? 0 : 1);
    i = 37 * i + (!constrained ? 0 : 1);
    return i;
  }
  
  String getBaseName()
  {
    if (baseName == null) {
      baseName = NameGenerator.capitalize(getName());
    }
    return baseName;
  }
  
  void appendTo(StringBuilder paramStringBuilder)
  {
    appendTo(paramStringBuilder, "bound", bound);
    appendTo(paramStringBuilder, "constrained", constrained);
    appendTo(paramStringBuilder, "propertyEditorClass", propertyEditorClassRef);
    appendTo(paramStringBuilder, "propertyType", propertyTypeRef);
    appendTo(paramStringBuilder, "readMethod", readMethodRef.get());
    appendTo(paramStringBuilder, "writeMethod", writeMethodRef.get());
  }
  
  private boolean isAssignable(Method paramMethod1, Method paramMethod2)
  {
    if (paramMethod1 == null) {
      return true;
    }
    if (paramMethod2 == null) {
      return false;
    }
    if (!paramMethod1.getName().equals(paramMethod2.getName())) {
      return true;
    }
    Class localClass1 = paramMethod1.getDeclaringClass();
    Class localClass2 = paramMethod2.getDeclaringClass();
    if (!localClass1.isAssignableFrom(localClass2)) {
      return false;
    }
    localClass1 = getReturnType(getClass0(), paramMethod1);
    localClass2 = getReturnType(getClass0(), paramMethod2);
    if (!localClass1.isAssignableFrom(localClass2)) {
      return false;
    }
    Class[] arrayOfClass1 = getParameterTypes(getClass0(), paramMethod1);
    Class[] arrayOfClass2 = getParameterTypes(getClass0(), paramMethod2);
    if (arrayOfClass1.length != arrayOfClass2.length) {
      return true;
    }
    for (int i = 0; i < arrayOfClass1.length; i++) {
      if (!arrayOfClass1[i].isAssignableFrom(arrayOfClass2[i])) {
        return false;
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\PropertyDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */