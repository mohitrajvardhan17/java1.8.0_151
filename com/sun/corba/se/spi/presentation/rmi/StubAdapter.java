package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.POAManagerImpl;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public abstract class StubAdapter
{
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");
  
  private StubAdapter() {}
  
  public static boolean isStubClass(Class paramClass)
  {
    return (ObjectImpl.class.isAssignableFrom(paramClass)) || (DynamicStub.class.isAssignableFrom(paramClass));
  }
  
  public static boolean isStub(Object paramObject)
  {
    return ((paramObject instanceof DynamicStub)) || ((paramObject instanceof ObjectImpl));
  }
  
  public static void setDelegate(Object paramObject, Delegate paramDelegate)
  {
    if ((paramObject instanceof DynamicStub)) {
      ((DynamicStub)paramObject).setDelegate(paramDelegate);
    } else if ((paramObject instanceof ObjectImpl)) {
      ((ObjectImpl)paramObject)._set_delegate(paramDelegate);
    } else {
      throw wrapper.setDelegateRequiresStub();
    }
  }
  
  public static org.omg.CORBA.Object activateServant(Servant paramServant)
  {
    POA localPOA = paramServant._default_POA();
    org.omg.CORBA.Object localObject = null;
    try
    {
      localObject = localPOA.servant_to_reference(paramServant);
    }
    catch (ServantNotActive localServantNotActive)
    {
      throw wrapper.getDelegateServantNotActive(localServantNotActive);
    }
    catch (WrongPolicy localWrongPolicy)
    {
      throw wrapper.getDelegateWrongPolicy(localWrongPolicy);
    }
    POAManager localPOAManager = localPOA.the_POAManager();
    if ((localPOAManager instanceof POAManagerImpl))
    {
      POAManagerImpl localPOAManagerImpl = (POAManagerImpl)localPOAManager;
      localPOAManagerImpl.implicitActivation();
    }
    return localObject;
  }
  
  public static org.omg.CORBA.Object activateTie(Tie paramTie)
  {
    if ((paramTie instanceof ObjectImpl)) {
      return paramTie.thisObject();
    }
    if ((paramTie instanceof Servant))
    {
      Servant localServant = (Servant)paramTie;
      return activateServant(localServant);
    }
    throw wrapper.badActivateTieCall();
  }
  
  public static Delegate getDelegate(Object paramObject)
  {
    if ((paramObject instanceof DynamicStub)) {
      return ((DynamicStub)paramObject).getDelegate();
    }
    if ((paramObject instanceof ObjectImpl)) {
      return ((ObjectImpl)paramObject)._get_delegate();
    }
    if ((paramObject instanceof Tie))
    {
      Tie localTie = (Tie)paramObject;
      org.omg.CORBA.Object localObject = activateTie(localTie);
      return getDelegate(localObject);
    }
    throw wrapper.getDelegateRequiresStub();
  }
  
  public static org.omg.CORBA.ORB getORB(Object paramObject)
  {
    if ((paramObject instanceof DynamicStub)) {
      return ((DynamicStub)paramObject).getORB();
    }
    if ((paramObject instanceof ObjectImpl)) {
      return ((ObjectImpl)paramObject)._orb();
    }
    throw wrapper.getOrbRequiresStub();
  }
  
  public static String[] getTypeIds(Object paramObject)
  {
    if ((paramObject instanceof DynamicStub)) {
      return ((DynamicStub)paramObject).getTypeIds();
    }
    if ((paramObject instanceof ObjectImpl)) {
      return ((ObjectImpl)paramObject)._ids();
    }
    throw wrapper.getTypeIdsRequiresStub();
  }
  
  public static void connect(Object paramObject, org.omg.CORBA.ORB paramORB)
    throws RemoteException
  {
    if ((paramObject instanceof DynamicStub)) {
      ((DynamicStub)paramObject).connect((com.sun.corba.se.spi.orb.ORB)paramORB);
    } else if ((paramObject instanceof Stub)) {
      ((Stub)paramObject).connect(paramORB);
    } else if ((paramObject instanceof ObjectImpl)) {
      paramORB.connect((org.omg.CORBA.Object)paramObject);
    } else {
      throw wrapper.connectRequiresStub();
    }
  }
  
  public static boolean isLocal(Object paramObject)
  {
    if ((paramObject instanceof DynamicStub)) {
      return ((DynamicStub)paramObject).isLocal();
    }
    if ((paramObject instanceof ObjectImpl)) {
      return ((ObjectImpl)paramObject)._is_local();
    }
    throw wrapper.isLocalRequiresStub();
  }
  
  public static OutputStream request(Object paramObject, String paramString, boolean paramBoolean)
  {
    if ((paramObject instanceof DynamicStub)) {
      return ((DynamicStub)paramObject).request(paramString, paramBoolean);
    }
    if ((paramObject instanceof ObjectImpl)) {
      return ((ObjectImpl)paramObject)._request(paramString, paramBoolean);
    }
    throw wrapper.requestRequiresStub();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\presentation\rmi\StubAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */