package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.ws.server.DraconianValidationErrorHandler;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
@Documented
@WebServiceFeatureAnnotation(id="http://jax-ws.dev.java.net/features/schema-validation", bean=SchemaValidationFeature.class)
public @interface SchemaValidation
{
  Class<? extends ValidationErrorHandler> handler() default DraconianValidationErrorHandler.class;
  
  boolean inbound() default true;
  
  boolean outbound() default true;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\SchemaValidation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */