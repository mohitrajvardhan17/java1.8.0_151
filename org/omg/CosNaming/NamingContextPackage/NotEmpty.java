package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;

public final class NotEmpty
  extends UserException
{
  public NotEmpty()
  {
    super(NotEmptyHelper.id());
  }
  
  public NotEmpty(String paramString)
  {
    super(NotEmptyHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotEmpty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */