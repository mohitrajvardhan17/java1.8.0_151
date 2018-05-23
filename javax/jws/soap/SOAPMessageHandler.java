package javax.jws.soap;

import java.lang.annotation.Annotation;

@Deprecated
public @interface SOAPMessageHandler
{
  String name() default "";
  
  String className();
  
  InitParam[] initParams() default {};
  
  String[] roles() default {};
  
  String[] headers() default {};
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\jws\soap\SOAPMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */