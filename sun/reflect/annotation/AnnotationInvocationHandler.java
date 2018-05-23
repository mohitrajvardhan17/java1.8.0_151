package sun.reflect.annotation;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import sun.misc.Unsafe;

class AnnotationInvocationHandler
  implements InvocationHandler, Serializable
{
  private static final long serialVersionUID = 6182022883658399397L;
  private final Class<? extends Annotation> type;
  private final Map<String, Object> memberValues;
  private volatile transient Method[] memberMethods = null;
  
  AnnotationInvocationHandler(Class<? extends Annotation> paramClass, Map<String, Object> paramMap)
  {
    Class[] arrayOfClass = paramClass.getInterfaces();
    if ((!paramClass.isAnnotation()) || (arrayOfClass.length != 1) || (arrayOfClass[0] != Annotation.class)) {
      throw new AnnotationFormatError("Attempt to create proxy for a non-annotation type.");
    }
    type = paramClass;
    memberValues = paramMap;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
  {
    String str = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    if ((str.equals("equals")) && (arrayOfClass.length == 1) && (arrayOfClass[0] == Object.class)) {
      return equalsImpl(paramArrayOfObject[0]);
    }
    if (arrayOfClass.length != 0) {
      throw new AssertionError("Too many parameters for an annotation method");
    }
    Object localObject = str;
    int i = -1;
    switch (((String)localObject).hashCode())
    {
    case -1776922004: 
      if (((String)localObject).equals("toString")) {
        i = 0;
      }
      break;
    case 147696667: 
      if (((String)localObject).equals("hashCode")) {
        i = 1;
      }
      break;
    case 1444986633: 
      if (((String)localObject).equals("annotationType")) {
        i = 2;
      }
      break;
    }
    switch (i)
    {
    case 0: 
      return toStringImpl();
    case 1: 
      return Integer.valueOf(hashCodeImpl());
    case 2: 
      return type;
    }
    localObject = memberValues.get(str);
    if (localObject == null) {
      throw new IncompleteAnnotationException(type, str);
    }
    if ((localObject instanceof ExceptionProxy)) {
      throw ((ExceptionProxy)localObject).generateException();
    }
    if ((localObject.getClass().isArray()) && (Array.getLength(localObject) != 0)) {
      localObject = cloneArray(localObject);
    }
    return localObject;
  }
  
  private Object cloneArray(Object paramObject)
  {
    Class localClass = paramObject.getClass();
    if (localClass == byte[].class)
    {
      localObject = (byte[])paramObject;
      return ((byte[])localObject).clone();
    }
    if (localClass == char[].class)
    {
      localObject = (char[])paramObject;
      return ((char[])localObject).clone();
    }
    if (localClass == double[].class)
    {
      localObject = (double[])paramObject;
      return ((double[])localObject).clone();
    }
    if (localClass == float[].class)
    {
      localObject = (float[])paramObject;
      return ((float[])localObject).clone();
    }
    if (localClass == int[].class)
    {
      localObject = (int[])paramObject;
      return ((int[])localObject).clone();
    }
    if (localClass == long[].class)
    {
      localObject = (long[])paramObject;
      return ((long[])localObject).clone();
    }
    if (localClass == short[].class)
    {
      localObject = (short[])paramObject;
      return ((short[])localObject).clone();
    }
    if (localClass == boolean[].class)
    {
      localObject = (boolean[])paramObject;
      return ((boolean[])localObject).clone();
    }
    Object localObject = (Object[])paramObject;
    return ((Object[])localObject).clone();
  }
  
  private String toStringImpl()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append('@');
    localStringBuilder.append(type.getName());
    localStringBuilder.append('(');
    int i = 1;
    Iterator localIterator = memberValues.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (i != 0) {
        i = 0;
      } else {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append('=');
      localStringBuilder.append(memberValueToString(localEntry.getValue()));
    }
    localStringBuilder.append(')');
    return localStringBuilder.toString();
  }
  
  private static String memberValueToString(Object paramObject)
  {
    Class localClass = paramObject.getClass();
    if (!localClass.isArray()) {
      return paramObject.toString();
    }
    if (localClass == byte[].class) {
      return Arrays.toString((byte[])paramObject);
    }
    if (localClass == char[].class) {
      return Arrays.toString((char[])paramObject);
    }
    if (localClass == double[].class) {
      return Arrays.toString((double[])paramObject);
    }
    if (localClass == float[].class) {
      return Arrays.toString((float[])paramObject);
    }
    if (localClass == int[].class) {
      return Arrays.toString((int[])paramObject);
    }
    if (localClass == long[].class) {
      return Arrays.toString((long[])paramObject);
    }
    if (localClass == short[].class) {
      return Arrays.toString((short[])paramObject);
    }
    if (localClass == boolean[].class) {
      return Arrays.toString((boolean[])paramObject);
    }
    return Arrays.toString((Object[])paramObject);
  }
  
  private Boolean equalsImpl(Object paramObject)
  {
    if (paramObject == this) {
      return Boolean.valueOf(true);
    }
    if (!type.isInstance(paramObject)) {
      return Boolean.valueOf(false);
    }
    for (Method localMethod : getMemberMethods())
    {
      String str = localMethod.getName();
      Object localObject1 = memberValues.get(str);
      Object localObject2 = null;
      AnnotationInvocationHandler localAnnotationInvocationHandler = asOneOfUs(paramObject);
      if (localAnnotationInvocationHandler != null) {
        localObject2 = memberValues.get(str);
      } else {
        try
        {
          localObject2 = localMethod.invoke(paramObject, new Object[0]);
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          return Boolean.valueOf(false);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new AssertionError(localIllegalAccessException);
        }
      }
      if (!memberValueEquals(localObject1, localObject2)) {
        return Boolean.valueOf(false);
      }
    }
    return Boolean.valueOf(true);
  }
  
  private AnnotationInvocationHandler asOneOfUs(Object paramObject)
  {
    if (Proxy.isProxyClass(paramObject.getClass()))
    {
      InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(paramObject);
      if ((localInvocationHandler instanceof AnnotationInvocationHandler)) {
        return (AnnotationInvocationHandler)localInvocationHandler;
      }
    }
    return null;
  }
  
  private static boolean memberValueEquals(Object paramObject1, Object paramObject2)
  {
    Class localClass = paramObject1.getClass();
    if (!localClass.isArray()) {
      return paramObject1.equals(paramObject2);
    }
    if (((paramObject1 instanceof Object[])) && ((paramObject2 instanceof Object[]))) {
      return Arrays.equals((Object[])paramObject1, (Object[])paramObject2);
    }
    if (paramObject2.getClass() != localClass) {
      return false;
    }
    if (localClass == byte[].class) {
      return Arrays.equals((byte[])paramObject1, (byte[])paramObject2);
    }
    if (localClass == char[].class) {
      return Arrays.equals((char[])paramObject1, (char[])paramObject2);
    }
    if (localClass == double[].class) {
      return Arrays.equals((double[])paramObject1, (double[])paramObject2);
    }
    if (localClass == float[].class) {
      return Arrays.equals((float[])paramObject1, (float[])paramObject2);
    }
    if (localClass == int[].class) {
      return Arrays.equals((int[])paramObject1, (int[])paramObject2);
    }
    if (localClass == long[].class) {
      return Arrays.equals((long[])paramObject1, (long[])paramObject2);
    }
    if (localClass == short[].class) {
      return Arrays.equals((short[])paramObject1, (short[])paramObject2);
    }
    assert (localClass == boolean[].class);
    return Arrays.equals((boolean[])paramObject1, (boolean[])paramObject2);
  }
  
  private Method[] getMemberMethods()
  {
    if (memberMethods == null) {
      memberMethods = ((Method[])AccessController.doPrivileged(new PrivilegedAction()
      {
        public Method[] run()
        {
          Method[] arrayOfMethod = type.getDeclaredMethods();
          AnnotationInvocationHandler.this.validateAnnotationMethods(arrayOfMethod);
          AccessibleObject.setAccessible(arrayOfMethod, true);
          return arrayOfMethod;
        }
      }));
    }
    return memberMethods;
  }
  
  private void validateAnnotationMethods(Method[] paramArrayOfMethod)
  {
    int i = 1;
    for (Method localMethod : paramArrayOfMethod)
    {
      if ((localMethod.getModifiers() != 1025) || (localMethod.isDefault()) || (localMethod.getParameterCount() != 0) || (localMethod.getExceptionTypes().length != 0))
      {
        i = 0;
        break;
      }
      Class localClass = localMethod.getReturnType();
      if (localClass.isArray())
      {
        localClass = localClass.getComponentType();
        if (localClass.isArray())
        {
          i = 0;
          break;
        }
      }
      if (((!localClass.isPrimitive()) || (localClass == Void.TYPE)) && (localClass != String.class) && (localClass != Class.class) && (!localClass.isEnum()) && (!localClass.isAnnotation()))
      {
        i = 0;
        break;
      }
      String str = localMethod.getName();
      if (((str.equals("toString")) && (localClass == String.class)) || ((str.equals("hashCode")) && (localClass == Integer.TYPE)) || ((str.equals("annotationType")) && (localClass == Class.class)))
      {
        i = 0;
        break;
      }
    }
    if (i != 0) {
      return;
    }
    throw new AnnotationFormatError("Malformed method on an annotation type");
  }
  
  private int hashCodeImpl()
  {
    int i = 0;
    Iterator localIterator = memberValues.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      i += (127 * ((String)localEntry.getKey()).hashCode() ^ memberValueHashCode(localEntry.getValue()));
    }
    return i;
  }
  
  private static int memberValueHashCode(Object paramObject)
  {
    Class localClass = paramObject.getClass();
    if (!localClass.isArray()) {
      return paramObject.hashCode();
    }
    if (localClass == byte[].class) {
      return Arrays.hashCode((byte[])paramObject);
    }
    if (localClass == char[].class) {
      return Arrays.hashCode((char[])paramObject);
    }
    if (localClass == double[].class) {
      return Arrays.hashCode((double[])paramObject);
    }
    if (localClass == float[].class) {
      return Arrays.hashCode((float[])paramObject);
    }
    if (localClass == int[].class) {
      return Arrays.hashCode((int[])paramObject);
    }
    if (localClass == long[].class) {
      return Arrays.hashCode((long[])paramObject);
    }
    if (localClass == short[].class) {
      return Arrays.hashCode((short[])paramObject);
    }
    if (localClass == boolean[].class) {
      return Arrays.hashCode((boolean[])paramObject);
    }
    return Arrays.hashCode((Object[])paramObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Class localClass1 = (Class)localGetField.get("type", null);
    Map localMap1 = (Map)localGetField.get("memberValues", null);
    AnnotationType localAnnotationType = null;
    try
    {
      localAnnotationType = AnnotationType.getInstance(localClass1);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new InvalidObjectException("Non-annotation type in annotation serial stream");
    }
    Map localMap2 = localAnnotationType.memberTypes();
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    Iterator localIterator = localMap1.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      Object localObject = null;
      Class localClass2 = (Class)localMap2.get(str);
      if (localClass2 != null)
      {
        localObject = localEntry.getValue();
        if ((!localClass2.isInstance(localObject)) && (!(localObject instanceof ExceptionProxy))) {
          localObject = new AnnotationTypeMismatchExceptionProxy(localObject.getClass() + "[" + localObject + "]").setMember((Method)localAnnotationType.members().get(str));
        }
      }
      localLinkedHashMap.put(str, localObject);
    }
    UnsafeAccessor.setType(this, localClass1);
    UnsafeAccessor.setMemberValues(this, localLinkedHashMap);
  }
  
  private static class UnsafeAccessor
  {
    private static final Unsafe unsafe;
    private static final long typeOffset;
    private static final long memberValuesOffset;
    
    private UnsafeAccessor() {}
    
    static void setType(AnnotationInvocationHandler paramAnnotationInvocationHandler, Class<? extends Annotation> paramClass)
    {
      unsafe.putObject(paramAnnotationInvocationHandler, typeOffset, paramClass);
    }
    
    static void setMemberValues(AnnotationInvocationHandler paramAnnotationInvocationHandler, Map<String, Object> paramMap)
    {
      unsafe.putObject(paramAnnotationInvocationHandler, memberValuesOffset, paramMap);
    }
    
    static
    {
      try
      {
        unsafe = Unsafe.getUnsafe();
        typeOffset = unsafe.objectFieldOffset(AnnotationInvocationHandler.class.getDeclaredField("type"));
        memberValuesOffset = unsafe.objectFieldOffset(AnnotationInvocationHandler.class.getDeclaredField("memberValues"));
      }
      catch (Exception localException)
      {
        throw new ExceptionInInitializerError(localException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\AnnotationInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */