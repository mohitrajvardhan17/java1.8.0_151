package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

public class ReflectAnnotationReader
  implements MetadataReader
{
  public ReflectAnnotationReader() {}
  
  public Annotation[] getAnnotations(Method paramMethod)
  {
    return paramMethod.getAnnotations();
  }
  
  public Annotation[][] getParameterAnnotations(final Method paramMethod)
  {
    (Annotation[][])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Annotation[][] run()
      {
        return paramMethod.getParameterAnnotations();
      }
    });
  }
  
  public <A extends Annotation> A getAnnotation(final Class<A> paramClass, final Method paramMethod)
  {
    (Annotation)AccessController.doPrivileged(new PrivilegedAction()
    {
      public A run()
      {
        return paramMethod.getAnnotation(paramClass);
      }
    });
  }
  
  public <A extends Annotation> A getAnnotation(final Class<A> paramClass, final Class<?> paramClass1)
  {
    (Annotation)AccessController.doPrivileged(new PrivilegedAction()
    {
      public A run()
      {
        return paramClass1.getAnnotation(paramClass);
      }
    });
  }
  
  public Annotation[] getAnnotations(final Class<?> paramClass)
  {
    (Annotation[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Annotation[] run()
      {
        return paramClass.getAnnotations();
      }
    });
  }
  
  public void getProperties(Map<String, Object> paramMap, Class<?> paramClass) {}
  
  public void getProperties(Map<String, Object> paramMap, Method paramMethod) {}
  
  public void getProperties(Map<String, Object> paramMap, Method paramMethod, int paramInt) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\ReflectAnnotationReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */