package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import java.io.PrintStream;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public abstract class POAPolicyMediatorBase_R
  extends POAPolicyMediatorBase
{
  protected ActiveObjectMap activeObjectMap;
  
  POAPolicyMediatorBase_R(Policies paramPolicies, POAImpl paramPOAImpl)
  {
    super(paramPolicies, paramPOAImpl);
    if (!paramPolicies.retainServants()) {
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
    }
    activeObjectMap = ActiveObjectMap.create(paramPOAImpl, !isUnique);
  }
  
  public void returnServant() {}
  
  public void clearAOM()
  {
    activeObjectMap.clear();
    activeObjectMap = null;
  }
  
  protected Servant internalKeyToServant(ActiveObjectMap.Key paramKey)
  {
    AOMEntry localAOMEntry = activeObjectMap.get(paramKey);
    if (localAOMEntry == null) {
      return null;
    }
    return activeObjectMap.getServant(localAOMEntry);
  }
  
  protected Servant internalIdToServant(byte[] paramArrayOfByte)
  {
    ActiveObjectMap.Key localKey = new ActiveObjectMap.Key(paramArrayOfByte);
    return internalKeyToServant(localKey);
  }
  
  protected void activateServant(ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry, Servant paramServant)
  {
    setDelegate(paramServant, id);
    if (orb.shutdownDebugFlag) {
      System.out.println("Activating object " + paramServant + " with POA " + poa);
    }
    activeObjectMap.putServant(paramServant, paramAOMEntry);
    if (Util.isInstanceDefined())
    {
      POAManagerImpl localPOAManagerImpl = (POAManagerImpl)poa.the_POAManager();
      POAFactory localPOAFactory = localPOAManagerImpl.getFactory();
      localPOAFactory.registerPOAForServant(poa, paramServant);
    }
  }
  
  public final void activateObject(byte[] paramArrayOfByte, Servant paramServant)
    throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive
  {
    if ((isUnique) && (activeObjectMap.contains(paramServant))) {
      throw new ServantAlreadyActive();
    }
    ActiveObjectMap.Key localKey = new ActiveObjectMap.Key(paramArrayOfByte);
    AOMEntry localAOMEntry = activeObjectMap.get(localKey);
    localAOMEntry.activateObject();
    activateServant(localKey, localAOMEntry, paramServant);
  }
  
  public Servant deactivateObject(byte[] paramArrayOfByte)
    throws ObjectNotActive, WrongPolicy
  {
    ActiveObjectMap.Key localKey = new ActiveObjectMap.Key(paramArrayOfByte);
    return deactivateObject(localKey);
  }
  
  protected void deactivateHelper(ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry, Servant paramServant)
    throws ObjectNotActive, WrongPolicy
  {
    activeObjectMap.remove(paramKey);
    if (Util.isInstanceDefined())
    {
      POAManagerImpl localPOAManagerImpl = (POAManagerImpl)poa.the_POAManager();
      POAFactory localPOAFactory = localPOAManagerImpl.getFactory();
      localPOAFactory.unregisterPOAForServant(poa, paramServant);
    }
  }
  
  public Servant deactivateObject(ActiveObjectMap.Key paramKey)
    throws ObjectNotActive, WrongPolicy
  {
    if (orb.poaDebugFlag) {
      ORBUtility.dprint(this, "Calling deactivateObject for key " + paramKey);
    }
    try
    {
      AOMEntry localAOMEntry = activeObjectMap.get(paramKey);
      if (localAOMEntry == null) {
        throw new ObjectNotActive();
      }
      Servant localServant1 = activeObjectMap.getServant(localAOMEntry);
      if (localServant1 == null) {
        throw new ObjectNotActive();
      }
      if (orb.poaDebugFlag) {
        System.out.println("Deactivating object " + localServant1 + " with POA " + poa);
      }
      deactivateHelper(paramKey, localAOMEntry, localServant1);
      Servant localServant2 = localServant1;
      return localServant2;
    }
    finally
    {
      if (orb.poaDebugFlag) {
        ORBUtility.dprint(this, "Exiting deactivateObject");
      }
    }
  }
  
  public byte[] servantToId(Servant paramServant)
    throws ServantNotActive, WrongPolicy
  {
    if ((!isUnique) && (!isImplicit)) {
      throw new WrongPolicy();
    }
    Object localObject;
    if (isUnique)
    {
      localObject = activeObjectMap.getKey(paramServant);
      if (localObject != null) {
        return id;
      }
    }
    if (isImplicit) {
      try
      {
        localObject = newSystemId();
        activateObject((byte[])localObject, paramServant);
        return (byte[])localObject;
      }
      catch (ObjectAlreadyActive localObjectAlreadyActive)
      {
        throw poa.invocationWrapper().servantToIdOaa(localObjectAlreadyActive);
      }
      catch (ServantAlreadyActive localServantAlreadyActive)
      {
        throw poa.invocationWrapper().servantToIdSaa(localServantAlreadyActive);
      }
      catch (WrongPolicy localWrongPolicy)
      {
        throw poa.invocationWrapper().servantToIdWp(localWrongPolicy);
      }
    }
    throw new ServantNotActive();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorBase_R.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */