package javax.xml.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.PACKAGE, java.lang.annotation.ElementType.TYPE})
public @interface XmlAccessorType
{
  XmlAccessType value() default XmlAccessType.PUBLIC_MEMBER;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\XmlAccessorType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */