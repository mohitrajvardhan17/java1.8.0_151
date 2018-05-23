package com.sun.corba.se.spi.orbutil.fsm;

public abstract interface FSM
{
  public abstract State getState();
  
  public abstract void doIt(Input paramInput);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\FSM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */