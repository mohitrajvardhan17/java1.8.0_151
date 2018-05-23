package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class FieldSetter
  extends PropertySetterBase
{
  protected Field field;
  
  public FieldSetter(Field paramField)
  {
    field = paramField;
    type = paramField.getType();
  }
  
  public Field getField()
  {
    return field;
  }
  
  public void set(final Object paramObject1, final Object paramObject2)
  {
    if (field.isAccessible()) {
      try
      {
        field.set(paramObject1, paramObject2);
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    } else {
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws IllegalAccessException
          {
            if (!field.isAccessible()) {
              field.setAccessible(true);
            }
            field.set(paramObject1, paramObject2);
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        localPrivilegedActionException.printStackTrace();
      }
    }
  }
  
  public <A> A getAnnotation(Class<A> paramClass)
  {
    Class<A> localClass = paramClass;
    return field.getAnnotation(localClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\FieldSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */