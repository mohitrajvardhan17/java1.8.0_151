package com.sun.corba.se.impl.orbutil.closure;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public class Future
  implements Closure
{
  private boolean evaluated = false;
  private Closure closure;
  private Object value;
  
  public Future(Closure paramClosure)
  {
    closure = paramClosure;
    value = null;
  }
  
  public synchronized Object evaluate()
  {
    if (!evaluated)
    {
      evaluated = true;
      value = closure.evaluate();
    }
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\closure\Future.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */