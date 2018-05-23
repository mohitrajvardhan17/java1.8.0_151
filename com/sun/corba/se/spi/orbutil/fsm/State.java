package com.sun.corba.se.spi.orbutil.fsm;

public abstract interface State
{
  public abstract void preAction(FSM paramFSM);
  
  public abstract void postAction(FSM paramFSM);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\State.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */