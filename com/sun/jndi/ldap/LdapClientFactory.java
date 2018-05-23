package com.sun.jndi.ldap;

import com.sun.jndi.ldap.pool.PoolCallback;
import com.sun.jndi.ldap.pool.PooledConnection;
import com.sun.jndi.ldap.pool.PooledConnectionFactory;
import java.io.OutputStream;
import javax.naming.NamingException;

final class LdapClientFactory
  implements PooledConnectionFactory
{
  private final String host;
  private final int port;
  private final String socketFactory;
  private final int connTimeout;
  private final int readTimeout;
  private final OutputStream trace;
  
  LdapClientFactory(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream)
  {
    host = paramString1;
    port = paramInt1;
    socketFactory = paramString2;
    connTimeout = paramInt2;
    readTimeout = paramInt3;
    trace = paramOutputStream;
  }
  
  public PooledConnection createPooledConnection(PoolCallback paramPoolCallback)
    throws NamingException
  {
    return new LdapClient(host, port, socketFactory, connTimeout, readTimeout, trace, paramPoolCallback);
  }
  
  public String toString()
  {
    return host + ":" + port;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapClientFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */