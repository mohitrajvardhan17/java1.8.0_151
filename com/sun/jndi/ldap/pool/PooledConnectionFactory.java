package com.sun.jndi.ldap.pool;

import javax.naming.NamingException;

public abstract interface PooledConnectionFactory
{
  public abstract PooledConnection createPooledConnection(PoolCallback paramPoolCallback)
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\pool\PooledConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */