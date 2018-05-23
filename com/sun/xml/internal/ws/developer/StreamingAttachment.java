package com.sun.xml.internal.ws.developer;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
@Documented
@WebServiceFeatureAnnotation(id="http://jax-ws.dev.java.net/features/mime", bean=StreamingAttachmentFeature.class)
public @interface StreamingAttachment
{
  String dir() default "";
  
  boolean parseEagerly() default false;
  
  long memoryThreshold() default 1048576L;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\StreamingAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */