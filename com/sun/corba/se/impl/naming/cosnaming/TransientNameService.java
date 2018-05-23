package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class TransientNameService
{
  private org.omg.CORBA.Object theInitialNamingContext;
  
  public TransientNameService(ORB paramORB)
    throws INITIALIZE
  {
    initialize(paramORB, "NameService");
  }
  
  public TransientNameService(ORB paramORB, String paramString)
    throws INITIALIZE
  {
    initialize(paramORB, paramString);
  }
  
  private void initialize(ORB paramORB, String paramString)
    throws INITIALIZE
  {
    NamingSystemException localNamingSystemException = NamingSystemException.get(paramORB, "naming");
    try
    {
      POA localPOA1 = (POA)paramORB.resolve_initial_references("RootPOA");
      localPOA1.the_POAManager().activate();
      int i = 0;
      Policy[] arrayOfPolicy = new Policy[3];
      arrayOfPolicy[(i++)] = localPOA1.create_lifespan_policy(LifespanPolicyValue.TRANSIENT);
      arrayOfPolicy[(i++)] = localPOA1.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID);
      arrayOfPolicy[(i++)] = localPOA1.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN);
      POA localPOA2 = localPOA1.create_POA("TNameService", null, arrayOfPolicy);
      localPOA2.the_POAManager().activate();
      TransientNamingContext localTransientNamingContext = new TransientNamingContext(paramORB, null, localPOA2);
      byte[] arrayOfByte = localPOA2.activate_object(localTransientNamingContext);
      localRoot = localPOA2.id_to_reference(arrayOfByte);
      theInitialNamingContext = localRoot;
      paramORB.register_initial_reference(paramString, theInitialNamingContext);
    }
    catch (SystemException localSystemException)
    {
      throw localNamingSystemException.transNsCannotCreateInitialNcSys(localSystemException);
    }
    catch (Exception localException)
    {
      throw localNamingSystemException.transNsCannotCreateInitialNc(localException);
    }
  }
  
  public org.omg.CORBA.Object initialNamingContext()
  {
    return theInitialNamingContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\TransientNameService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */