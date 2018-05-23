package com.sun.jndi.ldap.pool;

import com.sun.jndi.ldap.LdapPoolManager;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.naming.CommunicationException;
import javax.naming.InterruptedNamingException;
import javax.naming.NamingException;

final class Connections
  implements PoolCallback
{
  private static final boolean debug = Pool.debug;
  private static final boolean trace = LdapPoolManager.trace;
  private static final int DEFAULT_SIZE = 10;
  private final int maxSize;
  private final int prefSize;
  private final List<ConnectionDesc> conns;
  private boolean closed = false;
  private Reference<Object> ref;
  
  Connections(Object paramObject, int paramInt1, int paramInt2, int paramInt3, PooledConnectionFactory paramPooledConnectionFactory)
    throws NamingException
  {
    maxSize = paramInt3;
    if (paramInt3 > 0)
    {
      prefSize = Math.min(paramInt2, paramInt3);
      paramInt1 = Math.min(paramInt1, paramInt3);
    }
    else
    {
      prefSize = paramInt2;
    }
    conns = new ArrayList(paramInt3 > 0 ? paramInt3 : 10);
    ref = new SoftReference(paramObject);
    d("init size=", paramInt1);
    d("max size=", paramInt3);
    d("preferred size=", paramInt2);
    for (int i = 0; i < paramInt1; i++)
    {
      PooledConnection localPooledConnection = paramPooledConnectionFactory.createPooledConnection(this);
      td("Create ", localPooledConnection, paramPooledConnectionFactory);
      conns.add(new ConnectionDesc(localPooledConnection));
    }
  }
  
  synchronized PooledConnection get(long paramLong, PooledConnectionFactory paramPooledConnectionFactory)
    throws NamingException
  {
    long l1 = paramLong > 0L ? System.currentTimeMillis() : 0L;
    long l2 = paramLong;
    d("get(): before");
    PooledConnection localPooledConnection;
    while ((localPooledConnection = getOrCreateConnection(paramPooledConnectionFactory)) == null)
    {
      if ((paramLong > 0L) && (l2 <= 0L)) {
        throw new CommunicationException("Timeout exceeded while waiting for a connection: " + paramLong + "ms");
      }
      try
      {
        d("get(): waiting");
        if (l2 > 0L) {
          wait(l2);
        } else {
          wait();
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new InterruptedNamingException("Interrupted while waiting for a connection");
      }
      if (paramLong > 0L)
      {
        long l3 = System.currentTimeMillis();
        l2 = paramLong - (l3 - l1);
      }
    }
    d("get(): after");
    return localPooledConnection;
  }
  
  private PooledConnection getOrCreateConnection(PooledConnectionFactory paramPooledConnectionFactory)
    throws NamingException
  {
    int i = conns.size();
    PooledConnection localPooledConnection = null;
    if ((prefSize <= 0) || (i >= prefSize)) {
      for (int j = 0; j < i; j++)
      {
        ConnectionDesc localConnectionDesc = (ConnectionDesc)conns.get(j);
        if ((localPooledConnection = localConnectionDesc.tryUse()) != null)
        {
          d("get(): use ", localPooledConnection);
          td("Use ", localPooledConnection);
          return localPooledConnection;
        }
      }
    }
    if ((maxSize > 0) && (i >= maxSize)) {
      return null;
    }
    localPooledConnection = paramPooledConnectionFactory.createPooledConnection(this);
    td("Create and use ", localPooledConnection, paramPooledConnectionFactory);
    conns.add(new ConnectionDesc(localPooledConnection, true));
    return localPooledConnection;
  }
  
  public synchronized boolean releasePooledConnection(PooledConnection paramPooledConnection)
  {
    ConnectionDesc localConnectionDesc;
    int i = conns.indexOf(localConnectionDesc = new ConnectionDesc(paramPooledConnection));
    d("release(): ", paramPooledConnection);
    if (i >= 0)
    {
      if ((closed) || ((prefSize > 0) && (conns.size() > prefSize)))
      {
        d("release(): closing ", paramPooledConnection);
        td("Close ", paramPooledConnection);
        conns.remove(localConnectionDesc);
        paramPooledConnection.closeConnection();
      }
      else
      {
        d("release(): release ", paramPooledConnection);
        td("Release ", paramPooledConnection);
        localConnectionDesc = (ConnectionDesc)conns.get(i);
        localConnectionDesc.release();
      }
      notifyAll();
      d("release(): notify");
      return true;
    }
    return false;
  }
  
  public synchronized boolean removePooledConnection(PooledConnection paramPooledConnection)
  {
    if (conns.remove(new ConnectionDesc(paramPooledConnection)))
    {
      d("remove(): ", paramPooledConnection);
      notifyAll();
      d("remove(): notify");
      td("Remove ", paramPooledConnection);
      if (conns.isEmpty()) {
        ref = null;
      }
      return true;
    }
    d("remove(): not found ", paramPooledConnection);
    return false;
  }
  
  boolean expire(long paramLong)
  {
    ArrayList localArrayList;
    synchronized (this)
    {
      localArrayList = new ArrayList(conns);
    }
    ??? = new ArrayList();
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      ConnectionDesc localConnectionDesc = (ConnectionDesc)localIterator.next();
      d("expire(): ", localConnectionDesc);
      if (localConnectionDesc.expire(paramLong))
      {
        ((List)???).add(localConnectionDesc);
        td("expire(): Expired ", localConnectionDesc);
      }
    }
    synchronized (this)
    {
      conns.removeAll((Collection)???);
      return conns.isEmpty();
    }
  }
  
  synchronized void close()
  {
    expire(System.currentTimeMillis());
    closed = true;
  }
  
  String getStats()
  {
    int i = 0;
    int j = 0;
    int k = 0;
    long l = 0L;
    int m;
    synchronized (this)
    {
      m = conns.size();
      for (int n = 0; n < m; n++)
      {
        ConnectionDesc localConnectionDesc = (ConnectionDesc)conns.get(n);
        l += localConnectionDesc.getUseCount();
        switch (localConnectionDesc.getState())
        {
        case 0: 
          j++;
          break;
        case 1: 
          i++;
          break;
        case 2: 
          k++;
        }
      }
    }
    return "size=" + m + "; use=" + l + "; busy=" + j + "; idle=" + i + "; expired=" + k;
  }
  
  private void d(String paramString, Object paramObject)
  {
    if (debug) {
      d(paramString + paramObject);
    }
  }
  
  private void d(String paramString, int paramInt)
  {
    if (debug) {
      d(paramString + paramInt);
    }
  }
  
  private void d(String paramString)
  {
    if (debug) {
      System.err.println(this + "." + paramString + "; size: " + conns.size());
    }
  }
  
  private void td(String paramString, Object paramObject1, Object paramObject2)
  {
    if (trace) {
      td(paramString + paramObject1 + "[" + paramObject2 + "]");
    }
  }
  
  private void td(String paramString, Object paramObject)
  {
    if (trace) {
      td(paramString + paramObject);
    }
  }
  
  private void td(String paramString)
  {
    if (trace) {
      System.err.println(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\pool\Connections.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */