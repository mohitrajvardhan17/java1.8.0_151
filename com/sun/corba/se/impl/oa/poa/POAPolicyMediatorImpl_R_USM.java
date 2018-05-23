package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Set;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantActivator;
import org.omg.PortableServer.ServantManager;

public class POAPolicyMediatorImpl_R_USM
  extends POAPolicyMediatorBase_R
{
  protected ServantActivator activator = null;
  
  POAPolicyMediatorImpl_R_USM(Policies paramPolicies, POAImpl paramPOAImpl)
  {
    super(paramPolicies, paramPOAImpl);
    if (!paramPolicies.useServantManager()) {
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
    }
  }
  
  private AOMEntry enterEntry(ActiveObjectMap.Key paramKey)
  {
    AOMEntry localAOMEntry = null;
    int i;
    do
    {
      i = 0;
      localAOMEntry = activeObjectMap.get(paramKey);
      try
      {
        localAOMEntry.enter();
      }
      catch (Exception localException)
      {
        i = 1;
      }
    } while (i != 0);
    return localAOMEntry;
  }
  
  protected Object internalGetServant(byte[] paramArrayOfByte, String paramString)
    throws ForwardRequest
  {
    if (poa.getDebug()) {
      ORBUtility.dprint(this, "Calling POAPolicyMediatorImpl_R_USM.internalGetServant for poa " + poa + " operation=" + paramString);
    }
    try
    {
      ActiveObjectMap.Key localKey = new ActiveObjectMap.Key(paramArrayOfByte);
      AOMEntry localAOMEntry = enterEntry(localKey);
      Object localObject1 = activeObjectMap.getServant(localAOMEntry);
      if (localObject1 != null)
      {
        if (poa.getDebug()) {
          ORBUtility.dprint(this, "internalGetServant: servant already activated");
        }
        Object localObject2 = localObject1;
        return localObject2;
      }
      if (activator == null)
      {
        if (poa.getDebug()) {
          ORBUtility.dprint(this, "internalGetServant: no servant activator in POA");
        }
        localAOMEntry.incarnateFailure();
        throw poa.invocationWrapper().poaNoServantManager();
      }
      try
      {
        if (poa.getDebug()) {
          ORBUtility.dprint(this, "internalGetServant: upcall to incarnate");
        }
        poa.unlock();
        localObject1 = activator.incarnate(paramArrayOfByte, poa);
        if (localObject1 == null) {
          localObject1 = new NullServantImpl(poa.omgInvocationWrapper().nullServantReturned());
        }
      }
      catch (ForwardRequest localForwardRequest)
      {
        if (poa.getDebug()) {
          ORBUtility.dprint(this, "internalGetServant: incarnate threw ForwardRequest");
        }
        throw localForwardRequest;
      }
      catch (SystemException localSystemException)
      {
        if (poa.getDebug()) {
          ORBUtility.dprint(this, "internalGetServant: incarnate threw SystemException " + localSystemException);
        }
        throw localSystemException;
      }
      catch (Throwable localThrowable)
      {
        if (poa.getDebug()) {
          ORBUtility.dprint(this, "internalGetServant: incarnate threw Throwable " + localThrowable);
        }
        throw poa.invocationWrapper().poaServantActivatorLookupFailed(localThrowable);
      }
      finally
      {
        poa.lock();
        if ((localObject1 == null) || ((localObject1 instanceof NullServant)))
        {
          if (poa.getDebug()) {
            ORBUtility.dprint(this, "internalGetServant: incarnate failed");
          }
          localAOMEntry.incarnateFailure();
        }
        else
        {
          if ((isUnique) && (activeObjectMap.contains((Servant)localObject1)))
          {
            if (poa.getDebug()) {
              ORBUtility.dprint(this, "internalGetServant: servant already assigned to ID");
            }
            localAOMEntry.incarnateFailure();
            throw poa.invocationWrapper().poaServantNotUnique();
          }
          if (poa.getDebug()) {
            ORBUtility.dprint(this, "internalGetServant: incarnate complete");
          }
          localAOMEntry.incarnateComplete();
          activateServant(localKey, localAOMEntry, (Servant)localObject1);
        }
      }
      Object localObject3 = localObject1;
      return localObject3;
    }
    finally
    {
      if (poa.getDebug()) {
        ORBUtility.dprint(this, "Exiting POAPolicyMediatorImpl_R_USM.internalGetServant for poa " + poa);
      }
    }
  }
  
  public void returnServant()
  {
    OAInvocationInfo localOAInvocationInfo = orb.peekInvocationInfo();
    byte[] arrayOfByte = localOAInvocationInfo.id();
    ActiveObjectMap.Key localKey = new ActiveObjectMap.Key(arrayOfByte);
    AOMEntry localAOMEntry = activeObjectMap.get(localKey);
    localAOMEntry.exit();
  }
  
  public void etherealizeAll()
  {
    if (activator != null)
    {
      Set localSet = activeObjectMap.keySet();
      ActiveObjectMap.Key[] arrayOfKey = (ActiveObjectMap.Key[])localSet.toArray(new ActiveObjectMap.Key[localSet.size()]);
      for (int i = 0; i < localSet.size(); i++)
      {
        ActiveObjectMap.Key localKey = arrayOfKey[i];
        AOMEntry localAOMEntry = activeObjectMap.get(localKey);
        Servant localServant = activeObjectMap.getServant(localAOMEntry);
        if (localServant != null)
        {
          boolean bool = activeObjectMap.hasMultipleIDs(localAOMEntry);
          localAOMEntry.startEtherealize(null);
          try
          {
            poa.unlock();
            try
            {
              activator.etherealize(id, poa, localServant, true, bool);
            }
            catch (Exception localException) {}
          }
          finally
          {
            poa.lock();
            localAOMEntry.etherealizeComplete();
          }
        }
      }
    }
  }
  
  public ServantManager getServantManager()
    throws WrongPolicy
  {
    return activator;
  }
  
  public void setServantManager(ServantManager paramServantManager)
    throws WrongPolicy
  {
    if (activator != null) {
      throw poa.invocationWrapper().servantManagerAlreadySet();
    }
    if ((paramServantManager instanceof ServantActivator)) {
      activator = ((ServantActivator)paramServantManager);
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
  
  public void deactivateHelper(ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry, Servant paramServant)
    throws ObjectNotActive, WrongPolicy
  {
    if (activator == null) {
      throw poa.invocationWrapper().poaNoServantManager();
    }
    Etherealizer localEtherealizer = new Etherealizer(this, paramKey, paramAOMEntry, paramServant, poa.getDebug());
    paramAOMEntry.startEtherealize(localEtherealizer);
  }
  
  public Servant idToServant(byte[] paramArrayOfByte)
    throws WrongPolicy, ObjectNotActive
  {
    ActiveObjectMap.Key localKey = new ActiveObjectMap.Key(paramArrayOfByte);
    AOMEntry localAOMEntry = activeObjectMap.get(localKey);
    Servant localServant = activeObjectMap.getServant(localAOMEntry);
    if (localServant != null) {
      return localServant;
    }
    throw new ObjectNotActive();
  }
  
  class Etherealizer
    extends Thread
  {
    private POAPolicyMediatorImpl_R_USM mediator;
    private ActiveObjectMap.Key key;
    private AOMEntry entry;
    private Servant servant;
    private boolean debug;
    
    public Etherealizer(POAPolicyMediatorImpl_R_USM paramPOAPolicyMediatorImpl_R_USM, ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry, Servant paramServant, boolean paramBoolean)
    {
      mediator = paramPOAPolicyMediatorImpl_R_USM;
      key = paramKey;
      entry = paramAOMEntry;
      servant = paramServant;
      debug = paramBoolean;
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 110	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:debug	Z
      //   4: ifeq +29 -> 33
      //   7: aload_0
      //   8: new 61	java/lang/StringBuilder
      //   11: dup
      //   12: invokespecial 125	java/lang/StringBuilder:<init>	()V
      //   15: ldc 1
      //   17: invokevirtual 128	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   20: aload_0
      //   21: getfield 112	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:key	Lcom/sun/corba/se/impl/oa/poa/ActiveObjectMap$Key;
      //   24: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   27: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   30: invokestatic 124	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
      //   33: aload_0
      //   34: getfield 113	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM;
      //   37: getfield 109	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM:activator	Lorg/omg/PortableServer/ServantActivator;
      //   40: aload_0
      //   41: getfield 112	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:key	Lcom/sun/corba/se/impl/oa/poa/ActiveObjectMap$Key;
      //   44: getfield 106	com/sun/corba/se/impl/oa/poa/ActiveObjectMap$Key:id	[B
      //   47: aload_0
      //   48: getfield 113	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM;
      //   51: getfield 108	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM:poa	Lcom/sun/corba/se/impl/oa/poa/POAImpl;
      //   54: aload_0
      //   55: getfield 115	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:servant	Lorg/omg/PortableServer/Servant;
      //   58: iconst_0
      //   59: aload_0
      //   60: getfield 113	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM;
      //   63: getfield 107	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM:activeObjectMap	Lcom/sun/corba/se/impl/oa/poa/ActiveObjectMap;
      //   66: aload_0
      //   67: getfield 111	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:entry	Lcom/sun/corba/se/impl/oa/poa/AOMEntry;
      //   70: invokevirtual 117	com/sun/corba/se/impl/oa/poa/ActiveObjectMap:hasMultipleIDs	(Lcom/sun/corba/se/impl/oa/poa/AOMEntry;)Z
      //   73: invokeinterface 130 6 0
      //   78: goto +4 -> 82
      //   81: astore_1
      //   82: aload_0
      //   83: getfield 113	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM;
      //   86: getfield 108	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM:poa	Lcom/sun/corba/se/impl/oa/poa/POAImpl;
      //   89: invokevirtual 120	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
      //   92: aload_0
      //   93: getfield 111	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:entry	Lcom/sun/corba/se/impl/oa/poa/AOMEntry;
      //   96: invokevirtual 116	com/sun/corba/se/impl/oa/poa/AOMEntry:etherealizeComplete	()V
      //   99: aload_0
      //   100: getfield 113	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM;
      //   103: getfield 107	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM:activeObjectMap	Lcom/sun/corba/se/impl/oa/poa/ActiveObjectMap;
      //   106: aload_0
      //   107: getfield 112	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:key	Lcom/sun/corba/se/impl/oa/poa/ActiveObjectMap$Key;
      //   110: invokevirtual 118	com/sun/corba/se/impl/oa/poa/ActiveObjectMap:remove	(Lcom/sun/corba/se/impl/oa/poa/ActiveObjectMap$Key;)V
      //   113: aload_0
      //   114: getfield 113	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM;
      //   117: getfield 108	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM:poa	Lcom/sun/corba/se/impl/oa/poa/POAImpl;
      //   120: invokevirtual 122	com/sun/corba/se/impl/oa/poa/POAImpl:the_POAManager	()Lorg/omg/PortableServer/POAManager;
      //   123: checkcast 56	com/sun/corba/se/impl/oa/poa/POAManagerImpl
      //   126: astore_1
      //   127: aload_1
      //   128: invokevirtual 123	com/sun/corba/se/impl/oa/poa/POAManagerImpl:getFactory	()Lcom/sun/corba/se/impl/oa/poa/POAFactory;
      //   131: astore_2
      //   132: aload_2
      //   133: aload_0
      //   134: getfield 113	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM;
      //   137: getfield 108	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM:poa	Lcom/sun/corba/se/impl/oa/poa/POAImpl;
      //   140: aload_0
      //   141: getfield 115	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:servant	Lorg/omg/PortableServer/Servant;
      //   144: invokevirtual 119	com/sun/corba/se/impl/oa/poa/POAFactory:unregisterPOAForServant	(Lorg/omg/PortableServer/POA;Lorg/omg/PortableServer/Servant;)V
      //   147: aload_0
      //   148: getfield 113	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM;
      //   151: getfield 108	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM:poa	Lcom/sun/corba/se/impl/oa/poa/POAImpl;
      //   154: invokevirtual 121	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
      //   157: goto +16 -> 173
      //   160: astore_3
      //   161: aload_0
      //   162: getfield 113	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM;
      //   165: getfield 108	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM:poa	Lcom/sun/corba/se/impl/oa/poa/POAImpl;
      //   168: invokevirtual 121	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
      //   171: aload_3
      //   172: athrow
      //   173: aload_0
      //   174: getfield 110	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:debug	Z
      //   177: ifeq +30 -> 207
      //   180: aload_0
      //   181: ldc 2
      //   183: invokestatic 124	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
      //   186: goto +21 -> 207
      //   189: astore 4
      //   191: aload_0
      //   192: getfield 110	com/sun/corba/se/impl/oa/poa/POAPolicyMediatorImpl_R_USM$Etherealizer:debug	Z
      //   195: ifeq +9 -> 204
      //   198: aload_0
      //   199: ldc 2
      //   201: invokestatic 124	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
      //   204: aload 4
      //   206: athrow
      //   207: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	208	0	this	Etherealizer
      //   81	1	1	localException	Exception
      //   126	2	1	localPOAManagerImpl	POAManagerImpl
      //   131	2	2	localPOAFactory	POAFactory
      //   160	12	3	localObject1	Object
      //   189	16	4	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   33	78	81	java/lang/Exception
      //   82	147	160	finally
      //   33	173	189	finally
      //   189	191	189	finally
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorImpl_R_USM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */