package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.fsm.StateEngineImpl;

public class StateEngineFactory
{
  private StateEngineFactory() {}
  
  public static StateEngine create()
  {
    return new StateEngineImpl();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\StateEngineFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */