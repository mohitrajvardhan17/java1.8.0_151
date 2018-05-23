package com.sun.corba.se.impl.presentation.rmi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.portable.IDLEntity;

public final class IDLTypesUtil
{
  private static final String GET_PROPERTY_PREFIX = "get";
  private static final String SET_PROPERTY_PREFIX = "set";
  private static final String IS_PROPERTY_PREFIX = "is";
  public static final int VALID_TYPE = 0;
  public static final int INVALID_TYPE = 1;
  public static final boolean FOLLOW_RMIC = true;
  
  public IDLTypesUtil() {}
  
  public void validateRemoteInterface(Class paramClass)
    throws IDLTypeException
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    if (!paramClass.isInterface())
    {
      localObject = "Class " + paramClass + " must be a java interface.";
      throw new IDLTypeException((String)localObject);
    }
    if (!Remote.class.isAssignableFrom(paramClass))
    {
      localObject = "Class " + paramClass + " must extend java.rmi.Remote, either directly or indirectly.";
      throw new IDLTypeException((String)localObject);
    }
    Object localObject = paramClass.getMethods();
    for (int i = 0; i < localObject.length; i++)
    {
      Method localMethod = localObject[i];
      validateExceptions(localMethod);
    }
    validateConstants(paramClass);
  }
  
  public boolean isRemoteInterface(Class paramClass)
  {
    boolean bool = true;
    try
    {
      validateRemoteInterface(paramClass);
    }
    catch (IDLTypeException localIDLTypeException)
    {
      bool = false;
    }
    return bool;
  }
  
  public boolean isPrimitive(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return paramClass.isPrimitive();
  }
  
  public boolean isValue(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return (!paramClass.isInterface()) && (Serializable.class.isAssignableFrom(paramClass)) && (!Remote.class.isAssignableFrom(paramClass));
  }
  
  public boolean isArray(Class paramClass)
  {
    boolean bool = false;
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    if (paramClass.isArray())
    {
      Class localClass = paramClass.getComponentType();
      bool = (isPrimitive(localClass)) || (isRemoteInterface(localClass)) || (isEntity(localClass)) || (isException(localClass)) || (isValue(localClass)) || (isObjectReference(localClass));
    }
    return bool;
  }
  
  public boolean isException(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return (isCheckedException(paramClass)) && (!isRemoteException(paramClass)) && (isValue(paramClass));
  }
  
  public boolean isRemoteException(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return RemoteException.class.isAssignableFrom(paramClass);
  }
  
  public boolean isCheckedException(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return (Throwable.class.isAssignableFrom(paramClass)) && (!RuntimeException.class.isAssignableFrom(paramClass)) && (!Error.class.isAssignableFrom(paramClass));
  }
  
  public boolean isObjectReference(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return (paramClass.isInterface()) && (org.omg.CORBA.Object.class.isAssignableFrom(paramClass));
  }
  
  public boolean isEntity(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    Class localClass = paramClass.getSuperclass();
    return (!paramClass.isInterface()) && (localClass != null) && (IDLEntity.class.isAssignableFrom(paramClass));
  }
  
  public boolean isPropertyAccessorMethod(Method paramMethod, Class paramClass)
  {
    String str1 = paramMethod.getName();
    Class localClass = paramMethod.getReturnType();
    Class[] arrayOfClass1 = paramMethod.getParameterTypes();
    Class[] arrayOfClass2 = paramMethod.getExceptionTypes();
    String str2 = null;
    if (str1.startsWith("get"))
    {
      if ((arrayOfClass1.length == 0) && (localClass != Void.TYPE) && (!readHasCorrespondingIsProperty(paramMethod, paramClass))) {
        str2 = "get";
      }
    }
    else if (str1.startsWith("set"))
    {
      if ((localClass == Void.TYPE) && (arrayOfClass1.length == 1) && ((hasCorrespondingReadProperty(paramMethod, paramClass, "get")) || (hasCorrespondingReadProperty(paramMethod, paramClass, "is")))) {
        str2 = "set";
      }
    }
    else if ((str1.startsWith("is")) && (arrayOfClass1.length == 0) && (localClass == Boolean.TYPE) && (!isHasCorrespondingReadProperty(paramMethod, paramClass))) {
      str2 = "is";
    }
    if ((str2 != null) && ((!validPropertyExceptions(paramMethod)) || (str1.length() <= str2.length()))) {
      str2 = null;
    }
    return str2 != null;
  }
  
  private boolean hasCorrespondingReadProperty(Method paramMethod, Class paramClass, String paramString)
  {
    String str1 = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    boolean bool = false;
    try
    {
      String str2 = str1.replaceFirst("set", paramString);
      Method localMethod = paramClass.getMethod(str2, new Class[0]);
      bool = (isPropertyAccessorMethod(localMethod, paramClass)) && (localMethod.getReturnType() == arrayOfClass[0]);
    }
    catch (Exception localException) {}
    return bool;
  }
  
  private boolean readHasCorrespondingIsProperty(Method paramMethod, Class paramClass)
  {
    return false;
  }
  
  private boolean isHasCorrespondingReadProperty(Method paramMethod, Class paramClass)
  {
    String str1 = paramMethod.getName();
    boolean bool = false;
    try
    {
      String str2 = str1.replaceFirst("is", "get");
      Method localMethod = paramClass.getMethod(str2, new Class[0]);
      bool = isPropertyAccessorMethod(localMethod, paramClass);
    }
    catch (Exception localException) {}
    return bool;
  }
  
  public String getAttributeNameForProperty(String paramString)
  {
    Object localObject = null;
    String str1 = null;
    if (paramString.startsWith("get")) {
      str1 = "get";
    } else if (paramString.startsWith("set")) {
      str1 = "set";
    } else if (paramString.startsWith("is")) {
      str1 = "is";
    }
    if ((str1 != null) && (str1.length() < paramString.length()))
    {
      String str2 = paramString.substring(str1.length());
      if ((str2.length() >= 2) && (Character.isUpperCase(str2.charAt(0))) && (Character.isUpperCase(str2.charAt(1)))) {
        localObject = str2;
      } else {
        localObject = Character.toLowerCase(str2.charAt(0)) + str2.substring(1);
      }
    }
    return (String)localObject;
  }
  
  public IDLType getPrimitiveIDLTypeMapping(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    if (paramClass.isPrimitive())
    {
      if (paramClass == Void.TYPE) {
        return new IDLType(paramClass, "void");
      }
      if (paramClass == Boolean.TYPE) {
        return new IDLType(paramClass, "boolean");
      }
      if (paramClass == Character.TYPE) {
        return new IDLType(paramClass, "wchar");
      }
      if (paramClass == Byte.TYPE) {
        return new IDLType(paramClass, "octet");
      }
      if (paramClass == Short.TYPE) {
        return new IDLType(paramClass, "short");
      }
      if (paramClass == Integer.TYPE) {
        return new IDLType(paramClass, "long");
      }
      if (paramClass == Long.TYPE) {
        return new IDLType(paramClass, "long_long");
      }
      if (paramClass == Float.TYPE) {
        return new IDLType(paramClass, "float");
      }
      if (paramClass == Double.TYPE) {
        return new IDLType(paramClass, "double");
      }
    }
    return null;
  }
  
  public IDLType getSpecialCaseIDLTypeMapping(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    if (paramClass == Object.class) {
      return new IDLType(paramClass, new String[] { "java", "lang" }, "Object");
    }
    if (paramClass == String.class) {
      return new IDLType(paramClass, new String[] { "CORBA" }, "WStringValue");
    }
    if (paramClass == Class.class) {
      return new IDLType(paramClass, new String[] { "javax", "rmi", "CORBA" }, "ClassDesc");
    }
    if (paramClass == Serializable.class) {
      return new IDLType(paramClass, new String[] { "java", "io" }, "Serializable");
    }
    if (paramClass == Externalizable.class) {
      return new IDLType(paramClass, new String[] { "java", "io" }, "Externalizable");
    }
    if (paramClass == Remote.class) {
      return new IDLType(paramClass, new String[] { "java", "rmi" }, "Remote");
    }
    if (paramClass == org.omg.CORBA.Object.class) {
      return new IDLType(paramClass, "Object");
    }
    return null;
  }
  
  private void validateExceptions(Method paramMethod)
    throws IDLTypeException
  {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    int i = 0;
    Class localClass;
    for (int j = 0; j < arrayOfClass.length; j++)
    {
      localClass = arrayOfClass[j];
      if (isRemoteExceptionOrSuperClass(localClass))
      {
        i = 1;
        break;
      }
    }
    if (i == 0)
    {
      String str1 = "Method '" + paramMethod + "' must throw at least one exception of type java.rmi.RemoteException or one of its super-classes";
      throw new IDLTypeException(str1);
    }
    for (int k = 0; k < arrayOfClass.length; k++)
    {
      localClass = arrayOfClass[k];
      if ((isCheckedException(localClass)) && (!isValue(localClass)) && (!isRemoteException(localClass)))
      {
        String str2 = "Exception '" + localClass + "' on method '" + paramMethod + "' is not a allowed RMI/IIOP exception type";
        throw new IDLTypeException(str2);
      }
    }
  }
  
  private boolean validPropertyExceptions(Method paramMethod)
  {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      Class localClass = arrayOfClass[i];
      if ((isCheckedException(localClass)) && (!isRemoteException(localClass))) {
        return false;
      }
    }
    return true;
  }
  
  private boolean isRemoteExceptionOrSuperClass(Class paramClass)
  {
    return (paramClass == RemoteException.class) || (paramClass == IOException.class) || (paramClass == Exception.class) || (paramClass == Throwable.class);
  }
  
  private void validateDirectInterfaces(Class paramClass)
    throws IDLTypeException
  {
    Class[] arrayOfClass = paramClass.getInterfaces();
    if (arrayOfClass.length < 2) {
      return;
    }
    HashSet localHashSet1 = new HashSet();
    HashSet localHashSet2 = new HashSet();
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      Class localClass = arrayOfClass[i];
      Method[] arrayOfMethod = localClass.getMethods();
      localHashSet2.clear();
      for (int j = 0; j < arrayOfMethod.length; j++) {
        localHashSet2.add(arrayOfMethod[j].getName());
      }
      Iterator localIterator = localHashSet2.iterator();
      while (localIterator.hasNext())
      {
        String str1 = (String)localIterator.next();
        if (localHashSet1.contains(str1))
        {
          String str2 = "Class " + paramClass + " inherits method " + str1 + " from multiple direct interfaces.";
          throw new IDLTypeException(str2);
        }
        localHashSet1.add(str1);
      }
    }
  }
  
  private void validateConstants(final Class paramClass)
    throws IDLTypeException
  {
    Field[] arrayOfField = null;
    Object localObject;
    try
    {
      arrayOfField = (Field[])AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          return paramClass.getFields();
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      localObject = new IDLTypeException();
      ((IDLTypeException)localObject).initCause(localPrivilegedActionException);
      throw ((Throwable)localObject);
    }
    for (int i = 0; i < arrayOfField.length; i++)
    {
      localObject = arrayOfField[i];
      Class localClass = ((Field)localObject).getType();
      if ((localClass != String.class) && (!isPrimitive(localClass)))
      {
        String str = "Constant field '" + ((Field)localObject).getName() + "' in class '" + ((Field)localObject).getDeclaringClass().getName() + "' has invalid type' " + ((Field)localObject).getType() + "'. Constants in RMI/IIOP interfaces can only have primitive types and java.lang.String types.";
        throw new IDLTypeException(str);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\IDLTypesUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */