package javax.naming.event;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

public abstract interface EventDirContext
  extends EventContext, DirContext
{
  public abstract void addNamingListener(Name paramName, String paramString, SearchControls paramSearchControls, NamingListener paramNamingListener)
    throws NamingException;
  
  public abstract void addNamingListener(String paramString1, String paramString2, SearchControls paramSearchControls, NamingListener paramNamingListener)
    throws NamingException;
  
  public abstract void addNamingListener(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, NamingListener paramNamingListener)
    throws NamingException;
  
  public abstract void addNamingListener(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls, NamingListener paramNamingListener)
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\event\EventDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */