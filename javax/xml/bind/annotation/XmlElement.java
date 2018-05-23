package javax.xml.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.PARAMETER})
public @interface XmlElement
{
  String name() default "##default";
  
  boolean nillable() default false;
  
  boolean required() default false;
  
  String namespace() default "##default";
  
  String defaultValue() default "\000";
  
  Class type() default DEFAULT.class;
  
  public static final class DEFAULT
  {
    public DEFAULT() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\XmlElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */