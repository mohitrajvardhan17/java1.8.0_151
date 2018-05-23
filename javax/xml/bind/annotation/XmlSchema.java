package javax.xml.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.PACKAGE})
public @interface XmlSchema
{
  public static final String NO_LOCATION = "##generate";
  
  XmlNs[] xmlns() default {};
  
  String namespace() default "";
  
  XmlNsForm elementFormDefault() default XmlNsForm.UNSET;
  
  XmlNsForm attributeFormDefault() default XmlNsForm.UNSET;
  
  String location() default "##generate";
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\XmlSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */