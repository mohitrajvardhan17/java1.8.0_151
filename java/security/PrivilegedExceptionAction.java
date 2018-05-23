package java.security;

public abstract interface PrivilegedExceptionAction<T>
{
  public abstract T run()
    throws Exception;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\PrivilegedExceptionAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */