package com.sun.xml.internal.bind;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.bind.JAXBException;

public abstract interface AccessorFactory
{
  public abstract Accessor createFieldAccessor(Class paramClass, Field paramField, boolean paramBoolean)
    throws JAXBException;
  
  public abstract Accessor createPropertyAccessor(Class paramClass, Method paramMethod1, Method paramMethod2)
    throws JAXBException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\AccessorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */