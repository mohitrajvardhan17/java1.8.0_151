package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;

public abstract interface InitialContextFactory
{
  public abstract Context getInitialContext(Hashtable<?, ?> paramHashtable)
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\spi\InitialContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */