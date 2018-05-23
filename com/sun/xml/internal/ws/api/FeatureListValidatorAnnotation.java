package com.sun.xml.internal.ws.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureListValidatorAnnotation
{
  Class<? extends FeatureListValidator> bean();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\FeatureListValidatorAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */