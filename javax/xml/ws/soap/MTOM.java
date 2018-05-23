package javax.xml.ws.soap;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id="http://www.w3.org/2004/08/soap/features/http-optimization", bean=MTOMFeature.class)
public @interface MTOM
{
  boolean enabled() default true;
  
  int threshold() default 0;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\soap\MTOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */