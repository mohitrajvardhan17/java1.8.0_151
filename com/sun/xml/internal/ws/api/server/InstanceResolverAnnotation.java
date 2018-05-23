package com.sun.xml.internal.ws.api.server;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InstanceResolverAnnotation
{
  Class<? extends InstanceResolver> value();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\InstanceResolverAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */