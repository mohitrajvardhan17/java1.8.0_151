package com.sun.jndi.ldap.pool;

import com.sun.jndi.ldap.LdapPoolManager;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import javax.naming.NamingException;

public final class Pool
{
  static final boolean debug = LdapPoolManager.debug;
  private static final ReferenceQueue<ConnectionsRef> queue = new ReferenceQueue();
  private static final Collection<Reference<ConnectionsRef>> weakRefs = Collections.synchronizedList(new LinkedList());
  private final int maxSize;
  private final int prefSize;
  private final int initSize;
  private final Map<Object, ConnectionsRef> map = new WeakHashMap();
  
  public Pool(int paramInt1, int paramInt2, int paramInt3)
  {
    prefSize = paramInt2;
    maxSize = paramInt3;
    initSize = paramInt1;
  }
  
  public PooledConnection getPooledConnection(Object paramObject, long paramLong, PooledConnectionFactory paramPooledConnectionFactory)
    throws NamingException
  {
    d("get(): ", paramObject);
    if (debug) {
      synchronized (map)
      {
        d("size: ", map.size());
      }
    }
    expungeStaleConnections();
    synchronized (map)
    {
      ??? = getConnections(paramObject);
      if (??? == null)
      {
        d("get(): creating new connections list for ", paramObject);
        ??? = new Connections(paramObject, initSize, prefSize, maxSize, paramPooledConnectionFactory);
        ConnectionsRef localConnectionsRef = new ConnectionsRef((Connections)???);
        map.put(paramObject, localConnectionsRef);
        ConnectionsWeakRef localConnectionsWeakRef = new ConnectionsWeakRef(localConnectionsRef, queue);
        weakRefs.add(localConnectionsWeakRef);
      }
      d("get(): size after: ", map.size());
    }
    return ((Connections)???).get(paramLong, paramPooledConnectionFactory);
  }
  
  private Connections getConnections(Object paramObject)
  {
    ConnectionsRef localConnectionsRef = (ConnectionsRef)map.get(paramObject);
    return localConnectionsRef != null ? localConnectionsRef.getConnections() : null;
  }
  
  public void expire(long paramLong)
  {
    ArrayList localArrayList;
    synchronized (map)
    {
      localArrayList = new ArrayList(map.values());
    }
    ??? = new ArrayList();
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      ConnectionsRef localConnectionsRef = (ConnectionsRef)localIterator.next();
      Connections localConnections = localConnectionsRef.getConnections();
      if (localConnections.expire(paramLong))
      {
        d("expire(): removing ", localConnections);
        ((ArrayList)???).add(localConnectionsRef);
      }
    }
    synchronized (map)
    {
      map.values().removeAll((Collection)???);
    }
    expungeStaleConnections();
  }
  
  private static void expungeStaleConnections()
  {
    ConnectionsWeakRef localConnectionsWeakRef = null;
    while ((localConnectionsWeakRef = (ConnectionsWeakRef)queue.poll()) != null)
    {
      Connections localConnections = localConnectionsWeakRef.getConnections();
      if (debug) {
        System.err.println("weak reference cleanup: Closing Connections:" + localConnections);
      }
      localConnections.close();
      weakRefs.remove(localConnectionsWeakRef);
      localConnectionsWeakRef.clear();
    }
  }
  
  public void showStats(PrintStream paramPrintStream)
  {
    paramPrintStream.println("===== Pool start ======================");
    paramPrintStream.println("maximum pool size: " + maxSize);
    paramPrintStream.println("preferred pool size: " + prefSize);
    paramPrintStream.println("initial pool size: " + initSize);
    synchronized (map)
    {
      paramPrintStream.println("current pool size: " + map.size());
      Iterator localIterator = map.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Object localObject1 = localEntry.getKey();
        Connections localConnections = ((ConnectionsRef)localEntry.getValue()).getConnections();
        paramPrintStream.println("   " + localObject1 + ":" + localConnections.getStats());
      }
    }
    paramPrintStream.println("====== Pool end =====================");
  }
  
  /* Error */
  public String toString()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 211	com/sun/jndi/ldap/pool/Pool:map	Ljava/util/Map;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: new 120	java/lang/StringBuilder
    //   10: dup
    //   11: invokespecial 230	java/lang/StringBuilder:<init>	()V
    //   14: aload_0
    //   15: invokespecial 229	java/lang/Object:toString	()Ljava/lang/String;
    //   18: invokevirtual 234	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   21: ldc 1
    //   23: invokevirtual 234	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   26: aload_0
    //   27: getfield 211	com/sun/jndi/ldap/pool/Pool:map	Ljava/util/Map;
    //   30: invokevirtual 229	java/lang/Object:toString	()Ljava/lang/String;
    //   33: invokevirtual 234	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   36: invokevirtual 231	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   39: aload_1
    //   40: monitorexit
    //   41: areturn
    //   42: astore_2
    //   43: aload_1
    //   44: monitorexit
    //   45: aload_2
    //   46: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	47	0	this	Pool
    //   5	39	1	Ljava/lang/Object;	Object
    //   42	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	41	42	finally
    //   42	45	42	finally
  }
  
  private void d(String paramString, int paramInt)
  {
    if (debug) {
      System.err.println(this + "." + paramString + paramInt);
    }
  }
  
  private void d(String paramString, Object paramObject)
  {
    if (debug) {
      System.err.println(this + "." + paramString + paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\pool\Pool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */