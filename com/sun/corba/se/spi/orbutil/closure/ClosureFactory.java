package com.sun.corba.se.spi.orbutil.closure;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import com.sun.corba.se.impl.orbutil.closure.Future;

public abstract class ClosureFactory
{
  private ClosureFactory() {}
  
  public static Closure makeConstant(Object paramObject)
  {
    return new Constant(paramObject);
  }
  
  public static Closure makeFuture(Closure paramClosure)
  {
    return new Future(paramClosure);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\closure\ClosureFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */