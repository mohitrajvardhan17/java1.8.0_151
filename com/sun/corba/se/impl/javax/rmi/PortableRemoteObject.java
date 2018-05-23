package com.sun.corba.se.impl.javax.rmi;

import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.Externalizable;
import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;

public class PortableRemoteObject
  implements PortableRemoteObjectDelegate
{
  public PortableRemoteObject() {}
  
  public void exportObject(Remote paramRemote)
    throws RemoteException
  {
    if (paramRemote == null) {
      throw new NullPointerException("invalid argument");
    }
    if (Util.getTie(paramRemote) != null) {
      throw new ExportException(paramRemote.getClass().getName() + " already exported");
    }
    Tie localTie = Utility.loadTie(paramRemote);
    if (localTie != null) {
      Util.registerTarget(localTie, paramRemote);
    } else {
      UnicastRemoteObject.exportObject(paramRemote);
    }
  }
  
  public Remote toStub(Remote paramRemote)
    throws NoSuchObjectException
  {
    Remote localRemote = null;
    if (paramRemote == null) {
      throw new NullPointerException("invalid argument");
    }
    if (StubAdapter.isStub(paramRemote)) {
      return paramRemote;
    }
    if ((paramRemote instanceof RemoteStub)) {
      return paramRemote;
    }
    Tie localTie = Util.getTie(paramRemote);
    if (localTie != null) {
      localRemote = Utility.loadStub(localTie, null, null, true);
    } else if (Utility.loadTie(paramRemote) == null) {
      localRemote = RemoteObject.toStub(paramRemote);
    }
    if (localRemote == null) {
      throw new NoSuchObjectException("object not exported");
    }
    return localRemote;
  }
  
  public void unexportObject(Remote paramRemote)
    throws NoSuchObjectException
  {
    if (paramRemote == null) {
      throw new NullPointerException("invalid argument");
    }
    if ((StubAdapter.isStub(paramRemote)) || ((paramRemote instanceof RemoteStub))) {
      throw new NoSuchObjectException("Can only unexport a server object.");
    }
    Tie localTie = Util.getTie(paramRemote);
    if (localTie != null) {
      Util.unexportObject(paramRemote);
    } else if (Utility.loadTie(paramRemote) == null) {
      UnicastRemoteObject.unexportObject(paramRemote, true);
    } else {
      throw new NoSuchObjectException("Object not exported.");
    }
  }
  
  public Object narrow(Object paramObject, Class paramClass)
    throws ClassCastException
  {
    Object localObject1 = null;
    if (paramObject == null) {
      return null;
    }
    if (paramClass == null) {
      throw new NullPointerException("invalid argument");
    }
    try
    {
      if (paramClass.isAssignableFrom(paramObject.getClass())) {
        return paramObject;
      }
      if ((paramClass.isInterface()) && (paramClass != Serializable.class) && (paramClass != Externalizable.class))
      {
        org.omg.CORBA.Object localObject = (org.omg.CORBA.Object)paramObject;
        localObject2 = RepositoryId.createForAnyType(paramClass);
        if (localObject._is_a((String)localObject2)) {
          return Utility.loadStub(localObject, paramClass);
        }
        throw new ClassCastException("Object is not of remote type " + paramClass.getName());
      }
      throw new ClassCastException("Class " + paramClass.getName() + " is not a valid remote interface");
    }
    catch (Exception localException)
    {
      Object localObject2 = new ClassCastException();
      ((ClassCastException)localObject2).initCause(localException);
      throw ((Throwable)localObject2);
    }
  }
  
  public void connect(Remote paramRemote1, Remote paramRemote2)
    throws RemoteException
  {
    if ((paramRemote1 == null) || (paramRemote2 == null)) {
      throw new NullPointerException("invalid argument");
    }
    ORB localORB1 = null;
    try
    {
      if (StubAdapter.isStub(paramRemote2))
      {
        localORB1 = StubAdapter.getORB(paramRemote2);
      }
      else
      {
        Tie localTie1 = Util.getTie(paramRemote2);
        if (localTie1 != null) {
          localORB1 = localTie1.orb();
        }
      }
    }
    catch (SystemException localSystemException1)
    {
      throw new RemoteException("'source' object not connected", localSystemException1);
    }
    int i = 0;
    Tie localTie2 = null;
    if (StubAdapter.isStub(paramRemote1))
    {
      i = 1;
    }
    else
    {
      localTie2 = Util.getTie(paramRemote1);
      if (localTie2 != null) {
        i = 1;
      }
    }
    if (i == 0)
    {
      if (localORB1 != null) {
        throw new RemoteException("'source' object exported to IIOP, 'target' is JRMP");
      }
    }
    else
    {
      if (localORB1 == null) {
        throw new RemoteException("'source' object is JRMP, 'target' is IIOP");
      }
      try
      {
        if (localTie2 != null) {
          try
          {
            ORB localORB2 = localTie2.orb();
            if (localORB2 == localORB1) {
              return;
            }
            throw new RemoteException("'target' object was already connected");
          }
          catch (SystemException localSystemException2)
          {
            localTie2.orb(localORB1);
          }
        }
        StubAdapter.connect(paramRemote1, localORB1);
      }
      catch (SystemException localSystemException3)
      {
        throw new RemoteException("'target' object was already connected", localSystemException3);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\javax\rmi\PortableRemoteObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */