package org.omg.PortableServer.CurrentPackage;

import org.omg.CORBA.UserException;

public final class NoContext
  extends UserException
{
  public NoContext()
  {
    super(NoContextHelper.id());
  }
  
  public NoContext(String paramString)
  {
    super(NoContextHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\CurrentPackage\NoContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */