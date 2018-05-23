package com.sun.xml.internal.ws.dump;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WebServiceFeatureAnnotation(id="com.sun.xml.internal.ws.messagedump.MessageDumpingFeature", bean=MessageDumpingFeature.class)
public @interface MessageDumping
{
  boolean enabled() default true;
  
  String messageLoggingRoot() default "com.sun.xml.internal.ws.messagedump";
  
  String messageLoggingLevel() default "FINE";
  
  boolean storeMessages() default false;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\dump\MessageDumping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */