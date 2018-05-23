package com.oracle.webservices.internal.api.databinding;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@WebServiceFeatureAnnotation(id="", bean=DatabindingModeFeature.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabindingMode
{
  String value();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\databinding\DatabindingMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */