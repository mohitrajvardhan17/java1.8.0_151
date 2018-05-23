package com.sun.jmx.remote.internal;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

public abstract interface RMIExporter
{
  public static final String EXPORTER_ATTRIBUTE = "com.sun.jmx.remote.rmi.exporter";
  
  public abstract Remote exportObject(Remote paramRemote, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
    throws RemoteException;
  
  public abstract boolean unexportObject(Remote paramRemote, boolean paramBoolean)
    throws NoSuchObjectException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\RMIExporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */