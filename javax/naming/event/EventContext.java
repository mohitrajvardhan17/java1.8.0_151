package javax.naming.event;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public abstract interface EventContext
  extends Context
{
  public static final int OBJECT_SCOPE = 0;
  public static final int ONELEVEL_SCOPE = 1;
  public static final int SUBTREE_SCOPE = 2;
  
  public abstract void addNamingListener(Name paramName, int paramInt, NamingListener paramNamingListener)
    throws NamingException;
  
  public abstract void addNamingListener(String paramString, int paramInt, NamingListener paramNamingListener)
    throws NamingException;
  
  public abstract void removeNamingListener(NamingListener paramNamingListener)
    throws NamingException;
  
  public abstract boolean targetMustExist()
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\event\EventContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */