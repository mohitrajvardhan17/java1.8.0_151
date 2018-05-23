package javax.xml.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface XmlElementDecl
{
  Class scope() default GLOBAL.class;
  
  String namespace() default "##default";
  
  String name();
  
  String substitutionHeadNamespace() default "##default";
  
  String substitutionHeadName() default "";
  
  String defaultValue() default "\000";
  
  public static final class GLOBAL
  {
    public GLOBAL() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\XmlElementDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */