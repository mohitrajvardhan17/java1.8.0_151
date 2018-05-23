package javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource
{
  String name() default "";
  
  String lookup() default "";
  
  Class<?> type() default Object.class;
  
  AuthenticationType authenticationType() default AuthenticationType.CONTAINER;
  
  boolean shareable() default true;
  
  String mappedName() default "";
  
  String description() default "";
  
  public static enum AuthenticationType
  {
    CONTAINER,  APPLICATION;
    
    private AuthenticationType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\annotation\Resource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */