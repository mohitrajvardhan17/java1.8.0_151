package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ServantAlreadyActive
  extends UserException
{
  public ServantAlreadyActive()
  {
    super(ServantAlreadyActiveHelper.id());
  }
  
  public ServantAlreadyActive(String paramString)
  {
    super(ServantAlreadyActiveHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAPackage\ServantAlreadyActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */