package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;

public final class CannotProceed
  extends UserException
{
  public NamingContext cxt = null;
  public NameComponent[] rest_of_name = null;
  
  public CannotProceed()
  {
    super(CannotProceedHelper.id());
  }
  
  public CannotProceed(NamingContext paramNamingContext, NameComponent[] paramArrayOfNameComponent)
  {
    super(CannotProceedHelper.id());
    cxt = paramNamingContext;
    rest_of_name = paramArrayOfNameComponent;
  }
  
  public CannotProceed(String paramString, NamingContext paramNamingContext, NameComponent[] paramArrayOfNameComponent)
  {
    super(CannotProceedHelper.id() + "  " + paramString);
    cxt = paramNamingContext;
    rest_of_name = paramArrayOfNameComponent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\CannotProceed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */