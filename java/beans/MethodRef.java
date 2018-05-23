package java.beans;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;

final class MethodRef
{
  private String signature;
  private SoftReference<Method> methodRef;
  private WeakReference<Class<?>> typeRef;
  
  MethodRef() {}
  
  void set(Method paramMethod)
  {
    if (paramMethod == null)
    {
      signature = null;
      methodRef = null;
      typeRef = null;
    }
    else
    {
      signature = paramMethod.toGenericString();
      methodRef = new SoftReference(paramMethod);
      typeRef = new WeakReference(paramMethod.getDeclaringClass());
    }
  }
  
  boolean isSet()
  {
    return methodRef != null;
  }
  
  Method get()
  {
    if (methodRef == null) {
      return null;
    }
    Method localMethod = (Method)methodRef.get();
    if (localMethod == null)
    {
      localMethod = find((Class)typeRef.get(), signature);
      if (localMethod == null)
      {
        signature = null;
        methodRef = null;
        typeRef = null;
      }
      else
      {
        methodRef = new SoftReference(localMethod);
      }
    }
    return ReflectUtil.isPackageAccessible(localMethod.getDeclaringClass()) ? localMethod : null;
  }
  
  private static Method find(Class<?> paramClass, String paramString)
  {
    if (paramClass != null) {
      for (Method localMethod : paramClass.getMethods()) {
        if ((paramClass.equals(localMethod.getDeclaringClass())) && (localMethod.toGenericString().equals(paramString))) {
          return localMethod;
        }
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\MethodRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */