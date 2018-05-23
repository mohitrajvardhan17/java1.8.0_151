package sun.java2d;

public final class StateTrackableDelegate
  implements StateTrackable
{
  public static final StateTrackableDelegate UNTRACKABLE_DELEGATE = new StateTrackableDelegate(StateTrackable.State.UNTRACKABLE);
  public static final StateTrackableDelegate IMMUTABLE_DELEGATE = new StateTrackableDelegate(StateTrackable.State.IMMUTABLE);
  private StateTrackable.State theState;
  StateTracker theTracker;
  private int numDynamicAgents;
  
  public static StateTrackableDelegate createInstance(StateTrackable.State paramState)
  {
    switch (paramState)
    {
    case UNTRACKABLE: 
      return UNTRACKABLE_DELEGATE;
    case STABLE: 
      return new StateTrackableDelegate(StateTrackable.State.STABLE);
    case DYNAMIC: 
      return new StateTrackableDelegate(StateTrackable.State.DYNAMIC);
    case IMMUTABLE: 
      return IMMUTABLE_DELEGATE;
    }
    throw new InternalError("unknown state");
  }
  
  private StateTrackableDelegate(StateTrackable.State paramState)
  {
    theState = paramState;
  }
  
  public StateTrackable.State getState()
  {
    return theState;
  }
  
  public synchronized StateTracker getStateTracker()
  {
    Object localObject = theTracker;
    if (localObject == null)
    {
      switch (theState)
      {
      case IMMUTABLE: 
        localObject = StateTracker.ALWAYS_CURRENT;
        break;
      case STABLE: 
        localObject = new StateTracker()
        {
          public boolean isCurrent()
          {
            return theTracker == this;
          }
        };
        break;
      case UNTRACKABLE: 
      case DYNAMIC: 
        localObject = StateTracker.NEVER_CURRENT;
      }
      theTracker = ((StateTracker)localObject);
    }
    return (StateTracker)localObject;
  }
  
  public synchronized void setImmutable()
  {
    if ((theState == StateTrackable.State.UNTRACKABLE) || (theState == StateTrackable.State.DYNAMIC)) {
      throw new IllegalStateException("UNTRACKABLE or DYNAMIC objects cannot become IMMUTABLE");
    }
    theState = StateTrackable.State.IMMUTABLE;
    theTracker = null;
  }
  
  public synchronized void setUntrackable()
  {
    if (theState == StateTrackable.State.IMMUTABLE) {
      throw new IllegalStateException("IMMUTABLE objects cannot become UNTRACKABLE");
    }
    theState = StateTrackable.State.UNTRACKABLE;
    theTracker = null;
  }
  
  public synchronized void addDynamicAgent()
  {
    if (theState == StateTrackable.State.IMMUTABLE) {
      throw new IllegalStateException("Cannot change state from IMMUTABLE");
    }
    numDynamicAgents += 1;
    if (theState == StateTrackable.State.STABLE)
    {
      theState = StateTrackable.State.DYNAMIC;
      theTracker = null;
    }
  }
  
  protected synchronized void removeDynamicAgent()
  {
    if ((--numDynamicAgents == 0) && (theState == StateTrackable.State.DYNAMIC))
    {
      theState = StateTrackable.State.STABLE;
      theTracker = null;
    }
  }
  
  public final void markDirty()
  {
    theTracker = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\StateTrackableDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */