package sun.java2d;

public abstract interface StateTracker
{
  public static final StateTracker ALWAYS_CURRENT = new StateTracker()
  {
    public boolean isCurrent()
    {
      return true;
    }
  };
  public static final StateTracker NEVER_CURRENT = new StateTracker()
  {
    public boolean isCurrent()
    {
      return false;
    }
  };
  
  public abstract boolean isCurrent();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\StateTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */