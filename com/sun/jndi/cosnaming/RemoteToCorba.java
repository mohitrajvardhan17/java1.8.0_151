package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.rmi.Remote;
import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.StateFactory;

public class RemoteToCorba
  implements StateFactory
{
  public RemoteToCorba() {}
  
  public Object getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if ((paramObject instanceof org.omg.CORBA.Object)) {
      return null;
    }
    if ((paramObject instanceof Remote)) {
      try
      {
        return CorbaUtils.remoteToCorba((Remote)paramObject, _orb);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new ConfigurationException("javax.rmi packages not available");
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\cosnaming\RemoteToCorba.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */