package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;

public final class NotFound
  extends UserException
{
  public NotFoundReason why = null;
  public NameComponent[] rest_of_name = null;
  
  public NotFound()
  {
    super(NotFoundHelper.id());
  }
  
  public NotFound(NotFoundReason paramNotFoundReason, NameComponent[] paramArrayOfNameComponent)
  {
    super(NotFoundHelper.id());
    why = paramNotFoundReason;
    rest_of_name = paramArrayOfNameComponent;
  }
  
  public NotFound(String paramString, NotFoundReason paramNotFoundReason, NameComponent[] paramArrayOfNameComponent)
  {
    super(NotFoundHelper.id() + "  " + paramString);
    why = paramNotFoundReason;
    rest_of_name = paramArrayOfNameComponent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotFound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */