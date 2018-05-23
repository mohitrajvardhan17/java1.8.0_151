package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidValue;

@Deprecated
public abstract interface DynFixed
  extends Object, DynAny
{
  public abstract byte[] get_value();
  
  public abstract void set_value(byte[] paramArrayOfByte)
    throws InvalidValue;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\DynFixed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */