package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class AdapterNonExistent
  extends UserException
{
  public AdapterNonExistent()
  {
    super(AdapterNonExistentHelper.id());
  }
  
  public AdapterNonExistent(String paramString)
  {
    super(AdapterNonExistentHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAPackage\AdapterNonExistent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */