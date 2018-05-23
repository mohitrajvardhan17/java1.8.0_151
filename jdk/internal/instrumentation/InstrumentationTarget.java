package jdk.internal.instrumentation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InstrumentationTarget
{
  String value();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\InstrumentationTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */