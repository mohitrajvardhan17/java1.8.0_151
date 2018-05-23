package javax.naming;

import java.util.Enumeration;

public abstract interface NamingEnumeration<T>
  extends Enumeration<T>
{
  public abstract T next()
    throws NamingException;
  
  public abstract boolean hasMore()
    throws NamingException;
  
  public abstract void close()
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\NamingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */