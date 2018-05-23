package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public abstract class POAPolicyMediatorBase
  implements POAPolicyMediator
{
  protected POAImpl poa;
  protected ORB orb;
  private int sysIdCounter;
  private Policies policies;
  private DelegateImpl delegateImpl;
  private int serverid;
  private int scid;
  protected boolean isImplicit;
  protected boolean isUnique;
  protected boolean isSystemId;
  
  public final Policies getPolicies()
  {
    return policies;
  }
  
  public final int getScid()
  {
    return scid;
  }
  
  public final int getServerId()
  {
    return serverid;
  }
  
  POAPolicyMediatorBase(Policies paramPolicies, POAImpl paramPOAImpl)
  {
    if (paramPolicies.isSingleThreaded()) {
      throw paramPOAImpl.invocationWrapper().singleThreadNotSupported();
    }
    POAManagerImpl localPOAManagerImpl = (POAManagerImpl)paramPOAImpl.the_POAManager();
    POAFactory localPOAFactory = localPOAManagerImpl.getFactory();
    delegateImpl = ((DelegateImpl)localPOAFactory.getDelegateImpl());
    policies = paramPolicies;
    poa = paramPOAImpl;
    orb = paramPOAImpl.getORB();
    switch (paramPolicies.servantCachingLevel())
    {
    case 0: 
      scid = 32;
      break;
    case 1: 
      scid = 36;
      break;
    case 2: 
      scid = 40;
      break;
    case 3: 
      scid = 44;
    }
    if (paramPolicies.isTransient())
    {
      serverid = orb.getTransientServerId();
    }
    else
    {
      serverid = orb.getORBData().getPersistentServerId();
      scid = ORBConstants.makePersistent(scid);
    }
    isImplicit = paramPolicies.isImplicitlyActivated();
    isUnique = paramPolicies.isUniqueIds();
    isSystemId = paramPolicies.isSystemAssignedIds();
    sysIdCounter = 0;
  }
  
  public final Object getInvocationServant(byte[] paramArrayOfByte, String paramString)
    throws ForwardRequest
  {
    Object localObject = internalGetServant(paramArrayOfByte, paramString);
    return localObject;
  }
  
  protected final void setDelegate(Servant paramServant, byte[] paramArrayOfByte)
  {
    paramServant._set_delegate(delegateImpl);
  }
  
  public synchronized byte[] newSystemId()
    throws WrongPolicy
  {
    if (!isSystemId) {
      throw new WrongPolicy();
    }
    byte[] arrayOfByte = new byte[8];
    ORBUtility.intToBytes(++sysIdCounter, arrayOfByte, 0);
    ORBUtility.intToBytes(poa.getPOAId(), arrayOfByte, 4);
    return arrayOfByte;
  }
  
  protected abstract Object internalGetServant(byte[] paramArrayOfByte, String paramString)
    throws ForwardRequest;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */