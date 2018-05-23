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
@WebServiceFeatureAnnotation(id="http://jax-ws.java.net/features/serialization", bean=SerializationFeature.class)
public @interface Serialization
{
  String encoding() default "";
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\Serialization.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */