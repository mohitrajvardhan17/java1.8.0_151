package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.fsm.StateEngineImpl;

public class FSMImpl
  implements FSM
{
  private boolean debug;
  private State state;
  private StateEngineImpl stateEngine;
  
  public FSMImpl(StateEngine paramStateEngine, State paramState)
  {
    this(paramStateEngine, paramState, false);
  }
  
  public FSMImpl(StateEngine paramStateEngine, State paramState, boolean paramBoolean)
  {
    state = paramState;
    stateEngine = ((StateEngineImpl)paramStateEngine);
    debug = paramBoolean;
  }
  
  public State getState()
  {
    return state;
  }
  
  public void doIt(Input paramInput)
  {
    stateEngine.doIt(this, paramInput, debug);
  }
  
  public void internalSetState(State paramState)
  {
    if (debug) {
      ORBUtility.dprint(this, "Calling internalSetState with nextState = " + paramState);
    }
    state = paramState;
    if (debug) {
      ORBUtility.dprint(this, "Exiting internalSetState with state = " + state);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\FSMImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */