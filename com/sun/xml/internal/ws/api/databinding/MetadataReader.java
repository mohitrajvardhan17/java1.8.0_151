package com.sun.xml.internal.ws.api.databinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public abstract interface MetadataReader
{
  public abstract Annotation[] getAnnotations(Method paramMethod);
  
  public abstract Annotation[][] getParameterAnnotations(Method paramMethod);
  
  public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass, Method paramMethod);
  
  public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass, Class<?> paramClass1);
  
  public abstract Annotation[] getAnnotations(Class<?> paramClass);
  
  public abstract void getProperties(Map<String, Object> paramMap, Class<?> paramClass);
  
  public abstract void getProperties(Map<String, Object> paramMap, Method paramMethod);
  
  public abstract void getProperties(Map<String, Object> paramMap, Method paramMethod, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\databinding\MetadataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */