package org.omg.PortableInterceptor;

import org.omg.CORBA.UserException;

public final class InvalidSlot
  extends UserException
{
  public InvalidSlot()
  {
    super(InvalidSlotHelper.id());
  }
  
  public InvalidSlot(String paramString)
  {
    super(InvalidSlotHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\InvalidSlot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */