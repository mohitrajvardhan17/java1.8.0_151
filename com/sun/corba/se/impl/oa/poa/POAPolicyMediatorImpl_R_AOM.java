package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.oa.NullServantImpl;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;

public class POAPolicyMediatorImpl_R_AOM
  extends POAPolicyMediatorBase_R
{
  POAPolicyMediatorImpl_R_AOM(Policies paramPolicies, POAImpl paramPOAImpl)
  {
    super(paramPolicies, paramPOAImpl);
    if (!paramPolicies.useActiveMapOnly()) {
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
    }
  }
  
  protected Object internalGetServant(byte[] paramArrayOfByte, String paramString)
    throws ForwardRequest
  {
    Object localObject = internalIdToServant(paramArrayOfByte);
    if (localObject == null) {
      localObject = new NullServantImpl(poa.invocationWrapper().nullServant());
    }
    return localObject;
  }
  
  public void etherealizeAll() {}
  
  public ServantManager getServantManager()
    throws WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public void setServantManager(ServantManager paramServantManager)
    throws WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public Servant getDefaultServant()
    throws NoServant, WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public void setDefaultServant(Servant paramServant)
    throws WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public Servant idToServant(byte[] paramArrayOfByte)
    throws WrongPolicy, ObjectNotActive
  {
    Servant localServant = internalIdToServant(paramArrayOfByte);
    if (localServant == null) {
      throw new ObjectNotActive();
    }
    return localServant;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorImpl_R_AOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */