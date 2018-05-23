package com.sun.jndi.rmi.registry;

import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.naming.NamingException;
import javax.naming.Reference;

public abstract interface RemoteReference
  extends Remote
{
  public abstract Reference getReference()
    throws NamingException, RemoteException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\rmi\registry\RemoteReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */