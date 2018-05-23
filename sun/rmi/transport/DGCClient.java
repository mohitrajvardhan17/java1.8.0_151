package sun.rmi.transport;

import java.io.InvalidClassException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.net.SocketPermission;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.dgc.DGC;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sun.misc.GC;
import sun.misc.GC.LatencyRequest;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.Util;
import sun.security.action.GetLongAction;

final class DGCClient
{
  private static long nextSequenceNum = Long.MIN_VALUE;
  private static VMID vmid = new VMID();
  private static final long leaseValue = ((Long)AccessController.doPrivileged(new GetLongAction("java.rmi.dgc.leaseValue", 600000L))).longValue();
  private static final long cleanInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.cleanInterval", 180000L))).longValue();
  private static final long gcInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.client.gcInterval", 3600000L))).longValue();
  private static final int dirtyFailureRetries = 5;
  private static final int cleanFailureRetries = 5;
  private static final ObjID[] emptyObjIDArray = new ObjID[0];
  private static final ObjID dgcID = new ObjID(2);
  private static final AccessControlContext SOCKET_ACC;
  
  private DGCClient() {}
  
  static void registerRefs(Endpoint paramEndpoint, List<LiveRef> paramList)
  {
    EndpointEntry localEndpointEntry;
    do
    {
      localEndpointEntry = EndpointEntry.lookup(paramEndpoint);
    } while (!localEndpointEntry.registerRefs(paramList));
  }
  
  private static synchronized long getNextSequenceNum()
  {
    return nextSequenceNum++;
  }
  
  private static long computeRenewTime(long paramLong1, long paramLong2)
  {
    return paramLong1 + paramLong2 / 2L;
  }
  
  static
  {
    Permissions localPermissions = new Permissions();
    localPermissions.add(new SocketPermission("*", "connect,resolve"));
    ProtectionDomain[] arrayOfProtectionDomain = { new ProtectionDomain(null, localPermissions) };
    SOCKET_ACC = new AccessControlContext(arrayOfProtectionDomain);
  }
  
  private static class EndpointEntry
  {
    private Endpoint endpoint;
    private DGC dgc;
    private Map<LiveRef, RefEntry> refTable = new HashMap(5);
    private Set<RefEntry> invalidRefs = new HashSet(5);
    private boolean removed = false;
    private long renewTime = Long.MAX_VALUE;
    private long expirationTime = Long.MIN_VALUE;
    private int dirtyFailures = 0;
    private long dirtyFailureStartTime;
    private long dirtyFailureDuration;
    private Thread renewCleanThread;
    private boolean interruptible = false;
    private ReferenceQueue<LiveRef> refQueue = new ReferenceQueue();
    private Set<CleanRequest> pendingCleans = new HashSet(5);
    private static Map<Endpoint, EndpointEntry> endpointTable = new HashMap(5);
    private static GC.LatencyRequest gcLatencyRequest = null;
    
    public static EndpointEntry lookup(Endpoint paramEndpoint)
    {
      synchronized (endpointTable)
      {
        EndpointEntry localEndpointEntry = (EndpointEntry)endpointTable.get(paramEndpoint);
        if (localEndpointEntry == null)
        {
          localEndpointEntry = new EndpointEntry(paramEndpoint);
          endpointTable.put(paramEndpoint, localEndpointEntry);
          if (gcLatencyRequest == null) {
            gcLatencyRequest = GC.requestLatency(DGCClient.gcInterval);
          }
        }
        return localEndpointEntry;
      }
    }
    
    private EndpointEntry(Endpoint paramEndpoint)
    {
      endpoint = paramEndpoint;
      try
      {
        LiveRef localLiveRef = new LiveRef(DGCClient.dgcID, paramEndpoint, false);
        dgc = ((DGC)Util.createProxy(DGCImpl.class, new UnicastRef(localLiveRef), true));
      }
      catch (RemoteException localRemoteException)
      {
        throw new Error("internal error creating DGC stub");
      }
      renewCleanThread = ((Thread)AccessController.doPrivileged(new NewThreadAction(new RenewCleanThread(null), "RenewClean-" + paramEndpoint, true)));
      renewCleanThread.start();
    }
    
    public boolean registerRefs(List<LiveRef> paramList)
    {
      assert (!Thread.holdsLock(this));
      HashSet localHashSet = null;
      long l;
      synchronized (this)
      {
        if (removed) {
          return false;
        }
        Iterator localIterator = paramList.iterator();
        while (localIterator.hasNext())
        {
          LiveRef localLiveRef1 = (LiveRef)localIterator.next();
          assert (localLiveRef1.getEndpoint().equals(endpoint));
          RefEntry localRefEntry = (RefEntry)refTable.get(localLiveRef1);
          if (localRefEntry == null)
          {
            LiveRef localLiveRef2 = (LiveRef)localLiveRef1.clone();
            localRefEntry = new RefEntry(localLiveRef2);
            refTable.put(localLiveRef2, localRefEntry);
            if (localHashSet == null) {
              localHashSet = new HashSet(5);
            }
            localHashSet.add(localRefEntry);
          }
          localRefEntry.addInstanceToRefSet(localLiveRef1);
        }
        if (localHashSet == null) {
          return true;
        }
        localHashSet.addAll(invalidRefs);
        invalidRefs.clear();
        l = DGCClient.access$300();
      }
      makeDirtyCall(localHashSet, l);
      return true;
    }
    
    private void removeRefEntry(RefEntry paramRefEntry)
    {
      assert (Thread.holdsLock(this));
      assert (!removed);
      assert (refTable.containsKey(paramRefEntry.getRef()));
      refTable.remove(paramRefEntry.getRef());
      invalidRefs.remove(paramRefEntry);
      if (refTable.isEmpty()) {
        synchronized (endpointTable)
        {
          endpointTable.remove(endpoint);
          Transport localTransport = endpoint.getOutboundTransport();
          localTransport.free(endpoint);
          if (endpointTable.isEmpty())
          {
            assert (gcLatencyRequest != null);
            gcLatencyRequest.cancel();
            gcLatencyRequest = null;
          }
          removed = true;
        }
      }
    }
    
    private void makeDirtyCall(Set<RefEntry> paramSet, long paramLong)
    {
      assert (!Thread.holdsLock(this));
      ObjID[] arrayOfObjID;
      if (paramSet != null) {
        arrayOfObjID = createObjIDArray(paramSet);
      } else {
        arrayOfObjID = DGCClient.emptyObjIDArray;
      }
      long l1 = System.currentTimeMillis();
      try
      {
        Lease localLease = dgc.dirty(arrayOfObjID, paramLong, new Lease(DGCClient.vmid, DGCClient.leaseValue));
        l2 = localLease.getValue();
        long l3 = DGCClient.computeRenewTime(l1, l2);
        l4 = l1 + l2;
        synchronized (this)
        {
          dirtyFailures = 0;
          setRenewTime(l3);
          expirationTime = l4;
        }
      }
      catch (Exception localException)
      {
        long l4;
        long l2 = System.currentTimeMillis();
        synchronized (this)
        {
          dirtyFailures += 1;
          if (((localException instanceof UnmarshalException)) && ((localException.getCause() instanceof InvalidClassException)))
          {
            DGCImpl.dgcLog.log(Log.BRIEF, "InvalidClassException exception in DGC dirty call", localException);
            return;
          }
          if (dirtyFailures == 1)
          {
            dirtyFailureStartTime = l1;
            dirtyFailureDuration = (l2 - l1);
            setRenewTime(l2);
          }
          else
          {
            int i = dirtyFailures - 2;
            if (i == 0) {
              dirtyFailureDuration = Math.max(dirtyFailureDuration + (l2 - l1) >> 1, 1000L);
            }
            l4 = l2 + (dirtyFailureDuration << i);
            if ((l4 < expirationTime) || (dirtyFailures < 5) || (l4 < dirtyFailureStartTime + DGCClient.leaseValue)) {
              setRenewTime(l4);
            } else {
              setRenewTime(Long.MAX_VALUE);
            }
          }
          if (paramSet != null)
          {
            invalidRefs.addAll(paramSet);
            Iterator localIterator = paramSet.iterator();
            while (localIterator.hasNext())
            {
              RefEntry localRefEntry = (RefEntry)localIterator.next();
              localRefEntry.markDirtyFailed();
            }
          }
          if (renewTime >= expirationTime) {
            invalidRefs.addAll(refTable.values());
          }
        }
      }
    }
    
    private void setRenewTime(long paramLong)
    {
      assert (Thread.holdsLock(this));
      if (paramLong < renewTime)
      {
        renewTime = paramLong;
        if (interruptible) {
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Void run()
            {
              renewCleanThread.interrupt();
              return null;
            }
          });
        }
      }
      else
      {
        renewTime = paramLong;
      }
    }
    
    private void processPhantomRefs(DGCClient.EndpointEntry.RefEntry.PhantomLiveRef paramPhantomLiveRef)
    {
      assert (Thread.holdsLock(this));
      HashSet localHashSet1 = null;
      HashSet localHashSet2 = null;
      do
      {
        RefEntry localRefEntry = paramPhantomLiveRef.getRefEntry();
        localRefEntry.removeInstanceFromRefSet(paramPhantomLiveRef);
        if (localRefEntry.isRefSetEmpty())
        {
          if (localRefEntry.hasDirtyFailed())
          {
            if (localHashSet1 == null) {
              localHashSet1 = new HashSet(5);
            }
            localHashSet1.add(localRefEntry);
          }
          else
          {
            if (localHashSet2 == null) {
              localHashSet2 = new HashSet(5);
            }
            localHashSet2.add(localRefEntry);
          }
          removeRefEntry(localRefEntry);
        }
      } while ((paramPhantomLiveRef = (DGCClient.EndpointEntry.RefEntry.PhantomLiveRef)refQueue.poll()) != null);
      if (localHashSet1 != null) {
        pendingCleans.add(new CleanRequest(createObjIDArray(localHashSet1), DGCClient.access$300(), true));
      }
      if (localHashSet2 != null) {
        pendingCleans.add(new CleanRequest(createObjIDArray(localHashSet2), DGCClient.access$300(), false));
      }
    }
    
    private void makeCleanCalls()
    {
      assert (!Thread.holdsLock(this));
      Iterator localIterator = pendingCleans.iterator();
      while (localIterator.hasNext())
      {
        CleanRequest localCleanRequest = (CleanRequest)localIterator.next();
        try
        {
          dgc.clean(objIDs, sequenceNum, DGCClient.vmid, strong);
          localIterator.remove();
        }
        catch (Exception localException)
        {
          if (++failures >= 5) {
            localIterator.remove();
          }
        }
      }
    }
    
    private static ObjID[] createObjIDArray(Set<RefEntry> paramSet)
    {
      ObjID[] arrayOfObjID = new ObjID[paramSet.size()];
      Iterator localIterator = paramSet.iterator();
      for (int i = 0; i < arrayOfObjID.length; i++) {
        arrayOfObjID[i] = ((RefEntry)localIterator.next()).getRef().getObjID();
      }
      return arrayOfObjID;
    }
    
    private static class CleanRequest
    {
      final ObjID[] objIDs;
      final long sequenceNum;
      final boolean strong;
      int failures = 0;
      
      CleanRequest(ObjID[] paramArrayOfObjID, long paramLong, boolean paramBoolean)
      {
        objIDs = paramArrayOfObjID;
        sequenceNum = paramLong;
        strong = paramBoolean;
      }
    }
    
    private class RefEntry
    {
      private LiveRef ref;
      private Set<PhantomLiveRef> refSet = new HashSet(5);
      private boolean dirtyFailed = false;
      
      public RefEntry(LiveRef paramLiveRef)
      {
        ref = paramLiveRef;
      }
      
      public LiveRef getRef()
      {
        return ref;
      }
      
      public void addInstanceToRefSet(LiveRef paramLiveRef)
      {
        assert (Thread.holdsLock(DGCClient.EndpointEntry.this));
        assert (paramLiveRef.equals(ref));
        refSet.add(new PhantomLiveRef(paramLiveRef));
      }
      
      public void removeInstanceFromRefSet(PhantomLiveRef paramPhantomLiveRef)
      {
        assert (Thread.holdsLock(DGCClient.EndpointEntry.this));
        assert (refSet.contains(paramPhantomLiveRef));
        refSet.remove(paramPhantomLiveRef);
      }
      
      public boolean isRefSetEmpty()
      {
        assert (Thread.holdsLock(DGCClient.EndpointEntry.this));
        return refSet.size() == 0;
      }
      
      public void markDirtyFailed()
      {
        assert (Thread.holdsLock(DGCClient.EndpointEntry.this));
        dirtyFailed = true;
      }
      
      public boolean hasDirtyFailed()
      {
        assert (Thread.holdsLock(DGCClient.EndpointEntry.this));
        return dirtyFailed;
      }
      
      private class PhantomLiveRef
        extends PhantomReference<LiveRef>
      {
        public PhantomLiveRef(LiveRef paramLiveRef)
        {
          super(refQueue);
        }
        
        public DGCClient.EndpointEntry.RefEntry getRefEntry()
        {
          return DGCClient.EndpointEntry.RefEntry.this;
        }
      }
    }
    
    private class RenewCleanThread
      implements Runnable
    {
      private RenewCleanThread() {}
      
      public void run()
      {
        do
        {
          DGCClient.EndpointEntry.RefEntry.PhantomLiveRef localPhantomLiveRef = null;
          Object localObject1 = 0;
          Set localSet1 = null;
          long l2 = Long.MIN_VALUE;
          long l3;
          long l1;
          synchronized (DGCClient.EndpointEntry.this)
          {
            l3 = renewTime - System.currentTimeMillis();
            l1 = Math.max(l3, 1L);
            if (!pendingCleans.isEmpty()) {
              l1 = Math.min(l1, DGCClient.cleanInterval);
            }
            interruptible = true;
          }
          try
          {
            localPhantomLiveRef = (DGCClient.EndpointEntry.RefEntry.PhantomLiveRef)refQueue.remove(l1);
          }
          catch (InterruptedException ???) {}
          synchronized (DGCClient.EndpointEntry.this)
          {
            interruptible = false;
            Thread.interrupted();
            if (localPhantomLiveRef != null) {
              DGCClient.EndpointEntry.this.processPhantomRefs(localPhantomLiveRef);
            }
            l3 = System.currentTimeMillis();
            if (l3 > renewTime)
            {
              localObject1 = 1;
              if (!invalidRefs.isEmpty())
              {
                localSet1 = invalidRefs;
                invalidRefs = new HashSet(5);
              }
              l2 = DGCClient.access$300();
            }
          }
          ??? = localObject1;
          final Set localSet2 = localSet1;
          final long l4 = l2;
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Void run()
            {
              if (Ljava/lang/Object;) {
                DGCClient.EndpointEntry.this.makeDirtyCall(localSet2, l4);
              }
              if (!pendingCleans.isEmpty()) {
                DGCClient.EndpointEntry.this.makeCleanCalls();
              }
              return null;
            }
          }, DGCClient.SOCKET_ACC);
        } while ((!removed) || (!pendingCleans.isEmpty()));
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\DGCClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */