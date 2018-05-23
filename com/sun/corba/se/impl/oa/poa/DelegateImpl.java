package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import java.util.EmptyStackException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.portable.Delegate;

public class DelegateImpl
  implements Delegate
{
  private com.sun.corba.se.spi.orb.ORB orb;
  private POASystemException wrapper;
  private POAFactory factory;
  
  public DelegateImpl(com.sun.corba.se.spi.orb.ORB paramORB, POAFactory paramPOAFactory)
  {
    orb = paramORB;
    wrapper = POASystemException.get(paramORB, "oa");
    factory = paramPOAFactory;
  }
  
  public org.omg.CORBA.ORB orb(Servant paramServant)
  {
    return orb;
  }
  
  public org.omg.CORBA.Object this_object(Servant paramServant)
  {
    try
    {
      byte[] arrayOfByte = orb.peekInvocationInfo().id();
      POA localPOA = (POA)orb.peekInvocationInfo().oa();
      String str = paramServant._all_interfaces(localPOA, arrayOfByte)[0];
      return localPOA.create_reference_with_id(arrayOfByte, str);
    }
    catch (EmptyStackException localEmptyStackException)
    {
      POAImpl localPOAImpl = null;
      try
      {
        localPOAImpl = (POAImpl)paramServant._default_POA();
      }
      catch (ClassCastException localClassCastException2)
      {
        throw wrapper.defaultPoaNotPoaimpl(localClassCastException2);
      }
      try
      {
        if ((localPOAImpl.getPolicies().isImplicitlyActivated()) || ((localPOAImpl.getPolicies().isUniqueIds()) && (localPOAImpl.getPolicies().retainServants()))) {
          return localPOAImpl.servant_to_reference(paramServant);
        }
        throw wrapper.wrongPoliciesForThisObject();
      }
      catch (ServantNotActive localServantNotActive)
      {
        throw wrapper.thisObjectServantNotActive(localServantNotActive);
      }
      catch (WrongPolicy localWrongPolicy)
      {
        throw wrapper.thisObjectWrongPolicy(localWrongPolicy);
      }
    }
    catch (ClassCastException localClassCastException1)
    {
      throw wrapper.defaultPoaNotPoaimpl(localClassCastException1);
    }
  }
  
  public POA poa(Servant paramServant)
  {
    try
    {
      return (POA)orb.peekInvocationInfo().oa();
    }
    catch (EmptyStackException localEmptyStackException)
    {
      POA localPOA = factory.lookupPOA(paramServant);
      if (localPOA != null) {
        return localPOA;
      }
      throw wrapper.noContext(localEmptyStackException);
    }
  }
  
  public byte[] object_id(Servant paramServant)
  {
    try
    {
      return orb.peekInvocationInfo().id();
    }
    catch (EmptyStackException localEmptyStackException)
    {
      throw wrapper.noContext(localEmptyStackException);
    }
  }
  
  public POA default_POA(Servant paramServant)
  {
    return factory.getRootPOA();
  }
  
  public boolean is_a(Servant paramServant, String paramString)
  {
    String[] arrayOfString = paramServant._all_interfaces(poa(paramServant), object_id(paramServant));
    for (int i = 0; i < arrayOfString.length; i++) {
      if (paramString.equals(arrayOfString[i])) {
        return true;
      }
    }
    return false;
  }
  
  public boolean non_existent(Servant paramServant)
  {
    try
    {
      byte[] arrayOfByte = orb.peekInvocationInfo().id();
      return arrayOfByte == null;
    }
    catch (EmptyStackException localEmptyStackException)
    {
      throw wrapper.noContext(localEmptyStackException);
    }
  }
  
  public org.omg.CORBA.Object get_interface_def(Servant paramServant)
  {
    throw wrapper.methodNotImplemented();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\DelegateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */