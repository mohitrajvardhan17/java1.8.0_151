package com.sun.jndi.rmi.registry;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.naming.NamingException;
import javax.naming.Reference;

public class ReferenceWrapper
  extends UnicastRemoteObject
  implements RemoteReference
{
  protected Reference wrappee;
  private static final long serialVersionUID = 6078186197417641456L;
  
  public ReferenceWrapper(Reference paramReference)
    throws NamingException, RemoteException
  {
    wrappee = paramReference;
  }
  
  public Reference getReference()
    throws RemoteException
  {
    return wrappee;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\rmi\registry\ReferenceWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */