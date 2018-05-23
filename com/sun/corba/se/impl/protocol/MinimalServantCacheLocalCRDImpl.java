package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class MinimalServantCacheLocalCRDImpl
  extends ServantCacheLocalCRDBase
{
  public MinimalServantCacheLocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR)
  {
    super(paramORB, paramInt, paramIOR);
  }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass)
  {
    OAInvocationInfo localOAInvocationInfo = getCachedInfo();
    if (checkForCompatibleServant(localOAInvocationInfo, paramClass)) {
      return localOAInvocationInfo;
    }
    return null;
  }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\MinimalServantCacheLocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */