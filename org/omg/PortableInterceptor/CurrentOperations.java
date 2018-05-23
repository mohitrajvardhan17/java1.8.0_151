package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;

public abstract interface CurrentOperations
  extends org.omg.CORBA.CurrentOperations
{
  public abstract Any get_slot(int paramInt)
    throws InvalidSlot;
  
  public abstract void set_slot(int paramInt, Any paramAny)
    throws InvalidSlot;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\CurrentOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */