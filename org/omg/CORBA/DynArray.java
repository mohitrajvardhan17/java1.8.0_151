package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidSeq;

@Deprecated
public abstract interface DynArray
  extends Object, DynAny
{
  public abstract Any[] get_elements();
  
  public abstract void set_elements(Any[] paramArrayOfAny)
    throws InvalidSeq;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\DynArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */