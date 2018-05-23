package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class CachedCodeBase
  extends _CodeBaseImplBase
{
  private Hashtable implementations;
  private Hashtable fvds;
  private Hashtable bases;
  private volatile CodeBase delegate;
  private CorbaConnection conn;
  private static Object iorMapLock = new Object();
  private static Hashtable<IOR, CodeBase> iorMap = new Hashtable();
  
  public static synchronized void cleanCache(ORB paramORB)
  {
    synchronized (iorMapLock)
    {
      Iterator localIterator = iorMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        IOR localIOR = (IOR)localIterator.next();
        if (localIOR.getORB() == paramORB) {
          iorMap.remove(localIOR);
        }
      }
    }
  }
  
  public CachedCodeBase(CorbaConnection paramCorbaConnection)
  {
    conn = paramCorbaConnection;
  }
  
  public Repository get_ir()
  {
    return null;
  }
  
  public synchronized String implementation(String paramString)
  {
    String str = null;
    if (implementations == null) {
      implementations = new Hashtable();
    } else {
      str = (String)implementations.get(paramString);
    }
    if ((str == null) && (connectedCodeBase()))
    {
      str = delegate.implementation(paramString);
      if (str != null) {
        implementations.put(paramString, str);
      }
    }
    return str;
  }
  
  public synchronized String[] implementations(String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[paramArrayOfString.length];
    for (int i = 0; i < arrayOfString.length; i++) {
      arrayOfString[i] = implementation(paramArrayOfString[i]);
    }
    return arrayOfString;
  }
  
  public synchronized FullValueDescription meta(String paramString)
  {
    FullValueDescription localFullValueDescription = null;
    if (fvds == null) {
      fvds = new Hashtable();
    } else {
      localFullValueDescription = (FullValueDescription)fvds.get(paramString);
    }
    if ((localFullValueDescription == null) && (connectedCodeBase()))
    {
      localFullValueDescription = delegate.meta(paramString);
      if (localFullValueDescription != null) {
        fvds.put(paramString, localFullValueDescription);
      }
    }
    return localFullValueDescription;
  }
  
  public synchronized FullValueDescription[] metas(String[] paramArrayOfString)
  {
    FullValueDescription[] arrayOfFullValueDescription = new FullValueDescription[paramArrayOfString.length];
    for (int i = 0; i < arrayOfFullValueDescription.length; i++) {
      arrayOfFullValueDescription[i] = meta(paramArrayOfString[i]);
    }
    return arrayOfFullValueDescription;
  }
  
  public synchronized String[] bases(String paramString)
  {
    String[] arrayOfString = null;
    if (bases == null) {
      bases = new Hashtable();
    } else {
      arrayOfString = (String[])bases.get(paramString);
    }
    if ((arrayOfString == null) && (connectedCodeBase()))
    {
      arrayOfString = delegate.bases(paramString);
      if (arrayOfString != null) {
        bases.put(paramString, arrayOfString);
      }
    }
    return arrayOfString;
  }
  
  private synchronized boolean connectedCodeBase()
  {
    if (delegate != null) {
      return true;
    }
    if (conn.getCodeBaseIOR() == null)
    {
      if (conn.getBroker().transportDebugFlag) {
        conn.dprint("CodeBase unavailable on connection: " + conn);
      }
      return false;
    }
    synchronized (iorMapLock)
    {
      if (delegate != null) {
        return true;
      }
      delegate = ((CodeBase)iorMap.get(conn.getCodeBaseIOR()));
      if (delegate != null) {
        return true;
      }
      delegate = CodeBaseHelper.narrow(getObjectFromIOR());
      iorMap.put(conn.getCodeBaseIOR(), delegate);
    }
    return true;
  }
  
  private final org.omg.CORBA.Object getObjectFromIOR()
  {
    return CDRInputStream_1_0.internalIORToObject(conn.getCodeBaseIOR(), null, conn.getBroker());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CachedCodeBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */