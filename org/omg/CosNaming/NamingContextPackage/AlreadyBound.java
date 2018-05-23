package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;

public final class AlreadyBound
  extends UserException
{
  public AlreadyBound()
  {
    super(AlreadyBoundHelper.id());
  }
  
  public AlreadyBound(String paramString)
  {
    super(AlreadyBoundHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\AlreadyBound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */