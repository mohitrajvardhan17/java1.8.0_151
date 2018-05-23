package java.rmi.activation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessController;
import sun.rmi.server.ActivationGroupImpl;
import sun.security.action.GetIntegerAction;

public abstract class ActivationGroup
  extends UnicastRemoteObject
  implements ActivationInstantiator
{
  private ActivationGroupID groupID;
  private ActivationMonitor monitor;
  private long incarnation;
  private static ActivationGroup currGroup;
  private static ActivationGroupID currGroupID;
  private static ActivationSystem currSystem;
  private static boolean canCreate = true;
  private static final long serialVersionUID = -7696947875314805420L;
  
  protected ActivationGroup(ActivationGroupID paramActivationGroupID)
    throws RemoteException
  {
    groupID = paramActivationGroupID;
  }
  
  public boolean inactiveObject(ActivationID paramActivationID)
    throws ActivationException, UnknownObjectException, RemoteException
  {
    getMonitor().inactiveObject(paramActivationID);
    return true;
  }
  
  public abstract void activeObject(ActivationID paramActivationID, Remote paramRemote)
    throws ActivationException, UnknownObjectException, RemoteException;
  
  public static synchronized ActivationGroup createGroup(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc, long paramLong)
    throws ActivationException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    if (currGroup != null) {
      throw new ActivationException("group already exists");
    }
    if (!canCreate) {
      throw new ActivationException("group deactivated and cannot be recreated");
    }
    try
    {
      String str = paramActivationGroupDesc.getClassName();
      Class localClass2 = ActivationGroupImpl.class;
      Class localClass1;
      if ((str == null) || (str.equals(localClass2.getName())))
      {
        localClass1 = localClass2;
      }
      else
      {
        try
        {
          localObject = RMIClassLoader.loadClass(paramActivationGroupDesc.getLocation(), str);
        }
        catch (Exception localException2)
        {
          throw new ActivationException("Could not load group implementation class", localException2);
        }
        if (ActivationGroup.class.isAssignableFrom((Class)localObject)) {
          localClass1 = ((Class)localObject).asSubclass(ActivationGroup.class);
        } else {
          throw new ActivationException("group not correct class: " + ((Class)localObject).getName());
        }
      }
      Object localObject = localClass1.getConstructor(new Class[] { ActivationGroupID.class, MarshalledObject.class });
      ActivationGroup localActivationGroup = (ActivationGroup)((Constructor)localObject).newInstance(new Object[] { paramActivationGroupID, paramActivationGroupDesc.getData() });
      currSystem = paramActivationGroupID.getSystem();
      incarnation = paramLong;
      monitor = currSystem.activeGroup(paramActivationGroupID, localActivationGroup, paramLong);
      currGroup = localActivationGroup;
      currGroupID = paramActivationGroupID;
      canCreate = false;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      localInvocationTargetException.getTargetException().printStackTrace();
      throw new ActivationException("exception in group constructor", localInvocationTargetException.getTargetException());
    }
    catch (ActivationException localActivationException)
    {
      throw localActivationException;
    }
    catch (Exception localException1)
    {
      throw new ActivationException("exception creating group", localException1);
    }
    return currGroup;
  }
  
  public static synchronized ActivationGroupID currentGroupID()
  {
    return currGroupID;
  }
  
  static synchronized ActivationGroupID internalCurrentGroupID()
    throws ActivationException
  {
    if (currGroupID == null) {
      throw new ActivationException("nonexistent group");
    }
    return currGroupID;
  }
  
  public static synchronized void setSystem(ActivationSystem paramActivationSystem)
    throws ActivationException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    if (currSystem != null) {
      throw new ActivationException("activation system already set");
    }
    currSystem = paramActivationSystem;
  }
  
  public static synchronized ActivationSystem getSystem()
    throws ActivationException
  {
    if (currSystem == null) {
      try
      {
        int i = ((Integer)AccessController.doPrivileged(new GetIntegerAction("java.rmi.activation.port", 1098))).intValue();
        currSystem = (ActivationSystem)Naming.lookup("//:" + i + "/java.rmi.activation.ActivationSystem");
      }
      catch (Exception localException)
      {
        throw new ActivationException("unable to obtain ActivationSystem", localException);
      }
    }
    return currSystem;
  }
  
  protected void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
    throws ActivationException, UnknownObjectException, RemoteException
  {
    getMonitor().activeObject(paramActivationID, paramMarshalledObject);
  }
  
  /* Error */
  protected void inactiveGroup()
    throws UnknownGroupException, RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 237	java/rmi/activation/ActivationGroup:getMonitor	()Ljava/rmi/activation/ActivationMonitor;
    //   4: aload_0
    //   5: getfield 214	java/rmi/activation/ActivationGroup:groupID	Ljava/rmi/activation/ActivationGroupID;
    //   8: aload_0
    //   9: getfield 210	java/rmi/activation/ActivationGroup:incarnation	J
    //   12: invokeinterface 246 4 0
    //   17: invokestatic 236	java/rmi/activation/ActivationGroup:destroyGroup	()V
    //   20: goto +9 -> 29
    //   23: astore_1
    //   24: invokestatic 236	java/rmi/activation/ActivationGroup:destroyGroup	()V
    //   27: aload_1
    //   28: athrow
    //   29: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	ActivationGroup
    //   23	5	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	17	23	finally
  }
  
  private ActivationMonitor getMonitor()
    throws RemoteException
  {
    synchronized (ActivationGroup.class)
    {
      if (monitor != null) {
        return monitor;
      }
    }
    throw new RemoteException("monitor not received");
  }
  
  private static synchronized void destroyGroup()
  {
    currGroup = null;
    currGroupID = null;
  }
  
  static synchronized ActivationGroup currentGroup()
    throws ActivationException
  {
    if (currGroup == null) {
      throw new ActivationException("group is not active");
    }
    return currGroup;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\ActivationGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */