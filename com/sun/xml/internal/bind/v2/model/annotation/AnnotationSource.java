package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;

public abstract interface AnnotationSource
{
  public abstract <A extends Annotation> A readAnnotation(Class<A> paramClass);
  
  public abstract boolean hasAnnotation(Class<? extends Annotation> paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\AnnotationSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */