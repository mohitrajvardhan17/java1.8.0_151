package org.omg.CosNaming;

import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public abstract interface NamingContextExtOperations
  extends NamingContextOperations
{
  public abstract String to_string(NameComponent[] paramArrayOfNameComponent)
    throws InvalidName;
  
  public abstract NameComponent[] to_name(String paramString)
    throws InvalidName;
  
  public abstract String to_url(String paramString1, String paramString2)
    throws InvalidAddress, InvalidName;
  
  public abstract org.omg.CORBA.Object resolve_str(String paramString)
    throws NotFound, CannotProceed, InvalidName;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextExtOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */