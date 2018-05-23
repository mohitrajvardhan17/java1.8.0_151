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
@WebServiceFeatureAnnotation(id="http://jax-ws.dev.java.net/features/uses-jaxb-context", bean=UsesJAXBContextFeature.class)
public @interface UsesJAXBContext
{
  Class<? extends JAXBContextFactory> value();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\UsesJAXBContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */