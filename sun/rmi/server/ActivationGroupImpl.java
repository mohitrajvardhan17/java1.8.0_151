package sun.rmi.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import sun.rmi.registry.RegistryImpl;

public class ActivationGroupImpl
  extends ActivationGroup
{
  private static final long serialVersionUID = 5758693559430427303L;
  private final Hashtable<ActivationID, ActiveEntry> active = new Hashtable();
  private boolean groupInactive = false;
  private final ActivationGroupID groupID;
  private final List<ActivationID> lockedIDs = new ArrayList();
  
  public ActivationGroupImpl(ActivationGroupID paramActivationGroupID, MarshalledObject<?> paramMarshalledObject)
    throws RemoteException
  {
    super(paramActivationGroupID);
    groupID = paramActivationGroupID;
    unexportObject(this, true);
    ServerSocketFactoryImpl localServerSocketFactoryImpl = new ServerSocketFactoryImpl(null);
    UnicastRemoteObject.exportObject(this, 0, null, localServerSocketFactoryImpl);
    if (System.getSecurityManager() == null) {
      try
      {
        System.setSecurityManager(new SecurityManager());
      }
      catch (Exception localException)
      {
        throw new RemoteException("unable to set security manager", localException);
      }
    }
  }
  
  private void acquireLock(ActivationID paramActivationID)
  {
    for (;;)
    {
      ActivationID localActivationID1;
      synchronized (lockedIDs)
      {
        int i = lockedIDs.indexOf(paramActivationID);
        if (i < 0)
        {
          lockedIDs.add(paramActivationID);
          return;
        }
        localActivationID1 = (ActivationID)lockedIDs.get(i);
      }
      synchronized (localActivationID1)
      {
        synchronized (lockedIDs)
        {
          int j = lockedIDs.indexOf(localActivationID1);
          if (j < 0) {
            continue;
          }
          ActivationID localActivationID2 = (ActivationID)lockedIDs.get(j);
          if (localActivationID2 != localActivationID1) {
            continue;
          }
        }
        try
        {
          localActivationID1.wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
  }
  
  private void releaseLock(ActivationID paramActivationID)
  {
    synchronized (lockedIDs)
    {
      paramActivationID = (ActivationID)lockedIDs.remove(lockedIDs.indexOf(paramActivationID));
    }
    synchronized (paramActivationID)
    {
      paramActivationID.notifyAll();
    }
  }
  
  public MarshalledObject<? extends Remote> newInstance(final ActivationID paramActivationID, final ActivationDesc paramActivationDesc)
    throws ActivationException, RemoteException
  {
    RegistryImpl.checkAccess("ActivationInstantiator.newInstance");
    if (!groupID.equals(paramActivationDesc.getGroupID())) {
      throw new ActivationException("newInstance in wrong group");
    }
    try
    {
      acquireLock(paramActivationID);
      synchronized (this)
      {
        if (groupInactive == true) {
          throw new InactiveGroupException("group is inactive");
        }
      }
      ??? = (ActiveEntry)active.get(paramActivationID);
      if (??? != null)
      {
        localObject2 = mobj;
        return (MarshalledObject<? extends Remote>)localObject2;
      }
      Object localObject2 = paramActivationDesc.getClassName();
      final Class localClass = RMIClassLoader.loadClass(paramActivationDesc.getLocation(), (String)localObject2).asSubclass(Remote.class);
      Remote localRemote = null;
      final Thread localThread = Thread.currentThread();
      final ClassLoader localClassLoader1 = localThread.getContextClassLoader();
      ClassLoader localClassLoader2 = localClass.getClassLoader();
      final ClassLoader localClassLoader3 = covers(localClassLoader2, localClassLoader1) ? localClassLoader2 : localClassLoader1;
      try
      {
        localRemote = (Remote)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Remote run()
            throws InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
          {
            Constructor localConstructor = localClass.getDeclaredConstructor(new Class[] { ActivationID.class, MarshalledObject.class });
            localConstructor.setAccessible(true);
            try
            {
              localThread.setContextClassLoader(localClassLoader3);
              Remote localRemote = (Remote)localConstructor.newInstance(new Object[] { paramActivationID, paramActivationDesc.getData() });
              return localRemote;
            }
            finally
            {
              localThread.setContextClassLoader(localClassLoader1);
            }
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        Exception localException2 = localPrivilegedActionException.getException();
        if ((localException2 instanceof InstantiationException)) {
          throw ((InstantiationException)localException2);
        }
        if ((localException2 instanceof NoSuchMethodException)) {
          throw ((NoSuchMethodException)localException2);
        }
        if ((localException2 instanceof IllegalAccessException)) {
          throw ((IllegalAccessException)localException2);
        }
        if ((localException2 instanceof InvocationTargetException)) {
          throw ((InvocationTargetException)localException2);
        }
        if ((localException2 instanceof RuntimeException)) {
          throw ((RuntimeException)localException2);
        }
        if ((localException2 instanceof Error)) {
          throw ((Error)localException2);
        }
      }
      ??? = new ActiveEntry(localRemote);
      active.put(paramActivationID, ???);
      MarshalledObject localMarshalledObject = mobj;
      return localMarshalledObject;
    }
    catch (NoSuchMethodException|NoSuchMethodError localNoSuchMethodException)
    {
      throw new ActivationException("Activatable object must provide an activation constructor", localNoSuchMethodException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new ActivationException("exception in object constructor", localInvocationTargetException.getTargetException());
    }
    catch (Exception localException1)
    {
      throw new ActivationException("unable to activate object", localException1);
    }
    finally
    {
      releaseLock(paramActivationID);
      checkInactiveGroup();
    }
  }
  
  public boolean inactiveObject(ActivationID paramActivationID)
    throws ActivationException, UnknownObjectException, RemoteException
  {
    try
    {
      acquireLock(paramActivationID);
      synchronized (this)
      {
        if (groupInactive == true) {
          throw new ActivationException("group is inactive");
        }
      }
      ??? = (ActiveEntry)active.get(paramActivationID);
      if (??? == null) {
        throw new UnknownObjectException("object not active");
      }
      try
      {
        if (!Activatable.unexportObject(impl, false))
        {
          boolean bool = false;
          return bool;
        }
      }
      catch (NoSuchObjectException localNoSuchObjectException) {}
      try
      {
        super.inactiveObject(paramActivationID);
      }
      catch (UnknownObjectException localUnknownObjectException) {}
      active.remove(paramActivationID);
    }
    finally
    {
      releaseLock(paramActivationID);
      checkInactiveGroup();
    }
    return true;
  }
  
  private void checkInactiveGroup()
  {
    int i = 0;
    synchronized (this)
    {
      if ((active.size() == 0) && (lockedIDs.size() == 0) && (!groupInactive))
      {
        groupInactive = true;
        i = 1;
      }
    }
    if (i != 0)
    {
      try
      {
        super.inactiveGroup();
      }
      catch (Exception localException) {}
      try
      {
        UnicastRemoteObject.unexportObject(this, true);
      }
      catch (NoSuchObjectException localNoSuchObjectException) {}
    }
  }
  
  public void activeObject(ActivationID paramActivationID, Remote paramRemote)
    throws ActivationException, UnknownObjectException, RemoteException
  {
    try
    {
      acquireLock(paramActivationID);
      synchronized (this)
      {
        if (groupInactive == true) {
          throw new ActivationException("group is inactive");
        }
      }
      if (!active.contains(paramActivationID))
      {
        ??? = new ActiveEntry(paramRemote);
        active.put(paramActivationID, ???);
        try
        {
          super.activeObject(paramActivationID, mobj);
        }
        catch (RemoteException localRemoteException) {}
      }
    }
    finally
    {
      releaseLock(paramActivationID);
      checkInactiveGroup();
    }
  }
  
  private static boolean covers(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
  {
    if (paramClassLoader2 == null) {
      return true;
    }
    if (paramClassLoader1 == null) {
      return false;
    }
    do
    {
      if (paramClassLoader1 == paramClassLoader2) {
        return true;
      }
      paramClassLoader1 = paramClassLoader1.getParent();
    } while (paramClassLoader1 != null);
    return false;
  }
  
  private static class ActiveEntry
  {
    Remote impl;
    MarshalledObject<Remote> mobj;
    
    ActiveEntry(Remote paramRemote)
      throws ActivationException
    {
      impl = paramRemote;
      try
      {
        mobj = new MarshalledObject(paramRemote);
      }
      catch (IOException localIOException)
      {
        throw new ActivationException("failed to marshal remote object", localIOException);
      }
    }
  }
  
  private static class ServerSocketFactoryImpl
    implements RMIServerSocketFactory
  {
    private ServerSocketFactoryImpl() {}
    
    public ServerSocket createServerSocket(int paramInt)
      throws IOException
    {
      RMISocketFactory localRMISocketFactory = RMISocketFactory.getSocketFactory();
      if (localRMISocketFactory == null) {
        localRMISocketFactory = RMISocketFactory.getDefaultSocketFactory();
      }
      return localRMISocketFactory.createServerSocket(paramInt);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\ActivationGroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */