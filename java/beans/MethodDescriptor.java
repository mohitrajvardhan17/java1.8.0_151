package java.beans;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodDescriptor
  extends FeatureDescriptor
{
  private final MethodRef methodRef = new MethodRef();
  private String[] paramNames;
  private List<WeakReference<Class<?>>> params;
  private ParameterDescriptor[] parameterDescriptors;
  
  public MethodDescriptor(Method paramMethod)
  {
    this(paramMethod, null);
  }
  
  public MethodDescriptor(Method paramMethod, ParameterDescriptor[] paramArrayOfParameterDescriptor)
  {
    setName(paramMethod.getName());
    setMethod(paramMethod);
    parameterDescriptors = (paramArrayOfParameterDescriptor != null ? (ParameterDescriptor[])paramArrayOfParameterDescriptor.clone() : null);
  }
  
  public synchronized Method getMethod()
  {
    Method localMethod = methodRef.get();
    if (localMethod == null)
    {
      Class localClass = getClass0();
      String str = getName();
      if ((localClass != null) && (str != null))
      {
        Class[] arrayOfClass = getParams();
        if (arrayOfClass == null) {
          for (int i = 0; i < 3; i++)
          {
            localMethod = Introspector.findMethod(localClass, str, i, null);
            if (localMethod != null) {
              break;
            }
          }
        } else {
          localMethod = Introspector.findMethod(localClass, str, arrayOfClass.length, arrayOfClass);
        }
        setMethod(localMethod);
      }
    }
    return localMethod;
  }
  
  private synchronized void setMethod(Method paramMethod)
  {
    if (paramMethod == null) {
      return;
    }
    if (getClass0() == null) {
      setClass0(paramMethod.getDeclaringClass());
    }
    setParams(getParameterTypes(getClass0(), paramMethod));
    methodRef.set(paramMethod);
  }
  
  private synchronized void setParams(Class<?>[] paramArrayOfClass)
  {
    if (paramArrayOfClass == null) {
      return;
    }
    paramNames = new String[paramArrayOfClass.length];
    params = new ArrayList(paramArrayOfClass.length);
    for (int i = 0; i < paramArrayOfClass.length; i++)
    {
      paramNames[i] = paramArrayOfClass[i].getName();
      params.add(new WeakReference(paramArrayOfClass[i]));
    }
  }
  
  String[] getParamNames()
  {
    return paramNames;
  }
  
  private synchronized Class<?>[] getParams()
  {
    Class[] arrayOfClass = new Class[params.size()];
    for (int i = 0; i < params.size(); i++)
    {
      Reference localReference = (Reference)params.get(i);
      Class localClass = (Class)localReference.get();
      if (localClass == null) {
        return null;
      }
      arrayOfClass[i] = localClass;
    }
    return arrayOfClass;
  }
  
  public ParameterDescriptor[] getParameterDescriptors()
  {
    return parameterDescriptors != null ? (ParameterDescriptor[])parameterDescriptors.clone() : null;
  }
  
  private static Method resolve(Method paramMethod1, Method paramMethod2)
  {
    if (paramMethod1 == null) {
      return paramMethod2;
    }
    if (paramMethod2 == null) {
      return paramMethod1;
    }
    return (!paramMethod1.isSynthetic()) && (paramMethod2.isSynthetic()) ? paramMethod1 : paramMethod2;
  }
  
  MethodDescriptor(MethodDescriptor paramMethodDescriptor1, MethodDescriptor paramMethodDescriptor2)
  {
    super(paramMethodDescriptor1, paramMethodDescriptor2);
    methodRef.set(resolve(methodRef.get(), methodRef.get()));
    params = params;
    if (params != null) {
      params = params;
    }
    paramNames = paramNames;
    if (paramNames != null) {
      paramNames = paramNames;
    }
    parameterDescriptors = parameterDescriptors;
    if (parameterDescriptors != null) {
      parameterDescriptors = parameterDescriptors;
    }
  }
  
  MethodDescriptor(MethodDescriptor paramMethodDescriptor)
  {
    super(paramMethodDescriptor);
    methodRef.set(paramMethodDescriptor.getMethod());
    params = params;
    paramNames = paramNames;
    if (parameterDescriptors != null)
    {
      int i = parameterDescriptors.length;
      parameterDescriptors = new ParameterDescriptor[i];
      for (int j = 0; j < i; j++) {
        parameterDescriptors[j] = new ParameterDescriptor(parameterDescriptors[j]);
      }
    }
  }
  
  void appendTo(StringBuilder paramStringBuilder)
  {
    appendTo(paramStringBuilder, "method", methodRef.get());
    if (parameterDescriptors != null)
    {
      paramStringBuilder.append("; parameterDescriptors={");
      for (ParameterDescriptor localParameterDescriptor : parameterDescriptors) {
        paramStringBuilder.append(localParameterDescriptor).append(", ");
      }
      paramStringBuilder.setLength(paramStringBuilder.length() - 2);
      paramStringBuilder.append("}");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\MethodDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */