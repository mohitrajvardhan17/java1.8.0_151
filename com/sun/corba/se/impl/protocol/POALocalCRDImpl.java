package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ServantObject;

public class POALocalCRDImpl
  extends LocalClientRequestDispatcherBase
{
  private ORBUtilSystemException wrapper;
  private POASystemException poaWrapper;
  
  public POALocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR)
  {
    super(paramORB, paramInt, paramIOR);
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    poaWrapper = POASystemException.get(paramORB, "rpc.protocol");
  }
  
  private OAInvocationInfo servantEnter(ObjectAdapter paramObjectAdapter)
    throws OADestroyed
  {
    paramObjectAdapter.enter();
    OAInvocationInfo localOAInvocationInfo = paramObjectAdapter.makeInvocationInfo(objectId);
    orb.pushInvocationInfo(localOAInvocationInfo);
    return localOAInvocationInfo;
  }
  
  /* Error */
  private void servantExit(ObjectAdapter paramObjectAdapter)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 145 1 0
    //   6: aload_1
    //   7: invokeinterface 144 1 0
    //   12: aload_0
    //   13: getfield 126	com/sun/corba/se/impl/protocol/POALocalCRDImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   16: invokevirtual 139	com/sun/corba/se/spi/orb/ORB:popInvocationInfo	()Lcom/sun/corba/se/spi/oa/OAInvocationInfo;
    //   19: pop
    //   20: goto +20 -> 40
    //   23: astore_2
    //   24: aload_1
    //   25: invokeinterface 144 1 0
    //   30: aload_0
    //   31: getfield 126	com/sun/corba/se/impl/protocol/POALocalCRDImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   34: invokevirtual 139	com/sun/corba/se/spi/orb/ORB:popInvocationInfo	()Lcom/sun/corba/se/spi/oa/OAInvocationInfo;
    //   37: pop
    //   38: aload_2
    //   39: athrow
    //   40: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	this	POALocalCRDImpl
    //   0	41	1	paramObjectAdapter	ObjectAdapter
    //   23	16	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	6	23	finally
  }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass)
  {
    ObjectAdapter localObjectAdapter = oaf.find(oaid);
    OAInvocationInfo localOAInvocationInfo = null;
    try
    {
      localOAInvocationInfo = servantEnter(localObjectAdapter);
      localOAInvocationInfo.setOperation(paramString);
    }
    catch (OADestroyed localOADestroyed)
    {
      return servant_preinvoke(paramObject, paramString, paramClass);
    }
    try
    {
      try
      {
        localObjectAdapter.getInvocationServant(localOAInvocationInfo);
        if (!checkForCompatibleServant(localOAInvocationInfo, paramClass)) {
          return null;
        }
      }
      catch (Throwable localThrowable1)
      {
        servantExit(localObjectAdapter);
        throw localThrowable1;
      }
    }
    catch (ForwardException localForwardException)
    {
      RuntimeException localRuntimeException = new RuntimeException("deal with this.");
      localRuntimeException.initCause(localForwardException);
      throw localRuntimeException;
    }
    catch (ThreadDeath localThreadDeath)
    {
      throw wrapper.runtimeexception(localThreadDeath);
    }
    catch (Throwable localThrowable2)
    {
      if ((localThrowable2 instanceof SystemException)) {
        throw ((SystemException)localThrowable2);
      }
      throw poaWrapper.localServantLookup(localThrowable2);
    }
    if (!checkForCompatibleServant(localOAInvocationInfo, paramClass))
    {
      servantExit(localObjectAdapter);
      return null;
    }
    return localOAInvocationInfo;
  }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject)
  {
    ObjectAdapter localObjectAdapter = orb.peekInvocationInfo().oa();
    servantExit(localObjectAdapter);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\POALocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */