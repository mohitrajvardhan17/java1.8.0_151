package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ServantNotActive
  extends UserException
{
  public ServantNotActive()
  {
    super(ServantNotActiveHelper.id());
  }
  
  public ServantNotActive(String paramString)
  {
    super(ServantNotActiveHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAPackage\ServantNotActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */