package com.sun.corba.se.impl.orbutil.closure;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public class Constant
  implements Closure
{
  private Object value;
  
  public Constant(Object paramObject)
  {
    value = paramObject;
  }
  
  public Object evaluate()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\closure\Constant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */