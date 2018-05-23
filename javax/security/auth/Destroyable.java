package javax.security.auth;

public abstract interface Destroyable
{
  public void destroy()
    throws DestroyFailedException
  {
    throw new DestroyFailedException();
  }
  
  public boolean isDestroyed()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\Destroyable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */