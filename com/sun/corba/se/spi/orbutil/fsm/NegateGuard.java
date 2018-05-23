package com.sun.corba.se.spi.orbutil.fsm;

class NegateGuard
  implements Guard
{
  Guard guard;
  
  public NegateGuard(Guard paramGuard)
  {
    guard = paramGuard;
  }
  
  public Guard.Result evaluate(FSM paramFSM, Input paramInput)
  {
    return guard.evaluate(paramFSM, paramInput).complement();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\NegateGuard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */