package javax.xml.ws;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestWrapper
{
  String localName() default "";
  
  String targetNamespace() default "";
  
  String className() default "";
  
  String partName() default "";
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\RequestWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */