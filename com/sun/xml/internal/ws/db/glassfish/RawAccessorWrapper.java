package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.api.RawAccessor;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;

public class RawAccessorWrapper
  implements PropertyAccessor
{
  private RawAccessor accessor;
  
  public RawAccessorWrapper(RawAccessor paramRawAccessor)
  {
    accessor = paramRawAccessor;
  }
  
  public boolean equals(Object paramObject)
  {
    return accessor.equals(paramObject);
  }
  
  public Object get(Object paramObject)
    throws DatabindingException
  {
    try
    {
      return accessor.get(paramObject);
    }
    catch (AccessorException localAccessorException)
    {
      throw new DatabindingException(localAccessorException);
    }
  }
  
  public int hashCode()
  {
    return accessor.hashCode();
  }
  
  public void set(Object paramObject1, Object paramObject2)
    throws DatabindingException
  {
    try
    {
      accessor.set(paramObject1, paramObject2);
    }
    catch (AccessorException localAccessorException)
    {
      throw new DatabindingException(localAccessorException);
    }
  }
  
  public String toString()
  {
    return accessor.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\db\glassfish\RawAccessorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */