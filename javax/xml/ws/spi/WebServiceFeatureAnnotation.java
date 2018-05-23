package javax.xml.ws.spi;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.WebServiceFeature;

@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceFeatureAnnotation
{
  String id();
  
  Class<? extends WebServiceFeature> bean();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\spi\WebServiceFeatureAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */