package javax.security.auth;

public abstract interface Refreshable
{
  public abstract boolean isCurrent();
  
  public abstract void refresh()
    throws RefreshFailedException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\Refreshable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */