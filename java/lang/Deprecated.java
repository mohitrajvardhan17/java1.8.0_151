package java.lang;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.LOCAL_VARIABLE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.PACKAGE, java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.TYPE})
public @interface Deprecated {}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Deprecated.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */