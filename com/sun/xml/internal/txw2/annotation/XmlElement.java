package com.sun.xml.internal.txw2.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
public @interface XmlElement
{
  String value() default "";
  
  String ns() default "##default";
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\annotation\XmlElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */