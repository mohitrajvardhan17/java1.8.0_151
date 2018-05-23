package javax.jws.soap;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
@Deprecated
public @interface SOAPMessageHandlers
{
  SOAPMessageHandler[] value();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\jws\soap\SOAPMessageHandlers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */