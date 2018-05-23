package com.sun.corba.se.spi.orbutil.fsm;

import java.io.PrintStream;

class TestAction1
  implements Action
{
  private State oldState;
  private Input label;
  private State newState;
  
  public void doIt(FSM paramFSM, Input paramInput)
  {
    System.out.println("TestAction1:");
    System.out.println("\tlabel    = " + label);
    System.out.println("\toldState = " + oldState);
    System.out.println("\tnewState = " + newState);
    if (label != paramInput) {
      throw new Error("Unexcepted Input " + paramInput);
    }
    if (oldState != paramFSM.getState()) {
      throw new Error("Unexpected old State " + paramFSM.getState());
    }
  }
  
  public TestAction1(State paramState1, Input paramInput, State paramState2)
  {
    oldState = paramState1;
    newState = paramState2;
    label = paramInput;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\TestAction1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */