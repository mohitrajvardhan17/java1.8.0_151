package org.omg.PortableServer;

import org.omg.CORBA.ServerRequest;

public abstract class DynamicImplementation
  extends Servant
{
  public DynamicImplementation() {}
  
  public abstract void invoke(ServerRequest paramServerRequest);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\DynamicImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */