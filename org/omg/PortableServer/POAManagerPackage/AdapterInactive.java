package org.omg.PortableServer.POAManagerPackage;

import org.omg.CORBA.UserException;

public final class AdapterInactive
  extends UserException
{
  public AdapterInactive()
  {
    super(AdapterInactiveHelper.id());
  }
  
  public AdapterInactive(String paramString)
  {
    super(AdapterInactiveHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAManagerPackage\AdapterInactive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */