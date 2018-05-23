package com.sun.jndi.ldap.pool;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

class ConnectionsWeakRef
  extends WeakReference<ConnectionsRef>
{
  private final Connections conns;
  
  ConnectionsWeakRef(ConnectionsRef paramConnectionsRef, ReferenceQueue<? super ConnectionsRef> paramReferenceQueue)
  {
    super(paramConnectionsRef, paramReferenceQueue);
    conns = paramConnectionsRef.getConnections();
  }
  
  Connections getConnections()
  {
    return conns;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\pool\ConnectionsWeakRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */