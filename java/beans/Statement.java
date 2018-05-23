package java.beans;

import com.sun.beans.finder.ClassFinder;
import com.sun.beans.finder.ConstructorFinder;
import com.sun.beans.finder.MethodFinder;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.reflect.misc.MethodUtil;

public class Statement
{
  private static Object[] emptyArray = new Object[0];
  static ExceptionListener defaultExceptionListener = new ExceptionListener()
  {
    public void exceptionThrown(Exception paramAnonymousException)
    {
      System.err.println(paramAnonymousException);
      System.err.println("Continuing ...");
    }
  };
  private final AccessControlContext acc = AccessController.getContext();
  private final Object target;
  private final String methodName;
  private final Object[] arguments;
  ClassLoader loader;
  
  @ConstructorProperties({"target", "methodName", "arguments"})
  public Statement(Object paramObject, String paramString, Object[] paramArrayOfObject)
  {
    target = paramObject;
    methodName = paramString;
    arguments = (paramArrayOfObject == null ? emptyArray : (Object[])paramArrayOfObject.clone());
  }
  
  public Object getTarget()
  {
    return target;
  }
  
  public String getMethodName()
  {
    return methodName;
  }
  
  public Object[] getArguments()
  {
    return (Object[])arguments.clone();
  }
  
  public void execute()
    throws Exception
  {
    invoke();
  }
  
  Object invoke()
    throws Exception
  {
    AccessControlContext localAccessControlContext = acc;
    if ((localAccessControlContext == null) && (System.getSecurityManager() != null)) {
      throw new SecurityException("AccessControlContext is not set");
    }
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          return Statement.this.invokeInternal();
        }
      }, localAccessControlContext);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException.getException();
    }
  }
  
  private Object invokeInternal()
    throws Exception
  {
    Object localObject1 = getTarget();
    String str = getMethodName();
    if ((localObject1 == null) || (str == null)) {
      throw new NullPointerException((localObject1 == null ? "target" : "methodName") + " should not be null");
    }
    Object[] arrayOfObject = getArguments();
    if (arrayOfObject == null) {
      arrayOfObject = emptyArray;
    }
    if ((localObject1 == Class.class) && (str.equals("forName"))) {
      return ClassFinder.resolveClass((String)arrayOfObject[0], loader);
    }
    Class[] arrayOfClass = new Class[arrayOfObject.length];
    for (int i = 0; i < arrayOfObject.length; i++) {
      arrayOfClass[i] = (arrayOfObject[i] == null ? null : arrayOfObject[i].getClass());
    }
    Object localObject2 = null;
    if ((localObject1 instanceof Class))
    {
      if (str.equals("new")) {
        str = "newInstance";
      }
      if ((str.equals("newInstance")) && (((Class)localObject1).isArray()))
      {
        Object localObject3 = Array.newInstance(((Class)localObject1).getComponentType(), arrayOfObject.length);
        for (int k = 0; k < arrayOfObject.length; k++) {
          Array.set(localObject3, k, arrayOfObject[k]);
        }
        return localObject3;
      }
      if ((str.equals("newInstance")) && (arrayOfObject.length != 0))
      {
        if ((localObject1 == Character.class) && (arrayOfObject.length == 1) && (arrayOfClass[0] == String.class)) {
          return new Character(((String)arrayOfObject[0]).charAt(0));
        }
        try
        {
          localObject2 = ConstructorFinder.findConstructor((Class)localObject1, arrayOfClass);
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          localObject2 = null;
        }
      }
      if ((localObject2 == null) && (localObject1 != Class.class)) {
        localObject2 = getMethod((Class)localObject1, str, arrayOfClass);
      }
      if (localObject2 == null) {
        localObject2 = getMethod(Class.class, str, arrayOfClass);
      }
    }
    else
    {
      if ((localObject1.getClass().isArray()) && ((str.equals("set")) || (str.equals("get"))))
      {
        int j = ((Integer)arrayOfObject[0]).intValue();
        if (str.equals("get")) {
          return Array.get(localObject1, j);
        }
        Array.set(localObject1, j, arrayOfObject[1]);
        return null;
      }
      localObject2 = getMethod(localObject1.getClass(), str, arrayOfClass);
    }
    if (localObject2 != null) {
      try
      {
        if ((localObject2 instanceof Method)) {
          return MethodUtil.invoke((Method)localObject2, localObject1, arrayOfObject);
        }
        return ((Constructor)localObject2).newInstance(arrayOfObject);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new Exception("Statement cannot invoke: " + str + " on " + localObject1.getClass(), localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getTargetException();
        if ((localThrowable instanceof Exception)) {
          throw ((Exception)localThrowable);
        }
        throw localInvocationTargetException;
      }
    }
    throw new NoSuchMethodException(toString());
  }
  
  String instanceName(Object paramObject)
  {
    if (paramObject == null) {
      return "null";
    }
    if (paramObject.getClass() == String.class) {
      return "\"" + (String)paramObject + "\"";
    }
    return NameGenerator.unqualifiedClassName(paramObject.getClass());
  }
  
  public String toString()
  {
    Object localObject = getTarget();
    String str = getMethodName();
    Object[] arrayOfObject = getArguments();
    if (arrayOfObject == null) {
      arrayOfObject = emptyArray;
    }
    StringBuffer localStringBuffer = new StringBuffer(instanceName(localObject) + "." + str + "(");
    int i = arrayOfObject.length;
    for (int j = 0; j < i; j++)
    {
      localStringBuffer.append(instanceName(arrayOfObject[j]));
      if (j != i - 1) {
        localStringBuffer.append(", ");
      }
    }
    localStringBuffer.append(");");
    return localStringBuffer.toString();
  }
  
  static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
  {
    try
    {
      return MethodFinder.findMethod(paramClass, paramString, paramVarArgs);
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\Statement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */