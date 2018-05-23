package javax.xml.ws;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceRef
{
  String name() default "";
  
  Class<?> type() default Object.class;
  
  String mappedName() default "";
  
  Class<? extends Service> value() default Service.class;
  
  String wsdlLocation() default "";
  
  String lookup() default "";
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\WebServiceRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */