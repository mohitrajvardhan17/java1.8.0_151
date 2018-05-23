package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ObjectAlreadyActive
  extends UserException
{
  public ObjectAlreadyActive()
  {
    super(ObjectAlreadyActiveHelper.id());
  }
  
  public ObjectAlreadyActive(String paramString)
  {
    super(ObjectAlreadyActiveHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAPackage\ObjectAlreadyActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */