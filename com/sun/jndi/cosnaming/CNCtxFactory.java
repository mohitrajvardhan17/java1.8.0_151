package com.sun.jndi.cosnaming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class CNCtxFactory
  implements InitialContextFactory
{
  public CNCtxFactory() {}
  
  public Context getInitialContext(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    return new CNCtx(paramHashtable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\cosnaming\CNCtxFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */