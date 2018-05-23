package sun.tracing.dtrace;

import com.sun.tracing.ProbeName;
import com.sun.tracing.Provider;
import com.sun.tracing.dtrace.Attributes;
import com.sun.tracing.dtrace.DependencyClass;
import com.sun.tracing.dtrace.FunctionName;
import com.sun.tracing.dtrace.ModuleName;
import com.sun.tracing.dtrace.StabilityLevel;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import sun.misc.ProxyGenerator;
import sun.tracing.ProbeSkeleton;
import sun.tracing.ProviderSkeleton;

class DTraceProvider
  extends ProviderSkeleton
{
  private Activation activation;
  private Object proxy;
  private static final Class[] constructorParams = { InvocationHandler.class };
  private final String proxyClassNamePrefix = "$DTraceTracingProxy";
  static final String DEFAULT_MODULE = "java_tracing";
  static final String DEFAULT_FUNCTION = "unspecified";
  private static long nextUniqueNumber = 0L;
  
  private static synchronized long getUniqueNumber()
  {
    return nextUniqueNumber++;
  }
  
  protected ProbeSkeleton createProbe(Method paramMethod)
  {
    return new DTraceProbe(proxy, paramMethod);
  }
  
  DTraceProvider(Class<? extends Provider> paramClass)
  {
    super(paramClass);
  }
  
  void setProxy(Object paramObject)
  {
    proxy = paramObject;
  }
  
  void setActivation(Activation paramActivation)
  {
    activation = paramActivation;
  }
  
  public void dispose()
  {
    if (activation != null)
    {
      activation.disposeProvider(this);
      activation = null;
    }
    super.dispose();
  }
  
  public <T extends Provider> T newProxyInstance()
  {
    long l = getUniqueNumber();
    String str1 = "";
    if (!Modifier.isPublic(providerType.getModifiers()))
    {
      str2 = providerType.getName();
      int i = str2.lastIndexOf('.');
      str1 = i == -1 ? "" : str2.substring(0, i + 1);
    }
    String str2 = str1 + "$DTraceTracingProxy" + l;
    Class localClass = null;
    byte[] arrayOfByte = ProxyGenerator.generateProxyClass(str2, new Class[] { providerType });
    try
    {
      localClass = JVM.defineClass(providerType.getClassLoader(), str2, arrayOfByte, 0, arrayOfByte.length);
    }
    catch (ClassFormatError localClassFormatError)
    {
      throw new IllegalArgumentException(localClassFormatError.toString());
    }
    try
    {
      Constructor localConstructor = localClass.getConstructor(constructorParams);
      return (Provider)localConstructor.newInstance(new Object[] { this });
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new InternalError(localReflectiveOperationException.toString(), localReflectiveOperationException);
    }
  }
  
  protected void triggerProbe(Method paramMethod, Object[] paramArrayOfObject)
  {
    if (!$assertionsDisabled) {
      throw new AssertionError("This method should have been overridden by the JVM");
    }
  }
  
  public String getProviderName()
  {
    return super.getProviderName();
  }
  
  String getModuleName()
  {
    return getAnnotationString(providerType, ModuleName.class, "java_tracing");
  }
  
  static String getProbeName(Method paramMethod)
  {
    return getAnnotationString(paramMethod, ProbeName.class, paramMethod.getName());
  }
  
  static String getFunctionName(Method paramMethod)
  {
    return getAnnotationString(paramMethod, FunctionName.class, "unspecified");
  }
  
  DTraceProbe[] getProbes()
  {
    return (DTraceProbe[])probes.values().toArray(new DTraceProbe[0]);
  }
  
  StabilityLevel getNameStabilityFor(Class<? extends Annotation> paramClass)
  {
    Attributes localAttributes = (Attributes)getAnnotationValue(providerType, paramClass, "value", null);
    if (localAttributes == null) {
      return StabilityLevel.PRIVATE;
    }
    return localAttributes.name();
  }
  
  StabilityLevel getDataStabilityFor(Class<? extends Annotation> paramClass)
  {
    Attributes localAttributes = (Attributes)getAnnotationValue(providerType, paramClass, "value", null);
    if (localAttributes == null) {
      return StabilityLevel.PRIVATE;
    }
    return localAttributes.data();
  }
  
  DependencyClass getDependencyClassFor(Class<? extends Annotation> paramClass)
  {
    Attributes localAttributes = (Attributes)getAnnotationValue(providerType, paramClass, "value", null);
    if (localAttributes == null) {
      return DependencyClass.UNKNOWN;
    }
    return localAttributes.dependency();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\dtrace\DTraceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */