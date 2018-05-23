package javax.xml.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD})
public @interface XmlAnyElement
{
  boolean lax() default false;
  
  Class<? extends DomHandler> value() default W3CDomHandler.class;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\XmlAnyElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */