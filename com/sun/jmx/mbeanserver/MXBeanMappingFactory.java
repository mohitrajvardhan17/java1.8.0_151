package com.sun.jmx.mbeanserver;

import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;

public abstract class MXBeanMappingFactory
{
  public static final MXBeanMappingFactory DEFAULT = new DefaultMXBeanMappingFactory();
  
  protected MXBeanMappingFactory() {}
  
  public abstract MXBeanMapping mappingForType(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory)
    throws OpenDataException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\MXBeanMappingFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */