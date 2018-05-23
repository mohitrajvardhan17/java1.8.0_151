package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.UserException;

public final class InvalidAddress
  extends UserException
{
  public InvalidAddress()
  {
    super(InvalidAddressHelper.id());
  }
  
  public InvalidAddress(String paramString)
  {
    super(InvalidAddressHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextExtPackage\InvalidAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */