package com.sun.org.glassfish.external.probe.provider.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface Probe
{
  String name() default "";
  
  boolean hidden() default false;
  
  boolean self() default false;
  
  String providerName() default "";
  
  String moduleName() default "";
  
  boolean stateful() default false;
  
  String profileNames() default "";
  
  boolean statefulReturn() default false;
  
  boolean statefulException() default false;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\probe\provider\annotations\Probe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */