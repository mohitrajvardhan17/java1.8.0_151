package com.sun.xml.internal.ws.developer;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id="http://java.sun.com/xml/ns/jaxws/2004/08/addressing", bean=MemberSubmissionAddressingFeature.class)
public @interface MemberSubmissionAddressing
{
  boolean enabled() default true;
  
  boolean required() default false;
  
  Validation validation() default Validation.LAX;
  
  public static enum Validation
  {
    LAX,  STRICT;
    
    private Validation() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\MemberSubmissionAddressing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */