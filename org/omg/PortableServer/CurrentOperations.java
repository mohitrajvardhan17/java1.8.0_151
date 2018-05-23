package org.omg.PortableServer;

import org.omg.PortableServer.CurrentPackage.NoContext;

public abstract interface CurrentOperations
  extends org.omg.CORBA.CurrentOperations
{
  public abstract POA get_POA()
    throws NoContext;
  
  public abstract byte[] get_object_id()
    throws NoContext;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\CurrentOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */