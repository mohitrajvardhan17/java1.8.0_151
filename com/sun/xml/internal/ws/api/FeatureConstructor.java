package com.sun.xml.internal.ws.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.CONSTRUCTOR})
public @interface FeatureConstructor
{
  String[] value() default {};
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\FeatureConstructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */