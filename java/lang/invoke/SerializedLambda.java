package java.lang.invoke;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;

public final class SerializedLambda
  implements Serializable
{
  private static final long serialVersionUID = 8025925345765570181L;
  private final Class<?> capturingClass;
  private final String functionalInterfaceClass;
  private final String functionalInterfaceMethodName;
  private final String functionalInterfaceMethodSignature;
  private final String implClass;
  private final String implMethodName;
  private final String implMethodSignature;
  private final int implMethodKind;
  private final String instantiatedMethodType;
  private final Object[] capturedArgs;
  
  public SerializedLambda(Class<?> paramClass, String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5, String paramString6, String paramString7, Object[] paramArrayOfObject)
  {
    capturingClass = paramClass;
    functionalInterfaceClass = paramString1;
    functionalInterfaceMethodName = paramString2;
    functionalInterfaceMethodSignature = paramString3;
    implMethodKind = paramInt;
    implClass = paramString4;
    implMethodName = paramString5;
    implMethodSignature = paramString6;
    instantiatedMethodType = paramString7;
    capturedArgs = ((Object[])((Object[])Objects.requireNonNull(paramArrayOfObject)).clone());
  }
  
  public String getCapturingClass()
  {
    return capturingClass.getName().replace('.', '/');
  }
  
  public String getFunctionalInterfaceClass()
  {
    return functionalInterfaceClass;
  }
  
  public String getFunctionalInterfaceMethodName()
  {
    return functionalInterfaceMethodName;
  }
  
  public String getFunctionalInterfaceMethodSignature()
  {
    return functionalInterfaceMethodSignature;
  }
  
  public String getImplClass()
  {
    return implClass;
  }
  
  public String getImplMethodName()
  {
    return implMethodName;
  }
  
  public String getImplMethodSignature()
  {
    return implMethodSignature;
  }
  
  public int getImplMethodKind()
  {
    return implMethodKind;
  }
  
  public final String getInstantiatedMethodType()
  {
    return instantiatedMethodType;
  }
  
  public int getCapturedArgCount()
  {
    return capturedArgs.length;
  }
  
  public Object getCapturedArg(int paramInt)
  {
    return capturedArgs[paramInt];
  }
  
  private Object readResolve()
    throws ReflectiveOperationException
  {
    try
    {
      Method localMethod = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Method run()
          throws Exception
        {
          Method localMethod = capturingClass.getDeclaredMethod("$deserializeLambda$", new Class[] { SerializedLambda.class });
          localMethod.setAccessible(true);
          return localMethod;
        }
      });
      return localMethod.invoke(null, new Object[] { this });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Exception localException = localPrivilegedActionException.getException();
      if ((localException instanceof ReflectiveOperationException)) {
        throw ((ReflectiveOperationException)localException);
      }
      if ((localException instanceof RuntimeException)) {
        throw ((RuntimeException)localException);
      }
      throw new RuntimeException("Exception in SerializedLambda.readResolve", localPrivilegedActionException);
    }
  }
  
  public String toString()
  {
    String str = MethodHandleInfo.referenceKindToString(implMethodKind);
    return String.format("SerializedLambda[%s=%s, %s=%s.%s:%s, %s=%s %s.%s:%s, %s=%s, %s=%d]", new Object[] { "capturingClass", capturingClass, "functionalInterfaceMethod", functionalInterfaceClass, functionalInterfaceMethodName, functionalInterfaceMethodSignature, "implementation", str, implClass, implMethodName, implMethodSignature, "instantiatedMethodType", instantiatedMethodType, "numCaptured", Integer.valueOf(capturedArgs.length) });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\SerializedLambda.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */