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
@WebServiceFeatureAnnotation(id="http://www.w3.org/2005/08/addressing/module", bean=AddressingFeature.class)
public @interface Addressing
{
  boolean enabled() default true;
  
  boolean required() default false;
  
  AddressingFeature.Responses responses() default AddressingFeature.Responses.ALL;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\soap\Addressing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */