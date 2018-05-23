package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class NameService
{
  private NamingContext rootContext = null;
  private POA nsPOA = null;
  private ServantManagerImpl contextMgr;
  private ORB theorb;
  
  public NameService(ORB paramORB, File paramFile)
    throws Exception
  {
    theorb = paramORB;
    POA localPOA = (POA)paramORB.resolve_initial_references("RootPOA");
    localPOA.the_POAManager().activate();
    int i = 0;
    Policy[] arrayOfPolicy = new Policy[4];
    arrayOfPolicy[(i++)] = localPOA.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
    arrayOfPolicy[(i++)] = localPOA.create_request_processing_policy(RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
    arrayOfPolicy[(i++)] = localPOA.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID);
    arrayOfPolicy[(i++)] = localPOA.create_servant_retention_policy(ServantRetentionPolicyValue.NON_RETAIN);
    nsPOA = localPOA.create_POA("NameService", null, arrayOfPolicy);
    nsPOA.the_POAManager().activate();
    contextMgr = new ServantManagerImpl(paramORB, paramFile, this);
    String str = ServantManagerImpl.getRootObjectKey();
    NamingContextImpl localNamingContextImpl = new NamingContextImpl(paramORB, str, this, contextMgr);
    localNamingContextImpl = contextMgr.addContext(str, localNamingContextImpl);
    localNamingContextImpl.setServantManagerImpl(contextMgr);
    localNamingContextImpl.setORB(paramORB);
    localNamingContextImpl.setRootNameService(this);
    nsPOA.set_servant_manager(contextMgr);
    rootContext = NamingContextHelper.narrow(nsPOA.create_reference_with_id(str.getBytes(), NamingContextHelper.id()));
  }
  
  public NamingContext initialNamingContext()
  {
    return rootContext;
  }
  
  POA getNSPOA()
  {
    return nsPOA;
  }
  
  public NamingContext NewContext()
    throws SystemException
  {
    try
    {
      String str = contextMgr.getNewObjectKey();
      Object localObject = new NamingContextImpl(theorb, str, this, contextMgr);
      NamingContextImpl localNamingContextImpl = contextMgr.addContext(str, (NamingContextImpl)localObject);
      if (localNamingContextImpl != null) {
        localObject = localNamingContextImpl;
      }
      ((NamingContextImpl)localObject).setServantManagerImpl(contextMgr);
      ((NamingContextImpl)localObject).setORB(theorb);
      ((NamingContextImpl)localObject).setRootNameService(this);
      NamingContext localNamingContext = NamingContextHelper.narrow(nsPOA.create_reference_with_id(str.getBytes(), NamingContextHelper.id()));
      return localNamingContext;
    }
    catch (SystemException localSystemException)
    {
      throw localSystemException;
    }
    catch (Exception localException) {}
    return null;
  }
  
  org.omg.CORBA.Object getObjectReferenceFromKey(String paramString)
  {
    org.omg.CORBA.Object localObject = null;
    try
    {
      localObject = nsPOA.create_reference_with_id(paramString.getBytes(), NamingContextHelper.id());
    }
    catch (Exception localException)
    {
      localObject = null;
    }
    return localObject;
  }
  
  String getObjectKey(org.omg.CORBA.Object paramObject)
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = nsPOA.reference_to_id(paramObject);
    }
    catch (WrongAdapter localWrongAdapter)
    {
      return null;
    }
    catch (WrongPolicy localWrongPolicy)
    {
      return null;
    }
    catch (Exception localException)
    {
      return null;
    }
    String str = new String(arrayOfByte);
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\NameService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */