package sun.rmi.transport;

import java.lang.ref.ReferenceQueue;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.dgc.VMID;
import java.rmi.server.ExportException;
import java.rmi.server.ObjID;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import sun.misc.GC;
import sun.misc.GC.LatencyRequest;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.security.action.GetLongAction;

public final class ObjectTable
{
  private static final long gcInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.server.gcInterval", 3600000L))).longValue();
  private static final Object tableLock = new Object();
  private static final Map<ObjectEndpoint, Target> objTable = new HashMap();
  private static final Map<WeakRef, Target> implTable = new HashMap();
  private static final Object keepAliveLock = new Object();
  private static int keepAliveCount = 0;
  private static Thread reaper = null;
  static final ReferenceQueue<Object> reapQueue = new ReferenceQueue();
  private static GC.LatencyRequest gcLatencyRequest = null;
  
  private ObjectTable() {}
  
  /* Error */
  static Target getTarget(ObjectEndpoint paramObjectEndpoint)
  {
    // Byte code:
    //   0: getstatic 218	sun/rmi/transport/ObjectTable:tableLock	Ljava/lang/Object;
    //   3: dup
    //   4: astore_1
    //   5: monitorenter
    //   6: getstatic 222	sun/rmi/transport/ObjectTable:objTable	Ljava/util/Map;
    //   9: aload_0
    //   10: invokeinterface 261 2 0
    //   15: checkcast 128	sun/rmi/transport/Target
    //   18: aload_1
    //   19: monitorexit
    //   20: areturn
    //   21: astore_2
    //   22: aload_1
    //   23: monitorexit
    //   24: aload_2
    //   25: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	26	0	paramObjectEndpoint	ObjectEndpoint
    //   4	19	1	Ljava/lang/Object;	Object
    //   21	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   6	20	21	finally
    //   21	24	21	finally
  }
  
  /* Error */
  public static Target getTarget(Remote paramRemote)
  {
    // Byte code:
    //   0: getstatic 218	sun/rmi/transport/ObjectTable:tableLock	Ljava/lang/Object;
    //   3: dup
    //   4: astore_1
    //   5: monitorenter
    //   6: getstatic 221	sun/rmi/transport/ObjectTable:implTable	Ljava/util/Map;
    //   9: new 130	sun/rmi/transport/WeakRef
    //   12: dup
    //   13: aload_0
    //   14: invokespecial 258	sun/rmi/transport/WeakRef:<init>	(Ljava/lang/Object;)V
    //   17: invokeinterface 261 2 0
    //   22: checkcast 128	sun/rmi/transport/Target
    //   25: aload_1
    //   26: monitorexit
    //   27: areturn
    //   28: astore_2
    //   29: aload_1
    //   30: monitorexit
    //   31: aload_2
    //   32: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	paramRemote	Remote
    //   4	26	1	Ljava/lang/Object;	Object
    //   28	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   6	27	28	finally
    //   28	31	28	finally
  }
  
  public static Remote getStub(Remote paramRemote)
    throws NoSuchObjectException
  {
    Target localTarget = getTarget(paramRemote);
    if (localTarget == null) {
      throw new NoSuchObjectException("object not exported");
    }
    return localTarget.getStub();
  }
  
  public static boolean unexportObject(Remote paramRemote, boolean paramBoolean)
    throws NoSuchObjectException
  {
    synchronized (tableLock)
    {
      Target localTarget = getTarget(paramRemote);
      if (localTarget == null) {
        throw new NoSuchObjectException("object not exported");
      }
      if (localTarget.unexport(paramBoolean))
      {
        removeTarget(localTarget);
        return true;
      }
      return false;
    }
  }
  
  static void putTarget(Target paramTarget)
    throws ExportException
  {
    ObjectEndpoint localObjectEndpoint = paramTarget.getObjectEndpoint();
    WeakRef localWeakRef = paramTarget.getWeakImpl();
    if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
      DGCImpl.dgcLog.log(Log.VERBOSE, "add object " + localObjectEndpoint);
    }
    synchronized (tableLock)
    {
      if (paramTarget.getImpl() != null)
      {
        if (objTable.containsKey(localObjectEndpoint)) {
          throw new ExportException("internal error: ObjID already in use");
        }
        if (implTable.containsKey(localWeakRef)) {
          throw new ExportException("object already exported");
        }
        objTable.put(localObjectEndpoint, paramTarget);
        implTable.put(localWeakRef, paramTarget);
        if (!paramTarget.isPermanent()) {
          incrementKeepAliveCount();
        }
      }
    }
  }
  
  private static void removeTarget(Target paramTarget)
  {
    ObjectEndpoint localObjectEndpoint = paramTarget.getObjectEndpoint();
    WeakRef localWeakRef = paramTarget.getWeakImpl();
    if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
      DGCImpl.dgcLog.log(Log.VERBOSE, "remove object " + localObjectEndpoint);
    }
    objTable.remove(localObjectEndpoint);
    implTable.remove(localWeakRef);
    paramTarget.markRemoved();
  }
  
  static void referenced(ObjID paramObjID, long paramLong, VMID paramVMID)
  {
    synchronized (tableLock)
    {
      ObjectEndpoint localObjectEndpoint = new ObjectEndpoint(paramObjID, Transport.currentTransport());
      Target localTarget = (Target)objTable.get(localObjectEndpoint);
      if (localTarget != null) {
        localTarget.referenced(paramLong, paramVMID);
      }
    }
  }
  
  static void unreferenced(ObjID paramObjID, long paramLong, VMID paramVMID, boolean paramBoolean)
  {
    synchronized (tableLock)
    {
      ObjectEndpoint localObjectEndpoint = new ObjectEndpoint(paramObjID, Transport.currentTransport());
      Target localTarget = (Target)objTable.get(localObjectEndpoint);
      if (localTarget != null) {
        localTarget.unreferenced(paramLong, paramVMID, paramBoolean);
      }
    }
  }
  
  static void incrementKeepAliveCount()
  {
    synchronized (keepAliveLock)
    {
      keepAliveCount += 1;
      if (reaper == null)
      {
        reaper = (Thread)AccessController.doPrivileged(new NewThreadAction(new Reaper(null), "Reaper", false));
        reaper.start();
      }
      if (gcLatencyRequest == null) {
        gcLatencyRequest = GC.requestLatency(gcInterval);
      }
    }
  }
  
  static void decrementKeepAliveCount()
  {
    synchronized (keepAliveLock)
    {
      keepAliveCount -= 1;
      if (keepAliveCount == 0)
      {
        if (reaper == null) {
          throw new AssertionError();
        }
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            ObjectTable.reaper.interrupt();
            return null;
          }
        });
        reaper = null;
        gcLatencyRequest.cancel();
        gcLatencyRequest = null;
      }
    }
  }
  
  private static class Reaper
    implements Runnable
  {
    private Reaper() {}
    
    public void run()
    {
      try
      {
        do
        {
          WeakRef localWeakRef = (WeakRef)ObjectTable.reapQueue.remove();
          synchronized (ObjectTable.tableLock)
          {
            Target localTarget = (Target)ObjectTable.implTable.get(localWeakRef);
            if (localTarget != null)
            {
              if (!localTarget.isEmpty()) {
                throw new Error("object with known references collected");
              }
              if (localTarget.isPermanent()) {
                throw new Error("permanent object collected");
              }
              ObjectTable.removeTarget(localTarget);
            }
          }
        } while (!Thread.interrupted());
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\ObjectTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */