package com.sun.corba.se.spi.orbutil.fsm;

public abstract interface StateEngine
{
  public abstract StateEngine add(State paramState1, Input paramInput, Guard paramGuard, Action paramAction, State paramState2)
    throws IllegalStateException;
  
  public abstract StateEngine add(State paramState1, Input paramInput, Action paramAction, State paramState2)
    throws IllegalStateException;
  
  public abstract StateEngine setDefault(State paramState1, Action paramAction, State paramState2)
    throws IllegalStateException;
  
  public abstract StateEngine setDefault(State paramState1, State paramState2)
    throws IllegalStateException;
  
  public abstract StateEngine setDefault(State paramState)
    throws IllegalStateException;
  
  public abstract void setDefaultAction(Action paramAction)
    throws IllegalStateException;
  
  public abstract void done()
    throws IllegalStateException;
  
  public abstract FSM makeFSM(State paramState)
    throws IllegalStateException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\StateEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */