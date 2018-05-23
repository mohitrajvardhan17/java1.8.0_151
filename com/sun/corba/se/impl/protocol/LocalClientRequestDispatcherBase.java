package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import org.omg.CORBA.portable.ServantObject;

public abstract class LocalClientRequestDispatcherBase
  implements LocalClientRequestDispatcher
{
  protected ORB orb;
  int scid;
  protected boolean servantIsLocal;
  protected ObjectAdapterFactory oaf;
  protected ObjectAdapterId oaid;
  protected byte[] objectId;
  private static final ThreadLocal isNextCallValid = new ThreadLocal()
  {
    protected synchronized Object initialValue()
    {
      return Boolean.TRUE;
    }
  };
  
  protected LocalClientRequestDispatcherBase(ORB paramORB, int paramInt, IOR paramIOR)
  {
    orb = paramORB;
    IIOPProfile localIIOPProfile = paramIOR.getProfile();
    servantIsLocal = ((paramORB.getORBData().isLocalOptimizationAllowed()) && (localIIOPProfile.isLocal()));
    ObjectKeyTemplate localObjectKeyTemplate = localIIOPProfile.getObjectKeyTemplate();
    scid = localObjectKeyTemplate.getSubcontractId();
    RequestDispatcherRegistry localRequestDispatcherRegistry = paramORB.getRequestDispatcherRegistry();
    oaf = localRequestDispatcherRegistry.getObjectAdapterFactory(paramInt);
    oaid = localObjectKeyTemplate.getObjectAdapterId();
    ObjectId localObjectId = localIIOPProfile.getObjectId();
    objectId = localObjectId.getId();
  }
  
  public byte[] getObjectId()
  {
    return objectId;
  }
  
  public boolean is_local(org.omg.CORBA.Object paramObject)
  {
    return false;
  }
  
  public boolean useLocalInvocation(org.omg.CORBA.Object paramObject)
  {
    if (isNextCallValid.get() == Boolean.TRUE) {
      return servantIsLocal;
    }
    isNextCallValid.set(Boolean.TRUE);
    return false;
  }
  
  protected boolean checkForCompatibleServant(ServantObject paramServantObject, Class paramClass)
  {
    if (paramServantObject == null) {
      return false;
    }
    if (!paramClass.isInstance(servant))
    {
      isNextCallValid.set(Boolean.FALSE);
      return false;
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\LocalClientRequestDispatcherBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */