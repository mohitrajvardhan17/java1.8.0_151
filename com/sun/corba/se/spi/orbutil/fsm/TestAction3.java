package com.sun.corba.se.spi.orbutil.fsm;

import java.io.PrintStream;

class TestAction3
  implements Action
{
  private State oldState;
  private Input label;
  
  public void doIt(FSM paramFSM, Input paramInput)
  {
    System.out.println("TestAction1:");
    System.out.println("\tlabel    = " + label);
    System.out.println("\toldState = " + oldState);
    if (label != paramInput) {
      throw new Error("Unexcepted Input " + paramInput);
    }
  }
  
  public TestAction3(State paramState, Input paramInput)
  {
    oldState = paramState;
    label = paramInput;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\TestAction3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */