package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;

public class POAPolicyMediatorImpl_R_UDS
  extends POAPolicyMediatorBase_R
{
  private Servant defaultServant = null;
  
  POAPolicyMediatorImpl_R_UDS(Policies paramPolicies, POAImpl paramPOAImpl)
  {
    super(paramPolicies, paramPOAImpl);
    if (!paramPolicies.useDefaultServant()) {
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
    }
  }
  
  protected Object internalGetServant(byte[] paramArrayOfByte, String paramString)
    throws ForwardRequest
  {
    Servant localServant = internalIdToServant(paramArrayOfByte);
    if (localServant == null) {
      localServant = defaultServant;
    }
    if (localServant == null) {
      throw poa.invocationWrapper().poaNoDefaultServant();
    }
    return localServant;
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
    if (defaultServant == null) {
      throw new NoServant();
    }
    return defaultServant;
  }
  
  public void setDefaultServant(Servant paramServant)
    throws WrongPolicy
  {
    defaultServant = paramServant;
    setDelegate(defaultServant, "DefaultServant".getBytes());
  }
  
  public Servant idToServant(byte[] paramArrayOfByte)
    throws WrongPolicy, ObjectNotActive
  {
    ActiveObjectMap.Key localKey = new ActiveObjectMap.Key(paramArrayOfByte);
    Servant localServant = internalKeyToServant(localKey);
    if ((localServant == null) && (defaultServant != null)) {
      localServant = defaultServant;
    }
    if (localServant == null) {
      throw new ObjectNotActive();
    }
    return localServant;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorImpl_R_UDS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */