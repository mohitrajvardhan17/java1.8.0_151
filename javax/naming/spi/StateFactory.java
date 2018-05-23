package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public abstract interface StateFactory
{
  public abstract Object getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\spi\StateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */