package java.lang.invoke;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@interface InjectedProfile {}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\InjectedProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */