package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;

public abstract class ServantCacheLocalCRDBase
  extends LocalClientRequestDispatcherBase
{
  private OAInvocationInfo cachedInfo;
  protected POASystemException wrapper;
  
  protected ServantCacheLocalCRDBase(ORB paramORB, int paramInt, IOR paramIOR)
  {
    super(paramORB, paramInt, paramIOR);
    wrapper = POASystemException.get(paramORB, "rpc.protocol");
  }
  
  protected synchronized OAInvocationInfo getCachedInfo()
  {
    if (!servantIsLocal) {
      throw wrapper.servantMustBeLocal();
    }
    if (cachedInfo == null)
    {
      ObjectAdapter localObjectAdapter = oaf.find(oaid);
      cachedInfo = localObjectAdapter.makeInvocationInfo(objectId);
      orb.pushInvocationInfo(cachedInfo);
      try
      {
        localObjectAdapter.enter();
        localObjectAdapter.getInvocationServant(cachedInfo);
      }
      catch (ForwardException localForwardException)
      {
        throw wrapper.illegalForwardRequest(localForwardException);
      }
      catch (OADestroyed localOADestroyed)
      {
        throw wrapper.adapterDestroyed(localOADestroyed);
      }
      finally
      {
        localObjectAdapter.returnServant();
        localObjectAdapter.exit();
        orb.popInvocationInfo();
      }
    }
    return cachedInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\ServantCacheLocalCRDBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */