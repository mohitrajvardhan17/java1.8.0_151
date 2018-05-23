package com.sun.corba.se.spi.orbutil.fsm;

import java.io.PrintStream;

class TestAction2
  implements Action
{
  private State oldState;
  private State newState;
  
  public void doIt(FSM paramFSM, Input paramInput)
  {
    System.out.println("TestAction2:");
    System.out.println("\toldState = " + oldState);
    System.out.println("\tnewState = " + newState);
    System.out.println("\tinput    = " + paramInput);
    if (oldState != paramFSM.getState()) {
      throw new Error("Unexpected old State " + paramFSM.getState());
    }
  }
  
  public TestAction2(State paramState1, State paramState2)
  {
    oldState = paramState1;
    newState = paramState2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\TestAction2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */