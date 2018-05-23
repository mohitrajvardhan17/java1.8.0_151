package jdk.internal.instrumentation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(TypeMappings.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeMapping
{
  String from();
  
  String to();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\TypeMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */