package com.sun.jndi.ldap.pool;

public abstract interface PoolCallback
{
  public abstract boolean releasePooledConnection(PooledConnection paramPooledConnection);
  
  public abstract boolean removePooledConnection(PooledConnection paramPooledConnection);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\pool\PoolCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */