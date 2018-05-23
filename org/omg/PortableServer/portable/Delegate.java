package org.omg.PortableServer.portable;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public abstract interface Delegate
{
  public abstract ORB orb(Servant paramServant);
  
  public abstract org.omg.CORBA.Object this_object(Servant paramServant);
  
  public abstract POA poa(Servant paramServant);
  
  public abstract byte[] object_id(Servant paramServant);
  
  public abstract POA default_POA(Servant paramServant);
  
  public abstract boolean is_a(Servant paramServant, String paramString);
  
  public abstract boolean non_existent(Servant paramServant);
  
  public abstract org.omg.CORBA.Object get_interface_def(Servant paramServant);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\portable\Delegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */