package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.fsm.GuardedAction;
import com.sun.corba.se.impl.orbutil.fsm.NameBase;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StateImpl
  extends NameBase
  implements State
{
  private Action defaultAction = null;
  private State defaultNextState;
  private Map inputToGuardedActions = new HashMap();
  
  public StateImpl(String paramString)
  {
    super(paramString);
  }
  
  public void preAction(FSM paramFSM) {}
  
  public void postAction(FSM paramFSM) {}
  
  public State getDefaultNextState()
  {
    return defaultNextState;
  }
  
  public void setDefaultNextState(State paramState)
  {
    defaultNextState = paramState;
  }
  
  public Action getDefaultAction()
  {
    return defaultAction;
  }
  
  public void setDefaultAction(Action paramAction)
  {
    defaultAction = paramAction;
  }
  
  public void addGuardedAction(Input paramInput, GuardedAction paramGuardedAction)
  {
    Object localObject = (Set)inputToGuardedActions.get(paramInput);
    if (localObject == null)
    {
      localObject = new HashSet();
      inputToGuardedActions.put(paramInput, localObject);
    }
    ((Set)localObject).add(paramGuardedAction);
  }
  
  public Set getGuardedActions(Input paramInput)
  {
    return (Set)inputToGuardedActions.get(paramInput);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\StateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */