package com.sun.org.glassfish.gmbal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AMXMetadata
{
  @DescriptorKey("amx.isSingleton")
  boolean isSingleton() default false;
  
  @DescriptorKey("amx.group")
  String group() default "other";
  
  @DescriptorKey("amx.subTypes")
  String[] subTypes() default {};
  
  @DescriptorKey("amx.genericInterfaceName")
  String genericInterfaceName() default "com.sun.org.glassfish.admin.amx.core.AMXProxy";
  
  @DescriptorKey("immutableInfo")
  boolean immutableInfo() default true;
  
  @DescriptorKey("interfaceName")
  String interfaceClassName() default "";
  
  @DescriptorKey("type")
  String type() default "";
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\gmbal\AMXMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */