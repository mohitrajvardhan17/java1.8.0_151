package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class FieldGetter
  extends PropertyGetterBase
{
  protected Field field;
  
  public FieldGetter(Field paramField)
  {
    field = paramField;
    type = paramField.getType();
  }
  
  public Field getField()
  {
    return field;
  }
  
  public Object get(Object paramObject)
  {
    if (field.isAccessible())
    {
      try
      {
        return field.get(paramObject);
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
    else
    {
      PrivilegedGetter localPrivilegedGetter = new PrivilegedGetter(field, paramObject);
      try
      {
        AccessController.doPrivileged(localPrivilegedGetter);
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        localPrivilegedActionException.printStackTrace();
      }
      return value;
    }
    return null;
  }
  
  public <A> A getAnnotation(Class<A> paramClass)
  {
    Class<A> localClass = paramClass;
    return field.getAnnotation(localClass);
  }
  
  static class PrivilegedGetter
    implements PrivilegedExceptionAction
  {
    private Object value;
    private Field field;
    private Object instance;
    
    public PrivilegedGetter(Field paramField, Object paramObject)
    {
      field = paramField;
      instance = paramObject;
    }
    
    public Object run()
      throws IllegalAccessException
    {
      if (!field.isAccessible()) {
        field.setAccessible(true);
      }
      value = field.get(instance);
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\FieldGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */