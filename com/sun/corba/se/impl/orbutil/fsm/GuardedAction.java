package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.sun.corba.se.spi.orbutil.fsm.GuardBase;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.State;

public class GuardedAction
{
  private static Guard trueGuard = new GuardBase("true")
  {
    public Guard.Result evaluate(FSM paramAnonymousFSM, Input paramAnonymousInput)
    {
      return Guard.Result.ENABLED;
    }
  };
  private Guard guard;
  private Action action;
  private State nextState;
  
  public GuardedAction(Action paramAction, State paramState)
  {
    guard = trueGuard;
    action = paramAction;
    nextState = paramState;
  }
  
  public GuardedAction(Guard paramGuard, Action paramAction, State paramState)
  {
    guard = paramGuard;
    action = paramAction;
    nextState = paramState;
  }
  
  public String toString()
  {
    return "GuardedAction[action=" + action + " guard=" + guard + " nextState=" + nextState + "]";
  }
  
  public Action getAction()
  {
    return action;
  }
  
  public Guard getGuard()
  {
    return guard;
  }
  
  public State getNextState()
  {
    return nextState;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\fsm\GuardedAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */