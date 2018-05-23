package com.sun.jndi.ldap.pool;

final class ConnectionsRef
{
  private final Connections conns;
  
  ConnectionsRef(Connections paramConnections)
  {
    conns = paramConnections;
  }
  
  Connections getConnections()
  {
    return conns;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\pool\ConnectionsRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */