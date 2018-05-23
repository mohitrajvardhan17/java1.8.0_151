package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Environment;

public class EnvironmentImpl
  extends Environment
{
  private Exception _exc;
  
  public EnvironmentImpl() {}
  
  public Exception exception()
  {
    return _exc;
  }
  
  public void exception(Exception paramException)
  {
    _exc = paramException;
  }
  
  public void clear()
  {
    _exc = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\EnvironmentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */