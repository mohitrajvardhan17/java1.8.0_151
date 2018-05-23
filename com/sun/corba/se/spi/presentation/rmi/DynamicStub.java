package com.sun.corba.se.spi.presentation.rmi;

import java.rmi.RemoteException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.OutputStream;

public abstract interface DynamicStub
  extends org.omg.CORBA.Object
{
  public abstract void setDelegate(Delegate paramDelegate);
  
  public abstract Delegate getDelegate();
  
  public abstract ORB getORB();
  
  public abstract String[] getTypeIds();
  
  public abstract void connect(ORB paramORB)
    throws RemoteException;
  
  public abstract boolean isLocal();
  
  public abstract OutputStream request(String paramString, boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\presentation\rmi\DynamicStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */