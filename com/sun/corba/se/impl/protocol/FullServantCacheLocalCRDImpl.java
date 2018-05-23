package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class FullServantCacheLocalCRDImpl
  extends ServantCacheLocalCRDBase
{
  public FullServantCacheLocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR)
  {
    super(paramORB, paramInt, paramIOR);
  }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass)
  {
    OAInvocationInfo localOAInvocationInfo1 = getCachedInfo();
    if (!checkForCompatibleServant(localOAInvocationInfo1, paramClass)) {
      return null;
    }
    OAInvocationInfo localOAInvocationInfo2 = new OAInvocationInfo(localOAInvocationInfo1, paramString);
    orb.pushInvocationInfo(localOAInvocationInfo2);
    try
    {
      localOAInvocationInfo2.oa().enter();
    }
    catch (OADestroyed localOADestroyed)
    {
      throw wrapper.preinvokePoaDestroyed(localOADestroyed);
    }
    return localOAInvocationInfo2;
  }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject)
  {
    OAInvocationInfo localOAInvocationInfo = getCachedInfo();
    localOAInvocationInfo.oa().exit();
    orb.popInvocationInfo();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\FullServantCacheLocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */