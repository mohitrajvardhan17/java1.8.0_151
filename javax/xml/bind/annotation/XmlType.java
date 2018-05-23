package javax.xml.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface XmlType
{
  String name() default "##default";
  
  String[] propOrder() default {""};
  
  String namespace() default "##default";
  
  Class factoryClass() default DEFAULT.class;
  
  String factoryMethod() default "";
  
  public static final class DEFAULT
  {
    public DEFAULT() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\XmlType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */