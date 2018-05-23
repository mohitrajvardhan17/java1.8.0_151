package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.resolver.LocalResolver;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.TRANSIENT;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.portable.Delegate;

public class POAFactory
  implements ObjectAdapterFactory
{
  private Map exportedServantsToPOA = new WeakHashMap();
  private Set poaManagers = Collections.synchronizedSet(new HashSet(4));
  private int poaManagerId = 0;
  private int poaId = 0;
  private POAImpl rootPOA = null;
  private DelegateImpl delegateImpl = null;
  private ORB orb = null;
  private POASystemException wrapper;
  private OMGSystemException omgWrapper;
  private boolean isShuttingDown = false;
  
  public POASystemException getWrapper()
  {
    return wrapper;
  }
  
  public POAFactory() {}
  
  public synchronized POA lookupPOA(Servant paramServant)
  {
    return (POA)exportedServantsToPOA.get(paramServant);
  }
  
  public synchronized void registerPOAForServant(POA paramPOA, Servant paramServant)
  {
    exportedServantsToPOA.put(paramServant, paramPOA);
  }
  
  public synchronized void unregisterPOAForServant(POA paramPOA, Servant paramServant)
  {
    exportedServantsToPOA.remove(paramServant);
  }
  
  public void init(ORB paramORB)
  {
    orb = paramORB;
    wrapper = POASystemException.get(paramORB, "oa.lifecycle");
    omgWrapper = OMGSystemException.get(paramORB, "oa.lifecycle");
    delegateImpl = new DelegateImpl(paramORB, this);
    registerRootPOA();
    POACurrent localPOACurrent = new POACurrent(paramORB);
    paramORB.getLocalResolver().register("POACurrent", ClosureFactory.makeConstant(localPOACurrent));
  }
  
  public ObjectAdapter find(ObjectAdapterId paramObjectAdapterId)
  {
    POA localPOA = null;
    try
    {
      int i = 1;
      Iterator localIterator = paramObjectAdapterId.iterator();
      localPOA = getRootPOA();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (i != 0)
        {
          if (!str.equals("RootPOA")) {
            throw wrapper.makeFactoryNotPoa(str);
          }
          i = 0;
        }
        else
        {
          localPOA = localPOA.find_POA(str, true);
        }
      }
    }
    catch (AdapterNonExistent localAdapterNonExistent)
    {
      throw omgWrapper.noObjectAdaptor(localAdapterNonExistent);
    }
    catch (OBJECT_NOT_EXIST localOBJECT_NOT_EXIST)
    {
      throw localOBJECT_NOT_EXIST;
    }
    catch (TRANSIENT localTRANSIENT)
    {
      throw localTRANSIENT;
    }
    catch (Exception localException)
    {
      throw wrapper.poaLookupError(localException);
    }
    if (localPOA == null) {
      throw wrapper.poaLookupError();
    }
    return (ObjectAdapter)localPOA;
  }
  
  public void shutdown(boolean paramBoolean)
  {
    Iterator localIterator = null;
    synchronized (this)
    {
      isShuttingDown = true;
      localIterator = new HashSet(poaManagers).iterator();
    }
    while (localIterator.hasNext()) {
      try
      {
        ((POAManager)localIterator.next()).deactivate(true, paramBoolean);
      }
      catch (AdapterInactive localAdapterInactive) {}
    }
  }
  
  public synchronized void removePoaManager(POAManager paramPOAManager)
  {
    poaManagers.remove(paramPOAManager);
  }
  
  public synchronized void addPoaManager(POAManager paramPOAManager)
  {
    poaManagers.add(paramPOAManager);
  }
  
  public synchronized int newPOAManagerId()
  {
    return poaManagerId++;
  }
  
  public void registerRootPOA()
  {
    Closure local1 = new Closure()
    {
      public Object evaluate()
      {
        return POAImpl.makeRootPOA(orb);
      }
    };
    orb.getLocalResolver().register("RootPOA", ClosureFactory.makeFuture(local1));
  }
  
  public synchronized POA getRootPOA()
  {
    if (rootPOA == null)
    {
      if (isShuttingDown) {
        throw omgWrapper.noObjectAdaptor();
      }
      try
      {
        org.omg.CORBA.Object localObject = orb.resolve_initial_references("RootPOA");
        rootPOA = ((POAImpl)localObject);
      }
      catch (InvalidName localInvalidName)
      {
        throw wrapper.cantResolveRootPoa(localInvalidName);
      }
    }
    return rootPOA;
  }
  
  public Delegate getDelegateImpl()
  {
    return delegateImpl;
  }
  
  public synchronized int newPOAId()
  {
    return poaId++;
  }
  
  public ORB getORB()
  {
    return orb;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\POAFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */