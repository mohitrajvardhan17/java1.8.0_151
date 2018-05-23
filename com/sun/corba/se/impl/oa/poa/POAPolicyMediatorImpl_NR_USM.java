package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import org.omg.PortableServer.ServantManager;

public class POAPolicyMediatorImpl_NR_USM
  extends POAPolicyMediatorBase
{
  private ServantLocator locator;
  
  POAPolicyMediatorImpl_NR_USM(Policies paramPolicies, POAImpl paramPOAImpl)
  {
    super(paramPolicies, paramPOAImpl);
    if (paramPolicies.retainServants()) {
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
    }
    if (!paramPolicies.useServantManager()) {
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
    }
    locator = null;
  }
  
  protected Object internalGetServant(byte[] paramArrayOfByte, String paramString)
    throws ForwardRequest
  {
    if (locator == null) {
      throw poa.invocationWrapper().poaNoServantManager();
    }
    CookieHolder localCookieHolder = orb.peekInvocationInfo().getCookieHolder();
    Object localObject1;
    try
    {
      poa.unlock();
      localObject1 = locator.preinvoke(paramArrayOfByte, poa, paramString, localCookieHolder);
      if (localObject1 == null) {
        localObject1 = new NullServantImpl(poa.omgInvocationWrapper().nullServantReturned());
      } else {
        setDelegate((Servant)localObject1, paramArrayOfByte);
      }
    }
    finally
    {
      poa.lock();
    }
    return localObject1;
  }
  
  /* Error */
  public void returnServant()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 147	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_NR_USM:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   4: invokevirtual 169	com/sun/corba/se/spi/orb/ORB:peekInvocationInfo	()Lcom/sun/corba/se/spi/oa/OAInvocationInfo;
    //   7: astore_1
    //   8: aload_0
    //   9: getfield 148	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_NR_USM:locator	Lorg/omg/PortableServer/ServantLocator;
    //   12: ifnonnull +4 -> 16
    //   15: return
    //   16: aload_0
    //   17: getfield 146	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_NR_USM:poa	Lcom/sun/corba/se/impl/oa/poa/POAImpl;
    //   20: invokevirtual 157	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   23: aload_0
    //   24: getfield 148	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_NR_USM:locator	Lorg/omg/PortableServer/ServantLocator;
    //   27: aload_1
    //   28: invokevirtual 164	com/sun/corba/se/spi/oa/OAInvocationInfo:id	()[B
    //   31: aload_1
    //   32: invokevirtual 165	com/sun/corba/se/spi/oa/OAInvocationInfo:oa	()Lcom/sun/corba/se/spi/oa/ObjectAdapter;
    //   35: checkcast 85	org/omg/PortableServer/POA
    //   38: checkcast 85	org/omg/PortableServer/POA
    //   41: aload_1
    //   42: invokevirtual 167	com/sun/corba/se/spi/oa/OAInvocationInfo:getOperation	()Ljava/lang/String;
    //   45: aload_1
    //   46: invokevirtual 168	com/sun/corba/se/spi/oa/OAInvocationInfo:getCookieHolder	()Lorg/omg/PortableServer/ServantLocatorPackage/CookieHolder;
    //   49: getfield 149	org/omg/PortableServer/ServantLocatorPackage/CookieHolder:value	Ljava/lang/Object;
    //   52: aload_1
    //   53: invokevirtual 166	com/sun/corba/se/spi/oa/OAInvocationInfo:getServantContainer	()Ljava/lang/Object;
    //   56: checkcast 92	org/omg/PortableServer/Servant
    //   59: checkcast 92	org/omg/PortableServer/Servant
    //   62: invokeinterface 171 6 0
    //   67: aload_0
    //   68: getfield 146	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_NR_USM:poa	Lcom/sun/corba/se/impl/oa/poa/POAImpl;
    //   71: invokevirtual 156	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   74: goto +13 -> 87
    //   77: astore_2
    //   78: aload_0
    //   79: getfield 146	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_NR_USM:poa	Lcom/sun/corba/se/impl/oa/poa/POAImpl;
    //   82: invokevirtual 156	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   85: aload_2
    //   86: athrow
    //   87: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	POAPolicyMediatorImpl_NR_USM
    //   7	46	1	localOAInvocationInfo	OAInvocationInfo
    //   77	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   16	67	77	finally
  }
  
  public void etherealizeAll() {}
  
  public void clearAOM() {}
  
  public ServantManager getServantManager()
    throws WrongPolicy
  {
    return locator;
  }
  
  public void setServantManager(ServantManager paramServantManager)
    throws WrongPolicy
  {
    if (locator != null) {
      throw poa.invocationWrapper().servantManagerAlreadySet();
    }
    if ((paramServantManager instanceof ServantLocator)) {
      locator = ((ServantLocator)paramServantManager);
    } else {
      throw poa.invocationWrapper().servantManagerBadType();
    }
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
  
  public final void activateObject(byte[] paramArrayOfByte, Servant paramServant)
    throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive
  {
    throw new WrongPolicy();
  }
  
  public Servant deactivateObject(byte[] paramArrayOfByte)
    throws ObjectNotActive, WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public byte[] servantToId(Servant paramServant)
    throws ServantNotActive, WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public Servant idToServant(byte[] paramArrayOfByte)
    throws WrongPolicy, ObjectNotActive
  {
    throw new WrongPolicy();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorImpl_NR_USM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */