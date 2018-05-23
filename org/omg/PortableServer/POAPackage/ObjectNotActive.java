package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ObjectNotActive
  extends UserException
{
  public ObjectNotActive()
  {
    super(ObjectNotActiveHelper.id());
  }
  
  public ObjectNotActive(String paramString)
  {
    super(ObjectNotActiveHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAPackage\ObjectNotActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */