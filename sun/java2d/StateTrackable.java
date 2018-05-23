package sun.java2d;

public abstract interface StateTrackable
{
  public abstract State getState();
  
  public abstract StateTracker getStateTracker();
  
  public static enum State
  {
    IMMUTABLE,  STABLE,  DYNAMIC,  UNTRACKABLE;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\StateTrackable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */