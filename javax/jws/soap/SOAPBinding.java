package javax.jws.soap;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD})
public @interface SOAPBinding
{
  Style style() default Style.DOCUMENT;
  
  Use use() default Use.LITERAL;
  
  ParameterStyle parameterStyle() default ParameterStyle.WRAPPED;
  
  public static enum ParameterStyle
  {
    BARE,  WRAPPED;
    
    private ParameterStyle() {}
  }
  
  public static enum Style
  {
    DOCUMENT,  RPC;
    
    private Style() {}
  }
  
  public static enum Use
  {
    LITERAL,  ENCODED;
    
    private Use() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\jws\soap\SOAPBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */